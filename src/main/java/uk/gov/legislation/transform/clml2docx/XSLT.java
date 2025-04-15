package uk.gov.legislation.transform.clml2docx;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import net.sf.saxon.value.ObjectValue;
import uk.gov.legislation.transform.clml2docx.Delegate.Resource;

/**
 * Loads and runs the CLML to Word XSLT
 */
public class XSLT {
	
	static final String path = "/transforms/clml2docx/xslt/";
	
	private static final String stylesheet = "clml2docx.xsl";
	public Map<String, String> conversionParameters = new HashMap<>();
	
	static class Importer implements URIResolver {

		public Source resolve(String href, String base) throws TransformerException {
			InputStream file = this.getClass().getResourceAsStream(path + href);
			return new StreamSource(file, href);
		}

	}
	
	private static XsltExecutable executable;
	private static XPathExecutable docType;
	private static XPathExecutable resourceURIs;
	
	/**
	 * Load the XSLT
	 * @param delegate Provides access to the conversion parameters
	 * @throws IOException
	 */
	XSLT(Delegate delegate) throws IOException {
		
		this.conversionParameters = delegate.getConversionParameters();
		
		Processor processor = new Processor(false);
		
		processor.getUnderlyingConfiguration().registerExtensionFunction(new SaxonExtensionFunctions.GetImageWidth(delegate));
		processor.getUnderlyingConfiguration().registerExtensionFunction(new SaxonExtensionFunctions.GetImageHeight(delegate));
		processor.getUnderlyingConfiguration().registerExtensionFunction(new SaxonExtensionFunctions.GetImageType(delegate));
		
		if (executable == null) {
			XsltCompiler xsltCompiler = processor.newXsltCompiler();
			xsltCompiler.setURIResolver(new Importer());
			InputStream stream = this.getClass().getResourceAsStream(path + stylesheet);
			Source source = new StreamSource(stream, stylesheet);
			try {
				executable = xsltCompiler.compile(source);
			} catch (SaxonApiException e) {
				throw new RuntimeException(e);
			} finally {
				stream.close();
			}
			XPathCompiler xPathCompiler = processor.newXPathCompiler();
			xPathCompiler.declareNamespace("", "http://www.legislation.gov.uk/namespaces/legislation");
			xPathCompiler.declareNamespace("ukm", "http://www.legislation.gov.uk/namespaces/metadata");
			try {
				docType = xPathCompiler.compile("/Legislation/ukm:Metadata/*/ukm:DocumentClassification/ukm:DocumentMainType/@Value");
				resourceURIs = xPathCompiler.compile("/Legislation/Resources/Resource/ExternalVersion/@URI");
			} catch (SaxonApiException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Pass parameters into the XSLT 
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return Configured XSLT transformer
	 */
	private XsltTransformer loadAndSetParameters(Map<String, Resource> cache, boolean debug) {
		XsltTransformer transform = executable.load();
		transform.setParameter(new QName("cache"), XdmValue.makeValue( new ObjectValue<>(cache)));
		transform.setParameter(new QName("debug"), new XdmAtomicValue(debug));

		// set the xslt parameters received from the job instruction
		for (Map.Entry<String, String> entry : conversionParameters.entrySet()) {
			transform.setParameter(new QName(entry.getKey()), new XdmAtomicValue(entry.getValue()));
		}

		return transform;
	}

	/**
	 * Transform a document and return the output as an XML document 
	 * @param transform Configured transformer
	 * @return The document that was produced
	 */
	private XdmNode transformToNode(XsltTransformer transform) {
		XdmDestination destination = new XdmDestination();
		transform.setDestination(destination);
		try {
			transform.transform();
		} catch (SaxonApiException e) {
			throw new RuntimeException(e);
		}
		return destination.getXdmNode();
	}

	/**
	 * Run a template in the XSLT
	 * @param clml CLML to convert
	 * @param template Name of the template to call
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return The document that was produced
	 */
	private XdmNode callTemplate(XdmNode clml, String template, Map<String, Resource> cache, boolean debug) {
		XsltTransformer transform = loadAndSetParameters(cache, debug);
		transform.setInitialContextNode(clml);
		transform.setInitialTemplate(new QName(template));
		return transformToNode(transform);
	}
	
	/**
	 * Run XSLT to generate the core properties component of the docx file
	 * @param clml CLML to convert
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return The core properties document that was produced
	 */
	public XdmNode coreProperties(XdmNode clml, Map<String, Resource> cache, boolean debug) {
		return callTemplate(clml, "core-properties", cache, debug);
	}	
	
	/**
	 * Run XSLT to generate the document component of the docx file
	 * @param clml CLML to convert
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return The document that was produced
	 */
	public XdmNode document(XdmNode clml, Map<String, Resource> cache, boolean debug) {
		XsltTransformer transform = loadAndSetParameters(cache, debug);
		transform.setInitialContextNode(clml);
		return transformToNode(transform);
	}
	
	/**
	 * Run XSLT to generate the styles component of the docx file
	 * @param clml CLML to convert
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return The styles document that was produced
	 */
	public XdmNode styles(XdmNode clml, Map<String, Resource> cache, boolean debug) {
		return callTemplate(clml, "styles", cache, debug);
	}

	/**
	 * Run XSLT to generate the headers components of the docx file
	 * @param clml CLML to convert
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return The headers that were produced
	 */
	public XdmNode[] headers(XdmNode clml, Map<String, Resource> cache, boolean debug) {
		XdmNode[] headers = new XdmNode[3];
		headers[0] = callTemplate(clml, "header1", cache, debug);
		headers[1] = callTemplate(clml, "header2", cache, debug);
		headers[2] = callTemplate(clml, "header3", cache, debug);
		return headers;
	}

	/**
	 * Run XSLT to generate the headers components of the docx file
	 * @param clml CLML to convert
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return The headers that were produced
	 */
	public XdmNode[] footers(XdmNode clml, Map<String, Resource> cache, boolean debug) {
		XdmNode[] footers = new XdmNode[2];
		footers[0] = callTemplate(clml, "footer1", cache, debug);
		footers[1] = callTemplate(clml, "footer2", cache, debug);
		return footers;
	}

	/**
	 * Run XSLT to generate the footnotes components of the docx file
	 * @param clml CLML to convert
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return The footnotes that were produced
	 */	
	public XdmNode footnotes(XdmNode clml, Map<String, Resource> cache, boolean debug) {
		return callTemplate(clml, "footnotes", cache, debug);
	}

	/**
	 * Run XSLT to generate the relationships components of the docx file
	 * @param clml CLML to convert
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return The relationships that were produced
	 */	
	public XdmNode relationships(XdmNode clml, Map<String, Resource> cache, boolean debug) {
		return callTemplate(clml, "relationships", cache, debug);
	}

	/**
	 * Run XSLT to generate the footnote relationships components of the docx file
	 * @param clml CLML to convert
	 * @param cache Cache of all the linked images
	 * @param debug Enable debugging information in the output
	 * @return The footnote relationships  that were produced
	 */	
	public XdmNode footnoteRelationships(XdmNode clml, Map<String, Resource> cache, boolean debug) {
		return callTemplate(clml, "footnote-relationships", cache, debug);
	}

	/**
	 * Finds the name of the document type in the CLML
	 * @param clml CLML to process
	 * @return the name of the document type
	 */
	String getDocumentMainType(XdmNode clml) {
		XPathSelector selector = docType.load();
		try {
			selector.setContextItem(clml);
		} catch (SaxonApiException e) {
			throw new IllegalArgumentException(e);
		}
		try {
			return selector.evaluateSingle().getStringValue();
		} catch (SaxonApiException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Finds the linked images in the CLML
	 * @param clml CLML to process
	 * @return a list of the images
	 */
	List<String> getResourceURIs(XdmNode clml) {
		XPathSelector selector = resourceURIs.load();
		try {
			selector.setContextItem(clml);
		} catch (SaxonApiException e) {
			throw new IllegalArgumentException(e);
		}
		XdmValue uris;
		try {
			uris = selector.evaluate();
		} catch (SaxonApiException e) {
			throw new RuntimeException(e);
		}
		return StreamSupport.stream(uris.spliterator(), false)
			.map(item -> item.getStringValue())
			.collect(Collectors.toList());	
	}
	
	
	Processor getProcessor() {
		return executable.getProcessor();
	}

}

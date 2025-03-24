package uk.gov.legislation.transform.clml2docx;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import uk.gov.legislation.transform.clml2docx.Delegate.Resource;

/**
 * Convert a CLML file to docx. 
 */
public class TransformClmlToDocx {
	
	private final Delegate delegate;
	private final XSLT xslt;
	private Logger logger = Logger.getLogger(TransformClmlToDocx.class.getName());
	
	/**
	 * 
	 * @param delegate Provides access to the API that provides the source data
	 * @throws IOException
	 */
	public TransformClmlToDocx(Delegate delegate) throws IOException {
		this.delegate = delegate;
		this.xslt = new XSLT(delegate);  
	}
	
	public TransformClmlToDocx() throws IOException {
		this.delegate = new LegislationApiDelegate();
		this.xslt = new XSLT(delegate);
	}
	
	/**
	 * Convert a CLML file to docx
	 * @param clml CLML to convert
	 * @param debug Enable debugging information in the output
	 * @return docx file
	 * @throws IOException
	 */
	public byte[] transform(XdmNode clml, boolean debug) throws IOException {
		logger.log(Level.INFO, "Performing file conversion");
		Package bundle = new Package();
		Map<String, Resource> cache = new HashMap<>();
		bundle.coreProperties = xslt.coreProperties(clml, cache, debug);
		bundle.document = xslt.document(clml, cache, debug);
		bundle.styles = xslt.styles(clml, cache, debug);
		bundle.headers = xslt.headers(clml, cache, debug);
		bundle.footers =  xslt.footers(clml, cache, debug);
		bundle.footnotes = xslt.footnotes(clml, cache, debug);
		bundle.relationships = xslt.relationships(clml, cache, debug);
		bundle.footnoteRelationships = xslt.footnoteRelationships(clml, cache, debug);
		bundle.resources = fetchAllResources(clml, cache);
		return bundle.save();		
	}	
	
	/**
	 * Convert a CLML file to docx
	 * @param clml CLML to convert
	 * @param debug Enable debugging information in the output
	 * @return docx file
	 * @throws IOException
	 * @throws SaxonApiException
	 */
	public byte[] transform(InputStream clml, boolean debug) throws IOException, SaxonApiException {
		XdmNode doc = parse(clml);
		return transform(doc, debug);
	}
	
	/**
	 * Convert a CLML file to docx
	 * @param clml CLML to convert
	 * @return docx file
	 * @throws IOException
	 * @throws SaxonApiException
	 */
	public byte[] transform(InputStream clml) throws IOException, SaxonApiException {
		return transform(clml, false);
	}	
	
	/**
	 * Fetches the images and crest for the CLML 
	 * @param clml CLML to convert
	 * @param cache Cache of all the linked images
	 * @return Cache of all the linked images
	 * @throws IOException
	 */
	private Map<String, byte[]> fetchAllResources(XdmNode clml, Map<String, Resource> cache) throws IOException {
		List<String> resourceURIs = xslt.getResourceURIs(clml);
		LinkedHashMap<String, byte[]> resources = fetchLinkedResources(resourceURIs, cache);
		fetchCrest(clml, resources);
		return resources;
	}
	
	/**
	 * Fetches images that are linked inside the CLML file
	 * @param resourceURIs List of images to download
	 * @param cache Existing cache of the images
	 * @return Modified cache of the images
	 * @throws IOException
	 */
	private LinkedHashMap<String, byte[]> fetchLinkedResources(List<String> resourceURIs, Map<String, Resource> cache) throws IOException {
		LinkedHashMap<String, byte[]> resources = new LinkedHashMap<>();
		for (String uri : resourceURIs) {
			Resource resource;
			try {
				resource = delegate.fetch(uri, cache);
			} catch (IOException e) {
				continue;
			}
			String filename = uri.substring(uri.lastIndexOf('/') + 1);
			if (resource.contentType.equals("image/gif"))
				filename += ".gif";
			else if (resource.contentType.equals("image/jpeg") || resource.contentType.equals("image/jpg"))
				filename += ".jpg";
			else
				throw new RuntimeException(resource.contentType);
			resources.put(filename, resource.content);
		}
		return resources;
	}		
	
	/**
	 * Fetch the crest required for this CLML file
	 * Note that it doesn't match the logic used by the PDF generator, so it may use different crests 
	 * It's also using crest images inside the jar, instead of fetching them from the website.  
	 * @param clml CLML to convert
	 * @param cache Existing cache of the images
	 * @throws IOException
	 */
	private void fetchCrest(XdmNode clml, LinkedHashMap<String, byte[]> resources) throws IOException {
		
		// choose the appropriate crest depending on the document type
		final String crest;
		switch (xslt.getDocumentMainType(clml)) {
			case "UnitedKingdomPublicGeneralAct":
			case "UnitedKingdomLocalAct":
			case "UnitedKingdomChurchMeasure":
			case "NorthernIrelandAct":
			case "EnglandAct":
			case "IrelandAct":
			case "GreatBritainAct":
			case "NorthernIrelandAssemblyMeasure": 
			case "NorthernIrelandParliamentAct":
				crest = "ukpga.png";
				break;
			case "ScottishAct":
			case "ScottishOldAct":
				crest = "asp.png";
				break;
			case "WelshParliamentAct":
			case "WelshNationalAssemblyAct":
			case "WelshAssemblyMeasure":
				crest = "asc.png";
				break;
			default:
				crest = null;
		}
		if (crest != null) {
			// read the crest from the embedded resources
			InputStream input = getClass().getResourceAsStream("/images/" + crest);
			byte[] image = input.readAllBytes();
			resources.put("crest.png", image);
		}
	}
	


	
	public XdmNode parse(InputStream clml) throws IOException, SaxonApiException {
		DocumentBuilder builder = xslt.getProcessor().newDocumentBuilder();
		XdmNode document = builder.build(new StreamSource(clml));
		clml.close();
		return document;
	}
	


}

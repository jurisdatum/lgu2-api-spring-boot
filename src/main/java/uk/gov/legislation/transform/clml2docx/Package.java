package uk.gov.legislation.transform.clml2docx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.s9api.XdmNode;

public class Package {
	
	XdmNode document;
	XdmNode coreProperties;
	XdmNode styles;
	XdmNode[] headers;
	XdmNode[] footers;
	XdmNode footnotes;
	XdmNode relationships;
	XdmNode footnoteRelationships;
	Map<String, byte[]> resources = new LinkedHashMap<>();
	final Logger logger = Logger.getLogger(Package.class.getName());
	
	private final String[] components = new String[] {
		"_rels/.rels",
		"word/settings.xml",
		"word/webSettings.xml",
		"[Content_Types].xml"
	};
	
	private ZipOutputStream zip;
		
	/**
	 * Generates a docx file by generating the individual files within the archive, and returns it
	 * @return docx file
	 * @throws IOException
	 */
	byte[] save() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		zip = new ZipOutputStream(baos);
		saveComponents();
		saveCoreProperties();
		saveDocument();
		saveStyles();
		saveHeadersAndFooters();
		saveFootnotes();
		saveRelationships();
		saveFootnoteRelationships();
		saveResources();
		zip.close();
		return baos.toByteArray();
	}
	
	/**
	 * Save static components into the docx file
	 * @throws IOException
	 */
	private void saveComponents() throws IOException {
		logger.log(Level.FINE, "bundling static components");
		for (String component : components) {
			InputStream input = this.getClass().getResourceAsStream("/transforms/clml2docx/components/" + component);
			byte[] data = input.readAllBytes();
			input.close();
			zip.putNextEntry(new ZipEntry(component));
	        zip.write(data, 0, data.length);
	        zip.closeEntry();
		}
	}
	
	/**
	 * Save properties into the docx file
	 * @throws IOException
	 */
	private void saveCoreProperties() throws IOException {
		logger.log(Level.FINE, "bundling core properties");
		zip.putNextEntry(new ZipEntry("docProps/core.xml"));
		serialize(coreProperties, zip);
		zip.closeEntry();
	}
	
	/**
	 * Save the main document into the docx file
	 * @throws IOException
	 */
	private void saveDocument() throws IOException {
		logger.log(Level.FINE, "bundling document");
		zip.putNextEntry(new ZipEntry("word/document.xml"));
		serialize(document, zip);
		zip.closeEntry();
	}
	
	/**
	 * Save the styles into the docx file
	 * @throws IOException
	 */	
	private void saveStyles() throws IOException {
		logger.log(Level.FINE, "bundling styles");
		zip.putNextEntry(new ZipEntry("word/styles.xml"));
		serialize(styles, zip);
		zip.closeEntry();
	}

	/**
	 * Save the header and footer files into the docx file
	 * @throws IOException
	 */	
	private void saveHeadersAndFooters() throws IOException {
		for (int i = 0; i < headers.length; i++) {
			int n = i + 1;
			logger.log(Level.FINE, "bundling header " + n);
			zip.putNextEntry(new ZipEntry("word/header" + n + ".xml"));
			serialize(headers[i], zip);
			zip.closeEntry();
		}
		for (int i = 0; i < footers.length; i++) {
			int n = i + 1;
			logger.log(Level.FINE, "bundling footer " + n);
			zip.putNextEntry(new ZipEntry("word/footer" + n + ".xml"));
			serialize(footers[i], zip);
			zip.closeEntry();
		}
	}

	/**
	 * Save the footnotes into the docx file
	 * @throws IOException
	 */	
	private void saveFootnotes() throws IOException {
		logger.log(Level.FINE, "bundling footnotes");
		zip.putNextEntry(new ZipEntry("word/footnotes.xml"));
		serialize(footnotes, zip);
		zip.closeEntry();
	}

	/**
	 * Save the cross-references into the docx file
	 * @throws IOException
	 */	
	private void saveRelationships() throws IOException {
		logger.log(Level.FINE, "bundling relationships");
		zip.putNextEntry(new ZipEntry("word/_rels/document.xml.rels"));
		serialize(relationships, zip);
		zip.closeEntry();
	}

	/**
	 * Save the footnote cross-references into the docx file
	 * @throws IOException
	 */	
	private void saveFootnoteRelationships() throws IOException {
		logger.log(Level.FINE, "bundling footnote relationships");
		zip.putNextEntry(new ZipEntry("word/_rels/footnotes.xml.rels"));
		serialize(footnoteRelationships, zip);
		zip.closeEntry();
	}

	/**
	 * Saves the resources (e.g. images) to the docx file
	 * @throws IOException
	 */
	private void saveResources() throws IOException {
		logger.log(Level.FINE, "bundling resources");
		for (Entry<String, byte[]> image : resources.entrySet()) {
			String filename = image.getKey();
			logger.log(Level.FINE, "bundling " + filename);
			zip.putNextEntry(new ZipEntry("word/media/" + filename));
			byte[] data = image.getValue();
			zip.write(data, 0, data.length);
			zip.closeEntry();
		}
	}
	
	/**
	 * Run the XSLT on the input document and write the result to the output.
	 * @param document XML document to transform
	 * @param output Stream to write the output to 
	 */
	public static void serialize(XdmNode document, OutputStream output) {
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			transformer.transform(document.asSource(), new StreamResult(output));
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

}

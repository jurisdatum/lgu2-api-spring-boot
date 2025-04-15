package uk.gov.legislation.transform.clml2docx;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Defines an interface that's used by the conversion function to retrieve source data and conversion parameters.
 */
public interface Delegate {
	
	/**
	 * Fetches a resource from it's URI
	 * @param uri The URI of the resource
	 * @return A Resource that is populated from the URI
	 * @throws IOException
	 */
	public Resource fetch(String uri) throws IOException;

	/**
	 * Fetches a resource from the cache or from it's URI if it's not in the cache.
	 * @param uri The URI of the resource
	 * @param cache Cache of all the linked images
	 * @return The resource 
	 * @throws IOException
	 */
	public default Resource fetch(String uri, Map<String, Resource> cache) throws IOException {
		if (cache.containsKey(uri))
			return cache.get(uri);
		Resource resource = fetch(uri);
		cache.put(uri, resource);
		return resource;
	}
	
	/**
	 * Set parameters for the conversion
	 * @param conversionParameters Parameters for the conversion
	 */
	public void setConversionParameters(Map<String, String> conversionParameters);

	/**
	 * Get parameters for the conversion
	 * @return Parameters for the conversion
	 */
	public Map<String, String> getConversionParameters();

	/**
	 * A resource  that is referenced by the source document.  This is usually an image.
	 */
	public static class Resource {
		
		/**
		 * The resource data
		 */
		public final byte[] content;
		
		/**
		 * The media type of the resource
		 */
		public final String contentType;
		
		/**
		 * Instantiate a Resource
		 * @param data Source data for the resource
		 * @param contentType Media type of the resource
		 */
		public Resource(byte[] data, String contentType) {
			this.content = data;
			this.contentType = contentType;
		}
		
		/**
		 * The resource data
		 */
		private BufferedImage image;
		
		/**
		 * Returns the width of the image 
		 * @return Width of the image
		 * @throws IOException
		 */
		int getImageWidth() throws IOException {
			if (image == null)
				image = ImageIO.read(new ByteArrayInputStream(content));
			return image.getWidth();
		}
		
		/**
		 * Returns the height of the image 
		 * @return Height of the image
		 * @throws IOException
		 */
		int getImageHeight() throws IOException {
			if (image == null)
				image = ImageIO.read(new ByteArrayInputStream(content));
			return image.getHeight();
		}
		
	}

}

package uk.gov.legislation.transform.clml2docx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Provides the conversion function with access to the Legislation API to retrieve source data
 */
public class LegislationApiDelegate implements Delegate {
	
	public enum Authentication { 
		NONE, 
		BASIC
	}
	private Authentication authenticationMode = Authentication.NONE;
	private String username = "";
	private String password = "";
	private String rewriteHostName = "";
	private Map<String, String> conversionParameters = new HashMap<>();
	private Logger logger = Logger.getLogger(LegislationApiDelegate.class.getName());
	
	/**
	 * Fetches a resource from it's URI
	 * @param uri The URI of the resource
	 * @return A Resource that is populated from the URI
	 * @throws IOException
	 */
	@Override
	public Resource fetch(String uri) throws IOException {

		// rewrite host name for QA/UAT environments, because the links in the CLML don't go to the right
		// environment
		URL url = new URL(uri);
		if (!getRewriteHostName().isEmpty()) {
			url = new URL(url.getProtocol(), getRewriteHostName(), url.getPort(), url.getFile());
		}
		
		logger.log(Level.INFO, "Downloading: " + url.toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		// set auth
		if (getAuthenticationMode() == Authentication.BASIC) {
			logger.log(Level.FINE, "Applying basic auth");
			String auth = getUsername() + ":" + getPassword();
			byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
			String authHeaderValue = "Basic " + new String(encodedAuth);
			connection.setRequestProperty("Authorization", authHeaderValue);
		}		
		
		// follow a redirect
		// it redirects from http to https
		if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {
			url = new URL(connection.getHeaderField("Location"));
			logger.log(Level.INFO, "Redirected to: " + url.toString());
			connection.getInputStream().close();
			connection.disconnect();
			connection = (HttpURLConnection) url.openConnection();
			
			// we have a new connection so we need to set the auth header again
			if (getAuthenticationMode() == Authentication.BASIC) {
				logger.log(Level.FINE, "Applying basic auth");
				String auth = getUsername() + ":" + getPassword();
				byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
				String authHeaderValue = "Basic " + new String(encodedAuth);
				connection.setRequestProperty("Authorization", authHeaderValue);
			}	
		}
		String contentType = connection.getHeaderField("Content-Type");
		InputStream input = connection.getInputStream();
		byte[] data = input.readAllBytes();
	    connection.disconnect();
	    if (contentType.equals("binary/octet-stream")) {
	    	contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(data));
	    	if (contentType == null) {
	    		ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data));
	    		Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
	    		if (imageReaders.hasNext()) {
	    		    ImageReader reader = (ImageReader) imageReaders.next();
	    		    contentType = "image/" + reader.getFormatName().toLowerCase();
	    		} else {
	    			contentType = "image/gif";	// http://www.legislation.gov.uk/uksi/2018/4/images/uksi_20180004_en_001
	    		}
	    	}
	    } else if (contentType.equals("application/pdf")) {
	    	contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(data));
	    	if (contentType == null) {
	    		ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data));
	    		Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
	    		if (imageReaders.hasNext()) {
	    		    ImageReader reader = (ImageReader) imageReaders.next();
	    		    contentType = "image/" + reader.getFormatName().toLowerCase();
	    		} else {
	    			contentType = "application/pdf";
	    		}
	    	}
	    }
	    return new Resource(data, contentType);
	}
	
	/**
	 * Gets the authentication mode to use on the API
	 * @return Authentication mode to use
	 */
	public Authentication getAuthenticationMode() {
		return authenticationMode;
	}

	/**
	 * Sets the authentication mode to use on the API
	 * @param authenticationMode Authentication mode to use
	 */
	public void setAuthenticationMode(Authentication authenticationMode) {
		this.authenticationMode = authenticationMode;
	}

	/**
	 * Gets the username to use on the API
	 * @return Username to use
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username to use on the API
	 * @param username Username to use
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password to use on the API
	 * @return Password to use
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password to use on the API
	 * @param password Password to use
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the host name to use to override the host name in the resource URIs
	 * @return Host name
	 */
	public String getRewriteHostName() {
		return rewriteHostName;
	}

	/**
	 * Sets the host name to use to override the host name in the resource URIs
	 * @param rewriteHostName Host name
	 */
	public void setRewriteHostName(String rewriteHostName) {
		this.rewriteHostName = rewriteHostName;
	}
	
	/**
	 * Set parameters for the conversion
	 * @param conversionParameters Parameters for the conversion
	 */
	public void setConversionParameters(Map<String, String> conversionParameters) {
		this.conversionParameters = conversionParameters;
	}

	/**
	 * Get parameters for the conversion
	 * @return Parameters for the conversion
	 */
	public Map<String, String> getConversionParameters() {
		return conversionParameters;
	}
	

}

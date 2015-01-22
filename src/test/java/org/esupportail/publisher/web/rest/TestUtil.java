package org.esupportail.publisher.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

	/** MediaType for JSON UTF8 */
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	/**
	 * Convert an object to JSON byte array.
	 *
	 * @param object
	 *            the object to convert
	 * @return the JSON byte array
	 * @throws IOException
	 */
	public static byte[] convertObjectToJsonBytes(Object object)
			throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsBytes(object);
	}

	/**
	 * Convert JSON byte array to an Object.
	 *
	 * @param jsonBytes
     *          the JSON byte array
	 * @return object the object converted
	 * @throws IOException
	 */
	public static Object convertJsonBytesToObject(byte[] jsonBytes,
			Class<Object> objectType) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.readValue(jsonBytes, objectType);
	}
}

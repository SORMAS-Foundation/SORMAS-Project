package de.symeda.sormas.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces({"text/plain", "application/json"})
public class StringListMessageBodyWriter implements MessageBodyWriter<List<String>>{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(List<String> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// All {@code MessageBodyWriter} implementations are advised to return {@code -1}
		return -1;
	}

	@Override
	public void writeTo(List<String> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		if (t != null) {
			entityStream.write("[".getBytes());
			boolean firstEntry = true;
			for (String entry : t) {
				if (firstEntry) {
					firstEntry = false;
				} else {
					entityStream.write(",".getBytes());
				}
				entityStream.write(("\""+entry+"\"").getBytes());
			}
			entityStream.write("]".getBytes());
		}
	}
}
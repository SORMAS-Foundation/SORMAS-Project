package de.symeda.sormas.backend.news;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Collections;

import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.news.eios.EiosConfig;
import de.symeda.sormas.backend.sormastosormas.rest.auth.Oidc;
import de.symeda.sormas.backend.util.ClientHelper;

public class EIOSRestClient {

	private final String eiosRestUrlTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(EIOSRestClient.class);
	private static final int BODY_LOG_LIMIT = 2000;
	private final ObjectMapper mapper;
	private final EiosConfig eiosConfig;

	public EIOSRestClient(EiosConfig eiosConfig) {
		this.eiosConfig = eiosConfig;
		this.eiosRestUrlTemplate = eiosConfig.getEiosUrl() + "/%s";
		mapper = new ObjectMapper();
		JavaTimeModule timeModule = new JavaTimeModule();
		timeModule.addDeserializer(ZonedDateTime.class, new CustomZonedDateTimeDeserializer());
		mapper.registerModule(timeModule);
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public <T> T post(String endpoint, Object body, Class<T> responseType) throws Exception {
		return sendRequest(endpoint, body, responseType, HttpMethod.POST);

	}

	public <T> T put(String endpoint, Object body, Class<T> responseType) throws Exception {
		return sendRequest(endpoint, body, responseType, HttpMethod.PUT);
	}

	public <T> T get(String endpoint, Class<T> responseType) throws Exception {
		return sendRequest(endpoint, null, responseType, HttpMethod.GET);
	}

	private String buildAuthToken() throws Exception {
		String authToken;
		try {
			authToken = Oidc.requestAccessToken(
				eiosConfig.getOidUrl(),
				eiosConfig.getOidcClientId(),
				eiosConfig.getOidcClientSecret(),
				Collections.singletonList(eiosConfig.getOidScope()));
		} catch (Exception e) {
			LOGGER.info("Could not requested access token {}", e);
			throw new Exception(Strings.errorEiosRequestToken);
		}
		return String.format("Bearer %s", authToken);
	}

	private Invocation.Builder buildRestClient(String endpoint) throws Exception {

		String authToken = buildAuthToken();
		// the body is read as plain text and parsed with our own mapper, so EIOS may answer with any content type
		return ClientHelper.newBuilderWithProxy()
			.register(new StringEntityReader())
			.build()
			.target(String.format(eiosRestUrlTemplate, endpoint))
			.request()
			.header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN)
			.header("Accept-Terms", "true")
			.header("X-TermS-Accepted", "true")
			.header(HttpHeaders.AUTHORIZATION, authToken);
	}

	private <T> T sendRequest(String endpoint, Object body, Class<T> responseType, String method) throws Exception {
		try {
			Entity<String> entity = null;
			if (body != null) {
				entity = Entity.entity(mapper.writeValueAsString(body), MediaType.APPLICATION_JSON_TYPE);
			} else {
				// safely append the parameter
				endpoint = UriBuilder.fromUri(endpoint).build().toString();
			}

			Invocation.Builder invocation = buildRestClient(endpoint);

			try (Response response = executeRequest(method, invocation, entity)) {
				return handleResponse(response, responseType);
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Unable to serialize EIOS request", e);
			throw e;
		} catch (ResponseProcessingException e) {
			LOGGER.error("Unable to process EIOS response", e);
			throw e;
		} catch (ProcessingException e) {
			if (e.getCause() != null && ConnectException.class.isAssignableFrom(e.getCause().getClass())) {
				LOGGER.error("Connection error while sending request to EIOS", e);
			} else {
				LOGGER.error("Unable to send request to EIOS", e);
			}
			throw e;
		}
	}

	private Response executeRequest(String method, Invocation.Builder invocation, Entity<String> entity) throws Exception {
		switch (method) {
		case HttpMethod.POST:
			return invocation.post(entity);
		case HttpMethod.PUT:
			return invocation.put(entity);
		case HttpMethod.GET:
			return invocation.get();
		default:
			throw new Exception(Strings.errorSormasToSormasInvalidRequestMethod);
		}
	}

	private <T> T handleResponse(Response response, Class<T> responseType) {
		int statusCode = response.getStatus();
		if (statusCode == HttpStatus.SC_NO_CONTENT || responseType == null) {
			LOGGER.info("Received response with status code {} and no content", statusCode);
			return null;
		}
		if (statusCode != HttpStatus.SC_OK) {
			String responseBody = readResponseBodySafe(response);
			if (statusCode != HttpStatus.SC_BAD_REQUEST) {
				LOGGER.error("Sending request failed with status code {} and body {}", statusCode, abbreviate(responseBody));
			}
			throw new WebApplicationException(
				"EIOS request failed with status " + statusCode + (responseBody != null ? " body: " + abbreviate(responseBody) : ""),
				statusCode);
		}

		String responseBody = readResponseBodySafe(response);
		if (responseBody == null || responseBody.trim().isEmpty()) {
			return null;
		}

		if (responseType == String.class) {
			return responseType.cast(responseBody);
		}

		try {
			return mapper.readValue(responseBody, responseType);
		} catch (IOException e) {
			// never put the whole body into the message - EIOS pages are megabytes and would bury the actual cause
			throw new RuntimeException(
				String.format(
					"Unable to parse response body as %s: %s (body starts with: %s)",
					responseType.getSimpleName(),
					e.getMessage(),
					abbreviate(responseBody)),
				e);
		}
	}

	private static String abbreviate(String body) {
		if (body == null) {
			return null;
		}
		return body.length() <= BODY_LOG_LIMIT ? body : body.substring(0, BODY_LOG_LIMIT) + "... [" + body.length() + " chars total]";
	}

	private String readResponseBodySafe(Response response) {
		try {
			if (!response.hasEntity()) {
				return null;
			}
			response.bufferEntity();
			return response.readEntity(String.class);
		} catch (Exception e) {
			LOGGER.warn("Unable to read response body; status={}, mediaType={}", response.getStatus(), response.getMediaType(), e);
			return null;
		}
	}

	@Provider
	@Consumes(MediaType.WILDCARD)
	private static final class StringEntityReader implements MessageBodyReader<String> {

		@Override
		public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
			return String.class.equals(type);
		}

		@Override
		public String readFrom(
			Class<String> type,
			Type genericType,
			Annotation[] annotations,
			MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream)
			throws IOException {
			return IOUtils.toString(entityStream, StandardCharsets.UTF_8);
		}
	}
}

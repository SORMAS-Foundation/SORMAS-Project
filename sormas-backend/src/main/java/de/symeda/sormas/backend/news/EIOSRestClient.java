package de.symeda.sormas.backend.news;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Collections;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.news.eios.EiosConfig;
import de.symeda.sormas.backend.sormastosormas.rest.auth.Oidc;
import de.symeda.sormas.backend.util.ClientHelper;

public class EIOSRestClient {

	private final String eiosRestUrlTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(EIOSRestClient.class);
	private final ObjectMapper mapper;
	private final EiosConfig eiosConfig;

	public EIOSRestClient(EiosConfig eiosConfig) {
		this.eiosConfig = eiosConfig;
		this.eiosRestUrlTemplate = eiosConfig.getEiosUrl() + "/%s";
		mapper = new ObjectMapper();
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
		LOGGER.info("Successfully requested access token");
		return String.format("Bearer %s", authToken);
	}

	private Invocation.Builder buildRestClient(String endpoint) throws Exception {

		String host = "";
		String authToken = buildAuthToken();

		return ClientHelper.newBuilderWithProxy()
			.register(JacksonJsonProvider.class)
			.build()
			.target(String.format(eiosRestUrlTemplate, endpoint))
			.request()
			.header("Authorization", authToken);
	}

	private <T> T sendRequest(String endpoint, Object body, Class<T> responseType, String method) throws Exception {
		try {
			Entity<String> entity = null;
			if (body != null) {
				entity = Entity.entity(mapper.writeValueAsString(body), MediaType.APPLICATION_JSON_TYPE);
			} else {
				// no sender org id in the encrypted DTP, therefore, we pass it as query parameter

				// safely append the parameter
				endpoint = UriBuilder.fromUri(endpoint).build().toString();
			}

			Invocation.Builder invocation = buildRestClient(endpoint);

			Response response;
			switch (method) {
			case HttpMethod.POST:
				response = invocation.post(entity);
				break;
			case HttpMethod.PUT:
				response = invocation.put(entity);
				break;
			case HttpMethod.GET:
				response = invocation.get();
				break;
			default:
				throw new Exception(Strings.errorSormasToSormasInvalidRequestMethod);
			}
			return handleResponse(response, responseType);
		} catch (JsonProcessingException e) {
			LOGGER.error("Unable to send data sormas", e);
			throw e;
		} catch (ResponseProcessingException e) {
			LOGGER.error("Unable to process sormas response", e);
			throw e;
		} catch (ProcessingException e) {
			LOGGER.error("Unable to send data to sormas", e);

			String processingErrorStringProperty = Strings.errorSormasToSormasSend;
			if (ConnectException.class.isAssignableFrom(e.getCause().getClass())) {
				processingErrorStringProperty = Strings.errorSormasToSormasConnection;
			}
			throw e;
		}
	}

	private <T> T handleResponse(Response response, Class<T> responseType) {
		int statusCode = response.getStatus();
		if (statusCode != HttpStatus.SC_NO_CONTENT && statusCode != HttpStatus.SC_OK) {
			String errorMessage = response.readEntity(String.class);
			try {
				T responseObject = mapper.readValue(errorMessage, responseType);
				return responseObject;
			} catch (IOException e) {
				// do nothing, keep the unparsed response as error message
			}

			if (statusCode != HttpStatus.SC_BAD_REQUEST) {
				// don't log validation errors, will be displayed on the UI
				LOGGER.error("Sending request failed: {}; {}", statusCode, errorMessage);
			}
		}
		return responseType != null ? response.readEntity(responseType) : null;

	}
}

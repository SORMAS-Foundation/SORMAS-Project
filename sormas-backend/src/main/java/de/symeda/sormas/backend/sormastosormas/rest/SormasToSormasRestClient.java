/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.sormastosormas.rest;

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.SORMAS_REST_PATH;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasErrorResponse;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.crypto.SormasToSormasEncryptionFacadeEjb.SormasToSormasEncryptionFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.access.SormasToSormasDiscoveryService;
import de.symeda.sormas.backend.sormastosormas.rest.auth.Oidc;
import de.symeda.sormas.backend.util.ClientHelper;

/**
 * Meant to be instantiated using SormasToSormasRestClientProducer.
 * We are not using a LocalBean here, because we need to be able to mock this class for unit testing.
 * Alternatively, we could use Apache DeltaSpike: https://deltaspike.apache.org/documentation/test-control.html#MockFrameworks
 */
public class SormasToSormasRestClient {

	public static final String SORMAS_REST_URL_TEMPLATE = "https://%s" + SORMAS_REST_PATH + "%s";
	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasRestClient.class);

	private final SormasToSormasDiscoveryService sormasToSormasDiscoveryService;
	private final SormasToSormasEncryptionFacadeEjbLocal sormasToSormasEncryptionEjb;
	private final ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;

	private final ObjectMapper mapper;

	public SormasToSormasRestClient(
		SormasToSormasDiscoveryService sormasToSormasDiscoveryService,
		SormasToSormasEncryptionFacadeEjbLocal sormasToSormasEncryptionEjb,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb) {
		this.sormasToSormasDiscoveryService = sormasToSormasDiscoveryService;
		this.sormasToSormasEncryptionEjb = sormasToSormasEncryptionEjb;
		this.configFacadeEjb = configFacadeEjb;

		mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	}

	public <T> T post(String receiverId, String endpoint, Object body, Class<T> responseType) throws SormasToSormasException {
		return sendRequest(receiverId, endpoint, body, responseType, HttpMethod.POST);

	}

	public <T> T put(String receiverId, String endpoint, Object body, Class<T> responseType) throws SormasToSormasException {
		return sendRequest(receiverId, endpoint, body, responseType, HttpMethod.PUT);
	}

	public <T> T get(String receiverId, String endpoint, Class<T> responseType) throws SormasToSormasException {
		return sendRequest(receiverId, endpoint, null, responseType, HttpMethod.GET);
	}

	private String buildAuthToken(String targetId) throws SormasToSormasException {
		String scope = String.format("s2s-%s", targetId);
		String authToken;
		try {
			SormasToSormasConfig sormasToSormasConfig = configFacadeEjb.getS2SConfig();
			authToken = Oidc.requestAccessToken(
				sormasToSormasConfig.getOidcRealmTokenEndpoint(),
				sormasToSormasConfig.getOidcClientId(),
				sormasToSormasConfig.getOidcClientSecret(),
				Collections.singletonList(scope));
		} catch (Exception e) {
			LOGGER.info("Could not requested access token for {}: {}", targetId, e);
			throw  SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRequestToken);
		}
		LOGGER.info(String.format("Successfully requested access token for %s", targetId));
		return String.format("Bearer %s", authToken);
	}

	private Invocation.Builder buildRestClient(String receiverId, String endpoint) throws SormasToSormasException {
		SormasServerDescriptor targetServerDescriptor = sormasToSormasDiscoveryService.getSormasServerDescriptorById(receiverId);
		if (targetServerDescriptor == null) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasServerAccess);
		}

		String host = targetServerDescriptor.getHostName();
		String authToken = buildAuthToken(targetServerDescriptor.getId());

		return ClientHelper.newBuilderWithProxy()
			.build()
			.target(String.format(SORMAS_REST_URL_TEMPLATE, host, endpoint))
			.request()
			.header("Authorization", authToken);
	}

	private <T> T sendRequest(String receiverId, String endpoint, Object body, Class<T> responseType, String method) throws SormasToSormasException {
		try {
			Entity<String> entity = null;
			if (body != null) {
				SormasToSormasEncryptedDataDto encryptedBody = sormasToSormasEncryptionEjb.signAndEncrypt(body, receiverId);
				entity = Entity.entity(mapper.writeValueAsString(encryptedBody), MediaType.APPLICATION_JSON_TYPE);
			} else {
				// no sender org id in the encrypted DTP, therefore, we pass it as query parameter
				String ownId = configFacadeEjb.getS2SConfig().getId();

				// safely append the parameter
				endpoint = UriBuilder.fromUri(endpoint).queryParam(SormasToSormasConfig.SENDER_SERVER_ID, ownId).build().toString();
			}

			Invocation.Builder invocation = buildRestClient(receiverId, endpoint);

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
				throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasInvalidRequestMethod);
			}
			return handleResponse(response, responseType);
		} catch (JsonProcessingException e) {
			LOGGER.error("Unable to send data sormas", e);
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasSend);
		} catch (ResponseProcessingException e) {
			LOGGER.error("Unable to process sormas response", e);
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasResult);
		} catch (ProcessingException e) {
			LOGGER.error("Unable to send data to sormas", e);

			String processingErrorStringProperty = Strings.errorSormasToSormasSend;
			if (ConnectException.class.isAssignableFrom(e.getCause().getClass())) {
				processingErrorStringProperty = Strings.errorSormasToSormasConnection;
			}
			throw SormasToSormasException.fromStringProperty(processingErrorStringProperty);
		}
	}

	private <T> T handleResponse(Response response, Class<T> responseType) throws SormasToSormasException {
		int statusCode = response.getStatus();
		if (statusCode != HttpStatus.SC_NO_CONTENT && statusCode != HttpStatus.SC_OK) {
			String errorMessage = response.readEntity(String.class);
			String errorI18nTag = null;
			List<ValidationErrors> errors = null;
			Object[] args = null;

			try {
				SormasToSormasErrorResponse errorResponse = mapper.readValue(errorMessage, SormasToSormasErrorResponse.class);

				errorMessage = Optional.ofNullable(errorResponse.getMessage()).orElse(I18nProperties.getString(Strings.errorSormasToSormasSend));
				errorI18nTag = Optional.ofNullable(errorResponse.getI18nTag()).orElse(Strings.errorSormasToSormasShare);
				errors = errorResponse.getErrors();
				args = errorResponse.getArgs();
			} catch (IOException e) {
				// do nothing, keep the unparsed response as error message
			}

			if (statusCode != HttpStatus.SC_BAD_REQUEST) {
				// don't log validation errors, will be displayed on the UI
				LOGGER.error("Sending request failed: {}; {}", statusCode, errorMessage);
			}
			throw new SormasToSormasException(errorMessage, false, errors, errorI18nTag, args);
		}
		return responseType != null ? response.readEntity(responseType) : null;

	}
}

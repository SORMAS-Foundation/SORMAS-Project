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

package de.symeda.sormas.backend.externaljournal;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

import de.symeda.sormas.api.externaljournal.ExternalJournalSyncResponseDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryPersonDto;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryQueryResponse;
import de.symeda.sormas.api.externaljournal.patientdiary.PatientDiaryResult;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.util.ClientHelper;

@Stateless
@LocalBean
public class PatientDiaryClient {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String EMAIL_QUERY_PARAM = "Email";
	public static final String MOBILE_PHONE_QUERY_PARAM = "Mobile phone";

	private static final String PATIENT_DIARY_KEY = "patientDiary";
	private static final Cache<String, String> backendAuthTokenCache = CacheBuilder.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).build();
	private static final Cache<String, String> frontendAuthTokenCache = CacheBuilder.newBuilder().expireAfterWrite(6, TimeUnit.HOURS).build();

	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	/**
	 * Attempts to register a new patient in the CLIMEDO patient diary.
	 * Sets the person symptom journal status to REGISTERED if successful.
	 *
	 * @param personUuid
	 *            the UUID of the person to register as a patient in the external journal
	 * @param callback
	 *            callback to execute after successfully register in the external journal
	 * @return true if the registration was successful, false otherwise
	 */
	public PatientDiaryResult registerPatientDiaryPerson(String personUuid, Runnable callback) {
		try {
			Invocation.Builder invocationBuilder = getExternalDataPersonInvocationBuilder(personUuid);
			Response response = invocationBuilder.post(Entity.json(""));
			String responseJson = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readValue(responseJson, JsonNode.class);
			boolean success = node.get("success").booleanValue();
			String message = node.get("message").textValue();
			if (!success) {
				logger.warn("Could not create new patient diary person: " + message);
			} else {
				logger.info("Successfully registered patient " + personUuid + " in patient diary.");
				callback.run();
			}
			return new PatientDiaryResult(success, message);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return new PatientDiaryResult(false, e.getMessage());
		}
	}

	public ExternalJournalSyncResponseDto notifyPatientDiary(String personUuid) {
		try {
			Invocation.Builder invocationBuilder = getExternalDataPersonInvocationBuilder(personUuid);
			Response response = invocationBuilder.put(Entity.json(""));
			String responseJson = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			ExternalJournalSyncResponseDto responseDto = mapper.readValue(responseJson, ExternalJournalSyncResponseDto.class);

			if (!responseDto.isSuccess()) {
				logger.warn("Could not notify patient diary of person update: {}", responseDto.getMessage());
			} else {
				if (!responseDto.getErrors().isEmpty()) {
					logger.warn("The changes were just partially synchronized: {}", responseDto.getErrors().values());
				} else {
					logger.info("Successfully notified patient diary to update patient {}", personUuid);
				}
			}
			return responseDto;
		} catch (IOException e) {
			logger.error("Could not notify patient diary: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieves the person from the external patient diary with the given uuid
	 *
	 * @param personUuid
	 *            the uuid of the person to be retrieved
	 * @return optional containing the person
	 */
	public Optional<PatientDiaryPersonDto> getPatientDiaryPerson(String personUuid) {
		try {
			Invocation.Builder invocationBuilder = getExternalDataPersonInvocationBuilder(personUuid);
			Response response = invocationBuilder.get();
			if (response.getStatus() == HttpStatus.SC_NOT_FOUND) {
				return Optional.empty();
			}
			String responseJson = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readValue(responseJson, JsonNode.class);
			JsonNode idatData = node.get("idatData");
			PatientDiaryPersonDto personDto = mapper.treeToValue(idatData, PatientDiaryPersonDto.class);
			String endDate = node.get("endDate").textValue();
			personDto.setEndDate(endDate);
			return Optional.of(personDto);
		} catch (IOException e) {
			logger.error("Could not retrieve patient: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Attempts to cancel the follow up of a patient in the CLIMEDO patient diary.
	 * Sets the person symptom journal status to DELETED if successful.
	 *
	 * @param personUuid
	 *            the UUID of the person whose follow-up will be cancelled in the external journal
	 * @param callback
	 *            callback to execute after the follow-up is successfully cancelled in the external journal
	 * @return {@link PatientDiaryResult PatientDiaryResult} containing details about the result
	 */
	public PatientDiaryResult deletePatientDiaryPerson(String personUuid, Runnable callback) {
		try {
			Invocation.Builder invocationBuilder = getExternalDataPersonInvocationBuilder(personUuid);
			Response response = invocationBuilder.delete();
			String responseJson = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readValue(responseJson, JsonNode.class);
			boolean success = node.get("success").booleanValue();
			String message = node.get("message").textValue();
			if (!success) {
				logger.warn("Could not cancel follow-up for patient diary person: " + message);
			} else {
				logger.info("Successfully cancelled follow-up for person " + personUuid + " in patient diary.");
				callback.run();
			}
			return new PatientDiaryResult(success, message);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return new PatientDiaryResult(false, e.getMessage());
		}
	}

	/**
	 * Queries the CLIMEDO patients for ones matching the given property
	 *
	 * @param key
	 *            the name of the property to match
	 * @param value
	 *            the value of the property to match
	 * @return result of query
	 */
	public Optional<PatientDiaryQueryResponse> queryPatientDiary(String key, String value) {
		try {
			String probandsUrl = configFacade.getPatientDiaryConfig().getProbandsUrl() + "/probands";
			String queryParam = "\"" + key + "\" = \"" + value + "\"";
			String encodedParams = URLEncoder.encode(queryParam, StandardCharsets.UTF_8.toString());
			String fullUrl = probandsUrl + "?q=" + encodedParams;
			Client client = ClientHelper.newBuilderWithProxy().build();
			Response response =
				client.target(fullUrl).request(MediaType.APPLICATION_JSON).header("x-access-token", getPatientDiaryAuthToken(false)).get();
			if (response.getStatus() == HttpStatus.SC_NOT_FOUND) {
				return Optional.empty();
			}
			return Optional.ofNullable(response.readEntity(PatientDiaryQueryResponse.class));
		} catch (IOException e) {
			logger.error("Could not retrieve patient query response: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private Invocation.Builder getExternalDataPersonInvocationBuilder(String personUuid) {
		String externalDataUrl = configFacade.getPatientDiaryConfig().getProbandsUrl() + "/external-data/" + personUuid;
		Client client = ClientHelper.newBuilderWithProxy().build();
		return client.target(externalDataUrl).request(MediaType.APPLICATION_JSON).header("x-access-token", getPatientDiaryAuthToken(false));
	}

	/**
	 * Retrieves a token used for authenticating in the patient diary. The token will be cached.
	 * 
	 * @param frontendRequest
	 *            if true && a interface.patientdiary.frontendAuthurl is configured, this url is used to fetch the token.
	 *            Otherwise, the interface.patientdiary.authurl is used.
	 * @return the authentication token
	 */
	public String getPatientDiaryAuthToken(boolean frontendRequest) {
		try {
			if (frontendRequest && StringUtils.isNotBlank(configFacade.getPatientDiaryConfig().getFrontendAuthUrl())) {
				return frontendAuthTokenCache.get(PATIENT_DIARY_KEY, this::getPatientDiaryFrontendAuthTokenInternal);
			} else {
				return backendAuthTokenCache.get(PATIENT_DIARY_KEY, this::getPatientDiaryAuthTokenInternal);
			}
		} catch (ExecutionException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	private String getPatientDiaryFrontendAuthTokenInternal() {
		String authenticationUrl = configFacade.getPatientDiaryConfig().getFrontendAuthUrl();
		return executeTokenRequest(authenticationUrl);
	}

	private String getPatientDiaryAuthTokenInternal() {
		String authenticationUrl = configFacade.getPatientDiaryConfig().getAuthUrl();
		if (StringUtils.isBlank(authenticationUrl)) {
			throw new IllegalArgumentException("Property interface.patientdiary.authurl is not defined");
		}
		return executeTokenRequest(authenticationUrl);
	}

	private String executeTokenRequest(String authenticationUrl) {
		String email = configFacade.getPatientDiaryConfig().getEmail();
		String pass = configFacade.getPatientDiaryConfig().getPassword();

		if (StringUtils.isBlank(email)) {
			throw new IllegalArgumentException("Property interface.patientdiary.email is not defined");
		}
		if (StringUtils.isBlank(pass)) {
			throw new IllegalArgumentException("Property interface.patientdiary.password is not defined");
		}

		try {
			Client client = ClientHelper.newBuilderWithProxy().build();
			WebTarget webTarget = client.target(authenticationUrl);
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
			Response response = invocationBuilder.post(Entity.json(ImmutableMap.of("email", email, "password", pass)));

			String responseJson = response.readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readValue(responseJson, JsonNode.class);
			boolean success = node.get("success").booleanValue();
			if (!success) {
				throw new ExternalJournalException("Could not log in to patient diary with provided email and password");
			}
			return node.get("token").textValue();
		} catch (IOException e) {
			logger.error("Could not retrieve patient auth token, {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}

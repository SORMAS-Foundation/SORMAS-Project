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

package de.symeda.sormas.backend.sormastosormas;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasErrorResponse;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.backend.common.StartupShutdownService;

@Stateless
@LocalBean
public class SormasToSormasFacadeHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasFacadeHelper.class);
	@EJB
	private ServerAccessDataService serverAccessDataService;
	@EJB
	protected SormasToSormasEncryptionService encryptionService;

	private final ObjectMapper objectMapper;

	public SormasToSormasFacadeHelper() {
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	}

	public void sendEntitiesToSormas(List<?> entities, SormasToSormasOptionsDto options, RestCall restCall) throws SormasToSormasException {

		OrganizationServerAccessData serverAccessData = serverAccessDataService.getServerAccessData()
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));
		OrganizationServerAccessData targetServerAccessData = getOrganizationServerAccessData(options.getOrganization().getUuid())
			.orElseThrow(() -> new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasServerAccess)));

		String userCredentials = StartupShutdownService.SORMAS_TO_SORMAS_USER_NAME + ":" + targetServerAccessData.getRestUserPassword();

		Response response;
		try {
			byte[] encryptedEntities = encryptionService.encrypt(objectMapper.writeValueAsBytes(entities), targetServerAccessData.getId());
			response = restCall.call(
				targetServerAccessData.getHostName(),
				"Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8),
				new SormasToSormasEncryptedDataDto(serverAccessData.getId(), encryptedEntities));
		} catch (JsonProcessingException e) {
			LOGGER.error("Unable to send data sormas", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasSend));
		} catch (ResponseProcessingException e) {
			LOGGER.error("Unable to process sormas response", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasResult));
		} catch (ProcessingException e) {
			LOGGER.error("Unable to send data to sormas", e);

			String processingErrorMessage = I18nProperties.getString(Strings.errorSormasToSormasSend);
			if (ConnectException.class.isAssignableFrom(e.getCause().getClass())) {
				processingErrorMessage = I18nProperties.getString(Strings.errorSormasToSormasConnection);
			}

			throw new SormasToSormasException(processingErrorMessage);
		}

		int statusCode = response.getStatus();
		if (statusCode != HttpStatus.SC_NO_CONTENT) {
			String errorMessage = response.readEntity(String.class);
			Map<String, ValidationErrors> errors = null;

			try {
				SormasToSormasErrorResponse errorResponse = objectMapper.readValue(errorMessage, SormasToSormasErrorResponse.class);
				errorMessage = I18nProperties.getString(Strings.errorSormasToSormasShare);
				errors = errorResponse.getErrors();
			} catch (IOException e) {
				// do nothing, keep the unparsed response as error message
			}

			if (statusCode != HttpStatus.SC_BAD_REQUEST) {
				// don't log validation errors, will be displayed on the UI
				LOGGER.error("Share case failed: {}; {}", statusCode, errorMessage);
			}

			throw new SormasToSormasException(errorMessage, errors);
		}
	}

	public <T> T[] decryptSharedData(SormasToSormasEncryptedDataDto encryptedData, Class<T[]> dataType) throws SormasToSormasException {
		try {
			byte[] decryptedData = encryptionService.decrypt(encryptedData.getData(), encryptedData.getOrganizationId());

			return objectMapper.readValue(decryptedData, dataType);
		} catch (IOException e) {
			LOGGER.error("Can't parse shared data", e);
			throw new SormasToSormasException(I18nProperties.getString(Strings.errorSormasToSormasDecrypt));
		}
	}

	public Optional<OrganizationServerAccessData> getOrganizationServerAccessData(String id) {
		return serverAccessDataService.getServerListItemById(id);
	}

	public interface RestCall {

		Response call(String host, String authToken, SormasToSormasEncryptedDataDto encryptedData) throws JsonProcessingException;
	}
}

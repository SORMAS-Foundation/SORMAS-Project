/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.entities.labmessage;

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.LAB_MESSAGE_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildLabMessageValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.labmessage.SormasToSormasLabMessageDto;
import de.symeda.sormas.api.sormastosormas.labmessage.SormasToSormasLabMessageFacade;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.labmessage.LabMessage;
import de.symeda.sormas.backend.labmessage.LabMessageFacadeEjb.LabMessageFacadeEjbLocal;
import de.symeda.sormas.backend.labmessage.LabMessageService;
import de.symeda.sormas.backend.sormastosormas.ValidationHelper;
import de.symeda.sormas.backend.sormastosormas.crypto.SormasToSormasEncryptionFacadeEjb.SormasToSormasEncryptionFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.rest.SormasToSormasRestClient;

@Stateless(name = "SormasToSormasLabMessageFacade")
public class SormasToSormasLabMessageFacadeEjb implements SormasToSormasLabMessageFacade {

	public static final String SAVE_SHARED_LAB_MESSAGE_ENDPOINT = RESOURCE_PATH + LAB_MESSAGE_ENDPOINT;

	@EJB
	private LabMessageService labMessageService;
	@EJB
	private LabMessageFacadeEjbLocal labMessageFacade;
	@Inject
	private SormasToSormasRestClient sormasToSormasRestClient;
	@EJB
	private SormasToSormasEncryptionFacadeEjbLocal sormasToSormasEncryptionEjb;
	@EJB
	private SormasToSormasLabMessageDtoValidator dtoValidator;

	@Override
	public void sendLabMessages(List<String> uuids, SormasToSormasOptionsDto options) throws SormasToSormasException {
		List<LabMessage> labMessages = labMessageService.getByUuids(uuids);
		List<SormasToSormasLabMessageDto> dtos =
			labMessages.stream().map(labMessageFacade::toDto).map(SormasToSormasLabMessageDto::new).collect(Collectors.toList());
		List<ValidationErrors> validationErrors = new ArrayList<>();
		for (SormasToSormasLabMessageDto dto : dtos) {
			ValidationErrors labMessageError = dtoValidator.validateOutgoing(dto);
			if (labMessageError.hasError()) {
				validationErrors.add(new ValidationErrors(ValidationHelper.buildLabMessageValidationGroupName(dto.getEntity()), labMessageError));
			}
		}

		if (!validationErrors.isEmpty()) {
			throw SormasToSormasException.fromStringProperty(validationErrors, Strings.errorSormasToSormasShare);
		}

		sormasToSormasRestClient.post(options.getOrganization().getId(), SAVE_SHARED_LAB_MESSAGE_ENDPOINT, dtos, null);

		labMessages.forEach(labMessage -> {
			labMessage.setStatus(LabMessageStatus.FORWARDED);
			labMessageService.ensurePersisted(labMessage);
		});
	}

	@Override
	public void saveLabMessages(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasLabMessageDto[] sharedLabMessages =
			sormasToSormasEncryptionEjb.decryptAndVerify(encryptedData, SormasToSormasLabMessageDto[].class);

		List<ValidationErrors> validationErrors = new ArrayList<>();
		List<LabMessageDto> labMessagesToSave = new ArrayList<>(sharedLabMessages.length);

		for (SormasToSormasLabMessageDto labMessage : sharedLabMessages) {
			ValidationErrors errors = validateSharedLabMessage(labMessage);
			if (errors.hasError()) {
				errors.setGroup(buildLabMessageValidationGroupName(labMessage.getEntity()));
				validationErrors.add(errors);
			}
			labMessagesToSave.add(labMessage.getEntity());
		}

		if (!validationErrors.isEmpty()) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (LabMessageDto labMessage : labMessagesToSave) {
			handleValidationError(() -> labMessageFacade.save(labMessage), Captions.LabMessage, buildLabMessageValidationGroupName(labMessage));
		}
	}

	private ValidationErrors validateSharedLabMessage(SormasToSormasLabMessageDto dto) throws ValidationRuntimeException {
		ValidationErrors errors = new ValidationErrors();
		if (labMessageFacade.exists(dto.getEntity().getUuid())) {
			errors.add(new ValidationErrorGroup(Captions.LabMessage), new ValidationErrorMessage(Validations.sormasToSormasLabMessageExists));
		}

		errors.addAll(dtoValidator.validateIncoming(dto));
		return errors;
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasLabMessageFacadeEjbLocal extends SormasToSormasLabMessageFacadeEjb {

	}
}

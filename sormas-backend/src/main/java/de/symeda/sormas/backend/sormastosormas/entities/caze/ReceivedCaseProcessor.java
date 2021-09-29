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

package de.symeda.sormas.backend.sormastosormas.entities.caze;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasDataValidator;

@Stateless
@LocalBean
public class ReceivedCaseProcessor
	implements ReceivedDataProcessor<CaseDataDto, SormasToSormasCaseDto, ProcessedCaseData, SormasToSormasCasePreview> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private InfrastructureValidator infraValidator;

	@Override
	public ProcessedCaseData processReceivedData(SormasToSormasCaseDto receivedCase, CaseDataDto existingCaseData)
		throws SormasToSormasValidationException {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		PersonDto person = receivedCase.getPerson();
		CaseDataDto caze = receivedCase.getEntity();
		List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts = receivedCase.getAssociatedContacts();
		List<SormasToSormasSampleDto> samples = receivedCase.getSamples();
		SormasToSormasOriginInfoDto originInfo = receivedCase.getOriginInfo();

		ValidationErrors caseValidationErrors = new ValidationErrors();

		ValidationErrors originInfoErrorsErrors = dataValidator.validateOriginInfo(originInfo, Captions.CaseData);
		caseValidationErrors.addAll(originInfoErrorsErrors);

		ValidationErrors caseDataErrors = dataValidator.validateCaseData(caze, person, existingCaseData);
		caseValidationErrors.addAll(caseDataErrors);

		if (caseValidationErrors.hasError()) {
			validationErrors.add(new ValidationErrors(buildCaseValidationGroupName(caze), caseValidationErrors));
		}

		if (associatedContacts != null && associatedContacts.size() > 0) {
			List<ValidationErrors> contactValidationErrors = dataValidator.validateAssociatedContacts(associatedContacts);
			validationErrors.addAll(contactValidationErrors);
		}

		if (samples != null && samples.size() > 0) {
			List<ValidationErrors> sampleErrors = dataValidator.validateSamples(samples);
			validationErrors.addAll(sampleErrors);
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		return new ProcessedCaseData(person, caze, associatedContacts, samples, originInfo);
	}

	@Override
	public SormasToSormasCasePreview processReceivedPreview(SormasToSormasCasePreview preview) throws SormasToSormasValidationException {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		ValidationErrors caseValidationErrors = dataValidator.validateCasePreview(preview);

		if (caseValidationErrors.hasError()) {
			validationErrors.add(new ValidationErrors(buildCaseValidationGroupName(preview), caseValidationErrors));
		}

		ValidationErrors personValidationErrors = dataValidator.validatePersonPreview(preview.getPerson());
		caseValidationErrors.addAll(personValidationErrors);

		List<SormasToSormasContactPreview> contacts = preview.getContacts();
		if (contacts != null && contacts.size() > 0) {
			List<ValidationErrors> contactValidationErrors = dataValidator.validateContactPreviews(contacts);
			validationErrors.addAll(contactValidationErrors);
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}
		return preview;
	}
}

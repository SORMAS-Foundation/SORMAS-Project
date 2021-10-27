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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;

@Stateless
@LocalBean
public class ReceivedCaseProcessor extends ReceivedDataProcessor<CaseDataDto, SormasToSormasCaseDto, SormasToSormasCasePreview, Case> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;
	@EJB
	private CaseService caseService;

	@Override
	public void handleReceivedData(SormasToSormasCaseDto sharedData, Case existingCase) {
		dataValidator.handleIgnoredProperties(sharedData.getEntity(), CaseFacadeEjb.toDto(existingCase));
		dataValidator.handleIgnoredProperties(sharedData.getPerson(), dataValidator.getExitingPerson(existingCase));
	}

	@Override
	public ValidationErrors exists(String uuid) {
		ValidationErrors errors = new ValidationErrors();
		if (caseService.exists(
			(cb, caseRoot, cq) -> cb.and(
				cb.equal(caseRoot.get(AbstractDomainObject.UUID), uuid),
				cb.isNull(caseRoot.get(Case.SORMAS_TO_SORMAS_ORIGIN_INFO)),
				cb.isEmpty(caseRoot.get(Case.SORMAS_TO_SORMAS_SHARES))))) {
			errors.add(new ValidationErrorGroup(Captions.CaseData), new ValidationErrorMessage(Validations.sormasToSormasCaseExists));
		}

		return errors;
	}

	@Override
	public ValidationErrors validation(SormasToSormasCaseDto sharedData, Case existingData) {
		return dataValidator.validateCaseData(sharedData.getEntity(), sharedData.getPerson(), existingData);
	}

	@Override
	public ValidationErrors validatePreview(SormasToSormasCasePreview preview) {
		ValidationErrors validationErrors = dataValidator.validateCasePreview(preview);
		validationErrors.addAll(dataValidator.validatePersonPreview(preview.getPerson()));
		return validationErrors;
	}
}

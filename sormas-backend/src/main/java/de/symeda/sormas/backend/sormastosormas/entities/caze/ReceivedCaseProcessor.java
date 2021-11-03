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
import javax.inject.Inject;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;

@Stateless
@LocalBean
public class ReceivedCaseProcessor
	extends ReceivedDataProcessor<Case, CaseDataDto, SormasToSormasCaseDto, SormasToSormasCasePreview, Case, CaseService> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;

	public ReceivedCaseProcessor() {
	}

	@Inject
	protected ReceivedCaseProcessor(CaseService service) {
		super(service);
	}

	@Override
	public void handleReceivedData(SormasToSormasCaseDto sharedData, Case existingCase) {
		dataValidator.handleIgnoredProperties(sharedData.getEntity(), CaseFacadeEjb.toDto(existingCase));
		dataValidator.handleIgnoredProperties(sharedData.getPerson(), dataValidator.getExitingPerson(existingCase));

		CaseDataDto caze = sharedData.getEntity();
		PersonDto person = sharedData.getPerson();
		caze.setPerson(person.toReference());
		dataValidator.updateReportingUser(caze, existingCase);
	}

	@Override
	public ValidationErrors existsNotShared(String uuid) {
		return existsNotShared(
			uuid,
			Case.SORMAS_TO_SORMAS_ORIGIN_INFO,
			Case.SORMAS_TO_SORMAS_SHARES,
			Captions.CaseData,
			Validations.sormasToSormasCaseExists);
	}

	@Override
	public ValidationErrors validate(SormasToSormasCaseDto sharedData, Case existingData) {
		return dataValidator.validateCaseData(sharedData.getEntity(), sharedData.getPerson());
	}

	@Override
	public ValidationErrors validatePreview(SormasToSormasCasePreview preview) {
		ValidationErrors validationErrors = dataValidator.validateCasePreview(preview);
		validationErrors.addAll(dataValidator.validatePersonPreview(preview.getPerson()));
		return validationErrors;
	}
}

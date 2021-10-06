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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessorHelper;

@Stateless
@LocalBean
public class ReceivedCaseProcessor implements ReceivedDataProcessor<CaseDataDto, SormasToSormasCaseDto, SormasToSormasCasePreview, Case> {

	@EJB
	private ReceivedDataProcessorHelper dataProcessorHelper;
	@EJB
	private InfrastructureValidator infraValidator;
	@EJB
	private CaseService caseService;

	@Override
	public ValidationErrors processReceivedData(SormasToSormasCaseDto receivedCase, Case existingCase) {

		CaseDataDto caze = receivedCase.getEntity();
		PersonDto person = receivedCase.getPerson();

		ValidationErrors uuidError = validateSharedUuid(caze.getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}

		return processCaseData(caze, person, existingCase);
	}

	@Override
	public ValidationErrors processReceivedPreview(SormasToSormasCasePreview preview) {
		ValidationErrors uuidError = validateSharedUuid(preview.getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}

		ValidationErrors validationErrors = new ValidationErrors();

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(
				null,
				null,
				null,
				preview.getRegion(),
				preview.getDistrict(),
				preview.getCommunity(),
				preview.getFacilityType(),
				preview.getHealthFacility(),
				preview.getHealthFacilityDetails(),
				preview.getPointOfEntry(),
				preview.getPointOfEntryDetails());

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.CaseData, validationErrors, infrastructureData -> {
			preview.setRegion(infrastructureData.getRegion());
			preview.setDistrict(infrastructureData.getDistrict());
			preview.setCommunity(infrastructureData.getCommunity());
			preview.setHealthFacility(infrastructureData.getFacility());
			preview.setHealthFacilityDetails(infrastructureData.getFacilityDetails());
			preview.setPointOfEntry(infrastructureData.getPointOfEntry());
			preview.setPointOfEntryDetails(infrastructureData.getPointOfEntryDetails());
		});

		ValidationErrors personValidationErrors = dataProcessorHelper.processPersonPreview(preview.getPerson());
		if (personValidationErrors.hasError()) {
			validationErrors.addAll(personValidationErrors);
		}

		return validationErrors;
	}

	private ValidationErrors processCaseData(CaseDataDto caze, PersonDto person, Case existingCase) {
		ValidationErrors caseValidationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = dataProcessorHelper.processPerson(person);
		caseValidationErrors.addAll(personValidationErrors);

		caze.setPerson(person.toReference());
		dataProcessorHelper.updateReportingUser(caze, existingCase);

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(caze);

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.CaseData, caseValidationErrors, infrastructureData -> {
			caze.setResponsibleRegion(infrastructureData.getResponsibleRegion());
			caze.setResponsibleDistrict(infrastructureData.getResponsibleDistrict());
			caze.setResponsibleCommunity(infrastructureData.getResponsibleCommunity());
			caze.setRegion(infrastructureData.getRegion());
			caze.setDistrict(infrastructureData.getDistrict());
			caze.setCommunity(infrastructureData.getCommunity());
			caze.setHealthFacility(infrastructureData.getFacility());
			caze.setHealthFacilityDetails(infrastructureData.getFacilityDetails());
			caze.setPointOfEntry(infrastructureData.getPointOfEntry());
			caze.setPointOfEntryDetails(infrastructureData.getPointOfEntryDetails());
		});

		ValidationErrors embeddedObjectErrors = processEmbeddedObjects(caze);
		caseValidationErrors.addAll(embeddedObjectErrors);

		return caseValidationErrors;
	}

	private ValidationErrors processEmbeddedObjects(CaseDataDto caze) {
		ValidationErrors validationErrors = new ValidationErrors();

		if (caze.getHospitalization() != null) {

			caze.getHospitalization().getPreviousHospitalizations().forEach(ph -> {

				DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> phInfrastructureAndErrors =
					infraValidator.validateInfrastructure(
						null,
						null,
						null,
						ph.getRegion(),
						ph.getDistrict(),
						ph.getCommunity(),
						FacilityType.HOSPITAL,
						ph.getHealthFacility(),
						ph.getHealthFacilityDetails(),
						null,
						null);

				infraValidator.handleInfraStructure(
					phInfrastructureAndErrors,
					Captions.CaseHospitalization_previousHospitalizations,
					validationErrors,
					(phInfrastructure) -> {
						ph.setRegion(phInfrastructure.getRegion());
						ph.setDistrict(phInfrastructure.getDistrict());
						ph.setCommunity(phInfrastructure.getCommunity());
						ph.setHealthFacility(phInfrastructure.getFacility());
						ph.setHealthFacilityDetails(phInfrastructure.getFacilityDetails());
					});
			});
		}

		MaternalHistoryDto maternalHistory = caze.getMaternalHistory();
		if (maternalHistory != null) {

			DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> rashExposureInfrastructureAndErrors =
				infraValidator.validateInfrastructure(
					maternalHistory.getRashExposureRegion(),
					maternalHistory.getRashExposureDistrict(),
					maternalHistory.getRashExposureCommunity());

			infraValidator.handleInfraStructure(
				rashExposureInfrastructureAndErrors,
				Captions.MaternalHistory_rashExposure,
				validationErrors,
				(rashExposureInfrastructure) -> {
					maternalHistory.setRashExposureRegion(rashExposureInfrastructure.getRegion());
					maternalHistory.setRashExposureDistrict(rashExposureInfrastructure.getDistrict());
					maternalHistory.setRashExposureCommunity(rashExposureInfrastructure.getCommunity());
				});
		}

		dataProcessorHelper.processEpiData(caze.getEpiData(), validationErrors);

		return validationErrors;
	}

	private ValidationErrors validateSharedUuid(String uuid) {
		ValidationErrors errors = new ValidationErrors();
		if (caseService.exists(
			(cb, caseRoot) -> cb.and(
				cb.equal(caseRoot.get(Case.UUID), uuid),
				cb.isNull(caseRoot.get(Case.SORMAS_TO_SORMAS_ORIGIN_INFO)),
				cb.isEmpty(caseRoot.get(Case.SORMAS_TO_SORMAS_SHARES))))) {
			errors.add(new ValidationErrorGroup(Captions.CaseData), new ValidationErrorMessage(Validations.sormasToSormasCaseExists));
		}

		return errors;
	}
}

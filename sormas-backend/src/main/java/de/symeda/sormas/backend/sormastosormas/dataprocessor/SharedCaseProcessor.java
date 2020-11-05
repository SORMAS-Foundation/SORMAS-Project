/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.dataprocessor;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.ProcessedCaseData;
import de.symeda.sormas.backend.sormastosormas.SharedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.dataprocessor.SharedDataProcessorHelper.InfrastructureData;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class SharedCaseProcessor implements SharedDataProcessor<SormasToSormasCaseDto, ProcessedCaseData> {

	@EJB
	private UserService userService;
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private SharedDataProcessorHelper dataProcessorHelper;

	public ProcessedCaseData processSharedData(SormasToSormasCaseDto sharedCase) throws SormasToSormasValidationException {
		Map<String, ValidationErrors> validationErrors = new HashMap<>();

		PersonDto person = sharedCase.getPerson();
		CaseDataDto caze = sharedCase.getCaze();
		List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts = sharedCase.getAssociatedContacts();
		List<SormasToSormasSampleDto> samples = sharedCase.getSamples();
		SormasToSormasOriginInfoDto originInfo = sharedCase.getOriginInfo();

		ValidationErrors caseErrors = validateCase(caze);

		if (caseErrors.hasError()) {
			validationErrors.put(buildCaseValidationGroupName(caze), caseErrors);
		} else {
			ValidationErrors caseValidationErrors = new ValidationErrors();

			ValidationErrors originInfoErrorsErrors = dataProcessorHelper.processOriginInfo(originInfo, Captions.CaseData);
			caseValidationErrors.addAll(originInfoErrorsErrors);

			ValidationErrors caseDataErrors = processCaseData(caze, person, originInfo);
			caseValidationErrors.addAll(caseDataErrors);

			if (caseValidationErrors.hasError()) {
				validationErrors.put(buildCaseValidationGroupName(caze), caseValidationErrors);
			}

			if (associatedContacts != null) {
				Map<String, ValidationErrors> contactValidationErrors = processAssociatedContacts(associatedContacts, originInfo);
				validationErrors.putAll(contactValidationErrors);
			}

			if (samples != null) {
				Map<String, ValidationErrors> sampleErrors = dataProcessorHelper.processSamples(samples);
				validationErrors.putAll(sampleErrors);
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		return new ProcessedCaseData(person, caze, associatedContacts, samples);
	}

	private ValidationErrors validateCase(CaseDataDto caze) throws ValidationRuntimeException {
		ValidationErrors errors = new ValidationErrors();
		if (caseFacade.exists(caze.getUuid())) {
			errors.add(I18nProperties.getCaption(Captions.CaseData), I18nProperties.getValidationError(Validations.sormasToSormasCaseExists));
		}

		return errors;
	}

	private ValidationErrors processCaseData(CaseDataDto caze, PersonDto person, SormasToSormasOriginInfoDto originInfo) {
		ValidationErrors caseValidationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = dataProcessorHelper.processPerson(person);
		caseValidationErrors.addAll(personValidationErrors);

		caze.setPerson(person.toReference());
		caze.setReportingUser(userService.getCurrentUser().toReference());

		DataHelper.Pair<InfrastructureData, List<String>> infrastructureAndErrors = dataProcessorHelper.loadLocalInfrastructure(
			caze.getRegion(),
			caze.getDistrict(),
			caze.getCommunity(),
			caze.getFacilityType(),
			caze.getHealthFacility(),
			caze.getPointOfEntry());

		dataProcessorHelper.handleInfraStructure(infrastructureAndErrors, Captions.CaseData, caseValidationErrors, infrastructureData -> {
			caze.setRegion(infrastructureData.getRegion());
			caze.setDistrict(infrastructureData.getDistrict());
			caze.setCommunity(infrastructureData.getCommunity());
			caze.setHealthFacility(infrastructureData.getFacility());
			caze.setPointOfEntry(infrastructureData.getPointOfEntry());
		});

		ValidationErrors embeddedObjectErrors = processEmbeddedObjects(caze);
		caseValidationErrors.addAll(embeddedObjectErrors);

		caze.setSormasToSormasOriginInfo(originInfo);

		return caseValidationErrors;
	}

	private ValidationErrors processEmbeddedObjects(CaseDataDto caze) {
		ValidationErrors validationErrors = new ValidationErrors();

		if (caze.getHospitalization() != null) {
			caze.getHospitalization().setUuid(DataHelper.createUuid());
			caze.getHospitalization().getPreviousHospitalizations().forEach(ph -> {
				ph.setUuid(DataHelper.createUuid());

				DataHelper.Pair<InfrastructureData, List<String>> phInfrastructureAndErrors = dataProcessorHelper.loadLocalInfrastructure(
					ph.getRegion(),
					ph.getDistrict(),
					ph.getCommunity(),
					FacilityType.HOSPITAL,
					ph.getHealthFacility(),
					null);

				dataProcessorHelper.handleInfraStructure(
					phInfrastructureAndErrors,
					Captions.CaseHospitalization_previousHospitalizations,
					validationErrors,
					(phInfrastructure) -> {
						ph.setRegion(phInfrastructure.getRegion());
						ph.setDistrict(phInfrastructure.getDistrict());
						ph.setCommunity(phInfrastructure.getCommunity());
						ph.setHealthFacility(phInfrastructure.getFacility());
					});
			});
		}

		if (caze.getSymptoms() != null) {
			caze.getSymptoms().setUuid(DataHelper.createUuid());
		}

		if (caze.getEpiData() != null) {
			dataProcessorHelper.processEpiData(caze.getEpiData());
		}

		if (caze.getTherapy() != null) {
			caze.getTherapy().setUuid(DataHelper.createUuid());
		}

		if (caze.getClinicalCourse() != null) {
			caze.getClinicalCourse().setUuid(DataHelper.createUuid());

			if (caze.getClinicalCourse().getHealthConditions() != null) {
				dataProcessorHelper.processHealthConditions(caze.getClinicalCourse().getHealthConditions());
			}
		}

		MaternalHistoryDto maternalHistory = caze.getMaternalHistory();
		if (maternalHistory != null) {
			maternalHistory.setUuid(DataHelper.createUuid());

			DataHelper.Pair<InfrastructureData, List<String>> rashExposureInfrastructureAndErrors = dataProcessorHelper.loadLocalInfrastructure(
				maternalHistory.getRashExposureRegion(),
				maternalHistory.getRashExposureDistrict(),
				maternalHistory.getRashExposureCommunity(),
				null,
				null,
				null);

			dataProcessorHelper.handleInfraStructure(
				rashExposureInfrastructureAndErrors,
				Captions.MaternalHistory_rashExposure,
				validationErrors,
				(rashExposureInfrastructure) -> {
					maternalHistory.setRashExposureRegion(rashExposureInfrastructure.getRegion());
					maternalHistory.setRashExposureDistrict(rashExposureInfrastructure.getDistrict());
					maternalHistory.setRashExposureCommunity(rashExposureInfrastructure.getCommunity());
				});
		}

		if (caze.getPortHealthInfo() != null) {
			caze.getPortHealthInfo().setUuid(DataHelper.createUuid());
		}

		return validationErrors;
	}

	private Map<String, ValidationErrors> processAssociatedContacts(
		List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts,
		SormasToSormasOriginInfoDto originInfo) {
		Map<String, ValidationErrors> validationErrors = new HashMap<>();

		for (SormasToSormasCaseDto.AssociatedContactDto associatedContact : associatedContacts) {
			ContactDto contact = associatedContact.getContact();
			ValidationErrors contactErrors = new ValidationErrors();

			if (contactFacade.exists(contact.getUuid())) {
				contactErrors
					.add(I18nProperties.getCaption(Captions.Contact), I18nProperties.getValidationError(Validations.sormasToSormasContactExists));
				continue;
			}

			ValidationErrors contactProcessingErrors = dataProcessorHelper.processContactData(contact, associatedContact.getPerson(), originInfo);
			contactErrors.addAll(contactProcessingErrors);

			if (contactErrors.hasError()) {
				validationErrors.put(buildContactValidationGroupName(contact), contactErrors);
			}
		}

		return validationErrors;
	}
}

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

package de.symeda.sormas.backend.sormastosormas.data;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildEventParticipantValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildPathogenTestValidationGroupName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasPersonPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.labmessage.LabMessageFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;
import de.symeda.sormas.backend.user.UserService;

/**
 * Central place to validate all entities which are transferred by S2S.
 */
@Stateless
@LocalBean
public class Sormas2SormasDataValidator {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@EJB
	private UserService userService;
	@EJB
	private LabMessageFacadeEjb.LabMessageFacadeEjbLocal labMessageFacade;
	@EJB
	private InfrastructureValidator infraValidator;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	public ValidationErrors validateOriginInfo(SormasToSormasOriginInfoDto originInfo, String validationGroupCaption) {
		if (originInfo == null) {
			return ValidationErrors
				.create(new ValidationErrorGroup(validationGroupCaption), new ValidationErrorMessage(Validations.sormasToSormasShareInfoMissing));
		}

		ValidationErrors validationErrors = new ValidationErrors();

		if (originInfo.getOrganizationId() == null) {
			validationErrors.add(
				new ValidationErrorGroup(Captions.CaseData_sormasToSormasOriginInfo),
				new ValidationErrorMessage(Validations.sormasToSormasOrganizationIdMissing));
		}

		if (DataHelper.isNullOrEmpty(originInfo.getSenderName())) {
			validationErrors.add(
				new ValidationErrorGroup(Captions.CaseData_sormasToSormasOriginInfo),
				new ValidationErrorMessage(Validations.sormasToSormasSenderNameMissing));
		}

		originInfo.setUuid(DataHelper.createUuid());
		originInfo.setChangeDate(new Date());

		return validationErrors;
	}

	/*****************
	 * Person
	 *****************/

	public ValidationErrors validatePerson(PersonDto person) {
		ValidationErrors validationErrors = new ValidationErrors();

		validateLocation(person.getAddress(), Captions.Person, validationErrors);

		person.getAddresses().forEach(address -> {
			validateLocation(address, Captions.Person, validationErrors);
		});

		CountryReferenceDto birthCountry = validateCountry(person.getBirthCountry(), Captions.Person_birthCountry, validationErrors);
		person.setBirthCountry(birthCountry);

		CountryReferenceDto citizenship = validateCountry(person.getCitizenship(), Captions.Person_citizenship, validationErrors);
		person.setCitizenship(citizenship);

		return validationErrors;
	}

	public ValidationErrors validatePersonPreview(SormasToSormasPersonPreview person) {
		ValidationErrors validationErrors = new ValidationErrors();

		validateLocation(person.getAddress(), Captions.Person, validationErrors);

		return validationErrors;
	}

	/*****************
	 * CASES
	 *****************/
	public ValidationErrors validateCaseData(CaseDataDto caze, PersonDto person, Case existingCaseData) {
		ValidationErrors caseValidationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = validatePerson(person);
		caseValidationErrors.addAll(personValidationErrors);

		caze.setPerson(person.toReference());
		updateReportingUser(caze, existingCaseData);

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

		ValidationErrors embeddedObjectErrors = validateCaseData(caze);
		caseValidationErrors.addAll(embeddedObjectErrors);

		return caseValidationErrors;
	}

	public ValidationErrors validateCaseData(CaseDataDto caze) {
		// todo this function should be inlined above, it really does not help
		ValidationErrors validationErrors = new ValidationErrors();

		if (caze.getHospitalization() != null) {

			caze.getHospitalization().getPreviousHospitalizations().forEach(ph -> {
				validatePreviousHospitalization(validationErrors, ph);
			});
		}

		MaternalHistoryDto maternalHistory = caze.getMaternalHistory();
		if (maternalHistory != null) {
			validateMaternalHistory(validationErrors, maternalHistory);
		}

		validateEpiData(caze.getEpiData(), validationErrors);
		return validationErrors;
	}

	public ValidationErrors validateCasePreview(SormasToSormasCasePreview preview) {
		ValidationErrors caseValidationErrors = new ValidationErrors();

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

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.CaseData, caseValidationErrors, infrastructureData -> {
			preview.setRegion(infrastructureData.getRegion());
			preview.setDistrict(infrastructureData.getDistrict());
			preview.setCommunity(infrastructureData.getCommunity());
			preview.setHealthFacility(infrastructureData.getFacility());
			preview.setHealthFacilityDetails(infrastructureData.getFacilityDetails());
			preview.setPointOfEntry(infrastructureData.getPointOfEntry());
			preview.setPointOfEntryDetails(infrastructureData.getPointOfEntryDetails());
		});
		return caseValidationErrors;
	}

	/*****************
	 * CONTACTS
	 *****************/
	public ValidationErrors validateContactData(ContactDto contact, PersonDto person, Contact existingContact) {
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = validatePerson(person);
		validationErrors.addAll(personValidationErrors);

		contact.setPerson(person.toReference());
		updateReportingUser(contact, existingContact);

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity());

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Contact, validationErrors, (infrastructure -> {
			contact.setRegion(infrastructure.getRegion());
			contact.setDistrict(infrastructure.getDistrict());
			contact.setCommunity(infrastructure.getCommunity());
		}));

		validateEpiData(contact.getEpiData(), validationErrors);

		return validationErrors;
	}

	public List<ValidationErrors> validateContactPreviews(List<SormasToSormasContactPreview> contacts) {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		for (SormasToSormasContactPreview contact : contacts) {
			ValidationErrors contactErrors = validateContactPreview(contact);

			if (contactErrors.hasError()) {
				validationErrors.add(new ValidationErrors(buildContactValidationGroupName(contact), contactErrors));
			}
		}

		return validationErrors;
	}

	public ValidationErrors validateContactPreview(SormasToSormasContactPreview contact) {
		ValidationErrors validationErrors = new ValidationErrors();

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(contact.getRegion(), contact.getDistrict(), contact.getCommunity());

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Contact, validationErrors, (infrastructure -> {
			contact.setRegion(infrastructure.getRegion());
			contact.setDistrict(infrastructure.getDistrict());
			contact.setCommunity(infrastructure.getCommunity());
		}));

		return validationErrors;
	}

	/*****************
	 * EVENTS
	 *****************/
	public ValidationErrors validateEventData(EventDto event, Event existingEvent) {
		ValidationErrors validationErrors = new ValidationErrors();

		updateReportingUser(event, existingEvent);
		if (existingEvent == null || existingEvent.getResponsibleUser() == null) {
			event.setResponsibleUser(userService.getCurrentUser().toReference());
		} else {
			event.setResponsibleUser(existingEvent.getResponsibleUser().toReference());
		}

		LocationDto eventLocation = event.getEventLocation();
		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(
				eventLocation.getContinent(),
				eventLocation.getSubcontinent(),
				eventLocation.getCountry(),
				eventLocation.getRegion(),
				eventLocation.getDistrict(),
				eventLocation.getCommunity(),
				eventLocation.getFacilityType(),
				eventLocation.getFacility(),
				eventLocation.getFacilityDetails(),
				null,
				null);

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.CaseData, validationErrors, infrastructureData -> {
			eventLocation.setContinent(infrastructureData.getContinent());
			eventLocation.setSubcontinent(infrastructureData.getSubcontinent());
			eventLocation.setCountry(infrastructureData.getCountry());
			eventLocation.setRegion(infrastructureData.getRegion());
			eventLocation.setDistrict(infrastructureData.getDistrict());
			eventLocation.setCommunity(infrastructureData.getCommunity());
			eventLocation.setFacility(infrastructureData.getFacility());
		});

		return validationErrors;
	}

	public List<ValidationErrors> validateEventParticipantPreviews(List<SormasToSormasEventParticipantPreview> eventParticipants) {
		List<ValidationErrors> errors = new ArrayList<>();

		eventParticipants.forEach(eventParticipant -> {
			ValidationErrors validationErrors = validatePersonPreview(eventParticipant.getPerson());

			if (validationErrors.hasError()) {
				errors.add(new ValidationErrors(buildEventParticipantValidationGroupName(eventParticipant), validationErrors));
			}
		});

		return errors;
	}

	public ValidationErrors validateEventParticipant(EventParticipantDto eventParticipant) {
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = validatePerson(eventParticipant.getPerson());
		validationErrors.addAll(personValidationErrors);

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(eventParticipant.getRegion(), eventParticipant.getDistrict(), null);
		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.EventParticipant, validationErrors, (infrastructureData -> {
			eventParticipant.setRegion(infrastructureData.getRegion());
			eventParticipant.setDistrict(infrastructureData.getDistrict());
		}));

		return validationErrors;
	}

	/*****************
	 * SAMPLES
	 *****************/
	public void validatePathogenTest(ValidationErrors validationErrors, PathogenTestDto pathogenTest) {
		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> ptInfrastructureAndErrors =
			infraValidator.validateInfrastructure(
				null,
				null,
				null,
				null,
				null,
				null,
				FacilityType.LABORATORY,
				pathogenTest.getLab(),
				pathogenTest.getLabDetails(),
				null,
				null);

		ValidationErrors pathogenTestErrors = new ValidationErrors();
		infraValidator.handleInfraStructure(ptInfrastructureAndErrors, Captions.PathogenTest_lab, pathogenTestErrors, (infrastructureData -> {
			pathogenTest.setLab(infrastructureData.getFacility());
			pathogenTest.setLabDetails(infrastructureData.getFacilityDetails());
		}));

		if (pathogenTestErrors.hasError()) {
			validationErrors.addAll(new ValidationErrors(buildPathogenTestValidationGroupName(pathogenTest), pathogenTestErrors));
		}
	}

	public ValidationErrors validateSample(Sample existingSample, SampleDto sample) {

		ValidationErrors validationErrors = new ValidationErrors();

		updateReportingUser(sample, existingSample);

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(
				null,
				null,
				null,
				null,
				null,
				null,
				// todo shouldn't this be FacilityType.LABORATORY?
				null,
				sample.getLab(),
				sample.getLabDetails(),
				null,
				null);

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Sample_lab, validationErrors, (infrastructureData -> {
			sample.setLab(infrastructureData.getFacility());
			sample.setLabDetails(infrastructureData.getFacilityDetails());
		}));

		return validationErrors;
	}

	/*****************
	 * HOSPITALIZATION
	 *****************/
	public void validatePreviousHospitalization(ValidationErrors validationErrors, PreviousHospitalizationDto ph) {
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
	}

	/*****************
	 * MISC
	 *****************/
	public void validateMaternalHistory(ValidationErrors validationErrors, MaternalHistoryDto maternalHistory) {
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

	private CountryReferenceDto validateCountry(CountryReferenceDto country, String errorCaption, ValidationErrors validationErrors) {
		// todo this method looks like it should be handled in infraValidator
		CountryReferenceDto localCountry = infraValidator.lookupLocalCountry(country);
		if (country != null && localCountry == null) {
			validationErrors
				.add(new ValidationErrorGroup(errorCaption), new ValidationErrorMessage(Validations.sormasToSormasCountry, country.getCaption()));
		}
		return localCountry;
	}

	public void validateEpiData(EpiDataDto epiData, ValidationErrors validationErrors) {
		if (epiData != null) {
			epiData.getExposures().forEach(exposure -> {
				LocationDto exposureLocation = exposure.getLocation();
				if (exposureLocation != null) {
					validateLocation(exposureLocation, Captions.EpiData_exposures, validationErrors);
				}
			});
			epiData.getActivitiesAsCase().forEach(activity -> {
				LocationDto activityLocation = activity.getLocation();
				if (activityLocation != null) {
					validateLocation(activityLocation, Captions.EpiData_activitiesAsCase, validationErrors);
				}
			});
		}
	}

	public void validateLocation(LocationDto address, String groupNameTag, ValidationErrors validationErrors) {
		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(
				address.getContinent(),
				address.getSubcontinent(),
				address.getCountry(),
				address.getRegion(),
				address.getDistrict(),
				address.getCommunity(),
				address.getFacilityType(),
				address.getFacility(),
				address.getFacilityDetails(),
				null,
				null);

		infraValidator.handleInfraStructure(infrastructureAndErrors, groupNameTag, validationErrors, (infrastructure -> {
			address.setContinent(infrastructure.getContinent());
			address.setSubcontinent(infrastructure.getSubcontinent());
			address.setCountry(infrastructure.getCountry());
			address.setRegion(infrastructure.getRegion());
			address.setDistrict(infrastructure.getDistrict());
			address.setCommunity(infrastructure.getCommunity());
			address.setFacility(infrastructure.getFacility());
			address.setFacilityDetails(infrastructure.getFacilityDetails());
		}));
	}

	public ValidationErrors validateSharedLabMessage(LabMessageDto labMessage) throws ValidationRuntimeException {
		ValidationErrors errors = new ValidationErrors();

		if (labMessageFacade.exists(labMessage.getUuid())) {
			errors.add(new ValidationErrorGroup(Captions.LabMessage), new ValidationErrorMessage(Validations.sormasToSormasLabMessageExists));
		}

		return errors;
	}

	public void updateReportingUser(SormasToSormasShareableDto entity, SormasToSormasShareable originalEntiy) {
		UserReferenceDto reportingUser =
			originalEntiy == null ? userService.getCurrentUser().toReference() : originalEntiy.getReportingUser().toReference();
		entity.setReportingUser(reportingUser);
	}

	public <T> void handleIgnoredProperties(T receivedEntity, T originalEntity) {
		Class<?> dtoType = receivedEntity.getClass();
		SormasToSormasConfig s2SConfig = configFacade.getS2SConfig();
		for (Field field : dtoType.getDeclaredFields()) {
			if (field.isAnnotationPresent(S2SIgnoreProperty.class)) {
				String s2sConfigProperty = field.getAnnotation(S2SIgnoreProperty.class).configProperty();
				if (s2SConfig.getIgnoreProperties().get(s2sConfigProperty)) {
					field.setAccessible(true);
					try {
						Object originalValue = originalEntity != null ? field.get(originalEntity) : null;
						field.set(receivedEntity, originalValue);
					} catch (IllegalAccessException e) {
						LOGGER.error("Could not set field {} for {}", field.getName(), dtoType.getSimpleName());
					}
					field.setAccessible(false);
				}
			}
		}
	}

	public PersonDto getExitingPerson(@Nullable Case existingCase) {
		if (existingCase == null) {
			return null;
		}
		return PersonFacadeEjb.PersonFacadeEjbLocal.toDto(existingCase.getPerson());
	}

	public PersonDto getExistingPerson(@Nullable Contact existingContact) {
		if (existingContact == null) {
			return null;
		}
		return PersonFacadeEjb.PersonFacadeEjbLocal.toDto(existingContact.getPerson());
	}

	public PersonDto getExistingPerson(@Nullable EventParticipant existingEventParticipant) {
		if (existingEventParticipant == null) {
			return null;
		}
		return PersonFacadeEjb.PersonFacadeEjbLocal.toDto(existingEventParticipant.getPerson());
	}
}

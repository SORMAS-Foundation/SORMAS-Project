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

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildPathogenTestValidationGroupName;

import java.lang.reflect.Field;
import java.util.Date;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.hospitalization.HospitalizationDto;
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

	private final Logger logger = LoggerFactory.getLogger(getClass());

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

		person.getAddresses().forEach(address -> validateLocation(address, Captions.Person, validationErrors));

		infraValidator.validateCountry(person.getBirthCountry(), Captions.Person_birthCountry, validationErrors, person::setBirthCountry);

		infraValidator.validateCountry(person.getCitizenship(), Captions.Person_citizenship, validationErrors, person::setCitizenship);

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

		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = validatePerson(person);
		validationErrors.addAll(personValidationErrors);

		caze.setPerson(person.toReference());
		updateReportingUser(caze, existingCaseData);

		final String groupNameTag = Captions.CaseData;
		infraValidator.validateResponsibleRegion(caze.getResponsibleRegion(), groupNameTag, validationErrors, caze::setResponsibleRegion);
		infraValidator.validateResponsibleDistrict(caze.getResponsibleDistrict(), groupNameTag, validationErrors, caze::setResponsibleDistrict);
		infraValidator.validateResponsibleCommunity(caze.getResponsibleCommunity(), groupNameTag, validationErrors, caze::setResponsibleCommunity);

		infraValidator.validateRegion(caze.getRegion(), groupNameTag, validationErrors, caze::setRegion);
		infraValidator.validateDistrit(caze.getDistrict(), groupNameTag, validationErrors, caze::setDistrict);
		infraValidator.validateCommunity(caze.getCommunity(), groupNameTag, validationErrors, caze::setCommunity);

		infraValidator.validateFacility(
			caze.getHealthFacility(),
			caze.getFacilityType(),
			caze.getHealthFacilityDetails(),
			groupNameTag,
			validationErrors,
			f -> {
				caze.setHealthFacility(f.getEntity());
				caze.setHealthFacilityDetails(f.getDetails());
			});

		infraValidator.validatePointOfEntry(caze.getPointOfEntry(), caze.getPointOfEntryDetails(), groupNameTag, validationErrors, p -> {
			caze.setPointOfEntry(p.getEntity());
			caze.setPointOfEntryDetails(p.getDetails());
		});

		final HospitalizationDto hospitalization = caze.getHospitalization();
		if (hospitalization != null) {
			hospitalization.getPreviousHospitalizations().forEach(ph -> validatePreviousHospitalization(validationErrors, ph));
		}

		final MaternalHistoryDto maternalHistory = caze.getMaternalHistory();
		if (maternalHistory != null) {
			validateMaternalHistory(validationErrors, maternalHistory);
		}

		validateEpiData(caze.getEpiData(), validationErrors);

		return validationErrors;
	}

	public ValidationErrors validateCasePreview(SormasToSormasCasePreview preview) {
		ValidationErrors validationErrors = new ValidationErrors();

		final String groupNameTag = Captions.CaseData;
		infraValidator.validateRegion(preview.getRegion(), groupNameTag, validationErrors, preview::setRegion);
		infraValidator.validateDistrit(preview.getDistrict(), groupNameTag, validationErrors, preview::setDistrict);
		infraValidator.validateCommunity(preview.getCommunity(), groupNameTag, validationErrors, preview::setCommunity);
		infraValidator.validateFacility(
			preview.getHealthFacility(),
			preview.getFacilityType(),
			preview.getHealthFacilityDetails(),
			groupNameTag,
			validationErrors,
			f -> {
				preview.setHealthFacility(f.getEntity());
				preview.setHealthFacilityDetails(f.getDetails());
			});

		return validationErrors;
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

		String groupNameTag = Captions.Contact;
		infraValidator.validateRegion(contact.getRegion(), groupNameTag, validationErrors, contact::setRegion);
		infraValidator.validateDistrit(contact.getDistrict(), groupNameTag, validationErrors, contact::setDistrict);
		infraValidator.validateCommunity(contact.getCommunity(), groupNameTag, validationErrors, contact::setCommunity);

		validateEpiData(contact.getEpiData(), validationErrors);

		return validationErrors;
	}

	public ValidationErrors validateContactPreview(SormasToSormasContactPreview contact) {
		ValidationErrors validationErrors = new ValidationErrors();

		String groupNameTag = Captions.Contact;
		infraValidator.validateRegion(contact.getRegion(), groupNameTag, validationErrors, contact::setRegion);
		infraValidator.validateDistrit(contact.getDistrict(), groupNameTag, validationErrors, contact::setDistrict);
		infraValidator.validateCommunity(contact.getCommunity(), groupNameTag, validationErrors, contact::setCommunity);

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

		validateLocation(event.getEventLocation(), Captions.CaseData, validationErrors);

		return validationErrors;
	}

	public ValidationErrors validateEventParticipant(EventParticipantDto ep) {
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = validatePerson(ep.getPerson());
		validationErrors.addAll(personValidationErrors);

		final String groupNameTag = Captions.EventParticipant;
		infraValidator.validateRegion(ep.getRegion(), groupNameTag, validationErrors, ep::setRegion);
		infraValidator.validateDistrit(ep.getDistrict(), groupNameTag, validationErrors, ep::setDistrict);

		return validationErrors;
	}

	/*****************
	 * SAMPLES
	 *****************/
	public void validatePathogenTest(ValidationErrors validationErrors, PathogenTestDto pt) {
		ValidationErrors pathogenTestErrors = new ValidationErrors();
		infraValidator.validateFacility(pt.getLab(), FacilityType.LABORATORY, pt.getLabDetails(), Captions.PathogenTest_lab, validationErrors, f -> {
			pt.setLab(f.getEntity());
			pt.setLabDetails(f.getDetails());
		});

		if (pathogenTestErrors.hasError()) {
			validationErrors.addAll(new ValidationErrors(buildPathogenTestValidationGroupName(pt), pathogenTestErrors));
		}
	}

	public ValidationErrors validateSample(Sample existingSample, SampleDto sample) {
		ValidationErrors validationErrors = new ValidationErrors();
		updateReportingUser(sample, existingSample);

		// todo shouldn't this be FacilityType.LABORATORY?
		infraValidator.validateFacility(sample.getLab(), null, sample.getLabDetails(), Captions.Sample_lab, validationErrors, f -> {
			sample.setLab(f.getEntity());
			sample.setLabDetails(f.getDetails());
		});

		return validationErrors;
	}

	/*****************
	 * HOSPITALIZATION
	 *****************/
	public void validatePreviousHospitalization(ValidationErrors validationErrors, PreviousHospitalizationDto ph) {
		final String groupNameTag = Captions.CaseHospitalization_previousHospitalizations;

		infraValidator.validateRegion(ph.getRegion(), groupNameTag, validationErrors, ph::setRegion);
		infraValidator.validateDistrit(ph.getDistrict(), groupNameTag, validationErrors, ph::setDistrict);
		infraValidator.validateCommunity(ph.getCommunity(), groupNameTag, validationErrors, ph::setCommunity);
		infraValidator
			.validateFacility(ph.getHealthFacility(), FacilityType.HOSPITAL, ph.getHealthFacilityDetails(), groupNameTag, validationErrors, f -> {
				ph.setHealthFacility(f.getEntity());
				ph.setHealthFacilityDetails(f.getDetails());
			});

	}

	/*****************
	 * MISC
	 *****************/
	public void validateMaternalHistory(ValidationErrors validationErrors, MaternalHistoryDto mh) {
		final String groupNameTag = Captions.MaternalHistory_rashExposure;
		infraValidator.validateRegion(mh.getRashExposureRegion(), groupNameTag, validationErrors, mh::setRashExposureRegion);
		infraValidator.validateDistrit(mh.getRashExposureDistrict(), groupNameTag, validationErrors, mh::setRashExposureDistrict);
		infraValidator.validateCommunity(mh.getRashExposureCommunity(), groupNameTag, validationErrors, mh::setRashExposureCommunity);
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

	public void validateLocation(LocationDto location, String groupNameTag, ValidationErrors validationErrors) {
		infraValidator.validateContinent(location.getContinent(), groupNameTag, validationErrors, location::setContinent);
		infraValidator.validateSubcontinent(location.getSubcontinent(), groupNameTag, validationErrors, location::setSubcontinent);
		infraValidator.validateCountry(location.getCountry(), groupNameTag, validationErrors, location::setCountry);
		infraValidator.validateRegion(location.getRegion(), groupNameTag, validationErrors, location::setRegion);
		infraValidator.validateDistrit(location.getDistrict(), groupNameTag, validationErrors, location::setDistrict);
		infraValidator.validateCommunity(location.getCommunity(), groupNameTag, validationErrors, location::setCommunity);
		infraValidator.validateFacility(
			location.getFacility(),
			location.getFacilityType(),
			location.getFacilityDetails(),
			groupNameTag,
			validationErrors,
			f -> {
				location.setFacility(f.getEntity());
				location.setFacilityDetails(f.getDetails());
			});
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
						logger.error("Could not set field {} for {}", field.getName(), dtoType.getSimpleName());
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
		return PersonFacadeEjb.toDto(existingCase.getPerson());
	}

	public PersonDto getExistingPerson(@Nullable Contact existingContact) {
		if (existingContact == null) {
			return null;
		}
		return PersonFacadeEjb.toDto(existingContact.getPerson());
	}

	public PersonDto getExistingPerson(@Nullable EventParticipant existingEventParticipant) {
		if (existingEventParticipant == null) {
			return null;
		}
		return PersonFacadeEjb.toDto(existingEventParticipant.getPerson());
	}
}

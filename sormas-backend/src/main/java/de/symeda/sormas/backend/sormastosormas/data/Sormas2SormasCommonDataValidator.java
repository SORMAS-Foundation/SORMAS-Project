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
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.vaccination.Vaccination;
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
import de.symeda.sormas.api.utils.SormasToSormasEntityDtoInterface;
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
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasEntity;
import de.symeda.sormas.backend.user.UserService;

/**
 * Central place to validate all entities which are transferred by S2S.
 */
@Stateless
@LocalBean
public class Sormas2SormasCommonDataValidator {

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
	 * SAMPLES
	 *****************/
	public void validatePathogenTest(ValidationErrors validationErrors, PathogenTestDto pathogenTest) {

		ValidationErrors pathogenTestErrors = new ValidationErrors();
		infraValidator.validateFacility(
			pathogenTest.getLab(),
			FacilityType.LABORATORY,
			pathogenTest.getLabDetails(),
			Captions.PathogenTest_lab,
			pathogenTestErrors,
			f -> {
				pathogenTest.setLab(f.getEntity());
				pathogenTest.setLabDetails(f.getDetails());
			});

		if (pathogenTestErrors.hasError()) {
			validationErrors.addAll(new ValidationErrors(buildPathogenTestValidationGroupName(pathogenTest), pathogenTestErrors));
		}
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
	 * Immunization
	 *****************/

	public ValidationErrors validateReceivedImmunization(Immunization existingData, ImmunizationDto im) {
		ValidationErrors validationErrors = new ValidationErrors();

		updateReportingUser(im, existingData);
		handleIgnoredProperties(im, ImmunizationFacadeEjb.toDto(existingData));

		final String groupNameTag = Captions.Sample_lab;
		infraValidator.validateCountry(im.getCountry(), groupNameTag, validationErrors, im::setCountry);

		infraValidator.validateResponsibleRegion(im.getResponsibleRegion(), groupNameTag, validationErrors, im::setResponsibleRegion);
		infraValidator.validateResponsibleDistrict(im.getResponsibleDistrict(), groupNameTag, validationErrors, im::setResponsibleDistrict);
		infraValidator.validateResponsibleCommunity(im.getResponsibleCommunity(), groupNameTag, validationErrors, im::setResponsibleCommunity);

		infraValidator
			.validateFacility(im.getHealthFacility(), im.getFacilityType(), im.getHealthFacilityDetails(), groupNameTag, validationErrors, f -> {
				im.setHealthFacility(f.getEntity());
				im.setHealthFacilityDetails(f.getDetails());
			});

		im.getVaccinations().forEach(vaccination -> {
			Vaccination existingVaccination = existingData == null
				? null
				: existingData.getVaccinations().stream().filter(v -> v.getUuid().equals(vaccination.getUuid())).findFirst().orElse(null);
			UserReferenceDto reportingUser =
				existingVaccination == null ? userService.getCurrentUser().toReference() : existingVaccination.getReportingUser().toReference();

			vaccination.setReportingUser(reportingUser);
		});
		return validationErrors;
	}

	/*****************
	 * MISC
	 *****************/
	public void validateMaternalHistory(ValidationErrors validationErrors, MaternalHistoryDto mh) {

		infraValidator.validateRegion(mh.getRashExposureRegion(), Captions.MaternalHistory_rashExposure, validationErrors, mh::setRashExposureRegion);
		infraValidator
			.validateDistrit(mh.getRashExposureDistrict(), Captions.MaternalHistory_rashExposure, validationErrors, mh::setRashExposureDistrict);
		infraValidator
			.validateCommunity(mh.getRashExposureCommunity(), Captions.MaternalHistory_rashExposure, validationErrors, mh::setRashExposureCommunity);
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

	public void updateReportingUser(SormasToSormasEntityDtoInterface entity, SormasToSormasEntity originalEntiy) {
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

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
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildSampleValidationGroupName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventParticipantPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasPersonPreview;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SormasToSormasEntityDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb.PointOfEntryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.continent.ContinentFacadeEjb.ContinentFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.subcontinent.SubcontinentFacadeEjb.SubcontinentFacadeEjbLocal;
import de.symeda.sormas.backend.labmessage.LabMessageFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb.SampleFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.user.UserService;

/**
 * Central place to validate all entities which are transferred by S2S.
 */
@Stateless
@LocalBean
public class Sormas2SormasDataValidator {

	@EJB
	private UserService userService;
	@EJB
	private ContinentFacadeEjbLocal continentFacade;
	@EJB
	private SubcontinentFacadeEjbLocal subcontinentFacade;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityFacadeEjb.FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PointOfEntryFacadeEjbLocal pointOfEntryFacade;
	@EJB
	private CountryFacadeEjbLocal countryFacade;
	@EJB
	private SampleFacadeEjbLocal sampleFacade;
	@EJB
	private LabMessageFacadeEjb.LabMessageFacadeEjbLocal labMessageFacade;
	@EJB
	private InfrastructureValidator infraValidator;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;

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
	public ValidationErrors validateCaseData(CaseDataDto caze, PersonDto person, CaseDataDto existingCaseData) {
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
	public List<ValidationErrors> validateAssociatedContacts(List<SormasToSormasCaseDto.AssociatedContactDto> associatedContacts) {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		Map<String, ContactDto> existingContactsMap =
			contactFacade.getByUuids(associatedContacts.stream().map(c -> c.getContact().getUuid()).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(EntityDto::getUuid, Function.identity()));

		for (SormasToSormasCaseDto.AssociatedContactDto associatedContact : associatedContacts) {
			ContactDto contact = associatedContact.getContact();
			ValidationErrors contactErrors = validateContactData(contact, associatedContact.getPerson(), existingContactsMap.get(contact.getUuid()));

			if (contactErrors.hasError()) {
				validationErrors.add(new ValidationErrors(buildContactValidationGroupName(contact), contactErrors));
			}
		}

		return validationErrors;
	}

	public ValidationErrors validateContactData(ContactDto contact, PersonDto person, ContactDto existingContact) {
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
	public ValidationErrors validateEventData(EventDto event, EventDto existingEvent) {
		ValidationErrors validationErrors = new ValidationErrors();

		updateReportingUser(event, existingEvent);
		if (existingEvent == null) {
			event.setResponsibleUser(userService.getCurrentUser().toReference());
		} else {
			event.setResponsibleUser(existingEvent.getResponsibleUser());
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

	public List<ValidationErrors> validateEventParticipants(List<EventParticipantDto> eventParticipants) {
		List<ValidationErrors> errors = new ArrayList<>();

		eventParticipants.forEach(eventParticipant -> {
			validateEventParticipant(errors, eventParticipant);
		});

		return errors;
	}

	public void validateEventParticipant(List<ValidationErrors> errors, EventParticipantDto eventParticipant) {
		ValidationErrors validationErrors = new ValidationErrors();

		ValidationErrors personValidationErrors = validatePerson(eventParticipant.getPerson());
		validationErrors.addAll(personValidationErrors);

		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(eventParticipant.getRegion(), eventParticipant.getDistrict(), null);
		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.EventParticipant, validationErrors, (infrastructureData -> {
			eventParticipant.setRegion(infrastructureData.getRegion());
			eventParticipant.setDistrict(infrastructureData.getDistrict());
		}));

		if (validationErrors.hasError()) {
			errors.add(new ValidationErrors(buildEventParticipantValidationGroupName(eventParticipant), validationErrors));
		}
	}

	/*****************
	 * SAMPLES
	 *****************/

	public List<ValidationErrors> validateSamples(List<SormasToSormasSampleDto> samples) {
		List<ValidationErrors> validationErrors = new ArrayList<>();

		Map<String, SampleDto> existingSamplesMap =
			sampleFacade.getByUuids(samples.stream().map(s -> s.getSample().getUuid()).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(SampleDto::getUuid, Function.identity()));

		samples.forEach(sormasToSormasSample -> {
			SampleDto sample = sormasToSormasSample.getSample();
			validateSample(validationErrors, existingSamplesMap, sample);

			sormasToSormasSample.getPathogenTests().forEach(pathogenTest -> {
				validatePathogenTest(validationErrors, pathogenTest);
			});
		});

		return validationErrors;
	}

	public void validatePathogenTest(List<ValidationErrors> validationErrors, PathogenTestDto pathogenTest) {
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
			validationErrors.add(new ValidationErrors(buildPathogenTestValidationGroupName(pathogenTest), pathogenTestErrors));
		}
	}

	public void validateSample(List<ValidationErrors> validationErrors, Map<String, SampleDto> existingSamplesMap, SampleDto sample) {
		ValidationErrors sampleErrors = new ValidationErrors();

		updateReportingUser(sample, existingSamplesMap.get(sample.getUuid()));

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

		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Sample_lab, sampleErrors, (infrastructureData -> {
			sample.setLab(infrastructureData.getFacility());
			sample.setLabDetails(infrastructureData.getFacilityDetails());
		}));

		if (sampleErrors.hasError()) {
			validationErrors.add(new ValidationErrors(buildSampleValidationGroupName(sample), sampleErrors));
		}
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

	public void updateReportingUser(SormasToSormasEntityDto entity, SormasToSormasEntityDto originalEntiy) {
		UserReferenceDto reportingUser = originalEntiy == null ? userService.getCurrentUser().toReference() : originalEntiy.getReportingUser();
		entity.setReportingUser(reportingUser);
	}

}

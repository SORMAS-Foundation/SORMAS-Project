package de.symeda.sormas.backend.sormastosormas.data.validation;

import de.symeda.sormas.api.caze.maternalhistory.MaternalHistoryDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasPersonPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildPathogenTestValidationGroupName;

public abstract class SormasToSormasDtoValidator<DTO extends SormasToSormasShareableDto, SHARED extends SormasToSormasEntityDto<DTO>, PREVIEW extends PseudonymizableDto> {

	protected InfrastructureValidator infraValidator;

	protected SormasToSormasDtoValidator() {

	}

	protected SormasToSormasDtoValidator(InfrastructureValidator infraValidator) {
		this.infraValidator = infraValidator;
	}

	public abstract ValidationErrors validate(SHARED sharedData, ValidationDirection direction);

	public abstract ValidationErrors validatePreview(PREVIEW preview, ValidationDirection direction);

	public ValidationErrors validateIncoming(SHARED sharedData) {
		return validate(sharedData, ValidationDirection.INCOMING);
	}

	public ValidationErrors validateIncomingPreview(PREVIEW preview) {
		return validatePreview(preview, ValidationDirection.INCOMING);
	}

	public ValidationErrors validateOutgoing(SHARED sharedData) {
		return validate(sharedData, ValidationDirection.OUTGOING);
	}

	public ValidationErrors validateOutgoingPreview(PREVIEW preview) {
		return validatePreview(preview, ValidationDirection.OUTGOING);
	}

	protected ValidationErrors validatePerson(PersonDto person, ValidationDirection direction) {
		ValidationErrors validationErrors = new ValidationErrors();

		validateLocation(person.getAddress(), Captions.Person, validationErrors, direction);
		person.getAddresses().forEach(address -> validateLocation(address, Captions.Person, validationErrors, direction));
		infraValidator.validateCountry(person.getBirthCountry(), Captions.Person_birthCountry, validationErrors, person::setBirthCountry, direction);
		infraValidator.validateCountry(person.getCitizenship(), Captions.Person_citizenship, validationErrors, person::setCitizenship, direction);

		infraValidator.validateRegion(
			person.getPlaceOfBirthRegion(),
			Captions.Person_placeOfBirthRegion,
			validationErrors,
			person::setPlaceOfBirthRegion,
			direction);
		infraValidator.validateDistrict(
			person.getPlaceOfBirthDistrict(),
			Captions.Person_placeOfBirthDistrict,
			validationErrors,
			person::setPlaceOfBirthDistrict,
			direction);
		infraValidator.validateCommunity(
			person.getPlaceOfBirthCommunity(),
			Captions.Person_placeOfBirthCommunity,
			validationErrors,
			person::setPlaceOfBirthCommunity,
			direction);
		return validationErrors;
	}

	protected ValidationErrors validatePersonPreview(SormasToSormasPersonPreview person, ValidationDirection direction) {
		ValidationErrors validationErrors = new ValidationErrors();
		validateLocation(person.getAddress(), Captions.Person, validationErrors, direction);
		return validationErrors;
	}

	public void validateLocation(LocationDto location, String groupNameTag, ValidationErrors validationErrors, ValidationDirection direction) {
		infraValidator.validateContinent(location.getContinent(), groupNameTag, validationErrors, location::setContinent, direction);
		infraValidator.validateSubcontinent(location.getSubcontinent(), groupNameTag, validationErrors, location::setSubcontinent, direction);
		infraValidator.validateCountry(location.getCountry(), groupNameTag, validationErrors, location::setCountry, direction);
		infraValidator.validateRegion(location.getRegion(), groupNameTag, validationErrors, location::setRegion, direction);
		infraValidator.validateDistrict(location.getDistrict(), groupNameTag, validationErrors, location::setDistrict, direction);
		infraValidator.validateCommunity(location.getCommunity(), groupNameTag, validationErrors, location::setCommunity, direction);
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

	protected void validatePreviousHospitalization(ValidationErrors validationErrors, PreviousHospitalizationDto ph, ValidationDirection direction) {
		final String groupNameTag = Captions.CaseHospitalization_previousHospitalizations;
		infraValidator.validateRegion(ph.getRegion(), groupNameTag, validationErrors, ph::setRegion, direction);
		infraValidator.validateDistrict(ph.getDistrict(), groupNameTag, validationErrors, ph::setDistrict, direction);
		infraValidator.validateCommunity(ph.getCommunity(), groupNameTag, validationErrors, ph::setCommunity, direction);
		infraValidator
			.validateFacility(ph.getHealthFacility(), FacilityType.HOSPITAL, ph.getHealthFacilityDetails(), groupNameTag, validationErrors, f -> {
				ph.setHealthFacility(f.getEntity());
				ph.setHealthFacilityDetails(f.getDetails());
			});
	}

	protected void validateMaternalHistory(ValidationErrors validationErrors, MaternalHistoryDto mh, ValidationDirection direction) {
		final String groupNameTag = Captions.MaternalHistory_rashExposure;
		infraValidator.validateRegion(mh.getRashExposureRegion(), groupNameTag, validationErrors, mh::setRashExposureRegion, direction);
		infraValidator.validateDistrict(mh.getRashExposureDistrict(), groupNameTag, validationErrors, mh::setRashExposureDistrict, direction);
		infraValidator.validateCommunity(mh.getRashExposureCommunity(), groupNameTag, validationErrors, mh::setRashExposureCommunity, direction);
	}

	public void validateEpiData(EpiDataDto epiData, ValidationErrors validationErrors, ValidationDirection direction) {
		if (epiData != null) {
			epiData.getExposures().forEach(exposure -> {
				LocationDto exposureLocation = exposure.getLocation();
				if (exposureLocation != null) {
					validateLocation(exposureLocation, Captions.EpiData_exposures, validationErrors, direction);
				}
			});
			epiData.getActivitiesAsCase().forEach(activity -> {
				LocationDto activityLocation = activity.getLocation();
				if (activityLocation != null) {
					validateLocation(activityLocation, Captions.EpiData_activitiesAsCase, validationErrors, direction);
				}
			});
		}
	}

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
}

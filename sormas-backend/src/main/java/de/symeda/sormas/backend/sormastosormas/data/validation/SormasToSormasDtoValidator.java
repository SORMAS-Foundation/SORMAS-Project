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

	public abstract ValidationErrors validateIncoming(SHARED sharedData);

	public abstract ValidationErrors validateIncomingPreview(PREVIEW preview);

	public abstract ValidationErrors validateOutgoing(SHARED sharedData);

	public abstract ValidationErrors validateOutgoingPreview(PREVIEW preview);

	protected ValidationErrors validatePerson(PersonDto person) {
		ValidationErrors validationErrors = new ValidationErrors();

		validateLocation(person.getAddress(), Captions.Person, validationErrors);
		person.getAddresses().forEach(address -> validateLocation(address, Captions.Person, validationErrors));
		infraValidator.validateCountry(person.getBirthCountry(), Captions.Person_birthCountry, validationErrors, person::setBirthCountry);
		infraValidator.validateCountry(person.getCitizenship(), Captions.Person_citizenship, validationErrors, person::setCitizenship);

		return validationErrors;
	}

	protected ValidationErrors validatePersonPreview(SormasToSormasPersonPreview person) {
		ValidationErrors validationErrors = new ValidationErrors();
		validateLocation(person.getAddress(), Captions.Person, validationErrors);
		return validationErrors;
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

	protected void validatePreviousHospitalization(ValidationErrors validationErrors, PreviousHospitalizationDto ph) {
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

	protected void validateMaternalHistory(ValidationErrors validationErrors, MaternalHistoryDto mh) {
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

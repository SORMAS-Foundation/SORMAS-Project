/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.epidata;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataFacade;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.backend.activityascase.ActivityAsCaseService;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldValueService;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.exposure.ExposureService;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "EpiDataFacade")
public class EpiDataFacadeEjb implements EpiDataFacade {

	@EJB
	private ExposureService exposureService;
	@EJB
	private ActivityAsCaseService activityAsCaseService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private ContactService contactService;
	@EJB
	private UserService userService;
	@EJB
	private CountryService countryService;
	@EJB
	private CustomizableFieldValueService customizableFieldValueService;

	public void softDeleteCustomizableFieldValues(EpiData epiData, DeletionDetails deletionDetails) {
		if (epiData == null || epiData.getUuid() == null) {
			return;
		}
		customizableFieldValueService.softDeleteValuesForEntity(epiData.getUuid(), CustomizableFieldContext.EPIDATA, deletionDetails);
		for (Exposure exposure : epiData.getExposures()) {
			customizableFieldValueService.softDeleteValuesForEntity(exposure.getUuid(), CustomizableFieldContext.EXPOSURE, deletionDetails);
		}
	}

	public EpiData fillOrBuildEntity(EpiDataDto source, EpiData target, boolean checkChangeDate) {
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, EpiData::new, checkChangeDate);

		target.setExposureDetailsKnown(source.getExposureDetailsKnown());
		target.setActivityAsCaseDetailsKnown(source.getActivityAsCaseDetailsKnown());
		target.setContactWithSourceCaseKnown(source.getContactWithSourceCaseKnown());
		target.setHighTransmissionRiskArea(source.getHighTransmissionRiskArea());
		target.setLargeOutbreaksArea(source.getLargeOutbreaksArea());
		target.setAreaInfectedAnimals(source.getAreaInfectedAnimals());

		List<Exposure> exposures = new ArrayList<>();
		for (ExposureDto exposureDto : source.getExposures()) {
			Exposure exposure = exposureService.getByUuid(exposureDto.getUuid());
			exposure = fillOrBuildExposureEntity(exposureDto, exposure, checkChangeDate);
			exposure.setEpiData(target);
			exposures.add(exposure);
		}
		if (!DataHelper.equalContains(target.getExposures(), exposures)) {
			// note: DataHelper.equal does not work here, because target.getAddresses may be a PersistentBag when using lazy loading
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getExposures().clear();
		target.getExposures().addAll(exposures);

		List<ActivityAsCase> activitiesAsCase = new ArrayList<>();
		for (ActivityAsCaseDto activityAsCaseDto : source.getActivitiesAsCase()) {
			ActivityAsCase activityAsCase = activityAsCaseService.getByUuid(activityAsCaseDto.getUuid());
			activityAsCase = fillOrBuildActivityAsCaseEntity(activityAsCaseDto, activityAsCase, checkChangeDate);
			activityAsCase.setEpiData(target);
			activitiesAsCase.add(activityAsCase);
		}
		if (!DataHelper.equalContains(target.getActivitiesAsCase(), activitiesAsCase)) {
			// note: DataHelper.equal does not work here, because target.getAddresses may be a PersistentBag when using lazy loading
			target.setChangeDateOfEmbeddedLists(new Date());
		}
		target.getActivitiesAsCase().clear();
		target.getActivitiesAsCase().addAll(activitiesAsCase);
		target.setClusterType(source.getClusterType());
		target.setCaseImportedStatus(source.getCaseImportedStatus());
		target.setClusterTypeText(source.getClusterTypeText());
		target.setClusterRelated(source.isClusterRelated());
		target.setModeOfTransmission(source.getModeOfTransmission());
		target.setModeOfTransmissionType(source.getModeOfTransmissionType());
		target.setInfectionSource(source.getInfectionSource());
		target.setInfectionSourceText(source.getInfectionSourceText());
		target.setCountry(countryService.getByReferenceDto(source.getCountry()));
		target.setImportedCase(source.getImportedCase());
		target.setOtherDetails(source.getOtherDetails());
		target.setAirportWorker(source.getAirportWorker());
		target.setHealthcareProfessional(source.getHealthcareProfessional());
		target.setPlaceOfInfection(source.getPlaceOfInfection());
		target.setResidenceAtOnset(source.getResidenceAtOnset());
		target.setExposureInvestigationFromDate(source.getExposureInvestigationFromDate());
		target.setExposureInvestigationToDate(source.getExposureInvestigationToDate());
		target.setActivityAsCaseFromDate(source.getActivityAsCaseFromDate());
		target.setActivityAsCaseToDate(source.getActivityAsCaseToDate());

		return target;
	}

	public Exposure fillOrBuildExposureEntity(ExposureDto source, Exposure target, boolean checkChangeDate) {
		boolean targetWasNull = isNull(target);
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, Exposure::new, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getLocation(), source.getLocation());
		}

		target.setAnimalCondition(source.getAnimalCondition());
		target.setTypeOfAnimal(source.getTypeOfAnimal());
		target.setTypeOfAnimalDetails(source.getTypeOfAnimalDetails());
		target.setTypeOfChildcareFacility(source.getTypeOfChildcareFacility());
		target.setChildcareFacilityDetails(source.getChildcareFacilityDetails());
		target.setAnimalContactType(source.getAnimalContactType());
		target.setAnimalContactTypeDetails(source.getAnimalContactTypeDetails());
		target.setAnimalMarket(source.getAnimalMarket());
		target.setAnimalVaccinated(source.getAnimalVaccinated());
		target.setContactToBodyFluids(source.getContactToBodyFluids());
		target.setContactToCase(contactService.getByReferenceDto(source.getContactToCase()));
		target.setDeceasedPersonIll(source.getDeceasedPersonIll());
		target.setDeceasedPersonName(source.getDeceasedPersonName());
		target.setDeceasedPersonRelation(source.getDeceasedPersonRelation());
		target.setDescription(source.getDescription());
		target.setEatingRawAnimalProducts(source.getEatingRawAnimalProducts());
		target.setEndDate(source.getEndDate());
		target.setExposureType(source.getExposureType());
		target.setExposureTypeDetails(source.getExposureTypeDetails());
		target.setGatheringDetails(source.getGatheringDetails());
		target.setGatheringType(source.getGatheringType());
		target.setHabitationDetails(source.getHabitationDetails());
		target.setHabitationType(source.getHabitationType());
		target.setHandlingAnimals(source.getHandlingAnimals());
		target.setHandlingSamples(source.getHandlingSamples());
		target.setIndoors(source.getIndoors());
		target.setLocation(locationFacade.fillOrBuildEntity(source.getLocation(), target.getLocation(), checkChangeDate));
		target.setLongFaceToFaceContact(source.getLongFaceToFaceContact());
		target.setOtherProtectiveMeasures(source.getOtherProtectiveMeasures());
		target.setProtectiveMeasuresDetails(source.getProtectiveMeasuresDetails());
		target.setOutdoors(source.getOutdoors());
		target.setPercutaneous(source.getPercutaneous());
		target.setPhysicalContactDuringPreparation(source.getPhysicalContactDuringPreparation());
		target.setPhysicalContactWithBody(source.getPhysicalContactWithBody());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setProbableInfectionEnvironment(source.isProbableInfectionEnvironment());
		target.setShortDistance(source.getShortDistance());
		target.setStartDate(source.getStartDate());
		target.setWearingMask(source.getWearingMask());
		target.setWearingPpe(source.getWearingPpe());
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setTypeOfPlaceDetails(source.getTypeOfPlaceDetails());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setWorkEnvironment(source.getWorkEnvironment());
		target.setBodyOfWater(source.getBodyOfWater());
		target.setWaterSource(source.getWaterSource());
		target.setWaterSourceDetails(source.getWaterSourceDetails());
		target.setProphylaxis(source.getProphylaxis());
		target.setProphylaxisDate(source.getProphylaxisDate());
		target.setRiskArea(source.getRiskArea());
		target.setExposureRole(source.getExposureRole());
		target.setLargeAttendanceNumber(source.getLargeAttendanceNumber());
		target.setTravelAccommodation(source.getTravelAccommodation());
		target.setTravelAccommodationType(source.getTravelAccommodationType());
		target.setSwimmingLocation(source.getSwimmingLocation());
		target.setSwimmingLocationType(source.getSwimmingLocationType());

		target.setAnimalLocation(source.getAnimalLocation());
		target.setAnimalLocationText(source.getAnimalLocationText());
		target.setDomesticSwimming(source.getDomesticSwimming());
		target.setInternationalSwimming(source.getInternationalSwimming());
		target.setSexualExposureText(source.getSexualExposureText());

		target.setRawFoodContact(source.getRawFoodContact());
		target.setRawFoodContactText(source.getRawFoodContactText());
		target.setSymptomaticIndividualText(source.getSymptomaticIndividualText());

		target.setExposureCategory(source.getExposureCategory());
		target.setExposureSetting(source.getExposureSetting());
		target.setExposureSettingDetails(source.getExposureSettingDetails());
		target.setExposureSubSettingDetails(source.getExposureSubSettingDetails());
		target.setContactFactorDetails(source.getContactFactorDetails());
		target.setProtectiveMeasureDetails(source.getProtectiveMeasureDetails());
		target.setExposureComment(source.getExposureComment());
		target.setConditionOfAnimal(source.getConditionOfAnimal());
		target.setAnimalCategory(source.getAnimalCategory());
		target.setAnimalCategoryDetails(source.getAnimalCategoryDetails());
		target.setFomiteTransmissionLocation(source.getFomiteTransmissionLocation());

		target.setSubSettings(source.getSubSettings() != null ? source.getSubSettings() : new HashSet<>());
		target.setContactFactors(source.getContactFactors() != null ? source.getContactFactors() : new HashSet<>());
		target.setProtectiveMeasures(source.getProtectiveMeasures() != null ? source.getProtectiveMeasures() : new HashSet<>());
		target.setProphylaxisAdherence(source.getProphylaxisAdherence());
		target.setProphylaxisAdherenceDetails(source.getProphylaxisAdherenceDetails());
		target.setTravelPurpose(source.getTravelPurpose());
		target.setTravelPurposeDetails(source.getTravelPurposeDetails());
		target.setShoppingForFoodDetails(source.getShoppingForFoodDetails());
		return target;
	}

	public ActivityAsCase fillOrBuildActivityAsCaseEntity(ActivityAsCaseDto source, ActivityAsCase target, boolean checkChangeDate) {
		boolean targetWasNull = isNull(target);
		if (source == null) {
			return null;
		}

		target = DtoHelper.fillOrBuildEntity(source, target, ActivityAsCase::new, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getLocation(), source.getLocation());
		}

		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setDescription(source.getDescription());
		target.setActivityAsCaseType(source.getActivityAsCaseType());
		target.setActivityAsCaseTypeDetails(source.getActivityAsCaseTypeDetails());
		target.setLocation(locationFacade.fillOrBuildEntity(source.getLocation(), target.getLocation(), checkChangeDate));
		target.setRole(source.getRole());

		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setTypeOfPlaceDetails(source.getTypeOfPlaceDetails());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setWorkEnvironment(source.getWorkEnvironment());

		target.setGatheringType(source.getGatheringType());
		target.setGatheringDetails(source.getGatheringDetails());
		target.setHabitationType(source.getHabitationType());
		target.setHabitationDetails(source.getHabitationDetails());

		return target;
	}

	public static EpiDataDto toDto(EpiData epiData) {

		if (epiData == null) {
			return null;
		}

		EpiDataDto target = new EpiDataDto();
		EpiData source = epiData;

		DtoHelper.fillDto(target, source);

		target.setExposureDetailsKnown(source.getExposureDetailsKnown());
		target.setActivityAsCaseDetailsKnown(source.getActivityAsCaseDetailsKnown());
		target.setContactWithSourceCaseKnown(source.getContactWithSourceCaseKnown());
		target.setHighTransmissionRiskArea(source.getHighTransmissionRiskArea());
		target.setLargeOutbreaksArea(source.getLargeOutbreaksArea());
		target.setAreaInfectedAnimals(source.getAreaInfectedAnimals());

		List<ExposureDto> exposureDtos = new ArrayList<>();
		for (Exposure exposure : source.getExposures()) {
			ExposureDto exposureDto = toExposureDto(exposure);
			exposureDtos.add(exposureDto);
		}
		target.setExposures(exposureDtos);

		List<ActivityAsCaseDto> activityAsCaseDtos = new ArrayList<>();
		for (ActivityAsCase activityAsCase : source.getActivitiesAsCase()) {
			ActivityAsCaseDto activityAsCaseDto = toActivityAsCaseDto(activityAsCase);
			activityAsCaseDtos.add(activityAsCaseDto);
		}
		target.setActivitiesAsCase(activityAsCaseDtos);
		target.setClusterType(source.getClusterType());
		target.setCaseImportedStatus(source.getCaseImportedStatus());
		target.setClusterTypeText(source.getClusterTypeText());
		target.setClusterRelated(source.isClusterRelated());
		target.setModeOfTransmission(source.getModeOfTransmission());
		target.setModeOfTransmissionType(source.getModeOfTransmissionType());
		target.setInfectionSource(source.getInfectionSource());
		target.setInfectionSourceText(source.getInfectionSourceText());
		target.setCountry(CountryFacadeEjb.toReferenceDto(source.getCountry()));
		target.setImportedCase(source.getImportedCase());
		target.setOtherDetails(source.getOtherDetails());
		target.setAirportWorker(source.getAirportWorker());
		target.setHealthcareProfessional(source.getHealthcareProfessional());
		target.setPlaceOfInfection(source.getPlaceOfInfection());
		target.setResidenceAtOnset(source.getResidenceAtOnset());
		target.setExposureInvestigationFromDate(source.getExposureInvestigationFromDate());
		target.setExposureInvestigationToDate(source.getExposureInvestigationToDate());
		target.setActivityAsCaseFromDate(source.getActivityAsCaseFromDate());
		target.setActivityAsCaseToDate(source.getActivityAsCaseToDate());
		return target;
	}

	public static ExposureDto toExposureDto(Exposure source) {

		if (source == null) {
			return null;
		}

		ExposureDto target = new ExposureDto();

		DtoHelper.fillDto(target, source);

		target.setAnimalCondition(source.getAnimalCondition());
		target.setTypeOfAnimal(source.getTypeOfAnimal());
		target.setTypeOfAnimalDetails(source.getTypeOfAnimalDetails());
		target.setTypeOfChildcareFacility(source.getTypeOfChildcareFacility());
		target.setChildcareFacilityDetails(source.getChildcareFacilityDetails());
		target.setAnimalContactType(source.getAnimalContactType());
		target.setAnimalContactTypeDetails(source.getAnimalContactTypeDetails());
		target.setAnimalMarket(source.getAnimalMarket());
		target.setAnimalVaccinated(source.getAnimalVaccinated());
		target.setContactToBodyFluids(source.getContactToBodyFluids());
		target.setContactToCase(ContactFacadeEjb.toReferenceDto(source.getContactToCase()));
		target.setDeceasedPersonIll(source.getDeceasedPersonIll());
		target.setDeceasedPersonName(source.getDeceasedPersonName());
		target.setDeceasedPersonRelation(source.getDeceasedPersonRelation());
		target.setDescription(source.getDescription());
		target.setEatingRawAnimalProducts(source.getEatingRawAnimalProducts());
		target.setEndDate(source.getEndDate());
		target.setExposureType(source.getExposureType());
		target.setExposureTypeDetails(source.getExposureTypeDetails());
		target.setGatheringDetails(source.getGatheringDetails());
		target.setGatheringType(source.getGatheringType());
		target.setHabitationDetails(source.getHabitationDetails());
		target.setHabitationType(source.getHabitationType());
		target.setHandlingAnimals(source.getHandlingAnimals());
		target.setHandlingSamples(source.getHandlingSamples());
		target.setIndoors(source.getIndoors());
		target.setLocation(LocationFacadeEjb.toDto(source.getLocation()));
		target.setLongFaceToFaceContact(source.getLongFaceToFaceContact());
		target.setOtherProtectiveMeasures(source.getOtherProtectiveMeasures());
		target.setProtectiveMeasuresDetails(source.getProtectiveMeasuresDetails());
		target.setOutdoors(source.getOutdoors());
		target.setPercutaneous(source.getPercutaneous());
		target.setPhysicalContactDuringPreparation(source.getPhysicalContactDuringPreparation());
		target.setPhysicalContactWithBody(source.getPhysicalContactWithBody());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setProbableInfectionEnvironment(source.isProbableInfectionEnvironment());
		target.setShortDistance(source.getShortDistance());
		target.setStartDate(source.getStartDate());
		target.setWearingMask(source.getWearingMask());
		target.setWearingPpe(source.getWearingPpe());
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setTypeOfPlaceDetails(source.getTypeOfPlaceDetails());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setWorkEnvironment(source.getWorkEnvironment());
		target.setBodyOfWater(source.getBodyOfWater());
		target.setWaterSource(source.getWaterSource());
		target.setWaterSourceDetails(source.getWaterSourceDetails());
		target.setProphylaxis(source.getProphylaxis());
		target.setProphylaxisDate(source.getProphylaxisDate());
		target.setRiskArea(source.getRiskArea());
		target.setExposureRole(source.getExposureRole());
		target.setLargeAttendanceNumber(source.getLargeAttendanceNumber());
		target.setTravelAccommodation(source.getTravelAccommodation());
		target.setTravelAccommodationType(source.getTravelAccommodationType());
		target.setSwimmingLocation(source.getSwimmingLocation());
		target.setSwimmingLocationType(source.getSwimmingLocationType());
		target.setAnimalLocation(source.getAnimalLocation());
		target.setAnimalLocationText(source.getAnimalLocationText());
		target.setDomesticSwimming(source.getDomesticSwimming());
		target.setInternationalSwimming(source.getInternationalSwimming());
		target.setSexualExposureText(source.getSexualExposureText());
		target.setRawFoodContact(source.getRawFoodContact());
		target.setRawFoodContactText(source.getRawFoodContactText());
		target.setSymptomaticIndividualText(source.getSymptomaticIndividualText());

		target.setExposureCategory(source.getExposureCategory());
		target.setExposureSetting(source.getExposureSetting());
		target.setExposureSettingDetails(source.getExposureSettingDetails());
		target.setExposureSubSettingDetails(source.getExposureSubSettingDetails());
		target.setContactFactorDetails(source.getContactFactorDetails());
		target.setProtectiveMeasureDetails(source.getProtectiveMeasureDetails());
		target.setExposureComment(source.getExposureComment());
		target.setConditionOfAnimal(source.getConditionOfAnimal());
		target.setAnimalCategory(source.getAnimalCategory());
		target.setAnimalCategoryDetails(source.getAnimalCategoryDetails());
		target.setFomiteTransmissionLocation(source.getFomiteTransmissionLocation());

		target.setSubSettings(new HashSet<>(source.getSubSettings()));
		target.setContactFactors(new HashSet<>(source.getContactFactors()));
		target.setProtectiveMeasures(new HashSet<>(source.getProtectiveMeasures()));
		target.setProphylaxisAdherence(source.getProphylaxisAdherence());
		target.setProphylaxisAdherenceDetails(source.getProphylaxisAdherenceDetails());
		target.setTravelPurpose(source.getTravelPurpose());
		target.setTravelPurposeDetails(source.getTravelPurposeDetails());
		target.setShoppingForFoodDetails(source.getShoppingForFoodDetails());
		return target;
	}

	public static ActivityAsCaseDto toActivityAsCaseDto(ActivityAsCase source) {

		if (source == null) {
			return null;
		}

		ActivityAsCaseDto target = new ActivityAsCaseDto();

		DtoHelper.fillDto(target, source);

		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setDescription(source.getDescription());
		target.setActivityAsCaseType(source.getActivityAsCaseType());
		target.setActivityAsCaseTypeDetails(source.getActivityAsCaseTypeDetails());
		target.setLocation(LocationFacadeEjb.toDto(source.getLocation()));
		target.setRole(source.getRole());

		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setTypeOfPlaceDetails(source.getTypeOfPlaceDetails());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setWorkEnvironment(source.getWorkEnvironment());

		target.setGatheringType(source.getGatheringType());
		target.setGatheringDetails(source.getGatheringDetails());
		target.setHabitationType(source.getHabitationType());
		target.setHabitationDetails(source.getHabitationDetails());

		return target;
	}

	@LocalBean
	@Stateless
	public static class EpiDataFacadeEjbLocal extends EpiDataFacadeEjb {
	}
}

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.adverseeventsfollowingimmunization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationFacade;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationIndexDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationListCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationListEntryDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationReferenceDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AefiInvestigation;
import de.symeda.sormas.backend.common.AbstractCoreFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.vaccination.VaccinationFacadeEjb;
import de.symeda.sormas.backend.vaccination.VaccinationService;

@Stateless(name = "AefiInvestigationFacade")
@RightsAllowed(UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_VIEW)
public class AefiInvestigationFacadeEjb
	extends
	AbstractCoreFacadeEjb<AefiInvestigation, AefiInvestigationDto, AefiInvestigationIndexDto, AefiInvestigationReferenceDto, AefiInvestigationService, AefiInvestigationCriteria>
	implements AefiInvestigationFacade {

	private final Logger logger = LoggerFactory.getLogger(AefiInvestigationFacadeEjb.class);

	@EJB
	private AefiService aefiService;
	@EJB
	private LocationFacadeEjb.LocationFacadeEjbLocal locationFacade;
	@EJB
	private VaccinationFacadeEjb.VaccinationFacadeEjbLocal vaccinationFacade;
	@EJB
	private VaccinationService vaccinationService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CountryService countryService;

	public AefiInvestigationFacadeEjb() {
	}

	@Inject
	public AefiInvestigationFacadeEjb(AefiInvestigationService service) {
		super(AefiInvestigation.class, AefiInvestigationDto.class, service);
	}

	@Override
	@RightsAllowed({
		UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_CREATE,
		UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT })
	public AefiInvestigationDto save(@Valid @NotNull AefiInvestigationDto dto) {
		return save(dto, true, true);
	}

	@RightsAllowed({
		UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_CREATE,
		UserRight._ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT })
	public AefiInvestigationDto save(@Valid @NotNull AefiInvestigationDto dto, boolean checkChangeDate, boolean internal) {
		AefiInvestigation existingAefiInvestigation = service.getByUuid(dto.getUuid());

		FacadeHelper.checkCreateAndEditRights(
			existingAefiInvestigation,
			userService,
			UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_CREATE,
			UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT);

		if (internal && existingAefiInvestigation != null && !service.isEditAllowed(existingAefiInvestigation)) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorAdverseEventInvestigationNotEditable));
		}

		AefiInvestigationDto existingDto = toDto(existingAefiInvestigation);

		Pseudonymizer<AefiInvestigationDto> pseudonymizer = createPseudonymizer(existingAefiInvestigation);
		restorePseudonymizedDto(dto, existingDto, existingAefiInvestigation, pseudonymizer);

		validate(dto);

		AefiInvestigation aefiInvestigation = fillOrBuildEntity(dto, existingAefiInvestigation, checkChangeDate);

		service.ensurePersisted(aefiInvestigation);

		return toPseudonymizedDto(aefiInvestigation, pseudonymizer);
	}

	@Override
	public long count(AefiInvestigationCriteria criteria) {
		return service.count(criteria);
	}

	@Override
	public List<AefiInvestigationIndexDto> getIndexList(
		AefiInvestigationCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {
		List<AefiInvestigationIndexDto> resultsList = service.getIndexList(criteria, first, max, sortProperties);
		Pseudonymizer<AefiInvestigationIndexDto> pseudonymizer = createGenericPlaceholderPseudonymizer();
		pseudonymizer.pseudonymizeDtoCollection(AefiInvestigationIndexDto.class, resultsList, AefiInvestigationIndexDto::isInJurisdiction, null);
		return resultsList;
	}

	@Override
	public List<AefiInvestigationListEntryDto> getEntriesList(AefiInvestigationListCriteria criteria, Integer first, Integer max) {
		Long aefiId = aefiService.getIdByUuid(criteria.getAefiReport().getUuid());
		return service.getEntriesList(aefiId, first, max);
	}

	@Override
	public void validate(AefiInvestigationDto aefiInvestigationDto) throws ValidationRuntimeException {
		if (DateHelper.isDateAfter(aefiInvestigationDto.getInvestigationDate(), aefiInvestigationDto.getReportDate())) {
			String validationError = String.format(
				I18nProperties.getValidationError(Validations.afterDate),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, AefiInvestigationDto.INVESTIGATION_DATE),
				I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, AefiInvestigationDto.REPORT_DATE));
			throw new ValidationRuntimeException(validationError);
		}

		// Check whether any required field that does not have a not null constraint in the database is empty
		if (aefiInvestigationDto.getAefiReport() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validAefiReport));
		}

		/*
		 * if (aefiInvestigationDto.getPrimarySuspectVaccine() == null) {
		 * throw new
		 * ValidationRuntimeException(I18nProperties.getValidationError(Validations.aefiInvestigationWithoutPrimarySuspectVaccine));
		 * }
		 */

		if (aefiInvestigationDto.getReportingUser() == null && !aefiInvestigationDto.isPseudonymized()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.validReportingUser));
		}
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {
		return null;
	}

	@Override
	public List<ProcessedEntity> delete(List<String> uuids, DeletionDetails deletionDetails) {
		return null;
	}

	@Override
	public List<ProcessedEntity> restore(List<String> uuids) {
		return null;
	}

	@Override
	protected AefiInvestigation fillOrBuildEntity(AefiInvestigationDto source, AefiInvestigation target, boolean checkChangeDate) {
		return fillOrBuildEntity(source, target, checkChangeDate, false);
	}

	protected AefiInvestigation fillOrBuildEntity(
		@NotNull AefiInvestigationDto source,
		AefiInvestigation target,
		boolean checkChangeDate,
		boolean includeVaccinations) {

		target = DtoHelper.fillOrBuildEntity(source, target, AefiInvestigation::build, checkChangeDate);

		target.setAefiReport(aefiService.getByReferenceDto(source.getAefiReport()));
		target.setAddress(locationFacade.fillOrBuildEntity(source.getAddress(), target.getAddress(), checkChangeDate));

		if (includeVaccinations) {
			List<Vaccination> vaccinationEntities = new ArrayList<>();
			for (VaccinationDto vaccinationDto : source.getVaccinations()) {
				Vaccination vaccination = vaccinationService.getByUuid(vaccinationDto.getUuid());
				vaccination = vaccinationFacade.fillOrBuildEntity(vaccinationDto, vaccination, checkChangeDate);
				vaccinationEntities.add(vaccination);
			}
			target.getVaccinations().clear();
			target.getVaccinations().addAll(vaccinationEntities);
		}

		if (source.getPrimarySuspectVaccine() != null) {
			target.setPrimarySuspectVaccine(vaccinationService.getByUuid(source.getPrimarySuspectVaccine().getUuid()));
		}

		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setExternalId(source.getExternalId());
		target.setResponsibleRegion(regionService.getByReferenceDto(source.getResponsibleRegion()));
		target.setResponsibleDistrict(districtService.getByReferenceDto(source.getResponsibleDistrict()));
		target.setResponsibleCommunity(communityService.getByReferenceDto(source.getResponsibleCommunity()));
		target.setCountry(countryService.getByReferenceDto(source.getCountry()));
		target.setInvestigationCaseId(source.getInvestigationCaseId());
		target.setPlaceOfVaccination(source.getPlaceOfVaccination());
		target.setPlaceOfVaccinationDetails(source.getPlaceOfVaccinationDetails());
		target.setVaccinationActivity(source.getVaccinationActivity());
		target.setVaccinationActivityDetails(source.getVaccinationActivityDetails());
		target.setVaccinationFacility(facilityService.getByReferenceDto(source.getVaccinationFacility()));
		target.setVaccinationFacilityDetails(source.getVaccinationFacilityDetails());
		target.setReportingOfficerName(source.getReportingOfficerName());
		target.setReportingOfficerFacility(facilityService.getByReferenceDto(source.getReportingOfficerFacility()));
		target.setReportingOfficerFacilityDetails(source.getReportingOfficerFacilityDetails());
		target.setReportingOfficerDesignation(source.getReportingOfficerDesignation());
		target.setReportingOfficerDepartment(source.getReportingOfficerDepartment());
		target.setReportingOfficerAddress(
			locationFacade.fillOrBuildEntity(source.getReportingOfficerAddress(), target.getReportingOfficerAddress(), checkChangeDate));
		target.setReportingOfficerLandlinePhoneNumber(source.getReportingOfficerLandlinePhoneNumber());
		target.setReportingOfficerEmail(source.getReportingOfficerEmail());
		target.setReportingOfficerEmail(source.getReportingOfficerEmail());
		target.setInvestigationDate(source.getInvestigationDate());
		target.setFormCompletionDate(source.getFormCompletionDate());
		target.setInvestigationStage(source.getInvestigationStage());
		target.setTypeOfSite(source.getTypeOfSite());
		target.setTypeOfSiteDetails(source.getTypeOfSiteDetails());
		target.setKeySymptomDateTime(source.getKeySymptomDateTime());
		target.setHospitalizationDate(source.getHospitalizationDate());
		target.setReportedToHealthAuthorityDate(source.getReportedToHealthAuthorityDate());
		target.setStatusOnDateOfInvestigation(source.getStatusOnDateOfInvestigation());
		target.setDeathDateTime(source.getDeathDateTime());
		target.setAutopsyDone(source.getAutopsyDone());
		target.setAutopsyPlannedDateTime(source.getAutopsyPlannedDateTime());
		target.setPastHistoryOfSimilarEvent(source.getPastHistoryOfSimilarEvent());
		target.setPastHistoryOfSimilarEventDetails(source.getPastHistoryOfSimilarEventDetails());
		target.setAdverseEventAfterPreviousVaccinations(source.getAdverseEventAfterPreviousVaccinations());
		target.setAdverseEventAfterPreviousVaccinationsDetails(source.getAdverseEventAfterPreviousVaccinationsDetails());
		target.setHistoryOfAllergyToVaccineDrugOrFood(source.getHistoryOfAllergyToVaccineDrugOrFood());
		target.setHistoryOfAllergyToVaccineDrugOrFoodDetails(source.getHistoryOfAllergyToVaccineDrugOrFoodDetails());
		target.setPreExistingIllnessThirtyDaysOrCongenitalDisorder(source.getPreExistingIllnessThirtyDaysOrCongenitalDisorder());
		target.setPreExistingIllnessThirtyDaysOrCongenitalDisorderDetails(source.getPreExistingIllnessThirtyDaysOrCongenitalDisorderDetails());
		target.setHistoryOfHospitalizationInLastThirtyDaysWithCause(source.getHistoryOfHospitalizationInLastThirtyDaysWithCause());
		target.setHistoryOfHospitalizationInLastThirtyDaysWithCauseDetails(source.getHistoryOfHospitalizationInLastThirtyDaysWithCauseDetails());
		target.setCurrentlyOnConcomitantMedication(source.getCurrentlyOnConcomitantMedication());
		target.setCurrentlyOnConcomitantMedicationDetails(source.getCurrentlyOnConcomitantMedicationDetails());
		target.setFamilyHistoryOfDiseaseOrAllergy(source.getFamilyHistoryOfDiseaseOrAllergy());
		target.setFamilyHistoryOfDiseaseOrAllergyDetails(source.getFamilyHistoryOfDiseaseOrAllergyDetails());
		target.setBirthTerm(source.getBirthTerm());
		target.setBirthWeight(source.getBirthWeight());
		target.setDeliveryProcedure(source.getDeliveryProcedure());
		target.setDeliveryProcedureDetails(source.getDeliveryProcedureDetails());
		target.setSeriousAefiInfoSource(source.getSeriousAefiInfoSource());
		target.setSeriousAefiInfoSourceDetails(source.getSeriousAefiInfoSourceDetails());
		target.setSeriousAefiVerbalAutopsyInfoSourceDetails(source.getSeriousAefiVerbalAutopsyInfoSourceDetails());
		target.setFirstCaregiversName(source.getFirstCaregiversName());
		target.setOtherCaregiversNames(source.getOtherCaregiversNames());
		target.setOtherSourcesWhoProvidedInfo(source.getOtherSourcesWhoProvidedInfo());
		target.setSignsAndSymptomsFromTimeOfVaccination(source.getSignsAndSymptomsFromTimeOfVaccination());
		target.setClinicalDetailsOfficerName(source.getClinicalDetailsOfficerName());
		target.setClinicalDetailsOfficerPhoneNumber(source.getClinicalDetailsOfficerPhoneNumber());
		target.setClinicalDetailsOfficerEmail(source.getClinicalDetailsOfficerEmail());
		target.setClinicalDetailsOfficerDesignation(source.getClinicalDetailsOfficerDesignation());
		target.setClinicalDetailsDateTime(source.getClinicalDetailsDateTime());
		target.setPatientReceivedMedicalCare(source.getPatientReceivedMedicalCare());
		target.setPatientReceivedMedicalCareDetails(source.getPatientReceivedMedicalCareDetails());
		target.setProvisionalOrFinalDiagnosis(source.getProvisionalOrFinalDiagnosis());
		target.setPatientImmunizedPeriod(source.getPatientImmunizedPeriod());
		target.setPatientImmunizedPeriodDetails(source.getPatientImmunizedPeriodDetails());
		target.setVaccineGivenPeriod(source.getVaccineGivenPeriod());
		target.setVaccineGivenPeriodDetails(source.getVaccineGivenPeriodDetails());
		target.setErrorPrescribingVaccine(source.getErrorPrescribingVaccine());
		target.setErrorPrescribingVaccineDetails(source.getErrorPrescribingVaccineDetails());
		target.setVaccineCouldHaveBeenUnSterile(source.getVaccineCouldHaveBeenUnSterile());
		target.setVaccineCouldHaveBeenUnSterileDetails(source.getVaccineCouldHaveBeenUnSterileDetails());
		target.setVaccinePhysicalConditionAbnormal(source.getVaccinePhysicalConditionAbnormal());
		target.setVaccinePhysicalConditionAbnormalDetails(source.getVaccinePhysicalConditionAbnormalDetails());
		target.setErrorInVaccineReconstitution(source.getErrorInVaccineReconstitution());
		target.setErrorInVaccineReconstitutionDetails(source.getErrorInVaccineReconstitutionDetails());
		target.setErrorInVaccineHandling(source.getErrorInVaccineHandling());
		target.setErrorInVaccineHandlingDetails(source.getErrorInVaccineHandlingDetails());
		target.setVaccineAdministeredIncorrectly(source.getVaccineAdministeredIncorrectly());
		target.setVaccineAdministeredIncorrectlyDetails(source.getVaccineAdministeredIncorrectlyDetails());
		target.setNumberImmunizedFromConcernedVaccineVial(source.getNumberImmunizedFromConcernedVaccineVial());
		target.setNumberImmunizedWithConcernedVaccineInSameSession(source.getNumberImmunizedWithConcernedVaccineInSameSession());
		target.setNumberImmunizedConcernedVaccineSameBatchNumberOtherLocations(
			source.getNumberImmunizedConcernedVaccineSameBatchNumberOtherLocations());
		target.setNumberImmunizedConcernedVaccineSameBatchNumberLocationDetails(
			source.getNumberImmunizedConcernedVaccineSameBatchNumberLocationDetails());
		target.setVaccineHasQualityDefect(source.getVaccineHasQualityDefect());
		target.setVaccineHasQualityDefectDetails(source.getVaccineHasQualityDefectDetails());
		target.setEventIsAStressResponseRelatedToImmunization(source.getEventIsAStressResponseRelatedToImmunization());
		target.setEventIsAStressResponseRelatedToImmunizationDetails(source.getEventIsAStressResponseRelatedToImmunizationDetails());
		target.setCaseIsPartOfACluster(source.getCaseIsPartOfACluster());
		target.setCaseIsPartOfAClusterDetails(source.getCaseIsPartOfAClusterDetails());
		target.setNumberOfCasesDetectedInCluster(source.getNumberOfCasesDetectedInCluster());
		target.setAllCasesInClusterReceivedVaccineFromSameVial(source.getAllCasesInClusterReceivedVaccineFromSameVial());
		target.setAllCasesInClusterReceivedVaccineFromSameVialDetails(source.getAllCasesInClusterReceivedVaccineFromSameVialDetails());
		target.setNumberOfVialsUsedInCluster(source.getNumberOfVialsUsedInCluster());
		target.setNumberOfVialsUsedInClusterDetails(source.getNumberOfVialsUsedInClusterDetails());
		target.setAdSyringesUsedForImmunization(source.getAdSyringesUsedForImmunization());
		target.setTypeOfSyringesUsed(source.getTypeOfSyringesUsed());
		target.setTypeOfSyringesUsedDetails(source.getTypeOfSyringesUsedDetails());
		target.setSyringesUsedAdditionalDetails(source.getSyringesUsedAdditionalDetails());
		target.setSameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine(source.getSameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine());
		target.setSameReconstitutionSyringeUsedForReconstitutingDifferentVaccines(
			source.getSameReconstitutionSyringeUsedForReconstitutingDifferentVaccines());
		target.setSameReconstitutionSyringeForEachVaccineVial(source.getSameReconstitutionSyringeForEachVaccineVial());
		target.setSameReconstitutionSyringeForEachVaccination(source.getSameReconstitutionSyringeForEachVaccination());
		target.setVaccinesAndDiluentsUsedRecommendedByManufacturer(source.getVaccinesAndDiluentsUsedRecommendedByManufacturer());
		target.setReconstitutionAdditionalDetails(source.getReconstitutionAdditionalDetails());
		target.setCorrectDoseOrRoute(source.getCorrectDoseOrRoute());
		target.setTimeOfReconstitutionMentionedOnTheVial(source.getTimeOfReconstitutionMentionedOnTheVial());
		target.setNonTouchTechniqueFollowed(source.getNonTouchTechniqueFollowed());
		target.setContraIndicationScreenedPriorToVaccination(source.getContraIndicationScreenedPriorToVaccination());
		target.setNumberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays(
			source.getNumberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays());
		target.setTrainingReceivedByVaccinator(source.getTrainingReceivedByVaccinator());
		target.setLastTrainingReceivedByVaccinatorDate(source.getLastTrainingReceivedByVaccinatorDate());
		target.setInjectionTechniqueAdditionalDetails(source.getInjectionTechniqueAdditionalDetails());
		target.setVaccineStorageRefrigeratorTemperatureMonitored(source.getVaccineStorageRefrigeratorTemperatureMonitored());
		target.setAnyStorageTemperatureDeviationOutsideTwoToEightDegrees(source.getAnyStorageTemperatureDeviationOutsideTwoToEightDegrees());
		target.setStorageTemperatureMonitoringAdditionalDetails(source.getStorageTemperatureMonitoringAdditionalDetails());
		target.setCorrectProcedureForStorageFollowed(source.getCorrectProcedureForStorageFollowed());
		target.setAnyOtherItemInRefrigerator(source.getAnyOtherItemInRefrigerator());
		target.setPartiallyUsedReconstitutedVaccinesInRefrigerator(source.getPartiallyUsedReconstitutedVaccinesInRefrigerator());
		target.setUnusableVaccinesInRefrigerator(source.getUnusableVaccinesInRefrigerator());
		target.setUnusableDiluentsInStore(source.getUnusableDiluentsInStore());
		target.setVaccineStoragePointAdditionalDetails(source.getVaccineStoragePointAdditionalDetails());
		target.setVaccineCarrierType(source.getVaccineCarrierType());
		target.setVaccineCarrierTypeDetails(source.getVaccineCarrierTypeDetails());
		target.setVaccineCarrierSentToSiteOnSameDateAsVaccination(source.getVaccineCarrierSentToSiteOnSameDateAsVaccination());
		target.setVaccineCarrierReturnedFromSiteOnSameDateAsVaccination(source.getVaccineCarrierReturnedFromSiteOnSameDateAsVaccination());
		target.setConditionedIcepackUsed(source.getConditionedIcepackUsed());
		target.setVaccineTransportationAdditionalDetails(source.getVaccineTransportationAdditionalDetails());
		target.setSimilarEventsReportedSamePeriodAndLocality(source.getSimilarEventsReportedSamePeriodAndLocality());
		target.setSimilarEventsReportedSamePeriodAndLocalityDetails(source.getSimilarEventsReportedSamePeriodAndLocalityDetails());
		target.setNumberOfSimilarEventsReportedSamePeriodAndLocality(source.getNumberOfSimilarEventsReportedSamePeriodAndLocality());
		target.setNumberOfThoseAffectedVaccinated(source.getNumberOfThoseAffectedVaccinated());
		target.setNumberOfThoseAffectedNotVaccinated(source.getNumberOfThoseAffectedNotVaccinated());
		target.setNumberOfThoseAffectedVaccinatedUnknown(source.getNumberOfThoseAffectedVaccinatedUnknown());
		target.setCommunityInvestigationAdditionalDetails(source.getCommunityInvestigationAdditionalDetails());
		target.setOtherInvestigationFindings(source.getOtherInvestigationFindings());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setAefiClassification(source.getAefiClassification());
		target.setArchived(source.isArchived());
		target.setDeleted(source.isDeleted());
		target.setDeletionReason(source.getDeletionReason());
		target.setOtherDeletionReason(source.getOtherDeletionReason());

		return target;
	}

	@Override
	protected AefiInvestigationDto toDto(AefiInvestigation entity) {
		return toAefiInvestigationDto(entity);
	}

	public static AefiInvestigationDto toAefiInvestigationDto(AefiInvestigation entity) {

		if (entity == null) {
			return null;
		}
		AefiInvestigationDto dto = new AefiInvestigationDto();
		DtoHelper.fillDto(dto, entity);

		dto.setAefiReport(AefiFacadeEjb.toReferenceDto(entity.getAefiReport()));
		dto.setAddress(LocationFacadeEjb.toDto(entity.getAddress()));

		List<VaccinationDto> vaccinationDtos = new ArrayList<>();
		for (Vaccination vaccination : entity.getAefiReport().getImmunization().getVaccinations()) {
			VaccinationDto vaccinationDto = VaccinationFacadeEjb.toVaccinationDto(vaccination);
			vaccinationDtos.add(vaccinationDto);
		}
		dto.setVaccinations(vaccinationDtos);

		if (entity.getPrimarySuspectVaccine() != null) {
			dto.setPrimarySuspectVaccine(VaccinationFacadeEjb.toVaccinationDto(entity.getPrimarySuspectVaccine()));
		}

		dto.setReportDate(entity.getReportDate());
		dto.setReportingUser(UserFacadeEjb.toReferenceDto(entity.getReportingUser()));
		dto.setExternalId(entity.getExternalId());
		dto.setResponsibleRegion(RegionFacadeEjb.toReferenceDto(entity.getResponsibleRegion()));
		dto.setResponsibleDistrict(DistrictFacadeEjb.toReferenceDto(entity.getResponsibleDistrict()));
		dto.setResponsibleCommunity(CommunityFacadeEjb.toReferenceDto(entity.getResponsibleCommunity()));
		dto.setCountry(CountryFacadeEjb.toReferenceDto(entity.getCountry()));
		dto.setInvestigationCaseId(entity.getInvestigationCaseId());
		dto.setPlaceOfVaccination(entity.getPlaceOfVaccination());
		dto.setPlaceOfVaccinationDetails(entity.getPlaceOfVaccinationDetails());
		dto.setVaccinationActivity(entity.getVaccinationActivity());
		dto.setVaccinationActivityDetails(entity.getVaccinationActivityDetails());
		dto.setVaccinationFacility(FacilityFacadeEjb.toReferenceDto(entity.getVaccinationFacility()));
		dto.setVaccinationFacilityDetails(entity.getVaccinationFacilityDetails());
		dto.setReportingOfficerName(entity.getReportingOfficerName());
		dto.setReportingOfficerFacility(FacilityFacadeEjb.toReferenceDto(entity.getReportingOfficerFacility()));
		dto.setReportingOfficerFacilityDetails(entity.getReportingOfficerFacilityDetails());
		dto.setReportingOfficerDesignation(entity.getReportingOfficerDesignation());
		dto.setReportingOfficerDepartment(entity.getReportingOfficerDepartment());
		dto.setReportingOfficerAddress(LocationFacadeEjb.toDto(entity.getReportingOfficerAddress()));
		dto.setReportingOfficerLandlinePhoneNumber(entity.getReportingOfficerLandlinePhoneNumber());
		dto.setReportingOfficerEmail(entity.getReportingOfficerEmail());
		dto.setReportingOfficerEmail(entity.getReportingOfficerEmail());
		dto.setInvestigationDate(entity.getInvestigationDate());
		dto.setFormCompletionDate(entity.getFormCompletionDate());
		dto.setInvestigationStage(entity.getInvestigationStage());
		dto.setTypeOfSite(entity.getTypeOfSite());
		dto.setTypeOfSiteDetails(entity.getTypeOfSiteDetails());
		dto.setKeySymptomDateTime(entity.getKeySymptomDateTime());
		dto.setHospitalizationDate(entity.getHospitalizationDate());
		dto.setReportedToHealthAuthorityDate(entity.getReportedToHealthAuthorityDate());
		dto.setStatusOnDateOfInvestigation(entity.getStatusOnDateOfInvestigation());
		dto.setDeathDateTime(entity.getDeathDateTime());
		dto.setAutopsyDone(entity.getAutopsyDone());
		dto.setAutopsyPlannedDateTime(entity.getAutopsyPlannedDateTime());
		dto.setPastHistoryOfSimilarEvent(entity.getPastHistoryOfSimilarEvent());
		dto.setPastHistoryOfSimilarEventDetails(entity.getPastHistoryOfSimilarEventDetails());
		dto.setAdverseEventAfterPreviousVaccinations(entity.getAdverseEventAfterPreviousVaccinations());
		dto.setAdverseEventAfterPreviousVaccinationsDetails(entity.getAdverseEventAfterPreviousVaccinationsDetails());
		dto.setHistoryOfAllergyToVaccineDrugOrFood(entity.getHistoryOfAllergyToVaccineDrugOrFood());
		dto.setHistoryOfAllergyToVaccineDrugOrFoodDetails(entity.getHistoryOfAllergyToVaccineDrugOrFoodDetails());
		dto.setPreExistingIllnessThirtyDaysOrCongenitalDisorder(entity.getPreExistingIllnessThirtyDaysOrCongenitalDisorder());
		dto.setPreExistingIllnessThirtyDaysOrCongenitalDisorderDetails(entity.getPreExistingIllnessThirtyDaysOrCongenitalDisorderDetails());
		dto.setHistoryOfHospitalizationInLastThirtyDaysWithCause(entity.getHistoryOfHospitalizationInLastThirtyDaysWithCause());
		dto.setHistoryOfHospitalizationInLastThirtyDaysWithCauseDetails(entity.getHistoryOfHospitalizationInLastThirtyDaysWithCauseDetails());
		dto.setCurrentlyOnConcomitantMedication(entity.getCurrentlyOnConcomitantMedication());
		dto.setCurrentlyOnConcomitantMedicationDetails(entity.getCurrentlyOnConcomitantMedicationDetails());
		dto.setFamilyHistoryOfDiseaseOrAllergy(entity.getFamilyHistoryOfDiseaseOrAllergy());
		dto.setFamilyHistoryOfDiseaseOrAllergyDetails(entity.getFamilyHistoryOfDiseaseOrAllergyDetails());
		dto.setBirthTerm(entity.getBirthTerm());
		dto.setBirthWeight(entity.getBirthWeight());
		dto.setDeliveryProcedure(entity.getDeliveryProcedure());
		dto.setDeliveryProcedureDetails(entity.getDeliveryProcedureDetails());
		dto.setSeriousAefiInfoSource(entity.getSeriousAefiInfoSource());
		dto.setSeriousAefiInfoSourceDetails(entity.getSeriousAefiInfoSourceDetails());
		dto.setSeriousAefiVerbalAutopsyInfoSourceDetails(entity.getSeriousAefiVerbalAutopsyInfoSourceDetails());
		dto.setFirstCaregiversName(entity.getFirstCaregiversName());
		dto.setOtherCaregiversNames(entity.getOtherCaregiversNames());
		dto.setOtherSourcesWhoProvidedInfo(entity.getOtherSourcesWhoProvidedInfo());
		dto.setSignsAndSymptomsFromTimeOfVaccination(entity.getSignsAndSymptomsFromTimeOfVaccination());
		dto.setClinicalDetailsOfficerName(entity.getClinicalDetailsOfficerName());
		dto.setClinicalDetailsOfficerPhoneNumber(entity.getClinicalDetailsOfficerPhoneNumber());
		dto.setClinicalDetailsOfficerEmail(entity.getClinicalDetailsOfficerEmail());
		dto.setClinicalDetailsOfficerDesignation(entity.getClinicalDetailsOfficerDesignation());
		dto.setClinicalDetailsDateTime(entity.getClinicalDetailsDateTime());
		dto.setPatientReceivedMedicalCare(entity.getPatientReceivedMedicalCare());
		dto.setPatientReceivedMedicalCareDetails(entity.getPatientReceivedMedicalCareDetails());
		dto.setProvisionalOrFinalDiagnosis(entity.getProvisionalOrFinalDiagnosis());
		dto.setPatientImmunizedPeriod(entity.getPatientImmunizedPeriod());
		dto.setPatientImmunizedPeriodDetails(entity.getPatientImmunizedPeriodDetails());
		dto.setVaccineGivenPeriod(entity.getVaccineGivenPeriod());
		dto.setVaccineGivenPeriodDetails(entity.getVaccineGivenPeriodDetails());
		dto.setErrorPrescribingVaccine(entity.getErrorPrescribingVaccine());
		dto.setErrorPrescribingVaccineDetails(entity.getErrorPrescribingVaccineDetails());
		dto.setVaccineCouldHaveBeenUnSterile(entity.getVaccineCouldHaveBeenUnSterile());
		dto.setVaccineCouldHaveBeenUnSterileDetails(entity.getVaccineCouldHaveBeenUnSterileDetails());
		dto.setVaccinePhysicalConditionAbnormal(entity.getVaccinePhysicalConditionAbnormal());
		dto.setVaccinePhysicalConditionAbnormalDetails(entity.getVaccinePhysicalConditionAbnormalDetails());
		dto.setErrorInVaccineReconstitution(entity.getErrorInVaccineReconstitution());
		dto.setErrorInVaccineReconstitutionDetails(entity.getErrorInVaccineReconstitutionDetails());
		dto.setErrorInVaccineHandling(entity.getErrorInVaccineHandling());
		dto.setErrorInVaccineHandlingDetails(entity.getErrorInVaccineHandlingDetails());
		dto.setVaccineAdministeredIncorrectly(entity.getVaccineAdministeredIncorrectly());
		dto.setVaccineAdministeredIncorrectlyDetails(entity.getVaccineAdministeredIncorrectlyDetails());
		dto.setNumberImmunizedFromConcernedVaccineVial(entity.getNumberImmunizedFromConcernedVaccineVial());
		dto.setNumberImmunizedWithConcernedVaccineInSameSession(entity.getNumberImmunizedWithConcernedVaccineInSameSession());
		dto.setNumberImmunizedConcernedVaccineSameBatchNumberOtherLocations(entity.getNumberImmunizedConcernedVaccineSameBatchNumberOtherLocations());
		dto.setNumberImmunizedConcernedVaccineSameBatchNumberLocationDetails(
			entity.getNumberImmunizedConcernedVaccineSameBatchNumberLocationDetails());
		dto.setVaccineHasQualityDefect(entity.getVaccineHasQualityDefect());
		dto.setVaccineHasQualityDefectDetails(entity.getVaccineHasQualityDefectDetails());
		dto.setEventIsAStressResponseRelatedToImmunization(entity.getEventIsAStressResponseRelatedToImmunization());
		dto.setEventIsAStressResponseRelatedToImmunizationDetails(entity.getEventIsAStressResponseRelatedToImmunizationDetails());
		dto.setCaseIsPartOfACluster(entity.getCaseIsPartOfACluster());
		dto.setCaseIsPartOfAClusterDetails(entity.getCaseIsPartOfAClusterDetails());
		dto.setNumberOfCasesDetectedInCluster(entity.getNumberOfCasesDetectedInCluster());
		dto.setAllCasesInClusterReceivedVaccineFromSameVial(entity.getAllCasesInClusterReceivedVaccineFromSameVial());
		dto.setAllCasesInClusterReceivedVaccineFromSameVialDetails(entity.getAllCasesInClusterReceivedVaccineFromSameVialDetails());
		dto.setNumberOfVialsUsedInCluster(entity.getNumberOfVialsUsedInCluster());
		dto.setNumberOfVialsUsedInClusterDetails(entity.getNumberOfVialsUsedInClusterDetails());
		dto.setAdSyringesUsedForImmunization(entity.getAdSyringesUsedForImmunization());
		dto.setTypeOfSyringesUsed(entity.getTypeOfSyringesUsed());
		dto.setTypeOfSyringesUsedDetails(entity.getTypeOfSyringesUsedDetails());
		dto.setSyringesUsedAdditionalDetails(entity.getSyringesUsedAdditionalDetails());
		dto.setSameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine(entity.getSameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine());
		dto.setSameReconstitutionSyringeUsedForReconstitutingDifferentVaccines(
			entity.getSameReconstitutionSyringeUsedForReconstitutingDifferentVaccines());
		dto.setSameReconstitutionSyringeForEachVaccineVial(entity.getSameReconstitutionSyringeForEachVaccineVial());
		dto.setSameReconstitutionSyringeForEachVaccination(entity.getSameReconstitutionSyringeForEachVaccination());
		dto.setVaccinesAndDiluentsUsedRecommendedByManufacturer(entity.getVaccinesAndDiluentsUsedRecommendedByManufacturer());
		dto.setReconstitutionAdditionalDetails(entity.getReconstitutionAdditionalDetails());
		dto.setCorrectDoseOrRoute(entity.getCorrectDoseOrRoute());
		dto.setTimeOfReconstitutionMentionedOnTheVial(entity.getTimeOfReconstitutionMentionedOnTheVial());
		dto.setNonTouchTechniqueFollowed(entity.getNonTouchTechniqueFollowed());
		dto.setContraIndicationScreenedPriorToVaccination(entity.getContraIndicationScreenedPriorToVaccination());
		dto.setNumberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays(
			entity.getNumberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays());
		dto.setTrainingReceivedByVaccinator(entity.getTrainingReceivedByVaccinator());
		dto.setLastTrainingReceivedByVaccinatorDate(entity.getLastTrainingReceivedByVaccinatorDate());
		dto.setInjectionTechniqueAdditionalDetails(entity.getInjectionTechniqueAdditionalDetails());
		dto.setVaccineStorageRefrigeratorTemperatureMonitored(entity.getVaccineStorageRefrigeratorTemperatureMonitored());
		dto.setAnyStorageTemperatureDeviationOutsideTwoToEightDegrees(entity.getAnyStorageTemperatureDeviationOutsideTwoToEightDegrees());
		dto.setStorageTemperatureMonitoringAdditionalDetails(entity.getStorageTemperatureMonitoringAdditionalDetails());
		dto.setCorrectProcedureForStorageFollowed(entity.getCorrectProcedureForStorageFollowed());
		dto.setAnyOtherItemInRefrigerator(entity.getAnyOtherItemInRefrigerator());
		dto.setPartiallyUsedReconstitutedVaccinesInRefrigerator(entity.getPartiallyUsedReconstitutedVaccinesInRefrigerator());
		dto.setUnusableVaccinesInRefrigerator(entity.getUnusableVaccinesInRefrigerator());
		dto.setUnusableDiluentsInStore(entity.getUnusableDiluentsInStore());
		dto.setVaccineStoragePointAdditionalDetails(entity.getVaccineStoragePointAdditionalDetails());
		dto.setVaccineCarrierType(entity.getVaccineCarrierType());
		dto.setVaccineCarrierTypeDetails(entity.getVaccineCarrierTypeDetails());
		dto.setVaccineCarrierSentToSiteOnSameDateAsVaccination(entity.getVaccineCarrierSentToSiteOnSameDateAsVaccination());
		dto.setVaccineCarrierReturnedFromSiteOnSameDateAsVaccination(entity.getVaccineCarrierReturnedFromSiteOnSameDateAsVaccination());
		dto.setConditionedIcepackUsed(entity.getConditionedIcepackUsed());
		dto.setVaccineTransportationAdditionalDetails(entity.getVaccineTransportationAdditionalDetails());
		dto.setSimilarEventsReportedSamePeriodAndLocality(entity.getSimilarEventsReportedSamePeriodAndLocality());
		dto.setSimilarEventsReportedSamePeriodAndLocalityDetails(entity.getSimilarEventsReportedSamePeriodAndLocalityDetails());
		dto.setNumberOfSimilarEventsReportedSamePeriodAndLocality(entity.getNumberOfSimilarEventsReportedSamePeriodAndLocality());
		dto.setNumberOfThoseAffectedVaccinated(entity.getNumberOfThoseAffectedVaccinated());
		dto.setNumberOfThoseAffectedNotVaccinated(entity.getNumberOfThoseAffectedNotVaccinated());
		dto.setNumberOfThoseAffectedVaccinatedUnknown(entity.getNumberOfThoseAffectedVaccinatedUnknown());
		dto.setCommunityInvestigationAdditionalDetails(entity.getCommunityInvestigationAdditionalDetails());
		dto.setOtherInvestigationFindings(entity.getOtherInvestigationFindings());
		dto.setInvestigationStatus(entity.getInvestigationStatus());
		dto.setAefiClassification(entity.getAefiClassification());
		dto.setArchived(entity.isArchived());
		dto.setDeleted(entity.isDeleted());
		dto.setDeletionReason(entity.getDeletionReason());
		dto.setOtherDeletionReason(entity.getOtherDeletionReason());

		return dto;
	}

	@Override
	protected AefiInvestigationReferenceDto toRefDto(AefiInvestigation aefiInvestigation) {
		return toReferenceDto(aefiInvestigation);
	}

	public static AefiInvestigationReferenceDto toReferenceDto(AefiInvestigation entity) {

		if (entity == null) {
			return null;
		}

		return new AefiInvestigationReferenceDto(entity.getUuid(), "");
	}

	@Override
	protected void pseudonymizeDto(
		AefiInvestigation source,
		AefiInvestigationDto dto,
		Pseudonymizer<AefiInvestigationDto> pseudonymizer,
		boolean inJurisdiction) {

		if (dto != null) {
			pseudonymizer.pseudonymizeDto(AefiInvestigationDto.class, dto, inJurisdiction, c -> {
				pseudonymizer.pseudonymizeUser(source.getReportingUser(), userService.getCurrentUser(), dto::setReportingUser, dto);
			});
		}
	}

	@Override
	protected void restorePseudonymizedDto(
		AefiInvestigationDto dto,
		AefiInvestigationDto existingDto,
		AefiInvestigation entity,
		Pseudonymizer<AefiInvestigationDto> pseudonymizer) {

		if (existingDto != null) {
			final boolean inJurisdiction = service.inJurisdictionOrOwned(entity);
			final User currentUser = userService.getCurrentUser();
			pseudonymizer.restoreUser(entity.getReportingUser(), currentUser, dto, dto::setReportingUser);
			pseudonymizer.restorePseudonymizedValues(AefiInvestigationDto.class, dto, existingDto, inJurisdiction);
		}
	}

	@Override
	protected DeletableEntityType getDeletableEntityType() {
		return null;
	}

	@LocalBean
	@Stateless
	public static class AefiInvestigationFacadeEjbLocal extends AefiInvestigationFacadeEjb {

		public AefiInvestigationFacadeEjbLocal() {
			super();
		}

		@Inject
		public AefiInvestigationFacadeEjbLocal(AefiInvestigationService service) {
			super(service);
		}
	}
}

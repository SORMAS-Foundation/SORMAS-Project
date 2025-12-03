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

package de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiCausality;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiClassification;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiClassificationSubType;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiImmunizationPeriod;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationStage;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationStatus;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiVaccinationPeriod;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.BirthTerm;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.DeliveryProcedure;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.PatientStatusAtAefiInvestigation;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.PlaceOfVaccination;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.SeriousAefiInfoSource;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.SyringeType;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.VaccinationActivity;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.VaccinationSite;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.VaccineCarrier;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.vaccination.Vaccination;

@Entity(name = "adverseeventsfollowingimmunizationinvestigation")
public class AefiInvestigation extends CoreAdo {

	private static final long serialVersionUID = 6128204752074963848L;

	public static final String TABLE_NAME = "adverseeventsfollowingimmunizationinvestigation";
	public static final String AEFI_INVESTIGATION_VACCINATIONS_TABLE_NAME = "aefiinvestigation_vaccinations";

	public static final String AEFI_REPORT = "aefiReport";
	public static final String AEFI_REPORT_ID = "aefiReportId";
	public static final String ADDRESS = "address";
	public static final String VACCINATIONS = "vaccinations";
	public static final String PRIMARY_SUSPECT_VACCINE = "primarySuspectVaccine";
	public static final String REPORT_DATE = "reportDate";
	public static final String REPORTING_USER = "reportingUser";
	public static final String EXTERNAL_ID = "externalId";
	public static final String RESPONSIBLE_REGION = "responsibleRegion";
	public static final String RESPONSIBLE_DISTRICT = "responsibleDistrict";
	public static final String RESPONSIBLE_COMMUNITY = "responsibleCommunity";
	public static final String COUNTRY = "country";
	public static final String INVESTIGATION_CASE_ID = "investigationCaseId";
	public static final String PLACE_OF_VACCINATION = "placeOfVaccination";
	public static final String PLACE_OF_VACCINATION_DETAILS = "placeOfVaccinationDetails";
	public static final String VACCINATION_ACTIVITY = "vaccinationActivity";
	public static final String VACCINATION_ACTIVITY_DETAILS = "vaccinationActivityDetails";
	public static final String VACCINATION_FACILITY = "vaccinationFacility";
	public static final String VACCINATION_FACILITY_DETAILS = "vaccinationFacilityDetails";
	public static final String REPORTING_OFFICER_NAME = "reportingOfficerName";
	public static final String REPORTING_OFFICER_FACILITY = "reportingOfficerFacility";
	public static final String REPORTING_OFFICER_FACILITY_DETAILS = "reportingOfficerFacilityDetails";
	public static final String REPORTING_OFFICER_DESIGNATION = "reportingOfficerDesignation";
	public static final String REPORTING_OFFICER_DEPARTMENT = "reportingOfficerDepartment";
	public static final String REPORTING_OFFICER_ADDRESS = "reportingOfficerAddress";
	public static final String REPORTING_OFFICER_LANDLINE_PHONE_NUMBER = "reportingOfficerLandlinePhoneNumber";
	public static final String REPORTING_OFFICER_MOBILE_PHONE_NUMBER = "reportingOfficerMobilePhoneNumber";
	public static final String REPORTING_OFFICER_EMAIL = "reportingOfficerEmail";
	public static final String INVESTIGATION_DATE = "investigationDate";
	public static final String FORM_COMPLETION_DATE = "formCompletionDate";
	public static final String INVESTIGATION_STAGE = "investigationStage";
	public static final String TYPE_OF_SITE = "typeOfSite";
	public static final String TYPE_OF_SITE_DETAILS = "typeOfSiteDetails";
	public static final String KEY_SYMPTOM_DATE_TIME = "keySymptomDateTime";
	public static final String HOSPITALIZATION_DATE = "hospitalizationDate";
	public static final String REPORTED_TO_HEALTH_AUTHORITY_DATE = "reportedToHealthAuthorityDate";
	public static final String STATUS_ON_DATE_OF_INVESTIGATION = "statusOnDateOfInvestigation";
	public static final String DEATH_DATE_TIME = "deathDateTime";
	public static final String AUTOPSY_DONE = "autopsyDone";
	public static final String AUTOPSY_DATE = "autopsyDate";
	public static final String AUTOPSY_PLANNED_DATE_TIME = "autopsyPlannedDateTime";
	public static final String PAST_HISTORY_OF_SIMILAR_EVENT = "pastHistoryOfSimilarEvent";
	public static final String PAST_HISTORY_OF_SIMILAR_EVENT_DETAILS = "pastHistoryOfSimilarEventDetails";
	public static final String ADVERSE_EVENT_AFTER_PREVIOUS_VACCINATIONS = "adverseEventAfterPreviousVaccinations";
	public static final String ADVERSE_EVENT_AFTER_PREVIOUS_VACCINATIONS_DETAILS = "adverseEventAfterPreviousVaccinationsDetails";
	public static final String HISTORY_OF_ALLERGY_TO_VACCINE_DRUG_OR_FOOD = "historyOfAllergyToVaccineDrugOrFood";
	public static final String HISTORY_OF_ALLERGY_TO_VACCINE_DRUG_OR_FOOD_DETAILS = "historyOfAllergyToVaccineDrugOrFoodDetails";
	public static final String PRE_EXISTING_ILLNESS_THIRTY_DAYS_OR_CONGENITAL_DISORDER = "preExistingIllnessThirtyDaysOrCongenitalDisorder";
	public static final String PRE_EXISTING_ILLNESS_THIRTY_DAYS_OR_CONGENITAL_DISORDER_DETAILS =
		"preExistingIllnessThirtyDaysOrCongenitalDisorderDetails";
	public static final String HISTORY_OF_HOSPITALIZATION_IN_LAST_THIRTY_DAYS_WITH_CAUSE = "historyOfHospitalizationInLastThirtyDaysWithCause";
	public static final String HISTORY_OF_HOSPITALIZATION_IN_LAST_THIRTY_DAYS_WITH_CAUSE_DETAILS =
		"historyOfHospitalizationInLastThirtyDaysWithCauseDetails";
	public static final String CURRENTLY_ON_CONCOMITANT_MEDICATION = "currentlyOnConcomitantMedication";
	public static final String CURRENTLY_ON_CONCOMITANT_MEDICATION_DETAILS = "currentlyOnConcomitantMedicationDetails";
	public static final String FAMILY_HISTORY_OF_DISEASE_OR_ALLERGY = "familyHistoryOfDiseaseOrAllergy";
	public static final String FAMILY_HISTORY_OF_DISEASE_OR_ALLERGY_DETAILS = "familyHistoryOfDiseaseOrAllergyDetails";
	public static final String NUMBER_OF_WEEKS_PREGNANT = "numberOfWeeksPregnant";
	public static final String BIRTH_TERM = "birthTerm";
	public static final String BIRTH_WEIGHT = "birthWeight";
	public static final String DELIVERY_PROCEDURE = "deliveryProcedure";
	public static final String DELIVERY_PROCEDURE_DETAILS = "deliveryProcedureDetails";
	public static final String SERIOUS_AEFI_INFO_SOURCE_STRING = "seriousAefiInfoSourceString";
	public static final String SERIOUS_AEFI_INFO_SOURCE_DETAILS = "seriousAefiInfoSourceDetails";
	public static final String SERIOUS_AEFI_VERBAL_AUTOPSY_INFO_SOURCE_DETAILS = "seriousAefiVerbalAutopsyInfoSourceDetails";
	public static final String FIRST_CAREGIVERS_NAME = "firstCaregiversName";
	public static final String OTHER_CAREGIVERS_NAMES = "otherCaregiversNames";
	public static final String OTHER_SOURCES_WHO_PROVIDED_INFO = "otherSourcesWhoProvidedInfo";
	public static final String SIGNS_AND_SYMPTOMS_FROM_TIME_OF_VACCINATION = "signsAndSymptomsFromTimeOfVaccination";
	public static final String CLINICAL_DETAILS_OFFICER_NAME = "clinicalDetailsOfficerName";
	public static final String CLINICAL_DETAILS_OFFICER_PHONE_NUMBER = "clinicalDetailsOfficerPhoneNumber";
	public static final String CLINICAL_DETAILS_OFFICER_EMAIL = "clinicalDetailsOfficerEmail";
	public static final String CLINICAL_DETAILS_OFFICER_DESIGNATION = "clinicalDetailsOfficerDesignation";
	public static final String CLINICAL_DETAILS_DATE_TIME = "clinicalDetailsDateTime";
	public static final String PATIENT_RECEIVED_MEDICAL_CARE = "patientReceivedMedicalCare";
	public static final String PATIENT_RECEIVED_MEDICAL_CARE_DETAILS = "patientReceivedMedicalCareDetails";
	public static final String PROVISIONAL_OR_FINAL_DIAGNOSIS = "provisionalOrFinalDiagnosis";
	public static final String PATIENT_IMMUNIZED_PERIOD = "patientImmunizedPeriod";
	public static final String PATIENT_IMMUNIZED_PERIOD_DETAILS = "patientImmunizedPeriodDetails";
	public static final String VACCINE_GIVEN_PERIOD = "vaccineGivenPeriod";
	public static final String VACCINE_GIVEN_PERIOD_DETAILS = "vaccineGivenPeriodDetails";
	public static final String ERROR_PRESCRIBING_VACCINE = "errorPrescribingVaccine";
	public static final String ERROR_PRESCRIBING_VACCINE_DETAILS = "errorPrescribingVaccineDetails";
	public static final String VACCINE_COULD_HAVE_BEEN_UNSTERILE = "vaccineCouldHaveBeenUnSterile";
	public static final String VACCINE_COULD_HAVE_BEEN_UNSTERILE_DETAILS = "vaccineCouldHaveBeenUnSterileDetails";
	public static final String VACCINE_PHYSICAL_CONDITION_ABNORMAL = "vaccinePhysicalConditionAbnormal";
	public static final String VACCINE_PHYSICAL_CONDITION_ABNORMAL_DETAILS = "vaccinePhysicalConditionAbnormalDetails";
	public static final String ERROR_IN_VACCINE_RECONSTITUTION = "errorInVaccineReconstitution";
	public static final String ERROR_IN_VACCINE_RECONSTITUTION_DETAILS = "errorInVaccineReconstitutionDetails";
	public static final String ERROR_IN_VACCINE_HANDLING = "errorInVaccineHandling";
	public static final String ERROR_IN_VACCINE_HANDLING_DETAILS = "errorInVaccineHandlingDetails";
	public static final String VACCINE_ADMINISTERED_INCORRECTLY = "vaccineAdministeredIncorrectly";
	public static final String VACCINE_ADMINISTERED_INCORRECTLY_DETAILS = "vaccineAdministeredIncorrectlyDetails";
	public static final String NUMBER_IMMUNIZED_FROM_CONCERNED_VACCINE_VIAL = "numberImmunizedFromConcernedVaccineVial";
	public static final String NUMBER_IMMUNIZED_WITH_CONCERNED_VACCINE_IN_SAME_SESSION = "numberImmunizedWithConcernedVaccineInSameSession";
	public static final String NUMBER_IMMUNIZED_CONCERNED_VACCINE_SAME_BATCH_NUMBER_OTHER_LOCATIONS =
		"numberImmunizedConcernedVaccineSameBatchNumberOtherLocations";
	public static final String NUMBER_IMMUNIZED_CONCERNED_VACCINE_SAME_BATCH_NUMBER_LOCATION_DETAILS =
		"numberImmunizedConcernedVaccineSameBatchNumberLocationDetails";
	public static final String VACCINE_HAS_QUALITY_DEFECT = "vaccineHasQualityDefect";
	public static final String VACCINE_HAS_QUALITY_DEFECT_DETAILS = "vaccineHasQualityDefectDetails";
	public static final String EVENT_IS_A_STRESS_RESPONSE_RELATED_TO_IMMUNIZATION = "eventIsAStressResponseRelatedToImmunization";
	public static final String EVENT_IS_A_STRESS_RESPONSE_RELATED_TO_IMMUNIZATION_DETAILS = "eventIsAStressResponseRelatedToImmunizationDetails";
	public static final String CASE_IS_PART_OF_A_CLUSTER = "caseIsPartOfACluster";
	public static final String CASE_IS_PART_OF_A_CLUSTER_DETAILS = "caseIsPartOfAClusterDetails";
	public static final String NUMBER_OF_CASES_DETECTED_IN_CLUSTER = "numberOfCasesDetectedInCluster";
	public static final String ALL_CASES_IN_CLUSTER_RECEIVED_VACCINE_FROM_SAME_VIAL = "allCasesInClusterReceivedVaccineFromSameVial";
	public static final String ALL_CASES_IN_CLUSTER_RECEIVED_VACCINE_FROM_SAME_VIAL_DETAILS = "allCasesInClusterReceivedVaccineFromSameVialDetails";
	public static final String NUMBER_OF_VIALS_USED_IN_CLUSTER = "numberOfVialsUsedInCluster";
	public static final String NUMBER_OF_VIALS_USED_IN_CLUSTER_DETAILS = "numberOfVialsUsedInClusterDetails";
	public static final String AD_SYRINGES_USED_FOR_IMMUNIZATION = "adSyringesUsedForImmunization";
	public static final String TYPE_OF_SYRINGES_USED = "typeOfSyringesUsed";
	public static final String TYPE_OF_SYRINGES_USED_DETAILS = "typeOfSyringesUsedDetails";
	public static final String SYRINGES_USED_ADDITIONAL_DETAILS = "syringesUsedAdditionalDetails";
	public static final String SAME_RECONSTITUTION_SYRINGE_USED_FOR_MULTIPLE_VIALS_OF_SAME_VACCINE =
		"sameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine";
	public static final String SAME_RECONSTITUTION_SYRINGE_USED_FOR_RECONSTITUTING_DIFFERENT_VACCINES =
		"sameReconstitutionSyringeUsedForReconstitutingDifferentVaccines";
	public static final String SAME_RECONSTITUTION_SYRINGE_FOR_EACH_VACCINE_VIAL = "sameReconstitutionSyringeForEachVaccineVial";
	public static final String SAME_RECONSTITUTION_SYRINGE_FOR_EACH_VACCINATION = "sameReconstitutionSyringeForEachVaccination";
	public static final String VACCINES_AND_DILUENTS_USED_RECOMMENDED_BY_MANUFACTURER = "vaccinesAndDiluentsUsedRecommendedByManufacturer";
	public static final String RECONSTITUTION_ADDITIONAL_DETAILS = "reconstitutionAdditionalDetails";
	public static final String CORRECT_DOSE_OR_ROUTE = "correctDoseOrRoute";
	public static final String TIME_OF_RECONSTITUTION_MENTIONED_ON_THE_VIAL = "timeOfReconstitutionMentionedOnTheVial";
	public static final String NON_TOUCH_TECHNIQUE_FOLLOWED = "nonTouchTechniqueFollowed";
	public static final String CONTRAINDICATION_SCREENED_PRIOR_TO_VACCINATION = "contraIndicationScreenedPriorToVaccination";
	public static final String NUMBER_OF_AEFI_REPORTED_FROM_VACCINE_DISTRIBUTION_CENTER_LAST_THIRTY_DAYS =
		"numberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays";
	public static final String TRAINING_RECEIVED_BY_VACCINATOR = "trainingReceivedByVaccinator";
	public static final String LAST_TRAINING_RECEIVED_BY_VACCINATOR_DATE = "lastTrainingReceivedByVaccinatorDate";
	public static final String INJECTION_TECHNIQUE_ADDITIONAL_DETAILS = "injectionTechniqueAdditionalDetails";
	public static final String VACCINE_STORAGE_REFRIGERATOR_TEMPERATURE_MONITORED = "vaccineStorageRefrigeratorTemperatureMonitored";
	public static final String ANY_STORAGE_TEMPERATURE_DEVIATION_OUTSIDE_TWO_TO_EIGHT_DEGREES =
		"anyStorageTemperatureDeviationOutsideTwoToEightDegrees";
	public static final String STORAGE_TEMPERATURE_MONITORING_ADDITIONAL_DETAILS = "storageTemperatureMonitoringAdditionalDetails";
	public static final String CORRECT_PROCEDURE_FOR_STORAGE_FOLLOWED = "correctProcedureForStorageFollowed";
	public static final String ANY_OTHER_ITEM_IN_REFRIGERATOR = "anyOtherItemInRefrigerator";
	public static final String PARTIALLY_USED_RECONSTITUTED_VACCINES_IN_REFRIGERATOR = "partiallyUsedReconstitutedVaccinesInRefrigerator";
	public static final String UNUSABLE_VACCINES_IN_REFRIGERATOR = "unusableVaccinesInRefrigerator";
	public static final String UNUSABLE_DILUENTS_IN_STORE = "unusableDiluentsInStore";
	public static final String VACCINE_STORAGE_POINT_ADDITIONAL_DETAILS = "vaccineStoragePointAdditionalDetails";
	public static final String VACCINE_CARRIER_TYPE = "vaccineCarrierType";
	public static final String VACCINE_CARRIER_TYPE_DETAILS = "vaccineCarrierTypeDetails";
	public static final String VACCINE_CARRIER_SENT_TO_SITE_ON_SAME_DATE_AS_VACCINATION = "vaccineCarrierSentToSiteOnSameDateAsVaccination";
	public static final String VACCINE_CARRIER_RETURNED_FROM_SITE_ON_SAME_DATE_AS_VACCINATION =
		"vaccineCarrierReturnedFromSiteOnSameDateAsVaccination";
	public static final String CONDITIONED_ICE_PACK_USED = "conditionedIcepackUsed";
	public static final String VACCINE_TRANSPORTATION_ADDITIONAL_DETAILS = "vaccineTransportationAdditionalDetails";
	public static final String SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY = "similarEventsReportedSamePeriodAndLocality";
	public static final String SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY_DETAILS = "similarEventsReportedSamePeriodAndLocalityDetails";
	public static final String NUMBER_OF_SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY = "numberOfSimilarEventsReportedSamePeriodAndLocality";
	public static final String NUMBER_OF_THOSE_AFFECTED_VACCINATED = "numberOfThoseAffectedVaccinated";
	public static final String NUMBER_OF_THOSE_AFFECTED_NOT_VACCINATED = "numberOfThoseAffectedNotVaccinated";
	public static final String NUMBER_OF_THOSE_AFFECTED_VACCINATED_UNKNOWN = "numberOfThoseAffectedVaccinatedUnknown";
	public static final String COMMUNITY_INVESTIGATION_ADDITIONAL_DETAILS = "communityInvestigationAdditionalDetails";
	public static final String OTHER_INVESTIGATION_FINDINGS = "otherInvestigationFindings";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String INVESTIGATION_STATUS_DETAILS = "investigationStatusDetails";
	public static final String AEFI_CLASSIFICATION = "aefiClassification";
	public static final String AEFI_CLASSIFICATION_SUB_TYPE = "aefiClassificationSubType";
	public static final String AEFI_CLASSIFICATION_DETAILS = "aefiClassificationDetails";
	public static final String CAUSALITY = "causality";
	public static final String CAUSALITY_DETAILS = "causalityDetails";
	public static final String INVESTIGATION_COMPLETION_DATE = "investigationCompletionDate";

	private Aefi aefiReport;
	private Long aefiReportId;
	private Location address;
	private Set<Vaccination> vaccinations;
	private Vaccination primarySuspectVaccine;
	private Date reportDate;
	private User reportingUser;
	private String externalId;
	private Region responsibleRegion;
	private District responsibleDistrict;
	private Community responsibleCommunity;
	private Country country;
	private String investigationCaseId;
	private PlaceOfVaccination placeOfVaccination;
	private String placeOfVaccinationDetails;
	private VaccinationActivity vaccinationActivity;
	private String vaccinationActivityDetails;
	private Facility vaccinationFacility;
	private String vaccinationFacilityDetails;
	private String reportingOfficerName;
	private Facility reportingOfficerFacility;
	private String reportingOfficerFacilityDetails;
	private String reportingOfficerDesignation;
	private String reportingOfficerDepartment;
	private Location reportingOfficerAddress;
	private String reportingOfficerLandlinePhoneNumber;
	private String reportingOfficerMobilePhoneNumber;
	private String reportingOfficerEmail;
	private Date investigationDate;
	private Date formCompletionDate;
	private AefiInvestigationStage investigationStage;
	private VaccinationSite typeOfSite;
	private String typeOfSiteDetails;
	private Date keySymptomDateTime;
	private Date hospitalizationDate;
	private Date reportedToHealthAuthorityDate;
	private PatientStatusAtAefiInvestigation statusOnDateOfInvestigation;
	private Date deathDateTime;
	private YesNoUnknown autopsyDone;
	private Date autopsyDate;
	private Date autopsyPlannedDateTime;
	private YesNoUnknown pastHistoryOfSimilarEvent;
	private String pastHistoryOfSimilarEventDetails;
	private YesNoUnknown adverseEventAfterPreviousVaccinations;
	private String adverseEventAfterPreviousVaccinationsDetails;
	private YesNoUnknown historyOfAllergyToVaccineDrugOrFood;
	private String historyOfAllergyToVaccineDrugOrFoodDetails;
	private YesNoUnknown preExistingIllnessThirtyDaysOrCongenitalDisorder;
	private String preExistingIllnessThirtyDaysOrCongenitalDisorderDetails;
	private YesNoUnknown historyOfHospitalizationInLastThirtyDaysWithCause;
	private String historyOfHospitalizationInLastThirtyDaysWithCauseDetails;
	private YesNoUnknown currentlyOnConcomitantMedication;
	private String currentlyOnConcomitantMedicationDetails;
	private YesNoUnknown familyHistoryOfDiseaseOrAllergy;
	private String familyHistoryOfDiseaseOrAllergyDetails;
	private Integer numberOfWeeksPregnant;
	private BirthTerm birthTerm;
	private Float birthWeight;
	private DeliveryProcedure deliveryProcedure;
	private String deliveryProcedureDetails;
	private Set<SeriousAefiInfoSource> seriousAefiInfoSource;
	private String seriousAefiInfoSourceString;
	private String seriousAefiInfoSourceDetails;
	private String seriousAefiVerbalAutopsyInfoSourceDetails;
	private String firstCaregiversName;
	private String otherCaregiversNames;
	private String otherSourcesWhoProvidedInfo;
	private String signsAndSymptomsFromTimeOfVaccination;
	private String clinicalDetailsOfficerName;
	private String clinicalDetailsOfficerPhoneNumber;
	private String clinicalDetailsOfficerEmail;
	private String clinicalDetailsOfficerDesignation;
	private Date clinicalDetailsDateTime;
	private YesNoUnknown patientReceivedMedicalCare;
	private String patientReceivedMedicalCareDetails;
	private String provisionalOrFinalDiagnosis;
	private AefiImmunizationPeriod patientImmunizedPeriod;
	private String patientImmunizedPeriodDetails;
	private AefiVaccinationPeriod vaccineGivenPeriod;
	private String vaccineGivenPeriodDetails;
	private YesNoUnknown errorPrescribingVaccine;
	private String errorPrescribingVaccineDetails;
	private YesNoUnknown vaccineCouldHaveBeenUnSterile;
	private String vaccineCouldHaveBeenUnSterileDetails;
	private YesNoUnknown vaccinePhysicalConditionAbnormal;
	private String vaccinePhysicalConditionAbnormalDetails;
	private YesNoUnknown errorInVaccineReconstitution;
	private String errorInVaccineReconstitutionDetails;
	private YesNoUnknown errorInVaccineHandling;
	private String errorInVaccineHandlingDetails;
	private YesNoUnknown vaccineAdministeredIncorrectly;
	private String vaccineAdministeredIncorrectlyDetails;
	private Integer numberImmunizedFromConcernedVaccineVial;
	private Integer numberImmunizedWithConcernedVaccineInSameSession;
	private Integer numberImmunizedConcernedVaccineSameBatchNumberOtherLocations;
	private String numberImmunizedConcernedVaccineSameBatchNumberLocationDetails;
	private YesNoUnknown vaccineHasQualityDefect;
	private String vaccineHasQualityDefectDetails;
	private YesNoUnknown eventIsAStressResponseRelatedToImmunization;
	private String eventIsAStressResponseRelatedToImmunizationDetails;
	private YesNoUnknown caseIsPartOfACluster;
	private String caseIsPartOfAClusterDetails;
	private Integer numberOfCasesDetectedInCluster;
	private YesNoUnknown allCasesInClusterReceivedVaccineFromSameVial;
	private String allCasesInClusterReceivedVaccineFromSameVialDetails;
	private Integer numberOfVialsUsedInCluster;
	private String numberOfVialsUsedInClusterDetails;
	private YesNoUnknown adSyringesUsedForImmunization;
	private SyringeType typeOfSyringesUsed;
	private String typeOfSyringesUsedDetails;
	private String syringesUsedAdditionalDetails;
	private YesNoUnknown sameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine;
	private YesNoUnknown sameReconstitutionSyringeUsedForReconstitutingDifferentVaccines;
	private YesNoUnknown sameReconstitutionSyringeForEachVaccineVial;
	private YesNoUnknown sameReconstitutionSyringeForEachVaccination;
	private YesNoUnknown vaccinesAndDiluentsUsedRecommendedByManufacturer;
	private String reconstitutionAdditionalDetails;
	private YesNoUnknown correctDoseOrRoute;
	private YesNoUnknown timeOfReconstitutionMentionedOnTheVial;
	private YesNoUnknown nonTouchTechniqueFollowed;
	private YesNoUnknown contraIndicationScreenedPriorToVaccination;
	private Integer numberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays;
	private YesNoUnknown trainingReceivedByVaccinator;
	private Date lastTrainingReceivedByVaccinatorDate;
	private String injectionTechniqueAdditionalDetails;
	private YesNoUnknown vaccineStorageRefrigeratorTemperatureMonitored;
	private YesNoUnknown anyStorageTemperatureDeviationOutsideTwoToEightDegrees;
	private String storageTemperatureMonitoringAdditionalDetails;
	private YesNoUnknown correctProcedureForStorageFollowed;
	private YesNoUnknown anyOtherItemInRefrigerator;
	private YesNoUnknown partiallyUsedReconstitutedVaccinesInRefrigerator;
	private YesNoUnknown unusableVaccinesInRefrigerator;
	private YesNoUnknown unusableDiluentsInStore;
	private String vaccineStoragePointAdditionalDetails;
	private VaccineCarrier vaccineCarrierType;
	private String vaccineCarrierTypeDetails;
	private YesNoUnknown vaccineCarrierSentToSiteOnSameDateAsVaccination;
	private YesNoUnknown vaccineCarrierReturnedFromSiteOnSameDateAsVaccination;
	private YesNoUnknown conditionedIcepackUsed;
	private String vaccineTransportationAdditionalDetails;
	private YesNoUnknown similarEventsReportedSamePeriodAndLocality;
	private String similarEventsReportedSamePeriodAndLocalityDetails;
	private Integer numberOfSimilarEventsReportedSamePeriodAndLocality;
	private Integer numberOfThoseAffectedVaccinated;
	private Integer numberOfThoseAffectedNotVaccinated;
	private Integer numberOfThoseAffectedVaccinatedUnknown;
	private String communityInvestigationAdditionalDetails;
	private String otherInvestigationFindings;
	private AefiInvestigationStatus investigationStatus;
	private String investigationStatusDetails;
	private AefiClassification aefiClassification;
	private AefiClassificationSubType aefiClassificationSubType;
	private String aefiClassificationDetails;
	private AefiCausality causality;
	private String causalityDetails;
	private Date investigationCompletionDate;

	public static AefiInvestigation build() {
		AefiInvestigation aefiInvestigation = new AefiInvestigation();
		return aefiInvestigation;
	}

	@ManyToOne
	@JoinColumn(name = "adverseeventsfollowingimmunization_id", nullable = false)
	public Aefi getAefiReport() {
		return aefiReport;
	}

	public void setAefiReport(Aefi aefiReport) {
		this.aefiReport = aefiReport;
	}

	@Column(name = "adverseeventsfollowingimmunization_id", updatable = false, insertable = false)
	public Long getAefiReportId() {
		return aefiReportId;
	}

	public void setAefiReportId(Long aefiReportId) {
		this.aefiReportId = aefiReportId;
	}

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Location getAddress() {
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = AEFI_INVESTIGATION_VACCINATIONS_TABLE_NAME,
		joinColumns = @JoinColumn(name = "adverseeventsfollowingimmunizationinvestigation_id"),
		inverseJoinColumns = @JoinColumn(name = "vaccination_id"))
	public Set<Vaccination> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(Set<Vaccination> vaccinations) {
		this.vaccinations = vaccinations;
	}

	@OneToOne
	public Vaccination getPrimarySuspectVaccine() {
		return primarySuspectVaccine;
	}

	public void setPrimarySuspectVaccine(Vaccination primarySuspectVaccine) {
		this.primarySuspectVaccine = primarySuspectVaccine;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Region getResponsibleRegion() {
		return responsibleRegion;
	}

	public void setResponsibleRegion(Region responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public District getResponsibleDistrict() {
		return responsibleDistrict;
	}

	public void setResponsibleDistrict(District responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Community getResponsibleCommunity() {
		return responsibleCommunity;
	}

	public void setResponsibleCommunity(Community responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getInvestigationCaseId() {
		return investigationCaseId;
	}

	public void setInvestigationCaseId(String investigationCaseId) {
		this.investigationCaseId = investigationCaseId;
	}

	@Enumerated(EnumType.STRING)
	public PlaceOfVaccination getPlaceOfVaccination() {
		return placeOfVaccination;
	}

	public void setPlaceOfVaccination(PlaceOfVaccination placeOfVaccination) {
		this.placeOfVaccination = placeOfVaccination;
	}

	public String getPlaceOfVaccinationDetails() {
		return placeOfVaccinationDetails;
	}

	public void setPlaceOfVaccinationDetails(String placeOfVaccinationDetails) {
		this.placeOfVaccinationDetails = placeOfVaccinationDetails;
	}

	@Enumerated(EnumType.STRING)
	public VaccinationActivity getVaccinationActivity() {
		return vaccinationActivity;
	}

	public void setVaccinationActivity(VaccinationActivity vaccinationActivity) {
		this.vaccinationActivity = vaccinationActivity;
	}

	public String getVaccinationActivityDetails() {
		return vaccinationActivityDetails;
	}

	public void setVaccinationActivityDetails(String vaccinationActivityDetails) {
		this.vaccinationActivityDetails = vaccinationActivityDetails;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Facility getVaccinationFacility() {
		return vaccinationFacility;
	}

	public void setVaccinationFacility(Facility vaccinationFacility) {
		this.vaccinationFacility = vaccinationFacility;
	}

	public String getVaccinationFacilityDetails() {
		return vaccinationFacilityDetails;
	}

	public void setVaccinationFacilityDetails(String vaccinationFacilityDetails) {
		this.vaccinationFacilityDetails = vaccinationFacilityDetails;
	}

	public String getReportingOfficerName() {
		return reportingOfficerName;
	}

	public void setReportingOfficerName(String reportingOfficerName) {
		this.reportingOfficerName = reportingOfficerName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Facility getReportingOfficerFacility() {
		return reportingOfficerFacility;
	}

	public void setReportingOfficerFacility(Facility reportingOfficerFacility) {
		this.reportingOfficerFacility = reportingOfficerFacility;
	}

	public String getReportingOfficerFacilityDetails() {
		return reportingOfficerFacilityDetails;
	}

	public void setReportingOfficerFacilityDetails(String reportingOfficerFacilityDetails) {
		this.reportingOfficerFacilityDetails = reportingOfficerFacilityDetails;
	}

	public String getReportingOfficerDesignation() {
		return reportingOfficerDesignation;
	}

	public void setReportingOfficerDesignation(String reportingOfficerDesignation) {
		this.reportingOfficerDesignation = reportingOfficerDesignation;
	}

	public String getReportingOfficerDepartment() {
		return reportingOfficerDepartment;
	}

	public void setReportingOfficerDepartment(String reportingOfficerDepartment) {
		this.reportingOfficerDepartment = reportingOfficerDepartment;
	}

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "reportingofficeraddress_id")
	public Location getReportingOfficerAddress() {
		return reportingOfficerAddress;
	}

	public void setReportingOfficerAddress(Location reportingOfficerAddress) {
		this.reportingOfficerAddress = reportingOfficerAddress;
	}

	public String getReportingOfficerLandlinePhoneNumber() {
		return reportingOfficerLandlinePhoneNumber;
	}

	public void setReportingOfficerLandlinePhoneNumber(String reportingOfficerLandlinePhoneNumber) {
		this.reportingOfficerLandlinePhoneNumber = reportingOfficerLandlinePhoneNumber;
	}

	public String getReportingOfficerMobilePhoneNumber() {
		return reportingOfficerMobilePhoneNumber;
	}

	public void setReportingOfficerMobilePhoneNumber(String reportingOfficerMobilePhoneNumber) {
		this.reportingOfficerMobilePhoneNumber = reportingOfficerMobilePhoneNumber;
	}

	public String getReportingOfficerEmail() {
		return reportingOfficerEmail;
	}

	public void setReportingOfficerEmail(String reportingOfficerEmail) {
		this.reportingOfficerEmail = reportingOfficerEmail;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getInvestigationDate() {
		return investigationDate;
	}

	public void setInvestigationDate(Date investigationDate) {
		this.investigationDate = investigationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getFormCompletionDate() {
		return formCompletionDate;
	}

	public void setFormCompletionDate(Date formCompletionDate) {
		this.formCompletionDate = formCompletionDate;
	}

	@Enumerated(EnumType.STRING)
	public AefiInvestigationStage getInvestigationStage() {
		return investigationStage;
	}

	public void setInvestigationStage(AefiInvestigationStage investigationStage) {
		this.investigationStage = investigationStage;
	}

	@Enumerated(EnumType.STRING)
	public VaccinationSite getTypeOfSite() {
		return typeOfSite;
	}

	public void setTypeOfSite(VaccinationSite typeOfSite) {
		this.typeOfSite = typeOfSite;
	}

	@Column
	public String getTypeOfSiteDetails() {
		return typeOfSiteDetails;
	}

	public void setTypeOfSiteDetails(String typeOfSiteDetails) {
		this.typeOfSiteDetails = typeOfSiteDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getKeySymptomDateTime() {
		return keySymptomDateTime;
	}

	public void setKeySymptomDateTime(Date keySymptomDateTime) {
		this.keySymptomDateTime = keySymptomDateTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getHospitalizationDate() {
		return hospitalizationDate;
	}

	public void setHospitalizationDate(Date hospitalizationDate) {
		this.hospitalizationDate = hospitalizationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReportedToHealthAuthorityDate() {
		return reportedToHealthAuthorityDate;
	}

	public void setReportedToHealthAuthorityDate(Date reportedToHealthAuthorityDate) {
		this.reportedToHealthAuthorityDate = reportedToHealthAuthorityDate;
	}

	@Enumerated(EnumType.STRING)
	public PatientStatusAtAefiInvestigation getStatusOnDateOfInvestigation() {
		return statusOnDateOfInvestigation;
	}

	public void setStatusOnDateOfInvestigation(PatientStatusAtAefiInvestigation statusOnDateOfInvestigation) {
		this.statusOnDateOfInvestigation = statusOnDateOfInvestigation;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDeathDateTime() {
		return deathDateTime;
	}

	public void setDeathDateTime(Date deathDateTime) {
		this.deathDateTime = deathDateTime;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAutopsyDone() {
		return autopsyDone;
	}

	public void setAutopsyDone(YesNoUnknown autopsyDone) {
		this.autopsyDone = autopsyDone;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getAutopsyDate() {
		return autopsyDate;
	}

	public void setAutopsyDate(Date autopsyDate) {
		this.autopsyDate = autopsyDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getAutopsyPlannedDateTime() {
		return autopsyPlannedDateTime;
	}

	public void setAutopsyPlannedDateTime(Date autopsyPlannedDateTime) {
		this.autopsyPlannedDateTime = autopsyPlannedDateTime;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPastHistoryOfSimilarEvent() {
		return pastHistoryOfSimilarEvent;
	}

	public void setPastHistoryOfSimilarEvent(YesNoUnknown pastHistoryOfSimilarEvent) {
		this.pastHistoryOfSimilarEvent = pastHistoryOfSimilarEvent;
	}

	@Column
	public String getPastHistoryOfSimilarEventDetails() {
		return pastHistoryOfSimilarEventDetails;
	}

	public void setPastHistoryOfSimilarEventDetails(String pastHistoryOfSimilarEventDetails) {
		this.pastHistoryOfSimilarEventDetails = pastHistoryOfSimilarEventDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAdverseEventAfterPreviousVaccinations() {
		return adverseEventAfterPreviousVaccinations;
	}

	public void setAdverseEventAfterPreviousVaccinations(YesNoUnknown adverseEventAfterPreviousVaccinations) {
		this.adverseEventAfterPreviousVaccinations = adverseEventAfterPreviousVaccinations;
	}

	@Column
	public String getAdverseEventAfterPreviousVaccinationsDetails() {
		return adverseEventAfterPreviousVaccinationsDetails;
	}

	public void setAdverseEventAfterPreviousVaccinationsDetails(String adverseEventAfterPreviousVaccinationsDetails) {
		this.adverseEventAfterPreviousVaccinationsDetails = adverseEventAfterPreviousVaccinationsDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHistoryOfAllergyToVaccineDrugOrFood() {
		return historyOfAllergyToVaccineDrugOrFood;
	}

	public void setHistoryOfAllergyToVaccineDrugOrFood(YesNoUnknown historyOfAllergyToVaccineDrugOrFood) {
		this.historyOfAllergyToVaccineDrugOrFood = historyOfAllergyToVaccineDrugOrFood;
	}

	@Column
	public String getHistoryOfAllergyToVaccineDrugOrFoodDetails() {
		return historyOfAllergyToVaccineDrugOrFoodDetails;
	}

	public void setHistoryOfAllergyToVaccineDrugOrFoodDetails(String historyOfAllergyToVaccineDrugOrFoodDetails) {
		this.historyOfAllergyToVaccineDrugOrFoodDetails = historyOfAllergyToVaccineDrugOrFoodDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPreExistingIllnessThirtyDaysOrCongenitalDisorder() {
		return preExistingIllnessThirtyDaysOrCongenitalDisorder;
	}

	public void setPreExistingIllnessThirtyDaysOrCongenitalDisorder(YesNoUnknown preExistingIllnessThirtyDaysOrCongenitalDisorder) {
		this.preExistingIllnessThirtyDaysOrCongenitalDisorder = preExistingIllnessThirtyDaysOrCongenitalDisorder;
	}

	@Column
	public String getPreExistingIllnessThirtyDaysOrCongenitalDisorderDetails() {
		return preExistingIllnessThirtyDaysOrCongenitalDisorderDetails;
	}

	public void setPreExistingIllnessThirtyDaysOrCongenitalDisorderDetails(String preExistingIllnessThirtyDaysOrCongenitalDisorderDetails) {
		this.preExistingIllnessThirtyDaysOrCongenitalDisorderDetails = preExistingIllnessThirtyDaysOrCongenitalDisorderDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getHistoryOfHospitalizationInLastThirtyDaysWithCause() {
		return historyOfHospitalizationInLastThirtyDaysWithCause;
	}

	public void setHistoryOfHospitalizationInLastThirtyDaysWithCause(YesNoUnknown historyOfHospitalizationInLastThirtyDaysWithCause) {
		this.historyOfHospitalizationInLastThirtyDaysWithCause = historyOfHospitalizationInLastThirtyDaysWithCause;
	}

	@Column
	public String getHistoryOfHospitalizationInLastThirtyDaysWithCauseDetails() {
		return historyOfHospitalizationInLastThirtyDaysWithCauseDetails;
	}

	public void setHistoryOfHospitalizationInLastThirtyDaysWithCauseDetails(String historyOfHospitalizationInLastThirtyDaysWithCauseDetails) {
		this.historyOfHospitalizationInLastThirtyDaysWithCauseDetails = historyOfHospitalizationInLastThirtyDaysWithCauseDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getCurrentlyOnConcomitantMedication() {
		return currentlyOnConcomitantMedication;
	}

	public void setCurrentlyOnConcomitantMedication(YesNoUnknown currentlyOnConcomitantMedication) {
		this.currentlyOnConcomitantMedication = currentlyOnConcomitantMedication;
	}

	@Column
	public String getCurrentlyOnConcomitantMedicationDetails() {
		return currentlyOnConcomitantMedicationDetails;
	}

	public void setCurrentlyOnConcomitantMedicationDetails(String currentlyOnConcomitantMedicationDetails) {
		this.currentlyOnConcomitantMedicationDetails = currentlyOnConcomitantMedicationDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getFamilyHistoryOfDiseaseOrAllergy() {
		return familyHistoryOfDiseaseOrAllergy;
	}

	public void setFamilyHistoryOfDiseaseOrAllergy(YesNoUnknown familyHistoryOfDiseaseOrAllergy) {
		this.familyHistoryOfDiseaseOrAllergy = familyHistoryOfDiseaseOrAllergy;
	}

	@Column
	public String getFamilyHistoryOfDiseaseOrAllergyDetails() {
		return familyHistoryOfDiseaseOrAllergyDetails;
	}

	public void setFamilyHistoryOfDiseaseOrAllergyDetails(String familyHistoryOfDiseaseOrAllergyDetails) {
		this.familyHistoryOfDiseaseOrAllergyDetails = familyHistoryOfDiseaseOrAllergyDetails;
	}

	@Column
	public Integer getNumberOfWeeksPregnant() {
		return numberOfWeeksPregnant;
	}

	public void setNumberOfWeeksPregnant(Integer numberOfWeeksPregnant) {
		this.numberOfWeeksPregnant = numberOfWeeksPregnant;
	}

	@Enumerated(EnumType.STRING)
	public BirthTerm getBirthTerm() {
		return birthTerm;
	}

	public void setBirthTerm(BirthTerm birthTerm) {
		this.birthTerm = birthTerm;
	}

	@Column
	public Float getBirthWeight() {
		return birthWeight;
	}

	public void setBirthWeight(Float birthWeight) {
		this.birthWeight = birthWeight;
	}

	@Enumerated(EnumType.STRING)
	public DeliveryProcedure getDeliveryProcedure() {
		return deliveryProcedure;
	}

	public void setDeliveryProcedure(DeliveryProcedure deliveryProcedure) {
		this.deliveryProcedure = deliveryProcedure;
	}

	@Column
	public String getDeliveryProcedureDetails() {
		return deliveryProcedureDetails;
	}

	public void setDeliveryProcedureDetails(String deliveryProcedureDetails) {
		this.deliveryProcedureDetails = deliveryProcedureDetails;
	}

	@Transient
	public Set<SeriousAefiInfoSource> getSeriousAefiInfoSource() {
		if (seriousAefiInfoSource == null) {
			if (StringUtils.isEmpty(seriousAefiInfoSourceString)) {
				seriousAefiInfoSource = new HashSet<>();
			} else {
				seriousAefiInfoSource =
					Arrays.stream(seriousAefiInfoSourceString.split(",")).map(SeriousAefiInfoSource::valueOf).collect(Collectors.toSet());
			}
		}
		return seriousAefiInfoSource;
	}

	public void setSeriousAefiInfoSource(Set<SeriousAefiInfoSource> seriousAefiInfoSource) {
		this.seriousAefiInfoSource = seriousAefiInfoSource;

		if (this.seriousAefiInfoSource == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		seriousAefiInfoSource.stream().forEach(t -> {
			sb.append(t.name());
			sb.append(",");
		});
		if (sb.length() > 0) {
			sb.substring(0, sb.lastIndexOf(","));
		}
		seriousAefiInfoSourceString = sb.toString();
	}

	@Column
	public String getSeriousAefiInfoSourceString() {
		return seriousAefiInfoSourceString;
	}

	public void setSeriousAefiInfoSourceString(String seriousAefiInfoSourceString) {
		this.seriousAefiInfoSourceString = seriousAefiInfoSourceString;
	}

	@Column
	public String getSeriousAefiInfoSourceDetails() {
		return seriousAefiInfoSourceDetails;
	}

	public void setSeriousAefiInfoSourceDetails(String seriousAefiInfoSourceDetails) {
		this.seriousAefiInfoSourceDetails = seriousAefiInfoSourceDetails;
	}

	@Column
	public String getSeriousAefiVerbalAutopsyInfoSourceDetails() {
		return seriousAefiVerbalAutopsyInfoSourceDetails;
	}

	public void setSeriousAefiVerbalAutopsyInfoSourceDetails(String seriousAefiVerbalAutopsyInfoSourceDetails) {
		this.seriousAefiVerbalAutopsyInfoSourceDetails = seriousAefiVerbalAutopsyInfoSourceDetails;
	}

	@Column
	public String getFirstCaregiversName() {
		return firstCaregiversName;
	}

	public void setFirstCaregiversName(String firstCaregiversName) {
		this.firstCaregiversName = firstCaregiversName;
	}

	@Column
	public String getOtherCaregiversNames() {
		return otherCaregiversNames;
	}

	public void setOtherCaregiversNames(String otherCaregiversNames) {
		this.otherCaregiversNames = otherCaregiversNames;
	}

	@Column
	public String getOtherSourcesWhoProvidedInfo() {
		return otherSourcesWhoProvidedInfo;
	}

	public void setOtherSourcesWhoProvidedInfo(String otherSourcesWhoProvidedInfo) {
		this.otherSourcesWhoProvidedInfo = otherSourcesWhoProvidedInfo;
	}

	@Column
	public String getSignsAndSymptomsFromTimeOfVaccination() {
		return signsAndSymptomsFromTimeOfVaccination;
	}

	public void setSignsAndSymptomsFromTimeOfVaccination(String signsAndSymptomsFromTimeOfVaccination) {
		this.signsAndSymptomsFromTimeOfVaccination = signsAndSymptomsFromTimeOfVaccination;
	}

	@Column
	public String getClinicalDetailsOfficerName() {
		return clinicalDetailsOfficerName;
	}

	public void setClinicalDetailsOfficerName(String clinicalDetailsOfficerName) {
		this.clinicalDetailsOfficerName = clinicalDetailsOfficerName;
	}

	@Column
	public String getClinicalDetailsOfficerPhoneNumber() {
		return clinicalDetailsOfficerPhoneNumber;
	}

	public void setClinicalDetailsOfficerPhoneNumber(String clinicalDetailsOfficerPhoneNumber) {
		this.clinicalDetailsOfficerPhoneNumber = clinicalDetailsOfficerPhoneNumber;
	}

	@Column
	public String getClinicalDetailsOfficerEmail() {
		return clinicalDetailsOfficerEmail;
	}

	public void setClinicalDetailsOfficerEmail(String clinicalDetailsOfficerEmail) {
		this.clinicalDetailsOfficerEmail = clinicalDetailsOfficerEmail;
	}

	@Column
	public String getClinicalDetailsOfficerDesignation() {
		return clinicalDetailsOfficerDesignation;
	}

	public void setClinicalDetailsOfficerDesignation(String clinicalDetailsOfficerDesignation) {
		this.clinicalDetailsOfficerDesignation = clinicalDetailsOfficerDesignation;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getClinicalDetailsDateTime() {
		return clinicalDetailsDateTime;
	}

	public void setClinicalDetailsDateTime(Date clinicalDetailsDateTime) {
		this.clinicalDetailsDateTime = clinicalDetailsDateTime;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPatientReceivedMedicalCare() {
		return patientReceivedMedicalCare;
	}

	public void setPatientReceivedMedicalCare(YesNoUnknown patientReceivedMedicalCare) {
		this.patientReceivedMedicalCare = patientReceivedMedicalCare;
	}

	@Column
	public String getPatientReceivedMedicalCareDetails() {
		return patientReceivedMedicalCareDetails;
	}

	public void setPatientReceivedMedicalCareDetails(String patientReceivedMedicalCareDetails) {
		this.patientReceivedMedicalCareDetails = patientReceivedMedicalCareDetails;
	}

	@Column
	public String getProvisionalOrFinalDiagnosis() {
		return provisionalOrFinalDiagnosis;
	}

	public void setProvisionalOrFinalDiagnosis(String provisionalOrFinalDiagnosis) {
		this.provisionalOrFinalDiagnosis = provisionalOrFinalDiagnosis;
	}

	@Enumerated(EnumType.STRING)
	public AefiImmunizationPeriod getPatientImmunizedPeriod() {
		return patientImmunizedPeriod;
	}

	public void setPatientImmunizedPeriod(AefiImmunizationPeriod patientImmunizedPeriod) {
		this.patientImmunizedPeriod = patientImmunizedPeriod;
	}

	@Column
	public String getPatientImmunizedPeriodDetails() {
		return patientImmunizedPeriodDetails;
	}

	public void setPatientImmunizedPeriodDetails(String patientImmunizedPeriodDetails) {
		this.patientImmunizedPeriodDetails = patientImmunizedPeriodDetails;
	}

	@Enumerated(EnumType.STRING)
	public AefiVaccinationPeriod getVaccineGivenPeriod() {
		return vaccineGivenPeriod;
	}

	public void setVaccineGivenPeriod(AefiVaccinationPeriod vaccineGivenPeriod) {
		this.vaccineGivenPeriod = vaccineGivenPeriod;
	}

	@Column
	public String getVaccineGivenPeriodDetails() {
		return vaccineGivenPeriodDetails;
	}

	public void setVaccineGivenPeriodDetails(String vaccineGivenPeriodDetails) {
		this.vaccineGivenPeriodDetails = vaccineGivenPeriodDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getErrorPrescribingVaccine() {
		return errorPrescribingVaccine;
	}

	public void setErrorPrescribingVaccine(YesNoUnknown errorPrescribingVaccine) {
		this.errorPrescribingVaccine = errorPrescribingVaccine;
	}

	@Column
	public String getErrorPrescribingVaccineDetails() {
		return errorPrescribingVaccineDetails;
	}

	public void setErrorPrescribingVaccineDetails(String errorPrescribingVaccineDetails) {
		this.errorPrescribingVaccineDetails = errorPrescribingVaccineDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getVaccineCouldHaveBeenUnSterile() {
		return vaccineCouldHaveBeenUnSterile;
	}

	public void setVaccineCouldHaveBeenUnSterile(YesNoUnknown vaccineCouldHaveBeenUnSterile) {
		this.vaccineCouldHaveBeenUnSterile = vaccineCouldHaveBeenUnSterile;
	}

	@Column
	public String getVaccineCouldHaveBeenUnSterileDetails() {
		return vaccineCouldHaveBeenUnSterileDetails;
	}

	public void setVaccineCouldHaveBeenUnSterileDetails(String vaccineCouldHaveBeenUnSterileDetails) {
		this.vaccineCouldHaveBeenUnSterileDetails = vaccineCouldHaveBeenUnSterileDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getVaccinePhysicalConditionAbnormal() {
		return vaccinePhysicalConditionAbnormal;
	}

	public void setVaccinePhysicalConditionAbnormal(YesNoUnknown vaccinePhysicalConditionAbnormal) {
		this.vaccinePhysicalConditionAbnormal = vaccinePhysicalConditionAbnormal;
	}

	@Column
	public String getVaccinePhysicalConditionAbnormalDetails() {
		return vaccinePhysicalConditionAbnormalDetails;
	}

	public void setVaccinePhysicalConditionAbnormalDetails(String vaccinePhysicalConditionAbnormalDetails) {
		this.vaccinePhysicalConditionAbnormalDetails = vaccinePhysicalConditionAbnormalDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getErrorInVaccineReconstitution() {
		return errorInVaccineReconstitution;
	}

	public void setErrorInVaccineReconstitution(YesNoUnknown errorInVaccineReconstitution) {
		this.errorInVaccineReconstitution = errorInVaccineReconstitution;
	}

	@Column
	public String getErrorInVaccineReconstitutionDetails() {
		return errorInVaccineReconstitutionDetails;
	}

	public void setErrorInVaccineReconstitutionDetails(String errorInVaccineReconstitutionDetails) {
		this.errorInVaccineReconstitutionDetails = errorInVaccineReconstitutionDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getErrorInVaccineHandling() {
		return errorInVaccineHandling;
	}

	public void setErrorInVaccineHandling(YesNoUnknown errorInVaccineHandling) {
		this.errorInVaccineHandling = errorInVaccineHandling;
	}

	@Column
	public String getErrorInVaccineHandlingDetails() {
		return errorInVaccineHandlingDetails;
	}

	public void setErrorInVaccineHandlingDetails(String errorInVaccineHandlingDetails) {
		this.errorInVaccineHandlingDetails = errorInVaccineHandlingDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getVaccineAdministeredIncorrectly() {
		return vaccineAdministeredIncorrectly;
	}

	public void setVaccineAdministeredIncorrectly(YesNoUnknown vaccineAdministeredIncorrectly) {
		this.vaccineAdministeredIncorrectly = vaccineAdministeredIncorrectly;
	}

	@Column
	public String getVaccineAdministeredIncorrectlyDetails() {
		return vaccineAdministeredIncorrectlyDetails;
	}

	public void setVaccineAdministeredIncorrectlyDetails(String vaccineAdministeredIncorrectlyDetails) {
		this.vaccineAdministeredIncorrectlyDetails = vaccineAdministeredIncorrectlyDetails;
	}

	@Column
	public Integer getNumberImmunizedFromConcernedVaccineVial() {
		return numberImmunizedFromConcernedVaccineVial;
	}

	public void setNumberImmunizedFromConcernedVaccineVial(Integer numberImmunizedFromConcernedVaccineVial) {
		this.numberImmunizedFromConcernedVaccineVial = numberImmunizedFromConcernedVaccineVial;
	}

	@Column
	public Integer getNumberImmunizedWithConcernedVaccineInSameSession() {
		return numberImmunizedWithConcernedVaccineInSameSession;
	}

	public void setNumberImmunizedWithConcernedVaccineInSameSession(Integer numberImmunizedWithConcernedVaccineInSameSession) {
		this.numberImmunizedWithConcernedVaccineInSameSession = numberImmunizedWithConcernedVaccineInSameSession;
	}

	@Column
	public Integer getNumberImmunizedConcernedVaccineSameBatchNumberOtherLocations() {
		return numberImmunizedConcernedVaccineSameBatchNumberOtherLocations;
	}

	public void setNumberImmunizedConcernedVaccineSameBatchNumberOtherLocations(
		Integer numberImmunizedConcernedVaccineSameBatchNumberOtherLocations) {
		this.numberImmunizedConcernedVaccineSameBatchNumberOtherLocations = numberImmunizedConcernedVaccineSameBatchNumberOtherLocations;
	}

	@Column
	public String getNumberImmunizedConcernedVaccineSameBatchNumberLocationDetails() {
		return numberImmunizedConcernedVaccineSameBatchNumberLocationDetails;
	}

	public void setNumberImmunizedConcernedVaccineSameBatchNumberLocationDetails(
		String numberImmunizedConcernedVaccineSameBatchNumberLocationDetails) {
		this.numberImmunizedConcernedVaccineSameBatchNumberLocationDetails = numberImmunizedConcernedVaccineSameBatchNumberLocationDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getVaccineHasQualityDefect() {
		return vaccineHasQualityDefect;
	}

	public void setVaccineHasQualityDefect(YesNoUnknown vaccineHasQualityDefect) {
		this.vaccineHasQualityDefect = vaccineHasQualityDefect;
	}

	@Column
	public String getVaccineHasQualityDefectDetails() {
		return vaccineHasQualityDefectDetails;
	}

	public void setVaccineHasQualityDefectDetails(String vaccineHasQualityDefectDetails) {
		this.vaccineHasQualityDefectDetails = vaccineHasQualityDefectDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getEventIsAStressResponseRelatedToImmunization() {
		return eventIsAStressResponseRelatedToImmunization;
	}

	public void setEventIsAStressResponseRelatedToImmunization(YesNoUnknown eventIsAStressResponseRelatedToImmunization) {
		this.eventIsAStressResponseRelatedToImmunization = eventIsAStressResponseRelatedToImmunization;
	}

	@Column
	public String getEventIsAStressResponseRelatedToImmunizationDetails() {
		return eventIsAStressResponseRelatedToImmunizationDetails;
	}

	public void setEventIsAStressResponseRelatedToImmunizationDetails(String eventIsAStressResponseRelatedToImmunizationDetails) {
		this.eventIsAStressResponseRelatedToImmunizationDetails = eventIsAStressResponseRelatedToImmunizationDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getCaseIsPartOfACluster() {
		return caseIsPartOfACluster;
	}

	public void setCaseIsPartOfACluster(YesNoUnknown caseIsPartOfACluster) {
		this.caseIsPartOfACluster = caseIsPartOfACluster;
	}

	@Column
	public String getCaseIsPartOfAClusterDetails() {
		return caseIsPartOfAClusterDetails;
	}

	public void setCaseIsPartOfAClusterDetails(String caseIsPartOfAClusterDetails) {
		this.caseIsPartOfAClusterDetails = caseIsPartOfAClusterDetails;
	}

	@Column
	public Integer getNumberOfCasesDetectedInCluster() {
		return numberOfCasesDetectedInCluster;
	}

	public void setNumberOfCasesDetectedInCluster(Integer numberOfCasesDetectedInCluster) {
		this.numberOfCasesDetectedInCluster = numberOfCasesDetectedInCluster;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAllCasesInClusterReceivedVaccineFromSameVial() {
		return allCasesInClusterReceivedVaccineFromSameVial;
	}

	public void setAllCasesInClusterReceivedVaccineFromSameVial(YesNoUnknown allCasesInClusterReceivedVaccineFromSameVial) {
		this.allCasesInClusterReceivedVaccineFromSameVial = allCasesInClusterReceivedVaccineFromSameVial;
	}

	@Column
	public String getAllCasesInClusterReceivedVaccineFromSameVialDetails() {
		return allCasesInClusterReceivedVaccineFromSameVialDetails;
	}

	public void setAllCasesInClusterReceivedVaccineFromSameVialDetails(String allCasesInClusterReceivedVaccineFromSameVialDetails) {
		this.allCasesInClusterReceivedVaccineFromSameVialDetails = allCasesInClusterReceivedVaccineFromSameVialDetails;
	}

	@Column
	public Integer getNumberOfVialsUsedInCluster() {
		return numberOfVialsUsedInCluster;
	}

	public void setNumberOfVialsUsedInCluster(Integer numberOfVialsUsedInCluster) {
		this.numberOfVialsUsedInCluster = numberOfVialsUsedInCluster;
	}

	@Column
	public String getNumberOfVialsUsedInClusterDetails() {
		return numberOfVialsUsedInClusterDetails;
	}

	public void setNumberOfVialsUsedInClusterDetails(String numberOfVialsUsedInClusterDetails) {
		this.numberOfVialsUsedInClusterDetails = numberOfVialsUsedInClusterDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAdSyringesUsedForImmunization() {
		return adSyringesUsedForImmunization;
	}

	public void setAdSyringesUsedForImmunization(YesNoUnknown adSyringesUsedForImmunization) {
		this.adSyringesUsedForImmunization = adSyringesUsedForImmunization;
	}

	@Enumerated(EnumType.STRING)
	public SyringeType getTypeOfSyringesUsed() {
		return typeOfSyringesUsed;
	}

	public void setTypeOfSyringesUsed(SyringeType typeOfSyringesUsed) {
		this.typeOfSyringesUsed = typeOfSyringesUsed;
	}

	@Column
	public String getTypeOfSyringesUsedDetails() {
		return typeOfSyringesUsedDetails;
	}

	public void setTypeOfSyringesUsedDetails(String typeOfSyringesUsedDetails) {
		this.typeOfSyringesUsedDetails = typeOfSyringesUsedDetails;
	}

	@Column
	public String getSyringesUsedAdditionalDetails() {
		return syringesUsedAdditionalDetails;
	}

	public void setSyringesUsedAdditionalDetails(String syringesUsedAdditionalDetails) {
		this.syringesUsedAdditionalDetails = syringesUsedAdditionalDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine() {
		return sameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine;
	}

	public void setSameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine(
		YesNoUnknown sameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine) {
		this.sameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine = sameReconstitutionSyringeUsedForMultipleVialsOfSameVaccine;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSameReconstitutionSyringeUsedForReconstitutingDifferentVaccines() {
		return sameReconstitutionSyringeUsedForReconstitutingDifferentVaccines;
	}

	public void setSameReconstitutionSyringeUsedForReconstitutingDifferentVaccines(
		YesNoUnknown sameReconstitutionSyringeUsedForReconstitutingDifferentVaccines) {
		this.sameReconstitutionSyringeUsedForReconstitutingDifferentVaccines = sameReconstitutionSyringeUsedForReconstitutingDifferentVaccines;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSameReconstitutionSyringeForEachVaccineVial() {
		return sameReconstitutionSyringeForEachVaccineVial;
	}

	public void setSameReconstitutionSyringeForEachVaccineVial(YesNoUnknown sameReconstitutionSyringeForEachVaccineVial) {
		this.sameReconstitutionSyringeForEachVaccineVial = sameReconstitutionSyringeForEachVaccineVial;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSameReconstitutionSyringeForEachVaccination() {
		return sameReconstitutionSyringeForEachVaccination;
	}

	public void setSameReconstitutionSyringeForEachVaccination(YesNoUnknown sameReconstitutionSyringeForEachVaccination) {
		this.sameReconstitutionSyringeForEachVaccination = sameReconstitutionSyringeForEachVaccination;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getVaccinesAndDiluentsUsedRecommendedByManufacturer() {
		return vaccinesAndDiluentsUsedRecommendedByManufacturer;
	}

	public void setVaccinesAndDiluentsUsedRecommendedByManufacturer(YesNoUnknown vaccinesAndDiluentsUsedRecommendedByManufacturer) {
		this.vaccinesAndDiluentsUsedRecommendedByManufacturer = vaccinesAndDiluentsUsedRecommendedByManufacturer;
	}

	@Column
	public String getReconstitutionAdditionalDetails() {
		return reconstitutionAdditionalDetails;
	}

	public void setReconstitutionAdditionalDetails(String reconstitutionAdditionalDetails) {
		this.reconstitutionAdditionalDetails = reconstitutionAdditionalDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getCorrectDoseOrRoute() {
		return correctDoseOrRoute;
	}

	public void setCorrectDoseOrRoute(YesNoUnknown correctDoseOrRoute) {
		this.correctDoseOrRoute = correctDoseOrRoute;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getTimeOfReconstitutionMentionedOnTheVial() {
		return timeOfReconstitutionMentionedOnTheVial;
	}

	public void setTimeOfReconstitutionMentionedOnTheVial(YesNoUnknown timeOfReconstitutionMentionedOnTheVial) {
		this.timeOfReconstitutionMentionedOnTheVial = timeOfReconstitutionMentionedOnTheVial;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getNonTouchTechniqueFollowed() {
		return nonTouchTechniqueFollowed;
	}

	public void setNonTouchTechniqueFollowed(YesNoUnknown nonTouchTechniqueFollowed) {
		this.nonTouchTechniqueFollowed = nonTouchTechniqueFollowed;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getContraIndicationScreenedPriorToVaccination() {
		return contraIndicationScreenedPriorToVaccination;
	}

	public void setContraIndicationScreenedPriorToVaccination(YesNoUnknown contraIndicationScreenedPriorToVaccination) {
		this.contraIndicationScreenedPriorToVaccination = contraIndicationScreenedPriorToVaccination;
	}

	@Column
	public Integer getNumberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays() {
		return numberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays;
	}

	public void setNumberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays(
		Integer numberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays) {
		this.numberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays = numberOfAefiReportedFromVaccineDistributionCenterLastThirtyDays;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getTrainingReceivedByVaccinator() {
		return trainingReceivedByVaccinator;
	}

	public void setTrainingReceivedByVaccinator(YesNoUnknown trainingReceivedByVaccinator) {
		this.trainingReceivedByVaccinator = trainingReceivedByVaccinator;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastTrainingReceivedByVaccinatorDate() {
		return lastTrainingReceivedByVaccinatorDate;
	}

	public void setLastTrainingReceivedByVaccinatorDate(Date lastTrainingReceivedByVaccinatorDate) {
		this.lastTrainingReceivedByVaccinatorDate = lastTrainingReceivedByVaccinatorDate;
	}

	@Column
	public String getInjectionTechniqueAdditionalDetails() {
		return injectionTechniqueAdditionalDetails;
	}

	public void setInjectionTechniqueAdditionalDetails(String injectionTechniqueAdditionalDetails) {
		this.injectionTechniqueAdditionalDetails = injectionTechniqueAdditionalDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getVaccineStorageRefrigeratorTemperatureMonitored() {
		return vaccineStorageRefrigeratorTemperatureMonitored;
	}

	public void setVaccineStorageRefrigeratorTemperatureMonitored(YesNoUnknown vaccineStorageRefrigeratorTemperatureMonitored) {
		this.vaccineStorageRefrigeratorTemperatureMonitored = vaccineStorageRefrigeratorTemperatureMonitored;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAnyStorageTemperatureDeviationOutsideTwoToEightDegrees() {
		return anyStorageTemperatureDeviationOutsideTwoToEightDegrees;
	}

	public void setAnyStorageTemperatureDeviationOutsideTwoToEightDegrees(YesNoUnknown anyStorageTemperatureDeviationOutsideTwoToEightDegrees) {
		this.anyStorageTemperatureDeviationOutsideTwoToEightDegrees = anyStorageTemperatureDeviationOutsideTwoToEightDegrees;
	}

	@Column
	public String getStorageTemperatureMonitoringAdditionalDetails() {
		return storageTemperatureMonitoringAdditionalDetails;
	}

	public void setStorageTemperatureMonitoringAdditionalDetails(String storageTemperatureMonitoringAdditionalDetails) {
		this.storageTemperatureMonitoringAdditionalDetails = storageTemperatureMonitoringAdditionalDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getCorrectProcedureForStorageFollowed() {
		return correctProcedureForStorageFollowed;
	}

	public void setCorrectProcedureForStorageFollowed(YesNoUnknown correctProcedureForStorageFollowed) {
		this.correctProcedureForStorageFollowed = correctProcedureForStorageFollowed;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getAnyOtherItemInRefrigerator() {
		return anyOtherItemInRefrigerator;
	}

	public void setAnyOtherItemInRefrigerator(YesNoUnknown anyOtherItemInRefrigerator) {
		this.anyOtherItemInRefrigerator = anyOtherItemInRefrigerator;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getPartiallyUsedReconstitutedVaccinesInRefrigerator() {
		return partiallyUsedReconstitutedVaccinesInRefrigerator;
	}

	public void setPartiallyUsedReconstitutedVaccinesInRefrigerator(YesNoUnknown partiallyUsedReconstitutedVaccinesInRefrigerator) {
		this.partiallyUsedReconstitutedVaccinesInRefrigerator = partiallyUsedReconstitutedVaccinesInRefrigerator;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getUnusableVaccinesInRefrigerator() {
		return unusableVaccinesInRefrigerator;
	}

	public void setUnusableVaccinesInRefrigerator(YesNoUnknown unusableVaccinesInRefrigerator) {
		this.unusableVaccinesInRefrigerator = unusableVaccinesInRefrigerator;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getUnusableDiluentsInStore() {
		return unusableDiluentsInStore;
	}

	public void setUnusableDiluentsInStore(YesNoUnknown unusableDiluentsInStore) {
		this.unusableDiluentsInStore = unusableDiluentsInStore;
	}

	@Column
	public String getVaccineStoragePointAdditionalDetails() {
		return vaccineStoragePointAdditionalDetails;
	}

	public void setVaccineStoragePointAdditionalDetails(String vaccineStoragePointAdditionalDetails) {
		this.vaccineStoragePointAdditionalDetails = vaccineStoragePointAdditionalDetails;
	}

	@Enumerated(EnumType.STRING)
	public VaccineCarrier getVaccineCarrierType() {
		return vaccineCarrierType;
	}

	public void setVaccineCarrierType(VaccineCarrier vaccineCarrierType) {
		this.vaccineCarrierType = vaccineCarrierType;
	}

	@Column
	public String getVaccineCarrierTypeDetails() {
		return vaccineCarrierTypeDetails;
	}

	public void setVaccineCarrierTypeDetails(String vaccineCarrierTypeDetails) {
		this.vaccineCarrierTypeDetails = vaccineCarrierTypeDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getVaccineCarrierSentToSiteOnSameDateAsVaccination() {
		return vaccineCarrierSentToSiteOnSameDateAsVaccination;
	}

	public void setVaccineCarrierSentToSiteOnSameDateAsVaccination(YesNoUnknown vaccineCarrierSentToSiteOnSameDateAsVaccination) {
		this.vaccineCarrierSentToSiteOnSameDateAsVaccination = vaccineCarrierSentToSiteOnSameDateAsVaccination;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getVaccineCarrierReturnedFromSiteOnSameDateAsVaccination() {
		return vaccineCarrierReturnedFromSiteOnSameDateAsVaccination;
	}

	public void setVaccineCarrierReturnedFromSiteOnSameDateAsVaccination(YesNoUnknown vaccineCarrierReturnedFromSiteOnSameDateAsVaccination) {
		this.vaccineCarrierReturnedFromSiteOnSameDateAsVaccination = vaccineCarrierReturnedFromSiteOnSameDateAsVaccination;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getConditionedIcepackUsed() {
		return conditionedIcepackUsed;
	}

	public void setConditionedIcepackUsed(YesNoUnknown conditionedIcepackUsed) {
		this.conditionedIcepackUsed = conditionedIcepackUsed;
	}

	@Column
	public String getVaccineTransportationAdditionalDetails() {
		return vaccineTransportationAdditionalDetails;
	}

	public void setVaccineTransportationAdditionalDetails(String vaccineTransportationAdditionalDetails) {
		this.vaccineTransportationAdditionalDetails = vaccineTransportationAdditionalDetails;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getSimilarEventsReportedSamePeriodAndLocality() {
		return similarEventsReportedSamePeriodAndLocality;
	}

	public void setSimilarEventsReportedSamePeriodAndLocality(YesNoUnknown similarEventsReportedSamePeriodAndLocality) {
		this.similarEventsReportedSamePeriodAndLocality = similarEventsReportedSamePeriodAndLocality;
	}

	@Column
	public String getSimilarEventsReportedSamePeriodAndLocalityDetails() {
		return similarEventsReportedSamePeriodAndLocalityDetails;
	}

	public void setSimilarEventsReportedSamePeriodAndLocalityDetails(String similarEventsReportedSamePeriodAndLocalityDetails) {
		this.similarEventsReportedSamePeriodAndLocalityDetails = similarEventsReportedSamePeriodAndLocalityDetails;
	}

	@Column
	public Integer getNumberOfSimilarEventsReportedSamePeriodAndLocality() {
		return numberOfSimilarEventsReportedSamePeriodAndLocality;
	}

	public void setNumberOfSimilarEventsReportedSamePeriodAndLocality(Integer numberOfSimilarEventsReportedSamePeriodAndLocality) {
		this.numberOfSimilarEventsReportedSamePeriodAndLocality = numberOfSimilarEventsReportedSamePeriodAndLocality;
	}

	@Column
	public Integer getNumberOfThoseAffectedVaccinated() {
		return numberOfThoseAffectedVaccinated;
	}

	public void setNumberOfThoseAffectedVaccinated(Integer numberOfThoseAffectedVaccinated) {
		this.numberOfThoseAffectedVaccinated = numberOfThoseAffectedVaccinated;
	}

	@Column
	public Integer getNumberOfThoseAffectedNotVaccinated() {
		return numberOfThoseAffectedNotVaccinated;
	}

	public void setNumberOfThoseAffectedNotVaccinated(Integer numberOfThoseAffectedNotVaccinated) {
		this.numberOfThoseAffectedNotVaccinated = numberOfThoseAffectedNotVaccinated;
	}

	@Column
	public Integer getNumberOfThoseAffectedVaccinatedUnknown() {
		return numberOfThoseAffectedVaccinatedUnknown;
	}

	public void setNumberOfThoseAffectedVaccinatedUnknown(Integer numberOfThoseAffectedVaccinatedUnknown) {
		this.numberOfThoseAffectedVaccinatedUnknown = numberOfThoseAffectedVaccinatedUnknown;
	}

	@Column
	public String getCommunityInvestigationAdditionalDetails() {
		return communityInvestigationAdditionalDetails;
	}

	public void setCommunityInvestigationAdditionalDetails(String communityInvestigationAdditionalDetails) {
		this.communityInvestigationAdditionalDetails = communityInvestigationAdditionalDetails;
	}

	@Column
	public String getOtherInvestigationFindings() {
		return otherInvestigationFindings;
	}

	public void setOtherInvestigationFindings(String otherInvestigationFindings) {
		this.otherInvestigationFindings = otherInvestigationFindings;
	}

	@Enumerated(EnumType.STRING)
	public AefiInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(AefiInvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	@Column
	public String getInvestigationStatusDetails() {
		return investigationStatusDetails;
	}

	public void setInvestigationStatusDetails(String investigationStatusDetails) {
		this.investigationStatusDetails = investigationStatusDetails;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "adverseeventfollowingimmunizationclassification")
	public AefiClassification getAefiClassification() {
		return aefiClassification;
	}

	public void setAefiClassification(AefiClassification aefiClassification) {
		this.aefiClassification = aefiClassification;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "adverseeventfollowingimmunizationclassificationsubtype")
	public AefiClassificationSubType getAefiClassificationSubType() {
		return aefiClassificationSubType;
	}

	public void setAefiClassificationSubType(AefiClassificationSubType aefiClassificationSubType) {
		this.aefiClassificationSubType = aefiClassificationSubType;
	}

	@Column(name = "adverseeventfollowingimmunizationclassificationdetails")
	public String getAefiClassificationDetails() {
		return aefiClassificationDetails;
	}

	public void setAefiClassificationDetails(String aefiClassificationDetails) {
		this.aefiClassificationDetails = aefiClassificationDetails;
	}

	@Column
	public AefiCausality getCausality() {
		return causality;
	}

	public void setCausality(AefiCausality causality) {
		this.causality = causality;
	}

	@Column
	public String getCausalityDetails() {
		return causalityDetails;
	}

	public void setCausalityDetails(String causalityDetails) {
		this.causalityDetails = causalityDetails;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getInvestigationCompletionDate() {
		return investigationCompletionDate;
	}

	public void setInvestigationCompletionDate(Date investigationCompletionDate) {
		this.investigationCompletionDate = investigationCompletionDate;
	}
}

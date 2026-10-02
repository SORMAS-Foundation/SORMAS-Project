/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.epipulse;

import javax.annotation.Generated;

/**
 * Every variable EpiPulse defines for the case-based subject codes SORMAS reports, and the exact
 * name each one carries as a CSV column.
 *
 * <p>
 * <strong>Generated</strong> from the EpiPulse cases metadata published at
 * <a href="https://www.ecdc.europa.eu/en/publications-data/epipulse-cases-metadata">ECDC</a>:
 * the 279 variables of the 40 case-based subject codes in
 * {@code sormas-api/src/main/resources/epipulse/EpiPulseCasesMetadata_2026-09-17_subset.csv}. Rerun
 * {@code EpipulseVariableGenerator} from the {@code sormas-api} module directory when the metadata
 * changes; do not hand-edit this file.
 */
@Generated(value = "de.symeda.sormas.api.epipulse.EpipulseVariableGenerator")
public enum EpipulseVariable {

	/** AIDS indicator disease at the time of AIDS diagnosis - HIVAIDS */
	AIDS_INDICATOR_DISEASE("AIDSIndicatorDisease"),
	/** Antiretroviral treatment - HIVAIDS */
	ART("ART"),
	/** Test method for susceptibility testing - PNEU */
	AST_METHOD("ASTMethod"),
	/** Age - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +34 more */
	AGE("Age"),
	/** Age in months - CONSYPH, DIPH, HAEINF, HIVAIDS, LIST, LYMENEURO +10 more */
	AGE_MONTH("AgeMonth"),
	/** Alternative diagnosis - VCJD */
	ALTERNATIVE_DIAGNOSIS("AlternativeDiagnosis"),
	/** Antibiotic prophylaxis - CHLAM, GONO, LGV, SYPH */
	ANTIBIOTIC_PROPHYLAXIS("AntibioticProphylaxis"),
	/** AntigenH - STEC */
	ANTIGEN_H("AntigenH"),
	/** AntigenO - STEC */
	ANTIGEN_O("AntigenO"),
	/** Antimicrobial agent (antibiotic) - DIPH */
	ANTIMICROBIAL_AGENT("AntimicrobialAgent"),
	/** Ataxia - VCJD */
	ATAXIA("Ataxia"),
	/** Beta glucuronidase activity - STEC */
	BETA_GLUCORONIDASE_ACTIVITY("BetaGlucoronidaseActivity"),
	/** Biotype - DIPH */
	BIOTYPE("Biotype"),
	/** Blood donor - VCJD */
	BLOOD_DONOR("BloodDonor"),
	/** Blood transfusion recipient - VCJD */
	BLOOD_TRANSFUSION_RECIPIENT("BloodTransfusionRecipient"),
	/** Born in the country of report - TUBE */
	BORN_REPORTING_COUNTRY("BornReportingCountry"),
	/** PCV brand dose 1 - PNEU */
	BRAND_PCV1("BrandPCV1"),
	/** PCV brand dose 2 - PNEU */
	BRAND_PCV2("BrandPCV2"),
	/** PCV brand dose 3 - PNEU */
	BRAND_PCV3("BrandPCV3"),
	/** PCV brand dose 4 - PNEU */
	BRAND_PCV4("BrandPCV4"),
	/** Case classification - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +30 more */
	CASE_CLASSIFICATION("CaseClassification"),
	/** Case definition - MPOX */
	CASE_DEFINITION("CaseDefinition"),
	/** Case precondition - LIST */
	CASE_PRECONDITION("CasePrecondition"),
	/** Cause of death - MEAS */
	CAUSE_OF_DEATH("CauseOfDeath"),
	/** Clade - MPOX */
	CLADE("Clade"),
	/** Clinical manifestation (criteria) of the case - CHIK, DENGUE, DIPH, ECHI, HAEINF, LYMENEURO +7 more */
	CLINICAL_CRITERIA("ClinicalCriteria"),
	/** Clinical criteria (symptoms) other - MPOX */
	CLINICAL_CRITERIA_OTHER("ClinicalCriteriaOther"),
	/** Clinical criteria status - CCHF, CHIK, CHLAM, CONSYPH, DENGUE, GONO +8 more */
	CLINICAL_CRITERIA_STATUS("ClinicalCriteriaStatus"),
	/** Clinical service type - CHLAM, GONO, LGV, SYPH */
	CLINICAL_SERVICE_TYPE("ClinicalServiceType"),
	/** Cluster - LEGI */
	CLUSTER("Cluster"),
	/** Cluster identification - CHIK, DENGUE, DIPH, MEAS, MUMP, RUBE +1 more */
	CLUSTER_ID("ClusterId"),
	/** Cluster related - DIPH, MEAS, MUMP, RUBE */
	CLUSTER_RELATED("ClusterRelated"),
	/** Cluster setting - DIPH, MEAS, MUMP, RUBE */
	CLUSTER_SETTING("ClusterSetting"),
	/** Complication diagnosis - DIPH, MEAS, MPOX, MUMP, RUBE, TBE */
	COMPLICATION_DIAGNOSIS("ComplicationDiagnosis"),
	/** Complication diagnosis other - MPOX */
	COMPLICATION_DIAGNOSIS_OTHER("ComplicationDiagnosisOther"),
	/** Contact sex worker - GONO, LGV, SYPH */
	CONTACT_SW("ContactSW"),
	/** Country of birth of patient - CONSYPH, DIPH, GONO, HEPB, HEPC, HIVAIDS +3 more */
	COUNTRY_OF_BIRTH("CountryOfBirth"),
	/** Country of birth of mother - CONSYPH */
	COUNTRY_OF_BIRTH_OF_MOTHER("CountryOfBirthOfMother"),
	/** Country of nationality of patient - CONSYPH, GONO, HEPB, HEPC, HIVAIDS, LGV +2 more */
	COUNTRY_OF_NATIONALITY("CountryOfNationality"),
	/** Country of nationality of mother - CONSYPH */
	COUNTRY_OF_NATIONALITY_OF_MOTHER("CountryOfNationalityOfMother"),
	/** Data source - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +34 more */
	DATA_SOURCE("DataSource"),
	/** Date of AIDS diagnosis - HIVAIDS */
	DATE_OF_AIDS_DIAGNOSIS("DateOfAIDSDiagnosis"),
	/** Date for ART - HIVAIDS */
	DATE_OF_ART("DateOfART"),
	/** Year patient arrived in the reporting country - HIVAIDS */
	DATE_OF_ARRIVAL("DateOfArrival"),
	/** Date of death - HIVAIDS, MPOX, VCJD */
	DATE_OF_DEATH("DateOfDeath"),
	/** Date of diagnosis - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +28 more */
	DATE_OF_DIAGNOSIS("DateOfDiagnosis"),
	/** Date of entry to reporting country - DIPH, TUBE */
	DATE_OF_ENTRY("DateOfEntry"),
	/** Date of first CD4 cell count at time of HIV diagnosis - HIVAIDS */
	DATE_OF_FIRST_CD4_COUNT("DateOfFirstCD4Count"),
	/** Date of first positive diagnostic sample - DIPH */
	DATE_OF_FIRST_SAMPLE("DateOfFirstSample"),
	/** Date of first HIV diagnosis - HIVAIDS */
	DATE_OF_HIV_DIAGNOSIS("DateOfHIVDiagnosis"),
	/** Date of hospitalisation - WNF */
	DATE_OF_HOSPITALISATION("DateOfHospitalisation"),
	/** Date of investigation - MEAS, RUBE */
	DATE_OF_INVESTIGATION("DateOfInvestigation"),
	/** Date of laboratory result - MEAS, RUBE */
	DATE_OF_LAB_RESULT("DateOfLabResult"),
	/** Date the patient was last seen for HIV-related care - HIVAIDS */
	DATE_OF_LAST_ATTENDANCE("DateOfLastAttendance"),
	/** Date of last vaccination - DIPH, HAEINF, MEAS, MENI, MUMP, PERT +5 more */
	DATE_OF_LAST_VACCINATION("DateOfLastVaccination"),
	/** Date of last known CD4 cell count - HIVAIDS */
	DATE_OF_LATEST_CD4_COUNT("DateOfLatestCD4Count"),
	/** Date of last known viral load (date of blood test where available) - HIVAIDS */
	DATE_OF_LATEST_VL("DateOfLatestVL"),
	/** Date of notification - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +31 more */
	DATE_OF_NOTIFICATION("DateOfNotification"),
	/** Date of onset of disease - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +31 more */
	DATE_OF_ONSET("DateOfOnset"),
	/** Date of onset of HUS - STEC */
	DATE_OF_ONSET_HUS("DateOfOnsetHUS"),
	/** Previous MPOX infection date - MPOX */
	DATE_OF_PREVIOUS_MPOX("DateOfPreviousMPOX"),
	/** Date of receipt reference laboratory - CAMP, SALM, STEC */
	DATE_OF_RECEIPT_REFERENCE_LAB("DateOfReceiptReferenceLab"),
	/** Date of receipt source laboratory - CAMP, SALM, STEC */
	DATE_OF_RECEIPT_SOURCE_LAB("DateOfReceiptSourceLab"),
	/** Date of referral - VCJD */
	DATE_OF_REFERRAL("DateOfReferral"),
	/** Date of specimen - MEAS, RUBE */
	DATE_OF_SPECIMEN("DateOfSpecimen"),
	/** Date of first dose smallpox/MPOX vaccination - MPOX */
	DATE_OF_VACCINATION_DOSE1("DateOfVaccinationDose1"),
	/** Date of second dose smallpox/MPOX vaccination - MPOX */
	DATE_OF_VACCINATION_DOSE2("DateOfVaccinationDose2"),
	/** PCV date dose 1 - PNEU */
	DATE_PCV1("DatePCV1"),
	/** PCV date dose 2 - PNEU */
	DATE_PCV2("DatePCV2"),
	/** PCV date dose 3 - PNEU */
	DATE_PCV3("DatePCV3"),
	/** PCV date dose 4 - PNEU */
	DATE_PCV4("DatePCV4"),
	/** PPV date - PNEU */
	DATE_PPV("DatePPV"),
	/** Date used for statistics - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +34 more */
	DATE_USED_FOR_STATISTICS("DateUsedForStatistics"),
	/** Dementia - VCJD */
	DEMENTIA("Dementia"),
	/** TB diagnosed ante-mortem - TUBE */
	DIAGNOSED_ANTE_MORTEM("DiagnosedAnteMortem"),
	/** Disease - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +34 more */
	DISEASE("Disease"),
	/** PCV dose 1 - PNEU */
	DOSE_PCV1("DosePCV1"),
	/** PCV dose 2 - PNEU */
	DOSE_PCV2("DosePCV2"),
	/** PCV dose 3 - PNEU */
	DOSE_PCV3("DosePCV3"),
	/** PCV dose 4 - PNEU */
	DOSE_PCV4("DosePCV4"),
	/** PPV vaccination - PNEU */
	DOSE_PPV("DosePPV"),
	/** Duration of illness - VCJD */
	DURATION_OF_ILLNESS("DurationOfIllness"),
	/** Acquired resistance to Azithromycin - SHIG */
	ECOFF_AZM("ECOFF_AZM"),
	/** ESBL - SALM */
	ESBL("ESBL"),
	/** ESBL production - STEC */
	ESBL_PRODUCTION("ESBLProduction"),
	/** Early psychiatric symptoms - VCJD */
	EARLY_PSYCH_SYMPTOMS("EarlyPsychSymptoms"),
	/** Enrolment to treatment - TUBE */
	ENROLLED_TO_TREATMENT("EnrolledToTreatment"),
	/** Enterohaemolysis - STEC */
	ENTEROHAEMOLYSIS("Enterohaemolysis"),
	/** Environmental investigation - LEGI */
	ENVIRONMENTAL_INVESTIGATION("EnvironmentalInvestigation"),
	/** Epidemiological link - VCJD */
	EPI_LINK("EpiLink"),
	/** Epidemiological link case identification - DIPH */
	EPI_LINK_CASE_ID("EpiLinkCaseId"),
	/** Epidemiological link criteria met - CCHF, CHLAM, CONSYPH, GONO, LGV, MPOX +3 more */
	EPI_LINKED("EpiLinked"),
	/** Family history of vCJD - VCJD */
	FAMILY_HISTORY_OFV_CJD("FamilyHistoryOfvCJD"),
	/** CD4 cell count at time of HIV diagnosis - HIVAIDS */
	FIRST_CD4_COUNT("FirstCD4Count"),
	/** Gender - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +34 more */
	GENDER("Gender"),
	/** Gender other - MPOX */
	GENDER_OTHER("GenderOther"),
	/** Genetic evidence - VCJD */
	GENETIC_EVIDENCE("GeneticEvidence"),
	/** Genomic characterization - MPOX */
	GENOMIC_CHARACTERIZATION("GenomicCharacterization"),
	/** Genotype - MEAS, MUMP, RUBE */
	GENOTYPE("Genotype"),
	/** Gestational age at time of maternal vaccination - PERT */
	GESTATIONAL_AGE_AT_VACCINATION("GestationalAgeAtVaccination"),
	/** HCV antigen result - HEPC */
	HCV_ANTIGEN_RESULT("HCVAntigenResult"),
	/** HCV RNA result - HEPC */
	HCVRNA_RESULT("HCVRNAResult"),
	/** HDV result - HEPB */
	HDV_RESULT("HDVResult"),
	/** Pre-exposure prophylaxis for HIV - CHLAM, GONO, LGV, SYPH */
	HIV_PR_EP("HIVPrEP"),
	/** HIV status - previous or new HIV diagnosis - CHLAM, GONO, HIVAIDS, LGV, MPOX, SYPH +1 more */
	HIV_STATUS("HIVStatus"),
	/** Describes the type of HIV infection - HIVAIDS */
	HIV_TYPE("HIVType"),
	/** HUS - STEC */
	HUS("HUS"),
	/** Health care worker - MPOX */
	HEALTH_CARE_WORKER("HealthCareWorker"),
	/** Hospitalisation - BRUC, CAMP, CHIK, DENGUE, ECHI, HEPA +13 more */
	HOSPITALISATION("Hospitalisation"),
	/** Hospitalisation status - MPOX */
	HOSPITALISATION_STATUS("HospitalisationStatus"),
	/** Human products history - VCJD */
	HUMAN_PRODUCTS_HISTORY("HumanProductsHistory"),
	/** IgG avidity test - RUBE */
	IG_G_AVIDITY_TEST("IgGAvidityTest"),
	/** ImmunoCompromised - MPOX */
	IMMUNO_COMPROMISED("ImmunoCompromised"),
	/** Imported - BRUC, CAMP, CCHF, CHIK, DENGUE, ECHI +15 more */
	IMPORTED("Imported"),
	/** Imported status - DIPH, MEAS, MENI, RUBE */
	IMPORTED_STATUS("ImportedStatus"),
	/** Intensive care - MPOX */
	INTENSIVE_CARE("IntensiveCare"),
	/** Intimin eae gene - STEC */
	INTIMIN_EAE_GENE("IntiminEaeGene"),
	/** Isolate identifier - CAMP, LIST, MENI, SALM, SHIG, STEC +1 more */
	ISOLATE_ID("IsolateId"),
	/** Last known CD4 count - HIVAIDS */
	LATEST_CD4_COUNT("LatestCD4Count"),
	/** Last known viral load - HIVAIDS */
	LATEST_VL("LatestVL"),
	/** Legionella found - LEGI */
	LEGIONELLA_FOUND("LegionellaFound"),
	/** Living setting - LIST */
	LIVING_SETTING("LivingSetting"),
	/** MIC sign for CIP - MENI */
	MIC_SIGN_CIP("MICSign_CIP"),
	/** MIC sign for CTX_CFX - MENI, PNEU */
	MIC_SIGN_CTX_CFX("MICSign_CTX_CFX"),
	/** MIC sign for ERY - PNEU */
	MIC_SIGN_ERY("MICSign_ERY"),
	/** MIC sign for PEN - MENI, PNEU */
	MIC_SIGN_PEN("MICSign_PEN"),
	/** MIC sign for RIF - MENI */
	MIC_SIGN_RIF("MICSign_RIF"),
	/** MIC value for CIP - MENI */
	MIC_VALUE_AST_CIP("MICValueAST_CIP"),
	/** MIC value for CTX_CFX - MENI, PNEU */
	MIC_VALUE_AST_CTX_CFX("MICValueAST_CTX_CFX"),
	/** MIC value for ERY - PNEU */
	MIC_VALUE_AST_ERY("MICValueAST_ERY"),
	/** MIC value for PEN - MENI, PNEU */
	MIC_VALUE_AST_PEN("MICValueAST_PEN"),
	/** MIC value for RIF - MENI */
	MIC_VALUE_AST_RIF("MICValueAST_RIF"),
	/** Main pathogen detection method - DIPH, HAEINF, MENI */
	MAIN_PATHOGEN_DETECTION_METHOD("MainPathogenDetectionMethod"),
	/** Main specimen - DIPH */
	MAIN_SPECIMEN("MainSpecimen"),
	/** Major site of the disease - TUBE */
	MAJOR_SITE_OF_TB("MajorSiteOfTB"),
	/** Matching isolates - LEGI */
	MATCHING_ISOLATES("MatchingIsolates"),
	/** Minor site of the disease - TUBE */
	MINOR_SITE_OF_TB("MinorSiteOfTB"),
	/** Describes the most probable mode of transmission - BRUC, CAMP, CCHF, CHLAM, DENGUE, ECHI +17 more */
	MODE_OF_TRANSMISSION("ModeOfTransmission"),
	/** Other probable mode of transmission - HEPB, HEPC, MPOX */
	MODE_OF_TRANSMISSION_OTHER("ModeOfTransmissionOther"),
	/** Identifies how the partner may have acquired HIV when heterosexual contact is the likely transmission route - HIVAIDS */
	MODE_OF_TRANSMISSION_PARTNER("ModeOfTransmissionPartner"),
	/** Monoclonal subtype - LEGI */
	MONOCLONAL_SUBTYPE("MonoclonalSubtype"),
	/** Myoclonus or chorea or dystonia - VCJD */
	MYOCLONUS_CHOREA_DYSTONIA("MyoclonusChoreaDystonia"),
	/** Data from NRL - PNEU */
	NRL_DATA("NRLData"),
	/** National record identifier - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +34 more */
	NATIONAL_RECORD_ID("NationalRecordId"),
	/** Citizen of the country of report - TUBE */
	NATIONALITY_REPORTING_COUNTRY("NationalityReportingCountry"),
	/** Neuropsychiatric disorder - VCJD */
	NEURO_PSYCH_DISORDER("NeuroPsychDisorder"),
	/** Occupation - MALA */
	OCCUPATION("Occupation"),
	/** Outcome of the case - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +30 more */
	OUTCOME("Outcome"),
	/** Outcome at 12 months - TUBE */
	OUTCOME12_MONTHS("Outcome12Months"),
	/** Outcome at 24 months - TUBE */
	OUTCOME24_MONTHS("Outcome24Months"),
	/** Outcome at 36 months - TUBE */
	OUTCOME36_MONTHS("Outcome36Months"),
	/** Outcome at 6 months - TUBE */
	OUTCOME6_MONTHS("Outcome6Months"),
	/** Outcome of pregnancy - LIST, ZIKA */
	OUTCOME_OF_PREGNANCY("OutcomeOfPregnancy"),
	/** PCR serogroup - LIST */
	PCR_SEROGROUP("PCRSerogroup"),
	/** Total number of PCV doses - PNEU */
	PCV_DOSES("PCVDoses"),
	/** Total number of PPV doses - PNEU */
	PPV_DOSES("PPVDoses"),
	/** PRNP gene analysed - VCJD */
	PRNP_GENE_ANALYSED("PRNPGeneAnalysed"),
	/** Pathogen - BRUC, CAMP, DIPH, ECHI, LEGI, MALA +4 more */
	PATHOGEN("Pathogen"),
	/** Pathogen detection method (laboratory) - CHIK, DENGUE, LEGI, LYMENEURO, MPOX, PERT +5 more */
	PATHOGEN_DETECTION_METHOD("PathogenDetectionMethod"),
	/** Pathogen detection result (laboratory result) - CHLAM, CONSYPH, GONO, LGV, SYPH */
	PATHOGEN_DETECTION_RESULT("PathogenDetectionResult"),
	/** Probable country of infection - BRUC, CAMP, CCHF, CHIK, DENGUE, DIPH +23 more */
	PLACE_OF_INFECTION("PlaceOfInfection"),
	/** Place of notification - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +32 more */
	PLACE_OF_NOTIFICATION("PlaceOfNotification"),
	/** Place of residence - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +32 more */
	PLACE_OF_RESIDENCE("PlaceOfResidence"),
	/** Plasma product recipient - VCJD */
	PLASMA_PRODUCT_RECIPIENT("PlasmaProductRecipient"),
	/** Pneumonia - LEGI */
	PNEUMONIA("Pneumonia"),
	/** Pre-exposure prophylaxis for HIV - MPOX */
	PR_EPHIV("PrEPHIV"),
	/** Pregnancy at the time of infection - LIST, RUBE, ZIKA */
	PREGNANCY("Pregnancy"),
	/** Previous diagnosis - TUBE */
	PREV_DIAGNOSIS("PrevDiagnosis"),
	/** Year of previous diagnosis - TUBE */
	PREV_DIAGNOSIS_YEAR("PrevDiagnosisYear"),
	/** Previous anti-TB drug treatment - TUBE */
	PREV_TREATMENT("PrevTreatment"),
	/** Completion of the previous treatment - TUBE */
	PREV_TREATMENT_COMPLETION("PrevTreatmentCompletion"),
	/** Previous MPOX infection - MPOX */
	PREVIOUS_MPOX("PreviousMPOX"),
	/** Previous MPOX clade - MPOX */
	PREVIOUS_MPO_XCLADE("PreviousMPOXclade"),
	/** Previous smallpox vaccination - MPOX */
	PREVIOUS_POX_VACCINATION("PreviousPoxVaccination"),
	/** Prophylaxis - MALA */
	PROPHYLAXIS("Prophylaxis"),
	/** Purpose of travel - MALA */
	PURPOSE_OF_TRAVEL("PurposeOfTravel"),
	/** Evidence of recent infection, aside from the recent infection assay result - HIVAIDS */
	RECENT_INFECTION("RecentInfection"),
	/** Region of origin of patient - HIVAIDS */
	REGION_OF_ORIGIN("RegionOfOrigin"),
	/** Reported EMERT II - MENI */
	REPORTED_EMERTII("ReportedEMERTII"),
	/** Reporting country - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +34 more */
	REPORTING_COUNTRY("ReportingCountry"),
	/** Residence country abroad - VCJD */
	RESIDENCE_COUNTRY_ABROAD("ResidenceCountryAbroad"),
	/** Residential history abroad - VCJD */
	RESIDENTIAL_HISTORY_ABROAD("ResidentialHistoryAbroad"),
	/** Result of brain PRP type - VCJD */
	RESULT_BRAIN_PRP_TYPE("ResultBrainPRPType"),
	/** Result brain pathology - VCJD */
	RESULT_BRAIN_PATHOLOGY("ResultBrainPathology"),
	/** Result CSF 14-3-3 - VCJD */
	RESULT_CSF("ResultCSF"),
	/** Result carbapenemase - SALM */
	RESULT_CARBAPENEMASE("ResultCarbapenemase"),
	/** Result of the test for diagnosis - TUBE */
	RESULT_CULTURE("ResultCulture"),
	/** Result EEG - VCJD */
	RESULT_EEG("ResultEEG"),
	/** FetA VR - MENI */
	RESULT_FET_VR("ResultFetVR"),
	/** Result IgG - MEAS, RUBE */
	RESULT_IG_G("ResultIgG"),
	/** Result IgM - MEAS, RUBE */
	RESULT_IG_M("ResultIgM"),
	/** Multilocus sequence typing clonal complex of strain - MENI */
	RESULT_MLST("ResultMLST"),
	/** Result MRI - VCJD */
	RESULT_MRI("ResultMRI"),
	/** Result MRI scan reviewed - VCJD */
	RESULT_MRI_SCAN_REVIEWED("ResultMRIScanReviewed"),
	/** Result of test for acid-fast bacilli (AFB) - TUBE */
	RESULT_MICROSCOPY_AFB("ResultMicroscopyAFB"),
	/** Additional lab test results - TUBE */
	RESULT_OTHER_TEST("ResultOtherTest"),
	/** Result PRNP 129 - VCJD */
	RESULT_PRNP129("ResultPRNP129"),
	/** Genotype gene PorA variable region 1 - MENI */
	RESULT_POR_A1("ResultPorA1"),
	/** Genotype gene PorA variable region 2 - MENI */
	RESULT_POR_A2("ResultPorA2"),
	/** Tonsil biopsy result - VCJD */
	RESULT_TONSIL_BIOPSY("ResultTonsilBiopsy"),
	/** Result of virus detection or isolation - MEAS, RUBE */
	RESULT_VIR_DETECT("ResultVirDetect"),
	/** Susceptibility interpretation to Amoxicillin/Clavulanic acid - CAMP */
	SIR_AMC("SIR_AMC"),
	/** Susceptibility to Amikacin - TUBE */
	SIR_AMK("SIR_AMK"),
	/** Susceptibility to Ampicillin - SALM, SHIG, STEC */
	SIR_AMP("SIR_AMP"),
	/** Susceptibility to Bedaquiline - TUBE */
	SIR_BDQ("SIR_BDQ"),
	/** Susceptibility to Ceftazidime - SALM, SHIG */
	SIR_CAZ("SIR_CAZ"),
	/** Susceptibility to Chloramphenicol - SALM, STEC */
	SIR_CHL("SIR_CHL"),
	/** Susceptibility interpretation to Ciprofloxacin - CAMP, DIPH, MENI, SALM, SHIG, STEC */
	SIR_CIP("SIR_CIP"),
	/** Susceptibility to Clofazimine - TUBE */
	SIR_CLF("SIR_CLF"),
	/** Susceptibility to Clindamycin - DIPH */
	SIR_CLI("SIR_CLI"),
	/** Susceptibility to Cefotaxime - SALM, SHIG, STEC */
	SIR_CTX("SIR_CTX"),
	/** Susceptibility to Cefotaxime or Ceftriaxone - MENI, PNEU */
	SIR_CTX_CFX("SIR_CTX_CFX"),
	/** Susceptibility to Delamanid - TUBE */
	SIR_DLM("SIR_DLM"),
	/** Susceptibility interpretation to Erythromicin - CAMP, DIPH, PNEU */
	SIR_ERY("SIR_ERY"),
	/** Susceptibility to Ethambutol - TUBE */
	SIR_ETH("SIR_ETH"),
	/** Susceptibility to Ethionamide - TUBE */
	SIR_ETI("SIR_ETI"),
	/** Susceptibility interpretation to Gentamicin - CAMP, SALM, STEC */
	SIR_GEN("SIR_GEN"),
	/** Susceptibility to Isoniazid - TUBE */
	SIR_INH("SIR_INH"),
	/** Susceptibility to Kanamycin - STEC */
	SIR_KAN("SIR_KAN"),
	/** Susceptibility to Linezolid - DIPH, TUBE */
	SIR_LNZ("SIR_LNZ"),
	/** Susceptibility to Levofloxacin - TUBE */
	SIR_LVX("SIR_LVX"),
	/** Susceptibility to Meropenem - DIPH, SALM */
	SIR_MEM("SIR_MEM"),
	/** Susceptibility to Moxifloxacin - TUBE */
	SIR_MFX("SIR_MFX"),
	/** Susceptibility to Nalidixic Acid - SALM, STEC */
	SIR_NAL("SIR_NAL"),
	/** Susceptibility to Penicillin - DIPH, MENI, PNEU */
	SIR_PEN("SIR_PEN"),
	/** Susceptibility to Pretomanid - TUBE */
	SIR_PTM("SIR_PTM"),
	/** Susceptibility to Pyrazinamide - TUBE */
	SIR_PZA("SIR_PZA"),
	/** Susceptibility to Rifampicin - DIPH, MENI, TUBE */
	SIR_RIF("SIR_RIF"),
	/** Susceptibility to Sulfamethoxazole - SALM */
	SIR_SMX("SIR_SMX"),
	/** Susceptibility to Sulphonamides - STEC */
	SIR_SSS("SIR_SSS"),
	/** Susceptibility to Streptomycin - STEC, TUBE */
	SIR_STR("SIR_STR"),
	/** Susceptibility to Trimethoprim-sulfamethoxazole - DIPH, SALM, SHIG, STEC */
	SIR_SXT("SIR_SXT"),
	/** Susceptibility interpretation to Tetracyclines - CAMP, DIPH, SALM, STEC */
	SIR_TCY("SIR_TCY"),
	/** Susceptibility to Trimethoprim - SALM */
	SIR_TMP("SIR_TMP"),
	/** Positive sampling site - LEGI */
	SAMPLING_SITE("SamplingSite"),
	/** Second pathogen detection method - DIPH, HAEINF, MENI */
	SECOND_PATHOGEN_DETECTION_METHOD("SecondPathogenDetectionMethod"),
	/** Second specimen - DIPH */
	SECOND_SPECIMEN("SecondSpecimen"),
	/** Sensory symptoms - VCJD */
	SENSORY_SYMPTOMS("SensorySymptoms"),
	/** Sequence type - CAMP, DIPH, LEGI, LIST, SALM */
	SEQUENCE_TYPE("SequenceType"),
	/** Serogroup - LEGI, MENI */
	SEROGROUP("Serogroup"),
	/** Serotype - DENGUE, HAEINF, LIST, PNEU, SALM, SHIG */
	SEROTYPE("Serotype"),
	/** Setting clinical - LEGI */
	SETTING_CLINICAL("SettingClinical"),
	/** Setting of infection (exposure) - MPOX */
	SETTING_OF_INFECTION("SettingOfInfection"),
	/** Setting of infection (exposure) other - MPOX */
	SETTING_OF_INFECTION_OTHER("SettingOfInfectionOther"),
	/** Sex worker - GONO, LGV, SYPH */
	SEX_WORKER("SexWorker"),
	/** Sexual behaviour - MPOX */
	SEXUAL_BEHAVIOUR("SexualBehaviour"),
	/** Shigatoxin 1 - STEC */
	SHIGATOXIN1("Shigatoxin1"),
	/** Shigatoxin 1 sub-type - STEC */
	SHIGATOXIN1_SUBTYPE("Shigatoxin1Subtype"),
	/** Shigatoxin 2 - STEC */
	SHIGATOXIN2("Shigatoxin2"),
	/** Shigatoxin 2 sub-type - STEC */
	SHIGATOXIN2_SUBTYPE("Shigatoxin2Subtype"),
	/** Shigatoxin production - STEC */
	SHIGATOXIN_PRODUCTION("ShigatoxinProduction"),
	/** Site of infection - CHLAM, GONO, LGV, SYPH */
	SITE_OF_INFECTION("SiteOfInfection"),
	/** Site of test - HIVAIDS */
	SITE_OF_TEST("SiteOfTest"),
	/** Sorbitol fermenting - STEC */
	SORBITOL_FERMENTING("SorbitolFermenting"),
	/** Specific antibody response - STEC */
	SPECIFIC_ANTIBODY_RESPONSE("SpecificAntibodyResponse"),
	/** Specimen - LIST, MPOX, SALM, SHIG, STEC, TBE +1 more */
	SPECIMEN("Specimen"),
	/** Specimen other - MPOX */
	SPECIMEN_OTHER("SpecimenOther"),
	/** Type of specimen(s) for serological analysis - MEAS, RUBE */
	SPECIMEN_SERO("SpecimenSero"),
	/** Type of specimen(s) collected - MEAS, RUBE */
	SPECIMEN_VIR_DETECT("SpecimenVirDetect"),
	/** Stage of hepatitis - HEPB, HEPC */
	STAGE_HEP("StageHEP"),
	/** Stage syphilis - SYPH */
	STAGE_SYPH("StageSYPH"),
	/** Stage syphilis detailed - SYPH */
	STAGE_SYP_HDETAILED("StageSYPHdetailed"),
	/** Status - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +34 more */
	STATUS("Status"),
	/** SubGenotype - HEPA */
	SUB_GENOTYPE("SubGenotype"),
	/** Subject code - BRUC, CAMP, CCHF, CHIK, CHLAM, CONSYPH +34 more */
	SUBJECT_CODE("SubjectCode"),
	/** Suspected vehicle - BRUC, CAMP, DIPH, ECHI, HEPA, LIST +4 more */
	SUSPECTED_VEHICLE("SuspectedVehicle"),
	/** Travel and transit countries - DIPH, MPOX */
	TRAVEL_PLACES("TravelPlaces"),
	/** Travel status - MPOX */
	TRAVEL_STATUS("TravelStatus"),
	/** Context for first dose smallpox/MPOX vaccination - MPOX */
	VACCINATION_PURPOSE_DOSE1("VaccinationPurposeDose1"),
	/** Context for second dose smallpox/MPOX vaccination - MPOX */
	VACCINATION_PURPOSE_DOSE2("VaccinationPurposeDose2"),
	/** Vaccination status on current MPOX event/outbreak - DIPH, HAEINF, MEAS, MENI, MPOX, MUMP +6 more */
	VACCINATION_STATUS("VaccinationStatus"),
	/** Vaccination status of mother - PERT */
	VACCINATION_STATUS_MATERNAL("VaccinationStatusMaternal"),
	/** Type of pneumococcal vaccine - PNEU */
	VACCINE("Vaccine"),
	/** Week of gestation - RUBE */
	WEEK_OF_GESTATION("WeekOfGestation"),
	/** Whole genome sequencing - DIPH */
	WGS("Wgs"),
	/** Wgs accession identifier - DIPH, MPOX */
	WGS_ACCESSION("WgsAccession"),
	/** Wgs sequence identifier - DIPH */
	WGS_SEQUENCE_ID("WgsSequenceId"),
	/** aaiC gene - STEC */
	AAI_C_GENE("aaiCGene"),
	/** aggR gene - STEC */
	AGG_R_GENE("aggRGene");

	private final String variableName;

	EpipulseVariable(String variableName) {
		this.variableName = variableName;
	}

	/**
	 * The variable's name exactly as EpiPulse spells it, which is the CSV column header.
	 */
	public String getVariableName() {
		return variableName;
	}

	@Override
	public String toString() {
		return variableName;
	}
}

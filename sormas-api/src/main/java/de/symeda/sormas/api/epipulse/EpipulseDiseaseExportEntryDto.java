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

package de.symeda.sormas.api.epipulse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseClassificationRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseCaseOutcomeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseDiseaseRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseGenderRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulsePathogenTestTypeRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseStatusRef;
import de.symeda.sormas.api.epipulse.referencevalue.EpipulseVaccinationStatusRef;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EpipulseDiseaseExportEntryDto {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String COLLECTION_SPLIT_CHARACTER = "#";
	private static final String RECORD_SPLIT_CHARACTER = "\\|";
	private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private String reportingCountry;
	private Boolean deleted;
	private EpipulseSubjectCode subjectCode;
	private String nationalRecordId;
	private String dataSource;
	private Date reportDate;
	private Integer yearOfBirth;
	private Integer monthOfBirth;
	private Integer dayOfBirth;
	private Date symptomOnsetDate;
	private Integer ageYears;
	private Integer ageMonths;
	private Integer ageDays;
	private Sex sex;
	private String addressCommunityNutsCode;
	private String addressDistrictNutsCode;
	private String addressRegionNutsCode;
	private String addressCountryNutsCode;
	private String responsibleCommunityNutsCode;
	private String responsibleDistrictNutsCode;
	private String responsibleRegionNutsCode;
	private String serverCountryNutsCode;
	private CaseClassification caseClassification;
	private YesNoUnknown admittedToHealthFacility;
	private HospitalizationReasonType hospitalizationReason;
	private Date admissionDate;
	private Date dischargeDate;
	private CaseOutcome caseOutcome;
	private List<EpipulseHospitalizationCheckDto> previousHospitalizations;
	private List<EpipulsePathogentTestCheckDto> pathogenTests;
	private List<EpipulseImmunizationCheckDto> immunizations;
	private List<EpipulseVaccinationCheckDto> vaccinations;

	// MEAS-specific laboratory fields (can be mapped from existing SORMAS data)
	private Date dateOfSpecimen;
	private Date dateOfLaboratoryResult;
	private List<String> typeOfSpecimenCollected; // SampleMaterial mapped to EpiPulse codes (repeatable)
	private String resultOfVirusDetection; // PathogenTestResultType mapped to POS/NEG/EQUI/NOTEST
	private String genotype; // PathogenTest typingId/genoType
	private List<String> typeOfSpecimenSerology; // SampleMaterial for serology tests (repeatable)
	private String resultIgG; // IgG test result
	private String resultIgM; // IgM test result

	// Phase 3: Clinical and epidemiology fields (mapped from existing SORMAS data)
	private Date dateOfInvestigation; // CaseDataDto.investigatedDate
	private Boolean clusterRelated; // EpiDataDto.clusterRelated
	private String clusterIdentification; // EpiDataDto.clusterTypeText
	private List<String> clusterSetting; // EpiDataDto.clusterType mapped to EpiPulse codes (repeatable)
	private String importedStatus; // EpiDataDto.caseImportedStatus mapped to EpiPulse codes
	private List<String> complicationDiagnosis; // SymptomsDto complications (repeatable)
	private Boolean clinicalCriteriaStatus; // Derived from CaseDataDto.clinicalConfirmation
	private List<String> placeOfInfection; // EpiDataDto.exposures locations (repeatable)
	private String causeOfDeath; // PersonDto.causeOfDeathDetails

	// IPI-specific laboratory fields
	private String resultOfCulture; // PathogenTestResultType for CULTURE → POS/NEG/EQUI/NOTEST
	private String resultOfPCR; // PathogenTestResultType for PCR_RT_PCR → POS/NEG/EQUI/NOTEST
	private String serotype; // PathogenTest typingId for pneumococcal serotypes (1-93)
	private String serogroupMethod; // PathogenTestType.SEROGROUPING presence → POS/NEG/NOTEST
	private String penicillinResistance; // DrugSusceptibilityDto → SENS/RESIST/INTER/NOTEST

	// IPI-specific clinical fields
	private List<String> clinicalPresentation; // SymptomsDto (meningitis, septicaemia, etc.) → MENING/SEPT/PNEUM/OME/ASYMP (repeatable)

	// PNEU-specific fields (following metadata specification exactly)
	// Demographics
	private Boolean nrlData; // National Reference Laboratory data flag

	// Clinical/Diagnostic
	private Date dateOfDiagnosis; // Date of diagnosis
	private String clinicalCriteria; // REF: BACTERPNEUMO, MENI, MENISEPTI, OTH, SEPTI

	// Laboratory
	private String pathogenDetectionMethod; // REF: COAGG, GDIFF, MPCR, OTH, PTEST, QUE, SLAGG

	// Vaccination - Summary fields
	private String vaccine; // REF: PCV7, PCV10, PCV13, PCV15, PCV20, PCV3, PPV23
	private Date dateOfLastVaccination; // Date of last vaccination (shared with MEAS)

	// Vaccination - Detailed PCV doses (1-4)
	private Boolean dosePCV1;
	private Date datePCV1;
	private String brandPCV1; // REF: PCV7, PCV10, PCV13, PCV15, PCV20
	private Boolean dosePCV2;
	private Date datePCV2;
	private String brandPCV2;
	private Boolean dosePCV3;
	private Date datePCV3;
	private String brandPCV3;
	private Boolean dosePCV4;
	private Date datePCV4;
	private String brandPCV4;
	private Integer pcvDoses; // Total PCV dose count

	// Vaccination - PPV doses
	private Boolean dosePPV;
	private Date datePPV;
	private Integer ppvDoses; // Total PPV dose count

	// Antimicrobial Susceptibility Testing (AST)
	private String astMethod; // REF: AGARDIL, AUTOM, BROTHDIL, GRAD, OTH

	// CTX/CFX (Cefotaxime/Ceftriaxone) AST
	private String micSign_CTX_CFX; // REF: <, <=, =, >, >=
	private Double micValueAST_CTX_CFX; // MIC value
	private String sir_CTX_CFX; // REF: I, R, S

	// ERY (Erythromycin) AST
	private String micSign_ERY;
	private Double micValueAST_ERY;
	private String sir_ERY;

	// PEN (Penicillin) AST
	private String micSign_PEN;
	private Double micValueAST_PEN;
	private String sir_PEN;

	// MENI-specific fields (Invasive Meningococcal Infection)
	// Serogroup (NEIMENI_A/B/C/W/X/Y/Z/29E/NGA/OTH)
	private String serogroup;
	// Isolate IDs (repeatable)
	private List<String> isolateIds;
	// Main pathogen detection methods (repeatable)
	private List<String> mainPathogenDetectionMethods;
	// Second pathogen detection methods (repeatable)
	private List<String> secondPathogenDetectionMethods;
	// MLST results (repeatable)
	private List<String> resultMlst;
	// PorA1 result
	private String resultPorA1;
	// PorA2 result
	private String resultPorA2;
	// FetA VR result
	private String resultFetVR;
	// ReportedEMERTII flag
	private Boolean reportedEmertii;
	// CIP (Ciprofloxacin) AST - MENI uses this instead of ERY
	private String micSign_CIP;
	private Double micValueAST_CIP;
	private String sir_CIP;
	// RIF (Rifampicin) AST - MENI-specific antibiotic
	private String micSign_RIF;
	private Double micValueAST_RIF;
	private String sir_RIF;

	public String getReportingCountry() {
		return reportingCountry;
	}

	public void setReportingCountry(String reportingCountry) {
		this.reportingCountry = reportingCountry;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public EpipulseSubjectCode getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(EpipulseSubjectCode subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getNationalRecordId() {
		return nationalRecordId;
	}

	public void setNationalRecordId(String nationalRecordId) {
		this.nationalRecordId = nationalRecordId;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Integer getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(Integer yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public Integer getMonthOfBirth() {
		return monthOfBirth;
	}

	public void setMonthOfBirth(Integer monthOfBirth) {
		this.monthOfBirth = monthOfBirth;
	}

	public Integer getDayOfBirth() {
		return dayOfBirth;
	}

	public void setDayOfBirth(Integer dayOfBirth) {
		this.dayOfBirth = dayOfBirth;
	}

	public Date getSymptomOnsetDate() {
		return symptomOnsetDate;
	}

	public void setSymptomOnsetDate(Date symptomOnsetDate) {
		this.symptomOnsetDate = symptomOnsetDate;
	}

	public Integer getAgeYears() {
		return ageYears;
	}

	public Integer getAgeMonths() {
		return ageMonths;
	}

	public Integer getAgeDays() {
		return ageDays;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getAddressCommunityNutsCode() {
		return addressCommunityNutsCode;
	}

	public void setAddressCommunityNutsCode(String addressCommunityNutsCode) {
		this.addressCommunityNutsCode = addressCommunityNutsCode;
	}

	public String getAddressDistrictNutsCode() {
		return addressDistrictNutsCode;
	}

	public void setAddressDistrictNutsCode(String addressDistrictNutsCode) {
		this.addressDistrictNutsCode = addressDistrictNutsCode;
	}

	public String getAddressRegionNutsCode() {
		return addressRegionNutsCode;
	}

	public void setAddressRegionNutsCode(String addressRegionNutsCode) {
		this.addressRegionNutsCode = addressRegionNutsCode;
	}

	public String getAddressCountryNutsCode() {
		return addressCountryNutsCode;
	}

	public void setAddressCountryNutsCode(String addressCountryNutsCode) {
		this.addressCountryNutsCode = addressCountryNutsCode;
	}

	public String getResponsibleCommunityNutsCode() {
		return responsibleCommunityNutsCode;
	}

	public void setResponsibleCommunityNutsCode(String responsibleCommunityNutsCode) {
		this.responsibleCommunityNutsCode = responsibleCommunityNutsCode;
	}

	public String getResponsibleDistrictNutsCode() {
		return responsibleDistrictNutsCode;
	}

	public void setResponsibleDistrictNutsCode(String responsibleDistrictNutsCode) {
		this.responsibleDistrictNutsCode = responsibleDistrictNutsCode;
	}

	public String getResponsibleRegionNutsCode() {
		return responsibleRegionNutsCode;
	}

	public void setResponsibleRegionNutsCode(String responsibleRegionNutsCode) {
		this.responsibleRegionNutsCode = responsibleRegionNutsCode;
	}

	public String getServerCountryNutsCode() {
		return serverCountryNutsCode;
	}

	public void setServerCountryNutsCode(String serverCountryNutsCode) {
		this.serverCountryNutsCode = serverCountryNutsCode;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public YesNoUnknown getAdmittedToHealthFacility() {
		return admittedToHealthFacility;
	}

	public void setAdmittedToHealthFacility(YesNoUnknown admittedToHealthFacility) {
		this.admittedToHealthFacility = admittedToHealthFacility;
	}

	public HospitalizationReasonType getHospitalizationReason() {
		return hospitalizationReason;
	}

	public void setHospitalizationReason(HospitalizationReasonType hospitalizationReason) {
		this.hospitalizationReason = hospitalizationReason;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public CaseOutcome getCaseOutcome() {
		return caseOutcome;
	}

	public void setCaseOutcome(CaseOutcome caseOutcome) {
		this.caseOutcome = caseOutcome;
	}

	public List<EpipulseHospitalizationCheckDto> getPreviousHospitalizations() {
		return previousHospitalizations;
	}

	public void setPreviousHospitalizations(List<EpipulseHospitalizationCheckDto> previousHospitalizations) {
		this.previousHospitalizations = previousHospitalizations;
	}

	public List<EpipulsePathogentTestCheckDto> getPathogenTests() {
		return pathogenTests;
	}

	public void setPathogenTests(List<EpipulsePathogentTestCheckDto> pathogenTests) {
		this.pathogenTests = pathogenTests;
	}

	public List<EpipulseImmunizationCheckDto> getImmunizations() {
		return immunizations;
	}

	public void setImmunizations(List<EpipulseImmunizationCheckDto> immunizations) {
		this.immunizations = immunizations;
	}

	public List<EpipulseVaccinationCheckDto> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<EpipulseVaccinationCheckDto> vaccinations) {
		this.vaccinations = vaccinations;
	}

	public Date getDateOfSpecimen() {
		return dateOfSpecimen;
	}

	public void setDateOfSpecimen(Date dateOfSpecimen) {
		this.dateOfSpecimen = dateOfSpecimen;
	}

	public Date getDateOfLaboratoryResult() {
		return dateOfLaboratoryResult;
	}

	public void setDateOfLaboratoryResult(Date dateOfLaboratoryResult) {
		this.dateOfLaboratoryResult = dateOfLaboratoryResult;
	}

	public List<String> getTypeOfSpecimenCollected() {
		return typeOfSpecimenCollected;
	}

	public void setTypeOfSpecimenCollected(List<String> typeOfSpecimenCollected) {
		this.typeOfSpecimenCollected = typeOfSpecimenCollected;
	}

	public String getResultOfVirusDetection() {
		return resultOfVirusDetection;
	}

	public void setResultOfVirusDetection(String resultOfVirusDetection) {
		this.resultOfVirusDetection = resultOfVirusDetection;
	}

	public String getGenotype() {
		return genotype;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}

	public List<String> getTypeOfSpecimenSerology() {
		return typeOfSpecimenSerology;
	}

	public void setTypeOfSpecimenSerology(List<String> typeOfSpecimenSerology) {
		this.typeOfSpecimenSerology = typeOfSpecimenSerology;
	}

	public String getResultIgG() {
		return resultIgG;
	}

	public void setResultIgG(String resultIgG) {
		this.resultIgG = resultIgG;
	}

	public String getResultIgM() {
		return resultIgM;
	}

	public void setResultIgM(String resultIgM) {
		this.resultIgM = resultIgM;
	}

	public String getDiseaseForCsv() {
		return EpipulseDiseaseRef.getBySubjectCode(subjectCode).name();
	}

	public String getReportingCountryForCsv() {
		return reportingCountry;
	}

	public String getStatusForCsv() {
		if (deleted == Boolean.FALSE) {
			return EpipulseStatusRef.NEW_UPDATE.getCode();
		} else {
			return EpipulseStatusRef.DELETE.getCode();
		}
	}

	public String getSubjectCodeForCsv() {
		return subjectCode.name();
	}

	public String getNationalRecordIdForCsv() {
		return nationalRecordId;
	}

	public String getDataSourceForCsv() {
		return dataSource;
	}

	public String getDateUsedForStatisticsCsv() {
		return formatDateForCsv(reportDate);
	}

	public String getAgeForCsv() {
		return ageYears == null ? null : ageYears.toString();
	}

	public String getAgeMonthForCsv() {
		switch (subjectCode) {
		case PERT:
		case MEAS:
		case PNEU:
		case MENI:
			if (ageYears != null && ageYears < 2) {
				return ageMonths == null ? null : ageMonths.toString();
			}
			break;
		}
		return null;
	}

	public String getGenderForCsv() {
		EpipulseGenderRef genderRef = EpipulseGenderRef.getByGender(sex);
		if (genderRef != null) {
			return genderRef.name();
		}
		return null;
	}

	public String getPlaceOfResidenceForCsv() {
		switch (subjectCode) {
		case PERT:
		case MEAS:
		case PNEU:
		case MENI:
			if (addressCommunityNutsCode != null && !addressCommunityNutsCode.isEmpty()) {
				return addressCommunityNutsCode;
			} else if (addressDistrictNutsCode != null && !addressDistrictNutsCode.isEmpty()) {
				return addressDistrictNutsCode;
			} else if (addressRegionNutsCode != null && !addressRegionNutsCode.isEmpty()) {
				return addressRegionNutsCode;
			} else {
				return serverCountryNutsCode;
			}
		}
		return null;
	}

	public String getPlaceOfNotificationForCsv() {
		switch (subjectCode) {
		case PERT:
		case MEAS:
		case PNEU:
		case MENI:
			if (responsibleCommunityNutsCode != null && !responsibleCommunityNutsCode.isEmpty()) {
				return responsibleCommunityNutsCode;
			} else if (responsibleDistrictNutsCode != null && !responsibleDistrictNutsCode.isEmpty()) {
				return responsibleDistrictNutsCode;
			} else if (responsibleRegionNutsCode != null && !responsibleRegionNutsCode.isEmpty()) {
				return responsibleRegionNutsCode;
			} else {
				return serverCountryNutsCode;
			}
		}
		return null;
	}

	public String getCaseClassificationForCsv() {
		EpipulseCaseClassificationRef classificationRef = EpipulseCaseClassificationRef.getByCaseClassification(caseClassification);
		if (classificationRef != null) {
			return classificationRef.name();
		}
		return null;
	}

	public String getDateOfOnsetForCsv() {
		return formatDateForCsv(symptomOnsetDate);
	}

	public String getDateOfNotificationForCsv() {
		return formatDateForCsv(reportDate);
	}

	public String getHospitalizationForCsv() {
		return hasHistoryOfHospitalization();
	}

	public String getOutcomeForCsv() {
		EpipulseCaseOutcomeRef outcomeRef = EpipulseCaseOutcomeRef.getByCaseOutcome(caseOutcome);
		if (outcomeRef != null) {
			return outcomeRef.name();
		}
		return null;
	}

	public List<String> getPathogenDetectionMethodsForCsv(int maxPathogenTests) {
		List<String> pathogenDetectionMethods = new ArrayList<>();

		for (int i = 0; i < maxPathogenTests; i++) {
			if (i < pathogenTests.size()) {
				pathogenDetectionMethods.add(EpipulsePathogenTestTypeRef.getByPathogenTestType(pathogenTests.get(i).getTestType()).name());
			} else {
				pathogenDetectionMethods.add("");
			}
		}

		return pathogenDetectionMethods;
	}

	public String getDateOfLastVaccinationForCsv() {

		if (!vaccinations.isEmpty()) {
			for (EpipulseVaccinationCheckDto vaccinationCheckDto : vaccinations) {
				if (vaccinationCheckDto.getVaccinationDate() != null && symptomOnsetDate != null) {
					if (DateHelper.isDateAfter(symptomOnsetDate, vaccinationCheckDto.getVaccinationDate())) {
						return formatDateForCsv(vaccinationCheckDto.getVaccinationDate());
					}
				}
			}
		} else {
			for (EpipulseImmunizationCheckDto immunizationCheckDto : immunizations) {
				if (immunizationCheckDto.getEndDate() != null && symptomOnsetDate != null) {
					if (DateHelper.isDateAfter(symptomOnsetDate, immunizationCheckDto.getEndDate())) {
						return formatDateForCsv(immunizationCheckDto.getEndDate());
					}
				}
			}
		}

		return null;
	}

	public String getVaccinationStatusForCsv() {

		if (immunizations.isEmpty()) {
			// Fallback: check if we have PCV/PPV dose counts from actual vaccination records (PNEU)
			int fallbackDoses = getFallbackDoseCount();
			if (fallbackDoses > 0) {
				return mapDoseCountToStatus(fallbackDoses);
			}
			return EpipulseVaccinationStatusRef.NOTVACC.getCode();
		}

		int totalDoses = 0;
		boolean hasUnknownDoses = false;
		for (EpipulseImmunizationCheckDto immunizationCheckDto : immunizations) {
			if (immunizationCheckDto.getNumberOfDoses() == null) {
				hasUnknownDoses = true;
			} else {
				totalDoses += immunizationCheckDto.getNumberOfDoses();
			}
		}

		// If numberOfDoses is unknown but we have actual vaccination counts, use those
		if (hasUnknownDoses && totalDoses == 0) {
			int fallbackDoses = getFallbackDoseCount();
			if (fallbackDoses > 0) {
				return mapDoseCountToStatus(fallbackDoses);
			}
			return EpipulseVaccinationStatusRef.UNKDOSE.getCode();
		}

		return mapDoseCountToStatus(totalDoses);
	}

	private int getFallbackDoseCount() {
		int fallbackDoses = 0;
		if (pcvDoses != null) {
			fallbackDoses += pcvDoses;
		}
		if (ppvDoses != null) {
			fallbackDoses += ppvDoses;
		}
		return fallbackDoses;
	}

	private String mapDoseCountToStatus(int totalDoses) {
		if (totalDoses > 10) {
			return EpipulseVaccinationStatusRef.TEN_DOSE.getCode();
		}

		switch (totalDoses) {
		case 0:
			return EpipulseVaccinationStatusRef.NOTVACC.getCode();
		case 1:
			return EpipulseVaccinationStatusRef.ONE_DOSE.getCode();
		case 2:
			return EpipulseVaccinationStatusRef.TWO_DOSE.getCode();
		case 3:
			return EpipulseVaccinationStatusRef.THREE_DOSE.getCode();
		case 4:
			return EpipulseVaccinationStatusRef.FOUR_DOSE.getCode();
		case 5:
			return EpipulseVaccinationStatusRef.FIVE_DOSE.getCode();
		case 6:
			return EpipulseVaccinationStatusRef.SIX_DOSE.getCode();
		case 7:
			return EpipulseVaccinationStatusRef.SEVEN_DOSE.getCode();
		case 8:
			return EpipulseVaccinationStatusRef.EIGHT_DOSE.getCode();
		case 9:
			return EpipulseVaccinationStatusRef.NINE_DOSE.getCode();
		case 10:
			return EpipulseVaccinationStatusRef.TEN_DOSE.getCode();
		default:
			return null;
		}
	}

	public String getVaccinationStatusMaternalForCsv() {
		return null;
	}

	public String getGestationalAgeAtVaccinationForCsv() {
		return null;
	}

	// MEAS-specific laboratory field CSV getters
	public String getDateOfSpecimenForCsv() {
		return formatDateForCsv(dateOfSpecimen);
	}

	public String getDateOfLaboratoryResultForCsv() {
		return formatDateForCsv(dateOfLaboratoryResult);
	}

	public List<String> getTypeOfSpecimenCollectedForCsv(int maxSpecimenVirDetect) {
		// Repeatable field - return list padded to max length
		List<String> specimens = new ArrayList<>();
		for (int i = 0; i < maxSpecimenVirDetect; i++) {
			if (typeOfSpecimenCollected != null && i < typeOfSpecimenCollected.size()) {
				specimens.add(typeOfSpecimenCollected.get(i));
			} else {
				specimens.add("");
			}
		}
		return specimens;
	}

	public String getResultOfVirusDetectionForCsv() {
		// Already mapped to EpiPulse codes (POS/NEG/EQUI/NOTEST)
		return resultOfVirusDetection;
	}

	public String getGenotypeForCsv() {
		// Already mapped to EpiPulse genotype codes (MEASV_A, MEASV_B1, etc.)
		return genotype;
	}

	public List<String> getTypeOfSpecimenSerologyForCsv(int maxSpecimenSero) {
		// Repeatable field - return list padded to max length
		List<String> specimens = new ArrayList<>();
		for (int i = 0; i < maxSpecimenSero; i++) {
			if (typeOfSpecimenSerology != null && i < typeOfSpecimenSerology.size()) {
				specimens.add(typeOfSpecimenSerology.get(i));
			} else {
				specimens.add("");
			}
		}
		return specimens;
	}

	public String getResultIgGForCsv() {
		// Already mapped to EpiPulse codes (POS/NEG/EQUI/NOTEST)
		return resultIgG;
	}

	public String getResultIgMForCsv() {
		// Already mapped to EpiPulse codes (POS/NEG/EQUI/NOTEST)
		return resultIgM;
	}

	// Phase 3: Getters and setters for clinical and epidemiology fields
	public Date getDateOfInvestigation() {
		return dateOfInvestigation;
	}

	public void setDateOfInvestigation(Date dateOfInvestigation) {
		this.dateOfInvestigation = dateOfInvestigation;
	}

	public Boolean getClusterRelated() {
		return clusterRelated;
	}

	public void setClusterRelated(Boolean clusterRelated) {
		this.clusterRelated = clusterRelated;
	}

	public String getClusterIdentification() {
		return clusterIdentification;
	}

	public void setClusterIdentification(String clusterIdentification) {
		this.clusterIdentification = clusterIdentification;
	}

	public List<String> getClusterSetting() {
		return clusterSetting;
	}

	public void setClusterSetting(List<String> clusterSetting) {
		this.clusterSetting = clusterSetting;
	}

	public String getImportedStatus() {
		return importedStatus;
	}

	public void setImportedStatus(String importedStatus) {
		this.importedStatus = importedStatus;
	}

	public List<String> getComplicationDiagnosis() {
		return complicationDiagnosis;
	}

	public void setComplicationDiagnosis(List<String> complicationDiagnosis) {
		this.complicationDiagnosis = complicationDiagnosis;
	}

	public Boolean getClinicalCriteriaStatus() {
		return clinicalCriteriaStatus;
	}

	public void setClinicalCriteriaStatus(Boolean clinicalCriteriaStatus) {
		this.clinicalCriteriaStatus = clinicalCriteriaStatus;
	}

	public List<String> getPlaceOfInfection() {
		return placeOfInfection;
	}

	public void setPlaceOfInfection(List<String> placeOfInfection) {
		this.placeOfInfection = placeOfInfection;
	}

	public String getCauseOfDeath() {
		return causeOfDeath;
	}

	public void setCauseOfDeath(String causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}

	// IPI-specific laboratory field getters/setters
	public String getResultOfCulture() {
		return resultOfCulture;
	}

	public void setResultOfCulture(String resultOfCulture) {
		this.resultOfCulture = resultOfCulture;
	}

	public String getResultOfPCR() {
		return resultOfPCR;
	}

	public void setResultOfPCR(String resultOfPCR) {
		this.resultOfPCR = resultOfPCR;
	}

	public String getSerotype() {
		return serotype;
	}

	public void setSerotype(String serotype) {
		this.serotype = serotype;
	}

	public String getSerogroupMethod() {
		return serogroupMethod;
	}

	public void setSerogroupMethod(String serogroupMethod) {
		this.serogroupMethod = serogroupMethod;
	}

	public String getPenicillinResistance() {
		return penicillinResistance;
	}

	public void setPenicillinResistance(String penicillinResistance) {
		this.penicillinResistance = penicillinResistance;
	}

	// IPI-specific clinical field getters/setters
	public List<String> getClinicalPresentation() {
		return clinicalPresentation;
	}

	public void setClinicalPresentation(List<String> clinicalPresentation) {
		this.clinicalPresentation = clinicalPresentation;
	}

	// PNEU-specific field getters/setters
	public Boolean getNrlData() {
		return nrlData;
	}

	public void setNrlData(Boolean nrlData) {
		this.nrlData = nrlData;
	}

	public Date getDateOfDiagnosis() {
		return dateOfDiagnosis;
	}

	public void setDateOfDiagnosis(Date dateOfDiagnosis) {
		this.dateOfDiagnosis = dateOfDiagnosis;
	}

	public String getClinicalCriteria() {
		return clinicalCriteria;
	}

	public void setClinicalCriteria(String clinicalCriteria) {
		this.clinicalCriteria = clinicalCriteria;
	}

	public String getPathogenDetectionMethod() {
		return pathogenDetectionMethod;
	}

	public void setPathogenDetectionMethod(String pathogenDetectionMethod) {
		this.pathogenDetectionMethod = pathogenDetectionMethod;
	}

	public String getVaccine() {
		return vaccine;
	}

	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}

	public Date getDateOfLastVaccination() {
		return dateOfLastVaccination;
	}

	public void setDateOfLastVaccination(Date dateOfLastVaccination) {
		this.dateOfLastVaccination = dateOfLastVaccination;
	}

	public Boolean getDosePCV1() {
		return dosePCV1;
	}

	public void setDosePCV1(Boolean dosePCV1) {
		this.dosePCV1 = dosePCV1;
	}

	public Date getDatePCV1() {
		return datePCV1;
	}

	public void setDatePCV1(Date datePCV1) {
		this.datePCV1 = datePCV1;
	}

	public String getBrandPCV1() {
		return brandPCV1;
	}

	public void setBrandPCV1(String brandPCV1) {
		this.brandPCV1 = brandPCV1;
	}

	public Boolean getDosePCV2() {
		return dosePCV2;
	}

	public void setDosePCV2(Boolean dosePCV2) {
		this.dosePCV2 = dosePCV2;
	}

	public Date getDatePCV2() {
		return datePCV2;
	}

	public void setDatePCV2(Date datePCV2) {
		this.datePCV2 = datePCV2;
	}

	public String getBrandPCV2() {
		return brandPCV2;
	}

	public void setBrandPCV2(String brandPCV2) {
		this.brandPCV2 = brandPCV2;
	}

	public Boolean getDosePCV3() {
		return dosePCV3;
	}

	public void setDosePCV3(Boolean dosePCV3) {
		this.dosePCV3 = dosePCV3;
	}

	public Date getDatePCV3() {
		return datePCV3;
	}

	public void setDatePCV3(Date datePCV3) {
		this.datePCV3 = datePCV3;
	}

	public String getBrandPCV3() {
		return brandPCV3;
	}

	public void setBrandPCV3(String brandPCV3) {
		this.brandPCV3 = brandPCV3;
	}

	public Boolean getDosePCV4() {
		return dosePCV4;
	}

	public void setDosePCV4(Boolean dosePCV4) {
		this.dosePCV4 = dosePCV4;
	}

	public Date getDatePCV4() {
		return datePCV4;
	}

	public void setDatePCV4(Date datePCV4) {
		this.datePCV4 = datePCV4;
	}

	public String getBrandPCV4() {
		return brandPCV4;
	}

	public void setBrandPCV4(String brandPCV4) {
		this.brandPCV4 = brandPCV4;
	}

	public Integer getPcvDoses() {
		return pcvDoses;
	}

	public void setPcvDoses(Integer pcvDoses) {
		this.pcvDoses = pcvDoses;
	}

	public Boolean getDosePPV() {
		return dosePPV;
	}

	public void setDosePPV(Boolean dosePPV) {
		this.dosePPV = dosePPV;
	}

	public Date getDatePPV() {
		return datePPV;
	}

	public void setDatePPV(Date datePPV) {
		this.datePPV = datePPV;
	}

	public Integer getPpvDoses() {
		return ppvDoses;
	}

	public void setPpvDoses(Integer ppvDoses) {
		this.ppvDoses = ppvDoses;
	}

	public String getAstMethod() {
		return astMethod;
	}

	public void setAstMethod(String astMethod) {
		this.astMethod = astMethod;
	}

	public String getMicSign_CTX_CFX() {
		return micSign_CTX_CFX;
	}

	public void setMicSign_CTX_CFX(String micSign_CTX_CFX) {
		this.micSign_CTX_CFX = micSign_CTX_CFX;
	}

	public Double getMicValueAST_CTX_CFX() {
		return micValueAST_CTX_CFX;
	}

	public void setMicValueAST_CTX_CFX(Double micValueAST_CTX_CFX) {
		this.micValueAST_CTX_CFX = micValueAST_CTX_CFX;
	}

	public String getSir_CTX_CFX() {
		return sir_CTX_CFX;
	}

	public void setSir_CTX_CFX(String sir_CTX_CFX) {
		this.sir_CTX_CFX = sir_CTX_CFX;
	}

	public String getMicSign_ERY() {
		return micSign_ERY;
	}

	public void setMicSign_ERY(String micSign_ERY) {
		this.micSign_ERY = micSign_ERY;
	}

	public Double getMicValueAST_ERY() {
		return micValueAST_ERY;
	}

	public void setMicValueAST_ERY(Double micValueAST_ERY) {
		this.micValueAST_ERY = micValueAST_ERY;
	}

	public String getSir_ERY() {
		return sir_ERY;
	}

	public void setSir_ERY(String sir_ERY) {
		this.sir_ERY = sir_ERY;
	}

	public String getMicSign_PEN() {
		return micSign_PEN;
	}

	public void setMicSign_PEN(String micSign_PEN) {
		this.micSign_PEN = micSign_PEN;
	}

	public Double getMicValueAST_PEN() {
		return micValueAST_PEN;
	}

	public void setMicValueAST_PEN(Double micValueAST_PEN) {
		this.micValueAST_PEN = micValueAST_PEN;
	}

	public String getSir_PEN() {
		return sir_PEN;
	}

	public void setSir_PEN(String sir_PEN) {
		this.sir_PEN = sir_PEN;
	}

	// Phase 3: CSV getter methods
	public String getDateOfInvestigationForCsv() {
		return formatDateForCsv(dateOfInvestigation);
	}

	public String getClusterRelatedForCsv() {
		return clusterRelated != null ? String.valueOf(clusterRelated) : "";
	}

	public String getClusterIdentificationForCsv() {
		return clusterIdentification != null ? clusterIdentification : "";
	}

	public List<String> getClusterSettingForCsv(int maxClusterSettings) {
		// Repeatable field - return list padded to max length
		List<String> settings = new ArrayList<>();
		for (int i = 0; i < maxClusterSettings; i++) {
			if (clusterSetting != null && i < clusterSetting.size()) {
				settings.add(clusterSetting.get(i));
			} else {
				settings.add("");
			}
		}
		return settings;
	}

	public String getImportedStatusForCsv() {
		return importedStatus != null ? importedStatus : "";
	}

	public List<String> getComplicationDiagnosisForCsv(int maxComplicationDiagnosis) {
		// Repeatable field - return list padded to max length
		List<String> complications = new ArrayList<>();
		for (int i = 0; i < maxComplicationDiagnosis; i++) {
			if (complicationDiagnosis != null && i < complicationDiagnosis.size()) {
				complications.add(complicationDiagnosis.get(i));
			} else {
				// Use "NONE" for first empty slot if no complications, otherwise empty
				if (i == 0 && (complicationDiagnosis == null || complicationDiagnosis.isEmpty())) {
					complications.add("NONE");
				} else {
					complications.add("");
				}
			}
		}
		return complications;
	}

	public String getClinicalCriteriaStatusForCsv() {
		return clinicalCriteriaStatus != null ? String.valueOf(clinicalCriteriaStatus) : "";
	}

	public List<String> getPlaceOfInfectionForCsv(int maxPlaceOfInfection) {
		// Repeatable field - return list padded to max length
		List<String> places = new ArrayList<>();
		for (int i = 0; i < maxPlaceOfInfection; i++) {
			if (placeOfInfection != null && i < placeOfInfection.size()) {
				places.add(placeOfInfection.get(i));
			} else {
				places.add("");
			}
		}
		return places;
	}

	public String getCauseOfDeathForCsv() {
		return causeOfDeath != null ? causeOfDeath : "";
	}

	// IPI-specific CSV getter methods
	public String getResultOfCultureForCsv() {
		return resultOfCulture != null ? resultOfCulture : "";
	}

	public String getResultOfPCRForCsv() {
		return resultOfPCR != null ? resultOfPCR : "";
	}

	public String getSerotypeForCsv() {
		return serotype != null ? serotype : "";
	}

	public String getSerogroupMethodForCsv() {
		return serogroupMethod != null ? serogroupMethod : "";
	}

	public String getPenicillinResistanceForCsv() {
		return penicillinResistance != null ? penicillinResistance : "";
	}

	public List<String> getClinicalPresentationForCsv(int maxCount) {
		List<String> presentations = new ArrayList<>();
		if (clinicalPresentation != null && !clinicalPresentation.isEmpty()) {
			for (int i = 0; i < Math.min(maxCount, clinicalPresentation.size()); i++) {
				presentations.add(clinicalPresentation.get(i));
			}
		}
		// Pad with empty strings to match maxCount
		while (presentations.size() < maxCount) {
			presentations.add("");
		}
		return presentations;
	}

	// PNEU-specific CSV getter methods
	public String getNrlDataForCsv() {
		return nrlData != null ? String.valueOf(nrlData) : "";
	}

	public String getDateOfDiagnosisForCsv() {
		return formatDateForCsv(dateOfDiagnosis);
	}

	public String getClinicalCriteriaForCsv() {
		return clinicalCriteria != null ? clinicalCriteria : "";
	}

	public String getPathogenDetectionMethodForCsv() {
		return pathogenDetectionMethod != null ? pathogenDetectionMethod : "";
	}

	public String getVaccineForCsv() {
		return vaccine != null ? vaccine : "";
	}

	public String getDosePCV1ForCsv() {
		return dosePCV1 != null ? String.valueOf(dosePCV1) : "";
	}

	public String getDatePCV1ForCsv() {
		return formatDateForCsv(datePCV1);
	}

	public String getBrandPCV1ForCsv() {
		return brandPCV1 != null ? brandPCV1 : "";
	}

	public String getDosePCV2ForCsv() {
		return dosePCV2 != null ? String.valueOf(dosePCV2) : "";
	}

	public String getDatePCV2ForCsv() {
		return formatDateForCsv(datePCV2);
	}

	public String getBrandPCV2ForCsv() {
		return brandPCV2 != null ? brandPCV2 : "";
	}

	public String getDosePCV3ForCsv() {
		return dosePCV3 != null ? String.valueOf(dosePCV3) : "";
	}

	public String getDatePCV3ForCsv() {
		return formatDateForCsv(datePCV3);
	}

	public String getBrandPCV3ForCsv() {
		return brandPCV3 != null ? brandPCV3 : "";
	}

	public String getDosePCV4ForCsv() {
		return dosePCV4 != null ? String.valueOf(dosePCV4) : "";
	}

	public String getDatePCV4ForCsv() {
		return formatDateForCsv(datePCV4);
	}

	public String getBrandPCV4ForCsv() {
		return brandPCV4 != null ? brandPCV4 : "";
	}

	public String getPcvDosesForCsv() {
		return pcvDoses != null ? String.valueOf(pcvDoses) : "";
	}

	public String getDosePPVForCsv() {
		return dosePPV != null ? String.valueOf(dosePPV) : "";
	}

	public String getDatePPVForCsv() {
		return formatDateForCsv(datePPV);
	}

	public String getPpvDosesForCsv() {
		return ppvDoses != null ? String.valueOf(ppvDoses) : "";
	}

	public String getAstMethodForCsv() {
		return astMethod != null ? astMethod : "";
	}

	public String getMicSign_CTX_CFXForCsv() {
		return micSign_CTX_CFX != null ? micSign_CTX_CFX : "";
	}

	public String getMicValueAST_CTX_CFXForCsv() {
		return micValueAST_CTX_CFX != null ? String.valueOf(micValueAST_CTX_CFX) : "";
	}

	public String getSir_CTX_CFXForCsv() {
		return sir_CTX_CFX != null ? sir_CTX_CFX : "";
	}

	public String getMicSign_ERYForCsv() {
		return micSign_ERY != null ? micSign_ERY : "";
	}

	public String getMicValueAST_ERYForCsv() {
		return micValueAST_ERY != null ? String.valueOf(micValueAST_ERY) : "";
	}

	public String getSir_ERYForCsv() {
		return sir_ERY != null ? sir_ERY : "";
	}

	public String getMicSign_PENForCsv() {
		return micSign_PEN != null ? micSign_PEN : "";
	}

	public String getMicValueAST_PENForCsv() {
		return micValueAST_PEN != null ? String.valueOf(micValueAST_PEN) : "";
	}

	public String getSir_PENForCsv() {
		return sir_PEN != null ? sir_PEN : "";
	}

	// MENI-specific getters and setters
	public String getSerogroup() {
		return serogroup;
	}

	public void setSerogroup(String serogroup) {
		this.serogroup = serogroup;
	}

	public List<String> getIsolateIds() {
		return isolateIds;
	}

	public void setIsolateIds(List<String> isolateIds) {
		this.isolateIds = isolateIds;
	}

	public List<String> getMainPathogenDetectionMethods() {
		return mainPathogenDetectionMethods;
	}

	public void setMainPathogenDetectionMethods(List<String> mainPathogenDetectionMethods) {
		this.mainPathogenDetectionMethods = mainPathogenDetectionMethods;
	}

	public List<String> getSecondPathogenDetectionMethods() {
		return secondPathogenDetectionMethods;
	}

	public void setSecondPathogenDetectionMethods(List<String> secondPathogenDetectionMethods) {
		this.secondPathogenDetectionMethods = secondPathogenDetectionMethods;
	}

	public List<String> getResultMlst() {
		return resultMlst;
	}

	public void setResultMlst(List<String> resultMlst) {
		this.resultMlst = resultMlst;
	}

	public String getResultPorA1() {
		return resultPorA1;
	}

	public void setResultPorA1(String resultPorA1) {
		this.resultPorA1 = resultPorA1;
	}

	public String getResultPorA2() {
		return resultPorA2;
	}

	public void setResultPorA2(String resultPorA2) {
		this.resultPorA2 = resultPorA2;
	}

	public String getResultFetVR() {
		return resultFetVR;
	}

	public void setResultFetVR(String resultFetVR) {
		this.resultFetVR = resultFetVR;
	}

	public Boolean getReportedEmertii() {
		return reportedEmertii;
	}

	public void setReportedEmertii(Boolean reportedEmertii) {
		this.reportedEmertii = reportedEmertii;
	}

	public String getMicSign_CIP() {
		return micSign_CIP;
	}

	public void setMicSign_CIP(String micSign_CIP) {
		this.micSign_CIP = micSign_CIP;
	}

	public Double getMicValueAST_CIP() {
		return micValueAST_CIP;
	}

	public void setMicValueAST_CIP(Double micValueAST_CIP) {
		this.micValueAST_CIP = micValueAST_CIP;
	}

	public String getSir_CIP() {
		return sir_CIP;
	}

	public void setSir_CIP(String sir_CIP) {
		this.sir_CIP = sir_CIP;
	}

	public String getMicSign_RIF() {
		return micSign_RIF;
	}

	public void setMicSign_RIF(String micSign_RIF) {
		this.micSign_RIF = micSign_RIF;
	}

	public Double getMicValueAST_RIF() {
		return micValueAST_RIF;
	}

	public void setMicValueAST_RIF(Double micValueAST_RIF) {
		this.micValueAST_RIF = micValueAST_RIF;
	}

	public String getSir_RIF() {
		return sir_RIF;
	}

	public void setSir_RIF(String sir_RIF) {
		this.sir_RIF = sir_RIF;
	}

	// MENI-specific CSV getter methods
	public String getSerogroupForCsv() {
		return serogroup != null ? serogroup : "";
	}

	public List<String> getIsolateIdsForCsv(int maxCount) {
		List<String> ids = new ArrayList<>();
		for (int i = 0; i < maxCount; i++) {
			if (isolateIds != null && i < isolateIds.size()) {
				ids.add(isolateIds.get(i));
			} else {
				ids.add("");
			}
		}
		return ids;
	}

	public List<String> getMainPathogenDetectionMethodsForCsv(int maxCount) {
		List<String> methods = new ArrayList<>();
		for (int i = 0; i < maxCount; i++) {
			if (mainPathogenDetectionMethods != null && i < mainPathogenDetectionMethods.size()) {
				methods.add(mainPathogenDetectionMethods.get(i));
			} else {
				methods.add("");
			}
		}
		return methods;
	}

	public List<String> getSecondPathogenDetectionMethodsForCsv(int maxCount) {
		List<String> methods = new ArrayList<>();
		for (int i = 0; i < maxCount; i++) {
			if (secondPathogenDetectionMethods != null && i < secondPathogenDetectionMethods.size()) {
				methods.add(secondPathogenDetectionMethods.get(i));
			} else {
				methods.add("");
			}
		}
		return methods;
	}

	public List<String> getResultMlstForCsv(int maxCount) {
		List<String> results = new ArrayList<>();
		for (int i = 0; i < maxCount; i++) {
			if (resultMlst != null && i < resultMlst.size()) {
				results.add(resultMlst.get(i));
			} else {
				results.add("");
			}
		}
		return results;
	}

	public String getResultPorA1ForCsv() {
		return resultPorA1 != null ? resultPorA1 : "";
	}

	public String getResultPorA2ForCsv() {
		return resultPorA2 != null ? resultPorA2 : "";
	}

	public String getResultFetVRForCsv() {
		return resultFetVR != null ? resultFetVR : "";
	}

	public String getReportedEmertiiForCsv() {
		return reportedEmertii != null ? String.valueOf(reportedEmertii) : "";
	}

	public String getMicSign_CIPForCsv() {
		return micSign_CIP != null ? micSign_CIP : "";
	}

	public String getMicValueAST_CIPForCsv() {
		return micValueAST_CIP != null ? String.valueOf(micValueAST_CIP) : "";
	}

	public String getSir_CIPForCsv() {
		return sir_CIP != null ? sir_CIP : "";
	}

	public String getMicSign_RIFForCsv() {
		return micSign_RIF != null ? micSign_RIF : "";
	}

	public String getMicValueAST_RIFForCsv() {
		return micValueAST_RIF != null ? String.valueOf(micValueAST_RIF) : "";
	}

	public String getSir_RIFForCsv() {
		return sir_RIF != null ? sir_RIF : "";
	}

	/**
	 * Gets vaccination status for MENI (max 4 doses instead of 10).
	 * MENI uses 1DOSE through 4DOSE instead of the standard 10DOSE max.
	 */
	public String getVaccinationStatusMeniForCsv() {
		if (immunizations == null || immunizations.isEmpty()) {
			return EpipulseVaccinationStatusRef.NOTVACC.getCode();
		}

		int totalDoses = 0;
		for (EpipulseImmunizationCheckDto immunizationCheckDto : immunizations) {
			if (immunizationCheckDto.getNumberOfDoses() == null) {
				return EpipulseVaccinationStatusRef.UNKDOSE.getCode();
			} else {
				totalDoses += immunizationCheckDto.getNumberOfDoses();
			}
		}

		if (totalDoses >= 4) {
			return EpipulseVaccinationStatusRef.FOUR_DOSE.getCode();
		}

		switch (totalDoses) {
		case 1:
			return EpipulseVaccinationStatusRef.ONE_DOSE.getCode();
		case 2:
			return EpipulseVaccinationStatusRef.TWO_DOSE.getCode();
		case 3:
			return EpipulseVaccinationStatusRef.THREE_DOSE.getCode();
		}

		return EpipulseVaccinationStatusRef.NOTVACC.getCode();
	}

	public void calculateAge() {
		if (symptomOnsetDate == null || yearOfBirth == null || monthOfBirth == null || dayOfBirth == null) {
			return;
		}

		try {
			Date birthDate = DateHelper.getDateZero(yearOfBirth, monthOfBirth - 1, dayOfBirth);

			ageYears = DateHelper.getYearsBetween(birthDate, symptomOnsetDate);
			ageMonths = DateHelper.getMonthsBetween(birthDate, symptomOnsetDate);
			ageDays = DateHelper.getFullDaysBetween(birthDate, symptomOnsetDate);
		} catch (Exception e) {
			ageYears = null;
			ageMonths = null;
			ageDays = null;
		}
	}

	private String hasHistoryOfHospitalization() {
		boolean hospitalized = false;

		if (admittedToHealthFacility == YesNoUnknown.YES
			&& hospitalizationReason == HospitalizationReasonType.REPORTED_DISEASE
			&& DateHelper.getDaysBetween(admissionDate, dischargeDate) > 1) {
			hospitalized = true;
		} else {
			for (EpipulseHospitalizationCheckDto dto : previousHospitalizations) {
				if (dto.getAdmittedToHealthFacility() == YesNoUnknown.YES
					&& dto.getHospitalizationReason() == HospitalizationReasonType.REPORTED_DISEASE
					&& DateHelper.getDaysBetween(dto.getAdmissionDate(), dto.getDischargeDate()) > 1) {
					hospitalized = true;
					break;
				}
			}
		}

		return String.valueOf(hospitalized);
	}

	private boolean hasPathogenTest() {
		return (pathogenTests != null && !pathogenTests.isEmpty());
	}

	public static String formatDateForCsv(Date date) {

		if (date != null) {
			return DB_DATE_FORMAT.format(date);
		} else {
			return "";
		}
	}

	public List<EpipulseHospitalizationCheckDto> parsePreviousHospitalizationChecks(String dbPreviousHospitalizationStr) {
		if (StringUtils.isBlank(dbPreviousHospitalizationStr)) {
			return new ArrayList<>();
		}

		String[] allHospitalizationArr = dbPreviousHospitalizationStr.split(COLLECTION_SPLIT_CHARACTER);
		if (allHospitalizationArr.length == 0) {
			return new ArrayList<>();
		}

		List<EpipulseHospitalizationCheckDto> previousHospitalizations = new ArrayList<>();
		EpipulseHospitalizationCheckDto dto = null;
		for (String hospitalizationStr : allHospitalizationArr) {
			String[] hospitalizationArr = hospitalizationStr.split(RECORD_SPLIT_CHARACTER);
			if (hospitalizationArr.length < 4) {
				continue;
			}
			String admittedToHealthFacilityStr = hospitalizationArr[0];
			String hospitalizationReasonStr = hospitalizationArr[1];
			String admissionDateStr = hospitalizationArr[2];
			String dischargeDateStr = hospitalizationArr[3];

			dto = new EpipulseHospitalizationCheckDto();

			if (!StringUtils.isBlank(admittedToHealthFacilityStr)) {
				dto.setAdmittedToHealthFacility(YesNoUnknown.valueOf(admittedToHealthFacilityStr));
			}

			if (!StringUtils.isBlank(hospitalizationReasonStr)) {
				dto.setHospitalizationReason(HospitalizationReasonType.valueOf(hospitalizationReasonStr));
			}

			if (!StringUtils.isBlank(admissionDateStr)) {
				dto.setAdmissionDate(DateHelper.parseDate(admissionDateStr, DB_DATE_FORMAT));
			}

			if (!StringUtils.isBlank(dischargeDateStr)) {
				dto.setDischargeDate(DateHelper.parseDate(dischargeDateStr, DB_DATE_FORMAT));
			}

			previousHospitalizations.add(dto);
		}

		return previousHospitalizations;
	}

	public List<EpipulsePathogentTestCheckDto> parsePathogenTestChecks(
		String dbPathogenTestStr,
		List<PathogenTestType> subjectCodePathogenTestTypes) {

		if (StringUtils.isBlank(dbPathogenTestStr)) {
			return new ArrayList<>();
		}

		String[] allPathogenTestArr = dbPathogenTestStr.split(COLLECTION_SPLIT_CHARACTER);
		if (allPathogenTestArr.length == 0) {
			return new ArrayList<>();
		}

		List<EpipulsePathogentTestCheckDto> pathogenTests = new ArrayList<>();
		EpipulsePathogentTestCheckDto dto = null;
		for (String pathogenTestStr : allPathogenTestArr) {
			String[] pathogenTestArr = pathogenTestStr.split(RECORD_SPLIT_CHARACTER);
			if (pathogenTestArr.length < 2) {
				continue;
			}
			String testTypeStr = pathogenTestArr[0];
			String testResultStr = pathogenTestArr[1];

			PathogenTestType testType = null;
			if (!StringUtils.isBlank(testTypeStr)) {
				testType = PathogenTestType.fromLegacyName(testTypeStr);
			}

			PathogenTestResultType testResultType = null;
			if (!StringUtils.isBlank(testResultStr)) {
				testResultType = PathogenTestResultType.valueOf(testResultStr);
			}

			if (!subjectCodePathogenTestTypes.contains(testType) || testResultType != PathogenTestResultType.POSITIVE) {
				continue;
			}

			dto = new EpipulsePathogentTestCheckDto();
			dto.setTestType(testType);
			dto.setTestResult(testResultType);

			pathogenTests.add(dto);
		}

		return pathogenTests;
	}

	public List<EpipulseImmunizationCheckDto> parseImmunizationChecks(String dbPreviousImmunizationStr) {
		if (StringUtils.isBlank(dbPreviousImmunizationStr)) {
			return new ArrayList<>();
		}

		String[] allImmunizationArr = dbPreviousImmunizationStr.split(COLLECTION_SPLIT_CHARACTER);
		if (allImmunizationArr.length == 0) {
			return new ArrayList<>();
		}

		List<EpipulseImmunizationCheckDto> immunizations = new ArrayList<>();
		EpipulseImmunizationCheckDto dto = null;
		for (String immunizationStr : allImmunizationArr) {
			// Use -1 limit to preserve trailing empty strings (e.g., when numberOfDoses is null)
			String[] immunizationArr = immunizationStr.split(RECORD_SPLIT_CHARACTER, -1);
			if (immunizationArr.length < 4) {
				continue;
			}
			String startDate = immunizationArr[0];
			String endDate = immunizationArr[1];
			String meansOfImmunization = immunizationArr[2];
			String numberOfDoses = immunizationArr[3];

			dto = new EpipulseImmunizationCheckDto();

			if (!StringUtils.isBlank(startDate)) {
				dto.setStartDate(DateHelper.parseDate(startDate, DB_DATE_FORMAT));
			}

			if (!StringUtils.isBlank(endDate)) {
				dto.setEndDate(DateHelper.parseDate(endDate, DB_DATE_FORMAT));
			}

			if (!StringUtils.isBlank(meansOfImmunization)) {
				dto.setMeansOfImmunization(MeansOfImmunization.valueOf(meansOfImmunization));
			}

			if (!StringUtils.isBlank(numberOfDoses)) {
				dto.setNumberOfDoses(Integer.parseInt(numberOfDoses));
			}

			immunizations.add(dto);
		}

		return immunizations;
	}

	public List<EpipulseVaccinationCheckDto> parseVaccinations(String dbVaccinationStr) {
		if (StringUtils.isBlank(dbVaccinationStr)) {
			return new ArrayList<>();
		}

		String[] allVaccinationArr = dbVaccinationStr.split(COLLECTION_SPLIT_CHARACTER);
		if (allVaccinationArr.length == 0) {
			return new ArrayList<>();
		}

		List<EpipulseVaccinationCheckDto> vaccinations = new ArrayList<>();
		EpipulseVaccinationCheckDto dto = null;
		for (String vaccinationStr : allVaccinationArr) {
			String[] vaccinationArr = vaccinationStr.split(RECORD_SPLIT_CHARACTER);
			if (vaccinationArr.length < 2) {
				continue;
			}
			String vaccinationDate = vaccinationArr[0];
			String vaccineDose = vaccinationArr[1];

			dto = new EpipulseVaccinationCheckDto();

			if (!StringUtils.isBlank(vaccinationDate)) {
				dto.setVaccinationDate(DateHelper.parseDate(vaccinationDate, DB_DATE_FORMAT));
			}

			if (!StringUtils.isBlank(vaccineDose)) {
				try {
					dto.setVaccineDose(Integer.parseInt(vaccineDose));
				} catch (NumberFormatException e) {
				}
			}

			vaccinations.add(dto);
		}

		return vaccinations;
	}
}

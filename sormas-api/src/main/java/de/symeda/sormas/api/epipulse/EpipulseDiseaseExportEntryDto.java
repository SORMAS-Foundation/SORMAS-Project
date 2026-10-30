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

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Raw values for one case as returned by the export SQL query.
 *
 * <p>
 * This class is intentionally data-only. It stores SORMAS-typed values (dates, enums, parsed
 * aggregates), while CSV mapping and formatting are handled in {@link EpipulseMapping}.
 *
 * <p>
 * Keep business rules out of this DTO. Derived values belong in mapping helpers or disease field
 * models.
 */
public class EpipulseDiseaseExportEntryDto {

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
	private SymptomState asymptomatic;
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
	private List<EpipulseVaccinationCheckDto> vaccinations;
	private List<EpipulseImmunizationCheckDto> immunizations;

	// MEAS-specific laboratory fields (can be mapped from existing SORMAS data)
	private Date dateOfSpecimen;
	private Date dateOfLaboratoryResult;
	private List<String> typeOfSpecimenCollected; // SampleMaterial mapped to EpiPulse codes (repeatable)
	private String resultOfVirusDetection; // PathogenTestResultType mapped to POS/NEG/EQUI/NOTEST
	private String genotype; // EpipulseMeaslesGenotypeRef name, from PathogenTest.genoType
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
	private String resultOfCulture; // PathogenTestResultType for CULTURE -> POS/NEG/EQUI/NOTEST
	private String resultOfPCR; // PathogenTestResultType for PCR_RT_PCR -> POS/NEG/EQUI/NOTEST
	private String serogroupMethod; // PathogenTestType.SEROGROUPING presence -> POS/NEG/NOTEST
	private String penicillinResistance; // DrugSusceptibilityDto -> SENS/RESIST/INTER/NOTEST

	// IPI-specific clinical fields
	private List<String> clinicalPresentation; // SymptomsDto (meningitis, septicaemia, etc.) -> MENING/SEPT/PNEUM/OME/ASYMP (repeatable)

	// PNEU-specific fields (following metadata specification exactly)
	// Demographics
	private Boolean nrlData; // National Reference Laboratory data flag

	// Clinical/Diagnostic
	// the two dates DateOfDiagnosis is the earlier of, and the tiers DateUsedForStatistics
	// chooses between; the rules live in EpipulseCaseDates
	private Date firstPositiveTestDate;
	private Date doctorDateOfDiagnosis;
	private Date firstNotificationReportDate;
	private String clinicalCriteria; // REF: BACTERPNEUMO, MENI, MENISEPTI, OTH, SEPTI

	// -- Syphilis --
	private String birthCountryNutsCode; // CountryOfBirth
	private String citizenshipCountryNutsCode; // CountryOfNationality
	private String motherBirthCountryNutsCode; // CountryOfBirthOfMother, CONSYPH only
	private String motherCitizenshipCountryNutsCode; // CountryOfNationalityOfMother, CONSYPH only
	private List<String> siteOfInfection; // REF: AR, GEN, OTH, PH
	private String stageSyph; // REF: I, NI
	private String stageSyphDetailed; // REF: EL, L, LL, P, S
	private Boolean sexWorker;

	// Laboratory
	private List<SerotypingMethod> serotypingMethods; // PNEU PathogenDetectionMethod

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
	// PorA1 result
	private String resultPorA1;
	// PorA2 result
	private String resultPorA2;
	// FetA VR result
	private String resultFetVR;
	// CIP (Ciprofloxacin) AST - MENI uses this instead of ERY
	private String micSign_CIP;
	private Double micValueAST_CIP;
	private String sir_CIP;
	// RIF (Rifampicin) AST - MENI-specific antibiotic
	private String micSign_RIF;
	private Double micValueAST_RIF;
	private String sir_RIF;

	// -- SALM (Salmonellosis) --
	// SALM uses a BOOL here. MEAS and MENI map importation through ImportedStatus.
	private Boolean imported;
	private String modeOfTransmission; // EpiDataDto.modeOfTransmission mapped to EpiPulse codes
	private String suspectedVehicle; // EpiDataDto.infectionSource mapped to EpiPulse codes
	private String specimen; // SampleMaterial mapped to BLOOD/CSF/FAECES/OTH/PUS/URINE
	private Date dateOfReceiptReferenceLab; // Sample.receivedDate of the reference laboratory's sample
	// SALM reports SIR interpretation without MIC sign/value.
	private String sir_AMP; // Ampicillin
	private String sir_CAZ; // Ceftazidime
	private String sir_CTX; // Cefotaxime
	private String sir_SXT; // Trimethoprim-sulfamethoxazole

	// -- MALA (Malaria) --
	// Imported, ModeOfTransmission, ClinicalCriteriaStatus and PlaceOfInfection are reused here.
	private String occupation; // PersonDto.occupationType mapped to AIRW/HCW/OTH
	// Repeatable in MALA, therefore represented as a list.
	private List<String> pathogen; // PathogenTest.specie mapped to PLAS* (MALA) or SHI* (SHIG) codes
	private Boolean prophylaxis; // ExposureDto.prophylaxisAdherence on the case's travel
	private String purposeOfTravel; // ExposureDto.travelPurpose on the same travel

	// -- DENGUE --
	// CaseClassification, ClinicalCriteria, ClinicalCriteriaStatus, ClusterId, Imported,
	// ModeOfTransmission, PathogenDetectionMethod and PlaceOfInfection are declared above and
	// reused as they stand. clusterIdentification carries epiData.clusterIdentifier here where
	// measles reads epiData.clusterTypeText into it, and modeOfTransmission holds a code from
	// DENGUE's value set - both are properties of the derivation the field model chose, not of
	// these fields.
	private String serotype; // PathogenTest.serotype mapped to DENV1-DENV4/DENVOTH
	private Boolean seroconversionOrTitreRise; // PathogenTest.seroConversion or fourFoldIncreaseAntibodyTiter

	// -- GONO (Gonorrhoea) --
	// The STI block SYPH declared first and left blank. siteOfInfection, sexWorker,
	// modeOfTransmission and the country fields above are reused as they stand; gonorrhoea fills
	// siteOfInfection from nine symptom columns where syphilis fills it from one enum, which is a
	// property of the derivation rather than of the field.
	private String clinicalServiceType; // EpiDataDto.typeOfClinicalService mapped to EpiPulse codes
	private String hivStatus; // HealthConditionsDto.hivStatus mapped to NEG/POS/POSKNOWN/POSNEW
	private Boolean hivPrEP; // HealthConditionsDto.hivPrep
	private Boolean antibioticProphylaxis; // HealthConditionsDto.stiProphylaxis
	private Boolean contactSW; // EpiDataDto.contactWithSexWorker

	// -- RUBE (Rubella) --

	private Boolean pregnancy; // CaseDataDto.pregnant

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

	public List<EpipulseVaccinationCheckDto> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<EpipulseVaccinationCheckDto> vaccinations) {
		this.vaccinations = vaccinations;
	}

	/** The acquired vaccination courses {@code VaccinationStatus} chooses one of. */
	public List<EpipulseImmunizationCheckDto> getImmunizations() {
		return immunizations;
	}

	public void setImmunizations(List<EpipulseImmunizationCheckDto> immunizations) {
		this.immunizations = immunizations;
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

	public SymptomState getAsymptomatic() {
		return asymptomatic;
	}

	public void setAsymptomatic(SymptomState asymptomatic) {
		this.asymptomatic = asymptomatic;
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

	/** The oldest positive pathogen test on this case. */
	public Date getFirstPositiveTestDate() {
		return firstPositiveTestDate;
	}

	public void setFirstPositiveTestDate(Date firstPositiveTestDate) {
		this.firstPositiveTestDate = firstPositiveTestDate;
	}

	/** The oldest date of diagnosis notified by a doctor. */
	public Date getDoctorDateOfDiagnosis() {
		return doctorDateOfDiagnosis;
	}

	public void setDoctorDateOfDiagnosis(Date doctorDateOfDiagnosis) {
		this.doctorDateOfDiagnosis = doctorDateOfDiagnosis;
	}

	/** The oldest surveillance report of this case, whether a doctor's declaration or a lab message. */
	public Date getFirstNotificationReportDate() {
		return firstNotificationReportDate;
	}

	public void setFirstNotificationReportDate(Date firstNotificationReportDate) {
		this.firstNotificationReportDate = firstNotificationReportDate;
	}

	public String getClinicalCriteria() {
		return clinicalCriteria;
	}

	public void setClinicalCriteria(String clinicalCriteria) {
		this.clinicalCriteria = clinicalCriteria;
	}

	/**
	 * The serotyping techniques recorded on the case's qualifying tests, as SORMAS records them.
	 *
	 * <p>
	 * Not yet EpiPulse codes: which code each technique reports is a rule, and it lives in
	 * {@code EpipulseMapping.serotypingMethods}. This is the same split as {@link #pathogenTests}
	 * against {@code EpipulseMapping.pathogenDetectionMethods}, for the same reason - a rule that
	 * has already been applied cannot be tested or changed.
	 */
	public List<SerotypingMethod> getSerotypingMethods() {
		return serotypingMethods;
	}

	public void setSerotypingMethods(List<SerotypingMethod> serotypingMethods) {
		this.serotypingMethods = serotypingMethods;
	}

	public String getAstMethod() {
		return astMethod;
	}

	public void setAstMethod(String astMethod) {
		this.astMethod = astMethod;
	}

	public String getSir_CTX_CFX() {
		return sir_CTX_CFX;
	}

	public void setSir_CTX_CFX(String sir_CTX_CFX) {
		this.sir_CTX_CFX = sir_CTX_CFX;
	}

	public String getSir_ERY() {
		return sir_ERY;
	}

	public void setSir_ERY(String sir_ERY) {
		this.sir_ERY = sir_ERY;
	}

	public String getSir_PEN() {
		return sir_PEN;
	}

	public void setSir_PEN(String sir_PEN) {
		this.sir_PEN = sir_PEN;
	}

	// MENI-specific getters and setters
	public String getSerogroup() {
		return serogroup;
	}

	public void setSerogroup(String serogroup) {
		this.serogroup = serogroup;
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

	public String getSir_CIP() {
		return sir_CIP;
	}

	public void setSir_CIP(String sir_CIP) {
		this.sir_CIP = sir_CIP;
	}

	public String getSir_RIF() {
		return sir_RIF;
	}

	public void setSir_RIF(String sir_RIF) {
		this.sir_RIF = sir_RIF;
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

	// ==================== Syphilis ====================

	public String getBirthCountryNutsCode() {
		return birthCountryNutsCode;
	}

	public void setBirthCountryNutsCode(String birthCountryNutsCode) {
		this.birthCountryNutsCode = birthCountryNutsCode;
	}

	public String getMotherBirthCountryNutsCode() {
		return motherBirthCountryNutsCode;
	}

	public String getCitizenshipCountryNutsCode() {
		return citizenshipCountryNutsCode;
	}

	public void setCitizenshipCountryNutsCode(String citizenshipCountryNutsCode) {
		this.citizenshipCountryNutsCode = citizenshipCountryNutsCode;
	}

	public void setMotherBirthCountryNutsCode(String motherBirthCountryNutsCode) {
		this.motherBirthCountryNutsCode = motherBirthCountryNutsCode;
	}

	public String getMotherCitizenshipCountryNutsCode() {
		return motherCitizenshipCountryNutsCode;
	}

	public void setMotherCitizenshipCountryNutsCode(String motherCitizenshipCountryNutsCode) {
		this.motherCitizenshipCountryNutsCode = motherCitizenshipCountryNutsCode;
	}

	public List<String> getSiteOfInfection() {
		return siteOfInfection;
	}

	public void setSiteOfInfection(List<String> siteOfInfection) {
		this.siteOfInfection = siteOfInfection;
	}

	public String getStageSyph() {
		return stageSyph;
	}

	public void setStageSyph(String stageSyph) {
		this.stageSyph = stageSyph;
	}

	public String getStageSyphDetailed() {
		return stageSyphDetailed;
	}

	public void setStageSyphDetailed(String stageSyphDetailed) {
		this.stageSyphDetailed = stageSyphDetailed;
	}

	public Boolean getSexWorker() {
		return sexWorker;
	}

	public void setSexWorker(Boolean sexWorker) {
		this.sexWorker = sexWorker;
	}

	public Boolean getImported() {
		return imported;
	}

	public void setImported(Boolean imported) {
		this.imported = imported;
	}

	public String getModeOfTransmission() {
		return modeOfTransmission;
	}

	public void setModeOfTransmission(String modeOfTransmission) {
		this.modeOfTransmission = modeOfTransmission;
	}

	public String getSuspectedVehicle() {
		return suspectedVehicle;
	}

	public void setSuspectedVehicle(String suspectedVehicle) {
		this.suspectedVehicle = suspectedVehicle;
	}

	public String getSpecimen() {
		return specimen;
	}

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}

	public Date getDateOfReceiptReferenceLab() {
		return dateOfReceiptReferenceLab;
	}

	public void setDateOfReceiptReferenceLab(Date dateOfReceiptReferenceLab) {
		this.dateOfReceiptReferenceLab = dateOfReceiptReferenceLab;
	}

	public String getSir_AMP() {
		return sir_AMP;
	}

	public void setSir_AMP(String sir_AMP) {
		this.sir_AMP = sir_AMP;
	}

	public String getSir_CAZ() {
		return sir_CAZ;
	}

	public void setSir_CAZ(String sir_CAZ) {
		this.sir_CAZ = sir_CAZ;
	}

	public String getSir_CTX() {
		return sir_CTX;
	}

	public void setSir_CTX(String sir_CTX) {
		this.sir_CTX = sir_CTX;
	}

	public String getSir_SXT() {
		return sir_SXT;
	}

	public void setSir_SXT(String sir_SXT) {
		this.sir_SXT = sir_SXT;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public List<String> getPathogen() {
		return pathogen;
	}

	public void setPathogen(List<String> pathogen) {
		this.pathogen = pathogen;
	}

	public Boolean getProphylaxis() {
		return prophylaxis;
	}

	public void setProphylaxis(Boolean prophylaxis) {
		this.prophylaxis = prophylaxis;
	}

	public String getPurposeOfTravel() {
		return purposeOfTravel;
	}

	public void setPurposeOfTravel(String purposeOfTravel) {
		this.purposeOfTravel = purposeOfTravel;
	}

	public String getSerotype() {
		return serotype;
	}

	public void setSerotype(String serotype) {
		this.serotype = serotype;
	}

	public Boolean getSeroconversionOrTitreRise() {
		return seroconversionOrTitreRise;
	}

	public void setSeroconversionOrTitreRise(Boolean seroconversionOrTitreRise) {
		this.seroconversionOrTitreRise = seroconversionOrTitreRise;
	}

	public String getClinicalServiceType() {
		return clinicalServiceType;
	}

	public void setClinicalServiceType(String clinicalServiceType) {
		this.clinicalServiceType = clinicalServiceType;
	}

	public String getHivStatus() {
		return hivStatus;
	}

	public void setHivStatus(String hivStatus) {
		this.hivStatus = hivStatus;
	}

	public Boolean getHivPrEP() {
		return hivPrEP;
	}

	public void setHivPrEP(Boolean hivPrEP) {
		this.hivPrEP = hivPrEP;
	}

	public Boolean getAntibioticProphylaxis() {
		return antibioticProphylaxis;
	}

	public void setAntibioticProphylaxis(Boolean antibioticProphylaxis) {
		this.antibioticProphylaxis = antibioticProphylaxis;
	}

	public Boolean getContactSW() {
		return contactSW;
	}

	public void setContactSW(Boolean contactSW) {
		this.contactSW = contactSW;
	}

	// ==================== Rubella ====================

	public Boolean getPregnancy() {
		return pregnancy;
	}

	public void setPregnancy(Boolean pregnancy) {
		this.pregnancy = pregnancy;
	}

}

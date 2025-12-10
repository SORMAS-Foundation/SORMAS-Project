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
package de.symeda.sormas.backend.sample;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.disease.DiseaseVariantConverter;
import de.symeda.sormas.api.disease.PathogenConverter;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.sample.GenoTypeResult;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenStrainCallStatus;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestScale;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.RsvSubtype;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.api.sample.SerotypingMethod;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.therapy.DrugSusceptibility;
import de.symeda.sormas.backend.user.User;

@Entity
public class PathogenTest extends DeletableAdo {

	private static final long serialVersionUID = 2290351143518627813L;

	public static final String TABLE_NAME = "pathogentest";

	public static final String SAMPLE = "sample";
	public static final String ENVIRONMENT_SAMPLE = "environmentSample";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String TESTED_DISEASE_VARIANT_VALUE = "testedDiseaseVariantValue";
	public static final String TESTED_DISEASE_VARIANT_DETAILS = "testedDiseaseVariantDetails";
	public static final String TESTED_PATHOGEN_VALUE = "testedPathogenValue";
	public static final String TESTED_PATHOGEN_DETAILS = "testedPathogenDetails";
	public static final String TYPING_ID = "typingId";
	public static final String TEST_TYPE = "testType";
	public static final String PCR_TEST_SPECIFICATION = "pcrTestSpecification";
	public static final String TEST_TYPE_TEXT = "testTypeText";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String LAB = "lab";
	public static final String LAB_DETAILS = "labDetails";
	public static final String LAB_USER = "labUser";
	public static final String TEST_RESULT = "testResult";
	public static final String TEST_RESULT_TEXT = "testResultText";
	public static final String TEST_RESULT_VERIFIED = "testResultVerified";
	public static final String FOUR_FOLD_INCREASE_ANTIBODY_TITER = "fourFoldIncreaseAntibodyTiter";
	public static final String SEROTYPE = "serotype";
	public static final String CQ_VALUE = "cqValue";
	public static final String CT_VALUE_E = "ctValueE";
	public static final String CT_VALUE_N = "ctValueN";
	public static final String CT_VALUE_RDRP = "ctValueRdrp";
	public static final String CT_VALUE_S = "ctValueS";
	public static final String CT_VALUE_ORF_1 = "ctValueOrf1";
	public static final String CT_VALUE_RDRP_S = "ctValueRdrpS";
	public static final String REPORT_DATE = "reportDate";
	public static final String PRESCRIBER_PHYSICIAN_CODE = "prescriberPhysicianCode";
	public static final String PRESCRIBER_FIRST_NAME = "prescriberFirstName";
	public static final String PRESCRIBER_LAST_NAME = "prescriberLastName";
	public static final String PRESCRIBER_PHONE_NUMBER = "prescriberPhoneNumber";
	public static final String PRESCRIBER_ADDRESS = "prescriberAddress";
	public static final String PRESCRIBER_POSTAL_CODE = "prescriberPostalCode";
	public static final String PRESCRIBER_CITY = "prescriberCity";
	public static final String PRESCRIBER_COUNTRY = "prescriberCountry";
	public static final String RIFAMPICIN_RESISTANT = "rifampicinResistant";
	public static final String ISONIAZID_RESISTANT = "isoniazidResistant";
	public static final String SPECIE = "specie";
	public static final String PATTERN_PROFILE = "patternProfile";
	public static final String STRAIN_CALL_STATUS = "strainCallStatus";
	public static final String TEST_SCALE = "testScale";
	public static final String DRUG_SUSCEPTIBILITY = "drugSusceptibility";
	public static final String RSV_SUBTYPE = "rsvSubtype";

	private Sample sample;
	private EnvironmentSample environmentSample;
	private Disease testedDisease;
	private String testedDiseaseVariantValue;
	private DiseaseVariant testedDiseaseVariant;
	private String testedDiseaseDetails;
	private String testedDiseaseVariantDetails;
	private String testedPathogenValue;
	private Pathogen testedPathogen;
	private String testedPathogenDetails;
	private String typingId;
	private PathogenTestType testType;
	private PCRTestSpecification pcrTestSpecification;
	private String testTypeText;
	private Date testDateTime;
	private Facility lab;
	private String labDetails;
	private User labUser;
	private PathogenTestResultType testResult;
	private String testResultText;
	private Boolean testResultVerified;
	private boolean fourFoldIncreaseAntibodyTiter;
	private String serotype;
	private Float cqValue;
	private Float ctValueE;
	private Float ctValueN;
	private Float ctValueRdrp;
	private Float ctValueS;
	private Float ctValueOrf1;
	private Float ctValueRdrpS;
	private Date reportDate;
	private boolean viaLims;
	private String externalId;
	private String externalOrderId;
	private Boolean preliminary;
	private String prescriberPhysicianCode;
	private String prescriberFirstName;
	private String prescriberLastName;
	private String prescriberPhoneNumber;
	private String prescriberAddress;
	private String prescriberPostalCode;
	private String prescriberCity;
	private Country prescriberCountry;
	private YesNoUnknown rifampicinResistant;
	private YesNoUnknown isoniazidResistant;
	private PathogenSpecie specie;
	private String patternProfile;
	private PathogenStrainCallStatus strainCallStatus;
	private PathogenTestScale testScale;
	private DrugSusceptibility drugSusceptibility;
	private String miruPatternProfile;
	private SerotypingMethod seroTypingMethod;
	private String seroTypingMethodText;
	private GenoTypeResult genoTypeResult;
	private String genoTypeResultText;
	private SeroGroupSpecification seroGroupSpecification;
	private String seroGroupSpecificationText;
	private RsvSubtype rsvSubtype;
	private Float tubeNil;
	private Boolean tubeNilGT10;
	private Float tubeAgTb1;
	private Boolean tubeAgTb1GT10;
	private Float tubeAgTb2;
	private Boolean tubeAgTb2GT10;
	private Float tubeMitogene;
	private Boolean tubeMitogeneGT10;

	@ManyToOne(fetch = FetchType.LAZY)
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public EnvironmentSample getEnvironmentSample() {
		return environmentSample;
	}

	public void setEnvironmentSample(EnvironmentSample environmentSample) {
		this.environmentSample = environmentSample;
	}

	@Enumerated(EnumType.STRING)
	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestedDiseaseDetails() {
		return testedDiseaseDetails;
	}

	public void setTestedDiseaseDetails(String testedDiseaseDetails) {
		this.testedDiseaseDetails = testedDiseaseDetails;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestedDiseaseVariantDetails() {
		return testedDiseaseVariantDetails;
	}

	public void setTestedDiseaseVariantDetails(String testedDiseaseVariantDetails) {
		this.testedDiseaseVariantDetails = testedDiseaseVariantDetails;
	}

	@Column(name = "testeddiseasevariant")
	public String getTestedDiseaseVariantValue() {
		return testedDiseaseVariantValue;
	}

	public void setTestedDiseaseVariantValue(String diseaseVariantValue) {
		this.testedDiseaseVariantValue = diseaseVariantValue;
		this.testedDiseaseVariant = new DiseaseVariantConverter().convertToEntityAttribute(testedDisease, testedDiseaseVariantValue);
	}

	@Transient
	public DiseaseVariant getTestedDiseaseVariant() {
		return testedDiseaseVariant;
	}

	public void setTestedDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.testedDiseaseVariant = diseaseVariant;
		this.testedDiseaseVariantValue = new DiseaseVariantConverter().convertToDatabaseColumn(diseaseVariant);
	}

	@Column(name = "testedpathogen")
	public String getTestedPathogenValue() {
		return testedPathogenValue;
	}

	public void setTestedPathogenValue(String testedPathogenValue) {
		this.testedPathogenValue = testedPathogenValue;
		this.testedPathogen = new PathogenConverter().convertToEntityAttribute(null, testedPathogenValue);
	}

	@Transient
	public Pathogen getTestedPathogen() {
		return testedPathogen;
	}

	public void setTestedPathogen(Pathogen testedPathogen) {
		this.testedPathogen = testedPathogen;
		this.testedPathogenValue = new PathogenConverter().convertToDatabaseColumn(testedPathogen);
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestedPathogenDetails() {
		return testedPathogenDetails;
	}

	public void setTestedPathogenDetails(String testedPathogenDetails) {
		this.testedPathogenDetails = testedPathogenDetails;
	}

	@Column
	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	@Enumerated(EnumType.STRING)
	public PCRTestSpecification getPcrTestSpecification() {
		return pcrTestSpecification;
	}

	public void setPcrTestSpecification(PCRTestSpecification pcrTestSpecification) {
		this.pcrTestSpecification = pcrTestSpecification;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getTestTypeText() {
		return testTypeText;
	}

	public void setTestTypeText(String testTypeText) {
		this.testTypeText = testTypeText;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public Facility getLab() {
		return lab;
	}

	public void setLab(Facility lab) {
		this.lab = lab;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLabDetails() {
		return labDetails;
	}

	public void setLabDetails(String labDetails) {
		this.labDetails = labDetails;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	public User getLabUser() {
		return labUser;
	}

	public void setLabUser(User labUser) {
		this.labUser = labUser;
	}

	@Enumerated(EnumType.STRING)
	@JoinColumn(nullable = false)
	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	@Column(nullable = false)
	public Boolean getTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	@Column
	public boolean isFourFoldIncreaseAntibodyTiter() {
		return fourFoldIncreaseAntibodyTiter;
	}

	public void setFourFoldIncreaseAntibodyTiter(boolean fourFoldIncreaseAntibodyTiter) {
		this.fourFoldIncreaseAntibodyTiter = fourFoldIncreaseAntibodyTiter;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSerotype() {
		return serotype;
	}

	public void setSerotype(String serotype) {
		this.serotype = serotype;
	}

	@Column
	public Float getCqValue() {
		return cqValue;
	}

	public void setCqValue(Float cqValue) {
		this.cqValue = cqValue;
	}

	@Column
	public Float getCtValueE() {
		return ctValueE;
	}

	public void setCtValueE(Float ctValueE) {
		this.ctValueE = ctValueE;
	}

	@Column
	public Float getCtValueN() {
		return ctValueN;
	}

	public void setCtValueN(Float ctValueN) {
		this.ctValueN = ctValueN;
	}

	@Column
	public Float getCtValueRdrp() {
		return ctValueRdrp;
	}

	public void setCtValueRdrp(Float ctValueRdrp) {
		this.ctValueRdrp = ctValueRdrp;
	}

	@Column
	public Float getCtValueS() {
		return ctValueS;
	}

	public void setCtValueS(Float ctValueS) {
		this.ctValueS = ctValueS;
	}

	@Column
	public Float getCtValueOrf1() {
		return ctValueOrf1;
	}

	public void setCtValueOrf1(Float ctValueOrf1) {
		this.ctValueOrf1 = ctValueOrf1;
	}

	@Column
	public Float getCtValueRdrpS() {
		return ctValueRdrpS;
	}

	public void setCtValueRdrpS(Float ctValueRdrpS) {
		this.ctValueRdrpS = ctValueRdrpS;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@Column
	public boolean isViaLims() {
		return viaLims;
	}

	public void setViaLims(boolean viaLims) {
		this.viaLims = viaLims;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getExternalOrderId() {
		return externalOrderId;
	}

	public void setExternalOrderId(String externalOrderId) {
		this.externalOrderId = externalOrderId;
	}

	@Column
	public Boolean getPreliminary() {
		return preliminary;
	}

	public void setPreliminary(Boolean preliminary) {
		this.preliminary = preliminary;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberPhysicianCode() {
		return prescriberPhysicianCode;
	}

	public void setPrescriberPhysicianCode(String prescriberPhysicianCode) {
		this.prescriberPhysicianCode = prescriberPhysicianCode;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberFirstName() {
		return prescriberFirstName;
	}

	public void setPrescriberFirstName(String prescriberFirstName) {
		this.prescriberFirstName = prescriberFirstName;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberLastName() {
		return prescriberLastName;
	}

	public void setPrescriberLastName(String prescriberLastName) {
		this.prescriberLastName = prescriberLastName;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberPhoneNumber() {
		return prescriberPhoneNumber;
	}

	public void setPrescriberPhoneNumber(String prescriberPhoneNumber) {
		this.prescriberPhoneNumber = prescriberPhoneNumber;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberAddress() {
		return prescriberAddress;
	}

	public void setPrescriberAddress(String prescriberAddress) {
		this.prescriberAddress = prescriberAddress;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberPostalCode() {
		return prescriberPostalCode;
	}

	public void setPrescriberPostalCode(String prescriberPostalCode) {
		this.prescriberPostalCode = prescriberPostalCode;
	}

	@Column(columnDefinition = "text")
	public String getPrescriberCity() {
		return prescriberCity;
	}

	public void setPrescriberCity(String prescriberCity) {
		this.prescriberCity = prescriberCity;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Country getPrescriberCountry() {
		return prescriberCountry;
	}

	public void setPrescriberCountry(Country prescriberCountry) {
		this.prescriberCountry = prescriberCountry;
	}

	public PathogenTestReferenceDto toReference() {
		return new PathogenTestReferenceDto(getUuid());
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getRifampicinResistant() {
		return rifampicinResistant;
	}

	public void setRifampicinResistant(YesNoUnknown rifampicinResistant) {
		this.rifampicinResistant = rifampicinResistant;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getIsoniazidResistant() {
		return isoniazidResistant;
	}

	public void setIsoniazidResistant(YesNoUnknown isoniazidResistant) {
		this.isoniazidResistant = isoniazidResistant;
	}

	@Enumerated(EnumType.STRING)
	public PathogenSpecie getSpecie() {
		return specie;
	}

	public void setSpecie(PathogenSpecie specie) {
		this.specie = specie;
	}

	public String getPatternProfile() {
		return patternProfile;
	}

	public void setPatternProfile(String patternProfile) {
		this.patternProfile = patternProfile;
	}

	@Enumerated(EnumType.STRING)
	public PathogenStrainCallStatus getStrainCallStatus() {
		return strainCallStatus;
	}

	public void setStrainCallStatus(PathogenStrainCallStatus strainCallStatus) {
		this.strainCallStatus = strainCallStatus;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestScale getTestScale() {
		return testScale;
	}

	public void setTestScale(PathogenTestScale testScale) {
		this.testScale = testScale;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public DrugSusceptibility getDrugSusceptibility() {
		return drugSusceptibility;
	}

	public void setDrugSusceptibility(DrugSusceptibility drugSusceptibility) {
		this.drugSusceptibility = drugSusceptibility;
	}

	@Enumerated(EnumType.STRING)
	public SerotypingMethod getSeroTypingMethod() {
		return seroTypingMethod;
	}

	public void setSeroTypingMethod(SerotypingMethod seroTypingMethod) {
		this.seroTypingMethod = seroTypingMethod;
	}

	@Enumerated(EnumType.STRING)
	public GenoTypeResult getGenoTypeResult() {
		return genoTypeResult;
	}

	public void setGenoTypeResult(GenoTypeResult genoTypeResult) {
		this.genoTypeResult = genoTypeResult;
	}

	public String getGenoTypeResultText() {
		return genoTypeResultText;
	}

	public void setGenoTypeResultText(String genoTypeResultText) {
		this.genoTypeResultText = genoTypeResultText;
	}

	public String getSeroTypingMethodText() {
		return seroTypingMethodText;
	}

	public void setSeroTypingMethodText(String seroTypingMethodText) {
		this.seroTypingMethodText = seroTypingMethodText;
	}

	@Enumerated(EnumType.STRING)
	public SeroGroupSpecification getSeroGroupSpecification() {
		return seroGroupSpecification;
	}

	public void setSeroGroupSpecification(SeroGroupSpecification seroGroupSpecification) {
		this.seroGroupSpecification = seroGroupSpecification;
	}

	public String getSeroGroupSpecificationText() {
		return seroGroupSpecificationText;
	}

	public void setSeroGroupSpecificationText(String seroGroupSpecificationText) {
		this.seroGroupSpecificationText = seroGroupSpecificationText;
	}

	@Enumerated(EnumType.STRING)
	public RsvSubtype getRsvSubtype() {
		return rsvSubtype;
	}

	public void setRsvSubtype(RsvSubtype rsvSubtype) {
		this.rsvSubtype = rsvSubtype;
	}

	public Float getTubeNil() {
		return tubeNil;
	}

	public void setTubeNil(Float tubeNil) {
		this.tubeNil = tubeNil;
	}

	public Boolean getTubeNilGT10() {
		return tubeNilGT10;
	}

	public void setTubeNilGT10(Boolean tubeNilGT10) {
		this.tubeNilGT10 = tubeNilGT10;
	}

	public Float getTubeAgTb1() {
		return tubeAgTb1;
	}

	public void setTubeAgTb1(Float tubeAgTb1) {
		this.tubeAgTb1 = tubeAgTb1;
	}

	public Boolean getTubeAgTb1GT10() {
		return tubeAgTb1GT10;
	}

	public void setTubeAgTb1GT10(Boolean tubeAgTb1GT10) {
		this.tubeAgTb1GT10 = tubeAgTb1GT10;
	}

	public Float getTubeAgTb2() {
		return tubeAgTb2;
	}

	public void setTubeAgTb2(Float tubeAgTb2) {
		this.tubeAgTb2 = tubeAgTb2;
	}

	public Boolean getTubeAgTb2GT10() {
		return tubeAgTb2GT10;
	}

	public void setTubeAgTb2GT10(Boolean tubeAgTb2GT10) {
		this.tubeAgTb2GT10 = tubeAgTb2GT10;
	}

	public Float getTubeMitogene() {
		return tubeMitogene;
	}

	public void setTubeMitogene(Float tubeMitogene) {
		this.tubeMitogene = tubeMitogene;
	}

	public Boolean getTubeMitogeneGT10() {
		return tubeMitogeneGT10;
	}

	public void setTubeMitogeneGT10(Boolean tubeMitogeneGT10) {
		this.tubeMitogeneGT10 = tubeMitogeneGT10;
	}

}

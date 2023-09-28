/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api.sample;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateFormatHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = FeatureType.SAMPLES_LAB)
public class PathogenTestDto extends PseudonymizableDto {

	private static final long serialVersionUID = -5213210080802372054L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 3391;

	public static final String I18N_PREFIX = "PathogenTest";

	public static final String SAMPLE = "sample";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String TESTED_DISEASE_VARIANT = "testedDiseaseVariant";
	public static final String TESTED_DISEASE_VARIANT_DETAILS = "testedDiseaseVariantDetails";
	public static final String TYPING_ID = "typingId";
	public static final String TEST_TYPE = "testType";
	public static final String PCR_TEST_SPECIFICATION = "pcrTestSpecification";
	public static final String TESTED_DISEASE_DETAILS = "testedDiseaseDetails";
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
	public static final String VIA_LIMS = "viaLims";
	public static final String EXTERNAL_ID = "externalId";
	public static final String EXTERNAL_ORDER_ID = "externalOrderId";
	public static final String PRELIMINARY = "preliminary";
	public static final String DELETION_REASON = "deletionReason";
	public static final String OTHER_DELETION_REASON = "otherDeletionReason";
	public static final String PRESCRIBER_PHYSICIAN_CODE = "prescriberPhysicianCode";
	public static final String PRESCRIBER_FIRST_NAME = "prescriberFirstName";
	public static final String PRESCRIBER_LAST_NAME = "prescriberLastName";
	public static final String PRESCRIBER_PHONE_NUMBER = "prescriberPhoneNumber";
	public static final String PRESCRIBER_ADDRESS = "prescriberAddress";
	public static final String PRESCRIBER_POSTAL_CODE = "prescriberPostalCode";
	public static final String PRESCRIBER_CITY = "prescriberCity";
	public static final String PRESCRIBER_COUNTRY = "prescriberCountry";

	private SampleReferenceDto sample;
	private EnvironmentSampleReferenceDto environmentSample;
	@NotNull(message = Validations.validDisease)
	private Disease testedDisease;
	private DiseaseVariant testedDiseaseVariant;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String testedDiseaseDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String testedDiseaseVariantDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String typingId;
	@NotNull(message = Validations.requiredField)
	private PathogenTestType testType;
	private PCRTestSpecification pcrTestSpecification;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String testTypeText;
	private Date testDateTime;
	@NotNull(message = Validations.requiredField)
	private FacilityReferenceDto lab;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String labDetails;
	@SensitiveData
	private UserReferenceDto labUser;
	@NotNull(message = Validations.requiredField)
	private PathogenTestResultType testResult;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String testResultText;
	@NotNull(message = Validations.requiredField)
	private Boolean testResultVerified;
	private boolean fourFoldIncreaseAntibodyTiter;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String serotype;
	private Float cqValue;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueE;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueN;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueRdrp;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueS;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueOrf1;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private Float ctValueRdrpS;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private Date reportDate;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	private boolean viaLims;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	@S2SIgnoreProperty(configProperty = SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_GERMANY)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalOrderId;
	private Boolean preliminary;
	private boolean deleted;
	private DeletionReason deletionReason;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String otherDeletionReason;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberPhysicianCode;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberFirstName;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberLastName;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberPhoneNumber;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberAddress;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberPostalCode;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String prescriberCity;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	private CountryReferenceDto prescriberCountry;

	public static PathogenTestDto build(SampleDto sample, UserDto currentUser) {

		PathogenTestDto pathogenTest = new PathogenTestDto();
		pathogenTest.setUuid(DataHelper.createUuid());
		pathogenTest.setSample(sample.toReference());
		if (sample.getSamplePurpose() == SamplePurpose.INTERNAL) {
			pathogenTest.setTestResultVerified(true);
		}
		pathogenTest.setLab(currentUser.getLaboratory());
		if (pathogenTest.getLab() == null) {
			pathogenTest.setLab(sample.getLab());
			pathogenTest.setLabDetails(sample.getLabDetails());
		}
		pathogenTest.setLabUser(currentUser.toReference());
		return pathogenTest;
	}

	public static PathogenTestDto build(SampleReferenceDto sample, UserReferenceDto currentUser) {

		PathogenTestDto pathogenTest = new PathogenTestDto();
		pathogenTest.setUuid(DataHelper.createUuid());
		pathogenTest.setSample(sample);
		pathogenTest.setLabUser(currentUser);
		return pathogenTest;
	}

	public static PathogenTestDto build(EnvironmentSampleDto environmentSample, UserDto currentUser) {
		PathogenTestDto pathogenTest = new PathogenTestDto();
		pathogenTest.setUuid(DataHelper.createUuid());
		pathogenTest.setEnvironmentSample(environmentSample.toReference());

		pathogenTest.setLab(currentUser.getLaboratory());
		if (pathogenTest.getLab() == null) {
			pathogenTest.setLab(environmentSample.getLaboratory());
			pathogenTest.setLabDetails(environmentSample.getLaboratoryDetails());
		}
		pathogenTest.setLabUser(currentUser.toReference());
		return pathogenTest;
	}

	public static PathogenTestDto build(EnvironmentSampleReferenceDto environmentSample, UserReferenceDto currentUser) {
		PathogenTestDto pathogenTest = new PathogenTestDto();
		pathogenTest.setUuid(DataHelper.createUuid());
		pathogenTest.setEnvironmentSample(environmentSample);
		pathogenTest.setLabUser(currentUser);
		return pathogenTest;
	}

	@ImportIgnore
	public SampleReferenceDto getSample() {
		return sample;
	}

	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}

	@ImportIgnore
	public EnvironmentSampleReferenceDto getEnvironmentSample() {
		return environmentSample;
	}

	public void setEnvironmentSample(EnvironmentSampleReferenceDto environmentSample) {
		this.environmentSample = environmentSample;
	}

	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	public DiseaseVariant getTestedDiseaseVariant() {
		return testedDiseaseVariant;
	}

	public void setTestedDiseaseVariant(DiseaseVariant testedDiseaseVariant) {
		this.testedDiseaseVariant = testedDiseaseVariant;
	}

	public String getTestedDiseaseDetails() {
		return testedDiseaseDetails;
	}

	public void setTestedDiseaseDetails(String testedDiseaseDetails) {
		this.testedDiseaseDetails = testedDiseaseDetails;
	}

	public String getTestedDiseaseVariantDetails() {
		return testedDiseaseVariantDetails;
	}

	public void setTestedDiseaseVariantDetails(String testedDiseaseVariantDetails) {
		this.testedDiseaseVariantDetails = testedDiseaseVariantDetails;
	}

	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
	}

	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	public PCRTestSpecification getPcrTestSpecification() {
		return pcrTestSpecification;
	}

	public void setPcrTestSpecification(PCRTestSpecification pcrTestSpecification) {
		this.pcrTestSpecification = pcrTestSpecification;
	}

	public String getTestTypeText() {
		return testTypeText;
	}

	public void setTestTypeText(String testTypeText) {
		this.testTypeText = testTypeText;
	}

	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	public FacilityReferenceDto getLab() {
		return lab;
	}

	public void setLab(FacilityReferenceDto lab) {
		this.lab = lab;
	}

	public String getLabDetails() {
		return labDetails;
	}

	public void setLabDetails(String labDetails) {
		this.labDetails = labDetails;
	}

	public UserReferenceDto getLabUser() {
		return labUser;
	}

	public void setLabUser(UserReferenceDto labUser) {
		this.labUser = labUser;
	}

	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	public Boolean getTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	public boolean isFourFoldIncreaseAntibodyTiter() {
		return fourFoldIncreaseAntibodyTiter;
	}

	public void setFourFoldIncreaseAntibodyTiter(boolean fourFoldIncreaseAntibodyTiter) {
		this.fourFoldIncreaseAntibodyTiter = fourFoldIncreaseAntibodyTiter;
	}

	public PathogenTestReferenceDto toReference() {
		return new PathogenTestReferenceDto(getUuid());
	}

	public String getSerotype() {
		return serotype;
	}

	public void setSerotype(String serotype) {
		this.serotype = serotype;
	}

	public Float getCqValue() {
		return cqValue;
	}

	public void setCqValue(Float cqValue) {
		this.cqValue = cqValue;
	}

	public Float getCtValueE() {
		return ctValueE;
	}

	public void setCtValueE(Float ctValueE) {
		this.ctValueE = ctValueE;
	}

	public Float getCtValueN() {
		return ctValueN;
	}

	public void setCtValueN(Float ctValueN) {
		this.ctValueN = ctValueN;
	}

	public Float getCtValueRdrp() {
		return ctValueRdrp;
	}

	public void setCtValueRdrp(Float ctValueRdrp) {
		this.ctValueRdrp = ctValueRdrp;
	}

	public Float getCtValueS() {
		return ctValueS;
	}

	public void setCtValueS(Float ctValueS) {
		this.ctValueS = ctValueS;
	}

	public Float getCtValueOrf1() {
		return ctValueOrf1;
	}

	public void setCtValueOrf1(Float ctValueOrf1) {
		this.ctValueOrf1 = ctValueOrf1;
	}

	public Float getCtValueRdrpS() {
		return ctValueRdrpS;
	}

	public void setCtValueRdrpS(Float ctValueRdrpS) {
		this.ctValueRdrpS = ctValueRdrpS;
	}

	@Override
	public String buildCaption() {
		return DateFormatHelper.formatLocalDateTime(testDateTime) + " - " + testType + " (" + testedDisease + "): " + testResult;
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public boolean isViaLims() {
		return viaLims;
	}

	public void setViaLims(boolean viaLims) {
		this.viaLims = viaLims;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalOrderId() {
		return externalOrderId;
	}

	public void setExternalOrderId(String externalOrderId) {
		this.externalOrderId = externalOrderId;
	}

	public Boolean getPreliminary() {
		return preliminary;
	}

	public void setPreliminary(Boolean preliminary) {
		this.preliminary = preliminary;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}

	public String getPrescriberPhysicianCode() {
		return prescriberPhysicianCode;
	}

	public void setPrescriberPhysicianCode(String prescriberPhysicianCode) {
		this.prescriberPhysicianCode = prescriberPhysicianCode;
	}

	public String getPrescriberFirstName() {
		return prescriberFirstName;
	}

	public void setPrescriberFirstName(String prescriberFirstName) {
		this.prescriberFirstName = prescriberFirstName;
	}

	public String getPrescriberLastName() {
		return prescriberLastName;
	}

	public void setPrescriberLastName(String prescriberLastName) {
		this.prescriberLastName = prescriberLastName;
	}

	public String getPrescriberPhoneNumber() {
		return prescriberPhoneNumber;
	}

	public void setPrescriberPhoneNumber(String prescriberPhoneNumber) {
		this.prescriberPhoneNumber = prescriberPhoneNumber;
	}

	public String getPrescriberAddress() {
		return prescriberAddress;
	}

	public void setPrescriberAddress(String prescriberAddress) {
		this.prescriberAddress = prescriberAddress;
	}

	public String getPrescriberPostalCode() {
		return prescriberPostalCode;
	}

	public void setPrescriberPostalCode(String prescriberPostalCode) {
		this.prescriberPostalCode = prescriberPostalCode;
	}

	public String getPrescriberCity() {
		return prescriberCity;
	}

	public void setPrescriberCity(String prescriberCity) {
		this.prescriberCity = prescriberCity;
	}

	public CountryReferenceDto getPrescriberCountry() {
		return prescriberCountry;
	}

	public void setPrescriberCountry(CountryReferenceDto prescriberCountry) {
		this.prescriberCountry = prescriberCountry;
	}

	@Override
	public PathogenTestDto clone() throws CloneNotSupportedException {
		return (PathogenTestDto) super.clone();
	}
}

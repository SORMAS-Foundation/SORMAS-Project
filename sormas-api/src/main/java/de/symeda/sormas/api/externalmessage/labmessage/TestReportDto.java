package de.symeda.sormas.api.externalmessage.labmessage;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.Required;
import io.swagger.v3.oas.annotations.media.Schema;

@DependingOnFeatureType(featureType = FeatureType.EXTERNAL_MESSAGES)
@Schema(description = "Report of the test performed by the laboratory.")
public class TestReportDto extends EntityDto {

	private static final long serialVersionUID = 3377642632219354380L;

	public static final String I18N_PREFIX = "TestReport";

	public static final String TEST_LAB_NAME = "testLabName";
	public static final String TEST_LAB_EXTERNAL_ID = "testLabExternalId";
	public static final String TEST_LAB_POSTAL_CODE = "testLabPostalCode";
	public static final String TEST_LAB_CITY = "testLabCity";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String TEST_RESULT = "testResult";
	public static final String TEST_PCR_TEST_SPECIFICATION = "testPcrTestSpecification";

	@Required
	private SampleReportReferenceDto sampleReport;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Laboratory in which the test was performed.")
	private String testLabName;
	@Schema(description = "External IDs of the laboratory in which the test was performed.")
	private List<String> testLabExternalIds;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Postal code of the laboratory in which the test was performed.")
	private String testLabPostalCode;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "City of the laboratory in which the test was performed.")
	private String testLabCity;

	private PathogenTestType testType;
	@Schema(description = "Date and time when the test was performed.")
	private Date testDateTime;
	private PathogenTestResultType testResult;
	@Schema(description = "Whether the test result is verified by a lab supervisor.")
	private Boolean testResultVerified;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "Any additional information associated with this test.")
	private String testResultText;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private String typingId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private String externalId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private String externalOrderId;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	@Schema(description = "The disease variant for which this test is done.")
	private String testedDiseaseVariant;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	@Schema(description = "Any additional information associated with the disease variant.")
	private String testedDiseaseVariantDetails;
	@Schema(description = "Indicates whether this test result is preliminary.")
	private Boolean preliminary;
	private PCRTestSpecification testPcrTestSpecification;

	public SampleReportReferenceDto getSampleReport() {
		return sampleReport;
	}

	public void setSampleReport(SampleReportReferenceDto sampleReport) {
		this.sampleReport = sampleReport;
	}

	public String getTestLabName() {
		return testLabName;
	}

	public void setTestLabName(String testLabName) {
		this.testLabName = testLabName;
	}

	public List<String> getTestLabExternalIds() {
		return testLabExternalIds;
	}

	public void setTestLabExternalIds(List<String> testLabExternalIds) {
		this.testLabExternalIds = testLabExternalIds;
	}

	public String getTestLabPostalCode() {
		return testLabPostalCode;
	}

	public void setTestLabPostalCode(String testLabPostalCode) {
		this.testLabPostalCode = testLabPostalCode;
	}

	public String getTestLabCity() {
		return testLabCity;
	}

	public void setTestLabCity(String testLabCity) {
		this.testLabCity = testLabCity;
	}

	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	public Boolean isTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(Boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	public static TestReportDto build() {
		TestReportDto testResult = new TestReportDto();
		testResult.setUuid(DataHelper.createUuid());
		return testResult;
	}

	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
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

	public String getTestedDiseaseVariant() {
		return testedDiseaseVariant;
	}

	public void setTestedDiseaseVariant(String testedDiseaseVariant) {
		this.testedDiseaseVariant = testedDiseaseVariant;
	}

	public String getTestedDiseaseVariantDetails() {
		return testedDiseaseVariantDetails;
	}

	public void setTestedDiseaseVariantDetails(String testedDiseaseVariantDetails) {
		this.testedDiseaseVariantDetails = testedDiseaseVariantDetails;
	}

	public Boolean getPreliminary() {
		return preliminary;
	}

	public void setPreliminary(Boolean preliminary) {
		this.preliminary = preliminary;
	}

	public PCRTestSpecification getTestPcrTestSpecification() {
		return testPcrTestSpecification;
	}

	public void setTestPcrTestSpecification(PCRTestSpecification testPcrTestSpecification) {
		this.testPcrTestSpecification = testPcrTestSpecification;
	}
}

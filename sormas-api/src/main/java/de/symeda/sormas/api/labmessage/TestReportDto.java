package de.symeda.sormas.api.labmessage;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.Required;

import java.util.Date;

public class TestReportDto extends EntityDto {

	public static final String I18N_PREFIX = "TestResult";

	public static final String TEST_LAB_NAME = "testLabName";
	public static final String TEST_LAB_EXTERNAL_ID = "testLabExternalId";
	public static final String TEST_LAB_POSTAL_CODE = "testLabPostalCode";
	public static final String TEST_LAB_CITY = "testLabCity";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String TEST_RESULT = "testResult";
	public static final String PATHOGEN_TEST = "pathogenTest";

	@Required
	private LabMessageReferenceDto labMessage;
	private String testLabName;
	private String testLabExternalId;
	private String testLabPostalCode;
	private String testLabCity;

	private PathogenTestType testType;
	private Date testDateTime;
	private PathogenTestResultType testResult;
	private Boolean testResultVerified;
	private String testResultText;
	private String typingId;

	private PathogenTestReferenceDto pathogenTest;

	public LabMessageReferenceDto getLabMessage() {
		return labMessage;
	}

	public void setLabMessage(LabMessageReferenceDto labMessage) {
		this.labMessage = labMessage;
	}

	public String getTestLabName() {
		return testLabName;
	}

	public void setTestLabName(String testLabName) {
		this.testLabName = testLabName;
	}

	public String getTestLabExternalId() {
		return testLabExternalId;
	}

	public void setTestLabExternalId(String testLabExternalId) {
		this.testLabExternalId = testLabExternalId;
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

	public PathogenTestReferenceDto getPathogenTest() {
		return pathogenTest;
	}

	public void setPathogenTest(PathogenTestReferenceDto pathogenTest) {
		this.pathogenTest = pathogenTest;
	}

	public String getTypingId() {
		return typingId;
	}

	public void setTypingId(String typingId) {
		this.typingId = typingId;
	}
}

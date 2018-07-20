package de.symeda.sormas.api.sample;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Required;

public class SampleTestDto extends EntityDto {

	private static final long serialVersionUID = -5213210080802372054L;

	public static final String I18N_PREFIX = "SampleTest";

	public static final String SAMPLE = "sample";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_TYPE_TEXT = "testTypeText";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String LAB = "lab";
	public static final String LAB_DETAILS = "labDetails";
	public static final String LAB_USER = "labUser";
	public static final String TEST_RESULT = "testResult";
	public static final String TEST_RESULT_TEXT = "testResultText";
	public static final String TEST_RESULT_VERIFIED = "testResultVerified";
	public static final String FOUR_FOLD_INCREASE_ANTIBODY_TITER = "fourFoldIncreaseAntibodyTiter";

	@Required
	private SampleReferenceDto sample;
	@Required
	private SampleTestType testType;
	private String testTypeText;
	@Required
	private Date testDateTime;
	@Required
	private FacilityReferenceDto lab;
	private String labDetails;
	@Required
	private UserReferenceDto labUser;
	@Required
	private SampleTestResultType testResult;
	@Required
	private String testResultText;
	private boolean testResultVerified;
	private boolean fourFoldIncreaseAntibodyTiter;

	public SampleReferenceDto getSample() {
		return sample;
	}

	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}

	public SampleTestType getTestType() {
		return testType;
	}

	public void setTestType(SampleTestType testType) {
		this.testType = testType;
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

	public SampleTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(SampleTestResultType testResult) {
		this.testResult = testResult;
	}

	public String getTestResultText() {
		return testResultText;
	}

	public void setTestResultText(String testResultText) {
		this.testResultText = testResultText;
	}

	public boolean isTestResultVerified() {
		return testResultVerified;
	}

	public void setTestResultVerified(boolean testResultVerified) {
		this.testResultVerified = testResultVerified;
	}

	public boolean isFourFoldIncreaseAntibodyTiter() {
		return fourFoldIncreaseAntibodyTiter;
	}

	public void setFourFoldIncreaseAntibodyTiter(boolean fourFoldIncreaseAntibodyTiter) {
		this.fourFoldIncreaseAntibodyTiter = fourFoldIncreaseAntibodyTiter;
	}

	public SampleTestReferenceDto toReference() {
		return new SampleTestReferenceDto(getUuid());
	}

}

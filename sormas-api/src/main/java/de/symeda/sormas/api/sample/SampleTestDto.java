package de.symeda.sormas.api.sample;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class SampleTestDto extends SampleTestReferenceDto {

	private static final long serialVersionUID = -5213210080802372054L;

	public static final String I18N_PREFIX = "SampleTest";
	
	public static final String SAMPLE = "sample";
	public static final String TEST_TYPE = "testType";
	public static final String TEST_TYPE_TEXT = "testTypeText";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String LAB = "lab";
	public static final String LAB_USER = "labUser";
	public static final String TEST_RESULT = "testResult";
	public static final String TEST_RESULT_TEXT = "testResultText";
	public static final String TEST_RESULT_VERIFIED = "testResultVerified";
	
	private SampleReferenceDto sample;
	private SampleTestType testType;
	private String testTypeText;
	private Date testDateTime;
	private FacilityReferenceDto lab;
	private UserReferenceDto labUser;
	private SampleTestResultType testResult;
	private String testResultText;
	private boolean testResultVerified;
	
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
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getTestDateTime() {
		return testDateTime;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}
	public FacilityReferenceDto getLab() {
		return lab;
	}
	public void setLab(FacilityReferenceDto lab) {
		this.lab = lab;
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleTestDto other = (SampleTestDto) obj;
		if (lab == null) {
			if (other.lab != null)
				return false;
		} else if (!lab.equals(other.lab))
			return false;
		if (labUser == null) {
			if (other.labUser != null)
				return false;
		} else if (!labUser.equals(other.labUser))
			return false;
		if (sample == null) {
			if (other.sample != null)
				return false;
		} else if (!sample.equals(other.sample))
			return false;
		if (testDateTime == null) {
			if (other.testDateTime != null)
				return false;
		} else if (!testDateTime.equals(other.testDateTime))
			return false;
		if (testResult != other.testResult)
			return false;
		if (testResultText == null) {
			if (other.testResultText != null)
				return false;
		} else if (!testResultText.equals(other.testResultText))
			return false;
		if (testResultVerified != other.testResultVerified)
			return false;
		if (testType != other.testType)
			return false;
		if (testTypeText == null) {
			if (other.testTypeText != null)
				return false;
		} else if (!testTypeText.equals(other.testTypeText))
			return false;
		return true;
	}
	
}

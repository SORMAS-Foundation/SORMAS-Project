package de.symeda.sormas.api.sample;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;

public class DashboardTestResultDto implements Serializable {

	private static final long serialVersionUID = -6488827968218301232L;

	public static final String I18N_PREFIX = "SampleTest";

	public static final String TEST_RESULT = "testResult";
	public static final String DISEASE = "disease";
	
	private Disease disease;
	private SampleTestResultType testResult;
	
	public DashboardTestResultDto(Disease disease, SampleTestResultType testResult) {
		this.disease = disease;
		this.testResult = testResult;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public SampleTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(SampleTestResultType testResult) {
		this.testResult = testResult;
	}
	
}

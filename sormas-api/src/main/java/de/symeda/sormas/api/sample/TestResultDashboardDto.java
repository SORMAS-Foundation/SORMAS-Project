package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;

public class TestResultDashboardDto extends DataTransferObject {

	private static final long serialVersionUID = -6488827968218301232L;

	public static final String I18N_PREFIX = "SampleTest";

	public static final String TEST_RESULT = "testResult";
	public static final String DISEASE = "disease";
	public static final String SHIPPED = "shipped";
	public static final String RECEIVED = "received";
	
	private Disease disease;
	private boolean shipped;
	private boolean received;
	private SampleTestResultType testResult;
	
	public TestResultDashboardDto(String uuid, Disease disease, boolean shipped, boolean received, SampleTestResultType testResult) {
		setUuid(uuid);
		this.disease = disease;
		this.shipped = shipped;
		this.received = received;
		this.testResult = testResult;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public boolean isShipped() {
		return shipped;
	}

	public void setShipped(boolean shipped) {
		this.shipped = shipped;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	public SampleTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(SampleTestResultType testResult) {
		this.testResult = testResult;
	}
	
}

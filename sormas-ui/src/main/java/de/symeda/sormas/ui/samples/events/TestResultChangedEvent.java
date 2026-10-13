package de.symeda.sormas.ui.samples.events;

import de.symeda.sormas.api.sample.PathogenTestResultType;

public class TestResultChangedEvent {

	private final PathogenTestResultType testResult;

	public TestResultChangedEvent(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	public PathogenTestResultType getTestResult() {
		return testResult;
	}
}

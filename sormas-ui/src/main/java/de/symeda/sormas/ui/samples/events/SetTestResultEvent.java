package de.symeda.sormas.ui.samples.events;

import de.symeda.sormas.api.sample.PathogenTestResultType;

/**
 * Command event: requests that the test result field be set to a specific value.
 * Distinct from {@link TestResultChangedEvent}, which notifies that the value has already changed.
 * A {@code null} test result means "clear the field".
 */
public class SetTestResultEvent {

	private final PathogenTestResultType testResult;

	public SetTestResultEvent(PathogenTestResultType testResult) {
		this.testResult = testResult;
	}

	public PathogenTestResultType getTestResult() {
		return testResult;
	}
}

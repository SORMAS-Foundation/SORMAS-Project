package de.symeda.sormas.ui.samples.events;

import de.symeda.sormas.api.sample.PathogenTestType;

public class TestTypeChangedEvent {

	private final PathogenTestType testType;

	public TestTypeChangedEvent(PathogenTestType testType) {
		this.testType = testType;
	}

	public PathogenTestType getTestType() {
		return testType;
	}
}

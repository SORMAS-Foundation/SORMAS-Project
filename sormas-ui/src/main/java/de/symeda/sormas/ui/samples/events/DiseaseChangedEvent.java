package de.symeda.sormas.ui.samples.events;

import de.symeda.sormas.api.Disease;

public class DiseaseChangedEvent {

	private final Disease disease;

	public DiseaseChangedEvent(Disease disease) {
		this.disease = disease;
	}

	public Disease getDisease() {
		return disease;
	}
}

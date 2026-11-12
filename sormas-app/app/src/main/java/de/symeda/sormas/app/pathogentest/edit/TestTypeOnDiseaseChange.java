package de.symeda.sormas.app.pathogentest.edit;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestType;

final class TestTypeOnDiseaseChange {

	private TestTypeOnDiseaseChange() {
	}

	static PathogenTestType methodToKeep(Disease originalDisease, Disease selectedDisease, PathogenTestType currentMethod) {
		return selectedDisease == originalDisease ? currentMethod : null;
	}
}

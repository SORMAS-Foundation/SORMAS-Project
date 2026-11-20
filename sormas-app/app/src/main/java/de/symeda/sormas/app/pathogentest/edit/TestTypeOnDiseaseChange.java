package de.symeda.sormas.app.pathogentest.edit;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestType;

final class TestTypeOnDiseaseChange {

	private TestTypeOnDiseaseChange() {
	}

	static PathogenTestType methodToKeep(Disease originalDisease, Disease selectedDisease, PathogenTestType currentMethod) {
		// Keeps the saved method listed only while the tested disease is unchanged
		return selectedDisease == originalDisease ? currentMethod : null;
	}
}

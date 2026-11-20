package de.symeda.sormas.api.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class VaccinationStatusTest {

	@Test
	void selectableValuesHaveUniqueCaptions() {
		List<VaccinationStatus> selectable = VaccinationStatus.selectableValues();
		List<String> captions = selectable.stream().map(VaccinationStatus::toString).collect(Collectors.toList());

		assertFalse(selectable.contains(VaccinationStatus.RECOVERED));
		assertEquals(captions.size(), captions.stream().distinct().count());
	}

	@Test
	void hadTheDiseaseMatchesLegacyRecovered() {
		assertEquals(
			List.of(VaccinationStatus.HAD_THE_DISEASE, VaccinationStatus.RECOVERED),
			VaccinationStatus.HAD_THE_DISEASE.matchingStatuses());
		assertEquals(
			List.of(VaccinationStatus.HAD_THE_DISEASE, VaccinationStatus.RECOVERED),
			VaccinationStatus.RECOVERED.matchingStatuses());
		assertEquals(List.of(VaccinationStatus.VACCINATED), VaccinationStatus.VACCINATED.matchingStatuses());
	}
}

package de.symeda.sormas.api.importexport.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class CaseTemplateLayoutTest {

	@Test
	void mapsTemplateEntitiesToLevels() {
		assertEquals(Optional.of("case"), CaseTemplateLayout.levelOf("CaseData"));
		assertEquals(Optional.of("case"), CaseTemplateLayout.levelOf("Person"));
		assertEquals(Optional.of("case"), CaseTemplateLayout.levelOf("Location"));
		assertEquals(Optional.of("case"), CaseTemplateLayout.levelOf("Symptoms"));
		assertEquals(Optional.of("sample"), CaseTemplateLayout.levelOf("Sample"));
		assertEquals(Optional.of("test"), CaseTemplateLayout.levelOf("PathogenTest"));
		assertEquals(Optional.of("test"), CaseTemplateLayout.levelOf("DrugSusceptibility"));
		assertEquals(Optional.empty(), CaseTemplateLayout.levelOf("Vaccination"));
		assertEquals(Optional.empty(), CaseTemplateLayout.levelOf(""));
	}

	@Test
	void joinsAndSplitsHeadersLosslessly() {
		assertEquals("PathogenTest.lab", CaseTemplateLayout.join("PathogenTest", "lab"));
		assertEquals("Error description", CaseTemplateLayout.join("", "Error description"));
		String header = CaseTemplateLayout.join("DrugSusceptibility", "drugSusceptibility.amikacinMic");
		assertEquals("DrugSusceptibility", CaseTemplateLayout.entityOf(header));
		assertEquals("drugSusceptibility.amikacinMic", CaseTemplateLayout.propertyOf(header));
		assertEquals("", CaseTemplateLayout.entityOf("Error description"));
		assertEquals("Error description", CaseTemplateLayout.propertyOf("Error description"));
	}
}

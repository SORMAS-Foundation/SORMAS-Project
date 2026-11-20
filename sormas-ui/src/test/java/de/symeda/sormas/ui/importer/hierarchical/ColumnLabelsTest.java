package de.symeda.sormas.ui.importer.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

class ColumnLabelsTest {

	private final ColumnLabels labels =
		ColumnLabels.of(List.of("CaseData.healthFacility", "Person.person.birthdateDD", "Person.person.birthdateMM", "Sample.lab", "PathogenTest.lab"));

	@Test
	void showsTheFileHeaderWithoutItsEntity() {
		assertEquals("healthFacility", labels.label("CaseData.healthFacility"));
	}

	@Test
	void keepsTheEntityWhenTheFileHeaderIsAmbiguous() {
		assertEquals("Sample.lab", labels.label("Sample.lab"));
		assertEquals("PathogenTest.lab", labels.label("PathogenTest.lab"));
	}

	@Test
	void labelsEachColumnOfAMultiColumnProblem() {
		assertEquals("person.birthdateDD, person.birthdateMM", labels.label("Person.person.birthdateDD, Person.person.birthdateMM"));
	}

	@Test
	void leavesUnknownAndMissingColumnsAlone() {
		assertEquals("CaseData.reportingUser", labels.label("CaseData.reportingUser"));
		assertEquals("", labels.label(""));
		assertNull(labels.label(null));
	}
}

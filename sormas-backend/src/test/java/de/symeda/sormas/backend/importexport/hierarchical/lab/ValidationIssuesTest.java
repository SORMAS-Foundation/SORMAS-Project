package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;

class ValidationIssuesTest {

	@Test
	void reportsEachFieldErrorWithItsColumn() {
		Map<List<String>, String> errors = new LinkedHashMap<>();
		errors.put(List.of("lastName"), "You have to specify a last name");
		errors.put(List.of("firstName"), "You have to specify a first name");
		ImportNode node = node();

		ValidationIssues.report(node, LabImportSchema.PERSON, new ValidationRuntimeException(errors));

		assertEquals(2, node.issues().size());
		assertEquals("person.firstName", node.issues().get(0).getColumn());
		assertEquals("You have to specify a first name", node.issues().get(0).getMessage());
		assertEquals("person.lastName", node.issues().get(1).getColumn());
	}

	@Test
	void keepsAPlainMessageWithoutAColumn() {
		ImportNode node = node();

		ValidationIssues.report(node, LabImportSchema.CASE, new ValidationRuntimeException("You have to specify a valid disease"));

		assertEquals(1, node.issues().size());
		assertNull(node.issues().get(0).getColumn());
		assertEquals("You have to specify a valid disease", node.issues().get(0).getMessage());
	}

	private static ImportNode node() {
		return new ImportNode(LabImportSchema.CASE_LEVEL, 2, Map.of(LabImportSchema.CASE_EXTERNAL_ID, "NI-001"));
	}
}

package de.symeda.sormas.backend.importexport.hierarchical.caze;

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

		ValidationIssues.report(node, CaseTemplateSchema.PERSON, new ValidationRuntimeException(errors));

		assertEquals(2, node.issues().size());
		assertEquals("person.firstName", node.issues().get(0).getColumn());
		assertEquals("You have to specify a first name", node.issues().get(0).getMessage());
		assertEquals("person.lastName", node.issues().get(1).getColumn());
	}

	@Test
	void namesAFieldErrorByTheFileHeaderOfItsCell() {
		ImportNode node = new ImportNode(
			CaseTemplateSchema.CASE_LEVEL,
			2,
			Map.of("Person.person.firstName", "Jean", "Symptoms.symptoms.otherNonHemorrhagicSymptomsText", "cough"));

		ValidationIssues.report(node, CaseTemplateSchema.PERSON, new ValidationRuntimeException(Map.of(List.of("firstName"), "too long")));
		ValidationIssues.report(
			node,
			CaseTemplateSchema.CASE,
			new ValidationRuntimeException(Map.of(List.of("symptoms", "otherNonHemorrhagicSymptomsText"), "too long")));

		assertEquals("Person.person.firstName", node.issues().get(0).getColumn());
		assertEquals("Symptoms.symptoms.otherNonHemorrhagicSymptomsText", node.issues().get(1).getColumn());
	}

	@Test
	void keepsAPlainMessageWithoutAColumn() {
		ImportNode node = node();

		ValidationIssues.report(node, CaseTemplateSchema.CASE, new ValidationRuntimeException("You have to specify a valid disease"));

		assertEquals(1, node.issues().size());
		assertNull(node.issues().get(0).getColumn());
		assertEquals("You have to specify a valid disease", node.issues().get(0).getMessage());
	}

	private static ImportNode node() {
		return new ImportNode(CaseTemplateSchema.CASE_LEVEL, 2, Map.of(CaseTemplateSchema.EPID_NUMBER, "NI-001"));
	}
}

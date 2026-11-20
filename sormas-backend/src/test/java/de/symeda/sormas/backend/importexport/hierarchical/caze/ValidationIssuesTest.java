package de.symeda.sormas.backend.importexport.hierarchical.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.util.ValidationProblem;

class ValidationIssuesTest {

	private static final ColumnHeaders NO_HEADERS = ColumnHeaders.of(List.of());
	private static final ColumnHeaders TEMPLATE_HEADERS = ColumnHeaders.of(
		Stream
			.of(
				"CaseData.responsibleRegion",
				"CaseData.responsibleDistrict",
				"CaseData.healthFacility",
				"Person.person.address.region",
				"Person.person.firstName",
				"Person.person.lastName")
			.map(header -> CaseTemplateSchema.resolve(header).orElseThrow())
			.collect(Collectors.toList()));

	@Test
	void reportsEachFieldErrorWithItsColumn() {
		Map<List<String>, String> errors = new LinkedHashMap<>();
		errors.put(List.of("lastName"), "You have to specify a last name");
		errors.put(List.of("firstName"), "You have to specify a first name");
		ImportNode node = node();

		ValidationIssues.report(node, CaseTemplateSchema.PERSON, new ValidationRuntimeException(errors), TEMPLATE_HEADERS);

		assertEquals(2, node.issues().size());
		assertEquals("Person.person.firstName", node.issues().get(0).getColumn());
		assertEquals("You have to specify a first name", node.issues().get(0).getMessage());
		assertEquals("Person.person.lastName", node.issues().get(1).getColumn());
	}

	@Test
	void namesAFieldErrorOnANestedPropertyByItsColumn() {
		ImportNode node = node();
		ColumnHeaders headers = ColumnHeaders.of(List.of(CaseTemplateSchema.resolve("Symptoms.symptoms.otherNonHemorrhagicSymptomsText").orElseThrow()));

		ValidationIssues.report(
			node,
			CaseTemplateSchema.CASE,
			new ValidationRuntimeException(Map.of(List.of("symptoms", "otherNonHemorrhagicSymptomsText"), "too long")),
			headers);

		assertEquals("Symptoms.symptoms.otherNonHemorrhagicSymptomsText", node.issues().get(0).getColumn());
	}

	@Test
	void leavesAFieldErrorOnAPropertyWithoutAColumnWithoutAColumn() {
		ImportNode node = node();

		ValidationIssues.report(
			node,
			CaseTemplateSchema.CASE,
			new ValidationRuntimeException(Map.of(List.of("reportingUser"), "You have to specify a reporting user")),
			TEMPLATE_HEADERS);

		assertEquals(1, node.issues().size());
		assertNull(node.issues().get(0).getColumn());
		assertEquals("You have to specify a reporting user", node.issues().get(0).getMessage());
	}

	@Test
	void keepsAPlainMessageWithoutAColumn() {
		ImportNode node = node();

		ValidationIssues.report(node, CaseTemplateSchema.CASE, new ValidationRuntimeException("You have to specify a valid disease"), NO_HEADERS);

		assertEquals(1, node.issues().size());
		assertNull(node.issues().get(0).getColumn());
		assertEquals("You have to specify a valid disease", node.issues().get(0).getMessage());
	}

	@Test
	void namesAProblemByTheTemplateHeaderEvenWhenItsCellIsEmpty() {
		ImportNode node = node();

		ValidationIssues.report(
			node,
			CaseTemplateSchema.PERSON,
			List.of(new ValidationProblem(null, "region missing", "address.region")),
			TEMPLATE_HEADERS);

		assertEquals("Person.person.address.region", node.issues().get(0).getColumn());
	}

	@Test
	void joinsSeveralColumnsOfOneProblem() {
		ImportNode node = node();

		ValidationIssues.report(
			node,
			CaseTemplateSchema.CASE,
			List.of(new ValidationProblem(null, "not in region", "responsibleDistrict", "responsibleRegion")),
			TEMPLATE_HEADERS);

		assertEquals(1, node.issues().size());
		assertEquals("CaseData.responsibleDistrict, CaseData.responsibleRegion", node.issues().get(0).getColumn());
		assertEquals("not in region", node.issues().get(0).getMessage());
	}

	@Test
	void keepsTheProblemOrderAndLeavesAnUnknownPropertyWithoutAColumn() {
		ImportNode node = node();

		ValidationIssues.report(
			node,
			CaseTemplateSchema.CASE,
			List.of(new ValidationProblem(null, "second", "responsibleRegion"), new ValidationProblem(null, "first", "notInTemplate")),
			TEMPLATE_HEADERS);

		assertEquals("CaseData.responsibleRegion", node.issues().get(0).getColumn());
		assertNull(node.issues().get(1).getColumn());
		assertEquals("first", node.issues().get(1).getMessage());
	}

	@Test
	void dropsAProblemOnAColumnThatAlreadyHasACellIssue() {
		ImportNode node = node();
		node.reportIssue("CaseData.healthFacility", "No such facility");

		ValidationIssues.report(
			node,
			CaseTemplateSchema.CASE,
			List.of(new ValidationProblem(null, "facility missing", "healthFacility"), new ValidationProblem(null, "region missing", "responsibleRegion")),
			TEMPLATE_HEADERS);

		assertEquals(2, node.issues().size());
		assertEquals("No such facility", node.issues().get(0).getMessage());
		assertEquals("region missing", node.issues().get(1).getMessage());
	}

	@Test
	void keepsAProblemOnSeveralColumnsWhenOnlySomeOfThemHaveAnIssue() {
		ImportNode node = node();
		node.reportIssue("CaseData.responsibleRegion", "No such region");

		ValidationIssues.report(
			node,
			CaseTemplateSchema.CASE,
			List.of(new ValidationProblem(null, "not in region", "responsibleDistrict", "responsibleRegion")),
			TEMPLATE_HEADERS);

		assertEquals(2, node.issues().size());
		assertEquals("CaseData.responsibleDistrict, CaseData.responsibleRegion", node.issues().get(1).getColumn());
	}

	@Test
	void dropsAProblemWhenEachOfItsColumnsAlreadyHasAnIssue() {
		ImportNode node = node();
		node.reportIssue("CaseData.responsibleDistrict, CaseData.responsibleRegion", "not in region");

		ValidationIssues.report(
			node,
			CaseTemplateSchema.CASE,
			List.of(
				new ValidationProblem(null, "district problem", "responsibleDistrict"),
				new ValidationProblem(null, "region problem", "responsibleRegion")),
			TEMPLATE_HEADERS);

		assertEquals(1, node.issues().size());
	}

	@Test
	void keepsAProblemWithoutAColumnBesideAColumnIssue() {
		ImportNode node = node();
		node.reportIssue("CaseData.healthFacility", "No such facility");

		ValidationIssues.report(node, CaseTemplateSchema.CASE, List.of(new ValidationProblem(null, "no reporting user", "reportingUser")), TEMPLATE_HEADERS);

		assertEquals(2, node.issues().size());
		assertNull(node.issues().get(1).getColumn());
	}

	@Test
	void keepsAProblemWithoutAPropertyPathWithoutAColumn() {
		ImportNode node = node();

		ValidationIssues.report(node, CaseTemplateSchema.PERSON, List.of(new ValidationProblem(null, "invalid birth date")), TEMPLATE_HEADERS);

		assertNull(node.issues().get(0).getColumn());
	}

	private static ImportNode node() {
		return new ImportNode(CaseTemplateSchema.CASE_LEVEL, 2, Map.of(CaseTemplateSchema.EPID_NUMBER, "NI-001"));
	}
}

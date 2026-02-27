package de.symeda.sormas.backend.patch.alias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.AbstractUnitTest;

class PathAliasHelperTest extends AbstractUnitTest {

	private final PathAliasHelper victim = new PathAliasHelper();

	@Test
	void resolveAlias_noAlias_noDot_returnsOriginalPath() {
		// PREPARE
		String path = "some.field";

		// EXECUTE
		Tuple<String, DataPatchFailureCause> result = victim.resolveAlias(path);

		// CHECK
		assertEquals(path, result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_validAlias_caseDataPerson() {
		// PREPARE
		String aliasPath = "CaseData.person.firstName";

		// EXECUTE
		Tuple<String, DataPatchFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertEquals("Person.firstName", result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_validAlias_symptoms() {
		// PREPARE
		String aliasPath = "Symptoms.cough";

		// EXECUTE
		Tuple<String, DataPatchFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertEquals("CaseData.symptoms.cough", result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_validAlias_nestedPreviousHospitalization() {
		// PREPARE
		String aliasPath = "PreviousHospitalization.admissionDate";

		// EXECUTE
		Tuple<String, DataPatchFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertEquals("CaseData.hospitalization.previousHospitalizations.admissionDate", result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_unknownAlias_returnsOriginalPath() {
		// PREPARE
		String aliasPath = "UnknownAlias.field";

		// EXECUTE
		Tuple<String, DataPatchFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertEquals(aliasPath, result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_forbiddenCollision_location() {
		// PREPARE
		String aliasPath = "Location.region";

		// EXECUTE
		Tuple<String, DataPatchFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertNull(result.getFirst());
		assertEquals(DataPatchFailureCause.FORBIDDEN_NON_UNIQUE_ALIAS, result.getSecond());
	}

	@Test
	void resolveAlias_forbiddenCollision_address() {
		// PREPARE
		String aliasPath = "Location.street";

		// EXECUTE
		Tuple<String, DataPatchFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertNull(result.getFirst());
		assertEquals(DataPatchFailureCause.FORBIDDEN_NON_UNIQUE_ALIAS, result.getSecond());
	}

	@Test
	void resolveAlias_noDotInPath_returnsOriginal() {
		// PREPARE
		String path = "justAlias";

		// EXECUTE
		Tuple<String, DataPatchFailureCause> result = victim.resolveAlias(path);

		// CHECK
		assertEquals(path, result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_multipleDots_usesFirstDot() {
		// PREPARE
		String aliasPath = "Facility.name.somethingElse";

		// EXECUTE
		Tuple<String, DataPatchFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertEquals("CaseData.healthFacility.name.somethingElse", result.getFirst());
		assertNull(result.getSecond());
	}
}

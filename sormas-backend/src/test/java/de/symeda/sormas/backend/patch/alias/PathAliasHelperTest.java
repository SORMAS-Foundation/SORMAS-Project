package de.symeda.sormas.backend.patch.alias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.patch.PathFailureCause;

class PathAliasHelperTest extends AbstractUnitTest {

	private final PathAliasHelper victim = new PathAliasHelper();

	@Test
	void resolveAlias_noAlias_noDot_returnsOriginalPath() {
		// PREPARE
		String path = "some.field";

		// EXECUTE
		Tuple<String, PathFailureCause> result = victim.resolveAlias(path);

		// CHECK
		assertEquals(path, result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_validAlias_caseDataPerson() {
		// PREPARE
		String aliasPath = "CaseData.person.firstName";

		// EXECUTE
		Tuple<String, PathFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertEquals("Person.firstName", result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_validAlias_symptoms() {
		// PREPARE
		String aliasPath = "Symptoms.cough";

		// EXECUTE
		Tuple<String, PathFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertEquals("CaseData.symptoms.cough", result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_unknownAlias_returnsOriginalPath() {
		// PREPARE
		String aliasPath = "UnknownAlias.field";

		// EXECUTE
		Tuple<String, PathFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertEquals(aliasPath, result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_forbiddenCollision_location() {
		// PREPARE
		String aliasPath = "Location.region";

		// EXECUTE
		Tuple<String, PathFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertNull(result.getFirst());
		assertEquals(PathFailureCause.FORBIDDEN_NON_UNIQUE_ALIAS, result.getSecond());
	}

	@Test
	void resolveAlias_forbiddenCollision_address() {
		// PREPARE
		String aliasPath = "Location.street";

		// EXECUTE
		Tuple<String, PathFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertNull(result.getFirst());
		assertEquals(PathFailureCause.FORBIDDEN_NON_UNIQUE_ALIAS, result.getSecond());
	}

	@Test
	void resolveAlias_noDotInPath_returnsOriginal() {
		// PREPARE
		String path = "justAlias";

		// EXECUTE
		Tuple<String, PathFailureCause> result = victim.resolveAlias(path);

		// CHECK
		assertEquals(path, result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void resolveAlias_multipleDots_usesFirstDot() {
		// PREPARE
		String aliasPath = "Facility.name.somethingElse";

		// EXECUTE
		Tuple<String, PathFailureCause> result = victim.resolveAlias(aliasPath);

		// CHECK
		assertEquals("CaseData.healthFacility.name.somethingElse", result.getFirst());
		assertNull(result.getSecond());
	}

	@Test
	void toAliasPath_symptomsPath_isMappedToSymptomsAlias() {
		// PREPARE
		String pathWithoutAlias = "CaseData.symptoms.cough";

		// EXECUTE
		String result = victim.toAliasPath(pathWithoutAlias);

		// CHECK
		assertEquals("Symptoms.cough", result);
	}

	@Test
	void toAliasPath_healthFacility_isMappedToFacilityAlias() {
		// PREPARE
		String pathWithoutAlias = "CaseData.healthFacility.name";

		// EXECUTE
		String result = victim.toAliasPath(pathWithoutAlias);

		// CHECK
		assertEquals("Facility.name", result);
	}

	@Test
	void toAliasPath_birthCountry_isMappedToCountryAlias() {
		// PREPARE
		String pathWithoutAlias = "Person.birthCountry.name";

		// EXECUTE
		String result = victim.toAliasPath(pathWithoutAlias);

		// CHECK
		assertEquals("Country.name", result);
	}

	@Test
	void toAliasPath_addressSubcontinent_isMappedToSubcontinentAlias() {
		// PREPARE
		String pathWithoutAlias = "Person.address.subcontinent";

		// EXECUTE
		String result = victim.toAliasPath(pathWithoutAlias);

		// CHECK
		assertEquals("Location.subcontinent", result);
	}

	@Test
	void toAliasPath_addressContinent_isMappedToContinentAlias() {
		// PREPARE
		String pathWithoutAlias = "Person.address.continent";

		// EXECUTE
		String result = victim.toAliasPath(pathWithoutAlias);

		// CHECK
		assertEquals("Location.continent", result);
	}

	@Test
	void toAliasPath_locationForbiddenAliases_areMappedToLocationAlias() {
		// PREPARE
		String personAddressPath = "Person.address";
		String exposureLocationPath = "Exposure.location";

		// EXECUTE
		String personResult = victim.toAliasPath(personAddressPath);
		String exposureResult = victim.toAliasPath(exposureLocationPath);

		// CHECK
		assertEquals("Location", personResult);
		assertEquals("Location", exposureResult);
	}

	@Test
	void toAliasPath_unknownPath_isReturnedUnchanged() {
		// PREPARE
		String pathWithoutAlias = "SomeUnknown.path";

		// EXECUTE
		String result = victim.toAliasPath(pathWithoutAlias);

		// CHECK
		assertEquals(pathWithoutAlias, result);
	}
}

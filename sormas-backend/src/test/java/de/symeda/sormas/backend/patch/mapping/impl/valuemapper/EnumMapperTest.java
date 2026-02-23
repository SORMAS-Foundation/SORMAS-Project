package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import de.symeda.sormas.api.caze.InfectionSetting;
import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.AbstractUnitTest;

class EnumMapperTest extends AbstractUnitTest {

	@InjectMocks
	private EnumMapper victim;

	// getSupportedTypes

	@Test
	void getSupportedTypes_containsEnumClass() {
		// PREPARE
		Set<Class<?>> expected = Set.of(Enum.class);

		// EXECUTE
		Set<Class<?>> actual = victim.getSupportedTypes();

		// CHECK
		assertEquals(expected, actual);
	}

	// map - exact match (Sex enum, no OTHER/fallback ambiguity)

	@Test
	void map_sex_exactMatch_male() {
		// EXECUTE & CHECK
		assertEquals(Sex.MALE, victim.map("MALE", Sex.class));
	}

	@Test
	void map_sex_exactMatch_female() {
		// EXECUTE & CHECK
		assertEquals(Sex.FEMALE, victim.map("FEMALE", Sex.class));
	}

	@Test
	void map_sex_exactMatch_unknown() {
		// EXECUTE & CHECK
		assertEquals(Sex.UNKNOWN, victim.map("UNKNOWN", Sex.class));
	}

	@Test
	void map_sex_caseInsensitive_lowercase() {
		// EXECUTE & CHECK
		assertEquals(Sex.MALE, victim.map("male", Sex.class));
	}

	@Test
	void map_sex_caseInsensitive_mixedCase() {
		// EXECUTE & CHECK
		assertEquals(Sex.FEMALE, victim.map("fEmAlE", Sex.class));
	}

	@Test
	void map_sex_trimsWhitespace() {
		// EXECUTE & CHECK
		assertEquals(Sex.MALE, victim.map("  MALE  ", Sex.class));
	}

	// map - OTHER fallback (Sex has OTHER constant)

	@Test
	void map_sex_unknownValue_fallsBackToOther() {
		// EXECUTE & CHECK
		assertEquals(Sex.OTHER, victim.map("SOMETHING_UNKNOWN", Sex.class));
	}

	// map - @ValueMapperDefault fallback (InfectionSetting, no OTHER constant)

	@Test
	void map_infectionSetting_exactMatch_ambulatory() {
		// EXECUTE & CHECK
		assertEquals(InfectionSetting.AMBULATORY, victim.map("AMBULATORY", InfectionSetting.class));
	}

	@Test
	void map_infectionSetting_exactMatch_normalWard() {
		// EXECUTE & CHECK
		assertEquals(InfectionSetting.NORMAL_WARD, victim.map("NORMAL_WARD", InfectionSetting.class));
	}

	@Test
	void map_infectionSetting_unknownValue_fallsBackToAnnotatedDefault() {
		// EXECUTE & CHECK
		assertEquals(InfectionSetting.UNKNOWN, victim.map("SOMETHING_UNKNOWN", InfectionSetting.class));
	}

	// map - @ValueMapperDefault fallback (Trimester, no OTHER constant)

	@Test
	void map_trimester_exactMatch_first() {
		// EXECUTE & CHECK
		assertEquals(Trimester.FIRST, victim.map("FIRST", Trimester.class));
	}

	@Test
	void map_trimester_exactMatch_second() {
		// EXECUTE & CHECK
		assertEquals(Trimester.SECOND, victim.map("SECOND", Trimester.class));
	}

	@Test
	void map_trimester_exactMatch_third() {
		// EXECUTE & CHECK
		assertEquals(Trimester.THIRD, victim.map("THIRD", Trimester.class));
	}

	@Test
	void map_trimester_unknownValue_fallsBackToAnnotatedDefault() {
		// EXECUTE & CHECK
		assertEquals(Trimester.UNKNOWN, victim.map("SOMETHING_UNKNOWN", Trimester.class));
	}

	// map - no match, no OTHER, no @ValueMapperDefault → exception

	@Test
	void map_noFallback_throwsEnumConstantNotPresentException() {
		// PREPARE
		// Direction has no OTHER constant and no @ValueMapperDefault annotation

		// EXECUTE & CHECK
		assertThrows(EnumConstantNotPresentException.class, () -> victim.map("SOMETHING_UNKNOWN", NoFallbackEnum.class));
	}

	// map - null input

	@Test
	void map_nullValue_throwsNullPointerException() {
		// EXECUTE & CHECK
		assertThrows(NullPointerException.class, () -> victim.map(null, Sex.class));
	}

	private enum NoFallbackEnum {
		NORTH,
		SOUTH,
		EAST,
		WEST
	}
}

package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.backend.AbstractUnitTest;

class PrimitiveMapperTest extends AbstractUnitTest {

	@InjectMocks
	private PrimitivePatchMapper victim;

	@Test
	void map_string() {
		// PREPARE
		String expected = "toto";
		// EXECUTE & CHECK
		assertEquals(expected, victim.map(expected, String.class).getData());
	}

	@Test
	void map_Integer() {
		// PREPARE
		String input = "50";

		// EXECUTE
		Integer actual = victim.map(input, Integer.class).getData();

		// CHECK
		assertEquals(50, actual);
	}

	@Test
	void getSupportedTypes_containsAllExpectedTypes() {
		// PREPARE
		Set<Class<?>> expected = Set.of(
			String.class,
			int.class,
			Integer.class,
			long.class,
			Long.class,
			BigDecimal.class,
			double.class,
			Double.class,
			float.class,
			Float.class,
			Boolean.class,
			boolean.class);

		// EXECUTE
		Set<Class<?>> actual = victim.getSupportedTypes();

		// CHECK
		assertEquals(expected, actual);
	}

	// map - happy paths

	@Test
	void map_string_trimsWhitespace() {
		// EXECUTE & CHECK
		assertEquals("hello", victim.map("  hello  ", String.class).getData());
	}

	@Test
	void map_integer() {
		// PREPARE
		String input = "42";

		// EXECUTE
		Integer actual = victim.map(input, Integer.class).getData();

		// CHECK
		assertEquals(42, actual);
	}

	@Test
	void map_double() {
		// PREPARE
		String input = "3.14";

		// EXECUTE
		Double actual = victim.map(input, Double.class).getData();

		// CHECK
		assertEquals(3.14, actual);
	}

	@Test
	void map_float() {
		// PREPARE
		String input = "1.5";

		// EXECUTE
		Float actual = victim.map(input, Float.class).getData();

		// CHECK
		assertEquals(1.5f, actual);
	}

	@Test
	void map_boolean_true() {
		// PREPARE
		String input = "true";

		// EXECUTE
		Boolean actual = victim.map(input, Boolean.class).getData();

		// CHECK
		assertTrue(actual);
	}

	@Test
	void map_boolean_false() {
		// PREPARE
		String input = "false";

		// EXECUTE
		Boolean actual = victim.map(input, Boolean.class).getData();

		// CHECK
		assertFalse(actual);
	}

	@Test
	void map_primitiveBooleanClass_true() {
		// PREPARE
		String input = "true";

		// EXECUTE
		Boolean actual = victim.map(input, boolean.class).getData();

		// CHECK
		assertTrue(actual);
	}

	// map - edge cases

	@Test
	void map_integer_withSurroundingWhitespace() {
		// EXECUTE & CHECK
		assertEquals(99, victim.map("  99  ", Integer.class).getData());
	}

	@Test
	void map_boolean_invalidString_returnsFalse() {
		// EXECUTE & CHECK
		assertFalse(victim.map("notABoolean", Boolean.class).getData());
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"    yes     ",
		" JA",
		"oUi  " })
	void map_boolean_translation_true(String trueString) {
		// EXECUTE & CHECK
		assertTrue(
			victim
				.map(
					new ValuePatchRequest<Boolean>().setInputLanguages(List.of(Language.DE, Language.FR, Language.EN))
						.setTargetType(Boolean.class)
						.setValue(trueString))
				.getData());
	}

	@Test
	void map_boolean_translation_true_but_other_language() {
		// EXECUTE & CHECK
		assertFalse(
			victim.map(new ValuePatchRequest<Boolean>().setInputLanguages(List.of(Language.DE)).setTargetType(Boolean.class).setValue("OUI"))
				.getData());
	}

	@Test
	void map_unsupportedType_throwsIllegalArgumentException() {
		// PREPARE
		String input = "value";

		// EXECUTE & CHECK
		assertEquals(ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE), victim.map(input, Long.class));
	}

	@Test
	void map_invalidIntegerFormat_throwsNumberFormatException() {
		// EXECUTE & CHECK
		assertEquals(ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE), victim.map("notAnInt", Integer.class));
	}

	@Test
	void map_invalidDoubleFormat_throwsNumberFormatException() {
		// EXECUTE & CHECK
		assertEquals(ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE), victim.map("notADouble", Double.class));

	}

	@Test
	void map_invalidFloatFormat_throwsNumberFormatException() {
		// EXECUTE & CHECK
		assertEquals(ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE), victim.map("notAFloat", Float.class));
	}
}

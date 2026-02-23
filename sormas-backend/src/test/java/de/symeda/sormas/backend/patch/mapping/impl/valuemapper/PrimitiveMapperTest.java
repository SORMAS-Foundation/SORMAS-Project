package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import de.symeda.sormas.backend.AbstractUnitTest;

class PrimitiveMapperTest extends AbstractUnitTest {

	@InjectMocks
	private PrimitiveMapper victim;

	@Test
	void map_string() {
		// PREPARE
		String expected = "toto";
		// EXECUTE & CHECK
		assertEquals(expected, victim.map(expected, String.class));
	}

	@Test
	void map_Integer() {
		// PREPARE
		String input = "50";

		// EXECUTE
		Integer actual = victim.map(input, Integer.class);

		// CHECK
		assertEquals(50, actual);
	}

	@Test
	void getSupportedTypes_containsAllExpectedTypes() {
		// PREPARE
		Set<Class<?>> expected = Set.of(String.class, Integer.class, Double.class, Float.class, Boolean.class, boolean.class);

		// EXECUTE
		Set<Class<?>> actual = victim.getSupportedTypes();

		// CHECK
		assertEquals(expected, actual);
	}

	// map - happy paths

	@Test
	void map_string_trimsWhitespace() {
		// EXECUTE & CHECK
		assertEquals("hello", victim.map("  hello  ", String.class));
	}

	@Test
	void map_integer() {
		// PREPARE
		String input = "42";

		// EXECUTE
		Integer actual = victim.map(input, Integer.class);

		// CHECK
		assertEquals(42, actual);
	}

	@Test
	void map_double() {
		// PREPARE
		String input = "3.14";

		// EXECUTE
		Double actual = victim.map(input, Double.class);

		// CHECK
		assertEquals(3.14, actual);
	}

	@Test
	void map_float() {
		// PREPARE
		String input = "1.5";

		// EXECUTE
		Float actual = victim.map(input, Float.class);

		// CHECK
		assertEquals(1.5f, actual);
	}

	@Test
	void map_boolean_true() {
		// PREPARE
		String input = "true";

		// EXECUTE
		Boolean actual = victim.map(input, Boolean.class);

		// CHECK
		assertTrue(actual);
	}

	@Test
	void map_boolean_false() {
		// PREPARE
		String input = "false";

		// EXECUTE
		Boolean actual = victim.map(input, Boolean.class);

		// CHECK
		assertFalse(actual);
	}

	@Test
	void map_primitiveBooleanClass_true() {
		// PREPARE
		String input = "true";

		// EXECUTE
		Boolean actual = victim.map(input, boolean.class);

		// CHECK
		assertTrue(actual);
	}

	// map - edge cases

	@Test
	void map_integer_withSurroundingWhitespace() {
		// EXECUTE & CHECK
		assertEquals(99, victim.map("  99  ", Integer.class));
	}

	@Test
	void map_boolean_invalidString_returnsFalse() {
		// EXECUTE & CHECK
		assertFalse(victim.map("notABoolean", Boolean.class));
	}

	// map - error cases

	@Test
	void map_unsupportedType_throwsIllegalArgumentException() {
		// PREPARE
		String input = "value";

		// EXECUTE & CHECK
		assertThrows(IllegalArgumentException.class, () -> victim.map(input, Long.class));
	}

	@Test
	void map_nullValue_throwsNullPointerException() {
		// EXECUTE & CHECK
		assertThrows(NullPointerException.class, () -> victim.map(null, String.class));
	}

	@Test
	void map_invalidIntegerFormat_throwsNumberFormatException() {
		// EXECUTE & CHECK
		assertThrows(NumberFormatException.class, () -> victim.map("notAnInt", Integer.class));
	}

	@Test
	void map_invalidDoubleFormat_throwsNumberFormatException() {
		// EXECUTE & CHECK
		assertThrows(NumberFormatException.class, () -> victim.map("notADouble", Double.class));
	}

	@Test
	void map_invalidFloatFormat_throwsNumberFormatException() {
		// EXECUTE & CHECK
		assertThrows(NumberFormatException.class, () -> victim.map("notAFloat", Float.class));
	}
}

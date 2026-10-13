package de.symeda.sormas.backend.patch.mapping.impl.equalitychecker;

import static de.symeda.sormas.api.utils.OrderedRegisterable.LOW_PRECEDENCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import de.symeda.sormas.backend.AbstractUnitTest;

class ObjectPatchingEqualityCheckerTest extends AbstractUnitTest {

	@InjectMocks
	private ObjectPatchingEqualityChecker victim;

	@Test
	void getSupportedTypes_containsObjectClass() {
		// PREPARE
		Set<Class<?>> expected = Set.of(Object.class);

		// EXECUTE
		Set<Class<?>> actual = victim.getSupportedTypes();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void getOrder_isLowPrecedence() {
		assertEquals(LOW_PRECEDENCE, victim.getOrder());
	}

	@Test
	void areEqual_sameStringInstance_returnsTrue() {
		// PREPARE
		String value = "hello";

		// EXECUTE & CHECK
		assertTrue(victim.areEqual(value, value));
	}

	@Test
	void areEqual_equalStrings_returnsTrue() {
		// EXECUTE & CHECK
		assertTrue(victim.areEqual("hello", "hello"));
	}

	@Test
	void areEqual_differentStrings_returnsFalse() {
		// EXECUTE & CHECK
		assertFalse(victim.areEqual("hello", "world"));
	}

	@Test
	void areEqual_equalIntegers_returnsTrue() {
		// EXECUTE & CHECK
		assertTrue(victim.areEqual(42, 42));
	}

	@Test
	void areEqual_differentIntegers_returnsFalse() {
		// EXECUTE & CHECK
		assertFalse(victim.areEqual(1, 2));
	}

	@Test
	void areEqual_differentTypes_returnsFalse() {
		// EXECUTE & CHECK
		assertFalse(victim.areEqual("42", 42));
	}
}

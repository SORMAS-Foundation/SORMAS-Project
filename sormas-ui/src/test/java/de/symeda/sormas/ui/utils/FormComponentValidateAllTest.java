package de.symeda.sormas.ui.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.vaadin.v7.data.Validator.InvalidValueException;

/**
 * Unit tests for {@link FormComponent#validateAll(Runnable...)} — the accumulation that lets a single save
 * surface every missing/invalid field at once instead of one per save (issue #14012).
 */
public class FormComponentValidateAllTest {

	/** Minimal subclass that exposes the protected accumulation helper. */
	private static class TestComponent extends FormComponent<Object> {

		private static final long serialVersionUID = 1L;

		TestComponent() {
			super(Object.class);
		}

		void runAll(Runnable... checks) {
			validateAll(checks);
		}
	}

	private static Runnable fail(String message) {
		return () -> {
			throw new InvalidValueException(message);
		};
	}

	@Test
	public void allChecksPass_doesNotThrow() {
		TestComponent component = new TestComponent();
		assertDoesNotThrow(() -> component.runAll(() -> {
		}, () -> {
		}));
	}

	@Test
	public void singleFailure_throwsWithThatCause() {
		TestComponent component = new TestComponent();
		InvalidValueException thrown = assertThrows(InvalidValueException.class, () -> component.runAll(() -> {
		}, fail("lab is required")));

		assertEquals(1, thrown.getCauses().length);
		assertEquals("lab is required", thrown.getCauses()[0].getMessage());
	}

	@Test
	public void multipleFailures_areAllCollected() {
		TestComponent component = new TestComponent();
		InvalidValueException thrown = assertThrows(
			InvalidValueException.class,
			() -> component.runAll(fail("test type is required"), fail("test date is required"), fail("lab is required")));

		assertEquals(3, thrown.getCauses().length);
		String joined = Arrays.stream(thrown.getCauses()).map(InvalidValueException::getMessage).collect(Collectors.joining(", "));
		assertTrue(joined.contains("test type is required"));
		assertTrue(joined.contains("test date is required"));
		assertTrue(joined.contains("lab is required"));
		// The combined message also lists every caption rather than just the first.
		assertEquals(joined, thrown.getMessage());
	}

	@Test
	public void firstFailureDoesNotShortCircuitLaterChecks() {
		TestComponent component = new TestComponent();
		boolean[] secondRan = {
			false };

		InvalidValueException thrown = assertThrows(InvalidValueException.class, () -> component.runAll(fail("first"), () -> {
			secondRan[0] = true;
			throw new InvalidValueException("second");
		}));

		assertTrue(secondRan[0], "the check after the first failure must still run");
		assertEquals(2, thrown.getCauses().length);
	}

	@Test
	public void nestedCombinedException_isFlattenedIntoIndividualCauses() {
		TestComponent component = new TestComponent();
		// A check that itself throws a combined exception (as the binder validation does) must contribute its
		// individual field causes, not a single wrapper, so the parent form lists each missing field.
		Runnable combined = () -> {
			InvalidValueException a = new InvalidValueException("disease is required");
			InvalidValueException b = new InvalidValueException("pathogen is required");
			throw new InvalidValueException("disease is required, pathogen is required", a, b);
		};

		InvalidValueException thrown = assertThrows(InvalidValueException.class, () -> component.runAll(combined, fail("lab is required")));

		assertEquals(3, thrown.getCauses().length);
		String joined = Arrays.stream(thrown.getCauses()).map(InvalidValueException::getMessage).collect(Collectors.joining(", "));
		assertTrue(joined.contains("disease is required"));
		assertTrue(joined.contains("pathogen is required"));
		assertTrue(joined.contains("lab is required"));
	}
}

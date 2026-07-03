package de.symeda.sormas.backend.patch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractUnitTest;

class EqualValueOverrideHelperTest extends AbstractUnitTest {

	@InjectMocks
	private EqualValueOverrideHelper victim;

	@Mock
	private SystemConfigurationValueFacade systemConfigurationValueFacade;

	@Test
	void allowedOverride_configuredValueMatchesPlainValue_returnsTrue() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY)).thenReturn("UNKNOWN, OTHER");

		// EXECUTE
		boolean result = victim.allowedOverride("UNKNOWN");

		// CHECK
		assertTrue(result);
	}

	@Test
	void allowedOverride_configuredValueMatchesTypePrefixedValue_returnsTrue() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY)).thenReturn("Integer___42");

		// EXECUTE
		boolean result = victim.allowedOverride(42);

		// CHECK
		assertTrue(result);
	}

	@Test
	void allowedOverride_configuredValueMatchesTypePrefixedValue_unknown_returnsTrue() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY))
			.thenReturn("YesNoUnknown___UNKNOWN");

		// EXECUTE & CHECK
		assertTrue(victim.allowedOverride(YesNoUnknown.UNKNOWN));
		assertFalse(victim.allowedOverride(SymptomState.UNKNOWN));
	}

	@Test
	void allowedOverride_configuredValueDoesNotMatch_returnsFalse() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY)).thenReturn("SOME_OTHER_VALUE");

		// EXECUTE
		boolean result = victim.allowedOverride("UNKNOWN");

		// CHECK
		assertFalse(result);
	}

	@Test
	void allowedOverride_configValueNull_valueNotInDefaults_returnsFalse() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY)).thenReturn(null);

		// EXECUTE
		boolean result = victim.allowedOverride("NotAForbiddenField");

		// CHECK
		assertFalse(result);
	}

	@Test
	void allowedOverride_configValueWithWhitespaceAroundEntries_trimsEntries() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY)).thenReturn(" foo , bar ,, ");

		// EXECUTE & CHECK
		assertTrue(victim.allowedOverride("foo"));
		assertTrue(victim.allowedOverride("bar"));
	}

	@ParameterizedTest
	@CsvSource({
		"FOO, foo",
		"foo, FOO",
		"Foo, fOO",
		"café, cafe",
		"CAFÉ, cafe",
		"'  foo  ', foo",
		"'Café   Au   Lait', cafe au lait" })
	void allowedOverride_configuredAndInputValuesDifferByCaseAccentsOrWhitespace_stillMatch(String configuredValue, String inputValue) {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY)).thenReturn(configuredValue);

		// EXECUTE
		boolean result = victim.allowedOverride(inputValue);

		// CHECK
		assertTrue(result);
	}

	@Test
	void allowedOverride_typePrefixedConfiguredValueDifferentCase_stillMatches() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY)).thenReturn("INTEGER___42");

		// EXECUTE
		boolean result = victim.allowedOverride(42);

		// CHECK
		assertTrue(result);
	}

	@Test
	void allowedOverride_normalizedValuesDifferBeyondCaseAccentsOrWhitespace_returnsFalse() {
		// PREPARE
		when(systemConfigurationValueFacade.getValue(EqualValueOverrideHelper.ALLOWED_EQUALITY_VALUE_OVERRIDE_KEY)).thenReturn("FOO");

		// EXECUTE
		boolean result = victim.allowedOverride("foobar");

		// CHECK
		assertFalse(result);
	}
}

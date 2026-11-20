package de.symeda.sormas.api.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import de.symeda.sormas.api.AbstractUnitTest;

class ValueUtilsTest extends AbstractUnitTest {

	@Test
	void returnsFalseForNullArray() {
		// PREPARE
		Object[] values = null;

		// EXECUTE
		boolean result = ValueUtils.anyNonNullOrStringNotEmpty(values);

		// CHECK
		assertFalse(result);
	}

	@Test
	void returnsFalseWithoutArguments() {
		// PREPARE
		// No values supplied.

		// EXECUTE
		boolean result = ValueUtils.anyNonNullOrStringNotEmpty();

		// CHECK
		assertFalse(result);
	}

	@Test
	void returnsFalseForNullElements() {
		// PREPARE
		Object[] values = {
			null,
			null };

		// EXECUTE
		boolean result = ValueUtils.anyNonNullOrStringNotEmpty(values);

		// CHECK
		assertFalse(result);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"",
		" ",
		"\t",
		"\r\n",
		" \t\r\n ",
		"\u2003" })
	void returnsFalseForBlankStringsAndNulls(String value) {
		// PREPARE
		Object[] values = {
			null,
			value,
			null };

		// EXECUTE
		boolean result = ValueUtils.anyNonNullOrStringNotEmpty(values);

		// CHECK
		assertFalse(result);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"text",
		" text ",
		"0",
		"false" })
	void returnsTrueForNonBlankStringAfterBlankValues(String value) {
		// PREPARE
		Object[] values = {
			null,
			"",
			" ",
			value };

		// EXECUTE
		boolean result = ValueUtils.anyNonNullOrStringNotEmpty(values);

		// CHECK
		assertTrue(result);
	}

	@ParameterizedTest
	@MethodSource("nonStringValues")
	void returnsTrueForNonNullObjectsEvenWhenEmptyOrFalse(Object value) {
		// PREPARE
		Object[] values = {
			null,
			"",
			value,
			" " };

		// EXECUTE
		boolean result = ValueUtils.anyNonNullOrStringNotEmpty(values);

		// CHECK
		assertTrue(result);
	}

	private static Stream<Arguments> nonStringValues() {
		return Stream.of(0, false, Collections.emptyList(), new Object[0], new StringBuilder(), new Object()).map(value -> Arguments.of(value));
	}

	@Test
	void returnsTrueWhenFirstValueIsNonBlank() {
		// PREPARE
		Object[] values = {
			"text",
			null,
			"",
			" " };

		// EXECUTE
		boolean result = ValueUtils.anyNonNullOrStringNotEmpty(values);

		// CHECK
		assertTrue(result);
	}
}

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Tests {@link NullableOptionGroupConverter}, which turns the options checked in a multi-select option group into one
 * model value. Sets are built as {@link LinkedHashSet}s so the iteration order, and thus the expected value, is fixed.
 * <p>
 * Example: a Yes/No/Unknown field has NO selected and the user clicks YES; the browser sends {@code [NO, YES]} and the
 * converter returns {@code YES}, the newly clicked option.
 */
public class NullableOptionGroupConverterTest {

	/**
	 * Nothing checked gives no value, whatever was selected before.
	 * <p>
	 * Example: YES was selected and the user unchecks it; the browser sends {@code null} or {@code []} → {@code null}.
	 */
	@Test
	public void testConvertToModelNothingSelected() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(YesNoUnknown.YES);
		assertThat(convert(converter, null), is(nullValue()));

		converter = new NullableOptionGroupConverter(YesNoUnknown.YES);
		assertThat(convert(converter, Collections.emptySet()), is(nullValue()));
	}

	/**
	 * Exactly one checked option is the value.
	 * <p>
	 * Example: nothing was selected and the user clicks NO; the browser sends {@code [NO]} → {@code NO}.
	 */
	@Test
	public void testConvertToModelSingleOption() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(null);
		assertThat(convert(converter, Collections.singleton(YesNoUnknown.NO)), is(YesNoUnknown.NO));
	}

	/**
	 * With two checked options, the one that differs from the previous value is the new click and wins.
	 * <p>
	 * Example: NO was selected, the user clicks YES; the browser sends {@code [NO, YES]} → {@code YES}. The same set with
	 * YES selected before gives {@code NO}.
	 */
	@Test
	public void testConvertToModelTwoOptionsNewClickWins() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(YesNoUnknown.NO);
		assertThat(convert(converter, orderedSet(YesNoUnknown.NO, YesNoUnknown.YES)), is(YesNoUnknown.YES));

		converter = new NullableOptionGroupConverter(YesNoUnknown.YES);
		assertThat(convert(converter, orderedSet(YesNoUnknown.NO, YesNoUnknown.YES)), is(YesNoUnknown.NO));
	}

	/**
	 * With two checked options and no previous value, the last option in the set's iteration order is taken.
	 * <p>
	 * Example: no previous value, the browser sends {@code [NO, YES]} → {@code YES}.
	 */
	@Test
	public void testConvertToModelTwoOptionsWithoutPreviousValue() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(null);
		assertThat(convert(converter, orderedSet(YesNoUnknown.NO, YesNoUnknown.YES)), is(YesNoUnknown.YES));
	}

	/**
	 * With two checked options that both differ from the previous value (possible after rapid clicks), the last option in
	 * the set's iteration order is taken, the same rule as for three or more options.
	 * <p>
	 * Example: UNKNOWN was selected, the browser sends {@code [NO, YES]} → {@code YES}, not {@code NO}.
	 */
	@Test
	public void testConvertToModelTwoOptionsPreviousValueNotInSet() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(YesNoUnknown.UNKNOWN);
		assertThat(convert(converter, orderedSet(YesNoUnknown.NO, YesNoUnknown.YES)), is(YesNoUnknown.YES));
	}

	/**
	 * #14427: rapid clicks on YES, NO and UNKNOWN reach the server as three checked options at once; this used to throw an
	 * {@link IllegalArgumentException}. Now the last option that differs from the previous value is taken.
	 * <p>
	 * Example: UNKNOWN was selected, the browser sends {@code [UNKNOWN, NO, YES]} → {@code YES}, no exception.
	 */
	@Test
	public void testConvertToModelThreeOptionsAfterRapidClicks() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(YesNoUnknown.UNKNOWN);
		assertThat(
			convert(converter, orderedSet(YesNoUnknown.UNKNOWN, YesNoUnknown.NO, YesNoUnknown.YES)),
			is(YesNoUnknown.YES));
	}

	/**
	 * With three checked options and no previous value, the last option in the set's iteration order is taken.
	 * <p>
	 * Example: no previous value, the browser sends {@code [UNKNOWN, NO, YES]} → {@code YES}.
	 */
	@Test
	public void testConvertToModelThreeOptionsWithoutPreviousValue() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(null);
		assertThat(
			convert(converter, orderedSet(YesNoUnknown.UNKNOWN, YesNoUnknown.NO, YesNoUnknown.YES)),
			is(YesNoUnknown.YES));
	}

	/**
	 * The previous value is never chosen while a newly clicked option is in the set, even if it comes last in the set.
	 * <p>
	 * Example: YES was selected, the browser sends {@code [UNKNOWN, NO, YES]} → {@code NO} (the last of
	 * {@code UNKNOWN, NO}), not {@code YES}.
	 */
	@Test
	public void testConvertToModelThreeOptionsPreviousValueLast() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(YesNoUnknown.YES);
		assertThat(
			convert(converter, orderedSet(YesNoUnknown.UNKNOWN, YesNoUnknown.NO, YesNoUnknown.YES)),
			is(YesNoUnknown.NO));
	}

	/**
	 * After the rapid-click case, the converter remembers the chosen value, so the next regular click works as usual.
	 * <p>
	 * Example: UNKNOWN was selected, the browser sends {@code [UNKNOWN, NO, YES]} → {@code YES} is remembered; the next
	 * click on NO sends {@code [YES, NO]} → {@code NO}.
	 */
	@Test
	public void testConvertToModelNormalClickAfterRapidClicks() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(YesNoUnknown.UNKNOWN);
		convert(converter, orderedSet(YesNoUnknown.UNKNOWN, YesNoUnknown.NO, YesNoUnknown.YES));

		assertThat(convert(converter, orderedSet(YesNoUnknown.YES, YesNoUnknown.NO)), is(YesNoUnknown.NO));
	}

	/**
	 * A single value that is not a set is kept instead of failing with a {@link ClassCastException}.
	 * <p>
	 * Example: the converter gets {@code YES} instead of {@code [YES]} → {@code YES}.
	 */
	@Test
	public void testConvertToModelNonSetValueIsKept() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(null);
		assertThat(convert(converter, YesNoUnknown.YES), is(YesNoUnknown.YES));
	}

	/**
	 * The model value is shown as a set with that one option, no value as {@code null}.
	 * <p>
	 * Example: {@code YES} → {@code [YES]}, so only YES is checked; {@code null} → {@code null}, nothing is checked.
	 */
	@Test
	public void testConvertToPresentation() {

		NullableOptionGroupConverter converter = new NullableOptionGroupConverter(null);

		assertThat(converter.convertToPresentation(null, Object.class, Locale.ENGLISH), is(nullValue()));

		Object presentation = converter.convertToPresentation(YesNoUnknown.YES, Object.class, Locale.ENGLISH);
		assertThat(presentation, instanceOf(Collection.class));
		assertThat((Collection<?>) presentation, contains(YesNoUnknown.YES));
	}

	/**
	 * Converts {@code value} to the model value, e.g. {@code [NO, YES]} with previous value NO gives {@code YES}.
	 */
	private Object convert(NullableOptionGroupConverter converter, Object value) {
		return converter.convertToModel(value, Object.class, Locale.ENGLISH);
	}

	/**
	 * Builds a set that keeps the given order, e.g. {@code orderedSet(NO, YES)} always iterates NO first, then YES.
	 */
	private Set<Object> orderedSet(Object... values) {
		return new LinkedHashSet<>(Arrays.asList(values));
	}
}

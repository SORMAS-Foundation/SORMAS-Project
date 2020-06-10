/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.auditlog.api.value.format;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Test;

import de.symeda.auditlog.api.sample.CustomEnum;

/**
 * @see ComparableCollectionFormatter
 * @author Stefan Kock
 */
public class ComparableCollectionFormatterTest {

	private static final String ZERO_ENTRIES = "0 []";

	@Test
	public void testFormatEmptyCollections() {

		ComparableCollectionFormatter<String> cut = new ComparableCollectionFormatter<>();

		assertThat(cut.format(new ArrayList<>()), is(ZERO_ENTRIES));
		assertThat(cut.format(new LinkedHashSet<>()), is(ZERO_ENTRIES));
	}

	@Test
	public void testFormatStrings() {

		ComparableCollectionFormatter<String> cut = new ComparableCollectionFormatter<>();

		List<String> unsortedList = Arrays.asList(
			new String[] {
				"B",
				"A",
				"AA",
				"ABC",
				"a",
				"Z" });
		assertThat(cut.format(unsortedList), is("6 [A;AA;ABC;B;Z;a]"));
	}

	@Test
	public void testFormatBigDecimals() {

		ComparableCollectionFormatter<BigDecimal> cut = new ComparableCollectionFormatter<>();

		List<BigDecimal> unsortedList = Arrays.asList(
			new BigDecimal[] {
				new BigDecimal("1"),
				new BigDecimal("1.0"),
				new BigDecimal("1.01"),
				new BigDecimal("0"),
				new BigDecimal("-5"),
				new BigDecimal("-0.1") });

		assertThat(cut.format(unsortedList), is("6 [-5;-0.1;0;1;1.0;1.01]"));
	}

	@Test
	public void testFormatEnums() {

		ComparableCollectionFormatter<CustomEnum> cut = new ComparableCollectionFormatter<>(value -> value.name());

		List<CustomEnum> unsortedList = Arrays.asList(
			new CustomEnum[] {
				CustomEnum.VALUE_2,
				CustomEnum.VALUE_1,
				CustomEnum.VALUE_3 });
		assertThat(cut.format(unsortedList), is("3 [VALUE_1;VALUE_2;VALUE_3]"));
	}

	/**
	 * {@code null} within the Collection is not supported!
	 */
	@Test(expected = NullPointerException.class)
	public void testFormatEnumsWithNullInCollection() {

		ComparableCollectionFormatter<CustomEnum> cut = new ComparableCollectionFormatter<>(value -> value.name());

		List<CustomEnum> unsortedList = Arrays.asList(
			new CustomEnum[] {
				CustomEnum.VALUE_2,
				null,
				CustomEnum.VALUE_3 });

		cut.format(unsortedList);
	}
}

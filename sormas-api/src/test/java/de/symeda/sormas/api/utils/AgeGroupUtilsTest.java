/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.utils;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class AgeGroupUtilsTest {

	@Test
	public void testAgeGroupValidation() {

		Assert.assertTrue(isAgeGroupValid("6m"));
		Assert.assertTrue(isAgeGroupValid("0D_28Y"));
		Assert.assertTrue(isAgeGroupValid("0D_28D"));
		Assert.assertTrue(isAgeGroupValid("1M_12M"));
		Assert.assertTrue(isAgeGroupValid("1Y_4Y"));
		Assert.assertTrue(isAgeGroupValid("5Y_15Y"));
		Assert.assertTrue(isAgeGroupValid("16Y"));
		Assert.assertTrue(isAgeGroupValid("3y"));

		Assert.assertFalse(isAgeGroupValid("3yy"));
		Assert.assertFalse(isAgeGroupValid("3years"));
		Assert.assertFalse(isAgeGroupValid("1m12m"));
		Assert.assertFalse(isAgeGroupValid("1m_12"));
		Assert.assertFalse(isAgeGroupValid("0D-28Y"));
		Assert.assertFalse(isAgeGroupValid("0D_28C"));
		Assert.assertFalse(isAgeGroupValid("0D_28D_30D"));
	}

	@Test
	public void testCreateAgeGroupCaption() {
		Assert.assertEquals("6+ months", AgeGroupUtils.createCaption("6m"));
		Assert.assertEquals("0 days - 28 years", AgeGroupUtils.createCaption("0D_28Y"));
		Assert.assertEquals("0-28 days", AgeGroupUtils.createCaption("0D_28D"));
		Assert.assertEquals("1-12 months", AgeGroupUtils.createCaption("1M_12M"));
		Assert.assertEquals("1-4 years", AgeGroupUtils.createCaption("1Y_4Y"));
		Assert.assertEquals("5-15 years", AgeGroupUtils.createCaption("5Y_15Y"));
		Assert.assertEquals("16+ years", AgeGroupUtils.createCaption("16Y"));
		Assert.assertEquals("3+ years", AgeGroupUtils.createCaption("3y"));
	}

	private boolean isAgeGroupValid(String ageGroup) {
		try {
			AgeGroupUtils.validateAgeGroup(ageGroup);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Test
	public void testComparator() {
		String[] input = new String[]{
				"1Y_5Y",
				"3M_6M",
				"0D_28D",
				"29D_60D",
				"6M_2Y"};
		Arrays.sort(input, AgeGroupUtils.getComparator());
		Assert.assertArrayEquals(
				new String[]{
						"0D_28D",
						"29D_60D",
						"3M_6M",
						"6M_2Y",
						"1Y_5Y"}, input
		);

		input = new String[]{
				"6D_8Y",
				"5D_9Y",
				"4Y_6Y",
				"2M_300M",
				"2M_200M"};
		Arrays.sort(input, AgeGroupUtils.getComparator());
		Assert.assertArrayEquals(
				new String[]{
						"5D_9Y",
						"6D_8Y",
						"2M_200M",
						"2M_300M",
						"4Y_6Y"}, input
		);
	}
}

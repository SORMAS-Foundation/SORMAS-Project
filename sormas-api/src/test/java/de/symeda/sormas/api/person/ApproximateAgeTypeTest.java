package de.symeda.sormas.api.person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;

public class ApproximateAgeTypeTest {

	@Test
	public void testGetAgeYears() {

		assertNull(ApproximateAgeHelper.getAgeYears(null, null));
		assertNull(ApproximateAgeHelper.getAgeYears(null, ApproximateAgeType.DAYS));
		assertEquals((Integer) 5, ApproximateAgeHelper.getAgeYears(5, null));
		assertEquals((Integer) 13, ApproximateAgeHelper.getAgeYears(13, ApproximateAgeType.YEARS));
		assertEquals((Integer) 0, ApproximateAgeHelper.getAgeYears(0, ApproximateAgeType.MONTHS));
		assertEquals((Integer) 0, ApproximateAgeHelper.getAgeYears(11, ApproximateAgeType.MONTHS));
		assertEquals((Integer) 1, ApproximateAgeHelper.getAgeYears(12, ApproximateAgeType.MONTHS));
		assertEquals((Integer) 0, ApproximateAgeHelper.getAgeYears(0, ApproximateAgeType.DAYS));
		assertEquals((Integer) 0, ApproximateAgeHelper.getAgeYears(364, ApproximateAgeType.DAYS));
		assertEquals((Integer) 1, ApproximateAgeHelper.getAgeYears(365, ApproximateAgeType.DAYS));
	}

	@Test
	public void testAgeGroupCalculation() {
		assertEquals("0--4", ApproximateAgeHelper.getAgeGroupFromAge(380, ApproximateAgeType.DAYS));
		assertEquals("0--4", ApproximateAgeHelper.getAgeGroupFromAge(7, ApproximateAgeType.MONTHS));
		assertEquals("5--9", ApproximateAgeHelper.getAgeGroupFromAge(7, ApproximateAgeType.YEARS));
		assertEquals("20--24", ApproximateAgeHelper.getAgeGroupFromAge(20, ApproximateAgeType.YEARS));
		assertEquals("20--24", ApproximateAgeHelper.getAgeGroupFromAge(24, ApproximateAgeType.YEARS));
		assertEquals("0--4", ApproximateAgeHelper.getAgeGroupFromAge(2, ApproximateAgeType.YEARS));
		assertEquals("120+", ApproximateAgeHelper.getAgeGroupFromAge(121, ApproximateAgeType.YEARS));
	}
}

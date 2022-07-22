package de.symeda.sormas.api.person;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Test;

import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.UtilDate;

public class ApproximateAgeTypeTest {

	@Test
	public void testGetApproximateAgeDate() {

		Date birthDate;

		// 0. no birthDate -> 0 years
		birthDate = null;
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, null), null, ApproximateAgeType.YEARS);

		// 1. birthDate and deathDate
		birthDate = UtilDate.of(1981, Month.JUNE, 13);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1981, Month.JUNE, 13)), 0, ApproximateAgeType.DAYS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1981, Month.JUNE, 14)), 1, ApproximateAgeType.DAYS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1981, Month.JULY, 13)), 1, ApproximateAgeType.MONTHS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1981, Month.AUGUST, 12)), 1, ApproximateAgeType.MONTHS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1981, Month.AUGUST, 13)), 2, ApproximateAgeType.MONTHS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1982, Month.JUNE, 12)), 11, ApproximateAgeType.MONTHS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1982, Month.JUNE, 13)), 1, ApproximateAgeType.YEARS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1983, Month.JUNE, 12)), 1, ApproximateAgeType.YEARS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1983, Month.JUNE, 13)), 2, ApproximateAgeType.YEARS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1984, Month.JUNE, 12)), 2, ApproximateAgeType.YEARS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, UtilDate.of(1984, Month.JUNE, 13)), 3, ApproximateAgeType.YEARS);

		// 2. birthDate in relation to now
		Integer expectedAmount = (int) ChronoUnit.YEARS.between(UtilDate.toLocalDate(birthDate), LocalDate.now());
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate, null), expectedAmount, ApproximateAgeType.YEARS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(birthDate), expectedAmount, ApproximateAgeType.YEARS);
	}

	@Test
	public void testGetApproximateAgeInteger() {

		// 1. birthDate and deathDate
		Integer bdY = 1981;
		Integer bdM = 6;
		Integer bdD = 13;
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(bdY, bdM, bdD, UtilDate.of(1981, Month.JUNE, 13)), 0, ApproximateAgeType.DAYS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(bdY, bdM, bdD, UtilDate.of(1981, Month.JUNE, 14)), 1, ApproximateAgeType.DAYS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(bdY, bdM, bdD, UtilDate.of(1981, Month.JULY, 13)), 1, ApproximateAgeType.MONTHS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(bdY, bdM, bdD, UtilDate.of(1982, Month.JUNE, 13)), 1, ApproximateAgeType.YEARS);

		// 2. birthDate in relation to now
		Integer expectedAmount = (int) ChronoUnit.YEARS.between(LocalDate.of(bdY, Month.JUNE, bdD), LocalDate.now());
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(bdY, bdM, bdD, null), expectedAmount, ApproximateAgeType.YEARS);

		// 3. birthDate with less precision
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(bdY, bdM, null, UtilDate.of(1981, Month.JUNE, 13)), 12, ApproximateAgeType.DAYS);
		assertEqualAge(ApproximateAgeHelper.getApproximateAge(bdY, null, null, UtilDate.of(1981, Month.JUNE, 13)), 5, ApproximateAgeType.MONTHS);
	}

	private static void assertEqualAge(Pair<Integer, ApproximateAgeType> approximateAge, Integer expectedAmount, ApproximateAgeType expectedType) {

		assertNotNull(approximateAge);
		assertThat(approximateAge.getElement0(), equalTo(expectedAmount));
		assertThat(approximateAge.getElement1(), equalTo(expectedType));
	}

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
	public void testGetAgeGroupFromAge() {

		assertEquals("0--4", ApproximateAgeHelper.getAgeGroupFromAge(380, ApproximateAgeType.DAYS));
		assertEquals("0--4", ApproximateAgeHelper.getAgeGroupFromAge(7, ApproximateAgeType.MONTHS));
		assertEquals("5--9", ApproximateAgeHelper.getAgeGroupFromAge(7, ApproximateAgeType.YEARS));
		assertEquals("20--24", ApproximateAgeHelper.getAgeGroupFromAge(20, ApproximateAgeType.YEARS));
		assertEquals("20--24", ApproximateAgeHelper.getAgeGroupFromAge(24, ApproximateAgeType.YEARS));
		assertEquals("0--4", ApproximateAgeHelper.getAgeGroupFromAge(2, ApproximateAgeType.YEARS));
		assertEquals("120+", ApproximateAgeHelper.getAgeGroupFromAge(121, ApproximateAgeType.YEARS));
	}
}

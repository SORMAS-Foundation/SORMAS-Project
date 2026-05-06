package de.symeda.sormas.backend.patch.mapping.impl.equalitychecker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import de.symeda.sormas.api.utils.OrderedRegisterable;
import de.symeda.sormas.backend.AbstractUnitTest;

class DateEqualityCheckerTest extends AbstractUnitTest {

	@InjectMocks
	private DateEqualityChecker victim;

	@Test
	void getSupportedTypes_containsDateClass() {
		// PREPARE
		Set<Class<?>> expected = Set.of(Date.class);

		// EXECUTE
		Set<Class<?>> actual = victim.getSupportedTypes();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void getOrder_isHighPrecedence() {
		assertEquals(OrderedRegisterable.HIGH_PRECEDENCE, victim.getOrder());
	}

	@Test
	void areEqual_sameDateInstance_returnsTrue() {
		// PREPARE
		Date date = toDate(LocalDate.of(2024, 6, 15));

		// EXECUTE & CHECK
		assertTrue(victim.areEqual(date, date));
	}

	@Test
	void areEqual_sameTimestamp_returnsTrue() {
		// PREPARE
		long millis = toDate(LocalDate.of(2024, 6, 15)).getTime();
		Date a = new Date(millis);
		Date b = new Date(millis);

		// EXECUTE & CHECK
		assertTrue(victim.areEqual(a, b));
	}

	@Test
	void areEqual_sameDayDifferentTime_returnsTrue() {
		// PREPARE
		Date morning = toDate(LocalDateTime.of(2024, 6, 15, 8, 0, 0));
		Date evening = toDate(LocalDateTime.of(2024, 6, 15, 23, 59, 59));

		// EXECUTE & CHECK
		assertTrue(victim.areEqual(morning, evening));
	}

	@Test
	void areEqual_differentDays_returnsFalse() {
		// PREPARE
		Date day1 = toDate(LocalDate.of(2024, 6, 15));
		Date day2 = toDate(LocalDate.of(2024, 6, 16));

		// EXECUTE & CHECK
		assertFalse(victim.areEqual(day1, day2));
	}

	@Test
	void areEqual_differentMonths_returnsFalse() {
		// PREPARE
		Date june = toDate(LocalDate.of(2024, 6, 15));
		Date july = toDate(LocalDate.of(2024, 7, 15));

		// EXECUTE & CHECK
		assertFalse(victim.areEqual(june, july));
	}

	@Test
	void areEqual_differentYears_returnsFalse() {
		// PREPARE
		Date year2023 = toDate(LocalDate.of(2023, 6, 15));
		Date year2024 = toDate(LocalDate.of(2024, 6, 15));

		// EXECUTE & CHECK
		assertFalse(victim.areEqual(year2023, year2024));
	}

	@Test
	void areEqual_endOfDayAndStartOfNextDay_returnsFalse() {
		// PREPARE
		Date endOfDay = toDate(LocalDateTime.of(2024, 6, 15, 23, 59, 59));
		Date startOfNextDay = toDate(LocalDateTime.of(2024, 6, 16, 0, 0, 0));

		// EXECUTE & CHECK
		assertFalse(victim.areEqual(endOfDay, startOfNextDay));
	}

	private static Date toDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private static Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
}

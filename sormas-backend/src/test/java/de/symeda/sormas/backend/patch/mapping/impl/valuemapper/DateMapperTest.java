package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.backend.AbstractUnitTest;

class DateMapperTest extends AbstractUnitTest {

	@InjectMocks
	private DatePatchMapper victim;

	@Test
	void getSupportedTypes_containsDateClasses() {
		// PREPARE
		Set<Class<?>> expected = Set.of(Date.class, LocalDate.class, LocalDateTime.class);

		// EXECUTE
		Set<Class<?>> actual = victim.getSupportedTypes();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void map_validDate() throws Exception {
		// PREPARE
		String input = "2024-06-15";
		Date expected = new SimpleDateFormat("yyyy-MM-dd").parse(input);

		// EXECUTE
		Date actual = victim.map(input, Date.class).getData();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void map_firstDayOfYear() throws Exception {
		// PREPARE
		String input = "2024-01-01";
		Date expected = new SimpleDateFormat("yyyy-MM-dd").parse(input);

		// EXECUTE
		Date actual = victim.map(input, Date.class).getData();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void map_lastDayOfYear() throws Exception {
		// PREPARE
		String input = "2024-12-31";
		Date expected = new SimpleDateFormat("yyyy-MM-dd").parse(input);

		// EXECUTE
		Date actual = victim.map(input, Date.class).getData();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void map_valueAsNonStringObject_usesToString() throws Exception {
		// PREPARE
		Date expected = new SimpleDateFormat("yyyy-MM-dd").parse("2024-06-15");

		// EXECUTE
		Date actual = victim.map(new StringBuilder("2024-06-15"), Date.class).getData();

		// CHECK
		assertEquals(expected, actual);
	}

	@Test
	void map_invalidFormat_throwsIllegalArgumentException() {
		// EXECUTE & CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, victim.map("15/06/2024", Date.class).getDataPatchFailureCause());
	}

	@Test
	void map_lenientOff_invalidDay_throwsIllegalArgumentException() {
		// EXECUTE & CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, victim.map("2024-02-30", Date.class).getDataPatchFailureCause());
	}

	@Test
	void map_lenientOff_invalidMonth_throwsIllegalArgumentException() {
		// EXECUTE & CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, victim.map("2024-13-01", Date.class).getDataPatchFailureCause());
	}

	@Test
	void map_emptyString_throwsIllegalArgumentException() {
		// EXECUTE & CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, victim.map("", Date.class).getDataPatchFailureCause());
	}

	@Test
	void map_randomString_throwsIllegalArgumentException() {
		// EXECUTE & CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, victim.map("notADate", Date.class).getDataPatchFailureCause());
	}

	// --- LocalDate ---

	@Test
	void map_localDate_validDate() {
		// EXECUTE & CHECK
		assertEquals(LocalDate.of(2024, 6, 15), victim.map("2024-06-15", LocalDate.class).getData());
	}

	@Test
	void map_localDate_fromDateTimeString_returnsDatePart() {
		// EXECUTE & CHECK
		assertEquals(LocalDate.of(2024, 6, 15), victim.map("2024-06-15T14:30:00", LocalDate.class).getData());
	}

	@Test
	void map_localDate_invalidFormat() {
		// EXECUTE & CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, victim.map("15/06/2024", LocalDate.class).getDataPatchFailureCause());
	}

	@Test
	void map_localDate_invalidDay() {
		// EXECUTE & CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, victim.map("2024-02-30", LocalDate.class).getDataPatchFailureCause());
	}

	// --- LocalDateTime ---

	@Test
	void map_localDateTime_validDateTime() {
		// EXECUTE & CHECK
		assertEquals(LocalDateTime.of(2024, 6, 15, 14, 30, 0), victim.map("2024-06-15T14:30:00", LocalDateTime.class).getData());
	}

	@Test
	void map_localDateTime_validDateTimeWithoutSeconds() {
		// EXECUTE & CHECK
		assertEquals(LocalDateTime.of(2024, 6, 15, 14, 30, 0), victim.map("2024-06-15T14:30", LocalDateTime.class).getData());
	}

	@Test
	void map_localDateTime_fromDateOnlyString_returnsMidnight() {
		// EXECUTE & CHECK
		assertEquals(LocalDateTime.of(2024, 6, 15, 0, 0, 0), victim.map("2024-06-15", LocalDateTime.class).getData());
	}

	@Test
	void map_localDateTime_invalidFormat() {
		// EXECUTE & CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, victim.map("15/06/2024", LocalDateTime.class).getDataPatchFailureCause());
	}

	@Test
	void map_localDateTime_invalidDay() {
		// EXECUTE & CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, victim.map("2024-02-30T10:00:00", LocalDateTime.class).getDataPatchFailureCause());
	}
}

package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
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
	void getSupportedTypes_containsDateClass() {
		// PREPARE
		Set<Class<?>> expected = Set.of(Date.class);

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
}

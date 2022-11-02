package de.symeda.sormas.api.task;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.utils.UtilDate;

public class TaskHelperTest {

	@Test
	public void testGetDefaultSuggestedStart() {

		assertEquals(UtilDate.toLocalDateTime(TaskHelper.getDefaultSuggestedStart()), LocalDateTime.now());
	}

	@Test
	public void testGetDefaultDueDate() {

		assertEquals(UtilDate.toLocalDateTime(TaskHelper.getDefaultDueDate()), LocalDateTime.now().plusDays(1));
	}

	private static void assertEquals(LocalDateTime value, LocalDateTime expectedValue) {

		// Create a range of time of +/- 1s to respect different execution times of the unit test
		assertThat(value, allOf(greaterThan(expectedValue.minusSeconds(1)), lessThan(expectedValue.plusSeconds(1))));
	}
}

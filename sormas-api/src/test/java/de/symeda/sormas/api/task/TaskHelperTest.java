package de.symeda.sormas.api.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import de.symeda.sormas.api.utils.UtilDate;

public class TaskHelperTest {

	@Test
	public void testGetDefaultSuggestedStart() {

		assertThat(ChronoUnit.SECONDS.between(UtilDate.toLocalDateTime(TaskHelper.getDefaultSuggestedStart()), LocalDateTime.now()), equalTo(0L));
	}

	@Test
	public void testGetDefaultDueDate() throws Exception {

		assertThat(ChronoUnit.MINUTES.between(LocalDateTime.now(), UtilDate.toLocalDateTime(TaskHelper.getDefaultDueDate())), equalTo(1439L));
	}
}

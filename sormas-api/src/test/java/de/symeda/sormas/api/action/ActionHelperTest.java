package de.symeda.sormas.api.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import de.symeda.sormas.api.utils.UtilDate;

public class ActionHelperTest {

	@Test
	public void testGetDefaultDate() {

		assertThat(ChronoUnit.SECONDS.between(UtilDate.toLocalDateTime(ActionHelper.getDefaultDate()), LocalDateTime.now()), equalTo(0L));
	}
}

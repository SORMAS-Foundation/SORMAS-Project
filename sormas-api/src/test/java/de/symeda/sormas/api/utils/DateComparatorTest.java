package de.symeda.sormas.api.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;

import java.time.LocalDateTime;
import java.util.Date;

import org.junit.Test;

public class DateComparatorTest {

	@Test
	public void testCompareDate() {

		DateComparator cut = DateComparator.getDateInstance();

		assertThat(cut.compare(null, null), equalTo(0));
		assertThat(cut.compare(null, UtilDate.today()), equalTo(0));
		assertThat(cut.compare(UtilDate.today(), null), equalTo(0));
		assertThat(cut.compare(UtilDate.today(), UtilDate.today()), equalTo(0));

		assertThat(cut.compare(null, UtilDate.yesterday()), greaterThan(0));
		assertThat(cut.compare(UtilDate.yesterday(), null), lessThan(0));

		assertThat(cut.compare(null, UtilDate.tomorrow()), lessThan(0));
		assertThat(cut.compare(UtilDate.tomorrow(), null), greaterThan(0));

		assertThat(cut.compare(UtilDate.yesterday(), UtilDate.tomorrow()), lessThan(0));
		assertThat(cut.compare(UtilDate.tomorrow(), UtilDate.yesterday()), greaterThan(0));
	}

	@Test
	public void testCompareDateTime() throws InterruptedException {

		DateComparator cut = DateComparator.getDateTimeInstance();

		Date now = UtilDate.now();
		Thread.sleep(1);

		// Not always equal because null > now() not always happens in the same millisecond for both parameters
		assertThat(cut.compare(null, null), not(greaterThan(0)));

		assertThat(cut.compare(null, now), greaterThan(0));
		assertThat(cut.compare(now, null), lessThan(0));
		assertThat(cut.compare(now, now), equalTo(0));

		assertThat(cut.compare(UtilDate.now(), UtilDate.today()), greaterThan(0));
		assertThat(cut.compare(UtilDate.today(), UtilDate.now()), lessThan(0));

		assertThat(cut.compare(UtilDate.now(), UtilDate.from(LocalDateTime.now().plusHours(1))), lessThan(0));
		assertThat(cut.compare(UtilDate.from(LocalDateTime.now().plusHours(1)), UtilDate.now()), greaterThan(0));
	}
}

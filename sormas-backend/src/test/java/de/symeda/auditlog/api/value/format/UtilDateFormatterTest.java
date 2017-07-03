package de.symeda.auditlog.api.value.format;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.Month;

import javax.persistence.TemporalType;

import org.junit.Test;

import de.symeda.sormas.backend.auditlog.AuditLogDateHelper;

/**
 * @see UtilDateFormatter
 * @author Stefan Kock
 */
public class UtilDateFormatterTest {

	@Test
	public void testUtilDateFormatterTemporalType() {

		assertThat(new UtilDateFormatter(TemporalType.TIMESTAMP).getPattern(), equalTo(UtilDateFormatter.TIMESTAMP_PATTERN));
		assertThat(new UtilDateFormatter(TemporalType.DATE).getPattern(), equalTo(UtilDateFormatter.DAY_PATTERN));
		assertThat(new UtilDateFormatter(TemporalType.TIME).getPattern(), equalTo(UtilDateFormatter.HOUR_MIN_PATTERN));

		assertThat(new UtilDateFormatter((TemporalType) null).getPattern(), equalTo(UtilDateFormatter.TIMESTAMP_PATTERN));
	}

	@Test
	public void testUtilDateFormatterString() throws Exception {

		String pattern = "YYYY MMM";
		assertThat(new UtilDateFormatter(pattern).getPattern(), equalTo(pattern));
	}

	@Test
	public void testFormat() {

		UtilDateFormatter formatter = new UtilDateFormatter(TemporalType.DATE);
		assertThat(formatter.format(AuditLogDateHelper.of(2016, Month.APRIL, 1)), equalTo("2016-04-01"));
	}
}

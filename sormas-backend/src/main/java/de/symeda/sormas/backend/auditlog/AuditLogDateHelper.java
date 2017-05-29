package de.symeda.sormas.backend.auditlog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

public class AuditLogDateHelper {

	/**
	 * From this year on the conversion via Time API works without the shift of 6:32 minutes or 2 days.
	 */
	private static final int CONSISTENT_YEARS_START = 1894;
 
	/**
	 * Pattern used to parse {@link LocalDateTime} to {@link Date}.
	 */
	private static final String LOCAL_DATE_TIME_STRING_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	
	/**
	 * Transforms a {@link LocalDateTime} to a {@link Date}.
	 * 
	 * @param localDateTime
	 * @return <code>null</code> if <code>localDateTime == null</code>.
	 */
	public static Date from(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
 
		final Date utilDate;
		if (localDateTime.getYear() >= CONSISTENT_YEARS_START) {
			Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
			utilDate = Date.from(instant);
		} else {
 
			utilDate = parse(localDateTime, LOCAL_DATE_TIME_STRING_PATTERN);
		}
 
		return utilDate;
	}
	
	private static Date parse(Temporal temporal, final String pattern) {
		try {
			String temporalFormatted = DateTimeFormatter.ofPattern(pattern).format(temporal);
			Date utilDateParsed = new SimpleDateFormat(pattern).parse(temporalFormatted);
			return utilDateParsed;
		} catch (ParseException e) {
			throw new DateTimeException(String.format("Unexpected error while trying to parse %s to Date", temporal.getClass().getSimpleName()), e);
		}
	}
	
}

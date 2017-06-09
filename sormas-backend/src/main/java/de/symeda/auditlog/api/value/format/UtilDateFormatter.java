package de.symeda.auditlog.api.value.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.TemporalType;

/**
 * Formats {@link Date}s according to a configurable pattern.
 * 
 * @author Oliver Milke, Stefan Kock
 */
public class UtilDateFormatter implements ValueFormatter<Date> {

	public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DAY_PATTERN = "yyyy-MM-dd";
	public static final String HOUR_MIN_PATTERN = "HH:mm";

	private final String pattern;

	/**
	 * <ul>
	 * <li>{@link #TIMESTAMP_PATTERN}</li>
	 * </ul>
	 * 
	 * @see #toPattern(TemporalType)
	 */
	public UtilDateFormatter() {
		this(TIMESTAMP_PATTERN);
	}

	/**
	 * @param temporalType
	 * 			For this {@link TemporalType}, a matching {@link DateFormat} pattern shall be identified.
	 * @see #toPattern(TemporalType)
	 */
	public UtilDateFormatter(TemporalType temporalType) {

		this(toPattern(temporalType));
	}

	/**
	 * @param pattern
	 * 			The {@link DateFormat} pattern to use.
	 */
	public UtilDateFormatter(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return 	The {@link DateFormat} pattern to use.
	 */
	public String getPattern() {
		return pattern;
	}

	@Override
	public String format(Date value) {
		return new SimpleDateFormat(pattern).format(value);

	}

	/**
	 * @param temporalType
	 * 			For this {@link TemporalType}, a matching {@link DateFormat} pattern shall be identified. Returns a detailed pattern
	 * 			if temporalType is <code>null</code>.
	 * @return	A matching pattern for the given {@link TemporalType}.
	 */
	public static String toPattern(TemporalType temporalType) {

		final String pattern;

		if (temporalType == null) {
			pattern = TIMESTAMP_PATTERN;
		} else {

			switch (temporalType) {
			case DATE:
				pattern = DAY_PATTERN;
				break;
			case TIME:
				pattern = HOUR_MIN_PATTERN;
				break;
			case TIMESTAMP:
				pattern = TIMESTAMP_PATTERN;
				break;
			default:
				throw new IllegalArgumentException(temporalType.name());
			}

		}

		return pattern;
	}
}

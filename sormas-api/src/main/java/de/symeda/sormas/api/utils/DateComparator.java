package de.symeda.sormas.api.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

/**
 * Compares dates with {@code null = now} ({@link Type#DATETIME}) or {@code null = today} ({@link Type#DATE}) .
 */
public class DateComparator implements Comparator<Date> {

	public enum Type {
		DATE,
		DATETIME;
	}

	private static final DateComparator DATE_INSTANCE = new DateComparator(Type.DATE);
	private static final DateComparator DATETIME_INSTANCE = new DateComparator(Type.DATETIME);

	private final Type type;

	private <T extends Temporal> DateComparator(Type type) {

		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public int compare(Date o1, Date o2) {

		final int compare;
		switch (type) {
		case DATE:
			LocalDate ld1 = Optional.ofNullable(o1).map(UtilDate::toLocalDate).orElse(LocalDate.now());
			LocalDate ld2 = Optional.ofNullable(o2).map(UtilDate::toLocalDate).orElse(LocalDate.now());
			compare = ld1.compareTo(ld2);
			break;
		case DATETIME:
			LocalDateTime ldt1 = Optional.ofNullable(o1).map(UtilDate::toLocalDateTime).orElse(LocalDateTime.now());
			LocalDateTime ldt2 = Optional.ofNullable(o2).map(UtilDate::toLocalDateTime).orElse(LocalDateTime.now());
			compare = ldt1.compareTo(ldt2);
			break;
		default:
			throw new IllegalArgumentException(type.name());
		}

		return compare;
	}

	/**
	 * @see Type#DATE
	 */
	public static DateComparator getDateInstance() {
		return DATE_INSTANCE;
	}

	/**
	 * @see Type#DATETIME
	 */
	public static DateComparator getDateTimeInstance() {
		return DATETIME_INSTANCE;
	}
}

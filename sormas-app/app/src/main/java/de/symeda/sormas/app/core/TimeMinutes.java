package de.symeda.sormas.app.core;

/**
 * This class returns time periods in minutes.
 */
public enum TimeMinutes {

	NO_MINUTES(0),
	ONE(1),
	TWO(2),
	FORTY_FIVE_MINUTES(45),
	ONE_HOUR(60),
	ONE_HOUR_AND_HALF(90),
	ONE_DAY(1440),
	//30 days
	ONE_MONTH(43200),
	//60 days
	TWO_MONTHS(86400),
	//365 days
	ONE_YEAR(525600),
	//730 days
	TWO_YEAR(1051200);

	private long minutes;

	TimeMinutes(long minutes) {
		this.minutes = minutes;
	}

	public static long time(long days, long hours) {
		return days * 1440 + hours * 60;
	}

	public static long time(long days, long hours, long minutes) {
		return time(days, hours) + minutes;
	}

	public long getValue() {
		return minutes;
	}

	public long getNegativeValue() {
		return minutes * -1;
	}
}

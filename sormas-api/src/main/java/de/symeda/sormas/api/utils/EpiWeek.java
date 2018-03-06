package de.symeda.sormas.api.utils;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class EpiWeek implements Serializable {
	
	private final int year;
	private final int week;
	
	public EpiWeek(int year, int week) {
		this.year = year;
		this.week = week;
	}
	
	public int getYear() {
		return year;
	}
	
	public int getWeek() {
		return week;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EpiWeek other = (EpiWeek) obj;
		if (week != other.week)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(year, week);
	}

	@Override
	public String toString() {
		return week + "/" + year + " (" + DateHelper.formatDateWithoutYear(DateHelper.getEpiWeekStart(this)) + " - " + DateHelper.formatDateWithoutYear(DateHelper.getEpiWeekEnd(this)) + ")";
	}
	
	public String toShortString() {
		return week + "/" + year;
	}

}

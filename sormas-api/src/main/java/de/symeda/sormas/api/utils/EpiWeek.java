package de.symeda.sormas.api.utils;

import java.io.Serializable;
import java.util.Objects;

import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

@SuppressWarnings("serial")
public class EpiWeek implements Serializable, StatisticsGroupingKey {
	
	private final Integer year;
	private final Integer week;
	
	public EpiWeek(Integer year, Integer week) {
		this.year = year;
		this.week = week;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public Integer getWeek() {
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
		if (week == null) {
			if (other.week != null)
				return false;
		} else if (!week.equals(other.week))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(year, week);
	}

	@Override
	public String toString() {
		return "Wk " + week + (year != null ? ("-" + year + " (" + DateHelper.formatDateWithoutYear(DateHelper.getEpiWeekStart(this)) + " - " + DateHelper.formatDateWithoutYear(DateHelper.getEpiWeekEnd(this)) + ")") : "");
	}
	
	public String toShortString() {
		return "Wk " + week + (year != null ? ("-" + year) : "");
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {
		EpiWeek other = (EpiWeek) o;
		
		if (other == null) {
			throw new NullPointerException("Can't compare to null.");
		}
		if ((this.getYear() == null && other.getYear() != null) ||
				(this.getYear() != null && other.getYear() == null)) {
			throw new UnsupportedOperationException("Can't compare an epi week with a year to an epi week without a year");
		}
		
		if (this.equals(other)) {
			return 0;
		}
		if (this.getYear() != null) {
			if (this.getYear() < other.getYear()) {
				return -1;
			} else if (this.getYear().equals(other.getYear()) && this.getWeek() < other.getWeek()) {
				return -1;
			} else {
				return 1;
			}
		} else {
			if (this.getWeek() < other.getWeek()) {
				return -1;
			} else {
				return 1;
			}
		}
	}

}

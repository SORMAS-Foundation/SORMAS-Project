package de.symeda.sormas.api;

import java.io.Serializable;

import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class QuarterOfYear implements Serializable, StatisticsGroupingKey {

	private static final long serialVersionUID = -7158625755180563434L;

	private Quarter quarter;
	private Year year;

	public QuarterOfYear(Quarter quarter, Year year) {
		this.quarter = quarter;
		this.year = year;
	}

	public void increaseQuarter() {
		boolean increaseYear = quarter.increaseQuarter();
		if (increaseYear) {
			year.increaseYearBy(1);
		}
	}

	public Quarter getQuarter() {
		return quarter;
	}

	public Year getYear() {
		return year;
	}

	@Override
	public String toString() {
		return quarter.toString() + "/" + year.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((quarter == null) ? 0 : quarter.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuarterOfYear other = (QuarterOfYear) obj;
		if (quarter == null) {
			if (other.quarter != null)
				return false;
		} else if (!quarter.equals(other.quarter))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

	@Override
	public int keyCompareTo(StatisticsGroupingKey o) {
		if (o == null) {
			throw new NullPointerException("Can't compare to null.");
		}
		if (o.getClass() != this.getClass()) {
			throw new UnsupportedOperationException("Can't compare to class " + o.getClass().getName() + " that differs from " + this.getClass().getName());
		}

		if (this.equals(o)) {
			return 0;
		}
		if (this.getYear().keyCompareTo(((QuarterOfYear) o).getYear()) < 0 ||
				(this.getYear().equals(((QuarterOfYear) o).getYear()) &&
						this.getQuarter().keyCompareTo(((QuarterOfYear) o).getQuarter()) < 0)) {
			return -1;
		}
		return 1;
	}

}
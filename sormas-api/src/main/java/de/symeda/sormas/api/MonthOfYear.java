package de.symeda.sormas.api;

import java.io.Serializable;

import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class MonthOfYear implements Serializable, StatisticsGroupingKey {

	private static final long serialVersionUID = -5776682012649885759L;

	private Month month;
	private Year year;

	public MonthOfYear(Month month, Year year) {
		this.month = month;
		this.year = year;
	}

	public Month getMonth() {
		return month;
	}

	public Year getYear() {
		return year;
	}

	@Override
	public String toString() {
		return month.toString() + " " + year.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((month == null) ? 0 : month.hashCode());
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
		MonthOfYear other = (MonthOfYear) obj;
		if (month != other.month)
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
		if (this.getYear().keyCompareTo(((MonthOfYear) o).getYear()) < 0 ||
				(this.getYear().equals(((MonthOfYear) o).getYear()) && 
						this.getMonth().compareTo(((MonthOfYear) o).getMonth()) < 0)) {
			return -1;
		}
		return 1;
	}

}
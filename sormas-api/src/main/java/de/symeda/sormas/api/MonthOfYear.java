/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api;

import java.io.Serializable;

import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class MonthOfYear implements Serializable, Comparable<MonthOfYear>, StatisticsGroupingKey {

	private static final long serialVersionUID = -5776682012649885759L;

	private Month month;
	private Year year;

	public MonthOfYear(Month month, int year) {
		this.month = month;
		this.year = new Year(year);
	}

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
			throw new UnsupportedOperationException(
				"Can't compare to class " + o.getClass().getName() + " that differs from " + this.getClass().getName());
		}

		if (this.equals(o)) {
			return 0;
		}
		if (this.getYear().keyCompareTo(((MonthOfYear) o).getYear()) < 0
			|| (this.getYear().equals(((MonthOfYear) o).getYear()) && this.getMonth().compareTo(((MonthOfYear) o).getMonth()) < 0)) {
			return -1;
		}
		return 1;
	}

	@Override
	public int compareTo(MonthOfYear o) {
		return keyCompareTo(o);
	}
}

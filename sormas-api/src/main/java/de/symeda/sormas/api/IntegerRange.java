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

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;

public class IntegerRange implements Serializable, StatisticsGroupingKey {

	private static final long serialVersionUID = 4459253785381816968L;

	private Integer from;
	private Integer to;

	public IntegerRange(Integer from, Integer to) {
		this.from = from;
		this.to = to;
	}

	public Integer getFrom() {
		return from;
	}

	public Integer getTo() {
		return to;
	}

	@Override
	public String toString() {

		if (from != null && to != null) {
			if (from == to) {
				return String.valueOf(from);
			} else {
				return String.valueOf(from) + "-" + String.valueOf(to);
			}
		} else if (from == null && to != null) {
			return "< " + String.valueOf(to);
		} else if (from != null && to == null) {
			return String.valueOf(from) + "+";
		} else {
			return I18nProperties.getString(Strings.unknown);
		}
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		IntegerRange other = (IntegerRange) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
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
		IntegerRange oir = (IntegerRange) o;

		if (this.getFrom() == null) {
			return 1;
		} else if (oir.getFrom() == null) {
			return -1;
		}

		return Integer.compare(this.getFrom(), oir.getFrom());
	}
}

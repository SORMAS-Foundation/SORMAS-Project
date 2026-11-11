/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.climate;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * A day a district relink did not copy, because the new location already had a value of equal or higher rank: the
 * district's own value (still on the old location) beside the value the new location kept.
 */
@AuditedClass
public class ClimateRelinkDayDto implements Serializable {

	private static final long serialVersionUID = -5105616018946005170L;

	public static final String I18N_PREFIX = "ClimateRelinkDay";

	public static final String DATE = "date";
	public static final String DISTRICT_VALUE = "districtValue";
	public static final String KEPT_VALUE = "keptValue";
	public static final String SAME_VALUES = "sameValues";

	private final Date date;
	private final ClimateObservationIndexDto districtValue;
	private final ClimateObservationIndexDto keptValue;

	public ClimateRelinkDayDto(Date date, ClimateObservationIndexDto districtValue, ClimateObservationIndexDto keptValue) {
		this.date = date;
		this.districtValue = districtValue;
		this.keptValue = keptValue;
	}

	public Date getDate() {
		return date;
	}

	/**
	 * @return the district's own row for the day, on the location it moved away from
	 */
	public ClimateObservationIndexDto getDistrictValue() {
		return districtValue;
	}

	/**
	 * @return the row the new location kept for the day
	 */
	public ClimateObservationIndexDto getKeptValue() {
		return keptValue;
	}

	/**
	 * @return true when both rows hold the same values, for example after the district's value was entered as a manual
	 *         correction
	 */
	public boolean isSameValues() {
		return Objects.equals(districtValue.getTemperatureMin(), keptValue.getTemperatureMin())
			&& Objects.equals(districtValue.getTemperatureMean(), keptValue.getTemperatureMean())
			&& Objects.equals(districtValue.getTemperatureMax(), keptValue.getTemperatureMax())
			&& Objects.equals(districtValue.getRainfall(), keptValue.getRainfall())
			&& Objects.equals(districtValue.getRelativeHumidity(), keptValue.getRelativeHumidity())
			&& districtValue.getHumidityMeasure() == keptValue.getHumidityMeasure();
	}
}

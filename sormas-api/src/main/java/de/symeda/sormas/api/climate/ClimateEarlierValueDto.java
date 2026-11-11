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

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * Values a climate observation held before a later save replaced them, from the row's change history.
 */
@AuditedClass
public class ClimateEarlierValueDto implements Serializable {

	private static final long serialVersionUID = 6625096810462178519L;

	public static final String I18N_PREFIX = "ClimateEarlierValue";

	public static final String CHANGE_DATE = "changeDate";
	public static final String TEMPERATURE_MIN = "temperatureMin";
	public static final String TEMPERATURE_MEAN = "temperatureMean";
	public static final String TEMPERATURE_MAX = "temperatureMax";
	public static final String RAINFALL = "rainfall";
	public static final String RELATIVE_HUMIDITY = "relativeHumidity";
	public static final String HUMIDITY_MEASURE = "humidityMeasure";
	public static final String SOURCE = "source";
	public static final String SOURCE_DETAIL = "sourceDetail";

	private Date changeDate;
	private Double temperatureMin;
	private Double temperatureMean;
	private Double temperatureMax;
	private Double rainfall;
	private Double relativeHumidity;
	private HumidityMeasure humidityMeasure;
	private ClimateDataSource source;
	private String sourceDetail;

	/**
	 * @return when these values were saved
	 */
	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public Double getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(Double temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	public Double getTemperatureMean() {
		return temperatureMean;
	}

	public void setTemperatureMean(Double temperatureMean) {
		this.temperatureMean = temperatureMean;
	}

	public Double getTemperatureMax() {
		return temperatureMax;
	}

	public void setTemperatureMax(Double temperatureMax) {
		this.temperatureMax = temperatureMax;
	}

	public Double getRainfall() {
		return rainfall;
	}

	public void setRainfall(Double rainfall) {
		this.rainfall = rainfall;
	}

	public Double getRelativeHumidity() {
		return relativeHumidity;
	}

	public void setRelativeHumidity(Double relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}

	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}

	public void setHumidityMeasure(HumidityMeasure humidityMeasure) {
		this.humidityMeasure = humidityMeasure;
	}

	public ClimateDataSource getSource() {
		return source;
	}

	public void setSource(ClimateDataSource source) {
		this.source = source;
	}

	public String getSourceDetail() {
		return sourceDetail;
	}

	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}
}

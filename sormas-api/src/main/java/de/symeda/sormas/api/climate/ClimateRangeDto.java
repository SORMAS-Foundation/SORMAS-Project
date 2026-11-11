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

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * A normal range (climate) or usual range (cases): the 10th, 50th and 90th percentile of the values for the same time of
 * year in the reference years.
 */
@AuditedClass
public class ClimateRangeDto implements Serializable {

	private static final long serialVersionUID = 4920516380127731745L;

	private final double p10;
	private final double p50;
	private final double p90;

	public ClimateRangeDto(double p10, double p50, double p90) {
		this.p10 = p10;
		this.p50 = p50;
		this.p90 = p90;
	}

	public double getP10() {
		return p10;
	}

	public double getP50() {
		return p50;
	}

	public double getP90() {
		return p90;
	}

	/**
	 * @return true when the value is above the 90th percentile
	 */
	public boolean isAbove(double value) {
		return value > p90;
	}

	/**
	 * @return this range with every percentile multiplied by {@code factor}, for example counts turned into incidence
	 */
	public ClimateRangeDto scaled(double factor) {
		return new ClimateRangeDto(p10 * factor, p50 * factor, p90 * factor);
	}
}

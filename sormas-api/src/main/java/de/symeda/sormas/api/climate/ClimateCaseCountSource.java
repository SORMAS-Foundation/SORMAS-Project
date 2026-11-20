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

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * What the Climate dashboard counts for a disease (decision 147).
 */
public enum ClimateCaseCountSource {

	/** The disease's cases, by onset date else report date, with the classification filter: case surveillance is on. */
	CASES,
	/**
	 * The lab-confirmed counts of its weekly aggregate reports: aggregate reporting only. A week without a report has no
	 * value.
	 */
	AGGREGATE_LAB_CONFIRMED;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}

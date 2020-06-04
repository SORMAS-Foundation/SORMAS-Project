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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.statistics;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum StatisticsCaseSubAttribute {
	
	YEAR(true, true),
	QUARTER(true, true),
	MONTH(true, true),
	EPI_WEEK(true, true),
	QUARTER_OF_YEAR(true, true),
	MONTH_OF_YEAR(true, true),
	EPI_WEEK_OF_YEAR(true, true),
	DATE_RANGE(true, false),
	REGION(false, true),
	DISTRICT(false, true),
	COMMUNITY(false, true),
	HEALTH_FACILITY(false, true);
	
	private boolean usedForFilters;
	private boolean usedForGrouping;
	
	StatisticsCaseSubAttribute(boolean usedForFilters, boolean usedForGrouping) {
		this.usedForFilters = usedForFilters;
		this.usedForGrouping = usedForGrouping;
	}
	
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public boolean isUsedForFilters() {
		return usedForFilters;
	}	
	
	public boolean isUsedForGrouping() {
		return usedForGrouping;
	}
	
}
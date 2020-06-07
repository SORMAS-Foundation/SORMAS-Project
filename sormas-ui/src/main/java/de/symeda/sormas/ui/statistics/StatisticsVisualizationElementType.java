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
package de.symeda.sormas.ui.statistics;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum StatisticsVisualizationElementType {

	ROWS,
	COLUMNS;

	public String toString(StatisticsVisualizationType visualizationType) {
		if (visualizationType == StatisticsVisualizationType.CHART) {
			return I18nProperties.getEnumCaption(this, "Chart");
		} else {
			return I18nProperties.getEnumCaption(this);
		}
	}

	public String getEmptySelectionString(StatisticsVisualizationType visualizationType) {
		switch (this) {
		case ROWS:
			if (visualizationType == StatisticsVisualizationType.CHART) {
				return I18nProperties.getCaption(Captions.statisticsDontGroupSeries);
			} else {
				return I18nProperties.getCaption(Captions.statisticsDontGroupRows);
			}
		case COLUMNS:
			if (visualizationType == StatisticsVisualizationType.CHART) {
				return I18nProperties.getCaption(Captions.statisticsDontGroupX);
			} else {
				return I18nProperties.getCaption(Captions.statisticsDontGroupColumns);
			}
		default:
			return null;
		}
	}
}

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.dashboard.diagram;

import de.symeda.sormas.api.i18n.I18nProperties;

public class EpiCurveSeriesElement {

	private final String caption;
	private final String color;
	private final int[] values;

	public EpiCurveSeriesElement(String caption, String color, int[] values) {
		this.caption = I18nProperties.getCaption(caption);
		this.color = color;
		this.values = values;
	}

	public EpiCurveSeriesElement(Enum<?> group, String color, int[] values) {
		this.caption = group.toString();
		this.color = color;
		this.values = values;
	}

	String getCaption() {
		return caption;
	}

	String getColor() {
		return color;
	}

	int[] getValues() {
		return values;
	}
}

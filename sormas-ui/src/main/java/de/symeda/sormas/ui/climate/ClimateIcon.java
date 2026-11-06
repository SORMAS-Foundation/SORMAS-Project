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

package de.symeda.sormas.ui.climate;

import de.symeda.sormas.api.climate.ClimateVariable;

/**
 * Icons for the climate variables, which Vaadin Icons does not have (decision 111). The drawings are CSS masks in the
 * theme's views/climate.scss: an icon is as large as the text around it and takes its panel's colour from the CSS
 * color, so a size or colour style changes it like a font icon.
 */
public enum ClimateIcon {

	RAINFALL("climate-icon-rainfall"),
	TEMPERATURE("climate-icon-temperature"),
	HUMIDITY("climate-icon-humidity");

	/** Every climate icon has this style. */
	public static final String STYLE = "climate-icon";
	/** One and a half times the size of the text around it. */
	public static final String LARGE = "climate-icon-large";
	/** Grey instead of the panel colour. */
	public static final String MUTED = "climate-icon-muted";

	private final String style;

	ClimateIcon(String style) {
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

	/**
	 * @return the icon as HTML, for a label in HTML mode or a chart; extra styles such as {@link #LARGE} are added
	 */
	public String html(String... extraStyles) {
		StringBuilder classes = new StringBuilder(STYLE).append(' ').append(style);
		for (String extra : extraStyles) {
			classes.append(' ').append(extra);
		}
		return "<span class=\"" + classes + "\" aria-hidden=\"true\"></span>";
	}

	public static ClimateIcon of(ClimateVariable variable) {
		switch (variable) {
		case RAINFALL:
			return RAINFALL;
		case TEMPERATURE:
			return TEMPERATURE;
		default:
			return HUMIDITY;
		}
	}
}

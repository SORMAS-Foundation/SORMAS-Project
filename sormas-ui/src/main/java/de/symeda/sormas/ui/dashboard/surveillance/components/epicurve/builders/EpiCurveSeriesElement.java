package de.symeda.sormas.ui.dashboard.surveillance.components.epicurve.builders;

import de.symeda.sormas.api.i18n.I18nProperties;

public class EpiCurveSeriesElement {

	private final String caption;
	private final String color;
	private final int[] values;

	EpiCurveSeriesElement(String caption, String color, int[] values) {
		this.caption = I18nProperties.getCaption(caption);
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

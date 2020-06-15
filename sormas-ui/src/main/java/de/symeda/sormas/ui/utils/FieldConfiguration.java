package de.symeda.sormas.ui.utils;

import com.vaadin.server.Sizeable;

public class FieldConfiguration {

	private String propertyId;
	private Float width;
	private Sizeable.Unit widthUnit;
	private String caption;
	private String description;
	private String style;

	private FieldConfiguration(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public Float getWidth() {
		return width;
	}

	public Sizeable.Unit getWidthUnit() {
		return widthUnit;
	}

	public String getCaption() {
		return caption;
	}

	public String getDescription() {
		return description;
	}

	public String getStyle() {
		return style;
	}

	public static FieldConfiguration withCaptionAndPixelSized(String propertyId, String caption, float width) {
		FieldConfiguration configuration = pixelSized(propertyId, width);

		configuration.caption = caption;

		return configuration;
	}

	public static FieldConfiguration pixelSized(String propertyId, float width) {
		FieldConfiguration configuration = new FieldConfiguration(propertyId);
		configuration.width = width;
		configuration.widthUnit = Sizeable.Unit.PIXELS;

		return configuration;
	}

	public static FieldConfiguration withCaptionAndStyle(String propertyId, String caption, String description, String style) {
		FieldConfiguration configuration = new FieldConfiguration(propertyId);

		configuration.caption = caption;
		configuration.description = description;
		configuration.style = style;

		return configuration;
	}
}

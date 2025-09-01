package de.symeda.sormas.ui.utils;

import com.vaadin.server.Sizeable;
import com.vaadin.v7.data.Property;

public final class FieldConfiguration {

	private final String propertyId;
	private Float width;
	private Sizeable.Unit widthUnit;
	private String caption;
	private String description;
	private String style;
	private Property.ValueChangeListener valueChangeListener;

	private String validationMessageProperty;

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

	/**
	 * Get the listener to attach to the field
	 * 
	 * @return the listener
	 */
	public Property.ValueChangeListener getValueChangeListener() {
		return valueChangeListener;
	}

	public String getValidationMessageProperty() {
		return validationMessageProperty;
	}

	public static FieldConfiguration withConversionError(String propertyId, String validationMessageProperty) {
		return builder(propertyId).validationMessageProperty(validationMessageProperty).build();
	}

	public static FieldConfiguration withCaptionAndPixelSized(String propertyId, String caption, float width) {
		return builder(propertyId).widthPx(width).caption(caption).build();
	}

	public static FieldConfiguration pixelSized(String propertyId, float width) {
		return builder(propertyId).widthPx(width).build();
	}

	public static FieldConfiguration withCaptionAndStyle(String propertyId, String caption, String description, String style) {
		return builder(propertyId).caption(caption).description(description).style(style).build();
	}

	/**
	 * Create a new {@link Builder} for a field configuration.
	 */
	public static Builder builder(String propertyId) {
		return new Builder(propertyId);
	}

	public static final class Builder {

		private final String propertyId;
		private Float width;
		private Sizeable.Unit widthUnit;
		private String caption;
		private String description;
		private String style;
		private Property.ValueChangeListener valueChangeListener;
		private String validationMessageProperty;

		private Builder(String propertyId) {
			this.propertyId = propertyId;
		}

		public Builder widthPx(float width) {
			this.width = width;
			this.widthUnit = Sizeable.Unit.PIXELS;
			return this;
		}

		public Builder width(float width, Sizeable.Unit unit) {
			this.width = width;
			this.widthUnit = unit;
			return this;
		}

		public Builder caption(String caption) {
			this.caption = caption;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder style(String style) {
			this.style = style;
			return this;
		}

		public Builder valueChangeListener(Property.ValueChangeListener listener) {
			this.valueChangeListener = listener;
			return this;
		}

		public Builder validationMessageProperty(String validationMessageProperty) {
			this.validationMessageProperty = validationMessageProperty;
			return this;
		}

		public FieldConfiguration build() {
			FieldConfiguration configuration = new FieldConfiguration(propertyId);
			configuration.width = this.width;
			configuration.widthUnit = this.widthUnit;
			configuration.caption = this.caption;
			configuration.description = this.description;
			configuration.style = this.style;
			configuration.valueChangeListener = this.valueChangeListener;
			configuration.validationMessageProperty = this.validationMessageProperty;
			return configuration;
		}
	}
}

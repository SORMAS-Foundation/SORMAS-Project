package de.symeda.sormas.ui.utils.components.datetypeselector;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.ui.utils.CssStyles;

public class DateTypeSelectorComponent extends ComboBox {

	public static class Builder<E extends Enum<E>> {

		private final Class<E> dateType;

		private String id = "dateType";
		private int width = 200;

		private String dateTypePrompt;
		private Enum<E> defaultDateType;
		private boolean showCaption;

		public Builder(Class<E> dateType) {
			this.dateType = dateType;
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder dateTypePrompt(String dateTypePrompt) {
			this.dateTypePrompt = dateTypePrompt;
			return this;
		}

		public Builder defaultDateType(Enum<E> defaultDateType) {
			this.defaultDateType = defaultDateType;
			return this;
		}

		public Builder showCaption(boolean showCaption) {
			this.showCaption = showCaption;
			return this;
		}

		public DateTypeSelectorComponent build() {
			return new DateTypeSelectorComponent(this);
		}
	}

	private DateTypeSelectorComponent(Builder builder) {
		setId(builder.id);
		setWidth(builder.width, Unit.PIXELS);
		addItems(builder.dateType.getEnumConstants());
		if (builder.dateTypePrompt != null) {
			setInputPrompt(builder.dateTypePrompt);
		}
		if (builder.defaultDateType != null) {
			select(builder.defaultDateType);
		}
		if (builder.showCaption) {
			CssStyles.style(this, CssStyles.FORCE_CAPTION);
		}
	}
}

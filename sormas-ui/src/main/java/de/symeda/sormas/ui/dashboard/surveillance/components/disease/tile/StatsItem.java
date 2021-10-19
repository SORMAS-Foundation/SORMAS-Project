package de.symeda.sormas.ui.dashboard.surveillance.components.disease.tile;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class StatsItem extends HorizontalLayout {

	public static class Builder {

		private final String label;
		private final String value;

		private boolean isCritical;
		private boolean isSingleColumn;

		public Builder(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public Builder(String label, Long value) {
			this(label, value.toString());
		}

		public Builder critical(boolean isCritical) {
			this.isCritical = isCritical;
			return this;
		}

		public Builder singleColumn(boolean isSingleColumn) {
			this.isSingleColumn = isSingleColumn;
			return this;
		}

		public StatsItem build() {
			return new StatsItem(this);
		}
	}

	private StatsItem(Builder builder) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		Label nameLabel = new Label(I18nProperties.getCaption(builder.label));
		nameLabel.addStyleNames(CssStyles.LABEL_PRIMARY, builder.isCritical ? CssStyles.LABEL_CRITICAL : "", CssStyles.HSPACE_LEFT_3);
		addComponent(nameLabel);
		if (!builder.isSingleColumn) {
			setExpandRatio(nameLabel, 1);
		}

		Label valueLabel = new Label(builder.value);
		valueLabel.addStyleNames(
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_CAPTION_TRUNCATED,
			builder.isCritical ? CssStyles.LABEL_CRITICAL : "",
			builder.isSingleColumn ? CssStyles.HSPACE_LEFT_5 : CssStyles.ALIGN_CENTER);
		addComponent(valueLabel);
		setExpandRatio(valueLabel, builder.isSingleColumn ? 1f : 0.65f);
	}
}

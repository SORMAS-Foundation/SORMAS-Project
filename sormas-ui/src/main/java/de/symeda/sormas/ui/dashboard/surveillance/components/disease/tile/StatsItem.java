package de.symeda.sormas.ui.dashboard.surveillance.components.disease.tile;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.ui.utils.CssStyles;

public class StatsItem extends HorizontalLayout {

	public StatsItem(String label, String value, boolean isCritical, boolean singleColumn) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		Label nameLabel = new Label(label);
		CssStyles.style(nameLabel, CssStyles.LABEL_PRIMARY, isCritical ? CssStyles.LABEL_CRITICAL : "", CssStyles.HSPACE_LEFT_3);
		addComponent(nameLabel);
		if (!singleColumn) {
			setExpandRatio(nameLabel, 1);
		}

		Label valueLabel = new Label(value);
		CssStyles.style(
			valueLabel,
			CssStyles.LABEL_PRIMARY,
			isCritical ? CssStyles.LABEL_CRITICAL : "",
			singleColumn ? CssStyles.HSPACE_LEFT_5 : CssStyles.ALIGN_CENTER);
		addComponent(valueLabel);
		setExpandRatio(valueLabel, singleColumn ? 1f : 0.65f);
	}
}

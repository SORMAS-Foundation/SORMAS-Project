package de.symeda.sormas.ui.dashboard.surveillance.components.statistics.summary;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseSummaryElementComponent extends HorizontalLayout {

	private final Label totalLabel;

	public DiseaseSummaryElementComponent(String heading) {
		setMargin(false);
		setSpacing(false);

		Label headingLabel = new Label(I18nProperties.getString(heading));
		headingLabel.addStyleNames(CssStyles.LABEL_PRIMARY, CssStyles.LABEL_UPPERCASE, CssStyles.VSPACE_TOP_4);
		addComponent(headingLabel);

		totalLabel = new Label();
		totalLabel.addStyleNames(
			CssStyles.LABEL_PRIMARY,
			CssStyles.LABEL_CAPTION_TRUNCATED,
			CssStyles.LABEL_BOLD,
			CssStyles.LABEL_LARGE,
			CssStyles.HSPACE_LEFT_3,
			CssStyles.VSPACE_TOP_5);
		addComponent(totalLabel);
	}

	public DiseaseSummaryElementComponent(String heading, String defaultValue) {
		this(heading);
		updateTotalLabel(defaultValue);
	}

	public void updateTotalLabel(String value) {
		totalLabel.setValue(value);
	}
}

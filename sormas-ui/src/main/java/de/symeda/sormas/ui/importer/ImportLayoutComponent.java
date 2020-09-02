package de.symeda.sormas.ui.importer;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ImportLayoutComponent extends VerticalLayout {

	private static final long serialVersionUID = 3281659031721493105L;

	private Label headlineLabel;
	private Label infoTextLabel;
	private Button button;

	public ImportLayoutComponent(int step, String headline, String infoText, Resource buttonIcon, String buttonCaption) {
		setSpacing(false);
		setMargin(false);

		headlineLabel = new Label(I18nProperties.getString(Strings.step) + " " + step + ": " + headline);
		CssStyles.style(headlineLabel, CssStyles.H3);
		addComponent(headlineLabel);

		infoTextLabel = new Label(infoText);
		addComponent(infoTextLabel);

		if (buttonCaption != null) {
			button = ButtonHelper.createIconButtonWithCaption(
				"import-step-" + step,
				buttonCaption,
				buttonIcon,
				null,
				ValoTheme.BUTTON_PRIMARY,
				CssStyles.VSPACE_TOP_3);

			addComponent(button);
		}
	}

	public Button getButton() {
		return button;
	}
}

package de.symeda.sormas.ui.importer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
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
	private CheckBox checkbox;

	public ImportLayoutComponent(int step, String headline, String infoText, Resource buttonIcon, String buttonCaption) {
		this(step, headline, infoText, buttonIcon, buttonCaption, null, null);
	}

	public ImportLayoutComponent(
		int step,
		String headline,
		String infoText,
		Resource buttonIcon,
		String buttonCaption,
		String checkboxCaption,
		String checkboxDescription) {
		setSpacing(false);
		setMargin(false);

		headlineLabel = new Label(I18nProperties.getString(Strings.step) + " " + step + ": " + headline);
		CssStyles.style(headlineLabel, CssStyles.H3);
		addComponent(headlineLabel);

		if (infoText != null) {
			infoTextLabel = new Label(infoText);
			addComponent(infoTextLabel);
		}

		if (checkboxCaption != null) {
			checkbox = new CheckBox(checkboxCaption);
			checkbox.setValue(false);
			if (checkboxDescription != null) {
				HorizontalLayout checkboxBar = new HorizontalLayout();
				checkboxBar.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
				checkboxBar.setDescription(checkboxDescription);
				CssStyles.style(checkboxBar, CssStyles.VSPACE_TOP_3);
				checkboxBar.addComponent(checkbox);
				Label labelInfo = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
				checkboxBar.addComponent(labelInfo);
				addComponent(checkboxBar);
			} else {
				CssStyles.style(checkbox, CssStyles.VSPACE_TOP_3);
				addComponent(checkbox);
			}
		}

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

	public CheckBox getCheckbox() {
		return checkbox;
	}
}

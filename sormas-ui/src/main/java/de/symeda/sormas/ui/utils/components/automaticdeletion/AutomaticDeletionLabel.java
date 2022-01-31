package de.symeda.sormas.ui.utils.components.automaticdeletion;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class AutomaticDeletionLabel extends HorizontalLayout {

	public AutomaticDeletionLabel() {
		setMargin(false);
		setSpacing(false);

		String infoIconDesciption =
			String.format(I18nProperties.getString(Strings.infoAutomaticDeletionTooltip), "10/11/2031", "09/11/2021", "10 years");
		Label infoIcon = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoIcon.setDescription(infoIconDesciption, ContentMode.HTML);
		infoIcon.addStyleNames(CssStyles.VSPACE_TOP_4, CssStyles.HSPACE_RIGHT_4);
		addComponent(infoIcon);

		String infoText = String.format(I18nProperties.getString(Strings.infoAutomaticDeletion), "10/11/2031");
		Label infoTextLabel = new Label(infoText, ContentMode.HTML);
		infoTextLabel.addStyleName(CssStyles.VSPACE_TOP_4);
		addComponent(infoTextLabel);
	}
}

package de.symeda.sormas.ui.utils.components.linelisting.section;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

public class LineListingSection extends VerticalLayout {

	public LineListingSection(String caption) {
		setMargin(false);
		setSpacing(false);

		Label sectionTitle = new Label();
		sectionTitle.setValue(I18nProperties.getCaption(caption));
		sectionTitle.addStyleName(CssStyles.H3);
		addComponent(sectionTitle);
	}
}

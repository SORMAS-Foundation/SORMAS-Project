package de.symeda.sormas.ui.configuration.infrastructure.components;

import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class SearchField extends TextField {

	public SearchField() {
		setId("search");
		setWidth(200, Unit.PIXELS);
		setInputPrompt(I18nProperties.getString(Strings.promptSearch));
		setNullRepresentation("");
		CssStyles.style(this, CssStyles.FORCE_CAPTION);
	}
}

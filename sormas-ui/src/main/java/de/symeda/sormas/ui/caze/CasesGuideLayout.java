package de.symeda.sormas.ui.caze;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class CasesGuideLayout extends VerticalLayout {

	private static final long serialVersionUID = 8666113090299403477L;

	public CasesGuideLayout() {

		setMargin(true);
		setSpacing(false);

		Label lblHeadingTermsDefinition = new Label(I18nProperties.getString(Strings.headingExplanationOfTerms));
		CssStyles.style(lblHeadingTermsDefinition, CssStyles.H2);
		addComponent(lblHeadingTermsDefinition);

		Label lblHeadingCompleteness = new Label(I18nProperties.getString(Strings.headingCompleteness));
		CssStyles.style(lblHeadingCompleteness, CssStyles.H3, CssStyles.VSPACE_TOP_5);
		addComponent(lblHeadingCompleteness);
		Label lblCompletenessDescription = new Label(I18nProperties.getString(Strings.infoCaseCompleteness));
		lblCompletenessDescription.setContentMode(ContentMode.HTML);
		lblCompletenessDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblCompletenessDescription);
	}
}

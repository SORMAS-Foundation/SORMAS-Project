package de.symeda.sormas.ui.immunization.components.fields.info;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Label;

import de.symeda.sormas.ui.utils.CssStyles;

public class ImmunizationInfo extends VerticalLayout {

	public ImmunizationInfo(String heading) {
		setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(this, CssStyles.BACKGROUND_ROUNDED_CORNERS, CssStyles.BACKGROUND_SUB_CRITERIA, CssStyles.VSPACE_3, "v-scrollable");

		Label newCaseLabel = new Label(heading);
		CssStyles.style(newCaseLabel, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4);
		addComponent(newCaseLabel);
	}

}

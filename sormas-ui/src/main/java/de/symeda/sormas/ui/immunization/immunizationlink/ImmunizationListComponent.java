package de.symeda.sormas.ui.immunization.immunizationlink;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class ImmunizationListComponent extends VerticalLayout {

	public ImmunizationListComponent(String personuuid) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		ImmunizationList immunizationList = new ImmunizationList(personuuid);
		addComponent(immunizationList);
		immunizationList.reload();

		Label immunizationHeader = new Label(I18nProperties.getString(Strings.entityImmunization));
		immunizationHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(immunizationHeader);
	}
}

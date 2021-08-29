package de.symeda.sormas.ui.immunization.immunizationlink;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;

public class ImmunizationListComponent extends VerticalLayout {

	public static final String IMMUNIZATION_LOC = "immunizations";

	public ImmunizationListComponent(PersonReferenceDto personReferenceDto) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		ImmunizationList immunizationList = new ImmunizationList(personReferenceDto);
		addComponent(immunizationList);
		immunizationList.reload();

		Label immunizationHeader = new Label(I18nProperties.getString(Strings.entityImmunization));
		immunizationHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(immunizationHeader);
	}

	public static void addImmunizationListComponent(CustomLayout layout, PersonReferenceDto personReferenceDto) {
		VerticalLayout immunizationsLayout = new VerticalLayout();
		immunizationsLayout.setMargin(false);
		immunizationsLayout.setSpacing(false);

		ImmunizationListComponent immunizationList = new ImmunizationListComponent(personReferenceDto);
		immunizationList.addStyleName(CssStyles.SIDE_COMPONENT);
		immunizationsLayout.addComponent(immunizationList);
		layout.addComponent(immunizationsLayout, IMMUNIZATION_LOC);
	}

}

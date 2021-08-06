package de.symeda.sormas.ui.immunization.immunizationlink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ImmunizationListComponent extends VerticalLayout {

	public static final String IMMUNIZATION_LOC = "immunizations";

	private ImmunizationList immunizationList;

	private ImmunizationCriteria immunizationCriteria;
	private CaseReferenceDto caseReferenceDto;
	private PersonReferenceDto personReferenceDto;

	public ImmunizationListComponent(CaseReferenceDto caseReferenceDto, PersonReferenceDto personReferenceDto) {
		this.caseReferenceDto = caseReferenceDto;
		this.personReferenceDto = personReferenceDto;
		immunizationCriteria = new ImmunizationCriteria();

		if (caseReferenceDto != null) {
			immunizationCriteria.caze(caseReferenceDto);
		}

		if (personReferenceDto != null) {
			immunizationCriteria.person(personReferenceDto);
		}

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		immunizationList = new ImmunizationList(immunizationCriteria);
		addComponent(immunizationList);
		immunizationList.reload();

		Label immunizationHeader = new Label(I18nProperties.getString(Strings.entityImmunization));
		immunizationHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(immunizationHeader);

		if (caseReferenceDto != null && UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_CREATE)) {
			Button createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.immunizationNewImmunization));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(e -> ControllerProvider.getImmunizationController().create());
			componentHeader.addComponent(createButton);
			componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}

	}

	public static void addImmunizationListComponent(CustomLayout layout, PersonReferenceDto personReferenceDto) {
		addImmunizationListComponent(layout, null, personReferenceDto);
	}

	public static void addImmunizationListComponent(CustomLayout layout, CaseReferenceDto caseReferenceDto) {
		addImmunizationListComponent(layout, caseReferenceDto, null);
	}

	private static void addImmunizationListComponent(CustomLayout layout, CaseReferenceDto caseReferenceDto, PersonReferenceDto personReferenceDto) {
		VerticalLayout immunizationsLayout = new VerticalLayout();
		immunizationsLayout.setMargin(false);
		immunizationsLayout.setSpacing(false);

		ImmunizationListComponent immunizationList = new ImmunizationListComponent(caseReferenceDto, personReferenceDto);
		immunizationList.addStyleName(CssStyles.SIDE_COMPONENT);
		immunizationsLayout.addComponent(immunizationList);
		layout.addComponent(immunizationsLayout, IMMUNIZATION_LOC);
	}

}

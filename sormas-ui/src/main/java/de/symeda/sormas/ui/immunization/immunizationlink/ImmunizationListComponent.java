package de.symeda.sormas.ui.immunization.immunizationlink;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class ImmunizationListComponent extends SideComponent {

	public ImmunizationListComponent(ImmunizationListCriteria immunizationListCriteria) {

		super(I18nProperties.getString(Strings.entityImmunization));

		UserProvider currentUser = UserProvider.getCurrent();
		if (currentUser != null && currentUser.hasUserRight(UserRight.IMMUNIZATION_CREATE)) {
			Button createButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.immunizationNewImmunization));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(
				e -> ControllerProvider.getImmunizationController()
					.create(immunizationListCriteria.getPerson(), immunizationListCriteria.getDisease()));
			addCreateButton(createButton);
		}

		ImmunizationList immunizationList = new ImmunizationList(immunizationListCriteria);
		addComponent(immunizationList);
		immunizationList.reload();
	}
}

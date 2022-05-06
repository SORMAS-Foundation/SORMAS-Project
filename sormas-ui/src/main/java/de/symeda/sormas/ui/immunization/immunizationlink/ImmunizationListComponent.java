package de.symeda.sormas.ui.immunization.immunizationlink;

import java.util.function.Consumer;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

public class ImmunizationListComponent extends SideComponent {

	public ImmunizationListComponent(ImmunizationListCriteria immunizationListCriteria, Consumer<Runnable> actionCallback) {
		super(I18nProperties.getString(Strings.entityImmunization), actionCallback);

		addCreateButton(
			I18nProperties.getCaption(Captions.immunizationNewImmunization),
			() -> ControllerProvider.getImmunizationController().create(immunizationListCriteria.getPerson(), immunizationListCriteria.getDisease()),
			UserRight.IMMUNIZATION_CREATE);

		ImmunizationList immunizationList = new ImmunizationList(immunizationListCriteria);
		addComponent(immunizationList);
		immunizationList.reload();
	}
}

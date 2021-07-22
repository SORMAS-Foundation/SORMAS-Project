package de.symeda.sormas.ui.immunization;

import com.vaadin.navigator.Navigator;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.immunization.components.ImmunizationCreationForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ImmunizationController {

	public void registerViews(Navigator navigator) {

	}

	public void create() {
		CommitDiscardWrapperComponent<ImmunizationCreationForm> immunizationCreateComponent = getImmunizationCreateComponent();
		if (immunizationCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(immunizationCreateComponent, I18nProperties.getString(Strings.headingCreateNewImmunization));
		}
	}

	private CommitDiscardWrapperComponent<ImmunizationCreationForm> getImmunizationCreateComponent() {
		UserProvider currentUserProvider = UserProvider.getCurrent();
		if (currentUserProvider != null) {
			ImmunizationCreationForm immunizationCreationForm = new ImmunizationCreationForm();
			final CommitDiscardWrapperComponent<ImmunizationCreationForm> viewComponent = new CommitDiscardWrapperComponent<>(
				immunizationCreationForm,
				currentUserProvider.hasUserRight(UserRight.IMMUNIZATION_CREATE),
				immunizationCreationForm.getFieldGroup());
			return viewComponent;
		}
		return null;
	}
}

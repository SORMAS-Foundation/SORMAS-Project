package de.symeda.sormas.ui.immunization;

import com.vaadin.navigator.Navigator;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.immunization.components.ImmunizationDataForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class ImmunizationController {

	public void registerViews(Navigator navigator) {

	}

	public void create() {
		CommitDiscardWrapperComponent<ImmunizationDataForm> immunizationCreateComponent = getImmunizationCreateComponent();
		if (immunizationCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(immunizationCreateComponent, I18nProperties.getString(Strings.headingCreateNewImmunization));
		}
	}

	private CommitDiscardWrapperComponent<ImmunizationDataForm> getImmunizationCreateComponent() {
		UserProvider currentUserProvider = UserProvider.getCurrent();
		if (currentUserProvider != null) {
			ImmunizationDataForm immunizationDataForm = new ImmunizationDataForm();
			final CommitDiscardWrapperComponent<ImmunizationDataForm> viewComponent = new CommitDiscardWrapperComponent<>(
				immunizationDataForm,
				currentUserProvider.hasUserRight(UserRight.IMMUNIZATION_CREATE),
				immunizationDataForm.getFieldGroup());
			return viewComponent;
		}
		return null;
	}
}

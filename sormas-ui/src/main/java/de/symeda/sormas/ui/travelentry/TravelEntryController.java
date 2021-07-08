package de.symeda.sormas.ui.travelentry;

import com.vaadin.navigator.Navigator;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.travelentry.components.TravelEntryCreateForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TravelEntryController {

	public void registerViews(Navigator navigator) {

	}

	public void create() {
		CommitDiscardWrapperComponent<TravelEntryCreateForm> travelEntryCreateComponent = getTravelEntryCreateComponent();
		if (travelEntryCreateComponent != null) {
			VaadinUiUtil.showModalPopupWindow(travelEntryCreateComponent, I18nProperties.getString(Strings.headingCreateNewEntry));
		}
	}

	private CommitDiscardWrapperComponent<TravelEntryCreateForm> getTravelEntryCreateComponent() {
		UserProvider currentUserProvider = UserProvider.getCurrent();
		if (currentUserProvider != null) {
			TravelEntryCreateForm createForm = new TravelEntryCreateForm();
			final CommitDiscardWrapperComponent<TravelEntryCreateForm> editView = new CommitDiscardWrapperComponent<>(
				createForm,
				currentUserProvider.hasUserRight(UserRight.TRAVEL_ENTRY_CREATE),
				createForm.getFieldGroup());
			return editView;
		}
		return null;
	}
}

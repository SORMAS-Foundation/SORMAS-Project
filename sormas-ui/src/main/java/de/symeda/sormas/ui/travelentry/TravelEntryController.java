package de.symeda.sormas.ui.travelentry;

import com.vaadin.navigator.Navigator;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.travelentry.components.TravelEntryCreateForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TravelEntryController {

	public void registerViews(Navigator navigator) {
		navigator.addView(TravelEntriesView.VIEW_NAME, TravelEntriesView.class);
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

			editView.addCommitListener(() -> {
				if (!createForm.getFieldGroup().isModified()) {
					final TravelEntryDto dto = createForm.getValue();

					final PersonDto duplicatePerson = createForm.getPerson();

					ControllerProvider.getPersonController()
						.selectOrCreatePerson(duplicatePerson, I18nProperties.getString(Strings.infoSelectOrCreatePersonForCase), selectedPerson -> {
							if (selectedPerson != null) {
								dto.setPerson(selectedPerson);
								FacadeProvider.getTravelEntryFacade().save(dto);
							}
						}, true);
				}
			});

			return editView;
		}
		return null;
	}
}

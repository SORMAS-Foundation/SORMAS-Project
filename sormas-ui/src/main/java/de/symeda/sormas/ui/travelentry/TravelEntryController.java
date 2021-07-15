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
import de.symeda.sormas.ui.SormasUI;

public class TravelEntryController {

	public void registerViews(Navigator navigator) {
		navigator.addView(TravelEntriesView.VIEW_NAME, TravelEntriesView.class);
	}

	public void create() {
		CommitDiscardWrapperComponent<TravelEntryCreateForm> travelEntryCreateComponent = getTravelEntryCreateComponent();
		VaadinUiUtil.showModalPopupWindow(travelEntryCreateComponent, I18nProperties.getString(Strings.headingCreateNewTravelEntry));
	}

	private CommitDiscardWrapperComponent<TravelEntryCreateForm> getTravelEntryCreateComponent() {

		TravelEntryCreateForm createForm = new TravelEntryCreateForm();
		TravelEntryDto travelEntry = TravelEntryDto.build(null);
		travelEntry.setReportingUser(UserProvider.getCurrent().getUserReference());
		createForm.setValue(travelEntry);
		final CommitDiscardWrapperComponent<TravelEntryCreateForm> editView = new CommitDiscardWrapperComponent<>(
			createForm,
			UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {

				final TravelEntryDto dto = createForm.getValue();
				final PersonDto person = createForm.getPerson();
				ControllerProvider.getPersonController()
					.selectOrCreatePerson(person, I18nProperties.getString(Strings.infoSelectOrCreatePersonForCase), selectedPerson -> {
						if (selectedPerson != null) {
							dto.setPerson(selectedPerson);
							FacadeProvider.getTravelEntryFacade().save(dto);
						}
					}, true);
			}
		});

		return editView;
	}


	}

	public void navigateToTravelEntry(String uuid) {
		final String navigationState = TravelEntryDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}
}

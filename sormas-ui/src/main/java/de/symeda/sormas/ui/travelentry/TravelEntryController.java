package de.symeda.sormas.ui.travelentry;

import com.vaadin.navigator.Navigator;

import de.symeda.sormas.ui.SormasUI;

public class TravelEntryController {

	public void registerViews(Navigator navigator) {

	}

	public void navigateToTravelEntry(String uuid) {
		final String navigationState = TravelEntryDataView.VIEW_NAME + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}
}

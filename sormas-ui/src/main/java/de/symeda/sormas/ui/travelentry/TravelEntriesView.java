package de.symeda.sormas.ui.travelentry;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class TravelEntriesView extends AbstractView {

	public static final String VIEW_NAME = "entries";

	public TravelEntriesView() {
		super(VIEW_NAME);

		UserProvider currentUserProvider = UserProvider.getCurrent();
		if (currentUserProvider != null && currentUserProvider.hasUserRight(UserRight.TRAVEL_ENTRY_CREATE)) {
			final ExpandableButton createButton =
				new ExpandableButton(Captions.travelEntryNewEntry).expand(e -> ControllerProvider.getTravelEntryController().create());
			addHeaderComponent(createButton);
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

	}
}

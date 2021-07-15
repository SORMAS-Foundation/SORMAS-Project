package de.symeda.sormas.ui.travelentry;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractDetailView;

public class TravelEntryDataView extends AbstractDetailView<TravelEntryReferenceDto> {

	public static final String VIEW_NAME = TravelEntriesView.VIEW_NAME + "/data";

	public TravelEntryDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected TravelEntryReferenceDto getReferenceByUuid(String uuid) {
		final TravelEntryReferenceDto reference;
		if (FacadeProvider.getTravelEntryFacade().exists(uuid)) {
			reference = FacadeProvider.getTravelEntryFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return TravelEntriesView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {

	}
}

package de.symeda.sormas.ui.caze;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.ui.person.ExternalPersonDataComponent;

public class CaseExternalPersonDataView extends AbstractCaseView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/externalPersonData";

	public CaseExternalPersonDataView() {
		super(VIEW_NAME, false);
	}

	@Override
	protected void initView(String params) {
		if (!ExternalPersonDataComponent.isAvailable()) {
			UI.getCurrent().getNavigator().navigateTo(CaseDataView.VIEW_NAME + "/" + getReference().getUuid());
			return;
		}

		setSubComponent(new ExternalPersonDataComponent(FacadeProvider.getCaseFacade().getCaseDataByUuid(getReference().getUuid()).getPerson()));
	}
}

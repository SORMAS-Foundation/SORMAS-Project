package de.symeda.sormas.ui.caze;

import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public abstract class AbstractCaseView extends AbstractSubNavigationView {

	protected AbstractCaseView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, String entityUuid) {
		menu.removeAllViews();
		menu.addView(CasesView.VIEW_NAME, "Cases List");
		menu.addView(CaseDataView.VIEW_NAME, "Case Data", entityUuid);
		menu.addView(CasePersonView.VIEW_NAME, "Patient Information", entityUuid);
		menu.addView(CaseSymptomsView.VIEW_NAME, "Symptoms", entityUuid);
    }
}

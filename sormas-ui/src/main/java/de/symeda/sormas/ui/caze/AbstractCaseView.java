package de.symeda.sormas.ui.caze;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public abstract class AbstractCaseView extends AbstractSubNavigationView {

	private CaseReferenceDto caseRef;
	
	protected AbstractCaseView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, String params) {
		
		caseRef = FacadeProvider.getCaseFacade().getReferenceByUuid(params);
		
		menu.removeAllViews();
		menu.addView(CasesView.VIEW_NAME, "Cases List");
		menu.addView(CaseDataView.VIEW_NAME, "Case Data", params);
		menu.addView(CasePersonView.VIEW_NAME, "Person Data", params);
		menu.addView(CaseSymptomsView.VIEW_NAME, "Symptoms", params);
		menu.addView(CaseContactsView.VIEW_NAME, "Contacts", params);
    }

	public CaseReferenceDto getCaseRef() {
		return caseRef;
	}
}

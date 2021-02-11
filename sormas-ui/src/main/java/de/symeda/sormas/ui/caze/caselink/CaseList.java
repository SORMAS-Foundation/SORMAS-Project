package de.symeda.sormas.ui.caze.caselink;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

public class CaseList extends PaginationList<CaseIndexDto> {

	private final CaseCriteria caseCriteria = new CaseCriteria();
	private final Label noCaseLabel;

	public CaseList(PersonReferenceDto personRef) {
		super(5);
		caseCriteria.setPerson(personRef);
		caseCriteria.setIncludeCasesFromOtherJurisdictions(true);
		noCaseLabel = new Label(I18nProperties.getCaption(Captions.personNoCaseLinkedToPerson));
	}

	@Override
	public void reload() {
		List<CaseIndexDto> caseIndexDtos = FacadeProvider.getCaseFacade().getIndexList(caseCriteria, 0, maxDisplayedEntries * 20, null);

		setEntries(caseIndexDtos);
		if (!caseIndexDtos.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noCaseLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		List<CaseIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			final CaseIndexDto caze = displayedEntries.get(i);
			final CaseListEntry listEntry = new CaseListEntry(caze);
			if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT)) {
				listEntry.addEditListener(
					i,
					(Button.ClickListener) event -> ControllerProvider.getCaseController().navigateToCase(listEntry.getCaseIndexDto().getUuid()));
			}

			listLayout.addComponent(listEntry);
		}
	}
}

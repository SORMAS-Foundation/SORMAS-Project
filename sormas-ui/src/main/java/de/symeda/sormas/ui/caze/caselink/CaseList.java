package de.symeda.sormas.ui.caze.caselink;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseListEntryDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.PaginationList;

public class CaseList extends PaginationList<CaseListEntryDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final String personUuid;
	private final Label noCaseLabel;

	public CaseList(PersonReferenceDto personReferenceDto) {
		super(MAX_DISPLAYED_ENTRIES);
		this.personUuid = personReferenceDto != null ? personReferenceDto.getUuid() : null;
		noCaseLabel = new Label(I18nProperties.getCaption(Captions.personNoCaseLinkedToPerson));
	}

	@Override
	public void reload() {
		List<CaseListEntryDto> caseIndexDtos = FacadeProvider.getCaseFacade().getEntriesList(personUuid, 0, maxDisplayedEntries * 20);

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
		List<CaseListEntryDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			final CaseListEntryDto caze = displayedEntries.get(i);
			final CaseListEntry listEntry = new CaseListEntry(caze);
			final boolean isActiveCase = caze.getUuid().equals(getActiveUuid());
			if (isActiveCase) {
				listEntry.setActive();
			}
			if (UiUtil.permitted(UserRight.CASE_EDIT) && !isActiveCase) {
				listEntry.addEditButton(
					"edit-case-" + i,
					(Button.ClickListener) event -> ControllerProvider.getCaseController().navigateToCase(listEntry.getCaseListEntryDto().getUuid()));
			}

			listLayout.addComponent(listEntry);
		}
	}
}

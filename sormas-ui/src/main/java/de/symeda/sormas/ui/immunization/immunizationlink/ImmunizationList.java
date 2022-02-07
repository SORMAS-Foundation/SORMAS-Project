package de.symeda.sormas.ui.immunization.immunizationlink;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.immunization.ImmunizationListEntryDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.PaginationList;

public class ImmunizationList extends PaginationList<ImmunizationListEntryDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final ImmunizationListCriteria immunizationCriteria;

	public ImmunizationList(ImmunizationListCriteria immunizationCriteria) {
		super(MAX_DISPLAYED_ENTRIES);
		this.immunizationCriteria = immunizationCriteria;
	}

	@Override
	public void reload() {
		List<ImmunizationListEntryDto> immunizationsList =
			FacadeProvider.getImmunizationFacade().getEntriesList(immunizationCriteria, 0, maxDisplayedEntries * 20);

		setEntries(immunizationsList);
		if (!immunizationsList.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			Label noImmunizationsLabel = new Label(I18nProperties.getCaption(Captions.immunizationNoImmunizationsForPerson));
			listLayout.addComponent(noImmunizationsLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		for (ImmunizationListEntryDto immunization : getDisplayedEntries()) {
			ImmunizationListEntry listEntry = new ImmunizationListEntry(immunization);
			addEditButton(listEntry);
			listLayout.addComponent(listEntry);
		}
	}

	private void addEditButton(ImmunizationListEntry listEntry) {
		listEntry.addEditButton(
			"edit-immunization-" + listEntry.getImmunizationEntry().getUuid(),
			(Button.ClickListener) event -> ControllerProvider.getImmunizationController()
				.navigateToImmunization(listEntry.getImmunizationEntry().getUuid()));
	}
}

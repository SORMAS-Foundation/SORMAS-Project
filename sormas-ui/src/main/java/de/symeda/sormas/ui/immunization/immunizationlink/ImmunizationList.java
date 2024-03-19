package de.symeda.sormas.ui.immunization.immunizationlink;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.immunization.ImmunizationListEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

public class ImmunizationList extends PaginationList<ImmunizationListEntryDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final ImmunizationListCriteria immunizationCriteria;
	private boolean isEditAllowed;

	public ImmunizationList(ImmunizationListCriteria immunizationCriteria, boolean isEditAllowed) {
		super(MAX_DISPLAYED_ENTRIES);
		this.immunizationCriteria = immunizationCriteria;
		this.isEditAllowed = isEditAllowed;
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
		UserProvider currentUser = UserProvider.getCurrent();
		for (ImmunizationListEntryDto immunization : getDisplayedEntries()) {
			ImmunizationListEntry listEntry = new ImmunizationListEntry(immunization);
			boolean isActiveImmunization = immunization.getUuid().equals(getActiveUuid());
			if (isActiveImmunization) {
				listEntry.setActive();
			}
			if (currentUser != null && !isActiveImmunization) {
				boolean isEditableAndHasEditRight = UiUtil.permitted(isEditAllowed, UserRight.IMMUNIZATION_EDIT);
				listEntry.addActionButton(
					listEntry.getImmunizationEntry().getUuid(),
					(Button.ClickListener) event -> ControllerProvider.getImmunizationController()
						.navigateToImmunization(listEntry.getImmunizationEntry().getUuid()),
					isEditableAndHasEditRight);
				listEntry.setEnabled(isEditableAndHasEditRight);
			}

			listLayout.addComponent(listEntry);
		}
	}
}

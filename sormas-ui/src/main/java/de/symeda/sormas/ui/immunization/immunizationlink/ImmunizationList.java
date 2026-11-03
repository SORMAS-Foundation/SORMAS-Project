/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.immunization.immunizationlink;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
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
	private final ReferenceDto origin;

	public ImmunizationList(ImmunizationListCriteria immunizationCriteria, boolean isEditAllowed) {
		this(immunizationCriteria, isEditAllowed, null);
	}

	/**
	 * @param origin
	 *            Case, contact or event participant this list is displayed on, or {@code null}. If set, opening an immunization from
	 *            this list adds it to the URL, e.g. {@code immunizations/data/XYZ789-...?caseUuid=ABC123-...} for a case, so the
	 *            immunization views show a link back to it.
	 */
	public ImmunizationList(ImmunizationListCriteria immunizationCriteria, boolean isEditAllowed, ReferenceDto origin) {
		super(MAX_DISPLAYED_ENTRIES);
		this.immunizationCriteria = immunizationCriteria;
		this.isEditAllowed = isEditAllowed;
		this.origin = origin;
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
						.navigateToImmunization(listEntry.getImmunizationEntry().getUuid(), origin),
					isEditableAndHasEditRight);
				listEntry.setEnabled(isEditableAndHasEditRight);
			}

			listLayout.addComponent(listEntry);
		}
	}
}

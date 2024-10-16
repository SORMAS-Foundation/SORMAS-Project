/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.aefilink;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiListCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiListEntryDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class AefiList extends PaginationList<AefiListEntryDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private AefiListCriteria aefiListCriteria;
	private Consumer<Runnable> actionCallback;
	private final boolean isEditable;
	private Label noAdverseEventsLabel;

	public AefiList(AefiListCriteria aefiListCriteria, Consumer<Runnable> actionCallback, boolean isEditable) {

		super(MAX_DISPLAYED_ENTRIES);

		this.aefiListCriteria = aefiListCriteria;
		this.actionCallback = actionCallback;
		this.isEditable = isEditable;

		noAdverseEventsLabel = new Label(I18nProperties.getString(Strings.infoNoImmunizationAdverseEvents));
	}

	@Override
	public void reload() {

		List<AefiListEntryDto> listEntries = FacadeProvider.getAefiFacade().getEntriesList(aefiListCriteria, 0, maxDisplayedEntries * 20);

		setEntries(listEntries);
		if (!listEntries.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noAdverseEventsLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {

		List<AefiListEntryDto> displayedEntries = getDisplayedEntries();
		for (AefiListEntryDto aefiListEntry : displayedEntries) {
			AefiListEntry listEntry = new AefiListEntry(aefiListEntry);

			String aefiUuid = aefiListEntry.getUuid();
			listEntry.addEditButton(
				"edit-aefi-" + aefiUuid,
				(Button.ClickListener) event -> ControllerProvider.getAefiController().navigateToAefi(aefiUuid));

			listEntry.setEnabled(isEditable);
			listLayout.addComponent(listEntry);
		}
	}
}

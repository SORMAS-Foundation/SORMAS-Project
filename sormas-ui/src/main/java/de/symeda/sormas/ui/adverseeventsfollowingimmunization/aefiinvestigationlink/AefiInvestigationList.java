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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization.aefiinvestigationlink;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationListCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationListEntryDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.PaginationList;

@SuppressWarnings("serial")
public class AefiInvestigationList extends PaginationList<AefiInvestigationListEntryDto> {

	private static final int MAX_DISPLAYED_ENTRIES = 5;

	private final AefiInvestigationListCriteria listCriteria;
	private final Consumer<Runnable> actionCallback;
	private final boolean isEditable;
	private final Label noInvestigationsLabel;

	public AefiInvestigationList(AefiInvestigationListCriteria listCriteria, Consumer<Runnable> actionCallback, boolean isEditable) {

		super(MAX_DISPLAYED_ENTRIES);

		this.listCriteria = listCriteria;
		this.actionCallback = actionCallback;
		this.isEditable = isEditable;

		noInvestigationsLabel = new Label(I18nProperties.getString(Strings.infoNoAefiInvestigations));
	}

	@Override
	public void reload() {

		List<AefiInvestigationListEntryDto> listEntries =
			FacadeProvider.getAefiInvestigationFacade().getEntriesList(listCriteria, 0, maxDisplayedEntries * 20);

		setEntries(listEntries);
		if (CollectionUtils.isNotEmpty(listEntries)) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noInvestigationsLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {

		List<AefiInvestigationListEntryDto> displayedEntries = getDisplayedEntries();
		for (AefiInvestigationListEntryDto listEntryDto : displayedEntries) {
			AefiInvestigationListEntry listEntry = new AefiInvestigationListEntry(listEntryDto);

			String aefiInvestigationUuid = listEntryDto.getUuid();
			listEntry.addEditButton(
				"edit-aefiinvestigation-" + aefiInvestigationUuid,
				(Button.ClickListener) event -> ControllerProvider.getAefiInvestigationController()
					.navigateToAefiInvestigation(aefiInvestigationUuid));

			listEntry.setEnabled(isEditable);
			listLayout.addComponent(listEntry);
		}
	}
}

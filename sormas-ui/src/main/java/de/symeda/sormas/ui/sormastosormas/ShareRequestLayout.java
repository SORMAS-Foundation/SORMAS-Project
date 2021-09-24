/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import static de.symeda.sormas.ui.utils.CssStyles.H3;

import java.util.Collections;
import java.util.List;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasEventPreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ShareRequestLayout extends VerticalLayout {

	private static final long serialVersionUID = -2456820286350054385L;

	public ShareRequestLayout(SormasToSormasShareRequestDto shareRequest) {

		ShareRequestDataType dataType = shareRequest.getDataType();

		if (dataType == ShareRequestDataType.CASE) {
			Label casesLabel = new Label(I18nProperties.getString(Strings.headingShareRequestCases));
			casesLabel.addStyleName(H3);
			addComponent(casesLabel);
			List<SormasToSormasCasePreview> cases = shareRequest.getCases();
			CasePreviewGrid casesGrid = new CasePreviewGrid(cases);
			addComponent(casesGrid);

			ContactsPreviewGrid contactsGrid = addContactsGrid(Collections.emptyList());
			casesGrid.getSelectionModel().addSelectionListener(event -> {
				event.getFirstSelectedItem().ifPresent(casePreview -> {
					if (shareRequest.getContacts() != null) {
						contactsGrid.setItems(shareRequest.getContacts().stream().filter(c -> DataHelper.isSame(c.getCaze(), casePreview)));
					}
				});
			});

			if (cases.size() > 0) {
				casesGrid.getSelectionModel().select(cases.get(0));
			}
		}

		if (dataType == ShareRequestDataType.CONTACT) {
			addContactsGrid(shareRequest.getContacts());
		}

		if (dataType == ShareRequestDataType.EVENT) {
			Label eventsLabel = new Label(I18nProperties.getString(Strings.headingShareRequestEvents));
			eventsLabel.addStyleName(H3);
			addComponent(eventsLabel);
			List<SormasToSormasEventPreview> events = shareRequest.getEvents();
			EventPreviewGrid eventsGrid = new EventPreviewGrid(events);
			addComponent(eventsGrid);

			Label casesLabel = new Label(I18nProperties.getString(Strings.headingShareRequestEventParticipants));
			casesLabel.addStyleName(H3);
			addComponent(casesLabel);
			EventParticipantsPreviewGrid eventParticipantsGrid = new EventParticipantsPreviewGrid();
			addComponent(eventParticipantsGrid);

			eventsGrid.getSelectionModel().addSelectionListener(event -> {
				event.getFirstSelectedItem().ifPresent(eventPreview -> {
					if (shareRequest.getEventParticipants() != null) {
						eventParticipantsGrid
							.setItems(shareRequest.getEventParticipants().stream().filter(ep -> DataHelper.isSame(ep.getEvent(), eventPreview)));
					}
				});
			});

			if (events.size() > 0) {
				eventsGrid.getSelectionModel().select(events.get(0));
			}

		}
	}

	private ContactsPreviewGrid addContactsGrid(List<SormasToSormasContactPreview> contacts) {
		ContactsPreviewGrid contactsGrid;
		Label casesLabel = new Label(I18nProperties.getString(Strings.headingShareRequestContacts));
		casesLabel.addStyleName(H3);
		addComponent(casesLabel);

		contactsGrid = new ContactsPreviewGrid(contacts);
		addComponent(contactsGrid);
		return contactsGrid;
	}
}

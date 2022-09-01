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

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.share.ShareRequestDetailsDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasContactPreview;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasEventPreview;
import de.symeda.sormas.api.utils.DataHelper;

public class ShareRequestLayout extends VerticalLayout {

	private static final long serialVersionUID = -2456820286350054385L;

	private Runnable navigateCallback;

	public ShareRequestLayout(ShareRequestDetailsDto shareRequest) {

		ShareRequestDataType dataType = shareRequest.getDataType();
		boolean isPending = shareRequest.getStatus() == ShareRequestStatus.PENDING;

		if (dataType == ShareRequestDataType.CASE) {
			Label casesLabel = new Label(I18nProperties.getString(Strings.headingShareRequestCases));
			casesLabel.addStyleName(H3);
			addComponent(casesLabel);
			List<SormasToSormasCasePreview> cases = shareRequest.getCases();
			CasePreviewGrid casesGrid = new CasePreviewGrid(cases, isPending);
			casesGrid.addNavigateListener(this::onNavigateFromGrid);
			addComponent(casesGrid);

			List<SormasToSormasContactPreview> contacts = shareRequest.getContacts();

			if (CollectionUtils.isNotEmpty(contacts)) {
				ContactsPreviewGrid contactsGrid = addContactsGrid(Collections.emptyList(), isPending);
				contactsGrid.addNavigateListener(this::onNavigateFromGrid);
				casesGrid.getSelectionModel().addSelectionListener(event -> {
					event.getFirstSelectedItem().ifPresent(casePreview -> {
						contactsGrid.setItems(contacts.stream().filter(c -> DataHelper.isSame(c.getCaze(), casePreview)));
					});
				});
			}

			if (cases.size() > 0) {
				casesGrid.getSelectionModel().select(cases.get(0));
			}
		}

		if (dataType == ShareRequestDataType.CONTACT) {
			ContactsPreviewGrid contactsGrid = addContactsGrid(shareRequest.getContacts(), isPending);
			contactsGrid.addNavigateListener(this::onNavigateFromGrid);
		}

		if (dataType == ShareRequestDataType.EVENT) {
			Label eventsLabel = new Label(I18nProperties.getString(Strings.headingShareRequestEvents));
			eventsLabel.addStyleName(H3);
			addComponent(eventsLabel);
			List<SormasToSormasEventPreview> events = shareRequest.getEvents();
			EventPreviewGrid eventsGrid = new EventPreviewGrid(events, isPending);
			eventsGrid.addNavigateListener(this::onNavigateFromGrid);
			addComponent(eventsGrid);

			Label casesLabel = new Label(I18nProperties.getString(Strings.headingShareRequestEventParticipants));
			casesLabel.addStyleName(H3);
			addComponent(casesLabel);
			EventParticipantsPreviewGrid eventParticipantsGrid = new EventParticipantsPreviewGrid(isPending);
			eventParticipantsGrid.addNavigateListener(this::onNavigateFromGrid);
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

	public void setNavigateCallback(Runnable navigateCallback) {
		this.navigateCallback = navigateCallback;
	}

	private ContactsPreviewGrid addContactsGrid(List<SormasToSormasContactPreview> contacts, boolean isPending) {
		Label casesLabel = new Label(I18nProperties.getString(Strings.headingShareRequestContacts));
		casesLabel.addStyleName(H3);
		addComponent(casesLabel);

		ContactsPreviewGrid contactsGrid = new ContactsPreviewGrid(contacts, isPending);
		addComponent(contactsGrid);

		return contactsGrid;
	}

	private void onNavigateFromGrid(BasePreviewGrid.NavigateEvent event1) {
		if (navigateCallback != null) {
			navigateCallback.run();
		}
	}
}

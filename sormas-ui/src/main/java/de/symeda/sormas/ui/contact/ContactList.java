/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.contact;

import java.util.List;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
public class ContactList extends PaginationList<ContactIndexDto> {

	private final ContactCriteria contactCriteria = new ContactCriteria();

	public ContactList(EventParticipantReferenceDto eventParticipantRef) {
		super(5);
		contactCriteria.setEventParticipant(eventParticipantRef);
	}

	@Override
	public void reload() {
		List<ContactIndexDto> contactList = FacadeProvider.getContactFacade().getIndexList(contactCriteria, 0, maxDisplayedEntries * 20, null);

		setEntries(contactList);
		if (!contactList.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			Label noContactsLabel = new Label(I18nProperties.getCaption(Captions.contactNoContactsForEventParticipant));
			listLayout.addComponent(noContactsLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		SormasUI ui = (SormasUI)getUI();
		List<ContactIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			ContactIndexDto contact = displayedEntries.get(i);
			ContactListEntry listEntry = new ContactListEntry(contact);

			if (ui.getUserProvider().hasUserRight(UserRight.CONTACT_DELETE)) {
				listEntry.addDeleteListener(
					i,
					(ClickListener) event -> ControllerProvider.getContactController().deleteContact(listEntry.getContact(), this::reload));
			}

			if (ui.getUserProvider().hasUserRight(UserRight.CONTACT_EDIT)) {
				listEntry.addEditListener(
					i,
					(ClickListener) event -> ControllerProvider.getContactController().navigateToData(listEntry.getContact().getUuid()));
			}

			listLayout.addComponent(listEntry);
		}
	}

	protected void filterContactListBySourceCaseInGivenEvent(EventReferenceDto eventRef) {
		contactCriteria.setOnlyContactsWithSourceCaseInGivenEvent(eventRef);
	}
}

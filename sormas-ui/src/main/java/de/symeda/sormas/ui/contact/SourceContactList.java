/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.contact;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.utils.PaginationList;

public class SourceContactList extends PaginationList<ContactIndexDto> {

	private static final long serialVersionUID = -8266250137859127204L;

	private final ContactCriteria criteria = new ContactCriteria();

	public SourceContactList(CaseReferenceDto caseReference) {

		super(5);
		criteria.resultingCase(caseReference);
	}

	@Override
	public void reload() {

		List<ContactIndexDto> contacts = FacadeProvider.getContactFacade().getIndexList(criteria, 0, maxDisplayedEntries * 20, null);

		setEntries(contacts);
		if (!contacts.isEmpty()) {
			showPage(1);
		} else {
			updatePaginationLayout();
			listLayout.addComponent(new Label(I18nProperties.getCaption(Captions.epiDataNoSourceContacts)));
		}
	}

	@Override
	protected void drawDisplayedEntries() {
		boolean hasUserRightContactEdit = ((SormasUI) getUI()).getUserProvider().hasUserRight(UserRight.CONTACT_EDIT);
		List<ContactIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			ContactIndexDto contact = displayedEntries.get(i);
			SourceContactListEntry listEntry = new SourceContactListEntry(contact);
			if (hasUserRightContactEdit) {
				listEntry.addEditListener(
					i,
					(ClickListener) e -> ControllerProvider.getContactController().navigateToData(listEntry.getContact().getUuid()));
			}
			listLayout.addComponent(listEntry);
		}
	}

	public int getSize() {
		return getEntries().size();
	}

	public List<ContactReferenceDto> getSourceContacts() {
		return getEntries().stream()
			.map(
				c -> new ContactReferenceDto(
					c.getUuid(),
					c.getFirstName(),
					c.getLastName(),
					c.getCaze() != null ? c.getCaze().getFirstName() : null,
					c.getCaze() != null ? c.getCaze().getLastName() : null))
			.collect(Collectors.toList());
	}
}

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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.contact.ContactListEntryDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentField;

public class ContactListEntry extends SideComponentField {

	private final ContactListEntryDto contact;
	private Button editButton;
	private Button deleteButton;

	public ContactListEntry(ContactListEntryDto contact) {

		this.contact = contact;

		VerticalLayout leftLayout = new VerticalLayout();
		leftLayout.setMargin(false);
		leftLayout.setSpacing(false);

		Label contactUuid = new Label(DataHelper.toStringNullable(DataHelper.getShortUuid(contact.getUuid())));
		contactUuid.addStyleNames(CssStyles.LABEL_BOLD, CssStyles.LABEL_UPPERCASE);
		contactUuid.setDescription(contact.getUuid());
		leftLayout.addComponent(contactUuid);

		Label contactClassification = new Label(contact.getContactClassification().toString());
		contactClassification.addStyleName(CssStyles.LABEL_BOLD);
		contactClassification.setDescription(contact.getContactClassification().toString());
		contactClassification.setWidthFull();
		leftLayout.addComponent(contactClassification);

		Label contactStatus = new Label(contact.getContactStatus().toString());
		contactStatus.setDescription(contact.getContactStatus().toString());
		contactStatus.setWidthFull();
		leftLayout.addComponent(contactStatus);

		if (contact.getLastContactDate() != null) {
			Label lastContactDate = new Label(
				I18nProperties.getPrefixCaption(ContactListEntryDto.I18N_PREFIX, ContactListEntryDto.LAST_CONTACT_DATE) + ": "
					+ DateFormatHelper.formatDate(contact.getLastContactDate()));
			leftLayout.addComponent(lastContactDate);
		}
		addComponentToField(leftLayout);
	}

	public void addEditListener(int rowIndex, ClickListener editClickListener) {
		if (editButton == null) {
			editButton = ButtonHelper.createIconButtonWithCaption(
				"edit-contact-" + rowIndex,
				null,
				VaadinIcons.PENCIL,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}

		editButton.addClickListener(editClickListener);
	}

	public void addDeleteListener(int rowIndex, ClickListener deleteClickListener) {
		if (deleteButton == null) {
			deleteButton = ButtonHelper.createIconButtonWithCaption(
				"delete-contact-" + rowIndex,
				null,
				VaadinIcons.TRASH,
				null,
				ValoTheme.BUTTON_LINK,
				CssStyles.BUTTON_COMPACT);

			addComponent(deleteButton);
			setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(deleteButton, 0);
		}

		deleteButton.addClickListener(deleteClickListener);
	}

	public ContactListEntryDto getContact() {
		return contact;
	}
}

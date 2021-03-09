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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

import javax.validation.constraints.NotNull;

public class SourceContactListComponent extends VerticalLayout {

	private static final long serialVersionUID = -168334035260718276L;

	private SourceContactList list;
	private final CaseReferenceDto caseReference;

	public SourceContactListComponent(@NotNull final SormasUI ui, CaseReferenceDto caseReference) {
		this.caseReference = caseReference;
		createSourceContactListComponent(ui, new SourceContactList(caseReference));
	}

	private void createSourceContactListComponent(@NotNull final SormasUI ui, SourceContactList sourceContactList) {
		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);

		HorizontalLayout componentHeader = new HorizontalLayout();
		componentHeader.setMargin(false);
		componentHeader.setSpacing(false);
		componentHeader.setWidth(100, Unit.PERCENTAGE);
		addComponent(componentHeader);

		list = sourceContactList;
		addComponent(list);
		list.reload();

		Label sourceContactsHeader = new Label(I18nProperties.getString(Strings.headingEpiDataSourceCaseContacts));
		sourceContactsHeader.addStyleName(CssStyles.H3);
		componentHeader.addComponent(sourceContactsHeader);

		if (ui.getUserProvider().hasUserRight(UserRight.CONTACT_CREATE)) {
			Button createButton = ButtonHelper.createIconButton(
				Captions.contactNewContact,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getContactController().create(ui, caseReference, true, SormasUI::refreshView),
				ValoTheme.BUTTON_PRIMARY);
			componentHeader.addComponent(createButton);
			componentHeader.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}
	}

	public int getSize() {
		return list.getSize();
	}

	public List<ContactReferenceDto> getEntries() {
		return list.getSourceContacts();
	}

}

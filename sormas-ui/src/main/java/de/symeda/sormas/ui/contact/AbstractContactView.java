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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.contact;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public abstract class AbstractContactView extends AbstractSubNavigationView {

	private ContactReferenceDto contactRef;

	public static final String ROOT_VIEW_NAME = ContactsView.VIEW_NAME;
	
	protected AbstractContactView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		if (params.endsWith("/")) {
			params = params.substring(0, params.length() - 1);
		}
		
		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(params);
		contactRef = FacadeProvider.getContactFacade().getReferenceByUuid(contact.getUuid());
		
		if (FacadeProvider.getContactFacade().isDeleted(contact.getUuid())) {
			setEnabled(false);
		}
		
		menu.removeAllViews();
		menu.addView(ContactsView.VIEW_NAME, I18nProperties.getCaption(Captions.contactContactsList));
		menu.addView(CaseContactsView.VIEW_NAME, I18nProperties.getCaption(Captions.contactCaseContacts), contact.getCaze().getUuid(), true);
		menu.addView(ContactDataView.VIEW_NAME, I18nProperties.getCaption(ContactDto.I18N_PREFIX), params);
		menu.addView(ContactPersonView.VIEW_NAME, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.PERSON), params);
		menu.addView(ContactVisitsView.VIEW_NAME, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.VISITS), params);
		
		infoLabel.setValue(contactRef.getCaption());
		CaseDataDto caseData = FacadeProvider.getCaseFacade().getCaseDataByUuid(contact.getCaze().getUuid());
		infoLabelSub.setValue(
				caseData.getDisease() != Disease.OTHER 
				? caseData.getDisease().toShortString()
				: DataHelper.toStringNullable(caseData.getDiseaseDetails()));
    }

	public ContactReferenceDto getContactRef() {
		return contactRef;
	}
}

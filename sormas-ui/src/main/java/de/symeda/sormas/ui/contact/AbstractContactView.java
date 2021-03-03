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

import java.util.Collections;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.epidata.ContactEpiDataView;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DirtyStateComponent;
import de.symeda.sormas.ui.utils.ExternalJournalUtil;

@SuppressWarnings("serial")
public abstract class AbstractContactView extends AbstractDetailView<ContactReferenceDto> {

	public static final String ROOT_VIEW_NAME = ContactsView.VIEW_NAME;

	protected AbstractContactView(String viewName) {
		super(viewName);
	}

	@Override
	public void enter(ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {

		if (!findReferenceByParams(params)) {
			return;
		}

		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(getReference().getUuid());

		menu.removeAllViews();
		menu.addView(ContactsView.VIEW_NAME, I18nProperties.getCaption(Captions.contactContactsList));
		if (contact.getCaze() != null) {
			menu.addView(CaseContactsView.VIEW_NAME, I18nProperties.getCaption(Captions.contactCaseContacts), contact.getCaze().getUuid(), true);
		}
		menu.addView(ContactDataView.VIEW_NAME, I18nProperties.getCaption(ContactDto.I18N_PREFIX), params);
		menu.addView(ContactPersonView.VIEW_NAME, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.PERSON), params);
		menu.addView(ContactEpiDataView.VIEW_NAME, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.EPI_DATA), params);
		menu.addView(ContactVisitsView.VIEW_NAME, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.VISITS), params);

		setMainHeaderComponent(ControllerProvider.getContactController().getContactViewTitleLayout(contact));

		if (UserProvider.getCurrent().hasUserRight(UserRight.MANAGE_EXTERNAL_SYMPTOM_JOURNAL)) {
			PersonDto contactPerson = FacadeProvider.getPersonFacade().getPersonByUuid(contact.getPerson().getUuid());
			ExternalJournalUtil.getExternalJournalUiComponent(contactPerson).ifPresent(getButtonsLayout()::addComponent);
		}
	}

	@Override
	protected ContactReferenceDto getReferenceByUuid(String uuid) {

		final ContactReferenceDto reference;
		if (FacadeProvider.getContactFacade().exists(uuid)) {
			reference = FacadeProvider.getContactFacade().getReferenceByUuid(uuid);
		} else if (FacadeProvider.getPersonFacade().isValidPersonUuid(uuid)) {
			PersonReferenceDto person = FacadeProvider.getPersonFacade().getReferenceByUuid(uuid);
			ContactCriteria criteria = new ContactCriteria();
			criteria.setPerson(person);
			List<ContactIndexDto> personContacts = FacadeProvider.getContactFacade().getIndexList(criteria, null, null, Collections.emptyList());
			if (personContacts != null) {
				reference = FacadeProvider.getContactFacade().getReferenceByUuid(personContacts.get(0).getUuid());
			} else {
				reference = null;
			}
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

	@Override
	protected void setSubComponent(DirtyStateComponent newComponent) {
		super.setSubComponent(newComponent);

		if (FacadeProvider.getContactFacade().isDeleted(getReference().getUuid())) {
			newComponent.setEnabled(false);
		}
	}

	public ContactReferenceDto getContactRef() {
		return getReference();
	}

	public void setContactEditPermission(Component component) {
		boolean isContactEditAllowed = isContactEditAllowed();
		if (!isContactEditAllowed) {
			getComponent(getComponentIndex(component)).setEnabled(false);
		}
	}

	protected boolean isContactEditAllowed() {
		return FacadeProvider.getContactFacade().isContactEditAllowed(getContactRef().getUuid());
	}
}

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

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.epidata.ContactEpiDataView;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public abstract class AbstractContactView extends AbstractDetailView<ContactReferenceDto> {

	public static final String ROOT_VIEW_NAME = ContactsView.VIEW_NAME;

	protected AbstractContactView(String viewName) {
		super(viewName);

		if (FacadeProvider.getConfigFacade().getPIAUrl() != null && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CREATE_PIA_ACCOUNT)) {
			Button btnCreatePIAAccount = new Button(I18nProperties.getCaption(Captions.contactCreatePIAAccount));
			CssStyles.style(btnCreatePIAAccount, ValoTheme.BUTTON_PRIMARY);
			btnCreatePIAAccount.addClickListener(e -> {
				ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(getReference().getUuid());
				PersonDto contactPerson = FacadeProvider.getPersonFacade().getPersonByUuid(contact.getPerson().getUuid());
				ControllerProvider.getContactController().openPIAAccountCreationWindow(contactPerson);
			});
			getButtonsLayout().addComponent(btnCreatePIAAccount);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	public void refreshMenu(SubMenu menu, Label infoLabel, Label infoLabelSub, String params) {

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
		menu.addView(ContactVisitsView.VIEW_NAME, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.VISITS), params);
		menu.addView(ContactEpiDataView.VIEW_NAME, I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.EPI_DATA), params);

		infoLabel.setValue(getReference().getCaption());
		infoLabelSub.setValue(
			contact.getDisease() != Disease.OTHER ? contact.getDisease().toShortString() : DataHelper.toStringNullable(contact.getDiseaseDetails()));
	}

	@Override
	protected ContactReferenceDto getReferenceByUuid(String uuid) {

		final ContactReferenceDto reference;
		if (FacadeProvider.getContactFacade().exists(uuid)) {
			reference = FacadeProvider.getContactFacade().getReferenceByUuid(uuid);
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
	protected void setSubComponent(Component newComponent) {
		super.setSubComponent(newComponent);

		if (FacadeProvider.getContactFacade().isDeleted(getReference().getUuid())) {
			newComponent.setEnabled(false);
		}
	}

	public ContactReferenceDto getContactRef() {
		return getReference();
	}

	public void setContactEditPermission(Component component) {
		Boolean isContactEditAllowed = FacadeProvider.getContactFacade().isContactEditAllowed(getContactRef().getUuid());
		if (!isContactEditAllowed) {
			getComponent(getComponentIndex(component)).setEnabled(false);
		}
	}
}

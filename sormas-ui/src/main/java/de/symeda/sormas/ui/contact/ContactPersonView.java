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

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

public class ContactPersonView extends AbstractContactView {

	private static final long serialVersionUID = -1L;
	
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

    public ContactPersonView() {
    	super(VIEW_NAME);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	super.enter(event);
    	ContactDto dto = FacadeProvider.getContactFacade().getContactByUuid(getContactRef().getUuid());
    	
    	CommitDiscardWrapperComponent<PersonEditForm> contactPersonComponent = ControllerProvider.getPersonController().getPersonEditComponent(dto.getPerson().getUuid(),
				dto.getDisease(), dto.getDiseaseDetails(), UserRight.CONTACT_EDIT, null, FacadeProvider.getContactFacade().isContactEditAllowed(getContactRef().getUuid()));
    	setSubComponent(contactPersonComponent);
    	
    	setContactEditPermission(contactPersonComponent);    	
    }
}

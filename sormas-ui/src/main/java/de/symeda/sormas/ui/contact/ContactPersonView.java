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

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.person.PersonSideComponentsElement;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class ContactPersonView extends AbstractContactView implements PersonSideComponentsElement {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

	private PersonDto person;

	public ContactPersonView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		ContactDto contact = FacadeProvider.getContactFacade().getByUuid(getContactRef().getUuid());
		person = FacadeProvider.getPersonFacade().getByUuid(contact.getPerson().getUuid());
		CommitDiscardWrapperComponent<PersonEditForm> editComponent = ControllerProvider.getPersonController()
			.getPersonEditComponent(
				PersonContext.CASE,
				person,
				contact.getDisease(),
				contact.getDiseaseDetails(),
				UserRight.CONTACT_EDIT,
				null,
				isEditAllowed());
		DetailSubComponentWrapper componentWrapper = addComponentWrapper(editComponent);
		CustomLayout layout = addPageLayout(componentWrapper, editComponent);
		setSubComponent(componentWrapper);
		addSideComponents(
			layout,
			DeletableEntityType.CONTACT,
			contact.getUuid(),
			person.toReference(),
			this::showUnsavedChangesPopup,
			isEditAllowed());
		setEditPermission(
			editComponent,
			UserProvider.getCurrent().hasUserRight(UserRight.PERSON_EDIT),
			PersonDto.ADDRESSES,
			PersonDto.PERSON_CONTACT_DETAILS);
	}
}

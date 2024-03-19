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
package de.symeda.sormas.ui.caze;

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.person.PersonSideComponentsElement;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

@SuppressWarnings("serial")
public class CasePersonView extends AbstractCaseView implements PersonSideComponentsElement {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

	private PersonDto person;

	public CasePersonView() {
		super(VIEW_NAME, true);
	}

	@Override
	protected void initView(String params) {

		CaseDataDto caseData = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());
		person = FacadeProvider.getPersonFacade().getByUuid(caseData.getPerson().getUuid());
		boolean isEditAllowed = isEditAllowed();
		CommitDiscardWrapperComponent<PersonEditForm> editComponent = ControllerProvider.getPersonController()
			.getPersonEditComponent(
				PersonContext.CASE,
				person,
				caseData.getDisease(),
				caseData.getDiseaseDetails(),
				UserRight.CASE_EDIT,
				getViewMode(),
				isEditAllowed);
		DetailSubComponentWrapper componentWrapper = addComponentWrapper(editComponent);
		CustomLayout layout = addPageLayout(componentWrapper, editComponent);
		setSubComponent(componentWrapper);
		addSideComponents(layout, DeletableEntityType.CASE, caseData.getUuid(), person.toReference(), this::showUnsavedChangesPopup, isEditAllowed);
		setEditPermission(editComponent, UiUtil.permitted(UserRight.PERSON_EDIT), PersonDto.ADDRESSES, PersonDto.PERSON_CONTACT_DETAILS);
	}

	@Override
	protected boolean isEditAllowed() {
		return FacadeProvider.getPersonFacade().isEditAllowed(person.getUuid());
	}
}

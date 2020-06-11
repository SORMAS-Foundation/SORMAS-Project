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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

@SuppressWarnings("serial")
public class CasePersonView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

	public CasePersonView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		CaseDataDto caseData = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());
		CommitDiscardWrapperComponent<PersonEditForm> personEditComponent = ControllerProvider.getPersonController()
			.getPersonEditComponent(
				caseData.getPerson().getUuid(),
				caseData.getDisease(),
				caseData.getDiseaseDetails(),
				UserRight.CASE_EDIT,
				getViewMode(),
				FacadeProvider.getCaseFacade().isCaseEditAllowed(getCaseRef().getUuid()));

		setSubComponent(personEditComponent);
		setCaseEditPermission(personEditComponent);
	}
}

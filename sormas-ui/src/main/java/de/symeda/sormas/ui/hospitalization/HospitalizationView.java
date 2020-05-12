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
package de.symeda.sormas.ui.hospitalization;

import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.AbstractCaseView;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

@SuppressWarnings("serial")
public class HospitalizationView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/hospitalization";

	public HospitalizationView() {
		super(VIEW_NAME, true);
	}

	@Override
	protected void initView(String params) {

		CommitDiscardWrapperComponent<HospitalizationForm> hospitalizationForm =
			ControllerProvider.getCaseController().getHospitalizationComponent(getCaseRef().getUuid(), getViewMode());
		setSubComponent(hospitalizationForm);
		setCaseEditPermission(hospitalizationForm);
	}
}

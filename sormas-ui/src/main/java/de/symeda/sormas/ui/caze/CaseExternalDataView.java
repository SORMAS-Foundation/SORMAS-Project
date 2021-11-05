/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.caze;

import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class CaseExternalDataView extends AbstractCaseView {

	private static final long serialVersionUID = 6603714636934016140L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/externalData";

	public CaseExternalDataView() {
		super(VIEW_NAME, true);
	}

	@Override
	protected void initView(String params) {
		DetailSubComponentWrapper caseExternalDataComponent =
			ControllerProvider.getCaseController().getExternalDataComponent(getCaseRef().getUuid(), getViewMode());
		setSubComponent(caseExternalDataComponent);
		caseExternalDataComponent.setEnabled(false);
	}
}

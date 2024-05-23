/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.selfreport;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.samples.HasName;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class SelfReportDataView extends AbstractSelfReportView implements HasName {

	private static final long serialVersionUID = 1066490114457222798L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public SelfReportDataView() {
		super(VIEW_NAME);
	}

	@Override
	public String getName() {
		return VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		SelfReportDto selfReport = FacadeProvider.getSelfReportFacade().getByUuid(getReference().getUuid());

		CommitDiscardWrapperComponent<SelfReportDataForm> editComponent =
			ControllerProvider.getSelfReportController().getSelfReportEditComponent(selfReport);

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent);
		container.addComponent(layout);

		EditPermissionType editPermission = FacadeProvider.getSelfReportFacade().getEditPermissionType(selfReport.getUuid());
		layout.disableIfNecessary(selfReport.isDeleted(), editPermission);
	}
}

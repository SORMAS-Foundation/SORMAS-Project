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
package de.symeda.sormas.ui.configuration.infrastructure;

import java.util.Date;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.GridExportStreamResource;

public class HealthFacilitiesView extends AbstractFacilitiesView {

	private static final long serialVersionUID = -7708098278141028591L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/healthFacilities";

	public HealthFacilitiesView() {

		super(VIEW_NAME, null);

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EXPORT)) {
			StreamResource streamResource = new GridExportStreamResource(
				grid,
				"sormas_health_facilities",
				"sormas_health_facilities_" + DateHelper.formatDateForExport(new Date()) + ".csv",
				FacilitiesGrid.EDIT_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			createButton.setCaption(I18nProperties.getCaption(Captions.actionNewEntry));
			createButton.addClickListener(e -> ControllerProvider.getInfrastructureController().createHealthFacility(false));
		}
	}
}

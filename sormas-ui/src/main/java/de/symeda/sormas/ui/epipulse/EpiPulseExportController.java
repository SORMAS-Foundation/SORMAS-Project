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

package de.symeda.sormas.ui.epipulse;

import java.util.Date;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseExportIndexDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EpiPulseExportController {

	public EpiPulseExportController() {
	}

	public void create(Runnable callback) {

		EpipulseEditForm createForm = new EpipulseEditForm(true);
		createForm.setValue(EpipulseExportDto.build(UserProvider.getCurrent().getUserReference()));
		final CommitDiscardWrapperComponent<EpipulseEditForm> editView = new CommitDiscardWrapperComponent<EpipulseEditForm>(
			createForm,
			UiUtil.permitted(UserRight.EPIPULSE_EXPORT_CREATE),
			createForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!createForm.getFieldGroup().isModified()) {
				EpipulseExportDto dto = createForm.getValue();
				dto.setStatus(EpipulseExportStatus.PENDING);
				dto.setStatusChangeDate(new Date());

				FacadeProvider.getEpipulseExportFacade().saveEpipulseExport(dto);
				callback.run();
			}
		});

		VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingCreateNewEpipulseExport));
	}

	public void view(EpipulseExportIndexDto exportIndexDto, Runnable callback) {

	}
}

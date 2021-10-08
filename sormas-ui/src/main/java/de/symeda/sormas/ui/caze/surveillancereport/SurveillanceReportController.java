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

package de.symeda.sormas.ui.caze.surveillancereport;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SurveillanceReportController {

	public void createSurveillanceReport(CaseReferenceDto caze, Runnable callback) {
		openEditWindow(
			SurveillanceReportDto.build(caze, FacadeProvider.getUserFacade().getCurrentUser().toReference()),
			Strings.headingCreateSurveillanceReport,
			false,
			callback);
	}

	public void editSurveillanceReport(SurveillanceReportDto report, Runnable callback) {
		openEditWindow(report, Strings.headingEditSurveillanceReport, true, callback);
	}

	private void openEditWindow(SurveillanceReportDto report, String titleTag, boolean canDelete, Runnable callback) {
		SurveillanceReportForm surveillanceReportForm = new SurveillanceReportForm(report);
		surveillanceReportForm.setWidth(600, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<SurveillanceReportForm> editView =
			new CommitDiscardWrapperComponent<>(surveillanceReportForm, surveillanceReportForm.getFieldGroup());
		editView.addCommitListener(() -> {
			FacadeProvider.getSurveillanceReportFacade().saveSurveillanceReport(surveillanceReportForm.getValue());
			callback.run();
		});

		Window window = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(titleTag));

		if (canDelete) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getSurveillanceReportFacade().deleteSurveillanceReport(report.getUuid());
				window.close();
				callback.run();
			}, I18nProperties.getCaption(SurveillanceReportDto.I18N_PREFIX));
		}
	}
}

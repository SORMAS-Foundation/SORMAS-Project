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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SurveillanceReportController {

	public void createSurveillanceReport(CaseReferenceDto caze, Runnable callback) {
		openEditWindow(
			SurveillanceReportDto.build(caze, FacadeProvider.getUserFacade().getCurrentUser().toReference()),
			Strings.headingCreateSurveillanceReport,
			false,
			callback,
			true);
	}

	public void editSurveillanceReport(SurveillanceReportDto report, Runnable callback, boolean isEditAllowed) {
		openEditWindow(
			report,
			isEditAllowed ? Strings.headingEditSurveillanceReport : Strings.headingViewSurveillanceReport,
			true,
			callback,
			isEditAllowed);
	}

	private void openEditWindow(SurveillanceReportDto report, String titleTag, boolean canDelete, Runnable callback, boolean isEditAllowed) {
		SurveillanceReportForm surveillanceReportForm = new SurveillanceReportForm(report);
		surveillanceReportForm.setWidth(600, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<SurveillanceReportForm> editView =
			new CommitDiscardWrapperComponent<>(surveillanceReportForm, true, surveillanceReportForm.getFieldGroup());
		editView.setEditable(isEditAllowed);

		if (UiUtil.getUserRights().contains(UserRight.EXTERNAL_MESSAGE_VIEW)) {
			ExternalMessageDto externalMessage = FacadeProvider.getExternalMessageFacade().getForSurveillanceReport(report.toReference());

			if (externalMessage != null) {
				Button viewMessageButton = ButtonHelper.createIconButton(
					Captions.viewMessage,
					VaadinIcons.EYE,
					e -> ControllerProvider.getExternalMessageController().showExternalMessage(externalMessage.getUuid(), false, null));

				editView.getButtonsPanel().addComponent(viewMessageButton, 0);
				editView.getButtonsPanel().setComponentAlignment(viewMessageButton, Alignment.BOTTOM_LEFT);
			}
		}

		Window window = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(titleTag));

		if (isEditAllowed) {
			editView.addCommitListener(() -> {
				FacadeProvider.getSurveillanceReportFacade().save(surveillanceReportForm.getValue());
				callback.run();
			});
			if (canDelete) {
				editView.addDeleteListener(() -> {
					FacadeProvider.getSurveillanceReportFacade().delete(report.getUuid());
					window.close();
					callback.run();
				}, I18nProperties.getCaption(SurveillanceReportDto.I18N_PREFIX));
			}
		}
		editView.setEditable(isEditAllowed, Captions.viewMessage);
	}

	public void viewSurveillanceReport(SurveillanceReportDto report, Runnable callback, boolean isEditAllowed) {
		openEditWindow(report, Strings.headingViewSurveillanceReport, false, callback, isEditAllowed);
	}
}

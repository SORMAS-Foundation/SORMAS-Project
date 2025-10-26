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

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.epipulse.EpipulseExportDto;
import de.symeda.sormas.api.epipulse.EpipulseExportIndexDto;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
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
		EpipulseExportDto epipulseExportDto = FacadeProvider.getEpipulseExportFacade().getEpiPulseExportByUuid(exportIndexDto.getUuid());
		EpipulseExportInfoLayout exportInfoLayout = new EpipulseExportInfoLayout(epipulseExportDto, null);

		VaadinUiUtil.showConfirmationPopup(I18nProperties.getString(Strings.headingViewEpipulseExport), exportInfoLayout, popupWindow -> {
			ConfirmationComponent confirmationComponent = new ConfirmationComponent(false) {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onConfirm() {
					popupWindow.close();
				}

				@Override
				protected void onCancel() {
					popupWindow.close();
				}
			};

			confirmationComponent.getConfirmButton().setCaption(I18nProperties.getString(Strings.close));
			confirmationComponent.getCancelButton().setVisible(false);

			// Cancel export button - for canceling pending/incomplete exports
			if (epipulseExportDto.getStatus() == EpipulseExportStatus.PENDING || epipulseExportDto.getStatus() == EpipulseExportStatus.IN_PROGRESS) {
				Button cancelExportButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.actionCancel), event -> {
					/*
					 * VaadinUiUtil.showConfirmationPopup(
					 * I18nProperties.getCaption(Captions.actionConfirmAction),
					 * new Label(I18nProperties.getString(Strings.confirmationCancelBulkAction)),
					 * I18nProperties.getCaption(Captions.actionYes),
					 * I18nProperties.getCaption(Captions.actionNo),
					 * 560,
					 * result -> {
					 * if (result) {
					 * // Cancel the export
					 * epipulseExportDto.setStatus(EpipulseExportStatus.CANCELED);
					 * epipulseExportDto.setStatusChangeDate(new Date());
					 * FacadeProvider.getEpipulseExportFacade().saveEpipulseExport(epipulseExportDto);
					 * callback.run();
					 * popupWindow.close();
					 * }
					 * }
					 * );
					 */
				}, ValoTheme.BUTTON_DANGER);
				confirmationComponent.addExtraButton(cancelExportButton, e -> {
				});
			}

			// Delete button - always available
			Button deleteButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.actionDelete), event -> {
				/*
				 * VaadinUiUtil.showDeleteConfirmationWindow(
				 * String.format(
				 * I18nProperties.getString(Strings.confirmationDeleteEntity),
				 * "EpiPulse Export"
				 * ),
				 * () -> {
				 * FacadeProvider.getEpipulseExportFacade().deleteEpipulseExport(epipulseExportDto.getUuid());
				 * popupWindow.close();
				 * callback.run();
				 * }
				 * );
				 */
			}, ValoTheme.BUTTON_DANGER);
			confirmationComponent.addExtraButton(deleteButton, e -> {
			});

			return confirmationComponent;
		}, 640);
	}

	/*
	 * public void view(EpipulseExportIndexDto exportIndexDto, Runnable callback) {
	 * EpipulseExportDto epipulseExportDto = FacadeProvider.getEpipulseExportFacade().getEpiPulseExportByUuid(exportIndexDto.getUuid());
	 * EpipulseExportInfoLayout exportInfoLayout = new EpipulseExportInfoLayout(epipulseExportDto, null);
	 * VaadinUiUtil.showConfirmationPopup(
	 * I18nProperties.getString(Strings.headingViewEpipulseExport),
	 * exportInfoLayout,
	 * I18nProperties.getString(Strings.close),
	 * null,
	 * 640,
	 * confirmed -> {
	 * return true;
	 * });
	 * }
	 */
}

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.adverseeventsfollowingimmunization;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.fields.vaccines.AefiPrimarySuspectVaccinationSelectionField;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.form.AefiDataForm;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayoutHelper;

public class AefiController {

	public void registerViews(Navigator navigator) {
		navigator.addView(AefiView.VIEW_NAME, AefiView.class);
		//navigator.addView(ImmunizationDataView.VIEW_NAME, ImmunizationDataView.class);
		//navigator.addView(ImmunizationPersonView.VIEW_NAME, ImmunizationPersonView.class);
		navigator.addView(AefiDataView.VIEW_NAME, AefiDataView.class);
	}

	public void selectPrimarySuspectVaccination(AefiDto aefiDto, Consumer<VaccinationDto> commitCallback) {

		AefiPrimarySuspectVaccinationSelectionField selectionField =
			new AefiPrimarySuspectVaccinationSelectionField(aefiDto.getVaccinations(), aefiDto.getPrimarySuspectVaccine());
		selectionField.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<AefiPrimarySuspectVaccinationSelectionField> component =
			new CommitDiscardWrapperComponent<>(selectionField);
		component.addCommitListener(() -> {
			VaccinationDto selectedVaccination = selectionField.getValue();
			if (selectedVaccination != null) {
				aefiDto.setPrimarySuspectVaccine(selectedVaccination);

				if (commitCallback != null) {
					commitCallback.accept(selectedVaccination);
				}
			}
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingAefiPickPrimarySuspectVaccine));
	}

	public void navigateToAefi(String uuid) {
		navigateToView(AefiDataView.VIEW_NAME, uuid);
	}

	public void navigateToView(String viewName, String uuid) {
		final String navigationState = viewName + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public CommitDiscardWrapperComponent<AefiDataForm> getAefiDataEditComponent(
		boolean isCreateAction,
		AefiDto aefiDto,
		Consumer<Runnable> actionCallback) {

		AefiDataForm aefiDataForm = new AefiDataForm(isCreateAction, aefiDto.isPseudonymized(), aefiDto.isInJurisdiction(), actionCallback);
		aefiDataForm.setValue(aefiDto);

		CommitDiscardWrapperComponent<AefiDataForm> editComponent =
			new CommitDiscardWrapperComponent<>(aefiDataForm, true, aefiDataForm.getFieldGroup());

		if (!isCreateAction) {
			DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getAefiFacade().getAutomaticDeletionInfo(aefiDto.getUuid());
			DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getAefiFacade().getManuallyDeletionInfo(aefiDto.getUuid());

			editComponent.getButtonsPanel()
				.addComponentAsFirst(new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, aefiDto.isDeleted(), AefiDto.I18N_PREFIX));

			if (aefiDto.isDeleted()) {
				editComponent.getWrappedComponent().getField(AefiDto.DELETION_REASON).setVisible(true);
				if (editComponent.getWrappedComponent().getField(AefiDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
					editComponent.getWrappedComponent().getField(AefiDto.OTHER_DELETION_REASON).setVisible(true);
				}
			}
		}

		editComponent.addCommitListener(() -> {
			if (!aefiDataForm.getFieldGroup().isModified()) {
				AefiDto aefiDataFormValue = aefiDataForm.getValue();

				AefiDto savedAefiDto = FacadeProvider.getAefiFacade().save(aefiDataFormValue);
				Notification.show(I18nProperties.getString(Strings.messageAdverseEventSaved), Notification.Type.WARNING_MESSAGE);

				if (isCreateAction) {
					navigateToAefi(savedAefiDto.getUuid());
				} else {
					SormasUI.refreshView();
				}
			}
		});

		if (!isCreateAction) {
			// Initialize 'Delete' button
			if (UserProvider.getCurrent().hasUserRight(UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_DELETE)) {
				editComponent.addDeleteWithReasonOrRestoreListener(
					AefiView.VIEW_NAME,
					null,
					I18nProperties.getString(Strings.entityAdverseEvent),
					aefiDto.getUuid(),
					FacadeProvider.getAefiFacade());
			}

			// Initialize 'Archive' button
			if (UserProvider.getCurrent().hasUserRight(UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_ARCHIVE)) {
				ControllerProvider.getArchiveController()
					.addArchivingButton(aefiDto, ArchiveHandlers.forAefi(), editComponent, () -> navigateToAefi(aefiDto.getUuid()));
			}

			editComponent.restrictEditableComponentsOnEditView(
				UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT,
				null,
				UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_DELETE,
				UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_ARCHIVE,
				FacadeProvider.getAefiFacade().getEditPermissionType(aefiDto.getUuid()),
				aefiDto.isInJurisdiction());
		}

		return editComponent;
	}

	public TitleLayout getAefiViewTitleLayout(String aefiUuid, String immunizationUuid, boolean isCreateAction) {

		if (!isCreateAction) {
			AefiDto aefiDto = findAefi(aefiUuid);
			immunizationUuid = aefiDto.getImmunization().getUuid();
		}

		TitleLayout titleLayout = new TitleLayout();

		String shortUuid = DataHelper.getShortUuid(immunizationUuid);
		ImmunizationDto immunizationDto = FacadeProvider.getImmunizationFacade().getByUuid(immunizationUuid);
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(immunizationDto.getPerson().getUuid());
		StringBuilder mainRowText = TitleLayoutHelper.buildPersonString(person);
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private AefiDto findAefi(String uuid) {
		return FacadeProvider.getAefiFacade().getByUuid(uuid);
	}

	public boolean isCreateAction(String params) {
		return StringUtils.startsWith(params, "immunization") && StringUtils.endsWith(params, "create");
	}

	public String getCreateActionImmunizationUuid(String params) {
		return StringUtils.contains(params, "/")
			? StringUtils.substringBetween(params, "/", "/")
			: StringUtils.substringBetween(params, "immunization", "create");
	}
}

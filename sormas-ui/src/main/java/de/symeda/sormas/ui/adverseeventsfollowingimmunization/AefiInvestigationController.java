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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.fields.vaccines.AefiPrimarySuspectVaccinationSelectionField;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.RowLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayoutHelper;

public class AefiInvestigationController {

	public void registerViews(Navigator navigator) {
	}

	public void selectPrimarySuspectVaccination(AefiInvestigationDto aefiInvestigationDto, Consumer<VaccinationDto> commitCallback) {

		AefiPrimarySuspectVaccinationSelectionField selectionField =
			new AefiPrimarySuspectVaccinationSelectionField(aefiInvestigationDto.getVaccinations(), aefiInvestigationDto.getPrimarySuspectVaccine());
		selectionField.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<AefiPrimarySuspectVaccinationSelectionField> component =
			new CommitDiscardWrapperComponent<>(selectionField);
		component.addCommitListener(() -> {
			VaccinationDto selectedVaccination = selectionField.getValue();
			if (selectedVaccination != null) {
				aefiInvestigationDto.setPrimarySuspectVaccine(selectedVaccination);

				if (commitCallback != null) {
					commitCallback.accept(selectedVaccination);
				}
			}
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingAefiInvestigationSelectConcernedVaccine));
	}

	public void navigateToAefiInvestigation(String uuid) {
		navigateToView(AefiInvestigationDataView.VIEW_NAME, uuid);
	}

	public void navigateToView(String viewName, String uuid) {
		final String navigationState = viewName + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public CommitDiscardWrapperComponent<AefiInvestigationDataForm> getAefiInvestigationDataEditComponent(
		boolean isCreateAction,
		AefiInvestigationDto aefiInvestigationDto,
		Consumer<Runnable> actionCallback) {

		AefiInvestigationDataForm aefiInvestigationDataForm = new AefiInvestigationDataForm(
			isCreateAction,
			aefiInvestigationDto.isPseudonymized(),
			aefiInvestigationDto.isInJurisdiction(),
			actionCallback);
		aefiInvestigationDataForm.setValue(aefiInvestigationDto);

		CommitDiscardWrapperComponent<AefiInvestigationDataForm> editComponent =
			new CommitDiscardWrapperComponent<>(aefiInvestigationDataForm, true, aefiInvestigationDataForm.getFieldGroup());

		if (!isCreateAction) {
			DeletionInfoDto automaticDeletionInfoDto =
				FacadeProvider.getAefiInvestigationFacade().getAutomaticDeletionInfo(aefiInvestigationDto.getUuid());
			DeletionInfoDto manuallyDeletionInfoDto =
				FacadeProvider.getAefiInvestigationFacade().getManuallyDeletionInfo(aefiInvestigationDto.getUuid());

			editComponent.getButtonsPanel()
				.addComponentAsFirst(
					new DeletionLabel(
						automaticDeletionInfoDto,
						manuallyDeletionInfoDto,
						aefiInvestigationDto.isDeleted(),
						AefiInvestigationDto.I18N_PREFIX));

			if (aefiInvestigationDto.isDeleted()) {
				editComponent.getWrappedComponent().getField(AefiInvestigationDto.DELETION_REASON).setVisible(true);
				if (editComponent.getWrappedComponent().getField(AefiInvestigationDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
					editComponent.getWrappedComponent().getField(AefiInvestigationDto.OTHER_DELETION_REASON).setVisible(true);
				}
			}
		}

		editComponent.addCommitListener(() -> {
			if (!aefiInvestigationDataForm.getFieldGroup().isModified()) {
				AefiInvestigationDto aefiInvestigationDataFormValue = aefiInvestigationDataForm.getValue();

				AefiInvestigationDto savedAefiInvestigationDto = FacadeProvider.getAefiInvestigationFacade().save(aefiInvestigationDataFormValue);
				Notification.show(I18nProperties.getString(Strings.messageAdverseEventInvestigationSaved), Notification.Type.WARNING_MESSAGE);

				if (isCreateAction) {
					navigateToAefiInvestigation(savedAefiInvestigationDto.getUuid());
				} else {
					SormasUI.refreshView();
				}
			}
		});

		if (!isCreateAction) {
			// Initialize 'Delete' button
			if (UserProvider.getCurrent().hasUserRight(UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_DELETE)) {
				editComponent.addDeleteWithReasonOrRestoreListener(
					AefiInvestigationView.VIEW_NAME,
					null,
					I18nProperties.getString(Strings.entityAdverseEventInvestigation),
					aefiInvestigationDto.getUuid(),
					FacadeProvider.getAefiInvestigationFacade());
			}

			// Initialize 'Archive' button
			if (UserProvider.getCurrent().hasUserRight(UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_ARCHIVE)) {
				ControllerProvider.getArchiveController()
					.addArchivingButton(
						aefiInvestigationDto,
						ArchiveHandlers.forAefiInvestigation(),
						editComponent,
						() -> navigateToAefiInvestigation(aefiInvestigationDto.getUuid()));
			}

			editComponent.restrictEditableComponentsOnEditView(
				UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_EDIT,
				null,
				UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_DELETE,
				UserRight.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_ARCHIVE,
				FacadeProvider.getAefiInvestigationFacade().getEditPermissionType(aefiInvestigationDto.getUuid()),
				aefiInvestigationDto.isInJurisdiction());
		}

		return editComponent;
	}

	public TitleLayout getAefiInvestigationViewTitleLayout(
		String aefiInvestigationUuid,
		String aefiUuid,
		String immunizationUuid,
		boolean isCreateAction) {

		TitleLayout titleLayout = new TitleLayout();

		String investigationStage = "";

		if (isCreateAction) {
			investigationStage = I18nProperties.getCaption(Captions.aefiNewAefiInvestigationStageTitle);
		} else {
			AefiInvestigationDto aefiInvestigationDto = findAefiInvestigation(aefiInvestigationUuid);
			investigationStage = aefiInvestigationDto.getInvestigationStage().toString();
		}

		AefiDto aefiDto = FacadeProvider.getAefiFacade().getByUuid(aefiUuid);
		if (aefiDto.getSerious() == YesNoUnknown.YES) {
			RowLayout aefiTypeLayout = new RowLayout();
			aefiTypeLayout.addToLayout(investigationStage + ", ");
			aefiTypeLayout.addToLayout(AefiType.SERIOUS.toString(), CssStyles.LABEL_CRITICAL);
			titleLayout.addRow(aefiTypeLayout);
		} else {
			titleLayout.addRow(investigationStage + ", " + AefiType.NON_SERIOUS);
		}

		String shortUuid = DataHelper.getShortUuid(aefiInvestigationUuid);
		ImmunizationDto immunizationDto = FacadeProvider.getImmunizationFacade().getByUuid(immunizationUuid);
		PersonDto person = FacadeProvider.getPersonFacade().getByUuid(immunizationDto.getPerson().getUuid());
		StringBuilder mainRowText = TitleLayoutHelper.buildPersonString(person);

		if (!StringUtils.isBlank(shortUuid)) {
			mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);
		}
		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private AefiInvestigationDto findAefiInvestigation(String uuid) {
		return FacadeProvider.getAefiInvestigationFacade().getByUuid(uuid);
	}

	public boolean isCreateAction(String params) {
		return StringUtils.startsWith(params, "adverseevent") && StringUtils.endsWith(params, "create");
	}

	public String getCreateActionAefiReportUuid(String params) {
		return StringUtils.contains(params, "/")
			? StringUtils.substringBetween(params, "adverseevent/", "/investigation/create")
			: StringUtils.substringBetween(params, "adverseevent", "investigationcreate");
	}
}

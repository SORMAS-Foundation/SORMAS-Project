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

import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.api.selfreport.processing.SelfReportProcessingFacade;
import de.symeda.sormas.api.selfreport.processing.SelfReportProcessingResult;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.dataprocessing.ProcessingResult;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.selfreport.processing.SelfReportProcessingFLow;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class SelfReportController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void registerViews(Navigator navigator) {
		navigator.addView(SelfReportsView.VIEW_NAME, SelfReportsView.class);
		navigator.addView(SelfReportDataView.VIEW_NAME, SelfReportDataView.class);
	}

	public void navigateToSelfReport(String uuid) {
		navigateToView(SelfReportDataView.VIEW_NAME, uuid);
	}

	public void navigateToView(String viewName, String uuid) {
		final String navigationState = viewName + "/" + uuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public TitleLayout getSelfReporViewTitleLayout(String uuid) {
		SelfReportDto selfReport = findSelfReport(uuid);

		TitleLayout titleLayout = new TitleLayout();

		titleLayout.addRow(DiseaseHelper.toString(selfReport.getDisease(), null));
		titleLayout.addRow(selfReport.getInvestigationStatus().toString());

		String shortUuid = DataHelper.getShortUuid(selfReport.getUuid());
		String personFullName = PersonDto.buildCaption(selfReport.getFirstName(), selfReport.getLastName());

		StringBuilder mainRowText = new StringBuilder(personFullName);
		mainRowText.append(mainRowText.length() > 0 ? " (" + shortUuid + ")" : shortUuid);

		titleLayout.addMainRow(mainRowText.toString());

		return titleLayout;
	}

	private static SelfReportDto findSelfReport(String uuid) {
		return FacadeProvider.getSelfReportFacade().getByUuid(uuid);
	}

	public CommitDiscardWrapperComponent<SelfReportDataForm> getSelfReportEditComponent(SelfReportDto selfReport) {
		SelfReportDataForm editForm = new SelfReportDataForm(selfReport.getDisease(), selfReport.isInJurisdiction(), selfReport.isPseudonymized());
		editForm.setValue(selfReport);

		CommitDiscardWrapperComponent<SelfReportDataForm> editComponent = new CommitDiscardWrapperComponent<>(editForm, editForm.getFieldGroup());
		editComponent.addCommitListener(() -> {
			if (!editForm.getFieldGroup().isModified()) {
				FacadeProvider.getSelfReportFacade().save(selfReport);

				Notification.show(I18nProperties.getString(Strings.messageSelfReportSaved), Notification.Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});

		DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getSelfReportFacade().getAutomaticDeletionInfo(selfReport.getUuid());
		DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getSelfReportFacade().getManuallyDeletionInfo(selfReport.getUuid());
		editComponent.getButtonsPanel()
			.addComponentAsFirst(
				new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, selfReport.isDeleted(), SelfReportDto.I18N_PREFIX));

		if (selfReport.isDeleted()) {
			editComponent.getWrappedComponent().getField(SelfReportDto.DELETION_REASON).setVisible(true);
			if (editComponent.getWrappedComponent().getField(SelfReportDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editComponent.getWrappedComponent().getField(SelfReportDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}
		if (UiUtil.getUserRoles().stream().anyMatch(userRoleDto -> !userRoleDto.isRestrictAccessToAssignedEntities())
			|| DataHelper.equal(selfReport.getResponsibleUser(), UiUtil.getUserReference())) {
			if (UiUtil.permitted(UserRight.SELF_REPORT_DELETE)) {
				editComponent.addDeleteWithReasonOrRestoreListener(
					SelfReportsView.VIEW_NAME,
					null,
					I18nProperties.getString(Strings.entitySelfReport),
					selfReport.getUuid(),
					FacadeProvider.getSelfReportFacade());
			}

			if (UiUtil.permitted(UserRight.SELF_REPORT_ARCHIVE)) {
				ControllerProvider.getArchiveController()
					.addArchivingButton(selfReport, ArchiveHandlers.forSelfReport(), editComponent, () -> navigateToSelfReport(selfReport.getUuid()));
			}
		}

		editComponent.restrictEditableComponentsOnEditView(
			UserRight.SELF_REPORT_EDIT,
			null,
			UserRight.SELF_REPORT_DELETE,
			UserRight.SELF_REPORT_ARCHIVE,
			FacadeProvider.getSelfReportFacade().getEditPermissionType(selfReport.getUuid()),
			true);

		return editComponent;
	}

	public void processSelfReport(String uuid) {
		SelfReportDto selfReport = FacadeProvider.getSelfReportFacade().getByUuid(uuid);
		SelfReportProcessingFLow flow = new SelfReportProcessingFLow(getSelfReportProcessingFacade());

		boolean isCaseReport = selfReport.getType() == SelfReportType.CASE;
		CompletionStage<ProcessingResult<SelfReportProcessingResult>> result =
			isCaseReport ? flow.runCaseFlow(selfReport) : flow.runContactFlow(selfReport);

		result.handle((processingResult, throwable) -> {
			if (throwable != null) {
				logger.error("Unexpected exception while processing self report", throwable);

				Notification.show(
					I18nProperties.getString(Strings.errorOccurred, I18nProperties.getString(Strings.errorOccurred)),
					I18nProperties.getString(Strings.errorWasReported),
					Notification.Type.ERROR_MESSAGE);
			} else {
				SelfReportProcessingResult processingResultData = processingResult.getData();

				if (processingResultData.isOpenEntityOnDone()) {
					if (isCaseReport) {
						ControllerProvider.getCaseController().navigateToCase(processingResultData.getCaze().getEntity().getUuid());
					} else {
						ControllerProvider.getContactController().navigateToData(processingResultData.getContact().getEntity().getUuid());
					}
				} else {
					SormasUI.get().getNavigator().navigateTo(SelfReportsView.VIEW_NAME);
				}
			}

			return null;
		});
	}

	private static SelfReportProcessingFacade getSelfReportProcessingFacade() {
		return new SelfReportProcessingFacade(
			FacadeProvider.getFeatureConfigurationFacade(),
			FacadeProvider.getCaseFacade(),
			FacadeProvider.getContactFacade(),
			FacadeProvider.getRegionFacade(),
			FacadeProvider.getDistrictFacade(),
			FacadeProvider.getCommunityFacade(),
			FacadeProvider.getFacilityFacade(),
			FacadeProvider.getSelfReportFacade()) {

			@Override
			public boolean hasAllUserRights(UserRight... userRights) {
				return UiUtil.getCurrentUserProvider().hasAllUserRights(userRights);
			}
		};
	}
}

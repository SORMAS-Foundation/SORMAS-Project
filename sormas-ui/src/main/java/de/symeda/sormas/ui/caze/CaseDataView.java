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

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.immunization.ImmunizationListCriteria;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.vaccination.VaccinationListCriteria;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.messaging.SmsListComponent;
import de.symeda.sormas.ui.caze.surveillancereport.SurveillanceReportListComponent;
import de.symeda.sormas.ui.docgeneration.QuarantineOrderDocumentsComponent;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.events.eventLink.EventListComponent;
import de.symeda.sormas.ui.externalsurveillanceservice.ExternalSurveillanceServiceGateway;
import de.symeda.sormas.ui.immunization.immunizationlink.ImmunizationListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponentLayout;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.ArchivingController;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.SidePanelLayout;
import de.symeda.sormas.ui.utils.ViewMode;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;
import de.symeda.sormas.ui.vaccination.list.VaccinationListComponent;

/**
 * CaseDataView for reading and editing the case data fields. Contains the
 * {@link CaseDataForm}.
 */
public class CaseDataView extends AbstractCaseView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";
	public static final String CASE_LOC = "case";
	public static final String CASE_SIDE_PANEL_LOC = "caseSidePanel";
	public static final String TASKS_LOC = "tasks";
	public static final String SAMPLES_LOC = "samples";
	public static final String EVENTS_LOC = "events";
	public static final String IMMUNIZATION_LOC = "immunizations";
	public static final String VACCINATIONS_LOC = "vaccinations";
	public static final String SORMAS_TO_SORMAS_LOC = "sormasToSormas";
	public static final String SMS_LOC = "sms";
	public static final String SURVEILLANCE_REPORTS_LOC = "surveillanceReports";
	public static final String DOCUMENTS_LOC = "documents";
	private static final long serialVersionUID = -1L;
	private CommitDiscardWrapperComponent<CaseDataForm> editComponent;

	public CaseDataView() {
		super(VIEW_NAME, false);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		editComponent = ControllerProvider.getCaseController().getCaseDataEditComponent(getCaseRef().getUuid(), ViewMode.NORMAL);

		SidePanelLayout layout = new SidePanelLayout(
			editComponent,
			TASKS_LOC,
			SAMPLES_LOC,
			EVENTS_LOC,
			IMMUNIZATION_LOC,
			VACCINATIONS_LOC,
			SORMAS_TO_SORMAS_LOC,
			SMS_LOC,
			ExternalSurveillanceServiceGateway.EXTERANEL_SURVEILLANCE_TOOL_GATEWAY_LOC,
			SURVEILLANCE_REPORTS_LOC,
			DOCUMENTS_LOC,
			QuarantineOrderDocumentsComponent.QUARANTINE_LOC);

		container.addComponent(layout);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT)) {
			TaskListComponent taskList = new TaskListComponent(TaskContext.CASE, getCaseRef(), caze.getDisease());
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(taskList, TASKS_LOC);
		}

		final boolean externalMessagesEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.MANUAL_EXTERNAL_MESSAGES);
		final boolean isSmsServiceSetUp = FacadeProvider.getConfigFacade().isSmsServiceSetUp();
		if (isSmsServiceSetUp && externalMessagesEnabled && UserProvider.getCurrent().hasUserRight(UserRight.SEND_MANUAL_EXTERNAL_MESSAGES)) {
			SmsListComponent smsList = new SmsListComponent(getCaseRef(), caze.getPerson());
			smsList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(smsList, SMS_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.SAMPLES_LAB)
			&& UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW)
			&& !caze.checkIsUnreferredPortHealthCase()) {
			SampleListComponent sampleList =
				new SampleListComponent(new SampleCriteria().caze(getCaseRef()).sampleAssociationType(SampleAssociationType.CASE));
			sampleList.addSideComponentCreateEventListener(
				e -> showNavigationConfirmPopupIfDirty(() -> ControllerProvider.getSampleController().create(getCaseRef(), caze.getDisease(), () -> {
					final CaseDataDto caseDataByUuid = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());
					FacadeProvider.getCaseFacade().save(caseDataByUuid);
					SormasUI.refreshView();
				})));

			SampleListComponentLayout sampleListComponentLayout =
				new SampleListComponentLayout(sampleList, I18nProperties.getString(Strings.infoCreateNewSampleDiscardsChangesCase));
			layout.addSidePanelComponent(sampleListComponentLayout, SAMPLES_LOC);
		}

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE)) {
			VerticalLayout eventLayout = new VerticalLayout();
			eventLayout.setMargin(false);
			eventLayout.setSpacing(false);

			EventListComponent eventList = new EventListComponent(getCaseRef());
			eventList.addStyleName(CssStyles.SIDE_COMPONENT);
			eventLayout.addComponent(eventList);
			layout.addSidePanelComponent(eventLayout, EVENTS_LOC);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.IMMUNIZATION_VIEW)
			&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT)) {
			if (!FacadeProvider.getFeatureConfigurationFacade()
				.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
				final ImmunizationListCriteria immunizationListCriteria =
					new ImmunizationListCriteria.Builder(caze.getPerson()).wihDisease(caze.getDisease()).build();
				layout.addSidePanelComponent(new SideComponentLayout(new ImmunizationListComponent(immunizationListCriteria)), IMMUNIZATION_LOC);
			} else {
				VaccinationListCriteria criteria = new VaccinationListCriteria.Builder(caze.getPerson()).withDisease(caze.getDisease()).build();
				layout.addSidePanelComponent(
					new SideComponentLayout(
						new VaccinationListComponent(getCaseRef(), criteria, caze.getResponsibleRegion(), caze.getResponsibleDistrict(), this)),
					VACCINATIONS_LOC);
			}
		}

		boolean sormasToSormasEnabled = FacadeProvider.getSormasToSormasFacade().isSharingCasesContactsAndSamplesEnabledForUser();
		if (sormasToSormasEnabled || caze.getSormasToSormasOriginInfo() != null) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(caze, sormasToSormasEnabled);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addSidePanelComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		ExternalSurveillanceServiceGateway.addComponentToLayout(layout, editComponent, caze);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.SURVEILLANCE_REPORTS)) {
			SurveillanceReportListComponent surveillanceReportList = new SurveillanceReportListComponent(caze.toReference());
			surveillanceReportList.addStyleNames(CssStyles.SIDE_COMPONENT);
			VerticalLayout surveillanceReportListLocLayout = new VerticalLayout();
			surveillanceReportListLocLayout.setMargin(false);
			surveillanceReportListLocLayout.setSpacing(false);
			surveillanceReportListLocLayout.addComponent(surveillanceReportList);

			layout.addSidePanelComponent(surveillanceReportListLocLayout, SURVEILLANCE_REPORTS_LOC);
		}
		DocumentListComponent documentList = null;
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.DOCUMENTS)) {
			documentList = new DocumentListComponent(DocumentRelatedEntityType.CASE, getCaseRef(), UserRight.CASE_EDIT, caze.isPseudonymized());
			layout.addSidePanelComponent(new SideComponentLayout(documentList), DOCUMENTS_LOC);
		}

		QuarantineOrderDocumentsComponent.addComponentToLayout(layout.getSidePanelComponent(), caze, documentList);

		EditPermissionType caseEditAllowed = FacadeProvider.getCaseFacade().isCaseEditAllowed(caze.getUuid());

		if (caseEditAllowed.equals(EditPermissionType.ARCHIVING_STATUS_ONLY)) {
			layout.disable(ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID);
		} else if (caseEditAllowed.equals(EditPermissionType.REFUSED)) {
			layout.disable();
		}
	}
}

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
package de.symeda.sormas.ui.caze;

import java.util.Arrays;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.events.eventLink.EventListComponent;
import de.symeda.sormas.ui.docgeneration.DocGenerationComponent;
import de.symeda.sormas.ui.samples.sampleLink.SampleListComponent;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.survnet.SurvnetGateway;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.ViewMode;

/**
 * CaseDataView for reading and editing the case data fields. Contains the
 * {@link CaseDataForm}.
 */
public class CaseDataView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String CASE_LOC = "case";
	public static final String TASKS_LOC = "tasks";
	public static final String SAMPLES_LOC = "samples";
	public static final String EVENTS_LOC = "events";
	public static final String SORMAS_TO_SORMAS_LOC = "sormasToSormas";

	private CommitDiscardWrapperComponent<CaseDataForm> editComponent;

	public CaseDataView() {
		super(VIEW_NAME, false);
	}

	@Override
	protected void initView(String params) {

		setHeightUndefined();

		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());

		String htmlLayout = LayoutUtil.fluidRow(
			LayoutUtil.fluidColumnLoc(8, 0, 12, 0, CASE_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, TASKS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SAMPLES_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, EVENTS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SORMAS_TO_SORMAS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SurvnetGateway.SURVNET_GATEWAY_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, DocGenerationComponent.QUARANTINE_LOC));

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		//		if (getViewMode() == ViewMode.SIMPLE) {
		//			editComponent = ControllerProvider.getCaseController().getCaseCombinedEditComponent(getCaseRef().getUuid(),
		//					ViewMode.SIMPLE);
		//		} else {
		editComponent = ControllerProvider.getCaseController().getCaseDataEditComponent(getCaseRef().getUuid(), ViewMode.NORMAL);
		//		}

		// setSubComponent(editComponent);
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, CASE_LOC);

		TaskListComponent taskList = new TaskListComponent(TaskContext.CASE, getCaseRef());
		taskList.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(taskList, TASKS_LOC);

		if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_VIEW) && !caze.checkIsUnreferredPortHealthCase()) {
			VerticalLayout sampleLocLayout = new VerticalLayout();
			sampleLocLayout.setMargin(false);
			sampleLocLayout.setSpacing(false);

			SampleListComponent sampleList = new SampleListComponent(getCaseRef());
			sampleList.addStyleName(CssStyles.SIDE_COMPONENT);
			sampleLocLayout.addComponent(sampleList);

			if (UserProvider.getCurrent().hasUserRight(UserRight.SAMPLE_CREATE)) {
				sampleList.addStyleName(CssStyles.VSPACE_NONE);
				Label sampleInfo = new Label(
					VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoCreateNewSampleDiscardsChanges),
					ContentMode.HTML);
				sampleInfo.addStyleNames(CssStyles.VSPACE_2, CssStyles.VSPACE_TOP_4);

				sampleLocLayout.addComponent(sampleInfo);
			}

			layout.addComponent(sampleLocLayout, SAMPLES_LOC);
		}

		VerticalLayout eventLayout = new VerticalLayout();
		eventLayout.setMargin(false);
		eventLayout.setSpacing(false);

		EventListComponent eventList = new EventListComponent(getCaseRef());
		eventList.addStyleName(CssStyles.SIDE_COMPONENT);
		eventLayout.addComponent(eventList);
		layout.addComponent(eventLayout, EVENTS_LOC);

		boolean sormasToSormasEnabled = FacadeProvider.getSormasToSormasFacade().isFeatureEnabled();
		if (sormasToSormasEnabled || caze.getSormasToSormasOriginInfo() != null) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(caze, sormasToSormasEnabled);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		SurvnetGateway.addComponentToLayout(layout, () -> Arrays.asList(caze.getUuid()));

		DocGenerationComponent.addComponentToLayout(layout, getCaseRef(), caze.getQuarantine());

		setCaseEditPermission(container);
	}
}

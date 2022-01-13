package de.symeda.sormas.ui.travelentry;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.CaseInfoLayout;
import de.symeda.sormas.ui.docgeneration.QuarantineOrderDocumentsComponent;
import de.symeda.sormas.ui.document.DocumentListComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class TravelEntryDataView extends AbstractTravelEntryView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";
	public static final String TRAVEL_ENTRY_LOC = "travelEntry";
	public static final String CASE_LOC = "case";
	public static final String DOCUMENTS_LOC = "documents";
	public static final String TASKS_LOC = "tasks";

	private CommitDiscardWrapperComponent<TravelEntryDataForm> editComponent;

	public TravelEntryDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected String getRootViewName() {
		return TravelEntriesView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(
			LayoutUtil.fluidColumnLoc(8, 0, 12, 0, TRAVEL_ENTRY_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, CASE_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, DOCUMENTS_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, QuarantineOrderDocumentsComponent.QUARANTINE_LOC),
			LayoutUtil.fluidColumnLoc(4, 0, 6, 0, TASKS_LOC));

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

		TravelEntryDto travelEntryDto = FacadeProvider.getTravelEntryFacade().getByUuid(getReference().getUuid());

		editComponent = ControllerProvider.getTravelEntryController().getTravelEntryDataEditComponent(getTravelEntryRef().getUuid());
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, TRAVEL_ENTRY_LOC);

		CaseReferenceDto resultingCase = travelEntryDto.getResultingCase();
		if (resultingCase == null) {
			Button createCaseButton = ButtonHelper.createButton(
				Captions.travelEntryCreateCase,
				e -> ControllerProvider.getCaseController().createFromTravelEntry(travelEntryDto),
				ValoTheme.BUTTON_PRIMARY,
				CssStyles.VSPACE_2);
			layout.addComponent(createCaseButton, CASE_LOC);
		} else {
			layout.addComponent(createCaseInfoLayout(resultingCase.getUuid()), CASE_LOC);
		}

		DocumentListComponent documentList = null;
		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.DOCUMENTS)) {
			documentList = new DocumentListComponent(
				DocumentRelatedEntityType.TRAVEL_ENTRY,
				getReference(),
				UserRight.TRAVEL_ENTRY_EDIT,
				travelEntryDto.isPseudonymized());
			layout.addComponent(new SideComponentLayout(documentList), DOCUMENTS_LOC);
		}

		QuarantineOrderDocumentsComponent.addComponentToLayout(layout, getTravelEntryRef(), documentList);

		setTravelEntryEditPermission(container);

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT)) {
			TaskListComponent taskList = new TaskListComponent(TaskContext.TRAVEL_ENTRY, getTravelEntryRef(), travelEntryDto.getDisease());
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addComponent(taskList, TASKS_LOC);
		}
	}

	private CaseInfoLayout createCaseInfoLayout(String caseUuid) {

		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseUuid);
		CaseInfoLayout caseInfoLayout = new CaseInfoLayout(caseDto, true);
		caseInfoLayout.addStyleName(CssStyles.SIDE_COMPONENT);

		return caseInfoLayout;
	}
}

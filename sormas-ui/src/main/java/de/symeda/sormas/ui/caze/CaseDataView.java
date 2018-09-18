package de.symeda.sormas.ui.caze;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.samples.SampleListComponent;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.ViewMode;

/**
 * CaseDataView for reading and editing the case data fields. Contains the
 * {@link CaseDataForm}.
 * 
 * @author Stefan Szczesny
 *
 */
public class CaseDataView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String CASE_LOC = "case";
	public static final String TASKS_LOC = "tasks";
	public static final String SAMPLES_LOC = "samples";

	public CaseDataView() {
		super(VIEW_NAME);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		setHeightUndefined();

		String htmlLayout = LayoutUtil.fluidRow(LayoutUtil.fluidColumnLoc(8, 0, 12, 0, CASE_LOC),
				LayoutUtil.fluidColumnLoc(4, 0, 6, 0, TASKS_LOC), LayoutUtil.fluidColumnLoc(4, 0, 6, 0, SAMPLES_LOC));

		VerticalLayout container = new VerticalLayout();
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		CustomLayout layout = new CustomLayout();
		layout.addStyleName(CssStyles.ROOT_COMPONENT);
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();
		container.addComponent(layout);

		CommitDiscardWrapperComponent<?> editComponent;
		if (getViewMode() == ViewMode.FULL) {
			editComponent = ControllerProvider.getCaseController().getCaseDataEditComponent(getCaseRef().getUuid(),
					getViewMode());
		} else {
			editComponent = ControllerProvider.getCaseController().getCaseCombinedEditComponent(getCaseRef().getUuid(),
					getViewMode());
		}
		// setSubComponent(editComponent);
		editComponent.setMargin(false);
		editComponent.setWidth(100, Unit.PERCENTAGE);
		editComponent.getWrappedComponent().setWidth(100, Unit.PERCENTAGE);
		editComponent.addStyleName(CssStyles.MAIN_COMPONENT);
		layout.addComponent(editComponent, CASE_LOC);

		TaskListComponent taskList = new TaskListComponent(TaskContext.CASE, getCaseRef());
		taskList.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(taskList, TASKS_LOC);

		SampleListComponent sampleList = new SampleListComponent(getCaseRef());
		sampleList.addStyleName(CssStyles.SIDE_COMPONENT);
		layout.addComponent(sampleList, SAMPLES_LOC);
	}
}

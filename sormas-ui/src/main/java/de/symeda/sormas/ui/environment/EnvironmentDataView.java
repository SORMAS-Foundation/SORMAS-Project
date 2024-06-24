package de.symeda.sormas.ui.environment;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.samples.HasName;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class EnvironmentDataView extends AbstractEnvironmentView implements HasName {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String TASKS_LOC = "tasks";
	public static final String SAMPLES_LOC = "samples";

	private CommitDiscardWrapperComponent<EnvironmentDataForm> editComponent;
	private EnvironmentDto environment;

	public EnvironmentDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected String getRootViewName() {
		return EnvironmentsView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		environment = FacadeProvider.getEnvironmentFacade().getByUuid(getReference().getUuid());

		editComponent = ControllerProvider.getEnvironmentController()
			.getEnvironmentDataEditComponent(getReference().getUuid(), UserRight.ENVIRONMENT_EDIT, isEditAllowed());

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent, TASKS_LOC, SAMPLES_LOC);
		container.addComponent(layout);

		boolean isEditAllowed = isEditAllowed();

		if (UiUtil.permitted(FeatureType.TASK_MANAGEMENT, UserRight.TASK_VIEW)) {
			TaskListComponent taskList =
				new TaskListComponent(TaskContext.ENVIRONMENT, getEnvironmentRef(), null, this::showUnsavedChangesPopup, isEditAllowed);
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(taskList, TASKS_LOC);
		}

		if (UiUtil.permitted(FeatureType.SAMPLES_LAB, UserRight.ENVIRONMENT_SAMPLE_VIEW)) {
			EnvironmentSampleListComponent sampleList = new EnvironmentSampleListComponent(environment, isEditAllowed, this::showUnsavedChangesPopup);
			sampleList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(sampleList, SAMPLES_LOC);
		}

		final String uuid = environment.getUuid();
		final EditPermissionType environmentEditAllowed = FacadeProvider.getEnvironmentFacade().getEditPermissionType(uuid);
		final boolean deleted = FacadeProvider.getEnvironmentFacade().isDeleted(uuid);
		layout.disableIfNecessary(deleted, environmentEditAllowed);
	}

	@Override
	protected boolean isEditAllowed() {
		return FacadeProvider.getEnvironmentFacade().isEditAllowed(environment.getUuid());
	}

	@Override
	public String getName() {
		return VIEW_NAME;
	}
}

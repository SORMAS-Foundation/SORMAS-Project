package de.symeda.sormas.ui.environment;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.task.TaskListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class EnvironmentDataView extends AbstractEnvironmentView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	public static final String TASKS_LOC = "tasks";

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

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent, TASKS_LOC);
		container.addComponent(layout);

		boolean isEditAllowed = isEditAllowed();

		if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.TASK_MANAGEMENT)
			&& UserProvider.getCurrent().hasUserRight(UserRight.TASK_VIEW)) {
			TaskListComponent taskList =
				new TaskListComponent(TaskContext.ENVIRONMENT, getEnvironmentRef(), null, this::showUnsavedChangesPopup, isEditAllowed);
			taskList.addStyleName(CssStyles.SIDE_COMPONENT);
			layout.addSidePanelComponent(taskList, TASKS_LOC);
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
}

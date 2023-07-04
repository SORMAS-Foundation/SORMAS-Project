package de.symeda.sormas.ui.environment;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;

public class EnvironmentDataView extends AbstractEnvironmentView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";

	private CommitDiscardWrapperComponent<EnvironmentDataForm> editComponent;

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

		EnvironmentDto environment = FacadeProvider.getEnvironmentFacade().getByUuid(getReference().getUuid());

		editComponent =
			ControllerProvider.getEnvironmentController().getEnvironmentDataEditComponent(getReference().getUuid(), this::showUnsavedChangesPopup);

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent);
		container.addComponent(layout);

		final String uuid = environment.getUuid();
		final EditPermissionType environmentEditAllowed = FacadeProvider.getEnvironmentFacade().getEditPermissionType(uuid);
		final boolean deleted = FacadeProvider.getEnvironmentFacade().isDeleted(uuid);
		layout.disableIfNecessary(deleted, environmentEditAllowed);
	}
}

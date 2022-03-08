package de.symeda.sormas.ui.immunization;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.immunization.components.form.ImmunizationDataForm;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.utils.ArchivingController;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.SidePanelLayout;

public class ImmunizationDataView extends AbstractImmunizationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";
	public static final String IMMUNIZATION_LOC = "immunization";
	public static final String SORMAS_TO_SORMAS_LOC = "sormsToSormas";

	private CommitDiscardWrapperComponent<ImmunizationDataForm> editComponent;

	public ImmunizationDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected String getRootViewName() {
		return ImmunizationsView.VIEW_NAME;
	}

	@Override
	protected void initView(String params) {
		setHeightUndefined();

		ImmunizationDto immunization = FacadeProvider.getImmunizationFacade().getByUuid(getReference().getUuid());

		editComponent = ControllerProvider.getImmunizationController().getImmunizationDataEditComponent(immunization);

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);

		SidePanelLayout layout = new SidePanelLayout(editComponent, SORMAS_TO_SORMAS_LOC);

		container.addComponent(layout);

		boolean sormasToSormasEnabled = FacadeProvider.getSormasToSormasFacade().isFeatureEnabledForUser();
		if (sormasToSormasEnabled || immunization.getSormasToSormasOriginInfo() != null) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(immunization);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		if (isImmunizationEditAllowed(false)) {
			if (FacadeProvider.getImmunizationFacade().isArchived(immunization.getUuid())
				&& FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(FeatureType.EDIT_ARCHIVED_ENTITIES)) {
				layout.disable(ArchivingController.ARCHIVE_DEARCHIVE_BUTTON_ID);
			}
		} else {
			layout.disable();
		}
	}
}

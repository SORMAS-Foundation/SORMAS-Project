package de.symeda.sormas.ui.immunization;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiListCriteria;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.aefilink.AefiListComponent;
import de.symeda.sormas.ui.immunization.components.form.ImmunizationDataForm;
import de.symeda.sormas.ui.sormastosormas.SormasToSormasListComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.LayoutWithSidePanel;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponentLayout;

public class ImmunizationDataView extends AbstractImmunizationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/data";
	public static final String IMMUNIZATION_LOC = "immunization";
	public static final String ADVERSE_EVENTS_LOC = "adverseEvents";
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

		//ImmunizationDto immunization = FacadeProvider.getImmunizationFacade().getByUuid(getReference().getUuid());

		editComponent =
			ControllerProvider.getImmunizationController().getImmunizationDataEditComponent(getReference().getUuid(), this::showUnsavedChangesPopup);

		DetailSubComponentWrapper container = new DetailSubComponentWrapper(() -> editComponent);
		container.setWidth(100, Unit.PERCENTAGE);
		container.setMargin(true);
		setSubComponent(container);
		container.setEnabled(true);

		LayoutWithSidePanel layout = new LayoutWithSidePanel(editComponent, SORMAS_TO_SORMAS_LOC, ADVERSE_EVENTS_LOC);

		container.addComponent(layout);

		ImmunizationDto immunization = FacadeProvider.getImmunizationFacade().getImmunizationByUuid(getReference().getUuid());

        if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_MANAGEMENT)
                && (immunization.getMeansOfImmunization() == MeansOfImmunization.VACCINATION
                || immunization.getMeansOfImmunization() == MeansOfImmunization.VACCINATION_RECOVERY)) {
            AefiListCriteria aefiListCriteria = new AefiListCriteria.Builder(getReference()).build();

            AefiListComponent aefiListComponent =
                    new AefiListComponent(aefiListCriteria, this::showUnsavedChangesPopup, isEditAllowed(), immunization.getVaccinations().size());
            CssStyles.style(aefiListComponent, CssStyles.VIEW_SECTION);

            layout.addSidePanelComponent(new SideComponentLayout(aefiListComponent), ADVERSE_EVENTS_LOC);
        }

		boolean sormasToSormasEnabled = FacadeProvider.getSormasToSormasFacade()
			.isAnyFeatureConfigured(
				FeatureType.SORMAS_TO_SORMAS_SHARE_CASES,
				FeatureType.SORMAS_TO_SORMAS_SHARE_CONTACTS,
				FeatureType.SORMAS_TO_SORMAS_SHARE_EVENTS);
		if (sormasToSormasEnabled || immunization.getSormasToSormasOriginInfo() != null || immunization.isOwnershipHandedOver()) {
			VerticalLayout sormasToSormasLocLayout = new VerticalLayout();
			sormasToSormasLocLayout.setMargin(false);
			sormasToSormasLocLayout.setSpacing(false);

			SormasToSormasListComponent sormasToSormasListComponent = new SormasToSormasListComponent(immunization);
			sormasToSormasListComponent.addStyleNames(CssStyles.SIDE_COMPONENT);
			sormasToSormasLocLayout.addComponent(sormasToSormasListComponent);

			layout.addSidePanelComponent(sormasToSormasLocLayout, SORMAS_TO_SORMAS_LOC);
		}

		final String uuid = immunization.getUuid();
		final EditPermissionType immunizationEditAllowed = FacadeProvider.getImmunizationFacade().getEditPermissionType(uuid);
		final boolean deleted = FacadeProvider.getImmunizationFacade().isDeleted(uuid);
		layout.disableIfNecessary(deleted, immunizationEditAllowed);
	}
}

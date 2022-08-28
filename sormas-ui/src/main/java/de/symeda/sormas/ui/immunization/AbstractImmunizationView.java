package de.symeda.sormas.ui.immunization;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractDetailView;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;
import de.symeda.sormas.ui.utils.DirtyStateComponent;

public abstract class AbstractImmunizationView extends AbstractEditAllowedDetailView<ImmunizationReferenceDto> {

	public static final String ROOT_VIEW_NAME = ImmunizationsView.VIEW_NAME;

	protected AbstractImmunizationView(String viewName) {
		super(viewName, FacadeProvider.getImmunizationFacade());
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {

		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected ImmunizationReferenceDto getReferenceByUuid(String uuid) {
		final ImmunizationReferenceDto reference;
		if (FacadeProvider.getImmunizationFacade().exists(uuid)) {
			reference = FacadeProvider.getImmunizationFacade().getReferenceByUuid(uuid);
		} else {
			reference = null;
		}
		return reference;
	}

	@Override
	protected String getRootViewName() {
		return ROOT_VIEW_NAME;
	}

	@Override
	protected void initView(String params) {

	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(ImmunizationsView.VIEW_NAME, I18nProperties.getCaption(Captions.immunizationImmunizationsList));
		menu.addView(ImmunizationDataView.VIEW_NAME, I18nProperties.getCaption(ImmunizationDto.I18N_PREFIX), params);
		menu.addView(ImmunizationPersonView.VIEW_NAME, I18nProperties.getPrefixCaption(ImmunizationDto.I18N_PREFIX, ImmunizationDto.PERSON), params);

		setMainHeaderComponent(ControllerProvider.getImmunizationController().getImmunizationViewTitleLayout(getReference().getUuid()));
	}

	public void setImmunizationEditPermission(Component component) {
		if (!isEditAllowed()) {
			component.setEnabled(false);
		}
	}

	@Override
	protected void setSubComponent(DirtyStateComponent newComponent) {
		super.setSubComponent(newComponent);

		ImmunizationDto dto = FacadeProvider.getImmunizationFacade().getByUuid(getReference().getUuid());
		if (dto.isDeleted()) {
			newComponent.setEnabled(false);
		}
	}
}

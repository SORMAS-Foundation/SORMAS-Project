package de.symeda.sormas.ui.environment;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.EditPermissionFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;

public abstract class AbstractEnvironmentView extends AbstractEditAllowedDetailView<EnvironmentReferenceDto> {

	public static final String ROOT_VIEW_NAME = EnvironmentsView.VIEW_NAME;

	protected AbstractEnvironmentView(String viewName) {
		super(viewName);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}

	@Override
	protected EnvironmentReferenceDto getReferenceByUuid(String uuid) {
		final EnvironmentReferenceDto reference;
		if (FacadeProvider.getEnvironmentFacade().exists(uuid)) {
			reference = FacadeProvider.getEnvironmentFacade().getReferenceByUuid(uuid);
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
	protected EditPermissionFacade getEditPermissionFacade() {
		return FacadeProvider.getEnvironmentFacade();
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}

		menu.removeAllViews();
		menu.addView(EnvironmentsView.VIEW_NAME, I18nProperties.getCaption(Captions.environmentEnvironmentsList));
		menu.addView(EnvironmentDataView.VIEW_NAME, I18nProperties.getCaption(EnvironmentDto.I18N_PREFIX), params);

		setMainHeaderComponent(ControllerProvider.getEnvironmentController().getEnvironmentViewTitleLayout(getReference().getUuid()));
	}

	public EnvironmentReferenceDto getEnvironmentRef() {
		return getReference();
	}

}

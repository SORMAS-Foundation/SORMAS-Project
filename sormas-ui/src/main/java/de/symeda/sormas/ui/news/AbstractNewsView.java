package de.symeda.sormas.ui.news;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.EditPermissionFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.news.NewsReferenceDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SubMenu;
import de.symeda.sormas.ui.utils.AbstractEditAllowedDetailView;

public abstract class AbstractNewsView extends AbstractEditAllowedDetailView<NewsReferenceDto> {

	public static final String ROOT_VIEW_NAME = NewsView.VIEW_NAME;

	protected AbstractNewsView(String viewName) {
		super(viewName);
	}

	@Override
	protected NewsReferenceDto getReferenceByUuid(String uuid) {
		return FacadeProvider.getNewsFacade().getReferenceByUuid(uuid);
	}

	@Override
	protected EditPermissionFacade getEditPermissionFacade() {
		return FacadeProvider.getNewsFacade();
	}

	@Override
	protected String getRootViewName() {
		return NewsView.VIEW_NAME;
	}

	@Override
	public void refreshMenu(SubMenu menu, String params) {
		if (!findReferenceByParams(params)) {
			return;
		}
		menu.removeAllViews();
		menu.addView(NewsView.VIEW_NAME, I18nProperties.getCaption(Captions.newsList));
		menu.addView(NewsDataView.VIEW_NAME, I18nProperties.getCaption(Captions.news), params);
		setMainHeaderComponent(ControllerProvider.getNewsController().getNewsHeaderComponent(getReference().getUuid()));
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		super.enter(event);
		initOrRedirect(event);
	}
}

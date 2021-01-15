package de.symeda.sormas.ui.utils;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.UI;

import de.symeda.sormas.api.ReferenceDto;

/**
 * A detail view shows specific details of an object identified by the URL parameter.
 * 
 * @param <R>
 *            {@link ReferenceDto} with the uuid as parsed from the URL.
 */
public abstract class AbstractDetailView<R extends ReferenceDto> extends AbstractSubNavigationView {

	private static final long serialVersionUID = -8898842364286757415L;

	private R reference;

	protected AbstractDetailView(String viewName) {
		super(viewName);
	}

	/**
	 * Initiates the content of this view if object was found, or it redirects to the fallback view.
	 * 
	 * @param event
	 *            Passed by {@link #enter(ViewChangeEvent)}.
	 */
	protected void initOrRedirect(ViewChangeEvent event) {

		if (getReference() == null) {
			UI.getCurrent().getNavigator().navigateTo(getRootViewName());
		} else {
			initView(event.getParameters().trim());
		}
	}

	/**
	 * Parses the object parameter out of the URL and populates {@link #getReference()}.<br />
	 * This method should be called in {@code refreshMenu}, because this is called in {@link AbstractSubNavigationView}
	 * before custom logic can be executed.
	 * 
	 * @param params
	 *            URL parameters.
	 * @return {@code true} if {@code params} refers to an object that can be presented.
	 */
	protected boolean findReferenceByParams(String params) {

		String[] passedParams = params.split("\\?");
		if (passedParams.length > 0) {
			// Remove possible slash from filters
			String uuid = passedParams[0].replaceAll("/", "");

			if (StringUtils.isNotBlank(uuid)) {
				reference = getReferenceByUuid(uuid);
			}
		}

		return reference != null;
	}

	/**
	 * @return The root object displayed on this View.
	 */
	protected R getReference() {
		return reference;
	}

	/**
	 * Loads the {@link ReferenceDto} identified by the {@code uuid}.
	 */
	protected abstract R getReferenceByUuid(String uuid);

	/**
	 * @return The fallback View when no object is found to display.
	 */
	protected abstract String getRootViewName();

	/**
	 * Will be called by {@link #enter(ViewChangeEvent)}, when an object is selected and the view shall show its specific content.
	 * 
	 * @param params
	 *            The URL parameters String
	 */
	protected abstract void initView(String params);
}

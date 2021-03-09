package de.symeda.sormas.ui.utils;

import de.symeda.sormas.ui.SormasUI;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

import javax.validation.constraints.NotNull;

/**
 * A detail view shows specific details of an object identified by the URL parameter.
 * 
 * @param <R>
 *            {@link ReferenceDto} with the uuid as parsed from the URL.
 */
public abstract class AbstractDetailView<R extends ReferenceDto> extends AbstractSubNavigationView<DirtyStateComponent> {

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
	protected void initOrRedirect(@NotNull SormasUI ui, ViewChangeEvent event) {

		if (getReference() == null) {
			UI.getCurrent().getNavigator().navigateTo(getRootViewName());
		} else {
			initView(ui, event.getParameters().trim());
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

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		if (subComponent != null && subComponent.isDirty()) {
			showNavigationConfirmPopup(event);
		} else {
			event.navigate();
		}
	}

	private void showNavigationConfirmPopup(ViewBeforeLeaveEvent event) {
		Window warningPopup = VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.unsavedChanges_warningTitle),
			new Label(I18nProperties.getString(Strings.unsavedChanges_warningMessage)),
			popupWindow -> {
				ConfirmationComponent confirmationComponent = new ConfirmationComponent(false, null) {

					private static final long serialVersionUID = 3664636750443474734L;

					@Override
					protected void onConfirm() {
						subComponent.commitAndHandle();
						popupWindow.close();
						event.navigate();
					}

					@Override
					protected void onCancel() {
						subComponent.discard();
						popupWindow.close();
						event.navigate();
					}
				};

				confirmationComponent.getConfirmButton().setCaption(I18nProperties.getString(Strings.unsavedChanges_save));
				confirmationComponent.getCancelButton().setCaption(I18nProperties.getString(Strings.unsavedChanges_discard));

				confirmationComponent.addExtraButton(
					ButtonHelper.createButtonWithCaption(
						Strings.unsavedChanges_cancel,
						I18nProperties.getString(Strings.unsavedChanges_cancel),
						null,
						ValoTheme.BUTTON_LINK),
					buttonEvent -> {
						popupWindow.close();
					});

				return confirmationComponent;
			},
			600);

		warningPopup.setClosable(true);
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
	protected abstract void initView(@NotNull final SormasUI ui, String params);
}

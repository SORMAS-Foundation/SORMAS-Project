package de.symeda.sormas.ui.utils;

import java.util.function.Function;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.i18n.I18nProperties;

public class ButtonHelper {

	public static Button createButton(String caption) {
		return createButton(caption, false, null, null);
	}

	public static Button createButton(String captionKey, Button.ClickListener clickListener, String... styles) {
		return createButton(captionKey, false, clickListener, styles);
	}

	public static Button createButton(String captionKey, boolean enableDoubleClick, Button.ClickListener clickListener, String... styles) {
		Button button = createButtonWithCaption(captionKey, I18nProperties.getCaption(captionKey), clickListener, styles);
		if (!enableDoubleClick) {
			preventDoubleClick(button);
		}
		return button;
	}

	public static Button createButtonWithCaption(String id, String caption, Button.ClickListener clickListener, String... styles) {
		return createButtonWithCaption(id, caption, false, clickListener, styles);
	}

	public static Button createButtonWithCaption(
		String id,
		String caption,
		boolean enableDoubleClick,
		Button.ClickListener clickListener,
		String... styles) {

		Button button = createButton(Button::new, id, caption, enableDoubleClick, styles);
		if (clickListener != null) {
			button.addClickListener(clickListener);
		}
		return button;
	}

	public static Button createIconButtonWithCaption(String id, String caption, Resource icon, Button.ClickListener clickListener, String... styles) {

		return createIconButtonWithCaption(id, caption, icon, false, clickListener, styles);
	}

	public static Button createIconButtonWithCaption(
		String id,
		String caption,
		Resource icon,
		boolean enableDoubleClick,
		Button.ClickListener clickListener,
		String... styles) {

		Button button = createButtonWithCaption(id, caption, enableDoubleClick, clickListener, styles);
		button.setIcon(icon);
		return button;
	}

	public static Button createIconButton(String captionKey, Resource icon, Button.ClickListener clickListener, String... styles) {

		return createIconButton(captionKey, icon, false, clickListener, styles);
	}

	public static Button createIconButton(
		String captionKey,
		Resource icon,
		boolean enableDoubleClick,
		Button.ClickListener clickListener,
		String... styles) {

		Button button = createButton(captionKey, enableDoubleClick, clickListener, styles);
		button.setIcon(icon);
		return button;
	}

	public static PopupButton createPopupButtonWithDescription(String id, String caption, Component content, String... styles) {

		PopupButton button = createButton(PopupButton::new, id, caption, styles);
		button.setContent(content);
		preventDoubleClick(button);
		return button;
	}

	public static PopupButton createPopupButton(String captionKey, Component content, String... styles) {
		return createPopupButtonWithDescription(captionKey, I18nProperties.getCaption(captionKey), content, styles);
	}

	public static PopupButton createIconPopupButton(String captionKey, Resource icon, Component content, String... styles) {

		PopupButton button = createPopupButton(captionKey, content, styles);
		button.setIcon(icon);
		return button;
	}

	public static <T extends Button> T createButton(Function<String, T> buttonFactory, String id, String caption, String... styles) {

		return createButton(buttonFactory, id, caption, false, styles);
	}

	public static <T extends Button> T createButton(
		Function<String, T> buttonFactory,
		String id,
		String caption,
		boolean enableDoubleClick,
		String... styles) {

		T button = buttonFactory.apply(caption);
		button.setId(id);
		if (styles != null) {
			CssStyles.style(button, styles);
		}
		if (!enableDoubleClick) {
			preventDoubleClick(button);
		}
		return button;
	}

	static final Button.ClickListener ENABLE_LISTENER = event -> event.getButton().setEnabled(true);

	/**
	 * Prevent double-clicks causing the buttons action being executed twice
	 * 
	 * @param button
	 * @param <T>
	 * @return
	 */
	public static <T extends Button> T preventDoubleClick(T button) {
		button.setDisableOnClick(true);
		button.addClickListener(ENABLE_LISTENER);
		return button;
	}

	/**
	 * Re-Enable double clicks, if they were disabled using ButtonHelper.preventDoubleClick
	 * 
	 * @param button
	 * @param <T>
	 * @return
	 */
	public static <T extends Button> T allowDoubleClick(T button) {
		button.setEnabled(true); // Make sure the button doesn't end up in a forever-disabled state
		button.setDisableOnClick(false);
		button.removeClickListener(ENABLE_LISTENER);
		return button;
	}
}

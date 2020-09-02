package de.symeda.sormas.ui.utils;

import java.util.function.Function;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

import de.symeda.sormas.api.i18n.I18nProperties;

public class ButtonHelper {

	public static Button createButtonWithCaption(String id, String caption, Button.ClickListener clickListener, String... styles) {

		Button button = createButton(Button::new, id, caption, styles);
		if (clickListener != null) {
			button.addClickListener(clickListener);
		}
		return button;
	}

	public static Button createButton(String captionKey, Button.ClickListener clickListener, String... styles) {
		return createButtonWithCaption(captionKey, I18nProperties.getCaption(captionKey), clickListener, styles);
	}

	public static Button createIconButtonWithCaption(String id, String caption, Resource icon, Button.ClickListener clickListener, String... styles) {

		Button button = createButtonWithCaption(id, caption, clickListener, styles);
		button.setIcon(icon);
		return button;
	}

	public static Button createIconButton(String captionKey, Resource icon, Button.ClickListener clickListener, String... styles) {

		Button button = createButton(captionKey, clickListener, styles);
		button.setIcon(icon);
		return button;
	}

	public static PopupButton createPopupButtonWithDescription(String id, String caption, Component content, String... styles) {

		PopupButton button = createButton(PopupButton::new, id, caption, styles);
		button.setContent(content);
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

	public static PopupButton createIconPopupButtonWithCaption(String id, String caption, Resource icon, Component content, String... styles) {

		PopupButton button = createPopupButtonWithDescription(id, caption, content, styles);
		button.setIcon(icon);
		return button;
	}

	public static <T extends Button> T createButton(Function<String, T> buttonFactory, String id, String caption, String... styles) {

		T button = buttonFactory.apply(caption);
		button.setId(id);
		CssStyles.style(button, styles);
		return button;
	}
}

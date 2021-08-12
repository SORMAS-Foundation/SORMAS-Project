package de.symeda.sormas.ui.immunization.components.status;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class StatusBarLayout extends HorizontalLayout {

	private final StatusBarItems statusBarItems;

	public StatusBarLayout() {
		setSpacing(true);
		setMargin(false);
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		addStyleName(CssStyles.VSPACE_3);

		statusBarItems = new StatusBarItems();
	}

	public void addItem(String captionKey, Button.ClickListener listener) {
		Button button =
			ButtonHelper.createButton(I18nProperties.getCaption(captionKey), listener, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		button.setCaptionAsHtml(true);

		statusBarItems.putItem(captionKey, button);
		addComponent(button);
	}

	public void updateActiveBadge(int badgeValue) {
		statusBarItems.updateActiveItem(String.valueOf(badgeValue));
	}
}

package de.symeda.sormas.ui.immunization.components.filter.status;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class StatusBar extends HorizontalLayout {

	private final Map<String, Button> items;
	private String activeItem;

	public StatusBar() {
		setSpacing(true);
		setMargin(false);
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		addStyleName(CssStyles.VSPACE_3);

		items = new LinkedHashMap<>();
	}

	public void addItem(String captionKey, Button.ClickListener listener) {
		Button button =
			ButtonHelper.createButton(I18nProperties.getCaption(captionKey), listener, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		button.setCaptionAsHtml(true);

		items.put(captionKey, button);
		if (items.size() == 1) {
			activeItem = captionKey;
		}
		addComponent(button);
	}

	public void updateStatusButton(String badgeValue) {
		Button activeButton = items.get(activeItem);
		if (activeButton != null) {
			activeButton.setCaption(I18nProperties.getCaption(Captions.all) + LayoutUtil.spanCss(CssStyles.BADGE, badgeValue));
		}
	}
}

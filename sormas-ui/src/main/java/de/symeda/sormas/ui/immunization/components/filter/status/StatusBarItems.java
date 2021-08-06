package de.symeda.sormas.ui.immunization.components.filter.status;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.ui.Button;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class StatusBarItems {

	private final Map<String, Button> items;
	private String activeItem;

	public StatusBarItems() {
		items = new LinkedHashMap<>();
	}

	public void putItem(String captionKey, Button button) {
		items.put(captionKey, button);
		if (items.size() == 1) {
			activeItem = captionKey;
		}
	}

	public void updateActiveItem(String badgeValue) {
		Button activeButton = items.get(activeItem);
		if (activeButton != null) {
			activeButton.setCaption(I18nProperties.getCaption(Captions.all) + LayoutUtil.spanCss(CssStyles.BADGE, badgeValue));
		}
	}
}

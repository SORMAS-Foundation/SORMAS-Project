package de.symeda.sormas.ui.immunization.components.filter.status;

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

	private final Button statusAll;

	public StatusBar(String captionKey, Button.ClickListener listener) {
		setSpacing(true);
		setMargin(false);
		setWidth(100, Sizeable.Unit.PERCENTAGE);
		addStyleName(CssStyles.VSPACE_3);

		statusAll = ButtonHelper.createButton(I18nProperties.getCaption(captionKey), listener, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);

		addComponent(statusAll);
	}

	public void updateStatusButton(String badgeValue) {
		if (statusAll != null) {
			statusAll.setCaption(I18nProperties.getCaption(Captions.all) + LayoutUtil.spanCss(CssStyles.BADGE, badgeValue));
		}
	}
}

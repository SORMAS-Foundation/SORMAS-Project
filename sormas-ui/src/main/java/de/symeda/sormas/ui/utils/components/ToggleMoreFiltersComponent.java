package de.symeda.sormas.ui.utils.components;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ToggleMoreFiltersComponent extends HorizontalLayout {

	private final CustomLayout moreFiltersLayout;

	private final String showMoreCaption;
	private final String showLessCaption;
	private final Button showHideMoreButton;

	public ToggleMoreFiltersComponent(CustomLayout moreFiltersLayout) {
		this.moreFiltersLayout = moreFiltersLayout;

		showMoreCaption = I18nProperties.getCaption(Captions.actionShowMoreFilters);
		showLessCaption = I18nProperties.getCaption(Captions.actionShowLessFilters);
		showHideMoreButton =
			ButtonHelper.createIconButtonWithCaption("showHideMoreFilters", buildShowHideMoreCaption(), VaadinIcons.CHEVRON_DOWN, e -> {
				Button showHideButton = e.getButton();
				if (moreFiltersLayout.isVisible()) {
					showHideButton.setCaption(showMoreCaption);
					showHideButton.setIcon(VaadinIcons.CHEVRON_DOWN);
					moreFiltersLayout.setVisible(false);
				} else {
					showHideButton.setCaption(showLessCaption);
					showHideButton.setIcon(VaadinIcons.CHEVRON_UP);
					moreFiltersLayout.setVisible(true);
				}
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);

		addComponent(showHideMoreButton);
	}

	public void toggleMoreFilters(boolean shouldShowMoreFilters) {
		moreFiltersLayout.setVisible(shouldShowMoreFilters);
		showHideMoreButton.setCaption(buildShowHideMoreCaption());
		showHideMoreButton.setIcon(buildShowHideMoreIcon());
	}

	private String buildShowHideMoreCaption() {
		return moreFiltersLayout.isVisible() ? showLessCaption : showMoreCaption;
	}

	private VaadinIcons buildShowHideMoreIcon() {
		return moreFiltersLayout.isVisible() ? VaadinIcons.CHEVRON_UP : VaadinIcons.CHEVRON_DOWN;
	}
}

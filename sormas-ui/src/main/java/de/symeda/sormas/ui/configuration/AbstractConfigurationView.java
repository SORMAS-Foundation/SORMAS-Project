package de.symeda.sormas.ui.configuration;

import java.util.Optional;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

public abstract class AbstractConfigurationView extends AbstractSubNavigationView {

	private static final long serialVersionUID = 3193505016439327054L;

	protected AbstractConfigurationView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		menu.removeAllViews();
		if (LoginHelper.hasUserRight(UserRight.FACILITIES_VIEW)) {
			menu.addView(HealthFacilitiesView.VIEW_NAME, I18nProperties.getPrefixFragment("View",
					HealthFacilitiesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""), params);
			menu.addView(LaboratoriesView.VIEW_NAME, I18nProperties.getPrefixFragment("View",
					LaboratoriesView.VIEW_NAME.replaceAll("/", ".") + ".short", ""), params);
		}
		menu.addView(OutbreaksView.VIEW_NAME,
				I18nProperties.getPrefixFragment("View", OutbreaksView.VIEW_NAME.replaceAll("/", ".") + ".short", ""),
				params);
	}

	public static void registerViews(Navigator navigator) {
		if (LoginHelper.hasUserRight(UserRight.FACILITIES_VIEW)) {
			navigator.addView(HealthFacilitiesView.VIEW_NAME, HealthFacilitiesView.class);
			navigator.addView(LaboratoriesView.VIEW_NAME, LaboratoriesView.class);
		}
		navigator.addView(OutbreaksView.VIEW_NAME, OutbreaksView.class);
	}

	@Override
	protected Optional<VerticalLayout> createInfoLayout() {
		return Optional.empty();
	}
}

package de.symeda.sormas.ui.configuration;

import java.util.Optional;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

/**
 * @author Christopher Riedel
 *
 */
public abstract class AbstractConfigurationView extends AbstractSubNavigationView {

	private static final long serialVersionUID = 3193505016439327054L;

	protected AbstractConfigurationView(String viewName) {
		super(viewName);
	}
	
	@Override
	public void refreshMenu(SubNavigationMenu menu, Label infoLabel, Label infoLabelSub, String params) {
		menu.removeAllViews();
		//menu.addView(ConfigurationHealthFacilitiesView.VIEW_NAME, I18nProperties.getFieldCaption("Configuration.HealthFacilities"), params);
		//menu.addView(ConfigurationLaboratoriesView.VIEW_NAME, I18nProperties.getFieldCaption("Configuration.Laboratories"), params);
		menu.addView(ConfigurationOutbreakView.VIEW_NAME, I18nProperties.getFieldCaption("Configuration.Outbreaks"), params);
	}
	
	@Override
	protected Optional<VerticalLayout> createInfoLayout() {
		return Optional.empty();
	}
}

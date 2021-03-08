package de.symeda.sormas.ui.dashboard.surveillance.components;

import com.vaadin.v7.data.Property;

import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.dashboard.surveillance.SurveillanceDashboardView;
import de.symeda.sormas.ui.utils.components.datetypeselector.DateTypeSelectorComponent;

public class SurveillanceFilterLayout extends DashboardFilterLayout {

	private DateTypeSelectorComponent dateTypeSelectorComponent;

	public SurveillanceFilterLayout(SurveillanceDashboardView dashboardView, DashboardDataProvider dashboardDataProvider) {
		super(dashboardView, dashboardDataProvider);
	}

	public void populateLayout() {
		createDateFilters();
		createDateTypeSelectorFilter();
		createRegionAndDistrictFilter();
		createResetAndApplyButtons();
	}

	public void addDateTypeValueChangeListener(Property.ValueChangeListener listener) {
		dateTypeSelectorComponent.addValueChangeListener(listener);
	}

	private void createDateTypeSelectorFilter() {
		dateTypeSelectorComponent =
			new DateTypeSelectorComponent.Builder<>(NewCaseDateType.class).dateTypePrompt(I18nProperties.getString(Strings.promptNewCaseDateType))
				.build();
		dateTypeSelectorComponent.setValue(NewCaseDateType.MOST_RELEVANT);
		addComponent(dateTypeSelectorComponent);
	}
}

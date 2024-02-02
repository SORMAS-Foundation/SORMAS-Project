package de.symeda.sormas.ui.dashboard.surveillance.components;

import com.vaadin.v7.data.Property;

import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.dashboard.surveillance.SurveillanceDashboardView;
import de.symeda.sormas.ui.utils.components.datetypeselector.DateTypeSelectorComponent;


public class SurveillanceFilterLayout extends DashboardFilterLayout<DashboardDataProvider> {

	public static final String DATE_TYPE_SELECTOR_FILTER = "dateTypeSelectorFilter";
	private final static String[] SURVEILLANCE_FILTERS = new String[] {
		DATE_TYPE_SELECTOR_FILTER,
		REGION_FILTER,
		DISTRICT_FILTER ,CASE_CLASSIFICATION_FILTER
	};
	private DateTypeSelectorComponent dateTypeSelectorComponent;

	public SurveillanceFilterLayout(SurveillanceDashboardView dashboardView, DashboardDataProvider dashboardDataProvider) {
		super(dashboardView, dashboardDataProvider, SURVEILLANCE_FILTERS);
	}

	@Override
	public void populateLayout() {
		super.populateLayout();
		createDateTypeSelectorFilter();
		createRegionAndDistrictFilter();
		createCaseClassificationFilter();

	}

	public void addDateTypeValueChangeListener(Property.ValueChangeListener listener) {
		dateTypeSelectorComponent.addValueChangeListener(listener);
	}

//	private void createDateTypeSelectorFilter() {
//		dateTypeSelectorComponent =
//			new DateTypeSelectorComponent.Builder<>(NewCaseDateType.class).dateTypePrompt(I18nProperties.getString(Strings.promptNewCaseDateType))
//				.build();
//		dateTypeSelectorComponent.setValue(NewCaseDateType.MOST_RELEVANT);
//		addCustomComponent(dateTypeSelectorComponent, DATE_TYPE_SELECTOR_FILTER);
//	}

	private void createDateTypeSelectorFilter() {
		dateTypeSelectorComponent =
				new DateTypeSelectorComponent.Builder<>(NewCaseDateType.class).dateTypePrompt(I18nProperties.getString(Strings.promptNewCaseDateType))
						.build();
		dateTypeSelectorComponent.setValue(dashboardDataProvider.getNewCaseDateType());
		addCustomComponent(dateTypeSelectorComponent, DATE_TYPE_SELECTOR_FILTER);
	}

	public void setCriteria(DashboardCriteria criteria) {
		super.setCriteria(criteria);
		dateTypeSelectorComponent.setValue(criteria.getNewCaseDateType());
	}
}

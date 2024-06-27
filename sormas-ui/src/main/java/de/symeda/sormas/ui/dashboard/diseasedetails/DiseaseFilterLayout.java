package de.symeda.sormas.ui.dashboard.diseasedetails;


import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.utils.components.datetypeselector.DateTypeSelectorComponent;

public class DiseaseFilterLayout extends DashboardFilterLayout<DashboardDataProvider> {
    public static final String DATE_TYPE_SELECTOR_FILTER = "dateTypeSelectorFilter";

    private DateTypeSelectorComponent dateTypeSelectorComponent;

    private static final String CASE_CLASSIFICATION_FILTER ="caseClassificationFilter" ;
    private static final String[] DISEASE_FILTERS = new String[]{
            DATE_TYPE_SELECTOR_FILTER,
            REGION_FILTER,
            DISTRICT_FILTER,
            CASE_CLASSIFICATION_FILTER
    };

    public DiseaseFilterLayout(DiseaseDetailsView dashboardView, DashboardDataProvider dashboardDataProvider) {
        super(dashboardView, dashboardDataProvider,DISEASE_FILTERS);
    }


    @Override
    public void setCriteria(DashboardCriteria criteria) {
        super.setCriteria(criteria);
        dateTypeSelectorComponent.setValue(criteria.getNewCaseDateType());
    }

}

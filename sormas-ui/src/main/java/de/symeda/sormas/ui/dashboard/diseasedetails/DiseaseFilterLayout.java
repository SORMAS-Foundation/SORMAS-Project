package de.symeda.sormas.ui.dashboard.diseasedetails;




import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.dashboard.contacts.ContactsDashboardView;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.components.datetypeselector.DateTypeSelectorComponent;

public class DiseaseFilterLayout extends DashboardFilterLayout {
    public static final String DATE_TYPE_SELECTOR_FILTER = "dateTypeSelectorFilter";

    public static final String INFO_LABEL = "infoLabel";
    public static final String DISEASE_FILTER = "diseaseFilter";
    private DateTypeSelectorComponent dateTypeSelectorComponent;

    private static final String CASE_CLASSIFICATION_FILTER ="caseClassificationFilter" ;
    private final static String[] DISEASE_FILTERS = new String[]{
            DATE_TYPE_SELECTOR_FILTER,
            REGION_FILTER,
            DISTRICT_FILTER,
            CASE_CLASSIFICATION_FILTER
    };
    private ComboBox diseaseFilter;

    public DiseaseFilterLayout(DiseaseDetailsView dashboardView, DashboardDataProvider dashboardDataProvider) {
        super(dashboardView, dashboardDataProvider,DISEASE_FILTERS);
    }

    @Override
    public void populateLayout() {
        //createDateTypeSelectorFilter();
        //super.createDateFilters();
//		super.populateLayout();
//		createRegionAndDistrictFilter();
//		createDateTypeSelectorFilter();
//		createCaseClassificationFilter();

    }

    public void addDateTypeValueChangeListener(Property.ValueChangeListener listener) {
        dateTypeSelectorComponent.addValueChangeListener(listener);
    }

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

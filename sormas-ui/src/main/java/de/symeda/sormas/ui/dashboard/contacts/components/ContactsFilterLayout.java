package de.symeda.sormas.ui.dashboard.contacts.components;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.dashboard.contacts.ContactsDashboardView;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class ContactsFilterLayout extends DashboardFilterLayout {

	public static final String INFO_LABEL = "infoLabel";
	public static final String DISEASE_FILTER = "diseaseFilter";
	private final static String[] CONTACTS_FILTERS = new String[]{INFO_LABEL, REGION_FILTER, DISTRICT_FILTER, DISEASE_FILTER};
	private ComboBox diseaseFilter;

	public ContactsFilterLayout(ContactsDashboardView dashboardView, DashboardDataProvider dashboardDataProvider) {
		super(dashboardView, dashboardDataProvider, CONTACTS_FILTERS);
	}

	@Override
	public void populateLayout() {

		super.populateLayout();
		createInfoLabel();
		createRegionAndDistrictFilter();
		if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
			createDiseaseFilter();
		}
	}

	public boolean hasDiseaseSelected() {
		return diseaseFilter.getValue() != null;
	}

	private void createInfoLabel() {
		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription(I18nProperties.getString(Strings.infoContactDashboard));
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);

		addCustomComponent(infoLabel, INFO_LABEL);
	}

	private void createDiseaseFilter() {
		this.diseaseFilter = ComboBoxHelper.createComboBoxV7();
		diseaseFilter.setWidth(200, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getString(Strings.promptDisease));
		diseaseFilter.addItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseasesWithFollowUp(true, true, true).toArray());
		diseaseFilter.setValue(dashboardDataProvider.getDisease());
		diseaseFilter.addValueChangeListener(e -> {
			dashboardDataProvider.setDisease((Disease) diseaseFilter.getValue());
		});
		addCustomComponent(diseaseFilter, DISEASE_FILTER);
	}
}

package de.symeda.sormas.ui.dashboard.contacts.components;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
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
import de.symeda.sormas.ui.utils.CssStyles;

public class ContactsFilterLayout extends DashboardFilterLayout {

	private ComboBox diseaseFilter;

	public ContactsFilterLayout(ContactsDashboardView dashboardView, DashboardDataProvider dashboardDataProvider) {
		super(dashboardView, dashboardDataProvider);
	}

	public void populateLayout() {
		createDateFilters();
		createInfoLabel();
		createRegionAndDistrictFilter();
		if (dashboardDataProvider.getDashboardType() == DashboardType.CONTACTS) {
			createDiseaseFilter();
		}
		createResetAndApplyButtons();
	}

	public boolean hasDiseaseSelected() {
		return diseaseFilter.getValue() != null;
	}

	private void createInfoLabel() {
		Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription(I18nProperties.getString(Strings.infoContactDashboard));
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
		addComponent(infoLabel);
		setComponentAlignment(infoLabel, Alignment.TOP_RIGHT);
	}

	private void createDiseaseFilter() {
		this.diseaseFilter = new ComboBox();
		diseaseFilter.setWidth(200, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getString(Strings.promptDisease));
		diseaseFilter.addItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseasesWithFollowUp(true, true, true).toArray());
		diseaseFilter.setValue(dashboardDataProvider.getDisease());
		diseaseFilter.addValueChangeListener(e -> {
			dashboardDataProvider.setDisease((Disease) diseaseFilter.getValue());
		});
		addComponent(diseaseFilter);
	}
}

package de.symeda.sormas.ui.dashboard.diseasedetails;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.dashboard.components.DashboardFilterLayout;
import de.symeda.sormas.ui.utils.ViewConfiguration;

import static com.vaadin.navigator.ViewChangeListener.*;

@SuppressWarnings("serial")
public class DiseaseDetailsView extends AbstractDashboardView {

	private static final long serialVersionUID = -1L;
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/disease";

	protected DiseaseDetailsViewLayout diseaseDetailsViewLayout;
	private DiseaseDetailsComponent diseaseDetailsComponent;
	//private RegionalDiseaseBurdenGrid regionalDiseaseBurdenGrid;
	private ViewConfiguration viewConfiguration;

	public DiseaseDetailsView() {
		super(VIEW_NAME);
		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.classificationForDisease));
		//filterLayout = new DashboardFilterLayout(this, dashboardDataProvider);
		dashboardLayout.setSpacing(false);

		//Added Component
		diseaseDetailsViewLayout = new DiseaseDetailsViewLayout(dashboardDataProvider);
		dashboardLayout.addComponent(diseaseDetailsViewLayout);
		dashboardLayout.setExpandRatio(diseaseDetailsViewLayout, 1);

		/*diseaseDetailsComponent = new DiseaseDetailsComponent(dashboardDataProvider);
		dashboardLayout.addComponent(diseaseDetailsComponent);
		dashboardLayout.setExpandRatio(diseaseDetailsComponent, 1);

		regionalDiseaseBurdenGrid = new RegionalDiseaseBurdenGrid(dashboardDataProvider);
		dashboardLayout.addComponent(regionalDiseaseBurdenGrid);
		dashboardLayout.setExpandRatio(regionalDiseaseBurdenGrid, 2);*/
	}

	@Override
	public void refreshDiseaseData() {
		super.refreshDiseaseData();
		/*if (diseaseDetailsComponent != null)
			diseaseDetailsComponent.refresh();*/

		if (diseaseDetailsViewLayout != null)
			diseaseDetailsViewLayout.refresh();

	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		disease = Disease.valueOf(event.getParameters().toString());
		dashboardDataProvider.setDisease(Disease.valueOf(event.getParameters().toString()));
		refreshDiseaseData();
	}
}

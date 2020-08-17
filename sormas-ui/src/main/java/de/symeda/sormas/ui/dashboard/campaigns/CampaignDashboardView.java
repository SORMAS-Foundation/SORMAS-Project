package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;

public class CampaignDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigns";

	protected CampaignDashboardFilterLayout filterLayout;
	protected CampaignDashboardDataProvider dataProvider;

	private List<CampaignDashboardDiagramComponent> campaignDashboardDiagramComponents = new ArrayList<>();

	public CampaignDashboardView() {
		super(VIEW_NAME);

		dataProvider = new CampaignDashboardDataProvider();
		filterLayout = new CampaignDashboardFilterLayout(this, dataProvider);
		dashboardLayout.addComponent(filterLayout);
		dashboardLayout.setExpandRatio(filterLayout, 0);

		dashboardSwitcher.setValue(DashboardType.CAMPAIGNS);
		dashboardSwitcher.addValueChangeListener(e -> navigateToDashboardView(e));

		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoCampaignsDashboard));
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		refreshDashboard();
	}

	@Override
	public void refreshDashboard() {

		campaignDashboardDiagramComponents
			.forEach(campaignDashboardDiagramComponents -> dashboardLayout.removeComponent(campaignDashboardDiagramComponents));
		dataProvider.refreshData();
		dataProvider.getCampaignFormDataMap().forEach((campaignDiagramDefinitionDto, diagramData) -> {
			CampaignDashboardDiagramComponent diagramComponent = new CampaignDashboardDiagramComponent(campaignDiagramDefinitionDto, diagramData);
			campaignDashboardDiagramComponents.add(diagramComponent);
			dashboardLayout.addComponent(diagramComponent);
			dashboardLayout.setExpandRatio(diagramComponent, 1);
		});
	}
}

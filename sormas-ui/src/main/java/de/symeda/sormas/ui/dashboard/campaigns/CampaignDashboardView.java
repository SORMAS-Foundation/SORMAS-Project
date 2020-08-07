package de.symeda.sormas.ui.dashboard.campaigns;

import com.vaadin.navigator.ViewChangeListener;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;

public class CampaignDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigns";

	protected CampaignDashboardFilterLayout filterLayout;
	protected CampaignDashboardDataProvider dataProvider;

	public CampaignDashboardView() {
		super(VIEW_NAME);

		dataProvider = new CampaignDashboardDataProvider();
		filterLayout = new CampaignDashboardFilterLayout(this, dataProvider);
		dashboardLayout.addComponent(filterLayout);

		dashboardSwitcher.setValue(DashboardType.CAMPAIGNS);
		dashboardSwitcher.addValueChangeListener(e -> navigateToDashboardView(e));

		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoCampaignsDashboard));

		dashboardLayout.addComponent(filterLayout);
		dashboardLayout.setSpacing(false);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		refreshDashboard();
	}

	@Override
	public void refreshDashboard() {
		dataProvider.refreshData();
		dataProvider.getCampaignFormDataMap()
			.forEach(
				(campaignDiagramDefinitionDto, campaignFormDataDtos) -> dashboardLayout
					.addComponent(new CampaignDashboardDiagramComponent(campaignDiagramDefinitionDto, campaignFormDataDtos)));

	}
}

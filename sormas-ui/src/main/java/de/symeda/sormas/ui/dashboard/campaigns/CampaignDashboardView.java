package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.AbstractDashboardView;
import de.symeda.sormas.ui.dashboard.DashboardType;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class CampaignDashboardView extends AbstractDashboardView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/campaigns";

	protected CampaignDashboardFilterLayout filterLayout;
	protected CampaignDashboardDataProvider dataProvider;

	protected CustomLayout diagramsLayout;

	private List<VerticalLayout> campaignDashboardDiagramComponents = new ArrayList<>();

	public CampaignDashboardView() {
		super(VIEW_NAME);

		dataProvider = new CampaignDashboardDataProvider();
		filterLayout = new CampaignDashboardFilterLayout(this, dataProvider);
		dashboardLayout.addComponent(filterLayout);

		dashboardSwitcher.setValue(DashboardType.CAMPAIGNS);
		dashboardSwitcher.addValueChangeListener(e -> navigateToDashboardView(e));

		filterLayout.setInfoLabelText(I18nProperties.getString(Strings.infoCampaignsDashboard));

		diagramsLayout = new CustomLayout();

		diagramsLayout.setWidth(100, Unit.PERCENTAGE);
		dashboardLayout.addComponent(diagramsLayout);
		dashboardLayout.setHeightUndefined();
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		refreshDashboard();
	}

	@Override
	public void refreshDashboard() {

		campaignDashboardDiagramComponents
			.forEach(campaignDashboardDiagramComponents -> diagramsLayout.removeComponent(campaignDashboardDiagramComponents));
		campaignDashboardDiagramComponents.clear();
		dataProvider.refreshData();
		Map<CampaignDiagramDefinitionDto, List<CampaignDiagramDataDto>> campaignFormDataMap = dataProvider.getCampaignFormDataMap();
		List<LayoutUtil.FluidColumn> fluidColumns = campaignFormDataMap.keySet().stream().map(campaignDiagramDefinitionDto -> LayoutUtil.fluidColumnLoc(6, 0, 12, 0, campaignDiagramDefinitionDto.getDiagramId())).collect(Collectors.toList());
		diagramsLayout.setTemplateContents(
				LayoutUtil.fluidRow(fluidColumns.toArray(new LayoutUtil.FluidColumn[fluidColumns.size()])));
		campaignFormDataMap.forEach((campaignDiagramDefinitionDto, diagramData) -> {
			final CampaignDashboardDiagramComponent diagramComponent =
				new CampaignDashboardDiagramComponent(campaignDiagramDefinitionDto, diagramData);
			campaignDashboardDiagramComponents.add(diagramComponent);
			diagramsLayout.addComponent(diagramComponent, campaignDiagramDefinitionDto.getDiagramId());
		});
	}
}

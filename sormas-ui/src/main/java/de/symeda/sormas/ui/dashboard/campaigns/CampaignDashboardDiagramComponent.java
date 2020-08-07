package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.List;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.ui.highcharts.HighChart;

public class CampaignDashboardDiagramComponent extends VerticalLayout {

	private CampaignDiagramDefinitionDto campaignDiagramDefinitionDto;
	private List<CampaignFormDataDto> campaignFormDataDtoList;
	private final HighChart campaignColumnChart;

	public CampaignDashboardDiagramComponent(
		CampaignDiagramDefinitionDto campaignDiagramDefinitionDto,
		List<CampaignFormDataDto> campaignFormDataDtoList) {

		this.campaignDiagramDefinitionDto = campaignDiagramDefinitionDto;
		this.campaignFormDataDtoList = campaignFormDataDtoList;

		campaignColumnChart = new HighChart();
		campaignColumnChart.setSizeFull();

		addComponent(campaignColumnChart);
		setExpandRatio(campaignColumnChart, 1);

		buildDiagramChart();
	}

	private void buildDiagramChart() {
		final StringBuilder hcjs = new StringBuilder();

		//@formatter:off
		hcjs.append("var options = {"
				+ "chart:{ "
				+ " type: 'column', "
				+ " backgroundColor: 'transparent' "
				+ "},"
				+ "credits:{ enabled: false },"
				+ "exporting:{ "
				+ " enabled: true,"
				+ " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }"
				+ "},"
				+ "title:{ text: '' },");
		//@formatter:on

		hcjs.append("xAxis: { series: [");
		hcjs.append("'" + "testX" + "']},");

		//@formatter:off
		hcjs.append("yAxis: { min: 0, title: { text: testY}}}");
		//@formatter:on

		campaignColumnChart.setHcjs(hcjs.toString());
	}
}

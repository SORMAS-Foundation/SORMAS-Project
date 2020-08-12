package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.ui.highcharts.HighChart;

public class CampaignDashboardDiagramComponent extends VerticalLayout {

	private CampaignDiagramDefinitionDto diagramDefinition;

	private List<CampaignDiagramDataDto> diagramDataList;
	private Map<String, Map<Object, CampaignDiagramDataDto>> diagramDataBySeriesAndXAxis = new HashMap<String, Map<Object, CampaignDiagramDataDto>>();
	private List<Object> axisKeys = new ArrayList<Object>();
	private Map<Object, String> axisCaptions = new HashMap<Object, String>();

	private final HighChart campaignColumnChart;

	public CampaignDashboardDiagramComponent(CampaignDiagramDefinitionDto diagramDefinition, List<CampaignDiagramDataDto> diagramDataList) {

		this.diagramDefinition = diagramDefinition;
		this.diagramDataList = diagramDataList;

		campaignColumnChart = new HighChart();
		campaignColumnChart.setWidth(800, Unit.PIXELS);
		campaignColumnChart.setHeight(400, Unit.PIXELS);

		addComponent(campaignColumnChart);
		setExpandRatio(campaignColumnChart, 1);

		for (CampaignDiagramDataDto diagramData : diagramDataList) {
			if (!axisKeys.contains(diagramData.getGroupingKey())) {
				axisKeys.add(diagramData.getGroupingKey());
				axisCaptions.put(diagramData.getGroupingKey(), diagramData.getGroupingCaption());
			}

			// TODO key probably needs to be combination of form and field id
			String seriesKey = diagramData.getFieldId();
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey)) {
				diagramDataBySeriesAndXAxis.put(seriesKey, new HashMap<Object, CampaignDiagramDataDto>());
			}
			// TODO throw exception when entry already exists
			diagramDataBySeriesAndXAxis.get(seriesKey).put(diagramData.getGroupingKey(), diagramData);
		}

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

		hcjs.append("xAxis: { categories: [");
		for (Object axisKey : axisKeys) {
			hcjs.append("'").append(axisCaptions.get(axisKey)).append("',");
		}
		hcjs.append("]},");

		//@formatter:off
		hcjs.append("yAxis: { min: 0, title: { text: ''}},");
		//@formatter:on

		// series
		hcjs.append("series: [");
		for (CampaignDiagramSeries series : diagramDefinition.getCampaignDiagramSeriesList()) {
			String seriesKey = series.getFieldId(); // TODO
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey))
				continue;
			// TODO use name of field
			hcjs.append("{ name:'" + series.getFieldId() + "', data: [");
			Map<Object, CampaignDiagramDataDto> seriesData = diagramDataBySeriesAndXAxis.get(seriesKey);
			for (Object axisKey : axisKeys) {
				if (seriesData.containsKey(axisKey)) {
					hcjs.append(seriesData.get(axisKey).getValueSum().toString()).append(",");
				} else {
					hcjs.append("0,");
				}
			}
			hcjs.append("]},");
		}
		hcjs.append("]");

		// TODO include stacking

		hcjs.append("}");

		campaignColumnChart.setHcjs(hcjs.toString());
	}
}

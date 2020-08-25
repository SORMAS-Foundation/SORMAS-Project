package de.symeda.sormas.ui.dashboard.campaigns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.utils.CssStyles;

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

		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setWidth(100, Unit.PERCENTAGE);
		headerLayout.setSpacing(true);
		CssStyles.style(headerLayout, CssStyles.VSPACE_4);

		Label headerLabel = new Label(diagramDefinition.getDiagramCaption());
		headerLabel.setSizeUndefined();
		CssStyles.style(headerLabel, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);

		headerLayout.addComponent(headerLabel);
		headerLayout.setComponentAlignment(headerLabel, Alignment.BOTTOM_LEFT);
		headerLayout.setExpandRatio(headerLabel, 1);

		addComponent(headerLayout);

		setWidth(100, Unit.PERCENTAGE);

		campaignColumnChart = new HighChart();

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
		if (diagramDefinition.getCampaignDiagramSeriesList()
			.stream()
			.filter(campaignDiagramSeries -> campaignDiagramSeries.getStack() != null)
			.findAny()
			.isPresent()) {
			hcjs.append("plotOptions: {\n" + "    column: {\n" + "      stacking: 'normal'\n" + "    }\n" + "  },");
		}
		hcjs.append("series: [");
		for (CampaignDiagramSeries series : diagramDefinition.getCampaignDiagramSeriesList()) {
			String seriesKey = series.getFieldId();
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey))
				continue;

			Map<Object, CampaignDiagramDataDto> seriesData = diagramDataBySeriesAndXAxis.get(seriesKey);
			Collection<CampaignDiagramDataDto> values = seriesData.values();
			Iterator<CampaignDiagramDataDto> iterator = values.iterator();
			final String fieldName = iterator.hasNext() ? iterator.next().getFieldCaption() : seriesKey;
			hcjs.append("{ name:'" + fieldName + "', data: [");
			for (Object axisKey : axisKeys) {
				if (seriesData.containsKey(axisKey)) {
					hcjs.append(seriesData.get(axisKey).getValueSum().toString()).append(",");
				} else {
					hcjs.append("0,");
				}
			}
			if (series.getStack() != null) {
				hcjs.append("],stack:'" + series.getStack() + "'},");
			} else {
				hcjs.append("]},");
			}
		}
		hcjs.append("]");
		hcjs.append("}");

		campaignColumnChart.setHcjs(hcjs.toString());
	}
}

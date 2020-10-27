package de.symeda.sormas.ui.dashboard.campaigns;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;

import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.highcharts.HighChart;

@SuppressWarnings("serial")
public class CampaignDashboardDiagramComponent extends VerticalLayout {

	private final CampaignDiagramDefinitionDto diagramDefinition;

	private final Map<String, Map<Object, CampaignDiagramDataDto>> diagramDataBySeriesAndXAxis = new HashMap<>();
	private final List<Object> axisKeys = new ArrayList<>();
	private final Map<Object, String> axisCaptions = new HashMap<>();
	private final Map<CampaignDashboardTotalsReference, Double> totalValuesMap;
	private boolean showPercentages;

	private final HighChart campaignColumnChart;

	public CampaignDashboardDiagramComponent(
		CampaignDiagramDefinitionDto diagramDefinition,
		List<CampaignDiagramDataDto> diagramDataList,
		Map<CampaignDashboardTotalsReference, Double> totalValuesMap,
		boolean showPercentages) {

		this.diagramDefinition = diagramDefinition;
		this.showPercentages = showPercentages;
		this.totalValuesMap = totalValuesMap;

		campaignColumnChart = new HighChart();

		setSizeFull();
		campaignColumnChart.setSizeFull();

		setMargin(false);
		addComponent(campaignColumnChart);

		for (CampaignDiagramDataDto diagramData : diagramDataList) {
			if (!axisKeys.contains(diagramData.getGroupingKey())) {
				axisKeys.add(diagramData.getGroupingKey());
				axisCaptions.put(diagramData.getGroupingKey(), diagramData.getGroupingCaption());
			}

			String seriesKey = diagramData.getFormId() + diagramData.getFieldId();
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey)) {
				diagramDataBySeriesAndXAxis.put(seriesKey, new HashMap<>());
			}
			Map<Object, CampaignDiagramDataDto> objectCampaignDiagramDataDtoMap = diagramDataBySeriesAndXAxis.get(seriesKey);
			if (objectCampaignDiagramDataDtoMap.containsKey(diagramData.getGroupingKey())) {
				throw new RuntimeException("Campaign diagram data map already contains grouping");
			}
			objectCampaignDiagramDataDtoMap.put(diagramData.getGroupingKey(), diagramData);
		}

		buildDiagramChart(diagramDefinition.getDiagramCaption());
	}

	public void buildDiagramChart(String title) {
		final StringBuilder hcjs = new StringBuilder();

		//@formatter:off
		hcjs.append("var options = {"
				+ "chart:{ "
				+ " type: 'column', "
				+ " backgroundColor: 'transparent', "
				+ " borderRadius: '1', "
				+ " borderWidth: '1', "
				+ " spacing: [20, 20, 20, 20], "
				+ "},"
				+ "credits:{ enabled: false },"
				+ "exporting:{ "
				+ " enabled: true,");
		//@formatter:on

		if (totalValuesMap != null) {
			hcjs.append(
				" menuItemDefinitions: { togglePercentages: { onclick: function() { window.changeDiagramState_" + diagramDefinition.getDiagramId()
					+ "(); }, text: '"
					+ (showPercentages
						? I18nProperties.getCaption(Captions.dashboardShowTotalValues)
						: I18nProperties.getCaption(Captions.dashboardShowPercentageValues))
					+ "' } }, ");
		}

		hcjs.append(" buttons:{ contextButton:{ theme:{ fill: 'transparent' }");

		if (totalValuesMap != null) {
			hcjs.append(
				", menuItems: ['viewFullscreen', 'printChart', 'separator', 'downloadPNG', 'downloadJPEG', 'downloadPDF', 'downloadSVG', 'separator', 'downloadCSV', 'downloadXLS', 'viewData', 'separator', 'togglePercentages']");
		}

		Map<String, Long> stackMap = diagramDefinition.getCampaignDiagramSeries()
			.stream()
			.filter(campaignDiagramSeries -> campaignDiagramSeries.getStack() != null)
			.map(CampaignDiagramSeries::getStack)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		//@formatter:off
		final int legendMargin = stackMap.size() > 1 ? 60 : 30;
		hcjs.append("} } }," 
				+ "legend: { backgroundColor: 'transparent', margin: " + legendMargin + " },"
				+ "colors: ['#4472C4', '#ED7D31', '#A5A5A5', '#FFC000', '#5B9BD5', '#70AD47', '#FF0000', '#6691C4','#ffba08','#519e8a','#ed254e','#39a0ed','#FF8C00','#344055','#D36135','#82d173'],"
				+ "title:{ text: '" + StringEscapeUtils.escapeEcmaScript(title) + "', style: { fontSize: '15px' } },");
		//@formatter:on

		hcjs.append("xAxis: {");
		if (stackMap.size() > 1) {
			hcjs.append("opposite: true,");
		}
		hcjs.append("categories: [");
		for (Object axisKey : axisKeys) {
			hcjs.append("'").append(StringEscapeUtils.escapeEcmaScript(axisCaptions.get(axisKey))).append("',");
		}
		hcjs.append("]},");

		//@formatter:off
		hcjs.append("yAxis: { min: 0, title: { text: ''}");
		if (showPercentages && totalValuesMap != null) {
			hcjs.append(", max: 100 ");
		}
		if (stackMap.size() > 1) {
			hcjs.append(
					", stackLabels: {enabled: true,verticalAlign: 'bottom', allowOverlap: true, crop: false, rotation: 45, x:20,y: 20, overflow: 'none',y: 24,formatter: function() {  return this.stack;},style: {  color: 'grey'}}");
		}
		hcjs.append("},");
		//@formatter:on

		// series

		if (stackMap.size() > 0 || (showPercentages && totalValuesMap != null)) {
			hcjs.append("plotOptions: {");

			if (stackMap.size() > 0) {
				hcjs.append("column: { stacking: 'normal', borderWidth: 0}");
			}
			if (showPercentages && totalValuesMap != null) {
				hcjs.append(stackMap.size() > 0 ? ", " : "")
					.append("series: { dataLabels: { enabled: true, format: '{y} %', style: { fontSize: 14 + 'px' }}}");
			}

			hcjs.append("},");
		}

		hcjs.append("series: [");
		for (CampaignDiagramSeries series : diagramDefinition.getCampaignDiagramSeries()) {
			String seriesKey = series.getFormId() + series.getFieldId();
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey))
				continue;

			Map<Object, CampaignDiagramDataDto> seriesData = diagramDataBySeriesAndXAxis.get(seriesKey);
			Collection<CampaignDiagramDataDto> values = seriesData.values();
			Iterator<CampaignDiagramDataDto> iterator = values.iterator();
			final String fieldName = iterator.hasNext() ? iterator.next().getFieldCaption() : seriesKey;
			hcjs.append("{ name:'").append(StringEscapeUtils.escapeEcmaScript(fieldName)).append("', data: [");
			for (Object axisKey : axisKeys) {
				if (seriesData.containsKey(axisKey)) {
					if (showPercentages && totalValuesMap != null) {
						double totalValue =
							totalValuesMap.get(new CampaignDashboardTotalsReference(seriesData.get(axisKey).getGroupingKey(), series.getStack()));
						if (totalValue > 0) {
							hcjs.append(
								BigDecimal.valueOf(seriesData.get(axisKey).getValueSum().doubleValue() / totalValue * 100)
									.setScale(2, RoundingMode.HALF_UP)
									.doubleValue())
								.append(",");
						} else {
							hcjs.append("0,");
						}
					} else {
						hcjs.append(seriesData.get(axisKey).getValueSum().toString()).append(",");
					}
				} else {
					hcjs.append("0,");
				}
			}
			if (series.getStack() != null) {
				hcjs.append("],stack:'").append(StringEscapeUtils.escapeEcmaScript(series.getStack())).append("'},");
			} else {
				hcjs.append("]},");
			}
		}
		hcjs.append("]");
		hcjs.append("}");

		campaignColumnChart.setHcjs(hcjs.toString());
	}

	public boolean isShowPercentages() {
		return showPercentages;
	}

	public void setShowPercentages(boolean showPercentages) {
		this.showPercentages = showPercentages;
	}
}

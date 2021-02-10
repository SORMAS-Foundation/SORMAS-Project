package de.symeda.sormas.ui.dashboard.campaigns;

import static com.vaadin.ui.Notification.Type.ERROR_MESSAGE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import de.symeda.sormas.api.campaign.diagram.DiagramType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.highcharts.HighChart;

public class CampaignDashboardDiagramComponent extends VerticalLayout {

	private static final double MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_LOWER_BOUND = 70.0;
	private static final double MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_UPPER_BOUND = 101.0;

	private final CampaignDiagramDefinitionDto diagramDefinition;

	private final Map<String, Map<Object, CampaignDiagramDataDto>> diagramDataBySeriesAndXAxis = new HashMap<>();
	private final Map<Object, String> xAxisInfo;
	private final Map<CampaignDashboardTotalsReference, Double> totalValuesMap;
	private boolean totalValuesWithoutStacks;
	private boolean showPercentages;
	private boolean showAsColumnChart;
	private boolean showDataLabels = false;
	private final HighChart campaignColumnChart;

	public CampaignDashboardDiagramComponent(
		CampaignDiagramDefinitionDto diagramDefinition,
		List<CampaignDiagramDataDto> diagramDataList,
		Map<CampaignDashboardTotalsReference, Double> totalValuesMap,
		boolean showPercentages,
		CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		this.diagramDefinition = diagramDefinition;
		this.showPercentages = showPercentages;
		this.totalValuesMap = totalValuesMap;

		if (this.totalValuesMap != null && this.totalValuesMap.keySet().stream().noneMatch(r -> r.getStack() != null)) {
			totalValuesWithoutStacks = true;
		}

		showAsColumnChart = DiagramType.values().length > 0 && DiagramType.COLUMN == DiagramType.values()[0];
		campaignColumnChart = new HighChart();

		setSizeFull();
		campaignColumnChart.setSizeFull();

		setMargin(false);
		addComponent(campaignColumnChart);

		final Map<Object, String> axisInfo = new HashMap<>();
		for (CampaignDiagramDataDto diagramData : diagramDataList) {
			final Object groupingKey = diagramData.getGroupingKey();
			if (!axisInfo.containsKey(groupingKey)) {
				axisInfo.put(groupingKey, diagramData.getGroupingCaption());
			}

			String seriesKey = diagramData.getFormId() + diagramData.getFieldId();
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey)) {
				diagramDataBySeriesAndXAxis.put(seriesKey, new HashMap<>());
			}
			Map<Object, CampaignDiagramDataDto> objectCampaignDiagramDataDtoMap = diagramDataBySeriesAndXAxis.get(seriesKey);
			if (objectCampaignDiagramDataDtoMap.containsKey(groupingKey)) {
				throw new RuntimeException("Campaign diagram data map already contains grouping");
			}
			objectCampaignDiagramDataDtoMap.put(groupingKey, diagramData);
		}

		xAxisInfo = axisInfo.entrySet()
			.stream()
			.sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getValue(), o2.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		// TODO would be cleaner to extend the HighChart class to provide customizable toggle options
		JavaScript.getCurrent()
				.addFunction("changeDiagramPercentage_" + diagramDefinition.getDiagramId(), (JavaScriptFunction) jsonArray -> {
					setShowPercentages(!isShowPercentages());
					buildDiagramChart(diagramDefinition.getDiagramCaption(), campaignJurisdictionLevelGroupBy);
				});

		JavaScript.getCurrent()
				.addFunction("changeDiagramLabels_" + diagramDefinition.getDiagramId(), (JavaScriptFunction) jsonArray -> {
					setShowDataLabels(!isShowDataLabels());
					buildDiagramChart(diagramDefinition.getDiagramCaption(), campaignJurisdictionLevelGroupBy);
				});

		JavaScript.getCurrent()
				.addFunction("changeDiagramChartType_" + diagramDefinition.getDiagramId(), (JavaScriptFunction) jsonArray -> {
					setShowAsColumnChart(!isShowAsColumnChart());
					buildDiagramChart(diagramDefinition.getDiagramCaption(), campaignJurisdictionLevelGroupBy);
				});

		buildDiagramChart(diagramDefinition.getDiagramCaption(), campaignJurisdictionLevelGroupBy);
	}

	public void buildDiagramChart(String title, CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		final StringBuilder hcjs = new StringBuilder();

		//@formatter:off
		hcjs.append("var options = {"
				+ "chart:{ "
				+ " type: '" + (showAsColumnChart ? "column" : "bar") + "', "
				+ " backgroundColor: 'white', "
				+ " borderRadius: '1', "
				+ " borderWidth: '1', "
				+ " spacing: [20, 20, 20, 20], "
				+ "},"
				+ "credits:{ enabled: false },"
				+ "exporting:{ "
				+ " enabled: true,");
		//@formatter:on

		hcjs.append(
				" menuItemDefinitions: { toggleLabels: { onclick: function() { window.changeDiagramLabels_" + diagramDefinition.getDiagramId()
						+ "(); }, text: '"
						+ (showDataLabels
						? I18nProperties.getCaption(Captions.dashboardHideDataLabels)
						: I18nProperties.getCaption(Captions.dashboardShowDataLabels))
						+ "' } ");
		if (totalValuesMap != null) {
			hcjs.append(
				", togglePercentages: { onclick: function() { window.changeDiagramPercentage_" + diagramDefinition.getDiagramId()
					+ "(); }, text: '"
					+ (showPercentages
						? I18nProperties.getCaption(Captions.dashboardShowTotalValues)
						: I18nProperties.getCaption(Captions.dashboardShowPercentageValues))
					+ "' } ");
		}

		hcjs.append(
				", toggleChartType: { onclick: function() { window.changeDiagramChartType_" + diagramDefinition.getDiagramId()
						+ "(); }, text: '"
						+ (showAsColumnChart
						   ? I18nProperties.getCaption(Captions.dashboardViewAsBarChart)
						   : I18nProperties.getCaption(Captions.dashboardViewAsColumnChart))
						+ "' } ");

		hcjs.append(" }, ");

		hcjs.append(" buttons:{ contextButton:{ theme:{ fill: 'transparent' }, ")
			.append(
				"menuItems: ['viewFullscreen', 'printChart', 'separator', 'downloadPNG', 'downloadJPEG', 'downloadPDF', 'downloadSVG', 'separator', 'downloadCSV', 'downloadXLS'");

		hcjs.append(", 'separator', 'toggleLabels'");
		if (totalValuesMap != null) {
			hcjs.append(", 'togglePercentages'");
		}

		hcjs.append(", 'toggleChartType'");
		hcjs.append("]");

		final Map<String, Long> stackMap = diagramDefinition.getCampaignDiagramSeries()
			.stream()
			.map(CampaignDiagramSeries::getStack)
			.filter(Objects::nonNull)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		//@formatter:off
		final int legendMargin = stackMap.size() > 1 ? 60 : 30;
		hcjs.append("} } }," 
				+ "legend: { backgroundColor: 'transparent', margin: " + legendMargin + " },"
				+ "colors: ['#4472C4', '#ED7D31', '#A5A5A5', '#FFC000', '#5B9BD5', '#70AD47', '#FF0000', '#6691C4','#ffba08','#519e8a','#ed254e','#39a0ed','#FF8C00','#344055','#D36135','#82d173'],"
				+ "title:{ text: '" + StringEscapeUtils.escapeEcmaScript(title) + "', style: { fontSize: '15px' } },");
		//@formatter:on

		appendAxisInformation(hcjs, stackMap, campaignJurisdictionLevelGroupBy);
		appendPlotOptions(hcjs, stackMap);
		appendSeries(campaignJurisdictionLevelGroupBy, hcjs);

		hcjs.append("}");
		campaignColumnChart.setHcjs(hcjs.toString());
	}

	private void appendAxisInformation(StringBuilder hcjs, Map<String, Long> stackMap, CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		final List<Object> noPopulationDataLocations = new LinkedList<>();
		if (Objects.nonNull(totalValuesMap)) {
			for (Object key : xAxisInfo.keySet()) {
				if ((Double.valueOf(0)).equals(totalValuesMap.get(new CampaignDashboardTotalsReference(key, null)))) {
					noPopulationDataLocations.add(xAxisInfo.get(key));
				}
			}
		}

		hcjs.append("xAxis: {");
		if (Objects.nonNull(diagramDefinition.getCampaignSeriesTotal())) {
			Optional<CampaignDiagramSeries> isPopulationGroupUsed =
				diagramDefinition.getCampaignSeriesTotal().stream().filter(series -> Objects.nonNull(series.getPopulationGroup())).findFirst();
			if (showPercentages && isPopulationGroupUsed.isPresent() && !CollectionUtils.isEmpty(noPopulationDataLocations)) {
				hcjs.append(
					"title: {" + "        text:'"
						+ String
							.format(I18nProperties.getString(Strings.errorNoPopulationDataLocations), String.join(", ", noPopulationDataLocations.toString()))
						+ "' },");
			} else {
				hcjs.append("title: {" + "text:'" + campaignJurisdictionLevelGroupBy.toString() + "' },");
			}
		} else {
			hcjs.append("title: {" + "text:'" + campaignJurisdictionLevelGroupBy.toString() + "' },");
		}
		if (stackMap.size() > 1) {
			hcjs.append("opposite: true,");
		}
		hcjs.append("categories: [");
		for (String caption : xAxisInfo.values()) {
			hcjs.append("'").append(StringEscapeUtils.escapeEcmaScript(caption)).append("',");
		}
		hcjs.append("]},");

		//@formatter:off
		final String restrictMaxValueProperty = scaledValueExceedsThreshold() ? "max: 100, " : "";
		hcjs.append("yAxis: {" + restrictMaxValueProperty + "min: 0, title: { text: '"+ (showPercentages
				? I18nProperties.getCaption(Captions.dashboardProportion)
				: I18nProperties.getCaption(Captions.dashboardAggregatedNumber)) +"'}");
		if (stackMap.size() > 1) {
			hcjs.append(
					", stackLabels: {enabled: true,verticalAlign: 'bottom', allowOverlap: true, crop: false, rotation: 45, x:20,y: 20, overflow: 'none',y: 24,formatter: function() {  return this.stack;},style: {  color: 'grey'}}");
		}
		hcjs.append("},");
		//@formatter:on
	}

	private void appendSeries(CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy, StringBuilder hcjs) {
		hcjs.append("series: [");
		for (CampaignDiagramSeries series : diagramDefinition.getCampaignDiagramSeries()) {
			String seriesKey = series.getFormId() + series.getFieldId();
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey))
				continue;

			Map<Object, CampaignDiagramDataDto> seriesData = diagramDataBySeriesAndXAxis.get(seriesKey);
			Collection<CampaignDiagramDataDto> values = seriesData.values();
			String fieldName = assembleFieldname(values, series, seriesKey);
			if (showPercentages) {
				if (campaignJurisdictionLevelGroupBy == CampaignJurisdictionLevel.COMMUNITY) {
					fieldName = I18nProperties.getString(Strings.populationDataByCommunity);
				}
			}

			hcjs.append("{ name:'").append(StringEscapeUtils.escapeEcmaScript(fieldName)).append("', data: [");
			appendData(campaignJurisdictionLevelGroupBy == CampaignJurisdictionLevel.COMMUNITY, hcjs, series, seriesData);
			final String stack = series.getStack();
			final String color = series.getColor();
			if (color != null || stack != null) {
				hcjs.append("],");
				if (stack != null) {
					hcjs.append("stack:'").append(StringEscapeUtils.escapeEcmaScript(stack)).append("'");
					hcjs.append(color != null ? "," : "");
				}
				if (color != null) {
					hcjs.append("color:'").append(StringEscapeUtils.escapeEcmaScript(color)).append("'");
				}
				hcjs.append("},");
			} else {
				hcjs.append("]},");
			}
		}
		hcjs.append("]");
	}

	private String assembleFieldname(final Collection<CampaignDiagramDataDto> values, final CampaignDiagramSeries series, final String defaultValue) {
		if (series.getCaption() != null && !series.getCaption().isEmpty()) {
			return series.getCaption();
		}
		Iterator<CampaignDiagramDataDto> iterator = values.iterator();
		return iterator.hasNext() ? iterator.next().getFieldCaption() : defaultValue;
	}

	private boolean scaledValueExceedsThreshold() {
		if (!showPercentages || totalValuesMap == null) {
			return false;
		}
		boolean result;
		for (CampaignDiagramSeries series : diagramDefinition.getCampaignDiagramSeries()) {
			String seriesKey = series.getFormId() + series.getFieldId();
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey))
				continue;
			Map<Object, CampaignDiagramDataDto> seriesData = diagramDataBySeriesAndXAxis.get(seriesKey);
			for (Object axisKey : xAxisInfo.keySet()) {
				if (seriesData.containsKey(axisKey)) {
					Double totalValue = totalValuesMap.get(new CampaignDashboardTotalsReference(
									seriesData.get(axisKey).getGroupingKey(),
									totalValuesWithoutStacks ? null : series.getStack())
					);
					if (totalValue != null && totalValue > 0) {
						final double originalValue = seriesData.get(axisKey).getValueSum().doubleValue() / totalValue * 100;
						final double scaledValue =
								BigDecimal.valueOf(originalValue).setScale(originalValue < 2 ? 1 : 0, RoundingMode.HALF_UP).doubleValue();
						result = scaledValue > MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_LOWER_BOUND && scaledValue < MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_UPPER_BOUND;
						if (result) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private void appendData(
		boolean isCommunityGrouping,
		StringBuilder hcjs,
		CampaignDiagramSeries series,
		Map<Object, CampaignDiagramDataDto> seriesData) {
		for (Object axisKey : xAxisInfo.keySet()) {
			if (seriesData.containsKey(axisKey)) {
				if (showPercentages && totalValuesMap != null) {
					Double totalValue = totalValuesMap.get(
						new CampaignDashboardTotalsReference(
							seriesData.get(axisKey).getGroupingKey(),
							totalValuesWithoutStacks ? null : series.getStack()));
					if (totalValue == null) {
						if (!isCommunityGrouping) {
							Notification.show(
								String.format(
									I18nProperties.getString(Strings.errorCampaignDiagramTotalsCalculationError),
									diagramDefinition.getDiagramCaption()),
								ERROR_MESSAGE);
						}
					} else if (totalValue > 0) {
						final double originalValue = seriesData.get(axisKey).getValueSum().doubleValue() / totalValue * 100;
						final double scaledValue =
							BigDecimal.valueOf(originalValue).setScale(originalValue < 2 ? 1 : 0, RoundingMode.HALF_UP).doubleValue();
						hcjs.append(scaledValue).append(",");
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
	}

	private void appendPlotOptions(StringBuilder hcjs, Map<String, Long> stackMap) {
		if (stackMap.size() > 0 || showDataLabels) {
			hcjs.append("plotOptions: {");

			if (stackMap.size() > 0) {
				hcjs.append("column: { stacking: 'normal', borderWidth: 0}");
			}
			if (showDataLabels) {
				hcjs.append(stackMap.size() > 0 ? ", " : "")
					.append("series: { dataLabels: { enabled: true, formatter:function() { if (this.y != 0) return this.y; }, style: { fontSize: 14 + 'px' }");
				if (showPercentages && totalValuesMap != null) {
					hcjs.append(", format: '{y}%'");
				}
				hcjs.append("}}");
			}

			hcjs.append("},");
		}

		if (showPercentages && totalValuesMap != null) {
			hcjs.append("tooltip:{ valueSuffix: ' %' }, ");
		}
	}

	public boolean isShowPercentages() {
		return showPercentages;
	}

	public void setShowPercentages(boolean showPercentages) {
		this.showPercentages = showPercentages;
	}

	public boolean isShowDataLabels() {
		return showDataLabels;
	}

	public void setShowDataLabels(boolean showDataLabels) {
		this.showDataLabels = showDataLabels;
	}

	public boolean isShowAsColumnChart() {
		return showAsColumnChart;
	}

	public void setShowAsColumnChart(boolean showAsColumnChart) {
		this.showAsColumnChart = showAsColumnChart;
	}
}

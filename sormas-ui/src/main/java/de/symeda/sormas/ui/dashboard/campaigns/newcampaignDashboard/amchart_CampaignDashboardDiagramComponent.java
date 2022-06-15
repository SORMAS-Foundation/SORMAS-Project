package de.symeda.sormas.ui.dashboard.campaigns.newcampaignDashboard;

import static com.vaadin.ui.Notification.Type.ERROR_MESSAGE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.campaign.CampaignJurisdictionLevel;
import de.symeda.sormas.api.campaign.data.translation.TranslationElement;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDataDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramDefinitionDto;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramSeries;
import de.symeda.sormas.api.campaign.diagram.CampaignDiagramTranslations;
import de.symeda.sormas.api.campaign.diagram.DiagramType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.dashboard.campaigns.CampaignDashboardTotalsReference;

public class amchart_CampaignDashboardDiagramComponent extends VerticalLayout{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7066363490675204403L;
	private static final double MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_LOWER_BOUND = 70.0;
	private static final double MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_UPPER_BOUND = 100.0;

	private final CampaignDiagramDefinitionDto diagramDefinition;

	private final Map<String, Map<Object, CampaignDiagramDataDto>> diagramDataBySeriesAndXAxis = new HashMap<>();
	private final Map<Object, String> xAxisInfo;
	private final Map<CampaignDashboardTotalsReference, Double> totalValuesMap;
	private boolean totalValuesWithoutStacks;
	private boolean showPercentages;
	private boolean showAsColumnChart;
	private boolean showAsPieChart;
	private boolean showAsBarChart;
	private boolean showAsColumnStackChart;
	private boolean showAsScatterChart;
	private boolean showAsLineChart;
	private boolean showAsHeatmapChart;
	
	private boolean showDataLabels = false;
	private boolean ignoreTotalsError = false;
	
	private String chart_id;
	
	private int ii = 0;

	//private final HighChart campaignColumnChart;

	public amchart_CampaignDashboardDiagramComponent(
		CampaignDiagramDefinitionDto diagramDefinition,
		List<CampaignDiagramDataDto> diagramDataList,
		Map<CampaignDashboardTotalsReference, Double> totalValuesMap,
		CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy,
		String chart_id) {
		this.diagramDefinition = diagramDefinition;
		this.showPercentages = diagramDefinition.isPercentageDefault();
		this.totalValuesMap = totalValuesMap;
		this.chart_id = chart_id;

		if (this.totalValuesMap != null && this.totalValuesMap.keySet().stream().noneMatch(r -> r.getStack() != null)) {
			totalValuesWithoutStacks = true;
		}

		showAsColumnChart = DiagramType.COLUMN == diagramDefinition.getDiagramType();
		//showAsPieChart = DiagramType.PIE == diagramDefinition.getDiagramType();
		showAsBarChart = DiagramType.BAR == diagramDefinition.getDiagramType();
		showAsColumnStackChart = DiagramType.STACK == diagramDefinition.getDiagramType();
		//showAsScatterChart = DiagramType.SCATTER == diagramDefinition.getDiagramType();
		showAsLineChart = DiagramType.LINE == diagramDefinition.getDiagramType();
		//showAsHeatmapChart = DiagramType.HEAT == diagramDefinition.getDiagramType();
		
		
		//campaignColumnChart = new HighChart();

		setSizeFull();
	//	campaignColumnChart.setSizeFull();

		setMargin(false);
	//	addComponent(campaignColumnChart);

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
	/*	JavaScript.getCurrent().addFunction("changeDiagramPercentage_" + diagramDefinition.getDiagramId(), (JavaScriptFunction) jsonArray -> {
			setShowPercentages(!isShowPercentages());
			buildDiagramChart(getDiagramCaption(), campaignJurisdictionLevelGroupBy);
		});

		JavaScript.getCurrent().addFunction("changeDiagramLabels_" + diagramDefinition.getDiagramId(), (JavaScriptFunction) jsonArray -> {
			setShowDataLabels(!isShowDataLabels());
			buildDiagramChart(getDiagramCaption(), campaignJurisdictionLevelGroupBy);
		});

		JavaScript.getCurrent().addFunction("changeDiagramChartType_" + diagramDefinition.getDiagramId(), (JavaScriptFunction) jsonArray -> {
			setShowAsColumnChart(!isShowAsColumnChart());
			buildDiagramChart(getDiagramCaption(), campaignJurisdictionLevelGroupBy);
		});
*/
		JavaScript.getCurrent().execute("$('#"+chart_id+" .v-expand').remove()");
		JavaScript.getCurrent().execute(buildDiagramChartx(getDiagramCaption(), campaignJurisdictionLevelGroupBy));
		
}

	
	public String buildDiagramChartx(String title, CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		final StringBuilder hcjs = new StringBuilder();
		
		String allstr = "var root = am5.Root.new(\""+chart_id+"\");\n"
				+ "root.setThemes([\n"
				+ "  am5themes_Animated.new(root)\n"
				+ "]);\n"
				+ "var chart = root.container.children.push(am5xy.XYChart.new(root, {\n"
				+ "  layout: root.verticalLayout\n"
				+ "}));\n"
				/*+ "var data = [{\n"
				+ "  \"year\": \"2021\",\n"
				+ "  \"europe\": 2.5,\n"
				+ "  \"namerica\": 2.5,\n"
				+ "  \"asia\": 2.1,\n"
				+ "  \"lamerica\": 1,\n"
				+ "  \"meast\": 0.8,\n"
				+ "  \"africa\": 0.4\n"
				+ "}, {\n"
				+ "  \"year\": \"2022\",\n"
				+ "  \"europe\": 2.6,\n"
				+ "  \"namerica\": 2.7,\n"
				+ "  \"asia\": 2.2,\n"
				+ "  \"lamerica\": 0.5,\n"
				+ "  \"meast\": 0.4,\n"
				+ "  \"africa\": 0.3\n"
				+ "}, {\n"
				+ "  \"year\": \"2023\",\n"
				+ "  \"europe\": 2.8,\n"
				+ "  \"namerica\": 2.9,\n"
				+ "  \"asia\": 2.4,\n"
				+ "  \"lamerica\": 0.3,\n"
				+ "  \"meast\": 0.9,\n"
				+ "  \"africa\": 0.5\n"
				+ "}]\n"
			*/
				
//+" var data = [{\"Number of Households from Door Marking\" : 1111122},{\"Houses being covered by the team\" : 11222},{\"Children living in houses\" : 1233},{\"Missed children due to absent, Newborn/Sick/Sleep and refusal recorded on team tally sheet\" : 3454}] \n"
				+appendSeries(campaignJurisdictionLevelGroupBy)+"\n"
				
				+ "var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {\n"
				+ "  categoryField: \"year\",\n"
				+ "  renderer: am5xy.AxisRendererX.new(root, {}),\n"
				+ "  tooltip: am5.Tooltip.new(root, {})\n"
				+ "}));\n"
				+ "\n"
				+ "xAxis.data.setAll(data);\n"
				+ "\n"
				+ "var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {\n"
				+ "  min: 0,\n"
				+ "  max: 100,\n"
				+ "  numberFormat: \"#'%'\",\n"
				+ "  strictMinMax: true,\n"
				+ "  calculateTotals: true,\n"
				+ "  renderer: am5xy.AxisRendererY.new(root, {})\n"
				+ "}));\n"
				+ "var legend = chart.children.push(am5.Legend.new(root, {\n"
				+ "  centerX: am5.p50,\n"
				+ "  x: am5.p50\n"
				+ "}));\n"
				+ "function makeSeries(name, fieldName) {\n"
				+ "  var series = chart.series.push(am5xy.ColumnSeries.new(root, {\n"
				+ "    name: name,\n"
				+ "    stacked: true,\n"
				+ "    xAxis: xAxis,\n"
				+ "    yAxis: yAxis,\n"
				+ "    valueYField: fieldName,\n"
				+ "    valueYShow: \"valueYTotalPercent\",\n"
				+ "    categoryXField: \"year\"\n"
				+ "  }));\n"
				+ "\n"
				+ "  series.data.setAll(data);\n"
				+ "  series.appear();\n"
				+ "  series.bullets.push(function () {\n"
				+ "    return am5.Bullet.new(root, {\n"
				+ "      sprite: am5.Label.new(root, {\n"
				+ "        text: \"{valueYTotalPercent.formatNumber('#.#')}%\",\n"
				+ "        fill: root.interfaceColors.get(\"alternativeText\"),\n"
				+ "        centerY: am5.p50,\n"
				+ "        centerX: am5.p50,\n"
				+ "        populateText: true\n"
				+ "      })\n"
				+ "    });\n"
				+ "  });\n"
				+ "\n"
				+ "  legend.data.push(series);\n"
				+ "}\n"
				+ "\n"
				+ "makeSeries(\"Number of Households from Door Marking\", \"Number of Households from Door Marking\");\n"
				+ "makeSeries(\"Houses being covered by the team\", \"Houses being covered by the team\");\n"
				+ "makeSeries(\"Children living in houses\", \"Children living in houses\");\n"
				+ "makeSeries(\"Missed children due to absent, Newborn/Sick/Sleep and refusal recorded on team tally sheet\", \"Missed children due to absent, Newborn/Sick/Sleep and refusal recorded on team tally sheet\");\n"
				
				+ "chart.appear(1000, 100);";
		
	System.out.println("---------------------------------------------------------------------------------"+allstr);
		hcjs.append(allstr);
	
	return hcjs.toString();
	
	}
	
	
	private String appendSeries(CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		ii = 0;
		//check why this is been called over and over again...
		final StringBuilder hcjse = new StringBuilder();
		
		final StringBuilder hcjses = new StringBuilder();
		hcjses.append("");
		
		StringBuilder hcjsx = new StringBuilder();
		
		hcjse.append("var data = [{");
		//System.out.println("----------------------------"+ ii++ +"---------------------------------------");
		
		for (CampaignDiagramSeries series : diagramDefinition.getCampaignDiagramSeries()) {
			String seriesKey = series.getFormId() + series.getFieldId();
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey))
				continue;
			System.out.println("----------------------------"+ ii++ +"---------------------------------------");
			
			Map<Object, CampaignDiagramDataDto> seriesData = diagramDataBySeriesAndXAxis.get(seriesKey);
			Collection<CampaignDiagramDataDto> values = seriesData.values();
			String fieldName = assembleFieldname(values, series, seriesKey);
			if (showPercentages) {
				if (campaignJurisdictionLevelGroupBy == CampaignJurisdictionLevel.COMMUNITY) {
					fieldName = I18nProperties.getString(Strings.populationDataByCommunity);
				}
			}
			
			//System.out.println(fieldName+"----------------------------"+ seriesData +"---------------------------------------"+appendData(campaignJurisdictionLevelGroupBy == CampaignJurisdictionLevel.COMMUNITY, series, seriesData));
			hcjsx.append(appendData(campaignJurisdictionLevelGroupBy == CampaignJurisdictionLevel.COMMUNITY, series, seriesData));


			/*
			final String stack = series.getStack();
			final String color = series.getColor();
			if (color != null || stack != null) {
				hcjs.append("],");
				if (stack != null) {
					hcjs.append("stack:'").append(StringEscapeUtils.escapeEcmaScript(getStackCaption(stack))).append("'");
					hcjs.append(color != null ? "," : "");
				}
				if (color != null) {
					hcjs.append("color:'").append(StringEscapeUtils.escapeEcmaScript(color)).append("'");
				}
				hcjs.append("},");
			} else {
				hcjs.append("]},");
			}*/
			System.out.println("++++______________main_______________+++++ "+hcjsx);
			hcjse.append(hcjsx);
		}
		
		String allcode = "var data = ["+hcjsx+"]";
		
		System.out.println(allcode);
		return allcode;
	}
	
	
	
	private String getStackCaption(String stackName) {
		CampaignDiagramTranslations translations = getCampaignDiagramTranslations();
		if (translations != null && translations.getStackCaptions() != null) {
			TranslationElement stackCaption =
				translations.getStackCaptions().stream().filter(s -> s.getElementId().equalsIgnoreCase(stackName)).findFirst().orElse(null);
			if (stackCaption != null) {
				return stackCaption.getCaption();
			}
		}
		return stackName;
	}
	private String appendData(
			boolean isCommunityGrouping,
			
			CampaignDiagramSeries series,
			Map<Object, CampaignDiagramDataDto> seriesData) {
		String hcjsaw = "";
		if(ii > 1) {
		hcjsaw = ", {";
		}else {
			hcjsaw = "{";
		}
			for (Object axisKey : xAxisInfo.keySet()) {
				if (seriesData.containsKey(axisKey)) {
					
					
					System.out.println("++++ "+seriesData.get(axisKey).getFieldCaption());
					System.out.println(" :"+seriesData.get(axisKey).getValueSum()+" ++++");
					/*if (showPercentages && totalValuesMap != null) {
						Double totalValue = totalValuesMap.get(
							new CampaignDashboardTotalsReference(
								seriesData.get(axisKey).getGroupingKey(),
								totalValuesWithoutStacks ? null : series.getStack()));
						if (totalValue == null) {
							if (!isCommunityGrouping && !ignoreTotalsError) {
								Notification.show(
									String.format(I18nProperties.getString(Strings.errorCampaignDiagramTotalsCalculationError), getDiagramCaption()),
									ERROR_MESSAGE);
								ignoreTotalsError = true; // only show once
							}
						} else if (totalValue > 0) {
							final double originalValue = seriesData.get(axisKey).getValueSum().doubleValue() / totalValue * 100;
							final double scaledValue =
								BigDecimal.valueOf(originalValue).setScale(originalValue < 2 ? 1 : 0, RoundingMode.HALF_UP).doubleValue();
							hcjsaw.append(scaledValue).append(",");
						} else {
							hcjsaw.append("0,");
						}
					} else*/ 
						hcjsaw = hcjsaw + "\""+seriesData.get(axisKey).getFieldCaption()+"\" : "+seriesData.get(axisKey).getValueSum().toString()+"}";
					
				} else {
					//hcjsaw.append(seriesData.get(axisKey).getFieldCaption()).append(" : "+seriesData.get(axisKey).getValueSum().toString()).append("}");
				}
			}
			System.out.println("++++_____________________________+++++ "+hcjsaw);
			return hcjsaw;
		}
	private String assembleFieldname(final Collection<CampaignDiagramDataDto> values, final CampaignDiagramSeries series, final String defaultValue) {
		CampaignDiagramTranslations translations = getCampaignDiagramTranslations();
		if (translations != null && translations.getSeriesNames() != null) {
			TranslationElement seriesName =
				translations.getSeriesNames().stream().filter(s -> s.getElementId().equalsIgnoreCase(defaultValue)).findFirst().orElse(null);
			if (seriesName != null) {
				return seriesName.getCaption();
			}
		}
		if (series.getCaption() != null && !series.getCaption().isEmpty()) {
			return series.getCaption();
		}
		Iterator<CampaignDiagramDataDto> iterator = values.iterator();
		return iterator.hasNext() ? iterator.next().getFieldCaption() : defaultValue;
	}
	
	
	public String getDiagramCaption() {
		String diagramCaption = diagramDefinition.getDiagramCaption();
		CampaignDiagramTranslations translations = getCampaignDiagramTranslations();
		if (translations != null) {
			diagramCaption = translations.getDiagramCaption();
		}
		return diagramCaption;
	}
	
	private CampaignDiagramTranslations getCampaignDiagramTranslations() {
		Language userLanguage = UserProvider.getCurrent().getUser().getLanguage();
		CampaignDiagramTranslations translations = null;
		if (userLanguage != null && diagramDefinition.getCampaignDiagramTranslations() != null) {
			translations = diagramDefinition.getCampaignDiagramTranslations()
				.stream()
				.filter(t -> t.getLanguageCode().equals(userLanguage.getLocale().toString()))
				.findFirst()
				.orElse(null);
		}
		return translations;
	}

















































}

package de.symeda.sormas.ui.dashboard.campaigns;

import static com.vaadin.ui.Notification.Type.ERROR_MESSAGE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.highcharts.HighChart;

public class CampaignDashboardDiagramComponent extends VerticalLayout {

	private static final double MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_LOWER_BOUND = 70.0;
	private static final double MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_UPPER_BOUND = 100.0;

	private final CampaignDiagramDefinitionDto diagramDefinition;

	private final Map<String, Map<Object, CampaignDiagramDataDto>> diagramDataBySeriesAndXAxis = new HashMap<>();
	private final Map<Object, String> xAxisInfo;
	private final Map<CampaignDashboardTotalsReference, Double> totalValuesMap;
	private boolean totalValuesWithoutStacks;
	private boolean showPercentages;
	private boolean showAsColumnChart;
	private boolean showDataLabels = false;
	private boolean rotateDataLabels = true;
	private boolean ignoreTotalsError = false;
	private boolean pieChart = false;
	private boolean cardChart = false;
	private String chartType;
	private String secondaryChartType;
	private  Map mapSeries = new HashMap();  

	private final HighChart campaignColumnChart;

	public CampaignDashboardDiagramComponent(
		CampaignDiagramDefinitionDto diagramDefinition,
		List<CampaignDiagramDataDto> diagramDataList,
		Map<CampaignDashboardTotalsReference, Double> totalValuesMap,
		CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		this.diagramDefinition = diagramDefinition;
		this.showPercentages = diagramDefinition.isPercentageDefault();
		this.totalValuesMap = totalValuesMap;
		
		this.secondaryChartType = diagramDefinition.getDiagramType().name().toLowerCase();
		
		this.chartType = secondaryChartType.equalsIgnoreCase(DiagramType.DOUGHNUT.toString()) ? "pie" : secondaryChartType;
		pieChart = (chartType.equalsIgnoreCase(DiagramType.PIE.toString())) ? true : false;
		
		if (this.totalValuesMap != null && this.totalValuesMap.keySet().stream().noneMatch(r -> r.getStack() != null)) {
			totalValuesWithoutStacks = true;
		}

		showAsColumnChart = DiagramType.COLUMN == diagramDefinition.getDiagramType();
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
			if(!pieChart) {
				if (objectCampaignDiagramDataDtoMap.containsKey(groupingKey)) {
					throw new RuntimeException("Campaign diagram data map already contains grouping");
				}
				}
			objectCampaignDiagramDataDtoMap.put(groupingKey, diagramData);
		}

		xAxisInfo = axisInfo.entrySet()
			.stream()
			.sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getValue(), o2.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		// TODO would be cleaner to extend the HighChart class to provide customizable toggle options
		JavaScript.getCurrent().addFunction("changeDiagramPercentage_" + diagramDefinition.getDiagramId(), (JavaScriptFunction) jsonArray -> {
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
	//	JavaScript.getCurrent().execute("https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js");
	
		JavaScript.getCurrent().execute(" var el = document.getElementsByClassName('highcharts-data-table');"
				
				+ "  window.onclick = function(event) {\n"
				
			
				+ " var divsToHide = document.getElementsByClassName(\"highcharts-data-table\"); //divsToHide is an array\n"
				+ "    for(var i = 0; i < divsToHide.length; i++){\n"
				
				+ "        divsToHide[i].style.display = \"none\"; \n"
				+ "    };"
				
				+ "}");
		
		
		
		
		JavaScript.getCurrent().execute(
				"Highcharts.seriesTypes.wordcloud.prototype.deriveFontSize = function(\n"
				+ "  relativeWeight\n"
				+ ") {\n"
				+ "  var maxFontSize = 10;\n"
				+ "  // Will return a fontSize between 0px and 25px.\n"
				+ "  return Math.floor(maxFontSize * 1);\n"
				+ "};");

		buildDiagramChart(getDiagramCaption(), campaignJurisdictionLevelGroupBy);
	}

	public void buildDiagramChart(String title, CampaignJurisdictionLevel campaignJurisdictionLevelGroupBy) {
		final StringBuilder hcjs = new StringBuilder();
		if(chartType.equalsIgnoreCase(DiagramType.CARD.toString())) {
			
			cardChart = true;
			
			appendSeries(campaignJurisdictionLevelGroupBy, hcjs);
			
			return;
		} else {
		//@formatter:off
		hcjs.append(""
				+ "var stuffx = ['column', 'bar', 'line', 'area', 'scatter'],\n"
				+ "        counterx = 0;\n"
				+ "var options = {"
				+ "chart:{ "
				+ " type: '"+chartType+"', "
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
			" menuItemDefinitions: {  switchChart: {\n"
			+ "                onclick: function() {\n"
			+ " 			counterx = (counterx + 1) % stuffx.length;\n"
			+ "                    var chartType = this.options.chart.type;\n"
			+ "\n"
			
			+ "                    this.update({\n"
			+ "                        chart: {\n"
			+ "                            type: stuffx[counterx]\n"
			+ "                        }\n"
			+ "                    })\n"
			+ "                },\n"
			+ "                text: 'Switch Charts'\n"
			+ "            }\n");
		
		
		
		
		hcjs.append(
				",  toggleLabels: { onclick: function() { window.changeDiagramLabels_" + diagramDefinition.getDiagramId()
					+ "(); }, text: '"
					+ (showDataLabels
						? I18nProperties.getCaption(Captions.dashboardHideDataLabels)
						: I18nProperties.getCaption(Captions.dashboardShowDataLabels))
					+ "' } ");
			if (totalValuesMap != null) {
				hcjs.append(
					", togglePercentages: { onclick: function() { window.changeDiagramPercentage_" + diagramDefinition.getDiagramId() + "(); }, text: '"
						+ (showPercentages
							? I18nProperties.getCaption(Captions.dashboardShowTotalValues)
							: I18nProperties.getCaption(Captions.dashboardShowPercentageValues))
						+ "' } ");
			}
			
			hcjs.append(" }, ");
			
			hcjs.append(" buttons:{ contextButton:{ theme:{ fill: 'transparent' }, ")
				.append(
					"menuItems: ['switchChart','viewFullscreen', 'printChart', 'separator', 'downloadPNG', 'downloadJPEG', 'downloadPDF', 'downloadSVG', 'separator', 'downloadCSV', 'downloadXLS', 'View data table'");

		hcjs.append(", 'separator', 'toggleLabels'");
		if (totalValuesMap != null) {
			hcjs.append(", 'togglePercentages'");
		}

		//hcjs.append(", 'toggleChartType'");
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
if(pieChart) {
		hcjs.append(" exporting: {\n"
				+ "        showTable: false,"
				+ "togglePercentages: false\n"
				+ "    }, \nxAxis: {");
}else {
	hcjs.append("xAxis: {");
	
}
		
		
		if (Objects.nonNull(diagramDefinition.getCampaignSeriesTotal())) {
			Optional<CampaignDiagramSeries> isPopulationGroupUsed =
				diagramDefinition.getCampaignSeriesTotal().stream().filter(series -> Objects.nonNull(series.getPopulationGroup())).findFirst();
			if (showPercentages && isPopulationGroupUsed.isPresent() && !CollectionUtils.isEmpty(noPopulationDataLocations)) {
				hcjs.append(
					"title: {" + "        text:'"
						+ String.format(
							I18nProperties.getString(Strings.errorNoPopulationDataLocations),
							String.join(", ", noPopulationDataLocations.toString()))
						+ "' },");
			} else {
				hcjs.append("title: {" + "text:'" + campaignJurisdictionLevelGroupBy.toString() + "' },");
			}
		} else {
			hcjs.append("title: {" + "text:'"+ campaignJurisdictionLevelGroupBy.toString() + "' },");
		}
		if (stackMap.size() > 1) {
			hcjs.append("opposite: true,");
		}
		hcjs.append("categories: [");
		int op = 0;
		for (String caption : xAxisInfo.values()) {
			
			hcjs.append("'").append(StringEscapeUtils.escapeEcmaScript(caption)).append("',");
			
			mapSeries.put(op++, caption);
			
			}
		hcjs.append("]},");
//highcharts-data-table
		
		
		//@formatter:off
		final String restrictMaxValueProperty = totalsNeedClampTo100() ? "max: 100, " : "";
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
		String innerS = secondaryChartType.equalsIgnoreCase(DiagramType.DOUGHNUT.toString()) ? " innerSize: '50%'," : "";
		
		if(pieChart) {
			hcjs.append("series: [{ "+innerS+" data: [");
		}else if(cardChart) {
		
		}else{
			hcjs.append("series: [");
		}
		for (CampaignDiagramSeries series : diagramDefinition.getCampaignDiagramSeries()) {
			String seriesKey = series.getFormId() + series.getFieldId();
			
		//	System.out.println("card? "+cardChart+ "check 1"+diagramDataBySeriesAndXAxis.containsKey(seriesKey));
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey) && !cardChart) {
			//	System.out.println("passed card checker");

				continue; 
			}
			

			Map<Object, CampaignDiagramDataDto> seriesData = diagramDataBySeriesAndXAxis.get(seriesKey);
			if(seriesData == null && cardChart) {
				
			//	System.out.println("passed card check 1.1.0");
				String fieldNamex = assembleFieldnameCardwithoutValue(series, seriesKey);
				//assembleFieldnameCardwithoutValue
				appendCardData(hcjs, series, seriesData, fieldNamex);
				continue;
			}
				  
			
			
			Collection<CampaignDiagramDataDto> values = seriesData.values();
			String fieldName = assembleFieldname(values, series, seriesKey);
			if (showPercentages) {
				if (campaignJurisdictionLevelGroupBy == CampaignJurisdictionLevel.COMMUNITY) {
					fieldName = I18nProperties.getString(Strings.populationDataByCommunity);
				}
			}
			
			if(chartType.equalsIgnoreCase(DiagramType.PIE.toString())) {
				//hcjs.append("data: [{");
				appendPieData(campaignJurisdictionLevelGroupBy == CampaignJurisdictionLevel.COMMUNITY, hcjs, series, seriesData, fieldName);
				//hcjs.append("]},");
			}else if(cardChart) {
				appendCardData(hcjs, series, seriesData, fieldName);
				 
				
			}else{

			hcjs.append("{ name:'").append(StringEscapeUtils.escapeEcmaScript(fieldName)).append("', data: [");
			appendData(campaignJurisdictionLevelGroupBy == CampaignJurisdictionLevel.COMMUNITY, hcjs, series, seriesData);
			
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
			}
		}
		}
		
		if(chartType.equalsIgnoreCase(DiagramType.PIE.toString())) {
			hcjs.append("]},");
		}
		hcjs.append("]");
	//	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ "+hcjs);
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
	
	private String assembleFieldnameCardwithoutValue(final CampaignDiagramSeries series, final String defaultValue) {
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
	//	Iterator<CampaignDiagramDataDto> iterator = values.iterator();
		return "No Caption set";
	}

	private boolean totalsNeedClampTo100() {
		if (!showPercentages || totalValuesMap == null) {
			return false;
		}
		boolean result = false;
		for (CampaignDiagramSeries series : diagramDefinition.getCampaignDiagramSeries()) {
			String seriesKey = series.getFormId() + series.getFieldId();
			if (!diagramDataBySeriesAndXAxis.containsKey(seriesKey))
				continue;
			Map<Object, CampaignDiagramDataDto> seriesData = diagramDataBySeriesAndXAxis.get(seriesKey);
			for (Object axisKey : xAxisInfo.keySet()) {
				if (seriesData.containsKey(axisKey)) {
					Double totalValue = totalValuesMap.get(
						new CampaignDashboardTotalsReference(
							seriesData.get(axisKey).getGroupingKey(),
							totalValuesWithoutStacks ? null : series.getStack()));
					if (totalValue != null && totalValue > 0) {
						final double originalValue = seriesData.get(axisKey).getValueSum().doubleValue() / totalValue * 100;
						final double scaledValue =
							BigDecimal.valueOf(originalValue).setScale(originalValue < 2 ? 1 : 0, RoundingMode.HALF_UP).doubleValue();
						if (scaledValue > MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_UPPER_BOUND) {
							return false;
						}
						result |= scaledValue > MAX_YAXIS_VALUE_DYNAMIC_CHART_HEIGHT_LOWER_BOUND;
					}
				}
			}
		}
		return result;
	}

	private void appendData( 
		boolean isCommunityGrouping,
		StringBuilder hcjs,
		CampaignDiagramSeries series,
		Map<Object, CampaignDiagramDataDto> seriesData) {
		for (Object axisKey : xAxisInfo.keySet()) {
		//	System.out.println("+++++++++++++++++++++++++++++++++++++ "+axisKey);
			if (seriesData.containsKey(axisKey)) {
				if (showPercentages && totalValuesMap != null) {
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
						hcjs.append(scaledValue).append(",");
					} else {
						hcjs.append("0,");
					}
				} else {
					hcjs.append(seriesData.get(axisKey).getValueSum().doubleValue()).append(",");
				}
			} else {
				hcjs.append("0,");
			}
		}
	//	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ "+hcjs);
	}
	
	private void appendPieData(
			boolean isCommunityGrouping,
			StringBuilder hcjs,
			CampaignDiagramSeries series,
			Map<Object, CampaignDiagramDataDto> seriesData,
			String fieldName) {
		
		//System.out.println(isCommunityGrouping+""+hcjs+""+
		
		int iii = 0;
			for (Object axisKey : xAxisInfo.keySet()) { 
				
				//System.out.println(mapSeries.get(iii++) +" ==== "+iii+" +++++++++++++++++++++++++++++++++++++ "+axisKey);
				if (seriesData.containsKey(axisKey)) {
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
							hcjs.append(scaledValue).append(",");
						} else {
							hcjs.append("0,");
						}
					}*/// else {
					
						hcjs.append("{name: '"+seriesData.get(axisKey).getStack() +"', y: "+seriesData.get(axisKey).getValueSum().doubleValue()).append("},");
				//	}
				} else {
					hcjs.append("0,");  
				}
			}
			mapSeries.clear();
		}
	
	
	
	
	private void appendCardData(
			StringBuilder hcjs,
			CampaignDiagramSeries series,
			Map<Object, CampaignDiagramDataDto> seriesData,
			String fieldName) {
		int qq = 0;
		
		String perce = "";
		if(showPercentages) {
			perce = "%";
			
		}
		
	if(xAxisInfo.size() > 0) {
			for (Object axisKey : xAxisInfo.keySet()) {
				if(qq > 0) {
					break;
				}
				
				if (seriesData.containsKey(axisKey)) {
					
				//	System.out.println(totalValuesMap.size()+ " ______________ "+seriesData.size());
					
					
					if (showPercentages && totalValuesMap != null) {
						
						for (Object axisKeddy : totalValuesMap.keySet()) {
							System.out.println(totalValuesMap.get(axisKey));
						}
						
						
					//	System.out.println(seriesData.get(axisKey).getValueSum() +"_____check point 1_________"+totalValuesWithoutStacks+" 0000 "+ series.getStack());
						System.out.println(seriesData.get(axisKey).getFieldCaption());
						System.out.println(seriesData.get(axisKey).getFormId());
						Double totalValue = totalValuesMap.get(
							new CampaignDashboardTotalsReference(
								seriesData.get(axisKey).getGroupingKey(),
								totalValuesWithoutStacks ? null : series.getStack()));
						
					//	System.out.println(seriesData.get(axisKey).getValueSum().toString()+" ======== "+series.getStack()+" _____check point 2_________"+totalValue);
						if (totalValue == null) {
							if (!ignoreTotalsError) {
								Notification.show(
									String.format(I18nProperties.getString(Strings.errorCampaignDiagramTotalsCalculationError), getDiagramCaption()),
									ERROR_MESSAGE);
								ignoreTotalsError = true; // only show once
							}
						} else if (totalValue > 0) {
							
							final double originalValue = seriesData.get(axisKey).getValueSum().doubleValue() / totalValue * 100;
							final double scaledValue = BigDecimal.valueOf(originalValue).setScale(originalValue < 2 ? 1 : 0, RoundingMode.HALF_UP).doubleValue();
						
							//@formatter:off
							hcjs.append("var options = { \n"
									+ " chart: {\n"
									+ "        backgroundColor: 'white',\n"
									+ "        borderRadius: '1',\n"
									+ "        borderWidth: '1',\n"
									+ "        spacing: [20, 20, 20, 20],\n"
									+ "    },\n"
									+ "    credits: {\n"
									+ "        enabled: false\n"
									+ "    },\n"
									+ " exporting: { enabled: false },"
									+ " series: [{\n"
									+ "        type: 'wordcloud',\n"
									+ "        data: data = ['"+scaledValue+""+perce+"'].reduce((arr, word) => {\n"
									+ "        let obj = Highcharts.find(arr, obj => obj.name === word);\n"
									+ "        if (obj) {\n"
									+ "            obj.weight += 1;\n"
									+ "        } else {\n"
									+ "            obj = {\n"
									+ "                name: word,\n"
									+ "                weight: 1\n"
									+ "            };\n"
									+ "            arr.push(obj);\n"
									+ "        }\n"
									+ "        return arr;\n"
									+ "    }, []),\n"
									+ "        name: '"+series.getStack()+"'\n"
									+ "    }],\n"
									+ "    title: {\n"
									+ "        text: '"+series.getStack()+"',\n"
									+ "    style: {\n"
									+ "                color: '#0C090A',\n"
									+ "                fontWeight: 'normal',\n"
									+ "                 fontSize: '12px' \n"
									+ "            } }");
							//@formatter:on
							
							
							
						} else {
							hcjs.append("var options = { \n"
									+ " chart: {\n"
									+ "        backgroundColor: 'white',\n"
									+ "        borderRadius: '1',\n"
									+ "        borderWidth: '1',\n"
									+ "        spacing: [20, 20, 20, 20],\n"
									+ "    },\n"
									+ "    credits: {\n"
									+ "        enabled: false\n"
									+ "    },\n"
									+ " exporting: { enabled: false },"
									+ " series: [{\n"
									+ "        type: 'wordcloud',\n"
									+ "        data: data = ['0"+perce+"', ' '].reduce((arr, word) => {\n"
									+ "        let obj = Highcharts.find(arr, obj => obj.name === word);\n"
									+ "        if (obj) {\n"
									+ "            obj.weight += 1;\n"
									+ "        } else {\n"
									+ "            obj = {\n"
									+ "                name: word,\n"
									+ "                weight: 1\n"
									+ "            };\n"
									+ "            arr.push(obj);\n"
									+ "        }\n"
									+ "        return arr;\n"
									+ "    }, []),\n"
									+ "        name: '"+series.getStack()+"'\n"
									+ "    }],\n"
									+ "    title: {\n"
									+ "        text: '"+series.getStack()+"',\n"
									+ "    style: {\n"
									+ "                color: '#0C090A',\n"
									+ "                fontWeight: 'normal',\n"
									+ "                 fontSize: '12px' \n"
									+ "            } }");
							//@formatter:on
						}
						} else {
					
			
					String temNum = seriesData.get(axisKey).getValueSum().toString();
					double amount = Double.parseDouble(temNum);
					
					final double scaledValue =
							BigDecimal.valueOf(amount).setScale(amount < 2 ? 1 : 0, RoundingMode.HALF_UP).doubleValue();
					
				//	System.out.println("+__________CARD TOTAL = +: "+scaledValue);
					
					
					DecimalFormat formatter = new DecimalFormat("#,###.##");
					//YES_NO	hcjs.append("'"+seriesData.get(axisKey).getValueSum().toString()).append("'");
					//	System.out.println("..............................."+formatter.format(amount));
						//@formatter:off
						hcjs.append("var options = { \n"
								+ " chart: {\n"
								+ "        backgroundColor: 'white',\n"
								+ "        borderRadius: '1',\n"
								+ "        borderWidth: '1',\n"
								+ "        spacing: [20, 20, 20, 20],\n"
								+ "    },\n"
								+ "    credits: {\n"
								+ "        enabled: false\n"
								+ "    },\n"
								+ " exporting: { enabled: false },"
								+ " series: [{\n"
								+ "        type: 'wordcloud',\n"
								+ "        data: data = ['"+formatter.format(amount)+""+perce+"'].reduce((arr, word) => {\n"
								+ "        let obj = Highcharts.find(arr, obj => obj.name === word);\n"
								+ "        if (obj) {\n"
								+ "            obj.weight += 1;\n"
								+ "        } else {\n"
								+ "            obj = {\n"
								+ "                name: word,\n"
								+ "                weight: 1\n"
								+ "            };\n"
								+ "            arr.push(obj);\n"
								+ "        }\n"
								+ "        return arr;\n"
								+ "    }, []),\n"
								+ "        name: '"+series.getStack()+"'\n"
								+ "    }],\n"
								+ "    title: {\n"
								+ "        text: '"+series.getStack()+"',\n"
								+ "    style: {\n"
								+ "                color: '#0C090A',\n"
								+ "                fontWeight: 'normal',\n"
								+ "                 fontSize: '12px' \n"
								+ "            } }");
						//@formatter:on
				}
					} else {
					//@formatter:off
					hcjs.append("var options = { \n"
							+ " chart: {\n"
							+ "        backgroundColor: 'white',\n"
							+ "        borderRadius: '1',\n"
							+ "        borderWidth: '1',\n"
							+ "        spacing: [20, 20, 20, 20],\n"
							+ "    },\n"
							+ "    credits: {\n"
							+ "        enabled: false\n"
							+ "    },\n"
							+ " exporting: { enabled: false },"
							+ " series: [{\n"
							+ "        type: 'wordcloud',\n"
							+ "        data: data = ['0"+perce+"', ' '].reduce((arr, word) => {\n"
							+ "        let obj = Highcharts.find(arr, obj => obj.name === word);\n"
							+ "        if (obj) {\n"
							+ "            obj.weight += 1;\n"
							+ "        } else {\n"
							+ "            obj = {\n"
							+ "                name: word,\n"
							+ "                weight: 1\n"
							+ "            };\n"
							+ "            arr.push(obj);\n"
							+ "        }\n"
							+ "        return arr;\n"
							+ "    }, []),\n"
							+ "        name: '"+seriesData.get(axisKey).getStack().toString()+"'\n"
							+ "    }],\n"
							+ "    title: {\n"
							+ "        text: '"+seriesData.get(axisKey).getStack().toString()+"',\n"
							+ "    style: {\n"
							+ "                color: '#0C090A',\n"
							+ "                fontWeight: 'normal',\n"
							+ "                 fontSize: '12px' \n"
							+ "            } }");
					//@formatter:on
				} 
				if(cardChart) {
					qq = 1;
				}
			}
	}else {
		//@formatter:off
		hcjs.append("var options = { \n"
				+ " chart: {\n"
				+ "        backgroundColor: 'white',\n"
				+ "        borderRadius: '1',\n"
				+ "        borderWidth: '1',\n"
				+ "        spacing: [20, 20, 20, 20],\n"
				+ "    },\n"
				+ "    credits: {\n"
				+ "        enabled: false\n"
				+ "    },\n"
				+ " exporting: { enabled: false },"
				+ " series: [{\n"
				+ "        type: 'wordcloud',\n"
				+ "        data: data = ['No data to display', ' '].reduce((arr, word) => {\n"
				+ "        let obj = Highcharts.find(arr, obj => obj.name === word);\n"
				+ "        if (obj) {\n"
				+ "            obj.weight += 1;\n"
				+ "        } else {\n"
				+ "            obj = {\n"
				+ "                name: word,\n"
				+ "                weight: 1\n"
				+ "            };\n"
				+ "            arr.push(obj);\n"
				+ "        }\n"
				+ "        return arr;\n"
				+ "    }, []),\n"
				+ "        name: '"+series.getStack()+"'\n"
				+ "    }],\n"
				+ "    title: {\n"
				+ "        text: '"+series.getStack()+"',\n"
				+ "    style: {\n"
				+ "                color: '#0C090A',\n"
				+ "                fontWeight: 'normal',\n"
				+ "                 fontSize: '12px' \n"
				+ "            } }");
		//@formatter:on
	}
			
			
			

			hcjs.append("}");
			
	//	System.out.println(hcjs.toString());
			
			campaignColumnChart.setHcjs(hcjs.toString());
			
			
			mapSeries.clear();
		}

	private void appendPlotOptions(StringBuilder hcjs, Map<String, Long> stackMap) {
		if (stackMap.size() > 0 || showDataLabels || rotateDataLabels) {
			hcjs.append("plotOptions: {");

			if (stackMap.size() > 0) {
				hcjs.append("column: { stacking: 'normal', borderWidth: 0}");
			}
			if (showDataLabels) {
				hcjs.append(stackMap.size() > 0 ? ", " : "")
					.append(
						"series: { dataLabels: { enabled: true, formatter:function() { if (this.y != 0) return this.y; }, style: { fontSize: 14 + 'px' }");
				if (showPercentages && totalValuesMap != null) {
					hcjs.append(", format: '{y}%'");
				}
				hcjs.append("}}");
			}
			
			if (rotateDataLabels && chartType == DiagramType.COLUMN.name().toLowerCase()) {
				hcjs.append((stackMap.size() > 0 || showDataLabels) ? ", " : "")
					.append(
						"labels: { rotation: -45, style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'}}");
			}
			
			
			if(pieChart && !secondaryChartType.equalsIgnoreCase(DiagramType.DOUGHNUT.toString())) {
				hcjs.append((stackMap.size() > 0 || showDataLabels || rotateDataLabels) ? ", " : "");
				hcjs.append(" pie: {\n"
						+ "            allowPointSelect: true,\n"
						+ "            cursor: 'pointer',\n"
						+ "            dataLabels: {\n"
						+ "                enabled: true,\n"
						+ "                format: '<b>{point.name}</b>: {point.percentage:.1f} %'\n"
						+ "            }\n"
						+ "        }");
			} else if(pieChart && secondaryChartType.equalsIgnoreCase(DiagramType.DOUGHNUT.toString())){
				
				hcjs.append(", pie: {\n"
						+ "            dataLabels: {\n"
						+ "                enabled: true,\n"
						+ "                distance: -50,\n"
						+ " format: '<b>{point.name}</b>: {point.percentage:.1f} %',\n"
						+ "                style: {\n"
						+ "                    fontWeight: 'bold',\n"
						+ "                    color: 'white'\n"
						+ "                }\n"
						+ "            },\n"
						+ "            startAngle: -90,\n"
						+ "            endAngle: 90,\n"
						+ "            center: ['50%', '75%'],\n"
						+ "            size: '110%'\n"
						+ "        }");
				
			}

			hcjs.append("},");
		}

		if (showPercentages && totalValuesMap != null) {
			if(!pieChart) {
			hcjs.append("tooltip:{ valueSuffix: ' %' }, ");
			}
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
	

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	
	

	public String getDiagramCaption() {
		String diagramCaption = diagramDefinition.getDiagramCaption();
		CampaignDiagramTranslations translations = getCampaignDiagramTranslations();
		if (translations != null) {
			diagramCaption = translations.getDiagramCaption();
		}
		return diagramCaption;
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

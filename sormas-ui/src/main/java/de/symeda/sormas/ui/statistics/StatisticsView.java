/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.Quarter;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.Year;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCountDto;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsGroupingKey;
import de.symeda.sormas.api.statistics.StatisticsHelper;
import de.symeda.sormas.api.statistics.StatisticsHelper.StatisticsKeyComparator;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.highcharts.HighChart;
import de.symeda.sormas.ui.map.LeafletMap;
import de.symeda.sormas.ui.map.LeafletMapUtil;
import de.symeda.sormas.ui.map.LeafletPolygon;
import de.symeda.sormas.ui.statistics.StatisticsFilterElement.TokenizableValue;
import de.symeda.sormas.ui.statistics.StatisticsVisualizationType.StatisticsVisualizationChartType;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

public class StatisticsView extends AbstractStatisticsView {

	private static final long serialVersionUID = -4440568319850399685L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME;

	private VerticalLayout filtersLayout;
	private VerticalLayout resultsLayout;
	private CheckBox cbShowZeroValues;
	private CheckBox cbHideOtherCountries;
	private RadioButtonGroup<CaseCountOrIncidence> ogCaseCountOrIncidence;
	private TextField tfIncidenceDivisor;
	private Button exportButton;
	private final Label emptyResultLabel;
	private Label referenceYearLabel;
	private Label populationDataMissingLabel;
	private boolean showCaseIncidence;
	private boolean hasMissingPopulationData;
	private boolean caseIncidencePossible;
	private String missingPopulationDataNames;
	private int incidenceDivisor = 100000;
	private StatisticsCaseGrid statisticsCaseGrid;
	private StatisticsVisualizationComponent visualizationComponent;
	private List<StatisticsFilterComponent> filterComponents = new ArrayList<>();
	private StatisticsCaseCriteria caseCriteria;
	private Integer populationReferenceYear;

	public StatisticsView() {
		super(VIEW_NAME);
		setWidth(100, Unit.PERCENTAGE);

		emptyResultLabel = new Label(I18nProperties.getString(Strings.infoNoCasesFoundStatistics));

		// Main layout
		VerticalLayout statisticsLayout = new VerticalLayout();
		statisticsLayout.setMargin(true);
		statisticsLayout.setSpacing(true);
		statisticsLayout.setWidth(100, Unit.PERCENTAGE);

		// Filters layout
		addFiltersLayout(statisticsLayout);

		// Visualization layout
		Label visualizationTitle = new Label(I18nProperties.getString(Strings.headingVisualization));
		visualizationTitle.setWidthUndefined();
		CssStyles.style(visualizationTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(visualizationTitle);

		visualizationComponent = new StatisticsVisualizationComponent();
		CssStyles.style(visualizationComponent, CssStyles.STATISTICS_TITLE_BOX);
		statisticsLayout.addComponent(visualizationComponent);
		visualizationComponent.addVisualizationTypeChangedListener(visualizationType -> {
			cbHideOtherCountries.setVisible(StatisticsVisualizationType.MAP.equals(visualizationType));
		});

		// Options layout
		addOptionsLayout(statisticsLayout);

		// Generate button
		addGenerateButton(statisticsLayout);

		// Results layout
		addResultsLayout(statisticsLayout);

		// Disclaimer
		Label disclaimer = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " +
				I18nProperties.getString(Strings.infoStatisticsDisclaimer), ContentMode.HTML);
		statisticsLayout.addComponent(disclaimer);

		addComponent(statisticsLayout);
	}

	private void addFiltersLayout(VerticalLayout statisticsLayout) {
		Label filtersLayoutTitle = new Label(I18nProperties.getString(Strings.headingFilters));
		filtersLayoutTitle.setWidthUndefined();
		CssStyles.style(filtersLayoutTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(filtersLayoutTitle);

		VerticalLayout filtersSectionLayout = new VerticalLayout();
		CssStyles.style(filtersSectionLayout, CssStyles.STATISTICS_TITLE_BOX);
		filtersSectionLayout.setSpacing(true);
		filtersSectionLayout.setWidth(100, Unit.PERCENTAGE);
		Label filtersInfoText = new Label(I18nProperties.getString(Strings.infoStatisticsFilter), ContentMode.HTML);
		filtersSectionLayout.addComponent(filtersInfoText);

		filtersLayout = new VerticalLayout();
		filtersLayout.setSpacing(true);		
		filtersLayout.setMargin(false);
		filtersSectionLayout.addComponent(filtersLayout);

		// Filters footer
		HorizontalLayout filtersSectionFooter = new HorizontalLayout();
		{
			filtersSectionFooter.setSpacing(true);

			Button addFilterButton = new Button(I18nProperties.getCaption(Captions.statisticsAddFilter), VaadinIcons.PLUS);
			CssStyles.style(addFilterButton, ValoTheme.BUTTON_PRIMARY);
			addFilterButton.addClickListener(e -> {
				filtersLayout.addComponent(createFilterComponentLayout());
			});
			filtersSectionFooter.addComponent(addFilterButton);

			Button resetFiltersButton = new Button(I18nProperties.getCaption(Captions.statisticsResetFilters));
			resetFiltersButton.addClickListener(e -> {
				filtersLayout.removeAllComponents();
				filterComponents.clear();
			});
			filtersSectionFooter.addComponent(resetFiltersButton);
		}
		filtersSectionLayout.addComponent(filtersSectionFooter);

		statisticsLayout.addComponent(filtersSectionLayout);
	}

	private HorizontalLayout createFilterComponentLayout() {
		HorizontalLayout filterComponentLayout = new HorizontalLayout();
		filterComponentLayout.setSpacing(true);
		filterComponentLayout.setWidth(100, Unit.PERCENTAGE);

		StatisticsFilterComponent filterComponent = new StatisticsFilterComponent();

		Button removeFilterButton = new Button(VaadinIcons.CLOSE);
		removeFilterButton.setDescription(I18nProperties.getCaption(Captions.statisticsRemoveFilter));
		CssStyles.style(removeFilterButton, CssStyles.FORCE_CAPTION);
		removeFilterButton.addClickListener(e -> {
			filterComponents.remove(filterComponent);
			filtersLayout.removeComponent(filterComponentLayout);
		});

		filterComponentLayout.addComponent(removeFilterButton);
		filterComponents.add(filterComponent);
		filterComponentLayout.addComponent(filterComponent);
		filterComponentLayout.setExpandRatio(filterComponent, 1);

		return filterComponentLayout;
	}

	private void addResultsLayout(VerticalLayout statisticsLayout) {
		Label resultsLayoutTitle = new Label(I18nProperties.getString(Strings.headingResults));
		resultsLayoutTitle.setWidthUndefined();
		CssStyles.style(resultsLayoutTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(resultsLayoutTitle);

		resultsLayout = new VerticalLayout();
		resultsLayout.setWidth(100, Unit.PERCENTAGE);
		resultsLayout.setSpacing(true);
		CssStyles.style(resultsLayout, CssStyles.STATISTICS_TITLE_BOX);
		resultsLayout.addComponent(new Label(I18nProperties.getString(Strings.infoStatisticsResults)));

		statisticsLayout.addComponent(resultsLayout);
	}

	private void addOptionsLayout(VerticalLayout statisticsLayout) {
		Label optionsTitle = new Label(I18nProperties.getCaption(Captions.options));
		optionsTitle.setWidthUndefined();
		CssStyles.style(optionsTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(optionsTitle);

		HorizontalLayout optionsLayout = new HorizontalLayout();
		optionsLayout.setWidth(100, Unit.PERCENTAGE);
		optionsLayout.setSpacing(true);
		CssStyles.style(optionsLayout, CssStyles.STATISTICS_TITLE_BOX);
		{
			ogCaseCountOrIncidence = new RadioButtonGroup<CaseCountOrIncidence>(I18nProperties.getCaption(Captions.statisticsDataDisplayed), Arrays.asList(CaseCountOrIncidence.values()));
			ogCaseCountOrIncidence.setValue(CaseCountOrIncidence.CASE_COUNT);
			ogCaseCountOrIncidence.addValueChangeListener(e -> {
				showCaseIncidence = e.getValue() == CaseCountOrIncidence.CASE_INCIDENCE;
				tfIncidenceDivisor.setVisible(showCaseIncidence);
				visualizationComponent.setStackedColumnAndPieEnabled(!showCaseIncidence);
			});
			CssStyles.style(ogCaseCountOrIncidence, CssStyles.VSPACE_NONE, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.SOFT_REQUIRED);
			optionsLayout.addComponent(ogCaseCountOrIncidence);

			tfIncidenceDivisor = new TextField(I18nProperties.getCaption(Captions.statisticsIncidenceDivisor));
			tfIncidenceDivisor.setValue("100000");
			tfIncidenceDivisor.addValueChangeListener(e -> {
				try {
					// Store value in a temporary variable to trigger possible exception
					int newDivisor = Integer.valueOf(e.getValue());
					incidenceDivisor = newDivisor;
				} catch (NumberFormatException ex) {
					tfIncidenceDivisor.setValue(String.valueOf(incidenceDivisor));
					new Notification(null, I18nProperties.getValidationError(Validations.statisticsIncidenceOnlyNumbersAllowed), Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				}
			});
			optionsLayout.addComponent(tfIncidenceDivisor);
			tfIncidenceDivisor.setVisible(false);

			cbShowZeroValues = new CheckBox(I18nProperties.getCaption(Captions.statisticsShowZeroValues));
			cbShowZeroValues.setValue(false);
			CssStyles.style(cbShowZeroValues, CssStyles.FORCE_CAPTION_CHECKBOX);
			optionsLayout.addComponent(cbShowZeroValues);

			cbHideOtherCountries = new CheckBox(I18nProperties.getCaption(Captions.dashboardHideOtherCountries));
			cbHideOtherCountries.setValue(false);
			CssStyles.style(cbHideOtherCountries, CssStyles.FORCE_CAPTION_CHECKBOX);
			optionsLayout.addComponent(cbHideOtherCountries);
			cbHideOtherCountries.setVisible(StatisticsVisualizationType.MAP.equals(visualizationComponent.getVisualizationType()));

			Label expandedDummy = new Label();
			optionsLayout.addComponent(expandedDummy);
			optionsLayout.setExpandRatio(expandedDummy, 1);
		}
		statisticsLayout.addComponent(optionsLayout);
	}

	private void addGenerateButton(VerticalLayout statisticsLayout) {
		Button generateButton = new Button(I18nProperties.getCaption(Captions.actionGenerate));
		CssStyles.style(generateButton, ValoTheme.BUTTON_PRIMARY);
		generateButton.addClickListener(e -> {
			// Check whether there is any invalid empty filter or grouping data
			Notification errorNotification = null;
			for (StatisticsFilterComponent filterComponent : filterComponents) {
				if (filterComponent.getSelectedAttribute() != StatisticsCaseAttribute.REGION_DISTRICT
						&& (filterComponent.getSelectedAttribute() == null
						|| filterComponent.getSelectedAttribute().getSubAttributes().length > 0
						&& filterComponent.getSelectedSubAttribute() == null)) {
					errorNotification = new Notification(I18nProperties.getString(Strings.messageSpecifyFilterAttributes), Type.WARNING_MESSAGE);
					break;
				}
			}

			if (showCaseIncidence && hasPopulationFilterUnknownValue()) {
				errorNotification = new Notification(I18nProperties.getString(Strings.messageUnknownFilterAttributeForPopulationData), Type.ERROR_MESSAGE);
			}

			if (showCaseIncidence && visualizationComponent.hasAgeGroupGroupingWithoutPopulationData() || hasUnsupportedPopulationAgeGroupFilter()) {
				errorNotification = new Notification(I18nProperties.getString(Strings.messageCaseIncidenceUnsupportedAgeGroup), Type.ERROR_MESSAGE);
			}

			if (errorNotification == null && visualizationComponent.getRowsAttribute() != null
					&& visualizationComponent.getRowsAttribute().getSubAttributes().length > 0
					&& visualizationComponent.getRowsSubAttribute() == null) {
				errorNotification = new Notification(I18nProperties.getString(Strings.messageSpecifyRowAttribute), Type.WARNING_MESSAGE);
			} else if (errorNotification == null && visualizationComponent.getColumnsAttribute() != null
					&& visualizationComponent.getColumnsAttribute().getSubAttributes().length > 0
					&& visualizationComponent.getColumnsSubAttribute() == null) {
				errorNotification = new Notification(I18nProperties.getString(Strings.messageSpecifyColumnAttribute), Type.WARNING_MESSAGE);
			}

			if (errorNotification != null) {
				errorNotification.setDelayMsec(-1);
				errorNotification.show(Page.getCurrent());
			} else {
				resultsLayout.removeAllComponents();
				switch (visualizationComponent.getVisualizationType()) {
				case TABLE:
					generateTable();
					break;
				case MAP:
					generateMap();
					break;
				default:
					generateChart();
					break;
				}
			}
		});

		statisticsLayout.addComponent(generateButton);
	}

	public void generateTable() {
		List<StatisticsCaseCountDto> resultData = generateStatistics();

		if (resultData.isEmpty()) {
			resultsLayout.addComponent(emptyResultLabel);
			return;
		}		

		if (showCaseIncidence && caseIncidencePossible && populationReferenceYear != null && populationReferenceYear != Calendar.getInstance().get(Calendar.YEAR)) {
			referenceYearLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(Strings.infoPopulationReferenceYear), populationReferenceYear), ContentMode.HTML);
			resultsLayout.addComponent(referenceYearLabel);
			CssStyles.style(referenceYearLabel, CssStyles.VSPACE_TOP_4);
		}
		
		if (showCaseIncidence && hasMissingPopulationData) {
			if (!caseIncidencePossible) {
				populationDataMissingLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(Strings.infoCaseIncidenceNotPossible), missingPopulationDataNames), ContentMode.HTML);
				resultsLayout.addComponent(populationDataMissingLabel);
			} else {
				populationDataMissingLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(Strings.infoCaseIncidenceMissingPopulationData), missingPopulationDataNames), ContentMode.HTML);
			}
			populationDataMissingLabel.setWidth(100, Unit.PERCENTAGE);
			CssStyles.style(populationDataMissingLabel, CssStyles.VSPACE_TOP_4);
		}

		exportButton = new Button(I18nProperties.getCaption(Captions.export));
		exportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
		exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		exportButton.setIcon(VaadinIcons.TABLE);
		resultsLayout.addComponent(exportButton);
		resultsLayout.setComponentAlignment(exportButton, Alignment.TOP_RIGHT);

		statisticsCaseGrid = new StatisticsCaseGrid(
				visualizationComponent.getRowsAttribute(), visualizationComponent.getRowsSubAttribute(),
				visualizationComponent.getColumnsAttribute(), visualizationComponent.getColumnsSubAttribute(), 
				showCaseIncidence && caseIncidencePossible, incidenceDivisor, resultData, caseCriteria);
		resultsLayout.addComponent(statisticsCaseGrid);
		resultsLayout.setExpandRatio(statisticsCaseGrid, 1);

		if (showCaseIncidence && hasMissingPopulationData && caseIncidencePossible) {
			resultsLayout.addComponent(populationDataMissingLabel);
		}

		StreamResource streamResource = DownloadUtil.createGridExportStreamResource(
				statisticsCaseGrid.getContainerDataSource(), statisticsCaseGrid.getColumns(), "sormas_statistics",
				"sormas_statistics_" + DateHelper.formatDateForExport(new Date()) + ".csv");
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(exportButton);
	}

	public void generateChart() {
		List<StatisticsCaseCountDto> resultData = generateStatistics();

		if (resultData.isEmpty()) {
			resultsLayout.addComponent(emptyResultLabel);
			return;
		}		

		if (showCaseIncidence && caseIncidencePossible && populationReferenceYear != null && populationReferenceYear != Calendar.getInstance().get(Calendar.YEAR)) {
			referenceYearLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(Strings.infoPopulationReferenceYear), populationReferenceYear), ContentMode.HTML);
			resultsLayout.addComponent(referenceYearLabel);
			CssStyles.style(referenceYearLabel, CssStyles.VSPACE_TOP_4);
		}

		if (showCaseIncidence && hasMissingPopulationData) {
			if (!caseIncidencePossible) {
				populationDataMissingLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(Strings.infoCaseIncidenceNotPossible), missingPopulationDataNames), ContentMode.HTML);
				resultsLayout.addComponent(populationDataMissingLabel);
			} else {
				populationDataMissingLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(Strings.infoCaseIncidenceMissingPopulationData), missingPopulationDataNames), ContentMode.HTML);
			}
			populationDataMissingLabel.setWidth(100, Unit.PERCENTAGE);
			CssStyles.style(populationDataMissingLabel, CssStyles.VSPACE_TOP_4);
		}

		StatisticsVisualizationChartType chartType = visualizationComponent.getVisualizationChartType();
		StatisticsCaseAttribute xAxisAttribute = visualizationComponent.getColumnsAttribute();
		StatisticsCaseSubAttribute xAxisSubAttribute = visualizationComponent.getColumnsSubAttribute();
		StatisticsCaseAttribute seriesAttribute = visualizationComponent.getRowsAttribute();
		StatisticsCaseSubAttribute seriesSubAttribute = visualizationComponent.getRowsSubAttribute();

		HighChart chart = new HighChart();
		chart.setWidth(100, Unit.PERCENTAGE);
		chart.setHeight(580, Unit.PIXELS);

		StringBuilder hcjs = new StringBuilder();
		hcjs.append("var options = {").append("chart:{ " + " ignoreHiddenSeries: false, " + " type: '");
		switch (chartType) {
		case COLUMN:
		case STACKED_COLUMN:
			hcjs.append("column");
			break;
		case LINE:
			hcjs.append("line");
			break;
		case PIE:
			hcjs.append("pie");
			break;
		default:
			throw new IllegalArgumentException(chartType.toString());
		}

		hcjs.append("', " + " backgroundColor: 'transparent' " + "}," + "credits:{ enabled: false }," + "exporting:{ "
				+ " enabled: true," + " buttons:{ contextButton:{ theme:{ fill: 'transparent' } } }" + "},"
				+ "title:{ text: '' },");
		
		CaseCountOrIncidence dataStyle = showCaseIncidence && caseIncidencePossible ?CaseCountOrIncidence.CASE_INCIDENCE : CaseCountOrIncidence.CASE_COUNT;

		TreeMap<StatisticsGroupingKey, String> xAxisCaptions = new TreeMap<>(new StatisticsKeyComparator());
		TreeMap<StatisticsGroupingKey, String> seriesCaptions = new TreeMap<>(new StatisticsKeyComparator());
		boolean appendUnknownXAxisCaption = false;
		if (seriesAttribute != null || xAxisAttribute != null) {
			// Build captions for x-axis and/or series
			for (StatisticsCaseCountDto row : resultData) {
				
				if (xAxisAttribute != null) {
					if (!StatisticsHelper.isNullOrUnknown(row.getColumnKey())) {
						xAxisCaptions.putIfAbsent((StatisticsGroupingKey) row.getColumnKey(),
								row.getColumnKey().toString());
					} else {
						appendUnknownXAxisCaption = true;
					}
				}
				if (seriesAttribute != null) {
					if (!StatisticsHelper.isNullOrUnknown(row.getRowKey())) {
						seriesCaptions.putIfAbsent((StatisticsGroupingKey) row.getRowKey(),
								row.getRowKey().toString());
					}
				}
			}
		}

		if (chartType != StatisticsVisualizationChartType.PIE) {

			hcjs.append("xAxis: { categories: [");
			if (xAxisAttribute != null) {
				xAxisCaptions.forEach((key, value) -> {
					hcjs.append("'").append(xAxisCaptions.get(key)).append("',");
				});

				if (appendUnknownXAxisCaption) {
					hcjs.append("'").append(getEscapedFragment(StatisticsHelper.UNKNOWN)).append("'");
				}
			} else if (seriesAttribute != null) {
				hcjs.append("'").append(seriesSubAttribute != null ? seriesSubAttribute.toString() : seriesAttribute.toString()).append("'");
			} else {
				hcjs.append("'").append(getEscapedFragment(StatisticsHelper.TOTAL)).append("'");
			}
			int numberOfCategories = xAxisAttribute != null ? appendUnknownXAxisCaption ? xAxisCaptions.size() + 1 : xAxisCaptions.size() : 1;
			hcjs.append("], min: 0, max: " + (numberOfCategories - 1) + "},");

			hcjs.append("yAxis: { min: 0, title: { text: '").append(dataStyle)
			.append("' },").append("allowDecimals: false, softMax: ").append(showCaseIncidence && caseIncidencePossible ? 1 : 10).append(", stackLabels: { enabled: true, ")
			.append("style: {fontWeight: 'normal', textOutline: '0', gridLineColor: '#000000', color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray' } } },");

			hcjs.append("tooltip: { headerFormat: '<b>{point.x}</b><br/>', pointFormat: '{series.name}: {point.y}");
			if (chartType == StatisticsVisualizationChartType.STACKED_COLUMN) {
				hcjs.append("<br/>").append(I18nProperties.getCaption(Captions.total) + ": {point.stackTotal}");
			}
			hcjs.append("'},");
		}

		hcjs.append("legend: { verticalAlign: 'top', backgroundColor: 'transparent', align: 'left', "
				+ "borderWidth: 0, shadow: false, margin: 30, padding: 0 },");

		hcjs.append(
				"colors: ['#FF0000','#6691C4','#ffba08','#519e8a','#ed254e','#39a0ed','#FF8C00','#344055','#D36135','#82d173'],");

		if (chartType == StatisticsVisualizationChartType.STACKED_COLUMN || chartType == StatisticsVisualizationChartType.COLUMN) {
			hcjs.append("plotOptions: { column: { borderWidth: 0, ");
			if (chartType == StatisticsVisualizationChartType.STACKED_COLUMN) {
				hcjs.append("stacking: 'normal', ");
			}
			hcjs.append("groupPadding: 0.05, pointPadding: 0, " + "dataLabels: {" + "enabled: true,"
					+ "formatter: function() { if (this.y > 0) return this.y; }," + "color: '#444',"
					+ "backgroundColor: 'rgba(255, 255, 255, 0.75)'," + "borderRadius: 3," + "padding: 3,"
					+ "style:{textOutline:'none'}" + "} } },");
		}

		hcjs.append("series: [");
		if (seriesAttribute == null && xAxisAttribute == null) {
			hcjs.append("{ name: '").append(dataStyle)
			.append("', dataLabels: { allowOverlap: false }").append(", data: [['")
			.append(dataStyle).append("',");
			if (!showCaseIncidence || !caseIncidencePossible) {
				hcjs.append(resultData.get(0).getCaseCount().toString());
			} else {
				hcjs.append(resultData.get(0).getIncidence(incidenceDivisor));
			}
			hcjs.append("]]}");
		} else if (visualizationComponent.getVisualizationChartType() == StatisticsVisualizationChartType.PIE) {
			hcjs.append("{ name: '").append(dataStyle)
			.append("', dataLabels: { allowOverlap: false }").append(", data: [");
			TreeMap<StatisticsGroupingKey, StatisticsCaseCountDto> seriesElements = new TreeMap<>(new StatisticsKeyComparator());
			StatisticsCaseCountDto unknownSeriesElement = null;
			for (StatisticsCaseCountDto row : resultData) {
				Object seriesId = row.getRowKey();
				if (StatisticsHelper.isNullOrUnknown(seriesId)) {
					unknownSeriesElement = row;
				} else {
					seriesElements.put((StatisticsGroupingKey) seriesId, row);
				}
			}

			seriesElements.forEach((key, value) -> {
				Object seriesValue;
				if (!showCaseIncidence || !caseIncidencePossible) {
					seriesValue = value.getCaseCount();
				} else {
					seriesValue = value.getIncidence(incidenceDivisor);
				}
				Object seriesId = value.getRowKey();
				hcjs.append("['").append(seriesCaptions.get(seriesId)).append("',").append(seriesValue).append("],");
			});
			if (unknownSeriesElement != null) {
				Object seriesValue;
				if (!showCaseIncidence || !caseIncidencePossible) {
					seriesValue = unknownSeriesElement.getCaseCount();
				} else {
					seriesValue = unknownSeriesElement.getIncidence(incidenceDivisor);
				}
				hcjs.append("['").append(dataStyle).append("',")
				.append(seriesValue).append("],");
			}
			hcjs.append("]}");
		} else {
			Object seriesKey = null;
			TreeMap<StatisticsGroupingKey, String> seriesStrings = new TreeMap<>(new StatisticsKeyComparator());
			final StringBuilder currentSeriesString = new StringBuilder();
			final StringBuilder unknownSeriesString = new StringBuilder();
			final StringBuilder totalSeriesString = new StringBuilder();
			TreeMap<Integer, Number> currentSeriesValues = new TreeMap<>();

			for (StatisticsCaseCountDto row : resultData) {
				// Retrieve series caption of the current row
				Object rowSeriesKey;
				if (seriesAttribute != null) {
					if (!StatisticsHelper.isNullOrUnknown(row.getRowKey())) {
						rowSeriesKey = row.getRowKey();
					} else {
						rowSeriesKey = StatisticsHelper.VALUE_UNKNOWN;
					}
				} else {
					rowSeriesKey = StatisticsHelper.TOTAL;
				}

				// If the first row or a row with a new caption is processed, save the data and
				// begin a new series
				if (!DataHelper.equal(seriesKey, rowSeriesKey)) {
					finalizeChartSegment(seriesKey, currentSeriesValues, unknownSeriesString, currentSeriesString,
							totalSeriesString, seriesStrings);

					// Append the start sequence of the next series String
					if (StatisticsHelper.isNullOrUnknown(rowSeriesKey)) {
						seriesKey = StatisticsHelper.VALUE_UNKNOWN;
						unknownSeriesString.append("{ name: '").append(getEscapedFragment(StatisticsHelper.UNKNOWN))
						.append("', dataLabels: { allowOverlap: false }, data: [");
					} else if (rowSeriesKey.equals(StatisticsHelper.TOTAL)) {
						seriesKey = StatisticsHelper.TOTAL;
						totalSeriesString.append("{name : '").append(getEscapedFragment(StatisticsHelper.TOTAL))
						.append("', dataLabels: { allowOverlap: false }, data: [");
					} else {
						seriesKey = (StatisticsGroupingKey) row.getRowKey();
						currentSeriesString.append("{ name: '")
						.append(StringEscapeUtils.escapeEcmaScript(seriesCaptions.get(seriesKey)))
						.append("', dataLabels: { allowOverlap: false }, data: [");
					}
				}

				Object value;
				if (!showCaseIncidence || !caseIncidencePossible) {
					value = row.getCaseCount();
				} else {
					value = row.getIncidence(incidenceDivisor);
				}
				if (xAxisAttribute != null) {
					Object xAxisId = row.getColumnKey();
					int captionPosition = StatisticsHelper.isNullOrUnknown(xAxisId) ? xAxisCaptions.size()
							: xAxisCaptions.headMap((StatisticsGroupingKey) xAxisId).size();
					currentSeriesValues.put(captionPosition, (Number) value);
				} else {
					currentSeriesValues.put(0, (Number) value);
				}
			}

			// Add the last series
			finalizeChartSegment(seriesKey, currentSeriesValues, unknownSeriesString, currentSeriesString,
					totalSeriesString, seriesStrings);

			seriesStrings.forEach((key, value) -> {
				hcjs.append(value);
			});

			// Add the "Unknown" series
			if (unknownSeriesString.length() > 0) {
				hcjs.append(unknownSeriesString.toString());
			}

			// Add the "Total" series
			if (totalSeriesString.length() > 0) {
				hcjs.append(totalSeriesString.toString());
			}

			// Remove last three characters to avoid invalid chart
			hcjs.delete(hcjs.length() - 3, hcjs.length());

			hcjs.append("]}");
		}
		hcjs.append("]};");

		chart.setHcjs(hcjs.toString());
		resultsLayout.addComponent(chart);		
		resultsLayout.setExpandRatio(chart, 1);

		if (showCaseIncidence && hasMissingPopulationData && caseIncidencePossible) {
			resultsLayout.addComponent(populationDataMissingLabel);
		}
	}

	private String getEscapedFragment(String i18nFragmentKeykey) {
		return StringEscapeUtils.escapeEcmaScript(I18nProperties.getCaption(i18nFragmentKeykey));
	}

	private void finalizeChartSegment(Object seriesKey, TreeMap<Integer, Number> currentKeyValues,
			StringBuilder unknownKeyString, StringBuilder currentKeyString, StringBuilder totalKeyString,
			TreeMap<StatisticsGroupingKey, String> columnStrings) {
		if (seriesKey != null) {
			if (StatisticsHelper.isNullOrUnknown(seriesKey)) {
				currentKeyValues.forEach((key, value) -> {
					unknownKeyString.append("[").append(key).append(",").append(value).append("],");
				});
				unknownKeyString.append("]},");
				currentKeyValues.clear();
				currentKeyString.setLength(0);
			} else if (seriesKey.equals(StatisticsHelper.TOTAL)) {
				currentKeyValues.forEach((key, value) -> {
					totalKeyString.append("[").append(key).append(",").append(value).append("],");
				});
				totalKeyString.append("]},");
				currentKeyValues.clear();
				currentKeyString.setLength(0);
			} else {
				StatisticsGroupingKey seriesGroupingKey = (StatisticsGroupingKey) seriesKey;
				currentKeyValues.forEach((key, value) -> {
					currentKeyString.append("[").append(key).append(",").append(value).append("],");
				});
				currentKeyString.append("]},");
				columnStrings.put(seriesGroupingKey, currentKeyString.toString());
				currentKeyValues.clear();
				currentKeyString.setLength(0);
			}
		}
	}

	public void generateMap() {
		List<StatisticsCaseCountDto> resultData = generateStatistics();

		if (resultData.isEmpty()) {
			resultsLayout.addComponent(emptyResultLabel);
			return;
		}		

		if (showCaseIncidence && caseIncidencePossible && populationReferenceYear != null && populationReferenceYear != Calendar.getInstance().get(Calendar.YEAR)) {
			referenceYearLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(Strings.infoPopulationReferenceYear), populationReferenceYear), ContentMode.HTML);
			resultsLayout.addComponent(referenceYearLabel);
			CssStyles.style(referenceYearLabel, CssStyles.VSPACE_TOP_4);
		}

		if (showCaseIncidence && hasMissingPopulationData) {
			if (!caseIncidencePossible) {
				populationDataMissingLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(Strings.infoCaseIncidenceNotPossible), missingPopulationDataNames), ContentMode.HTML);
				resultsLayout.addComponent(populationDataMissingLabel);
			} else {
				populationDataMissingLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(I18nProperties.getString(Strings.infoCaseIncidenceMissingPopulationData), missingPopulationDataNames), ContentMode.HTML);
			}
			populationDataMissingLabel.setWidth(100, Unit.PERCENTAGE);
			CssStyles.style(populationDataMissingLabel, CssStyles.VSPACE_TOP_4);
		}

		HorizontalLayout mapLayout = new HorizontalLayout();
		mapLayout.setSpacing(true);
		mapLayout.setMargin(false);
		mapLayout.setWidth(100, Unit.PERCENTAGE);
		mapLayout.setHeightUndefined();

		LeafletMap map = new LeafletMap();
		map.setTileLayerOpacity(0.5f);
		map.setWidth(100, Unit.PERCENTAGE);
		map.setHeight(580, Unit.PIXELS);
		map.setZoom(FacadeProvider.getConfigFacade().getMapZoom());
		GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfAllRegions();
		if (mapCenter != null) {
			map.setCenter(mapCenter);
		} else {
			GeoLatLon countryCenter = FacadeProvider.getConfigFacade().getCountryCenter();
			map.setCenter(countryCenter);
		}

		if (cbHideOtherCountries.getValue()) {
			LeafletMapUtil.addOtherCountriesOverlay(map);
		}
		
		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveAsReference();

		List<LeafletPolygon> outlinePolygones = new ArrayList<LeafletPolygon>();

		// draw outlines of all regions
		for (RegionReferenceDto region : regions) {

			GeoLatLon[][] regionShape = FacadeProvider.getGeoShapeProvider().getRegionShape(region);
			if (regionShape == null) {
				continue;
			}

			for (int part = 0; part < regionShape.length; part++) {
				GeoLatLon[] regionShapePart = regionShape[part];
				LeafletPolygon polygon = new LeafletPolygon();
				polygon.setCaption(region.getCaption());
				// fillOpacity is used, so we can still hover the region
				polygon.setOptions("{\"weight\": 1, \"color\": '#888', \"fillOpacity\": 0.02}");
				polygon.setLatLons(regionShapePart);
				outlinePolygones.add(polygon);
			}
		}

		map.addPolygonGroup("outlines", outlinePolygones);

		if (!showCaseIncidence || !caseIncidencePossible) {
			resultData.sort((a, b) -> {
				return Integer.compare(a.getCaseCount(), b.getCaseCount());
			});
		} else {
			resultData.sort((a, b) -> {
				BigDecimal incidenceA = a.getIncidence(incidenceDivisor);
				BigDecimal incidenceB = b.getIncidence(incidenceDivisor);
				return DataHelper.compare(incidenceA, incidenceB);
			});
		}

		BigDecimal valuesLowerQuartile, valuesMedian, valuesUpperQuartile;
		if (!showCaseIncidence || !caseIncidencePossible) {
			valuesLowerQuartile = resultData.size() > 0 ? new BigDecimal(resultData.get((int) (resultData.size() * 0.25)).getCaseCount()) : BigDecimal.ZERO;
			valuesMedian = resultData.size() > 0 ? new BigDecimal(resultData.get((int) (resultData.size() * 0.5)).getCaseCount()) : BigDecimal.ZERO;
			valuesUpperQuartile = resultData.size() > 0 ? new BigDecimal(resultData.get((int) (resultData.size() * 0.75)).getCaseCount()) : BigDecimal.ZERO;
		} else {
			valuesLowerQuartile = resultData.size() > 0 ? resultData.get((int) (resultData.size() * 0.25)).getIncidence(incidenceDivisor) : BigDecimal.ZERO;
			if (valuesLowerQuartile == null) {
				valuesLowerQuartile = BigDecimal.ZERO;
			}
			valuesMedian = resultData.size() > 0 ? resultData.get((int) (resultData.size() * 0.5)).getIncidence(incidenceDivisor) : BigDecimal.ZERO;
			if (valuesMedian == null) {
				valuesMedian = BigDecimal.ZERO;
			}
			valuesUpperQuartile = resultData.size() > 0 ? resultData.get((int) (resultData.size() * 0.75)).getIncidence(incidenceDivisor) : BigDecimal.ZERO;
			if (valuesUpperQuartile == null) {
				valuesUpperQuartile = BigDecimal.ZERO;
			}
		}

		List<LeafletPolygon> resultPolygons = new ArrayList<LeafletPolygon>();

		boolean hasNullValue = false;
		// Draw relevant district fills
		for (StatisticsCaseCountDto resultRow : resultData) {
			ReferenceDto regionOrDistrict = (ReferenceDto) resultRow.getRowKey();
			String shapeUuid = regionOrDistrict.getUuid();
			BigDecimal regionOrDistrictValue;
			if (!showCaseIncidence || !caseIncidencePossible) {
				regionOrDistrictValue = new BigDecimal(resultRow.getCaseCount());
			} else {
				regionOrDistrictValue = resultRow.getIncidence(incidenceDivisor);
			}
			hasNullValue |= regionOrDistrictValue == null;
			
			GeoLatLon[][] shape;
			switch (visualizationComponent.getVisualizationMapType()) {
			case REGIONS:
				shape = FacadeProvider.getGeoShapeProvider().getRegionShape(new RegionReferenceDto(shapeUuid));
				break;
			case DISTRICTS:
				shape = FacadeProvider.getGeoShapeProvider().getDistrictShape(new DistrictReferenceDto(shapeUuid));
				break;
			default:
				throw new IllegalArgumentException(visualizationComponent.getVisualizationMapType().toString());
			}

			if (shape == null) {
				continue;
			}

			for (int part = 0; part < shape.length; part++) {
				GeoLatLon[] shapePart = shape[part];
				String fillColor;
				String fillOpacity = "0.8";
				if (regionOrDistrictValue == null) {
					fillColor = "#888";
				} else if (regionOrDistrictValue.compareTo(BigDecimal.ZERO) == 0) {
					fillColor = "#000";
					fillOpacity = "0";
				} else if (regionOrDistrictValue.compareTo(valuesLowerQuartile) < 0) {
					fillColor = "#FEDD6C";
				} else if (regionOrDistrictValue.compareTo(valuesMedian) < 0) {
					fillColor = "#FDBF44";
				} else if (regionOrDistrictValue.compareTo(valuesUpperQuartile) < 0) {
					fillColor = "#F47B20";
				} else {
					fillColor = "#ED1B24";
				}

				LeafletPolygon polygon = new LeafletPolygon();
				if (regionOrDistrictValue == null) {
					polygon.setCaption(regionOrDistrict.getCaption() + "<br>" + I18nProperties.getCaption(Captions.notAvailableShort));
				} else {
					polygon.setCaption(regionOrDistrict.getCaption() + "<br>" + regionOrDistrictValue);
				}
				// fillOpacity is used, so we can still hover the region
				polygon.setOptions("{\"stroke\": false, \"color\": '" + fillColor + "', \"fillOpacity\": " + fillOpacity + "}");
				polygon.setLatLons(shapePart);
				resultPolygons.add(polygon);
			}
		}
		map.addPolygonGroup("results", resultPolygons);

		mapLayout.addComponent(map);
		mapLayout.setExpandRatio(map, 1);

		if (showCaseIncidence && caseIncidencePossible) {
			valuesLowerQuartile = valuesLowerQuartile.setScale(2, RoundingMode.HALF_UP);
			valuesMedian = valuesMedian.setScale(2, RoundingMode.HALF_UP);
			valuesUpperQuartile = valuesUpperQuartile.setScale(2, RoundingMode.HALF_UP);
		}

		AbstractOrderedLayout regionLegend = DashboardMapComponent.buildRegionLegend(true, 
				showCaseIncidence && caseIncidencePossible ? CaseMeasure.CASE_INCIDENCE : CaseMeasure.CASE_COUNT,
						hasNullValue, valuesLowerQuartile, valuesMedian, valuesUpperQuartile, incidenceDivisor);
		Label legendHeader = new Label(I18nProperties.getCaption(Captions.dashboardMapKey));
		CssStyles.style(legendHeader, CssStyles.H4, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		regionLegend.addComponent(legendHeader, 0);

		mapLayout.addComponent(regionLegend);
		mapLayout.setExpandRatio(regionLegend, 0);

		resultsLayout.addComponent(mapLayout);
		resultsLayout.setExpandRatio(mapLayout, 1);

		if (showCaseIncidence && hasMissingPopulationData && caseIncidencePossible) {
			resultsLayout.addComponent(populationDataMissingLabel);
		}
	}

	private List<StatisticsCaseCountDto> generateStatistics() {
		fillCaseCriteria(showCaseIncidence);

		if (showCaseIncidence) {	
			hasMissingPopulationData = false;
			caseIncidencePossible = true;
			missingPopulationDataNames = null;

			if (!visualizationComponent.hasRegionGrouping() && !visualizationComponent.hasDistrictGrouping()) {
				// we don't have a territorial grouping, so the system will sum up the population of all regions.
				// make sure the user is informed about regions with missing population data
				
				 List<Long> missingPopulationDataRegionIds = FacadeProvider.getPopulationDataFacade().getMissingPopulationDataForStatistics(caseCriteria, false, false, visualizationComponent.hasSexGrouping(), visualizationComponent.hasAgeGroupGroupingWithPopulationData());
				 hasMissingPopulationData = missingPopulationDataRegionIds.size() > 0;
				 if (hasMissingPopulationData) {
					 caseIncidencePossible = false;
					 List<String> missingPopulationDataNamesList = FacadeProvider.getRegionFacade().getNamesByIds(missingPopulationDataRegionIds);
					 missingPopulationDataNames = String.join(", ", missingPopulationDataNamesList);
				 }
			}

			// Calculate projected population by either using the current year or, if a date filter has been selected, the maximum year from the date filter
			populationReferenceYear = calculateMaximumReferenceYear(null, caseCriteria.getOnsetYears(), Comparator.naturalOrder(), e -> e.getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, caseCriteria.getReportYears(), Comparator.naturalOrder(), e -> e.getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, caseCriteria.getOnsetMonthsOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, caseCriteria.getReportMonthsOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, caseCriteria.getOnsetQuartersOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, caseCriteria.getReportQuartersOfYear(), Comparator.naturalOrder(), e -> e.getYear().getValue());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, caseCriteria.getOnsetEpiWeeksOfYear(), Comparator.naturalOrder(), e -> e.getYear());
			populationReferenceYear = calculateMaximumReferenceYear(populationReferenceYear, caseCriteria.getReportEpiWeeksOfYear(), Comparator.naturalOrder(), e -> e.getYear());
		}

		List<StatisticsCaseCountDto> resultData = FacadeProvider.getCaseStatisticsFacade().queryCaseCount(caseCriteria,
				visualizationComponent.getRowsAttribute(), visualizationComponent.getRowsSubAttribute(),
				visualizationComponent.getColumnsAttribute(), visualizationComponent.getColumnsSubAttribute(), 
				showCaseIncidence && caseIncidencePossible, cbShowZeroValues.getValue(), populationReferenceYear);
		
		StatisticsKeyComparator keyComparator = new StatisticsKeyComparator();
		resultData.sort((c1, c2) -> {
			int result = keyComparator.compare(c1.getRowKey(), c2.getRowKey());
			if (result == 0) {
				result = keyComparator.compare(c1.getColumnKey(), c2.getColumnKey());
			}
			return result;
		});

		return resultData;
	}

	private boolean hasPopulationFilterUnknownValue() {
		for (StatisticsFilterComponent filterComponent : filterComponents) {
			if (filterComponent.getSelectedAttribute() == StatisticsCaseAttribute.SEX ||
					filterComponent.getSelectedAttribute() == StatisticsCaseAttribute.AGE_INTERVAL_5_YEARS) {
				for (TokenizableValue selectedValue : filterComponent.getFilterElement().getSelectedValues()) {
					if (selectedValue.getValue().toString().equals(I18nProperties.getString(Strings.unknown))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean hasUnsupportedPopulationAgeGroupFilter() {
		for (StatisticsFilterComponent filterComponent : filterComponents) {
			if (filterComponent.getSelectedAttribute() == StatisticsCaseAttribute.AGE_INTERVAL_1_YEAR
					|| filterComponent.getSelectedAttribute() == StatisticsCaseAttribute.AGE_INTERVAL_BASIC
					|| filterComponent.getSelectedAttribute() == StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_COARSE
					|| filterComponent.getSelectedAttribute() == StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_MEDIUM
					|| filterComponent.getSelectedAttribute() == StatisticsCaseAttribute.AGE_INTERVAL_CHILDREN_FINE) {
				return true;
			}
		}

		return false;
	}

	private <T extends StatisticsGroupingKey> Integer calculateMaximumReferenceYear(Integer currentMaxYear, List<T> list, Comparator<? super T> comparator, Function<? super T, Integer> mapFunction) {
		Integer maxYear = null;

		if (!CollectionUtils.isEmpty(list)) {
			maxYear = (Integer) list.stream().max(comparator).map(mapFunction).orElse(null);
		}

		return currentMaxYear != null ? ((maxYear != null && maxYear > currentMaxYear) ? maxYear : currentMaxYear) : maxYear;
	}


	private void fillCaseCriteria(boolean showCaseIncidence) {
		caseCriteria = new StatisticsCaseCriteria();

		for (StatisticsFilterComponent filterComponent : filterComponents) {
			StatisticsFilterElement filterElement = filterComponent.getFilterElement();
			switch (filterComponent.getSelectedAttribute()) {
			case SEX:
				if (filterElement.getSelectedValues() != null) {
					List<Sex> sexes = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
						if (tokenizableValue.getValue().equals(I18nProperties.getString(Strings.unknown))) {
							caseCriteria.sexUnknown(true);
						} else {
							sexes.add((Sex) tokenizableValue.getValue());
						}
					}
					caseCriteria.sexes(sexes);
				}
				break;
			case DISEASE:
				if (filterElement.getSelectedValues() != null) {
					List<Disease> diseases = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
						diseases.add((Disease) tokenizableValue.getValue());
					}
					caseCriteria.diseases(diseases);
				}
				break;
			case CLASSIFICATION:
				if (filterElement.getSelectedValues() != null) {
					List<CaseClassification> classifications = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
						classifications.add((CaseClassification) tokenizableValue.getValue());
					}
					caseCriteria.classifications(classifications);
				}
				break;
			case OUTCOME:
				if (filterElement.getSelectedValues() != null) {
					List<CaseOutcome> outcomes = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
						outcomes.add((CaseOutcome) tokenizableValue.getValue());
					}
					caseCriteria.outcomes(outcomes);
				}
				break;
			case AGE_INTERVAL_1_YEAR:
			case AGE_INTERVAL_5_YEARS:
			case AGE_INTERVAL_CHILDREN_COARSE:
			case AGE_INTERVAL_CHILDREN_FINE:
			case AGE_INTERVAL_CHILDREN_MEDIUM:
			case AGE_INTERVAL_BASIC:
				if (filterElement.getSelectedValues() != null) {
					List<IntegerRange> ageIntervals = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
						ageIntervals.add((IntegerRange) tokenizableValue.getValue());
					}
					caseCriteria.addAgeIntervals(ageIntervals);

					// Fill age groups if 5 years interval has been selected and case incidence is shown
					if (showCaseIncidence && filterComponent.getSelectedAttribute() == StatisticsCaseAttribute.AGE_INTERVAL_5_YEARS) {
						List<AgeGroup> ageGroups = new ArrayList<>();
						for (IntegerRange ageInterval : ageIntervals) {
							if (ageInterval.getFrom() != null || ageInterval.getTo() != null) {
								ageGroups.add(AgeGroup.getAgeGroupFromIntegerRange(ageInterval));
							}
						}
						caseCriteria.addAgeGroups(ageGroups);
					}
				}
				break;
			case REGION_DISTRICT:
				StatisticsFilterRegionDistrictElement regionDistrictElement = (StatisticsFilterRegionDistrictElement) filterElement;
				if (regionDistrictElement.getSelectedRegions() != null) {
					List<RegionReferenceDto> regions = new ArrayList<>();
					for (TokenizableValue tokenizableValue : regionDistrictElement.getSelectedRegions()) {
						regions.add((RegionReferenceDto) tokenizableValue.getValue());
					}
					caseCriteria.regions(regions);
				}
				if (regionDistrictElement.getSelectedDistricts() != null) {
					List<DistrictReferenceDto> districts = new ArrayList<>();
					for (TokenizableValue tokenizableValue : regionDistrictElement.getSelectedDistricts()) {
						districts.add((DistrictReferenceDto) tokenizableValue.getValue());
					}
					caseCriteria.districts(districts);
				}
				break;
			case REPORTING_USER_ROLE:
				if (filterElement.getSelectedValues() != null) {
					List<UserRole> reportingUserRoles = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
						reportingUserRoles.add((UserRole) tokenizableValue.getValue());
					}
					caseCriteria.reportingUserRoles(reportingUserRoles);
				}
				break;
			default:
				switch (filterComponent.getSelectedSubAttribute()) {
				case YEAR:
					if (filterElement.getSelectedValues() != null) {
						List<Year> years = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							years.add((Year) tokenizableValue.getValue());
						}
						caseCriteria.years(years, filterComponent.getSelectedAttribute());
					}
					break;
				case QUARTER:
					if (filterElement.getSelectedValues() != null) {
						List<Quarter> quarters = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							quarters.add((Quarter) tokenizableValue.getValue());
						}
						caseCriteria.quarters(quarters, filterComponent.getSelectedAttribute());
					}
					break;
				case MONTH:
					if (filterElement.getSelectedValues() != null) {
						List<Month> months = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							months.add((Month) tokenizableValue.getValue());
						}
						caseCriteria.months(months, filterComponent.getSelectedAttribute());
					}
					break;
				case EPI_WEEK:
					if (filterElement.getSelectedValues() != null) {
						List<EpiWeek> epiWeeks = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							epiWeeks.add((EpiWeek) tokenizableValue.getValue());
						}
						caseCriteria.epiWeeks(epiWeeks, filterComponent.getSelectedAttribute());
					}
					break;
				case QUARTER_OF_YEAR:
					if (filterElement.getSelectedValues() != null) {
						List<QuarterOfYear> quartersOfYear = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							quartersOfYear.add((QuarterOfYear) tokenizableValue.getValue());
						}
						caseCriteria.quartersOfYear(quartersOfYear, filterComponent.getSelectedAttribute());
					}
					break;
				case MONTH_OF_YEAR:
					if (filterElement.getSelectedValues() != null) {
						List<MonthOfYear> monthsOfYear = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							monthsOfYear.add((MonthOfYear) tokenizableValue.getValue());
						}
						caseCriteria.monthsOfYear(monthsOfYear, filterComponent.getSelectedAttribute());
					}
					break;
				case EPI_WEEK_OF_YEAR:
					if (filterElement.getSelectedValues() != null) {
						List<EpiWeek> epiWeeksOfYear = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElement.getSelectedValues()) {
							epiWeeksOfYear.add((EpiWeek) tokenizableValue.getValue());
						}
						caseCriteria.epiWeeksOfYear(epiWeeksOfYear, filterComponent.getSelectedAttribute());
					}
					break;
				case DATE_RANGE:
					caseCriteria.dateRange((Date) filterElement.getSelectedValues().get(0).getValue(),
							(Date) filterElement.getSelectedValues().get(1).getValue(),
							filterComponent.getSelectedAttribute());
					break;
				default:
					throw new IllegalArgumentException(filterComponent.getSelectedSubAttribute().toString());
				}
			}
		}
	}

}

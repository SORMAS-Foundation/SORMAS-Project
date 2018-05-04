package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.server.FontAwesome;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapPolygon;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Month;
import de.symeda.sormas.api.MonthOfYear;
import de.symeda.sormas.api.QuarterOfYear;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.statistics.StatisticsFilterElement.TokenizableValue;
import de.symeda.sormas.ui.utils.CssStyles;

public class StatisticsView extends AbstractStatisticsView {

	private static final long serialVersionUID = -4440568319850399685L;

	public static final String VIEW_NAME = "statistics";

	private VerticalLayout statisticsLayout;
	private VerticalLayout filtersLayout;
	private StatisticsDisplayedAttributesElement displayedAttributesElement;
	private VerticalLayout resultsLayout;
	private StatisticsCaseGrid statisticsCaseGrid;
	private List<StatisticsFilterComponent> filterComponents = new ArrayList<>();

	public StatisticsView() {
		super(VIEW_NAME);
		setWidth(100, Unit.PERCENTAGE);

		statisticsLayout = new VerticalLayout();
		statisticsLayout.setMargin(true);
		statisticsLayout.setSpacing(true);
		statisticsLayout.setWidth(100, Unit.PERCENTAGE);

		Label filtersLayoutTitle = new Label("Filters");
		filtersLayoutTitle.setWidthUndefined();
		CssStyles.style(filtersLayoutTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(filtersLayoutTitle);
		
		filtersLayout = new VerticalLayout();
		CssStyles.style(filtersLayout, CssStyles.STATISTICS_TITLE_BOX);
		filtersLayout.setSpacing(true);
		filtersLayout.setWidth(100, Unit.PERCENTAGE);
		Label filtersInfoText = new Label("You can restrict the collected data by defining attribute filters. To add a new filter, click on the + icon at the bottom of\n" + 
				"the list, and to specify the values you want to include, simply type the value in the appropriate filter bar.");
		filtersLayout.addComponent(filtersInfoText);
		filtersLayout.addComponent(createAddFilterButton());
		statisticsLayout.addComponent(filtersLayout);

		Label displayedAttributesTitle = new Label("Visualization");
		displayedAttributesTitle.setWidthUndefined();
		CssStyles.style(displayedAttributesTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(displayedAttributesTitle);
		
		displayedAttributesElement = new StatisticsDisplayedAttributesElement();
		CssStyles.style(displayedAttributesElement, CssStyles.STATISTICS_TITLE_BOX);
		statisticsLayout.addComponent(displayedAttributesElement);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		{
			Button generateButton = new Button("Generate");
			CssStyles.style(generateButton, ValoTheme.BUTTON_PRIMARY);
			generateButton.addClickListener(e -> {
				resultsLayout.removeAllComponents();
				switch (displayedAttributesElement.getSelectedVisualizationType()) {
				case TABLE:
					generateTable();
					break;
				case REGIONS_MAP:
				case DISTRICTS_MAP:
					generateMap();
					break;
				default:
					throw new IllegalArgumentException(displayedAttributesElement.getSelectedVisualizationType().toString());
				}
			});
			buttonLayout.addComponent(generateButton);

			Button resetFiltersButton = new Button("Reset filters");
			resetFiltersButton.addClickListener(e -> {
				filtersLayout.removeAllComponents();
				filtersLayout.addComponent(filtersInfoText);
				filtersLayout.addComponent(createAddFilterButton());
				if (statisticsCaseGrid != null) {
					resultsLayout.removeComponent(statisticsCaseGrid);
					statisticsCaseGrid = null;
				}
			});
			buttonLayout.addComponent(resetFiltersButton);
		}

		statisticsLayout.addComponent(buttonLayout);

		Label resultsLayoutTitle = new Label("Results");
		resultsLayoutTitle.setWidthUndefined();
		CssStyles.style(resultsLayoutTitle, CssStyles.STATISTICS_TITLE);
		statisticsLayout.addComponent(resultsLayoutTitle);
		
		resultsLayout = new VerticalLayout();
		resultsLayout.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(resultsLayout, CssStyles.STATISTICS_TITLE_BOX);
		statisticsLayout.addComponent(resultsLayout);
		
		addComponent(statisticsLayout);
	}

	private List<Object[]> generateStatistics() {
		StatisticsCaseCriteria caseCriteria = new StatisticsCaseCriteria();

		for (StatisticsFilterComponent filterComponent : filterComponents) {
			Map<Object, StatisticsFilterElement> filterElements = filterComponent.getFilterElements();
			switch (filterComponent.getSelectedAttribute()) {
			case SEX:
				if (filterElements.get(StatisticsCaseAttribute.SEX).getSelectedValues() != null) {
					List<Sex> sexes = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseAttribute.SEX).getSelectedValues()) {
						sexes.add((Sex) tokenizableValue.getValue());
					}
					caseCriteria.sexes(sexes);
				}
				break;
			case DISEASE:
				if (filterElements.get(StatisticsCaseAttribute.DISEASE).getSelectedValues() != null) {
					List<Disease> diseases = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseAttribute.DISEASE).getSelectedValues()) {
						diseases.add((Disease) tokenizableValue.getValue());
					}
					caseCriteria.diseases(diseases);
				}
				break;
			case CLASSIFICATION:
				if (filterElements.get(StatisticsCaseAttribute.CLASSIFICATION).getSelectedValues() != null) {
					List<CaseClassification> classifications = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseAttribute.CLASSIFICATION).getSelectedValues()) {
						classifications.add((CaseClassification) tokenizableValue.getValue());
					}
					caseCriteria.classifications(classifications);
				}
				break;	
			case OUTCOME:
				if (filterElements.get(StatisticsCaseAttribute.OUTCOME).getSelectedValues() != null) {
					List<CaseOutcome> outcomes = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseAttribute.OUTCOME).getSelectedValues()) {
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
				if (filterElements.get(filterComponent.getSelectedAttribute()).getSelectedValues() != null) {
					List<IntegerRange> ageIntervals = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElements.get(filterComponent.getSelectedAttribute()).getSelectedValues()) {
						ageIntervals.add((IntegerRange) tokenizableValue.getValue());
					}
					caseCriteria.addAgeIntervals(ageIntervals);
				}
				break;
			case REGION_DISTRICT:
				if (filterElements.get(StatisticsCaseSubAttribute.REGION).getSelectedValues() != null) {
					List<RegionReferenceDto> regions = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseSubAttribute.REGION).getSelectedValues()) {
						regions.add((RegionReferenceDto) tokenizableValue.getValue());
					}
					caseCriteria.regions(regions);
				}
				if (filterElements.get(StatisticsCaseSubAttribute.DISTRICT).getSelectedValues() != null) {
					List<DistrictReferenceDto> districts = new ArrayList<>();
					for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseSubAttribute.DISTRICT).getSelectedValues()) {
						districts.add((DistrictReferenceDto) tokenizableValue.getValue());
					}
					caseCriteria.districts(districts);
				}
				break;
			default:
				switch (filterComponent.getSelectedSubAttribute()) {
				case YEAR:
					if (filterElements.get(StatisticsCaseSubAttribute.YEAR).getSelectedValues() != null) {
						List<Integer> years = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseSubAttribute.YEAR).getSelectedValues()) {
							years.add((Integer) tokenizableValue.getValue());
						}
						caseCriteria.years(years, filterComponent.getSelectedAttribute());
					}
					break;
				case QUARTER:
					if (filterElements.get(StatisticsCaseSubAttribute.QUARTER).getSelectedValues() != null) {
						List<Integer> quarters = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseSubAttribute.QUARTER).getSelectedValues()) {
							quarters.add((Integer) tokenizableValue.getValue());
						}
						caseCriteria.quarters(quarters, filterComponent.getSelectedAttribute());
					}
					break;
				case MONTH:
					if (filterElements.get(StatisticsCaseSubAttribute.MONTH).getSelectedValues() != null) {
						List<Month> months = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseSubAttribute.MONTH).getSelectedValues()) {
							months.add((Month) tokenizableValue.getValue());
						}
						caseCriteria.months(months, filterComponent.getSelectedAttribute());
					}
					break;
				case EPI_WEEK:
					if (filterElements.get(StatisticsCaseSubAttribute.EPI_WEEK).getSelectedValues() != null) {
						List<EpiWeek> epiWeeks = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseSubAttribute.EPI_WEEK).getSelectedValues()) {
							epiWeeks.add((EpiWeek) tokenizableValue.getValue());
						}
						caseCriteria.epiWeeks(epiWeeks, filterComponent.getSelectedAttribute());
					}
					break;
				case QUARTER_OF_YEAR:
					if (filterElements.get(StatisticsCaseSubAttribute.QUARTER_OF_YEAR).getSelectedValues() != null) {
						List<QuarterOfYear> quartersOfYear = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseSubAttribute.QUARTER_OF_YEAR).getSelectedValues()) {
							quartersOfYear.add((QuarterOfYear) tokenizableValue.getValue());
						}
						caseCriteria.quartersOfYear(quartersOfYear, filterComponent.getSelectedAttribute());
					}
					break;
				case MONTH_OF_YEAR:
					if (filterElements.get(StatisticsCaseSubAttribute.MONTH_OF_YEAR).getSelectedValues() != null) {
						List<MonthOfYear> monthsOfYear = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseSubAttribute.MONTH_OF_YEAR).getSelectedValues()) {
							monthsOfYear.add((MonthOfYear) tokenizableValue.getValue());
						}
						caseCriteria.monthsOfYear(monthsOfYear, filterComponent.getSelectedAttribute());
					}
					break;
				case EPI_WEEK_OF_YEAR:
					if (filterElements.get(StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR).getSelectedValues() != null) {
						List<EpiWeek> epiWeeksOfYear = new ArrayList<>();
						for (TokenizableValue tokenizableValue : filterElements.get(StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR).getSelectedValues()) {
							epiWeeksOfYear.add((EpiWeek) tokenizableValue.getValue());
						}
						caseCriteria.epiWeeksOfYear(epiWeeksOfYear, filterComponent.getSelectedAttribute());
					}
					break;
				case DATE_RANGE:
					caseCriteria.dateRange((Date) filterElements.get(StatisticsCaseSubAttribute.DATE_RANGE).getSelectedValues().get(0).getValue(), 
							(Date) filterElements.get(StatisticsCaseSubAttribute.DATE_RANGE).getSelectedValues().get(1).getValue(), 
							filterComponent.getSelectedAttribute());
					break;
				default:
					throw new IllegalArgumentException(filterComponent.getSelectedSubAttribute().toString());
				}
			}
		}

		List<Object[]> resultData;
		switch (displayedAttributesElement.getSelectedVisualizationType()) {
		case REGIONS_MAP:
			resultData = FacadeProvider.getCaseFacade().queryCaseCount(caseCriteria, 
					StatisticsCaseAttribute.REGION_DISTRICT, StatisticsCaseSubAttribute.REGION, null, null);
			break;
		case DISTRICTS_MAP:
			resultData = FacadeProvider.getCaseFacade().queryCaseCount(caseCriteria, 
					StatisticsCaseAttribute.REGION_DISTRICT, StatisticsCaseSubAttribute.DISTRICT, null, null);
			break;
		default:
			resultData = FacadeProvider.getCaseFacade().queryCaseCount(caseCriteria, 
					displayedAttributesElement.getSelectedRowsAttribute(), displayedAttributesElement.getSelectedRowsSubAttribute(), 
					displayedAttributesElement.getSelectedColumnsAttribute(), displayedAttributesElement.getSelectedColumnsSubAttribute());
			break;
		}

		return resultData;
	}
	
	public void generateTable() {
		
		List<Object[]> resultData = generateStatistics();
		statisticsCaseGrid = new StatisticsCaseGrid(displayedAttributesElement.getSelectedRowsAttribute(), displayedAttributesElement.getSelectedRowsSubAttribute(), displayedAttributesElement.getSelectedColumnsAttribute(), displayedAttributesElement.getSelectedColumnsSubAttribute(), resultData);
		statisticsCaseGrid.setWidth(100, Unit.PERCENTAGE);
		resultsLayout.addComponent(statisticsCaseGrid);
	}
	
	public void generateMap() {
		
		GoogleMap map = new GoogleMap("AIzaSyAaJpN8a_NhEU-02-t5uVi02cAaZtKafkw", null, null);
		map.addStyleName("no-tiles");
		map.setWidth(100, Unit.PERCENTAGE);
		map.setHeight(580, Unit.PIXELS);
		map.setMinZoom(4);
		map.setMaxZoom(16);
		map.setZoom(6);
		GeoLatLon mapCenter = FacadeProvider.getGeoShapeProvider().getCenterOfAllRegions();
		map.setCenter(new LatLon(mapCenter.getLon(), mapCenter.getLat()));

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllAsReference();

		// draw outlines of all regions
		for (RegionReferenceDto region : regions) {

			GeoLatLon[][] regionShape = FacadeProvider.getGeoShapeProvider().getRegionShape(region);
			if (regionShape == null) {
				continue;
			}

			GoogleMapPolygon[] regionPolygons = new GoogleMapPolygon[regionShape.length];
			for (int part = 0; part<regionShape.length; part++) {
				GeoLatLon[] regionShapePart = regionShape[part];
				GoogleMapPolygon polygon = new GoogleMapPolygon(
						Arrays.stream(regionShapePart)
						.map(c -> new LatLon(c.getLat(), c.getLon()))
						.collect(Collectors.toList()));

				polygon.setStrokeOpacity(0.5);
				polygon.setFillOpacity(0);
				regionPolygons[part] = polygon;
				map.addPolygonOverlay(polygon);
			}
		}

		List<Object[]> resultData = generateStatistics();
		resultData.sort((a,b) -> {
			return Long.compare((Long)a[0], (Long)b[0]);
		});

		Long valuesLowerQuartile = resultData.size() > 0 ? (Long)resultData.get((int) (resultData.size()  * 0.25))[0] : null;
		Long valuesMedian = resultData.size() > 0 ? (Long)resultData.get((int) (resultData.size() * 0.5))[0] : null;
		Long valuesUpperQuartile = resultData.size() > 0 ? (Long)resultData.get((int) (resultData.size() * 0.75))[0] : null;

		// Draw relevant district fills
		for (Object[] resultRow : resultData) {
			
			String shapeUuid = (String)resultRow[1];
			Long shapeValue = (Long)resultRow[0];
			GeoLatLon[][] shape;
			if (displayedAttributesElement.getSelectedVisualizationType() == StatisticsVisualizationType.REGIONS_MAP) {
				shape = FacadeProvider.getGeoShapeProvider().getRegionShape(new RegionReferenceDto(shapeUuid));
			} else if (displayedAttributesElement.getSelectedVisualizationType() == StatisticsVisualizationType.DISTRICTS_MAP) {
				shape = FacadeProvider.getGeoShapeProvider().getDistrictShape(new DistrictReferenceDto(shapeUuid));
			} else {
				throw new IllegalArgumentException(displayedAttributesElement.getSelectedVisualizationType().toString());
			}
			
			if (shape == null) {
				continue;
			}

			GoogleMapPolygon[] shapePolygons = new GoogleMapPolygon[shape.length];
			for (int part = 0; part < shape.length; part++) {
				GeoLatLon[] shapePart = shape[part];
				GoogleMapPolygon polygon = new GoogleMapPolygon(
						Arrays.stream(shapePart)
						.map(c -> new LatLon(c.getLat(), c.getLon()))
						.collect(Collectors.toList()));

				polygon.setStrokeOpacity(0);

				if (shapeValue.compareTo(0l) == 0) {
					polygon.setFillOpacity(0);
				} else if (shapeValue.compareTo(valuesLowerQuartile) <= 0) {
					polygon.setFillColor("#FEDD6C");
					polygon.setFillOpacity(0.5);
				} else if (shapeValue.compareTo(valuesMedian) <= 0) {
					polygon.setFillColor("#FDBF44");
					polygon.setFillOpacity(0.5);
				} else if (shapeValue.compareTo(valuesUpperQuartile) <= 0) {
					polygon.setFillColor("#F47B20");
					polygon.setFillOpacity(0.5);							
				} else {
					polygon.setFillColor("#ED1B24");
					polygon.setFillOpacity(0.5);
				}

//				if (caseMeasure == CaseMeasure.CASE_INCIDENCE) {
//					if (district.getPopulation() == null || district.getPopulation() <= 0) {
//						// grey when region has no population data
//						emptyPopulationDistrictPresent = true;
//						polygon.setFillColor("#999999");
//						polygon.setFillOpacity(0.5);
//					}
//				}

				shapePolygons[part] = polygon;
				map.addPolygonOverlay(polygon);
			}
		}
		
		resultsLayout.addComponent(map);
		resultsLayout.setExpandRatio(map, 1);
	}

	private Button createAddFilterButton() {
		Button addFilterButton = new Button(FontAwesome.PLUS);
		CssStyles.style(addFilterButton, ValoTheme.BUTTON_PRIMARY, CssStyles.FORCE_CAPTION);
		addFilterButton.addClickListener(e -> {
			filtersLayout.removeComponent(addFilterButton);
			filtersLayout.addComponent(createFilterComponentLayout());
			filtersLayout.addComponent(createAddFilterButton());
		});

		return addFilterButton;
	}

	private HorizontalLayout createFilterComponentLayout() {
		HorizontalLayout filterComponentLayout = new HorizontalLayout();
		filterComponentLayout.setSpacing(true);
		filterComponentLayout.setWidth(100, Unit.PERCENTAGE);

		StatisticsFilterComponent filterComponent = new StatisticsFilterComponent();

		Button removeFilterButton = new Button(FontAwesome.TIMES);
		CssStyles.style(removeFilterButton, ValoTheme.BUTTON_PRIMARY, CssStyles.FORCE_CAPTION);
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

}

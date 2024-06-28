package de.symeda.sormas.ui.dashboard.surveillance.components.disease.tile;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.ColumnResizeMode;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.HtmlRenderer;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RegionalDiseaseBurdenGrid extends Grid {

	private final DashboardDataProvider dashboardDataProvider;
	private final List<RegionDto> regionDtoList;


	Grid.Column regionDistrictColumn;
	Grid.Column totalColumn;

	Grid.Column totalCountColumn;

	Grid.Column activeCaseColumn;

	Grid.Column activeCaseCountColumn;

	Grid.Column recoveredCasesColumn;
	Grid.Column recoveredCasesCountColumn;

	Grid.Column deathColumn;
	Grid.Column deathCountColumn;

	Grid.Column otherColumn;
	Grid.Column otherCountColumn;

	private final DecimalFormat decimalFormat;

	public RegionalDiseaseBurdenGrid(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		regionDtoList = FacadeProvider.getRegionFacade().getAllRegion();


		setCaption(I18nProperties.getCaption(Captions.dashboardRegionalDiseaseBurden));

		decimalFormat = new DecimalFormat("0.00");
		setColumnReorderingAllowed(true);
		setWidthFull();
		setWidth(1000, Unit.PIXELS);

		setColumns(
				DiseaseBurdenDto.CASES_REGION,
				DiseaseBurdenDto.CASES_COUNT_TOTAL,
				DiseaseBurdenDto.CASES_TOTAL,
				DiseaseBurdenDto.ACTIVE_COUNT_CASE,
				DiseaseBurdenDto.ACTIVE_CASE,
				DiseaseBurdenDto.RECOVERED_COUNT_CASES,
				DiseaseBurdenDto.RECOVERED_CASES,
				DiseaseBurdenDto.DEATH_COUNT,
				DiseaseBurdenDto.DEATH,
				DiseaseBurdenDto.OTHER_COUNT,
				DiseaseBurdenDto.OTHER
		);

		regionDistrictColumn = getColumn(DiseaseBurdenDto.CASES_REGION);

		totalCountColumn = getColumn(DiseaseBurdenDto.CASES_COUNT_TOTAL);

		totalColumn = getColumn(DiseaseBurdenDto.CASES_TOTAL);

		activeCaseCountColumn = getColumn(DiseaseBurdenDto.ACTIVE_COUNT_CASE);

		activeCaseColumn = getColumn(DiseaseBurdenDto.ACTIVE_CASE);

		recoveredCasesCountColumn = getColumn(DiseaseBurdenDto.RECOVERED_COUNT_CASES);

		recoveredCasesColumn = getColumn(DiseaseBurdenDto.RECOVERED_CASES);

		deathCountColumn = getColumn(DiseaseBurdenDto.DEATH_COUNT);

		deathColumn = getColumn(DiseaseBurdenDto.DEATH);

		otherCountColumn = getColumn(DiseaseBurdenDto.OTHER_COUNT);

		otherColumn = getColumn(DiseaseBurdenDto.OTHER);

	}

	public void refresh(){
		setColumnResizeMode(ColumnResizeMode.ANIMATED);
		setSelectionMode(Grid.SelectionMode.NONE);

		reload();
	}

	public void reload() {

		List<DiseaseBurdenDto> diseaseBurdenDtoList = new ArrayList<>();

		Long casePercental = dashboardDataProvider.getDiseaseBurdenDetail().getCaseCount();

		regionDistrictColumn.setWidth(100);
		regionDistrictColumn.setHeaderCaption("REGION NAME");

		totalCountColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		totalColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		totalColumn.setHeaderCaption("TOTAL %");

		activeCaseCountColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		activeCaseColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		activeCaseColumn.setHeaderCaption("ACTIVE CASES %");

		recoveredCasesCountColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		recoveredCasesCountColumn.setHeaderCaption("RECOVER COUNT");
		recoveredCasesColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		recoveredCasesColumn.setHeaderCaption("RECOVER CASES %");


		deathCountColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		deathColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		deathColumn.setHeaderCaption("DEATH CASES %");

		otherCountColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		otherColumn.setRenderer(new HtmlRenderer()).setWidth(100);

		otherColumn.setHeaderCaption("OTHER CASES %");

		if(dashboardDataProvider.getRegion()!=null) {

			regionDistrictColumn.setHeaderCaption("DISTRICT NAME");

			setCaption(I18nProperties.getCaption(Captions.dashboardDistrictDiseaseBurden));

			String regionUuid=dashboardDataProvider.getRegion().getUuid();

			List<DistrictReferenceDto> districtDtoList = FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionUuid);

			for (DistrictReferenceDto districtDto : districtDtoList){

				DiseaseBurdenDto diseaseBurdenDto = FacadeProvider.getDiseaseFacade().getDiseaseGridForDashboard(
						null,
						districtDto,
						dashboardDataProvider.getDisease(),
						dashboardDataProvider.getFromDate(),
						dashboardDataProvider.getToDate(),
						dashboardDataProvider.getPreviousFromDate(),
						dashboardDataProvider.getPreviousToDate(),
						dashboardDataProvider.getNewCaseDateType(),
						dashboardDataProvider.getCaseClassification()
				);


				String total = diseaseBurdenDto.getTotal();

				String activeCases = diseaseBurdenDto.getActiveCases();

				String recovered = diseaseBurdenDto.getRecovered();

				String deaths = diseaseBurdenDto.getDeaths();

				String other = diseaseBurdenDto.getOther();

				RegionDto regionDto = new RegionDto();

				regionDto.setName(districtDto.getCaption());

				diseaseBurdenDto.setRegion(regionDto);

				diseaseBurdenDto.setTotal(makeDIvs(Long.parseLong(total), casePercental, "#5a95f4bf","#2f7df9"));

				diseaseBurdenDto.setTotalCount(makeDIvsCount(total));

				diseaseBurdenDto.setActiveCases(makeDIvs(Long.parseLong(activeCases), casePercental,  "#feba0199", "#dfa507"));

				diseaseBurdenDto.setActiveCount(makeDIvsCount(activeCases));

				diseaseBurdenDto.setRecovered(makeDIvs(Long.parseLong(recovered), casePercental, "#00e0a19c", "#038d66"));

				diseaseBurdenDto.setRecoveredCount(makeDIvsCount(recovered));

				diseaseBurdenDto.setDeaths(makeDIvs(Long.parseLong(deaths), casePercental,"#FFAEAE", "#FF4040"));

				diseaseBurdenDto.setDeathsCount(makeDIvsCount(deaths));

				diseaseBurdenDto.setOther(makeDIvs(Long.parseLong(other), casePercental,"#bf8678ba", "#91675d"));

				diseaseBurdenDto.setOtherCount(makeDIvsCount(other));

				diseaseBurdenDtoList.add(diseaseBurdenDto);
			}
		}else {

			for (RegionDto regionDto : regionDtoList){
				DiseaseBurdenDto diseaseBurdenDto = FacadeProvider.getDiseaseFacade().getDiseaseGridForDashboard(
						regionDto.toReference(),
						null,
						dashboardDataProvider.getDisease(),
						dashboardDataProvider.getFromDate(),
						dashboardDataProvider.getToDate(),
						dashboardDataProvider.getPreviousFromDate(),
						dashboardDataProvider.getPreviousToDate(),
						dashboardDataProvider.getNewCaseDateType(),
						dashboardDataProvider.getCaseClassification()
				);

				String total = diseaseBurdenDto.getTotal();

				String activeCases = diseaseBurdenDto.getActiveCases();

				String recovered = diseaseBurdenDto.getRecovered();

				String deaths = diseaseBurdenDto.getDeaths();

				String other = diseaseBurdenDto.getOther();

				diseaseBurdenDto.setTotal(makeDIvs(Long.parseLong(total), casePercental, "#5a95f4bf","#2f7df9"));

				diseaseBurdenDto.setTotalCount(makeDIvsCount(total));

				diseaseBurdenDto.setActiveCases(makeDIvs(Long.parseLong(activeCases), casePercental,  "#feba0199", "#dfa507"));

				diseaseBurdenDto.setActiveCount(makeDIvsCount(activeCases));

				diseaseBurdenDto.setRecovered(makeDIvs(Long.parseLong(recovered), casePercental, "#00e0a19c", "#038d66"));

				diseaseBurdenDto.setRecoveredCount(makeDIvsCount(recovered));

				diseaseBurdenDto.setDeaths(makeDIvs(Long.parseLong(deaths), casePercental,"#FFAEAE", "#FF4040"));

				diseaseBurdenDto.setDeathsCount(makeDIvsCount(deaths));

				diseaseBurdenDto.setOther(makeDIvs(Long.parseLong(other), casePercental,"#bf8678ba", "#91675d"));

				diseaseBurdenDto.setOtherCount(makeDIvsCount(other));

				diseaseBurdenDtoList.add(diseaseBurdenDto);
			}
		}

		BeanItemContainer<DiseaseBurdenDto> container = new BeanItemContainer<>(DiseaseBurdenDto.class, diseaseBurdenDtoList);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);
	}

	public String makeDIvs(long number, long total, String lightColor, String deepColor) {

		String  endDiv = " </div>";
		String divWithStyleAttr="<div style='";

		String mainStyle = "text-align: center; height:15px; width: 100%; background:"+lightColor;
		String progressPercentStyle = "position: absolute; width: 8%; color: #ffffff; font-weight: 700; margin: -1px;";
		double regionalTotal = (double)number/total * 100;

		String textColor = "#ffffff";
		String div ="div";
		String style = "height:15px; width:"+ decimalFormat.format(regionalTotal)+"%; color:"+textColor+"; font-size: 10px;"+"background:"+deepColor;

		if (number == 0 && total == 0) {
			regionalTotal=0.0;
			return divWithStyleAttr+mainStyle+"; font-size: 11px; font-weight: 700; color:"+textColor+" '>"
					+ decimalFormat.format(regionalTotal)+"% "+endDiv
					+ element( div, style, null) + endDiv;
		}

		return divWithStyleAttr+mainStyle+"'><div style='"+progressPercentStyle+"'>"
				+ decimalFormat.format(regionalTotal)+"% "+endDiv
				+ element(div , style, null) + endDiv;

	}

	public String makeDIvsCount(String num) {
		String  endDiv = " </div>";
		String divWithStyleAttr="<div style='";

		String mainStyle = "text-align: center; height:15px; width: 100%; ";
		String regionalTotal = num;

		return divWithStyleAttr+mainStyle+"'>"
				+ regionalTotal+endDiv;
	}

	public String element(String type, String style, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(type);
		if (style != null) {
			sb.append(" style='").append(style).append("'");
		}
		sb.append(">");

		if (content != null)
			sb.append(content);

		sb.append("</").append(type).append(">");

		return sb.toString();

	}

}

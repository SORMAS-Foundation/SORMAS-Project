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
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RegionalDiseaseBurdenGrid extends Grid {

	private final DashboardDataProvider dashboardDataProvider;
	private final List<RegionDto> regionDtoList;
	Grid.Column regionColumn;
	Grid.Column totalColumn;
	Grid.Column activeCaseColumn;
	Grid.Column recoveredCasesColumn;
	Grid.Column deathColumn;
	private final DecimalFormat decimalFormat;

	public RegionalDiseaseBurdenGrid(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		regionDtoList = FacadeProvider.getRegionFacade().getAllRegion();
		setCaption(I18nProperties.getCaption(Captions.dashboardRegionalDiseaseBurden));

		decimalFormat = new DecimalFormat("0.00");
		setColumnReorderingAllowed(true);
		setWidthFull();
		setWidth(800, Unit.PIXELS);

		setColumns(
				DiseaseBurdenDto.CASES_REGION,
				DiseaseBurdenDto.CASES_TOTAL,
				DiseaseBurdenDto.ACTIVE_CASE,
				DiseaseBurdenDto.RECOVERED_CASES,
				DiseaseBurdenDto.DEATH
		);

		regionColumn = getColumn(DiseaseBurdenDto.CASES_REGION);
		totalColumn = getColumn(DiseaseBurdenDto.CASES_TOTAL);
		activeCaseColumn = getColumn(DiseaseBurdenDto.ACTIVE_CASE);
		recoveredCasesColumn = getColumn(DiseaseBurdenDto.RECOVERED_CASES);
		deathColumn = getColumn(DiseaseBurdenDto.DEATH);
	}

	public void refresh(){
		setColumnResizeMode(ColumnResizeMode.ANIMATED);
		setSelectionMode(Grid.SelectionMode.NONE);

		reload();
	}

	public void reload() {
		List<DiseaseBurdenDto> diseaseBurdenDtoList = new ArrayList<>();
		Long casePercental = dashboardDataProvider.getDiseaseBurdenDetail().getCaseCount();

		regionColumn.setWidth(100);
		totalColumn.setRenderer(new HtmlRenderer()).setWidth(180);
		activeCaseColumn.setRenderer(new HtmlRenderer()).setWidth(180);
		recoveredCasesColumn.setRenderer(new HtmlRenderer()).setWidth(180);
		deathColumn.setRenderer(new HtmlRenderer()).setWidth(180);

		for (RegionDto regionDto : regionDtoList){
			DiseaseBurdenDto diseaseBurdenDto = FacadeProvider.getDiseaseFacade().getDiseaseGridForDashboard(
								regionDto.toReference(),
								null,
								dashboardDataProvider.getDisease(),
								dashboardDataProvider.getFromDate(),
								dashboardDataProvider.getToDate(),
								dashboardDataProvider.getPreviousFromDate(),
								dashboardDataProvider.getPreviousToDate());

			diseaseBurdenDto.setTotal(makeDIvs(Long.parseLong(diseaseBurdenDto.getTotal()), casePercental, "#5a95f4bf","#2f7df9"));
			diseaseBurdenDto.setActiveCases(makeDIvs(Long.parseLong(diseaseBurdenDto.getActiveCases()), casePercental,  "#feba0199", "#dfa507"));
			diseaseBurdenDto.setRecovered(makeDIvs(Long.parseLong(diseaseBurdenDto.getRecovered()), casePercental, "#00e0a19c", "#038d66"));
			diseaseBurdenDto.setDeaths(makeDIvs(Long.parseLong(diseaseBurdenDto.getDeaths()), casePercental,"#bf8678ba", "#91675d"));

			diseaseBurdenDtoList.add(diseaseBurdenDto);
		}
		BeanItemContainer<DiseaseBurdenDto> container = new BeanItemContainer<DiseaseBurdenDto>(DiseaseBurdenDto.class, diseaseBurdenDtoList);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);
	}

	public String makeDIvs(long number, long total, String lightColor, String deepColor) {

		if (number == 0 && total == 0)
			return ("0.0%");

		String mainStyle = "text-align: center; height:15px; width: 100%; background:"+lightColor;
		String progressPercentStyle = "position: absolute; width: 20%; color: #ffffff; font-weight: 700; margin: -1px;";
		double regionalTotal = (double)number/total * 100;

//		String textColor = regionalTotal > 10 ? "#ffffff" : "#000000";
		String textColor = "#ffffff";

		String style = "height:15px; width:"+ decimalFormat.format(regionalTotal)+"%; color:"+textColor+"; font-size: 10px;"+"background:"+deepColor;
		String content = decimalFormat.format(regionalTotal) +"%";

//		return "<div style="+mainStyle+">" + "</div>";
		return "<div style='"+mainStyle+"'><div style='"+progressPercentStyle+"'>"
				+ decimalFormat.format(regionalTotal)+"% </div>"
				+ element("div" , style, null) + "</div>";

//		return "<div style='"+mainStyle+"'>" + element("div" , style, content) + "</div>";
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

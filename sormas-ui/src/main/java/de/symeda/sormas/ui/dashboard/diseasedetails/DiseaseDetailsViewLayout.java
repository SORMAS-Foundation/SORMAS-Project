package de.symeda.sormas.ui.dashboard.diseasedetails;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class DiseaseDetailsViewLayout extends CustomLayout {

	private static final long serialVersionUID = 6582975657305031105L;
	private static final String EXTEND_BUTTONS_LOC = "extendButtons";

	private static final String CARD = "card";
	private static final String GRID_TABLE = "table";
	private static final String MAP = "map";

	private final DashboardDataProvider dashboardDataProvider;
	private DiseaseDetailsComponent diseaseDetailsComponent;
	private RegionalDiseaseBurdenGrid regionalDiseaseBurdenGrid;

	public DiseaseDetailsViewLayout(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
		setTemplateContents(
			LayoutUtil.fluidRow(
				LayoutUtil.fluidColumnLoc(3, 1, 0, 12, CARD),
				LayoutUtil.fluidColumnLoc(8, 0, 0, 12, GRID_TABLE),
				LayoutUtil.fluidColumnLoc(12, 0, 12, 6, MAP)));
//				NOTE span is width size, offset is margin left

		diseaseDetailsComponent = new DiseaseDetailsComponent(dashboardDataProvider);
		regionalDiseaseBurdenGrid = new RegionalDiseaseBurdenGrid(dashboardDataProvider);
		reload();
	}

	public void refresh(){
		diseaseDetailsComponent.refresh();
		regionalDiseaseBurdenGrid.refresh();
	}

	public void reload() {
//		Disease Card layout
//		System.out.println("The disease clicked in is....:"+ diseasesBurden.getDisease());
//		DiseaseDetailsComponent diseaseCardLayout = new DiseaseDetailsComponent(dashboardDataProvider);
//		DiseaseBurdenDto diseasesBurden = dashboardDataProvider.getDiseaseBurdenDetail();
//		DiseaseDetailsComponent diseaseCardLayout = new DiseaseDetailsComponent(diseasesBurden);
//		addComponent(diseaseCardLayout, CARD);
//		Label label = new Label("Outbreak..");
//		diseaseCardLayout.addComponent(label);
//		addComponent(diseaseCardLayout, CARD);
//		DiseaseDetailsComponent diseasesBurden = dashboardDataProvider.getDiseaseBurdenDetail();
//		DiseaseDetailsComponent diseasesBurden = DiseaseBurdenDto(
//			Disease.AFP,
//			Long.valueOf(120),
//			Long.valueOf(350),
//			Long.valueOf(100),
//			Long.valueOf(30),
//			Long.valueOf(50),
//			"Some District here",
//			2145,
//			null,
//			null,
//			1154);

		HorizontalLayout diseaseCardLayout = new HorizontalLayout();
		diseaseCardLayout.addComponent(diseaseDetailsComponent);
		addComponent(diseaseCardLayout, CARD);

//		Grid card layout
		HorizontalLayout diseaseGridLayout = new HorizontalLayout();
		diseaseGridLayout.addComponent(regionalDiseaseBurdenGrid);
		addComponent(diseaseGridLayout, GRID_TABLE);

//		Map layout 
//		HorizontalLayout mapLayout = new HorizontalLayout();
//		mapLayout.addComponent(mapVerticalLayout());
//		addComponent(mapLayout, MAP);
	}

	private HorizontalLayout cardLayout(DiseaseBurdenDto diseaseBurdenDto) {
		HorizontalLayout diseaseCaseCountLayout = new HorizontalLayout();
		Label title = new Label(diseaseBurdenDto.getDisease().getName());
		diseaseCaseCountLayout.addComponent(title);

		HorizontalLayout caseCountLayout = new HorizontalLayout();
		Label totalCaseCount = new Label(diseaseBurdenDto.getCaseCount().toString());
		CssStyles.style(totalCaseCount, CssStyles.H2, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_NONE);
		caseCountLayout.addComponent(totalCaseCount);

		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(diseaseCaseCountLayout);
		layout.addComponent(totalCaseCount);

		return layout;
	}

	private HorizontalLayout gridTableLayout() {
		HorizontalLayout tableLayout = new HorizontalLayout();
		tableLayout.setMargin(false);
		tableLayout.setSpacing(false);

//		Label title = new Label(I18nProperties.getCaption(Strings.DiseaseNetworkDiagram_heading));
		Label labelGrid = new Label("Grid Table here");

		tableLayout.addComponent(labelGrid);
		addComponent(tableLayout);
		return tableLayout;
	}

	private HorizontalLayout mapVerticalLayout() {
		HorizontalLayout mapLayout = new HorizontalLayout();
		mapLayout.setMargin(false);
		mapLayout.setSpacing(false);
		DashboardMapComponent dashboardMapComponent = new DashboardMapComponent(dashboardDataProvider);
		dashboardMapComponent.addStyleName(CssStyles.SIDE_COMPONENT);

		mapLayout.addComponent(dashboardMapComponent);

		addComponent(mapLayout);
		return mapLayout;
	}

}

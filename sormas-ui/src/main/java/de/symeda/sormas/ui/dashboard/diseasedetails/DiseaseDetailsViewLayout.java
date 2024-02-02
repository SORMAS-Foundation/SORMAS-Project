package de.symeda.sormas.ui.dashboard.diseasedetails;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;

import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.dashboard.map.DashboardMapComponent;
import de.symeda.sormas.ui.dashboard.surveillance.components.disease.tile.RegionalDiseaseBurdenGrid;
import de.symeda.sormas.ui.dashboard.map.MapCaseDisplayMode;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.LayoutUtil;

import java.util.function.Consumer;

public class DiseaseDetailsViewLayout extends CustomLayout {

	private static final long serialVersionUID = 6582975657305031105L;

	private static final String CARD = "card";
	private static final String GRID_TABLE = "table";
	private static final String GRID_VIEW_MORE = "viewMore";
	private static final String MAP = "map";

	private final DashboardDataProvider dashboardDataProvider;
	private final DiseaseDetailsComponent diseaseDetailsComponent;
	private final RegionalDiseaseBurdenGrid regionalDiseaseBurdenGrid;
	private final DashboardMapComponent dashboardMapComponent;

	private boolean isShowMore;
	private Button button;
	public DiseaseDetailsViewLayout(DashboardDataProvider dashboardDataProvider) {
		this.dashboardDataProvider = dashboardDataProvider;
//		this.setWidthFull();
//		this.setSizeFull();
		setWidth(100, Unit.PERCENTAGE);
		setTemplateContents(
			LayoutUtil.fluidRow(
				LayoutUtil.fluidColumnLoc(2, 0, 0, 0, CARD),
				LayoutUtil.fluidColumnLoc(7, 1, 0, 0, GRID_TABLE),
				LayoutUtil.fluidColumnLoc(6, 4, 0, 0, GRID_VIEW_MORE),
				LayoutUtil.fluidColumnLoc(12, 0, 5, 0, MAP)

					)
		); //NOTE span is heigth size, offset is row down

		isShowMore = false;

		diseaseDetailsComponent = new DiseaseDetailsComponent(dashboardDataProvider);
		regionalDiseaseBurdenGrid = new RegionalDiseaseBurdenGrid(dashboardDataProvider);
		dashboardMapComponent = new DashboardMapComponent(dashboardDataProvider);

		reload();
	}

	public void refresh() {
		diseaseDetailsComponent.removeAllComponents();
		diseaseDetailsComponent.refresh();
		regionalDiseaseBurdenGrid.refresh();
		dashboardMapComponent.refreshMap();
	}

	public void reload() {
//		Disease Card Layout
		HorizontalLayout diseaseCardLayout = new HorizontalLayout();
		diseaseCardLayout.setWidth(50, Unit.PERCENTAGE);
		diseaseCardLayout.setMargin(true);
		diseaseCardLayout.setSpacing(true);
		diseaseCardLayout.addComponent(diseaseDetailsComponent);
		addComponent(diseaseCardLayout, CARD);

//		Grid card layout
		HorizontalLayout diseaseGridLayout = new HorizontalLayout();
		diseaseGridLayout.setWidth(400, Unit.PIXELS);
		regionalDiseaseBurdenGrid.setHeight(320, Unit.PIXELS);
		diseaseGridLayout.setMargin(false);
		diseaseGridLayout.setSpacing(false);
		diseaseGridLayout.addComponents(regionalDiseaseBurdenGrid);
		addComponent(diseaseGridLayout, GRID_TABLE);

		viewMoreLayout(I18nProperties.getCaption(Captions.viewMore));
		button.addClickListener(event -> {
			if(!isShowMore) {
				regionalDiseaseBurdenGrid.setHeight(750, Unit.PIXELS);
				isShowMore = true;
			}else {
				regionalDiseaseBurdenGrid.setHeight(320, Unit.PIXELS);
				isShowMore = false;
			}
		});

//		Map layout 
		HorizontalLayout mapLayout = new HorizontalLayout();
		mapLayout.setWidth(100, Unit.PERCENTAGE);
		final int BASE_HEIGHT = 600;
		mapLayout.setHeight(BASE_HEIGHT, Unit.PIXELS);
//		mapLayout.setMargin(true);
		mapLayout.setSpacing(false);

		dashboardMapComponent.setMargin(false);
		dashboardMapComponent.setSpacing(false);
		mapLayout.setMargin(new MarginInfo(true, true, false, true));
		dashboardMapComponent.addStyleName("map-border-layout");
		mapLayout.addComponent(dashboardMapComponent);
		addComponent(mapLayout, MAP);
	}

	public void removeTopComponents() {
		removeAllComponents();
	}

	public void addMapComponent() {
		addComponent(dashboardMapComponent, "1");

	}

	public HorizontalLayout viewMoreLayout(String title){
		HorizontalLayout viewMoreLayout = new HorizontalLayout();
		viewMoreLayout.setMargin(false);
		viewMoreLayout.setSpacing(false);
		button = new Button(title);
		viewMoreLayout.setHeight(5, Unit.PIXELS);
		button.setHeight(30, Unit.PIXELS);
		viewMoreLayout.addComponent(button);
		viewMoreLayout.setComponentAlignment(button, Alignment.TOP_CENTER);
		addComponent(viewMoreLayout, GRID_VIEW_MORE);

		return viewMoreLayout;
	}
}

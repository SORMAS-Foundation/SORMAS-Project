package com.cinoteck.application.views.reports;

import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteriaNew;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.report.CommunityUserReportModelDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.ui.utils.CollectionValueProvider;

@Route(layout =UserAnalysisView.class)
public class UserAnalysisGridView extends VerticalLayout {
	


	public ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>();
	public ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>();
	public ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>();
	public Button resetButton;

	
	List<AreaReferenceDto> regions;
	List<RegionReferenceDto> provinces;
	List<DistrictReferenceDto> districts;
	Grid<CommunityUserReportModelDto> grid = new Grid<>(CommunityUserReportModelDto.class, false);
	GridListDataView<CommunityUserReportModelDto> dataView;
	
	

	
	
	public UserAnalysisGridView(CommunityCriteriaNew criteria, FormAccess formAccess) {
		List<CommunityUserReportModelDto> analysis = FacadeProvider.getCommunityFacade().getAllActiveCommunitytoRerence(criteria,
				null,null, null, formAccess);
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setPadding(false);
		filterLayout.setVisible(false);
		filterLayout.setAlignItems(Alignment.END);


		regionFilter.setLabel("Region");
		regionFilter.setPlaceholder("All Regions");
		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		regionFilter.addValueChangeListener(e -> {
			provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid());
			provinceFilter.setItems(provinces);
		});

		
		provinceFilter.setLabel("Province");
		provinceFilter.setPlaceholder("All Province");
		provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		provinceFilter.addValueChangeListener(e -> {
			districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid());
			districtFilter.setItems(districts);
		});
		
		districtFilter.setLabel("District");
		districtFilter.setPlaceholder("All District");
		districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
		
		resetButton =  new Button("Reset Filters");
		resetButton.addClickListener(e->{
			provinceFilter.clear();
			districtFilter.clear();
			regionFilter.clear();
	
		});
		
		
		Button displayFilters = new Button("Show Filters", new Icon(VaadinIcon.SLIDERS));
		displayFilters.addClickListener(e->{
			if(filterLayout.isVisible() == false) {
				filterLayout.setVisible(true);
				displayFilters.setText("Hide Filters");
			}else {
				filterLayout.setVisible(false);
				displayFilters.setText("Show Filters");
			}
		});
		
		filterLayout.add( regionFilter, provinceFilter, districtFilter, resetButton);
		
		add(displayFilters,filterLayout);
		userAnalysisGrid(criteria, formAccess);
		
	}


	private void userAnalysisGrid(CommunityCriteriaNew criteria, FormAccess formAccess) {
		List<CommunityUserReportModelDto> analysis = FacadeProvider.getCommunityFacade().getAllActiveCommunitytoRerence(criteria,
				null,null, null, formAccess);
		
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);

		grid.addColumn(CommunityUserReportModelDto::getArea).setHeader("Region").setSortable(true).setResizable(true);
		grid.addColumn(CommunityUserReportModelDto::getRegion).setHeader("Province").setSortable(true).setResizable(true);
		grid.addColumn(CommunityUserReportModelDto::getDistrict).setHeader("District").setSortable(true).setResizable(true);
		grid.addColumn(CommunityUserReportModelDto::getFormAccess).setHeader("Form Access").setSortable(true).setResizable(true);
		grid.addColumn(CommunityUserReportModelDto::getClusterNumber).setHeader("Cluster Number").setSortable(true).setResizable(true);
		grid.addColumn(CommunityUserReportModelDto::getcCode).setHeader("CCode").setSortable(true).setResizable(true);

		grid.addColumn(CommunityUserReportModelDto::getUsername).setHeader("Username").setSortable(true).setResizable(true);
		grid.addColumn(CommunityUserReportModelDto::getMessage).setHeader("Message").setSortable(true).setResizable(true);
		
		dataView = grid.setItems(analysis);
		grid.setVisible(true);
		grid.setAllRowsVisible(true);
		
		
		add(grid);
		
	}


	

}

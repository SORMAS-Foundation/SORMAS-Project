package com.cinoteck.application.views.reports;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

public class CompletionAnalysisFilter extends VerticalLayout {

	private ComboBox<CampaignReferenceDto> campaign = new ComboBox<>();
	private ComboBox<AreaReferenceDto> regionFilter = new ComboBox<>();
	private ComboBox<RegionReferenceDto> provinceFilter = new ComboBox<>();
	private ComboBox<DistrictReferenceDto> districtFilter = new ComboBox<>();
	private Button resetButton;

	List<CampaignReferenceDto> campaigns;

	public CompletionAnalysisFilter() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setPadding(false);
		filterLayout.setVisible(false);
		filterLayout.setAlignItems(Alignment.END);

		campaign.setLabel("Campaigns");
		campaign.setPlaceholder("All Campaigns");
		campaigns = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
		campaign.setItems(campaigns);

		regionFilter.setLabel("Region");
		regionFilter.setPlaceholder("All Regions");
		regionFilter.setItems(FacadeProvider.getAreaFacade().getAllActiveAsReference());
		regionFilter.addValueChangeListener(e -> {
			provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		});

		
		provinceFilter.setLabel("Province");
		provinceFilter.setPlaceholder("All Province");
		provinceFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
		provinceFilter.addValueChangeListener(e -> {
			districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
		});
		
		districtFilter.setLabel("District");
		districtFilter.setPlaceholder("All District");
		districtFilter.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
		
		resetButton =  new Button("Reset Filters");
		
		
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
		
		filterLayout.add(campaign, regionFilter, provinceFilter, districtFilter, resetButton);
		
		add(displayFilters,filterLayout);

	}

}

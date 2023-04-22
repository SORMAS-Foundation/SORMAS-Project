package com.cinoteck.application.views.campaigndata;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignIndexDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;

public class CamapignDataFilter extends VerticalLayout {

//	private final TextField filterr = new TextField();
//
//	private final Button addNewBtnn = new Button();
//
//	private TextField searchField = new TextField();
	private ComboBox<String> campaignYear;
	private ComboBox<CampaignReferenceDto> campaign = new ComboBox<>();
	ComboBox<String> campaignPhase = new ComboBox<>();
	private Button newForm = new Button("NEW FORM");
	private Button importData = new Button("IMPORT");
	private Button exportData = new Button("EXPORT");

	List<String> campaignsYears;
	List<CampaignReferenceDto> campaigns;
	List<CampaignReferenceDto> campaignPhases;
	List<String> camYearList;

	private ComboBox<CampaignFormMetaReferenceDto> campaignForm = new ComboBox<>();
	private ComboBox<AreaReferenceDto> region = new ComboBox<>();
	private ComboBox<RegionReferenceDto> province = new ComboBox<>();
	private ComboBox<DistrictReferenceDto> district = new ComboBox<>();
	private ComboBox cluster = new ComboBox<>();
	private Button resetHandler = new Button();
	private Button applyHandler = new Button();

	List<CampaignFormMetaReferenceDto> campaignForms;
	List<AreaReferenceDto> regions;
	List<RegionReferenceDto> provinces;
	List<DistrictReferenceDto> districts;
	List<CommunityReferenceDto> communities;

	private final TextField filterr = new TextField();

	private final Button addNewBtnn = new Button();

	private TextField searchField = new TextField();

	public CamapignDataFilter() {
		HorizontalLayout level1Filters = new HorizontalLayout();
		level1Filters.getStyle().set("margin-left", "12px");
		level1Filters.setAlignItems(Alignment.END);
//		
//		for(CampaignReferenceDto camdreg :  FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference()) {
//			campaignsYears.add(camdreg.getCampaignYear());
//			System.out.println(camdreg +"campaign hyear ");
//			
//			campaignYear.setItems(campaignsYears);
//		}
//		
//		campaignYear.setLabel("Campaign Year");
//		campaignYear.setId("jgcjgcjgcj");
//
//		

		campaign.setLabel("Campaign");
		campaign.setId("jgcjgcjgcj");
		campaigns = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
		campaign.setItems(campaigns);

		campaignPhase.setLabel("Campaign Phase");
//		campaignPhases = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference()
		campaignPhase.setItems("Pre-Campaign", "Intra- Campaign", "Post- Campaign");
		campaignPhase.setValue("");
		campaignPhase.getStyle().set("padding-top", "0px");
		campaignPhase.setClassName("col-sm-6, col-xs-6");

		newForm.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));

		importData.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));

		exportData.setIcon(new Icon(VaadinIcon.DOWNLOAD));

		level1Filters.add(campaign, campaignPhase, newForm, importData, exportData);

		Button displayFilters = new Button("Show Filters");
		displayFilters.getStyle().set("margin-left", "12px");
		displayFilters.getStyle().set("margin-top", "12px");
		displayFilters.setIcon(new Icon(VaadinIcon.SLIDERS));

		HorizontalLayout campaignDataFilterLayout = new HorizontalLayout();
		campaignDataFilterLayout.getStyle().set("margin-left", "12px");
		campaignDataFilterLayout.setAlignItems(Alignment.END);

		campaignForm.setLabel("Form");
		campaignForm.setPlaceholder("Form");
		campaignForms = FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferences();
		campaignForm.setItems(campaignForms);

		region.setLabel("Region");
		region.setPlaceholder("Regions");

		regions = FacadeProvider.getAreaFacade().getAllActiveAsReference();
		region.setItems(regions);
		region.addValueChangeListener(e -> {
			provinces = FacadeProvider.getRegionFacade().getAllActiveByArea(e.getValue().getUuid());
			province.setItems(provinces);
		});

		province.setLabel("Province");
		province.setPlaceholder("Provinces");
		provinces = FacadeProvider.getRegionFacade().getAllActiveAsReference();
		province.setItems(provinces);
		province.addValueChangeListener(e -> {
			districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(e.getValue().getUuid());
			district.setItems(districts);
		});
		province.getStyle().set("padding-top", "0px");
		province.setClassName("col-sm-6, col-xs-6");

		district.setLabel("District");
		district.setPlaceholder("Districts");
		districts = FacadeProvider.getDistrictFacade().getAllActiveAsReference();
		district.setItems(districts);
		district.addValueChangeListener(e -> {
			communities = FacadeProvider.getCommunityFacade().getAllActiveByDistrict(e.getValue().getUuid());
//			cluster.setItemLabelGenerator(CommunityReferenceDto::getCaption);
			cluster.setItems(communities);
		});
		district.getStyle().set("padding-top", "0px");
		district.setClassName("col-sm-6, col-xs-6");

		cluster.setPlaceholder("Clusters");
		cluster.setItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());

		resetHandler.setText("Reset Filters");

		applyHandler.setText("Apply Filters");

		campaignDataFilterLayout.add(campaignForm, region, province, district, cluster, resetHandler, applyHandler);

		campaignDataFilterLayout.setVisible(false);

		displayFilters.addClickListener(e -> {
			if (!campaignDataFilterLayout.isVisible()) {
				campaignDataFilterLayout.setVisible(true);
				displayFilters.setText("Hide Filters");

			} else {
				campaignDataFilterLayout.setVisible(false);
				displayFilters.setText("Show Filters");
			}

		});

		add(level1Filters, displayFilters, campaignDataFilterLayout);

	}

}

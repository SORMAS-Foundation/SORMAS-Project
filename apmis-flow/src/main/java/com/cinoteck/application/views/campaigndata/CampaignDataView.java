package com.cinoteck.application.views.campaigndata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.accordion.Accordion;
//import com.cinoteck.application.views.campaign.MonthlyExpense.DailyExpenses;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataCriteria;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataIndexDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;

@PageTitle("Campaign Data")
@Route(value = "campaigndata", layout = MainLayout.class)
public class CampaignDataView extends VerticalLayout {
	CamapignDataFilter camapignDataFilter = new CamapignDataFilter();
	List<CampaignFormMetaReferenceDto> campaigns = FacadeProvider.getCampaignFormMetaFacade().getAllCampaignFormMetasAsReferences();// getAllActiveUuids();
	List<CampaignFormMetaReferenceDto> campaignName, campaignForm;
//	campaignStartDate, campaignEndDate, campaignDescription;
	Grid<CampaignFormDataIndexDto> grid = new Grid<>(CampaignFormDataIndexDto.class, false);
	private GridListDataView<CampaignFormDataIndexDto> dataView;

	
	
	public CampaignDataView() {
		CampaignFormDataCriteria criteria;
		criteria = new CampaignFormDataCriteria();
		add(camapignDataFilter);
		configureGrid(criteria);

	}
	
	
	private void configureGrid(CampaignFormDataCriteria criteria) {
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);
		
//		grid.setColumns(
//				// EDIT_BTN_ID,
//				CampaignFormDataIndexDto.CAMPAIGN, CampaignFormDataIndexDto.FORM, CampaignFormDataIndexDto.AREA,
//				CampaignFormDataIndexDto.RCODE, CampaignFormDataIndexDto.REGION, CampaignFormDataIndexDto.PCODE,
//				CampaignFormDataIndexDto.DISTRICT, CampaignFormDataIndexDto.DCODE, CampaignFormDataIndexDto.COMMUNITY,
//				CampaignFormDataIndexDto.COMMUNITYNUMBER, CampaignFormDataIndexDto.CCODE,
//				CampaignFormDataIndexDto.FORM_DATE, CampaignFormDataIndexDto.FORM_TYPE);
////
		grid.addColumn(CampaignFormDataIndexDto.CAMPAIGN).setHeader("Campaign").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.FORM).setHeader("Form").setSortable(true).setResizable(true);
////
		grid.addColumn(CampaignFormDataIndexDto.AREA).setHeader("Region").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.RCODE).setHeader("Region").setSortable(true).setResizable(true);
////		
		grid.addColumn(CampaignFormDataIndexDto.REGION).setHeader("Proince").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.PCODE).setHeader("PCode").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.DISTRICT).setHeader("District").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.DCODE).setHeader("DCode").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.COMMUNITY).setHeader("Cluster").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.COMMUNITYNUMBER).setHeader("Cluster Number").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.CCODE).setHeader("CCode").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.FORM_DATE).setHeader("Form Date").setSortable(true).setResizable(true);
		grid.addColumn(CampaignFormDataIndexDto.FORM_TYPE).setHeader("Form Phase").setSortable(true).setResizable(true);
////
////		
////		
		
		grid.setVisible(true);
		grid.setAllRowsVisible(true);

//		List<CampaignFormDataIndexDto> campaigns = FacadeProvider.getCampaignFormDataFacade().getIndexList(criteria, null, null, null);
//		grid.setItems(campaigns);
//		grid.asSingleSelect().addValueChangeListener(event -> editCampaign(event.getValue()));
add(grid);
	}

}

package com.cinoteck.application.views.campaign;

import java.util.List;
import java.util.stream.Collectors;

import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;

@PageTitle("All Campaigns")
@Route(value = "campaign", layout = MainLayout.class)
public class CampaignsView extends VerticalLayout {

	CampaignFilterView campFilter = new CampaignFilterView();
	List<CampaignReferenceDto> campaigns = FacadeProvider.getCampaignFacade().getAllActiveCampaignsAsReference();
	List<CampaignReferenceDto> campaignName, campaignRound, campaignStartDate, campaignEndDate, campaignDescription;

	Grid<CampaignDto> grid = new Grid<>(CampaignDto.class, false);
	private GridListDataView<CampaignDto> dataView;
	CampaignForm form;

	public CampaignsView() {

		add(campFilter);
		configureGrid();
		configureForm();
		add(getContent());
	}

	private Component getContent() {
		HorizontalLayout content = new HorizontalLayout();
		// content.setFlexGrow(2, grid);
		content.setFlexGrow(4, form);
		content.addClassNames("content");
		content.setSizeFull();
		content.add(grid, form);
		return content;
	}

	private void configureGrid() {
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);

		grid.addColumn(CampaignDto::getName).setHeader("Name").setSortable(true).setResizable(true);
		grid.addColumn(CampaignDto::getCampaignStatus).setHeader("Status").setSortable(true).setResizable(true);

		grid.addColumn(CampaignDto::getStartDate).setHeader("Start Date").setSortable(true).setResizable(true);
		grid.addColumn(CampaignDto::getEndDate).setHeader("End Date").setSortable(true).setResizable(true);
		grid.addColumn(CampaignDto::getCampaignYear).setHeader("Campaign Year").setSortable(true).setResizable(true);

		grid.setVisible(true);
		grid.setAllRowsVisible(true);

		List<CampaignDto> campaigns = FacadeProvider.getCampaignFacade().getAllActive().stream()
				.collect(Collectors.toList());
		grid.setItems(campaigns);
		grid.asSingleSelect().addValueChangeListener(event -> editCampaign(event.getValue()));

	}

	private void configureForm() {
		form = new CampaignForm(campaignName, campaignRound, campaignStartDate, campaignEndDate, campaignDescription);
		form.setVisible(false);
		form.getStyle().set("margin", "20px");
		form.addSaveListener(this::saveCampaign);
		form.addDeleteListener(this::deleteCampaign);
		form.addCloseListener(e -> closeEditor());
	}

	public void editCampaign(CampaignDto campaign) {

		form.setCampaign(campaign);
		form.setVisible(true);
		form.setSizeFull();
		grid.setVisible(false);

		addClassName("editing");

	}

	private void closeEditor() {
		form.setCampaign(null);
		form.setVisible(false);
//		setFiltersVisible(true);
		grid.setVisible(true);
		removeClassName("editing");
	}

	private void addCampaign() {
		grid.asSingleSelect().clear();
		editCampaign(new CampaignDto());
	}

	private void saveCampaign(CampaignForm.SaveEvent event) {
		FacadeProvider.getCampaignFacade().saveCampaign(event.getCampaign()); // .getUserFacade().saveUser(event.getContact());
		// updateList();
		closeEditor();
	}

	private void deleteCampaign(CampaignForm.DeleteEvent event) {
		// FacadeProvider.getUserFacade(). .getContact());
		// updateList();
		closeEditor();
	}

}

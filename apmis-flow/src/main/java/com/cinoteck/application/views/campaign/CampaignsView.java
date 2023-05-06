package com.cinoteck.application.views.campaign;

import java.util.List;
import java.util.stream.Collectors;

import com.cinoteck.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.MultiSortPriority;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;

@PageTitle("All Campaigns")
@Route(value = "campaign", layout = MainLayout.class)
public class CampaignsView extends VerticalLayout {

	private Button filterDisplayToggle;

	private Button validateFormsButton;

	private Button createButton;

	private TextField searchField;

	private ComboBox<EntityRelevanceStatus> relevanceStatusFilter;

	VerticalLayout campaignsFilterLayout = new VerticalLayout();
	
	private Grid<CampaignDto> grid = new Grid<>(CampaignDto.class, false);
	
	private GridListDataView<CampaignDto> dataView;

	private List<CampaignDto> campaigns = FacadeProvider.getCampaignFacade().getAllActive().stream()
			.collect(Collectors.toList());
	
	private CampaignForm campaignForm;
	
	private List<CampaignReferenceDto> campaignName, campaignRound, campaignStartDate, campaignEndDate,
	campaignDescription;


	public CampaignsView() {

		createFilterBar();
		campaignsGrid();
		configureForm();
		add(getContent());
	}

	private Component getContent() {
		HorizontalLayout content = new HorizontalLayout();
		// content.setFlexGrow(2, grid);
		content.setFlexGrow(4, campaignForm);
		content.addClassNames("content");
		content.setSizeFull();
		content.add(grid, campaignForm);
		return content;
	}

	private boolean matchesTerm() {
		// TODO Auto-generated method stub
		return false;
	}

	private void campaignsGrid() {
		
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setMultiSort(true, MultiSortPriority.APPEND);
		grid.setSizeFull();
		grid.setColumnReorderingAllowed(true);
		grid.addColumn(CampaignDto.NAME).setHeader("Name").setSortable(true).setResizable(true);
		grid.addColumn(CampaignDto.CAMPAIGN_STATUS).setHeader("Status").setSortable(true).setResizable(true);
		grid.addColumn(CampaignDto.START_DATE).setHeader("Start Date").setSortable(true).setResizable(true);
		grid.addColumn(CampaignDto.END_DATE).setHeader("End Date").setSortable(true).setResizable(true);
		grid.addColumn(CampaignDto.CAMPAIGN_YEAR).setHeader("Campaign Year").setSortable(true).setResizable(true);

		grid.setVisible(true);
		grid.setAllRowsVisible(true);

		grid.asSingleSelect().addValueChangeListener(event -> editCampaign(event.getValue()));
	}

	private void createFilterBar() {
		HorizontalLayout filterToggleLayout = new HorizontalLayout();
		filterToggleLayout.setAlignItems(Alignment.END);

		filterDisplayToggle = new Button("Show Filters");
		filterDisplayToggle.getStyle().set("margin-left", "12px");
		filterDisplayToggle.getStyle().set("margin-top", "12px");
		filterDisplayToggle.setIcon(new Icon(VaadinIcon.SLIDERS));

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.getStyle().set("margin-left", "12px");
		filterLayout.setVisible(false);

		filterDisplayToggle.addClickListener(e -> {
			if (!filterLayout.isVisible()) {
				filterLayout.setVisible(true);
				filterDisplayToggle.setText("Hide Filters");

			} else {
				filterLayout.setVisible(false);
				filterDisplayToggle.setText("Show Filters");
			}

		});

		searchField = new TextField();
		searchField.setLabel("Search Campaign");
		searchField.setPlaceholder("Search");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		dataView = grid.setItems(campaigns);
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(e -> dataView.addFilter(campaignsz -> {
			String searchTerm = searchField.getValue().trim();

			if (searchTerm.isEmpty())
				return true;

			boolean matchesFullName = matchesTerm();

			return matchesFullName;
		}));

		relevanceStatusFilter = new ComboBox<EntityRelevanceStatus>();
		relevanceStatusFilter.setLabel("Campaign Status");
		relevanceStatusFilter.setItems((EntityRelevanceStatus[]) EntityRelevanceStatus.values());

		validateFormsButton = new Button("Validate Forms", new Icon(VaadinIcon.CHECK_CIRCLE));

		createButton = new Button("Add New Forms", new Icon(VaadinIcon.PLUS_CIRCLE));

		filterToggleLayout.add(filterDisplayToggle, validateFormsButton, createButton);
		filterLayout.add(searchField, relevanceStatusFilter);
		campaignsFilterLayout.add(filterToggleLayout, filterLayout);

		add(campaignsFilterLayout);
	}

	private void configureForm() {
		campaignForm = new CampaignForm(campaignName, campaignRound, campaignStartDate, campaignEndDate,
				campaignDescription);
		campaignForm.setVisible(false);
		campaignForm.getStyle().set("margin", "20px");
		campaignForm.addSaveListener(this::saveCampaign);
		campaignForm.addDeleteListener(this::deleteCampaign);
		campaignForm.addCloseListener(e -> closeEditor());
	}

	public void editCampaign(CampaignDto campaign) {

		campaignForm.setCampaign(campaign);
		campaignForm.setVisible(true);
		campaignForm.setSizeFull();
		campaignsFilterLayout.setVisible(false);
		grid.setVisible(false);

		addClassName("editing");

	}

	private void closeEditor() {
		campaignForm.setCampaign(null);
		campaignForm.setVisible(false);
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

/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples;

import java.util.Date;
import java.util.HashMap;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.CurrentUser;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.LayoutUtil;

@SuppressWarnings("serial")
public class SampleGridComponent extends VerticalLayout {

	public static final String LGA = "lga";
	public static final String SEARCH_FIELD = "searchField";

	private SampleGrid grid;

	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	private VerticalLayout gridLayout;
	
	private boolean showArchivedSamples;
	private Label viewTitleLabel;
	private String originalViewTitle;

	public SampleGridComponent(Label viewTitleLabel) {
		setSizeFull();
		
		this.viewTitleLabel = viewTitleLabel;
		originalViewTitle = viewTitleLabel.getValue();

		grid = new SampleGrid();
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createShipmentFilterBar());
		gridLayout.addComponent(grid);
		grid.getContainer().addItemSetChangeListener(e -> {
			updateActiveStatusButtonCaption();
		});

		styleGridLayout(gridLayout);
		gridLayout.setMargin(true);

		addComponent(gridLayout);
	}

	public SampleGridComponent(CaseReferenceDto caseRef) {
		setSizeFull();
		setMargin(true);

		grid = new SampleGrid(caseRef);
		grid.setHeightMode(HeightMode.ROW);

		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createShipmentFilterBarForCase(caseRef));
		gridLayout.addComponent(grid);
		grid.getContainer().addItemSetChangeListener(e -> {
			updateActiveStatusButtonCaption();
		});

		gridLayout.setMargin(new MarginInfo(true, false, false, false));
		styleGridLayout(gridLayout);

		addComponent(gridLayout);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		UserDto user = CurrentUser.getCurrent().getUser();

		ComboBox testResultFilter = new ComboBox();
		testResultFilter.setWidth(140, Unit.PIXELS);
		testResultFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(SampleTestDto.I18N_PREFIX, SampleTestDto.TEST_RESULT));
		testResultFilter.addItems((Object[])SampleTestResultType.values());
		testResultFilter.addValueChangeListener(e -> {
			grid.setTestResultFilter(((SampleTestResultType)e.getProperty().getValue()));
		});
		filterLayout.addComponent(testResultFilter);        

		ComboBox specimenConditionFilter = new ComboBox();
		specimenConditionFilter.setWidth(140, Unit.PIXELS);
		specimenConditionFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(SampleDto.I18N_PREFIX, SampleDto.SPECIMEN_CONDITION));
		specimenConditionFilter.addItems((Object[])SpecimenCondition.values());
		specimenConditionFilter.addValueChangeListener(e -> {
			grid.setSpecimenConditionFilter(((SpecimenCondition)e.getProperty().getValue()));
		});
		filterLayout.addComponent(specimenConditionFilter);        

		ComboBox classificationFilter = new ComboBox();
		classificationFilter.setWidth(140, Unit.PIXELS);
		classificationFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
		classificationFilter.addItems((Object[])CaseClassification.values());
		classificationFilter.addValueChangeListener(e -> {
			grid.setCaseClassificationFilter(((CaseClassification)e.getProperty().getValue()));
		});
		filterLayout.addComponent(classificationFilter);        

		ComboBox diseaseFilter = new ComboBox();
		diseaseFilter.setWidth(140, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
		diseaseFilter.addItems((Object[])Disease.values());
		diseaseFilter.addValueChangeListener(e -> {
			grid.setDiseaseFilter(((Disease)e.getProperty().getValue()));
		});
		filterLayout.addComponent(diseaseFilter);        


		ComboBox regionFilter = new ComboBox();
		if (user.getRegion() == null) {
			regionFilter.setWidth(140, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
			regionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
				grid.setRegionFilter(region);
			});
			filterLayout.addComponent(regionFilter);
		}

		ComboBox districtFilter = new ComboBox();
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
		districtFilter.setDescription("Select a district in the state");
		districtFilter.addValueChangeListener(e -> {
			grid.setDistrictFilter(((DistrictReferenceDto)e.getProperty().getValue()));
		});

		if (user.getRegion() != null) {
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(user.getRegion().getUuid()));
			districtFilter.setEnabled(true);
		} else {
			regionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
				districtFilter.removeAllItems();
				if (region != null) {
					districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(region.getUuid()));
					districtFilter.setEnabled(true);
				} else {
					districtFilter.setEnabled(false);
				}
			});
			districtFilter.setEnabled(false);
		}
		filterLayout.addComponent(districtFilter);

		ComboBox labFilter = new ComboBox();
		labFilter.setWidth(140, Unit.PIXELS);
		labFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(SampleIndexDto.I18N_PREFIX, SampleIndexDto.LAB));
		labFilter.addItems(FacadeProvider.getFacilityFacade().getAllLaboratories(true));
		labFilter.addValueChangeListener(e -> {
			grid.setLabFilter(((FacilityReferenceDto)e.getProperty().getValue()));
		});
		filterLayout.addComponent(labFilter);

		TextField searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setInputPrompt(I18nProperties.getPrefixFieldCaption(SampleIndexDto.I18N_PREFIX, SEARCH_FIELD));
		searchField.addTextChangeListener(e -> {
			grid.filterByText(e.getText());
		});
		filterLayout.addComponent(searchField);

		return filterLayout;
	}

	public HorizontalLayout createShipmentFilterBar() {
		HorizontalLayout shipmentFilterLayout = new HorizontalLayout();
		shipmentFilterLayout.setSpacing(true);
		shipmentFilterLayout.setWidth(100, Unit.PERCENTAGE);
		shipmentFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		HorizontalLayout buttonFilterLayout = new HorizontalLayout();
		buttonFilterLayout.setSpacing(true);
		{
			Button statusAll = new Button("All", e -> processStatusChange(false, false, false, false, e.getButton()));
			initializeStatusButton(statusAll, buttonFilterLayout, "All");
			CssStyles.removeStyles(statusAll, CssStyles.BUTTON_FILTER_LIGHT);
			activeStatusButton = statusAll;

			Button notShippedButton = new Button("Not shipped", e -> processStatusChange(true, false, false, false, e.getButton()));
			initializeStatusButton(notShippedButton, buttonFilterLayout, "Not shipped");
			Button shippedButton = new Button("Shipped", e -> processStatusChange(false, true, false, false, e.getButton()));
			initializeStatusButton(shippedButton, buttonFilterLayout, "Shipped");
			Button receivedButton = new Button("Received", e -> processStatusChange(false, false, true, false, e.getButton()));
			initializeStatusButton(receivedButton, buttonFilterLayout, "Received");
			Button referredButton = new Button("Referred to other lab", e -> processStatusChange(false, false, false, true, e.getButton()));
			initializeStatusButton(referredButton, buttonFilterLayout, "Referred to other lab");
		}

		shipmentFilterLayout.addComponent(buttonFilterLayout);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show archived/active cases button
			if (CurrentUser.getCurrent().hasUserRight(UserRight.CONTACT_VIEW_ARCHIVED)) {
				Button switchArchivedActiveButton = new Button(I18nProperties.getText("showArchivedSamples"));
				switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
				switchArchivedActiveButton.addClickListener(e -> {
					showArchivedSamples = !showArchivedSamples;
					if (!showArchivedSamples) {
						viewTitleLabel.setValue(originalViewTitle);
						switchArchivedActiveButton.setCaption(I18nProperties.getText("showArchivedSamples"));
						switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
						grid.getSampleCriteria().archived(false);
						grid.reload();
					} else {
						viewTitleLabel.setValue(I18nProperties.getPrefixFragment("View", SamplesView.VIEW_NAME.replaceAll("/", ".") + ".archive"));
						switchArchivedActiveButton.setCaption(I18nProperties.getText("showActiveSamples"));
						switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
						grid.getSampleCriteria().archived(true);
						grid.reload();
					}
				});
				actionButtonsLayout.addComponent(switchArchivedActiveButton);
			}

			// Bulk operation dropdown
			if (CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				shipmentFilterLayout.setWidth(100, Unit.PERCENTAGE);

				MenuBar bulkOperationsDropdown = new MenuBar();	
				MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem("Bulk Actions", null);

				Command deleteCommand = selectedItem -> {
					ControllerProvider.getSampleController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.deselectAll();
							grid.reload();
						}
					});
				};
				bulkOperationsItem.addItem("Delete", FontAwesome.TRASH, deleteCommand);
				
				actionButtonsLayout.addComponent(bulkOperationsDropdown);
			}
		}
		shipmentFilterLayout.addComponent(actionButtonsLayout);
		shipmentFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		shipmentFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return shipmentFilterLayout;
	}

	public HorizontalLayout createShipmentFilterBarForCase(CaseReferenceDto caseRef) {
		HorizontalLayout shipmentFilterLayout = new HorizontalLayout();
		shipmentFilterLayout.setSpacing(true);
		shipmentFilterLayout.setWidth(100, Unit.PERCENTAGE);
		shipmentFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		HorizontalLayout buttonFilterLayout = new HorizontalLayout();
		buttonFilterLayout.setSpacing(true);
		{
			Button statusAll = new Button("All", e -> processStatusChange(false, false, false, false, e.getButton()));
			initializeStatusButton(statusAll, buttonFilterLayout, "All");
			CssStyles.removeStyles(statusAll, CssStyles.BUTTON_FILTER_LIGHT);
			activeStatusButton = statusAll;

			Button notShippedButton = new Button("Not shipped", e -> processStatusChange(true, false, false, false, e.getButton()));
			initializeStatusButton(notShippedButton, buttonFilterLayout, "Not shipped");
			Button shippedButton = new Button("Shipped", e -> processStatusChange(false, true, false, false, e.getButton()));
			initializeStatusButton(shippedButton, buttonFilterLayout, "Shipped");
			Button receivedButton = new Button("Received", e -> processStatusChange(false, false, true, false, e.getButton()));
			initializeStatusButton(receivedButton, buttonFilterLayout, "Received");
			Button referredButton = new Button("Referred to other lab", e -> processStatusChange(false, false, false, true, e.getButton()));
			initializeStatusButton(referredButton, buttonFilterLayout, "Referred to other lab");
		}
		shipmentFilterLayout.addComponent(buttonFilterLayout);

		// Bulk operation dropdown
		if (CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			shipmentFilterLayout.setWidth(100, Unit.PERCENTAGE);

			MenuBar bulkOperationsDropdown = new MenuBar();	
			MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem("Bulk Actions", null);

			Command deleteCommand = selectedItem -> {
				ControllerProvider.getSampleController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload();
					}
				});
			};
			bulkOperationsItem.addItem("Delete", FontAwesome.TRASH, deleteCommand);

			shipmentFilterLayout.addComponent(bulkOperationsDropdown);
			shipmentFilterLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			shipmentFilterLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}

		if (CurrentUser.getCurrent().hasUserRight(UserRight.SAMPLE_EXPORT)) {
			Button exportButton = new Button("Export");
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(FontAwesome.DOWNLOAD);

			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(grid.getContainerDataSource(), grid.getColumns(), "sormas_samples", "sormas_samples_" + DateHelper.formatDateForExport(new Date()) + ".csv", SampleGrid.EDIT_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);

			shipmentFilterLayout.addComponent(exportButton);
			shipmentFilterLayout.setComponentAlignment(exportButton, Alignment.MIDDLE_RIGHT);
			if (!CurrentUser.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				shipmentFilterLayout.setExpandRatio(exportButton, 1);
			}
		}

		if (CurrentUser.getCurrent().hasUserRight(UserRight.SAMPLE_CREATE)) {
			Button createButton = new Button("New sample");
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			createButton.addClickListener(e -> ControllerProvider.getSampleController().create(caseRef, this::reload));
			shipmentFilterLayout.addComponent(createButton);
			shipmentFilterLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
		}

		return shipmentFilterLayout;
	}

	public void reload() {
		grid.reload();
	}

	private void styleGridLayout(VerticalLayout gridLayout) {
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
	}

	public SampleGrid getGrid() {
		return grid;
	}

	public void updateActiveStatusButtonCaption() {
		if (activeStatusButton != null) {
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getContainer().size())));
		}
	}

	private void processStatusChange(boolean notShipped, boolean shipped, boolean received, boolean referred, Button button) {
		if (notShipped) {
			grid.filterForNotShipped();
		} else if (shipped) {
			grid.filterForShipped();
		} else if (received) {
			grid.filterForReceived();
		} else if (referred) {
			grid.filterForReferred();
		} else {
			grid.clearShipmentFilters(true);
		}

		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
		});
		CssStyles.removeStyles(button, CssStyles.BUTTON_FILTER_LIGHT);
		activeStatusButton = button;
		updateActiveStatusButtonCaption();
	}

	private void initializeStatusButton(Button button, HorizontalLayout filterLayout, String caption) {
		CssStyles.style(button, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
		button.setCaptionAsHtml(true);
		filterLayout.addComponent(button);
		statusButtons.put(button, caption);
	}

}

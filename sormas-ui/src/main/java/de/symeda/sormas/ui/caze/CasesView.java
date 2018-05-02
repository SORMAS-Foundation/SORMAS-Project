package de.symeda.sormas.ui.caze;

import java.util.Date;
import java.util.HashMap;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.dashboard.DateFilterOption;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link CaseController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class CasesView extends AbstractView {

	private static final long serialVersionUID = -3533557348144005469L;

	public static final String VIEW_NAME = "cases";

	public static final String SEARCH_FIELD = "searchField";

	private CaseGrid grid;    
	private Button createButton;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	private VerticalLayout gridLayout;
	private HorizontalLayout firstFilterRowLayout;
	private HorizontalLayout secondFilterRowLayout;
	private HorizontalLayout dateFilterRowLayout;

	private DateFilterOption dateFilterOption;
	private Date fromDate = null;
	private Date toDate = null;

	public CasesView() {
		super(VIEW_NAME);

		grid = new CaseGrid();
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		grid.getContainer().addItemSetChangeListener(e -> {
			updateActiveStatusButtonCaption();
		});
		
		if (LoginHelper.hasUserRight(UserRight.CASE_IMPORT)) {
			Button importButton = new Button("Import");
			importButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			importButton.setIcon(FontAwesome.UPLOAD);
			importButton.addClickListener(e -> {
				Window popupWindow = VaadinUiUtil.showPopupWindow(new CaseImportLayout());
				popupWindow.setCaption("Import cases");
				popupWindow.addCloseListener(c -> {
					grid.reload();
				});
			});
			addHeaderComponent(importButton);
		}

		if (LoginHelper.hasUserRight(UserRight.CASE_EXPORT)) {
			
			PopupButton exportButton = new PopupButton("Export"); 
			exportButton.setIcon(FontAwesome.DOWNLOAD);
			VerticalLayout exportLayout = new VerticalLayout();
			exportLayout.setSpacing(true); 
			exportLayout.setMargin(true);
			exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			exportLayout.setWidth(200, Unit.PIXELS);
			exportButton.setContent(exportLayout);
			addHeaderComponent(exportButton);

			Button basicExportButton = new Button("Basic Export");
			basicExportButton.setDescription("Export the columns and rows that are shown in the table below.");
			basicExportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			basicExportButton.setIcon(FontAwesome.TABLE);
			basicExportButton.setWidth(100, Unit.PERCENTAGE);
			exportLayout.addComponent(basicExportButton);
			
			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(grid.getContainerDataSource(), grid.getColumns(), "sormas_cases", "sormas_cases_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(basicExportButton);
			
			Button extendedExportButton = new Button("Full Export");
			extendedExportButton.setDescription("Export the rows that are shown in the table below with an extended set of columns. This may take a while.");
			extendedExportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			extendedExportButton.setIcon(FontAwesome.FILE_TEXT);
			extendedExportButton.setWidth(100, Unit.PERCENTAGE);
			exportLayout.addComponent(extendedExportButton);
			
			StreamResource extendedExportStreamResource = DownloadUtil.createCsvExportStreamResource(CaseExportDto.class,
					() -> FacadeProvider.getCaseFacade().getExportList(LoginHelper.getCurrentUser().getUuid(), grid.getFilterCriteria()), 
					propertyId -> {
						return I18nProperties.getPrefixFieldCaption(CaseExportDto.I18N_PREFIX, propertyId,
							I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, propertyId,
								I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, propertyId,
									I18nProperties.getPrefixFieldCaption(SymptomsDto.I18N_PREFIX, propertyId,
										I18nProperties.getPrefixFieldCaption(HospitalizationDto.I18N_PREFIX, propertyId)))));
					},
					"sormas_cases_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			new FileDownloader(extendedExportStreamResource).extend(extendedExportButton);
		}

		if (LoginHelper.hasUserRight(UserRight.CASE_CREATE)) {
			createButton = new Button("New case");
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			createButton.addClickListener(e -> ControllerProvider.getCaseController().create());
			addHeaderComponent(createButton);
		}
		
		addComponent(gridLayout);
		grid.setReloadEnabled(true);
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		firstFilterRowLayout = new HorizontalLayout();
		firstFilterRowLayout.setSpacing(true);
		firstFilterRowLayout.setWidth(100, Unit.PERCENTAGE);
		{
			ComboBox outcomeFilter = new ComboBox();
			outcomeFilter.setWidth(140, Unit.PIXELS);
			outcomeFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.OUTCOME));
			outcomeFilter.addItems((Object[]) CaseOutcome.values());
			outcomeFilter.addValueChangeListener(e -> {
				grid.setOutcomeFilter(((CaseOutcome) e.getProperty().getValue()));
			});
			firstFilterRowLayout.addComponent(outcomeFilter);

			ComboBox diseaseFilter = new ComboBox();
			diseaseFilter.setWidth(140, Unit.PIXELS);
			diseaseFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
			diseaseFilter.addItems((Object[])Disease.values());
			diseaseFilter.addValueChangeListener(e -> {
				grid.setDiseaseFilter(((Disease)e.getProperty().getValue()));
			});
			firstFilterRowLayout.addComponent(diseaseFilter);

			ComboBox classificationFilter = new ComboBox();
			classificationFilter.setWidth(140, Unit.PIXELS);
			classificationFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
			classificationFilter.addItems((Object[])CaseClassification.values());
			classificationFilter.addValueChangeListener(e -> {
				grid.setClassificationFilter(((CaseClassification)e.getProperty().getValue()));
			});
			firstFilterRowLayout.addComponent(classificationFilter);

			TextField searchField = new TextField();
			searchField.setWidth(200, Unit.PIXELS);
			searchField.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, SEARCH_FIELD));
			searchField.addTextChangeListener(e -> {
				grid.filterByText(e.getText());
			});
			firstFilterRowLayout.addComponent(searchField);

			addShowMoreOrLessFiltersButtons(firstFilterRowLayout);
		}
		filterLayout.addComponent(firstFilterRowLayout);

		secondFilterRowLayout = new HorizontalLayout();
		secondFilterRowLayout.setSpacing(true);
		secondFilterRowLayout.setSizeUndefined();
		{
			ComboBox presentConditionFilter = new ComboBox();
			presentConditionFilter.setWidth(140, Unit.PIXELS);
			presentConditionFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(PersonDto.I18N_PREFIX, PersonDto.PRESENT_CONDITION));
			presentConditionFilter.addItems((Object[])PresentCondition.values());
			presentConditionFilter.addValueChangeListener(e -> {
				grid.setPresentConditionFilter(((PresentCondition)e.getProperty().getValue()));
			});
			secondFilterRowLayout.addComponent(presentConditionFilter);      

			UserDto user = LoginHelper.getCurrentUser();

			ComboBox regionFilter = new ComboBox();
			if (user.getRegion() == null) {
				regionFilter.setWidth(140, Unit.PIXELS);
				regionFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
				regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
				regionFilter.addValueChangeListener(e -> {
					RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
					grid.setRegionFilter(region);
				});
				secondFilterRowLayout.addComponent(regionFilter);
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
			secondFilterRowLayout.addComponent(districtFilter);
			
			ComboBox facilityFilter = new ComboBox();
			facilityFilter.setWidth(140, Unit.PIXELS);
			facilityFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
			facilityFilter.setDescription("Select a facility in the LGA");
			facilityFilter.addValueChangeListener(e -> {
				grid.setHealthFacilityFilter(((FacilityReferenceDto)e.getProperty().getValue()));
			});
			facilityFilter.setEnabled(false);
			secondFilterRowLayout.addComponent(facilityFilter);

			districtFilter.addValueChangeListener(e-> {
				facilityFilter.removeAllItems();
				DistrictReferenceDto district = (DistrictReferenceDto)e.getProperty().getValue();
				if (district != null) {
					facilityFilter.addItems(FacadeProvider.getFacilityFacade().getHealthFacilitiesByDistrict(district, true));
					facilityFilter.setEnabled(true);
				} else {
					facilityFilter.setEnabled(false);
				}
			});

			ComboBox officerFilter = new ComboBox();
			officerFilter.setWidth(140, Unit.PIXELS);
			officerFilter.setInputPrompt(I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.SURVEILLANCE_OFFICER));
			if (user.getRegion() != null) {
				officerFilter.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(user.getRegion(), UserRole.SURVEILLANCE_OFFICER));
			}
			officerFilter.addValueChangeListener(e -> {
				grid.setSurveillanceOfficerFilter(((UserReferenceDto)e.getProperty().getValue()));
			});
			secondFilterRowLayout.addComponent(officerFilter);

			ComboBox reportedByFilter = new ComboBox();
			reportedByFilter.setWidth(140, Unit.PIXELS);
			reportedByFilter.setInputPrompt("Reported By");
			reportedByFilter.addItems((Object[]) UserRole.values());
			reportedByFilter.addValueChangeListener(e -> {
				grid.setReportedByFilter((UserRole) e.getProperty().getValue());
			});
			secondFilterRowLayout.addComponent(reportedByFilter);
			
			CheckBox casesWithoutGeoCoordsFilter = new CheckBox();
			CssStyles.style(casesWithoutGeoCoordsFilter, CssStyles.CHECKBOX_FILTER_INLINE);
			casesWithoutGeoCoordsFilter.setCaption("Only cases without geo coordinates");
			casesWithoutGeoCoordsFilter.setDescription("Only list cases that don't have address or report geo coordinates");
			casesWithoutGeoCoordsFilter.addValueChangeListener(e -> {
				grid.setNoGeoCoordinatesFilter((boolean) e.getProperty().getValue());
			});
			secondFilterRowLayout.addComponent(casesWithoutGeoCoordsFilter);
		}
		filterLayout.addComponent(secondFilterRowLayout);
		secondFilterRowLayout.setVisible(false);

		dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();
		{
			Button applyButton = new Button("Apply date filter");
			
			EpiWeekAndDateFilterComponent weekAndDateFilter = new EpiWeekAndDateFilterComponent(applyButton, false, false);
			weekAndDateFilter.getWeekFromFilter().setInputPrompt("New cases from epi week...");
			weekAndDateFilter.getWeekToFilter().setInputPrompt("... to epi week");
			weekAndDateFilter.getDateFromFilter().setInputPrompt("New cases from...");
			weekAndDateFilter.getDateToFilter().setInputPrompt("... to");
			dateFilterRowLayout.addComponent(weekAndDateFilter);
			dateFilterRowLayout.addComponent(applyButton);

			applyButton.addClickListener(e -> {
				dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
				if (dateFilterOption == DateFilterOption.DATE) {
					fromDate = weekAndDateFilter.getDateFromFilter().getValue();
					toDate = weekAndDateFilter.getDateToFilter().getValue();
				} else {
					fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
					toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
				}
				if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
					applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
					grid.setDateFilter(fromDate, toDate);
				} else {
					if (dateFilterOption == DateFilterOption.DATE) {
						Notification notification = new Notification("Missing date filter", "Please fill in both date filter fields", Type.WARNING_MESSAGE, false);
						notification.setDelayMsec(-1);
						notification.show(Page.getCurrent());
					} else {
						Notification notification = new Notification("Missing epi week filter", "Please fill in both epi week filter fields", Type.WARNING_MESSAGE, false);
						notification.setDelayMsec(-1);
						notification.show(Page.getCurrent());
					}
				}
			});
		}
		filterLayout.addComponent(dateFilterRowLayout);
		dateFilterRowLayout.setVisible(false);

		return filterLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setSizeUndefined();
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);
		
		statusButtons = new HashMap<>();
		
		Button statusAll = new Button("All", e -> {
			processStatusChange(null, e.getButton());
		});
		CssStyles.style(statusAll, ValoTheme.BUTTON_LINK, CssStyles.LINK_HIGHLIGHTED);
		statusAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, "All");
		activeStatusButton = statusAll;

		for (InvestigationStatus status : InvestigationStatus.values()) {
			Button statusButton = new Button(status.toString(), e -> {
				processStatusChange(status, e.getButton());
			});
			CssStyles.style(statusButton, ValoTheme.BUTTON_LINK, CssStyles.LINK_HIGHLIGHTED, CssStyles.LINK_HIGHLIGHTED_LIGHT);
			statusButton.setCaptionAsHtml(true);
			statusFilterLayout.addComponent(statusButton);
			statusButtons.put(statusButton, status.toString());
		}
		
		// Bulk operation dropdown
		if (LoginHelper.hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
			
			MenuBar bulkOperationsDropdown = new MenuBar();	
			MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem("Bulk Actions", null);
			
			Command changeCommand = selectedItem -> {
				ControllerProvider.getCaseController().showBulkCaseDataEditComponent(grid.getSelectedRows());
			};
			bulkOperationsItem.addItem("Edit...", FontAwesome.ELLIPSIS_H, changeCommand);
			
			Command deleteCommand = selectedItem -> {
				ControllerProvider.getCaseController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload();
					}
				});
			};
			bulkOperationsItem.addItem("Delete", FontAwesome.TRASH, deleteCommand);
			
			statusFilterLayout.addComponent(bulkOperationsDropdown);
			statusFilterLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			statusFilterLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}
		
		return statusFilterLayout;
	}

	private void addShowMoreOrLessFiltersButtons(HorizontalLayout parentLayout) {
		Button showMoreButton = new Button("Show More Filters", FontAwesome.CHEVRON_DOWN);
		CssStyles.style(showMoreButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE);
		Button showLessButton = new Button("Show Less Filters", FontAwesome.CHEVRON_UP);
		CssStyles.style(showLessButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE);

		showMoreButton.addClickListener(e -> {
			showMoreButton.setVisible(false);
			showLessButton.setVisible(true);
			secondFilterRowLayout.setVisible(true);
			dateFilterRowLayout.setVisible(true);
		});

		showLessButton.addClickListener(e -> {
			showLessButton.setVisible(false);
			showMoreButton.setVisible(true);
			secondFilterRowLayout.setVisible(false);
			dateFilterRowLayout.setVisible(false);
		});

		parentLayout.addComponent(showMoreButton);
		parentLayout.addComponent(showLessButton);
		parentLayout.setComponentAlignment(showMoreButton, Alignment.TOP_RIGHT);
		parentLayout.setComponentAlignment(showLessButton, Alignment.TOP_RIGHT);
		parentLayout.setExpandRatio(showMoreButton, 1);
		parentLayout.setExpandRatio(showLessButton, 1);
		showLessButton.setVisible(false);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
		updateActiveStatusButtonCaption();
	}

	public void clearSelection() {
		grid.getSelectionModel().reset();
	}
	
	private void updateActiveStatusButtonCaption() {
		if (activeStatusButton != null) {
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getContainer().size())));
		}
	}
	
	private void processStatusChange(InvestigationStatus investigationStatus, Button button) {
		grid.setInvestigationFilter(investigationStatus);
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.LINK_HIGHLIGHTED_LIGHT);
			b.setCaption(statusButtons.get(b));
		});
		CssStyles.removeStyles(button, CssStyles.LINK_HIGHLIGHTED_LIGHT);
		activeStatusButton = button;
		updateActiveStatusButtonCaption();
	}
	
}

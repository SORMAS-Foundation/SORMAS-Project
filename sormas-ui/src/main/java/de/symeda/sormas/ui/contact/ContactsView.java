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
package de.symeda.sormas.ui.contact;

import java.util.Date;
import java.util.HashMap;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDateType;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
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
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.CaseController;
import de.symeda.sormas.ui.dashboard.DateFilterOption;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link CaseController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class ContactsView extends AbstractView {

	private static final long serialVersionUID = -3533557348144005469L;

	public static final String VIEW_NAME = "contacts";

	private ContactCriteria criteria;

	private ContactGrid grid;    
	private VerticalLayout gridLayout;

	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	// Filters
	private HorizontalLayout firstFilterRowLayout;
	private HorizontalLayout secondFilterRowLayout;
	private HorizontalLayout dateFilterRowLayout;
	private ComboBox classificationFilter;
	private ComboBox diseaseFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox facilityFilter;
	private ComboBox officerFilter;
	private ComboBox followUpStatusFilter;
	private ComboBox reportedByFilter;
	private TextField searchField;
	private Button resetButton;
	private EpiWeekAndDateFilterComponent<ContactDateType> weekAndDateFilter;
	private Button expandFiltersButton;
	private Button collapseFiltersButton;

	private Button switchArchivedActiveButton;
	private String originalViewTitle;

	public ContactsView() {
		super(VIEW_NAME);

		originalViewTitle = getViewTitleLabel().getValue();

		criteria = ViewModelProviders.of(ContactsView.class).get(ContactCriteria.class);
		if (criteria.getArchived() == null) {
			criteria.archived(false);
		}

		grid = new ContactGrid();  
		grid.setCriteria(criteria);
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
			updateStatusButtons();
		});

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EXPORT)) {

			PopupButton exportButton = new PopupButton(I18nProperties.getCaption(Captions.export)); 
			exportButton.setIcon(VaadinIcons.DOWNLOAD);
			VerticalLayout exportLayout = new VerticalLayout();
			exportLayout.setSpacing(true); 
			exportLayout.setMargin(true);
			exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			exportLayout.setWidth(200, Unit.PIXELS);
			exportButton.setContent(exportLayout);
			addHeaderComponent(exportButton);

			Button basicExportButton = new Button(I18nProperties.getCaption(Captions.exportBasic));
			basicExportButton.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			basicExportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			basicExportButton.setIcon(VaadinIcons.TABLE);
			basicExportButton.setWidth(100, Unit.PERCENTAGE);
			exportLayout.addComponent(basicExportButton);

			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(grid.getContainerDataSource(), grid.getColumns(), "sormas_contacts", "sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(basicExportButton);

			Button extendedExportButton = new Button(I18nProperties.getCaption(Captions.exportDetailed));
			extendedExportButton.setDescription(I18nProperties.getDescription(Descriptions.descDetailedExportButton));
			extendedExportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			extendedExportButton.setIcon(VaadinIcons.FILE_TEXT);
			extendedExportButton.setWidth(100, Unit.PERCENTAGE);
			exportLayout.addComponent(extendedExportButton);

			StreamResource extendedExportStreamResource = DownloadUtil.createCsvExportStreamResource(ContactExportDto.class,
					(Integer start, Integer max) -> FacadeProvider.getContactFacade().getExportList(UserProvider.getCurrent().getUuid(), grid.getCriteria(), start, max), 
					(propertyId,type) -> {
						String caption = I18nProperties.getPrefixCaption(ContactExportDto.I18N_PREFIX, propertyId,
								I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, propertyId,
										I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, propertyId,
												I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, propertyId,
														I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, propertyId,
																I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX, propertyId))))));
						if (Date.class.isAssignableFrom(type)) {
							caption += " (" + DateHelper.getLocalShortDatePattern() + ")";
						}
						return caption;
					},
					"sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			new FileDownloader(extendedExportStreamResource).extend(extendedExportButton);

			// Warning if no filters have been selected
			Label warningLabel = new Label(I18nProperties.getString(Strings.infoExportNoFilters), ContentMode.HTML);
			exportLayout.addComponent(warningLabel);
			warningLabel.setVisible(false);

			exportButton.addClickListener(e -> {
				warningLabel.setVisible(!criteria.hasAnyFilterActive());
			});
		}

		addComponent(gridLayout);
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		firstFilterRowLayout = new HorizontalLayout();
		firstFilterRowLayout.setMargin(false);
		firstFilterRowLayout.setSpacing(true);
		firstFilterRowLayout.setSizeUndefined();
		{
			classificationFilter = new ComboBox();
			classificationFilter.setWidth(140, Unit.PIXELS);
			classificationFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_CLASSIFICATION));
			classificationFilter.addItems((Object[]) ContactClassification.values());
			classificationFilter.addValueChangeListener(e -> {
				criteria.contactClassification((ContactClassification) e.getProperty().getValue());
				navigateTo(criteria);
			});
			firstFilterRowLayout.addComponent(classificationFilter);
			
			diseaseFilter = new ComboBox();
			diseaseFilter.setWidth(140, Unit.PIXELS);
			diseaseFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_DISEASE));
			diseaseFilter.addItems((Object[])Disease.values());
			diseaseFilter.addValueChangeListener(e -> {
				criteria.caseDisease(((Disease)e.getProperty().getValue()));
				navigateTo(criteria);
			});
			firstFilterRowLayout.addComponent(diseaseFilter);

			followUpStatusFilter = new ComboBox();
			followUpStatusFilter.setWidth(140, Unit.PIXELS);
			followUpStatusFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.FOLLOW_UP_STATUS));
			followUpStatusFilter.addItems((Object[])FollowUpStatus.values());
			followUpStatusFilter.addValueChangeListener(e -> {
				criteria.followUpStatus(((FollowUpStatus)e.getProperty().getValue()));
				navigateTo(criteria);
			});
			firstFilterRowLayout.addComponent(followUpStatusFilter);

			searchField = new TextField();
			searchField.setWidth(200, Unit.PIXELS);
			searchField.setNullRepresentation("");
			searchField.setInputPrompt(I18nProperties.getString(Strings.promptContactsSearchField));
			searchField.addTextChangeListener(e -> {
				criteria.nameUuidCaseLike(e.getText());
				grid.reload();
			});
			firstFilterRowLayout.addComponent(searchField);
			
			addShowMoreOrLessFiltersButtons(firstFilterRowLayout);

			resetButton = new Button(I18nProperties.getCaption(Captions.actionResetFilters));
			resetButton.setVisible(false);
			resetButton.addClickListener(event -> {
				ViewModelProviders.of(ContactsView.class).remove(ContactCriteria.class);
				navigateTo(null);
			});
			firstFilterRowLayout.addComponent(resetButton);
		}
		filterLayout.addComponent(firstFilterRowLayout);

		secondFilterRowLayout = new HorizontalLayout();
		secondFilterRowLayout.setMargin(false);
		secondFilterRowLayout.setSpacing(true);
		secondFilterRowLayout.setSizeUndefined();
		{
			UserDto user = UserProvider.getCurrent().getUser();
			regionFilter = new ComboBox();
			if (user.getRegion() == null) {
				regionFilter.setWidth(140, Unit.PIXELS);
				regionFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_REGION_UUID));
				regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
				regionFilter.addValueChangeListener(e -> {
					RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
					criteria.caseRegion(region);
					navigateTo(criteria);
				});
				secondFilterRowLayout.addComponent(regionFilter);
			}

			districtFilter = new ComboBox();
			districtFilter.setWidth(140, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_DISTRICT_UUID));
			districtFilter.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
			districtFilter.addValueChangeListener(e -> {
				DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
				criteria.caseDistrict(district);
				navigateTo(criteria);
			});

			if (user.getRegion() != null && user.getDistrict() == null) {	
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

			facilityFilter = new ComboBox();
			facilityFilter.setWidth(140, Unit.PIXELS);
			facilityFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_HEALTH_FACILITY_UUID));
			facilityFilter.setDescription(I18nProperties.getDescription(Descriptions.descFacilityFilter));
			facilityFilter.addValueChangeListener(e -> {
				FacilityReferenceDto facility = (FacilityReferenceDto) e.getProperty().getValue();
				criteria.caseFacility(facility);
				navigateTo(criteria);
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

			officerFilter = new ComboBox();
			officerFilter.setWidth(140, Unit.PIXELS);
			officerFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER_UUID));
			if (user.getRegion() != null) {
				officerFilter.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(user.getRegion(), UserRole.CONTACT_OFFICER));
			}
			officerFilter.addValueChangeListener(e -> {
				UserReferenceDto officer = (UserReferenceDto) e.getProperty().getValue();
				criteria.contactOfficer(officer);
				navigateTo(criteria);
			});
			secondFilterRowLayout.addComponent(officerFilter);

			reportedByFilter = new ComboBox();
			reportedByFilter.setWidth(140, Unit.PIXELS);
			reportedByFilter.setInputPrompt(I18nProperties.getString(Strings.reportedBy));
			reportedByFilter.addItems((Object[]) UserRole.values());
			reportedByFilter.addValueChangeListener(e -> {
				criteria.reportingUserRole((UserRole) e.getProperty().getValue());
				navigateTo(criteria);
			});
			secondFilterRowLayout.addComponent(reportedByFilter);
		}
		filterLayout.addComponent(secondFilterRowLayout);
		secondFilterRowLayout.setVisible(false);

		dateFilterRowLayout = new HorizontalLayout();
		dateFilterRowLayout.setSpacing(true);
		dateFilterRowLayout.setSizeUndefined();
		{
			Button applyButton = new Button(I18nProperties.getCaption(Captions.actionApplyDateFilter));
			
			weekAndDateFilter = new EpiWeekAndDateFilterComponent<>(applyButton, false, false, null, ContactDateType.class, I18nProperties.getString(Strings.promptContactDateType), ContactDateType.REPORT_DATE);
			weekAndDateFilter.getWeekFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactEpiWeekFrom));
			weekAndDateFilter.getWeekToFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactEpiWeekTo));
			weekAndDateFilter.getDateFromFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactDateFrom));
			weekAndDateFilter.getDateToFilter().setInputPrompt(I18nProperties.getString(Strings.promptContactDateTo));
			dateFilterRowLayout.addComponent(weekAndDateFilter);
			dateFilterRowLayout.addComponent(applyButton);
			
			applyButton.addClickListener(e -> {
				DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
				Date fromDate, toDate;
				if (dateFilterOption == DateFilterOption.DATE) {
					fromDate = DateHelper.getStartOfDay(weekAndDateFilter.getDateFromFilter().getValue());
					toDate = DateHelper.getEndOfDay(weekAndDateFilter.getDateToFilter().getValue());
				} else {
					fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
					toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
				}
				if ((fromDate != null && toDate != null) || (fromDate == null && toDate == null)) {
					applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
					ContactDateType contactDateType = (ContactDateType) weekAndDateFilter.getDateTypeSelector().getValue();
					if (contactDateType == ContactDateType.LAST_CONTACT_DATE) {
						criteria.lastContactDateBetween(fromDate, toDate);
						criteria.reportDateBetween(null, null);
					} else {
						criteria.reportDateBetween(fromDate, toDate);
						criteria.lastContactDateBetween(null, null);
					}
					navigateTo(criteria);
				} else {
					if (dateFilterOption == DateFilterOption.DATE) {
						Notification notification = new Notification(I18nProperties.getString(Strings.headingMissingDateFilter), 
								I18nProperties.getString(Strings.messageMissingDateFilter), Type.WARNING_MESSAGE, false);
						notification.setDelayMsec(-1);
						notification.show(Page.getCurrent());
					} else {
						Notification notification = new Notification(I18nProperties.getString(Strings.headingMissingEpiWeekFilter), 
								I18nProperties.getString(Strings.messageMissingEpiWeekFilter), Type.WARNING_MESSAGE, false);
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
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		Button statusAll = new Button(I18nProperties.getCaption(Captions.all), e -> {
			criteria.contactStatus(null);
			navigateTo(criteria);
		});
		CssStyles.style(statusAll, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
		activeStatusButton = statusAll;

		for (ContactStatus status : ContactStatus.values()) {
			Button statusButton = new Button(status.toString(), e -> {
				criteria.contactStatus(status);
				navigateTo(criteria);
			});
			statusButton.setData(status);
			CssStyles.style(statusButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
			statusButton.setCaptionAsHtml(true);
			statusFilterLayout.addComponent(statusButton);
			statusButtons.put(statusButton, status.toString());
		}

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show archived/active cases button
			if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW_ARCHIVED)) {
				switchArchivedActiveButton = new Button(I18nProperties.getCaption(Captions.contactShowArchived));
				switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
				switchArchivedActiveButton.addClickListener(e -> {
					criteria.archived(Boolean.TRUE.equals(criteria.getArchived()) ? null : Boolean.TRUE);
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(switchArchivedActiveButton);
			}

			// Bulk operation dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				statusFilterLayout.setWidth(100, Unit.PERCENTAGE);

				MenuBar bulkOperationsDropdown = new MenuBar();	
				MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem(I18nProperties.getCaption(Captions.bulkActions), null);

				Command changeCommand = selectedItem -> {
					ControllerProvider.getContactController().showBulkContactDataEditComponent(grid.getSelectedRows(), null);
				};
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkEdit), VaadinIcons.ELLIPSIS_H, changeCommand);

				Command cancelFollowUpCommand = selectedItem -> {
					ControllerProvider.getContactController().cancelFollowUpOfAllSelectedItems(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.reload();
						}
					});
				};
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkCancelFollowUp), VaadinIcons.CLOSE, cancelFollowUpCommand);

				Command lostToFollowUpCommand = selectedItem -> {
					ControllerProvider.getContactController().setAllSelectedItemsToLostToFollowUp(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.reload();
						}
					});
				};
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkLostToFollowUp), VaadinIcons.UNLINK, lostToFollowUpCommand);

				Command deleteCommand = selectedItem -> {
					ControllerProvider.getContactController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.reload();
						}
					});
				};
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, deleteCommand);

				actionButtonsLayout.addComponent(bulkOperationsDropdown);
			}
		}
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterLayout;
	}
	
	private void addShowMoreOrLessFiltersButtons(HorizontalLayout parentLayout) {
		expandFiltersButton = new Button(I18nProperties.getCaption(Captions.actionShowMoreFilters), VaadinIcons.CHEVRON_DOWN);
		CssStyles.style(expandFiltersButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		collapseFiltersButton = new Button(I18nProperties.getCaption(Captions.actionShowLessFilters), VaadinIcons.CHEVRON_UP);
		CssStyles.style(collapseFiltersButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);

		expandFiltersButton.addClickListener(e -> {
			setFiltersExpanded(true);
		});

		collapseFiltersButton.addClickListener(e -> {
			setFiltersExpanded(false);
		});

		parentLayout.addComponent(expandFiltersButton);
		parentLayout.addComponent(collapseFiltersButton);
		parentLayout.setComponentAlignment(expandFiltersButton, Alignment.TOP_LEFT);
		parentLayout.setComponentAlignment(collapseFiltersButton, Alignment.TOP_LEFT);
		collapseFiltersButton.setVisible(false);
	}
	
	public void setFiltersExpanded(boolean expanded) {
		expandFiltersButton.setVisible(!expanded);
		collapseFiltersButton.setVisible(expanded);
		secondFilterRowLayout.setVisible(expanded);
		dateFilterRowLayout.setVisible(expanded);
	}

	public void enter(ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
	}
	
	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;
		
		resetButton.setVisible(criteria.hasAnyFilterActive());
		
		updateStatusButtons();
		updateArchivedButton();
		
		classificationFilter.setValue(criteria.getContactClassification());
		diseaseFilter.setValue(criteria.getCaseDisease());
		regionFilter.setValue(criteria.getCaseRegion());
		districtFilter.setValue(criteria.getCaseDistrict());
		facilityFilter.setValue(criteria.getCaseFacility());
		officerFilter.setValue(criteria.getContactOfficer());
		followUpStatusFilter.setValue(criteria.getFollowUpStatus());
		reportedByFilter.setValue(criteria.getReportingUserRole());
		searchField.setValue(criteria.getNameUuidCaseLike());
		ContactDateType contactDateType = criteria.getReportDateFrom() != null ? ContactDateType.REPORT_DATE 
				: criteria.getLastContactDateFrom() != null ? ContactDateType.LAST_CONTACT_DATE : null;
		weekAndDateFilter.getDateTypeSelector().setValue(contactDateType);
		weekAndDateFilter.getDateFromFilter().setValue(contactDateType == ContactDateType.REPORT_DATE ? criteria.getReportDateFrom()
				: contactDateType == ContactDateType.LAST_CONTACT_DATE ? criteria.getLastContactDateFrom() : null);
		weekAndDateFilter.getDateToFilter().setValue(contactDateType == ContactDateType.REPORT_DATE ? criteria.getReportDateTo() 
				: contactDateType == ContactDateType.LAST_CONTACT_DATE ? criteria.getLastContactDateTo() : null);
		weekAndDateFilter.getDateFilterOptionFilter().setValue(DateFilterOption.DATE);
		
		boolean hasExpandedFilter = FieldHelper.streamFields(secondFilterRowLayout)
				.anyMatch(f -> !f.isEmpty());
		hasExpandedFilter |=  FieldHelper.streamFields(dateFilterRowLayout)
				.filter(f -> f != weekAndDateFilter.getDateFilterOptionFilter())
				.anyMatch(f -> !f.isEmpty());
		if (hasExpandedFilter) {
			setFiltersExpanded(true);
		}	
		
		applyingCriteria = false;
	}
	
	private void updateStatusButtons() {
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == criteria.getContactStatus()) {
				activeStatusButton = b;
			}
		});
		CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
		if (activeStatusButton != null) {
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getContainer().size())));
		}
	}
	
	private void updateArchivedButton() {
		if (switchArchivedActiveButton == null) {
			return;
		}
		
		if (Boolean.TRUE.equals(criteria.getArchived())) {
			getViewTitleLabel().setValue(I18nProperties.getPrefixCaption("View", viewName.replaceAll("/", ".") + ".archive"));
			switchArchivedActiveButton.setCaption(I18nProperties.getCaption(I18nProperties.getCaption(Captions.contactShowActive)));
			switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		} else {
			getViewTitleLabel().setValue(originalViewTitle);
			switchArchivedActiveButton.setCaption(I18nProperties.getCaption(I18nProperties.getCaption(Captions.contactShowArchived)));
			switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
		} 
	}
	
}

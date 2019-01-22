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

import org.apache.commons.lang3.StringUtils;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.CaseController;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
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

	public static final String SEARCH_FIELD = "searchField";

	private ContactGrid grid;    
	private VerticalLayout gridLayout;

	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	// Filters
	private ComboBox classificationFilter;
	private ComboBox diseaseFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox facilityFilter;
	private ComboBox officerFilter;
	private ComboBox followUpStatusFilter;
	private ComboBox reportedByFilter;
	private TextField searchField;
	
	private boolean showArchivedContacts = false;
	private String originalViewTitle;

	public ContactsView() {
		super(VIEW_NAME);
		
		originalViewTitle = getViewTitleLabel().getValue();

		grid = new ContactGrid(false);        
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

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EXPORT)) {

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

			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(grid.getContainerDataSource(), grid.getColumns(), "sormas_contacts", "sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(basicExportButton);

			Button extendedExportButton = new Button("Detailed Export");
			extendedExportButton.setDescription("Export the rows that are shown in the table below with an extended set of columns. This may take a while.");
			extendedExportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			extendedExportButton.setIcon(FontAwesome.FILE_TEXT);
			extendedExportButton.setWidth(100, Unit.PERCENTAGE);
			exportLayout.addComponent(extendedExportButton);

			StreamResource extendedExportStreamResource = DownloadUtil.createCsvExportStreamResource(ContactExportDto.class,
					(Integer start, Integer max) -> FacadeProvider.getContactFacade().getExportList(UserProvider.getCurrent().getUuid(), grid.getFilterCriteria(), start, max), 
					propertyId -> {
						return I18nProperties.getPrefixCaption(ContactExportDto.I18N_PREFIX, propertyId,
								I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, propertyId,
										I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, propertyId,
												I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, propertyId,
														I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, propertyId,
																I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX, propertyId))))));
					},
					"sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			new FileDownloader(extendedExportStreamResource).extend(extendedExportButton);

			// Warning if no filters have been selected
			Label warningLabel = new Label("<b>Warning:</b> No filters have been selected. Export may take a while.", ContentMode.HTML);
			exportLayout.addComponent(warningLabel);
			warningLabel.setVisible(false);

			exportButton.addClickListener(e -> {
				warningLabel.setVisible(!isAnyFilterEnabled());
			});
		}

		addComponent(gridLayout);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		classificationFilter = new ComboBox();
		classificationFilter.setWidth(140, Unit.PIXELS);
		classificationFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_CLASSIFICATION));
		classificationFilter.addItems((Object[]) ContactClassification.values());
		classificationFilter.addValueChangeListener(e -> {
			grid.setClassificationFilter((ContactClassification) e.getProperty().getValue());
		});
		filterLayout.addComponent(classificationFilter);

		diseaseFilter = new ComboBox();
		diseaseFilter.setWidth(140, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_DISEASE));
		diseaseFilter.addItems((Object[])Disease.values());
		diseaseFilter.addValueChangeListener(e -> {
			grid.setDiseaseFilter(((Disease)e.getProperty().getValue()));
		});
		filterLayout.addComponent(diseaseFilter);

		UserDto user = UserProvider.getCurrent().getUser();

		regionFilter = new ComboBox();
		if (user.getRegion() == null) {
			regionFilter.setWidth(140, Unit.PIXELS);
			regionFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_REGION_UUID));
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
			regionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
				grid.setRegionFilter(region);
			});
			filterLayout.addComponent(regionFilter);
		}

		districtFilter = new ComboBox();
		districtFilter.setWidth(140, Unit.PIXELS);
		districtFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_DISTRICT_UUID));
		districtFilter.setDescription("Select a district in the state");
		districtFilter.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
			grid.setDistrictFilter(district);
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
		filterLayout.addComponent(districtFilter);

		facilityFilter = new ComboBox();
		facilityFilter.setWidth(140, Unit.PIXELS);
		facilityFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_HEALTH_FACILITY_UUID));
		facilityFilter.setDescription("Select a facility in the LGA");
		facilityFilter.addValueChangeListener(e -> {
			FacilityReferenceDto facility = (FacilityReferenceDto) e.getProperty().getValue();
			grid.setHealthFacilityFilter(facility);
		});
		facilityFilter.setEnabled(false);
		filterLayout.addComponent(facilityFilter);

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
			grid.setContactOfficerFilter(officer);
		});
		filterLayout.addComponent(officerFilter);

		followUpStatusFilter = new ComboBox();
		followUpStatusFilter.setWidth(140, Unit.PIXELS);
		followUpStatusFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.FOLLOW_UP_STATUS));
		followUpStatusFilter.addItems((Object[])FollowUpStatus.values());
		followUpStatusFilter.addValueChangeListener(e -> {
			grid.setFollowUpStatusFilter(((FollowUpStatus)e.getProperty().getValue()));
		});
		filterLayout.addComponent(followUpStatusFilter);

		reportedByFilter = new ComboBox();
		reportedByFilter.setWidth(140, Unit.PIXELS);
		reportedByFilter.setInputPrompt("Reported By");
		reportedByFilter.addItems((Object[]) UserRole.values());
		reportedByFilter.addValueChangeListener(e -> {
			grid.setReportedByFilter((UserRole) e.getProperty().getValue());
		});
		filterLayout.addComponent(reportedByFilter);

		searchField = new TextField();
		searchField.setWidth(200, Unit.PIXELS);
		searchField.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, SEARCH_FIELD));
		searchField.addTextChangeListener(e -> {
			grid.setNameUuidCaseLike(e.getText());
		});
		filterLayout.addComponent(searchField);

		return filterLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		Button statusAll = new Button("All", e -> processStatusChange(null, e.getButton()));
		CssStyles.style(statusAll, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, "All");

		for (ContactStatus status : ContactStatus.values()) {
			Button statusButton = new Button(status.toString(), e -> processStatusChange(status, e.getButton()));
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
				Button switchArchivedActiveButton = new Button(I18nProperties.getCaption("showArchivedContacts"));
				switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
				switchArchivedActiveButton.addClickListener(e -> {
					showArchivedContacts = !showArchivedContacts;
					if (!showArchivedContacts) {
						getViewTitleLabel().setValue(originalViewTitle);
						switchArchivedActiveButton.setCaption(I18nProperties.getCaption("showArchivedContacts"));
						switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
						grid.getFilterCriteria().archived(false);
						grid.reload();
					} else {
						getViewTitleLabel().setValue(I18nProperties.getPrefixCaption("View", viewName.replaceAll("/", ".") + ".archive"));
						switchArchivedActiveButton.setCaption(I18nProperties.getCaption("showActiveContacts"));
						switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
						grid.getFilterCriteria().archived(true);
						grid.reload();
					}
				});
				actionButtonsLayout.addComponent(switchArchivedActiveButton);
			}

			// Bulk operation dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				statusFilterLayout.setWidth(100, Unit.PERCENTAGE);

				MenuBar bulkOperationsDropdown = new MenuBar();	
				MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem("Bulk Actions", null);

				Command changeCommand = selectedItem -> {
					ControllerProvider.getContactController().showBulkContactDataEditComponent(grid.getSelectedRows(), null);
				};
				bulkOperationsItem.addItem("Edit...", FontAwesome.ELLIPSIS_H, changeCommand);

				Command cancelFollowUpCommand = selectedItem -> {
					ControllerProvider.getContactController().cancelFollowUpOfAllSelectedItems(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.deselectAll();
							grid.reload();
						}
					});
				};
				bulkOperationsItem.addItem("Cancel follow-up", FontAwesome.TIMES, cancelFollowUpCommand);

				Command lostToFollowUpCommand = selectedItem -> {
					ControllerProvider.getContactController().setAllSelectedItemsToLostToFollowUp(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.deselectAll();
							grid.reload();
						}
					});
				};
				bulkOperationsItem.addItem("Set to lost to follow-up", FontAwesome.UNLINK, lostToFollowUpCommand);

				Command deleteCommand = selectedItem -> {
					ControllerProvider.getContactController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
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
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		activeStatusButton = statusAll;
		return statusFilterLayout;
	}

	private void updateActiveStatusButtonCaption() {
		if (activeStatusButton != null) {
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getContainer().size())));
		}
	}

	private void processStatusChange(ContactStatus contactStatus, Button button) {
		grid.setStatusFilter(contactStatus);
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
		});
		CssStyles.removeStyles(button, CssStyles.BUTTON_FILTER_LIGHT);
		activeStatusButton = button;
		updateActiveStatusButtonCaption();
	}

	private boolean isAnyFilterEnabled() {
		return classificationFilter.getValue() != null || diseaseFilter.getValue() != null || !StringUtils.isEmpty(searchField.getValue())
				|| (regionFilter.isVisible() && regionFilter.getValue() != null) || (districtFilter.isVisible() && districtFilter.getValue() != null) 
				|| facilityFilter.getValue() != null || officerFilter.getValue() != null || reportedByFilter.getValue() != null 
				|| followUpStatusFilter.getValue() != null;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		grid.reload();
		updateActiveStatusButtonCaption();
	}
}

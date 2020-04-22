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

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

import de.symeda.sormas.ui.utils.*;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.PopupDateField;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDateType;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
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
import de.symeda.sormas.api.visit.VisitResult;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.CaseController;
import de.symeda.sormas.ui.contact.importer.ContactsImportLayout;
import de.symeda.sormas.ui.dashboard.DateFilterOption;

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
	private ContactsViewConfiguration viewConfiguration;

	private FilteredGrid<?, ContactCriteria> grid;
	private VerticalLayout gridLayout;

	private MenuBar bulkOperationsDropdown;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	// Filters
	private HorizontalLayout firstFilterRowLayout;
	private HorizontalLayout secondFilterRowLayout;
	private HorizontalLayout dateFilterRowLayout;
	private ComboBox classificationFilter;
	private ComboBox diseaseFilter;
	private ComboBox caseClassificationFilter;
	private ComboBox regionFilter;
	private ComboBox districtFilter;
	private ComboBox officerFilter;
	private ComboBox followUpStatusFilter;
	private ComboBox reportedByFilter;
	private PopupDateField quarantineToFilter;
	private CheckBox onlyHighPriorityContacts;
	private TextField searchField;
	private Button resetButton;
	private EpiWeekAndDateFilterComponent<ContactDateType> weekAndDateFilter;
	private Button expandFiltersButton;
	private Button collapseFiltersButton;
	private ComboBox relevanceStatusFilter;
	private ComboBox categoryFilter;

	public ContactsView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(getClass()).get(ContactsViewConfiguration.class);
		if (viewConfiguration.getViewType() == null) {
			viewConfiguration.setViewType(ContactsViewType.CONTACTS_OVERVIEW);
		}
		criteria = ViewModelProviders.of(ContactsView.class).get(ContactCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		if (ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
			criteria.reportDateTo(DateHelper.getEndOfDay(new Date()));
			criteria.followUpUntilFrom(DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 7)));
			grid = new ContactFollowUpGrid(criteria, new Date(), getClass());
		} else {
			criteria.followUpUntilFrom(null);
			grid = new ContactGrid(criteria, getClass());
		}
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		if (ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
			gridLayout.addComponent(createFollowUpLegend());
		}
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");
		grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());

		OptionGroup contactsViewSwitcher = new OptionGroup();
		CssStyles.style(contactsViewSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		contactsViewSwitcher.addItem(ContactsViewType.CONTACTS_OVERVIEW);
		contactsViewSwitcher.setItemCaption(ContactsViewType.CONTACTS_OVERVIEW,
				I18nProperties.getCaption(Captions.contactContactsOverview));

		contactsViewSwitcher.addItem(ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW);
		contactsViewSwitcher.setItemCaption(ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW,
				I18nProperties.getCaption(Captions.contactFollowUpVisitsOverview));

		contactsViewSwitcher.setValue(viewConfiguration.getViewType());
		contactsViewSwitcher.addValueChangeListener(e -> {
			if (ContactsViewType.CONTACTS_OVERVIEW.equals(e.getProperty().getValue())) {
				viewConfiguration.setViewType(ContactsViewType.CONTACTS_OVERVIEW);
				SormasUI.get().getNavigator().navigateTo(ContactsView.VIEW_NAME);
			} else {
				viewConfiguration.setViewType(ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW);
				SormasUI.get().getNavigator().navigateTo(ContactsView.VIEW_NAME);
			}
		});
		addHeaderComponent(contactsViewSwitcher);

		if (ContactsViewType.CONTACTS_OVERVIEW.equals(viewConfiguration.getViewType())
				&& UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_IMPORT)) {
			Button importButton = new Button(I18nProperties.getCaption(Captions.actionImport));
			importButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			importButton.setIcon(VaadinIcons.UPLOAD);

			importButton.addClickListener(e -> {
				Window popupWindow = VaadinUiUtil.showPopupWindow(new ContactsImportLayout());
				popupWindow.setCaption(I18nProperties.getString(Strings.headingImportContacts));
				popupWindow.addCloseListener(c -> {
					ContactGrid grid = (ContactGrid) this.grid;
					grid.reload();
				});
			});

			addHeaderComponent(importButton);
		}

		if (ContactsViewType.CONTACTS_OVERVIEW.equals(viewConfiguration.getViewType()) && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EXPORT)) {
			PopupButton exportButton = new PopupButton(I18nProperties.getCaption(Captions.export)); 
			VerticalLayout exportLayout = new VerticalLayout();
			{
				exportButton.setIcon(VaadinIcons.DOWNLOAD);
				exportLayout.setSpacing(true); 
				exportLayout.setMargin(true);
				exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				exportLayout.setWidth(200, Unit.PIXELS);
				exportButton.setContent(exportLayout);
				addHeaderComponent(exportButton);
			}
			{
				StreamResource streamResource = new GridExportStreamResource(grid, "sormas_contacts", "sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv");
				
				addExportButton(streamResource, exportButton, exportLayout, null, VaadinIcons.TABLE, Captions.exportBasic, Descriptions.descExportButton);
			}
			{
				StreamResource extendedExportStreamResource = DownloadUtil.createCsvExportStreamResource(ContactExportDto.class, null,
						(Integer start, Integer max) -> FacadeProvider.getContactFacade().getExportList(grid.getCriteria(), start, max, I18nProperties.getUserLanguage()),
						(propertyId,type) -> {
							String caption = I18nProperties.getPrefixCaption(ContactExportDto.I18N_PREFIX, propertyId,
									I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, propertyId,
											I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, propertyId,
													I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, propertyId,
															I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, propertyId,
																	I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, propertyId,
																			I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX, propertyId)))))));
							if (Date.class.isAssignableFrom(type)) {
								caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
							}
							return caption;
						},
						"sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv", null);
				
				addExportButton(extendedExportStreamResource, exportButton, exportLayout, null, VaadinIcons.FILE_TEXT, Captions.exportDetailed, Descriptions.descDetailedExportButton);
			}

			// Warning if no filters have been selected
			{
				Label warningLabel = new Label(I18nProperties.getString(Strings.infoExportNoFilters));
				warningLabel.setWidth(100, Unit.PERCENTAGE);
				exportLayout.addComponent(warningLabel);
				warningLabel.setVisible(false);
	
				exportButton.addClickListener(e -> warningLabel.setVisible(!criteria.hasAnyFilterActive()));
			}
		}

		if (ContactsViewType.CONTACTS_OVERVIEW.equals(viewConfiguration.getViewType()) && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			Button btnEnterBulkEditMode = new Button(I18nProperties.getCaption(Captions.actionEnterBulkEditMode));
			{
				btnEnterBulkEditMode.setId("enterBulkEditMode");
				btnEnterBulkEditMode.setIcon(VaadinIcons.CHECK_SQUARE_O);
				btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
				addHeaderComponent(btnEnterBulkEditMode);
			}

			Button btnLeaveBulkEditMode = new Button(I18nProperties.getCaption(Captions.actionLeaveBulkEditMode));
			{
				btnLeaveBulkEditMode.setId("leaveBulkEditMode");
				btnLeaveBulkEditMode.setIcon(VaadinIcons.CLOSE);
				btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
				btnLeaveBulkEditMode.setStyleName(ValoTheme.BUTTON_PRIMARY);
				addHeaderComponent(btnLeaveBulkEditMode);
			}

			btnEnterBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(true);
				viewConfiguration.setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				searchField.setEnabled(false);
				((ContactGrid) grid).setEagerDataProvider();
				((ContactGrid) grid).reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(false);
				viewConfiguration.setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				searchField.setEnabled(true);
				navigateTo(criteria);
			});
		}

		if (ContactsViewType.CONTACTS_OVERVIEW.equals(viewConfiguration.getViewType()) && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CREATE)) {
			Button btnNewContact = new Button(I18nProperties.getCaption(Captions.contactNewContact));
			btnNewContact.addStyleName(ValoTheme.BUTTON_PRIMARY);
			btnNewContact.setIcon(VaadinIcons.PLUS_CIRCLE);
			btnNewContact.addClickListener(e -> ControllerProvider.getContactController().create());
			addHeaderComponent(btnNewContact);
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
			diseaseFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.DISEASE));
			diseaseFilter.addItems(FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true).toArray());
			diseaseFilter.addValueChangeListener(e -> {
				criteria.disease(((Disease)e.getProperty().getValue()));
				navigateTo(criteria);
			});
			firstFilterRowLayout.addComponent(diseaseFilter);

			caseClassificationFilter = new ComboBox();
			caseClassificationFilter.setWidth(140, Unit.PIXELS);
			caseClassificationFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_CLASSIFICATION));
			caseClassificationFilter.setDescription(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_CLASSIFICATION));
			caseClassificationFilter.addItems((Object[]) CaseClassification.values());
			caseClassificationFilter.addValueChangeListener(e -> {
				criteria.caseClassification(((CaseClassification) e.getProperty().getValue()));
				navigateTo(criteria);
			});
			firstFilterRowLayout.addComponent(caseClassificationFilter);

			if (isGermanServer()) {
				categoryFilter = new ComboBox();
				categoryFilter.setWidth(140, Unit.PIXELS);
				categoryFilter.setInputPrompt(
						I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_CATEGORY));
				categoryFilter.addItems((Object[]) ContactCategory.values());
				categoryFilter.addValueChangeListener(e -> {
					criteria.contactCategory(((ContactCategory) e.getProperty().getValue()));
					navigateTo(criteria);
				});
				firstFilterRowLayout.addComponent(categoryFilter);
			}

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
				if (ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
					((ContactFollowUpGrid) grid).reload();
				} else {
					((ContactGrid) grid).reload();
				}
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
				regionFilter.setWidth(240, Unit.PIXELS);
				regionFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.REGION_UUID));
				regionFilter.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
				regionFilter.addValueChangeListener(e -> {
					RegionReferenceDto region = (RegionReferenceDto) e.getProperty().getValue();
					if (region != null) {
						officerFilter.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(region, UserRole.CONTACT_OFFICER));
					} else {
						officerFilter.removeAllItems();
					}
					criteria.region(region);
					navigateTo(criteria);
				});
				secondFilterRowLayout.addComponent(regionFilter);
			}

			districtFilter = new ComboBox();
			districtFilter.setWidth(240, Unit.PIXELS);
			districtFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.DISTRICT_UUID));
			districtFilter.setDescription(I18nProperties.getDescription(Descriptions.descDistrictFilter));
			districtFilter.addValueChangeListener(e -> {
				DistrictReferenceDto district = (DistrictReferenceDto) e.getProperty().getValue();
				criteria.district(district);
				navigateTo(criteria);
			});

			if (user.getRegion() != null && user.getDistrict() == null) {	
				districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(user.getRegion().getUuid()));
				districtFilter.setEnabled(true);
			} else {
				regionFilter.addValueChangeListener(e -> {
					RegionReferenceDto region = (RegionReferenceDto)e.getProperty().getValue();
					districtFilter.removeAllItems();
					if (region != null) {
						districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
						districtFilter.setEnabled(true);
					} else {
						districtFilter.setEnabled(false);
					}
				});
				districtFilter.setEnabled(false);
			}
			secondFilterRowLayout.addComponent(districtFilter);

			Label infoLabel = new Label(VaadinIcons.INFO_CIRCLE.getHtml(), ContentMode.HTML);
			infoLabel.setSizeUndefined();
			infoLabel.setDescription(I18nProperties.getString(Strings.infoContactsViewRegionDistrictFilter), ContentMode.HTML);
			CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
			secondFilterRowLayout.addComponent(infoLabel);

			officerFilter = new ComboBox();
			officerFilter.setWidth(140, Unit.PIXELS);
			officerFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER_UUID));
			officerFilter.addValueChangeListener(e -> {
				UserReferenceDto officer = (UserReferenceDto) e.getProperty().getValue();
				criteria.contactOfficer(officer);
				navigateTo(criteria);
			});
			if (user.getRegion() != null) {
				officerFilter.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(user.getRegion(), UserRole.CONTACT_OFFICER));
			}
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

			quarantineToFilter = new PopupDateField();
			quarantineToFilter.setWidth(200, Unit.PIXELS);
			quarantineToFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.QUARANTINE_TO));
			quarantineToFilter.addValueChangeListener(e -> {
				criteria.quarantineTo((Date) e.getProperty().getValue());
				navigateTo(criteria);
			});
			secondFilterRowLayout.addComponent(quarantineToFilter);

			onlyHighPriorityContacts = new CheckBox();
			onlyHighPriorityContacts.setCaption(I18nProperties.getCaption(Captions.contactOnlyHighPriorityContacts));
			CssStyles.style(onlyHighPriorityContacts, CssStyles.CHECKBOX_FILTER_INLINE);
			onlyHighPriorityContacts.addValueChangeListener(e -> {
				criteria.onlyHighPriorityContacts((Boolean) e.getProperty().getValue());
				navigateTo(criteria);
			});
			secondFilterRowLayout.addComponent(onlyHighPriorityContacts);
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
		if (ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
			CssStyles.style(statusAll, CssStyles.FORCE_CAPTION);
		}
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
			if (ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
				CssStyles.style(statusButton, CssStyles.FORCE_CAPTION);
			}
			statusButton.setCaptionAsHtml(true);
			statusFilterLayout.addComponent(statusButton);
			statusButtons.put(statusButton, status.toString());
		}

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (ContactsViewType.CONTACTS_OVERVIEW.equals(viewConfiguration.getViewType()) && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW_ARCHIVED)) {
				relevanceStatusFilter = new ComboBox();
				relevanceStatusFilter.setWidth(140, Unit.PERCENTAGE);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.contactActiveContacts));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.contactArchivedContacts));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.contactAllContacts));
				relevanceStatusFilter.addValueChangeListener(e -> {
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);
			}

			// Bulk operation dropdown
			if (ContactsViewType.CONTACTS_OVERVIEW.equals(viewConfiguration.getViewType()) && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				statusFilterLayout.setWidth(100, Unit.PERCENTAGE);

				bulkOperationsDropdown = new MenuBar();	
				MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem(I18nProperties.getCaption(Captions.bulkActions), null);

				Command changeCommand = mi -> ControllerProvider.getContactController().showBulkContactDataEditComponent(((ContactGrid) grid).asMultiSelect().getSelectedItems(), null);
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkEdit), VaadinIcons.ELLIPSIS_H, changeCommand);

				Command cancelFollowUpCommand = mi -> ControllerProvider.getContactController().cancelFollowUpOfAllSelectedItems(((ContactGrid) grid).asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkCancelFollowUp), VaadinIcons.CLOSE, cancelFollowUpCommand);

				Command lostToFollowUpCommand = mi -> ControllerProvider.getContactController().setAllSelectedItemsToLostToFollowUp(((ContactGrid) grid).asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkLostToFollowUp), VaadinIcons.UNLINK, lostToFollowUpCommand);

				Command deleteCommand = mi -> ControllerProvider.getContactController().deleteAllSelectedItems(((ContactGrid) grid).asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, deleteCommand);

				bulkOperationsDropdown.setVisible(viewConfiguration.isInEagerMode());
				actionButtonsLayout.addComponent(bulkOperationsDropdown);
			}

			// Follow-up overview scrolling
			if (ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
				statusFilterLayout.setWidth(100, Unit.PERCENTAGE);

				HorizontalLayout scrollLayout = new HorizontalLayout();
				scrollLayout.setMargin(false);

				DateField followUpReferenceDate = new DateField(I18nProperties.getCaption(Captions.contactFollowUpOverviewReferenceDate), LocalDate.now());

				Button minusDaysButton = new Button(I18nProperties.getCaption(Captions.contactMinusDays));
				CssStyles.style(minusDaysButton, ValoTheme.BUTTON_PRIMARY, CssStyles.FORCE_CAPTION);
				minusDaysButton.addClickListener(e -> {
					followUpReferenceDate.setValue(followUpReferenceDate.getValue().minusDays(8));
				});
				scrollLayout.addComponent(minusDaysButton);

				followUpReferenceDate.addValueChangeListener(e -> {
					Date newDate = e.getValue() != null ? DateHelper8.toDate(e.getValue()) : new Date();

					applyingCriteria = true;

					((ContactFollowUpGrid) grid).setReferenceDate(newDate);
					criteria.reportDateTo(DateHelper.getEndOfDay(newDate));
					criteria.followUpUntilFrom(DateHelper.getStartOfDay(DateHelper.subtractDays(newDate, 7)));

					applyingCriteria = false;

					((ContactFollowUpGrid) grid).reload();
				});
				scrollLayout.addComponent(followUpReferenceDate);

				Button plusDaysButton = new Button(I18nProperties.getCaption(Captions.contactPlusDays));
				CssStyles.style(plusDaysButton, ValoTheme.BUTTON_PRIMARY, CssStyles.FORCE_CAPTION);
				plusDaysButton.addClickListener(e -> {
					followUpReferenceDate.setValue(followUpReferenceDate.getValue().plusDays(8));
				});
				scrollLayout.addComponent(plusDaysButton);

				actionButtonsLayout.addComponent(scrollLayout);

			}
		}
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterLayout;
	}

	private HorizontalLayout createFollowUpLegend() {
		HorizontalLayout legendLayout = new HorizontalLayout();
		legendLayout.setSpacing(false);
		CssStyles.style(legendLayout, CssStyles.VSPACE_TOP_4);

		Label notSymptomaticColor = new Label("");
		styleLegendEntry(notSymptomaticColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_NOT_SYMPTOMATIC, true);
		legendLayout.addComponent(notSymptomaticColor);

		Label notSymptomaticLabel = new Label(VisitResult.NOT_SYMPTOMATIC.toString());
		legendLayout.addComponent(notSymptomaticLabel);

		Label symptomaticColor = new Label("");
		styleLegendEntry(symptomaticColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_SYMPTOMATIC, false);
		legendLayout.addComponent(symptomaticColor);

		Label symptomaticLabel = new Label(VisitResult.SYMPTOMATIC.toString());
		legendLayout.addComponent(symptomaticLabel);

		Label unavailableColor = new Label("");
		styleLegendEntry(unavailableColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_UNAVAILABLE, false);
		legendLayout.addComponent(unavailableColor);

		Label unavailableLabel = new Label(VisitResult.UNAVAILABLE.toString());
		legendLayout.addComponent(unavailableLabel);

		Label uncooperativeColor = new Label("");
		styleLegendEntry(uncooperativeColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_UNCOOPERATIVE, false);
		legendLayout.addComponent(uncooperativeColor);

		Label uncooperativeLabel = new Label(VisitResult.UNCOOPERATIVE.toString());
		legendLayout.addComponent(uncooperativeLabel);

		Label notPerformedColor = new Label("");
		styleLegendEntry(notPerformedColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_NOT_PERFORMED, false);
		legendLayout.addComponent(notPerformedColor);

		Label notPerformedLabel = new Label(VisitResult.NOT_PERFORMED.toString());
		legendLayout.addComponent(notPerformedLabel);

		return legendLayout;
	}

	private void styleLegendEntry(Label label, String style, boolean first) {
		label.setHeight(18, Unit.PIXELS);
		label.setWidth(12, Unit.PIXELS);
		CssStyles.style(label, style, CssStyles.HSPACE_RIGHT_4);

		if (!first) {
			CssStyles.style(label, CssStyles.HSPACE_LEFT_3);
		}
	}

	private void addShowMoreOrLessFiltersButtons(HorizontalLayout parentLayout) {
		expandFiltersButton = new Button(I18nProperties.getCaption(Captions.actionShowMoreFilters), VaadinIcons.CHEVRON_DOWN);
		CssStyles.style(expandFiltersButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		collapseFiltersButton = new Button(I18nProperties.getCaption(Captions.actionShowLessFilters), VaadinIcons.CHEVRON_UP);
		CssStyles.style(collapseFiltersButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);

		expandFiltersButton.addClickListener(e -> setFiltersExpanded(true));

		collapseFiltersButton.addClickListener(e -> setFiltersExpanded(false));

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

		if (ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
			((ContactFollowUpGrid) grid).reload();
		} else {
			((ContactGrid) grid).reload();
		}
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		resetButton.setVisible(criteria.hasAnyFilterActive());

		updateStatusButtons();
		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}
		classificationFilter.setValue(criteria.getContactClassification());
		diseaseFilter.setValue(criteria.getDisease());
		regionFilter.setValue(criteria.getRegion());
		districtFilter.setValue(criteria.getDistrict());
		officerFilter.setValue(criteria.getContactOfficer());
		followUpStatusFilter.setValue(criteria.getFollowUpStatus());
		reportedByFilter.setValue(criteria.getReportingUserRole());
		quarantineToFilter.setValue(criteria.getQuarantineTo());
		onlyHighPriorityContacts.setValue(criteria.getOnlyHighPriorityContacts());
		searchField.setValue(criteria.getNameUuidCaseLike());
		if (categoryFilter != null) {
			categoryFilter.setValue(criteria.getContactCategory());
		}
		caseClassificationFilter.setValue(criteria.getCaseClassification());

		ContactDateType contactDateType = criteria.getReportDateFrom() != null ? ContactDateType.REPORT_DATE 
				: criteria.getLastContactDateFrom() != null ? ContactDateType.LAST_CONTACT_DATE : null;
		weekAndDateFilter.getDateTypeSelector().setValue(contactDateType);
		Date dateFrom = contactDateType == ContactDateType.REPORT_DATE ? criteria.getReportDateFrom()
				: contactDateType == ContactDateType.LAST_CONTACT_DATE ? criteria.getLastContactDateFrom() : null;
				Date dateTo = contactDateType == ContactDateType.REPORT_DATE ? criteria.getReportDateTo() 
						: contactDateType == ContactDateType.LAST_CONTACT_DATE ? criteria.getLastContactDateTo() : null;
						// Reconstruct date/epi week choice
						if ((dateFrom != null && dateTo != null && (DateHelper.getEpiWeekStart(DateHelper.getEpiWeek(dateFrom)).equals(dateFrom) && DateHelper.getEpiWeekEnd(DateHelper.getEpiWeek(dateTo)).equals(dateTo)))
								|| (dateFrom != null && DateHelper.getEpiWeekStart(DateHelper.getEpiWeek(dateFrom)).equals(dateFrom))
								|| (dateTo != null && DateHelper.getEpiWeekEnd(DateHelper.getEpiWeek(dateTo)).equals(dateTo))) {
							weekAndDateFilter.getDateFilterOptionFilter().setValue(DateFilterOption.EPI_WEEK);
							weekAndDateFilter.getWeekFromFilter().setValue(DateHelper.getEpiWeek(dateFrom));
							weekAndDateFilter.getWeekToFilter().setValue(DateHelper.getEpiWeek(dateTo));
						} else {
							weekAndDateFilter.getDateFilterOptionFilter().setValue(DateFilterOption.DATE);
							weekAndDateFilter.getDateFromFilter().setValue(dateFrom);
							weekAndDateFilter.getDateToFilter().setValue(dateTo);
						}

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
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) 
					+ LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}
	}

}

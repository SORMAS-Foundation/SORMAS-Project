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

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.ui.caze.CasesView;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
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
import de.symeda.sormas.api.contact.OrderMeans;
import de.symeda.sormas.api.contact.QuarantineType;
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
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateHelper8;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

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
	private ContactsFilterForm filterForm;
	private ComboBox relevanceStatusFilter;

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
		contactsViewSwitcher.setId("contactsViewSwitcher");
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
			importButton.setId("contactImport");
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
			exportButton.setId("contactExport");
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
						(Integer start, Integer max) -> FacadeProvider.getContactFacade().getExportList(grid.getCriteria(), start, max),
						(propertyId,type) -> {
							String caption = I18nProperties.getPrefixCaption(ContactExportDto.I18N_PREFIX, propertyId,
									I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, propertyId,
											I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, propertyId,
													I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, propertyId,
															I18nProperties.getPrefixCaption(LocationDto.I18N_PREFIX, propertyId,
																	I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, propertyId,
																			I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX, propertyId)))))));
							if (Date.class.isAssignableFrom(type)) {
								caption += " (" + DateHelper.getLocalShortDatePattern() + ")";
							}
							return caption;
						},
						"sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv", null);

				addExportButton(extendedExportStreamResource, exportButton, exportLayout, null, VaadinIcons.FILE_TEXT, Captions.exportDetailed, Descriptions.descDetailedExportButton);
			}

			// Warning if no filters have been selected
			{
				Label warningLabel = new Label(I18nProperties.getString(Strings.infoExportNoFilters));
				warningLabel.setId("contactWarningLabel");
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
				filterForm.setSearchFieldEnabled(false);
				((ContactGrid) grid).setEagerDataProvider();
				((ContactGrid) grid).reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(false);
				viewConfiguration.setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				filterForm.setSearchFieldEnabled(true);
				navigateTo(criteria);
			});
		}

		if (ContactsViewType.CONTACTS_OVERVIEW.equals(viewConfiguration.getViewType()) && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CREATE)) {
			Button btnNewContact = new Button(I18nProperties.getCaption(Captions.contactNewContact));
			btnNewContact.setId("newContact");
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

		filterForm = new ContactsFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!navigateTo(criteria)) {
				if (ContactsViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
					((ContactFollowUpGrid) grid).reload();
				} else {
					((ContactGrid) grid).reload();
				}
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(ContactsView.class).remove(ContactCriteria.class);
			navigateTo(null);
		});
		filterLayout.addComponent(filterForm);

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
		statusAll.setId("statusAll");
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
			statusButton.setId("status-button-" + status.name());
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
				relevanceStatusFilter.setId("relevanceStatus");
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
				bulkOperationsDropdown.setId("bulkOperationsDropdown");
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
				followUpReferenceDate.setId("followUpReferenceDate");

				Button minusDaysButton = new Button(I18nProperties.getCaption(Captions.contactMinusDays));
				minusDaysButton.setId("minusDaysButton");
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
				plusDaysButton.setId("plusDaysButton");
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
		filterForm.setValue(criteria);
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		updateStatusButtons();
		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
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

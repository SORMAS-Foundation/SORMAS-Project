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
package de.symeda.sormas.ui.caze;

import java.util.Date;
import java.util.HashMap;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.contact.ContactGrid;
import de.symeda.sormas.ui.contact.importer.CaseContactsImportLayout;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

public class CaseContactsView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/contacts";

	private ContactCriteria criteria;
	private ViewConfiguration viewConfiguration;
	
	private ContactGrid grid;  
	
	//Filters
	private ComboBox classificationFilter;
	private ComboBox districtFilter;
	private ComboBox officerFilter;
	private Button resetButton;
	
	private Button newButton;
	private VerticalLayout gridLayout;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	public CaseContactsView() {
		super(VIEW_NAME);
		setSizeFull();

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);
		viewConfiguration.setInEagerMode(true);
		criteria = ViewModelProviders.of(CaseContactsView.class).get(ContactCriteria.class);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setSizeUndefined();

		classificationFilter = new ComboBox();
		classificationFilter.setWidth(240, Unit.PIXELS);
		classificationFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_CLASSIFICATION));
		classificationFilter.addValueChangeListener(e -> {
			criteria.contactClassification((ContactClassification) e.getProperty().getValue());
			navigateTo(criteria);
		});
		topLayout.addComponent(classificationFilter);

		districtFilter = new ComboBox();
		districtFilter.setWidth(240, Unit.PIXELS);
		districtFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CASE_DISTRICT_UUID));
		districtFilter.addValueChangeListener(e -> {
			criteria.caseDistrict((DistrictReferenceDto) e.getProperty().getValue());
			navigateTo(criteria);
		});
		topLayout.addComponent(districtFilter);

		officerFilter = new ComboBox();
		officerFilter.setWidth(240, Unit.PIXELS);
		officerFilter.setInputPrompt(I18nProperties.getPrefixCaption(ContactIndexDto.I18N_PREFIX, ContactIndexDto.CONTACT_OFFICER_UUID));
		officerFilter.addValueChangeListener(e -> {
			criteria.contactOfficer((UserReferenceDto) e.getProperty().getValue());
			navigateTo(criteria);
		});
		topLayout.addComponent(officerFilter);

		resetButton = new Button(I18nProperties.getCaption(Captions.actionResetFilters));
		resetButton.setVisible(false);
		resetButton.addClickListener(event -> {
			ViewModelProviders.of(CaseContactsView.class).remove(ContactCriteria.class);
			navigateTo(null);
		});
		topLayout.addComponent(resetButton);

		return topLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth("100%");
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
		statusFilterLayout.setExpandRatio(statusFilterLayout.getComponent(statusFilterLayout.getComponentCount()-1), 1);

		// Bulk operation dropdown
		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			statusFilterLayout.setWidth(100, Unit.PERCENTAGE);

			MenuBar bulkOperationsDropdown = new MenuBar();	
			MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem(I18nProperties.getCaption(Captions.bulkActions), null);

			Command changeCommand = selectedItem -> {
				ControllerProvider.getContactController().showBulkContactDataEditComponent(grid.asMultiSelect().getSelectedItems(), getCaseRef().getUuid());
			};
			bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkEdit), VaadinIcons.ELLIPSIS_H, changeCommand);

			Command cancelFollowUpCommand = selectedItem -> {
				ControllerProvider.getContactController().cancelFollowUpOfAllSelectedItems(grid.asMultiSelect().getSelectedItems(), new Runnable() {
					public void run() {
						navigateTo(criteria);
					}
				});
			};
			bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkCancelFollowUp), VaadinIcons.CLOSE, cancelFollowUpCommand);

			Command lostToFollowUpCommand = selectedItem -> {
				ControllerProvider.getContactController().setAllSelectedItemsToLostToFollowUp(grid.asMultiSelect().getSelectedItems(), new Runnable() {
					public void run() {
						navigateTo(criteria);
					}
				});
			};
			bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkLostToFollowUp), VaadinIcons.UNLINK, lostToFollowUpCommand);

			Command deleteCommand = selectedItem -> {
				ControllerProvider.getContactController().deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), new Runnable() {
					public void run() {
						navigateTo(criteria);
					}
				});
			};
			bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, deleteCommand);

			statusFilterLayout.addComponent(bulkOperationsDropdown);
			statusFilterLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			statusFilterLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_IMPORT)) {
			Button importButton = new Button(I18nProperties.getCaption(Captions.actionImport));
			importButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			importButton.setIcon(VaadinIcons.UPLOAD);

			importButton.addClickListener(e -> {
				Window popupWindow = VaadinUiUtil
						.showPopupWindow(new CaseContactsImportLayout(criteria.getCaze(), criteria.getCaseDisease()));
				popupWindow.setCaption(I18nProperties.getString(Strings.headingImportCaseContacts));
				popupWindow.addCloseListener(c -> {
					grid.reload();
				});
			});

			statusFilterLayout.addComponent(importButton);
			statusFilterLayout.setComponentAlignment(importButton, Alignment.MIDDLE_RIGHT);
			if (!UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				statusFilterLayout.setExpandRatio(importButton, 1);
			}
		}
		
		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EXPORT)) {
			Button exportButton = new Button(I18nProperties.getCaption(Captions.export));
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(VaadinIcons.DOWNLOAD);

			StreamResource streamResource = new GridExportStreamResource(grid, "sormas_contacts", "sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);

			statusFilterLayout.addComponent(exportButton);
			statusFilterLayout.setComponentAlignment(exportButton, Alignment.MIDDLE_RIGHT);
			if (!UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				statusFilterLayout.setExpandRatio(exportButton, 1);
			}
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CREATE)) {
			newButton = new Button(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, Captions.contactNewContact));
			newButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			newButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			newButton.addClickListener(e -> ControllerProvider.getContactController().create(this.getCaseRef()));
			statusFilterLayout.addComponent(newButton);
			statusFilterLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);
		}

		statusFilterLayout.addStyleName("top-bar");
		activeStatusButton = statusAll;
		return statusFilterLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);

		criteria.caze(getCaseRef());
		
		if (grid == null) {
			grid = new ContactGrid(criteria, getClass());
			gridLayout = new VerticalLayout();
			gridLayout.addComponent(createFilterBar());
			gridLayout.addComponent(createStatusFilterBar());
			gridLayout.addComponent(grid);
			gridLayout.setMargin(true);
			gridLayout.setSpacing(false);
			gridLayout.setSizeFull();
			gridLayout.setExpandRatio(grid, 1);
			grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());

			setSubComponent(gridLayout);
		}

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
		
		classificationFilter.removeAllItems();
		classificationFilter.addItems((Object[]) ContactClassification.values());
		classificationFilter.setValue(criteria.getContactClassification());

		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());

		districtFilter.removeAllItems();
		districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(caseDto.getRegion().getUuid()));
		districtFilter.setValue(criteria.getCaseDistrict());

		officerFilter.removeAllItems();
		officerFilter.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(caseDto.getRegion(), UserRole.CONTACT_OFFICER));
		officerFilter.setValue(criteria.getContactOfficer());
		
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

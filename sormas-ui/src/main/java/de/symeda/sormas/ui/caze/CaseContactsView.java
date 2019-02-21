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

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.contact.ContactGrid;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class CaseContactsView extends AbstractCaseView {

	private static final long serialVersionUID = -1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/contacts";

	private ContactCriteria criteria;
	
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
		
		criteria = ViewModelProviders.of(CaseContactsView.class).get(ContactCriteria.class);
		
		grid = new ContactGrid(true);
		grid.setCriteria(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		grid.getContainer().addItemSetChangeListener(e -> {
			updateStatusButtons();
		});

		setSubComponent(gridLayout);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth(100, Unit.PERCENTAGE);

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

		topLayout.setExpandRatio(resetButton, 1);
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
				ControllerProvider.getContactController().showBulkContactDataEditComponent(grid.getSelectedRows(), getCaseRef().getUuid());
			};
			bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkEdit), FontAwesome.ELLIPSIS_H, changeCommand);

			Command cancelFollowUpCommand = selectedItem -> {
				ControllerProvider.getContactController().cancelFollowUpOfAllSelectedItems(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload();
					}
				});
			};
			bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkCancelFollowUp), FontAwesome.TIMES, cancelFollowUpCommand);

			Command lostToFollowUpCommand = selectedItem -> {
				ControllerProvider.getContactController().setAllSelectedItemsToLostToFollowUp(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload();
					}
				});
			};
			bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkLostToFollowUp), FontAwesome.UNLINK, lostToFollowUpCommand);

			Command deleteCommand = selectedItem -> {
				ControllerProvider.getContactController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
					public void run() {
						grid.deselectAll();
						grid.reload();
					}
				});
			};
			bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkDelete), FontAwesome.TRASH, deleteCommand);

			statusFilterLayout.addComponent(bulkOperationsDropdown);
			statusFilterLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);
			statusFilterLayout.setExpandRatio(bulkOperationsDropdown, 1);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EXPORT)) {
			Button exportButton = new Button(I18nProperties.getCaption(Captions.export));
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(FontAwesome.DOWNLOAD);

			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(grid.getContainerDataSource(), grid.getColumns(), "sormas_contacts", "sormas_contacts_" + DateHelper.formatDateForExport(new Date()) + ".csv");
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
			newButton.setIcon(FontAwesome.PLUS_CIRCLE);
			newButton.addClickListener(e -> ControllerProvider.getContactController().create(this.getCaseRef()));
			statusFilterLayout.addComponent(newButton);
			statusFilterLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);
		}

		statusFilterLayout.addStyleName("top-bar");
		activeStatusButton = statusAll;
		return statusFilterLayout;
	}

	private void update() {
		grid.setCaseFilter(getCaseRef());

		CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getCaseRef().getUuid());

		classificationFilter.removeAllItems();
		classificationFilter.addItems((Object[]) ContactClassification.values());

		districtFilter.removeAllItems();
		districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(caseDto.getRegion().getUuid()));

		officerFilter.removeAllItems();
		officerFilter.addItems(FacadeProvider.getUserFacade().getUsersByRegionAndRoles(caseDto.getRegion(), UserRole.CONTACT_OFFICER));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		update();		
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
		
		classificationFilter.setValue(criteria.getContactClassification());
		districtFilter.setValue(criteria.getCaseDistrict());
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
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getContainer().size())));
		}
	}

}

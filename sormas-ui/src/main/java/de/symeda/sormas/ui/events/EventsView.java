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
package de.symeda.sormas.ui.events;

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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.LayoutUtil;

public class EventsView extends AbstractView {

	private static final long serialVersionUID = -3048977745713631200L;

	public static final String VIEW_NAME = "events";

	private EventCriteria criteria;

	private EventGrid grid;
	private Button createButton;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	// Filter
	private ComboBox typeFilter;
	private ComboBox diseaseFilter;
	private ComboBox reportedByFilter;
	private Button resetButton;

	private VerticalLayout gridLayout;

	private Button switchArchivedActiveButton;
	private String originalViewTitle;

	// Bulk operations
	private MenuItem archiveItem;
	private MenuItem dearchiveItem;

	public EventsView() {
		super(VIEW_NAME);

		originalViewTitle = getViewTitleLabel().getValue();

		criteria = ViewModelProviders.of(EventsView.class).get(EventCriteria.class);

		grid = new EventGrid();
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

		addComponent(gridLayout);

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_EXPORT)) {
			Button exportButton = new Button(I18nProperties.getCaption(Captions.export));
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(FontAwesome.DOWNLOAD);

			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(grid.getContainerDataSource(), grid.getColumns(), "sormas_events", "sormas_events_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);

			addHeaderComponent(exportButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE)) {
			createButton = new Button(I18nProperties.getCaption(Captions.eventNewEvent));
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(FontAwesome.PLUS_CIRCLE);
			createButton.addClickListener(e -> ControllerProvider.getEventController().create());
			addHeaderComponent(createButton);
		}
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();

		typeFilter = new ComboBox();
		typeFilter.setWidth(140, Unit.PIXELS);
		typeFilter.setInputPrompt(I18nProperties.getPrefixCaption(EventIndexDto.I18N_PREFIX, EventIndexDto.EVENT_TYPE));
		typeFilter.addItems((Object[])EventType.values());
		typeFilter.addValueChangeListener(e -> {
			criteria.eventType(((EventType)e.getProperty().getValue()));
			navigateTo(criteria);
		});
		filterLayout.addComponent(typeFilter);

		diseaseFilter = new ComboBox();
		diseaseFilter.setWidth(140, Unit.PIXELS);
		diseaseFilter.setInputPrompt(I18nProperties.getPrefixCaption(EventIndexDto.I18N_PREFIX, EventIndexDto.DISEASE));
		diseaseFilter.addItems((Object[])Disease.values());
		diseaseFilter.addValueChangeListener(e -> {
			criteria.disease(((Disease)e.getProperty().getValue()));
			navigateTo(criteria);
		});
		filterLayout.addComponent(diseaseFilter);

		reportedByFilter = new ComboBox();
		reportedByFilter.setWidth(140, Unit.PIXELS);
		reportedByFilter.setInputPrompt(I18nProperties.getString(Strings.reportedBy));
		reportedByFilter.addItems((Object[]) UserRole.values());
		reportedByFilter.addValueChangeListener(e -> {
			criteria.reportingUserRole((UserRole) e.getProperty().getValue());
			navigateTo(criteria);
		});
		filterLayout.addComponent(reportedByFilter);

		resetButton = new Button(I18nProperties.getCaption(Captions.actionResetFilters));
		resetButton.setVisible(false);
		resetButton.addClickListener(event -> {
			ViewModelProviders.of(EventsView.class).remove(EventCriteria.class);
			navigateTo(null);
		});
		filterLayout.addComponent(resetButton);

		return filterLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		Button statusAll = new Button(I18nProperties.getCaption(Captions.all), e -> {
			criteria.eventStatus(null);
			navigateTo(criteria);
		});
		CssStyles.style(statusAll, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
		activeStatusButton = statusAll;

		for(EventStatus status : EventStatus.values()) {
			Button statusButton = new Button(status.toString(), e -> {
				criteria.eventStatus(status);
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
			if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_VIEW_ARCHIVED)) {
				switchArchivedActiveButton = new Button(I18nProperties.getCaption(Captions.eventShowArchived));
				switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
				switchArchivedActiveButton.addClickListener(e -> {
					criteria.archived(Boolean.TRUE.equals(criteria.getArchived()) ? null : Boolean.TRUE);
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(switchArchivedActiveButton);
			}

			// Bulk operation dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				MenuBar bulkOperationsDropdown = new MenuBar();	
				MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem(I18nProperties.getCaption(Captions.bulkActions), null);

				Command changeCommand = selectedItem -> {
					ControllerProvider.getEventController().showBulkEventDataEditComponent(grid.getSelectedRows());
				};
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkEdit), FontAwesome.ELLIPSIS_H, changeCommand);

				Command deleteCommand = selectedItem -> {
					ControllerProvider.getEventController().deleteAllSelectedItems(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.reload();
						}
					});
				};
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkDelete), FontAwesome.TRASH, deleteCommand);

				Command archiveCommand = selectedItem -> {
					ControllerProvider.getEventController().archiveAllSelectedItems(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.reload();
						}
					});
				};
				archiveItem = bulkOperationsItem.addItem(I18nProperties.getCaption(I18nProperties.getCaption(Captions.actionArchive)), FontAwesome.ARCHIVE, archiveCommand);

				Command dearchiveCommand = selectedItem -> {
					ControllerProvider.getEventController().dearchiveAllSelectedItems(grid.getSelectedRows(), new Runnable() {
						public void run() {
							grid.reload();
						}
					});
				};
				dearchiveItem = bulkOperationsItem.addItem(I18nProperties.getCaption(I18nProperties.getCaption(Captions.actionDearchive)), FontAwesome.ARCHIVE, dearchiveCommand);
				dearchiveItem.setVisible(false);

				actionButtonsLayout.addComponent(bulkOperationsDropdown);
			}
		}
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterLayout;
	}

	@Override
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

		typeFilter.setValue(criteria.getEventType());
		diseaseFilter.setValue(criteria.getDisease());
		reportedByFilter.setValue(criteria.getReportingUserRole());
		
		applyingCriteria = false;
	}
	
	private void updateStatusButtons() {
		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == criteria.getEventStatus()) {
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
			switchArchivedActiveButton.setCaption(I18nProperties.getCaption(I18nProperties.getCaption(Captions.eventShowArchived)));
			switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
			if (archiveItem != null && dearchiveItem != null) {
				archiveItem.setVisible(false);
				dearchiveItem.setVisible(true);
			}
		} else {
			getViewTitleLabel().setValue(originalViewTitle);
			switchArchivedActiveButton.setCaption(I18nProperties.getCaption(I18nProperties.getCaption(Captions.eventShowArchived)));
			switchArchivedActiveButton.setStyleName(ValoTheme.BUTTON_LINK);
			if (archiveItem != null && dearchiveItem != null) {
				dearchiveItem.setVisible(false);
				archiveItem.setVisible(true);
			}
		} 
	}

}

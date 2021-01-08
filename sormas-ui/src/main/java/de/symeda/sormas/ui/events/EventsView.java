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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import java.util.Date;
import java.util.HashMap;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.action.ActionStatus;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventActionExportDto;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventExportDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SearchSpecificLayout;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventsView extends AbstractView {

	private static final long serialVersionUID = -3048977745713631200L;

	public static final String VIEW_NAME = "events";

	private EventCriteria criteria;
	private EventsViewConfiguration viewConfiguration;

	private FilteredGrid<?, ?> grid;
	private Button createButton;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	// Filter
	private EventsFilterForm filterForm;
	private Label relevanceStatusInfoLabel;
	private ComboBox relevanceStatusFilter;

	private VerticalLayout gridLayout;

	// Bulk operations
	private MenuBar bulkOperationsDropdown;

	public EventsView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(getClass()).get(EventsViewConfiguration.class);
		if (viewConfiguration.getViewType() == null) {
			viewConfiguration.setViewType(EventsViewType.DEFAULT);
		}

		criteria = ViewModelProviders.of(EventsView.class).get(EventCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		if (isDefaultViewType()) {
			grid = new EventGrid(criteria, getClass());
			grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());
		} else {
			grid = new EventActionsGrid(criteria, getClass());
			grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());
			getViewTitleLabel().setValue(I18nProperties.getCaption(Captions.View_actions));
		}
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		OptionGroup eventsViewSwitcher = new OptionGroup();
		eventsViewSwitcher.setId("eventsViewSwitcher");
		CssStyles.style(eventsViewSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		eventsViewSwitcher.addItem(EventsViewType.DEFAULT);
		eventsViewSwitcher.setItemCaption(EventsViewType.DEFAULT, I18nProperties.getCaption(Captions.eventDefaultView));

		eventsViewSwitcher.addItem(EventsViewType.ACTIONS);
		eventsViewSwitcher.setItemCaption(EventsViewType.ACTIONS, I18nProperties.getCaption(Captions.eventActionsView));

		eventsViewSwitcher.setValue(viewConfiguration.getViewType());
		eventsViewSwitcher.addValueChangeListener(e -> {
			EventsViewType viewType = (EventsViewType) e.getProperty().getValue();

			viewConfiguration.setViewType(viewType);
			SormasUI.get().getNavigator().navigateTo(EventsView.VIEW_NAME);
		});
		addHeaderComponent(eventsViewSwitcher);

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_EXPORT)) {
			VerticalLayout exportLayout = new VerticalLayout();
			{
				exportLayout.setSpacing(true);
				exportLayout.setMargin(true);
				exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				exportLayout.setWidth(250, Unit.PIXELS);
			}

			PopupButton exportPopupButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);
			addHeaderComponent(exportPopupButton);

			{
				StreamResource streamResource =
					new GridExportStreamResource(grid, "sormas_events", "sormas_events_" + DateHelper.formatDateForExport(new Date()) + ".csv");
				addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.exportBasic, Strings.infoBasicExport);
			}

			{
				if (isDefaultViewType()) {
					StreamResource exportStreamResource = DownloadUtil.createCsvExportStreamResource(
						EventExportDto.class,
						null,
						(Integer start, Integer max) -> FacadeProvider.getEventFacade().getExportList((EventCriteria) grid.getCriteria(), start, max),
						(propertyId, type) -> {
							String caption = I18nProperties.findPrefixCaption(
								propertyId,
								EventExportDto.I18N_PREFIX,
								EventIndexDto.I18N_PREFIX,
								EventDto.I18N_PREFIX,
								LocationDto.I18N_PREFIX);
							if (Date.class.isAssignableFrom(type)) {
								caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
							}
							return caption;
						},
						createFileNameWithCurrentDate("sormas_events_", ".csv"),
						null);
					addExportButton(
						exportStreamResource,
						exportPopupButton,
						exportLayout,
						VaadinIcons.FILE_TEXT,
						Captions.exportDetailed,
						Strings.infoDetailedExport);
				} else {
					StreamResource exportStreamResource = DownloadUtil.createCsvExportStreamResource(
						EventActionExportDto.class,
						null,
						(Integer start, Integer max) -> FacadeProvider.getActionFacade()
							.getEventActionExportList((EventCriteria) grid.getCriteria(), start, max),
						(propertyId, type) -> {
							String caption = I18nProperties.findPrefixCaption(
								propertyId,
								EventActionExportDto.I18N_PREFIX,
								EventActionIndexDto.I18N_PREFIX,
								ActionDto.I18N_PREFIX,
								EventDto.I18N_PREFIX);
							if (Date.class.isAssignableFrom(type)) {
								caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
							}
							return caption;
						},
						createFileNameWithCurrentDate("sormas_events_actions", ".csv"),
						null);
					addExportButton(
						exportStreamResource,
						exportPopupButton,
						exportLayout,
						VaadinIcons.FILE_TEXT,
						Captions.exportDetailed,
						Strings.infoDetailedExport);
				}
			}
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS) && isDefaultViewType()) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());

			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());

			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(true);
				viewConfiguration.setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				((EventGrid) grid).setEagerDataProvider();
				((EventGrid) grid).reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(false);
				viewConfiguration.setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				navigateTo(criteria);
			});
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE)) {
			createButton = ButtonHelper.createIconButton(
				Captions.eventNewEvent,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getEventController().create((CaseReferenceDto) null),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}

		if (isDefaultViewType()) {
			Button searchSpecificEventButton = ButtonHelper.createIconButton(
				Captions.eventSearchSpecificEvent,
				VaadinIcons.SEARCH,
				e -> buildAndOpenSearchSpecificEventWindow(),
				ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(searchSpecificEventButton);
		}
	}

	private boolean isDefaultViewType() {
		return viewConfiguration.getViewType() == EventsViewType.DEFAULT;
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setMargin(false);
		filterLayout.setSizeUndefined();

		filterForm = new EventsFilterForm(isDefaultViewType(), isDefaultViewType());
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(EventsView.class).remove(EventCriteria.class);
			navigateTo(null);
		});
		filterForm.addApplyHandler(e -> navigateTo(criteria));
		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	private void buildAndOpenSearchSpecificEventWindow() {
		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getCaption(Captions.eventSearchSpecificEvent));
		window.setWidth(768, Unit.PIXELS);

		SearchSpecificLayout layout = buildSearchSpecificLayout(window);
		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}

	private SearchSpecificLayout buildSearchSpecificLayout(Window window) {

		String description = I18nProperties.getString(Strings.infoSpecificEventSearch);
		String confirmCaption = I18nProperties.getCaption(Captions.eventSearchEvent);

		TextField searchField = new TextField();
		Runnable confirmCallback = () -> {
			String foundEventUuid = FacadeProvider.getEventFacade().getUuidByCaseUuidOrPersonUuid(searchField.getValue());

			if (foundEventUuid != null) {
				ControllerProvider.getEventController().navigateToData(foundEventUuid);
				window.close();
			} else {
				VaadinUiUtil.showSimplePopupWindow(
					I18nProperties.getString(Strings.headingNoEventFound),
					I18nProperties.getString(Strings.messageNoEventFound));
			}
		};

		return new SearchSpecificLayout(confirmCallback, () -> window.close(), searchField, description, confirmCaption);
	}

	public HorizontalLayout createStatusFilterBar() {

		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		if (isDefaultViewType()) {
			Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
				criteria.setEventStatus(null);
				navigateTo(criteria);
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
			statusAll.setCaptionAsHtml(true);

			statusFilterLayout.addComponent(statusAll);

			statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
			activeStatusButton = statusAll;

			for (EventStatus status : EventStatus.values()) {
				Button statusButton = ButtonHelper.createButtonWithCaption("status-" + status, status.toString(), e -> {
					criteria.setEventStatus(status);
					navigateTo(criteria);
				}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
				statusButton.setCaptionAsHtml(true);
				statusButton.setData(status);

				statusFilterLayout.addComponent(statusButton);

				statusButtons.put(statusButton, status.toString());
			}
		} else {
			Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
				criteria.setActionStatus(null);
				navigateTo(criteria);
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
			statusAll.setCaptionAsHtml(true);

			statusFilterLayout.addComponent(statusAll);

			statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
			activeStatusButton = statusAll;

			for (ActionStatus status : ActionStatus.values()) {
				Button statusButton = ButtonHelper.createButtonWithCaption("status-" + status, status.toString(), e -> {
					criteria.actionStatus(status);
					navigateTo(criteria);
				}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
				statusButton.setCaptionAsHtml(true);
				statusButton.setData(status);

				statusFilterLayout.addComponent(statusButton);

				statusButtons.put(statusButton, status.toString());
			}
		}

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_VIEW_ARCHIVED)) {
				int daysAfterEventGetsArchived = FacadeProvider.getConfigFacade().getDaysAfterEventGetsArchived();
				if (daysAfterEventGetsArchived > 0) {
					relevanceStatusInfoLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " "
							+ String.format(I18nProperties.getString(Strings.infoArchivedEvents), daysAfterEventGetsArchived),
						ContentMode.HTML);
					relevanceStatusInfoLabel.setVisible(false);
					relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_SUPER);
					actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
					actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, Alignment.MIDDLE_RIGHT);
				}
				relevanceStatusFilter = new ComboBox();
				relevanceStatusFilter.setId("relevanceStatusFilter");
				relevanceStatusFilter.setWidth(140, Unit.PERCENTAGE);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.eventActiveEvents));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.eventArchivedEvents));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.eventAllEvents));
				relevanceStatusFilter.addValueChangeListener(e -> {
					relevanceStatusInfoLabel.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);
			}

			// Bulk operation dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS) && isDefaultViewType()) {
				EventGrid eventGrid = (EventGrid) grid;
				bulkOperationsDropdown = MenuBarHelper.createDropDown(
					Captions.bulkActions,
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkEdit), VaadinIcons.ELLIPSIS_H, selectedItem -> {
						ControllerProvider.getEventController().showBulkEventDataEditComponent(eventGrid.asMultiSelect().getSelectedItems());
					}),
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, selectedItem -> {
						ControllerProvider.getEventController()
							.deleteAllSelectedItems(eventGrid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
					}),
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.actionArchive), VaadinIcons.ARCHIVE, selectedItem -> {
						ControllerProvider.getEventController()
							.archiveAllSelectedItems(eventGrid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
					}, EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())),
					new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.actionDearchive), VaadinIcons.ARCHIVE, selectedItem -> {
						ControllerProvider.getEventController()
							.dearchiveAllSelectedItems(eventGrid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
					}, EntityRelevanceStatus.ARCHIVED.equals(criteria.getRelevanceStatus())));

				bulkOperationsDropdown.setVisible(viewConfiguration.isInEagerMode());
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

		if (viewConfiguration.isInEagerMode() && isDefaultViewType()) {
			((EventGrid) grid).setEagerDataProvider();
		}

		updateFilterComponents();
		if (isDefaultViewType()) {
			((EventGrid) grid).reload();
		} else {
			((EventActionsGrid) grid).reload();
		}
	}

	public void updateFilterComponents() {

		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		updateStatusButtons();
		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}

		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	private void updateStatusButtons() {

		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == (isDefaultViewType() ? criteria.getEventStatus() : criteria.getActionStatus())) {
				activeStatusButton = b;
			}
		});
		CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
		if (activeStatusButton != null) {
			activeStatusButton
				.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}
	}
}

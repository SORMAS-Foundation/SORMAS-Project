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

import static de.symeda.sormas.ui.docgeneration.DocGenerationHelper.isDocGenerationAllowed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
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
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.event.EventActionExportDto;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportPropertyMetaInfo;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SearchSpecificLayout;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.events.importer.EventImportLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.EventDownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.popupmenu.PopupMenu;

public class EventsView extends AbstractView {

	private static final long serialVersionUID = -3048977745713631200L;

	public static final String VIEW_NAME = "events";

	private EventCriteria eventCriteria;
	private EventGroupCriteria eventGroupCriteria;
	private EventsViewConfiguration viewConfiguration;

	private FilteredGrid<?, ?> grid;
	private Button createButton;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;

	// Filter
	private EventsFilterForm eventsFilterForm;
	private EventGroupsFilterForm eventGroupsFilterForm;
	private Label relevanceStatusInfoLabel;
	private ComboBox eventRelevanceStatusFilter;
	private ComboBox groupRelevanceStatusFilter;

	private ComboBox contactCountMethod;

	private VerticalLayout gridLayout;

	// Bulk operations
	private MenuBar bulkOperationsDropdown;

	private Set<String> getSelectedRows() {
		EventGrid eventGrid = (EventGrid) this.grid;
		return this.viewConfiguration.isInEagerMode()
			? eventGrid.asMultiSelect().getSelectedItems().stream().map(EventIndexDto::getUuid).collect(Collectors.toSet())
			: Collections.emptySet();
	}

	public EventsView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(getClass()).get(EventsViewConfiguration.class);
		if (viewConfiguration.getViewType() == null) {
			viewConfiguration.setViewType(EventsViewType.DEFAULT);
		}

		eventGroupCriteria = ViewModelProviders.of(EventsView.class).get(EventGroupCriteria.class);
		if (eventGroupCriteria.getRelevanceStatus() == null) {
			eventGroupCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		eventCriteria = ViewModelProviders.of(EventsView.class).get(EventCriteria.class);
		if (eventCriteria.getRelevanceStatus() == null) {
			eventCriteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		if (isDefaultViewType()) {
			grid = new EventGrid(eventCriteria, getClass());
		} else if (isActionViewType()) {
			grid = new EventActionsGrid(eventCriteria, getClass());
			getViewTitleLabel().setValue(I18nProperties.getCaption(Captions.View_actions));
		} else {
			grid = new EventGroupsGrid(eventGroupCriteria, getClass());
			getViewTitleLabel().setValue(I18nProperties.getCaption(Captions.View_groups));
		}
		grid.addDataSizeChangeListener(e -> updateStatusButtons());

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
		CssStyles.style(
			eventsViewSwitcher,
			CssStyles.FORCE_CAPTION,
			ValoTheme.OPTIONGROUP_HORIZONTAL,
			CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY,
			CssStyles.VSPACE_TOP_3);
		eventsViewSwitcher.addItem(EventsViewType.DEFAULT);
		eventsViewSwitcher.setItemCaption(EventsViewType.DEFAULT, I18nProperties.getCaption(Captions.eventDefaultView));

		eventsViewSwitcher.addItem(EventsViewType.ACTIONS);
		eventsViewSwitcher.setItemCaption(EventsViewType.ACTIONS, I18nProperties.getCaption(Captions.eventActionsView));

		boolean eventGroupsFeatureEnabled = UiUtil.enabled(FeatureType.EVENT_GROUPS);
		if (eventGroupsFeatureEnabled) {
			eventsViewSwitcher.addItem(EventsViewType.GROUPS);
			eventsViewSwitcher.setItemCaption(EventsViewType.GROUPS, I18nProperties.getCaption(Captions.eventGroupsView));
		}

		eventsViewSwitcher.setValue(viewConfiguration.getViewType());
		eventsViewSwitcher.addValueChangeListener(e -> {
			EventsViewType viewType = (EventsViewType) e.getProperty().getValue();

			viewConfiguration.setViewType(viewType);
			SormasUI.get().getNavigator().navigateTo(EventsView.VIEW_NAME);
		});
		addHeaderComponent(eventsViewSwitcher);

		if (isDefaultViewType() && UiUtil.permitted(UserRight.EVENT_IMPORT)) {
			Button importButton = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window popupWindow = VaadinUiUtil.showPopupWindow(new EventImportLayout());
				popupWindow.setCaption(I18nProperties.getString(Strings.headingImportEvent));
				popupWindow.addCloseListener(c -> ((EventGrid) grid).reload());
			}, ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(importButton);
		}

		if (UiUtil.permitted(UserRight.EVENT_EXPORT)) {
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
				StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
					grid,
					() -> isDefaultViewType() && this.viewConfiguration.isInEagerMode()
						? this.grid.asMultiSelect().getSelectedItems()
						: Collections.emptySet(),
					ExportEntityName.EVENTS);
				addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.exportBasic, Strings.infoBasicExport);
			}

			{
				if (isDefaultViewType()) {
					StreamResource exportStreamResource = EventDownloadUtil
						.createEventExportResource((EventCriteria) grid.getCriteria(), this::getSelectedRows, buildDetailedExportConfiguration());

					addExportButton(
						exportStreamResource,
						exportPopupButton,
						exportLayout,
						VaadinIcons.FILE_TEXT,
						Captions.exportDetailed,
						Strings.infoDetailedExport);
				} else if (isActionViewType()) {
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
						ExportEntityName.EVENT_ACTIONS,
						null);
					addExportButton(
						exportStreamResource,
						exportPopupButton,
						exportLayout,
						VaadinIcons.FILE_TEXT,
						Captions.exportDetailed,
						Strings.infoDetailedExport);
				} else {
					// NOOP: No detailed export for the groups view
				}
			}
		}

		if (UiUtil.permitted(UserRight.EVENT_CREATE)) {
			createButton = ButtonHelper.createIconButton(
				Captions.eventNewEvent,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getEventController().create((CaseReferenceDto) null),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(createButton);
		}

		final PopupMenu moreButton = new PopupMenu(I18nProperties.getCaption(Captions.moreActions));

		if (UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS) && isDefaultViewType()) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			{
				btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
				btnEnterBulkEditMode.addStyleName(ValoTheme.BUTTON_PRIMARY);
				btnEnterBulkEditMode.setWidth(100, Unit.PERCENTAGE);
				moreButton.addMenuEntry(btnEnterBulkEditMode);
			}

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			{
				btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
				btnLeaveBulkEditMode.setWidth(100, Unit.PERCENTAGE);
				moreButton.addMenuEntry(btnLeaveBulkEditMode);
			}

			btnEnterBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(true);
				ViewModelProviders.of(EventsView.class).get(EventsViewConfiguration.class).setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				((EventGrid) grid).reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(false);
				ViewModelProviders.of(EventsView.class).get(EventsViewConfiguration.class).setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				navigateTo(eventCriteria);
			});
		}

		if (isDefaultViewType()) {
			Button searchSpecificEventButton = ButtonHelper.createIconButton(Captions.eventSearchSpecificEvent, VaadinIcons.SEARCH, e -> {
				buildAndOpenSearchSpecificEventWindow();
				moreButton.setPopupVisible(false);
			}, ValoTheme.BUTTON_PRIMARY);
			searchSpecificEventButton.setWidth(100, Unit.PERCENTAGE);
			moreButton.addMenuEntry(searchSpecificEventButton);
		}

		if (moreButton.hasMenuEntries()) {
			addHeaderComponent(moreButton);
		}
	}

	private boolean isDefaultViewType() {
		return viewConfiguration.getViewType() == EventsViewType.DEFAULT;
	}

	private boolean isActionViewType() {
		return viewConfiguration.getViewType() == EventsViewType.ACTIONS;
	}

	private boolean isGroupViewType() {
		return viewConfiguration.getViewType() == EventsViewType.GROUPS;
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setMargin(false);
		filterLayout.setSizeUndefined();

		eventGroupsFilterForm = new EventGroupsFilterForm();
		eventGroupsFilterForm.addValueChangeListener(e -> {
			if (!eventGroupsFilterForm.hasFilter()) {
				navigateTo(null);
			}
		});
		eventGroupsFilterForm.addResetHandler(e -> {
			ViewModelProviders.of(EventsView.class).remove(EventGroupCriteria.class);
			ViewModelProviders.of(EventsView.class).remove(EventCriteria.class);
			navigateTo(null);
		});
		eventGroupsFilterForm.addApplyHandler(e -> {
			((EventGroupsGrid) grid).reload();
		});

		eventsFilterForm = new EventsFilterForm(isDefaultViewType(), isDefaultViewType());
		eventsFilterForm.addValueChangeListener(e -> {
			if (!eventsFilterForm.hasFilter()) {
				navigateTo(null);
			}
		});
		eventsFilterForm.addResetHandler(e -> {
			ViewModelProviders.of(EventsView.class).remove(EventGroupCriteria.class);
			ViewModelProviders.of(EventsView.class).remove(EventCriteria.class);
			navigateTo(null);
		});
		eventsFilterForm.addApplyHandler(e -> {
			if (isDefaultViewType()) {
				((EventGrid) grid).reload();
			} else {
				((EventActionsGrid) grid).reload();
			}
		});

		if (isGroupViewType()) {
			filterLayout.addComponent(eventGroupsFilterForm);
		} else {
			filterLayout.addComponent(eventsFilterForm);
		}

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
				eventCriteria.setEventStatus(null);
				navigateTo(eventCriteria);
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
			statusAll.setCaptionAsHtml(true);

			statusFilterLayout.addComponent(statusAll);

			statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
			activeStatusButton = statusAll;

			for (EventStatus status : EventStatus.values()) {
				Button statusButton = ButtonHelper.createButton("status-" + status, status.toString(), e -> {
					eventCriteria.setEventStatus(status);
					navigateTo(eventCriteria);
				}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
				statusButton.setCaptionAsHtml(true);
				statusButton.setData(status);

				statusFilterLayout.addComponent(statusButton);

				statusButtons.put(statusButton, status.toString());
			}
		} else if (isActionViewType()) {
			Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
				eventCriteria.setActionStatus(null);
				navigateTo(eventCriteria);
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
			statusAll.setCaptionAsHtml(true);

			statusFilterLayout.addComponent(statusAll);

			statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
			activeStatusButton = statusAll;

			for (ActionStatus status : ActionStatus.values()) {
				Button statusButton = ButtonHelper.createButton("status-" + status, status.toString(), e -> {
					eventCriteria.actionStatus(status);
					navigateTo(eventCriteria);
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
			if (UiUtil.permitted(UserRight.EVENT_VIEW)) {
				if (isGroupViewType()) {
					groupRelevanceStatusFilter =
						buildRelevanceStatus(Captions.eventActiveGroups, Captions.eventArchivedGroups, Captions.eventAllGroups);
					groupRelevanceStatusFilter.removeItem(EntityRelevanceStatus.DELETED);
					groupRelevanceStatusFilter.addValueChangeListener(e -> {
						eventGroupCriteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
						navigateTo(eventGroupCriteria);
					});
					actionButtonsLayout.addComponent(groupRelevanceStatusFilter);
				} else {
					if (UiUtil.enabled(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.EVENT)) {

						int daysAfterEventGetsArchived = FacadeProvider.getFeatureConfigurationFacade()
							.getProperty(
								FeatureType.AUTOMATIC_ARCHIVING,
								DeletableEntityType.EVENT,
								FeatureTypeProperty.THRESHOLD_IN_DAYS,
								Integer.class);
						if (daysAfterEventGetsArchived > 0) {
							relevanceStatusInfoLabel = new Label(
								VaadinIcons.INFO_CIRCLE.getHtml() + " "
									+ String.format(I18nProperties.getString(Strings.infoArchivedEvents), daysAfterEventGetsArchived),
								ContentMode.HTML);
							relevanceStatusInfoLabel.setVisible(false);
							relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_TOP);
							actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
							actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, new Alignment(38));
						}
					}

					eventRelevanceStatusFilter =
						buildRelevanceStatus(Captions.eventActiveEvents, Captions.eventArchivedEvents, Captions.eventAllActiveAndArchivedEvents);
					eventRelevanceStatusFilter.addValueChangeListener(e -> {
						if (relevanceStatusInfoLabel != null) {
							relevanceStatusInfoLabel.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
						}

						if (grid.getColumn(DELETE_REASON_COLUMN) != null) {
							grid.getColumn(DELETE_REASON_COLUMN)
								.setHidden(!eventRelevanceStatusFilter.getValue().equals(EntityRelevanceStatus.DELETED));
						}

						eventCriteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
						navigateTo(eventCriteria);
					});
					actionButtonsLayout.addComponent(eventRelevanceStatusFilter);
				}
			}

			// Bulk operation dropdown
			if (UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS) && isDefaultViewType()) {
				EventGrid eventGrid = (EventGrid) grid;
				List<MenuBarHelper.MenuBarItem> bulkActions = new ArrayList<>();
				if (eventCriteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
					if (UiUtil.permitted(UserRight.EVENT_EDIT)) {
						bulkActions.add(
							new MenuBarHelper.MenuBarItem(
								I18nProperties.getCaption(Captions.bulkEdit),
								VaadinIcons.ELLIPSIS_H,
								mi -> grid.bulkActionHandler(
									items -> ControllerProvider.getEventController().showBulkEventDataEditComponent(items, (EventGrid) grid))));
					}
					if (UiUtil.permitted(UserRight.EVENT_DELETE)) {
						bulkActions.add(
							new MenuBarHelper.MenuBarItem(
								I18nProperties.getCaption(Captions.bulkDelete),
								VaadinIcons.TRASH,
								mi -> grid.bulkActionHandler(
									items -> ControllerProvider.getEventController().deleteAllSelectedItems(items, (EventGrid) grid),
									true)));
					}
					if (UiUtil.permitted(UserRight.EVENT_ARCHIVE)) {
						bulkActions.add(
							new MenuBarHelper.MenuBarItem(
								I18nProperties.getCaption(Captions.actionArchiveCoreEntity),
								VaadinIcons.ARCHIVE,
								mi -> grid.bulkActionHandler(
									items -> ControllerProvider.getEventController().archiveAllSelectedItems(items, eventGrid),
									true),
								EntityRelevanceStatus.ACTIVE.equals(eventCriteria.getRelevanceStatus())));
						bulkActions.add(
							new MenuBarHelper.MenuBarItem(
								I18nProperties.getCaption(Captions.actionDearchiveCoreEntity),
								VaadinIcons.ARCHIVE,
								mi -> grid.bulkActionHandler(
									items -> ControllerProvider.getEventController()
										.dearchiveAllSelectedItems(eventGrid.asMultiSelect().getSelectedItems(), eventGrid),
									true),
								EntityRelevanceStatus.ARCHIVED.equals(eventCriteria.getRelevanceStatus())));
					}
					if (UiUtil.permitted(UserRight.EVENTGROUP_CREATE, UserRight.EVENTGROUP_LINK)) {
						bulkActions.add(
							new MenuBarHelper.MenuBarItem(
								I18nProperties.getCaption(Captions.actionGroupEvent),
								VaadinIcons.FILE_TREE,
								mi -> grid.bulkActionHandler(
									items -> ControllerProvider.getEventController()
										.linkAllToGroup(eventGrid.asMultiSelect().getSelectedItems(), eventGrid))));
					}
					bulkActions.add(
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_send),
							VaadinIcons.SHARE,
							mi -> grid.bulkActionHandler(
								items -> ControllerProvider.getEventController()
									.sendAllSelectedToExternalSurveillanceTool(eventGrid.asMultiSelect().getSelectedItems(), eventGrid)),
							UiUtil.permitted(UserRight.EXTERNAL_SURVEILLANCE_SHARE)
								&& FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()));

					if (isDocGenerationAllowed()) {
						bulkActions.add(
							new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkActionCreatDocuments), VaadinIcons.FILE_TEXT, mi -> {
								grid.bulkActionHandler(items -> {

									EventGrid eventGrid1 = (EventGrid) this.grid;
									List<EventReferenceDto> references = eventGrid1.asMultiSelect()
										.getSelectedItems()
										.stream()
										.map(EventIndexDto::toReference)
										.collect(Collectors.toList());
									if (references.size() == 0) {
										new Notification(
											I18nProperties.getString(Strings.headingNoEventsSelected),
											I18nProperties.getString(Strings.headingNoEventsSelected),
											Notification.Type.WARNING_MESSAGE,
											false).show(Page.getCurrent());

										return;
									}

									ControllerProvider.getDocGenerationController().showEventDocumentDialog(references);
								});
							}));
					}
				} else if (UiUtil.permitted(UserRight.EVENT_DELETE)) {
					bulkActions.add(
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.bulkRestore),
							VaadinIcons.ARROW_BACKWARD,
							mi -> grid.bulkActionHandler(
								items -> ControllerProvider.getEventController().restoreSelectedEvents(items, (EventGrid) grid),
								true)));
				}

				bulkOperationsDropdown = MenuBarHelper.createDropDown(Captions.bulkActions, bulkActions);

				bulkOperationsDropdown.setVisible(viewConfiguration.isInEagerMode());
				bulkOperationsDropdown.setCaption("");
				actionButtonsLayout.addComponent(bulkOperationsDropdown);
			}

			if (isDefaultViewType()) {
				// Contact Count Method Dropdown
				contactCountMethod = ComboBoxHelper.createComboBoxV7();
				contactCountMethod.setCaption(I18nProperties.getCaption(Captions.Event_contactCountMethod));
				contactCountMethod.addItem(EventContactCountMethod.ALL);
				contactCountMethod.addItem(EventContactCountMethod.SOURCE_CASE_IN_EVENT);
				contactCountMethod.addItem(EventContactCountMethod.BOTH_METHODS);
				contactCountMethod.setItemCaption(EventContactCountMethod.ALL, I18nProperties.getEnumCaption(EventContactCountMethod.ALL));
				contactCountMethod.setItemCaption(
					EventContactCountMethod.SOURCE_CASE_IN_EVENT,
					I18nProperties.getEnumCaption(EventContactCountMethod.SOURCE_CASE_IN_EVENT));
				contactCountMethod
					.setItemCaption(EventContactCountMethod.BOTH_METHODS, I18nProperties.getEnumCaption(EventContactCountMethod.BOTH_METHODS));
				contactCountMethod.setValue(EventContactCountMethod.ALL);
				contactCountMethod.setTextInputAllowed(false);
				contactCountMethod.setNullSelectionAllowed(false);
				contactCountMethod.addValueChangeListener(event -> {
					((EventGrid) grid).setContactCountMethod((EventContactCountMethod) event.getProperty().getValue());
					((EventGrid) grid).reload();
				});
				actionButtonsLayout.addComponent(contactCountMethod);
			}
		}
		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterLayout;

	}

	@Override
	public void enter(ViewChangeEvent event) {
		if (isGroupViewType()) {
			eventCriteria = ViewModelProviders.of(EventsView.class).get(EventCriteria.class);
		} else {
			eventGroupCriteria = ViewModelProviders.of(EventsView.class).get(EventGroupCriteria.class);
		}
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			if (isGroupViewType()) {
				eventGroupCriteria.fromUrlParams(params);
			} else {
				eventCriteria.fromUrlParams(params);
			}
		}

		if (viewConfiguration.isInEagerMode() && isDefaultViewType()) {
			((EventGrid) grid).setEagerDataProvider();
		}

		updateFilterComponents();
		if (isDefaultViewType()) {
			((EventGrid) grid).reload();
		} else if (isActionViewType()) {
			((EventActionsGrid) grid).reload();
		} else {
			((EventGroupsGrid) grid).reload();
		}
	}

	public void updateFilterComponents() {

		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		updateStatusButtons();
		if (eventRelevanceStatusFilter != null) {
			eventRelevanceStatusFilter.setValue(eventCriteria.getRelevanceStatus());
		}
		if (groupRelevanceStatusFilter != null) {
			groupRelevanceStatusFilter.setValue(eventGroupCriteria.getRelevanceStatus());
		}

		eventsFilterForm.setValue(eventCriteria);
		eventGroupsFilterForm.setValue(eventGroupCriteria);

		applyingCriteria = false;
	}

	private void updateStatusButtons() {

		statusButtons.keySet().forEach(b -> {
			CssStyles.style(b, CssStyles.BUTTON_FILTER_LIGHT);
			b.setCaption(statusButtons.get(b));
			if (b.getData() == (isDefaultViewType() ? eventCriteria.getEventStatus() : eventCriteria.getActionStatus())) {
				activeStatusButton = b;
			}
		});

		if (activeStatusButton != null) {
			CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
			if (activeStatusButton != null) {
				activeStatusButton
					.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getDataSize())));
			}
		}
	}

	private ExportConfigurationDto buildDetailedExportConfiguration() {
		ExportConfigurationDto config = ExportConfigurationDto.build(UiUtil.getUserReference(), null);
		boolean eventGroupFeatureEnabled = UiUtil.enabled(FeatureType.EVENT_GROUPS);
		config.setProperties(
			ImportExportUtils
				.getEventExportProperties(
					EventDownloadUtil::getPropertyCaption,
					eventGroupFeatureEnabled,
					FacadeProvider.getConfigFacade().getCountryLocale())
				.stream()
				.map(ExportPropertyMetaInfo::getPropertyId)
				.collect(Collectors.toSet()));
		return config;
	}

	private ComboBox buildRelevanceStatus(String eventActiveCaption, String eventArchivedCaption, String eventAllCaption) {
		ComboBox relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
		relevanceStatusFilter.setId("relevanceStatusFilter");
		relevanceStatusFilter.setWidth(210, Unit.PIXELS);
		relevanceStatusFilter.setNullSelectionAllowed(false);
		relevanceStatusFilter.setTextInputAllowed(false);
		relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(eventActiveCaption));
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(eventArchivedCaption));
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED, I18nProperties.getCaption(eventAllCaption));

		if (UiUtil.permitted(UserRight.EVENT_DELETE)) {
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.DELETED, I18nProperties.getCaption(Captions.eventDeletedEvents));
		} else {
			relevanceStatusFilter.removeItem(EntityRelevanceStatus.DELETED);
		}

		relevanceStatusFilter.setCaption("");
		return relevanceStatusFilter;
	}
}

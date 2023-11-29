/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.MultiSelectionModelImpl;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.customexport.ExportConfigurationsLayout;
import de.symeda.sormas.ui.events.eventparticipantimporter.EventParticipantImportLayout;
import de.symeda.sormas.ui.samples.HasName;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;
import de.symeda.sormas.ui.utils.EventParticipantDownloadUtil;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class EventParticipantsView extends AbstractEventView implements HasName {

	private static final long serialVersionUID = -1L;

	public static final String EVENTPARTICIPANTS = "eventparticipants";
	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/" + EVENTPARTICIPANTS;

	private EventParticipantCriteria criteria;
	private EventParticipantsViewConfiguration viewConfiguration;

	private EventParticipantsGrid grid;
	private Button addButton;
	private Button btnEnterBulkEditMode;
	private MenuBar bulkOperationsDropdown;
	private DetailSubComponentWrapper gridLayout;
	private Button activeStatusButton;
	private EventParticipantsFilterForm filterForm;

	private Label relevanceStatusInfoLabel;
	private ComboBox eventParticipantRelevanceStatusFilter;

	public EventParticipantsView() {
		super(VIEW_NAME);

		setSizeFull();
		addStyleName("crud-view");

		viewConfiguration = ViewModelProviders.of(getClass()).get(EventParticipantsViewConfiguration.class);
	}

	public HorizontalLayout createTopBar() {

		HorizontalLayout topLayout = new HorizontalLayout();
		topLayout.setSpacing(true);
		topLayout.setWidth("100%");

		VerticalLayout exportLayout = new VerticalLayout();
		{
			exportLayout.setSpacing(true);
			exportLayout.setMargin(true);
			exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			exportLayout.setWidth(250, Unit.PIXELS);
		}

		// import
		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_IMPORT)) {
			Button importButton = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window popupWindow = VaadinUiUtil.showPopupWindow(new EventParticipantImportLayout(getEventRef()));
				popupWindow.setCaption(I18nProperties.getString(Strings.headingImportEventParticipant));
				popupWindow.addCloseListener(c -> this.grid.reload());
			}, ValoTheme.BUTTON_PRIMARY);
			if (shouldDisableButton()) {
				importButton.setEnabled(false);
			}

			addHeaderComponent(importButton);
		}

		// export
		PopupButton exportPopupButton = ButtonHelper.createIconPopupButton(Captions.export, VaadinIcons.DOWNLOAD, exportLayout);
		if (shouldDisableButton()) {
			exportPopupButton.setEnabled(false);
		}
		addHeaderComponent(exportPopupButton);

		{
			StreamResource streamResource = GridExportStreamResource.createStreamResourceWithSelectedItems(
				grid,
				() -> this.grid.getSelectionModel() instanceof MultiSelectionModelImpl ? this.grid.asMultiSelect().getSelectedItems() : null,
				ExportEntityName.EVENT_PARTICIPANTS);
			addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.exportBasic, Strings.infoBasicExport);
		}

		{
			StreamResource extendedExportStreamResource =
				EventParticipantDownloadUtil.createExtendedEventParticipantExportResource(grid.getCriteria(), this::getSelectedRows, null);

			addExportButton(
				extendedExportStreamResource,
				exportPopupButton,
				exportLayout,
				VaadinIcons.FILE_TEXT,
				Captions.exportDetailed,
				Descriptions.descDetailedExportButton);
		}

		{
			Button btnCustomExport = ButtonHelper.createIconButton(Captions.exportCustom, VaadinIcons.FILE_TEXT, e -> {
				Window customExportWindow = VaadinUiUtil.createPopupWindow();

				ExportConfigurationsLayout customExportsLayout = new ExportConfigurationsLayout(
					ExportType.EVENT_PARTICIPANTS,
					ImportExportUtils.getEventParticipantExportProperties(
						EventParticipantDownloadUtil::getPropertyCaption,
						FacadeProvider.getConfigFacade().getCountryLocale()),
					customExportWindow::close);
				customExportsLayout.setExportCallback(
					(exportConfig) -> Page.getCurrent()
						.open(
							EventParticipantDownloadUtil
								.createExtendedEventParticipantExportResource(grid.getCriteria(), this::getSelectedRows, exportConfig),
							null,
							true));
				customExportWindow.setWidth(1024, Unit.PIXELS);
				customExportWindow.setCaption(I18nProperties.getCaption(Captions.exportCustom));
				customExportWindow.setContent(customExportsLayout);
				UI.getCurrent().addWindow(customExportWindow);
				exportPopupButton.setPopupVisible(false);
			}, ValoTheme.BUTTON_PRIMARY);
			btnCustomExport.setDescription(I18nProperties.getString(Strings.infoCustomExport));
			btnCustomExport.setWidth(100, Unit.PERCENTAGE);
			exportLayout.addComponent(btnCustomExport);
		}

		filterForm = new EventParticipantsFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(EventParticipantsView.class).remove(EventParticipantCriteria.class);
			navigateTo(null);
		});
		filterForm.addApplyHandler(e -> grid.reload());

		topLayout.addComponent(filterForm);

		// Bulk operation dropdown
		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS_EVENTPARTICIPANT)) {
			topLayout.setWidth(100, Unit.PERCENTAGE);

			List<MenuBarHelper.MenuBarItem> bulkActions = new ArrayList<>();
			bulkActions
				.add(new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkEventParticipantsToContacts), VaadinIcons.HAND, mi -> {
					grid.bulkActionHandler(items -> {
						EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(getEventRef().getUuid(), false);
						ControllerProvider.getContactController().openLineListingWindow(eventDto, items);
					}, true);
				}));
			if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_DELETE)) {
				if (criteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
					bulkActions.add(new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, mi -> {
						grid.bulkActionHandler(items -> {
							ControllerProvider.getEventParticipantController().deleteAllSelectedItems(items, grid, () -> grid.reload());
						}, true);
					}));
				} else {
					bulkActions.add(new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkRestore), VaadinIcons.ARROW_BACKWARD, mi -> {
						grid.bulkActionHandler(items -> {
							ControllerProvider.getEventParticipantController().restoreSelectedEventParticipants(items, grid, () -> grid.reload());
						}, true);
					}));
				}
			}
			if (isDocGenerationAllowed()) {
				bulkActions
					.add(new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.bulkActionCreatDocuments), VaadinIcons.FILE_TEXT, mi -> {
						grid.bulkActionHandler(items -> {
							List<EventParticipantReferenceDto> references = grid.asMultiSelect()
								.getSelectedItems()
								.stream()
								.map(EventParticipantIndexDto::toReference)
								.collect(Collectors.toList());
							if (references.size() == 0) {
								new Notification(
									I18nProperties.getString(Strings.headingNoEventParticipantsSelected),
									I18nProperties.getString(Strings.messageNoEventParticipantsSelected),
									Notification.Type.WARNING_MESSAGE,
									false).show(Page.getCurrent());

								return;
							}

							EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(getEventRef().getUuid(), false);

							ControllerProvider.getDocGenerationController()
								.showBulkEventParticipantQuarantineOrderDocumentDialog(references, eventDto.getDisease());
						});
					}));
			}

			bulkOperationsDropdown = MenuBarHelper.createDropDown(Captions.bulkActions, bulkActions);
			bulkOperationsDropdown.setVisible(viewConfiguration.isInEagerMode());

			topLayout.addComponent(bulkOperationsDropdown);
			topLayout.setComponentAlignment(bulkOperationsDropdown, Alignment.TOP_RIGHT);

			btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());

			if (shouldDisableButton()) {
				btnEnterBulkEditMode.setEnabled(false);
				btnLeaveBulkEditMode.setEnabled(false);
				bulkOperationsDropdown.setEnabled(false);
			}
			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(true);
				ViewModelProviders.of(EventParticipantsView.class).get(EventParticipantsViewConfiguration.class).setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				grid.reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(false);
				ViewModelProviders.of(EventParticipantsView.class).get(EventParticipantsViewConfiguration.class).setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				navigateTo(criteria);
			});

		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE)) {
			final ExpandableButton lineListingButton = new ExpandableButton(Captions.lineListing)
				.expand(e -> ControllerProvider.getEventParticipantController().openLineListingWindow(getEventRef()));
			addHeaderComponent(lineListingButton);
			lineListingButton.setEnabled(isGridEnabled());
		}

		topLayout.addStyleName(CssStyles.VSPACE_3);
		return topLayout;
	}

	private boolean isGridEnabled() {
		return !isEventDeleted() && isEditAllowed() && UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_EDIT);
	}

	private boolean shouldDisableButton() {
		return FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(FeatureType.EDIT_ARCHIVED_ENTITIES)
			&& FacadeProvider.getEventFacade().isArchived(getEventRef().getUuid());
	}

	private Set<String> getSelectedRows() {
		return this.grid.getSelectionModel() instanceof MultiSelectionModelImpl
			? grid.asMultiSelect().getSelectedItems().stream().map(EventParticipantIndexDto::getUuid).collect(Collectors.toSet())
			: Collections.emptySet();
	}

	@Override
	protected void initView(String params) {
		EventReferenceDto eventRef = getEventRef();

		criteria = ViewModelProviders.of(EventParticipantsView.class).get(EventParticipantCriteria.class);
		boolean isEventArchived = FacadeProvider.getEventFacade().isArchived(eventRef.getUuid());

		if (!DataHelper.isSame(eventRef, criteria.getEvent()) || !viewConfiguration.isRelevanceStatusChanged(eventRef)) {
			criteria.relevanceStatus(isEventArchived ? EntityRelevanceStatus.ACTIVE_AND_ARCHIVED : EntityRelevanceStatus.ACTIVE);
		}
		criteria.withEvent(eventRef);

		if (grid == null) {
			grid = new EventParticipantsGrid(criteria);
			gridLayout = new DetailSubComponentWrapper(() -> null);
			gridLayout.setSizeFull();
			gridLayout.setMargin(true);
			gridLayout.setSpacing(false);
			gridLayout.addComponent(createTopBar());
			gridLayout.addComponent(createStatusFilterBar());
			gridLayout.addComponent(grid);
			gridLayout.setExpandRatio(grid, 1);
			gridLayout.setStyleName("crud-main-layout");
			grid.addDataSizeChangeListener(e -> updateStatusButtons());
			setSubComponent(gridLayout);
			gridLayout.setEnabled(isGridEnabled());
		}

		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
	}

	public HorizontalLayout createStatusFilterBar() {

		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(statusAll);
		activeStatusButton = statusAll;

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);

		// Show active/archived/all dropdown
		if (Objects.nonNull(UserProvider.getCurrent()) && UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_VIEW)) {

			if (FacadeProvider.getFeatureConfigurationFacade()
				.isFeatureEnabled(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.EVENT_PARTICIPANT)) {
				int daysAfterEventParticipantGetsArchived = FacadeProvider.getFeatureConfigurationFacade()
					.getProperty(
						FeatureType.AUTOMATIC_ARCHIVING,
						DeletableEntityType.EVENT_PARTICIPANT,
						FeatureTypeProperty.THRESHOLD_IN_DAYS,
						Integer.class);
				if (daysAfterEventParticipantGetsArchived > 0) {
					relevanceStatusInfoLabel =
						new Label(
							VaadinIcons.INFO_CIRCLE.getHtml() + " "
								+ String
									.format(I18nProperties.getString(Strings.infoArchivedEventParticipants), daysAfterEventParticipantGetsArchived),
							ContentMode.HTML);
					relevanceStatusInfoLabel.setVisible(false);
					relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_SUPER);
					actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
					actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, Alignment.MIDDLE_RIGHT);
				}
			}

			eventParticipantRelevanceStatusFilter = buildRelevanceStatusFilter(
				Captions.eventParticipantActiveEventParticipants,
				Captions.eventParticipantArchivedEventParticipants,
				Captions.eventParticipantActiveAndArchivedEventParticipants);

			eventParticipantRelevanceStatusFilter.addValueChangeListener(e -> {
				viewConfiguration.setRelevanceStatusChangedEvent(getEventRef().getUuid());
				if (relevanceStatusInfoLabel != null) {
					relevanceStatusInfoLabel.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
				}
				criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
				navigateTo(criteria);
			});
			actionButtonsLayout.addComponent(eventParticipantRelevanceStatusFilter);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE)) {
			addButton = ButtonHelper.createIconButton(Captions.eventParticipantAddPerson, VaadinIcons.PLUS_CIRCLE, e -> {
				ControllerProvider.getEventParticipantController().createEventParticipant(this.getEventRef(), r -> navigateTo(criteria));
			}, ValoTheme.BUTTON_PRIMARY);

			actionButtonsLayout.addComponent(addButton);
		}

		statusFilterLayout.addComponent(actionButtonsLayout);
		statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.MIDDLE_RIGHT);
		statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);

		return statusFilterLayout;
	}

	private ComboBox buildRelevanceStatusFilter(
		String eventParticipantActiveCaption,
		String eventParticipantArchivedCaption,
		String eventParticipantAllCaption) {

		ComboBox relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
		relevanceStatusFilter.setId("relevanceStatusFilter");
		relevanceStatusFilter.setWidth(200, Unit.PIXELS);
		relevanceStatusFilter.setNullSelectionAllowed(false);
		relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(eventParticipantActiveCaption));
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(eventParticipantArchivedCaption));
		relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE_AND_ARCHIVED, I18nProperties.getCaption(eventParticipantAllCaption));

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_DELETE)) {
			relevanceStatusFilter
				.setItemCaption(EntityRelevanceStatus.DELETED, I18nProperties.getCaption(Captions.eventParticipantDeletedEventParticipants));
		} else {
			relevanceStatusFilter.removeItem(EntityRelevanceStatus.DELETED);
		}

		return relevanceStatusFilter;
	}

	public void updateFilterComponents() {

		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		updateStatusButtons();

		if (eventParticipantRelevanceStatusFilter != null) {
			eventParticipantRelevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}

		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	private void updateStatusButtons() {

		if (activeStatusButton != null) {
			activeStatusButton
				.setCaption(I18nProperties.getCaption(Captions.all) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getDataSize())));
		}
	}

	@Override
	public String getName() {
		return VIEW_NAME;
	}
}

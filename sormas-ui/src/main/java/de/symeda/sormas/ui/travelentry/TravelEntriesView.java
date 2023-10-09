package de.symeda.sormas.ui.travelentry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.travelentry.importer.TravelEntryImportLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;
import de.symeda.sormas.ui.utils.components.expandablebutton.ExpandableButton;

public class TravelEntriesView extends AbstractView {

	public static final String VIEW_NAME = "travelEntries";

	private final TravelEntryCriteria criteria;
	private ViewConfiguration viewConfiguration;
	private final FilteredGrid<?, TravelEntryCriteria> grid;
	private TravelEntryFilterForm filterForm;

	// Filters
	private Label relevanceStatusInfoLabel;
	private ComboBox relevanceStatusFilter;

	// Bulk operations
	private MenuBar bulkOperationsDropdown;

	public TravelEntriesView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(getClass()).get(ViewConfiguration.class);

		criteria = ViewModelProviders.of(TravelEntriesView.class).get(TravelEntryCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = new TravelEntryGrid(criteria);

		final VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_CREATE)) {
			addHeaderComponent(ButtonHelper.createIconButton(I18nProperties.getCaption(Captions.actionImport), VaadinIcons.UPLOAD, e -> {
				Window popupWindow = VaadinUiUtil.showPopupWindow(new TravelEntryImportLayout());
				popupWindow.setCaption(I18nProperties.getString(Strings.headingImportTravelEntries));
				popupWindow.addCloseListener(c -> ((TravelEntryGrid) grid).reload());
			}));

			long countTravelEntries = FacadeProvider.getTravelEntryFacade().count(new TravelEntryCriteria(), true);
			if (countTravelEntries > 0) {
				final ExpandableButton createButton =
					new ExpandableButton(Captions.travelEntryNewTravelEntry).expand(e -> ControllerProvider.getTravelEntryController().create());
				addHeaderComponent(createButton);
			}
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_DELETE)
			&& UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {

			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(true);
				ViewModelProviders.of(TravelEntriesView.class).get(ViewConfiguration.class).setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				((TravelEntryGrid) grid).reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(false);
				ViewModelProviders.of(TravelEntriesView.class).get(ViewConfiguration.class).setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				navigateTo(criteria);
			});
		}
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}

		if (viewConfiguration.isInEagerMode()) {
			TravelEntryGrid travelEntryGrid = (TravelEntryGrid) this.grid;
			travelEntryGrid.setEagerDataProvider();
		}

		updateFilterComponents();
	}

	public void updateFilterComponents() {
		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}

		filterForm.setValue(criteria);

		applyingCriteria = false;
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterForm = new TravelEntryFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				navigateTo(null);
			}
		});

		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(TravelEntriesView.class).remove(TravelEntryCriteria.class);
			navigateTo(null, true);
		});

		filterForm.addApplyHandler(clickEvent -> ((TravelEntryGrid) grid).reload());
		filterLayout.addComponent(filterForm);

		return filterLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);

		// Show active/archived/all dropdown
		if (Objects.nonNull(UserProvider.getCurrent()) && UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_VIEW)) {

			if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.TRAVEL_ENTRY)) {

				int daysAfterTravelEntryGetsArchived = FacadeProvider.getFeatureConfigurationFacade()
					.getProperty(
						FeatureType.AUTOMATIC_ARCHIVING,
						DeletableEntityType.TRAVEL_ENTRY,
						FeatureTypeProperty.THRESHOLD_IN_DAYS,
						Integer.class);
				if (daysAfterTravelEntryGetsArchived > 0) {
					relevanceStatusInfoLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " "
							+ String.format(I18nProperties.getString(Strings.infoArchivedTravelEntries), daysAfterTravelEntryGetsArchived),
						ContentMode.HTML);
					relevanceStatusInfoLabel.setVisible(false);
					relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_SUPER);
					actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
					actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, Alignment.MIDDLE_RIGHT);
				}
			}
			relevanceStatusFilter = ComboBoxHelper.createComboBoxV7();
			relevanceStatusFilter.setId("relevanceStatus");
			relevanceStatusFilter.setWidth(250, Unit.PIXELS);
			relevanceStatusFilter.setNullSelectionAllowed(false);
			relevanceStatusFilter.setTextInputAllowed(false);
			relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
			relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.travelEntryActiveTravelEntries));
			relevanceStatusFilter
				.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.travelEntryArchivedTravelEntries));
			relevanceStatusFilter.setItemCaption(
				EntityRelevanceStatus.ACTIVE_AND_ARCHIVED,
				I18nProperties.getCaption(Captions.travelEntryAllActiveAndArchivedTravelEntries));
			relevanceStatusFilter.setCaption("");

			if (UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_DELETE)) {
				relevanceStatusFilter
					.setItemCaption(EntityRelevanceStatus.DELETED, I18nProperties.getCaption(Captions.TravelEntry_deletedTravelEntries));
			} else {
				relevanceStatusFilter.removeItem(EntityRelevanceStatus.DELETED);
			}

			relevanceStatusFilter.addValueChangeListener(e -> {
				if (relevanceStatusInfoLabel != null) {
					relevanceStatusInfoLabel.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
				}
				criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
				navigateTo(criteria);
			});
			actionButtonsLayout.addComponent(relevanceStatusFilter);
		}

		// Bulk operation dropdown
		if (UserProvider.getCurrent().hasUserRight(UserRight.TRAVEL_ENTRY_DELETE)
			&& UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			List<MenuBarHelper.MenuBarItem> bulkActions = new ArrayList<>();
			if (criteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
				bulkActions.add(
					new MenuBarHelper.MenuBarItem(
						I18nProperties.getCaption(Captions.bulkDelete),
						VaadinIcons.TRASH,
						mi -> grid.bulkActionHandler(
							items -> ControllerProvider.getTravelEntryController()
								.deleteAllSelectedItems(items, (TravelEntryGrid) grid, () -> navigateTo(criteria)),
							true)));
			} else {
				bulkActions.add(
					new MenuBarHelper.MenuBarItem(
						I18nProperties.getCaption(Captions.bulkRestore),
						VaadinIcons.ARROW_BACKWARD,
						mi -> grid.bulkActionHandler(
							items -> ControllerProvider.getTravelEntryController()
								.restoreSelectedTravelEntries(items, (TravelEntryGrid) grid, () -> navigateTo(criteria)),
							true)));
			}

			bulkOperationsDropdown = MenuBarHelper.createDropDown(Captions.bulkActions, bulkActions);

			bulkOperationsDropdown.setVisible(viewConfiguration.isInEagerMode());
			bulkOperationsDropdown.setCaption("");
			actionButtonsLayout.addComponent(bulkOperationsDropdown);
		}

		if (actionButtonsLayout.getComponentCount() > 0) {
			statusFilterLayout.addComponent(actionButtonsLayout);
			statusFilterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
			statusFilterLayout.setExpandRatio(actionButtonsLayout, 1);
		}

		return statusFilterLayout;
	}

}

package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.TextField;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.region.AreaCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.configuration.AbstractConfigurationView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.RowCount;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ViewConfiguration;

import java.util.Date;

import static com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class AreasView extends AbstractConfigurationView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/areas";

	private final AreaCriteria criteria;
	private final ViewConfiguration viewConfiguration;

	private TextField filterTextFilter;
	private ComboBox<EntityRelevanceStatus> filterRelevanceStatus;
	private Button btnResetFilters;

	private final AreasGrid grid;
	protected Button btnCreate;
	private Button btnImport;
	private MenuBar dropdownBulkOperations;

	public AreasView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(AreasView.class).get(ViewConfiguration.class);
		criteria = ViewModelProviders.of(AreasView.class).get(AreaCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = new AreasGrid(criteria);
		VerticalLayout gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(new RowCount(Strings.labelNumberOfAreas, grid.getItemCount()));
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setSizeFull();
		gridLayout.setStyleName("crud-main-layout");

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_IMPORT)) {
			btnImport = ButtonHelper.createIconButton(Captions.actionImport, VaadinIcons.UPLOAD, e -> {
				Window window = VaadinUiUtil.showPopupWindow(new InfrastructureImportLayout(InfrastructureType.AREA));
				window.setCaption(I18nProperties.getString(Strings.headingImportAreas));
				window.addCloseListener(c -> {
					grid.reload();
				});
			}, ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(btnImport);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_EXPORT)) {
			Button btnExport = ButtonHelper.createIconButton(Captions.export, VaadinIcons.TABLE, null, ValoTheme.BUTTON_PRIMARY);
			btnExport.setDescription(I18nProperties.getDescription(Descriptions.descExportButton));
			addHeaderComponent(btnExport);

			StreamResource streamResource = new GridExportStreamResource(
				grid,
				"sormas_areas",
				"sormas_areas_" + DateHelper.formatDateForExport(new Date()) + ".csv",
				AreasGrid.EDIT_BTN_ID);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(btnExport);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_CREATE)) {
			btnCreate = ButtonHelper.createIconButton(
				Captions.actionNewEntry,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getInfrastructureController().createArea(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(btnCreate);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			Button btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, null);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			addHeaderComponent(btnEnterBulkEditMode);

			Button btnLeaveBulkEditMode =
				ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, null, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				dropdownBulkOperations.setVisible(true);
				viewConfiguration.setInEagerMode(true);
				btnEnterBulkEditMode.setVisible(false);
				btnLeaveBulkEditMode.setVisible(true);
				filterTextFilter.setEnabled(false);
				grid.setEagerDataProvider();
				grid.reload();
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				dropdownBulkOperations.setVisible(false);
				viewConfiguration.setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				filterTextFilter.setEnabled(true);
				navigateTo(criteria);
			});
		}

		addComponent(gridLayout);
	}

	private HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setMargin(false);
		filterLayout.setSpacing(true);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterTextFilter = new TextField();
		filterTextFilter.setId("search");
		filterTextFilter.setWidth(300, Unit.PIXELS);
		filterTextFilter.setInputPrompt(I18nProperties.getString(Strings.promptSearch));
		filterTextFilter.setNullRepresentation("");
		filterTextFilter.addTextChangeListener(e -> {
			criteria.textFilter(e.getText());
			navigateTo(criteria);
		});
		CssStyles.style(filterTextFilter, CssStyles.FORCE_CAPTION);
		filterLayout.addComponent(filterTextFilter);

		btnResetFilters = ButtonHelper.createButton(Captions.actionResetFilters, event -> {
			ViewModelProviders.of(AreasView.class).remove(AreaCriteria.class);
			navigateTo(null);
		}, CssStyles.FORCE_CAPTION);
		btnResetFilters.setVisible(false);
		filterLayout.addComponent(btnResetFilters);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.INFRASTRUCTURE_VIEW_ARCHIVED)) {
				filterRelevanceStatus = new ComboBox<>();
				filterRelevanceStatus.setId("relevanceStatus");
				filterRelevanceStatus.setWidth(220, Unit.PERCENTAGE);
				filterRelevanceStatus.setEmptySelectionAllowed(false);
				filterRelevanceStatus.setItems(EntityRelevanceStatus.values());
				filterRelevanceStatus.setItemCaptionGenerator(status -> {
					switch (status) {
					case ACTIVE:
						return I18nProperties.getCaption(Captions.areaActiveAreas);
					case ARCHIVED:
						return I18nProperties.getCaption(Captions.areaArchivedAreas);
					default:
						return I18nProperties.getCaption(Captions.areaAllAreas);
					}
				});
				filterRelevanceStatus.addValueChangeListener(e -> {
					criteria.relevanceStatus(e.getValue());
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(filterRelevanceStatus);

				// Bulk operation dropdown
				if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
					dropdownBulkOperations = MenuBarHelper.createDropDown(
						Captions.bulkActions,
						new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.actionArchive), VaadinIcons.ARCHIVE, selectedItem -> {
							ControllerProvider.getInfrastructureController()
								.archiveOrDearchiveAllSelectedItems(
									true,
									grid.asMultiSelect().getSelectedItems(),
									InfrastructureType.AREA,
									null,
									() -> navigateTo(criteria));
						}, EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())),
						new MenuBarHelper.MenuBarItem(I18nProperties.getCaption(Captions.actionDearchive), VaadinIcons.ARCHIVE, selectedItem -> {
							ControllerProvider.getInfrastructureController()
								.archiveOrDearchiveAllSelectedItems(
									false,
									grid.asMultiSelect().getSelectedItems(),
									InfrastructureType.AREA,
									null,
									() -> navigateTo(criteria));
						}, EntityRelevanceStatus.ARCHIVED.equals(criteria.getRelevanceStatus())));

					dropdownBulkOperations
						.setVisible(viewConfiguration.isInEagerMode() && !EntityRelevanceStatus.ALL.equals(criteria.getRelevanceStatus()));
					actionButtonsLayout.addComponent(dropdownBulkOperations);
				}
			}
		}
		filterLayout.addComponent(actionButtonsLayout);
		filterLayout.setComponentAlignment(actionButtonsLayout, Alignment.BOTTOM_RIGHT);
		filterLayout.setExpandRatio(actionButtonsLayout, 1);

		return filterLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}
		updateFilterComponents();
		grid.reload();
	}

	public void updateFilterComponents() {
		applyingCriteria = true;

		btnResetFilters.setVisible(criteria.hasAnyFilterActive());

		if (filterRelevanceStatus != null) {
			filterRelevanceStatus.setValue(criteria.getRelevanceStatus());
		}
		filterTextFilter.setValue(criteria.getTextFilter());

		applyingCriteria = false;
	}

}

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
package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.FollowUpUtils.createFollowUpLegend;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import de.symeda.sormas.api.utils.DataHelper;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
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

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.bagexport.BAGExportCaseDto;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseExportType;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleExportDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SearchSpecificLayout;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.exporter.CaseExportConfigurationsLayout;
import de.symeda.sormas.ui.caze.importer.CaseImportLayout;
import de.symeda.sormas.ui.caze.importer.LineListingImportLayout;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CaseDownloadUtil;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateHelper8;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.MenuBarHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * A view for performing create-read-update-delete operations on products.
 * <p>
 * See also {@link CaseController} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
public class CasesView extends AbstractView {

	private static final long serialVersionUID = -3533557348144005469L;

	public static final String VIEW_NAME = "cases";

	/**
	 * When the number of cases exceeds this amount, the user will be confronted with a warning when trying
	 * to enter bulk edit mode.
	 */
	public static final int BULK_EDIT_MODE_WARNING_THRESHOLD = 1000;

	private final boolean caseFollowUpEnabled;
	private final ExportConfigurationDto detailedExportConfiguration;

	private CaseCriteria criteria;
	private CasesViewConfiguration viewConfiguration;

	private FilteredGrid<?, CaseCriteria> grid;
	private Button createButton;
	private Button lineListingButton;
	private HashMap<Button, String> statusButtons;
	private Button activeStatusButton;
	private PopupButton moreButton;
	private VerticalLayout moreLayout;

	private VerticalLayout gridLayout;

	private CaseFilterForm filterForm;

	// Filters
	private Label relevanceStatusInfoLabel;
	private ComboBox relevanceStatusFilter;

	// Bulk operations
	private MenuBar bulkOperationsDropdown;
	private Button btnEnterBulkEditMode;
	private Button btnLeaveBulkEditMode;

	private int followUpRangeInterval = 14;
	private boolean buttonPreviousOrNextClick = false;
	private Date followUpToDate;

	public CasesView() {

		super(VIEW_NAME);

		caseFollowUpEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_FOLLOWUP);
		detailedExportConfiguration = buildDetailedExportConfiguration();
		viewConfiguration = ViewModelProviders.of(CasesView.class).get(CasesViewConfiguration.class);
		if (viewConfiguration.getViewType() == null) {
			viewConfiguration.setViewType(CasesViewType.DEFAULT);
		}

		criteria = ViewModelProviders.of(CasesView.class).get(CaseCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		if (CasesViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
			criteria.followUpUntilFrom(DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 4)));
			grid = new CaseFollowUpGrid(criteria, new Date(), followUpRangeInterval, getClass());
		} else {
			criteria.followUpUntilFrom(null);
			grid = CasesViewType.DETAILED.equals(viewConfiguration.getViewType()) ? new CaseGridDetailed(criteria) : new CaseGrid(criteria);
		}
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);

		if (CasesViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
			gridLayout.addComponent(createFollowUpLegend());
		}

		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());

		OptionGroup casesViewSwitcher = new OptionGroup();
		casesViewSwitcher.setId("casesViewSwitcher");
		CssStyles.style(casesViewSwitcher, CssStyles.FORCE_CAPTION, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_PRIMARY);
		casesViewSwitcher.addItem(CasesViewType.DEFAULT);
		casesViewSwitcher.setItemCaption(CasesViewType.DEFAULT, I18nProperties.getCaption(Captions.caseDefaultView));

		casesViewSwitcher.addItem(CasesViewType.DETAILED);
		casesViewSwitcher.setItemCaption(CasesViewType.DETAILED, I18nProperties.getCaption(Captions.caseDetailedView));

		if (caseFollowUpEnabled) {
			casesViewSwitcher.addItem(CasesViewType.FOLLOW_UP_VISITS_OVERVIEW);
			casesViewSwitcher.setItemCaption(CasesViewType.FOLLOW_UP_VISITS_OVERVIEW, I18nProperties.getCaption(Captions.caseFollowupVisitsView));
		}

		casesViewSwitcher.setValue(viewConfiguration.getViewType());
		casesViewSwitcher.addValueChangeListener(e -> {
			CasesViewType viewType = (CasesViewType) e.getProperty().getValue();

			viewConfiguration.setViewType(viewType);
			SormasUI.get().getNavigator().navigateTo(CasesView.VIEW_NAME);
		});
		addHeaderComponent(casesViewSwitcher);

		if (viewConfiguration.getViewType().isCaseOverview()) {
			addCommonCasesOverviewToolbar();
		}

		addComponent(gridLayout);
	}

	private ExportConfigurationDto buildDetailedExportConfiguration() {
		ExportConfigurationDto res = ExportConfigurationDto.build(UserProvider.getCurrent().getUserReference());

		res.setExportType(ExportType.CASE);

		res.setProperties(
			ImportExportUtils.getCaseExportProperties(caseFollowUpEnabled, UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS))
				.stream()
				.map(DataHelper.Pair::getElement0)
				.collect(Collectors.toSet()));
		return res;
	}

	private void addCommonCasesOverviewToolbar() {
		moreButton = new PopupButton(I18nProperties.getCaption(Captions.moreActions));
		moreButton.setId("more");
		moreButton.setIcon(VaadinIcons.ELLIPSIS_DOTS_V);
		moreLayout = new VerticalLayout();
		moreLayout.setSpacing(true);
		moreLayout.setMargin(true);
		moreLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
		moreLayout.setWidth(250, Unit.PIXELS);
		moreButton.setContent(moreLayout);

		Button openGuideButton = ButtonHelper
			.createIconButton(Captions.caseOpenCasesGuide, VaadinIcons.QUESTION, e -> buildAndOpenCasesInstructions(), ValoTheme.BUTTON_PRIMARY);
		openGuideButton.setWidth(100, Unit.PERCENTAGE);
		moreLayout.addComponent(openGuideButton);

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_IMPORT)) {
			VerticalLayout importLayout = new VerticalLayout();
			{
				importLayout.setSpacing(true);
				importLayout.setMargin(true);
				importLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				importLayout.setWidth(250, Unit.PIXELS);

				PopupButton importButton = ButtonHelper.createIconPopupButton(Captions.actionImport, VaadinIcons.UPLOAD, importLayout);

				addHeaderComponent(importButton);
			}
			addImportButton(importLayout, Captions.importLineListing, Strings.headingLineListingImport, LineListingImportLayout::new);
			addImportButton(importLayout, Captions.importDetailed, Strings.headingImportCases, CaseImportLayout::new);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_EXPORT)) {
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
					new GridExportStreamResource(grid, "sormas_cases", createFileNameWithCurrentDate("sormas_cases_", ".csv"));
				addExportButton(streamResource, exportPopupButton, exportLayout, VaadinIcons.TABLE, Captions.exportBasic, Strings.infoBasicExport);
			}

			{
				StreamResource exportStreamResource =
					CaseDownloadUtil.createCaseExportResource(grid.getCriteria(), CaseExportType.CASE_SURVEILLANCE, detailedExportConfiguration);

				addExportButton(
					exportStreamResource,
					exportPopupButton,
					exportLayout,
					VaadinIcons.FILE_TEXT,
					Captions.exportDetailed,
					Strings.infoDetailedExport);
			}

			if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS)) {
				StreamResource caseManagementExportStreamResource = DownloadUtil
					.createCaseManagementExportResource(grid.getCriteria(), createFileNameWithCurrentDate("sormas_case_management_", ".zip"));
				addExportButton(
					caseManagementExportStreamResource,
					exportPopupButton,
					exportLayout,
					VaadinIcons.FILE_TEXT,
					Captions.exportCaseManagement,
					Strings.infoCaseManagementExport);
			}

			{
				StreamResource sampleExportStreamResource = DownloadUtil.createCsvExportStreamResource(
					SampleExportDto.class,
					null,
					(Integer start, Integer max) -> FacadeProvider.getSampleFacade().getExportList(grid.getCriteria(), start, max),
					(propertyId, type) -> {
						String caption = I18nProperties.findPrefixCaption(
							propertyId,
							SampleExportDto.I18N_PREFIX,
							SampleDto.I18N_PREFIX,
							CaseDataDto.I18N_PREFIX,
							PersonDto.I18N_PREFIX,
							AdditionalTestDto.I18N_PREFIX);
						if (Date.class.isAssignableFrom(type)) {
							caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
						}
						return caption;
					},
					createFileNameWithCurrentDate("sormas_samples_", ".csv"),
					null);
				addExportButton(
					sampleExportStreamResource,
					exportPopupButton,
					exportLayout,
					VaadinIcons.FILE_TEXT,
					Captions.exportSamples,
					Strings.infoSampleExport);
			}

			if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_SWITZERLAND)
				&& UserProvider.getCurrent().hasUserRight(UserRight.BAG_EXPORT)) {
				StreamResource bagExportResource = DownloadUtil.createCsvExportStreamResource(
					BAGExportCaseDto.class,
					null,
					(Integer start, Integer max) -> FacadeProvider.getBAGExportFacade().getCaseExportList(start, max),
					(propertyId, type) -> {
						String caption = I18nProperties.findPrefixCaption(propertyId);
						if (Date.class.isAssignableFrom(type)) {
							caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
						}
						return caption;
					},
					createFileNameWithCurrentDate("sormas_BAG_cases_", ".csv"),
					null);

				addExportButton(bagExportResource, exportPopupButton, exportLayout, VaadinIcons.FILE_TEXT, Captions.BAGExport, Strings.infoBAGExport);
			}

			{
				Button btnCustomCaseExport = ButtonHelper.createIconButton(Captions.exportCaseCustom, VaadinIcons.FILE_TEXT, e -> {
					Window customExportWindow = VaadinUiUtil.createPopupWindow();
					CaseExportConfigurationsLayout customExportsLayout = new CaseExportConfigurationsLayout(customExportWindow::close);
					customExportsLayout.setExportCallback((exportConfig) -> {
						Page.getCurrent().open(CaseDownloadUtil.createCaseExportResource(grid.getCriteria(), null, exportConfig), null, true);
					});
					customExportWindow.setWidth(1024, Unit.PIXELS);
					customExportWindow.setCaption(I18nProperties.getCaption(Captions.exportCaseCustom));
					customExportWindow.setContent(customExportsLayout);
					UI.getCurrent().addWindow(customExportWindow);
				}, ValoTheme.BUTTON_PRIMARY);
				btnCustomCaseExport.setDescription(I18nProperties.getString(Strings.infoCustomCaseExport));
				btnCustomCaseExport.setWidth(100, Unit.PERCENTAGE);
				exportLayout.addComponent(btnCustomCaseExport);
			}

			{
				// Warning if no filters have been selected
				Label warningLabel = new Label(I18nProperties.getString(Strings.infoExportNoFilters), ContentMode.HTML);
				warningLabel.setWidth(100, Unit.PERCENTAGE);
				exportLayout.addComponent(warningLabel);
				warningLabel.setVisible(false);

				exportPopupButton.addClickListener(e -> warningLabel.setVisible(!criteria.hasAnyFilterActive()));
			}
		}

		if (isBulkEditAllowed()) {
			btnEnterBulkEditMode = ButtonHelper.createIconButton(Captions.actionEnterBulkEditMode, VaadinIcons.CHECK_SQUARE_O, e -> {
				if (grid.getItemCount() > BULK_EDIT_MODE_WARNING_THRESHOLD) {
					VaadinUiUtil.showConfirmationPopup(
						I18nProperties.getCaption(Captions.actionEnterBulkEditMode),
						new Label(String.format(I18nProperties.getString(Strings.confirmationEnterBulkEditMode), BULK_EDIT_MODE_WARNING_THRESHOLD)),
						I18nProperties.getString(Strings.yes),
						I18nProperties.getString(Strings.no),
						640,
						(result) -> {
							if (result.booleanValue() == true) {
								enterBulkEditMode();
							}
						});
				} else {
					enterBulkEditMode();
				}
			}, ValoTheme.BUTTON_PRIMARY);

			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			btnEnterBulkEditMode.setWidth(100, Unit.PERCENTAGE);
			moreLayout.addComponent(btnEnterBulkEditMode);

			btnLeaveBulkEditMode = ButtonHelper.createIconButton(Captions.actionLeaveBulkEditMode, VaadinIcons.CLOSE, e -> {
				bulkOperationsDropdown.setVisible(false);
				viewConfiguration.setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				this.filterForm.enableSearchAndReportingUser();
				navigateTo(criteria);
			}, ValoTheme.BUTTON_PRIMARY);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());

			addHeaderComponent(btnLeaveBulkEditMode);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_MERGE)) {
			Button mergeDuplicatesButton = ButtonHelper.createIconButton(
				Captions.caseMergeDuplicates,
				VaadinIcons.COMPRESS_SQUARE,
				e -> ControllerProvider.getCaseController().navigateToMergeCasesView(),
				ValoTheme.BUTTON_PRIMARY);
			mergeDuplicatesButton.setWidth(100, Unit.PERCENTAGE);
			moreLayout.addComponent(mergeDuplicatesButton);
		}

		Button searchSpecificCaseButton = ButtonHelper.createIconButton(
			Captions.caseSearchSpecificCase,
			VaadinIcons.SEARCH,
			e -> buildAndOpenSearchSpecificCaseWindow(),
			ValoTheme.BUTTON_PRIMARY);
		searchSpecificCaseButton.setWidth(100, Unit.PERCENTAGE);
		moreLayout.addComponent(searchSpecificCaseButton);

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_CREATE)) {
			lineListingButton = ButtonHelper.createIconButton(
				Captions.caseLineListing,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getCaseController().openLineListingWindow(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(lineListingButton);

			createButton = ButtonHelper.createIconButton(
				Captions.caseNewCase,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getCaseController().create(),
				ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(createButton);
		}
		addHeaderComponent(moreButton);
	}

	protected void changeViewType(CasesViewType type) {
		viewConfiguration.setViewType(type);
		SormasUI.get().getNavigator().navigateTo(CasesView.VIEW_NAME);
	}

	private void addImportButton(VerticalLayout importLayout, String captionKey, String windowHeadingKey, Supplier<Component> windowContentSupplier) {
		Button lineListingImportButton = ButtonHelper.createIconButton(captionKey, VaadinIcons.UPLOAD, e -> {
			Window popupWindow = VaadinUiUtil.showPopupWindow(windowContentSupplier.get());
			popupWindow.setCaption(I18nProperties.getString(windowHeadingKey));
			AbstractCaseGrid<?> caseGrid = (AbstractCaseGrid<?>) this.grid;
			popupWindow.addCloseListener(c -> caseGrid.reload());
		}, ValoTheme.BUTTON_PRIMARY);
		lineListingImportButton.setWidth(100, Unit.PERCENTAGE);
		importLayout.addComponent(lineListingImportButton);
	}

	private void buildAndOpenCasesInstructions() {
		Window window = VaadinUiUtil.showPopupWindow(new CasesGuideLayout());
		window.setWidth(1024, Unit.PIXELS);
		window.setCaption(I18nProperties.getString(Strings.headingCasesGuide));
	}

	private void buildAndOpenSearchSpecificCaseWindow() {
		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getCaption(Captions.caseSearchSpecificCase));
		window.setWidth(768, Unit.PIXELS);

		SearchSpecificLayout layout = buildSearchSpecificLayout(window);
		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}

	private SearchSpecificLayout buildSearchSpecificLayout(Window window) {

		String description = I18nProperties.getString(Strings.infoSpecificCaseSearch);
		String confirmCaption = I18nProperties.getCaption(Captions.caseSearchCase);

		TextField searchField = new TextField();
		Runnable confirmCallback = () -> {
			String foundCaseUuid = FacadeProvider.getCaseFacade().getUuidByUuidEpidNumberOrExternalId(searchField.getValue());

			if (foundCaseUuid != null) {
				ControllerProvider.getCaseController().navigateToCase(foundCaseUuid);
				window.close();
			} else {
				VaadinUiUtil.showSimplePopupWindow(
					I18nProperties.getString(Strings.headingNoCaseFound),
					I18nProperties.getString(Strings.messageNoCaseFound));
			}
		};

		return new SearchSpecificLayout(confirmCallback, () -> window.close(), searchField, description, confirmCaption);
	}

	private void enterBulkEditMode() {
		bulkOperationsDropdown.setVisible(true);
		viewConfiguration.setInEagerMode(true);
		btnEnterBulkEditMode.setVisible(false);
		btnLeaveBulkEditMode.setVisible(true);
		filterForm.disableSearchAndReportingUser();
		AbstractCaseGrid<?> caseGrid = (AbstractCaseGrid<?>) this.grid;
		caseGrid.setEagerDataProvider();
		caseGrid.reload();
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterForm = new CaseFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!filterForm.hasFilter()) {
				navigateTo(null);
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(CasesView.class).remove(CaseCriteria.class);
			navigateTo(null, true);
		});
		filterForm.addApplyHandler(e -> {
			if (!navigateTo(criteria, false)) {
				if (CasesViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
					((CaseFollowUpGrid) grid).reload();
				} else {
					((AbstractCaseGrid<?>) grid).reload();
				}
			}
		});
		filterLayout.addComponent(filterForm);

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Follow-up overview scrolling
			if (CasesViewType.FOLLOW_UP_VISITS_OVERVIEW.equals(viewConfiguration.getViewType())) {
				filterLayout.setWidth(100, Unit.PERCENTAGE);

				HorizontalLayout scrollLayout = new HorizontalLayout();
				scrollLayout.setMargin(false);

				DateField toReferenceDate = new DateField(I18nProperties.getCaption(Captions.to), LocalDate.now());
				toReferenceDate.setId("toReferenceDateField");
				LocalDate fromReferenceLocal =
					DateHelper8.toLocalDate(DateHelper.subtractDays(DateHelper8.toDate(LocalDate.now()), followUpRangeInterval - 1));
				DateField fromReferenceDate = new DateField(I18nProperties.getCaption(Captions.from), fromReferenceLocal);
				fromReferenceDate.setId("fromReferenceDateField");

				Button minusDaysButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.caseMinusDays), e -> {
					followUpRangeInterval =
						DateHelper.getDaysBetween(DateHelper8.toDate(fromReferenceDate.getValue()), DateHelper8.toDate(toReferenceDate.getValue()));
					buttonPreviousOrNextClick = true;
					toReferenceDate.setValue(toReferenceDate.getValue().minusDays(followUpRangeInterval));
					fromReferenceDate.setValue(fromReferenceDate.getValue().minusDays(followUpRangeInterval));
				}, ValoTheme.BUTTON_PRIMARY, CssStyles.FORCE_CAPTION);
				scrollLayout.addComponent(minusDaysButton);

				fromReferenceDate.addValueChangeListener(e -> {
					Date newFromDate = e.getValue() != null ? DateHelper8.toDate(e.getValue()) : new Date();
					applyingCriteria = true;
					criteria.followUpUntilFrom(DateHelper.getStartOfDay(newFromDate));
					applyingCriteria = false;
					followUpRangeInterval = DateHelper.getDaysBetween(newFromDate, DateHelper8.toDate(toReferenceDate.getValue()));
					reloadGrid();
				});
				scrollLayout.addComponent(fromReferenceDate);

				toReferenceDate.addValueChangeListener(e -> {
					followUpToDate = e.getValue() != null ? DateHelper8.toDate(e.getValue()) : new Date();
					applyingCriteria = true;
					criteria.reportDateTo(DateHelper.getEndOfDay(followUpToDate));
					applyingCriteria = false;
					if (!buttonPreviousOrNextClick) {
						followUpRangeInterval = DateHelper.getDaysBetween(DateHelper8.toDate(fromReferenceDate.getValue()), followUpToDate);
						reloadGrid();
					}
				});
				scrollLayout.addComponent(toReferenceDate);

				Button plusDaysButton = ButtonHelper.createButton(I18nProperties.getCaption(Captions.casePlusDays), e -> {
					followUpRangeInterval =
						DateHelper.getDaysBetween(DateHelper8.toDate(fromReferenceDate.getValue()), DateHelper8.toDate(toReferenceDate.getValue()));
					buttonPreviousOrNextClick = true;
					toReferenceDate.setValue(toReferenceDate.getValue().plusDays(followUpRangeInterval));
					fromReferenceDate.setValue(fromReferenceDate.getValue().plusDays(followUpRangeInterval));
				}, ValoTheme.BUTTON_PRIMARY, CssStyles.FORCE_CAPTION);
				scrollLayout.addComponent(plusDaysButton);

				actionButtonsLayout.addComponent(scrollLayout);

			}
		}
		filterLayout.addComponent(actionButtonsLayout);
		filterLayout.setComponentAlignment(actionButtonsLayout, Alignment.TOP_RIGHT);
		filterLayout.setExpandRatio(actionButtonsLayout, 1);

		return filterLayout;
	}

	private void reloadGrid() {
		((CaseFollowUpGrid) grid).setVisitColumns(followUpToDate, followUpRangeInterval, criteria);
		((CaseFollowUpGrid) grid).reload();
		updateStatusButtons();
		buttonPreviousOrNextClick = false;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		Button statusAll = ButtonHelper.createButton(Captions.all, e -> {
			criteria.investigationStatus(null);
			navigateTo(criteria);
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);

		statusFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
		activeStatusButton = statusAll;

		for (InvestigationStatus status : InvestigationStatus.values()) {
			Button statusButton = ButtonHelper.createButton(status.toString(), e -> {
				criteria.investigationStatus(status);
				navigateTo(criteria);
			}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
			statusButton.setData(status);
			statusButton.setCaptionAsHtml(true);

			statusFilterLayout.addComponent(statusButton);
			statusButtons.put(statusButton, status.toString());
		}

		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		{
			// Show active/archived/all dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW_ARCHIVED)) {
				int daysAfterCaseGetsArchived = FacadeProvider.getConfigFacade().getDaysAfterCaseGetsArchived();
				if (daysAfterCaseGetsArchived > 0) {
					relevanceStatusInfoLabel = new Label(
						VaadinIcons.INFO_CIRCLE.getHtml() + " "
							+ String.format(I18nProperties.getString(Strings.infoArchivedCases), daysAfterCaseGetsArchived),
						ContentMode.HTML);
					relevanceStatusInfoLabel.setVisible(false);
					relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_SUPER);
					actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
					actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, Alignment.MIDDLE_RIGHT);
				}
				relevanceStatusFilter = new ComboBox();
				relevanceStatusFilter.setId("relevanceStatus");
				relevanceStatusFilter.setWidth(140, Unit.PIXELS);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.caseActiveCases));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.caseArchivedCases));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.caseAllCases));
				relevanceStatusFilter.addValueChangeListener(e -> {
					relevanceStatusInfoLabel.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);
			}

			if (viewConfiguration.getViewType().isCaseOverview()) {
				AbstractCaseGrid<?> caseGrid = (AbstractCaseGrid<?>) this.grid;
				// Bulk operation dropdown
				if (isBulkEditAllowed()) {
					boolean hasBulkOperationsRight = UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS);

					bulkOperationsDropdown = MenuBarHelper.createDropDown(
						Captions.bulkActions,
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.bulkEdit),
							VaadinIcons.ELLIPSIS_H,
							mi -> ControllerProvider.getCaseController().showBulkCaseDataEditComponent(caseGrid.asMultiSelect().getSelectedItems()),
							hasBulkOperationsRight),
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.bulkDelete),
							VaadinIcons.TRASH,
							selectedItem -> ControllerProvider.getCaseController()
								.deleteAllSelectedItems(caseGrid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria)),
							hasBulkOperationsRight),
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.actionArchive),
							VaadinIcons.ARCHIVE,
							mi -> ControllerProvider.getCaseController()
								.archiveAllSelectedItems(caseGrid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria)),
							hasBulkOperationsRight && EntityRelevanceStatus.ACTIVE.equals(criteria.getRelevanceStatus())),
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.actionDearchive),
							VaadinIcons.ARCHIVE,
							mi -> ControllerProvider.getCaseController()
								.dearchiveAllSelectedItems(caseGrid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria)),
							hasBulkOperationsRight && EntityRelevanceStatus.ARCHIVED.equals(criteria.getRelevanceStatus())),
						new MenuBarHelper.MenuBarItem(
							I18nProperties.getCaption(Captions.sormasToSormasShare),
							VaadinIcons.SHARE,
							mi -> ControllerProvider.getSormasToSormasController()
								.shareSelectedCases(caseGrid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria)),
							FacadeProvider.getSormasToSormasFacade().isFeatureEnabled()));

					bulkOperationsDropdown.setVisible(viewConfiguration.isInEagerMode());
					actionButtonsLayout.addComponent(bulkOperationsDropdown);
				}
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

		if (viewConfiguration.isInEagerMode()) {
			AbstractCaseGrid<?> caseGrid = (AbstractCaseGrid<?>) this.grid;
			caseGrid.setEagerDataProvider();
		}

		updateFilterComponents();
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
			if (b.getData() == criteria.getInvestigationStatus()) {
				activeStatusButton = b;
			}
		});
		CssStyles.removeStyles(activeStatusButton, CssStyles.BUTTON_FILTER_LIGHT);
		if (activeStatusButton != null) {
			activeStatusButton
				.setCaption(statusButtons.get(activeStatusButton) + LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}
	}

	private boolean isBulkEditAllowed() {
		return UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)
			|| FacadeProvider.getSormasToSormasFacade().isFeatureEnabled();
	}
}

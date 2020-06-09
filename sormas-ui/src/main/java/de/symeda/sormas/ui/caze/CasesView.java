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

import java.util.Date;
import java.util.HashMap;
import java.util.function.Supplier;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.*;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleExportDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.exporter.CaseExportConfigurationsLayout;
import de.symeda.sormas.ui.caze.importer.CaseImportLayout;
import de.symeda.sormas.ui.caze.importer.LineListingImportLayout;
import de.symeda.sormas.ui.utils.*;

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

	private CaseCriteria criteria;
	private CasesViewConfiguration viewConfiguration;

	private AbstractCaseGrid<?> grid;
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

	public CasesView() {

		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(CasesView.class).get(CasesViewConfiguration.class);
		if (viewConfiguration.getViewType() == null) {
			viewConfiguration.setViewType(CasesViewType.DEFAULT);
		}

		criteria = ViewModelProviders.of(CasesView.class).get(CaseCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = CasesViewType.DEFAULT.equals(viewConfiguration.getViewType()) ? new CaseGrid(criteria) : new CaseGridDetailed(criteria);

		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar());
		gridLayout.addComponent(createStatusFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		grid.getDataProvider().addDataProviderListener(e -> updateStatusButtons());

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
				StreamResource exportStreamResource = DownloadUtil.createCsvExportStreamResource(
					CaseExportDto.class,
					CaseExportType.CASE_SURVEILLANCE,
					(Integer start, Integer max) -> FacadeProvider.getCaseFacade()
						.getExportList(grid.getCriteria(), CaseExportType.CASE_SURVEILLANCE, start, max, null, I18nProperties.getUserLanguage()),
					(propertyId, type) -> {
						String caption = I18nProperties.findPrefixCaption(
							propertyId,
							CaseExportDto.I18N_PREFIX,
							CaseDataDto.I18N_PREFIX,
							PersonDto.I18N_PREFIX,
							LocationDto.I18N_PREFIX,
							SymptomsDto.I18N_PREFIX,
							EpiDataDto.I18N_PREFIX,
							HospitalizationDto.I18N_PREFIX);
						if (Date.class.isAssignableFrom(type)) {
							caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
						}
						return caption;
					},
					createFileNameWithCurrentDate("sormas_cases_", ".csv"),
					null);
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

			{
				Button btnCustomCaseExport = ButtonHelper.createIconButton(Captions.exportCaseCustom, VaadinIcons.FILE_TEXT, e -> {
					Window customExportWindow = VaadinUiUtil.createPopupWindow();
					CaseExportConfigurationsLayout customExportsLayout = new CaseExportConfigurationsLayout(customExportWindow::close);
					customExportsLayout.setExportCallback((exportConfig) -> {
						Page.getCurrent()
							.open(
								DownloadUtil.createCsvExportStreamResource(
									CaseExportDto.class,
									null,
									(Integer start, Integer max) -> FacadeProvider.getCaseFacade()
										.getExportList(grid.getCriteria(), null, start, max, exportConfig, I18nProperties.getUserLanguage()),
									(propertyId, type) -> {
										String caption = I18nProperties.findPrefixCaption(
											propertyId,
											CaseExportDto.I18N_PREFIX,
											CaseDataDto.I18N_PREFIX,
											PersonDto.I18N_PREFIX,
											SymptomsDto.I18N_PREFIX,
											EpiDataDto.I18N_PREFIX,
											HospitalizationDto.I18N_PREFIX);
										if (Date.class.isAssignableFrom(type)) {
											caption += " (" + DateFormatHelper.getDateFormatPattern() + ")";
										}
										return caption;
									},
									createFileNameWithCurrentDate("sormas_cases_", ".csv"),
									exportConfig),
								null,
								true);
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

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
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
				e -> ControllerProvider.getCaseController().lineListing(),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(lineListingButton);

			createButton = ButtonHelper.createIconButton(
				Captions.caseNewCase,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getCaseController().create(),
				ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(createButton);
		}

		Button defaultVewButton = ButtonHelper.createButton(Captions.caseDefaultView, null, ValoTheme.BUTTON_PRIMARY);
		defaultVewButton.setWidth(100, Unit.PERCENTAGE);
		defaultVewButton.setVisible(viewConfiguration.getViewType() == CasesViewType.DETAILED);
		moreLayout.addComponent(defaultVewButton);

		Button detailedViewButton = ButtonHelper.createButton(Captions.caseDetailedView, null, ValoTheme.BUTTON_PRIMARY);
		detailedViewButton.setWidth(100, Unit.PERCENTAGE);
		detailedViewButton.setVisible(viewConfiguration.getViewType() == CasesViewType.DEFAULT);
		moreLayout.addComponent(detailedViewButton);

		defaultVewButton.addClickListener(e -> {
			changeViewType(CasesViewType.DEFAULT);
			defaultVewButton.setVisible(false);
			detailedViewButton.setVisible(true);
		});
		detailedViewButton.addClickListener(e -> {
			changeViewType(CasesViewType.DETAILED);
			defaultVewButton.setVisible(true);
			detailedViewButton.setVisible(false);
		});

		addHeaderComponent(moreButton);

		addComponent(gridLayout);
	}

	protected void changeViewType(CasesViewType type) {
		viewConfiguration.setViewType(type);
		SormasUI.get().getNavigator().navigateTo(CasesView.VIEW_NAME);
	}

	private void addImportButton(VerticalLayout importLayout, String captionKey, String windowHeadingKey, Supplier<Component> windowContentSupplier) {
		Button lineListingImportButton = ButtonHelper.createIconButton(captionKey, VaadinIcons.UPLOAD, e -> {
			Window popupWindow = VaadinUiUtil.showPopupWindow(windowContentSupplier.get());
			popupWindow.setCaption(I18nProperties.getString(windowHeadingKey));
			popupWindow.addCloseListener(c -> grid.reload());
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

		SearchSpecificCaseLayout layout = new SearchSpecificCaseLayout(() -> window.close());
		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}

	private void enterBulkEditMode() {
		bulkOperationsDropdown.setVisible(true);
		viewConfiguration.setInEagerMode(true);
		btnEnterBulkEditMode.setVisible(false);
		btnLeaveBulkEditMode.setVisible(true);
		filterForm.disableSearchAndReportingUser();
		grid.setEagerDataProvider();
		grid.reload();
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterForm = new CaseFilterForm();
		filterForm.addValueChangeListener(e -> {
			if (!navigateTo(criteria, false)) {
				filterForm.updateResetButtonState();
				grid.reload();
			}
		});
		filterForm.addResetHandler(e -> {
			ViewModelProviders.of(CasesView.class).remove(CaseCriteria.class);
			navigateTo(null, true);
		});
		filterLayout.addComponent(filterForm);

		return filterLayout;
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

			// Bulk operation dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				bulkOperationsDropdown = MenuBarHelper.createDropDown(
					Captions.bulkActions,
					new MenuBarHelper.MenuBarItem(
						I18nProperties.getCaption(Captions.bulkEdit),
						VaadinIcons.ELLIPSIS_H,
						mi -> ControllerProvider.getCaseController().showBulkCaseDataEditComponent(grid.asMultiSelect().getSelectedItems())),
					new MenuBarHelper.MenuBarItem(
						I18nProperties.getCaption(Captions.bulkDelete),
						VaadinIcons.TRASH,
						selectedItem -> ControllerProvider.getCaseController()
							.deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria))),
					new MenuBarHelper.MenuBarItem(
						I18nProperties.getCaption(Captions.actionArchive),
						VaadinIcons.ARCHIVE,
						mi -> ControllerProvider.getCaseController()
							.archiveAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria))),
					new MenuBarHelper.MenuBarItem(
						I18nProperties.getCaption(Captions.actionDearchive),
						VaadinIcons.ARCHIVE,
						mi -> ControllerProvider.getCaseController()
							.dearchiveAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria)),
						false));

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
}

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

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
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
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.caze.exporter.CaseExportConfigurationsLayout;
import de.symeda.sormas.ui.caze.importer.CaseImportLayout;
import de.symeda.sormas.ui.caze.importer.LineListingImportLayout;
import de.symeda.sormas.ui.utils.*;
import org.vaadin.hene.popupbutton.PopupButton;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

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
	private ViewConfiguration viewConfiguration;

	private CaseGrid grid;
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
	private MenuItem dearchiveItem;
	private Button btnEnterBulkEditMode;
	private Button btnLeaveBulkEditMode;

	public CasesView() {
		super(VIEW_NAME);

		viewConfiguration = ViewModelProviders.of(CasesView.class).get(ViewConfiguration.class);
		criteria = ViewModelProviders.of(CasesView.class).get(CaseCriteria.class);
		if (criteria.getRelevanceStatus() == null) {
			criteria.relevanceStatus(EntityRelevanceStatus.ACTIVE);
		}

		grid = new CaseGrid(criteria);
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

		if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS) || UserProvider.getCurrent().hasUserRight(UserRight.CASE_MERGE)) {
			moreButton = new PopupButton(I18nProperties.getCaption(Captions.moreActions));
			moreButton.setId("more");
			moreButton.setIcon(VaadinIcons.ELLIPSIS_DOTS_V);
			moreLayout = new VerticalLayout();
			moreLayout.setSpacing(true);
			moreLayout.setMargin(true);
			moreLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
			moreLayout.setWidth(250, Unit.PIXELS);
			moreButton.setContent(moreLayout);
		}

		Button openGuideButton = new Button(I18nProperties.getCaption(Captions.caseOpenCasesGuide));
		openGuideButton.setId("openCasesGuide");
		openGuideButton.setIcon(VaadinIcons.QUESTION);
		openGuideButton.addClickListener(e -> buildAndOpenCasesInstructions());
		if (moreLayout != null) {
			openGuideButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			openGuideButton.setWidth(100, Unit.PERCENTAGE);
			moreLayout.addComponent(openGuideButton);
		} else {
			addHeaderComponent(openGuideButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_IMPORT)) {
			VerticalLayout importLayout = new VerticalLayout();
			{
				PopupButton importButton = new PopupButton(I18nProperties.getCaption(Captions.actionImport));
				importButton.setId("import");
				importButton.setIcon(VaadinIcons.UPLOAD);
				importLayout.setSpacing(true);
				importLayout.setMargin(true);
				importLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				importLayout.setWidth(250, Unit.PIXELS);
				importButton.setContent(importLayout);
				addHeaderComponent(importButton);
			}
			addImportButton(importLayout,
					"lineListingImport", Captions.importLineListing,
					Strings.headingLineListingImport, LineListingImportLayout::new);
			addImportButton(importLayout,
					"extendedImport", Captions.importDetailed,
					Strings.headingImportCases, CaseImportLayout::new);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_EXPORT)) {
			PopupButton exportPopupButton = new PopupButton(I18nProperties.getCaption(Captions.export));
			VerticalLayout exportLayout = new VerticalLayout();
			{
				exportPopupButton.setId("export");
				exportPopupButton.setIcon(VaadinIcons.DOWNLOAD);
				exportLayout.setSpacing(true);
				exportLayout.setMargin(true);
				exportLayout.addStyleName(CssStyles.LAYOUT_MINIMAL);
				exportLayout.setWidth(250, Unit.PIXELS);
				exportPopupButton.setContent(exportLayout);
				addHeaderComponent(exportPopupButton);
			}

			{
				StreamResource streamResource = new GridExportStreamResource(grid, "sormas_cases", "sormas_cases_" + DateHelper.formatDateForExport(new Date()) + ".csv");

				addExportButton(streamResource, exportPopupButton, exportLayout, "basicExport", VaadinIcons.TABLE, Captions.exportBasic, Strings.infoBasicExport);
			}

			{
				StreamResource exportStreamResource = DownloadUtil.createCsvExportStreamResource(CaseExportDto.class, CaseExportType.CASE_SURVEILLANCE,
						(Integer start, Integer max) -> FacadeProvider.getCaseFacade().getExportList(grid.getCriteria(), CaseExportType.CASE_SURVEILLANCE, start, max, null),
						(propertyId, type) -> {
							String caption = findPrefixCaption(propertyId,
									CaseExportDto.I18N_PREFIX,
									CaseDataDto.I18N_PREFIX,
									PersonDto.I18N_PREFIX,
									LocationDto.I18N_PREFIX,
									SymptomsDto.I18N_PREFIX,
									EpiDataDto.I18N_PREFIX,
									HospitalizationDto.I18N_PREFIX);
							if (Date.class.isAssignableFrom(type)) {
								caption += " (" + DateHelper.getLocalShortDatePattern() + ")";
							}
							return caption;
						},
						"sormas_cases_" + DateHelper.formatDateForExport(new Date()) + ".csv", null);

				addExportButton(exportStreamResource, exportPopupButton, exportLayout, "extendedExport", VaadinIcons.FILE_TEXT, Captions.exportDetailed, Strings.infoDetailedExport);
			}

			if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_MANAGEMENT_ACCESS)) {
				StreamResource caseManagementExportStreamResource = DownloadUtil.createCaseManagementExportResource(grid.getCriteria(),
						"sormas_case_management_" + DateHelper.formatDateForExport(new Date()) + ".zip");

				addExportButton(caseManagementExportStreamResource, exportPopupButton, exportLayout, "caseManagementExport", VaadinIcons.FILE_TEXT, Captions.exportCaseManagement, Strings.infoCaseManagementExport);
			}

			{
				StreamResource sampleExportStreamResource = DownloadUtil.createCsvExportStreamResource(SampleExportDto.class, null,
						(Integer start, Integer max) -> FacadeProvider.getSampleFacade().getExportList(grid.getCriteria(), start, max),
						(propertyId, type) -> {
							String caption = findPrefixCaption(propertyId,
									SampleExportDto.I18N_PREFIX,
									SampleDto.I18N_PREFIX,
									CaseDataDto.I18N_PREFIX,
									PersonDto.I18N_PREFIX,
									AdditionalTestDto.I18N_PREFIX);
							if (Date.class.isAssignableFrom(type)) {
								caption += " (" + DateHelper.getLocalShortDatePattern() + ")";
							}
							return caption;
						},
						"sormas_samples_" + DateHelper.formatDateForExport(new Date()) + ".csv", null);

				addExportButton(sampleExportStreamResource, exportPopupButton, exportLayout, "sampleExport", VaadinIcons.FILE_TEXT, Captions.exportSamples, Strings.infoSampleExport);
			}

			{
				Button btnCustomCaseExport = new Button(I18nProperties.getCaption(Captions.exportCaseCustom));
				btnCustomCaseExport.setId("customCaseExport");
				btnCustomCaseExport.setDescription(I18nProperties.getString(Strings.infoCustomCaseExport));
				btnCustomCaseExport.addStyleName(ValoTheme.BUTTON_PRIMARY);
				btnCustomCaseExport.setIcon(VaadinIcons.FILE_TEXT);
				btnCustomCaseExport.setWidth(100, Unit.PERCENTAGE);
				exportLayout.addComponent(btnCustomCaseExport);
				btnCustomCaseExport.addClickListener(e -> {
					Window customExportWindow = VaadinUiUtil.createPopupWindow();
					CaseExportConfigurationsLayout customExportsLayout = new CaseExportConfigurationsLayout(
							customExportWindow::close);
					customExportsLayout.setExportCallback(
							(exportConfig) -> {
								Page.getCurrent().open(DownloadUtil.createCsvExportStreamResource(CaseExportDto.class, null,
										(Integer start, Integer max) -> FacadeProvider.getCaseFacade().getExportList(grid.getCriteria(), null, start, max, exportConfig),
										(propertyId, type) -> {
											String caption = findPrefixCaption(propertyId,
													CaseExportDto.I18N_PREFIX,
													CaseDataDto.I18N_PREFIX,
													PersonDto.I18N_PREFIX,
													SymptomsDto.I18N_PREFIX,
													EpiDataDto.I18N_PREFIX,
													HospitalizationDto.I18N_PREFIX);
											if (Date.class.isAssignableFrom(type)) {
												caption += " (" + DateHelper.getLocalShortDatePattern() + ")";
											}
											return caption;
										},
										"sormas_cases_" + DateHelper.formatDateForExport(new Date()) + ".csv", exportConfig), null, true);
							});
					customExportWindow.setWidth(1024, Unit.PIXELS);
					customExportWindow.setCaption(I18nProperties.getCaption(Captions.exportCaseCustom));
					customExportWindow.setContent(customExportsLayout);
					UI.getCurrent().addWindow(customExportWindow);
				});
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
			btnEnterBulkEditMode = new Button(I18nProperties.getCaption(Captions.actionEnterBulkEditMode));
			btnEnterBulkEditMode.setId("enterBulkEditMode");
			btnEnterBulkEditMode.setIcon(VaadinIcons.CHECK_SQUARE_O);
			btnEnterBulkEditMode.setVisible(!viewConfiguration.isInEagerMode());
			if (moreLayout != null) {
				btnEnterBulkEditMode.setStyleName(ValoTheme.BUTTON_PRIMARY);
				btnEnterBulkEditMode.setWidth(100, Unit.PERCENTAGE);
				moreLayout.addComponent(btnEnterBulkEditMode);
			} else {
				addHeaderComponent(btnEnterBulkEditMode);
			}

			btnLeaveBulkEditMode = new Button(I18nProperties.getCaption(Captions.actionLeaveBulkEditMode));
			btnLeaveBulkEditMode.setId("leaveBulkEditMode");
			btnLeaveBulkEditMode.setIcon(VaadinIcons.CLOSE);
			btnLeaveBulkEditMode.setVisible(viewConfiguration.isInEagerMode());
			btnLeaveBulkEditMode.setStyleName(ValoTheme.BUTTON_PRIMARY);
			addHeaderComponent(btnLeaveBulkEditMode);

			btnEnterBulkEditMode.addClickListener(e -> {
				if (grid.getItemCount() > BULK_EDIT_MODE_WARNING_THRESHOLD) {
					VaadinUiUtil.showConfirmationPopup(I18nProperties.getCaption(Captions.actionEnterBulkEditMode), new Label(String.format(I18nProperties.getString(Strings.confirmationEnterBulkEditMode), BULK_EDIT_MODE_WARNING_THRESHOLD)),
							I18nProperties.getString(Strings.yes), I18nProperties.getString(Strings.no), 640, (result) -> {
								if (result.booleanValue() == true) {
									enterBulkEditMode();
								}
							});
				} else {
					enterBulkEditMode();
				}
			});
			btnLeaveBulkEditMode.addClickListener(e -> {
				bulkOperationsDropdown.setVisible(false);
				viewConfiguration.setInEagerMode(false);
				btnLeaveBulkEditMode.setVisible(false);
				btnEnterBulkEditMode.setVisible(true);
				this.filterForm.enableSearchAndReportingUser();
				navigateTo(criteria);
			});
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_MERGE)) {
			Button mergeDuplicatesButton = new Button(I18nProperties.getCaption(Captions.caseMergeDuplicates));
			mergeDuplicatesButton.setId("mergeDuplicates");
			mergeDuplicatesButton.setIcon(VaadinIcons.COMPRESS_SQUARE);
			mergeDuplicatesButton.addClickListener(e -> ControllerProvider.getCaseController().navigateToMergeCasesView());
			if (moreLayout != null) {
				mergeDuplicatesButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
				mergeDuplicatesButton.setWidth(100, Unit.PERCENTAGE);
				moreLayout.addComponent(mergeDuplicatesButton);
			} else {
				addHeaderComponent(mergeDuplicatesButton);
			}
		}

		Button searchSpecificCaseButton = new Button(I18nProperties.getCaption(Captions.caseSearchSpecificCase));
		searchSpecificCaseButton.setId("searchSpecificCase");
		searchSpecificCaseButton.setIcon(VaadinIcons.SEARCH);
		searchSpecificCaseButton.addClickListener(e -> buildAndOpenSearchSpecificCaseWindow());
		if (moreLayout != null) {
			searchSpecificCaseButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			searchSpecificCaseButton.setWidth(100, Unit.PERCENTAGE);
			moreLayout.addComponent(searchSpecificCaseButton);
		} else {
			addHeaderComponent(searchSpecificCaseButton);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_CREATE)) {
			lineListingButton = new Button(I18nProperties.getCaption(Captions.caseLineListing));
			lineListingButton.setId("lineListing");
			lineListingButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			lineListingButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			lineListingButton.addClickListener(e -> ControllerProvider.getCaseController().lineListing());
			addHeaderComponent(lineListingButton);

			createButton = new Button(I18nProperties.getCaption(Captions.caseNewCase));
			createButton.setId("create");
			createButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			createButton.setIcon(VaadinIcons.PLUS_CIRCLE);
			createButton.addClickListener(e -> ControllerProvider.getCaseController().create());
			addHeaderComponent(createButton);
		}

		if (moreButton != null) {
			addHeaderComponent(moreButton);
		}
		addComponent(gridLayout);
	}

	/**
	 * Iterates through the prefixes to determines the caption for the specified propertyId.
	 *
	 * @return
	 */
	private static String findPrefixCaption(String propertyId, String... prefixes) {
		return Arrays.stream(prefixes)
				.map(p -> I18nProperties.getPrefixCaption(p, propertyId, null))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(propertyId);
	}

	private void addImportButton(VerticalLayout importLayout, String buttonId, String captionKey,
								 String windowHeadingKey, Supplier<Component> windowContentSupplier) {
		Button lineListingImportButton = new Button(I18nProperties.getCaption(captionKey));
		lineListingImportButton.setId(buttonId);
		lineListingImportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		lineListingImportButton.setIcon(VaadinIcons.UPLOAD);
		lineListingImportButton.setWidth(100, Unit.PERCENTAGE);
		lineListingImportButton.addClickListener(e -> {
			Window popupWindow = VaadinUiUtil.showPopupWindow(windowContentSupplier.get());
			popupWindow.setCaption(I18nProperties.getString(windowHeadingKey));
			popupWindow.addCloseListener(c -> grid.reload());
		});
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
		this.filterForm.disableSearchAndReportingUser();
		grid.setEagerDataProvider();
		grid.reload();
	}

	public VerticalLayout createFilterBar() {
		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		this.filterForm = new CaseFilterForm();
		this.filterForm.addValueChangeListener(e -> {
			if (!navigateTo(criteria)) {
				grid.reload();
			}
		});
		this.filterForm.addResetHandler(e -> {
			ViewModelProviders.of(CasesView.class).remove(CaseCriteria.class);
			navigateTo(null);
		});
		filterLayout.addComponent(this.filterForm);

		return filterLayout;
	}

	public HorizontalLayout createStatusFilterBar() {
		HorizontalLayout statusFilterLayout = new HorizontalLayout();
		statusFilterLayout.setSpacing(true);
		statusFilterLayout.setMargin(false);
		statusFilterLayout.setWidth(100, Unit.PERCENTAGE);
		statusFilterLayout.addStyleName(CssStyles.VSPACE_3);

		statusButtons = new HashMap<>();

		Button statusAll = new Button(I18nProperties.getCaption(Captions.all), e -> {
			criteria.investigationStatus(null);
			navigateTo(criteria);
		});
		statusAll.setId("statusAll");
		CssStyles.style(statusAll, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER);
		statusAll.setCaptionAsHtml(true);
		statusFilterLayout.addComponent(statusAll);
		statusButtons.put(statusAll, I18nProperties.getCaption(Captions.all));
		activeStatusButton = statusAll;

		for (InvestigationStatus status : InvestigationStatus.values()) {
			Button statusButton = new Button(status.toString(), e -> {
				criteria.investigationStatus(status);
				navigateTo(criteria);
			});
			statusButton.setId("status-button-" + status.name());
			statusButton.setData(status);
			CssStyles.style(statusButton, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT);
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
							VaadinIcons.INFO_CIRCLE.getHtml() + " " + String.format(
									I18nProperties.getString(Strings.infoArchivedCases), daysAfterCaseGetsArchived),
							ContentMode.HTML);
					relevanceStatusInfoLabel.setVisible(false);
					relevanceStatusInfoLabel.addStyleName(CssStyles.LABEL_VERTICAL_ALIGN_SUPER);
					actionButtonsLayout.addComponent(relevanceStatusInfoLabel);
					actionButtonsLayout.setComponentAlignment(relevanceStatusInfoLabel, Alignment.MIDDLE_RIGHT);
				}
				relevanceStatusFilter = new ComboBox();
				relevanceStatusFilter.setId("relevanceStatus");
				relevanceStatusFilter.setWidth(140, Unit.PERCENTAGE);
				relevanceStatusFilter.setNullSelectionAllowed(false);
				relevanceStatusFilter.addItems((Object[]) EntityRelevanceStatus.values());
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ACTIVE, I18nProperties.getCaption(Captions.caseActiveCases));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ARCHIVED, I18nProperties.getCaption(Captions.caseArchivedCases));
				relevanceStatusFilter.setItemCaption(EntityRelevanceStatus.ALL, I18nProperties.getCaption(Captions.caseAllCases));
				relevanceStatusFilter.addValueChangeListener(e -> {
					relevanceStatusInfoLabel
							.setVisible(EntityRelevanceStatus.ARCHIVED.equals(e.getProperty().getValue()));
					criteria.relevanceStatus((EntityRelevanceStatus) e.getProperty().getValue());
					navigateTo(criteria);
				});
				actionButtonsLayout.addComponent(relevanceStatusFilter);
			}

			// Bulk operation dropdown
			if (UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
				bulkOperationsDropdown = new MenuBar();
				MenuItem bulkOperationsItem = bulkOperationsDropdown.addItem(I18nProperties.getCaption(Captions.bulkActions), null);

				Command changeCommand = mi -> ControllerProvider.getCaseController().showBulkCaseDataEditComponent(grid.asMultiSelect().getSelectedItems());
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkEdit), VaadinIcons.ELLIPSIS_H, changeCommand);

				Command deleteCommand = selectedItem -> ControllerProvider.getCaseController().deleteAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.bulkDelete), VaadinIcons.TRASH, deleteCommand);

				Command archiveCommand = mi -> ControllerProvider.getCaseController().archiveAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
				bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.actionArchive), VaadinIcons.ARCHIVE, archiveCommand);

				Command dearchiveCommand = mi -> ControllerProvider.getCaseController().dearchiveAllSelectedItems(grid.asMultiSelect().getSelectedItems(), () -> navigateTo(criteria));
				dearchiveItem = bulkOperationsItem.addItem(I18nProperties.getCaption(Captions.actionDearchive), VaadinIcons.ARCHIVE, dearchiveCommand);
				dearchiveItem.setVisible(false);

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
		grid.reload();
	}

	public void updateFilterComponents() {
		filterForm.setValue(criteria);

		// TODO replace with Vaadin 8 databinding
		applyingCriteria = true;

		updateStatusButtons();
		if (relevanceStatusFilter != null) {
			relevanceStatusFilter.setValue(criteria.getRelevanceStatus());
		}

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
			activeStatusButton.setCaption(statusButtons.get(activeStatusButton)
					+ LayoutUtil.spanCss(CssStyles.BADGE, String.valueOf(grid.getItemCount())));
		}
	}

}

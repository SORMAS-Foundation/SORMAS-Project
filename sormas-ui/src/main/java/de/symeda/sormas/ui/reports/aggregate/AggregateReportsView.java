package de.symeda.sormas.ui.reports.aggregate;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportGroupingLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;

@SuppressWarnings("serial")
public class AggregateReportsView extends AbstractView {

	public static final String VIEW_NAME = "aggregatereports";

	private AggregateReportCriteria criteria;

	private AggregateReportsGrid grid;
	private VerticalLayout gridLayout;
	private Button btnExport;
	private Button btnCreate;
	private Button btnEdit;

	// Filters
	private HorizontalLayout hlSecondFilterRow;
	private ComboBox<Integer> cbFromYearFilter;
	private ComboBox<EpiWeek> cbFromEpiWeekFilter;
	private ComboBox<Integer> cbToYearFilter;
	private ComboBox<EpiWeek> cbToEpiWeekFilter;
	private CheckBox showZeroRowsGrouping;

	private AggregateReportsFilterForm aggregateReportsFilterForm;

	public AggregateReportsView() {
		super(VIEW_NAME);

		UserDto user = UserProvider.getCurrent().getUser();

		boolean criteriaUninitialized = !ViewModelProviders.of(AggregateReportsView.class).has(AggregateReportCriteria.class);
		criteria = ViewModelProviders.of(AggregateReportsView.class).get(AggregateReportCriteria.class);
		if (criteriaUninitialized) {
			criteria.epiWeekFrom(DateHelper.getEpiWeek(new Date())).epiWeekTo(DateHelper.getEpiWeek(new Date()));
		}

		criteria.setAggregateReportGroupingLevel(AggregateReportGroupingLevel.REGION);
		grid = new AggregateReportsGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createGroupingBar());
		gridLayout.addComponent(createFilterBar(user));
		gridLayout.addComponent(createEpiWeekFilterBar());
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UserProvider.getCurrent().hasUserRight(UserRight.AGGREGATE_REPORT_EDIT)) {
			btnCreate = ButtonHelper.createIconButton(
				Captions.aggregateReportNewAggregateReport,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getAggregateReportController().openEditOrCreateWindow(() -> grid.reload(), false),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(btnCreate);

			btnEdit = ButtonHelper.createIconButton(
				Captions.aggregateReportEditAggregateReport,
				VaadinIcons.EDIT,
				e -> ControllerProvider.getAggregateReportController().openEditOrCreateWindow(() -> grid.reload(), true),
				ValoTheme.BUTTON_PRIMARY);
			btnEdit.setVisible(false);

			addHeaderComponent(btnEdit);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.AGGREGATE_REPORT_EXPORT)) {
			btnExport = ButtonHelper.createIconButton(Captions.export, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(btnExport);

			StreamResource streamResource = GridExportStreamResource.createStreamResource(grid, ExportEntityName.AGGREGATE_REPORTS);
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(btnExport);
		}
	}

	private HorizontalLayout createGroupingBar() {
		HorizontalLayout jurisdictionLayout = new HorizontalLayout();
		AggregateReportGroupingSelector aggregateReportGroupingSelector = new AggregateReportGroupingSelector();
		aggregateReportGroupingSelector.addValueChangeListener(e -> {
			AggregateReportGroupingLevel groupingValue = (AggregateReportGroupingLevel) e.getValue();
			criteria.setAggregateReportGroupingLevel(groupingValue);
			grid.setColumnsVisibility(groupingValue);
			grid.reload();
		});
		jurisdictionLayout.addComponent(aggregateReportGroupingSelector);

		showZeroRowsGrouping = new CheckBox();
		showZeroRowsGrouping.setId(AggregateReportCriteria.SHOW_ZERO_ROWS_FOR_GROUPING);
		showZeroRowsGrouping.setCaption(I18nProperties.getCaption(Captions.aggregateReportShowZeroRowsForGrouping));
		showZeroRowsGrouping.addStyleName(CssStyles.FORCE_CAPTION_CHECKBOX);
		showZeroRowsGrouping.setValue(false);

		showZeroRowsGrouping.addValueChangeListener(e -> {
			criteria.setShowZeroRowsForGrouping(e.getValue());
		});

		jurisdictionLayout.addComponent(showZeroRowsGrouping);

		return jurisdictionLayout;
	}

	private VerticalLayout createFilterBar(UserDto user) {

		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		aggregateReportsFilterForm = new AggregateReportsFilterForm();
		aggregateReportsFilterForm.setValue(criteria);

		aggregateReportsFilterForm.addValueChangeListener(e -> {
			if (!aggregateReportsFilterForm.hasFilter()) {
				navigateTo(null);
			}
		});

		aggregateReportsFilterForm.addResetHandler(e -> {
			ViewModelProviders.of(AggregateReportsView.class).remove(AggregateReportCriteria.class);
			navigateTo(null, true);
		});

		aggregateReportsFilterForm.addApplyHandler(e -> {
			grid.reload();
		});

		filterLayout.addComponent(aggregateReportsFilterForm);

		return filterLayout;
	}

	private HorizontalLayout createEpiWeekFilterBar() {
		hlSecondFilterRow = new HorizontalLayout();
		hlSecondFilterRow.setMargin(false);
		hlSecondFilterRow.setSpacing(true);
		hlSecondFilterRow.setWidthUndefined();
		{
			Label lblFrom = new Label(I18nProperties.getCaption(Captions.from));
			CssStyles.style(lblFrom, CssStyles.LABEL_BOLD, CssStyles.VSPACE_TOP_4);
			hlSecondFilterRow.addComponent(lblFrom);

			cbFromYearFilter = new ComboBox<>();
			cbFromYearFilter.setId("yearFrom");
			cbFromYearFilter.addValueChangeListener(e -> clearFilterIfEmpty(cbFromYearFilter, cbFromEpiWeekFilter));
			cbFromEpiWeekFilter = new ComboBox<>();
			cbFromEpiWeekFilter.setId(AggregateReportCriteria.EPI_WEEK_FROM);
			cbFromEpiWeekFilter.addValueChangeListener(e -> {
				criteria.setEpiWeekFrom(e.getValue());
			});
			cbToYearFilter = new ComboBox<>();
			cbToYearFilter.setId("yearTo");
			cbToYearFilter.addValueChangeListener(e -> clearFilterIfEmpty(cbFromYearFilter, cbToEpiWeekFilter));
			cbToEpiWeekFilter = new ComboBox<>();
			cbToEpiWeekFilter.setId(AggregateReportCriteria.EPI_WEEK_TO);
			cbToEpiWeekFilter.addValueChangeListener(e -> {
				criteria.setEpiWeekTo(e.getValue());
			});

			cbFromYearFilter.setWidth(140, Unit.PIXELS);
			cbFromYearFilter.setPlaceholder(I18nProperties.getString(Strings.year));
			cbFromYearFilter.setItems(DateHelper.getYearsToNow(2000));
			cbFromYearFilter.addValueChangeListener(e -> {
				cbFromEpiWeekFilter.clear();
				if (e.getValue() != null) {
					cbFromEpiWeekFilter.setItems(DateHelper.createEpiWeekList(e.getValue()));
				}
			});
			if (criteria.getEpiWeekFrom() != null) {
				cbFromYearFilter.setValue(criteria.getEpiWeekFrom().getYear());
			}
			hlSecondFilterRow.addComponent(cbFromYearFilter);

			cbFromEpiWeekFilter.setWidth(200, Unit.PIXELS);
			cbFromEpiWeekFilter.setPlaceholder(I18nProperties.getString(Strings.epiWeek));
			criteria.setEpiWeekFrom(cbFromEpiWeekFilter.getValue());

			hlSecondFilterRow.addComponent(cbFromEpiWeekFilter);

			Label lblTo = new Label(I18nProperties.getCaption(Captions.to));
			CssStyles.style(lblTo, CssStyles.LABEL_BOLD, CssStyles.VSPACE_TOP_4);
			hlSecondFilterRow.addComponent(lblTo);

			cbToYearFilter.setWidth(140, Unit.PIXELS);
			cbToYearFilter.setPlaceholder(I18nProperties.getString(Strings.year));
			cbToYearFilter.setItems(DateHelper.getYearsToNow(2000));
			cbToYearFilter.addValueChangeListener(e -> {
				cbToEpiWeekFilter.clear();
				if (e.getValue() != null) {
					cbToEpiWeekFilter.setItems(DateHelper.createEpiWeekList(e.getValue()));
				}
			});
			if (criteria.getEpiWeekTo() != null) {
				cbToYearFilter.setValue(criteria.getEpiWeekTo().getYear());
			}
			hlSecondFilterRow.addComponent(cbToYearFilter);

			cbToEpiWeekFilter.setWidth(200, Unit.PIXELS);
			cbToEpiWeekFilter.setPlaceholder(I18nProperties.getString(Strings.epiWeek));
			criteria.setEpiWeekTo(cbToEpiWeekFilter.getValue());
			hlSecondFilterRow.addComponent(cbToEpiWeekFilter);
		}
		return hlSecondFilterRow;
	}

	private void clearFilterIfEmpty(ComboBox<?> filter1, ComboBox<?> filter2) {
		if (filter1.getValue() == null) {
			filter2.clear();
		}
	}

	private void clearFilterIfNotEmpty(ComboBox<?> filter1, ComboBox<?> filter2) {
		if (filter1.getValue() != null) {
			filter2.clear();
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		EpiWeek epiWeekFrom = criteria.getEpiWeekFrom();
		EpiWeek epiWeekTo = criteria.getEpiWeekTo();

		String params = event.getParameters().trim();
		if (params.startsWith("?")) {
			params = params.substring(1);
			criteria.fromUrlParams(params);
		}

		if (criteria.getEpiWeekFrom() == null) {
			criteria.setEpiWeekFrom(epiWeekFrom);
		}
		if (criteria.getEpiWeekTo() == null) {
			criteria.setEpiWeekTo(epiWeekTo);
		}

		grid.reload();
	}

	AggregateReportCriteria getCriteria() {
		return criteria;
	}
}

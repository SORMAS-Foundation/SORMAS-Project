package de.symeda.sormas.ui.reports.aggregate;

import java.util.Date;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
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
import de.symeda.sormas.ui.utils.NotificationHelper;

@SuppressWarnings("serial")
public class AggregateReportsView extends AbstractView {

	public static final String VIEW_NAME = "aggregatereports";

	private AggregateReportCriteria criteria;

	private AggregateReportsGrid grid;
	private VerticalLayout gridLayout;
	private Button btnExport;
	private Button btnCreate;
	private Button btnEdit;

	private CheckBox showZeroRowsGrouping;

	private AggregateReportsFilterForm aggregateReportsFilterForm;

	public AggregateReportsView() {
		super(VIEW_NAME);

		UserDto user = UserProvider.getCurrent().getUser();

		boolean criteriaUninitialized = !ViewModelProviders.of(AggregateReportsView.class).has(AggregateReportCriteria.class);
		criteria = ViewModelProviders.of(AggregateReportsView.class).get(AggregateReportCriteria.class);
		if (criteriaUninitialized || criteria.getEpiWeekFrom() == null || criteria.getEpiWeekTo() == null) {
			criteria.epiWeekFrom(DateHelper.getEpiWeek(new Date())).epiWeekTo(DateHelper.getEpiWeek(new Date()));
		}

		criteria.setAggregateReportGroupingLevel(AggregateReportGroupingLevel.REGION);
		grid = new AggregateReportsGrid(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createGroupingBar());
		gridLayout.addComponent(createFilterBar(user));
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

		criteria.setRegion(user.getRegion());
		criteria.setDistrict(user.getDistrict());
		criteria.setHealthFacility(user.getHealthFacility());
		criteria.setPointOfEntry(user.getPointOfEntry());

		aggregateReportsFilterForm = new AggregateReportsFilterForm();
		aggregateReportsFilterForm.setValue(criteria);

		aggregateReportsFilterForm.addValueChangeListener(e -> {
			if (!aggregateReportsFilterForm.hasFilter()) {
				navigateTo(null);
			}
		});

		aggregateReportsFilterForm.addResetHandler(e -> {
			ViewModelProviders.of(AggregateReportsView.class).remove(AggregateReportCriteria.class);
			criteria.epiWeekFrom(DateHelper.getEpiWeek(new Date())).epiWeekTo(DateHelper.getEpiWeek(new Date()));
			criteria.setDisease(null);
			navigateTo(criteria, true);
		});

		aggregateReportsFilterForm.addApplyHandler(e -> {
			if (epiWeekFilterBarDataValidation()) {
				grid.reload();
			} else {
				NotificationHelper.showNotification(
					I18nProperties.getString(Strings.messageAggregatedReportEpiWeekFilterNotFilled),
					Notification.Type.HUMANIZED_MESSAGE,
					10000);
			}
		});

		filterLayout.addComponent(aggregateReportsFilterForm);

		return filterLayout;
	}

	private boolean epiWeekFilterBarDataValidation() {
		return criteria.getEpiWeekTo() != null && criteria.getEpiWeekFrom() != null;
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
			criteria.setEpiWeekFrom(epiWeekFrom != null ? epiWeekFrom : DateHelper.getEpiWeek(new Date()));
		}
		if (criteria.getEpiWeekTo() == null) {
			criteria.setEpiWeekTo(epiWeekTo != null ? epiWeekTo : DateHelper.getEpiWeek(new Date()));
		}

		grid.reload();
	}

	AggregateReportCriteria getCriteria() {
		return criteria;
	}
}

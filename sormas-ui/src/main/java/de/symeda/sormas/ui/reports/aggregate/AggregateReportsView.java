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
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.ExportEntityName;
import de.symeda.sormas.ui.utils.GridExportStreamResource;
import de.symeda.sormas.ui.utils.NotificationHelper;

@SuppressWarnings("serial")
public class AggregateReportsView extends AbstractAggregateReportsView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/aggregatereporting";

	private AggregateReportsGrid grid;
	private VerticalLayout gridLayout;
	private Button btnExport;
	private Button btnCreate;
	private Button btnEdit;

	private CheckBox showZeroRows;

	private AggregateReportsFilterForm aggregateReportsFilterForm;

	public AggregateReportsView() {
		super(VIEW_NAME);

		UserDto user = UiUtil.getUser();

		boolean criteriaUninitialized = !ViewModelProviders.of(AggregateReportsView.class).has(AggregateReportCriteria.class);
		criteria = ViewModelProviders.of(AggregateReportsView.class).get(AggregateReportCriteria.class);
		if (criteriaUninitialized || criteria.getEpiWeekFrom() == null || criteria.getEpiWeekTo() == null) {
			criteria.epiWeekFrom(DateHelper.getEpiWeek(new Date())).epiWeekTo(DateHelper.getEpiWeek(new Date()));
		}

		criteria.setAggregateReportGroupingLevel(
			criteria.getAggregateReportGroupingLevel() != null ? criteria.getAggregateReportGroupingLevel() : AggregateReportGroupingLevel.REGION);

		gridLayout = new VerticalLayout();

		gridLayout.addComponent(createGroupingBar());

		gridLayout.addComponent(createFilterBar(user));

		grid = new AggregateReportsGrid(criteria);
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UiUtil.permitted(UserRight.AGGREGATE_REPORT_EDIT)) {
			btnCreate = ButtonHelper.createIconButton(
				Captions.aggregateReportNewAggregateReport,
				VaadinIcons.PLUS_CIRCLE,
				e -> ControllerProvider.getAggregateReportController().openEditOrCreateWindow(() -> navigateTo(criteria), false, null),
				ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(btnCreate);

			btnEdit = ButtonHelper.createIconButton(
				Captions.aggregateReportEditAggregateReport,
				VaadinIcons.EDIT,
				e -> ControllerProvider.getAggregateReportController().openEditOrCreateWindow(() -> navigateTo(criteria), true, null),
				ValoTheme.BUTTON_PRIMARY);
			btnEdit.setVisible(false);

			addHeaderComponent(btnEdit);
		}

		if (UiUtil.permitted(UserRight.AGGREGATE_REPORT_EXPORT)) {
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
		aggregateReportGroupingSelector.setValue(criteria.getAggregateReportGroupingLevel());

		aggregateReportGroupingSelector.addValueChangeListener(e -> {
			AggregateReportGroupingLevel groupingValue = (AggregateReportGroupingLevel) e.getValue();
			criteria.setAggregateReportGroupingLevel(groupingValue);
			grid.setColumnsVisibility(groupingValue);
			grid.reload();
		});
		jurisdictionLayout.addComponent(aggregateReportGroupingSelector);

		showZeroRows = new CheckBox();
		showZeroRows.setId(AggregateReportCriteria.SHOW_ZERO_ROWS);
		showZeroRows.setCaption(I18nProperties.getCaption(Captions.aggregateReportShowZeroRows));
		showZeroRows.addStyleName(CssStyles.FORCE_CAPTION_CHECKBOX);

		showZeroRows.setValue(criteria.getShowZeroRows());

		showZeroRows.addValueChangeListener(e -> {
			criteria.setShowZeroRows(e.getValue());
			grid.reload();
		});

		jurisdictionLayout.addComponent(showZeroRows);

		return jurisdictionLayout;
	}

	private VerticalLayout createFilterBar(UserDto user) {

		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		if (criteria.getRegion() == null) {
			criteria.setRegion(user.getRegion());
		}

		if (criteria.getDistrict() == null) {
			criteria.setDistrict(user.getDistrict());
		}

		if (criteria.getHealthFacility() == null) {
			criteria.setHealthFacility(user.getHealthFacility());
		}

		if (criteria.getPointOfEntry() == null) {
			criteria.setPointOfEntry(user.getPointOfEntry());
		}

		aggregateReportsFilterForm = new AggregateReportsFilterForm();
		aggregateReportsFilterForm.setValue(criteria);

		aggregateReportsFilterForm.addValueChangeListener(e -> {
			if (!aggregateReportsFilterForm.hasFilter()) {
				navigateTo(null);
			}
		});

		aggregateReportsFilterForm.addResetHandler(e -> {
			ViewModelProviders.of(AggregateReportsView.class).remove(AggregateReportCriteria.class);
			AggregateReportCriteria emptyCriteria = new AggregateReportCriteria();
			navigateTo(emptyCriteria, true);
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
		super.enter(event);
		EpiWeek epiWeekFrom = criteria.getEpiWeekFrom();
		EpiWeek epiWeekTo = criteria.getEpiWeekTo();

		if (criteria.getEpiWeekFrom() == null) {
			criteria.setEpiWeekFrom(epiWeekFrom != null ? epiWeekFrom : DateHelper.getEpiWeek(new Date()));
		}
		if (criteria.getEpiWeekTo() == null) {
			criteria.setEpiWeekTo(epiWeekTo != null ? epiWeekTo : DateHelper.getEpiWeek(new Date()));
		}

		grid.reload();
	}

}

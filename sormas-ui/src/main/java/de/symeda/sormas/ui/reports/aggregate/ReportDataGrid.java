package de.symeda.sormas.ui.reports.aggregate;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.utils.AgeGroupUtils;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;

public class ReportDataGrid extends FilteredGrid<AggregateReportDto, AggregateReportCriteria> {

	public static final String EDIT_AGGREGATE_REPORT = "showAggregateReport";
	public static final String DELETE_AGGREGATE_REPORT = "deleteAggregateReport";

	public ReportDataGrid(AggregateReportCriteria criteria) {
		super(AggregateReportDto.class);
		setSizeFull();
		setSelectionMode(SelectionMode.NONE);
		setInEagerMode(true);
		setCriteria(criteria);

		addEditColumn();

		addDeleteColumn();

		addDefaultColumns();

		setStyleGenerator(aggregateReportDto -> {
			if (aggregateReportDto.isDuplicate()) {
				return CssStyles.BACKGROUND_DUPLICATE_AGGREGATE_REPORT;
			}
			return "";
		});

		reload();
	}

	protected void addDefaultColumns() {
		setColumns(
			EDIT_AGGREGATE_REPORT,
			AggregateReportDto.REPORTING_USER,
			AggregateReportDto.DISEASE,
			AggregateReportDto.REGION,
			AggregateReportDto.DISTRICT,
			AggregateReportDto.HEALTH_FACILITY,
			AggregateReportDto.POINT_OF_ENTRY,
			AggregateReportDto.YEAR,
			AggregateReportDto.EPI_WEEK,
			AggregateReportDto.AGE_GROUP,
			AggregateReportDto.NEW_CASES,
			AggregateReportDto.LAB_CONFIRMATIONS,
			AggregateReportDto.DEATHS,
			DELETE_AGGREGATE_REPORT);
	}

	protected void addEditColumn() {

		addComponentColumn(this::createEditButton).setId(EDIT_AGGREGATE_REPORT).setSortable(false);

	}

	private Button createEditButton(AggregateReportDto aggregateReport) {
		if (!aggregateReport.isDuplicate()) {
			Button editButton = ButtonHelper.createIconButton(VaadinIcons.EDIT);
			editButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			editButton.addClickListener(clickEvent -> {
				ControllerProvider.getAggregateReportController().openEditOrCreateWindow(this::reload, true, aggregateReport);
				reload();
			});
			return editButton;
		}
		return null;

	}

	protected void addDeleteColumn() {
		addComponentColumn(this::createDeleteButton).setId(DELETE_AGGREGATE_REPORT).setSortable(false);
	}

	private Button createDeleteButton(AggregateReportDto aggregateReport) {

		if (aggregateReport.isDuplicate()) {
			Button deleteButton = ButtonHelper.createIconButton(VaadinIcons.TRASH);
			deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			deleteButton.addClickListener(clickEvent -> {
				ControllerProvider.getAggregateReportController().deleteAggregateReport(aggregateReport.getUuid(), this::reload);
			});
			return deleteButton;
		}
		return null;

	}

	public void reload() {
		ListDataProvider<AggregateReportDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getAggregateReportFacade().getAggregateReports(getCriteria()).stream().map(aggregatedReportDto -> {
				if (aggregatedReportDto.getAgeGroup() != null) {
					aggregatedReportDto.setAgeGroup(AgeGroupUtils.createCaption(aggregatedReportDto.getAgeGroup()));
				}
				return aggregatedReportDto;
			}));
		setDataProvider(dataProvider);
		dataProvider.refreshAll();
	}
}

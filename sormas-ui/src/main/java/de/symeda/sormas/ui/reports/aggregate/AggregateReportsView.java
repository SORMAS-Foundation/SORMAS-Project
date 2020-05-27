package de.symeda.sormas.ui.reports.aggregate;

import java.util.Date;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
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
	private HorizontalLayout hlFirstFilterRow;
	private HorizontalLayout hlSecondFilterRow;
	private ComboBox<RegionReferenceDto> cbRegionFilter;
	private ComboBox<DistrictReferenceDto> cbDistrictFilter;
	private ComboBox<FacilityReferenceDto> cbFacilityFilter;
	private ComboBox<PointOfEntryReferenceDto> cbPoeFilter;
	private ComboBox<Integer> cbFromYearFilter;
	private ComboBox<EpiWeek> cbFromEpiWeekFilter;
	private ComboBox<Integer> cbToYearFilter;
	private ComboBox<EpiWeek> cbToEpiWeekFilter;

	private Binder<AggregateReportCriteria> binder = new Binder<>(AggregateReportCriteria.class);

	public AggregateReportsView() {
		super(VIEW_NAME);

		UserDto user = UserProvider.getCurrent().getUser();

		boolean criteriaUninitialized = !ViewModelProviders.of(AggregateReportsView.class).has(AggregateReportCriteria.class);
		criteria = ViewModelProviders.of(AggregateReportsView.class).get(AggregateReportCriteria.class);
		if (criteriaUninitialized) {
			criteria.epiWeekFrom(DateHelper.getEpiWeek(new Date())).epiWeekTo(DateHelper.getEpiWeek(new Date()));
		}

		grid = new AggregateReportsGrid();
		grid.setCriteria(criteria);
		gridLayout = new VerticalLayout();
		gridLayout.addComponent(createFilterBar(user));
		gridLayout.addComponent(grid);
		gridLayout.setMargin(true);
		gridLayout.setSpacing(false);
		gridLayout.setSizeFull();
		gridLayout.setExpandRatio(grid, 1);
		gridLayout.setStyleName("crud-main-layout");

		addComponent(gridLayout);

		if (UserProvider.getCurrent().hasUserRight(UserRight.AGGREGATE_REPORT_EDIT)) {
			btnCreate = ButtonHelper.createIconButton(Captions.aggregateReportNewAggregateReport, VaadinIcons.PLUS_CIRCLE, e ->
					ControllerProvider.getAggregateReportController()
					.openEditOrCreateWindow(() -> grid.reload(), false), ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(btnCreate);

			btnEdit = ButtonHelper.createIconButton(Captions.aggregateReportEditAggregateReport, VaadinIcons.EDIT,
					e -> ControllerProvider.getAggregateReportController()
					.openEditOrCreateWindow(() -> grid.reload(), true), ValoTheme.BUTTON_PRIMARY);
			btnEdit.setVisible(false);

			addHeaderComponent(btnEdit);
		}

		if (UserProvider.getCurrent().hasUserRight(UserRight.AGGREGATE_REPORT_EXPORT)) {
			btnExport = ButtonHelper.createIconButton(Captions.export, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);

			addHeaderComponent(btnExport);

			StreamResource streamResource = new GridExportStreamResource(grid, "sormas_aggregate_reports", "sormas_aggregate_reports_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(btnExport);
		}

		binder.readBean(criteria);
		binder.addValueChangeListener(e -> {
			try {
				binder.writeBean(criteria);
				grid.reload();
			} catch (ValidationException ex) {
				// No validation needed
			}
		});

		if (user.getRegion() != null) {
			cbRegionFilter.setValue(user.getRegion());
			if (user.getDistrict() != null) {
				cbDistrictFilter.setValue(user.getDistrict());
				if (user.getHealthFacility() != null) {
					cbFacilityFilter.setValue(user.getHealthFacility());
				} else if (user.getPointOfEntry() != null) {
					cbPoeFilter.setValue(user.getPointOfEntry());
				}
			}
		}
	}

	private VerticalLayout createFilterBar(UserDto user) {

		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		hlFirstFilterRow = new HorizontalLayout();
		hlFirstFilterRow.setMargin(false);
		hlFirstFilterRow.setSpacing(true);
		hlFirstFilterRow.setWidthUndefined();
		{
			cbRegionFilter = new ComboBox<>();
			cbRegionFilter.setId(AggregateReportCriteria.REGION);
			cbRegionFilter.addValueChangeListener(e -> updateButtonVisibility());
			cbRegionFilter.addValueChangeListener(e -> {
				RegionReferenceDto region = e.getValue();
				cbDistrictFilter.clear();
				if (region != null) {
					cbDistrictFilter
							.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
					cbDistrictFilter.setEnabled(true);
				} else {
					cbDistrictFilter.setEnabled(false);
				}
			});
			if (user.getRegion() == null) {
				cbRegionFilter.setWidth(200, Unit.PIXELS);
				cbRegionFilter.setPlaceholder(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REGION));
				cbRegionFilter.setItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());
				binder.bind(cbRegionFilter, AggregateReportCriteria.REGION);
				hlFirstFilterRow.addComponent(cbRegionFilter);
			}

			cbDistrictFilter = new ComboBox<>();
			cbDistrictFilter.setId(AggregateReportCriteria.DISTRICT);
			cbDistrictFilter.addValueChangeListener(e -> updateButtonVisibility());
			cbDistrictFilter.setWidth(200, Unit.PIXELS);
			cbDistrictFilter.setPlaceholder(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
			binder.bind(cbDistrictFilter, AggregateReportCriteria.DISTRICT);
			cbDistrictFilter.addValueChangeListener(e -> {
				DistrictReferenceDto district = e.getValue();
				if (cbFacilityFilter != null) {
					cbFacilityFilter.clear();
				}
				if (cbPoeFilter != null) {
					cbPoeFilter.clear();
				}
				if (district != null) {
					if (cbFacilityFilter != null) {
						cbFacilityFilter.setItems(FacadeProvider.getFacilityFacade().getActiveHealthFacilitiesByDistrict(district, false));
						cbFacilityFilter.setEnabled(true);
					}
					if (cbPoeFilter != null) {
						cbPoeFilter.setItems(FacadeProvider.getPointOfEntryFacade().getAllActiveByDistrict(district.getUuid(), false));
						cbPoeFilter.setEnabled(true);
					}
				} else {
					cbFacilityFilter.setEnabled(false);
					cbPoeFilter.setEnabled(false);
				}
			});
			cbDistrictFilter.setEnabled(false);
			hlFirstFilterRow.addComponent(cbDistrictFilter);

			cbFacilityFilter = new ComboBox<>();
			cbFacilityFilter.setId(AggregateReportCriteria.HEALTH_FACILITY);
			if (!UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles())) {
				cbFacilityFilter.addValueChangeListener(e -> updateButtonVisibility());
				cbFacilityFilter.addValueChangeListener(e -> clearFilterIfNotEmpty(cbFacilityFilter, cbPoeFilter));
				cbFacilityFilter.setWidth(200, Unit.PIXELS);
				cbFacilityFilter.setPlaceholder(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
				binder.bind(cbFacilityFilter, AggregateReportCriteria.HEALTH_FACILITY);
				cbFacilityFilter.setEnabled(false);
				hlFirstFilterRow.addComponent(cbFacilityFilter);
			}

			cbPoeFilter = new ComboBox<>();
			cbPoeFilter.setId(AggregateReportCriteria.POINT_OF_ENTRY);
			if (UserProvider.getCurrent().hasUserRight(UserRight.PORT_HEALTH_INFO_VIEW)) {
				cbPoeFilter.addValueChangeListener(e -> updateButtonVisibility());
				cbPoeFilter.addValueChangeListener(e -> clearFilterIfNotEmpty(cbPoeFilter, cbFacilityFilter));
				cbPoeFilter.setWidth(200, Unit.PIXELS);
				cbPoeFilter.setPlaceholder(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.POINT_OF_ENTRY));
				binder.bind(cbPoeFilter, AggregateReportCriteria.POINT_OF_ENTRY);
				cbPoeFilter.setEnabled(false);
				hlFirstFilterRow.addComponent(cbPoeFilter);
			}
		}
		filterLayout.addComponent(hlFirstFilterRow);

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
			cbFromYearFilter.addValueChangeListener(e -> updateButtonVisibility());
			cbFromEpiWeekFilter = new ComboBox<>();
			cbFromEpiWeekFilter.setId(AggregateReportCriteria.EPI_WEEK_FROM);
			cbFromEpiWeekFilter.addValueChangeListener(e -> updateButtonVisibility());
			cbToYearFilter = new ComboBox<>();
			cbToYearFilter.setId("yearTo");
			cbToYearFilter.addValueChangeListener(e -> clearFilterIfEmpty(cbFromYearFilter, cbToEpiWeekFilter));
			cbToYearFilter.addValueChangeListener(e -> updateButtonVisibility());
			cbToEpiWeekFilter = new ComboBox<>();
			cbToEpiWeekFilter.setId(AggregateReportCriteria.EPI_WEEK_TO);
			cbToEpiWeekFilter.addValueChangeListener(e -> updateButtonVisibility());

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
			binder.bind(cbFromEpiWeekFilter, AggregateReportCriteria.EPI_WEEK_FROM);
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
			binder.bind(cbToEpiWeekFilter, AggregateReportCriteria.EPI_WEEK_TO);
			hlSecondFilterRow.addComponent(cbToEpiWeekFilter);
		}
		filterLayout.addComponent(hlSecondFilterRow);

		return filterLayout;
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

	private void updateButtonVisibility() {
		if (btnEdit != null && btnCreate != null) {
			if (cbRegionFilter.getValue() != null && cbDistrictFilter.getValue() != null
					&& (cbFacilityFilter.getValue() != null || cbPoeFilter.getValue() != null)
					&& cbFromEpiWeekFilter.getValue() != null
					&& cbFromEpiWeekFilter.getValue().equals(cbToEpiWeekFilter.getValue())) {
				criteria.healthFacility(cbFacilityFilter.getValue());
				criteria.pointOfEntry(cbPoeFilter.getValue());
				if (FacadeProvider.getAggregateReportFacade().countWithCriteria(criteria) > 0) {
					btnCreate.setVisible(false);
					btnEdit.setVisible(true);
				} else {
					btnCreate.setVisible(true);
					btnEdit.setVisible(false);
				}
			} else {
				btnCreate.setVisible(true);
				btnEdit.setVisible(false);
			}
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

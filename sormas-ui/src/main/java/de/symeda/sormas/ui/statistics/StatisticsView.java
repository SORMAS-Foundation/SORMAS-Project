package de.symeda.sormas.ui.statistics;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.dashboard.DateFilterOption;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.CssStyles;

public class StatisticsView extends AbstractView {

	private static final long serialVersionUID = -4440568319850399685L;

	public static final String VIEW_NAME = "statistics";

	private VerticalLayout statisticsLayout;
	private StatisticsAgeSexGrid ageSexGrid;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	private Date fromDate;
	private Date toDate;

	public StatisticsView() {
		super(VIEW_NAME);
		
		ageSexGrid = new StatisticsAgeSexGrid();
		ageSexGrid.setHeightMode(HeightMode.ROW);
		ageSexGrid.setHeightByRows(5);

		statisticsLayout = new VerticalLayout();
		statisticsLayout.addComponent(createFilterBar());
		statisticsLayout.addComponent(ageSexGrid);
		statisticsLayout.setMargin(true);
		statisticsLayout.setSpacing(true);
		
		addComponent(statisticsLayout);
	}

	public HorizontalLayout createFilterBar() {
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setSizeUndefined();
		filterLayout.addStyleName(CssStyles.VSPACE_3);

		ComboBox regionFilter = new ComboBox();
		ComboBox districtFilter = new ComboBox();
		ComboBox diseaseFilter = new ComboBox();
		ComboBox dateFilterOptionFilter = new ComboBox();
		DateField dateFromFilter = new DateField();
		DateField dateToFilter = new DateField();
		ComboBox weekFromFilter = new ComboBox();
		ComboBox weekToFilter = new ComboBox();

		// 'Apply Filter' button
		Button applyButton = new Button("Apply filters");
		CssStyles.style(applyButton, CssStyles.FORCE_CAPTION);
		applyButton.addClickListener(e -> {
			region = (RegionReferenceDto) regionFilter.getValue();
			district = (DistrictReferenceDto) districtFilter.getValue();
			disease = (Disease) diseaseFilter.getValue();
			DateFilterOption dateFilterOption = (DateFilterOption) dateFilterOptionFilter.getValue();
			if (dateFilterOption == DateFilterOption.DATE) {
				fromDate = dateFromFilter.getValue();
				toDate = dateToFilter.getValue();
			} else {
				fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekFromFilter.getValue());
				toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekToFilter.getValue());
			}
			applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
			refreshStatistics();
		});

		// Region/District filter
		if (LoginHelper.getCurrentUser().getRegion() == null) {
			regionFilter.setWidth(200, Unit.PIXELS);
			regionFilter.setInputPrompt("State");
			regionFilter.addItems(FacadeProvider.getRegionFacade().getAllAsReference());
			regionFilter.addValueChangeListener(e -> {
				applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			});
			regionFilter.setCaption("State");
			filterLayout.addComponent(regionFilter);
			region = (RegionReferenceDto) regionFilter.getValue();
		} else {
			districtFilter.setWidth(200, Unit.PIXELS);
			districtFilter.setInputPrompt("Local Government Area");
			districtFilter.addItems(FacadeProvider.getDistrictFacade().getAllByRegion(LoginHelper.getCurrentUser().getRegion().getUuid()));
			districtFilter.addValueChangeListener(e -> {
				applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			});
			districtFilter.setCaption("Local Government Area");
			filterLayout.addComponent(districtFilter);
			district = (DistrictReferenceDto) districtFilter.getValue();
		}

		// Disease filter
		diseaseFilter.setWidth(200, Unit.PIXELS);
		diseaseFilter.setInputPrompt("Disease");
		diseaseFilter.addItems((Object[])Disease.values());
		diseaseFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		diseaseFilter.setCaption("Disease");
		filterLayout.addComponent(diseaseFilter);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());

		// Date filter options
		dateFilterOptionFilter.setWidth(200, Unit.PIXELS);
		CssStyles.style(dateFilterOptionFilter, CssStyles.FORCE_CAPTION);
		dateFilterOptionFilter.addItems((Object[])DateFilterOption.values());
		dateFilterOptionFilter.setNullSelectionAllowed(false);
		dateFilterOptionFilter.select(DateFilterOption.EPI_WEEK);
		dateFilterOptionFilter.addValueChangeListener(e -> {
			if (e.getProperty().getValue() == DateFilterOption.DATE) {
				filterLayout.removeComponent(weekFromFilter);
				filterLayout.removeComponent(weekToFilter);
				filterLayout.addComponent(dateFromFilter, filterLayout.getComponentIndex(dateFilterOptionFilter) + 1);
				dateFromFilter.setValue(DateHelper.subtractDays(c.getTime(), 7));
				filterLayout.addComponent(dateToFilter, filterLayout.getComponentIndex(dateFromFilter) + 1);
				dateToFilter.setValue(c.getTime());
			} else {
				filterLayout.removeComponent(dateFromFilter);
				filterLayout.removeComponent(dateToFilter);
				filterLayout.addComponent(weekFromFilter, filterLayout.getComponentIndex(dateFilterOptionFilter) + 1);
				weekFromFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
				filterLayout.addComponent(weekToFilter, filterLayout.getComponentIndex(weekFromFilter) + 1);
				weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
			}
		});
		filterLayout.addComponent(dateFilterOptionFilter);

		// Epi week filter
		List<EpiWeek> epiWeekList = DateHelper.createEpiWeekList(c.get(Calendar.YEAR), c.get(Calendar.WEEK_OF_YEAR));

		weekFromFilter.setWidth(200, Unit.PIXELS);
		for (EpiWeek week : epiWeekList) {
			weekFromFilter.addItem(week);
		}
		weekFromFilter.setNullSelectionAllowed(false);
		weekFromFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
		weekFromFilter.setCaption("From Epi Week");
		weekFromFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		filterLayout.addComponent(weekFromFilter);
		fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekFromFilter.getValue());

		weekToFilter.setWidth(200, Unit.PIXELS);
		for (EpiWeek week : epiWeekList) {
			weekToFilter.addItem(week);
		}
		weekToFilter.setNullSelectionAllowed(false);
		weekToFilter.setValue(DateHelper.getEpiWeek(c.getTime()));
		weekToFilter.setCaption("To Epi Week");
		weekToFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		filterLayout.addComponent(weekToFilter);
		toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekToFilter.getValue());

		// Date filter
		dateFromFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
		dateFromFilter.setWidth(200, Unit.PIXELS);
		dateFromFilter.setCaption("From");
		dateFromFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});

		dateToFilter.setDateFormat(DateHelper.getShortDateFormat().toPattern());
		dateToFilter.setWidth(200, Unit.PIXELS);
		dateToFilter.setCaption("To");
		dateToFilter.addValueChangeListener(e -> {
			applyButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		});
		filterLayout.addComponent(applyButton);

		Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription("All Statistics elements use the onset date of the first symptom for the date/epi week filter. If this date is not available, the date of report is used instead.");
		CssStyles.style(infoLabel, CssStyles.SIZE_XLARGE, CssStyles.COLOR_SECONDARY);
		filterLayout.addComponent(infoLabel);
		filterLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_RIGHT);

		return filterLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		refreshStatistics();
	}

	private void refreshStatistics() {
		ageSexGrid.reload(region, district, disease, fromDate, toDate);
	}

}

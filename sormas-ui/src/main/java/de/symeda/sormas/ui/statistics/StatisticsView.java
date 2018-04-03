package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.dashboard.DateFilterOption;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.EpiWeekAndDateFilterComponent;

public class StatisticsView extends AbstractStatisticsView {

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
		
		if (LoginHelper.hasUserRight(UserRight.CASE_EXPORT)) {
			Button exportButton = new Button("Export");
			exportButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
			exportButton.setIcon(FontAwesome.DOWNLOAD);
			
			StreamResource streamResource = DownloadUtil.createGridExportStreamResource(ageSexGrid.getContainerDataSource(), new ArrayList<>(ageSexGrid.getColumns()), "sormas_statistics_age_sex", "sormas_statistics_age_sex_" + DateHelper.formatDateForExport(new Date()) + ".csv");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(exportButton);
			
			addHeaderComponent(exportButton);
		}
		
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

		// 'Apply Filter' button
		Button applyButton = new Button("Apply filters");
		CssStyles.style(applyButton, CssStyles.FORCE_CAPTION);

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

		EpiWeekAndDateFilterComponent weekAndDateFilter = new EpiWeekAndDateFilterComponent(applyButton, true, true);
		filterLayout.addComponent(weekAndDateFilter);	
		fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
		toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
		filterLayout.addComponent(applyButton);

		Label infoLabel = new Label(FontAwesome.INFO_CIRCLE.getHtml(), ContentMode.HTML);
		infoLabel.setSizeUndefined();
		infoLabel.setDescription("All Statistics elements use the onset date of the first symptom for the date/epi week filter. If this date is not available, the reception date or date of report is used instead.");
		CssStyles.style(infoLabel, CssStyles.LABEL_XLARGE, CssStyles.LABEL_SECONDARY);
		filterLayout.addComponent(infoLabel);
		filterLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_RIGHT);
		
		applyButton.addClickListener(e -> {
			region = (RegionReferenceDto) regionFilter.getValue();
			district = (DistrictReferenceDto) districtFilter.getValue();
			disease = (Disease) diseaseFilter.getValue();
			DateFilterOption dateFilterOption = (DateFilterOption) weekAndDateFilter.getDateFilterOptionFilter().getValue();
			if (dateFilterOption == DateFilterOption.DATE) {
				fromDate = weekAndDateFilter.getDateFromFilter().getValue();
				toDate = weekAndDateFilter.getDateToFilter().getValue();
			} else {
				fromDate = DateHelper.getEpiWeekStart((EpiWeek) weekAndDateFilter.getWeekFromFilter().getValue());
				toDate = DateHelper.getEpiWeekEnd((EpiWeek) weekAndDateFilter.getWeekToFilter().getValue());
			}
			applyButton.removeStyleName(ValoTheme.BUTTON_PRIMARY);
			refreshStatistics();
		});
		
		return filterLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		refreshStatistics();
	}

	private void refreshStatistics() {
		ageSexGrid.reload(region, district, disease, fromDate, toDate);
	}

}

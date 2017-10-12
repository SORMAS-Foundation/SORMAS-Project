package de.symeda.sormas.ui.reports;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.report.WeeklyReportSummaryDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class WeeklyReportGrid extends Grid implements ItemClickListener {

	private static final String VIEW_DETAILS_BTN_ID = "viewDetails";
	
	private int week;
	private int year;
	
	public WeeklyReportGrid() {
		setSizeFull();
		
		setSelectionMode(SelectionMode.NONE);
		
		BeanItemContainer<WeeklyReportSummaryDto> container = new BeanItemContainer<WeeklyReportSummaryDto>(WeeklyReportSummaryDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
        VaadinUiUtil.addIconColumn(generatedContainer, VIEW_DETAILS_BTN_ID, FontAwesome.EYE);
		setContainerDataSource(generatedContainer);
		
		setColumns(VIEW_DETAILS_BTN_ID, WeeklyReportSummaryDto.REGION, WeeklyReportSummaryDto.DISTRICT, WeeklyReportSummaryDto.FACILITIES, 
				WeeklyReportSummaryDto.REPORTS, WeeklyReportSummaryDto.REPORTS_PERCENTAGE,
				WeeklyReportSummaryDto.ZERO_REPORTS, WeeklyReportSummaryDto.ZERO_REPORTS_PERCENTAGE,
				WeeklyReportSummaryDto.MISSING_REPORTS, WeeklyReportSummaryDto.MISSING_REPORTS_PERCENTAGE);
	
		if (LoginHelper.getCurrentUserRoles().contains(UserRole.NATIONAL_USER)) {
			getColumn(VIEW_DETAILS_BTN_ID).setHidden(true);
		} else {
	        getColumn(VIEW_DETAILS_BTN_ID).setRenderer(new HtmlRenderer());
	        getColumn(VIEW_DETAILS_BTN_ID).setWidth(60);
		}
		
		for (Column column : getColumns()) {
			if (column.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
				column.setHeaderCaption("");
			} else {
				column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
						WeeklyReportSummaryDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
			}
		}
		
		if (LoginHelper.getCurrentUserRoles().contains(UserRole.NATIONAL_USER)) {
			getColumn(WeeklyReportSummaryDto.DISTRICT).setHidden(true);
		} else {
			getColumn(WeeklyReportSummaryDto.REGION).setHidden(true);
		}
	
		addItemClickListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private BeanItemContainer<WeeklyReportSummaryDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<WeeklyReportSummaryDto>) container.getWrappedContainer();
	}
	
	public void reload(int year, int week) {
		this.week = week;
		this.year = year;
		getContainer().removeAllItems();
		EpiWeek epiWeek = new EpiWeek(year, week);
		
		if (LoginHelper.getCurrentUserRoles().contains(UserRole.NATIONAL_USER)) {
			List<WeeklyReportSummaryDto> summaryDtos = FacadeProvider.getWeeklyReportFacade().getSummariesPerRegion(epiWeek);
			summaryDtos.forEach(s -> getContainer().addItem(s));
		} else {
			List<WeeklyReportSummaryDto> summaryDtos = FacadeProvider.getWeeklyReportFacade().getSummariesPerDistrict(LoginHelper.getCurrentUser().getRegion(), epiWeek);
			summaryDtos.forEach(s -> getContainer().addItem(s));
		}
	}
	
	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.getPropertyId().equals(VIEW_DETAILS_BTN_ID)) {
			WeeklyReportSummaryDto summaryDto = (WeeklyReportSummaryDto) event.getItemId();
			VerticalLayout layout = new VerticalLayout();
			WeeklyReportDetailsGrid grid = new WeeklyReportDetailsGrid(summaryDto.getDistrict(), new EpiWeek(year, week));
			grid.setHeightMode(HeightMode.ROW);
			layout.addComponent(grid);
			layout.setMargin(true);
			Window window = VaadinUiUtil.showPopupWindow(layout);
			window.setCaption("Weekly Reports in " + summaryDto.getDistrict().toString() + " - Epi Week " + week + "/" + year);
		}
	}
	
}

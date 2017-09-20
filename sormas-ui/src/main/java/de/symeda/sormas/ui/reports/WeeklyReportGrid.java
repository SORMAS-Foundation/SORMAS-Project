package de.symeda.sormas.ui.reports;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.report.WeeklyReportSummaryDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.login.LoginHelper;

@SuppressWarnings("serial")
public class WeeklyReportGrid extends Grid {

	public WeeklyReportGrid() {
		setSizeFull();
		
		setSelectionMode(SelectionMode.NONE);
		
		BeanItemContainer<WeeklyReportSummaryDto> container = new BeanItemContainer<WeeklyReportSummaryDto>(WeeklyReportSummaryDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);
		
		setColumns(WeeklyReportSummaryDto.REGION, WeeklyReportSummaryDto.DISTRICT, WeeklyReportSummaryDto.FACILITIES, 
				WeeklyReportSummaryDto.REPORTS, WeeklyReportSummaryDto.REPORTS_PERCENTAGE,
				WeeklyReportSummaryDto.ZERO_REPORTS, WeeklyReportSummaryDto.ZERO_REPORTS_PERCENTAGE,
				WeeklyReportSummaryDto.MISSING_REPORTS, WeeklyReportSummaryDto.MISSING_REPORTS_PERCENTAGE);
	
		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					WeeklyReportSummaryDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
		
		if (LoginHelper.getCurrentUserRoles().contains(UserRole.NATIONAL_USER)) {
			getColumn(WeeklyReportSummaryDto.DISTRICT).setHidden(true);
		} else {
			getColumn(WeeklyReportSummaryDto.REGION).setHidable(true);
		}
	}
	
	@SuppressWarnings("unchecked")
	private BeanItemContainer<WeeklyReportSummaryDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<WeeklyReportSummaryDto>) container.getWrappedContainer();
	}
	
	public void reload(int year, int week) {
		getContainer().removeAllItems();
		EpiWeek epiWeek = new EpiWeek(year, week);
		
		if (LoginHelper.getCurrentUserRoles().contains(UserRole.NATIONAL_USER)) {
			List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllAsReference();
			for (RegionReferenceDto region : regions) {
				WeeklyReportSummaryDto dto = FacadeProvider.getWeeklyReportFacade().getSummaryDtoByRegion(region, epiWeek);
				getContainer().addItem(dto);
			}
		} else {
			List<DistrictReferenceDto> districts = FacadeProvider.getDistrictFacade().getAllByRegion(LoginHelper.getCurrentUser().getRegion().getUuid());
			for (DistrictReferenceDto district : districts) {
				WeeklyReportSummaryDto dto = FacadeProvider.getWeeklyReportFacade().getSummaryDtoByDistrict(district, epiWeek);
				getContainer().addItem(dto);
			}
		}
	}
	
}

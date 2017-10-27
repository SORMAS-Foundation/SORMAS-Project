package de.symeda.sormas.ui.reports;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.utils.BooleanRenderer;

public class WeeklyReportDetailsGrid extends Grid {

	private static final long serialVersionUID = 4840654834928465293L;

	public static final String COLUMN_CONFIRMED = "columnConfirmed";
	
	private final DistrictReferenceDto districtRef;
	private final EpiWeek epiWeek;
	
	public WeeklyReportDetailsGrid(DistrictReferenceDto districtRef, EpiWeek epiWeek) {
		this.districtRef = districtRef;
		this.epiWeek = epiWeek;
		setSizeFull();
		
		setSelectionMode(SelectionMode.NONE);
		
		BeanItemContainer<WeeklyReportDetails> container = new BeanItemContainer<WeeklyReportDetails>(WeeklyReportDetails.class);
		setContainerDataSource(container);
		
		setColumns(WeeklyReportDetails.FACILITY_AND_INFORMANT, WeeklyReportDetails.TOTAL_NUMBER_OF_CASES, WeeklyReportDetails.CONFIRMED);
	
		getColumn(WeeklyReportDetails.CONFIRMED).setRenderer(new BooleanRenderer());
		
		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(
					WeeklyReportDetails.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
		
		reload();
	}
	
	@SuppressWarnings("unchecked")
	private BeanItemContainer<WeeklyReportDetails> getContainer() {
		return (BeanItemContainer<WeeklyReportDetails>) super.getContainerDataSource();
    }
	
	public void reload() {
		getContainer().removeAllItems();
		
		List<WeeklyReportDetails> reportDetailDtos = new ArrayList<>();
		List<UserReferenceDto> informants = FacadeProvider.getUserFacade().getForWeeklyReportDetails(districtRef);
		for (UserReferenceDto informant : informants) {
			WeeklyReportReferenceDto weeklyReportRef = FacadeProvider.getWeeklyReportFacade().getByEpiWeekAndUser(epiWeek, informant);
			WeeklyReportDto weeklyReport = null;
			if (weeklyReportRef != null) {
				weeklyReport = FacadeProvider.getWeeklyReportFacade().getByUuid(weeklyReportRef.getUuid());
			}
			WeeklyReportDetails reportDetails = new WeeklyReportDetails(informant, weeklyReport != null ? weeklyReport.getTotalNumberOfCases() : 0, weeklyReport != null ? true : false);
			reportDetailDtos.add(reportDetails);
		}
		
		getContainer().addAll(reportDetailDtos);
		this.setHeightByRows(reportDetailDtos.size());
	}

	public class WeeklyReportDetails {
		public static final String I18N_PREFIX = "WeeklyReportDetails";
		public static final String FACILITY_AND_INFORMANT = "facilityAndInformant";
		public static final String TOTAL_NUMBER_OF_CASES = "totalNumberOfCases";
		public static final String CONFIRMED = "confirmed";
		
		private UserReferenceDto informant;
		private String facilityAndInformant;
		private int totalNumberOfCases;
		private boolean confirmed;
		
		public WeeklyReportDetails(UserReferenceDto informant, int totalNumberOfCases, boolean confirmed) {
			this.informant = informant;
			
			if (informant != null) {
				UserDto informantDto = FacadeProvider.getUserFacade().getByUuid(informant.getUuid());
				facilityAndInformant = informantDto.getHealthFacility().toString() + " | " + informantDto.toString();
			}
			
			this.totalNumberOfCases = totalNumberOfCases;
			this.confirmed = confirmed;
		}

		public UserReferenceDto getInformant() {
			return informant;
		}
		public void setInformant(UserReferenceDto informant) {
			this.informant = informant;
		}
		public String getFacilityAndInformant() {
			return facilityAndInformant;
		}
		public void setFacilityAndInformant(String facilityAndInformant) {
			this.facilityAndInformant = facilityAndInformant;
		}
		public int getTotalNumberOfCases() {
			return totalNumberOfCases;
		}
		public void setTotalNumberOfCases(int totalNumberOfCases) {
			this.totalNumberOfCases = totalNumberOfCases;
		}
		public boolean isConfirmed() {
			return confirmed;
		}
		public void setConfirmed(boolean confirmed) {
			this.confirmed = confirmed;
		}
	}
	
}

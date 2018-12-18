/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.EpiWeek;

public class WeeklyReportInformantsGrid extends Grid {

	private static final long serialVersionUID = 4840654834928465293L;

	private final UserReferenceDto officerRef;
	private final EpiWeek epiWeek;

	public WeeklyReportInformantsGrid(UserReferenceDto officerRef, EpiWeek epiWeek) {
		this.officerRef = officerRef;
		this.epiWeek = epiWeek;
		setSizeFull();

		setSelectionMode(SelectionMode.NONE);

		BeanItemContainer<WeeklyReportInformantSummary> container = new BeanItemContainer<WeeklyReportInformantSummary>(
				WeeklyReportInformantSummary.class);
		setContainerDataSource(container);

		setColumns(WeeklyReportInformantSummary.INFORMANT, 
				WeeklyReportInformantSummary.COMMUNITY,
				WeeklyReportInformantSummary.FACILITY,
				WeeklyReportInformantSummary.INFORMANT_REPORT_DATE,
				WeeklyReportInformantSummary.TOTAL_CASE_COUNT);

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixFieldCaption(WeeklyReportInformantSummary.I18N_PREFIX,
					column.getPropertyId().toString(), column.getHeaderCaption()));
		}

		reload();
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<WeeklyReportInformantSummary> getContainer() {
		return (BeanItemContainer<WeeklyReportInformantSummary>) super.getContainerDataSource();
	}

	public void reload() {
		getContainer().removeAllItems();

		List<WeeklyReportInformantSummary> reportDetailDtos = new ArrayList<>();
		List<UserDto> informants = FacadeProvider.getUserFacade().getUsersByAssociatedOfficer(officerRef,
				UserRole.HOSPITAL_INFORMANT, UserRole.COMMUNITY_INFORMANT);
		// sort by...
		informants.sort((a, b) -> {
			// 1. community
			if (a.getCommunity() != null && b.getCommunity() != null) {
				int result = a.getCommunity().getCaption().compareTo(b.getCommunity().getCaption());
				if (result != 0)
					return result;
			} else if (a.getCommunity() != null) {
				return 1;
			} else if (b.getCommunity() != null) {
				return -1;
			}
			// 2. facility
			if (a.getHealthFacility() != null && b.getHealthFacility() != null) {
				int result = a.getHealthFacility().getCaption().compareTo(b.getHealthFacility().getCaption());
				if (result != 0)
					return result;
			}
			// 3. name
			return a.getName().compareTo(b.getName());
		});

		for (UserDto informant : informants) {
			WeeklyReportInformantSummary reportDetails = new WeeklyReportInformantSummary();
			reportDetails.setInformant(informant.toReference());
			reportDetails.setCommunity(informant.getCommunity());
			reportDetails.setFacility(informant.getHealthFacility());
			WeeklyReportDto weeklyReport = FacadeProvider.getWeeklyReportFacade().getByEpiWeekAndUser(epiWeek,
					informant.toReference());
			if (weeklyReport != null) {
				reportDetails.setFacility(weeklyReport.getHealthFacility());
				reportDetails.setCommunity(weeklyReport.getCommunity());
				reportDetails.setInformantReportDate(weeklyReport.getReportDateTime());
				reportDetails.setTotalCaseCount(weeklyReport.getTotalNumberOfCases());
			}
			reportDetailDtos.add(reportDetails);
		}

		getContainer().addAll(reportDetailDtos);
		this.setHeightByRows(reportDetailDtos.size());
	}

	public class WeeklyReportInformantSummary {
		public static final String I18N_PREFIX = "WeeklyReportDetails";
		public static final String INFORMANT = "informant";
		public static final String COMMUNITY = "community";
		public static final String FACILITY = "facility";
		public static final String INFORMANT_REPORT_DATE = "informantReportDate";
		public static final String TOTAL_CASE_COUNT = "totalCaseCount";

		private UserReferenceDto informant;
		private CommunityReferenceDto community;
		private FacilityReferenceDto facility;
		private Date informantReportDate;
		private int totalCaseCount;

		public UserReferenceDto getInformant() {
			return informant;
		}

		public void setInformant(UserReferenceDto informant) {
			this.informant = informant;
		}

		public FacilityReferenceDto getFacility() {
			return facility;
		}

		public void setFacility(FacilityReferenceDto facility) {
			this.facility = facility;
		}

		public Date getInformantReportDate() {
			return informantReportDate;
		}

		public void setInformantReportDate(Date informantReportDate) {
			this.informantReportDate = informantReportDate;
		}

		public int getTotalCaseCount() {
			return totalCaseCount;
		}

		public void setTotalCaseCount(int totalCaseCount) {
			this.totalCaseCount = totalCaseCount;
		}

		public CommunityReferenceDto getCommunity() {
			return community;
		}

		public void setCommunity(CommunityReferenceDto community) {
			this.community = community;
		}
	}

}

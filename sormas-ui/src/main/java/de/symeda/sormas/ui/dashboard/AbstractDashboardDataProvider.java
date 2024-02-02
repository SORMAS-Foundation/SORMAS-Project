/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.dashboard;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.BaseDashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.NewDateFilterType;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;

public abstract class AbstractDashboardDataProvider<C extends BaseDashboardCriteria<C>> {

	protected Date fromDate;
	protected Date toDate;
	protected Date previousFromDate;
	protected Date previousToDate;
	protected RegionReferenceDto region;
	protected DistrictReferenceDto district;
	protected Disease disease;
	private DashboardType dashboardType;
	private CriteriaDateType newCaseDateType;

	private CaseClassification caseClassification;
	private NewDateFilterType dateFilterType;


	public abstract void refreshData();

	public C buildDashboardCriteriaWithDates() {
		return buildDashboardCriteria().dateBetween(fromDate, toDate);
	}

	protected C buildDashboardCriteria() {
		return newCriteria().region(region).district(district).disease(disease);
	}

	protected abstract C newCriteria();

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getPreviousFromDate() {
		return previousFromDate;
	}

	public void setPreviousFromDate(Date previousFromDate) {
		this.previousFromDate = previousFromDate;
	}

	public Date getPreviousToDate() {
		return previousToDate;
	}

	public void setPreviousToDate(Date previousToDate) {
		this.previousToDate = previousToDate;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public DashboardType getDashboardType() {
		return dashboardType;
	}

	public void setDashboardType(DashboardType dashboardType) {
		this.dashboardType = dashboardType;
	}

	public void setNewCaseDateType(CriteriaDateType newCaseDateType) {
		this.newCaseDateType = newCaseDateType;
	}

	public CriteriaDateType getNewCaseDateType() {
		return newCaseDateType;
	}


	public DashboardCriteria getCriteria() {
		return new DashboardCriteria().region(region)
				.district(district)
				.disease(disease)
				.dateBetween(fromDate, toDate)
				.caseClassification(caseClassification)
				.newCaseDateType(newCaseDateType)
				.dateFilterType(dateFilterType);
	}


	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}




	public void setNewCaseDateType(NewCaseDateType newCaseDateType) {
		this.newCaseDateType = newCaseDateType;
	}

	public NewDateFilterType getDateFilterType() {
		if (dateFilterType == NewDateFilterType.TODAY) {
			setFromDate(DateHelper.getStartOfDay(new Date()));
			setToDate(new Date());
		}
		if (dateFilterType == NewDateFilterType.YESTERDAY) {
			setFromDate(DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 1)));
			setToDate(DateHelper.getEndOfDay(DateHelper.subtractDays(new Date(), 1)));
		}
		if (dateFilterType == NewDateFilterType.THIS_WEEK) {
			setFromDate(DateHelper.getStartOfWeek(new Date()));
			setToDate(new Date());
		}
		if (dateFilterType == NewDateFilterType.LAST_WEEK) {
			setFromDate(DateHelper.getStartOfWeek(DateHelper.subtractWeeks(new Date(), 1)));
			setToDate(DateHelper.getEndOfWeek(DateHelper.subtractWeeks(new Date(), 1)));
		}
		if (dateFilterType == NewDateFilterType.THIS_YEAR) {
			setFromDate(DateHelper.getStartOfWeek(DateHelper.getStartOfYear(new Date())));
			setToDate(new Date());
		}
		return dateFilterType;
	}

	public void setDateFilterType(NewDateFilterType dateFilterType) {
		this.dateFilterType = dateFilterType;
	}

}

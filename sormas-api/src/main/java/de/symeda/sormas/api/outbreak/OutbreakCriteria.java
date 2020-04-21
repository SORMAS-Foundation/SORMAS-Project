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
package de.symeda.sormas.api.outbreak;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.statistics.PeriodFilterMode;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class OutbreakCriteria extends BaseCriteria implements Serializable {

	private static final long serialVersionUID = 326691431810294295L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	private Boolean active;
	private Date activeLower, activeUpper;
	private Date changeDateAfter;
	private Date reportedDateFrom;
	private Date reportedDateTo;
	private PeriodFilterMode periodFilterMode;
	
	public RegionReferenceDto getRegion() {
		return region;
	}
	public OutbreakCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	public OutbreakCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}
	
	public Disease getDisease() {
		return disease;
	}
	public OutbreakCriteria disease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public Boolean getActive() {
		return active;
	}
	public Date getActiveLower() {
		return activeLower;
	}
	public Date getActiveUpper() {
		return activeUpper;
	}
	public OutbreakCriteria active(Boolean active) {
		this.active = active;
		if (active != null)
		{
			this.activeLower = new Date();
			this.activeUpper = new Date();
		} else {
			this.activeLower = null;
			this.activeUpper = null;			
		}
		return this;
	}
	public OutbreakCriteria active(boolean active, Date activeLower, Date activeUpper) {
		this.active = active;
		this.activeLower = activeLower;
		this.activeUpper = activeUpper;
		return this;
	}
	
	public Date getChangeDateAfter() {
		return changeDateAfter;
	}
	public OutbreakCriteria changeDateAfter(Date changeDateAfter) {
		this.changeDateAfter = changeDateAfter;
		return this;
	}
	
	public OutbreakCriteria reportedBetween (Date reportedDateFrom, Date reportedDateTo) {
		this.reportedDateFrom = reportedDateFrom;
		this.reportedDateTo = reportedDateTo;
		return this;
	}
	public OutbreakCriteria reportedDateFrom (Date reportedDateFrom) {
		this.reportedDateFrom = reportedDateFrom;
		return this;
	}
	public Date getReportedDateFrom() {
		return reportedDateFrom;
	}
	public OutbreakCriteria reportedDateTo (Date reportedDateTo) {
		this.reportedDateTo = reportedDateTo;
		return this;
	}
	public Date getReportedDateTo() {
		return reportedDateTo;
	}
	
	public PeriodFilterMode getPeriodSelectionType () {
		return periodFilterMode;
	}
	
	public OutbreakCriteria periodSelectionType(PeriodFilterMode periodSelectionType) {
		this.periodFilterMode = periodSelectionType;
		return this;
	}
}

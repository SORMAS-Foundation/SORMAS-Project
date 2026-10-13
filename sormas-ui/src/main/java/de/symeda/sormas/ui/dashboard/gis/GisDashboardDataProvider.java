/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.dashboard.gis;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDashboardFilterDateType;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.dashboard.AefiDashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.GisDashboardCriteria;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.ui.dashboard.AbstractDashboardDataProvider;

public class GisDashboardDataProvider extends AbstractDashboardDataProvider<GisDashboardCriteria> {

	private NewCaseDateType caseDateType = NewCaseDateType.MOST_RELEVANT;
	private SampleDashboardFilterDateType sampleDateType = SampleDashboardFilterDateType.MOST_RELEVANT;
	private SampleMaterial sampleMaterial;
	private Boolean sampleWithNoDisease;
	private AefiDashboardFilterDateType aefiDateType = AefiDashboardFilterDateType.REPORT_DATE;
	private Sex sex;

	//disease specific

	@Override
	public void refreshData() {
	}

	@Override
	protected GisDashboardCriteria newCriteria() {
		return new GisDashboardCriteria();
	}

	@Override
	protected GisDashboardCriteria buildDashboardCriteria() {
		return super.buildDashboardCriteriaWithDates();
	}

	public DashboardCriteria buildCaseDashboardCriteria() {
		return newCaseDashboardCriteria().newCaseDateType(caseDateType).dateBetween(fromDate, toDate);
	}

	public SampleDashboardCriteria buildSampleDashboardCriteria() {
		return newSampleDashboardCriteria().sampleDateType(sampleDateType)
			.sampleMaterial(sampleMaterial)
			.withNoDisease(sampleWithNoDisease)
			.dateBetween(fromDate, toDate);
	}

	public AefiDashboardCriteria buildAefiDashboardCriteria() {
		return newAefiDashboardCriteria().aefiDashboardDateType(aefiDateType).dateBetween(fromDate, toDate);
	}

	public DashboardCriteria buildEventDashboardCriteria() {
		return newEventDashboardCriteria().disease(disease).dateBetween(fromDate, toDate);
	}

	private DashboardCriteria newCaseDashboardCriteria() {
		return new DashboardCriteria();
	}

	private SampleDashboardCriteria newSampleDashboardCriteria() {
		return new SampleDashboardCriteria();
	}

	private AefiDashboardCriteria newAefiDashboardCriteria() {
		return new AefiDashboardCriteria();
	}

	private DashboardCriteria newEventDashboardCriteria() {
		return new DashboardCriteria();
	}

	public NewCaseDateType getCaseDateType() {
		if (caseDateType == null) {
			caseDateType = NewCaseDateType.MOST_RELEVANT;
		}
		return caseDateType;
	}

	public void setCaseDateType(NewCaseDateType caseDateType) {
		this.caseDateType = caseDateType;
	}

	public SampleDashboardFilterDateType getSampleDateType() {
		return sampleDateType;
	}

	public void setSampleDateType(SampleDashboardFilterDateType sampleDateType) {
		this.sampleDateType = sampleDateType;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public Boolean getSampleWithNoDisease() {
		return sampleWithNoDisease;
	}

	public void setSampleWithNoDisease(Boolean sampleWithNoDisease) {
		this.sampleWithNoDisease = sampleWithNoDisease;
	}

	public AefiDashboardFilterDateType getAefiDateType() {
		return aefiDateType;
	}

	public void setAefiDateType(AefiDashboardFilterDateType aefiDateType) {
		this.aefiDateType = aefiDateType;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}
}

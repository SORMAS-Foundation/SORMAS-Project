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

package de.symeda.sormas.ui.dashboard.sample;

import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.ui.dashboard.AbstractDashboardDataProvider;

public class SampleDashboardDataProvider extends AbstractDashboardDataProvider<SampleDashboardCriteria> {

	private SampleDashboardFilterDateType dateType = SampleDashboardFilterDateType.MOST_RELEVANT;

	private SampleMaterial sampleMaterial;

	private Boolean withNoDisease;

	private Map<PathogenTestResultType, Long> testResultCountByResultType;

	@Override
	public void refreshData() {
		testResultCountByResultType = FacadeProvider.getSampleDashboardFacade().getSampleCountsByResultType(buildDashboardCriteriaWithDates());
	}

	@Override
	protected SampleDashboardCriteria newCriteria() {
		return new SampleDashboardCriteria();
	}

	@Override
	protected SampleDashboardCriteria buildDashboardCriteriaWithDates() {
		return super.buildDashboardCriteriaWithDates();
	}

	@Override
	protected SampleDashboardCriteria buildDashboardCriteria() {
		return super.buildDashboardCriteria().sampleDateType(dateType).sampleMaterial(sampleMaterial).withNoDisease(withNoDisease);
	}

	public SampleDashboardFilterDateType getDateType() {
		return dateType;
	}

	public void setDateType(SampleDashboardFilterDateType dateType) {
		this.dateType = dateType;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public Boolean getWithNoDisease() {
		return withNoDisease;
	}

	public void setWithNoDisease(Boolean withNoDisease) {
		this.withNoDisease = withNoDisease;
	}

	public Map<PathogenTestResultType, Long> getTestResultCountByResultType() {
		return testResultCountByResultType;
	}
}

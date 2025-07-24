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
import de.symeda.sormas.api.dashboard.sample.SampleShipmentStatus;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.ui.dashboard.AbstractDashboardDataProvider;

public class SampleDashboardDataProvider extends AbstractDashboardDataProvider<SampleDashboardCriteria> {

	private SampleDashboardFilterDateType dateType = SampleDashboardFilterDateType.MOST_RELEVANT;

	private SampleMaterial sampleMaterial;

	private EnvironmentSampleMaterial environmentSampleMaterial;

	private Boolean withNoDisease;

	private Map<PathogenTestResultType, Long> sampleCountsByResultType;
	private Map<SamplePurpose, Long> sampleCountsByPurpose;
	private Map<SpecimenCondition, Long> sampleCountsBySpecimenCondition;
	private Map<SampleShipmentStatus, Long> sampleCountsByShipmentStatus;
	private Map<PathogenTestResultType, Long> testResultCountsByResultType;

	@Override
	public void refreshData() {
		sampleCountsByResultType = FacadeProvider.getSampleDashboardFacade().getSampleCountsByResultType(buildDashboardCriteriaWithDates());
		sampleCountsByPurpose = FacadeProvider.getSampleDashboardFacade().getSampleCountsByPurpose(buildDashboardCriteriaWithDates());
		sampleCountsBySpecimenCondition =
			FacadeProvider.getSampleDashboardFacade().getSampleCountsBySpecimenCondition(buildDashboardCriteriaWithDates());
		sampleCountsByShipmentStatus = FacadeProvider.getSampleDashboardFacade().getSampleCountsByShipmentStatus(buildDashboardCriteriaWithDates());
		testResultCountsByResultType = FacadeProvider.getSampleDashboardFacade().getTestResultCountsByResultType(buildDashboardCriteriaWithDates());
	}

	@Override
	protected SampleDashboardCriteria newCriteria() {
		return new SampleDashboardCriteria();
	}

	@Override
	protected SampleDashboardCriteria buildDashboardCriteria() {
		return super.buildDashboardCriteria().sampleDateType(dateType).sampleMaterial(sampleMaterial).environmentSampleMaterial(environmentSampleMaterial).withNoDisease(withNoDisease);
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

	public EnvironmentSampleMaterial getEnvironmentSampleMaterial() {
		return environmentSampleMaterial;
	}

	public void setEnvironmentSampleMaterial(EnvironmentSampleMaterial environmentSampleMaterial) {
		this.environmentSampleMaterial = environmentSampleMaterial;
	}

	public Map<PathogenTestResultType, Long> getSampleCountsByResultType() {
		return sampleCountsByResultType;
	}

	public Map<SamplePurpose, Long> getSampleCountsByPurpose() {
		return sampleCountsByPurpose;
	}

	public Map<SpecimenCondition, Long> getSampleCountsBySpecimenCondition() {
		return sampleCountsBySpecimenCondition;
	}

	public Map<SampleShipmentStatus, Long> getSampleCountsByShipmentStatus() {
		return sampleCountsByShipmentStatus;
	}

	public Map<PathogenTestResultType, Long> getTestResultCountsByResultType() {
		return testResultCountsByResultType;
	}
}

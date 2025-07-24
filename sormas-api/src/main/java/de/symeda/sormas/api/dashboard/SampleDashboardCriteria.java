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

package de.symeda.sormas.api.dashboard;

import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.sample.SampleDashboardFilterDateType;
import de.symeda.sormas.api.sample.SampleMaterial;

public class SampleDashboardCriteria extends BaseDashboardCriteria<SampleDashboardCriteria> {

	private SampleDashboardFilterDateType sampleDateType;
	private SampleMaterial sampleMaterial;
	private EnvironmentSampleMaterial environmentSampleMaterial;

	private Boolean withNoDisease;

	public SampleDashboardCriteria() {
		super(SampleDashboardCriteria.class);
	}

	public SampleDashboardFilterDateType getSampleDateType() {
		return sampleDateType;
	}

	public SampleDashboardCriteria sampleDateType(SampleDashboardFilterDateType sampleDateType) {
		this.sampleDateType = sampleDateType;

		return self;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public SampleDashboardCriteria sampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;

		return self;
	}

	public Boolean getWithNoDisease() {
		return withNoDisease;
	}

	public SampleDashboardCriteria withNoDisease(Boolean withNoDisease) {
		this.withNoDisease = withNoDisease;

		return self;
	}
	public EnvironmentSampleMaterial getEnvironmentSampleMaterial() {
		return environmentSampleMaterial;
	}
	public SampleDashboardCriteria environmentSampleMaterial(EnvironmentSampleMaterial environmentSampleMaterial) {
		this.environmentSampleMaterial = environmentSampleMaterial;

		return self;
	}
}

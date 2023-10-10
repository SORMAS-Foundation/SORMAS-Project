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

package de.symeda.sormas.backend.dashboard.sample;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.dashboard.sample.MapSampleDto;
import de.symeda.sormas.api.dashboard.sample.SampleDashboardFacade;
import de.symeda.sormas.api.dashboard.sample.SampleShipmentStatus;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SampleDashboardFacade")
@RightsAllowed(UserRight._DASHBOARD_SAMPLES_VIEW)
public class SampleDashboardFacadeEjb implements SampleDashboardFacade {

	@EJB
	private SampleDashboardService sampleDashboardService;

	@Override
	public Map<PathogenTestResultType, Long> getSampleCountsByResultType(SampleDashboardCriteria dashboardCriteria) {
		return sampleDashboardService.getSampleCountsByResultType(dashboardCriteria);
	}

	@Override
	public Map<SamplePurpose, Long> getSampleCountsByPurpose(SampleDashboardCriteria dashboardCriteria) {
		return sampleDashboardService.getSampleCountsByPurpose(dashboardCriteria);
	}

	@Override
	public Map<SpecimenCondition, Long> getSampleCountsBySpecimenCondition(SampleDashboardCriteria dashboardCriteria) {
		return sampleDashboardService.getSampleCountsBySpecimenCondition(dashboardCriteria);
	}

	@Override
	public Map<SampleShipmentStatus, Long> getSampleCountsByShipmentStatus(SampleDashboardCriteria dashboardCriteria) {
		return sampleDashboardService.getSampleCountsByShipmentStatus(dashboardCriteria);
	}

	@Override
	public Map<PathogenTestResultType, Long> getTestResultCountsByResultType(SampleDashboardCriteria dashboardCriteria) {
		return sampleDashboardService.getTestResultCountsByResultType(dashboardCriteria);
	}

	@Override
	public Long countSamplesForMap(SampleDashboardCriteria criteria, Set<SampleAssociationType> associationTypes) {
		return sampleDashboardService.countSamplesForMap(criteria, associationTypes);
	}

	@Override
	public Long countEnvironmentalSamplesForMap(SampleDashboardCriteria criteria) {
		return sampleDashboardService.countEnvironmentSamplesForMap(criteria);
	}

	@Override
	public List<MapSampleDto> getSamplesForMap(SampleDashboardCriteria criteria, Set<SampleAssociationType> associationTypes) {
		return sampleDashboardService.getSamplesForMap(criteria, associationTypes);
	}

	@Override
	public List<MapSampleDto> getEnvironmentalSamplesForMap(SampleDashboardCriteria criteria) {
		return sampleDashboardService.getEnvironmentalSamplesForMap(criteria);
	}

	@LocalBean
	@Stateless
	public static class SampleDashboardFacadeEjbLocal extends SampleDashboardFacadeEjb {

	}
}

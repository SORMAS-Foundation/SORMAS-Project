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

package de.symeda.sormas.backend.dashboard.adverseeventsfollowingimmunization;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.dashboard.AefiDashboardCriteria;
import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.AefiChartData;
import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.AefiDashboardFacade;
import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.MapAefiDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "AefiDashboardFacade")
@RightsAllowed(UserRight._DASHBOARD_ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_VIEW)
public class AefiDashboardFacadeEjb implements AefiDashboardFacade {

	@EJB
	private AefiDashboardService aefiDashboardService;

	@Override
	public Map<AefiType, Long> getAefiCountsByType(AefiDashboardCriteria dashboardCriteria) {
		return aefiDashboardService.getAefiCountsByType(dashboardCriteria);
	}

	@Override
	public Map<Vaccine, Map<AefiType, Long>> getAefiCountsByVaccine(AefiDashboardCriteria dashboardCriteria) {
		return aefiDashboardService.getAefiCountsByVaccine(dashboardCriteria);
	}

	@Override
	public AefiChartData getAefiByVaccineDoseChartData(AefiDashboardCriteria dashboardCriteria) {
		return aefiDashboardService.getAefiByVaccineDoseChartData(dashboardCriteria);
	}

	@Override
	public AefiChartData getAefiEventsByGenderChartData(AefiDashboardCriteria dashboardCriteria) {
		return aefiDashboardService.getAefiEventsByGenderChartData(dashboardCriteria);
	}

	@Override
	public Long countAefiForMap(AefiDashboardCriteria criteria) {
		return aefiDashboardService.countAefiForMap(criteria);
	}

	@Override
	public List<MapAefiDto> getAefiForMap(AefiDashboardCriteria criteria) {
		return aefiDashboardService.getAefiForMap(criteria);
	}

	@LocalBean
	@Stateless
	public static class AefiDashboardFacadeEjbLocal extends AefiDashboardFacadeEjb {

	}
}

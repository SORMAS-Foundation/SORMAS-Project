/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.dashboard.adverseeventsfollowingimmunization;

import java.util.Map;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDashboardFilterDateType;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.dashboard.AefiDashboardCriteria;
import de.symeda.sormas.api.dashboard.adverseeventsfollowingimmunization.AefiChartData;
import de.symeda.sormas.ui.dashboard.AbstractDashboardDataProvider;

public class AefiDashboardDataProvider extends AbstractDashboardDataProvider<AefiDashboardCriteria> {

	private AefiDashboardFilterDateType dateType = AefiDashboardFilterDateType.REPORT_DATE;

	private Map<AefiType, Long> aefiCountsByType;
	private Map<Vaccine, Map<AefiType, Long>> aefiCountsByVaccine;
	private AefiChartData aefiByVaccineDoseChartData;
	private AefiChartData aefiEventsByGenderChartData;

	@Override
	public void refreshData() {
		aefiCountsByType = FacadeProvider.getAefiDashboardFacade().getAefiCountsByType(buildDashboardCriteriaWithDates());
		aefiCountsByVaccine = FacadeProvider.getAefiDashboardFacade().getAefiCountsByVaccine(buildDashboardCriteriaWithDates());
		aefiByVaccineDoseChartData = FacadeProvider.getAefiDashboardFacade().getAefiByVaccineDoseChartData(buildDashboardCriteriaWithDates());
		aefiEventsByGenderChartData = FacadeProvider.getAefiDashboardFacade().getAefiEventsByGenderChartData(buildDashboardCriteriaWithDates());
	}

	@Override
	protected AefiDashboardCriteria newCriteria() {
		return new AefiDashboardCriteria();
	}

	@Override
	protected AefiDashboardCriteria buildDashboardCriteria() {
		return super.buildDashboardCriteria().aefiDashboardDateType(dateType);
	}

	public AefiDashboardFilterDateType getDateType() {
		return dateType;
	}

	public void setDateType(AefiDashboardFilterDateType dateType) {
		this.dateType = dateType;
	}

	public Map<AefiType, Long> getAefiCountsByType() {
		return aefiCountsByType;
	}

	public Map<Vaccine, Map<AefiType, Long>> getAefiCountsByVaccine() {
		return aefiCountsByVaccine;
	}

	public AefiChartData getAefiByVaccineDoseChartData() {
		return aefiByVaccineDoseChartData;
	}

	public AefiChartData getAefiEventsByGenderChartData() {
		return aefiEventsByGenderChartData;
	}
}

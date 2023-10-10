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

package de.symeda.sormas.api.dashboard.sample;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;

import de.symeda.sormas.api.dashboard.SampleDashboardCriteria;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SpecimenCondition;

@Remote
public interface SampleDashboardFacade {

	Map<PathogenTestResultType, Long> getSampleCountsByResultType(SampleDashboardCriteria dashboardCriteria);

	Map<SamplePurpose, Long> getSampleCountsByPurpose(SampleDashboardCriteria dashboardCriteria);

	Map<SpecimenCondition, Long> getSampleCountsBySpecimenCondition(SampleDashboardCriteria dashboardCriteria);

	Map<SampleShipmentStatus, Long> getSampleCountsByShipmentStatus(SampleDashboardCriteria dashboardCriteria);

	Map<PathogenTestResultType, Long> getTestResultCountsByResultType(SampleDashboardCriteria dashboardCriteria);

	Long countSamplesForMap(SampleDashboardCriteria criteria, Set<SampleAssociationType> associationTypes);

	Long countEnvironmentalSamplesForMap(SampleDashboardCriteria criteria);

	List<MapSampleDto> getSamplesForMap(SampleDashboardCriteria criteria, Set<SampleAssociationType> associationTypes);

	List<MapSampleDto> getEnvironmentalSamplesForMap(SampleDashboardCriteria criteria);
}

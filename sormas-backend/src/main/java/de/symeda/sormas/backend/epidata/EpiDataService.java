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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.epidata;

import java.sql.Timestamp;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.ChangeDateBuilder;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.exposure.Exposure;

@Stateless
@LocalBean
public class EpiDataService extends BaseAdoService<EpiData> {

	public EpiDataService() {
		super(EpiData.class);
	}

	public EpiData createEpiData() {

		EpiData epiData = new EpiData();
		epiData.setUuid(DataHelper.createUuid());
		return epiData;
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, EpiData> epiData, Timestamp date) {
		return addChangeDateFilters(new ChangeDateFilterBuilder(cb, date), epiData).build();
	}

	public ChangeDateFilterBuilder addChangeDateFilters(ChangeDateFilterBuilder filterBuilder, From<?, EpiData> epiData) {
		Join<EpiData, Exposure> exposures = epiData.join(EpiData.EXPOSURES, JoinType.LEFT);
		Join<EpiData, ActivityAsCase> activitiesAsCaseJoin = epiData.join(EpiData.ACTIVITIES_AS_CASE, JoinType.LEFT);

		return filterBuilder.add(epiData)
			.add(exposures)
			.add(exposures, Exposure.LOCATION)
			.add(activitiesAsCaseJoin)
			.add(activitiesAsCaseJoin, ActivityAsCase.LOCATION);
	}

	public <T extends ChangeDateBuilder<T>> T addChangeDates(T builder, From<?, EpiData> epiData) {

		Join<EpiData, Exposure> exposures = epiData.join(EpiData.EXPOSURES, JoinType.LEFT);
		Join<EpiData, ActivityAsCase> activitiesAsCaseJoin = epiData.join(EpiData.ACTIVITIES_AS_CASE, JoinType.LEFT);

		return builder.add(epiData)
			.add(exposures)
			.add(exposures, Exposure.LOCATION)
			.add(activitiesAsCaseJoin)
			.add(activitiesAsCaseJoin, ActivityAsCase.LOCATION);
	}
}

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.backend.climate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class ClimateObservationService extends BaseAdoService<ClimateObservation> {

	public ClimateObservationService() {
		super(ClimateObservation.class);
	}

	public List<ClimateObservation> getByLocation(ClimateLocation location, ClimateDataSource... sources) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClimateObservation> cq = cb.createQuery(ClimateObservation.class);
		Root<ClimateObservation> from = cq.from(ClimateObservation.class);

		Predicate filter = cb.equal(from.get(ClimateObservation.CLIMATE_LOCATION), location);
		if (sources.length > 0) {
			filter = cb.and(filter, from.get(ClimateObservation.SOURCE).in(Arrays.asList(sources)));
		}
		cq.where(filter);
		cq.orderBy(cb.asc(from.get(ClimateObservation.OBSERVATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * Copies the IMPORT and MANUAL rows of one location to another, for example when a district's point changes, because
	 * those rows describe the district rather than the old point. A row is copied when the target has no row that day, or
	 * only one of a lower-ranked source; the target's own data is never overwritten by something it outranks. The rows of
	 * {@code from} stay, so anything still sharing the old cell keeps them. OPENWEATHER rows are not copied: the new point
	 * gets its own. On a tie (IMPORT onto IMPORT, MANUAL onto MANUAL) the target's row stays and the day is counted as
	 * skipped.
	 *
	 * @return how many rows were created or replaced on {@code to}, and how many days were skipped
	 */
	public ClimateRowCopyResult copyExternalRows(ClimateLocation from, ClimateLocation to) {

		if (from == null || to == null || from.getId() == null || from.getId().equals(to.getId())) {
			return ClimateRowCopyResult.NOTHING;
		}

		List<ClimateObservation> sourceRows = getByLocation(from, ClimateDataSource.IMPORT, ClimateDataSource.MANUAL);
		if (sourceRows.isEmpty()) {
			return ClimateRowCopyResult.NOTHING;
		}

		Map<Long, ClimateObservation> targetRowsByDay = new HashMap<>();
		for (ClimateObservation targetRow : getByLocation(to)) {
			targetRowsByDay.put(targetRow.getObservationDate().getTime(), targetRow);
		}

		ClimateRowCopyResult result = new ClimateRowCopyResult();
		for (ClimateObservation sourceRow : sourceRows) {
			ClimateObservation targetRow = targetRowsByDay.get(sourceRow.getObservationDate().getTime());
			if (targetRow == null) {
				targetRow = new ClimateObservation();
				targetRow.setClimateLocation(to);
				targetRow.setObservationDate(sourceRow.getObservationDate());
				targetRow.copyValuesFrom(sourceRow);
				persist(targetRow);
				result.addCopied();
			} else if (sourceRow.getSource().outranks(targetRow.getSource())) {
				// managed entity: the change is written with the flush below
				targetRow.copyValuesFrom(sourceRow);
				result.addCopied();
			} else {
				result.addSkipped(sourceRow.getObservationDate());
			}
		}
		doFlush();
		return result;
	}
}

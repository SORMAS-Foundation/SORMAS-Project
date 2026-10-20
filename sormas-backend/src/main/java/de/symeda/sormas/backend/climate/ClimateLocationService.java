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

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.climate.ClimateGrid;
import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class ClimateLocationService extends BaseAdoService<ClimateLocation> {

	public ClimateLocationService() {
		super(ClimateLocation.class);
	}

	/**
	 * @return the location of the grid cell, or null when none has been created yet
	 */
	public ClimateLocation getByCell(double gridSize, int latIndex, int lonIndex) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClimateLocation> cq = cb.createQuery(ClimateLocation.class);
		Root<ClimateLocation> from = cq.from(ClimateLocation.class);
		cq.where(
			cb.equal(from.get(ClimateLocation.GRID_SIZE), gridSize),
			cb.equal(from.get(ClimateLocation.LAT_INDEX), latIndex),
			cb.equal(from.get(ClimateLocation.LON_INDEX), lonIndex));

		List<ClimateLocation> result = em.createQuery(cq).setMaxResults(1).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	/**
	 * @return the location of the grid cell the point falls in, created when it does not exist yet
	 */
	public ClimateLocation getOrCreate(double latitude, double longitude, double gridSize) {

		int latIndex = ClimateGrid.cellIndex(latitude, gridSize);
		int lonIndex = ClimateGrid.cellIndex(longitude, gridSize);

		ClimateLocation location = getByCell(gridSize, latIndex, lonIndex);
		if (location == null) {
			location = new ClimateLocation();
			location.setGridSize(gridSize);
			location.setLatIndex(latIndex);
			location.setLonIndex(lonIndex);
			location.setLatitude(ClimateGrid.cellCentre(latIndex, gridSize));
			location.setLongitude(ClimateGrid.cellCentre(lonIndex, gridSize));
			ensurePersisted(location);
		}
		return location;
	}

	/**
	 * @return a new location without coordinates, for a single district that has no point
	 */
	public ClimateLocation createWithoutCoordinates() {

		ClimateLocation location = new ClimateLocation();
		ensurePersisted(location);
		return location;
	}
}

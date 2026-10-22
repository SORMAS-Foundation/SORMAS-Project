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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;

import de.symeda.sormas.api.climate.ClimateGrid;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.location.Location;

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
			assignCell(location, latitude, longitude, gridSize);
		}
		return location;
	}

	/**
	 * Gives a location the grid cell of a point, for a new location or one without coordinates. The caller makes sure the
	 * cell has no location yet: a cell has at most one location per grid size.
	 */
	public void assignCell(ClimateLocation location, double latitude, double longitude, double gridSize) {

		int latIndex = ClimateGrid.cellIndex(latitude, gridSize);
		int lonIndex = ClimateGrid.cellIndex(longitude, gridSize);

		location.setGridSize(gridSize);
		location.setLatIndex(latIndex);
		location.setLonIndex(lonIndex);
		location.setLatitude(ClimateGrid.cellCentre(latIndex, gridSize));
		location.setLongitude(ClimateGrid.cellCentre(lonIndex, gridSize));
		ensurePersisted(location);
	}

	/**
	 * @return the ids of locations with coordinates that districts in the scope link to, archived districts left out, in id
	 *         order. No region and no district means every district.
	 */
	public List<Long> getDistrictLocationIds(Long regionId, Long districtId) {

		String scope = districtId != null ? " AND d.id = :scope" : regionId != null ? " AND d." + District.REGION + ".id = :scope" : "";
		TypedQuery<Long> query = em.createQuery(
			"SELECT DISTINCT l.id FROM District d JOIN d." + District.CLIMATE_LOCATION + " l WHERE d." + District.ARCHIVED + " = false AND l."
				+ ClimateLocation.LATITUDE + " IS NOT NULL" + scope + " ORDER BY l.id",
			Long.class);
		return withScope(query, regionId, districtId).getResultList();
	}

	/**
	 * @return the ids of locations with coordinates that environments in the scope link to, archived and deleted
	 *         environments left out, in id order. An environment is in the scope of its own district and region.
	 */
	public List<Long> getEnvironmentLocationIds(Long regionId, Long districtId) {

		String scope = districtId != null
			? " AND e." + Environment.LOCATION + "." + Location.DISTRICT + ".id = :scope"
			: regionId != null ? " AND e." + Environment.LOCATION + "." + Location.REGION + ".id = :scope" : "";
		TypedQuery<Long> query = em.createQuery(
			"SELECT DISTINCT l.id FROM environments e JOIN e." + Environment.CLIMATE_LOCATION + " l WHERE e." + Environment.ARCHIVED
				+ " = false AND e." + Environment.DELETED + " = false AND l." + ClimateLocation.LATITUDE + " IS NOT NULL" + scope + " ORDER BY l.id",
			Long.class);
		return withScope(query, regionId, districtId).getResultList();
	}

	/**
	 * @return true when a district that is not archived, or an environment that is neither archived nor deleted, links to
	 *         the location
	 */
	public boolean isInUse(Long locationId) {

		boolean byDistrict = !em
			.createQuery(
				"SELECT d.id FROM District d WHERE d." + District.CLIMATE_LOCATION + ".id = :location AND d." + District.ARCHIVED + " = false",
				Long.class)
			.setParameter("location", locationId)
			.setMaxResults(1)
			.getResultList()
			.isEmpty();
		return byDistrict
			|| !em
				.createQuery(
					"SELECT e.id FROM environments e WHERE e." + Environment.CLIMATE_LOCATION + ".id = :location AND e." + Environment.ARCHIVED
						+ " = false AND e." + Environment.DELETED + " = false",
					Long.class)
				.setParameter("location", locationId)
				.setMaxResults(1)
				.getResultList()
				.isEmpty();
	}

	private static TypedQuery<Long> withScope(TypedQuery<Long> query, Long regionId, Long districtId) {
		if (districtId != null) {
			query.setParameter("scope", districtId);
		} else if (regionId != null) {
			query.setParameter("scope", regionId);
		}
		return query;
	}

	/**
	 * Deletes the locations that nothing refers to: no district, environment, observation, job or anything else, archived
	 * and deleted records included. A location that holds data is therefore never deleted.
	 * <p>
	 * The tables that can refer to a location are not listed here: every entity with a relation to {@link ClimateLocation}
	 * found in the JPA metamodel is checked, so a link added later is covered without changing this method. A link must
	 * therefore be a JPA relation (a foreign key), never a bare id. If a reference was still missed, its foreign key makes
	 * the delete fail, and nothing is deleted.
	 *
	 * @return the number of locations deleted
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int deleteUnreferenced() {

		StringBuilder jpql = new StringBuilder("SELECT l.id FROM " + ClimateLocation.TABLE_NAME + " l WHERE 1 = 1");
		for (EntityType<?> entity : em.getMetamodel().getEntities()) {
			if (entity.getJavaType() == ClimateLocation.class) {
				continue;
			}
			for (Attribute<?, ?> attribute : entity.getAttributes()) {
				if (attribute instanceof PluralAttribute) {
					if (((PluralAttribute<?, ?, ?>) attribute).getElementType().getJavaType() == ClimateLocation.class) {
						jpql.append(" AND NOT EXISTS (SELECT 1 FROM ")
							.append(entity.getName())
							.append(" x JOIN x.")
							.append(attribute.getName())
							.append(" y WHERE y = l)");
					}
				} else if (attribute.getJavaType() == ClimateLocation.class) {
					jpql.append(" AND NOT EXISTS (SELECT 1 FROM ")
						.append(entity.getName())
						.append(" x WHERE x.")
						.append(attribute.getName())
						.append(" = l)");
				}
			}
		}

		List<Long> unreferenced = em.createQuery(jpql.toString(), Long.class).getResultList();
		if (unreferenced.isEmpty()) {
			return 0;
		}
		return em.createQuery("DELETE FROM " + ClimateLocation.TABLE_NAME + " l WHERE l.id IN :ids")
			.setParameter("ids", unreferenced)
			.executeUpdate();
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

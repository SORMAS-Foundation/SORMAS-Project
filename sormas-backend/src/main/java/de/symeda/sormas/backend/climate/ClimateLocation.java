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

import javax.persistence.Entity;
import javax.persistence.Transient;

import de.symeda.sormas.backend.common.AbstractDomainObject;

/**
 * One weather point that districts and environments link to. Points in the same grid cell (same grid size and cell
 * indexes) share one location. A location without coordinates belongs to a single district that has no point; it is
 * never shared and never fetched, and only imports and manual entries fill it.
 * <p>
 * Whether a location is in use, its first and last day and its last update are worked out by query, not stored.
 */
@Entity(name = ClimateLocation.TABLE_NAME)
public class ClimateLocation extends AbstractDomainObject {

	private static final long serialVersionUID = 2873464170458431210L;

	public static final String TABLE_NAME = "climatelocation";

	public static final String GRID_SIZE = "gridSize";
	public static final String LAT_INDEX = "latIndex";
	public static final String LON_INDEX = "lonIndex";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";

	private Double gridSize;
	private Integer latIndex;
	private Integer lonIndex;
	private Double latitude;
	private Double longitude;

	public Double getGridSize() {
		return gridSize;
	}

	public void setGridSize(Double gridSize) {
		this.gridSize = gridSize;
	}

	public Integer getLatIndex() {
		return latIndex;
	}

	public void setLatIndex(Integer latIndex) {
		this.latIndex = latIndex;
	}

	public Integer getLonIndex() {
		return lonIndex;
	}

	public void setLonIndex(Integer lonIndex) {
		this.lonIndex = lonIndex;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Transient
	public boolean hasCoordinates() {
		return latitude != null && longitude != null;
	}
}

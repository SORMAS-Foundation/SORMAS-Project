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

package de.symeda.sormas.api.climate;

/**
 * The grid that climate points are snapped to. Points in the same grid cell share one climate location, so their weather
 * is stored and fetched once. A cell is identified by whole-number indexes (coordinate ÷ grid size, rounded), which avoids
 * comparing decimals that can differ in the last digit.
 */
public final class ClimateGrid {

	/** Grid size in degrees used when the CLIMATE_GRID_SIZE setting is missing or invalid (about 5.5 km). */
	public static final double DEFAULT_GRID_SIZE = 0.05;

	/** Grid sizes offered by the CLIMATE_GRID_SIZE setting, in degrees. */
	public static final double[] ALLOWED_GRID_SIZES = {
		0.01,
		0.025,
		0.05,
		0.1,
		0.25 };

	private ClimateGrid() {
	}

	/**
	 * GPS is usable for climate data when both values are present and in range, and the point is not 0,0 (a common
	 * placeholder for "unknown").
	 */
	public static boolean isUsableGps(Double latitude, Double longitude) {

		if (latitude == null || longitude == null || latitude.isNaN() || longitude.isNaN()) {
			return false;
		}
		if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
			return false;
		}
		return latitude != 0 || longitude != 0;
	}

	public static int cellIndex(double coordinate, double gridSize) {
		return (int) Math.round(coordinate / gridSize);
	}

	/**
	 * @return the cell centre for a cell index, rounded to 6 decimals so that the same cell always gets the same centre.
	 */
	public static double cellCentre(int cellIndex, double gridSize) {
		return Math.round(cellIndex * gridSize * 1_000_000d) / 1_000_000d;
	}

	/**
	 * @return the configured grid size when it is one of {@link #ALLOWED_GRID_SIZES}, otherwise {@link #DEFAULT_GRID_SIZE}.
	 */
	public static double parseGridSize(String value) {

		if (value != null) {
			try {
				double size = Double.parseDouble(value.trim());
				for (double allowed : ALLOWED_GRID_SIZES) {
					if (Double.compare(allowed, size) == 0) {
						return size;
					}
				}
			} catch (NumberFormatException e) {
				// fall through to the default
			}
		}
		return DEFAULT_GRID_SIZE;
	}
}

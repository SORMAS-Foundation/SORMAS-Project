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

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.climate.ClimateGrid;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.geo.GeoShapeProviderEjb.GeoShapeProviderEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.UserService;

/**
 * Links districts and environments to the climate location their climate data is stored on. Linking is idempotent: a
 * district or environment already linked to the right location is left alone, so every method can be run again safely
 * (on save, when the climate feature is switched on, nightly before the fetch, and after a grid size change).
 */
@Stateless
@LocalBean
public class ClimateLinkingService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ClimateLocationService climateLocationService;
	@EJB
	private ClimateObservationService climateObservationService;
	@EJB
	private ClimateJobService climateJobService;
	@EJB
	private DistrictService districtService;
	@EJB
	private EnvironmentService environmentService;
	@EJB
	private GeoShapeProviderEjbLocal geoShapeProvider;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private SystemConfigurationValueFacade systemConfigurationValueFacade;
	@EJB
	private UserService userService;

	public boolean isClimateEnabled() {
		return featureConfigurationFacade.isFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT);
	}

	public double getGridSize() {
		return ClimateGrid.parseGridSize(systemConfigurationValueFacade.getValue(ClimateConfiguration.GRID_SIZE));
	}

	/**
	 * @return the point a district's climate data is fetched for: its climate reference point when usable, else its
	 *         shapefile centre when the district has a shape, else null. {@code getCenterOfDistrict} on its own would fall
	 *         back to the centre of the whole country, which must not be used for a district.
	 */
	public GeoLatLon resolveDistrictPoint(District district) {

		if (ClimateGrid.isUsableGps(district.getClimateLatitude(), district.getClimateLongitude())) {
			return new GeoLatLon(district.getClimateLatitude(), district.getClimateLongitude());
		}

		DistrictReferenceDto districtReference = DistrictFacadeEjb.toReferenceDto(district);
		if (geoShapeProvider.getDistrictShape(districtReference) != null) {
			GeoLatLon centre = geoShapeProvider.getCenterOfDistrict(districtReference);
			if (centre != null && ClimateGrid.isUsableGps(centre.getLat(), centre.getLon())) {
				return centre;
			}
		}
		return null;
	}

	/**
	 * Links the district to the location for its point, or to a location without coordinates when it has none.
	 * <p>
	 * When a district whose location has no coordinates gets a point and nothing else is in that point's cell yet, the same
	 * location is given the cell, so its imported and manual rows stay where they are and no empty location is left
	 * behind. (A location without coordinates belongs to this district alone.) Otherwise, when the location changes, the
	 * district's IMPORT and MANUAL rows are copied to the new location.
	 *
	 * @return true when the district's location changed, or its location without coordinates was given a cell
	 */
	public boolean linkDistrict(District district) {
		return relinkDistrict(district) != null;
	}

	/**
	 * @return null when the district stays on its location; otherwise what was copied to the new location (empty when
	 *         nothing was copied or the location was given a cell in place)
	 */
	private ClimateRowCopyResult relinkDistrict(District district) {

		ClimateLocation current = district.getClimateLocation();
		GeoLatLon point = resolveDistrictPoint(district);

		ClimateLocation target;
		if (point != null) {
			double gridSize = getGridSize();
			ClimateLocation cellLocation = climateLocationService
				.getByCell(gridSize, ClimateGrid.cellIndex(point.getLat(), gridSize), ClimateGrid.cellIndex(point.getLon(), gridSize));

			if (cellLocation == null && current != null && !current.hasCoordinates()) {
				climateLocationService.assignCell(current, point.getLat(), point.getLon(), gridSize);
				climateJobService.queueTopUp(current, LocalDate.now());
				return ClimateRowCopyResult.NOTHING;
			}
			target = cellLocation != null ? cellLocation : climateLocationService.getOrCreate(point.getLat(), point.getLon(), gridSize);
		} else if (current != null && !current.hasCoordinates()) {
			target = current;
		} else {
			target = climateLocationService.createWithoutCoordinates();
		}

		if (current != null && current.getId().equals(target.getId())) {
			return null;
		}

		ClimateRowCopyResult copyResult = climateObservationService.copyExternalRows(current, target);
		if (!copyResult.isEmpty()) {
			logMove(district, target, copyResult);
			climateJobService.recordRelink(district, current, target, copyResult, userService.getCurrentUser());
		}
		district.setClimateLocation(target);
		districtService.ensurePersisted(district);
		if (!isGridSizeChange(current)) {
			climateJobService.queueTopUp(target, LocalDate.now());
		}
		return copyResult;
	}

	/**
	 * A move away from a location made with another grid size is part of relinking everything after a grid size change.
	 * Such moves get no automatic top-up: refilling every location is a deployment-wide backfill, which an admin starts
	 * after confirming the calls.
	 */
	private boolean isGridSizeChange(ClimateLocation current) {
		return current != null && current.hasCoordinates() && Double.compare(current.getGridSize(), getGridSize()) != 0;
	}

	/**
	 * One line per district move, never one per row. The skipped days stay on the old location, so nothing is lost.
	 */
	private void logMove(District district, ClimateLocation target, ClimateRowCopyResult copyResult) {

		if (copyResult.getSkipped() == 0) {
			logger.info(
				"District {} ({}) moved to climate location {}: {} imported/manual rows copied",
				district.getName(),
				district.getUuid(),
				target.getId(),
				copyResult.getCopied());
		} else {
			// SimpleDateFormat is not thread-safe, so one per call
			SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
			logger.warn(
				"District {} ({}) moved to climate location {}: {} imported/manual rows copied, {} skipped because the cell already had equal or better data ({} to {})",
				district.getName(),
				district.getUuid(),
				target.getId(),
				copyResult.getCopied(),
				copyResult.getSkipped(),
				DateHelper.formatLocalDate(copyResult.getFirstSkippedDay(), dayFormat),
				DateHelper.formatLocalDate(copyResult.getLastSkippedDay(), dayFormat));
		}
	}

	/**
	 * Links the environment to the location for its GPS, or unlinks it when the GPS is not usable (its district's climate
	 * data is then shown). The old location keeps its data.
	 *
	 * @return true when the environment's location changed
	 */
	public boolean linkEnvironment(Environment environment) {

		Location location = environment.getLocation();
		ClimateLocation target = ClimateGrid.isUsableGps(location.getLatitude(), location.getLongitude())
			? climateLocationService.getOrCreate(location.getLatitude(), location.getLongitude(), getGridSize())
			: null;

		ClimateLocation current = environment.getClimateLocation();
		if (current == null ? target == null : target != null && current.getId().equals(target.getId())) {
			return false;
		}

		// the site's imported and manual values describe the site, so they move with it, as a district's do (decision 132);
		// without usable GPS the tab shows the district's climate and nothing is copied
		if (current != null && target != null) {
			ClimateRowCopyResult copyResult = climateObservationService.copyExternalRows(current, target);
			if (!copyResult.isEmpty()) {
				logger.info(
					"Environment {} moved to climate location {}: {} imported/manual rows copied, {} days skipped because the cell already had equal or better data",
					environment.getUuid(),
					target.getId(),
					copyResult.getCopied(),
					copyResult.getSkipped());
			}
		}
		environment.setClimateLocation(target);
		environmentService.ensurePersisted(environment);
		if (!isGridSizeChange(current)) {
			climateJobService.queueTopUp(target, LocalDate.now());
		}
		return true;
	}

	/**
	 * Links every district and logs one summary line when any of them moved.
	 *
	 * @return the number of districts whose location changed
	 */
	public int linkAllDistricts() {

		int changed = 0;
		int withRowsCopied = 0;
		int withDaysSkipped = 0;
		for (District district : districtService.getAll()) {
			ClimateRowCopyResult result = relinkDistrict(district);
			if (result != null) {
				changed++;
				if (result.getCopied() > 0) {
					withRowsCopied++;
				}
				if (result.getSkipped() > 0) {
					withDaysSkipped++;
				}
			}
		}

		if (changed > 0) {
			logger.info(
				"Relinked {} districts to climate locations: {} had imported/manual rows copied, {} had days skipped",
				changed,
				withRowsCopied,
				withDaysSkipped);
		}
		return changed;
	}

	/**
	 * Links every environment that is not deleted.
	 *
	 * @return the number of environments whose location changed
	 */
	public int linkAllEnvironments() {

		int changed = 0;
		for (Environment environment : environmentService.getAll()) {
			if (!environment.isDeleted() && linkEnvironment(environment)) {
				changed++;
			}
		}
		return changed;
	}
}

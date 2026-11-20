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

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;

/**
 * What the environment Climate data tab's context strip says: which point the data belongs to, who shares it, how far
 * back it goes, and the warnings (no usable GPS, point outside the district, archived, history still loading). It does
 * not change with the period shown.
 */
@AuditedClass
public class ClimateEnvironmentContextDto implements Serializable {

	private static final long serialVersionUID = -1946220173830436152L;

	private EnvironmentReferenceDto environment;
	private Double latitude;
	private Double longitude;
	private boolean ownLocation;
	private Double cellLatitude;
	private Double cellLongitude;
	private DistrictReferenceDto district;
	private boolean districtHasPoint;
	private List<String> sharedDistricts = new ArrayList<>();
	private List<String> sharedEnvironments = new ArrayList<>();
	private boolean outsideDistrict;
	private DistrictReferenceDto districtAtPoint;
	private Date reportDate;
	private boolean archived;
	private boolean deleted;
	private boolean stillUpdated;
	private LocalDate latestDay;
	private LocalDate historyFrom;
	private ClimateJobType loadingJobType;
	private int loadingLocationsDone;
	private int loadingLocationsTotal;

	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	public void setEnvironment(EnvironmentReferenceDto environment) {
		this.environment = environment;
	}

	/**
	 * @return the environment's GPS latitude as entered, or null
	 */
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

	/**
	 * @return true when the environment has usable GPS and so its own climate location; false in the district fallback
	 */
	public boolean isOwnLocation() {
		return ownLocation;
	}

	public void setOwnLocation(boolean ownLocation) {
		this.ownLocation = ownLocation;
	}

	/**
	 * @return the centre of the grid cell whose weather is shown (own location, or the district's), or null without
	 *         coordinates
	 */
	public Double getCellLatitude() {
		return cellLatitude;
	}

	public void setCellLatitude(Double cellLatitude) {
		this.cellLatitude = cellLatitude;
	}

	public Double getCellLongitude() {
		return cellLongitude;
	}

	public void setCellLongitude(Double cellLongitude) {
		this.cellLongitude = cellLongitude;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	/**
	 * @return in the district fallback: whether the district's climate location has coordinates (otherwise only imports
	 *         and manual entries fill it)
	 */
	public boolean isDistrictHasPoint() {
		return districtHasPoint;
	}

	public void setDistrictHasPoint(boolean districtHasPoint) {
		this.districtHasPoint = districtHasPoint;
	}

	/**
	 * @return with its own location: the districts (not archived) that share it, sorted by name
	 */
	public List<String> getSharedDistricts() {
		return sharedDistricts;
	}

	/**
	 * @return with its own location: the other environments (not deleted) that share it, sorted by name
	 */
	public List<String> getSharedEnvironments() {
		return sharedEnvironments;
	}

	public boolean isShared() {
		return !sharedDistricts.isEmpty() || !sharedEnvironments.isEmpty();
	}

	/**
	 * @return true when district shapes are loaded and the GPS point does not fall in the environment's district
	 */
	public boolean isOutsideDistrict() {
		return outsideDistrict;
	}

	public void setOutsideDistrict(boolean outsideDistrict) {
		this.outsideDistrict = outsideDistrict;
	}

	/**
	 * @return when {@link #isOutsideDistrict()}: the district the point falls in, or null when it falls in none
	 */
	public DistrictReferenceDto getDistrictAtPoint() {
		return districtAtPoint;
	}

	public void setDistrictAtPoint(DistrictReferenceDto districtAtPoint) {
		this.districtAtPoint = districtAtPoint;
	}

	/**
	 * @return the environment's report date, marked on the chart
	 */
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return true when the location shown is still in use (a district that is not archived, or a live environment, links to
	 *         it), so it is still fetched; used for archived and deleted environments
	 */
	public boolean isStillUpdated() {
		return stillUpdated;
	}

	public void setStillUpdated(boolean stillUpdated) {
		this.stillUpdated = stillUpdated;
	}

	/**
	 * @return the latest day up to yesterday that the location holds, or null without data
	 */
	public LocalDate getLatestDay() {
		return latestDay;
	}

	public void setLatestDay(LocalDate latestDay) {
		this.latestDay = latestDay;
	}

	/**
	 * @return the earliest day the location holds, or null without data
	 */
	public LocalDate getHistoryFrom() {
		return historyFrom;
	}

	public void setHistoryFrom(LocalDate historyFrom) {
		this.historyFrom = historyFrom;
	}

	/**
	 * @return the type of an open job that fills the location's history (TOP_UP, BACKFILL or FETCH), or null when none is
	 *         open
	 */
	public ClimateJobType getLoadingJobType() {
		return loadingJobType;
	}

	public void setLoadingJobType(ClimateJobType loadingJobType) {
		this.loadingJobType = loadingJobType;
	}

	public int getLoadingLocationsDone() {
		return loadingLocationsDone;
	}

	public void setLoadingLocationsDone(int loadingLocationsDone) {
		this.loadingLocationsDone = loadingLocationsDone;
	}

	public int getLoadingLocationsTotal() {
		return loadingLocationsTotal;
	}

	public void setLoadingLocationsTotal(int loadingLocationsTotal) {
		this.loadingLocationsTotal = loadingLocationsTotal;
	}
}

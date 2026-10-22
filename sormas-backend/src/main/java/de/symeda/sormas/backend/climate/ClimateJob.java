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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;

/**
 * One fetch, backfill or top-up run, or the record of a district relink. The locations a job covers are worked out when
 * it runs, from its scope; the cursor fields only say how far it got, so it can resume after a restart or a pause.
 * <p>
 * Scope: region, district and climate location all empty means every district; a region means its districts; a district
 * means that district; a climate location means that one location (TOP_UP). For RELINK, the district moved from
 * {@link #getFromClimateLocation()} to {@link #getClimateLocation()}.
 * <p>
 * Job rows have no history table: they change once per location as the job progresses.
 */
@Entity(name = ClimateJob.TABLE_NAME)
public class ClimateJob extends AbstractDomainObject {

	private static final long serialVersionUID = 6390451780260315243L;

	public static final String TABLE_NAME = "climatejob";

	public static final String JOB_TYPE = "jobType";
	public static final String STATUS = "status";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String CLIMATE_LOCATION = "climateLocation";
	public static final String FROM_CLIMATE_LOCATION = "fromClimateLocation";
	public static final String FINISHED_AT = "finishedAt";

	private ClimateJobType jobType;
	private ClimateJobStatus status;
	private Region region;
	private District district;
	private ClimateLocation climateLocation;
	private ClimateLocation fromClimateLocation;
	private boolean includeEnvironments;
	private Date fromDate;
	private Date toDate;
	private int locationsTotal;
	private int locationsDone;
	private int locationsFailed;
	private Integer estimatedCalls;
	private int callsMade;
	private int rowsAdded;
	private int rowsUpdated;
	private int rowsSkipped;
	private ClimateLocationGroup cursorGroup;
	private Long cursorLocationId;
	private Date cursorDate;
	private Long rotationStartLocationId;
	private String lastError;
	private User startedBy;
	private Date startedAt;
	private Date pausedAt;
	private Date finishedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ClimateJobType getJobType() {
		return jobType;
	}

	public void setJobType(ClimateJobType jobType) {
		this.jobType = jobType;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ClimateJobStatus getStatus() {
		return status;
	}

	public void setStatus(ClimateJobStatus status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public ClimateLocation getClimateLocation() {
		return climateLocation;
	}

	public void setClimateLocation(ClimateLocation climateLocation) {
		this.climateLocation = climateLocation;
	}

	/**
	 * RELINK only: the location the district moved away from.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public ClimateLocation getFromClimateLocation() {
		return fromClimateLocation;
	}

	public void setFromClimateLocation(ClimateLocation fromClimateLocation) {
		this.fromClimateLocation = fromClimateLocation;
	}

	/**
	 * BACKFILL and FETCH: also the locations of the environments in the scope.
	 */
	@Column(nullable = false)
	public boolean isIncludeEnvironments() {
		return includeEnvironments;
	}

	public void setIncludeEnvironments(boolean includeEnvironments) {
		this.includeEnvironments = includeEnvironments;
	}

	/**
	 * The first day to fill; for RELINK the first skipped day.
	 */
	@Temporal(TemporalType.DATE)
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * The last day to fill; for RELINK the last skipped day.
	 */
	@Temporal(TemporalType.DATE)
	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Column(nullable = false)
	public int getLocationsTotal() {
		return locationsTotal;
	}

	public void setLocationsTotal(int locationsTotal) {
		this.locationsTotal = locationsTotal;
	}

	@Column(nullable = false)
	public int getLocationsDone() {
		return locationsDone;
	}

	public void setLocationsDone(int locationsDone) {
		this.locationsDone = locationsDone;
	}

	/**
	 * Locations skipped because of an error for that location only (for example a refused point).
	 */
	@Column(nullable = false)
	public int getLocationsFailed() {
		return locationsFailed;
	}

	public void setLocationsFailed(int locationsFailed) {
		this.locationsFailed = locationsFailed;
	}

	/**
	 * BACKFILL: the number of calls the user confirmed.
	 */
	public Integer getEstimatedCalls() {
		return estimatedCalls;
	}

	public void setEstimatedCalls(Integer estimatedCalls) {
		this.estimatedCalls = estimatedCalls;
	}

	/**
	 * Every request sent, retries included.
	 */
	@Column(nullable = false)
	public int getCallsMade() {
		return callsMade;
	}

	public void setCallsMade(int callsMade) {
		this.callsMade = callsMade;
	}

	/**
	 * Rows created; for RELINK the rows copied to the new location.
	 */
	@Column(nullable = false)
	public int getRowsAdded() {
		return rowsAdded;
	}

	public void setRowsAdded(int rowsAdded) {
		this.rowsAdded = rowsAdded;
	}

	/**
	 * OPENWEATHER rows whose values changed on a re-fetch.
	 */
	@Column(nullable = false)
	public int getRowsUpdated() {
		return rowsUpdated;
	}

	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}

	/**
	 * RELINK only: days not copied because the new location already had a row of equal or higher rank.
	 */
	@Column(nullable = false)
	public int getRowsSkipped() {
		return rowsSkipped;
	}

	public void setRowsSkipped(int rowsSkipped) {
		this.rowsSkipped = rowsSkipped;
	}

	/**
	 * The group the job is working through; empty before it starts.
	 */
	@Enumerated(EnumType.STRING)
	public ClimateLocationGroup getCursorGroup() {
		return cursorGroup;
	}

	public void setCursorGroup(ClimateLocationGroup cursorGroup) {
		this.cursorGroup = cursorGroup;
	}

	/**
	 * The id of the last location finished in {@link #getCursorGroup()}. A position in the order the locations are worked
	 * through, not a link: the location may have been deleted since.
	 */
	public Long getCursorLocationId() {
		return cursorLocationId;
	}

	public void setCursorLocationId(Long cursorLocationId) {
		this.cursorLocationId = cursorLocationId;
	}

	/**
	 * History jobs: the first day still to look at in the location after {@link #getCursorLocationId()}, when a batch ended
	 * in the middle of that location.
	 */
	@Temporal(TemporalType.DATE)
	public Date getCursorDate() {
		return cursorDate;
	}

	public void setCursorDate(Date cursorDate) {
		this.cursorDate = cursorDate;
	}

	/**
	 * DAILY_FETCH: locations used only by environments are worked through starting after this id, so that they take turns
	 * from night to night when the daily limit is short. A position, not a link.
	 */
	public Long getRotationStartLocationId() {
		return rotationStartLocationId;
	}

	public void setRotationStartLocationId(Long rotationStartLocationId) {
		this.rotationStartLocationId = rotationStartLocationId;
	}

	@Column(columnDefinition = "text")
	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	/**
	 * Empty for scheduled and automatic jobs and for system relinks.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public User getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(User startedBy) {
		this.startedBy = startedBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getPausedAt() {
		return pausedAt;
	}

	public void setPausedAt(Date pausedAt) {
		this.pausedAt = pausedAt;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}
}

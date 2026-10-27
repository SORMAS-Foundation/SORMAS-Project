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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.uuid.HasUuid;

/**
 * One climate job (a fetch, backfill, top-up or district relink) as the Climate data screen lists it.
 */
@AuditedClass
public class ClimateJobIndexDto implements Serializable, HasUuid {

	private static final long serialVersionUID = -2004873524155563208L;

	public static final String I18N_PREFIX = "ClimateJob";

	public static final String CREATION_DATE = "creationDate";
	public static final String JOB_TYPE = "jobType";
	public static final String SCOPE = "scope";
	public static final String STATUS = "status";
	public static final String PERIOD = "period";
	public static final String LOCATIONS = "locations";
	public static final String CALLS = "calls";
	public static final String ROWS = "rows";
	public static final String STARTED_BY = "startedBy";
	public static final String FINISHED_AT = "finishedAt";

	@AuditIncludeProperty
	private String uuid;
	private ClimateJobType jobType;
	private ClimateJobStatus status;
	private String regionName;
	private String districtName;
	private List<String> placeNames = new ArrayList<>();
	private Double latitude;
	private Double longitude;
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
	private String lastError;
	private String startedBy;
	private Date creationDate;
	private Date startedAt;
	private Date finishedAt;

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ClimateJobType getJobType() {
		return jobType;
	}

	public void setJobType(ClimateJobType jobType) {
		this.jobType = jobType;
	}

	public ClimateJobStatus getStatus() {
		return status;
	}

	public void setStatus(ClimateJobStatus status) {
		this.status = status;
	}

	/**
	 * @return the region of the scope; with no region and no district the job covers every district
	 */
	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	/**
	 * @return the district of the scope; for a relink, the district that moved
	 */
	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	/**
	 * @return for a top-up, the districts and environments that use its location
	 */
	public List<String> getPlaceNames() {
		return placeNames;
	}

	public void setPlaceNames(List<String> placeNames) {
		this.placeNames = placeNames;
	}

	/**
	 * @return for a top-up or relink, the latitude of the job's location
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

	public boolean isIncludeEnvironments() {
		return includeEnvironments;
	}

	public void setIncludeEnvironments(boolean includeEnvironments) {
		this.includeEnvironments = includeEnvironments;
	}

	/**
	 * @return the first day to fill; for a relink, the first day not copied
	 */
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public int getLocationsTotal() {
		return locationsTotal;
	}

	public void setLocationsTotal(int locationsTotal) {
		this.locationsTotal = locationsTotal;
	}

	public int getLocationsDone() {
		return locationsDone;
	}

	public void setLocationsDone(int locationsDone) {
		this.locationsDone = locationsDone;
	}

	public int getLocationsFailed() {
		return locationsFailed;
	}

	public void setLocationsFailed(int locationsFailed) {
		this.locationsFailed = locationsFailed;
	}

	/**
	 * @return the calls the user confirmed when starting a backfill; null for other jobs
	 */
	public Integer getEstimatedCalls() {
		return estimatedCalls;
	}

	public void setEstimatedCalls(Integer estimatedCalls) {
		this.estimatedCalls = estimatedCalls;
	}

	/**
	 * @return every call this job made, retries included, over all the days it ran
	 */
	public int getCallsMade() {
		return callsMade;
	}

	public void setCallsMade(int callsMade) {
		this.callsMade = callsMade;
	}

	/**
	 * @return rows added; for a relink, rows copied to the new location
	 */
	public int getRowsAdded() {
		return rowsAdded;
	}

	public void setRowsAdded(int rowsAdded) {
		this.rowsAdded = rowsAdded;
	}

	public int getRowsUpdated() {
		return rowsUpdated;
	}

	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}

	/**
	 * @return for a relink, the days not copied because the new location already had equal or better data
	 */
	public int getRowsSkipped() {
		return rowsSkipped;
	}

	public void setRowsSkipped(int rowsSkipped) {
		this.rowsSkipped = rowsSkipped;
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	/**
	 * @return the user name of who started the job; null for scheduled and automatic jobs
	 */
	public String getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

	public Date getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Date finishedAt) {
		this.finishedAt = finishedAt;
	}

	/**
	 * @return true when {@link ClimateJobFacade#cancel} accepts the job
	 */
	public boolean isCancellable() {
		return status != null && status.isOpen();
	}

	/**
	 * @return true when {@link ClimateJobFacade#startAgain} accepts the job
	 */
	public boolean isResumable() {
		return (jobType == ClimateJobType.BACKFILL || jobType == ClimateJobType.TOP_UP || jobType == ClimateJobType.FETCH)
			&& (status == ClimateJobStatus.PAUSED_AT_LIMIT || status == ClimateJobStatus.FAILED || status == ClimateJobStatus.CANCELLED);
	}
}

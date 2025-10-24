/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.epipulse;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epipulse.EpipulseExportStatus;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.common.NotExposedToApi;
import de.symeda.sormas.backend.user.User;

@Entity(name = "epipulse_export")
public class EpipulseExport extends DeletableAdo {

	private static final long serialVersionUID = -1295083699393414918L;

	public static final String TABLE_NAME = "epipulse_export";

	public static final String DISEASE = "disease";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String STATUS = "status";
	public static final String STATUS_CHANGE_DATE = "statusChangeDate";
	public static final String TOTAL_RECORDS = "totalRecords";
	public static final String EXPORT_FILE_NAME = "exportFileName";
	public static final String EXPORT_FILE_SIZE = "exportFileSize";
	public static final String CREATION_USER = "creationUser";
	public static final String ARCHIVED = "archived";

	private Disease disease;

	private Date startDate;
	private Date endDate;

	private EpipulseExportStatus status;

	private Date statusChangeDate;
	private Long totalRecords;
	private String exportFileName;
	private BigDecimal exportFileSize;

	@NotExposedToApi
	private boolean archived;

	@NotExposedToApi
	private String archiveUndoneReason;

	private User creationUser;

	@Enumerated(EnumType.STRING)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Column(name = "start_date")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Enumerated(EnumType.STRING)
	public EpipulseExportStatus getStatus() {
		return status;
	}

	public void setStatus(EpipulseExportStatus status) {
		this.status = status;
	}

	@Column(name = "status_change_date")
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}

	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	@Column(name = "total_records")
	public Long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}

	@Column(name = "export_file_name")
	public String getExportFileName() {
		return exportFileName;
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}

	@Column(name = "export_file_size")
	public BigDecimal getExportFileSize() {
		return exportFileSize;
	}

	public void setExportFileSize(BigDecimal exportFileSize) {
		this.exportFileSize = exportFileSize;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public String getArchiveUndoneReason() {
		return archiveUndoneReason;
	}

	public void setArchiveUndoneReason(String archiveUndoneReason) {
		this.archiveUndoneReason = archiveUndoneReason;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "creation_user_id")
	public User getCreationUser() {
		return creationUser;
	}

	public void setCreationUser(User creationUser) {
		this.creationUser = creationUser;
	}
}

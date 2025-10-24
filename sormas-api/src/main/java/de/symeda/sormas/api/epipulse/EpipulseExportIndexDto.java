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

package de.symeda.sormas.api.epipulse;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class EpipulseExportIndexDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	private static final long serialVersionUID = 2144662213908783139L;

	public static final String I18N_PREFIX = "EpipulseExport";

	public static final String UUID = "uuid";
	public static final String DISEASE = "disease";
	public static final String CREATION_DATE = "creationDate";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String STATUS = "status";
	public static final String STATUS_CHANGE_DATE = "statusChangeDate";
	public static final String TOTAL_RECORDS = "totalRecords";
	public static final String EXPORT_FILE_NAME = "exportFileName";
	public static final String EXPORT_FILE_SIZE = "exportFileSize";
	public static final String CREATION_USER = "creationUser";

	private Disease disease;
	private Date startDate;
	private Date endDate;
	private EpipulseExportStatus status;
	private Date statusChangeDate;
	private Long totalRecords;
	private String exportFileName;
	private BigDecimal exportFileSize;
	private Date creationDate;
	private UserReferenceDto creationUser;

	public EpipulseExportIndexDto(
		String uuid,
		Disease disease,
		Date startDate,
		Date endDate,
		EpipulseExportStatus status,
		Date statusChangeDate,
		Long totalRecords,
		String exportFileName,
		BigDecimal exportFileSize,
		Date creationDate,
		String creationUserUuid,
		String creationUserFirstName,
		String creationUserLastName) {
		super(uuid);
		this.disease = disease;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.statusChangeDate = statusChangeDate;
		this.totalRecords = totalRecords;
		this.exportFileName = exportFileName;
		this.exportFileSize = exportFileSize;
		this.creationDate = creationDate;
		this.creationUser = new UserReferenceDto(creationUserUuid, creationUserFirstName, creationUserLastName);;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public EpipulseExportStatus getStatus() {
		return status;
	}

	public void setStatus(EpipulseExportStatus status) {
		this.status = status;
	}

	public Date getStatusChangeDate() {
		return statusChangeDate;
	}

	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	public Long getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getExportFileName() {
		return exportFileName;
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}

	public BigDecimal getExportFileSize() {
		return exportFileSize;
	}

	public void setExportFileSize(BigDecimal exportFileSize) {
		this.exportFileSize = exportFileSize;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public UserReferenceDto getCreationUser() {
		return creationUser;
	}

	public void setCreationUser(UserReferenceDto creationUser) {
		this.creationUser = creationUser;
	}
}

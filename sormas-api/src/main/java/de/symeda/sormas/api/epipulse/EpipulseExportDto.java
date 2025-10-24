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

import java.math.BigDecimal;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = {
	FeatureType.EPIPULSE_EXPORT })
public class EpipulseExportDto extends PseudonymizableDto {

	private static final long serialVersionUID = -4035150837980425899L;

	public static final long APPROXIMATE_JSON_SIZE_IN_BYTES = 25455;

	public static final String I18N_PREFIX = "EpipulseExport";

	public static final String DISEASE = "disease";
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
	private UserReferenceDto creationUser;

	public static EpipulseExportDto build(UserReferenceDto user) {

		final EpipulseExportDto dto = new EpipulseExportDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setCreationUser(user);

		return dto;
	}

	public static EpipulseExportDto build(EpipulseExportReferenceDto referenceDto) {

		final EpipulseExportDto dto = new EpipulseExportDto();
		dto.setUuid(referenceDto.getUuid());

		return dto;
	}

	public EpipulseExportReferenceDto toReference() {
		return new EpipulseExportReferenceDto(getUuid());
	}

	public BigDecimal getExportFileSize() {
		return exportFileSize;
	}

	public void setExportFileSize(BigDecimal exportFileSize) {
		this.exportFileSize = exportFileSize;
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

	public UserReferenceDto getCreationUser() {
		return creationUser;
	}

	public void setCreationUser(UserReferenceDto creationUser) {
		this.creationUser = creationUser;
	}
}

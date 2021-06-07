/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.sormastosormas.sharerequest;

import java.io.Serializable;
import java.util.Date;

public class SormasToSormasShareRequestIndexDto implements Serializable {

	private static final long serialVersionUID = 2314636780125234734L;

	public static final String I18N_PREFIX = "SormasToSormasShareRequest";

	public static final String UUID = "uuid";
	public static final String CREATION_DATE = "creationDate";
	public static final String DATA_TYPE = "dataType";
	public static final String STATUS = "status";
	public static final String ORGANIZATION_ID = "organizationId";
	public static final String ORGANIZATION_NAME = "organizationName";
	public static final String SENDER_NAME = "senderName";
	public static final String OWNERSHIP_HANDED_OVER = "ownershipHandedOver";
	public static final String COMMENT = "comment";

	private String uuid;
	private Date creationDate;
	private ShareRequestDataType dataType;
	private ShareRequestStatus status;
	private String organizationId;
	private String organizationName;
	private String senderName;
	private boolean ownershipHandedOver;
	private String comment;

	public SormasToSormasShareRequestIndexDto(
		String uuid,
		Date creationDate,
		ShareRequestDataType dataType,
		ShareRequestStatus status,
		String organizationId,
		String senderName,
		boolean ownershipHandedOver,
		String comment) {
		this.uuid = uuid;
		this.creationDate = creationDate;
		this.dataType = dataType;
		this.status = status;
		this.organizationId = organizationId;
		this.senderName = senderName;
		this.ownershipHandedOver = ownershipHandedOver;
		this.comment = comment;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public ShareRequestDataType getDataType() {
		return dataType;
	}

	public void setDataType(ShareRequestDataType dataType) {
		this.dataType = dataType;
	}

	public ShareRequestStatus getStatus() {
		return status;
	}

	public void setStatus(ShareRequestStatus status) {
		this.status = status;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public boolean isOwnershipHandedOver() {
		return ownershipHandedOver;
	}

	public void setOwnershipHandedOver(boolean ownershipHandedOver) {
		this.ownershipHandedOver = ownershipHandedOver;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}

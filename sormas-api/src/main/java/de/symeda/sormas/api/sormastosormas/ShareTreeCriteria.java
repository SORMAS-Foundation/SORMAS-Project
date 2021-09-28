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

package de.symeda.sormas.api.sormastosormas;

import static de.symeda.sormas.api.HasUuid.UUID_REGEX;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MAX;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MIN;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;

public class ShareTreeCriteria implements Serializable {

	private static final long serialVersionUID = 131837357088474316L;

	@Pattern(regexp = UUID_REGEX, message = Validations.patternNotMatching)
	@Size(min = CHARACTER_LIMIT_UUID_MIN, max = CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	private String entityUuid;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String exceptedOrganizationId;
	private boolean forwardOnly;
	@Valid
	private SormasToSormasOriginInfoDto originInfo;

	public ShareTreeCriteria() {
	}

	public ShareTreeCriteria(String entityUuid) {
		this(entityUuid, null, false);
	}

	public ShareTreeCriteria(String entityUuid, String exceptedOrganizationId, boolean forwardOnly) {
		this.entityUuid = entityUuid;
		this.exceptedOrganizationId = exceptedOrganizationId;
		this.forwardOnly = forwardOnly;
	}

	public String getEntityUuid() {
		return entityUuid;
	}

	public void setEntityUuid(String entityUuid) {
		this.entityUuid = entityUuid;
	}

	public String getExceptedOrganizationId() {
		return exceptedOrganizationId;
	}

	public void setExceptedOrganizationId(String exceptedOrganizationId) {
		this.exceptedOrganizationId = exceptedOrganizationId;
	}

	public boolean isForwardOnly() {
		return forwardOnly;
	}

	public void setForwardOnly(boolean forwardOnly) {
		this.forwardOnly = forwardOnly;
	}

	public SormasToSormasOriginInfoDto getOriginInfo() {
		return originInfo;
	}

	public void setOriginInfo(SormasToSormasOriginInfoDto originInfo) {
		this.originInfo = originInfo;
	}
}

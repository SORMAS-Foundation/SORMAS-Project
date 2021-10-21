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

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;

public class RequestResponseDataDto implements Serializable {

	private static final long serialVersionUID = 8682273164157677666L;

	@Pattern(regexp = HasUuid.UUID_REGEX, message = Validations.uuidPatternNotMatching)
	@Size(min = FieldConstraints.CHARACTER_LIMIT_UUID_MIN, max = FieldConstraints.CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	private String requestUuid;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String comment;

	public RequestResponseDataDto() {
	}

	public RequestResponseDataDto(String requestUuid, String comment) {
		this.requestUuid = requestUuid;
		this.comment = comment;
	}

	public String getRequestUuid() {
		return requestUuid;
	}

	public void setRequestUuid(String requestUuid) {
		this.requestUuid = requestUuid;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}

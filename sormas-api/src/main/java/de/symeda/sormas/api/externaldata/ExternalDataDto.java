/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.externaldata;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.uuid.HasUuid;
import io.swagger.v3.oas.annotations.media.Schema;

@AuditedClass
@Schema(description = "Data transfer object for external identification related data")
public class ExternalDataDto implements Serializable, HasExternalData, HasUuid {

	@AuditIncludeProperty
	@Schema(description = "UUID of the object whose external data shall be changed")
	private String uuid;
	@AuditIncludeProperty
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "External ID of the case/contact/event object")
	private String externalId;
	@AuditIncludeProperty
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Schema(description = "External token ID / file number of the object")
	private String externalToken;

	@Pattern(regexp = UUID_REGEX, message = Validations.uuidPatternNotMatching)

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalToken() {
		return externalToken;
	}

	public void setExternalToken(String externalToken) {
		this.externalToken = externalToken;
	}
}

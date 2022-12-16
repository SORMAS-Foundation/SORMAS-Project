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

package de.symeda.sormas.api.uuid;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * An abstract class for all DTOs that have an UUID.
 */
@AuditedClass
public class AbstractUuidDto implements HasUuid, Serializable {
	// FIXME(@JonasCir): I would like to make this class the base class for EntityDto, but there is an @Outbreak annotation
	//  which needs special handling. Also, this should be the base class for ReferenceDto, however, the uuid field there is
	//  required. I argue that the uuid field in this class should be required here as well, however, this would be a big 
	//  breaking change which we need to handle separately.

	public static final String UUID = "uuid";
	@AuditIncludeProperty
	@Pattern(regexp = UUID_REGEX, message = Validations.uuidPatternNotMatching)
	@Size(min = FieldConstraints.CHARACTER_LIMIT_UUID_MIN, max = FieldConstraints.CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)

	@Schema(description = "UUID of the object")
	private String uuid;

	public AbstractUuidDto(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}

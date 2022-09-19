package de.symeda.sormas.api.uuid;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.FieldConstraints;

/**
 * An abstract class for all DTOs that have an UUID.
 */

public class AbstractUuidDto implements HasUuid, Serializable {
// FIXME(@JonasCir): I would like to make this class the base class for EntityDto, but there is an @Outbreak annotation
//  which needs special handling. Also, this should be the base class for ReferenceDto, however, the uuid field there is
//  required. I argue that the uuid field in this class should be required here as well, however, this would be a big 
//  breaking change which we need to handle separately.

	public static final String UUID = "uuid";
	@Pattern(regexp = UUID_REGEX, message = Validations.uuidPatternNotMatching)
	@Size(min = FieldConstraints.CHARACTER_LIMIT_UUID_MIN, max = FieldConstraints.CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
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

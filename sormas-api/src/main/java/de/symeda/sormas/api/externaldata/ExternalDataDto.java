package de.symeda.sormas.api.externaldata;

import static de.symeda.sormas.api.HasUuid.UUID_REGEX;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MAX;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_UUID_MIN;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;

public class ExternalDataDto implements Serializable, HasExternalData {

	@Pattern(regexp = UUID_REGEX, message = Validations.uuidPatternNotMatching)
	@Size(min = CHARACTER_LIMIT_UUID_MIN, max = CHARACTER_LIMIT_UUID_MAX, message = Validations.textSizeNotInRange)
	private String uuid;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalId;
	@Size(max = CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalToken;

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

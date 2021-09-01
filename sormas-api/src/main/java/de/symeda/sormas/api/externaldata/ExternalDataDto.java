package de.symeda.sormas.api.externaldata;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_UUID_MAX;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_UUID_MIN;
import static de.symeda.sormas.api.HasUuid.UUID_REGEX;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;

public class ExternalDataDto implements Serializable, HasExternalData {

	@Pattern(regexp = UUID_REGEX, message = Validations.uuidPatternNotMatching)
	@Size(min = COLUMN_LENGTH_UUID_MIN, max = COLUMN_LENGTH_UUID_MAX, message = Validations.textSizeNotInRange)
    private String uuid;
	@Size(max = COLUMN_LENGTH_DEFAULT, message = Validations.textTooLong)
    private String externalId;
	@Size(max = COLUMN_LENGTH_DEFAULT, message = Validations.textTooLong)
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

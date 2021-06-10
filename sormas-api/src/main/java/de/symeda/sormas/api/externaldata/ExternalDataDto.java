package de.symeda.sormas.api.externaldata;

import java.io.Serializable;

public class ExternalDataDto implements Serializable, HasExternalData {

    private String uuid;
    private String externalId;
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

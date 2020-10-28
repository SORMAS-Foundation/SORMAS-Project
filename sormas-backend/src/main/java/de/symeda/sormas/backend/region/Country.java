package de.symeda.sormas.backend.region;

import de.symeda.sormas.backend.common.InfrastructureAdo;

import javax.persistence.Entity;

@Entity
public class Country extends InfrastructureAdo {

    private static final long serialVersionUID = -6050390899060395940L;

    public static final String TABLE_NAME = "country";

    public static final String DEFAULT_NAME = "default_name";
    public static final String EXTERNAL_ID = "external_id";
    public static final String ISO_CODE = "iso_code";
    public static final String UNO_CODE = "uno_code";

    private String defaultName;
    private String externalId;
    private String isoCode;
    private String unoCode;

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getUnoCode() {
        return unoCode;
    }

    public void setUnoCode(String unoCode) {
        this.unoCode = unoCode;
    }

    @Override
    public String toString() {
        return this.defaultName;
    }
}

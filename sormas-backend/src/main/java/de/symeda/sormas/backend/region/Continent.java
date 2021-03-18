package de.symeda.sormas.backend.region;

import de.symeda.sormas.api.region.ContinentReferenceDto;
import de.symeda.sormas.backend.common.InfrastructureAdo;

import javax.persistence.Entity;

@Entity
public class Continent extends InfrastructureAdo {

    public static final String TABLE_NAME = "continent";

    public static final String DEFAULT_NAME = "defaultName";
    public static final String EXTERNAL_ID = "externalId";

    private String defaultName;
    private String externalId;

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


    public ContinentReferenceDto toReference() {
        return new ContinentReferenceDto(getUuid(), getDefaultName(), externalId);
    }

    @Override
    public String toString() {
        return getDefaultName();
    }
}

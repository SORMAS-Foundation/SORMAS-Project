package de.symeda.sormas.app.backend.common;

import com.j256.ormlite.field.DatabaseField;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class InfrastructureAdo extends AbstractDomainObject {

    public static final String ARCHIVED = "archived";

    @DatabaseField
    private boolean archived = false;

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

}
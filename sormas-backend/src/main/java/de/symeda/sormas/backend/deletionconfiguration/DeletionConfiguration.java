package de.symeda.sormas.backend.deletionconfiguration;

import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.backend.common.AbstractDomainObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity(name = "deletionconfiguration")
public class DeletionConfiguration extends AbstractDomainObject {

    public static final String ENTITY_TYPE = "entityType";


	CoreEntityType entityType;
	DeletionReference deletionReference;

	@NotNull
    @Size(min = 7)
	Integer deletionPeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public CoreEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(CoreEntityType entityType) {
        this.entityType = entityType;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public DeletionReference getDeletionReference() {
        return deletionReference;
    }

    public void setDeletionReference(DeletionReference deletionReference) {
        this.deletionReference = deletionReference;
    }

    public Integer getDeletionPeriod() {
        return deletionPeriod;
    }

    public void setDeletionPeriod(Integer deletionPeriod) {
        this.deletionPeriod = deletionPeriod;
    }
}

package de.symeda.sormas.backend.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import de.symeda.auditlog.api.Audited;

/**
 * An extension of the {@link AbstractDomainObject} that defines data that is essential to the system.
 * This data may not be deleted from the system, e.g. because it is
 * relevant for external systems that share data with or use data from SORMAS.
 */
@MappedSuperclass
@Audited
public abstract class DeletableAdo extends AbstractDomainObject {

	private static final long serialVersionUID = 6512756286608581221L;

	public static final String DELETED = "deleted";

	private boolean deleted;

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Column
	public boolean isDeleted() {
		return deleted;
	}
}

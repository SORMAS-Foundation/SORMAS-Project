package de.symeda.sormas.backend.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * An extension of the {@link AbstractDomainObject} that defines core data that is essential to the system.
 * The integral definition of core data is that it may not be deleted from the system, e.g. because it is
 * relevant for external systems that share data with or use data from SORMAS.
 */
@MappedSuperclass
public abstract class CoreAdo extends AbstractDomainObject {

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

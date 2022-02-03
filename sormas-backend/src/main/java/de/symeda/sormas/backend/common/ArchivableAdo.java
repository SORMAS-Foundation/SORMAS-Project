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
public abstract class ArchivableAdo extends AbstractDomainObject {

	public static final String ARCHIVED = "archived";

	private boolean archived;

	@Column
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

}

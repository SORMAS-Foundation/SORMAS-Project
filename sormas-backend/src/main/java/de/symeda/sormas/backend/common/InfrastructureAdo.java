package de.symeda.sormas.backend.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * An extension of the {@link AbstractDomainObject} that defines infrastructure data (e.g. regions, districts).
 * Infrastructure data should not be deleted from the system, but can be archived. Archived infrastructure data
 * still has to be transfered to the mobile application.
 */
@MappedSuperclass
public abstract class InfrastructureAdo extends AbstractDomainObject {

	private static final long serialVersionUID = 6512756286608581221L;

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

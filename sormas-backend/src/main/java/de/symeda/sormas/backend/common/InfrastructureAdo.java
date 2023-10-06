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

	// todo this should be included, however, we face problems as externalID used used sometimes in the code. See #6549.
	//public static final String EXTERNAL_ID = "externalId";
	public static final String ARCHIVED = "archived";
	public static final String DEFAULT_INFRASTRUCTURE = "defaultInfrastructure";

	private boolean centrallyManaged;
	private boolean archived;
	private boolean defaultInfrastructure;

	@Column(name = "centrally_managed")
	public boolean isCentrallyManaged() {
		return centrallyManaged;
	}

	public void setCentrallyManaged(boolean centrallyManaged) {
		this.centrallyManaged = centrallyManaged;
	}

	@Column
	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	@Column
	public boolean isDefaultInfrastructure() {
		return defaultInfrastructure;
	}

	public void setDefaultInfrastructure(boolean defaultInfrastructure) {
		this.defaultInfrastructure = defaultInfrastructure;
	}
}

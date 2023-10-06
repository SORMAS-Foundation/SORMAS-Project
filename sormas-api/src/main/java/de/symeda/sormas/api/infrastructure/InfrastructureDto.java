package de.symeda.sormas.api.infrastructure;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;

public abstract class InfrastructureDto extends EntityDto {

	public static final String DEFAULT_INFRASTRUCTURE = "defaultInfrastructure";

	private boolean centrallyManaged;
	private boolean archived;
	private boolean defaultInfrastructure;

	protected InfrastructureDto() {
	}

	protected InfrastructureDto(Date creationDate, Date changeDate, String uuid, boolean archived) {
		super(creationDate, changeDate, uuid);
		this.archived = archived;
	}

	public boolean isCentrallyManaged() {
		return centrallyManaged;
	}

	public void setCentrallyManaged(boolean centrallyManaged) {
		this.centrallyManaged = centrallyManaged;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isDefaultInfrastructure() {
		return defaultInfrastructure;
	}

	public void setDefaultInfrastructure(boolean defaultInfrastructure) {
		this.defaultInfrastructure = defaultInfrastructure;
	}
}

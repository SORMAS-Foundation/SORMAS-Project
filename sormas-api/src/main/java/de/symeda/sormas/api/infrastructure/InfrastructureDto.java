package de.symeda.sormas.api.infrastructure;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;

public abstract class InfrastructureDto extends EntityDto {

	private boolean centrallyManaged;
	private boolean archived;

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

}

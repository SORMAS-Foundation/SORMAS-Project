package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.EntityDto;

import java.util.Date;

public class InfrastructureDto extends EntityDto {

	private boolean centrallyManaged;

	public InfrastructureDto() {
	}

	public InfrastructureDto(Date creationDate, Date changeDate, String uuid) {
		super(creationDate, changeDate, uuid);
	}

	public boolean isCentrallyManaged() {
		return centrallyManaged;
	}

	public void setCentrallyManaged(boolean centrallyManaged) {
		this.centrallyManaged = centrallyManaged;
	}
}

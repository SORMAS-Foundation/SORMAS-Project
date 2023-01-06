package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.EntityDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public abstract class InfrastructureDto extends EntityDto {

	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private boolean centrallyManaged;

	protected InfrastructureDto() {
	}

	protected InfrastructureDto(Date creationDate, Date changeDate, String uuid) {
		super(creationDate, changeDate, uuid);
	}

	public boolean isCentrallyManaged() {
		return centrallyManaged;
	}

	public void setCentrallyManaged(boolean centrallyManaged) {
		this.centrallyManaged = centrallyManaged;
	}

}

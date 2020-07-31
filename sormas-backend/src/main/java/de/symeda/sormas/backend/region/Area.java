package de.symeda.sormas.backend.region;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;

import de.symeda.sormas.backend.common.InfrastructureAdo;

@Entity(name = "areas")
public class Area extends InfrastructureAdo {

	private static final long serialVersionUID = 1076938355128939661L;

	public static final String TABLE_NAME = "areas";

	public static final String NAME = "name";
	public static final String EXTERNAL_ID = "externalId";

	private String name;
	private String externalId;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Override
	public String toString() {
		return getName();
	}
}

package de.symeda.sormas.backend.region;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import de.symeda.sormas.backend.common.InfrastructureAdo;

@Entity
public class SubContinent extends InfrastructureAdo {

	public static final String TABLE_NAME = "subcontinent";

	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";
	public static final String CONTINENT = "continent";

	private String defaultName;
	private String externalId;
	private Continent continent;

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

    @ManyToOne
	public Continent getContinent() {
		return continent;
	}

	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	@Override
	public String toString() {
		return getDefaultName();
	}
}

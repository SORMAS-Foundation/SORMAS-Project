package de.symeda.sormas.backend.infrastructure.continent;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;

@Entity
public class Continent extends InfrastructureAdo {

	public static final String TABLE_NAME = "continent";

	public static final String DEFAULT_NAME = "defaultName";
	public static final String EXTERNAL_ID = "externalId";

	private String defaultName;
	private Long externalId;
	private List<Subcontinent> subcontinents;

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	@OneToMany(mappedBy = Subcontinent.CONTINENT, cascade = {}, fetch = FetchType.LAZY)
	@OrderBy(Subcontinent.DEFAULT_NAME)
	public List<Subcontinent> getSubcontinents() {
		return subcontinents;
	}

	public void setSubcontinents(List<Subcontinent> subcontinents) {
		this.subcontinents = subcontinents;
	}

	public ContinentReferenceDto toReference() {
		return new ContinentReferenceDto(getUuid(), getDefaultName(), externalId);
	}

	@Override
	public String toString() {
		return getDefaultName();
	}
}

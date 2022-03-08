package de.symeda.sormas.backend.infrastructure.area;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.region.Region;

@Entity(name = "areas")
public class Area extends InfrastructureAdo {

	private static final long serialVersionUID = 1076938355128939661L;

	public static final String TABLE_NAME = "areas";
	public static final String REGION = "regions";
	public static final String NAME = "name";
	public static final String EXTERNAL_ID = "externalId";

	private String name;
	private List<Region> regions;
	private String externalId;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@OneToMany(mappedBy = Region.AREA, cascade = {}, fetch = FetchType.LAZY)
	@OrderBy(Region.NAME)
	public List<Region> getRegions() {
		return regions;
	}

	public void setRegions(List<Region> regions) {
		this.regions = regions;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
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

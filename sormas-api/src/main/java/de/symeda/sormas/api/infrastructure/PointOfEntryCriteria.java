package de.symeda.sormas.api.infrastructure;

import java.io.Serializable;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;

public class PointOfEntryCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = 2244899630454224009L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private String nameLike;
	private PointOfEntryType type;
	private Boolean active;
	private EntityRelevanceStatus relevanceStatus;

	public RegionReferenceDto getRegion() {
		return region;
	}

	public PointOfEntryCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public PointOfEntryCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}

	public String getNameLike() {
		return nameLike;
	}

	public PointOfEntryCriteria nameLike(String nameLike) {
		this.nameLike = nameLike;
		return this;
	}

	public PointOfEntryType getType() {
		return type;
	}

	public PointOfEntryCriteria type(PointOfEntryType type) {
		this.type = type;
		return this;
	}

	public Boolean getActive() {
		return active;
	}

	public void active(Boolean active) {
		this.active = active;
	}

	public PointOfEntryCriteria relevanceStatus(EntityRelevanceStatus relevanceStatus) {
		this.relevanceStatus = relevanceStatus;
		return this;
	}

	@IgnoreForUrl
	public EntityRelevanceStatus getRelevanceStatus() {
		return relevanceStatus;
	}
}

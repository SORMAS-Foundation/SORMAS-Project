package de.symeda.sormas.api.infrastructure.pointofentry;

import java.io.Serializable;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.IgnoreForUrl;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import io.swagger.v3.oas.annotations.media.Schema;

public class PointOfEntryCriteria extends BaseCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = 2244899630454224009L;

	private CountryReferenceDto country;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	@Schema(description = "Search-term for point-of-entry name")
	private String nameLike;
	private PointOfEntryType type;
	@Schema(description = "Whether to filter for active points-of-entry only")
	private Boolean active;
	private EntityRelevanceStatus relevanceStatus;

	public CountryReferenceDto getCountry() {
		return country;
	}

	public PointOfEntryCriteria country(CountryReferenceDto country) {
		this.country = country;

		return this;
	}

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

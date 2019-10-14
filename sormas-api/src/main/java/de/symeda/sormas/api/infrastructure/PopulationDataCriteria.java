package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class PopulationDataCriteria extends BaseCriteria implements Cloneable {

	private static final long serialVersionUID = -876047405287325714L;
	
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Sex sex;
	private AgeGroup ageGroup;

	public PopulationDataCriteria region(RegionReferenceDto region) {
		this.region = region;
		return this;
	}
	
	public RegionReferenceDto getRegion() {
		return region;
	}

	public PopulationDataCriteria district(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	
	public PopulationDataCriteria sex(Sex sex) {
		this.sex = sex;
		return this;
	}
	
	public Sex getSex() {
		return sex;
	}
	
	public PopulationDataCriteria ageGroup(AgeGroup ageGroup) {
		this.ageGroup = ageGroup;
		return this;
	}
	
	public AgeGroup getAgeGroup() {
		return ageGroup;
	}
	
}

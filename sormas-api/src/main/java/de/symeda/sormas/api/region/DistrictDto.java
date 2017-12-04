package de.symeda.sormas.api.region;

import de.symeda.sormas.api.EntityDto;

public class DistrictDto extends EntityDto {

	private static final long serialVersionUID = 8990957700033431836L;

	public static final String I18N_PREFIX = "District";

	private String name;
	private String epidCode;
	private Integer population;
	private Float growthRate;
	private RegionReferenceDto region;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEpidCode() {
		return epidCode;
	}
	public void setEpidCode(String epidCode) {
		this.epidCode = epidCode;
	}
	
	public RegionReferenceDto getRegion() {
		return region;
	}
	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public Integer getPopulation() {
		return population;
	}
	public void setPopulation(Integer population) {
		this.population = population;
	}
	
	public Float getGrowthRate() {
		return growthRate;
	}
	public void setGrowthRate(Float growthRate) {
		this.growthRate = growthRate;
	}
	
	public DistrictReferenceDto toReference() {
		return new DistrictReferenceDto(getUuid());
	}
}

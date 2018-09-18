package de.symeda.sormas.api.region;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;

public class DistrictDto extends EntityDto {

	private static final long serialVersionUID = 8990957700033431836L;

	public static final String I18N_PREFIX = "District";
	public static final int CASE_INCIDENCE_DIVISOR = 100000;

	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";
	public static final String POPULATION = "population";
	public static final String GROWTH_RATE = "growthRate";
	public static final String REGION = "region";
	
	private String name;
	private String epidCode;
	private Integer population;
	private Float growthRate;
	private RegionReferenceDto region;
	
	public DistrictDto(Date creationDate, Date changeDate, String uuid, String name, String epidCode, Integer population, Float growthRate, String regionUuid, String regionName) {
		super(creationDate, changeDate, uuid);
		this.name = name;
		this.epidCode = epidCode;
		this.population = population;
		this.growthRate = growthRate;
		this.region = new RegionReferenceDto(regionUuid, regionName);
	}
	
	public DistrictDto() {
		super();
	}
	
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
	
	public static DistrictDto build() {
		DistrictDto dto = new DistrictDto();
		return dto;
	}
	
}

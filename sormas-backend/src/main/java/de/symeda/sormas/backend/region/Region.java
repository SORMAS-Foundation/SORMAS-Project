package de.symeda.sormas.backend.region;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
public class Region extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2958216667876104358L;

	public static final String TABLE_NAME = "region";
	
	public static final String NAME = "name";
	public static final String EPID_CODE = "epidCode";
	public static final String DISTRICTS = "districts";
	public static final String POPULATION = "population";
	public static final String GROWTH_RATE = "growthRate";
	
	private String name;
	private String epidCode;
	private List<District> districts;
	private Integer population;
	private Float growthRate;
	
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
	
	@OneToMany(mappedBy = District.REGION, cascade = {}, fetch = FetchType.LAZY)
	@OrderBy(District.NAME)
	public List<District> getDistricts() {
		return districts;
	}
	public void setDistricts(List<District> districts) {
		this.districts = districts;
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
}

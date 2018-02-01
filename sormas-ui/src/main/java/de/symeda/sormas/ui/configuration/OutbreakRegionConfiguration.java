package de.symeda.sormas.ui.configuration;

import java.util.HashSet;
import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class OutbreakRegionConfiguration {
	
	private Disease disease;
	private int totalDistricts;
	private RegionReferenceDto region;
	private Set<DistrictReferenceDto> affectedDistricts;
	
	public OutbreakRegionConfiguration(Disease disease, RegionReferenceDto region, int totalDistricts, Set<DistrictReferenceDto> affectedDistricts) {
		this.disease = disease;
		this.region = region;
		this.totalDistricts = totalDistricts;
		if (affectedDistricts != null) {
			this.affectedDistricts = affectedDistricts;
		} else {
			this.affectedDistricts = new HashSet<>();
		}
	}
	
	public int getTotalDistricts() {
		return totalDistricts;
	}
	
	public void setTotalDistricts(int totalDistricts) {
		this.totalDistricts = totalDistricts;
	}
	
	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public Set<DistrictReferenceDto> getAffectedDistricts() {
		return affectedDistricts;
	}
	
	public void setAffectedDistricts(Set<DistrictReferenceDto> affectedDistricts) {
		this.affectedDistricts = affectedDistricts;
	}
	
	@Override
	public String toString() {
		
		if (affectedDistricts.isEmpty()) {
			return "0";
		} else {
			return affectedDistricts.size() + "/" + totalDistricts;
		}
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

}
package de.symeda.sormas.api.infrastructure;

import java.util.List;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public interface PopulationStatisticsCriteria {
	public List<Sex> getSexes();
	
	public List<AgeGroup> getAgeGroups();
	
	public List<RegionReferenceDto> getRegions();

	public List<DistrictReferenceDto> getDistricts();
}

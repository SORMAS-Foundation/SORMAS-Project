package de.symeda.sormas.api.infrastructure;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface PopulationDataFacade {

	Integer getDistrictPopulation(String districtUuid);
	
	/**
	 * Returns the population of the district, projected to the current point in time based on its growth rate
	 */
	Integer getProjectedDistrictPopulation(String districtUuid);
	
	Integer getRegionPopulation(String regionUuid);
	
	/**
	 * Returns the population of the region, projected to the current point in time based on its growth rate
	 */
	Integer getProjectedRegionPopulation(String regionUuid);
	
	void savePopulationData(PopulationDataDto populationData) throws ValidationRuntimeException;
	
	PopulationDataDto getPopulationData(PopulationDataCriteria criteria);
	
	List<Object[]> getPopulationDataForExport();
	
}

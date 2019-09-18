package de.symeda.sormas.api.infrastructure;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface PopulationDataFacade {

	Integer getDistrictPopulation(String districtUuid);
	
	Integer getRegionPopulation(String regionUuid);
	
	void savePopulationData(PopulationDataDto populationData) throws ValidationRuntimeException;
	
	PopulationDataDto getPopulationData(PopulationDataCriteria criteria);
	
	List<Object[]> getPopulationDataForExport();
	
}

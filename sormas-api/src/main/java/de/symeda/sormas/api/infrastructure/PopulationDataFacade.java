package de.symeda.sormas.api.infrastructure;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
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

	void savePopulationData(List<PopulationDataDto> populationDataList) throws ValidationRuntimeException;

	List<PopulationDataDto> getPopulationData(PopulationDataCriteria criteria);

	List<Object[]> getPopulationDataForExport();

	/**
	 * Checks whether there is general population data available for all regions and districts
	 */
	List<Long> getMissingPopulationDataForStatistics(
		StatisticsCaseCriteria criteria,
		boolean groupByRegion,
		boolean groupByDistrict,
		boolean groupBySex,
		boolean groupByAgeGroup);
}

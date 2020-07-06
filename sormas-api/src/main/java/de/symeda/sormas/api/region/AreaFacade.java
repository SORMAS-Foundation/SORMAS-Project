package de.symeda.sormas.api.region;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface AreaFacade {

	List<AreaReferenceDto> getAllActiveAsReference();

	AreaDto getAreaByUuid(String uuid);

	List<AreaDto> getIndexList(AreaCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(AreaCriteria criteria);

	void saveArea(AreaDto area);

	boolean isUsedInOtherInfrastructureData(Collection<String> areaUuids);

	void archive(String areaUuid);

	void deArchive(String areaUuid);

	List<AreaReferenceDto> getByName(String name, boolean includeArchivedAreas);

}

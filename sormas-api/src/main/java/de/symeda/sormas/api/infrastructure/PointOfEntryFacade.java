package de.symeda.sormas.api.infrastructure;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface PointOfEntryFacade {
	
	/**
	 * @param includeOthers Whether to include generic points of entry that can be used when a specific
	 * point of entry is not in the database.
	 */
	List<PointOfEntryReferenceDto> getAllByDistrict(String districtUuid, boolean includeOthers);
	
	PointOfEntryDto getByUuid(String uuid);
	
	void save(PointOfEntryDto pointOfEntry) throws ValidationRuntimeException;
	
	List<PointOfEntryDto> getIndexList(PointOfEntryCriteria criteria, int first, int max, List<SortProperty> sortProperties);
	
	long count(PointOfEntryCriteria criteria);

	void validate(PointOfEntryDto pointOfEntry) throws ValidationRuntimeException;
	
}

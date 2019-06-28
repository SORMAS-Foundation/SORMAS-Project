package de.symeda.sormas.api.infrastructure;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface PointOfEntryFacade {
	
	/**
	 * @param includeOthers Whether to include generic points of entry that can be used when a specific
	 * point of entry is not in the database.
	 */
	List<PointOfEntryReferenceDto> getAllByDistrict(String districtUuid, boolean includeOthers);

}

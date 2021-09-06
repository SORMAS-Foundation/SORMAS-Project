package de.symeda.sormas.api.infrastructure.pointofentry;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.infrastructure.InfrastructureBaseFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface PointOfEntryFacade
	extends InfrastructureBaseFacade<PointOfEntryDto, PointOfEntryDto, PointOfEntryReferenceDto, PointOfEntryCriteria> {

	/**
	 * @param includeOthers
	 *            Whether to include generic points of entry that can be used when a specific
	 *            point of entry is not in the database.
	 */
	List<PointOfEntryReferenceDto> getAllActiveByDistrict(String districtUuid, boolean includeOthers);

	void validate(PointOfEntryDto pointOfEntry) throws ValidationRuntimeException;

	List<PointOfEntryReferenceDto> getByName(String name, DistrictReferenceDto district, boolean includeArchivedEntities);

	boolean hasArchivedParentInfrastructure(Collection<String> pointOfEntryUuids);
}

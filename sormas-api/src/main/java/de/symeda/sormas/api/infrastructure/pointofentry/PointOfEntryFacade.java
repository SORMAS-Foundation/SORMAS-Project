package de.symeda.sormas.api.infrastructure.pointofentry;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface PointOfEntryFacade {

	/**
	 * @param includeOthers
	 *            Whether to include generic points of entry that can be used when a specific
	 *            point of entry is not in the database.
	 */
	List<PointOfEntryReferenceDto> getAllActiveByDistrict(String districtUuid, boolean includeOthers);

	PointOfEntryDto getByUuid(String uuid);

	void save(@Valid PointOfEntryDto pointOfEntry) throws ValidationRuntimeException;

	void save(@Valid PointOfEntryDto dto, boolean allowMerge) throws ValidationRuntimeException;

	List<PointOfEntryDto> getIndexList(PointOfEntryCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(PointOfEntryCriteria criteria);

	void validate(PointOfEntryDto pointOfEntry) throws ValidationRuntimeException;

	List<PointOfEntryDto> getAllAfter(Date date);

	List<String> getAllUuids();

	List<PointOfEntryDto> getByUuids(List<String> uuids);

	List<PointOfEntryReferenceDto> getByName(String name, DistrictReferenceDto district, boolean includeArchivedEntities);

	List<PointOfEntryReferenceDto> getByExternalId(String name, boolean includeArchivedEntities);

	void archive(String pointOfEntryUuid);

	void dearchive(String pointOfEntryUuid);

	boolean hasArchivedParentInfrastructure(Collection<String> pointOfEntryUuids);
}

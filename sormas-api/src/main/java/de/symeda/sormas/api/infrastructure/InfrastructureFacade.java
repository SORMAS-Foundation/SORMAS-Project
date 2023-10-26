package de.symeda.sormas.api.infrastructure;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import de.symeda.sormas.api.ArchivableFacade;
import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public interface InfrastructureFacade<DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends InfrastructureDataReferenceDto, CRITERIA extends BaseCriteria>
	extends BaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA>, ArchivableFacade {

	DTO save(@Valid DTO dtoToSave, boolean allowMerge);

	List<REF_DTO> getReferencesByExternalId(String externalId, boolean includeArchivedEntities);

	/**
	 * Save the given DTO received from central, but skip checks for infrastructure locks, archived entities etc.
	 * 
	 * @param dto
	 *            The DTO which should be saved.
	 * @return The saved DTO.
	 */
	DTO saveFromCentral(@Valid DTO dto);

	boolean isUsedInOtherInfrastructureData(Collection<String> uuids);

	boolean hasArchivedParentInfrastructure(Collection<String> uuids);

	REF_DTO getDefaultInfrastructureReference();
}

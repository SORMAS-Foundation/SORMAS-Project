package de.symeda.sormas.api.infrastructure;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

@Remote
public interface InfrastructureBaseFacade<DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends InfrastructureDataReferenceDto, CRITERIA extends BaseCriteria>
	extends BaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	List<REF_DTO> getByExternalId(String externalId, boolean includeArchivedEntities);

	/**
	 * Save the given DTO, but skip checks for infrastructure locks etc.
	 * 
	 * @param dto
	 *            The DTO which should be saved.
	 * @return The saved DTO.
	 */
	DTO saveUnchecked(@Valid DTO dto);
}

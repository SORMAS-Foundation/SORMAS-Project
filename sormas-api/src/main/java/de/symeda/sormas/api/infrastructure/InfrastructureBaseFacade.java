package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

import javax.ejb.Remote;
import java.io.Serializable;
import java.util.List;

@Remote
public interface InfrastructureBaseFacade<DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, CRITERIA extends BaseCriteria>
	extends BaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	DTO save(DTO dto, boolean allowMerge);

	List<REF_DTO> getByExternalId(String externalId, boolean includeArchivedEntities);

}

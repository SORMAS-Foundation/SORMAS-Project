package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

import javax.ejb.Remote;
import java.io.Serializable;
import java.util.List;

/**
 * Facade to group all infrastructure/location together which indicates a real geographical entity, in contrast to facilities etc.
 * 
 * @param <DTO>
 * @param <INDEX_DTO>
 * @param <REF_DTO>
 * @param <CRITERIA>
 */
@Remote
public interface GeoLocationFacade<DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends InfrastructureDataReferenceDto, CRITERIA extends BaseCriteria>
	extends InfrastructureBaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	List<REF_DTO> getReferencesByName(String name, boolean includeArchived); 
}

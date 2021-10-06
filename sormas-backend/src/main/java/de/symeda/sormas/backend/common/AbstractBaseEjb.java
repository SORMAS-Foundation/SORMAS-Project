package de.symeda.sormas.backend.common;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.util.DtoHelper;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public abstract class AbstractBaseEjb<ADO extends AbstractDomainObject, DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, SRV extends AdoServiceWithUserFilter<ADO>, CRITERIA extends BaseCriteria>
	implements BaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	protected SRV service;

	protected AbstractBaseEjb() {
	}

	protected AbstractBaseEjb(SRV service) {
		this.service = service;
	}

	// todo cannot be filled right now as we are missing ArchivableAbstractDomainObject
	// with this abstract class e.g., ImmunizationFacadeEjb could be wired up to this as well
	public abstract void archive(String uuid);

	public abstract void dearchive(String uuid);

	// FIXME(@JonasCir) #6821: Add missing functions like save, getByUuid etc

	@Override
	public DTO save(DTO dtoToSave) {
		return save(dtoToSave, false);
	}

	protected DTO persistEntity(DTO dto, ADO entityToPersist) {
		entityToPersist = fillOrBuildEntity(dto, entityToPersist, true);
		service.ensurePersisted(entityToPersist);
		return toDto(entityToPersist);
	}

	protected DTO mergeAndPersist(DTO dtoToSave, List<ADO> duplicates) {
		ADO existingEntity = duplicates.get(0);
		DTO existingDto = toDto(existingEntity);
		DtoHelper.copyDtoValues(existingDto, dtoToSave, true);
		return persistEntity(dtoToSave, existingEntity);

	}

	public DTO getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	protected abstract List<ADO> findDuplicates(DTO dto);

	protected abstract ADO fillOrBuildEntity(@NotNull DTO source, ADO target, boolean checkChangeDate);

	public abstract DTO toDto(ADO ado);
}

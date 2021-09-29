package de.symeda.sormas.backend.common;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBaseEjb<ADO extends AbstractDomainObject, DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, SRV extends AdoServiceWithUserFilter<ADO>, CRITERIA extends BaseCriteria>
	implements BaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	protected SRV service;
	private UserService userService;

	protected AbstractBaseEjb() {
	}

	protected AbstractBaseEjb(SRV service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}

	// FIXME(@JonasCir) #6821: Add missing functions like save, getByUuid etc

	@Override
	public DTO save(DTO dtoToSave) {
		return save(dtoToSave, false);
	}

	public abstract DTO save(@Valid DTO dtoToSave, boolean allowMerge);

	// todo cannot be filled right now as we are missing ArchivableAbstractDomainObject
	// with this abstract class e.g., ImmunizationFacadeEjb could be wired up to this as well
	public abstract void archive(String uuid);

	public abstract void dearchive(String uuid);

	public DTO getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	public List<DTO> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	public List<String> getAllUuids() {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return service.getAllUuids();
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

	protected abstract List<ADO> findDuplicates(DTO dto);

	protected abstract ADO fillOrBuildEntity(@NotNull DTO source, ADO target, boolean checkChangeDate);

	public abstract DTO toDto(ADO ado);
}

package de.symeda.sormas.backend.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ModelConstants;

public abstract class AbstractBaseEjb<ADO extends AbstractDomainObject, DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, SRV extends AdoServiceWithUserFilter<ADO>, CRITERIA extends BaseCriteria>
	implements BaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	protected SRV service;
	protected UserService userService;
	protected Class<ADO> adoClass;
	protected Class<DTO> dtoClass;

	protected AbstractBaseEjb() {
	}

	protected AbstractBaseEjb(Class<ADO> adoClass, Class<DTO> dtoClass, SRV service, UserService userService) {
		this.adoClass = adoClass;
		this.dtoClass = dtoClass;
		this.service = service;
		this.userService = userService;
	}

	@Override
	public DTO getByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}

	@Override
	public REF_DTO getReferenceByUuid(String uuid) {
		return Optional.ofNullable(uuid).map(u -> service.getByUuid(u)).map(this::toRefDto).orElse(null);
	}

	@Override
	public List<DTO> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return service.getAllUuids();
	}

	@Override
	public List<DTO> getAllAfter(Date date) {
		return service.getAll((cb, root) -> service.createChangeDateFilter(cb, root, date)).stream().map(this::toDto).collect(Collectors.toList());
	}

	// todo find a better name, it is not clear what it does
	protected abstract void selectDtoFields(CriteriaQuery<DTO> cq, Root<ADO> root);

	protected abstract ADO fillOrBuildEntity(@NotNull DTO source, ADO target, boolean checkChangeDate);

	public abstract DTO toDto(ADO ado);

	protected abstract REF_DTO toRefDto(ADO ado);
}

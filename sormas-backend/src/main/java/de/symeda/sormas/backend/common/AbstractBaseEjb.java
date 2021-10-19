package de.symeda.sormas.backend.common;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

	public List<DTO> getAllAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DTO> cq = cb.createQuery(dtoClass);
		Root<ADO> ado = cq.from(adoClass);

		selectDtoFields(cq, ado);
		Predicate filter = service.createChangeDateFilter(cb, ado, date);

		if (filter != null) {
			cq.where(filter);
		}
		return em.createQuery(cq).getResultList();
	}

	// todo find a better name, it is not clear what it does
	protected abstract void selectDtoFields(CriteriaQuery<DTO> cq, Root<ADO> root);

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

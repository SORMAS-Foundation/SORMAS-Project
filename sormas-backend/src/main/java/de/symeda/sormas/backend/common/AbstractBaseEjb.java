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
import de.symeda.sormas.backend.util.Pseudonymizer;

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
		return Optional.of(uuid).map(u -> service.getByUuid(u, true)).map(this::toPseudonymizedDto).orElse(null);
	}

	@Override
	public REF_DTO getReferenceByUuid(String uuid) {
		return Optional.ofNullable(uuid).map(u -> service.getByUuid(u)).map(this::toRefDto).orElse(null);
	}

	@Override
	public List<DTO> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = createPseudonymizer();
		return service.getByUuids(uuids).stream().map(source -> toPseudonymizedDto(source, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {
		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}
		return service.getAllUuids();
	}

	@Override
	public List<String> getObsoleteUuidsSince(Date since) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return service.getObsoleteUuidsSince(since);
	}

	public DTO toPseudonymizedDto(ADO source) {
		return toPseudonymizedDto(source, createPseudonymizer());
	}

	public DTO toPseudonymizedDto(ADO source, Pseudonymizer pseudonymizer) {

		if (source == null) {
			return null;
		}

		boolean inJurisdiction = isAdoInJurisdiction(source);
		return toPseudonymizedDto(source, pseudonymizer, inJurisdiction);
	}

	public DTO toPseudonymizedDto(ADO source, Pseudonymizer pseudonymizer, boolean inJurisdiction) {

		if (source == null) {
			return null;
		}

		DTO dto = toDto(source);
		pseudonymizeDto(source, dto, pseudonymizer, inJurisdiction);
		return dto;
	}

	protected void restorePseudonymizedDto(DTO dto, DTO existingDto, ADO entity) {
		restorePseudonymizedDto(dto, existingDto, entity, createPseudonymizer());
	}

	protected Pseudonymizer createPseudonymizer() {
		return Pseudonymizer.getDefault(userService::hasRight);
	}

	// todo find a better name, it is not clear what it does
	protected abstract void selectDtoFields(CriteriaQuery<DTO> cq, Root<ADO> root);

	protected abstract ADO fillOrBuildEntity(@NotNull DTO source, ADO target, boolean checkChangeDate);

	public abstract DTO toDto(ADO ado);

	protected abstract REF_DTO toRefDto(ADO ado);

	protected abstract void pseudonymizeDto(ADO source, DTO dto, Pseudonymizer pseudonymizer, boolean inJurisdiction);

	protected abstract void restorePseudonymizedDto(DTO dto, DTO existingDto, ADO entity, Pseudonymizer pseudonymizer);

	protected abstract boolean isAdoInJurisdiction(ADO ado);
}

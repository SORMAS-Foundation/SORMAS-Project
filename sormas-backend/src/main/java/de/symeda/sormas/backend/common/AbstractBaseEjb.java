package de.symeda.sormas.backend.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.api.utils.fieldaccess.checkers.AnnotationBasedFieldAccessChecker.SpecialAccessCheck;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

public abstract class AbstractBaseEjb<ADO extends AbstractDomainObject, DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, SRV extends AdoServiceWithUserFilterAndJurisdiction<ADO>, CRITERIA extends BaseCriteria>
	implements BaseFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	protected SRV service;
	@Inject
	protected UserService userService;
	protected Class<ADO> adoClass;
	protected Class<DTO> dtoClass;

	protected AbstractBaseEjb() {
	}

	protected AbstractBaseEjb(Class<ADO> adoClass, Class<DTO> dtoClass, SRV service) {
		this.adoClass = adoClass;
		this.dtoClass = dtoClass;
		this.service = service;
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
		return toPseudonymizedDtos(service.getByUuids(uuids));
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

	@Override
	public List<DTO> getAllAfter(Date date) {
		return getAllAfter(date, null, null);
	}

	@Override
	public List<DTO> getAllAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		List<ADO> entities = service.getAllAfter(date, batchSize, lastSynchronizedUuid);
		return toPseudonymizedDtos(entities);
	}

	public DTO toPseudonymizedDto(ADO source) {
		return toPseudonymizedDto(source, createPseudonymizer(source));
	}

	public DTO toPseudonymizedDto(ADO source, Pseudonymizer<DTO> pseudonymizer) {

		if (source == null) {
			return null;
		}

		boolean inJurisdiction = isAdoInJurisdiction(source);
		return toPseudonymizedDto(source, pseudonymizer, inJurisdiction);
	}

	public DTO toPseudonymizedDto(ADO source, Pseudonymizer<DTO> pseudonymizer, boolean inJurisdiction) {

		if (source == null) {
			return null;
		}

		DTO dto = toDto(source);
		pseudonymizeDto(source, dto, pseudonymizer, inJurisdiction);
		return dto;
	}

	protected List<DTO> toPseudonymizedDtos(List<ADO> adoList) {
		if (adoList == null) {
			return Collections.emptyList();
		}

		Pseudonymizer<DTO> pseudonymizer = createPseudonymizer(adoList);
		List<Long> jurisdictionIds = service.getInJurisdictionIds(adoList);

		return adoList.stream()
			.map(ado -> toPseudonymizedDto(ado, pseudonymizer, jurisdictionIds.contains(ado.getId())))
			.collect(Collectors.toList());
	}

	protected void restorePseudonymizedDto(DTO dto, DTO existingDto, ADO entity) {
		restorePseudonymizedDto(dto, existingDto, entity, createPseudonymizer(entity));
	}

	protected Pseudonymizer<DTO> createPseudonymizer(ADO ado) {
		return createPseudonymizer(ado != null ? Collections.singletonList(ado) : Collections.emptyList());
	}

	protected Pseudonymizer<DTO> createPseudonymizer(List<ADO> adoList) {
		return Pseudonymizer.getDefault(userService);
	}

	protected <T> Pseudonymizer<T> createGenericPseudonymizer() {
		return Pseudonymizer.getDefault(userService);
	}

	protected <T> Pseudonymizer<T> createGenericPlaceholderPseudonymizer() {
		return Pseudonymizer.getDefault(userService, I18nProperties.getCaption(Captions.inaccessibleValue));
	}

	protected <T> Pseudonymizer<T> createGenericPlaceholderPseudonymizer(SpecialAccessCheck<T> specialAccessCheck) {
		return Pseudonymizer.getDefault(userService, specialAccessCheck, I18nProperties.getCaption(Captions.inaccessibleValue));
	}

	protected abstract ADO fillOrBuildEntity(@NotNull DTO source, ADO target, boolean checkChangeDate);

	protected abstract DTO toDto(ADO ado);

	public List<DTO> toDtos(Stream<ADO> adoStream) {
		return adoStream.map(this::toDto).collect(Collectors.toList());
	}

	protected abstract REF_DTO toRefDto(ADO ado);

	protected List<REF_DTO> toRefDtos(Stream<ADO> adoStream) {
		return adoStream.map(this::toRefDto).collect(Collectors.toList());
	}

	protected abstract void pseudonymizeDto(ADO source, DTO dto, Pseudonymizer<DTO> pseudonymizer, boolean inJurisdiction);

	protected abstract void restorePseudonymizedDto(DTO dto, DTO existingDto, ADO entity, Pseudonymizer<DTO> pseudonymizer);

	protected boolean isAdoInJurisdiction(ADO ado) {
		return service.inJurisdictionOrOwned(ado);
	}
}

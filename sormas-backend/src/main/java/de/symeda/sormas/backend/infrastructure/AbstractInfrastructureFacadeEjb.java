package de.symeda.sormas.backend.infrastructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.InfrastructureFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DtoCopyHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.backend.util.RightsAllowed;

@AuditIgnore(retainWrites = true)
public abstract class AbstractInfrastructureFacadeEjb<ADO extends InfrastructureAdo, DTO extends InfrastructureDto, INDEX_DTO extends Serializable, REF_DTO extends InfrastructureDataReferenceDto, SRV extends AbstractInfrastructureAdoService<ADO, CRITERIA>, CRITERIA extends BaseCriteria>
	extends AbstractBaseEjb<ADO, DTO, INDEX_DTO, REF_DTO, SRV, CRITERIA>
	implements InfrastructureFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	protected FeatureConfigurationFacadeEjb featureConfiguration;
	private String duplicateErrorMessageProperty;
	private String archivingNotPossibleMessageProperty;
	private String dearchivingNotPossibleMessageProperty;

	protected AbstractInfrastructureFacadeEjb() {
		super();
	}

	protected AbstractInfrastructureFacadeEjb(
		Class<ADO> adoClass,
		Class<DTO> dtoClass,
		SRV service,
		FeatureConfigurationFacadeEjb featureConfiguration,
		String duplicateErrorMessageProperty,
		String archivingNotPossibleMessageProperty,
		String dearchivingNotPossibleMessageProperty) {
		super(adoClass, dtoClass, service);
		this.featureConfiguration = featureConfiguration;
		this.duplicateErrorMessageProperty = duplicateErrorMessageProperty;
		this.archivingNotPossibleMessageProperty = archivingNotPossibleMessageProperty;
		this.dearchivingNotPossibleMessageProperty = dearchivingNotPossibleMessageProperty;
	}

	@Override
	@PermitAll
	public List<String> getAllUuids() {
		return super.getAllUuids();
	}

	@Override
	@PermitAll
	public List<DTO> getAllAfter(Date date) {
		return super.getAllAfter(date);
	}

	@Override
	@PermitAll
	public List<String> getObsoleteUuidsSince(Date since) {
		return super.getObsoleteUuidsSince(since);
	}

	@Override
	@PermitAll
	public DTO getByUuid(String uuid) {
		return super.getByUuid(uuid);
	}

	@Override
	@PermitAll
	public REF_DTO getReferenceByUuid(String uuid) {
		return super.getReferenceByUuid(uuid);
	}

	@Override
	@PermitAll
	public List<DTO> getByUuids(List<String> uuids) {
		return super.getByUuids(uuids);
	}

	@Override
	@RightsAllowed({
		UserRight._INFRASTRUCTURE_CREATE,
		UserRight._INFRASTRUCTURE_EDIT })
	public DTO save(@Valid @NotNull DTO dtoToSave) {
		return save(dtoToSave, false);
	}

	@RightsAllowed({
		UserRight._INFRASTRUCTURE_CREATE,
		UserRight._INFRASTRUCTURE_EDIT })
	public DTO save(DTO dto, boolean allowMerge) {
		checkInfraDataLocked();
		// default behaviour is to include archived data and check for the change date
		return doSave(dto, allowMerge, true, true, false);
	}

	@RightsAllowed(UserRight._SYSTEM)
	public DTO saveFromCentral(DTO dtoToSave) {
		// merge, but do not include archived data (we consider archive data to be completely broken)
		// also ignore change date as merging will always cause the date to be newer to what is present in central
		return doSave(dtoToSave, true, false, false, true);
	}

	protected DTO doSave(DTO dtoToSave, boolean allowMerge, boolean includeArchived, boolean checkChangeDate, boolean allowUuidOverwrite) {
		if (dtoToSave == null) {
			return null;
		}
		ADO existingEntity = service.getByUuid(dtoToSave.getUuid());

		final User currentUser = userService.getCurrentUser();

		// currentUser is the currently logged-in user. However, in the most cases, there is no user session active as
		// the call to AbstractInfrastructureEjb::save is the result of a cron service invocation or server start.
		// In this case it is safe to grant access.
		// In case the invocation is the result of a user action (e.g., via the REST API) we do an access check.

		if (currentUser != null) {
			if (existingEntity == null && !userService.hasRight(UserRight.INFRASTRUCTURE_CREATE)) {
				throw new UnsupportedOperationException("User " + currentUser.getUuid() + " is not allowed to create infrastructure data.");
			}
			if (existingEntity != null && !userService.hasRight(UserRight.INFRASTRUCTURE_EDIT)) {
				throw new UnsupportedOperationException("User " + currentUser.getUuid() + " is not allowed to edit infrastructure data.");
			}
		}

		if (existingEntity == null) {
			List<ADO> duplicates = findDuplicates(dtoToSave, includeArchived);
			if (!duplicates.isEmpty()) {
				if (allowMerge) {
					return mergeAndPersist(dtoToSave, duplicates, checkChangeDate, allowUuidOverwrite);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(duplicateErrorMessageProperty));
				}
			}
		}
		return persistEntity(dtoToSave, existingEntity, checkChangeDate, false);
	}

	protected DTO persistEntity(DTO dto, ADO entityToPersist, boolean checkChangeDate, boolean allowUuidOverwrite) {
		entityToPersist = fillOrBuildEntity(dto, entityToPersist, checkChangeDate, allowUuidOverwrite);
		service.ensurePersisted(entityToPersist);
		return toDto(entityToPersist);
	}

	protected DTO mergeAndPersist(DTO dtoToSave, List<ADO> duplicates, boolean checkChangeDate, boolean allowUuidOverwrite) {
		ADO existingEntity = duplicates.get(0);
		DTO existingDto = toDto(existingEntity);
		DtoCopyHelper.copyDtoValues(existingDto, dtoToSave, true);

		return persistEntity(dtoToSave, existingEntity, checkChangeDate, allowUuidOverwrite);
	}

	protected abstract ADO fillOrBuildEntity(@NotNull DTO source, ADO target, boolean checkChangeDate, boolean allowUuidOverwrite);

	@Override
	protected ADO fillOrBuildEntity(DTO source, ADO target, boolean checkChangeDate) {
		return fillOrBuildEntity(source, target, checkChangeDate, false);
	}

	@Override
	@RightsAllowed(UserRight._INFRASTRUCTURE_ARCHIVE)
	public ProcessedEntity archive(String uuid) {
		ProcessedEntity processedEntity;

		// todo this should be really in the parent but right now there the setter for archived is not available there
		checkInfraDataLocked();
		if (isUsedInOtherInfrastructureData(Collections.singletonList(uuid))) {
			processedEntity = new ProcessedEntity(uuid, ProcessedEntityStatus.ACCESS_DENIED_FAILURE);
		} else {
			processedEntity = new ProcessedEntity(uuid, ProcessedEntityStatus.SUCCESS);
		}

		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(true);
			service.ensurePersisted(ado);
		}

		return processedEntity;
	}

	@RightsAllowed(UserRight._INFRASTRUCTURE_ARCHIVE)
	public ProcessedEntity dearchive(String uuid) {
		checkInfraDataLocked();
		if (hasArchivedParentInfrastructure(Collections.singletonList(uuid))) {
			throw new AccessDeniedException(I18nProperties.getString(dearchivingNotPossibleMessageProperty));
		}
		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(false);
			service.ensurePersisted(ado);
		}
		return new ProcessedEntity(uuid, ProcessedEntityStatus.SUCCESS);
	}

	@RightsAllowed(UserRight._INFRASTRUCTURE_ARCHIVE)
	public List<ProcessedEntity> archive(List<String> entityUuids) {
		List<ProcessedEntity> processedEntities = new ArrayList<>();
		entityUuids.forEach(entityUuid -> {
			if (!isUsedInOtherInfrastructureData(Collections.singletonList(entityUuid))) {
				archive(entityUuid);
				processedEntities.add(new ProcessedEntity(entityUuid, ProcessedEntityStatus.SUCCESS));
			} else {
				processedEntities.add(new ProcessedEntity(entityUuid, ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
			}
		});
		return processedEntities;
	}

	@RightsAllowed(UserRight._INFRASTRUCTURE_ARCHIVE)
	public List<ProcessedEntity> dearchive(List<String> entityUuids) {
		List<ProcessedEntity> processedEntities = new ArrayList<>();

		entityUuids.forEach(entityUuid -> {
			if (!hasArchivedParentInfrastructure(Arrays.asList(entityUuid))) {
				processedEntities.add(dearchive(entityUuid));
			} else {
				processedEntities.add(new ProcessedEntity(entityUuid, ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
			}
		});
		return processedEntities;
	}

	@Override
	@RightsAllowed(UserRight._INFRASTRUCTURE_VIEW)
	public boolean isArchived(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(adoClass);

		cq.where(cb.and(cb.equal(from.get(InfrastructureAdo.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));

		long count = em.createQuery(cq).getSingleResult();

		return count > 0;
	}

	protected void checkInfraDataLocked() {
		if (!featureConfiguration.isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.infrastructureDataLocked));
		}
	}

	// todo this can be moved up
	@PermitAll
	public long count(CRITERIA criteria) {
		return service.count((cb, root) -> service.buildCriteriaFilter(criteria, cb, root));
	}

	@RightsAllowed({
		UserRight._INFRASTRUCTURE_VIEW })
	public boolean isUsedInOtherInfrastructureData(Collection<String> uuids) {
		return false;
	}

	@RightsAllowed({
		UserRight._INFRASTRUCTURE_VIEW })
	public boolean hasArchivedParentInfrastructure(Collection<String> uuids) {
		return false;
	}

	protected abstract List<ADO> findDuplicates(DTO dto, boolean includeArchived);

	// todo implement toDto() here

	@Override
	@RightsAllowed({
		UserRight._INFRASTRUCTURE_VIEW })
	public void validate(@Valid DTO dto) throws ValidationRuntimeException {
		// todo we do not run any generic validation logic for infra yet
	}

	@Override
	protected void pseudonymizeDto(ADO source, DTO dto, Pseudonymizer pseudonymizer, boolean inJurisdiction) {
		// we do not pseudonymize infra data
	}

	@Override
	protected void restorePseudonymizedDto(DTO dto, DTO existingDto, ADO entity, Pseudonymizer pseudonymizer) {
		// we do not pseudonymize infra data
	}
}

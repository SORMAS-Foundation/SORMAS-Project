package de.symeda.sormas.backend.infrastructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.InfrastructureFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

public abstract class AbstractInfrastructureFacadeEjb<ADO extends InfrastructureAdo, DTO extends InfrastructureDto, INDEX_DTO extends Serializable, REF_DTO extends InfrastructureDataReferenceDto, SRV extends AbstractInfrastructureAdoService<ADO, CRITERIA>, CRITERIA extends BaseCriteria>
	extends AbstractBaseEjb<ADO, DTO, INDEX_DTO, REF_DTO, SRV, CRITERIA>
	implements InfrastructureFacade<DTO, INDEX_DTO, REF_DTO, CRITERIA> {

	protected FeatureConfigurationFacadeEjb featureConfiguration;
	private String duplicateErrorMessageProperty;

	protected AbstractInfrastructureFacadeEjb() {
		super();
	}

	protected AbstractInfrastructureFacadeEjb(
		Class<ADO> adoClass,
		Class<DTO> dtoClass,
		SRV service,
		FeatureConfigurationFacadeEjb featureConfiguration,
		UserService userService,
		String duplicateErrorMessageProperty) {
		super(adoClass, dtoClass, service, userService);
		this.featureConfiguration = featureConfiguration;
		this.duplicateErrorMessageProperty = duplicateErrorMessageProperty;
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
	public List<DTO> getByUuids(List<String> uuids) {
		return service.getByUuids(uuids).stream().map(this::toDto).collect(Collectors.toList());
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
		return doSave(dto, allowMerge, true, true, duplicateErrorMessageProperty);
	}

	@RightsAllowed(UserRight._SYSTEM)
	public DTO saveFromCentral(DTO dtoToSave) {
		// merge, but do not include archived data (we consider archive data to be completely broken)
		// also ignore change date as merging will always cause the date to be newer to what is present in central
		return doSave(dtoToSave, true, false, false, duplicateErrorMessageProperty);
	}

	protected DTO doSave(DTO dtoToSave, boolean allowMerge, boolean includeArchived, boolean checkChangeDate, String duplicateErrorMessageProperty) {
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
					return mergeAndPersist(dtoToSave, duplicates, checkChangeDate);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(duplicateErrorMessageProperty));
				}
			}
		}
		return persistEntity(dtoToSave, existingEntity, checkChangeDate);
	}

	protected DTO persistEntity(DTO dto, ADO entityToPersist, boolean checkChangeDate) {
		entityToPersist = fillOrBuildEntity(dto, entityToPersist, checkChangeDate);
		service.ensurePersisted(entityToPersist);
		return toDto(entityToPersist);
	}

	protected DTO mergeAndPersist(DTO dtoToSave, List<ADO> duplicates, boolean checkChangeDate) {
		ADO existingEntity = duplicates.get(0);
		DTO existingDto = toDto(existingEntity);
		DtoHelper.copyDtoValues(existingDto, dtoToSave, true);
		return persistEntity(dtoToSave, existingEntity, checkChangeDate);
	}

	@Override
	@RightsAllowed(UserRight._INFRASTRUCTURE_ARCHIVE)
	public void archive(String uuid) {
		// todo this should be really in the parent but right now there the setter for archived is not available there
		checkInfraDataLocked();
		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(true);
			service.ensurePersisted(ado);
		}
	}

	@RightsAllowed(UserRight._INFRASTRUCTURE_ARCHIVE)
	public void dearchive(String uuid) {
		checkInfraDataLocked();
		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(false);
			service.ensurePersisted(ado);
		}
	}

	@RightsAllowed(UserRight._INFRASTRUCTURE_ARCHIVE)
	public List<String> archive(List<String> entityUuids) {
		List<String> archivedEntityUuids = new ArrayList<>();
		entityUuids.forEach(entityUuid -> {
			if (!isUsedInOtherInfrastructureData(Arrays.asList(entityUuid))) {
				archive(entityUuid);
				archivedEntityUuids.add(entityUuid);
			}
		});
		return archivedEntityUuids;
	}

	@RightsAllowed(UserRight._INFRASTRUCTURE_ARCHIVE)
	public List<String> dearchive(List<String> entityUuids) {
		List<String> dearchivedEntityUuids = new ArrayList<>();
		entityUuids.forEach(entityUuid -> {
			if (!hasArchivedParentInfrastructure(Arrays.asList(entityUuid))) {
				dearchive(entityUuid);
				dearchivedEntityUuids.add(entityUuid);
			}
		});
		return dearchivedEntityUuids;
	}

	protected void checkInfraDataLocked() {
		if (!featureConfiguration.isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.infrastructureDataLocked));
		}
	}

	// todo this can be moved up
	@RightsAllowed({
		UserRight._INFRASTRUCTURE_VIEW,
		UserRight._SYSTEM })
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
}

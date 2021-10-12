package de.symeda.sormas.backend.infrastructure;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractInfrastructureEjb<ADO extends InfrastructureAdo, DTO extends EntityDto, INDEX_DTO extends Serializable, REF_DTO extends ReferenceDto, SRV extends AbstractInfrastructureAdoService<ADO, CRITERIA>, CRITERIA extends BaseCriteria>
	extends AbstractBaseEjb<ADO, DTO, INDEX_DTO, REF_DTO, SRV, CRITERIA> {

	protected FeatureConfigurationFacadeEjb featureConfiguration;

	protected AbstractInfrastructureEjb() {
		super();
	}

	protected AbstractInfrastructureEjb(SRV service, FeatureConfigurationFacadeEjb featureConfiguration) {
		super(service);
		this.featureConfiguration = featureConfiguration;
	}

	protected DTO save(DTO dtoToSave, boolean allowMerge, String duplicateErrorMessageProperty) {
		checkInfraDataLocked();
		if (dtoToSave == null) {
			return null;
		}
		ADO existingEntity = service.getByUuid(dtoToSave.getUuid());

		if (existingEntity == null) {
			List<ADO> duplicates = findDuplicates(dtoToSave);
			if (!duplicates.isEmpty()) {
				if (allowMerge) {
					return mergeAndPersist(dtoToSave, duplicates);
				} else {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(duplicateErrorMessageProperty));
				}
			}
		}
		return persistEntity(dtoToSave, existingEntity);
	}

	@Override
	public void archive(String uuid) {
		// todo this should be really in the parent but right now there the setter for archived is not available there
		checkInfraDataLocked();
		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(true);
			service.ensurePersisted(ado);
		}
	}

	public void dearchive(String uuid) {
		checkInfraDataLocked();

		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(false);
			service.ensurePersisted(ado);
		}
	}

	protected void checkInfraDataLocked() {
		if (!featureConfiguration.isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.infrastructureDataLocked));
		}
	}
}

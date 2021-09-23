package de.symeda.sormas.backend.infrastructure;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBaseEjb;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;

public abstract class AbstractInfrastructureEjb<DTO extends InfrastructureAdo, SRV extends AbstractInfrastructureAdoService<DTO>>
	extends AbstractBaseEjb<DTO, SRV> {

	protected FeatureConfigurationFacadeEjb featureConfiguration;

	protected AbstractInfrastructureEjb() {
		super();
	}

	protected AbstractInfrastructureEjb(SRV service, FeatureConfigurationFacadeEjb featureConfiguration) {
		super(service);
		this.featureConfiguration = featureConfiguration;
	}

	@Override
	public void archive(String uuid) {
		// todo this should be really in the parent but right now there the setter for archived is not available there
		DTO dto = service.getByUuid(uuid);
		if (dto != null) {
			dto.setArchived(true);
			service.ensurePersisted(dto);
		}
	}

	public void dearchive(String uuid) {
		if (!featureConfiguration.isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.infrastructureDataLocked));
		}
		doDearchive(uuid);
	}

	protected void dearchiveUnchecked(String uuid) {
		doDearchive(uuid);
	}

	private void doDearchive(String uuid) {
		DTO dto = service.getByUuid(uuid);
		if (dto != null) {
			dto.setArchived(false);
			service.ensurePersisted(dto);
		}
	}

}

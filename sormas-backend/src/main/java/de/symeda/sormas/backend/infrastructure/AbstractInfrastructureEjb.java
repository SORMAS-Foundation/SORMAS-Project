package de.symeda.sormas.backend.infrastructure;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractBaseEjb;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.InfrastructureAdo;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;

public abstract class AbstractInfrastructureEjb<ADO extends InfrastructureAdo, SRV extends AbstractInfrastructureAdoService<ADO>>
	extends AbstractBaseEjb<ADO, SRV> {

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
		checkInfraDataLocked();
		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(true);
			service.ensurePersisted(ado);
		}
	}

	public void dearchive(String uuid) {
		checkInfraDataLocked();
		doDearchive(uuid);
	}

	protected void dearchiveUnchecked(String uuid) {
		doDearchive(uuid);
	}

	private void checkInfraDataLocked() {
		if (!featureConfiguration.isFeatureEnabled(FeatureType.EDIT_INFRASTRUCTURE_DATA)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.infrastructureDataLocked));
		}
	}

	private void doDearchive(String uuid) {
		ADO ado = service.getByUuid(uuid);
		if (ado != null) {
			ado.setArchived(false);
			service.ensurePersisted(ado);
		}
	}
}

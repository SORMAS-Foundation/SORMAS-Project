package de.symeda.sormas.ui;

import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public abstract class AbstractUiBeanTest extends AbstractBeanTest {

    @Override
    public void init() {
        super.init();

		createFeatureConfigurationForSormasUserSave();
        
        MockProducer.mockProperty(ConfigFacadeEjb.CSV_SEPARATOR, ",");
        MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_EPID_PREFIX, "ng");

        FacadeProviderMock.MockFacadeProvider(this);
    }

	private void createFeatureConfigurationForSormasUserSave() {
		FeatureConfigurationIndexDto featureConfigurationKeycloak =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, false, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfigurationKeycloak, FeatureType.KEYCLOAK_TO_SORMAS_USER_SYNC);
	}
}

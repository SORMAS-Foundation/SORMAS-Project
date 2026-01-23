package de.symeda.sormas.ui;

import de.symeda.sormas.api.systemconfiguration.Config;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;

public abstract class AbstractUiBeanTest extends AbstractBeanTest {

	@Override
	public void init() {
		super.init();

		MockProducer.mockProperty(Config.CSV_SEPARATOR, ",");
		MockProducer.mockProperty(Config.COUNTRY_EPID_PREFIX, "ng");

		FacadeProviderMock.MockFacadeProvider(this);
	}
}

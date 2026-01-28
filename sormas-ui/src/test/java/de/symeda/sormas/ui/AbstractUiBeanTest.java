package de.symeda.sormas.ui;

import de.symeda.sormas.api.systemconfiguration.Config;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;

public abstract class AbstractUiBeanTest extends AbstractBeanTest {

	@Override
	public void init() {
		super.init();

		FacadeProviderMock.MockFacadeProvider(this);
	}
}

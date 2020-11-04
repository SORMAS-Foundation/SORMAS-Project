package de.symeda.sormas.backend.docgeneration;

import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class AbstractDocGenerationTest extends AbstractBeanTest {

	protected void resetCustomPath() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, getClass().getResource("/").getPath());
	}
}

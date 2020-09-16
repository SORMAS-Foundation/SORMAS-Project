package de.symeda.sormas.backend.docgeneration;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class QuarantineOrderFacadeEjbTest extends AbstractBeanTest {

	private QuarantineOrderFacade quarantineOrderFacadeEjb;

	@Before
	public void setup() {
		quarantineOrderFacadeEjb = getQuarantineOrderFacade();
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, getClass().getResource("/").getPath());
	}

	@Test
	public void generateQuarantineOrder() {
		quarantineOrderFacadeEjb.getGeneratedDocument("Quarantine.docx", "", new Properties());
	}
}

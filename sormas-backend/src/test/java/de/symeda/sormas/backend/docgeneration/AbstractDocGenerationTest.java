package de.symeda.sormas.backend.docgeneration;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class AbstractDocGenerationTest extends AbstractBeanTest {

	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	protected void resetCustomPath() throws URISyntaxException {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, Paths.get(getClass().getResource("/").toURI()).toAbsolutePath().toString());
	}
}

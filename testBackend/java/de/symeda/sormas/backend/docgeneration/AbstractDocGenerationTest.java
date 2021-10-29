package de.symeda.sormas.backend.docgeneration;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.junit.After;

import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public abstract class AbstractDocGenerationTest extends AbstractBeanTest {

	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	protected void reset() throws URISyntaxException {
		resetCustomPath();
		resetDefaultNullReplacement();
	}

	protected void resetCustomPath() throws URISyntaxException {
		MockProducer.getProperties()
			.setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, Paths.get(getClass().getResource("/").toURI()).toAbsolutePath().toString());
	}

	protected void setNullReplacement(String nullReplacement) {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.DOCGENERATION_NULL_REPLACEMENT, nullReplacement);
	}

	protected void resetDefaultNullReplacement() {
		MockProducer.getProperties().remove(ConfigFacadeEjb.DOCGENERATION_NULL_REPLACEMENT);
	}

	@After
	public void teardown() throws URISyntaxException {
		reset();
	}
}

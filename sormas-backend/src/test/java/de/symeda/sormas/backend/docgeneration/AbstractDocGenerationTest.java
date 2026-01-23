package de.symeda.sormas.backend.docgeneration;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.AfterEach;

import de.symeda.sormas.api.docgeneneration.DocumentTemplateReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.systemconfiguration.Config;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;

public abstract class AbstractDocGenerationTest extends AbstractBeanTest {

	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	protected void reset() throws URISyntaxException {
		resetCustomPath();
		resetDefaultNullReplacement();
	}

	protected void resetCustomPath() throws URISyntaxException {
		MockProducer.getProperties()
			.setProperty(Config.CUSTOM_FILES_PATH, Paths.get(getClass().getResource("/").toURI()).toAbsolutePath().toString());
	}

	protected void setNullReplacement(String nullReplacement) {
		MockProducer.getProperties().setProperty(Config.DOCGENERATION_NULL_REPLACEMENT, nullReplacement);
	}

	protected void resetDefaultNullReplacement() {
		MockProducer.getProperties().remove(Config.DOCGENERATION_NULL_REPLACEMENT);
	}

	@AfterEach
	public void teardown() throws URISyntaxException {
		reset();
	}

	protected DocumentTemplate createDocumentTemplate(DocumentWorkflow workflow, String templateFileName) {
		DocumentTemplate template = new DocumentTemplate();
		template.setUuid(DataHelper.createUuid());
		template.setFileName(templateFileName);
		template.setWorkflow(workflow);

		getDocumentTemplateService().persist(template);

		return template;
	}

	protected static DocumentTemplateReferenceDto toReference(DocumentTemplate documentTemplate) {
		return new DocumentTemplateReferenceDto(documentTemplate.getUuid(), documentTemplate.getFileName());
	}
}

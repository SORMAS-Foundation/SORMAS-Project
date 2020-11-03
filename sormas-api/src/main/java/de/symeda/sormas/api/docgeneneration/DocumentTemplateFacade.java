package de.symeda.sormas.api.docgeneneration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import de.symeda.sormas.api.EntityDto;

public interface DocumentTemplateFacade {

	byte[] generateDocument(String templateName, EntityDto entityDto, Properties extraProperties) throws IOException;

	List<String> getAvailableTemplates();

	boolean isExistingTemplate(String templateName);

	List<String> getAdditionalVariables(String templateName) throws IOException;

	void writeQuarantineTemplate(String templateName, byte[] document) throws IOException;

	boolean deleteQuarantineTemplate(String templateName);

	byte[] getTemplate(String templateName) throws IOException;
}

package de.symeda.sormas.api.docgeneneration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Remote;

import de.symeda.sormas.api.EntityDto;

@Remote
public interface DocumentTemplateFacade {

	byte[] generateDocumentFromEntities(
		DocumentWorkflow documentWorkflow,
		String templateName,
		Map<String, EntityDto> entities,
		Properties extraProperties)
		throws IOException;

	byte[] generateDocument(DocumentWorkflow documentWorkflow, String templateName, Properties properties) throws IOException;

	List<String> getAvailableTemplates(DocumentWorkflow documentWorkflow);

	List<String> getAdditionalVariables(DocumentWorkflow documentWorkflow, String templateName) throws IOException;

	boolean isExistingTemplate(DocumentWorkflow documentWorkflow, String templateName);

	void writeDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName, byte[] document) throws IOException;

	boolean deleteDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName);

	byte[] getDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName) throws IOException;
}

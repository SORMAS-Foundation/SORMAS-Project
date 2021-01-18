package de.symeda.sormas.api.docgeneneration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Remote;

@Remote
public interface DocumentTemplateFacade {

	byte[] generateDocumentDocxFromEntities(
		DocumentWorkflow documentWorkflow,
		String templateName,
		Map<String, Object> entities,
		Properties extraProperties)
		throws IOException;

	String generateDocumentTxtFromEntities(
		DocumentWorkflow documentWorkflow,
		String templateName,
		Map<String, Object> entities,
		Properties extraProperties)
		throws IOException;

	List<String> getAvailableTemplates(DocumentWorkflow documentWorkflow);

	DocumentVariables getDocumentVariables(DocumentWorkflow documentWorkflow, String templateName) throws IOException;

	boolean isExistingTemplate(DocumentWorkflow documentWorkflow, String templateName);

	void writeDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName, byte[] document) throws IOException;

	boolean deleteDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName);

	byte[] getDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName) throws IOException;
}

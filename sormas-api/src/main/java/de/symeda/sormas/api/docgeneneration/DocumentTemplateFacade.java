package de.symeda.sormas.api.docgeneneration;

import java.util.List;
import java.util.Properties;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;

@Remote
public interface DocumentTemplateFacade {

	byte[] generateDocumentDocxFromEntities(
		DocumentTemplateReferenceDto templateReference,
		DocumentTemplateEntities entities,
		Properties extraProperties)
		throws DocumentTemplateException;

	String generateDocumentTxtFromEntities(
		DocumentTemplateReferenceDto templateReference,
		DocumentTemplateEntities entities,
		Properties extraProperties)
		throws DocumentTemplateException;

	List<DocumentTemplateDto> getAvailableTemplates(DocumentWorkflow documentWorkflow, Disease disease);

	DocumentVariables getDocumentVariables(DocumentTemplateReferenceDto templateReference) throws DocumentTemplateException;

	boolean isExistingTemplate(DocumentWorkflow documentWorkflow, String templateName, Disease disease);

	void writeDocumentTemplate(DocumentWorkflow documentWorkflow, String templateName, Disease disease, byte[] document)
		throws DocumentTemplateException;

	boolean deleteDocumentTemplate(DocumentTemplateReferenceDto templateReference) throws DocumentTemplateException;

	byte[] getDocumentTemplateContent(DocumentTemplateReferenceDto templateReference) throws DocumentTemplateException;
}

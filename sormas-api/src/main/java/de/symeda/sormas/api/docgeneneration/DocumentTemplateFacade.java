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

	List<DocumentTemplateDto> getAvailableTemplates(DocumentTemplateCriteria documentTemplateCriteria);

	boolean isExistingTemplateFile(DocumentWorkflow documentWorkflow, Disease disease, String templateName);

	DocumentVariables getDocumentVariables(DocumentTemplateReferenceDto templateReference) throws DocumentTemplateException;

	DocumentTemplateDto saveDocumentTemplate(DocumentTemplateDto template, byte[] document) throws DocumentTemplateException;

	boolean deleteDocumentTemplate(DocumentTemplateReferenceDto templateReference, DocumentWorkflow documentWorkflow)
		throws DocumentTemplateException;

	byte[] getDocumentTemplateContent(DocumentTemplateReferenceDto templateReference) throws DocumentTemplateException;
}

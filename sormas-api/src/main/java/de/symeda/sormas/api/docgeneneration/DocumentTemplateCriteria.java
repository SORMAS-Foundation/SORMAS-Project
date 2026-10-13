package de.symeda.sormas.api.docgeneneration;

import javax.annotation.Nullable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.survey.SurveyReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class DocumentTemplateCriteria extends BaseCriteria {

	public static final String DOCUMENT_WORKFLOW = "documentWorkflow";
	public static final String DISEASE = "disease";
	public static final String SURVEY = "surveyReference";

	private DocumentWorkflow documentWorkflow;
	@Nullable
	private Disease disease;
	private SurveyReferenceDto surveyReference;

	public DocumentTemplateCriteria(DocumentWorkflow documentWorkflow, Disease disease, SurveyReferenceDto surveyReference) {
		this.documentWorkflow = documentWorkflow;
		this.disease = disease;
		this.surveyReference = surveyReference;
	}

	public DocumentWorkflow getDocumentWorkflow() {
		return documentWorkflow;
	}

	public DocumentTemplateCriteria setDocumentWorkflow(DocumentWorkflow documentWorkflow) {
		this.documentWorkflow = documentWorkflow;
		return this;
	}

	public Disease getDisease() {
		return disease;
	}

	public DocumentTemplateCriteria setDisease(Disease disease) {
		this.disease = disease;
		return this;
	}

	public SurveyReferenceDto getSurveyReference() {
		return surveyReference;
	}

	public DocumentTemplateCriteria setSurveyReference(SurveyReferenceDto surveyReference) {
		this.surveyReference = surveyReference;
		return this;
	}
}

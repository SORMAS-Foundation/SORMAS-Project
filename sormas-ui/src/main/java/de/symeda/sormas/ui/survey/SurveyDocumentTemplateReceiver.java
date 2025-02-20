package de.symeda.sormas.ui.survey;

import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.survey.SurveyReferenceDto;
import de.symeda.sormas.ui.importer.DocumentTemplateReceiver;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SurveyDocumentTemplateReceiver extends DocumentTemplateReceiver {

	private final SurveyReferenceDto surveyReference;

	public SurveyDocumentTemplateReceiver(DocumentWorkflow documentWorkflow, SurveyReferenceDto surveyReference) {
		super(documentWorkflow);
		this.surveyReference = surveyReference;
	}

	@Override
	protected void writeDocumentTemplate(DocumentTemplateDto documentTemplateDto, byte[] fileContent) throws DocumentTemplateException {
		FacadeProvider.getSurveyFacade().uploadDocumentTemplate(surveyReference, documentTemplateDto, fileContent);
	}

	@Override
	protected void duplicateTemplateFileResponse(String filename) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingFileExists),
			new Label(String.format(I18nProperties.getString(Strings.infoDocumentAlreadyExistsCannotUploadAnotherOne), filename)),
			I18nProperties.getCaption(Captions.actionOkay),
			null);
	}

}

package de.symeda.sormas.ui.survey;

import java.io.IOException;
import java.nio.file.Files;

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

	private void missingTokenVariableResponse(String filename) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingMissingTokenVariableInTemplate),
			new Label(String.format(I18nProperties.getString(Strings.infoMissingTokenVariable), filename)),
			I18nProperties.getCaption(Captions.actionOkay),
			null);
	}

	@Override
	public void uploadSucceeded(com.vaadin.v7.ui.Upload.SucceededEvent succeededEvent) {
		if (super.getfName() == null) {
			return;
		}

		// Check for duplicate files
		boolean validFile = true;
		if (FacadeProvider.getDocumentTemplateFacade().isExistingTemplateFile(DocumentWorkflow.SURVEY_DOCUMENT, null, this.getfName())) {
			duplicateTemplateFileResponse(super.getfName());
			validFile = false;
		}

		// Check existing token variable in the uploaded file
		try {
			if (!FacadeProvider.getSurveyFacade().validateSurveyDocumentTemplate(Files.readAllBytes(this.getFile().toPath()))) {
				missingTokenVariableResponse(succeededEvent.getFilename());
				validFile = false;
			}
		} catch (DocumentTemplateException | IOException e) {
			throw new RuntimeException(e);
		}

		if (validFile) {
			writeTemplateFile();
		}
	}
}

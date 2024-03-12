package de.symeda.sormas.ui.importer;

import static de.symeda.sormas.ui.docgeneration.DocGenerationHelper.isFileSizeLimitExceeded;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.Upload.StartedEvent;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocumentTemplateReceiver
	implements com.vaadin.v7.ui.Upload.Receiver, com.vaadin.v7.ui.Upload.StartedListener, com.vaadin.v7.ui.Upload.SucceededListener {

	private File file;
	private String fName;
	private final DocumentWorkflow documentWorkflow;

	public DocumentTemplateReceiver(DocumentWorkflow documentWorkflow) {
		this.documentWorkflow = documentWorkflow;
	}

	@Override
	public OutputStream receiveUpload(String fileName, String mimeType) {
		fName = fileName;
		// Reject empty files
		if (fileName == null || fileName.isEmpty()) {
			file = null;
			new Notification(
				I18nProperties.getString(Strings.headingNoFile),
				I18nProperties.getString(Strings.messageNoDocumentTemplateUploadFile),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			// Workaround because returning null here throws an uncatchable UploadException
			return new ByteArrayOutputStream();
		}

		try {
			String newFileName = ImportExportUtils.TEMP_FILE_PREFIX + "_template_upload" + DateHelper.formatDateForExport(new Date()) + "_"
				+ DataHelper.getShortUuid(UiUtil.getUserUuid()) + ".docx";
			file = new File(Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath()).resolve(newFileName).toString());

			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			file = null;
			new Notification(
				I18nProperties.getString(Strings.headingImportError),
				I18nProperties.getString(Strings.messageImportError),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			// Workaround because returning null here throws an uncatchable UploadException
			return new ByteArrayOutputStream();
		}
	}

	@Override
	public void uploadStarted(StartedEvent startedEvent) {
		long fileSizeLimitMb = FacadeProvider.getConfigFacade().getDocumentUploadSizeLimitMb();

		if (isFileSizeLimitExceeded(startedEvent.getContentLength(), fileSizeLimitMb)) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.fileTooBig, fileSizeLimitMb));
		}
	}

	@Override
	public void uploadSucceeded(com.vaadin.v7.ui.Upload.SucceededEvent succeededEvent) {
		if (file == null) {
			return;
		}

		// Check for duplicate files
		if (FacadeProvider.getDocumentTemplateFacade().isExistingTemplate(documentWorkflow, fName)) {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingFileExists),
				new Label(String.format(I18nProperties.getString(Strings.infoDocumentAlreadyExists), fName)),
				I18nProperties.getCaption(Captions.actionConfirm),
				I18nProperties.getCaption(Captions.actionCancel),
				null,
				ok -> {
					if (ok) {
						writeTemplateFile();
					}
				});
		} else {
			writeTemplateFile();
		}
	}

	private void writeTemplateFile() {
		try {
			byte[] filecontent = Files.readAllBytes(file.toPath());
			FacadeProvider.getDocumentTemplateFacade().writeDocumentTemplate(documentWorkflow, fName, filecontent);
			VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getString(Strings.headingUploadSuccess),
				I18nProperties.getString(Strings.messageUploadSuccessful));
		} catch (Exception e) {
			new Notification(I18nProperties.getString(Strings.headingImportFailed), e.getMessage(), Notification.Type.ERROR_MESSAGE, false)
				.show(Page.getCurrent());
		}
	}
}

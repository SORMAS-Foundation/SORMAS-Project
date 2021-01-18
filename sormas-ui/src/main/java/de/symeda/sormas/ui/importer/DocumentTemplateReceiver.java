package de.symeda.sormas.ui.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import com.vaadin.ui.Upload;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DocumentTemplateReceiver implements com.vaadin.ui.Upload.Receiver, Upload.SucceededListener {

	private File file;
	private String fName;

	@Override
	public OutputStream receiveUpload(String fileName, String mimeType) {
		final FileOutputStream fos;
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
				+ DataHelper.getShortUuid(UserProvider.getCurrent().getUuid()) + ".docx";
			file = new File(Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath()).resolve(newFileName).toString());
			fos = new FileOutputStream(file);
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

		return fos;
	}

	@Override
	public void uploadSucceeded(com.vaadin.ui.Upload.SucceededEvent succeededEvent) {
		if (file == null) {
			return;
		}

		// Check for duplicate files
		if (FacadeProvider.getQuarantineOrderFacade().isExistingTemplate(fName)) {
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
			// This should be more general for reusability
			FacadeProvider.getQuarantineOrderFacade().writeQuarantineTemplate(fName, filecontent);
			VaadinUiUtil.showSimplePopupWindow(
				I18nProperties.getString(Strings.headingUploadSuccess),
				I18nProperties.getString(Strings.messageUploadSuccessful));
		} catch (IOException | IllegalArgumentException e) {
			new Notification(I18nProperties.getString(Strings.headingImportFailed), e.getMessage(), Notification.Type.ERROR_MESSAGE, false)
				.show(Page.getCurrent());
		}
	}
}

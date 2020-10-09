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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class TemplateReceiver implements com.vaadin.v7.ui.Upload.Receiver, com.vaadin.v7.ui.Upload.SucceededListener {

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
				I18nProperties.getString(Strings.messageNoCsvFile), // i18n required
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			// Workaround because returning null here throws an uncatchable UploadException
			return new ByteArrayOutputStream();
		}

		try {
			String newFileName = ImportExportUtils.TEMP_FILE_PREFIX + "_template_import" + DateHelper.formatDateForExport(new Date()) + "_"
				+ DataHelper.getShortUuid(UserProvider.getCurrent().getUuid()) + ".docx";
			file = new File(Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath()).resolve(newFileName).toString());
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			file = null;
			new Notification(
				I18nProperties.getString(Strings.headingImportError),
				I18nProperties.getString(Strings.messageImportFailed),
				Notification.Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			// Workaround because returning null here throws an uncatchable UploadException
			return new ByteArrayOutputStream();
		}

		return fos;
	}

	@Override
	public void uploadSucceeded(com.vaadin.v7.ui.Upload.SucceededEvent succeededEvent) {
		// Success! Do something here
		if (file == null) {
			return;
		}

		// Check for duplicate files
		if (FacadeProvider.getQuarantineOrderFacade().isExistingTemplate(fName)) {
			VaadinUiUtil.showConfirmationPopup("i18n File already exists", new Label("Override File? i18n"), "Continue", "Cancel", null, ok -> {
				if (ok.booleanValue() == true) {
					byte[] filecontent;
					try {
						filecontent = Files.readAllBytes(file.toPath());
						// This should be more general for reusability
						FacadeProvider.getQuarantineOrderFacade().writeQuarantineTemplate(fName, filecontent);
					} catch (IOException e) {
						e.printStackTrace();
						new Notification(
							I18nProperties.getString(Strings.headingImportFailed),
							I18nProperties.getString(Strings.messageImportFailed),
							Notification.Type.ERROR_MESSAGE,
							false).show(Page.getCurrent());
						return;
					} catch (ValidationException e) {
						e.printStackTrace();
						new Notification(
							I18nProperties.getString("i18n import failed"),
							I18nProperties.getString("e.getMessage() <- doesnt work :("),
							Notification.Type.ERROR_MESSAGE,
							false).show(Page.getCurrent());
						return;
					}

					VaadinUiUtil.showSimplePopupWindow("Success! i18n required", "Template has been uploaded successfully.");
				}
			});
		} else {
			byte[] filecontent;
			try {
				filecontent = Files.readAllBytes(file.toPath());
				// This should be more general for reusability
				FacadeProvider.getQuarantineOrderFacade().writeQuarantineTemplate(fName, filecontent);
			} catch (IOException e) {
				e.printStackTrace();
				new Notification(
					I18nProperties.getString(Strings.headingImportFailed),
					I18nProperties.getString(Strings.messageImportFailed),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
				return;
			} catch (ValidationException e) {
				e.printStackTrace();
				new Notification(
					I18nProperties.getString("i18n import failed"),
					I18nProperties.getString("e.getMessage() <- doesnt work :("),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
				return;
			}

			VaadinUiUtil.showSimplePopupWindow("Success! i18n required", "Template has been uploaded successfully.");
		}
	}
}

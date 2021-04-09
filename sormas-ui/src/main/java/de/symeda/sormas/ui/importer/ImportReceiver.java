package de.symeda.sormas.ui.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Date;
import java.util.function.Consumer;

import de.symeda.sormas.ui.SormasUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.ui.Upload.Receiver;
import com.vaadin.v7.ui.Upload.SucceededEvent;
import com.vaadin.v7.ui.Upload.SucceededListener;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UserProvider;

@SuppressWarnings("serial")
public class ImportReceiver implements Receiver, SucceededListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private File file;
	private String fileNameAddition;
	Consumer<File> fileConsumer;

	public ImportReceiver(String fileNameAddition, Consumer<File> fileConsumer) {
		this.fileNameAddition = fileNameAddition;
		this.fileConsumer = fileConsumer;
	}

	@Override
	public OutputStream receiveUpload(String fileName, String mimeType) {
		// Reject empty files
		if (fileName == null || fileName.isEmpty()) {
			file = null;
			new Notification(
				I18nProperties.getString(Strings.headingNoFile),
				I18nProperties.getString(Strings.messageNoCsvFile),
				Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			// Workaround because returning null here throws an uncatchable UploadException
			return new ByteArrayOutputStream();
		}
		// Reject all files except .csv files - we also need to accept excel files here
		if (!(mimeType.equals("text/csv") || mimeType.equals("application/vnd.ms-excel"))) {
			file = null;
			new Notification(
				I18nProperties.getString(Strings.headingWrongFileType),
				I18nProperties.getString(Strings.messageWrongFileType),
				Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			// Workaround because returning null here throws an uncatchable UploadException
			return new ByteArrayOutputStream();
		}

		final FileOutputStream fos;
		try {
			String newFileName = ImportExportUtils.TEMP_FILE_PREFIX + fileNameAddition + DateHelper.formatDateForExport(new Date()) + "_"
				+ DataHelper.getShortUuid(UserProvider.getCurrent().getUuid()) + ".csv";
			file = new File(Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath()).resolve(newFileName).toString());
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			file = null;
			logger.error("Reading the file to import failed", e);
			new Notification(
				I18nProperties.getString(Strings.headingImportError),
				I18nProperties.getString(Strings.messageImportError),
				Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
			// Workaround because returning null here throws an uncatchable UploadException
			return new ByteArrayOutputStream();
		}

		return fos;
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {

		if (file == null) {
			return;
		}

		try {
			// Read file and create readers
			File csvFile = new File(file.getPath());
			if (!csvFile.exists()) {
				throw new FileNotFoundException("CSV file does not exist");
			}

			fileConsumer.accept(csvFile);
		} catch (IOException e) {
			new Notification(
				I18nProperties.getString(Strings.headingImportFailed),
				I18nProperties.getString(Strings.messageImportFailed),
				Type.ERROR_MESSAGE,
				false).show(Page.getCurrent());
		}
	}
}

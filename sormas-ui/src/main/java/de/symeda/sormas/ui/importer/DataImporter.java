package de.symeda.sormas.ui.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public abstract class DataImporter {

	protected static final String ERROR_COLUMN_NAME = I18nProperties.getCaption(Captions.importErrorDescription);
	protected static final Logger logger = LoggerFactory.getLogger(CaseImporter.class);

	protected Consumer<ImportResult> importedCallback;
	protected ImportProgressLayout progressLayout;
	protected Integer importFileLength;
	protected CSVReader csvReader;
	protected CSVWriter csvWriter;
	protected boolean cancelAfterCurrent;
	protected boolean hasImportError;
	protected UserReferenceDto currentUser;
	protected UI currentUI;
	protected String errorReportFilePath;
	protected Button downloadErrorReportButton;

	public DataImporter(File inputFile, Button downloadErrorReportButton, UserReferenceDto currentUser, UI currentUI) throws IOException {
		this.currentUser = currentUser;
		this.currentUI = currentUI;
		this.downloadErrorReportButton = downloadErrorReportButton;

		if (!inputFile.exists()) {
			throw new FileNotFoundException("CSV file does not exist");
		}

		// Generate the error report file
		Path exportDirectory = Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath());
		Path errorReportFilePath = exportDirectory.resolve(ImportExportUtils.TEMP_FILE_PREFIX + "_error_report_" 
				+ DataHelper.getShortUuid(currentUser.getUuid()) + "_" 
				+ DateHelper.formatDateForExport(new Date()) + ".csv");
		this.errorReportFilePath = errorReportFilePath.toString();
		// If the error report file already exists, delete it
		File errorReportFile = new File(errorReportFilePath.toString());
		if (errorReportFile.exists()) {
			errorReportFile.delete();
		}

		// Remove FileDownloader extension from "Download Error Report" button
		downloadErrorReportButton.setEnabled(false);
		for (int i = 0; i < downloadErrorReportButton.getExtensions().size(); i++) {
			Extension ext = downloadErrorReportButton.getExtensions().iterator().next();
			downloadErrorReportButton.removeExtension(ext);
		}	

		csvReader = CSVUtils.createCSVReader(new FileReader(inputFile.getPath()), FacadeProvider.getConfigFacade().getCsvSeparator());
		csvWriter = CSVUtils.createCSVWriter(new FileWriter(errorReportFile.getPath()), FacadeProvider.getConfigFacade().getCsvSeparator());

		progressLayout = new ImportProgressLayout(getImportFileLength(new FileReader(inputFile.getPath())), currentUI, new Runnable() {
			@Override
			public void run() {
				cancelImport();
			}
		});

		importedCallback = new Consumer<ImportResult>() {
			@Override
			public void accept(ImportResult result) {
				progressLayout.updateProgress(result);
			}
		};
	}

	public void startImport() {
		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getString(Strings.headingPointOfEntryImport));
		window.setWidth(800, Unit.PIXELS);
		window.setContent(progressLayout);
		window.setClosable(false);
		currentUI.addWindow(window);

		new ImportThread(this, importedCallback, window, progressLayout, errorReportFilePath, downloadErrorReportButton).start();
	}

	public int getImportFileLength(InputStreamReader inputReader) throws IOException {
		if (importFileLength != null) {
			return importFileLength;
		}

		try (CSVReader caseCountReader = CSVUtils.createCSVReader(inputReader, FacadeProvider.getConfigFacade().getCsvSeparator())) {
			// Initialize with -1 to not count header line
			importFileLength = -1;
			while (caseCountReader.readNext() != null) {
				importFileLength++;
			}
		}

		return importFileLength;
	}

	public ImportResultStatus importAllData(Consumer<ImportResult> importedCallback) throws IOException, InvalidColumnException, InterruptedException {
		this.importedCallback = importedCallback;

		// Build dictionary of column paths
		String[] headersLine = csvReader.readNext();
		List<String[]> headers = new ArrayList<>();
		for (String header : headersLine) {
			String[] headerPath = header.split("\\.");
			headers.add(headerPath);
		}

		// Write first line to the error report writer
		List<String> columnNames = new ArrayList<>();
		columnNames.add(ERROR_COLUMN_NAME);
		for (String column : headersLine) {
			columnNames.add(column);
		}
		csvWriter.writeNext(columnNames.toArray(new String[columnNames.size()]));

		// Create a new case for each line in the .csv file
		readNextLineFromCsv(headersLine, headers);

		csvReader.close();
		csvWriter.flush();
		csvWriter.close();

		if (cancelAfterCurrent) {
			if (!hasImportError) {
				return ImportResultStatus.CANCELED;
			} else {
				return ImportResultStatus.CANCELED_WITH_ERRORS;
			}
		} else if (hasImportError) {
			return ImportResultStatus.COMPLETED_WITH_ERRORS;
		} else {
			return ImportResultStatus.COMPLETED;
		}		
	}

	public void cancelImport() {
		cancelAfterCurrent = true;
	}

	protected void readNextLineFromCsv(String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException {
		if (cancelAfterCurrent) {
			return;
		}

		String[] nextLine = csvReader.readNext();
		if (nextLine != null) {
			importDataFromCsvLine(nextLine, headersLine, headers);
		}
	}

	protected abstract void importDataFromCsvLine(String[] nextLine, String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException;

	protected boolean insertRowIntoData(String[] row, List<String[]> headers, boolean ignoreEmptyEntries, BiFunction<String, String[], Exception> insertCallback) throws IOException, InvalidColumnException {
		boolean dataHasImportError = false;

		for (int i = 0; i < row.length; i++) {
			String entry = row[i];
			if (entry == null || entry.isEmpty()) {
				continue;
			}

			String[] entryHeaderPath = headers.get(i);
			// Error description column is ignored
			if (entryHeaderPath[0].equals(ERROR_COLUMN_NAME)) {
				continue;
			}

			if (!(ignoreEmptyEntries && (StringUtils.isEmpty(entry)))) {
				Exception exception = insertCallback.apply(entry, entryHeaderPath);
				if (exception != null) {
					if (exception instanceof ImportErrorException) {
						hasImportError = true;
						dataHasImportError = true;
						writeImportError(row, exception.getMessage());
						break;
					} else if (exception instanceof InvalidColumnException) {
						csvReader.close();
						csvWriter.flush();
						csvWriter.close();
						throw (InvalidColumnException) exception;
					}
				}
			}

		}

		return dataHasImportError;
	}

	protected void writeImportError(String[] errorLine, String message) throws IOException {
		List<String> errorLineAsList = new ArrayList<>();
		errorLineAsList.add(message);
		errorLineAsList.addAll(Arrays.asList(errorLine));
		csvWriter.writeNext(errorLineAsList.toArray(new String[errorLineAsList.size()]));
	}

	protected String buildHeaderPathString(String[] entryHeaderPath) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String headerPathElement : entryHeaderPath) {
			if (first) {
				sb.append(headerPathElement);
				first = false;
			} else {
				sb.append(".").append(headerPathElement);
			}
		}

		return sb.toString();
	}

	private class ImportThread extends Thread {
		private DataImporter importer;
		private Consumer<ImportResult> importedCallback;
		private Window window;
		private ImportProgressLayout progressLayout;
		private String errorReportFilePath;
		private Button downloadErrorReportButton;

		public ImportThread(DataImporter importer, Consumer<ImportResult> importedCallback, Window window,
				ImportProgressLayout progressLayout, String errorReportFilePath, Button downloadErrorReportButton) {
			this.importer = importer;
			this.importedCallback = importedCallback;
			this.window = window;
			this.progressLayout = progressLayout;
			this.errorReportFilePath = errorReportFilePath;
			this.downloadErrorReportButton = downloadErrorReportButton;
		}

		@Override
		public void run() {
			try {
				currentUI.setPollInterval(50);

				ImportResultStatus importResult = importer.importAllData(importedCallback);

				currentUI.access(new Runnable() {
					@Override
					public void run() {
						window.setClosable(true);
						progressLayout.makeClosable(() -> {
							window.close();
						});

						if (importResult == ImportResultStatus.COMPLETED) {
							progressLayout.displaySuccessIcon();
							progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageImportSuccessful));
						} else if (importResult == ImportResultStatus.COMPLETED_WITH_ERRORS) {
							progressLayout.displayWarningIcon();
							progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageImportPartiallySuccessful));
						} else if (importResult == ImportResultStatus.CANCELED) {
							progressLayout.displaySuccessIcon();
							progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageImportCanceled));
						} else {
							progressLayout.displayWarningIcon();
							progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageImportCanceledErrors));
						}								

						window.addCloseListener(e -> {
							if (importResult == ImportResultStatus.COMPLETED_WITH_ERRORS || importResult == ImportResultStatus.CANCELED_WITH_ERRORS) {
								StreamResource streamResource = DownloadUtil.createFileStreamResource(errorReportFilePath, "sormas_import_error_report.csv", "text/csv",
										I18nProperties.getString(Strings.headingErrorReportNotAvailable), I18nProperties.getString(Strings.messageErrorReportNotAvailable));
								FileDownloader fileDownloader = new FileDownloader(streamResource);
								fileDownloader.extend(downloadErrorReportButton);
								downloadErrorReportButton.setEnabled(true);
							}
						});

						currentUI.setPollInterval(-1);
					}
				});
			} catch (IOException | InterruptedException e) {
				currentUI.access(new Runnable() {
					@Override
					public void run() {
						window.setClosable(true);
						progressLayout.makeClosable(() -> {
							window.close();
						});
						progressLayout.displayErrorIcon();
						progressLayout.setInfoLabelText(I18nProperties.getString(Strings.messageImportFailedFull));
						currentUI.setPollInterval(-1);
					}
				});
			} catch (InvalidColumnException e) {
				currentUI.access(new Runnable() {
					@Override
					public void run() {
						window.setClosable(true);
						progressLayout.makeClosable(() -> {
							window.close();
						});
						progressLayout.displayErrorIcon();
						progressLayout.setInfoLabelText(String.format(I18nProperties.getString(Strings.messageImportInvalidColumn), e.getColumnName()));
						currentUI.setPollInterval(-1);
					}
				});
			}
		}
	}

}

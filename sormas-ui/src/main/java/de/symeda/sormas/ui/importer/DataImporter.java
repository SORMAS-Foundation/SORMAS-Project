package de.symeda.sormas.ui.importer;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;
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

	public DataImporter(File inputFile, UserReferenceDto currentUser, UI currentUI) throws IOException {
		this(inputFile, null, currentUser, currentUI);
	}

	public DataImporter(File inputFile, OutputStreamWriter errorReportWriter, UserReferenceDto currentUser, UI currentUI) throws IOException {
		this.currentUser = currentUser;
		this.currentUI = currentUI;

		if (!inputFile.exists()) {
			throw new FileNotFoundException("CSV file does not exist");
		}

		if (errorReportWriter == null) {
			Path exportDirectory = Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath());
			Path errorReportFilePath = exportDirectory.resolve(ImportExportUtils.TEMP_FILE_PREFIX + "_error_report_" 
					+ DataHelper.getShortUuid(currentUser.getUuid()) + "_" 
					+ DateHelper.formatDateForExport(new Date()) + ".csv");
			this.errorReportFilePath = errorReportFilePath.toString();

			File errorReportFile = new File(errorReportFilePath.toString());
			if (errorReportFile.exists()) {
				errorReportFile.delete();
			}

			errorReportWriter = new FileWriter(errorReportFile.getPath());
		}

		csvReader = CSVUtils.createCSVReader(new FileReader(inputFile.getPath()), FacadeProvider.getConfigFacade().getCsvSeparator());
		csvWriter = CSVUtils.createCSVWriter(errorReportWriter, FacadeProvider.getConfigFacade().getCsvSeparator());

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

	public void startImport(Consumer<StreamResource> addErrorReportToLayoutCallback) {
		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getString(Strings.headingPointOfEntryImport));
		window.setWidth(800, Unit.PIXELS);
		window.setContent(progressLayout);
		window.setClosable(false);
		currentUI.addWindow(window);

		new ImportThread(this, importedCallback, addErrorReportToLayoutCallback, window, progressLayout, errorReportFilePath).start();
	}

	public int getImportFileLength(InputStreamReader inputReader) throws IOException {
		if (importFileLength != null) {
			return importFileLength;
		}

		try (CSVReader caseCountReader = CSVUtils.createCSVReader(inputReader, FacadeProvider.getConfigFacade().getCsvSeparator())) {
			// Initialize with -2 to not count header lines
			importFileLength = -2;
			while (caseCountReader.readNext() != null) {
				importFileLength++;
			}
		}

		return importFileLength;
	}

	public ImportResultStatus importAllData(Consumer<ImportResult> importedCallback) throws IOException, InvalidColumnException, InterruptedException {
		this.importedCallback = importedCallback;

		// Build dictionary of entity headers
		List<String> entityHeaders = Arrays.asList(csvReader.readNext());

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
		readNextLineFromCsv(entityHeaders, headersLine, headers);

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

	protected void readNextLineFromCsv(List<String> entityHeaders, String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException {
		if (cancelAfterCurrent) {
			return;
		}

		String[] nextLine = csvReader.readNext();
		if (nextLine != null) {
			importDataFromCsvLine(nextLine, entityHeaders, headersLine, headers);
		}
	}

	protected abstract void importDataFromCsvLine(String[] nextLine, List<String> entityHeaders, String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected boolean executeDefaultInvokings(PropertyDescriptor pd, Object element, String entry, String[] entryHeaderPath) throws InvocationTargetException, IllegalAccessException, ParseException, ImportErrorException {
		Class<?> propertyType = pd.getPropertyType();

		if (propertyType.isEnum()) {
			pd.getWriteMethod().invoke(element, Enum.valueOf((Class<? extends Enum>) propertyType, entry.toUpperCase()));
			return true;
		}
		if (propertyType.isAssignableFrom(Date.class)) {
			pd.getWriteMethod().invoke(element, DateHelper.parseDateWithException(entry));
			return true;
		} 
		if (propertyType.isAssignableFrom(Integer.class)) {
			pd.getWriteMethod().invoke(element, Integer.parseInt(entry));
			return true;
		}
		if (propertyType.isAssignableFrom(Double.class)) {
			pd.getWriteMethod().invoke(element, Double.parseDouble(entry));
			return true;
		} 
		if (propertyType.isAssignableFrom(Float.class)) {
			pd.getWriteMethod().invoke(element, Float.parseFloat(entry));
			return true;
		} 
		if (propertyType.isAssignableFrom(Boolean.class) || propertyType.isAssignableFrom(boolean.class)) {
			pd.getWriteMethod().invoke(element, Boolean.parseBoolean(entry));
			return true;
		} 
		if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
			List<RegionReferenceDto> region = FacadeProvider.getRegionFacade().getByName(entry);
			if (region.isEmpty()) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildHeaderPathString(entryHeaderPath)));
			} else if (region.size() > 1) {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importRegionNotUnique, entry, buildHeaderPathString(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, region.get(0));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(UserReferenceDto.class)) {
			UserDto user = FacadeProvider.getUserFacade().getByUserName(entry);
			if (user != null) {
				pd.getWriteMethod().invoke(element, user.toReference());
				return true;
			} else {
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildHeaderPathString(entryHeaderPath)));
			}
		}
		if (propertyType.isAssignableFrom(String.class)) {
			pd.getWriteMethod().invoke(element, entry);
			return true;
		}

		return false;
	}

	protected boolean insertRowIntoData(String[] row, List<String> entityHeaders, List<String[]> headers, boolean ignoreEmptyEntries, Function<ImportColumnInformation, Exception> insertCallback) throws IOException, InvalidColumnException {
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
				Class<?> entityClass;
				switch (entityHeaders.get(i)) {
				case "Sample":
					entityClass = SampleDto.class;
					break;
				case "PathogenTest":
					entityClass = PathogenTestDto.class;
					break;
				default:
					entityClass = CaseDataDto.class;
					break;
				}
				Exception exception = insertCallback.apply(new ImportColumnInformation(entry, entryHeaderPath, entityClass));
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
		private Consumer<StreamResource> addErrorReportToLayoutCallback;
		private Window window;
		private ImportProgressLayout progressLayout;
		private String errorReportFilePath;

		public ImportThread(DataImporter importer, Consumer<ImportResult> importedCallback, Consumer<StreamResource> addErrorReportToLayoutCallback,
				Window window, ImportProgressLayout progressLayout, String errorReportFilePath) {
			this.importer = importer;
			this.importedCallback = importedCallback;
			this.addErrorReportToLayoutCallback = addErrorReportToLayoutCallback;
			this.window = window;
			this.progressLayout = progressLayout;
			this.errorReportFilePath = errorReportFilePath;
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
								addErrorReportToLayoutCallback.accept(streamResource);
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

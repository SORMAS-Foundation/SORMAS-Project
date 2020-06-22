package de.symeda.sormas.ui.importer;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

/**
 * Base class for all importers that are used to get data from CSV files into SORMAS.
 * 
 * These are the steps performed by the data importer (sub classes might add additional logic):
 * 1) Read the CSV file from the passed file path and open an error report file
 * 2) Read the header row(s) from the CSV and build a list of properties based on its columns
 * 3) Insert every line of data into the object using a callback
 * 4) Present the result of the import and, if errors occurred, an error report file to the user
 */
public abstract class DataImporter {

	protected static final String ERROR_COLUMN_NAME = I18nProperties.getCaption(Captions.importErrorDescription);

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * The input CSV file that contains the data to be imported.
	 */
	private File inputFile;
	/**
	 * Whether or not the import file is supposed to have an additional row on top containing the entity name.
	 * This is necessary for importers that also import data that is not referenced in the root entity,
	 * e.g. samples for cases.
	 */
	private boolean hasEntityClassRow;
	/**
	 * The file path to the generated error report file that lists all problems that occurred during the import.
	 */
	protected String errorReportFilePath;
	/**
	 * Called whenever one line of the import file has been processed. Used e.g. to update the progress bar.
	 */
	private Consumer<ImportLineResult> importedLineCallback;
	/**
	 * Whether the import should be canceled after the current line.
	 */
	private boolean cancelAfterCurrent;
	/**
	 * Whether or not the current import has resulted in at least one error.
	 */
	private boolean hasImportError;

	protected UserReferenceDto currentUser;
	private CSVWriter errorReportCsvWriter;

	public DataImporter(File inputFile, boolean hasEntityClassRow, UserReferenceDto currentUser) {
		this.inputFile = inputFile;
		this.hasEntityClassRow = hasEntityClassRow;
		this.currentUser = currentUser;

		Path exportDirectory = Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath());
		Path errorReportFilePath = exportDirectory.resolve(
			ImportExportUtils.TEMP_FILE_PREFIX + "_error_report_" + DataHelper.getShortUuid(currentUser.getUuid()) + "_"
				+ DateHelper.formatDateForExport(new Date()) + ".csv");
		this.errorReportFilePath = errorReportFilePath.toString();
	}

	/**
	 * Opens a progress layout and runs the import logic in a separate thread.
	 */
	public void startImport(Consumer<StreamResource> errorReportConsumer, UI currentUI, boolean duplicatesPossible) throws IOException {

		ImportProgressLayout progressLayout =
			new ImportProgressLayout(readImportFileLength(inputFile), currentUI, this::cancelImport, duplicatesPossible);

		importedLineCallback = result -> progressLayout.updateProgress(result);

		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getString(Strings.headingDataImport));
		window.setWidth(800, Unit.PIXELS);
		window.setContent(progressLayout);
		window.setClosable(false);
		currentUI.addWindow(window);

		Thread importThread = new Thread() {

			@Override
			public void run() {
				try {
					currentUI.setPollInterval(300);

					ImportResultStatus importResult = runImport();

					// Display a window presenting the import result
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
								if (importResult == ImportResultStatus.COMPLETED_WITH_ERRORS
									|| importResult == ImportResultStatus.CANCELED_WITH_ERRORS) {
									StreamResource streamResource = createErrorReportStreamResource();
									errorReportConsumer.accept(streamResource);
								}
							});

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
							progressLayout
								.setInfoLabelText(String.format(I18nProperties.getString(Strings.messageImportInvalidColumn), e.getColumnName()));
							currentUI.setPollInterval(-1);
						}
					});
				} catch (Exception e) {
					logger.error(e.getMessage(), e);

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
				}
			}
		};

		importThread.start();
	}

	/**
	 * To be called by async import thread or unit test
	 */
	public ImportResultStatus runImport() throws IOException, InvalidColumnException, InterruptedException {
		logger.debug("runImport - " + inputFile.getAbsolutePath());

		CSVReader csvReader = null;
		try {
			csvReader = CSVUtils
				.createCSVReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"), FacadeProvider.getConfigFacade().getCsvSeparator());
			errorReportCsvWriter = CSVUtils.createCSVWriter(createErrorReportWriter(), FacadeProvider.getConfigFacade().getCsvSeparator());

			// Build dictionary of entity headers
			String[] entityClasses;
			if (hasEntityClassRow) {
				entityClasses = csvReader.readNext();
			} else {
				entityClasses = null;
			}

			// Build dictionary of column paths
			String[] entityProperties = csvReader.readNext();
			String[][] entityPropertyPaths = new String[entityProperties.length][];
			for (int i = 0; i < entityProperties.length; i++) {
				String[] entityPropertyPath = entityProperties[i].split("\\.");
				entityPropertyPaths[i] = entityPropertyPath;
			}

			// Write first line to the error report writer
			String[] columnNames = new String[entityProperties.length + 1];
			columnNames[0] = ERROR_COLUMN_NAME;
			for (int i = 0; i < entityProperties.length; i++) {
				columnNames[i + 1] = entityProperties[i];
			}
			errorReportCsvWriter.writeNext(columnNames);

			// Read and import all lines from the import file
			String[] nextLine = csvReader.readNext();
			int lineCounter = 0;
			while (nextLine != null) {
				ImportLineResult lineResult = importDataFromCsvLine(nextLine, entityClasses, entityProperties, entityPropertyPaths, lineCounter == 0);
				logger.debug("runImport - line " + lineCounter);
				if (importedLineCallback != null) {
					importedLineCallback.accept(lineResult);
				}
				if (cancelAfterCurrent) {
					break;
				}
				nextLine = csvReader.readNext();
			}

			logger.debug("runImport - done");

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
		} finally {
			if (csvReader != null) {
				csvReader.close();
			}
			if (errorReportCsvWriter != null) {
				errorReportCsvWriter.flush();
				errorReportCsvWriter.close();
			}
		}
	}

	public void cancelImport() {
		cancelAfterCurrent = true;
	}

	protected Writer createErrorReportWriter() throws IOException {
		File errorReportFile = new File(errorReportFilePath.toString());
		if (errorReportFile.exists()) {
			errorReportFile.delete();
		}

		return new FileWriter(errorReportFile.getPath());
	}

	protected StreamResource createErrorReportStreamResource() {
		return DownloadUtil.createFileStreamResource(
			errorReportFilePath,
			"sormas_import_error_report.csv",
			"text/csv",
			I18nProperties.getString(Strings.headingErrorReportNotAvailable),
			I18nProperties.getString(Strings.messageErrorReportNotAvailable));
	}

	/**
	 * Reads the number of actual CSV lines in the file minus the header line(s).
	 * This is different from "normal" lines, because CSV may have escaped multi-line text blocks.
	 */
	protected int readImportFileLength(File inputFile) throws IOException {
		int importFileLength = 0;
		try (CSVReader caseCountReader = CSVUtils.createCSVReader(new FileReader(inputFile), FacadeProvider.getConfigFacade().getCsvSeparator())) {
			while (caseCountReader.readNext() != null) {
				importFileLength++;
			}
			// subtract header line(s)
			importFileLength--;
			if (hasEntityClassRow) {
				importFileLength--;
			}
		}

		return importFileLength;
	}

	/**
	 * Import the data from a line in the import file into new objects of the associated entities.
	 * 
	 * @param values
	 *            The contents of the line
	 * @param entityClasses
	 *            The contents of the entity class row, if present
	 * @param entityProperties
	 *            The contents of the entity properties row
	 * @param entityPropertyPaths
	 *            The contents of the entity properties row, split by entities
	 * @param firstLine
	 *            Whether the imported line is the first data line in the document (which alters some logic)
	 */
	protected abstract ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException, InterruptedException;

	/**
	 * Contains checks for the most common data types for entries in the import file. This method should be called
	 * in every subclass whenever data from the import file is supposed to be written to the entity in question.
	 * Additional invokes need to be executed manually in the subclass.
	 */
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	protected boolean executeDefaultInvokings(PropertyDescriptor pd, Object element, String entry, String[] entryHeaderPath)
		throws InvocationTargetException, IllegalAccessException, ParseException, ImportErrorException {
		Class<?> propertyType = pd.getPropertyType();

		if (propertyType.isEnum()) {
			pd.getWriteMethod().invoke(element, Enum.valueOf((Class<? extends Enum>) propertyType, entry.toUpperCase()));
			return true;
		}
		if (propertyType.isAssignableFrom(Date.class)) {
			// If the string is smaller than the length of the expected date format, throw an exception
			if (entry.length() < 10) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importInvalidDate, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, DateHelper.parseDateWithException(entry));
				return true;
			}
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
		if (propertyType.isAssignableFrom(AreaReferenceDto.class)) {
			List<AreaReferenceDto> areas = FacadeProvider.getAreaFacade().getByName(entry, false);
			if (areas.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (areas.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importAreaNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, areas.get(0));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
			List<RegionReferenceDto> region = FacadeProvider.getRegionFacade().getByName(entry, false);
			if (region.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (region.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importRegionNotUnique, entry, buildEntityProperty(entryHeaderPath)));
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
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			}
		}
		if (propertyType.isAssignableFrom(String.class)) {
			pd.getWriteMethod().invoke(element, entry);
			return true;
		}

		return false;
	}

	/**
	 * Provides the structure to insert a whole line into the object entity. The actual inserting has to take
	 * place in a callback.
	 * 
	 * @param ignoreEmptyEntries
	 *            If true, invokes won't be performed for empty values
	 * @param insertCallback
	 *            The callback that is used to actually do the inserting
	 * 
	 * @return True if the import succeeded without errors, false if not
	 */
	protected boolean insertRowIntoData(
		String[] values,
		String[] entityClasses,
		String[][] entityPropertyPaths,
		boolean ignoreEmptyEntries,
		Function<ImportCellData, Exception> insertCallback)
		throws IOException, InvalidColumnException {
		boolean dataHasImportError = false;

		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			if (ignoreEmptyEntries && (value == null || value.isEmpty())) {
				continue;
			}

			String[] entityPropertyPath = entityPropertyPaths[i];
			// Error description column is ignored
			if (entityPropertyPath[0].equals(ERROR_COLUMN_NAME)) {
				continue;
			}

			if (!(ignoreEmptyEntries && StringUtils.isEmpty(value))) {
				Exception exception =
					insertCallback.apply(new ImportCellData(value, hasEntityClassRow ? entityClasses[i] : null, entityPropertyPath));
				if (exception != null) {
					if (exception instanceof ImportErrorException) {
						dataHasImportError = true;
						writeImportError(values, exception.getMessage());
						break;
					} else if (exception instanceof InvalidColumnException) {
						throw (InvalidColumnException) exception;
					}
				}
			}

		}

		return dataHasImportError;
	}

	protected void writeImportError(String[] errorLine, String message) throws IOException {
		hasImportError = true;
		List<String> errorLineAsList = new ArrayList<>();
		errorLineAsList.add(message);
		errorLineAsList.addAll(Arrays.asList(errorLine));
		errorReportCsvWriter.writeNext(errorLineAsList.toArray(new String[errorLineAsList.size()]));
	}

	protected String buildEntityProperty(String[] entityPropertyPath) {
		return String.join(".", entityPropertyPath);
	}
}

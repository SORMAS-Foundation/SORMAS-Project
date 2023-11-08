package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.ejb.EJBException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportCellData;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.ImportLineResultDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.person.OccupationType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.CSVCommentLineValidator;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.CharsetHelper;
import de.symeda.sormas.api.utils.ConstraintValidationHelper;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.person.PersonSelectionField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.progress.ProgressResult;

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
	protected File inputFile;
	/**
	 * Whether or not the import file is supposed to have an additional row on top containing the entity name.
	 * This is necessary for importers that also import data that is not referenced in the root entity,
	 * e.g. samples for cases.
	 */
	private final boolean hasEntityClassRow;
	/**
	 * The file path to the generated error report file that lists all problems that occurred during the import.
	 */
	protected String errorReportFilePath;
	protected String errorReportFileName = "sormas_import_error_report.csv";
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
	/**
	 * CSV separator used in the file
	 */
	private char csvSeparator;

	protected UserDto currentUser;
	private CSVWriter errorReportCsvWriter;

	private final EnumCaptionCache enumCaptionCache;

	public DataImporter(File inputFile, boolean hasEntityClassRow, UserDto currentUser, ValueSeparator csvSeparator) throws IOException {
		this.inputFile = inputFile;
		this.hasEntityClassRow = hasEntityClassRow;
		this.currentUser = currentUser;
		this.enumCaptionCache = new EnumCaptionCache(currentUser.getLanguage());

		Path exportDirectory = getErrorReportFolderPath();
		if (!exportDirectory.toFile().exists() || !exportDirectory.toFile().canWrite()) {
			logger.error(exportDirectory + " doesn't exist or cannot be accessed");
			throw new FileNotFoundException("Temp directory doesn't exist or cannot be accessed");
		}
		Path errorReportFilePath = exportDirectory.resolve(
			ImportExportUtils.TEMP_FILE_PREFIX + "_error_report_" + DataHelper.getShortUuid(currentUser.getUuid()) + "_"
				+ DateHelper.formatDateForExport(new Date()) + ".csv");
		this.errorReportFilePath = errorReportFilePath.toString();

		this.csvSeparator = ValueSeparator.getSeparator(csvSeparator, FacadeProvider.getConfigFacade().getCsvSeparator());
	}

	/**
	 * Opens a progress layout and runs the import logic in a separate thread.
	 */
	public void startImport(Consumer<StreamResource> errorReportConsumer, UI currentUI, boolean duplicatesPossible)
		throws IOException, CsvValidationException {

		ImportProgressLayout progressLayout = this.getImportProgressLayout(currentUI, duplicatesPossible);

		importedLineCallback = result -> progressLayout.updateProgress(new ImportProgressUpdateInfo(result));

		Window window = VaadinUiUtil.createPopupWindow();
		window.setCaption(I18nProperties.getString(Strings.headingDataImport));
		window.setWidth(800, Unit.PIXELS);
		window.setContent(progressLayout);
		window.setClosable(false);
		currentUI.addWindow(window);

		Thread importThread = new Thread(() -> {
			try {
				currentUI.setPollInterval(300);
				I18nProperties.setUserLanguage(currentUser.getLanguage());
				FacadeProvider.getI18nFacade().setUserLanguage(currentUser.getLanguage());

				ImportResultStatus importResult = runImport();

				// Display a window presenting the import result
				currentUI.access(() -> {
					window.setClosable(true);
					ProgressResult progressResult;
					String newInfoText;
					switch (importResult) {
					case COMPLETED:
						progressResult = ProgressResult.SUCCESS;
						newInfoText = I18nProperties.getString(Strings.messageImportSuccessful);
						break;
					case COMPLETED_WITH_ERRORS:
						progressResult = ProgressResult.SUCCESS_WITH_WARNING;
						newInfoText = I18nProperties.getString(Strings.messageImportPartiallySuccessful);
						break;
					case CANCELED:
						progressResult = ProgressResult.SUCCESS;
						newInfoText = I18nProperties.getString(Strings.messageImportCanceled);
						break;
					default:
						progressResult = ProgressResult.FAILURE;
						newInfoText = I18nProperties.getString(Strings.messageImportCanceledErrors);
						break;
					}
					progressLayout.finishProgress(progressResult, newInfoText, null, window::close);

					window.addCloseListener(e -> {
						if (importResult == ImportResultStatus.COMPLETED_WITH_ERRORS || importResult == ImportResultStatus.CANCELED_WITH_ERRORS) {
							StreamResource streamResource = createErrorReportStreamResource();
							errorReportConsumer.accept(streamResource);
						}
					});

					currentUI.setPollInterval(-1);
				});
			} catch (InvalidColumnException e) {
				currentUI.access(() -> {
					window.setClosable(true);
					progressLayout.finishProgress(
						ProgressResult.FAILURE,
						String.format(I18nProperties.getString(Strings.messageImportInvalidColumn), e.getColumnName()),
						null,
						window::close);
					currentUI.setPollInterval(-1);
				});
			} catch (Exception e) {
				logger.error(e.getMessage(), e);

				currentUI.access(() -> {
					window.setClosable(true);
					progressLayout
						.finishProgress(ProgressResult.FAILURE, I18nProperties.getString(Strings.messageImportFailedFull), null, window::close);
					currentUI.setPollInterval(-1);
				});
			}
		});

		importThread.start();
	}

	/**
	 * Can be overriden by subclasses to provide alternative progress layouts
	 */
	protected ImportProgressLayout getImportProgressLayout(UI currentUI, boolean showDuplicates) throws IOException, CsvValidationException {
		return new ImportProgressLayout(currentUI, readImportFileLength(inputFile), this::cancelImport, showDuplicates);
	}

	/**
	 * To be called by async import thread or unit test
	 */
	public ImportResultStatus runImport() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException {
		logger.debug("runImport - {}", inputFile.getAbsolutePath());

		long t0 = System.currentTimeMillis();

		try (CSVReader csvReader = getCSVReader(inputFile)) {
			errorReportCsvWriter = CSVUtils.createCSVWriter(createErrorReportWriter(), this.csvSeparator);

			// Build dictionary of entity headers
			String[] entityClasses;
			if (hasEntityClassRow) {
				entityClasses = readNextValidLine(csvReader);
				errorReportCsvWriter.writeNext(ArrayUtils.insert(0, entityClasses, ""));
			} else {
				entityClasses = null;
			}

			// Build dictionary of column paths
			String[] entityProperties = readNextValidLine(csvReader);
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

			// validate headers
			if (entityClasses != null && entityClasses.length <= 1 || entityProperties.length <= 1) {
				writeImportError(new String[0], I18nProperties.getValidationError(Validations.importProbablyInvalidSeparator));
				return ImportResultStatus.CANCELED_WITH_ERRORS;
			}

			// Read and import all lines from the import file
			String[] nextLine = readNextValidLine(csvReader);

			if (nextLine == null) {
				writeImportError(new String[0], I18nProperties.getValidationError(Validations.importIncompleteContent));
				return ImportResultStatus.CANCELED_WITH_ERRORS;
			}

			int lineCounter = 0;
			while (nextLine != null) {
				ImportLineResult lineResult = importDataFromCsvLine(nextLine, entityClasses, entityProperties, entityPropertyPaths, lineCounter == 0);
				logger.debug("runImport - line {}", lineCounter);
				if (importedLineCallback != null) {
					importedLineCallback.accept(lineResult);
				}
				if (cancelAfterCurrent) {
					break;
				}
				nextLine = readNextValidLine(csvReader);
				lineCounter++;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("runImport - done");
				long dt = System.currentTimeMillis() - t0;
				logger.debug("import of {} lines took {} ms ({} ms/line)", lineCounter, dt, lineCounter > 0 ? dt / lineCounter : -1);
			}

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
			if (errorReportCsvWriter != null) {
				errorReportCsvWriter.close();
			}
		}
	}

	public void cancelImport() {
		cancelAfterCurrent = true;
	}

	protected Writer createErrorReportWriter() throws IOException {
		File errorReportFile = new File(errorReportFilePath);
		if (errorReportFile.exists()) {
			errorReportFile.delete();
		}

		return new FileWriter(errorReportFile.getPath());
	}

	protected StreamResource createErrorReportStreamResource() {
		return DownloadUtil.createFileStreamResource(
			errorReportFilePath,
			getErrorReportFileName(),
			"text/csv",
			I18nProperties.getString(Strings.headingErrorReportNotAvailable),
			I18nProperties.getString(Strings.messageErrorReportNotAvailable));
	}

	/**
	 * Reads the number of actual CSV lines in the file minus the header line(s).
	 * This is different from "normal" lines, because CSV may have escaped multi-line text blocks.
	 */
	protected int readImportFileLength(File inputFile) throws IOException, CsvValidationException {
		int importFileLength = 0;
		try (CSVReader caseCountReader = getCSVReader(inputFile)) {
			while (readNextValidLine(caseCountReader) != null) {
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

	private CSVReader getCSVReader(File inputFile) throws IOException {
		CharsetDecoder decoder = CharsetHelper.getDecoder(inputFile);
		InputStream inputStream = Files.newInputStream(inputFile.toPath());
		BOMInputStream bomInputStream = new BOMInputStream(inputStream);
		Reader reader = new InputStreamReader(bomInputStream, decoder);
		BufferedReader bufferedReader = new BufferedReader(reader);
		return CSVUtils.createCSVReader(bufferedReader, this.csvSeparator, new CSVCommentLineValidator());
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
	protected boolean executeDefaultInvoke(PropertyDescriptor pd, Object element, String entry, String[] entryHeaderPath)
		throws InvocationTargetException, IllegalAccessException, ImportErrorException {
		Class<?> propertyType = pd.getPropertyType();

		if (propertyType.isEnum()) {
			Enum enumValue = null;
			Class<Enum> enumType = (Class<Enum>) propertyType;
			try {
				enumValue = Enum.valueOf(enumType, entry.toUpperCase());
			} catch (IllegalArgumentException e) {
				// ignore
			}

			if (enumValue == null) {
				enumValue = enumCaptionCache.getEnumByCaption(enumType, entry);
			}

			pd.getWriteMethod().invoke(element, enumValue);

			return true;
		}

		if (propertyType.isAssignableFrom(OccupationType.class)) {
			OccupationType occupationType = null;
			try {
				occupationType = FacadeProvider.getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.OCCUPATION_TYPE, entry);
			} catch (EJBException e) {
				//ignore, occupationType will remain null
			}

			if (occupationType == null) {
				throw new IllegalArgumentException();
			}
			pd.getWriteMethod().invoke(element, occupationType);

			return true;
		}

		if (propertyType.isAssignableFrom(Date.class)) {
			try {
				pd.getWriteMethod().invoke(element, DateHelper.parseDateWithException(entry, I18nProperties.getUserLanguage()));
				return true;
			} catch (ParseException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(
						Validations.importInvalidDate,
						pd.getName(),
						DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
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
			pd.getWriteMethod().invoke(element, DataHelper.parseBoolean(entry));
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
		if (propertyType.isAssignableFrom(SubcontinentReferenceDto.class)) {
			List<SubcontinentReferenceDto> subcontinents = FacadeProvider.getSubcontinentFacade().getByDefaultName(entry, false);
			if (subcontinents.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (subcontinents.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importSubcontinentNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, subcontinents.get(0));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(CountryReferenceDto.class)) {
			List<CountryReferenceDto> countries = FacadeProvider.getCountryFacade().getByDefaultName(entry, false);
			if (countries.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (countries.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importSubcontinentNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, countries.get(0));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(ContinentReferenceDto.class)) {
			List<ContinentReferenceDto> continents = FacadeProvider.getContinentFacade().getByDefaultName(entry, false);
			if (continents.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (continents.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importSubcontinentNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, continents.get(0));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
			List<RegionDto> regions = FacadeProvider.getRegionFacade().getByName(entry, false);
			if (regions.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (regions.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importRegionNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else {
				RegionDto region = regions.get(0);
				CountryReferenceDto serverCountry = FacadeProvider.getCountryFacade().getServerCountry();

				if (region.getCountry() != null && !region.getCountry().equals(serverCountry)) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importRegionNotInServerCountry, entry, buildEntityProperty(entryHeaderPath)));
				} else {
					pd.getWriteMethod().invoke(element, region.toReference());
					return true;
				}
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
		throws IOException {

		boolean dataHasImportError = false;
		List<String> invalidColumns = new ArrayList<>();

		for (int i = 0; i < values.length; i++) {
			String value = StringUtils.trimToNull(values[i]);
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
						invalidColumns.add(((InvalidColumnException) exception).getColumnName());
					}
				}
			}

		}

		if (invalidColumns.size() > 0) {
			LoggerFactory.getLogger(getClass()).warn("Unhandled columns [{}]", String.join(", ", invalidColumns));
		}

		return dataHasImportError;
	}

	/**
	 * Presents a popup window to the user that allows them to deal with detected potentially duplicate persons.
	 * By passing the desired result to the resultConsumer, the importer decided how to proceed with the import process.
	 */
	protected <T extends PersonImportSimilarityResult> void handlePersonSimilarity(
		PersonDto newPerson,
		Consumer<T> resultConsumer,
		BiFunction<SimilarPersonDto, ImportSimilarityResultOption, T> createSimilarityResult,
		String infoText,
		UI currentUI) {
		currentUI.accessSynchronously(() -> {
			PersonSelectionField personSelect = new PersonSelectionField(newPerson, I18nProperties.getString(infoText));
			personSelect.setWidth(1024, Unit.PIXELS);

			if (personSelect.hasMatches()) {
				final CommitDiscardWrapperComponent<PersonSelectionField> component = new CommitDiscardWrapperComponent<>(personSelect);
				component.addCommitListener(() -> {
					SimilarPersonDto person = personSelect.getValue();
					if (person == null) {
						resultConsumer.accept(createSimilarityResult.apply(null, ImportSimilarityResultOption.CREATE));
					} else {
						resultConsumer.accept(createSimilarityResult.apply(person, ImportSimilarityResultOption.PICK));
					}
				});

				component.addDiscardListener(() -> resultConsumer.accept(createSimilarityResult.apply(null, ImportSimilarityResultOption.SKIP)));

				personSelect.setSelectionChangeCallback((commitAllowed) -> {
					component.getCommitButton().setEnabled(commitAllowed);
				});

				VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreatePerson));

				personSelect.selectBestMatch();
			} else {
				resultConsumer.accept(createSimilarityResult.apply(null, ImportSimilarityResultOption.CREATE));
			}
		});
	}

	protected FacilityType getTypeOfFacility(String propertyName, Object currentElement)
		throws IntrospectionException, InvocationTargetException, IllegalAccessException {
		String typeProperty;
		if (CaseDataDto.class.equals(currentElement.getClass()) && CaseDataDto.HEALTH_FACILITY.equals(propertyName)) {
			typeProperty = CaseDataDto.FACILITY_TYPE;
		} else {
			typeProperty = propertyName + "Type";
		}
		PropertyDescriptor pd = new PropertyDescriptor(typeProperty, currentElement.getClass());
		return (FacilityType) pd.getReadMethod().invoke(currentElement);
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

	private String[] readNextValidLine(CSVReader csvReader) throws IOException, CsvValidationException {
		String[] nextValidLine = null;
		boolean isCommentLine;

		do {
			try {
				nextValidLine = csvReader.readNext();
				isCommentLine = false;
			} catch (CsvValidationException e) {
				if (StringUtils.contains(e.getMessage(), CSVCommentLineValidator.ERROR_MESSAGE)) {
					logger.debug("Found comment line. Skipping it");
					csvReader.skip(1);
					isCommentLine = true;
				} else {
					throw e;
				}
			}
		}
		while (isCommentLine);
		return nextValidLine;
	}

	public void setCsvSeparator(char csvSeparator) {
		this.csvSeparator = csvSeparator;
	}

	protected String getErrorReportFileName() {
		return errorReportFileName;
	}

	protected <T> ImportLineResultDto<T> validateConstraints(T object) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
		if (constraintViolations.size() > 0) {
			return ImportLineResultDto
				.errorResult(ConstraintValidationHelper.formatPropertyErrors(ConstraintValidationHelper.getPropertyErrors(constraintViolations)));
		}

		return ImportLineResultDto.successResult();
	}

	protected Path getErrorReportFolderPath() {
		return Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath());
	}

	/**
	 * Inserts the entry of a single cell into a related object
	 */
	protected void insertColumnEntryIntoRelatedObject(Object currentElement, String entry, String[] entryHeaderPath)
		throws InvalidColumnException, de.symeda.sormas.api.importexport.ImportErrorException {
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					// Execute the default invokes specified in the data importer; if none of those were triggered, execute additional invokes
					// according to the types of the sample or pathogen test fields
					if (executeDefaultInvoke(pd, currentElement, entry, entryHeaderPath)) {
						continue;
					} else {
						throw new UnsupportedOperationException(
							I18nProperties.getValidationError(Validations.importCasesPropertyTypeNotAllowed, propertyType.getName()));
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildEntityProperty(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorInColumn, buildEntityProperty(entryHeaderPath)));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildEntityProperty(entryHeaderPath));
			} catch (ImportErrorException e) {
				throw e;
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import related object data data for {}: {}", entryHeaderPath, e.getMessage());
				throw new ImportErrorException(I18nProperties.getValidationError(Validations.importCasesUnexpectedError));
			}
		}
	}
}

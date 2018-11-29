/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.importer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

public class CaseImporter {

	private static final String ERROR_COLUMN_NAME = "Error description";
	private static final Logger logger = LoggerFactory.getLogger(CaseImporter.class);

	private Consumer<CaseImportResult> caseImportedCallback;
	private BiConsumer<ImportSimilarityInput, Consumer<ImportSimilarityResult>> similarityCallback;
	private BiFunction<CaseDataDto, PersonDto, PersonDto> importSuccessfulCallback;
	private Integer numberOfCases;

	private String csvFilePath;
	private CSVReader csvReader;
	private CSVWriter errorReportCsvWriter;
	private Path errorReportFilePath;

	private boolean cancelAfterCurrentImport;
	private boolean hasImportError;

	private List<PersonNameDto> personNames;

	public CaseImporter(String csvFilePath, UserReferenceDto currentUser) throws IOException {
		this.csvFilePath = csvFilePath;
		personNames = FacadeProvider.getPersonFacade().getNameDtos(currentUser);
		importSuccessfulCallback = (caze, person) -> {
			PersonDto savedPerson = FacadeProvider.getPersonFacade().savePerson(person);
			caze.setPerson(savedPerson.toReference());
			FacadeProvider.getCaseFacade().saveCase(caze);
			return savedPerson;
		};

		// Read file and create readers
		File file = new File(csvFilePath);
		if (!file.exists()) {
			throw new FileNotFoundException("Cases .csv file does not exist");
		}

		// Generate the error report file
		Path exportDirectory = Paths.get(FacadeProvider.getConfigFacade().getTempFilesPath());
		errorReportFilePath = exportDirectory.resolve(ImportExportUtils.TEMP_FILE_PREFIX + "_error_report_" 
				+ DataHelper.getShortUuid(currentUser.getUuid()) + "_" 
				+ DateHelper.formatDateForExport(new Date()) + ".csv");
		// If the error report file already exists, delete it
		File errorReportFile = new File(errorReportFilePath.toString());
		if (errorReportFile.exists()) {
			errorReportFile.delete();
		}

		csvReader = CSVUtils.createCSVReader(new FileReader(csvFilePath), FacadeProvider.getConfigFacade().getCsvSeparator());
		errorReportCsvWriter = CSVUtils.createCSVWriter(new FileWriter(errorReportFilePath.toString(), true), FacadeProvider.getConfigFacade().getCsvSeparator());
	}

	public int getNumberOfCases() throws IOException {
		if (numberOfCases != null) {
			return numberOfCases;
		}

		// Initialize with -1 because first line is not a case
		numberOfCases = -1;
		while (csvReader.readNext() != null) {
			numberOfCases++;
		}

		// Re-create the CSV reader
		csvReader.close();
		csvReader = CSVUtils.createCSVReader(new FileReader(csvFilePath), FacadeProvider.getConfigFacade().getCsvSeparator());

		return numberOfCases;
	}

	/**
	 * Imports all cases without errors and adds those with errors to the error file. The path to this file is returned.
	 * Cases that are potential duplicates are handled by calling the similarityCallback.
	 */
	public ImportResultStatus importAllCases(BiConsumer<ImportSimilarityInput, Consumer<ImportSimilarityResult>> similarityCallback, Consumer<CaseImportResult> caseImportedCallback) throws IOException, InvalidColumnException, InterruptedException {
		this.similarityCallback = similarityCallback;
		this.caseImportedCallback = caseImportedCallback;

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
		errorReportCsvWriter.writeNext(columnNames.toArray(new String[columnNames.size()]));

		// Create a new case for each line in the .csv file
		readNextLineFromCsv(headersLine, headers);

		csvReader.close();
		errorReportCsvWriter.flush();
		errorReportCsvWriter.close();

		if (cancelAfterCurrentImport) {
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
		cancelAfterCurrentImport = true;
	}

	public String getErrorReportFilePath() {
		return errorReportFilePath != null ? errorReportFilePath.toString() : null;
	}

	private void readNextLineFromCsv(String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException {
		if (cancelAfterCurrentImport) {
			return;
		}

		String[] nextLine = csvReader.readNext();
		if (nextLine != null) {
			importCaseFromCsvLine(nextLine, headersLine, headers);
		}
	}

	private void importCaseFromCsvLine(String[] nextLine, String[] headersLine, List<String[]> headers) throws IOException, InvalidColumnException, InterruptedException {
		// Check whether the new line has the same length as the header line
		if (nextLine.length > headersLine.length) {
			hasImportError = true;
			writeImportError(errorReportCsvWriter, nextLine, "This line is longer than the header line");
			readNextLineFromCsv(headersLine, headers);
		}

		PersonDto newPerson = PersonDto.build();
		CaseDataDto newCase = CaseDataDto.build(newPerson.toReference(), null);
		UserDto user = FacadeProvider.getUserFacade().getCurrentUser();
		newCase.setReportingUser(user.toReference());

		boolean caseHasImportError = insertRowIntoCase(newCase, newPerson, nextLine, headers);

		if (!caseHasImportError) {
			try {
				FacadeProvider.getPersonFacade().validate(newPerson);
				FacadeProvider.getCaseFacade().validate(newCase);
			} catch (ValidationRuntimeException e) {
				hasImportError = true;
				caseHasImportError = true;
				writeImportError(errorReportCsvWriter, nextLine, e.getMessage());
			}
		}

		if (!caseHasImportError) {
			try {
				ImportConsumer consumer = new ImportConsumer();
				Object LOCK = new Object();
				synchronized(LOCK) {
					for (PersonNameDto personName : personNames) {
						if (PersonHelper.areNamesSimilar(personName, newPerson.getFirstName(), newPerson.getLastName())) {
							if (similarityCallback != null) {
								similarityCallback.accept(
										new ImportSimilarityInput(personNames, newCase, newPerson),
										new Consumer<ImportSimilarityResult>() {
											@Override
											public void accept(ImportSimilarityResult result) {
												consumer.onImportResult(result, LOCK);
											}
										});

								try {
									LOCK.wait();
								} catch (InterruptedException e) {
									logger.error("InterruptedException when trying to perform LOCK.wait() in case import: " + e.getMessage());
									throw e;
								}

								if (consumer.result != null && !(consumer.result.isSkip() || consumer.result.isCancelImport())) {
									if (consumer.result.isUseCase()) {
										newCase = consumer.result.getMatchingCase();
										newPerson = FacadeProvider.getPersonFacade().getPersonByUuid(newCase.getPerson().getUuid());
										caseHasImportError = insertRowIntoCase(newCase, newPerson, nextLine, headers);
									} else if (consumer.result.isUsePerson()) {
										newPerson = FacadeProvider.getPersonFacade().getPersonByUuid(consumer.result.getMatchingPerson().getUuid());
										caseHasImportError = insertRowIntoCase(newCase, newPerson, nextLine, headers);
									}
								}
							}

							break;
						}
					}

					if (caseHasImportError) {
						// In case insertRowIntoCase when matching person/case has thrown an unexpected error
						caseImportedCallback.accept(CaseImportResult.ERROR);
						readNextLineFromCsv(headersLine, headers);
					} else if (consumer.result != null && consumer.result.isSkip()) {
						// Reset the import result
						consumer.result = null;
						caseImportedCallback.accept(CaseImportResult.SKIPPED);
						readNextLineFromCsv(headersLine, headers);
					} else if (consumer.result != null && consumer.result.isCancelImport()) {
						cancelAfterCurrentImport = true;
						return;
					} else {
						PersonDto savedPerson = importSuccessfulCallback.apply(newCase, newPerson);
						if (consumer.result == null || !consumer.result.isUseCase() || !consumer.result.isUsePerson()) {
							personNames.add(new PersonNameDto(newPerson.getFirstName(), newPerson.getLastName(), savedPerson.getUuid()));
						}
						// Reset the import result
						consumer.result = null;
						caseImportedCallback.accept(CaseImportResult.SUCCESS);
						readNextLineFromCsv(headersLine, headers);
					}
				}
			} catch (ValidationRuntimeException e) {
				hasImportError = true;
				writeImportError(errorReportCsvWriter, nextLine, e.getMessage());
				caseImportedCallback.accept(CaseImportResult.ERROR);
				readNextLineFromCsv(headersLine, headers);
			}
		} else {
			caseImportedCallback.accept(CaseImportResult.ERROR);
			readNextLineFromCsv(headersLine, headers);
		}
	}

	private boolean insertRowIntoCase(CaseDataDto caze, PersonDto person, String[] row, List<String[]> headers) throws IOException, InvalidColumnException {
		boolean caseHasImportError = false;

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

			try {
				insertColumnEntryIntoCase(caze, person, entry, entryHeaderPath);
			} catch (ImportErrorException e) {
				hasImportError = true;
				caseHasImportError = true;
				writeImportError(errorReportCsvWriter, row, e.getMessage());
				break;
			} catch (InvalidColumnException e) {
				csvReader.close();
				errorReportCsvWriter.flush();
				errorReportCsvWriter.close();
				throw e;
			}
		}

		return caseHasImportError;
	}

	private void writeImportError(CSVWriter errorReportWriter, String[] nextLine, String message) throws IOException {
		List<String> nextLineAsList = new ArrayList<>();
		nextLineAsList.add(message);
		nextLineAsList.addAll(Arrays.asList(nextLine));
		errorReportWriter.writeNext(nextLineAsList.toArray(new String[nextLineAsList.size()]));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void insertColumnEntryIntoCase(CaseDataDto caze, PersonDto person, String entry, String[] entryHeaderPath) throws InvalidColumnException, ImportErrorException {
		Object currentElement = caze;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i != entryHeaderPath.length - 1) {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
					// Replace PersonReferenceDto with the created person
					if (currentElement instanceof PersonReferenceDto) {
						currentElement = person;
					}
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (propertyType.isEnum()) {
						pd.getWriteMethod().invoke(currentElement, Enum.valueOf((Class<? extends Enum>) propertyType, entry.toUpperCase()));
					} else if (propertyType.isAssignableFrom(Date.class)) {
						pd.getWriteMethod().invoke(currentElement, DateHelper.parseDateWithException(entry));
					} else if (propertyType.isAssignableFrom(Integer.class)) {
						pd.getWriteMethod().invoke(currentElement, Integer.parseInt(entry));
					} else if (propertyType.isAssignableFrom(Double.class)) {
						pd.getWriteMethod().invoke(currentElement, Double.parseDouble(entry));
					} else if (propertyType.isAssignableFrom(Float.class)) {
						pd.getWriteMethod().invoke(currentElement, Float.parseFloat(entry));
					} else if (propertyType.isAssignableFrom(Boolean.class)) {
						pd.getWriteMethod().invoke(currentElement, Boolean.parseBoolean(entry));
					} else if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
						List<RegionReferenceDto> region = FacadeProvider.getRegionFacade().getByName(entry);
						if (region.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						} else if (region.size() > 1) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Region name is not unique, make sure there is only one region with this name in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, region.get(0));
						}
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = FacadeProvider.getDistrictFacade().getByName(entry, caze.getRegion());
						if (district.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database or in the specified region");
						} else if (district.size() > 1) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; District name is not unique in the chosen region, make sure there is only one district with this name belonging to the chosen region in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
						List<CommunityReferenceDto> community = FacadeProvider.getCommunityFacade().getByName(entry, caze.getDistrict());
						if (community.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database or in the specified district");
						} else if (community.size() > 1) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Community name is not unique in the chosen district, make sure there is only one community with this name belonging to the chosen district in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						List<FacilityReferenceDto> facility = FacadeProvider.getFacilityFacade().getByName(entry, caze.getDistrict(), caze.getCommunity());
						if (facility.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database or in the specified " + (caze.getCommunity() == null ? "district" : "community"));
						} else if (facility.size() > 1 && caze.getCommunity() == null) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Facility name is not unique in the chosen district, make sure there is only one facility with this name belonging to the chosen district in the database or specify a community");
						} else if (facility.size() > 1 && caze.getCommunity() != null) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Facility name is not unique in the chosen community, make sure there is only one facility with this name belonging to the chosen community in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, facility.get(0));
						}
					} else if (propertyType.isAssignableFrom(UserReferenceDto.class)) {
						UserDto user = FacadeProvider.getUserFacade().getByUserName(entry);
						if (user != null) {
							pd.getWriteMethod().invoke(currentElement, user.toReference());
						} else {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						}
					} else if (propertyType.isAssignableFrom(String.class)) {
						pd.getWriteMethod().invoke(currentElement, entry);
					} else {
						throw new UnsupportedOperationException ("Property type " + propertyType.getName() + " not allowed when importing cases.");
					}
				}
			} catch (IntrospectionException e) {
				throw new InvalidColumnException(buildHeaderPathString(entryHeaderPath));
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new ImportErrorException("The import failed because of an error in column " + buildHeaderPathString(entryHeaderPath));
			} catch (IllegalArgumentException e) {
				throw new ImportErrorException(entry, buildHeaderPathString(entryHeaderPath));
			} catch (ParseException e) {
				throw new ImportErrorException("Invalid date in column " + buildHeaderPathString(entryHeaderPath) + "; Allowed date formats are dd/MM/yyyy, dd.MM.yyyy and dd-MM-yyyy");
			} catch (Exception e) {
				logger.error("Unexpected error when trying to import a case: " + e.getMessage());
				throw new ImportErrorException("Unexpected error when trying to import this case. Please send your error report file to an administrator and remove this case from your import file.");
			}
		}
	}

	private String buildHeaderPathString(String[] entryHeaderPath) {
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

	private class ImportConsumer {
		protected ImportSimilarityResult result;

		private void onImportResult(ImportSimilarityResult result, Object LOCK) {
			this.result = result;
			synchronized(LOCK) {
				LOCK.notify();
			}
		}
	}

}

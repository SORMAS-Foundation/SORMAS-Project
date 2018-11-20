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
package de.symeda.sormas.backend.importexport;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.internal.org.apache.commons.lang3.text.WordUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.person.PersonDto;
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
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.ImportIgnore;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.CommunityFacadeEjb.CommunityFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ImportFacade")
public class ImportFacadeEjb implements ImportFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private UserService userService;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private RegionFacadeEjbLocal regionFacade;
	@EJB
	private DistrictService districtService;
	@EJB
	private DistrictFacadeEjbLocal districtFacade;
	@EJB
	private CommunityService communityService;
	@EJB
	private CommunityFacadeEjbLocal communityFacade;
	@EJB
	private FacilityService facilityService;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private PersonService personService;
	@EJB
	private HospitalizationService hospitalizationService;
	@EJB
	private EpiDataService epiDataService;

	private static final Logger logger = LoggerFactory.getLogger(ImportFacadeEjb.class);

	private static final String CASE_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_case_template.csv";
	private static final String ERROR_COLUMN_NAME = "Error description";

	@Override
	public void generateCaseImportTemplateFile() throws IOException {				
		// Create the export directory if it doesn't exist
		try {	
			Files.createDirectories(Paths.get(configFacade.getGeneratedFilesPath()));
		} catch (IOException e) {
			logger.error("Generated files directory doesn't exist and creation failed.");
			throw e;
		}

		List<String> columnNames = new ArrayList<>();
		buildListOfFields(columnNames, CaseDataDto.class, "");
		Path filePath = Paths.get(getCaseImportTemplateFilePath());
		CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(filePath.toString()), configFacade.getCsvSeparator());
		writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
		writer.flush();
		writer.close();
	}

	@Override
	public String getCaseImportTemplateFilePath() {
		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(CASE_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String importCasesFromCsvFile(String csvFilePath, String userUuid) throws IOException, InvalidColumnException {
		File file = new File(csvFilePath);
		if (!file.exists()) {
			throw new FileNotFoundException("Cases .csv file does not exist");
		}

		// Generate the error report file
		Path exportDirectory = Paths.get(configFacade.getTempFilesPath());
		Path errorReportFilePath = exportDirectory.resolve(ImportExportUtils.TEMP_FILE_PREFIX + "_error_report_" + DataHelper.getShortUuid(userUuid) + "_" + DateHelper.formatDateForExport(new Date()) + ".csv");
		// If the error report file already exists, delete it
		File errorReportFile = new File(errorReportFilePath.toString());
		if (errorReportFile.exists()) {
			errorReportFile.delete();
		}

		boolean hasImportError = importCasesFromCsvFile(new FileReader(csvFilePath), new FileWriter(errorReportFilePath.toString(), true), userUuid);
		return hasImportError ? errorReportFilePath.toString() : null;
	}

	@Override
	public boolean importCasesFromCsvFile(Reader reader, Writer errorReportwriter, String userUuid) throws IOException, InvalidColumnException {
		CSVReader csvReader = CSVUtils.createCSVReader(reader, configFacade.getCsvSeparator());
		CSVWriter errorReportCsvWriter = CSVUtils.createCSVWriter(errorReportwriter, configFacade.getCsvSeparator());

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
		String[] nextLine;
		boolean hasImportError = false;
		while ((nextLine = csvReader.readNext()) != null) {
			// Check whether the new line has the same length as the header line
			if (nextLine.length > headersLine.length) {
				hasImportError = true;
				writeImportError(errorReportCsvWriter, nextLine, "This line is longer than the header line.");
				continue;
			}

			PersonDto newPerson = PersonDto.build();
			CaseDataDto newCase = CaseDataDto.build(newPerson.toReference(), null);
			User user = userService.getCurrentUser();
			newCase.setReportingUser(user.toReference());

			boolean caseHasImportError = false;
			for (int i = 0; i < nextLine.length; i++) {
				String entry = nextLine[i];
				if (entry == null || entry.isEmpty()) {
					continue;
				}

				String[] entryHeaderPath = headers.get(i);
				// Error description column is ignored
				if (entryHeaderPath[0].equals(ERROR_COLUMN_NAME)) {
					continue;
				}

				try {
					insertColumnEntryIntoCase(newCase, newPerson, entry, entryHeaderPath);
				} catch (ImportErrorException e) {
					hasImportError = true;
					caseHasImportError = true;
					writeImportError(errorReportCsvWriter, nextLine, e.getMessage());
					break;
				} catch (InvalidColumnException e) {
					csvReader.close();
					errorReportCsvWriter.flush();
					errorReportCsvWriter.close();
					throw e;
				}
			}

			if (!caseHasImportError) {
				try {
					personFacade.savePerson(newPerson);
					caseFacade.saveCase(newCase);
				} catch (ValidationRuntimeException e) {
					hasImportError = true;
					writeImportError(errorReportCsvWriter, nextLine, e.getMessage());
					continue;
				}
			}
		}

		csvReader.close();
		errorReportCsvWriter.flush();
		errorReportCsvWriter.close();
		return hasImportError;
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
						List<RegionReferenceDto> region = regionFacade.getByName(entry);
						if (region.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						} else if (region.size() > 1) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Region name is not unique, make sure there is only one region with this name in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, region.get(0));
						}
					} else if (propertyType.isAssignableFrom(DistrictReferenceDto.class)) {
						List<DistrictReferenceDto> district = districtFacade.getByName(entry, caze.getRegion());
						if (district.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database or in the specified region");
						} else if (district.size() > 1) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; District name is not unique in the chosen region, make sure there is only one district with this name belonging to the chosen region in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(CommunityReferenceDto.class)) {
						List<CommunityReferenceDto> community = communityFacade.getByName(entry, caze.getDistrict());
						if (community.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database or in the specified district");
						} else if (community.size() > 1) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Community name is not unique in the chosen district, make sure there is only one community with this name belonging to the chosen district in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else if (propertyType.isAssignableFrom(FacilityReferenceDto.class)) {
						List<FacilityReferenceDto> facility = facilityFacade.getByName(entry, caze.getDistrict(), caze.getCommunity());
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
						UserDto user = userFacade.getByUserName(entry);
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

	private void writeImportError(CSVWriter errorReportWriter, String[] nextLine, String message) throws IOException {
		List<String> nextLineAsList = new ArrayList<>();
		nextLineAsList.add(message);
		nextLineAsList.addAll(Arrays.asList(nextLine));
		errorReportWriter.writeNext(nextLineAsList.toArray(new String[nextLineAsList.size()]));
	}

	/**
	 * Builds a list of all fields in the case and its relevant sub entities. IMPORTANT: The order
	 * is not guaranteed; at the time of writing, clazz.getDeclaredFields() seems to return the
	 * fields in the order of declaration (which is what we need here), but that could change
	 * in the future.
	 */
	private void buildListOfFields(List<String> resultFieldNames, Class<?> clazz, String prefix) {
		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			Method readMethod = null;
			try {
				readMethod = clazz.getDeclaredMethod("get" + WordUtils.capitalize(field.getName()));
			} catch (NoSuchMethodException e) {
				try {
					readMethod = clazz.getDeclaredMethod("is" + WordUtils.capitalize(field.getName()));
				} catch (NoSuchMethodException f) {
					continue;
				}
			}

			// Fields without a getter or whose getters are declared in a superclass are ignored
			if (readMethod == null || readMethod.getDeclaringClass() != clazz) {
				continue;
			}			
			// Fields with the @ImportIgnore annotation are ignored
			if (readMethod.isAnnotationPresent(ImportIgnore.class)) {
				continue;
			}
			// List types are ignored
			if (Collection.class.isAssignableFrom(field.getType())) {
				continue;
			}
			// Certain field types are ignored
			if (field.getType() == UserReferenceDto.class) {
				continue;
			}
			// Other non-infrastructure EntityDto/ReferenceDto classes, recursively call this method to include fields of the sub-entity
			if (EntityDto.class.isAssignableFrom(field.getType()) && !isInfrastructureClass(field.getType())) {
				buildListOfFields(resultFieldNames, field.getType(), prefix == null || prefix.isEmpty() ? field.getName() + "." :  prefix + field.getName() + ".");
			} else if (PersonReferenceDto.class.isAssignableFrom(field.getType()) && !isInfrastructureClass(field.getType())) {
				buildListOfFields(resultFieldNames, PersonDto.class, prefix == null || prefix.isEmpty() ? field.getName() + "." : prefix + field.getName() + ".");
			} else {
				resultFieldNames.add(prefix + field.getName());
			}
		}
	}

	private boolean isInfrastructureClass(Class<?> clazz) {
		return clazz == RegionReferenceDto.class || clazz == DistrictReferenceDto.class || clazz == CommunityReferenceDto.class || clazz == FacilityReferenceDto.class;
	}

	@LocalBean
	@Stateless
	public static class ImportFacadeEjbLocal extends ImportFacadeEjb {
	}

}

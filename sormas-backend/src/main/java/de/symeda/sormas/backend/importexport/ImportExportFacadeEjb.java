package de.symeda.sormas.backend.importexport;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.importexport.ImportExportFacade;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.ImportIgnore;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataBurial;
import de.symeda.sormas.backend.epidata.EpiDataGathering;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.epidata.EpiDataTravel;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.outbreak.Outbreak;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleTest;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.CSVUtils;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.visit.Visit;

@Stateless(name = "ImportExportFacade")
public class ImportExportFacadeEjb implements ImportExportFacade {

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
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private PersonService personService;
	@EJB
	private HospitalizationService hospitalizationService;
	@EJB
	private EpiDataService epiDataService;

	private static final String CASE_IMPORT_TEMPLATE_FILE_NAME = "sormas_import_case_template.csv";
	private static final String SORMAS_IMPORT_GUIDE_FILE_NAME = "SORMAS_Import_Guide.pdf";
	private static final String ERROR_COLUMN_NAME = "Error description";

	private static final Logger logger = LoggerFactory.getLogger(CaseFacadeEjb.class);

	@Override
	public String generateDatabaseExportArchive(List<DatabaseTable> databaseTables) throws ExportErrorException, IOException {
		// Create the folder if it doesn't exist
		try {	
			Files.createDirectories(Paths.get(configFacade.getTempFilesPath()));
		} catch (IOException e) {
			logger.error("Temp directory doesn't exist and creation failed.");
			throw e;
		}

		// Export all selected tables to .csv files
		String date = DateHelper.formatDateForExport(new Date());
		int randomNumber = new Random().nextInt(Integer.MAX_VALUE);
		for (DatabaseTable databaseTable : databaseTables) {
			switch (databaseTable) {
			case CASES:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Case.TABLE_NAME).getResultList();
				break;
			case HOSPITALIZATIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Hospitalization.TABLE_NAME).getResultList();
				break;
			case PREVIOUSHOSPITALIZATIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, PreviousHospitalization.TABLE_NAME).getResultList();
				break;
			case EPIDATA:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EpiData.TABLE_NAME).getResultList();
				break;
			case EPIDATABURIALS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EpiDataBurial.TABLE_NAME).getResultList();
				break;
			case EPIDATAGATHERINGS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EpiDataGathering.TABLE_NAME).getResultList();
				break;
			case EPIDATATRAVELS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EpiDataTravel.TABLE_NAME).getResultList();
				break;
			case CONTACTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Contact.TABLE_NAME).getResultList();
				break;
			case VISITS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Visit.TABLE_NAME).getResultList();
				break;
			case EVENTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Event.TABLE_NAME).getResultList();
				break;
			case EVENTPARTICIPANTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EventParticipant.TABLE_NAME).getResultList();
				break;
			case SAMPLES:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Sample.TABLE_NAME).getResultList();
				break;
			case SAMPLETESTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, SampleTest.TABLE_NAME).getResultList();
				break;
			case TASKS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Task.TABLE_NAME).getResultList();
				break;
			case PERSONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Person.TABLE_NAME).getResultList();
				break;
			case LOCATIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Location.TABLE_NAME).getResultList();
				break;
			case REGIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Region.TABLE_NAME).getResultList();
				break;
			case DISTRICTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, District.TABLE_NAME).getResultList();
				break;
			case COMMUNITIES:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Community.TABLE_NAME).getResultList();
				break;
			case FACILITIES:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Facility.TABLE_NAME).getResultList();
				break;
			case OUTBREAKS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Outbreak.TABLE_NAME).getResultList();
				break;
			case CASE_SYMPTOMS:
				generateCsvExportJoinQuery(databaseTable.getFileName(), date, randomNumber, Symptoms.TABLE_NAME, Case.TABLE_NAME, "id", "symptoms_id").getResultList();
				break;
			case VISIT_SYMPTOMS:
				generateCsvExportJoinQuery(databaseTable.getFileName(), date, randomNumber, Symptoms.TABLE_NAME, Visit.TABLE_NAME, "id", "symptoms_id").getResultList();
				break;				
			}
		}

		// Create a zip containing all created .csv files
		return createZipFromCsvFiles(databaseTables, date, randomNumber);
	}

	@Override
	public void generateCaseImportTemplateFile() throws IOException {				
		// Create the export directory if it doesn't exist
		try {	
			Files.createDirectories(Paths.get(configFacade.getTempFilesPath()));
		} catch (IOException e) {
			logger.error("Temp directory doesn't exist and creation failed.");
			throw e;
		}

		List<String> columnNames = new ArrayList<>();
		try {
			buildListOfFields(columnNames, Case.class, "");
		} catch (IntrospectionException e) {
			throw new IOException(e);
		}

		Path filePath = Paths.get(getCaseImportTemplateFilePath());
		CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(filePath.toString()));
		writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
		writer.flush();
		writer.close();
	}

	@Override
	public String getCaseImportTemplateFilePath() {
		Path exportDirectory = Paths.get(configFacade.getTempFilesPath());
		Path filePath = exportDirectory.resolve(CASE_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}
	
	@Override
	public String getSormasImportGuideFilePath() {
		Path exportDirectory = Paths.get(configFacade.getTempFilesPath());
		Path filePath = exportDirectory.resolve(SORMAS_IMPORT_GUIDE_FILE_NAME);
		return filePath.toString();
	}

	@Override
	public String importCasesFromCsvFile(String csvFilePath, String userUuid) throws IOException, InvalidColumnException {
		File file = new File(csvFilePath);
		if (!file.exists()) {
			throw new FileNotFoundException("Cases .csv file does not exist");
		}

		CSVReader reader = CSVUtils.createCSVReader(new FileReader(csvFilePath));

		// Build dictionary of column paths
		String[] headersLine = reader.readNext();
		List<String[]> headers = new ArrayList<>();
		for (String header : headersLine) {
			String[] headerPath = header.split("\\.");
			headers.add(headerPath);
		}

		// Create error report file
		Path exportDirectory = Paths.get(configFacade.getTempFilesPath());
		Path errorReportFilePath = exportDirectory.resolve("sormas_error_report_" + DataHelper.getShortUuid(userUuid) + "_" + DateHelper.formatDateForExport(new Date()) + ".csv");
		generateCaseErrorReportFile(headersLine, errorReportFilePath.toString());
		CSVWriter errorReportWriter = CSVUtils.createCSVWriter(new FileWriter(errorReportFilePath.toString(), true));

		// Create a new case for each line in the .csv file
		String[] nextLine;
		boolean hasImportError = false;
		while ((nextLine = reader.readNext()) != null) {
			// Check whether the new line has the same length as the header line
			if (nextLine.length > headersLine.length) {
				hasImportError = true;
				writeImportErrorToFile(errorReportWriter, nextLine, "This line is longer than the header line.");
				continue;
			}

			Case newCase = caseService.createCase();
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
					insertColumnEntryIntoCase(newCase, entry, entryHeaderPath);
				} catch (ImportErrorException e) {
					hasImportError = true;
					caseHasImportError = true;
					if (e.getCustomMessage() != null) {
						writeImportErrorToFile(errorReportWriter, nextLine, e.getCustomMessage());
					} else {
						writeImportErrorToFile(errorReportWriter, nextLine, "Invalid value " + e.getValue() + " in column " + e.getColumnName());
					}
					break;
				} catch (InvalidColumnException e) {
					reader.close();
					errorReportWriter.flush();
					errorReportWriter.close();
					throw e;
				}
			}

			if (!caseHasImportError) {
				// Check whether any required field that does not have a not null constraint in the database is empty
				if (newCase.getRegion() == null) {
					hasImportError = true;
					writeImportErrorToFile(errorReportWriter, nextLine, "You have to specify a valid region.");
					continue;
				}
				if (newCase.getDistrict() == null) {
					hasImportError = true;
					writeImportErrorToFile(errorReportWriter, nextLine, "You have to specify a valid district.");
					continue;
				}
				if (newCase.getHealthFacility() == null) {
					hasImportError = true;
					writeImportErrorToFile(errorReportWriter, nextLine, "You have to specify a valid health facility.");
					continue;
				}
				if (newCase.getDisease() == null) {
					hasImportError = true;
					writeImportErrorToFile(errorReportWriter, nextLine, "You have to specify a valid disease.");
					continue;
				}
				// Check whether there are any infrastructure errors
				if (!newCase.getDistrict().getRegion().equals(newCase.getRegion())) {
					hasImportError = true;
					writeImportErrorToFile(errorReportWriter, nextLine, "Could not find a database entry for the specified district in the specified region.");
					continue;
				}
				if (newCase.getCommunity() != null && !newCase.getCommunity().getDistrict().equals(newCase.getDistrict())) {
					hasImportError = true;
					writeImportErrorToFile(errorReportWriter, nextLine, "Could not find a database entry for the specified community in the specified district.");
					continue;
				}
				if (newCase.getCommunity() == null && !newCase.getHealthFacility().getDistrict().equals(newCase.getDistrict())) {
					hasImportError = true;
					writeImportErrorToFile(errorReportWriter, nextLine, "Could not find a database entry for the specified health facility in the specified district.");
					continue;
				}
				if (newCase.getCommunity() != null && !newCase.getHealthFacility().getCommunity().equals(newCase.getCommunity())) {
					hasImportError = true;
					writeImportErrorToFile(errorReportWriter, nextLine, "Could not find a database entry for the specified health facility in the specified community.");
					continue;
				}

				personService.ensurePersisted(newCase.getPerson());
				hospitalizationService.ensurePersisted(newCase.getHospitalization());
				epiDataService.ensurePersisted(newCase.getEpiData());
				caseFacade.saveCase(CaseFacadeEjbLocal.toDto(newCase));
			}
		}

		reader.close();
		errorReportWriter.flush();
		errorReportWriter.close();
		return hasImportError ? errorReportFilePath.toString() : null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void insertColumnEntryIntoCase(Case caze, String entry, String[] entryHeaderPath) throws InvalidColumnException, ImportErrorException {
		Object currentElement = caze;
		for (int i = 0; i < entryHeaderPath.length; i++) {
			String headerPathElementName = entryHeaderPath[i];

			try {
				if (i == entryHeaderPath.length - 1) {
					PropertyDescriptor pd = new PropertyDescriptor(headerPathElementName, currentElement.getClass());
					Class<?> propertyType = pd.getPropertyType();

					if (propertyType.isEnum()) {
						pd.getWriteMethod().invoke(currentElement, Enum.valueOf((Class<? extends Enum>) propertyType, entry));
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
					} else if (propertyType.isAssignableFrom(Region.class)) {
						Region region = regionService.getByName(entry);
						if (region != null) {
							pd.getWriteMethod().invoke(currentElement, region);
						} else {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						}
					} else if (propertyType.isAssignableFrom(District.class)) {
						District district = districtService.getByName(entry);
						if (district != null) {
							pd.getWriteMethod().invoke(currentElement, district);
						} else {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						}
					} else if (propertyType.isAssignableFrom(Community.class)) {
						Community community = communityService.getByName(entry);
						if (community != null) {
							pd.getWriteMethod().invoke(currentElement, community);
						} else {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						}
					} else if (propertyType.isAssignableFrom(Facility.class)) {
						Facility facility = facilityService.getByName(entry);
						if (facility != null) {
							pd.getWriteMethod().invoke(currentElement, facility);
						} else {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						}
					} else if (propertyType.isAssignableFrom(User.class)) {
						User user = userService.getByUserName(entry);
						if (user != null) {
							pd.getWriteMethod().invoke(currentElement, user);
						} else {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						}
					} else if (propertyType.isAssignableFrom(String.class)) {
						pd.getWriteMethod().invoke(currentElement, entry);
					} else {
						throw new UnsupportedOperationException ("Property type " + propertyType.getName() + " not allowed when importing cases.");
					}
				} else {
					currentElement = new PropertyDescriptor(headerPathElementName, currentElement.getClass()).getReadMethod().invoke(currentElement);
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

	private void writeImportErrorToFile(CSVWriter errorReportWriter, String[] nextLine, String message) throws IOException {
		List<String> nextLineAsList = new ArrayList<>();
		nextLineAsList.add(message);
		nextLineAsList.addAll(Arrays.asList(nextLine));
		errorReportWriter.writeNext(nextLineAsList.toArray(new String[nextLineAsList.size()]));
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

	/**
	 * Creates a zip by collecting all .csv files that match the file names of the passed databaseTables plus
	 * the date and randomNumber suffixes. The zip is stored in the same export folder that contains the .csv files
	 * and its file path is returned.
	 */
	private String createZipFromCsvFiles(List<DatabaseTable> databaseTables, String date, int randomNumber) throws ExportErrorException {
		try {
			Path path = new File(configFacade.getTempFilesPath()).toPath();
			String name = "sormas_export_" + date + "_" + randomNumber + ".zip";
			Path filePath = path.resolve(name);
			String zipPath = filePath.toString();
			FileOutputStream fos = new FileOutputStream(zipPath);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ZipOutputStream zos = new ZipOutputStream(bos);

			for (DatabaseTable databaseTable : databaseTables) {
				name = "sormas_export_" + databaseTable.getFileName() + "_" + date + "_" + randomNumber + ".csv";
				filePath = path.resolve(name);
				zos.putNextEntry(new ZipEntry(databaseTable.getFileName() + ".csv"));
				byte[] bytes = Files.readAllBytes(filePath);
				zos.write(bytes, 0, bytes.length);
				zos.closeEntry();
			}

			zos.close();
			return zipPath;
		} catch (IOException e) {
			logger.error("Failed to generate a zip file for database export.");
			throw new ExportErrorException();
		}
	}

	/**
	 * Generates the query used to create a .csv file of this table. In order to gain access to the server file system, a function
	 * that needs to be defined in the database is used. The path to save the .csv file to needs to be specified in the sormas.properties file.
	 */
	private Query generateCsvExportQuery(String fileName, String date, int randomNumber, String tableName) {
		Path path = new File(FacadeProvider.getConfigFacade().getTempFilesPath()).toPath();
		String name = "sormas_export_" + fileName + "_" + date + "_" + randomNumber + ".csv";
		Path filePath = path.resolve(name);
		return em.createNativeQuery("SELECT export_database('" + tableName + "', '" + filePath + "');");
	}

	/**
	 * Generates the query used to create a .csv file of a this table, joined with another table. This is specifially used to only retrieve
	 * the symptoms of cases or visits to export two different tables for this data.
	 */
	private Query generateCsvExportJoinQuery(String fileName, String date, int randomNumber, String tableName, String joinTableName, String columnName, String joinColumnName) {
		Path path = new File(FacadeProvider.getConfigFacade().getTempFilesPath()).toPath();
		String name = "sormas_export_" + fileName + "_" + date + "_" + randomNumber + ".csv";
		Path filePath = path.resolve(name);
		return em.createNativeQuery("SELECT export_database_join('" + tableName + "', '" + joinTableName + "', '" + tableName + "." + columnName + "', '" + joinTableName + "." + joinColumnName + "', '" + 
				filePath + "');");
	}

	private void buildListOfFields(List<String> fieldNames, Class<?> clazz, String prefix) throws IntrospectionException {
		for (PropertyDescriptor pd : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
			// Fields with the @ImportIgnore annotation are ignored
			if (pd.getReadMethod().isAnnotationPresent(ImportIgnore.class)) {
				continue;
			}
			// Fields without a getter or whose getters are declared in a superclass are ignored
			if (pd.getReadMethod() == null || pd.getReadMethod().getDeclaringClass() != clazz) {
				continue;
			}
			// List types are ignored
			if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
				continue;
			}
			// Certain field types are ignored
			if (pd.getPropertyType() == User.class) {
				continue;
			}
			// Other non-infrastructure entity class, recursively call this method to include fields of the sub-entity
			if (AbstractDomainObject.class.isAssignableFrom(pd.getPropertyType()) && !isInfrastructureClass(pd.getPropertyType())) {
				buildListOfFields(fieldNames, pd.getPropertyType(), prefix == null || prefix.isEmpty() ? pd.getName() + "." :  prefix + pd.getName() + ".");
			} else {
				// All other field types
				fieldNames.add(prefix + pd.getName());
			}
		}
	}

	private void generateCaseErrorReportFile(String[] columns, String filePath) throws IOException {
		List<String> columnNames = new ArrayList<>();
		columnNames.add(ERROR_COLUMN_NAME);
		for (String column : columns) {
			columnNames.add(column);
		}

		// If the file already exists, delete it
		File errorReportFile = new File(filePath);
		if (errorReportFile.exists()) {
			errorReportFile.delete();
		}

		CSVWriter writer = CSVUtils.createCSVWriter(new FileWriter(filePath));
		writer.writeNext(columnNames.toArray(new String[columnNames.size()]));
		writer.flush();
		writer.close();
	}

	private boolean isInfrastructureClass(Class<?> clazz) {
		return clazz == Region.class || clazz == District.class || clazz == Community.class || clazz == Facility.class;
	}

	@LocalBean
	@Stateless
	public static class ImportExportFacadeEjbLocal extends ImportExportFacadeEjb {
	}

}

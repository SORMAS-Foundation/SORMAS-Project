package de.symeda.sormas.backend.importexport;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.ImportIgnore;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ImportFacade")
public class ImportFacadeEjb implements ImportFacade {

	@Resource
	private SessionContext sessionContext;
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
	private PersonFacadeEjbLocal personFacade;
	@EJB
	private PersonService personService;
	@EJB
	private HospitalizationService hospitalizationService;
	@EJB
	private EpiDataService epiDataService;
	
	private static final Logger logger = LoggerFactory.getLogger(ImportFacadeEjb.class);

	private static final String CASE_IMPORT_TEMPLATE_FILE_NAME = ImportExportUtils.FILE_PREFIX + "_import_case_template.csv";
	private static final String SORMAS_IMPORT_GUIDE_FILE_NAME = ImportExportUtils.FILE_PREFIX.toUpperCase() + "_Import_Guide.pdf";
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
		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(CASE_IMPORT_TEMPLATE_FILE_NAME);
		return filePath.toString();
	}
	
	@Override
	public String getSormasImportGuideFilePath() {
		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		Path filePath = exportDirectory.resolve(SORMAS_IMPORT_GUIDE_FILE_NAME);
		return filePath.toString();
	}
	
	private Case buildCase() {
		Case caze = new Case();
    	caze.setUuid(DataHelper.createUuid());
    	
    	caze.setInvestigationStatus(InvestigationStatus.PENDING);
    	caze.setCaseClassification(CaseClassification.NOT_CLASSIFIED);
    	caze.setOutcome(CaseOutcome.NO_OUTCOME);
    	
    	caze.setPerson(personService.createPerson());
    	
    	caze.setReportDate(new Date());
    	User user = userService.getByUserName(sessionContext.getCallerPrincipal().getName());
    	caze.setReportingUser(user);
    	
    	return caze;
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
		Path errorReportFilePath = exportDirectory.resolve(ImportExportUtils.TEMP_FILE_PREFIX + "_error_report_" + DataHelper.getShortUuid(userUuid) + "_" + DateHelper.formatDateForExport(new Date()) + ".csv");
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

			Case newCase = buildCase();
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
					writeImportErrorToFile(errorReportWriter, nextLine, e.getMessage());
					break;
				} catch (InvalidColumnException e) {
					reader.close();
					errorReportWriter.flush();
					errorReportWriter.close();
					throw e;
				}
			}

			if (!caseHasImportError) {
				// It's necessary to use the save methods from the facades because of the additional logic they contain
				try {
					personFacade.savePerson(PersonFacadeEjbLocal.toDto(newCase.getPerson()));
					caseFacade.saveCase(CaseFacadeEjbLocal.toDto(newCase));
				} catch (ValidationRuntimeException e) {
					hasImportError = true;
					writeImportErrorToFile(errorReportWriter, nextLine, e.getMessage());
					continue;
				}
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
					} else if (propertyType.isAssignableFrom(Region.class)) {
						List<Region> region = regionService.getByName(entry);
						if (region.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						} else if (region.size() > 1) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Region name is not unique, make sure there is only one region with this name in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, region.get(0));
						}
					} else if (propertyType.isAssignableFrom(District.class)) {
						List<District> district = districtService.getByName(entry, caze.getRegion());
						if (district.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						} else if (district.size() > 1) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; District name is not unique in the chosen region, make sure there is only one district with this name belonging to the chosen region in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, district.get(0));
						}
					} else if (propertyType.isAssignableFrom(Community.class)) {
						List<Community> community = communityService.getByName(entry, caze.getDistrict());
						if (community.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						} else if (community.size() > 1) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Community name is not unique in the chosen district, make sure there is only one community with this name belonging to the chosen district in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, community.get(0));
						}
					} else if (propertyType.isAssignableFrom(Facility.class)) {
						List<Facility> facility = facilityService.getHealthFacilitiesByName(entry, caze.getDistrict(), caze.getCommunity());
						if (facility.isEmpty()) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Entry does not exist in the database");
						} else if (facility.size() > 1 && caze.getCommunity() == null) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Facility name is not unique in the chosen district, make sure there is only one facility with this name belonging to the chosen district in the database or specify a community");
						} else if (facility.size() > 1 && caze.getCommunity() != null) {
							throw new ImportErrorException("Invalid value \"" + entry + "\" in column " + buildHeaderPathString(entryHeaderPath) + "; Facility name is not unique in the chosen community, make sure there is only one facility with this name belonging to the chosen community in the database");
						} else {
							pd.getWriteMethod().invoke(currentElement, facility.get(0));
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

	private void writeImportErrorToFile(CSVWriter errorReportWriter, String[] nextLine, String message) throws IOException {
		List<String> nextLineAsList = new ArrayList<>();
		nextLineAsList.add(message);
		nextLineAsList.addAll(Arrays.asList(nextLine));
		errorReportWriter.writeNext(nextLineAsList.toArray(new String[nextLineAsList.size()]));
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
				// All other field types - make sure that region, district, community and healthFacility are entered in this order
				if (pd.getName().equals("region") && fieldNames.contains("district")) {
					fieldNames.add(fieldNames.indexOf("district"), prefix + pd.getName());
				} else if (pd.getName().equals("district") && fieldNames.contains("community")) {
					fieldNames.add(fieldNames.indexOf("community"), prefix + pd.getName());
				} else if (pd.getName().equals("community") && fieldNames.contains("healthFacility")) {
					fieldNames.add(fieldNames.indexOf("healthFacility"), prefix + pd.getName());
				} else {
					fieldNames.add(prefix + pd.getName());
				}
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
	public static class ImportFacadeEjbLocal extends ImportFacadeEjb {
	}

}

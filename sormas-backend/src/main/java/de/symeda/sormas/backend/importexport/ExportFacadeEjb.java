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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportFacade;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
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
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.Therapy;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.visit.Visit;

@Stateless(name = "ExportFacade")
public class ExportFacadeEjb implements ExportFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

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
	@EJB
	private ExportConfigurationService exportConfigurationService;

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
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Case.TABLE_NAME).execute();
				break;
			case HOSPITALIZATIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Hospitalization.TABLE_NAME).execute();
				break;
			case PREVIOUSHOSPITALIZATIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, PreviousHospitalization.TABLE_NAME).execute();
				break;
			case EPIDATA:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EpiData.TABLE_NAME).execute();
				break;
			case EPIDATABURIALS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EpiDataBurial.TABLE_NAME).execute();
				break;
			case EPIDATAGATHERINGS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EpiDataGathering.TABLE_NAME).execute();
				break;
			case EPIDATATRAVELS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EpiDataTravel.TABLE_NAME).execute();
				break;
			case THERAPIES:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Therapy.TABLE_NAME).execute();
				break;
			case PRESCRIPTIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Prescription.TABLE_NAME).execute();
				break;
			case TREATMENTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Treatment.TABLE_NAME).execute();
				break;
			case CLINICAL_COURSES:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, ClinicalCourse.TABLE_NAME).execute();
				break;
			case HEALTH_CONDITIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, HealthConditions.TABLE_NAME).execute();
				break;
			case CLINICAL_VISITS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, ClinicalVisit.TABLE_NAME).execute();
				break;
			case CONTACTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Contact.TABLE_NAME).execute();
				break;
			case VISITS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Visit.TABLE_NAME).execute();
				break;
			case EVENTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Event.TABLE_NAME).execute();
				break;
			case EVENTPARTICIPANTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, EventParticipant.TABLE_NAME).execute();
				break;
			case SAMPLES:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Sample.TABLE_NAME).execute();
				break;
			case SAMPLETESTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, PathogenTest.TABLE_NAME).execute();
				break;
			case TASKS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Task.TABLE_NAME).execute();
				break;
			case PERSONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Person.TABLE_NAME).execute();
				break;
			case LOCATIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Location.TABLE_NAME).execute();
				break;
			case REGIONS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Region.TABLE_NAME).execute();
				break;
			case DISTRICTS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, District.TABLE_NAME).execute();
				break;
			case COMMUNITIES:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Community.TABLE_NAME).execute();
				break;
			case FACILITIES:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Facility.TABLE_NAME).execute();
				break;
			case OUTBREAKS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Outbreak.TABLE_NAME).execute();
				break;
			case CASE_SYMPTOMS:
				generateCsvExportQuery(databaseTable.getFileName(), date, randomNumber, Symptoms.TABLE_NAME).execute();
				break;
			case VISIT_SYMPTOMS:
				generateCsvExportJoinQuery(databaseTable.getFileName(), date, randomNumber, Symptoms.TABLE_NAME, Visit.TABLE_NAME, "id", "symptoms_id").execute();
				break;	
			case CLINICAL_VISIT_SYMPTOMS:
				generateCsvExportJoinQuery(databaseTable.getFileName(), date, randomNumber, Symptoms.TABLE_NAME, ClinicalVisit.TABLE_NAME, "id", "symptoms_id").execute();
				break;
			}
		}

		// Create a zip containing all created .csv files
		return createZipFromCsvFiles(databaseTables.stream().map(t -> t.getFileName()).collect(Collectors.toList()), date, randomNumber);
	}

	@Override
	public String generateZipArchive(String date, int randomNumber) {
		Path path = new File(configFacade.getTempFilesPath()).toPath();
		String fileName = ImportExportUtils.TEMP_FILE_PREFIX + "_export_" + DateHelper.formatDateForExport(new Date()) + "_" + new Random().nextInt(Integer.MAX_VALUE) + ".zip";
		Path filePath = path.resolve(fileName);
		String zipPath = filePath.toString();
		return zipPath;
	}
	
	@Override
	public List<ExportConfigurationDto> getExportConfigurations() {
		User user = userService.getCurrentUser();
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExportConfiguration> cq = cb.createQuery(ExportConfiguration.class);
		Root<ExportConfiguration> config = cq.from(ExportConfiguration.class);
		
		cq.where(cb.equal(config.get(ExportConfiguration.USER), user));
		cq.orderBy(cb.desc(config.get(ExportConfiguration.CHANGE_DATE)));
		
		return em.createQuery(cq).getResultList().stream()
				.map(c -> toExportConfigurationDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public void saveExportConfiguration(ExportConfigurationDto exportConfiguration) {
		ExportConfiguration entity = fromExportConfigurationDto(exportConfiguration);
		exportConfigurationService.ensurePersisted(entity);
	}
	
	@Override
	public void deleteExportConfiguration(String exportConfigurationUuid) {
		ExportConfiguration exportConfiguration = exportConfigurationService.getByUuid(exportConfigurationUuid);
		exportConfigurationService.delete(exportConfiguration);
	}
	
	/**
	 * Creates a zip by collecting all .csv files that match the file names of the passed databaseTables plus
	 * the date and randomNumber suffixes. The zip is stored in the same export folder that contains the .csv files
	 * and its file path is returned.
	 */
	private String createZipFromCsvFiles(List<String> fileNames, String date, int randomNumber) throws ExportErrorException {
		Path tempPath = new File(configFacade.getTempFilesPath()).toPath();
		String zipPath = generateZipArchive(date, randomNumber);
		try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream( new FileOutputStream(zipPath)))) {
			for (String fileName : fileNames) {
				String name = ImportExportUtils.TEMP_FILE_PREFIX + "_export_" + fileName + "_" + date + "_" + randomNumber + ".csv";
				Path filePath = tempPath.resolve(name);
				zos.putNextEntry(new ZipEntry(fileName + ".csv"));
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
	private StoredProcedureQuery generateCsvExportQuery(String fileName, String date, int randomNumber, String tableName) {
		Path path = new File(FacadeProvider.getConfigFacade().getTempFilesPath()).toPath();
		String name = ImportExportUtils.TEMP_FILE_PREFIX + "_export_" + fileName + "_" + date + "_" + randomNumber + ".csv";
		Path filePath = path.resolve(name);
		StoredProcedureQuery query = em.createStoredProcedureQuery("export_database");
		query.registerStoredProcedureParameter("table_name", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("file_path", String.class, ParameterMode.IN);
		query.setParameter("table_name", tableName);
		query.setParameter("file_path", filePath.toString());
		return query;
	}

	/**
	 * Generates the query used to create a .csv file of a this table, joined with another table. This is specifially used to only retrieve
	 * the symptoms of cases or visits to export two different tables for this data.
	 */
	private StoredProcedureQuery generateCsvExportJoinQuery(String fileName, String date, int randomNumber, String tableName, String joinTableName, String columnName, String joinColumnName) {
		Path path = new File(FacadeProvider.getConfigFacade().getTempFilesPath()).toPath();
		String name = ImportExportUtils.TEMP_FILE_PREFIX + "_export_" + fileName + "_" + date + "_" + randomNumber + ".csv";
		Path filePath = path.resolve(name);
		StoredProcedureQuery query = em.createStoredProcedureQuery("export_database_join");
		query.registerStoredProcedureParameter("table_name", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("join_table_name", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("column_name", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("join_column_name", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("file_path", String.class, ParameterMode.IN);
		query.setParameter("table_name", tableName);
		query.setParameter("join_table_name", joinTableName);
		query.setParameter("column_name", tableName + "." + columnName);
		query.setParameter("join_column_name", joinTableName + "." + joinColumnName);		
		query.setParameter("file_path", filePath.toString());
		return query;
	}
	
	public ExportConfiguration fromExportConfigurationDto(@NotNull ExportConfigurationDto source) {
		ExportConfiguration target = exportConfigurationService.getByUuid(source.getUuid());
		if (target == null) {
			target = new ExportConfiguration();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		DtoHelper.validateDto(source, target);
		
		target.setName(source.getName());
		target.setUser(userService.getByReferenceDto(source.getUser()));
		target.setExportType(source.getExportType());
		target.setProperties(source.getProperties());
		
		return target;
	}
	
	public static ExportConfigurationDto toExportConfigurationDto(ExportConfiguration source) {
		if (source == null) {
			return null;
		}
		
		ExportConfigurationDto target = new ExportConfigurationDto();
		DtoHelper.fillDto(target, source);

		target.setName(source.getName());
		target.setUser(UserFacadeEjb.toReferenceDto(source.getUser()));
		target.setExportType(source.getExportType());
		target.setProperties(source.getProperties());
		
		return target;
	}

	@LocalBean
	@Stateless
	public static class ExportFacadeEjbLocal extends ExportFacadeEjb {
	}

}

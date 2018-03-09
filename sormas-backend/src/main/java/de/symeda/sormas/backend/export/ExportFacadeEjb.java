package de.symeda.sormas.backend.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.export.DatabaseTable;
import de.symeda.sormas.api.export.ExportFacade;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataBurial;
import de.symeda.sormas.backend.epidata.EpiDataGathering;
import de.symeda.sormas.backend.epidata.EpiDataTravel;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.outbreak.Outbreak;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleTest;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.visit.Visit;

@Stateless(name = "ExportFacade")
public class ExportFacadeEjb implements ExportFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private ConfigFacadeEjbLocal configFacade;

	private static final Logger logger = LoggerFactory.getLogger(CaseFacadeEjb.class);

	@Override
	public String generateDatabaseExportArchive(List<DatabaseTable> databaseTables) throws ExportErrorException {
		// Create the folder if it doesn't exist
		try {
			Files.createDirectories(Paths.get(configFacade.getExportPath()));
		} catch (IOException e) {
			logger.error("Export directory doesn't exist and creation failed.");
			throw new ExportErrorException();
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

	/**
	 * Creates a zip by collecting all .csv files that match the file names of the passed databaseTables plus
	 * the date and randomNumber suffixes. The zip is stored in the same export folder that contains the .csv files
	 * and its file path is returned.
	 */
	private String createZipFromCsvFiles(List<DatabaseTable> databaseTables, String date, int randomNumber) throws ExportErrorException {
		try {
			Path path = new File(configFacade.getExportPath()).toPath();
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
		Path path = new File(FacadeProvider.getConfigFacade().getExportPath()).toPath();
		String name = "sormas_export_" + fileName + "_" + date + "_" + randomNumber + ".csv";
		Path filePath = path.resolve(name);
		return em.createNativeQuery("SELECT export_database('" + tableName + "', '" + filePath + "');");
	}

	/**
	 * Generates the query used to create a .csv file of a this table, joined with another table. This is specifially used to only retrieve
	 * the symptoms of cases or visits to export two different tables for this data.
	 */
	private Query generateCsvExportJoinQuery(String fileName, String date, int randomNumber, String tableName, String joinTableName, String columnName, String joinColumnName) {
		Path path = new File(FacadeProvider.getConfigFacade().getExportPath()).toPath();
		String name = "sormas_export_" + fileName + "_" + date + "_" + randomNumber + ".csv";
		Path filePath = path.resolve(name);
		return em.createNativeQuery("SELECT export_database_join('" + tableName + "', '" + joinTableName + "', '" + tableName + "." + columnName + "', '" + joinTableName + "." + joinColumnName + "', '" + 
				filePath + "');");
	}

	@LocalBean
	@Stateless
	public static class ExportFacadeEjbLocal extends ExportFacadeEjb {
	}

}

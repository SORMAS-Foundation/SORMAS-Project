package de.symeda.sormas.backend.importexport;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
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
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.Therapy;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.visit.Visit;

/**
 * Exporting data directly from the PostgreSQL database with COPY commands as .csv files.
 * 
 * @author Stefan Kock
 */
@Stateless
@LocalBean
public class DatabaseExportService {

	private static final String COPY_SINGLE_TABLE = "COPY (SELECT * FROM %s) TO STDOUT WITH (FORMAT CSV, DELIMITER ';', HEADER)";
	private static final String COPY_WITH_JOIN_TABLE =
		"COPY (SELECT * FROM %s AS root_table INNER JOIN %s AS leaf_table ON (root_table.%s = leaf_table.%s)) TO STDOUT WITH (FORMAT CSV, DELIMITER ';', HEADER)";

	private static final Map<DatabaseTable, DatabaseExportConfiguration> EXPORT_CONFIGS = new LinkedHashMap<>();
	static {
		EXPORT_CONFIGS.put(DatabaseTable.CASES, new DatabaseExportConfiguration(Case.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.HOSPITALIZATIONS, new DatabaseExportConfiguration(Hospitalization.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.PREVIOUSHOSPITALIZATIONS, new DatabaseExportConfiguration(PreviousHospitalization.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.EPIDATA, new DatabaseExportConfiguration(EpiData.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.EPIDATABURIALS, new DatabaseExportConfiguration(EpiDataBurial.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.EPIDATAGATHERINGS, new DatabaseExportConfiguration(EpiDataGathering.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.EPIDATATRAVELS, new DatabaseExportConfiguration(EpiDataTravel.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.THERAPIES, new DatabaseExportConfiguration(Therapy.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.PRESCRIPTIONS, new DatabaseExportConfiguration(Prescription.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.TREATMENTS, new DatabaseExportConfiguration(Treatment.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.CLINICAL_COURSES, new DatabaseExportConfiguration(ClinicalCourse.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.HEALTH_CONDITIONS, new DatabaseExportConfiguration(HealthConditions.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.CLINICAL_VISITS, new DatabaseExportConfiguration(ClinicalVisit.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.CONTACTS, new DatabaseExportConfiguration(Contact.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.VISITS, new DatabaseExportConfiguration(Visit.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.EVENTS, new DatabaseExportConfiguration(Event.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.EVENTPARTICIPANTS, new DatabaseExportConfiguration(EventParticipant.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.SAMPLES, new DatabaseExportConfiguration(Sample.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.SAMPLETESTS, new DatabaseExportConfiguration(PathogenTest.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.TASKS, new DatabaseExportConfiguration(Task.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.PERSONS, new DatabaseExportConfiguration(Person.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.LOCATIONS, new DatabaseExportConfiguration(Location.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.REGIONS, new DatabaseExportConfiguration(Region.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.DISTRICTS, new DatabaseExportConfiguration(District.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.COMMUNITIES, new DatabaseExportConfiguration(Community.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.FACILITIES, new DatabaseExportConfiguration(Facility.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.OUTBREAKS, new DatabaseExportConfiguration(Outbreak.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.CASE_SYMPTOMS, new DatabaseExportConfiguration(Symptoms.TABLE_NAME));
		EXPORT_CONFIGS.put(DatabaseTable.VISIT_SYMPTOMS, new DatabaseExportConfiguration(Symptoms.TABLE_NAME, Visit.TABLE_NAME, "id", "symptoms_id"));
		EXPORT_CONFIGS
			.put(
				DatabaseTable.CLINICAL_VISIT_SYMPTOMS,
				new DatabaseExportConfiguration(Symptoms.TABLE_NAME, ClinicalVisit.TABLE_NAME, "id", "symptoms_id"));
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = ModelConstants.PERSISTENCE_UNIT_DATA_SOURCE)
	private DataSource database;

	/**
	 * Export the given {@code databaseTables} as separate .csv files to the temp directory.
	 * 
	 * @param databaseTables
	 *            The tables to export as separate .csv files.
	 * @param formattedDate
	 *            Used to identify the files of the same export.
	 * @param exportId
	 *            Used to identify the files of the same export.
	 */
	public void exportAsCsvFiles(List<DatabaseTable> databaseTables, String formattedDate, int exportId) {

		for (DatabaseTable databaseTable : databaseTables) {

			DatabaseExportConfiguration config = getConfig(databaseTable);
			final String sql;
			if (config.isUseJoinTable()) {
				sql = String
					.format(
						COPY_WITH_JOIN_TABLE,
						config.getTableName(),
						config.getJoinTableName(),
						config.getColumnName(),
						config.getJoinColumnName());
			} else {
				sql = String.format(COPY_SINGLE_TABLE, config.getTableName());
			}
			copyToCsvFile(databaseTable.getFileName(), formattedDate, exportId, sql);
		}
	}

	/**
	 * Run an export command and store the result directly into a .csv file.
	 * 
	 * @param fileName
	 *            Human readable file name similar to selected entry.
	 * @param formattedDate
	 *            Used to identify the files of the same export.
	 * @param exportId
	 *            Used to identify the files of the same export.
	 * @param sql
	 *            Actual native sql command to copy data to CSV.
	 * @return The full filename to where the export is done in {@link ConfigFacade#getTempFilesPath()}.
	 */
	private String copyToCsvFile(String fileName, String formattedDate, int exportId, String sql) {

		long startTime = System.currentTimeMillis();

		String fullFilename = ImportExportUtils.TEMP_FILE_PREFIX + "_export_" + fileName + "_" + formattedDate + "_" + exportId + ".csv";
		Path tempFilesPath = new File(FacadeProvider.getConfigFacade().getTempFilesPath()).toPath();
		Path filePath = tempFilesPath.resolve(fullFilename);
		File file = new File(filePath.toString());

		try (Writer target = new FileWriterWithEncoding(file, StandardCharsets.UTF_8)) {

			/*
			 * Here happens the PostgreSQL specific magic, which is not covered by JPA
			 * and therefore solved with org.postgresql implementations.
			 * The connection is manually closed to give it back to the connection pool.
			 */
			Connection conn = database.getConnection();
			BaseConnection pgConn = conn.unwrap(BaseConnection.class);
			CopyManager copyManager = (pgConn).getCopyAPI();
			copyManager.copyOut(sql, target);
			conn.close();

			logger
				.trace(
					"copyToCsvFile(): Exported '{}' in {} ms. fullFilename='{}', sql='{}'",
					fileName,
					System.currentTimeMillis() - startTime,
					fullFilename,
					sql);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (SQLException e) {
			throw new RuntimeException(String.format("Failed to export '%s' with COPY operation", fileName), e);
		}

		return fullFilename;
	}

	static DatabaseExportConfiguration getConfig(DatabaseTable databaseTable) {

		// leave EXPORT_CONFIGS strictly private to fulfill the expectation to a constant
		return EXPORT_CONFIGS.get(databaseTable);
	}
}

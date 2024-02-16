/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.importexport;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.backend.action.Action;
import de.symeda.sormas.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.backend.campaign.Campaign;
import de.symeda.sormas.backend.campaign.data.CampaignFormData;
import de.symeda.sormas.backend.campaign.diagram.CampaignDiagramDefinition;
import de.symeda.sormas.backend.campaign.form.CampaignFormMeta;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.maternalhistory.MaternalHistory;
import de.symeda.sormas.backend.caze.porthealthinfo.PortHealthInfo;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfiguration;
import de.symeda.sormas.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.backend.document.Document;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventGroup;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.exposure.Exposure;
import de.symeda.sormas.backend.externalmessage.ExternalMessage;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReport;
import de.symeda.sormas.backend.externalmessage.labmessage.TestReport;
import de.symeda.sormas.backend.feature.FeatureConfiguration;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.PopulationData;
import de.symeda.sormas.backend.infrastructure.area.Area;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.outbreak.Outbreak;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonContactDetail;
import de.symeda.sormas.backend.report.AggregateReport;
import de.symeda.sormas.backend.report.WeeklyReport;
import de.symeda.sormas.backend.report.WeeklyReportEntry;
import de.symeda.sormas.backend.sample.AdditionalTest;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.share.ExternalShareInfo;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequest;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.specialcaseaccess.SpecialCaseAccess;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.therapy.Prescription;
import de.symeda.sormas.backend.therapy.Therapy;
import de.symeda.sormas.backend.therapy.Treatment;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.vaccination.Vaccination;
import de.symeda.sormas.backend.visit.Visit;

/**
 * Exporting data directly from the PostgreSQL database with COPY commands as .csv files.
 * 
 * @author Stefan Kock
 */
@Stateless
@LocalBean
public class DatabaseExportService {

	private static final String COPY_SINGLE_TABLE = "COPY (SELECT * FROM %s) TO STDOUT WITH (FORMAT CSV, DELIMITER '%s', HEADER)";
	public static final String COUNT_TABLE_COLUMNS = "SELECT COUNT(column_name) FROM information_schema.columns WHERE table_name=:tableName";

	static final Map<DatabaseTable, String> EXPORT_CONFIGS = new LinkedHashMap<>();

	static {
		EXPORT_CONFIGS.put(DatabaseTable.CASES, Case.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.HOSPITALIZATIONS, Hospitalization.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.PREVIOUSHOSPITALIZATIONS, PreviousHospitalization.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.EPIDATA, EpiData.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.EXPOSURES, Exposure.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.ACTIVITIES_AS_CASE, ActivityAsCase.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.THERAPIES, Therapy.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.PRESCRIPTIONS, Prescription.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.TREATMENTS, Treatment.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CLINICAL_COURSES, ClinicalCourse.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.HEALTH_CONDITIONS, HealthConditions.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CLINICAL_VISITS, ClinicalVisit.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.PORT_HEALTH_INFO, PortHealthInfo.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.MATERNAL_HISTORIES, MaternalHistory.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SPECIAL_CASE_ACCESSES, SpecialCaseAccess.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CONTACTS, Contact.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.VISITS, Visit.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CONTACTS_VISITS, Visit.CONTACTS_VISITS_TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.EVENTS, Event.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.EVENTS_EVENTGROUPS, Event.EVENTS_EVENT_GROUPS_TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.EVENTGROUPS, EventGroup.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.EVENTPARTICIPANTS, EventParticipant.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.ACTIONS, Action.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.TRAVEL_ENTRIES, TravelEntry.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.IMMUNIZATIONS, Immunization.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.VACCINATIONS, Vaccination.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SAMPLES, Sample.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.PATHOGEN_TESTS, PathogenTest.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.ADDITIONAL_TESTS, AdditionalTest.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.TASKS, Task.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.TASK_OBSERVER, Task.TASK_OBSERVER_TABLE);
		EXPORT_CONFIGS.put(DatabaseTable.PERSONS, Person.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.PERSON_CONTACT_DETAILS, PersonContactDetail.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.PERSON_LOCATIONS, Person.PERSON_LOCATIONS_TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.LOCATIONS, Location.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CONTINENTS, Continent.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SUBCONTINENTS, Subcontinent.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.COUNTRIES, Country.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.AREAS, Area.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.REGIONS, Region.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.DISTRICTS, District.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.COMMUNITIES, Community.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.FACILITIES, Facility.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.POINTS_OF_ENTRY, PointOfEntry.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.OUTBREAKS, Outbreak.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CUSTOMIZABLE_ENUM_VALUES, CustomizableEnumValue.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SYMPTOMS, Symptoms.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CAMPAIGNS, Campaign.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CAMPAIGN_CAMPAIGNFORMMETA, Campaign.CAMPAIGN_CAMPAIGNFORMMETA_TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CAMPAIGN_FORM_META, CampaignFormMeta.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CAMPAIGN_FORM_DATA, CampaignFormData.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.CAMPAIGN_DIAGRAM_DEFINITIONS, CampaignDiagramDefinition.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.EXTERNAL_MESSAGES, ExternalMessage.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SAMPLE_REPORTS, SampleReport.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.TEST_REPORTS, TestReport.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SORMAS_TO_SORMAS_ORIGIN_INFO, SormasToSormasOriginInfo.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SORMAS_TO_SORMAS_SHARE_INFO, SormasToSormasShareInfo.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SORMAS_TO_SORMAS_SHARE_REQUESTS, SormasToSormasShareRequest.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SHARE_REQUEST_INFO, ShareRequestInfo.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SHARE_REQUEST_INFO_SHARE_INFO, ShareRequestInfo.SHARE_REQUEST_INFO_SHARE_INFO_TABLE);
		EXPORT_CONFIGS.put(DatabaseTable.EXTERNAL_SHARE_INFO, ExternalShareInfo.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.USERS, User.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.USER_ROLES, User.TABLE_NAME_USERROLES);
		EXPORT_CONFIGS.put(DatabaseTable.USERS_USERROLES, User.TABLE_NAME_USERS_USERROLES);
		EXPORT_CONFIGS.put(DatabaseTable.USERROLES_USERRIGHTS, User.TABLE_NAME_USERROLES_USERRIGHTS);
		EXPORT_CONFIGS.put(DatabaseTable.USERROLES_EMAILNOTIFICATIONTYPES, User.TABLE_NAME_USERROLES_EMAILNOTIFICATIONTYPES);
		EXPORT_CONFIGS.put(DatabaseTable.USERROLES_SMSNOTIFICATIONTYPES, User.TABLE_NAME_USERROLES_SMSNOTIFICATIONTYPES);
		EXPORT_CONFIGS.put(DatabaseTable.POPULATION_DATA, PopulationData.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.SURVEILLANCE_REPORTS, SurveillanceReport.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.AGGREGATE_REPORTS, AggregateReport.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.WEEKLY_REPORTS, WeeklyReport.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.WEEKLY_REPORT_ENTRIES, WeeklyReportEntry.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.DOCUMENTS, Document.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.EXPORT_CONFIGURATIONS, ExportConfiguration.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.FEATURE_CONFIGURATIONS, FeatureConfiguration.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.DISEASE_CONFIGURATIONS, DiseaseConfiguration.TABLE_NAME);
		EXPORT_CONFIGS.put(DatabaseTable.DELETION_CONFIGURATIONS, DeletionConfiguration.TABLE_NAME);
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public void exportAsCsvFiles(ZipOutputStream zos, List<DatabaseTable> databaseTables) throws IOException {

		//Writer must not be closed so it does not close the zip too early
		Writer writer = new OutputStreamWriter(zos, StandardCharsets.UTF_8);

		List<FeatureConfigurationDto> featureConfigurations = featureConfigurationFacade.getActiveServerFeatureConfigurations();

		// Export all selected tables to .csv files
		for (DatabaseTable databaseTable : databaseTables) {
			if (!databaseTable.isEnabled(featureConfigurations, configFacade)) {
				continue;
			}

			zos.putNextEntry(new ZipEntry(databaseTable.getFileName() + ".csv"));
			String tableName = getTableName(databaseTable);
			addEntityNamesRow(tableName, writer);
			addDataRows(databaseTable, tableName, writer);
			writer.flush();
			zos.closeEntry();
		}
	}

	private void addEntityNamesRow(String tableName, Writer writer) throws IOException {
		final int mainTableColumnCount = getColumnCount(tableName);
		char csvSeparator = configFacade.getCsvSeparator();
		if (mainTableColumnCount > 0) {
			writer.write(tableName);
		}
		for (int i = 0; i < mainTableColumnCount - 1; i++) {
			writer.write(csvSeparator + tableName);
		}

		writer.write('\n');
	}

	private int getColumnCount(String tableName) {
		BigInteger bigIntegerResult = (BigInteger) em.createNativeQuery(COUNT_TABLE_COLUMNS).setParameter("tableName", tableName).getSingleResult();
		return bigIntegerResult.intValue();
	}

	private void addDataRows(DatabaseTable databaseTable, String tableName, Writer writer) {
		long startTime = System.currentTimeMillis();
		final String sql = String.format(COPY_SINGLE_TABLE, tableName, configFacade.getCsvSeparator());

		writeCsv(writer, sql, databaseTable.getFileName());

		// Be able to check performance for each export query
		logger
			.trace("exportAsCsvFiles(): Exported '{}' in {} ms. sql='{}'", databaseTable.getFileName(), System.currentTimeMillis() - startTime, sql);
	}

	/**
	 * Run an export command and write the result directly into a Writer
	 * 
	 * @param writer
	 * @param sql
	 *            Actual native sql command to copy data to CSV.
	 * @param fileName
	 *            for debugging purposes: Human readable file name similar to selected entry
	 */
	private void writeCsv(Writer writer, String sql, String fileName) {
		/*
		 * Here happens the PostgreSQL specific magic, which is not covered by JPA and
		 * therefore solved with org.postgresql implementations.
		 */
		Session session = em.unwrap(Session.class);
		session.doWork(conn -> {
			PGConnection pgConn = conn.unwrap(PGConnection.class);
			CopyManager copyManager = pgConn.getCopyAPI();
			try {
				copyManager.copyOut(sql, writer);
			} catch (SQLException e) {
				throw new RuntimeException(String.format("Failed to export '%s' with COPY operation", fileName), e);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	static String getTableName(DatabaseTable databaseTable) {

		// leave EXPORT_CONFIGS strictly private to fulfill the expectation to a constant
		return EXPORT_CONFIGS.get(databaseTable);
	}
}

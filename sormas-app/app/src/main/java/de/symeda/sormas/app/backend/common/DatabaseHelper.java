/*
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
 */

package de.symeda.sormas.app.backend.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationCriteria;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationCriteriaDao;
import de.symeda.sormas.app.backend.config.Config;
import de.symeda.sormas.app.backend.config.ConfigDao;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataBurialDao;
import de.symeda.sormas.app.backend.epidata.EpiDataDao;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataGatheringDao;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.backend.epidata.EpiDataTravelDao;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.event.EventParticipantDao;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDao;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.HospitalizationDao;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalizationDao;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDao;
import de.symeda.sormas.app.backend.outbreak.Outbreak;
import de.symeda.sormas.app.backend.outbreak.OutbreakDao;
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.CommunityDao;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDao;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDao;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.report.WeeklyReportDao;
import de.symeda.sormas.app.backend.report.WeeklyReportEntry;
import de.symeda.sormas.app.backend.report.WeeklyReportEntryDao;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.backend.sample.SampleTestDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDao;
import de.symeda.sormas.app.backend.synclog.SyncLog;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDao;
import de.symeda.sormas.app.backend.user.UserRoleConfig;
import de.symeda.sormas.app.backend.user.UserRoleConfigDao;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.backend.visit.VisitDao;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 * @see <a href="http://stackoverflow.com/questions/17529766/view-contents-of-database-file-in-android-studio">Viewing databases from Android Studio</a>
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application. Stored in data/data/de.symeda.sormas.app/databases
	public static final String DATABASE_NAME = "sormas.db";
	// any time you make changes to your database objects, you may have to increase the database version
	public static final int DATABASE_VERSION = 140;

	private static DatabaseHelper instance = null;
	public static void init(Context context) {
		if (instance != null) {
			Log.e(DatabaseHelper.class.getName(),"DatabaseHelper has already been initalized");
		}
		instance = new DatabaseHelper(context);
	}

	private boolean clearingTables = false;

	private ConfigDao configDao = null;
	private final Context context;

	private final HashMap<Class<? extends AbstractDomainObject>, AbstractAdoDao<? extends AbstractDomainObject>> adoDaos = new HashMap<>();

	private SyncLogDao syncLogDao = null;

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);//, R.raw.ormlite_config);
		this.context = context;
		// HACK to make sure database is initialized - otherwise we could run into problems caused by threads
		this.getReadableDatabase();
	}

	public static void clearTables(boolean clearInfrastructure) {
		if (instance.clearingTables) {
			return;
		}
		instance.clearingTables = true;

		try {
			ConnectionSource connectionSource = getCaseDao().getConnectionSource();
			TableUtils.clearTable(connectionSource, Case.class);
			TableUtils.clearTable(connectionSource, Person.class);
			TableUtils.clearTable(connectionSource, Symptoms.class);
			TableUtils.clearTable(connectionSource, Task.class);
			TableUtils.clearTable(connectionSource, Contact.class);
			TableUtils.clearTable(connectionSource, Visit.class);
			TableUtils.clearTable(connectionSource, Event.class);
			TableUtils.clearTable(connectionSource, Sample.class);
			TableUtils.clearTable(connectionSource, SampleTest.class);
			TableUtils.clearTable(connectionSource, EventParticipant.class);
			TableUtils.clearTable(connectionSource, Hospitalization.class);
			TableUtils.clearTable(connectionSource, PreviousHospitalization.class);
			TableUtils.clearTable(connectionSource, EpiData.class);
			TableUtils.clearTable(connectionSource, EpiDataBurial.class);
			TableUtils.clearTable(connectionSource, EpiDataGathering.class);
			TableUtils.clearTable(connectionSource, EpiDataTravel.class);
			TableUtils.clearTable(connectionSource, WeeklyReport.class);
			TableUtils.clearTable(connectionSource, WeeklyReportEntry.class);
			TableUtils.clearTable(connectionSource, Location.class);
			TableUtils.clearTable(connectionSource, Outbreak.class);
			TableUtils.clearTable(connectionSource, SyncLog.class);
			TableUtils.clearTable(connectionSource, DiseaseClassificationCriteria.class);

			if (clearInfrastructure) {
				TableUtils.clearTable(connectionSource, User.class);
				TableUtils.clearTable(connectionSource, UserRoleConfig.class);
				TableUtils.clearTable(connectionSource, Facility.class);
				TableUtils.clearTable(connectionSource, Community.class);
				TableUtils.clearTable(connectionSource, District.class);
				TableUtils.clearTable(connectionSource, Region.class);

				ConfigProvider.init(instance.context);
			}

			// keep config!
			//TableUtils.clearTable(connectionSource, Config.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't clear database", e);
			throw new RuntimeException(e);
		} finally {
			instance.clearingTables = false;
		}
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to build
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, Config.class);
			TableUtils.createTable(connectionSource, Location.class);
			TableUtils.createTable(connectionSource, Region.class);
			TableUtils.createTable(connectionSource, District.class);
			TableUtils.createTable(connectionSource, Community.class);
			TableUtils.createTable(connectionSource, Facility.class);
			TableUtils.createTable(connectionSource, UserRoleConfig.class);
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, Person.class);
			TableUtils.createTable(connectionSource, Case.class);
			TableUtils.createTable(connectionSource, Symptoms.class);
			TableUtils.createTable(connectionSource, Contact.class);
			TableUtils.createTable(connectionSource, Visit.class);
			TableUtils.createTable(connectionSource, Task.class);
			TableUtils.createTable(connectionSource, Event.class);
			TableUtils.createTable(connectionSource, Sample.class);
			TableUtils.createTable(connectionSource, SampleTest.class);
			TableUtils.createTable(connectionSource, EventParticipant.class);
			TableUtils.createTable(connectionSource, Hospitalization.class);
			TableUtils.createTable(connectionSource, PreviousHospitalization.class);
			TableUtils.createTable(connectionSource, EpiData.class);
			TableUtils.createTable(connectionSource, EpiDataBurial.class);
			TableUtils.createTable(connectionSource, EpiDataGathering.class);
			TableUtils.createTable(connectionSource, EpiDataTravel.class);
			TableUtils.createTable(connectionSource, SyncLog.class);
			TableUtils.createTable(connectionSource, WeeklyReport.class);
			TableUtils.createTable(connectionSource, WeeklyReportEntry.class);
			TableUtils.createTable(connectionSource, Outbreak.class);
			TableUtils.createTable(connectionSource, DiseaseClassificationCriteria.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't build database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {

		if (oldVersion < 91) {
			upgradeFromUnupgradableVersion(db, connectionSource, oldVersion);
			return;
		}

		// see http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Upgrading-Schema
		// IMPORTANT: table and column names are CASE SENSITIVE!
		int currentVersion = oldVersion;
		try {
			switch (oldVersion) {
				case 91:
					currentVersion = 91;
					getDao(District.class).executeRaw("ALTER TABLE district ADD COLUMN epidCode varchar(255);");
				case 92:
					currentVersion = 92;
					getDao(District.class).executeRaw("ALTER TABLE region ADD COLUMN epidCode varchar(255);");
				case 93:
					currentVersion = 93;
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN symptomatic boolean;");
				case 94:
					currentVersion = 94;
					// nothing
				case 95:
					currentVersion = 95;
					getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN referredTo_id bigint REFERENCES samples(id);");
				case 96:
					currentVersion = 96;
					getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN shipped boolean;");
					getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN received boolean;");
					getDao(Sample.class).executeRaw("UPDATE samples SET shipped='true' WHERE shipmentStatus = 'SHIPPED' OR shipmentStatus = 'RECEIVED' OR shipmentStatus = 'REFERRED_OTHER_LAB';");
					getDao(Sample.class).executeRaw("UPDATE samples SET received='true' WHERE shipmentStatus = 'RECEIVED' OR shipmentStatus = 'REFERRED_OTHER_LAB';");
					getDao(Sample.class).executeRaw("UPDATE samples SET shipped='false' WHERE shipmentStatus = 'NOT_SHIPPED';");
					getDao(Sample.class).executeRaw("UPDATE samples SET received='false' WHERE shipmentStatus = 'NOT_SHIPPED' OR shipmentStatus = 'SHIPPED';");
				case 97:
					currentVersion = 97;
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN reportLat float8;");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN reportLon float8;");
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN reportLat float8;");
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN reportLon float8;");
					getDao(Event.class).executeRaw("ALTER TABLE events ADD COLUMN reportLat float8;");
					getDao(Event.class).executeRaw("ALTER TABLE events ADD COLUMN reportLon float8;");
					getDao(Visit.class).executeRaw("ALTER TABLE visits ADD COLUMN reportLat float8;");
					getDao(Visit.class).executeRaw("ALTER TABLE visits ADD COLUMN reportLon float8;");
					getDao(Task.class).executeRaw("ALTER TABLE tasks ADD COLUMN closedLat float8;");
					getDao(Task.class).executeRaw("ALTER TABLE tasks ADD COLUMN closedLon float8;");
				case 98:
					currentVersion = 98;
					// nothing
				case 99:
					currentVersion = 99;
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN followUpComment varchar(512);");
				case 100:
					currentVersion = 100;
					// nothing
				case 101:
					currentVersion = 101;
					getDao(Facility.class).executeRaw("UPDATE facility SET name = 'Other health facility' WHERE uuid = 'SORMAS-CONSTID-OTHERS-FACILITY'");
					getDao(Facility.class).executeRaw("UPDATE facility SET name = 'Not a health facility' WHERE uuid = 'SORMAS-CONSTID-ISNONE-FACILITY'");
				case 102:
					currentVersion = 102;
					getDao(PreviousHospitalization.class).executeRaw("ALTER TABLE previoushospitalizations ADD COLUMN region_id bigint REFERENCES region (id);");
					getDao(PreviousHospitalization.class).executeRaw("ALTER TABLE previoushospitalizations ADD COLUMN district_id bigint REFERENCES district (id);");
					getDao(PreviousHospitalization.class).executeRaw("ALTER TABLE previoushospitalizations ADD COLUMN community_id bigint REFERENCES community (id);");
					getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET region_id = (SELECT region_id FROM facility WHERE facility.id = previoushospitalizations.healthfacility_id);");
					getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET district_id = (SELECT district_id FROM facility WHERE facility.id = previoushospitalizations.healthfacility_id);");
					getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET community_id = (SELECT community_id FROM facility WHERE facility.id = previoushospitalizations.healthfacility_id);");
					// Set region, district and community to the values of the case for 'Other' and 'None' health facilities
					getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET region_id = (SELECT region_id FROM cases WHERE cases.hospitalization_id = previoushospitalizations.hospitalization_id) WHERE region_id IS NULL;");
					getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET district_id = (SELECT district_id FROM cases WHERE cases.hospitalization_id = previoushospitalizations.hospitalization_id) WHERE district_id IS NULL;");
					getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET community_id = (SELECT community_id FROM cases WHERE cases.hospitalization_id = previoushospitalizations.hospitalization_id) WHERE community_id IS NULL;");
				case 103:
					currentVersion = 103;
					getDao(Facility.class).executeRaw("UPDATE facility SET name = 'OTHER_FACILITY' WHERE uuid = 'SORMAS-CONSTID-OTHERS-FACILITY'");
					getDao(Facility.class).executeRaw("UPDATE facility SET name = 'NO_FACILITY' WHERE uuid = 'SORMAS-CONSTID-ISNONE-FACILITY'");
				case 104:
					currentVersion = 104;
					getDao(WeeklyReport.class).executeRaw("CREATE TABLE weeklyreport(" +
							"id integer primary key autoincrement," +
							"uuid varchar(36) not null unique," +
							"changeDate timestamp not null," +
							"creationDate timestamp not null," +
							"lastOpenedDate timestamp," +
							"localChangeDate timestamp not null," +
							"modified integer," +
							"snapshot integer," +
							"healthFacility_id bigint not null REFERENCES facility(id)," +
							"informant_id bigint not null REFERENCES users(id)," +
							"reportDateTime timestamp not null," +
							"totalNumberOfCases integer not null" +
							");");
					getDao(WeeklyReportEntry.class).executeRaw("CREATE TABLE weeklyreportentry(" +
							"id integer primary key autoincrement," +
							"uuid varchar(36) not null unique," +
							"changeDate timestamp not null," +
							"creationDate timestamp not null," +
							"lastOpenedDate timestamp," +
							"localChangeDate timestamp not null," +
							"modified integer," +
							"snapshot integer," +
							"weeklyReport_id bigint not null REFERENCES weeklyreport(id)," +
							"disease character varying(255) not null," +
							"numberOfCases integer not null" +
							");");
				case 105:
					currentVersion = 105;
					getDao(WeeklyReport.class).executeRaw("ALTER TABLE weeklyreport ADD COLUMN year integer NOT NULL DEFAULT 1900;");
					getDao(WeeklyReport.class).executeRaw("ALTER TABLE weeklyreport ADD COLUMN epiWeek integer NOT NULL DEFAULT l;");
				case 106:
					currentVersion = 106;
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN yellowFeverVaccination varchar(255);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN yellowFeverVaccinationInfoSource varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN backache varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN eyesBleeding varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN jaundice varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN darkUrine varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN stomachBleeding varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN rapidBreathing varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN swollenGlands varchar(255);");
				case 107:
					currentVersion = 107;
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN diseaseDetails varchar(512);");
					getDao(Event.class).executeRaw("ALTER TABLE events ADD COLUMN diseaseDetails varchar(512);");
				case 108:
					currentVersion = 108;
					getDao(Location.class).executeRaw("ALTER TABLE location ADD COLUMN latLonAccuracy real;");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN reportLatLonAccuracy real;");
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN reportLatLonAccuracy real;");
					getDao(Event.class).executeRaw("ALTER TABLE events ADD COLUMN reportLatLonAccuracy real;");
					getDao(Visit.class).executeRaw("ALTER TABLE visits ADD COLUMN reportLatLonAccuracy real;");
					getDao(Task.class).executeRaw("ALTER TABLE tasks ADD COLUMN closedLatLonAccuracy real;");
					getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN reportLat double precision;");
					getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN reportLon double precision;");
					getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN reportLatLonAccuracy real;");
				case 109:
					currentVersion = 109;
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN smallpoxVaccinationScar varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN cutaneousEruption varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesions varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsSameState varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsSameSize varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsDeepProfound varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsFace boolean;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsLegs boolean;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsSolesFeet boolean;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsPalmsHands boolean;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsThorax boolean;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsArms boolean;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsGenitals boolean;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsAllOverBody boolean;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsResembleImg1 varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsResembleImg2 varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsResembleImg3 varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsResembleImg4 varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lymphadenopathyInguinal varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lymphadenopathyAxillary varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lymphadenopathyCervical varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN chillsSweats varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsThatItch varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN bedridden varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN oralUlcers varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN patientIllLocation varchar(512);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN dateOfLastExposure timestamp;");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN placeOfLastExposure varchar(512);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN animalCondition varchar(255);");
				case 110:
					currentVersion = 110;
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN painfulLymphadenitis varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN buboesGroinArmpitNeck varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN blackeningDeathOfTissue varchar(255);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN plagueType varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN fleaBite varchar(255);");
				case 111:
					currentVersion = 111;
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN smallpoxVaccinationReceived varchar(255);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN smallpoxVaccinationDate timestamp;");
				case 112:
					currentVersion = 112;
					getDao(Event.class).executeRaw("ALTER TABLE events RENAME TO tmp_events;");
					getDao(Event.class).executeRaw("CREATE TABLE events (disease VARCHAR, diseaseDetails VARCHAR, eventDate BIGINT, eventDesc VARCHAR, eventLocation_id BIGINT, eventStatus VARCHAR, eventType VARCHAR, reportDateTime BIGINT, reportLat DOUBLE PRECISION, reportLatLonAccuracy FLOAT, reportLon DOUBLE PRECISION, reportingUser_id BIGINT, srcEmail VARCHAR, srcFirstName VARCHAR, srcLastName VARCHAR, srcTelNo VARCHAR, surveillanceOfficer_id BIGINT, typeOfPlace VARCHAR, typeOfPlaceText VARCHAR, changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT, lastOpenedDate BIGINT, localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, UNIQUE(snapshot, uuid));");
					getDao(Event.class).executeRaw("INSERT INTO events(disease, diseaseDetails, eventDate, eventDesc, eventLocation_id, eventStatus, eventType, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, srcEmail, srcFirstName, srcLastName, srcTelNo, surveillanceOfficer_id, typeOfPlace, typeOfPlaceText, changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid) " +
							"SELECT disease, diseaseDetails, eventDate, eventDesc, eventLocation_id, eventStatus, eventType, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, srcEmail, srcFirstName, srcLastName, srcTelNo, surveillanceOfficer_id, typeOfPlace, typeOfPlaceText, changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid " +
							"FROM tmp_events;");
					getDao(Event.class).executeRaw("DROP TABLE tmp_events;");
				case 113:
					currentVersion = 113;
					getDao(User.class).executeRaw("UPDATE users SET userRole = '[\"' || userRole || '\"]';");
				case 114:
					// Re-creation of the events table has to be done again because of fractions of the not-null constraints not being removed properly
					currentVersion = 114;
					getDao(Event.class).executeRaw("ALTER TABLE events RENAME TO tmp_events;");
					getDao(Event.class).executeRaw("CREATE TABLE events (disease VARCHAR, diseaseDetails VARCHAR, eventDate BIGINT, eventDesc VARCHAR, eventLocation_id BIGINT, eventStatus VARCHAR, eventType VARCHAR, reportDateTime BIGINT, reportLat DOUBLE PRECISION, reportLatLonAccuracy FLOAT, reportLon DOUBLE PRECISION, reportingUser_id BIGINT, srcEmail VARCHAR, srcFirstName VARCHAR, srcLastName VARCHAR, srcTelNo VARCHAR, surveillanceOfficer_id BIGINT, typeOfPlace VARCHAR, typeOfPlaceText VARCHAR, changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT, lastOpenedDate BIGINT, localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, UNIQUE(snapshot, uuid));");
					getDao(Event.class).executeRaw("INSERT INTO events(disease, diseaseDetails, eventDate, eventDesc, eventLocation_id, eventStatus, eventType, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, srcEmail, srcFirstName, srcLastName, srcTelNo, surveillanceOfficer_id, typeOfPlace, typeOfPlaceText, changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid) " +
							"SELECT disease, diseaseDetails, eventDate, eventDesc, eventLocation_id, eventStatus, eventType, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, srcEmail, srcFirstName, srcLastName, srcTelNo, surveillanceOfficer_id, typeOfPlace, typeOfPlaceText, changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid " +
							"FROM tmp_events;");
					getDao(Event.class).executeRaw("DROP TABLE tmp_events;");
				case 115:
					currentVersion = 115;
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN causeOfDeath varchar(255);");
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN causeOfDeathDetails varchar(512);");
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN causeOfDeathDisease varchar(255);");
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN causeOfDeathDiseaseDetails varchar(512);");
				case 116:
					currentVersion = 116;
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN outcome varchar(255);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN outcomeDate timestamp without time zone;");
				case 117:
					currentVersion = 117;
					getDao(Hospitalization.class).executeRaw("ALTER TABLE hospitalizations ADD COLUMN admittedToHealthFacility varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN bulgingFontanelle varchar(255);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN vaccination varchar(255);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN vaccinationDoses varchar(512);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN vaccinationInfoSource varchar(255);");

					getDao(Case.class).executeRaw("UPDATE cases SET vaccination = measlesVaccination WHERE measlesVaccination IS NOT NULL;");
					getDao(Case.class).executeRaw("UPDATE cases SET vaccination = yellowFeverVaccination WHERE yellowFeverVaccination IS NOT NULL;");
					getDao(Case.class).executeRaw("UPDATE cases SET vaccination = measlesDoses WHERE measlesDoses IS NOT NULL;");
					getDao(Case.class).executeRaw("UPDATE cases SET vaccination = measlesVaccinationInfoSource WHERE measlesVaccinationInfoSource IS NOT NULL;");
					getDao(Case.class).executeRaw("UPDATE cases SET vaccination = yellowFeverVaccinationInfoSource WHERE yellowFeverVaccinationInfoSource IS NOT NULL;");
				case 118:
					currentVersion = 118;
					getDao(Outbreak.class).executeRaw("CREATE TABLE outbreak(" +
							"id integer primary key autoincrement," +
							"uuid varchar(36) not null unique," +
							"changeDate timestamp not null," +
							"creationDate timestamp not null," +
							"district_id bigint REFERENCES district(id)," +
							"disease varchar(255)," +
							"reportDate timestamp," +
							"reportingUser_id bigint REFERENCES users(id)," +
							"lastOpenedDate timestamp," +
							"localChangeDate timestamp not null," +
							"modified integer," +
							"snapshot integer);");
				case 119:
					currentVersion = 119;
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN contactStatus varchar(255);");
					getDao(Contact.class).executeRaw("UPDATE contacts SET contactClassification = 'UNCONFIRMED' where contactClassification = 'POSSIBLE';");
					getDao(Contact.class).executeRaw("UPDATE contacts SET contactStatus = 'DROPPED' where contactClassification = 'DROPPED';");
					getDao(Contact.class).executeRaw("UPDATE contacts SET contactStatus = 'DROPPED' where contactClassification = 'NO_CONTACT';");
					getDao(Contact.class).executeRaw("UPDATE contacts SET contactStatus = 'CONVERTED' where contactClassification = 'CONVERTED';");
					getDao(Contact.class).executeRaw("UPDATE contacts SET contactStatus = 'ACTIVE' where contactClassification = 'UNCONFIRMED' or contactClassification = 'CONFIRMED';");
					getDao(Contact.class).executeRaw("UPDATE contacts SET contactClassification = 'CONFIRMED' where contactClassification = 'CONVERTED' or contactClassification = 'DROPPED';");
				case 120:
					currentVersion = 120;
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN vaccinationDate timestamp;");
					getDao(Case.class).executeRaw("UPDATE cases SET vaccinationDate = smallpoxVaccinationDate;");
				case 121:
					currentVersion = 121;
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lesionsOnsetDate timestamp;");
				case 122:
					currentVersion = 122;
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN resultingCaseUuid varchar(36);");
					getDao(EventParticipant.class).executeRaw("ALTER TABLE eventParticipants ADD COLUMN resultingCaseUuid varchar(36);");
				case 123:
					currentVersion = 123;
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN dengueFeverType varchar(255);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN classificationDate timestamp;");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN classificationComment varchar(512);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN classificationUser_id bigint REFERENCES users(id);");
					getDao(Case.class).executeRaw("UPDATE cases SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';");
					getDao(Event.class).executeRaw("UPDATE events SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';");
					getDao(Outbreak.class).executeRaw("UPDATE outbreak SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';");
					getDao(Person.class).executeRaw("UPDATE person SET causeofdeathdisease = 'NEW_INFLUENCA' where causeofdeathdisease = 'AVIAN_INFLUENCA';");
					getDao(Visit.class).executeRaw("UPDATE visits SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';");
					getDao(WeeklyReportEntry.class).executeRaw("UPDATE weeklyreportentry SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';");
				case 124:
					currentVersion = 124;
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN occupationFacilityDetails varchar(512);");
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN occupationRegion_id bigint REFERENCES region(id);");
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN occupationDistrict_id bigint REFERENCES district(id);");
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN occupationCommunity_id bigint REFERENCES community(id);");
					getDao(Person.class).executeRaw("UPDATE person SET occupationRegion_id = (SELECT region_id FROM facility WHERE facility.id = person.occupationFacility_id) WHERE occupationFacility_id IS NOT NULL;");
					getDao(Person.class).executeRaw("UPDATE person SET occupationDistrict_id = (SELECT district_id FROM facility WHERE facility.id = person.occupationFacility_id) WHERE occupationFacility_id IS NOT NULL;");
					getDao(Person.class).executeRaw("UPDATE person SET occupationCommunity_id = (SELECT community_id FROM facility WHERE facility.id = person.occupationFacility_id) WHERE occupationFacility_id IS NOT NULL;");
					getDao(PreviousHospitalization.class).executeRaw("ALTER TABLE previoushospitalizations ADD COLUMN healthFacilityDetails varchar(512);");
				case 125:
					currentVersion = 125;
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN meningealSigns varchar(255);");
				case 126:
					currentVersion = 126;
					getDao(SampleTest.class).executeRaw("UPDATE sampleTests SET testType = 'IGM_SERUM_ANTIBODY' WHERE testType = 'SERUM_ANTIBODY_TITER';");
					getDao(SampleTest.class).executeRaw("UPDATE sampleTests SET testType = 'IGM_SERUM_ANTIBODY' WHERE testType = 'ELISA';");
					getDao(SampleTest.class).executeRaw("UPDATE sampleTests SET testType = 'PCR_RT_PCR' WHERE testType = 'PCR' OR testType = 'RT_PCR';");
					getDao(Sample.class).executeRaw("UPDATE samples SET suggestedTypeOfTest = 'IGM_SERUM_ANTIBODY' WHERE suggestedTypeOfTest = 'SERUM_ANTIBODY_TITER';");
					getDao(Sample.class).executeRaw("UPDATE samples SET suggestedTypeOfTest = 'IGM_SERUM_ANTIBODY' WHERE suggestedTypeOfTest = 'ELISA';");
					getDao(Sample.class).executeRaw("UPDATE samples SET suggestedTypeOfTest = 'PCR_RT_PCR' WHERE suggestedTypeOfTest = 'PCR' OR suggestedTypeOfTest = 'RT_PCR';");
					getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN labDetails varchar(512);");
				case 127:
					currentVersion = 127;
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN directContactConfirmedCase varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN directContactProbableCase varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN closeContactProbableCase varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN areaConfirmedCases varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN processingConfirmedCaseFluidUnsafe varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN percutaneousCaseBlood varchar(255);");
					getDao(EpiData.class).executeRaw("UPDATE epidata SET poultryDetails=null, poultry=null," +
							" wildbirds=null, wildbirdsDetails=null, wildbirdsDate=null, wildbirdsLocation=null;");
				case 128:
					currentVersion = 128;
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN resultingCaseUser_id bigint REFERENCES users(id);");
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN caseUuid VARCHAR;");
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN caseDisease VARCHAR;");
					getDao(Contact.class).executeRaw("UPDATE contacts SET caseUuid = (SELECT uuid FROM cases WHERE cases.id = contacts.caze_id) WHERE caze_id IS NOT NULL;");
					getDao(Contact.class).executeRaw("UPDATE contacts SET caseDisease = (SELECT disease FROM cases WHERE cases.id = contacts.caze_id) WHERE caze_id IS NOT NULL;");
					// Re-creation of the contacts table has to be done to remove not null constraints and the case id column
					getDao(Contact.class).executeRaw("ALTER TABLE contacts RENAME TO tmp_contacts;");
					getDao(Contact.class).executeRaw("CREATE TABLE contacts (caseUuid VARCHAR, caseDisease VARCHAR, contactClassification VARCHAR, " +
							"contactOfficer_id BIGINT REFERENCES users(id), contactProximity VARCHAR, contactStatus VARCHAR, " +
							"description VARCHAR, followUpComment VARCHAR, followUpStatus VARCHAR, followUpUntil BIGINT, " +
							"lastContactDate BIGINT, person_id BIGINT REFERENCES person(id), relationToCase VARCHAR, " +
							"reportDateTime BIGINT, reportLat DOUBLEPRECISION, reportLatLonAccuracy FLOAT, reportLon DOUBLEPRECISION, reportingUser_id BIGINT REFERENCES users(id), " +
							"resultingCaseUuid VARCHAR, resultingCaseUser_id BIGINT REFERENCES users(id)," +
							"changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT, " +
							"lastOpenedDate BIGINT, localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, UNIQUE(snapshot, uuid));");
					getDao(Contact.class).executeRaw("INSERT INTO contacts(caseUuid, caseDisease, contactClassification, " +
							"contactOfficer_id, contactProximity, contactStatus, description, followUpComment, followUpStatus, followUpUntil," +
							"lastContactDate, person_id, relationToCase, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id," +
							"resultingCaseUuid, resultingCaseUser_id," +
							"changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid) " +
							"SELECT caseUuid, caseDisease, contactClassification, " +
							"contactOfficer_id, contactProximity, contactStatus, description, followUpComment, followUpStatus, followUpUntil, " +
							"lastContactDate, person_id, relationToCase, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, " +
							"resultingCaseUuid, resultingCaseUser_id, " +
							"changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid " +
							"FROM tmp_contacts;");
					getDao(Contact.class).executeRaw("DROP TABLE tmp_contacts;");
				case 129:
					currentVersion = 129;
					getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN referredToUuid varchar(36);");
					getDao(Sample.class).executeRaw("UPDATE samples SET referredToUuid = (SELECT uuid FROM samples s2 WHERE s2.id = samples.referredTo_id);");
				case 130:
					currentVersion = 130;
					getDao(DiseaseClassificationCriteria.class).executeRaw("CREATE TABLE diseaseClassificationCriteria (disease VARCHAR, suspectCriteria TEXT, " +
									"probableCriteria TEXT, confirmedCriteria TEXT, changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, " +
									"id INTEGER PRIMARY KEY AUTOINCREMENT, localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, " +
									"UNIQUE(snapshot, uuid));");
				case 131:
					currentVersion = 131;
					getDao(User.class).executeRaw("ALTER TABLE users ADD COLUMN community_id bigint REFERENCES community(id);");
					getDao(User.class).executeRaw("UPDATE users SET userRole = replace(userRole, 'INFORMANT', 'HOSPITAL_INFORMANT');");
				case 132:
					currentVersion = 132;
					getDao(UserRoleConfig.class).executeRaw("CREATE TABLE userrolesconfig(" +
							"id integer primary key autoincrement," +
							"uuid varchar(36)," +
							"changeDate timestamp," +
							"creationDate timestamp," +
							"userRole varchar(255)," +
							"userRights varchar(1023)," +
							"lastOpenedDate timestamp," +
							"localChangeDate timestamp," +
							"modified integer," +
							"snapshot integer);");
				case 133:
					currentVersion = 133;
					getDao(WeeklyReport.class).executeRaw("ALTER TABLE weeklyreport RENAME TO tmp_weeklyreport;");
					getDao(WeeklyReport.class).executeRaw("CREATE TABLE weeklyreport(id integer primary key autoincrement, uuid varchar(36) not null unique, changeDate timestamp not null, " +
							"creationDate timestamp not null, lastOpenedDate timestamp, localChangeDate timestamp not null, modified integer, snapshot integer, healthFacility_id bigint REFERENCES facility(id), " +
							"reportingUser_id bigint REFERENCES users(id), reportDateTime timestamp, totalNumberOfCases integer, epiWeek integer, year integer, district_id bigint REFERENCES district(id), " +
							"community_id bigint REFERENCES community(id), assignedOfficer_id bigint REFERENCES users(id), UNIQUE(snapshot, uuid));");
					getDao(WeeklyReport.class).executeRaw("INSERT INTO weeklyreport(id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, healthFacility_id, " +
							"reportingUser_id, reportDateTime, totalNumberOfCases, epiWeek, year) " +
							"SELECT id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, healthFacility_id, informant_id, reportDateTime, " +
							"totalNumberOfCases, epiWeek, year " +
							"FROM tmp_weeklyreport;");
					getDao(WeeklyReport.class).executeRaw("DROP TABLE tmp_weeklyreport;");
				case 134:
					currentVersion = 134;
					getDao(Outbreak.class).executeRaw("ALTER TABLE outbreak ADD COLUMN startDate timestamp;");
					getDao(Outbreak.class).executeRaw("ALTER TABLE outbreak ADD COLUMN endDate timestamp;");
					getDao(Outbreak.class).executeRaw("UPDATE outbreak SET startDate=creationDate");
				case 135:
					currentVersion = 135;
					getDao(Sample.class).executeRaw("UPDATE samples SET suggestedTypeOfTest='ISOLATION' WHERE suggestedTypeOfTest='VIRUS_ISOLATION'");
					getDao(SampleTest.class).executeRaw("UPDATE sampleTests SET testType='ISOLATION' WHERE testType='VIRUS_ISOLATION'");
				case 136:
					currentVersion = 136;
					try {
						getDao(Outbreak.class).executeRaw("ALTER TABLE diseaseClassificationCriteria ADD COLUMN lastOpenedDate timestamp;");
					} catch(SQLException e) { } // may already exist
					getDao(WeeklyReportEntry.class).executeRaw("ALTER TABLE weeklyreportentry RENAME TO tmp_weeklyreportentry;");
					getDao(WeeklyReportEntry.class).executeRaw("CREATE TABLE weeklyreportentry(" +
							"id integer primary key autoincrement," +
							"uuid varchar(36) not null unique," +
							"changeDate timestamp not null," +
							"creationDate timestamp not null," +
							"lastOpenedDate timestamp," +
							"localChangeDate timestamp not null," +
							"modified integer," +
							"snapshot integer," +
							"weeklyReport_id bigint REFERENCES weeklyreport(id)," +
							"disease character varying(255)," +
							"numberOfCases integer" +
							");");
					getDao(WeeklyReportEntry.class).executeRaw("INSERT INTO weeklyreportentry(id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, weeklyReport_id, " +
							"disease, numberOfCases) " +
							"SELECT id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, weeklyReport_id, disease, numberOfCases " +
							"FROM tmp_weeklyreportentry;");
					getDao(WeeklyReportEntry.class).executeRaw("DROP TABLE tmp_weeklyreportentry;");
				case 137:
					currentVersion = 137;
					try {
						getDao(DiseaseClassificationCriteria.class).executeRaw("DROP TABLE diseaseClassificationCriteria");
						getDao(DiseaseClassificationCriteria.class).executeRaw("DROP TABLE diseaseClassification");
					} catch (SQLException e) { } // one of the tables won't exist
					getDao(DiseaseClassificationCriteria.class).executeRaw("CREATE TABLE diseaseClassificationCriteria (disease VARCHAR, suspectCriteria TEXT, " +
							"probableCriteria TEXT, confirmedCriteria TEXT, changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, " +
							"id INTEGER PRIMARY KEY AUTOINCREMENT, localChangeDate BIGINT NOT NULL, modified SMALLINT, lastOpenedDate timestamp, snapshot SMALLINT, uuid VARCHAR NOT NULL, " +
							"UNIQUE(snapshot, uuid));");
				case 138:
					currentVersion = 138;
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN sequelae varchar(255);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN sequelaeDetails varchar(255);");
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN educationType varchar(255);");
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN educationDetails varchar(255);");
					getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN approximateAgeReferenceDate varchar(255);");
					getDao(Person.class).executeRaw("UPDATE person SET approximateAgeReferenceDate=changeDate WHERE person.approximateAge IS NOT NULL;");
					getDao(Hospitalization.class).executeRaw("ALTER TABLE hospitalizations ADD COLUMN accommodation varchar(255);");
					getDao(Hospitalization.class).executeRaw("ALTER TABLE hospitalizations ADD COLUMN leftAgainstAdvice varchar(255);");
				case 139:
					currentVersion = 139;
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN bloodPressureSystolic integer;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN bloodPressureDiastolic integer;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN heartRate integer;");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN pharyngealErythema varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN pharyngealExudate varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN oedemaFaceNeck varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN oedemaLowerExtremity varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lossSkinTurgor varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN palpableLiver varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN palpableSpleen varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN malaise varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN sunkenEyesFontanelle varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN sidePain varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN fluidInLungCavity varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN tremor varchar(255);");

					// ATTENTION: break should only be done after last version
					break;
				default:
					throw new IllegalStateException(
							"onUpgrade() with unknown oldVersion " + oldVersion);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Database upgrade failed for version " + currentVersion + ": " + ex.getMessage(), ex);
		}
	}

	private void upgradeFromUnupgradableVersion(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Case.class, true);
			TableUtils.dropTable(connectionSource, Person.class, true);
			TableUtils.dropTable(connectionSource, Location.class, true);
			TableUtils.dropTable(connectionSource, Region.class, true);
			TableUtils.dropTable(connectionSource, District.class, true);
			TableUtils.dropTable(connectionSource, Community.class, true);
			TableUtils.dropTable(connectionSource, Facility.class, true);
			TableUtils.dropTable(connectionSource, User.class, true);
			TableUtils.dropTable(connectionSource, Symptoms.class, true);
			TableUtils.dropTable(connectionSource, Task.class, true);
			TableUtils.dropTable(connectionSource, Contact.class, true);
			TableUtils.dropTable(connectionSource, Visit.class, true);
			TableUtils.dropTable(connectionSource, Event.class, true);
			TableUtils.dropTable(connectionSource, Sample.class, true);
			TableUtils.dropTable(connectionSource, SampleTest.class, true);
			TableUtils.dropTable(connectionSource, EventParticipant.class, true);
			TableUtils.dropTable(connectionSource, Hospitalization.class, true);
			TableUtils.dropTable(connectionSource, PreviousHospitalization.class, true);
			TableUtils.dropTable(connectionSource, EpiData.class, true);
			TableUtils.dropTable(connectionSource, EpiDataBurial.class, true);
			TableUtils.dropTable(connectionSource, EpiDataGathering.class, true);
			TableUtils.dropTable(connectionSource, EpiDataTravel.class, true);
			TableUtils.dropTable(connectionSource, SyncLog.class, true);
			TableUtils.dropTable(connectionSource, WeeklyReport.class, true);
			TableUtils.dropTable(connectionSource, WeeklyReportEntry.class, true);
			TableUtils.dropTable(connectionSource, Outbreak.class, true);
			TableUtils.dropTable(connectionSource, DiseaseClassificationCriteria.class, true);

			if (oldVersion < 30) {
				TableUtils.dropTable(connectionSource, Config.class, true);
			}
			// after we drop the old databases, we build the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public <ADO extends AbstractDomainObject> AbstractAdoDao<ADO> getAdoDaoInner(Class<ADO> type) {

		if (!adoDaos.containsKey(type)) {

			// build dao
			AbstractAdoDao<ADO> dao;
			Dao<ADO, Long> innerDao;

			try {
				innerDao = super.getDao(type);

				if (type.equals(Case.class)) {
					dao = (AbstractAdoDao<ADO>) new CaseDao((Dao<Case, Long>) innerDao);
				} else if (type.equals(Person.class)) {
					dao = (AbstractAdoDao<ADO>) new PersonDao((Dao<Person, Long>) innerDao);
				} else if (type.equals(Location.class)) {
					dao = (AbstractAdoDao<ADO>) new LocationDao((Dao<Location, Long>) innerDao);
				} else if (type.equals(Facility.class)) {
					dao = (AbstractAdoDao<ADO>) new FacilityDao((Dao<Facility, Long>) innerDao);
				} else if (type.equals(Region.class)) {
					dao = (AbstractAdoDao<ADO>) new RegionDao((Dao<Region, Long>) innerDao);
				} else if (type.equals(District.class)) {
					dao = (AbstractAdoDao<ADO>) new DistrictDao((Dao<District, Long>) innerDao);
				} else if (type.equals(Community.class)) {
					dao = (AbstractAdoDao<ADO>) new CommunityDao((Dao<Community, Long>) innerDao);
				} else if (type.equals(User.class)) {
					dao = (AbstractAdoDao<ADO>) new UserDao((Dao<User, Long>) innerDao);
				} else if (type.equals(UserRoleConfig.class)) {
					dao = (AbstractAdoDao<ADO>) new UserRoleConfigDao((Dao<UserRoleConfig, Long>) innerDao);
				} else if (type.equals(Symptoms.class)) {
					dao = (AbstractAdoDao<ADO>) new SymptomsDao((Dao<Symptoms, Long>) innerDao);
				} else if (type.equals(Task.class)) {
					dao = (AbstractAdoDao<ADO>) new TaskDao((Dao<Task, Long>) innerDao);
				} else if (type.equals(Contact.class)) {
					dao = (AbstractAdoDao<ADO>) new ContactDao((Dao<Contact, Long>) innerDao);
				} else if (type.equals(Visit.class)) {
					dao = (AbstractAdoDao<ADO>) new VisitDao((Dao<Visit, Long>) innerDao);
				} else if (type.equals(Event.class)) {
					dao = (AbstractAdoDao<ADO>) new EventDao((Dao<Event, Long>) innerDao);
				} else if (type.equals(EventParticipant.class)) {
					dao = (AbstractAdoDao<ADO>) new EventParticipantDao((Dao<EventParticipant, Long>) innerDao);
				} else if (type.equals(Sample.class)) {
					dao = (AbstractAdoDao<ADO>) new SampleDao((Dao<Sample, Long>) innerDao);
				} else if (type.equals(SampleTest.class)) {
					dao = (AbstractAdoDao<ADO>) new SampleTestDao((Dao<SampleTest, Long>) innerDao);
				} else if (type.equals(Hospitalization.class)) {
					dao = (AbstractAdoDao<ADO>) new HospitalizationDao((Dao<Hospitalization, Long>) innerDao);
				} else if (type.equals(PreviousHospitalization.class)) {
					dao = (AbstractAdoDao<ADO>) new PreviousHospitalizationDao((Dao<PreviousHospitalization, Long>) innerDao);
				} else if (type.equals(EpiData.class)) {
					dao = (AbstractAdoDao<ADO>) new EpiDataDao((Dao<EpiData, Long>) innerDao);
				} else if (type.equals(EpiDataGathering.class)) {
					dao = (AbstractAdoDao<ADO>) new EpiDataGatheringDao((Dao<EpiDataGathering, Long>) innerDao);
				} else if (type.equals(EpiDataBurial.class)) {
					dao = (AbstractAdoDao<ADO>) new EpiDataBurialDao((Dao<EpiDataBurial, Long>) innerDao);
				} else if (type.equals(EpiDataTravel.class)) {
					dao = (AbstractAdoDao<ADO>) new EpiDataTravelDao((Dao<EpiDataTravel, Long>) innerDao);
				} else if (type.equals(WeeklyReport.class)) {
					dao = (AbstractAdoDao<ADO>) new WeeklyReportDao((Dao<WeeklyReport, Long>) innerDao);
				} else if (type.equals(WeeklyReportEntry.class)) {
					dao = (AbstractAdoDao<ADO>) new WeeklyReportEntryDao((Dao<WeeklyReportEntry, Long>) innerDao);
				} else if (type.equals(Outbreak.class)) {
					dao = (AbstractAdoDao<ADO>) new OutbreakDao((Dao<Outbreak, Long>) innerDao);
				} else if (type.equals(DiseaseClassificationCriteria.class)) {
					dao = (AbstractAdoDao<ADO>) new DiseaseClassificationCriteriaDao((Dao<DiseaseClassificationCriteria, Long>) innerDao);
				} else {
					throw new UnsupportedOperationException(type.toString());
				}

				adoDaos.put(type, dao);

			} catch (SQLException e) {
				Log.e(DatabaseHelper.class.getName(), "Can't build dao", e);
				throw new RuntimeException(e);
			}
		}

		return (AbstractAdoDao<ADO>) adoDaos.get(type);
	}

	public static <ADO extends AbstractDomainObject> AbstractAdoDao<ADO> getAdoDao(Class<ADO> type) {

		if (!instance.adoDaos.containsKey(type)) {
			synchronized (DatabaseHelper.class) {
				return instance.getAdoDaoInner(type);
			}
		}
		return (AbstractAdoDao<ADO>) instance.adoDaos.get(type);
	}

	public static ConfigDao getConfigDao() {
		if (instance.configDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.configDao == null) {
					try {
						instance.configDao = new ConfigDao((Dao<Config, String>) instance.getDao(Config.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't build ConfigDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.configDao;
	}

	public static SyncLogDao getSyncLogDao() {
		if (instance.syncLogDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.syncLogDao == null) {
					try {
						instance.syncLogDao = new SyncLogDao((Dao<SyncLog, Long>) instance.getDao(SyncLog.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't build SyncLogDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.syncLogDao;
	}

	public static CaseDao getCaseDao() {
		return (CaseDao) getAdoDao(Case.class);
	}

	public static PersonDao getPersonDao() {
		return (PersonDao) getAdoDao(Person.class);
	}

	public static LocationDao getLocationDao() {
		return (LocationDao) getAdoDao(Location.class);
	}

	public static FacilityDao getFacilityDao() {
		return (FacilityDao) getAdoDao(Facility.class);
	}

	public static RegionDao getRegionDao() {
		return (RegionDao) getAdoDao(Region.class);
	}

	public static DistrictDao getDistrictDao() {
		return (DistrictDao) getAdoDao(District.class);
	}

	public static CommunityDao getCommunityDao() {
		return (CommunityDao) getAdoDao(Community.class);
	}

	public static UserDao getUserDao() {
		return (UserDao) getAdoDao(User.class);
	}

	public static UserRoleConfigDao getUserRoleConfigDao() {
		return (UserRoleConfigDao) getAdoDao(UserRoleConfig.class);
	}

	public static SymptomsDao getSymptomsDao() {
		return (SymptomsDao) getAdoDao(Symptoms.class);
	}

	public static TaskDao getTaskDao() {
		return (TaskDao) getAdoDao(Task.class);
	}

	public static ContactDao getContactDao() {
		return (ContactDao) getAdoDao(Contact.class);
	}

	public static VisitDao getVisitDao() {
		return (VisitDao) getAdoDao(Visit.class);
	}

	public static EventDao getEventDao() {
		return (EventDao) getAdoDao(Event.class);
	}

	public static EventParticipantDao getEventParticipantDao() {
		return (EventParticipantDao) getAdoDao(EventParticipant.class);
	}

	public static SampleDao getSampleDao() {
		return (SampleDao) getAdoDao(Sample.class);
	}

	public static SampleTestDao getSampleTestDao() {
		return (SampleTestDao) getAdoDao(SampleTest.class);
	}

	public static HospitalizationDao getHospitalizationDao() {
		return (HospitalizationDao) getAdoDao(Hospitalization.class);
	}

	public static PreviousHospitalizationDao getPreviousHospitalizationDao() {
		return (PreviousHospitalizationDao) getAdoDao(PreviousHospitalization.class);
	}

	public static EpiDataDao getEpiDataDao() {
		return (EpiDataDao) getAdoDao(EpiData.class);
	}

	public static EpiDataBurialDao getEpiDataBurialDao() {
		return (EpiDataBurialDao) getAdoDao(EpiDataBurial.class);
	}

	public static EpiDataGatheringDao getEpiDataGatheringDao() {
		return (EpiDataGatheringDao) getAdoDao(EpiDataGathering.class);
	}

	public static EpiDataTravelDao getEpiDataTravelDao() {
		return (EpiDataTravelDao) getAdoDao(EpiDataTravel.class);
	}

	public static WeeklyReportDao getWeeklyReportDao() {
		return (WeeklyReportDao) getAdoDao(WeeklyReport.class);
	}

	public static WeeklyReportEntryDao getWeeklyReportEntryDao() {
		return (WeeklyReportEntryDao) getAdoDao(WeeklyReportEntry.class);
	}

	public static OutbreakDao getOutbreakDao() {
		return (OutbreakDao) getAdoDao(Outbreak.class);
	}

	public static DiseaseClassificationCriteriaDao getDiseaseClassificationCriteriaDao() {
		return (DiseaseClassificationCriteriaDao) getAdoDao(DiseaseClassificationCriteria.class);
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		configDao = null;
		adoDaos.clear();
		syncLogDao = null;
	}

	public static Context getContext() {
		return instance.context;
	}

	public static String getString(int stringResourceId) {
		if (instance.context == null) {
			return null;
		}

		return instance.context.getResources().getString(stringResourceId);
	}

}

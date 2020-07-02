/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.common;

import java.sql.Array;
import java.sql.SQLException;
import java.util.HashMap;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.caze.maternalhistory.MaternalHistory;
import de.symeda.sormas.app.backend.caze.maternalhistory.MaternalHistoryDao;
import de.symeda.sormas.app.backend.caze.porthealthinfo.PortHealthInfo;
import de.symeda.sormas.app.backend.caze.porthealthinfo.PortHealthInfoDao;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationCriteria;
import de.symeda.sormas.app.backend.classification.DiseaseClassificationCriteriaDao;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalCourse;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalCourseDao;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisitDao;
import de.symeda.sormas.app.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.app.backend.clinicalcourse.HealthConditionsDao;
import de.symeda.sormas.app.backend.config.Config;
import de.symeda.sormas.app.backend.config.ConfigDao;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.app.backend.disease.DiseaseConfigurationDao;
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
import de.symeda.sormas.app.backend.feature.FeatureConfiguration;
import de.symeda.sormas.app.backend.feature.FeatureConfigurationDao;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.HospitalizationDao;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalizationDao;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntryDao;
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
import de.symeda.sormas.app.backend.report.AggregateReport;
import de.symeda.sormas.app.backend.report.AggregateReportDao;
import de.symeda.sormas.app.backend.report.WeeklyReport;
import de.symeda.sormas.app.backend.report.WeeklyReportDao;
import de.symeda.sormas.app.backend.report.WeeklyReportEntry;
import de.symeda.sormas.app.backend.report.WeeklyReportEntryDao;
import de.symeda.sormas.app.backend.sample.AdditionalTest;
import de.symeda.sormas.app.backend.sample.AdditionalTestDao;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.PathogenTestDao;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDao;
import de.symeda.sormas.app.backend.synclog.SyncLog;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.backend.therapy.Prescription;
import de.symeda.sormas.app.backend.therapy.PrescriptionDao;
import de.symeda.sormas.app.backend.therapy.Therapy;
import de.symeda.sormas.app.backend.therapy.TherapyDao;
import de.symeda.sormas.app.backend.therapy.Treatment;
import de.symeda.sormas.app.backend.therapy.TreatmentDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDao;
import de.symeda.sormas.app.backend.user.UserRoleConfig;
import de.symeda.sormas.app.backend.user.UserRoleConfigDao;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.backend.visit.VisitDao;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 * 
 * @see <a href="http://stackoverflow.com/questions/17529766/view-contents-of-database-file-in-android-studio">Viewing databases from
 *      Android Studio</a>
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application. Stored in data/data/de.symeda.sormas.app/databases
	public static final String DATABASE_NAME = "sormas.db";
	// any time you make changes to your database objects, you may have to increase the database version
	public static final int DATABASE_VERSION = 209;

	private static DatabaseHelper instance = null;

	public static void init(Context context) {
		if (instance != null) {
			Log.e(DatabaseHelper.class.getName(), "DatabaseHelper has already been initalized");
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
			TableUtils.clearTable(connectionSource, Treatment.class);
			TableUtils.clearTable(connectionSource, Prescription.class);
			TableUtils.clearTable(connectionSource, Therapy.class);
			TableUtils.clearTable(connectionSource, ClinicalVisit.class);
			TableUtils.clearTable(connectionSource, ClinicalCourse.class);
			TableUtils.clearTable(connectionSource, HealthConditions.class);
			TableUtils.clearTable(connectionSource, MaternalHistory.class);
			TableUtils.clearTable(connectionSource, PortHealthInfo.class);
			TableUtils.clearTable(connectionSource, Person.class);
			TableUtils.clearTable(connectionSource, Symptoms.class);
			TableUtils.clearTable(connectionSource, Task.class);
			TableUtils.clearTable(connectionSource, Contact.class);
			TableUtils.clearTable(connectionSource, Visit.class);
			TableUtils.clearTable(connectionSource, Event.class);
			TableUtils.clearTable(connectionSource, Sample.class);
			TableUtils.clearTable(connectionSource, PathogenTest.class);
			TableUtils.clearTable(connectionSource, AdditionalTest.class);
			TableUtils.clearTable(connectionSource, EventParticipant.class);
			TableUtils.clearTable(connectionSource, Hospitalization.class);
			TableUtils.clearTable(connectionSource, PreviousHospitalization.class);
			TableUtils.clearTable(connectionSource, EpiData.class);
			TableUtils.clearTable(connectionSource, EpiDataBurial.class);
			TableUtils.clearTable(connectionSource, EpiDataGathering.class);
			TableUtils.clearTable(connectionSource, EpiDataTravel.class);
			TableUtils.clearTable(connectionSource, WeeklyReport.class);
			TableUtils.clearTable(connectionSource, WeeklyReportEntry.class);
			TableUtils.clearTable(connectionSource, AggregateReport.class);
			TableUtils.clearTable(connectionSource, Location.class);
			TableUtils.clearTable(connectionSource, Outbreak.class);
			TableUtils.clearTable(connectionSource, SyncLog.class);
			TableUtils.clearTable(connectionSource, DiseaseClassificationCriteria.class);

			if (clearInfrastructure) {
				TableUtils.clearTable(connectionSource, User.class);
				TableUtils.clearTable(connectionSource, UserRoleConfig.class);
				TableUtils.clearTable(connectionSource, DiseaseConfiguration.class);
				TableUtils.clearTable(connectionSource, FeatureConfiguration.class);
				TableUtils.clearTable(connectionSource, PointOfEntry.class);
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
			TableUtils.createTable(connectionSource, PointOfEntry.class);
			TableUtils.createTable(connectionSource, UserRoleConfig.class);
			TableUtils.createTable(connectionSource, DiseaseConfiguration.class);
			TableUtils.createTable(connectionSource, FeatureConfiguration.class);
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, Person.class);
			TableUtils.createTable(connectionSource, Case.class);
			TableUtils.createTable(connectionSource, Symptoms.class);
			TableUtils.createTable(connectionSource, Therapy.class);
			TableUtils.createTable(connectionSource, Prescription.class);
			TableUtils.createTable(connectionSource, Treatment.class);
			TableUtils.createTable(connectionSource, HealthConditions.class);
			TableUtils.createTable(connectionSource, ClinicalCourse.class);
			TableUtils.createTable(connectionSource, ClinicalVisit.class);
			TableUtils.createTable(connectionSource, MaternalHistory.class);
			TableUtils.createTable(connectionSource, PortHealthInfo.class);
			TableUtils.createTable(connectionSource, Contact.class);
			TableUtils.createTable(connectionSource, Visit.class);
			TableUtils.createTable(connectionSource, Task.class);
			TableUtils.createTable(connectionSource, Event.class);
			TableUtils.createTable(connectionSource, Sample.class);
			TableUtils.createTable(connectionSource, PathogenTest.class);
			TableUtils.createTable(connectionSource, AdditionalTest.class);
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
			TableUtils.createTable(connectionSource, AggregateReport.class);
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
				getDao(Sample.class).executeRaw(
					"UPDATE samples SET shipped='true' WHERE shipmentStatus = 'SHIPPED' OR shipmentStatus = 'RECEIVED' OR shipmentStatus = 'REFERRED_OTHER_LAB';");
				getDao(Sample.class)
					.executeRaw("UPDATE samples SET received='true' WHERE shipmentStatus = 'RECEIVED' OR shipmentStatus = 'REFERRED_OTHER_LAB';");
				getDao(Sample.class).executeRaw("UPDATE samples SET shipped='false' WHERE shipmentStatus = 'NOT_SHIPPED';");
				getDao(Sample.class)
					.executeRaw("UPDATE samples SET received='false' WHERE shipmentStatus = 'NOT_SHIPPED' OR shipmentStatus = 'SHIPPED';");
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
				getDao(PreviousHospitalization.class)
					.executeRaw("ALTER TABLE previoushospitalizations ADD COLUMN region_id bigint REFERENCES region (id);");
				getDao(PreviousHospitalization.class)
					.executeRaw("ALTER TABLE previoushospitalizations ADD COLUMN district_id bigint REFERENCES district (id);");
				getDao(PreviousHospitalization.class)
					.executeRaw("ALTER TABLE previoushospitalizations ADD COLUMN community_id bigint REFERENCES community (id);");
				getDao(PreviousHospitalization.class).executeRaw(
					"UPDATE previoushospitalizations SET region_id = (SELECT region_id FROM facility WHERE facility.id = previoushospitalizations.healthfacility_id);");
				getDao(PreviousHospitalization.class).executeRaw(
					"UPDATE previoushospitalizations SET district_id = (SELECT district_id FROM facility WHERE facility.id = previoushospitalizations.healthfacility_id);");
				getDao(PreviousHospitalization.class).executeRaw(
					"UPDATE previoushospitalizations SET community_id = (SELECT community_id FROM facility WHERE facility.id = previoushospitalizations.healthfacility_id);");
				// Set region, district and community to the values of the case for 'Other' and 'None' health facilities
				getDao(PreviousHospitalization.class).executeRaw(
					"UPDATE previoushospitalizations SET region_id = (SELECT region_id FROM cases WHERE cases.hospitalization_id = previoushospitalizations.hospitalization_id) WHERE region_id IS NULL;");
				getDao(PreviousHospitalization.class).executeRaw(
					"UPDATE previoushospitalizations SET district_id = (SELECT district_id FROM cases WHERE cases.hospitalization_id = previoushospitalizations.hospitalization_id) WHERE district_id IS NULL;");
				getDao(PreviousHospitalization.class).executeRaw(
					"UPDATE previoushospitalizations SET community_id = (SELECT community_id FROM cases WHERE cases.hospitalization_id = previoushospitalizations.hospitalization_id) WHERE community_id IS NULL;");
			case 103:
				currentVersion = 103;
				getDao(Facility.class).executeRaw("UPDATE facility SET name = 'OTHER_FACILITY' WHERE uuid = 'SORMAS-CONSTID-OTHERS-FACILITY'");
				getDao(Facility.class).executeRaw("UPDATE facility SET name = 'NO_FACILITY' WHERE uuid = 'SORMAS-CONSTID-ISNONE-FACILITY'");
			case 104:
				currentVersion = 104;
				getDao(WeeklyReport.class).executeRaw(
					"CREATE TABLE weeklyreport(" + "id integer primary key autoincrement," + "uuid varchar(36) not null unique,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "healthFacility_id bigint not null REFERENCES facility(id)," + "informant_id bigint not null REFERENCES users(id),"
						+ "reportDateTime timestamp not null," + "totalNumberOfCases integer not null" + ");");
				getDao(WeeklyReportEntry.class).executeRaw(
					"CREATE TABLE weeklyreportentry(" + "id integer primary key autoincrement," + "uuid varchar(36) not null unique,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "weeklyReport_id bigint not null REFERENCES weeklyreport(id)," + "disease character varying(255) not null,"
						+ "numberOfCases integer not null" + ");");
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
				getDao(Event.class).executeRaw(
					"CREATE TABLE events (disease VARCHAR, diseaseDetails VARCHAR, eventDate BIGINT, eventDesc VARCHAR, eventLocation_id BIGINT, eventStatus VARCHAR, eventType VARCHAR, reportDateTime BIGINT, reportLat DOUBLE PRECISION, reportLatLonAccuracy FLOAT, reportLon DOUBLE PRECISION, reportingUser_id BIGINT, srcEmail VARCHAR, srcFirstName VARCHAR, srcLastName VARCHAR, srcTelNo VARCHAR, surveillanceOfficer_id BIGINT, typeOfPlace VARCHAR, typeOfPlaceText VARCHAR, changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT, lastOpenedDate BIGINT, localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, UNIQUE(snapshot, uuid));");
				getDao(Event.class).executeRaw(
					"INSERT INTO events(disease, diseaseDetails, eventDate, eventDesc, eventLocation_id, eventStatus, eventType, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, srcEmail, srcFirstName, srcLastName, srcTelNo, surveillanceOfficer_id, typeOfPlace, typeOfPlaceText, changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid) "
						+ "SELECT disease, diseaseDetails, eventDate, eventDesc, eventLocation_id, eventStatus, eventType, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, srcEmail, srcFirstName, srcLastName, srcTelNo, surveillanceOfficer_id, typeOfPlace, typeOfPlaceText, changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid "
						+ "FROM tmp_events;");
				getDao(Event.class).executeRaw("DROP TABLE tmp_events;");
			case 113:
				currentVersion = 113;
				getDao(User.class).executeRaw("UPDATE users SET userRole = '[\"' || userRole || '\"]';");
			case 114:
				// Re-creation of the events table has to be done again because of fractions of the not-null constraints not being removed properly
				currentVersion = 114;
				getDao(Event.class).executeRaw("ALTER TABLE events RENAME TO tmp_events;");
				getDao(Event.class).executeRaw(
					"CREATE TABLE events (disease VARCHAR, diseaseDetails VARCHAR, eventDate BIGINT, eventDesc VARCHAR, eventLocation_id BIGINT, eventStatus VARCHAR, eventType VARCHAR, reportDateTime BIGINT, reportLat DOUBLE PRECISION, reportLatLonAccuracy FLOAT, reportLon DOUBLE PRECISION, reportingUser_id BIGINT, srcEmail VARCHAR, srcFirstName VARCHAR, srcLastName VARCHAR, srcTelNo VARCHAR, surveillanceOfficer_id BIGINT, typeOfPlace VARCHAR, typeOfPlaceText VARCHAR, changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT, lastOpenedDate BIGINT, localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, UNIQUE(snapshot, uuid));");
				getDao(Event.class).executeRaw(
					"INSERT INTO events(disease, diseaseDetails, eventDate, eventDesc, eventLocation_id, eventStatus, eventType, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, srcEmail, srcFirstName, srcLastName, srcTelNo, surveillanceOfficer_id, typeOfPlace, typeOfPlaceText, changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid) "
						+ "SELECT disease, diseaseDetails, eventDate, eventDesc, eventLocation_id, eventStatus, eventType, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, srcEmail, srcFirstName, srcLastName, srcTelNo, surveillanceOfficer_id, typeOfPlace, typeOfPlaceText, changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid "
						+ "FROM tmp_events;");
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
				getDao(Case.class)
					.executeRaw("UPDATE cases SET vaccination = measlesVaccinationInfoSource WHERE measlesVaccinationInfoSource IS NOT NULL;");
				getDao(Case.class).executeRaw(
					"UPDATE cases SET vaccination = yellowFeverVaccinationInfoSource WHERE yellowFeverVaccinationInfoSource IS NOT NULL;");
			case 118:
				currentVersion = 118;
				getDao(Outbreak.class).executeRaw(
					"CREATE TABLE outbreak(" + "id integer primary key autoincrement," + "uuid varchar(36) not null unique,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "district_id bigint REFERENCES district(id),"
						+ "disease varchar(255)," + "reportDate timestamp," + "reportingUser_id bigint REFERENCES users(id),"
						+ "lastOpenedDate timestamp," + "localChangeDate timestamp not null," + "modified integer," + "snapshot integer);");
			case 119:
				currentVersion = 119;
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN contactStatus varchar(255);");
				getDao(Contact.class)
					.executeRaw("UPDATE contacts SET contactClassification = 'UNCONFIRMED' where contactClassification = 'POSSIBLE';");
				getDao(Contact.class).executeRaw("UPDATE contacts SET contactStatus = 'DROPPED' where contactClassification = 'DROPPED';");
				getDao(Contact.class).executeRaw("UPDATE contacts SET contactStatus = 'DROPPED' where contactClassification = 'NO_CONTACT';");
				getDao(Contact.class).executeRaw("UPDATE contacts SET contactStatus = 'CONVERTED' where contactClassification = 'CONVERTED';");
				getDao(Contact.class).executeRaw(
					"UPDATE contacts SET contactStatus = 'ACTIVE' where contactClassification = 'UNCONFIRMED' or contactClassification = 'CONFIRMED';");
				getDao(Contact.class).executeRaw(
					"UPDATE contacts SET contactClassification = 'CONFIRMED' where contactClassification = 'CONVERTED' or contactClassification = 'DROPPED';");
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
				getDao(Person.class)
					.executeRaw("UPDATE person SET causeofdeathdisease = 'NEW_INFLUENCA' where causeofdeathdisease = 'AVIAN_INFLUENCA';");
				getDao(Visit.class).executeRaw("UPDATE visits SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';");
				getDao(WeeklyReportEntry.class)
					.executeRaw("UPDATE weeklyreportentry SET disease = 'NEW_INFLUENCA' where disease = 'AVIAN_INFLUENCA';");
			case 124:
				currentVersion = 124;
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN occupationFacilityDetails varchar(512);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN occupationRegion_id bigint REFERENCES region(id);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN occupationDistrict_id bigint REFERENCES district(id);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN occupationCommunity_id bigint REFERENCES community(id);");
				getDao(Person.class).executeRaw(
					"UPDATE person SET occupationRegion_id = (SELECT region_id FROM facility WHERE facility.id = person.occupationFacility_id) WHERE occupationFacility_id IS NOT NULL;");
				getDao(Person.class).executeRaw(
					"UPDATE person SET occupationDistrict_id = (SELECT district_id FROM facility WHERE facility.id = person.occupationFacility_id) WHERE occupationFacility_id IS NOT NULL;");
				getDao(Person.class).executeRaw(
					"UPDATE person SET occupationCommunity_id = (SELECT community_id FROM facility WHERE facility.id = person.occupationFacility_id) WHERE occupationFacility_id IS NOT NULL;");
				getDao(PreviousHospitalization.class)
					.executeRaw("ALTER TABLE previoushospitalizations ADD COLUMN healthFacilityDetails varchar(512);");
			case 125:
				currentVersion = 125;
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN meningealSigns varchar(255);");
			case 126:
				currentVersion = 126;
				getDao(PathogenTest.class)
					.executeRaw("UPDATE sampleTests SET testType = 'IGM_SERUM_ANTIBODY' WHERE testType = 'SERUM_ANTIBODY_TITER';");
				getDao(PathogenTest.class).executeRaw("UPDATE sampleTests SET testType = 'IGM_SERUM_ANTIBODY' WHERE testType = 'ELISA';");
				getDao(PathogenTest.class)
					.executeRaw("UPDATE sampleTests SET testType = 'PCR_RT_PCR' WHERE testType = 'PCR' OR testType = 'RT_PCR';");
				getDao(Sample.class)
					.executeRaw("UPDATE samples SET suggestedTypeOfTest = 'IGM_SERUM_ANTIBODY' WHERE suggestedTypeOfTest = 'SERUM_ANTIBODY_TITER';");
				getDao(Sample.class).executeRaw("UPDATE samples SET suggestedTypeOfTest = 'IGM_SERUM_ANTIBODY' WHERE suggestedTypeOfTest = 'ELISA';");
				getDao(Sample.class).executeRaw(
					"UPDATE samples SET suggestedTypeOfTest = 'PCR_RT_PCR' WHERE suggestedTypeOfTest = 'PCR' OR suggestedTypeOfTest = 'RT_PCR';");
				getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN labDetails varchar(512);");
			case 127:
				currentVersion = 127;
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN directContactConfirmedCase varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN directContactProbableCase varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN closeContactProbableCase varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN areaConfirmedCases varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN processingConfirmedCaseFluidUnsafe varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN percutaneousCaseBlood varchar(255);");
				getDao(EpiData.class).executeRaw(
					"UPDATE epidata SET poultryDetails=null, poultry=null,"
						+ " wildbirds=null, wildbirdsDetails=null, wildbirdsDate=null, wildbirdsLocation=null;");
			case 128:
				currentVersion = 128;
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN resultingCaseUser_id bigint REFERENCES users(id);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN caseUuid VARCHAR;");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN caseDisease VARCHAR;");
				getDao(Contact.class).executeRaw(
					"UPDATE contacts SET caseUuid = (SELECT uuid FROM cases WHERE cases.id = contacts.caze_id) WHERE caze_id IS NOT NULL;");
				getDao(Contact.class).executeRaw(
					"UPDATE contacts SET caseDisease = (SELECT disease FROM cases WHERE cases.id = contacts.caze_id) WHERE caze_id IS NOT NULL;");
				// Re-creation of the contacts table has to be done to remove not null constraints and the case id column
				getDao(Contact.class).executeRaw("ALTER TABLE contacts RENAME TO tmp_contacts;");
				getDao(Contact.class).executeRaw(
					"CREATE TABLE contacts (caseUuid VARCHAR, caseDisease VARCHAR, contactClassification VARCHAR, "
						+ "contactOfficer_id BIGINT REFERENCES users(id), contactProximity VARCHAR, contactStatus VARCHAR, "
						+ "description VARCHAR, followUpComment VARCHAR, followUpStatus VARCHAR, followUpUntil BIGINT, "
						+ "lastContactDate BIGINT, person_id BIGINT REFERENCES person(id), relationToCase VARCHAR, "
						+ "reportDateTime BIGINT, reportLat DOUBLEPRECISION, reportLatLonAccuracy FLOAT, reportLon DOUBLEPRECISION, reportingUser_id BIGINT REFERENCES users(id), "
						+ "resultingCaseUuid VARCHAR, resultingCaseUser_id BIGINT REFERENCES users(id),"
						+ "changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ "lastOpenedDate BIGINT, localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, UNIQUE(snapshot, uuid));");
				getDao(Contact.class).executeRaw(
					"INSERT INTO contacts(caseUuid, caseDisease, contactClassification, "
						+ "contactOfficer_id, contactProximity, contactStatus, description, followUpComment, followUpStatus, followUpUntil,"
						+ "lastContactDate, person_id, relationToCase, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id,"
						+ "resultingCaseUuid, resultingCaseUser_id,"
						+ "changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid) "
						+ "SELECT caseUuid, caseDisease, contactClassification, "
						+ "contactOfficer_id, contactProximity, contactStatus, description, followUpComment, followUpStatus, followUpUntil, "
						+ "lastContactDate, person_id, relationToCase, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, "
						+ "resultingCaseUuid, resultingCaseUser_id, "
						+ "changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid " + "FROM tmp_contacts;");
				getDao(Contact.class).executeRaw("DROP TABLE tmp_contacts;");
			case 129:
				currentVersion = 129;
				getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN referredToUuid varchar(36);");
				getDao(Sample.class)
					.executeRaw("UPDATE samples SET referredToUuid = (SELECT uuid FROM samples s2 WHERE s2.id = samples.referredTo_id);");
			case 130:
				currentVersion = 130;
				getDao(DiseaseClassificationCriteria.class).executeRaw(
					"CREATE TABLE diseaseClassificationCriteria (disease VARCHAR, suspectCriteria TEXT, "
						+ "probableCriteria TEXT, confirmedCriteria TEXT, changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, "
						+ "id INTEGER PRIMARY KEY AUTOINCREMENT, localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, "
						+ "UNIQUE(snapshot, uuid));");
			case 131:
				currentVersion = 131;
				getDao(User.class).executeRaw("ALTER TABLE users ADD COLUMN community_id bigint REFERENCES community(id);");
				getDao(User.class).executeRaw("UPDATE users SET userRole = replace(userRole, 'INFORMANT', 'HOSPITAL_INFORMANT');");
			case 132:
				currentVersion = 132;
				getDao(UserRoleConfig.class).executeRaw(
					"CREATE TABLE userrolesconfig(" + "id integer primary key autoincrement," + "uuid varchar(36)," + "changeDate timestamp,"
						+ "creationDate timestamp," + "userRole varchar(255)," + "userRights varchar(1023)," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp," + "modified integer," + "snapshot integer);");
			case 133:
				currentVersion = 133;
				getDao(WeeklyReport.class).executeRaw("ALTER TABLE weeklyreport RENAME TO tmp_weeklyreport;");
				getDao(WeeklyReport.class).executeRaw(
					"CREATE TABLE weeklyreport(id integer primary key autoincrement, uuid varchar(36) not null unique, changeDate timestamp not null, "
						+ "creationDate timestamp not null, lastOpenedDate timestamp, localChangeDate timestamp not null, modified integer, snapshot integer, healthFacility_id bigint REFERENCES facility(id), "
						+ "reportingUser_id bigint REFERENCES users(id), reportDateTime timestamp, totalNumberOfCases integer, epiWeek integer, year integer, district_id bigint REFERENCES district(id), "
						+ "community_id bigint REFERENCES community(id), assignedOfficer_id bigint REFERENCES users(id), UNIQUE(snapshot, uuid));");
				getDao(WeeklyReport.class).executeRaw(
					"INSERT INTO weeklyreport(id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, healthFacility_id, "
						+ "reportingUser_id, reportDateTime, totalNumberOfCases, epiWeek, year) "
						+ "SELECT id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, healthFacility_id, informant_id, reportDateTime, "
						+ "totalNumberOfCases, epiWeek, year " + "FROM tmp_weeklyreport;");
				getDao(WeeklyReport.class).executeRaw("DROP TABLE tmp_weeklyreport;");
			case 134:
				currentVersion = 134;
				getDao(Outbreak.class).executeRaw("ALTER TABLE outbreak ADD COLUMN startDate timestamp;");
				getDao(Outbreak.class).executeRaw("ALTER TABLE outbreak ADD COLUMN endDate timestamp;");
				getDao(Outbreak.class).executeRaw("UPDATE outbreak SET startDate=creationDate");
			case 135:
				currentVersion = 135;
				getDao(Sample.class).executeRaw("UPDATE samples SET suggestedTypeOfTest='ISOLATION' WHERE suggestedTypeOfTest='VIRUS_ISOLATION'");
				getDao(PathogenTest.class).executeRaw("UPDATE sampleTests SET testType='ISOLATION' WHERE testType='VIRUS_ISOLATION'");
			case 136:
				currentVersion = 136;
				try {
					getDao(Outbreak.class).executeRaw("ALTER TABLE diseaseClassificationCriteria ADD COLUMN lastOpenedDate timestamp;");
				} catch (SQLException e) {
				} // may already exist
				getDao(WeeklyReportEntry.class).executeRaw("ALTER TABLE weeklyreportentry RENAME TO tmp_weeklyreportentry;");
				getDao(WeeklyReportEntry.class).executeRaw(
					"CREATE TABLE weeklyreportentry(" + "id integer primary key autoincrement," + "uuid varchar(36) not null unique,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "weeklyReport_id bigint REFERENCES weeklyreport(id)," + "disease character varying(255)," + "numberOfCases integer" + ");");
				getDao(WeeklyReportEntry.class).executeRaw(
					"INSERT INTO weeklyreportentry(id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, weeklyReport_id, "
						+ "disease, numberOfCases) "
						+ "SELECT id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, weeklyReport_id, disease, numberOfCases "
						+ "FROM tmp_weeklyreportentry;");
				getDao(WeeklyReportEntry.class).executeRaw("DROP TABLE tmp_weeklyreportentry;");
			case 137:
				currentVersion = 137;
				try {
					getDao(DiseaseClassificationCriteria.class).executeRaw("DROP TABLE diseaseClassificationCriteria");
					getDao(DiseaseClassificationCriteria.class).executeRaw("DROP TABLE diseaseClassification");
				} catch (SQLException e) {
				} // one of the tables won't exist
				getDao(DiseaseClassificationCriteria.class).executeRaw(
					"CREATE TABLE diseaseClassificationCriteria (disease VARCHAR, suspectCriteria TEXT, "
						+ "probableCriteria TEXT, confirmedCriteria TEXT, changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, "
						+ "id INTEGER PRIMARY KEY AUTOINCREMENT, localChangeDate BIGINT NOT NULL, modified SMALLINT, lastOpenedDate timestamp, snapshot SMALLINT, uuid VARCHAR NOT NULL, "
						+ "UNIQUE(snapshot, uuid));");
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
			case 140:
				currentVersion = 140;
				getDao(PathogenTest.class).executeRaw("ALTER TABLE sampleTests RENAME TO pathogenTests;");
				getDao(Sample.class).executeRaw("ALTER TABLE samples RENAME TO tmp_samples;");
				getDao(Sample.class).executeRaw(
					"CREATE TABLE samples (associatedCase_id BIGINT, comment VARCHAR, lab_id BIGINT, "
						+ "labDetails VARCHAR, labSampleID VARCHAR, noTestPossibleReason VARCHAR, received SMALLINT, receivedDate BIGINT, referredToUuid VARCHAR, "
						+ "reportDateTime BIGINT, reportLat DOUBLE PRECISION, reportLatLonAccuracy FLOAT, reportLon DOUBLE PRECISION, "
						+ "reportingUser_id BIGINT, sampleCode VARCHAR, sampleDateTime BIGINT, sampleMaterial VARCHAR, sampleMaterialText VARCHAR, "
						+ "sampleSource VARCHAR, shipmentDate BIGINT, shipmentDetails VARCHAR, shipped SMALLINT, specimenCondition VARCHAR, "
						+ "pathogenTestingRequested SMALLINT, additionalTestingRequested SMALLINT, requestedPathogenTestsString VARCHAR, requestedAdditionalTestsString VARCHAR, "
						+ "changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT, lastOpenedDate BIGINT, "
						+ "localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, UNIQUE (snapshot, uuid));");
				getDao(Sample.class).executeRaw(
					"INSERT INTO samples(associatedCase_id, comment, lab_id, labDetails, labSampleID, noTestPossibleReason, "
						+ "received, receivedDate, referredToUuid, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, sampleCode, "
						+ "sampleDateTime, sampleMaterial, sampleMaterialText, sampleSource, shipmentDate, shipmentDetails, shipped, specimenCondition, "
						+ "requestedPathogenTestsString, changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid) "
						+ "SELECT associatedCase_id, comment, lab_id, labDetails, labSampleID, noTestPossibleReason, received, receivedDate, referredToUuid, "
						+ "reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, sampleCode, sampleDateTime, sampleMaterial, "
						+ "sampleMaterialText, sampleSource, shipmentDate, shipmentDetails, shipped, specimenCondition, suggestedTypeOfTest, changeDate, creationDate, "
						+ "id, lastOpenedDate, localChangeDate, modified, snapshot, uuid FROM tmp_samples;");
				getDao(Sample.class).executeRaw("UPDATE samples SET pathogenTestingRequested = 1;");
				getDao(Sample.class).executeRaw("UPDATE samples SET additionalTestingRequested = 0;");
				getDao(Sample.class).executeRaw("DROP TABLE tmp_samples;");
			case 141:
				currentVersion = 141;
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN respiratoryRate integer;");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN weight integer;");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN height integer;");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN midUpperArmCircumference integer;");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN glasgowComaScale integer;");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN hemorrhagicSyndrome varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN hyperglycemia varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN hypoglycemia varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN sepsis varchar(255);");
			case 142:
				currentVersion = 142;
				getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN pathogenTestResult varchar(255);");
			case 143:
				currentVersion = 143;
				getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN requestedOtherPathogenTests varchar(512);");
				getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN requestedOtherAdditionalTests varchar(512);");
			case 144:
				currentVersion = 144;
				getDao(WeeklyReportEntry.class).executeRaw("ALTER TABLE weeklyreportentry RENAME TO tmp_weeklyreportentry;");
				getDao(WeeklyReportEntry.class).executeRaw(
					"CREATE TABLE weeklyreportentry(" + "id integer primary key autoincrement," + "uuid varchar(36) not null unique,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer," + "weeklyReport_id bigint,"
						+ "disease character varying(255)," + "numberOfCases integer,"
						+ "CONSTRAINT fk_weeklyreportentry_weeklyreport_id FOREIGN KEY (weeklyReport_id) REFERENCES weeklyreport (id) ON DELETE CASCADE"
						+ ");");
				getDao(WeeklyReportEntry.class).executeRaw(
					"INSERT INTO weeklyreportentry(id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, weeklyReport_id, "
						+ "disease, numberOfCases) "
						+ "SELECT id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, weeklyReport_id, disease, numberOfCases "
						+ "FROM tmp_weeklyreportentry;");
				getDao(WeeklyReportEntry.class).executeRaw("DROP TABLE tmp_weeklyreportentry;");
				getDao(WeeklyReport.class).executeRaw(
					"DELETE FROM weeklyreport WHERE rowid NOT IN (SELECT min(rowid) FROM weeklyreport GROUP BY epiWeek, year, reportingUser_id);");
			case 145:
				currentVersion = 145;
				getDao(Therapy.class).executeRaw(
					"CREATE TABLE therapy(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer," + "UNIQUE (snapshot, uuid));");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN therapy_id bigint REFERENCES therapy(id)");
				getDao(Prescription.class).executeRaw(
					"CREATE TABLE prescription(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "therapy_id bigint REFERENCES therapy(id)," + "prescriptionDate timestamp," + "prescriptionStart timestamp,"
						+ "prescriptionEnd timestamp," + "prescribingClinician varchar(512)," + "prescriptionType varchar(255),"
						+ "prescriptionDetails varchar(512)," + "frequency varchar(512)," + "dose varchar(512)," + "route varchar(255),"
						+ "routeDetails varchar(512)," + "additionalNotes varchar(512)," + "typeOfDrug varchar(255)," + "UNIQUE(snapshot, uuid));");
				getDao(Treatment.class).executeRaw(
					"CREATE TABLE treatment(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "therapy_id bigint REFERENCES therapy(id)," + "treatmentDateTime timestamp," + "executingClinician varchar(512),"
						+ "treatmentType varchar(255)," + "treatmentDetails varchar(512)," + "dose varchar(512)," + "route varchar(255),"
						+ "routeDetails varchar(512)," + "additionalNotes varchar(512)," + "typeOfDrug varchar(255),"
						+ "prescription_id bigint REFERENCES prescription(id)," + "UNIQUE(snapshot, uuid));");
			case 146:
				currentVersion = 146;
				getDao(Case.class).executeRaw("UPDATE cases SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Symptoms.class).executeRaw("UPDATE symptoms SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Hospitalization.class).executeRaw("UPDATE hospitalizations SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiData.class).executeRaw("UPDATE epidata SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataBurial.class).executeRaw("UPDATE epidataburial SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataTravel.class).executeRaw("UPDATE epidatatravel SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataGathering.class).executeRaw("UPDATE epidatagathering SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Location.class).executeRaw("UPDATE location SET changeDate = 0 WHERE changeDate IS NOT NULL;");
			case 147:
				currentVersion = 147;
				getDao(AdditionalTest.class).executeRaw(
					"CREATE TABLE additionaltest(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "sample_id bigint REFERENCES samples(id)," + "testDateTime timestamp," + "haemoglobinuria varchar(255),"
						+ "proteinuria varchar(255)," + "hematuria varchar(255)," + "arterialVenousGasPh integer," + "arterialVenousGasPco2 integer,"
						+ "arterialVenousGasPao2 integer," + "arterialVenousGasHco3 integer," + "gasOxygenTherapy integer," + "altSgpt integer,"
						+ "astSgot integer," + "creatinine integer," + "potassium integer," + "urea integer," + "haemoglobin integer,"
						+ "totalBilirubin integer," + "conjBilirubin integer," + "wbcCount integer," + "platelets integer,"
						+ "prothrombinTime integer," + "otherTestResults varchar(512)," + "UNIQUE(snapshot, uuid));");
			case 148:
				currentVersion = 148;
				getDao(HealthConditions.class).executeRaw(
					"CREATE TABLE healthConditions(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer," + "tuberculosis varchar(255),"
						+ "asplenia varchar(255)," + "hepatitis varchar(255)," + "diabetes varchar(255)," + "hiv varchar(255),"
						+ "hivArt varchar(255)," + "chronicLiverDisease varchar(255)," + "malignancyChemotherapy varchar(255),"
						+ "chronicHeartFailure varchar(255)," + "chronicPulmonaryDisease varchar(255)," + "chronicKidneyDisease varchar(255),"
						+ "chronicNeurologicCondition varchar(255)," + "otherConditions varchar(512)," + "UNIQUE(snapshot, uuid));");
				getDao(ClinicalCourse.class).executeRaw(
					"CREATE TABLE clinicalCourse(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "healthConditions_id bigint REFERENCES healthConditions(id)," + "UNIQUE(snapshot, uuid));");
				getDao(ClinicalVisit.class).executeRaw(
					"CREATE TABLE clinicalVisit(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "clinicalCourse_id bigint REFERENCES clinicalCourse(id)," + "symptoms_id bigint REFERENCES symptoms(id),"
						+ "disease varchar(255)," + "visitDateTime timestamp," + "visitRemarks varchar(512)," + "visitingPerson varchar(512),"
						+ "UNIQUE(snapshot, uuid));");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN clinicalCourse_id bigint REFERENCES therapy(id)");
				getDao(Case.class).executeRaw("UPDATE cases SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Therapy.class).executeRaw("UPDATE therapy SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Symptoms.class).executeRaw("UPDATE symptoms SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Hospitalization.class).executeRaw("UPDATE hospitalizations SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiData.class).executeRaw("UPDATE epidata SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataBurial.class).executeRaw("UPDATE epidataburial SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataTravel.class).executeRaw("UPDATE epidatatravel SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataGathering.class).executeRaw("UPDATE epidatagathering SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Location.class).executeRaw("UPDATE location SET changeDate = 0 WHERE changeDate IS NOT NULL;");
			case 149:
				currentVersion = 149;
				getDao(DiseaseConfiguration.class).executeRaw(
					"CREATE TABLE diseaseConfiguration(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer," + "disease varchar(255),"
						+ "active boolean," + "primaryDisease boolean," + "followUpEnabled boolean," + "followUpDuration integer,"
						+ "UNIQUE(snapshot, uuid));");
			case 150:
				currentVersion = 150;
				getDao(Person.class).executeRaw("UPDATE person SET educationtype='NONE' WHERE educationtype='NURSERY';");
			case 151:
				currentVersion = 151;
				getDao(PathogenTest.class).executeRaw("ALTER TABLE pathogenTests ADD COLUMN testedDisease varchar(255);");
				getDao(PathogenTest.class).executeRaw(
					"UPDATE pathogenTests SET testedDisease = 'WEST_NILE_FEVER' WHERE testType = 'WEST_NILE_FEVER_IGM' OR testType = 'WEST_NILE_FEVER_ANTIBODIES';");
				getDao(PathogenTest.class).executeRaw(
					"UPDATE pathogenTests SET testedDisease = 'DENGUE' WHERE testType = 'DENGUE_FEVER_IGM' OR testType = 'DENGUE_FEVER_ANTIBODIES';");
				getDao(PathogenTest.class).executeRaw(
					"UPDATE pathogenTests SET testedDisease = 'YELLOW_FEVER' WHERE testType = 'YELLOW_FEVER_IGM' OR testType = 'YELLOW_FEVER_ANTIBODIES';");
				getDao(PathogenTest.class)
					.executeRaw("UPDATE pathogenTests SET testedDisease = 'PLAGUE' WHERE testType = 'YERSINIA_PESTIS_ANTIGEN';");
				getDao(PathogenTest.class).executeRaw(
					"UPDATE pathogenTests SET testedDisease = (SELECT disease FROM cases WHERE cases.id = (SELECT associatedCase_id FROM samples WHERE samples.id = pathogenTests.sample_id)) WHERE testedDisease IS NULL;");
				getDao(PathogenTest.class).executeRaw(
					"UPDATE pathogenTests SET testType = 'IGM_SERUM_ANTIBODY' WHERE testType = 'DENGUE_FEVER_IGM' OR testType = 'WEST_NILE_FEVER_IGM' OR testType = 'YELLOW_FEVER_IGM';");
				getDao(PathogenTest.class).executeRaw(
					"UPDATE pathogenTests SET testType = 'NEUTRALIZING_ANTIBODIES' WHERE testType = 'DENGUE_FEVER_ANTIBODIES' OR testType = 'WEST_NILE_FEVER_ANTIBODIES' OR testType = 'YELLOW_FEVER_ANTIBODIES';");
				getDao(PathogenTest.class)
					.executeRaw("UPDATE pathogenTests SET testType = 'ANTIGEN_DETECTION' WHERE testType = 'YERSINIA_PESTIS_ANTIGEN';");
				getDao(Sample.class).executeRaw(
					"UPDATE samples SET requestedPathogenTestsString = REPLACE(requestedPathogenTestsString, 'DENGUE_FEVER_IGM', 'IGM_SERUM_ANTIBODY');");
				getDao(Sample.class).executeRaw(
					"UPDATE samples SET requestedPathogenTestsString = REPLACE(requestedPathogenTestsString, 'WEST_NILE_FEVER_IGM', 'IGM_SERUM_ANTIBODY');");
				getDao(Sample.class).executeRaw(
					"UPDATE samples SET requestedPathogenTestsString = REPLACE(requestedPathogenTestsString, 'YELLOW_FEVER_IGM', 'IGM_SERUM_ANTIBODY');");
				getDao(Sample.class).executeRaw(
					"UPDATE samples SET requestedPathogenTestsString = REPLACE(requestedPathogenTestsString, 'DENGUE_FEVER_ANTIBODIES', 'NEUTRALIZING_ANTIBODIES');");
				getDao(Sample.class).executeRaw(
					"UPDATE samples SET requestedPathogenTestsString = REPLACE(requestedPathogenTestsString, 'WEST_NILE_FEVER_ANTIBODIES', 'NEUTRALIZING_ANTIBODIES');");
				getDao(Sample.class).executeRaw(
					"UPDATE samples SET requestedPathogenTestsString = REPLACE(requestedPathogenTestsString, 'YELLOW_FEVER_ANTIBODIES', 'NEUTRALIZING_ANTIBODIES');");
				getDao(Sample.class).executeRaw(
					"UPDATE samples SET requestedPathogenTestsString = REPLACE(requestedPathogenTestsString, 'YERSINIA_PESTIS_ANTIGEN', 'ANTIGEN_DETECTION');");
				getDao(PathogenTest.class).executeRaw("ALTER TABLE pathogenTests ADD COLUMN testedDiseaseDetails varchar(512);");
			case 152:
				currentVersion = 152;
				getDao(Person.class).executeRaw("UPDATE person SET educationType='NURSERY' WHERE educationType='NONE';");
			case 153:
				currentVersion = 153;
				getDao(Location.class).executeRaw("ALTER TABLE location ADD COLUMN areaType varchar(255);");
			case 154:
				currentVersion = 154;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN clinicianDetails varchar(512);");
			case 155:
				currentVersion = 155;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN districtLevelDate timestamp;");
			case 156:
				currentVersion = 156;
				getDao(User.class).executeRaw("ALTER TABLE users ADD COLUMN limitedDisease varchar(255);");
			case 157:
				currentVersion = 157;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN notifyingClinic varchar(255);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN notifyingClinicDetails varchar(512);");
				getDao(HealthConditions.class).executeRaw("ALTER TABLE healthConditions ADD COLUMN downSyndrome varchar(255);");
				getDao(HealthConditions.class).executeRaw("ALTER TABLE healthConditions ADD COLUMN congenitalSyphilis varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN bilateralCataracts varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN unilateralCataracts varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN congenitalGlaucoma varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN pigmentaryRetinopathy varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN purpuricRash varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN microcephaly varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN developmentalDelay varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN splenomegaly varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN meningoencephalitis varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN radiolucentBoneDisease varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN congenitalHeartDisease varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN congenitalHeartDiseaseType varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN congenitalHeartDiseaseDetails varchar(512);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN jaundiceWithin24HoursOfBirth varchar(255);");
			case 158:
				currentVersion = 158;
				getDao(MaternalHistory.class).executeRaw(
					"CREATE TABLE maternalHistory(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer," + "childrenNumber integer,"
						+ "ageAtBirth integer," + "conjunctivitis varchar(255)," + "conjunctivitisOnset timestamp," + "conjunctivitisMonth integer,"
						+ "maculopapularRash varchar(255)," + "maculopapularRashOnset timestamp," + "maculopapularRashMonth integer,"
						+ "swollenLymphs varchar(255)," + "swollenLymphsOnset timestamp," + "swollenLymphsMonth integer,"
						+ "arthralgiaArthritis varchar(255)," + "arthralgiaArthritisOnset timestamp," + "arthralgiaArthritisMonth integer,"
						+ "otherComplications varchar(255)," + "otherComplicationsOnset timestamp," + "otherComplicationsMonth integer,"
						+ "otherComplicationsDetails varchar(512)," + "rubella varchar(255)," + "rubellaOnset timestamp,"
						+ "rashExposure varchar(255)," + "rashExposureDate timestamp," + "rashExposureMonth integer,"
						+ "rashExposureRegion_id bigint REFERENCES region(id)," + "rashExposureDistrict_id bigint REFERENCES district(id),"
						+ "rashExposureCommunity_id bigint REFERENCES community(id)," + "UNIQUE(snapshot, uuid));");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN maternalHistory_id bigint REFERENCES maternalHistory(id);");
				getDao(Case.class).executeRaw("UPDATE cases SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Therapy.class).executeRaw("UPDATE therapy SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(ClinicalCourse.class).executeRaw("UPDATE clinicalCourse SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(HealthConditions.class).executeRaw("UPDATE healthConditions SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Symptoms.class).executeRaw("UPDATE symptoms SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Hospitalization.class).executeRaw("UPDATE hospitalizations SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiData.class).executeRaw("UPDATE epidata SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataBurial.class).executeRaw("UPDATE epidataburial SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataTravel.class).executeRaw("UPDATE epidatatravel SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataGathering.class).executeRaw("UPDATE epidatagathering SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Location.class).executeRaw("UPDATE location SET changeDate = 0 WHERE changeDate IS NOT NULL;");
			case 159:
				currentVersion = 159;
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN mothersName varchar(512);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN fathersName varchar(512);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN placeOfBirthRegion_id bigint REFERENCES region(id);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN placeOfBirthDistrict_id bigint REFERENCES district(id);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN placeOfBirthCommunity_id bigint REFERENCES community(id);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN placeOfBirthFacility_id bigint REFERENCES facility(id);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN placeOfBirthFacilityDetails varchar(512);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN gestationAgeAtBirth integer;");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN birthWeight integer;");
			case 160:
				currentVersion = 160;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN versionCreated varchar(32);");
			case 161:
				currentVersion = 161;
				getDao(PointOfEntry.class).executeRaw(
					"CREATE TABLE pointOfEntry(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer," + "pointOfEntryType varchar(255),"
						+ "name varchar(512)," + "region_id bigint REFERENCES region(id)," + "district_id bigint REFERENCES district(id),"
						+ "latitude double precision," + "longitude double precision," + "active boolean," + "UNIQUE(snapshot, uuid));");
				getDao(PointOfEntry.class).executeRaw(
					"CREATE TABLE portHealthInfo(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer," + "airlineName varchar(512),"
						+ "flightNumber varchar(512)," + "departureDateTime timestamp," + "arrivalDateTime timestamp," + "freeSeating varchar(255),"
						+ "seatNumber varchar(512)," + "departureAirport varchar(512)," + "numberOfTransitStops integer,"
						+ "transitStopDetails1 varchar(512)," + "transitStopDetails2 varchar(512)," + "transitStopDetails3 varchar(512),"
						+ "transitStopDetails4 varchar(512)," + "transitStopDetails5 varchar(512)," + "vesselName varchar(512),"
						+ "vesselDetails varchar(512)," + "portOfDeparture varchar(512)," + "lastPortOfCall varchar(512),"
						+ "conveyanceType varchar(255)," + "conveyanceTypeDetails varchar(512)," + "departureLocation varchar(512),"
						+ "finalDestination varchar(512)," + "details varchar(512)," + "UNIQUE(snapshot, uuid));");
				getDao(User.class).executeRaw("ALTER TABLE users ADD COLUMN pointOfEntry_id bigint REFERENCES pointOfEntry(id);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN portHealthInfo_id bigint REFERENCES portHealthInfo(id);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN caseOrigin varchar(255);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN pointOfEntry_id bigint REFERENCES pointOfEntry(id);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN pointOfEntryDetails varchar(512);");
				getDao(Case.class).executeRaw("UPDATE cases SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Therapy.class).executeRaw("UPDATE therapy SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(ClinicalCourse.class).executeRaw("UPDATE clinicalCourse SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(HealthConditions.class).executeRaw("UPDATE healthConditions SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Symptoms.class).executeRaw("UPDATE symptoms SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Hospitalization.class).executeRaw("UPDATE hospitalizations SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(PreviousHospitalization.class).executeRaw("UPDATE previoushospitalizations SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiData.class).executeRaw("UPDATE epidata SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataBurial.class).executeRaw("UPDATE epidataburial SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataTravel.class).executeRaw("UPDATE epidatatravel SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(EpiDataGathering.class).executeRaw("UPDATE epidatagathering SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(MaternalHistory.class).executeRaw("UPDATE maternalHistory SET changeDate = 0 WHERE changeDate IS NOT NULL;");
				getDao(Location.class).executeRaw("UPDATE location SET changeDate = 0 WHERE changeDate IS NOT NULL;");
			case 162:
				currentVersion = 162;
				getDao(AdditionalTest.class).executeRaw("ALTER TABLE additionalTest RENAME TO tmp_additionalTest");
				getDao(AdditionalTest.class).executeRaw(
					"CREATE TABLE additionalTest(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "sample_id bigint REFERENCES samples(id)," + "testDateTime timestamp," + "haemoglobinuria varchar(255),"
						+ "proteinuria varchar(255)," + "hematuria varchar(255)," + "arterialVenousGasPh real," + "arterialVenousGasPco2 real,"
						+ "arterialVenousGasPao2 real," + "arterialVenousGasHco3 real," + "gasOxygenTherapy real," + "altSgpt real," + "astSgot real,"
						+ "creatinine real," + "potassium real," + "urea real," + "haemoglobin real," + "totalBilirubin real," + "conjBilirubin real,"
						+ "wbcCount real," + "platelets real," + "prothrombinTime real," + "otherTestResults varchar(512),"
						+ "UNIQUE(snapshot, uuid));");
				getDao(AdditionalTest.class).executeRaw(
					"INSERT INTO additionalTest(id, uuid, changeDate, creationDate, lastOpenedDate, "
						+ "localChangeDate, modified, snapshot, sample_id, testDateTime, haemoglobinuria, proteinuria, hematuria, arterialVenousGasPh, "
						+ "arterialVenousGasPco2, arterialVenousGasPao2, arterialVenousGasHco3, gasOxygenTherapy, altSgpt, astSgot, creatinine, "
						+ "potassium, urea, haemoglobin, totalBilirubin, conjBilirubin, wbcCount, platelets, prothrombinTime, otherTestResults) "
						+ "SELECT id, uuid, changeDate, creationDate, lastOpenedDate, localChangeDate, modified, snapshot, sample_id, testDateTime, "
						+ "haemoglobinuria, proteinuria, hematuria, arterialVenousGasPh, arterialVenousGasPco2, arterialVenousGasPao2, arterialVenousGasHco3, "
						+ "gasOxygenTherapy, altSgpt, astSgot, creatinine, potassium, urea, haemoglobin, totalBilirubin, conjBilirubin, wbcCount, "
						+ "platelets, prothrombinTime, otherTestResults FROM tmp_additionalTest");
				getDao(Sample.class).executeRaw("DROP TABLE tmp_additionalTest;");
			case 163:
				currentVersion = 163;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN clinicianPhone varchar(512);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN clinicianEmail varchar(512);");
			case 164:
				currentVersion = 164;
				getDao(Case.class).executeRaw("DELETE FROM cases WHERE person_id IS NULL");
			case 165:
				currentVersion = 165;
				// normally we would have a temp table and copy the data. In this context we have to pull all samples anyway
				getDao(WeeklyReportEntry.class).executeRaw("DROP TABLE pathogenTests;");
				getDao(WeeklyReportEntry.class).executeRaw(
					"CREATE TABLE pathogenTest(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer,"
						+ "sample_id bigint REFERENCES samples(id)," + "testDateTime timestamp," + "testType character varying(255),"
						+ "testTypeText character varying(512)," + "lab_id bigint REFERENCES facility(id),"
						+ "labUser_id bigint REFERENCES users(id)," + "testResult character varying(255)," + "testResultText character varying(512) ,"
						+ "testResultVerified boolean," + "fourFoldIncreaseAntibodyTiter boolean," + "labDetails character varying(512) ,"
						+ "testedDisease character varying(255)," + "testedDiseaseDetails character varying(512)," + "UNIQUE(snapshot, uuid));");

				// we had an invalid break here - re-execute db update for all versions in between

			case 166:
			case 167:
			case 168:
			case 169:
			case 170:
			case 171:

				// this is a special solution to repair broken devices and at the same time not corrupt healthy ones

				try {
					currentVersion = 166;
					getDao(PathogenTest.class).executeRaw("ALTER TABLE pathogenTest ADD COLUMN serotype varchar(255);");
					getDao(PathogenTest.class).executeRaw("ALTER TABLE pathogenTest ADD COLUMN cqValue real;");
				} catch (SQLException e) {
				}

				try {
					currentVersion = 167;
					getDao(Sample.class).executeRaw("ALTER TABLE samples RENAME TO tmp_samples;");
					getDao(Sample.class).executeRaw(
						"CREATE TABLE samples (associatedCase_id BIGINT, comment VARCHAR, lab_id BIGINT, "
							+ "labDetails VARCHAR, labSampleID VARCHAR, noTestPossibleReason VARCHAR, received SMALLINT, receivedDate BIGINT, referredToUuid VARCHAR, "
							+ "reportDateTime BIGINT, reportLat DOUBLE PRECISION, reportLatLonAccuracy FLOAT, reportLon DOUBLE PRECISION, "
							+ "reportingUser_id BIGINT, sampleDateTime BIGINT, sampleMaterial VARCHAR, sampleMaterialText VARCHAR, "
							+ "sampleSource VARCHAR, shipmentDate BIGINT, shipmentDetails VARCHAR, shipped SMALLINT, specimenCondition VARCHAR, "
							+ "pathogenTestingRequested SMALLINT, additionalTestingRequested SMALLINT, requestedPathogenTestsString VARCHAR, requestedAdditionalTestsString VARCHAR, "
							+ "requestedOtherPathogenTests varchar(512), requestedOtherAdditionalTests varchar(512), samplePurpose varchar(255), pathogenTestResult varchar(255), "
							+ "changeDate BIGINT NOT NULL, creationDate BIGINT NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT, lastOpenedDate BIGINT, "
							+ "localChangeDate BIGINT NOT NULL, modified SMALLINT, snapshot SMALLINT, uuid VARCHAR NOT NULL, UNIQUE (snapshot, uuid));");
					getDao(Sample.class).executeRaw(
						"INSERT INTO samples(associatedCase_id, comment, lab_id, labDetails, labSampleID, noTestPossibleReason, "
							+ "received, receivedDate, referredToUuid, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, "
							+ "sampleDateTime, sampleMaterial, sampleMaterialText, sampleSource, shipmentDate, shipmentDetails, shipped, specimenCondition, "
							+ "pathogenTestingRequested, additionalTestingRequested, requestedPathogenTestsString, requestedAdditionalTestsString, "
							+ "requestedOtherPathogenTests, requestedOtherAdditionalTests, samplePurpose, pathogenTestResult, "
							+ "changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid) "
							+ "SELECT associatedCase_id, comment, lab_id, labDetails, labSampleID, noTestPossibleReason, "
							+ "received, receivedDate, referredToUuid, reportDateTime, reportLat, reportLatLonAccuracy, reportLon, reportingUser_id, "
							+ "sampleDateTime, sampleMaterial, sampleMaterialText, sampleSource, shipmentDate, shipmentDetails, shipped, specimenCondition, "
							+ "pathogenTestingRequested, additionalTestingRequested, requestedPathogenTestsString, requestedAdditionalTestsString, "
							+ "requestedOtherPathogenTests, requestedOtherAdditionalTests, 'EXTERNAL', pathogenTestResult, "
							+ "changeDate, creationDate, id, lastOpenedDate, localChangeDate, modified, snapshot, uuid FROM tmp_samples;");
					getDao(Sample.class).executeRaw("DROP TABLE tmp_samples;");
				} catch (SQLException e) {
				}

				try {
					currentVersion = 168;

					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN vaccine varchar(512);");
					getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN rabiesType varchar(255);");

					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN hydrophobia varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN opisthotonus varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN anxietyStates varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN delirium varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN uproariousness varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN paresthesiaAroundWound varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN excessSalivation varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN insomnia varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN paralysis varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN excitation varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN dysphagia varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN aerophobia varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN hyperactivity varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN paresis varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN agitation varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN ascendingFlaccidParalysis varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN erraticBehaviour varchar(255);");
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN coma varchar(255);");

					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN dogs varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN cats varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN canidae varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN rabbits varchar(255);");

					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN prophylaxisStatus varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN dateOfProphylaxis timestamp;");

					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN kindOfExposureBite varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN kindOfExposureTouch varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN kindOfExposureScratch varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN kindOfExposureLick varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN kindOfExposureOther varchar(255);");
					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN kindOfExposureDetails varchar(512);");

					getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN animalVaccinationStatus varchar(255);");
				} catch (SQLException e) {
				}

				try {
					currentVersion = 169;
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN relationDescription varchar(512);");
				} catch (SQLException e) {
				}

				try {
					currentVersion = 170;
					getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN convulsion varchar(255);");
				} catch (SQLException e) {
				}
			case 172:
				currentVersion = 172;
				getDao(FeatureConfiguration.class).executeRaw(
					"CREATE TABLE featureConfiguration(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer," + "disease varchar(255),"
						+ "featureType varchar(255)," + "endDate timestamp," + "UNIQUE(snapshot, uuid));");
			case 173:
				currentVersion = 173;
				getDao(AggregateReport.class).executeRaw(
					"CREATE TABLE aggregateReport(" + "id integer primary key autoincrement," + "uuid varchar(36) not null,"
						+ "changeDate timestamp not null," + "creationDate timestamp not null," + "lastOpenedDate timestamp,"
						+ "localChangeDate timestamp not null," + "modified integer," + "snapshot integer," + "disease varchar(255),"
						+ "reportingUser_id bigint REFERENCES users(id)," + "region_id bigint REFERENCES region(id),"
						+ "district_id bigint REFERENCES district(id)," + "healthFacility_id bigint REFERENCES facility(id),"
						+ "pointOfEntry_id bigint REFERENCES pointOfEntry(id)," + "year integer," + "epiWeek integer," + "newCases integer,"
						+ "labConfirmations integer," + "deaths integer," + "UNIQUE(snapshot, uuid));");
			case 174:
				currentVersion = 174;
				getDao(DiseaseConfiguration.class).executeRaw("ALTER TABLE diseaseConfiguration ADD COLUMN caseBased boolean;");
			case 175:
				currentVersion = 175;
				getDao(Region.class).executeRaw("ALTER TABLE region ADD COLUMN archived SMALLINT DEFAULT 0;");
				getDao(District.class).executeRaw("ALTER TABLE district ADD COLUMN archived SMALLINT DEFAULT 0;");
				getDao(Community.class).executeRaw("ALTER TABLE community ADD COLUMN archived SMALLINT DEFAULT 0;");
				getDao(Facility.class).executeRaw("ALTER TABLE facility ADD COLUMN archived SMALLINT DEFAULT 0;");
				getDao(PointOfEntry.class).executeRaw("ALTER TABLE pointOfEntry ADD COLUMN archived SMALLINT DEFAULT 0;");
			case 176:
				currentVersion = 176;
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN fluidInLungCavityAuscultation varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN fluidInLungCavityXray varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN abnormalLungXrayFindings varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN conjunctivalInjection varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN acuteRespiratoryDistressSyndrome varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN pneumoniaClinicalOrRadiologic varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN visitedHealthFacility varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN contactWithSourceRespiratoryCase varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN visitedAnimalMarket varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN camels varchar(255);");
				getDao(EpiData.class).executeRaw("ALTER TABLE epidata ADD COLUMN snakes varchar(255);");
				getDao(HealthConditions.class).executeRaw("ALTER TABLE healthConditions ADD COLUMN immunodeficiencyOtherThanHiv varchar(255);");
				getDao(HealthConditions.class)
					.executeRaw("ALTER TABLE healthConditions ADD COLUMN cardiovascularDiseaseIncludingHypertension varchar(255);");
			case 177:
				currentVersion = 177;
				getDao(Case.class).executeRaw("UPDATE cases SET disease = 'NEW_INFLUENZA' WHERE disease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE events SET disease = 'NEW_INFLUENZA' WHERE disease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE outbreak SET disease = 'NEW_INFLUENZA' WHERE disease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE person SET causeOfDeathDisease = 'NEW_INFLUENZA' WHERE causeOfDeathDisease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE visits SET disease = 'NEW_INFLUENZA' WHERE disease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE weeklyreportentry SET disease = 'NEW_INFLUENZA' WHERE disease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE clinicalVisit SET disease = 'NEW_INFLUENZA' WHERE disease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE diseaseConfiguration SET disease = 'NEW_INFLUENZA' WHERE disease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE featureConfiguration SET disease = 'NEW_INFLUENZA' WHERE disease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE pathogenTest SET testedDisease = 'NEW_INFLUENZA' WHERE testedDisease = 'NEW_INFLUENCA';");
				getDao(Case.class).executeRaw("UPDATE users SET limitedDisease = 'NEW_INFLUENZA' WHERE limitedDisease = 'NEW_INFLUENCA';");
			case 178:
				currentVersion = 178;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN completeness float;");
			case 179:
				currentVersion = 179;
				getDao(User.class).executeRaw("ALTER TABLE users ADD COLUMN language varchar(255);");
			case 180:
				currentVersion = 180;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN additionalDetails varchar(512);");
			case 181:
				currentVersion = 181;
				getDao(Location.class).executeRaw("ALTER TABLE location ADD COLUMN postalCode varchar(255);");
			case 182:
				currentVersion = 182;
				getDao(FeatureConfiguration.class).executeRaw("ALTER TABLE featureConfiguration ADD COLUMN enabled SMALLINT DEFAULT 0;");
			case 183:
				currentVersion = 183;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN externalID varchar(255);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN externalID varchar(255);");
			case 184:
				currentVersion = 184;
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN region_id bigint REFERENCES region(id);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN district_id bigint REFERENCES district(id);");
			case 185:
				currentVersion = 185;
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN highPriority SMALLINT DEFAULT 0;");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN immunosuppressiveTherapyBasicDisease varchar(255);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN immunosuppressiveTherapyBasicDiseaseDetails varchar(512);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN careForPeopleOver60 varchar(255);");
			case 186:
				currentVersion = 186;
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN generalPractitionerDetails varchar(512);");
			case 187:
				currentVersion = 187;
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantine varchar(255);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineFrom timestamp;");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineTo timestamp;");
			case 188:
				currentVersion = 188;
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN diseaseDetails varchar(512);");
				getDao(Contact.class).executeRaw("UPDATE contacts SET caseDisease = (SELECT disease FROM cases WHERE uuid = contacts.caseUuid);");
				getDao(Contact.class)
					.executeRaw("UPDATE contacts SET diseaseDetails = (SELECT diseaseDetails FROM cases WHERE uuid = contacts.caseUuid);");
			case 189:
				currentVersion = 189;
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN caseIdExternalSystem varchar(255);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN caseOrEventInformation varchar(255);");
			case 190:
				currentVersion = 190;
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN emailAddress varchar(255);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN passportNumber varchar(255);");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN nationalHealthId varchar(255);");
			case 191:
				currentVersion = 191;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantine varchar(255);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineFrom timestamp;");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineTo timestamp;");
			case 192:
				currentVersion = 192;
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN contactCategory varchar(255);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN contactProximityDetails varchar(512);");
			case 193:
				currentVersion = 193;
				// Fill report date if necessary; required because the report date was not a mandatory field before
				getDao(Contact.class).executeRaw("UPDATE contacts SET reportDateTime = creationDate WHERE reportDateTime IS NULL;");
			case 194:
				currentVersion = 194;
				getDao(Contact.class).executeRaw("UPDATE contacts SET followUpUntil = quarantineTo WHERE quarantineTo > followUpUntil;");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineOrderMeans varchar(255);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineHelpNeeded varchar(512);");
			case 195:
				currentVersion = 195;
				getDao(Hospitalization.class).executeRaw("ALTER TABLE hospitalizations ADD COLUMN intensiveCareUnit varchar(255);");
				getDao(Hospitalization.class).executeRaw("ALTER TABLE hospitalizations ADD COLUMN intensiveCareUnitStart timestamp;");
				getDao(Hospitalization.class).executeRaw("ALTER TABLE hospitalizations ADD COLUMN intensiveCareUnitEnd timestamp");
				getDao(Hospitalization.class).executeRaw("UPDATE hospitalizations SET intensiveCareUnit = 'YES' WHERE accommodation = 'ICU';");
			case 196:
				currentVersion = 196;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineHelpNeeded varchar(512);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineOrderedVerbally SMALLINT DEFAULT 0;");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineOrderedOfficialDocument SMALLINT DEFAULT 0;");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineOrderedVerballyDate timestamp;");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineOrderedOfficialDocumentDate timestamp;");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineHomePossible varchar(255);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineHomePossibleComment varchar(512);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineHomeSupplyEnsured varchar(255);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineHomeSupplyEnsuredComment varchar(512);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineOrderedVerbally SMALLINT DEFAULT 0;");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineOrderedOfficialDocument SMALLINT DEFAULT 0;");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineOrderedVerballyDate timestamp;");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineOrderedOfficialDocumentDate timestamp;");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineHomePossible varchar(255);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineHomePossibleComment varchar(512);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineHomeSupplyEnsured varchar(255);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineHomeSupplyEnsuredComment varchar(512);");
				getDao(Contact.class)
					.executeRaw("UPDATE contacts SET quarantineOrderedVerbally = CASE WHEN quarantineOrderMeans = 'VERBALLY' THEN 1 ELSE 0 END;");
				getDao(Contact.class).executeRaw(
					"UPDATE contacts SET quarantineOrderedOfficialDocument = CASE WHEN quarantineOrderMeans = 'OFFICIAL_DOCUMENT' THEN 1 ELSE 0 END;");
			case 197:
				currentVersion = 197;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN reportingType varchar(255);");
			case 198:
				currentVersion = 198;
				getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN fieldSampleID varchar(512);");
			case 199:
				currentVersion = 199;
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lossOfTaste varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lossOfSmell varchar(255);");
			case 200:
				currentVersion = 200;
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN coughWithSputum varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN coughWithHeamoptysis varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN lymphadenopathy varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN wheezing varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN skinUlcers varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN inabilityToWalk varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN inDrawingOfChestWall varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN otherComplications varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN otherComplicationsText varchar(255);");

				getDao(Symptoms.class).executeRaw("ALTER TABLE healthconditions ADD COLUMN obesity varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE healthconditions ADD COLUMN currentSmoker varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE healthconditions ADD COLUMN formerSmoker varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE healthconditions ADD COLUMN asthma varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE healthconditions ADD COLUMN sickleCellDisease varchar(255);");
			case 201:
				currentVersion = 201;
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN additionalDetails varchar(512);");
			case 202:
				currentVersion = 202;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN postpartum varchar(255);");
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN trimester varchar(255);");
			case 203:
				currentVersion = 203;
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN respiratoryDiseaseVentilation varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN generalSignsOfDisease varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN fastHeartRate varchar(255);");
				getDao(Symptoms.class).executeRaw("ALTER TABLE symptoms ADD COLUMN oxygenSaturationLower94 varchar(255);");
				getDao(HealthConditions.class).executeRaw("ALTER TABLE healthConditions ADD COLUMN immunodeficiencyIncludingHiv varchar(255);");
			case 204:
				currentVersion = 204;
				getDao(Sample.class).executeRaw("ALTER TABLE samples ADD COLUMN associatedContact_id bigint REFERENCES contact (id);");
			case 205:
				currentVersion = 205;
				getDao(Contact.class).executeRaw("UPDATE contacts SET contactProximity = 'MEDICAL_SAFE' WHERE contactProximity = 'MEDICAL_SAVE';");
				getDao(Contact.class)
					.executeRaw("UPDATE contacts SET contactProximity = 'MEDICAL_UNSAFE' WHERE contactProximity = 'MEDICAL_UNSAVE';");
			case 206:
				currentVersion = 206;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN pseudonymized boolean;");
				getDao(Person.class).executeRaw("ALTER TABLE person ADD COLUMN pseudonymized boolean;");
				getDao(Location.class).executeRaw("ALTER TABLE location ADD COLUMN pseudonymized boolean;");
			case 207:
				currentVersion = 207;
				getDao(Case.class).executeRaw("ALTER TABLE cases ADD COLUMN quarantineTypeDetails varchar(512);");
				getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN quarantineTypeDetails varchar(512);");
			case 208:
				currentVersion = 208;

				getDao(Event.class).executeRaw("UPDATE events set eventstatus='SIGNAL' where eventstatus='POSSIBLE'");
				getDao(Event.class).executeRaw("UPDATE events set eventstatus='EVENT' where eventstatus='CONFIRMED'");
				getDao(Event.class).executeRaw("UPDATE events set eventstatus='DROPPED' where eventstatus='NO_EVENT'");

				Cursor dbCursor = db.query(Event.TABLE_NAME, null, null, null, null, null, null);
				String[] columnNames = dbCursor.getColumnNames();
				dbCursor.close();

				String queryColumns = TextUtils.join(",", columnNames);

				getDao(Event.class).executeRaw("ALTER TABLE events RENAME TO tmp_events;");
				TableUtils.createTable(connectionSource, Event.class);

				db.execSQL("INSERT INTO events (" + queryColumns.replace("eventDate", "startDate") + ") SELECT (" + queryColumns + ") FROM tmp_events");
				db.execSQL("DROP TABLE tmp_events;");

				getDao(Event.class).executeRaw("UPDATE events set srcType='HOTLINE_PERSON' where length(ifnull(srcFirstName,'')||ifnull(srcLastName,'')||ifnull(srcTelNo,'')||ifnull(srcEmail,'')) > 0;");

				// ATTENTION: break should only be done after last version
				break;
			default:
				throw new IllegalStateException("onUpgrade() with unknown oldVersion " + oldVersion);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Database upgrade failed for version " + currentVersion + ": " + ex.getMessage(), ex);
		}
	}

	private void upgradeFromUnupgradableVersion(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Case.class, true);
			TableUtils.dropTable(connectionSource, Prescription.class, true);
			TableUtils.dropTable(connectionSource, Treatment.class, true);
			TableUtils.dropTable(connectionSource, Therapy.class, true);
			TableUtils.dropTable(connectionSource, ClinicalVisit.class, true);
			TableUtils.dropTable(connectionSource, ClinicalCourse.class, true);
			TableUtils.dropTable(connectionSource, HealthConditions.class, true);
			TableUtils.dropTable(connectionSource, MaternalHistory.class, true);
			TableUtils.dropTable(connectionSource, PortHealthInfo.class, true);
			TableUtils.dropTable(connectionSource, Person.class, true);
			TableUtils.dropTable(connectionSource, Location.class, true);
			TableUtils.dropTable(connectionSource, Region.class, true);
			TableUtils.dropTable(connectionSource, District.class, true);
			TableUtils.dropTable(connectionSource, Community.class, true);
			TableUtils.dropTable(connectionSource, Facility.class, true);
			TableUtils.dropTable(connectionSource, PointOfEntry.class, true);
			TableUtils.dropTable(connectionSource, User.class, true);
			TableUtils.dropTable(connectionSource, Symptoms.class, true);
			TableUtils.dropTable(connectionSource, Task.class, true);
			TableUtils.dropTable(connectionSource, Contact.class, true);
			TableUtils.dropTable(connectionSource, Visit.class, true);
			TableUtils.dropTable(connectionSource, Event.class, true);
			TableUtils.dropTable(connectionSource, Sample.class, true);
			TableUtils.dropTable(connectionSource, PathogenTest.class, true);
			TableUtils.dropTable(connectionSource, AdditionalTest.class, true);
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
			TableUtils.dropTable(connectionSource, AggregateReport.class, true);
			TableUtils.dropTable(connectionSource, Outbreak.class, true);
			TableUtils.dropTable(connectionSource, DiseaseClassificationCriteria.class, true);
			TableUtils.dropTable(connectionSource, DiseaseConfiguration.class, true);
			TableUtils.dropTable(connectionSource, FeatureConfiguration.class, true);

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
				} else if (type.equals(Therapy.class)) {
					dao = (AbstractAdoDao<ADO>) new TherapyDao((Dao<Therapy, Long>) innerDao);
				} else if (type.equals(Prescription.class)) {
					dao = (AbstractAdoDao<ADO>) new PrescriptionDao((Dao<Prescription, Long>) innerDao);
				} else if (type.equals(Treatment.class)) {
					dao = (AbstractAdoDao<ADO>) new TreatmentDao((Dao<Treatment, Long>) innerDao);
				} else if (type.equals(Person.class)) {
					dao = (AbstractAdoDao<ADO>) new PersonDao((Dao<Person, Long>) innerDao);
				} else if (type.equals(Location.class)) {
					dao = (AbstractAdoDao<ADO>) new LocationDao((Dao<Location, Long>) innerDao);
				} else if (type.equals(PointOfEntry.class)) {
					dao = (AbstractAdoDao<ADO>) new PointOfEntryDao((Dao<PointOfEntry, Long>) innerDao);
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
				} else if (type.equals(DiseaseConfiguration.class)) {
					dao = (AbstractAdoDao<ADO>) new DiseaseConfigurationDao((Dao<DiseaseConfiguration, Long>) innerDao);
				} else if (type.equals(FeatureConfiguration.class)) {
					dao = (AbstractAdoDao<ADO>) new FeatureConfigurationDao((Dao<FeatureConfiguration, Long>) innerDao);
				} else if (type.equals(Symptoms.class)) {
					dao = (AbstractAdoDao<ADO>) new SymptomsDao((Dao<Symptoms, Long>) innerDao);
				} else if (type.equals(HealthConditions.class)) {
					dao = (AbstractAdoDao<ADO>) new HealthConditionsDao((Dao<HealthConditions, Long>) innerDao);
				} else if (type.equals(ClinicalCourse.class)) {
					dao = (AbstractAdoDao<ADO>) new ClinicalCourseDao((Dao<ClinicalCourse, Long>) innerDao);
				} else if (type.equals(ClinicalVisit.class)) {
					dao = (AbstractAdoDao<ADO>) new ClinicalVisitDao((Dao<ClinicalVisit, Long>) innerDao);
				} else if (type.equals(MaternalHistory.class)) {
					dao = (AbstractAdoDao<ADO>) new MaternalHistoryDao((Dao<MaternalHistory, Long>) innerDao);
				} else if (type.equals(PortHealthInfo.class)) {
					dao = (AbstractAdoDao<ADO>) new PortHealthInfoDao((Dao<PortHealthInfo, Long>) innerDao);
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
				} else if (type.equals(PathogenTest.class)) {
					dao = (AbstractAdoDao<ADO>) new PathogenTestDao((Dao<PathogenTest, Long>) innerDao);
				} else if (type.equals(AdditionalTest.class)) {
					dao = (AbstractAdoDao<ADO>) new AdditionalTestDao((Dao<AdditionalTest, Long>) innerDao);
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
				} else if (type.equals(AggregateReport.class)) {
					dao = (AbstractAdoDao<ADO>) new AggregateReportDao((Dao<AggregateReport, Long>) innerDao);
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

	public static TherapyDao getTherapyDao() {
		return (TherapyDao) getAdoDao(Therapy.class);
	}

	public static PrescriptionDao getPrescriptionDao() {
		return (PrescriptionDao) getAdoDao(Prescription.class);
	}

	public static TreatmentDao getTreatmentDao() {
		return (TreatmentDao) getAdoDao(Treatment.class);
	}

	public static ClinicalCourseDao getClinicalCourseDao() {
		return (ClinicalCourseDao) getAdoDao(ClinicalCourse.class);
	}

	public static HealthConditionsDao getHealthConditionsDao() {
		return (HealthConditionsDao) getAdoDao(HealthConditions.class);
	}

	public static ClinicalVisitDao getClinicalVisitDao() {
		return (ClinicalVisitDao) getAdoDao(ClinicalVisit.class);
	}

	public static MaternalHistoryDao getMaternalHistoryDao() {
		return (MaternalHistoryDao) getAdoDao(MaternalHistory.class);
	}

	public static PortHealthInfoDao getPortHealthInfoDao() {
		return (PortHealthInfoDao) getAdoDao(PortHealthInfo.class);
	}

	public static PersonDao getPersonDao() {
		return (PersonDao) getAdoDao(Person.class);
	}

	public static LocationDao getLocationDao() {
		return (LocationDao) getAdoDao(Location.class);
	}

	public static PointOfEntryDao getPointOfEntryDao() {
		return (PointOfEntryDao) getAdoDao(PointOfEntry.class);
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

	public static DiseaseConfigurationDao getDiseaseConfigurationDao() {
		return (DiseaseConfigurationDao) getAdoDao(DiseaseConfiguration.class);
	}

	public static FeatureConfigurationDao getFeatureConfigurationDao() {
		return (FeatureConfigurationDao) getAdoDao(FeatureConfiguration.class);
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

	public static PathogenTestDao getSampleTestDao() {
		return (PathogenTestDao) getAdoDao(PathogenTest.class);
	}

	public static AdditionalTestDao getAdditionalTestDao() {
		return (AdditionalTestDao) getAdoDao(AdditionalTest.class);
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

	public static AggregateReportDao getAggregateReportDao() {
		return (AggregateReportDao) getAdoDao(AggregateReport.class);
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

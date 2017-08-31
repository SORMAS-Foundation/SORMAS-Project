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
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.CommunityDao;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.DistrictDao;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.region.RegionDao;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleDao;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.backend.sample.SampleTestDao;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.backend.symptoms.SymptomsDao;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.backend.task.TaskDao;
import de.symeda.sormas.app.backend.synclog.SyncLog;
import de.symeda.sormas.app.backend.synclog.SyncLogDao;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDao;
import de.symeda.sormas.app.backend.visit.Visit;
import de.symeda.sormas.app.backend.visit.VisitDao;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 * @see <a href="http://stackoverflow.com/questions/17529766/view-contents-of-database-file-in-android-studio">Viewing databases from Android Studio</a>
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "sormas.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 102;

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
			TableUtils.clearTable(connectionSource, Location.class);
			TableUtils.clearTable(connectionSource, EpiData.class);
			TableUtils.clearTable(connectionSource, EpiDataBurial.class);
			TableUtils.clearTable(connectionSource, EpiDataGathering.class);
			TableUtils.clearTable(connectionSource, EpiDataTravel.class);
			TableUtils.clearTable(connectionSource, SyncLog.class);

			if (clearInfrastructure) {
				TableUtils.clearTable(connectionSource, Region.class);
				TableUtils.clearTable(connectionSource, District.class);
				TableUtils.clearTable(connectionSource, Community.class);
				TableUtils.clearTable(connectionSource, Facility.class);
				TableUtils.clearTable(connectionSource, User.class);

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
					// nothing
				case 100:
					currentVersion = 100;
					getDao(Contact.class).executeRaw("ALTER TABLE contacts ADD COLUMN followUpComment varchar(512);");
				case 101:
					currentVersion = 101;
					getDao(Facility.class).executeRaw("UPDATE facility SET name = 'Other health facility' WHERE uuid = 'SORMAS-CONSTID-OTHERS-FACILITY'");
					getDao(Facility.class).executeRaw("UPDATE facility SET name = 'Not a health facility' WHERE uuid = 'SORMAS-CONSTID-ISNONE-FACILITY'");

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

}

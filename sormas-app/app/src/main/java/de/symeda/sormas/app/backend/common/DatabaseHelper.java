package de.symeda.sormas.app.backend.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataBurialDao;
import de.symeda.sormas.app.backend.epidata.EpiDataDao;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataGatheringDao;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.backend.epidata.EpiDataTravelDao;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.HospitalizationDao;
import de.symeda.sormas.app.backend.config.Config;
import de.symeda.sormas.app.backend.config.ConfigDao;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.contact.ContactDao;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.event.EventDao;
import de.symeda.sormas.app.backend.event.EventParticipant;
import de.symeda.sormas.app.backend.event.EventParticipantDao;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDao;
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
	private static final int DATABASE_VERSION = 73;

	private static DatabaseHelper instance = null;
	public static void init(Context context) {
		if (instance != null) {
			Log.e(DatabaseHelper.class.getName(),"DatabaseHelper has already been initalized");
		}
		instance = new DatabaseHelper(context);
	}

	private boolean clearingTables = false;

	private ConfigDao configDao = null;

	private PersonDao personDao = null;

	private CaseDao caseDao = null;
	private LocationDao locationDao = null;
	private FacilityDao facilityDao = null;
	private RegionDao regionDao = null;
	private DistrictDao districtDao = null;
	private CommunityDao communityDao = null;
	private UserDao userDao = null;
	private SymptomsDao symptomsDao = null;
	private TaskDao taskDao = null;
	private ContactDao contactDao = null;
	private VisitDao visitDao;
	private EventDao eventDao;
	private SampleDao sampleDao;
	private SampleTestDao sampleTestDao;
	private EventParticipantDao eventParticipantDao;
	private HospitalizationDao hospitalizationDao;
	private PreviousHospitalizationDao previousHospitalizationDao;
	private EpiDataDao epiDataDao;
	private EpiDataBurialDao epiDataBurialDao;
	private EpiDataGatheringDao epiDataGatheringDao;
	private EpiDataTravelDao epiDataTravelDao;

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);//, R.raw.ormlite_config);
		// HACK to make sure database is initialized - otherwise we could run into problems caused by threads
		this.getReadableDatabase();
	}

	public static void clearTables(boolean clearInfrastructure) {
		if (instance.clearingTables) {
			return;
		}
		instance.clearingTables = true;

		ConnectionSource connectionSource = getCaseDao().getConnectionSource();
		try {
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

			if (clearInfrastructure) {
				TableUtils.clearTable(connectionSource, Region.class);
				TableUtils.clearTable(connectionSource, District.class);
				TableUtils.clearTable(connectionSource, Community.class);
				TableUtils.clearTable(connectionSource, Facility.class);
				TableUtils.clearTable(connectionSource, User.class);
				ConfigProvider.setUser(null); // important - otherwise the old instance is further used
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
	 * This is called when the database is first created. Usually you should call createTable statements here to create
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
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
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
			if (oldVersion < 30) {
				TableUtils.dropTable(connectionSource, Config.class, true);
			}
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public static ConfigDao getConfigDao() {
		if (instance.configDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.configDao == null) {
					try {
						instance.configDao = new ConfigDao((Dao<Config, String>) instance.getDao(Config.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create ConfigDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.configDao;
	}

	public static CaseDao getCaseDao() {
		if (instance.caseDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.caseDao == null) {
					try {
						instance.caseDao = new CaseDao((Dao<Case, Long>) instance.getDao(Case.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create CaseDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.caseDao;
	}

	public static PersonDao getPersonDao() {
		if (instance.personDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.personDao == null) {
					try {
						instance.personDao = new PersonDao((Dao<Person, Long>) instance.getDao(Person.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create PersonDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.personDao;
	}

	public static LocationDao getLocationDao() {
		if (instance.locationDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.locationDao == null) {
					try {
						instance.locationDao = new LocationDao((Dao<Location, Long>) instance.getDao(Location.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create PersonDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.locationDao;
	}

	public static FacilityDao getFacilityDao() {
		if (instance.facilityDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.facilityDao == null) {
					try {
						instance.facilityDao = new FacilityDao((Dao<Facility, Long>) instance.getDao(Facility.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create FacilityDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.facilityDao;
	}

	public static RegionDao getRegionDao() {
		if (instance.regionDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.regionDao == null) {
					try {
						instance.regionDao = new RegionDao((Dao<Region, Long>) instance.getDao(Region.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create RegionDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.regionDao;
	}

	public static DistrictDao getDistrictDao() {
		if (instance.districtDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.districtDao == null) {
					try {
						instance.districtDao = new DistrictDao((Dao<District, Long>) instance.getDao(District.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create DistrictDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.districtDao;
	}

	public static CommunityDao getCommunityDao() {
		if (instance.communityDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.communityDao == null) {
					try {
						instance.communityDao = new CommunityDao((Dao<Community, Long>) instance.getDao(Community.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create CommunityDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.communityDao;
	}

	public static UserDao getUserDao() {
		if (instance.userDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.userDao == null) {
					try {
						instance.userDao = new UserDao((Dao<User, Long>) instance.getDao(User.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create UserDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.userDao;
	}

	public static SymptomsDao getSymptomsDao() {
		if (instance.symptomsDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.symptomsDao == null) {
					try {
						instance.symptomsDao = new SymptomsDao((Dao<Symptoms, Long>) instance.getDao(Symptoms.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create SymptomsDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.symptomsDao;
	}

	public static TaskDao getTaskDao() {
		if (instance.taskDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.taskDao == null) {
					try {
						instance.taskDao = new TaskDao((Dao<Task, Long>) instance.getDao(Task.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create TaskDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.taskDao;
	}

	public static ContactDao getContactDao() {
		if (instance.contactDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.contactDao == null) {
					try {
						instance.contactDao = new ContactDao((Dao<Contact, Long>) instance.getDao(Contact.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create ContactDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.contactDao;
	}

	public static VisitDao getVisitDao() {
		if (instance.visitDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.visitDao == null) {
					try {
						instance.visitDao = new VisitDao((Dao<Visit, Long>) instance.getDao(Visit.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create VisitDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.visitDao;
	}

	public static EventDao getEventDao() {
		if (instance.eventDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.eventDao == null) {
					try {
						instance.eventDao = new EventDao((Dao<Event, Long>) instance.getDao(Event.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create EventDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.eventDao;
	}

	public static SampleDao getSampleDao() {
		if (instance.sampleDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.sampleDao == null) {
					try {
						instance.sampleDao = new SampleDao((Dao<Sample, Long>) instance.getDao(Sample.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create SampleDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.sampleDao;
	}
	public static EventParticipantDao getEventParticipantDao() {
		if (instance.eventParticipantDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.eventParticipantDao == null) {
					try {
						instance.eventParticipantDao = new EventParticipantDao((Dao<EventParticipant, Long>) instance.getDao(EventParticipant.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create EventParticipantDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.eventParticipantDao;
	}

	public static SampleTestDao getSampleTestDao() {
		if (instance.sampleTestDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.sampleTestDao == null) {
					try {
						instance.sampleTestDao = new SampleTestDao((Dao<SampleTest, Long>) instance.getDao(SampleTest.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create SampleTestDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.sampleTestDao;
	}

	public static HospitalizationDao getHospitalizationDao() {
		if (instance.hospitalizationDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.hospitalizationDao == null) {
					try  {
						instance.hospitalizationDao = new HospitalizationDao((Dao<Hospitalization, Long>) instance.getDao(Hospitalization.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create HospitalizationDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.hospitalizationDao;
	}

	public static PreviousHospitalizationDao getPreviousHospitalizationDao() {
		if (instance.previousHospitalizationDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.previousHospitalizationDao == null) {
					try  {
						instance.previousHospitalizationDao = new PreviousHospitalizationDao((Dao<PreviousHospitalization, Long>) instance.getDao(PreviousHospitalization.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create PreviousHospitalizationDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.previousHospitalizationDao;
	}

	public static EpiDataDao getEpiDataDao() {
		if (instance.epiDataDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.epiDataDao == null) {
					try {
						instance.epiDataDao = new EpiDataDao((Dao<EpiData, Long>) instance.getDao(EpiData.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create EpiDataDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.epiDataDao;
	}

	public static EpiDataBurialDao getEpiDataBurialDao() {
		if (instance.epiDataBurialDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.epiDataBurialDao == null) {
					try {
						instance.epiDataBurialDao = new EpiDataBurialDao((Dao<EpiDataBurial, Long>) instance.getDao(EpiDataBurial.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create EpiDataBurialDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.epiDataBurialDao;
	}

	public static EpiDataGatheringDao getEpiDataGatheringDao() {
		if (instance.epiDataGatheringDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.epiDataGatheringDao == null) {
					try {
						instance.epiDataGatheringDao = new EpiDataGatheringDao((Dao<EpiDataGathering, Long>) instance.getDao(EpiDataGathering.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create EpiDataGatheringDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.epiDataGatheringDao;
	}

	public static EpiDataTravelDao getEpiDataTravelDao() {
		if (instance.epiDataTravelDao == null) {
			synchronized (DatabaseHelper.class) {
				if (instance.epiDataTravelDao == null) {
					try {
						instance.epiDataTravelDao = new EpiDataTravelDao((Dao<EpiDataTravel, Long>) instance.getDao(EpiDataTravel.class));
					} catch (SQLException e) {
						Log.e(DatabaseHelper.class.getName(), "Can't create EpiDataTravelDao", e);
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance.epiDataTravelDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		caseDao = null;
		personDao = null;
		facilityDao = null;
		regionDao = null;
		districtDao = null;
		communityDao = null;
		userDao = null;
		symptomsDao = null;
		taskDao = null;
		contactDao = null;
		visitDao = null;
		eventDao = null;
		sampleDao = null;
		sampleTestDao = null;
		eventParticipantDao = null;
		hospitalizationDao = null;
		previousHospitalizationDao= null;
		epiDataDao = null;
		epiDataBurialDao = null;
		epiDataGatheringDao = null;
		epiDataTravelDao = null;
	}
}

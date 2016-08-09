package de.symeda.sormas.app.backend.common;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.facility.FacilityDao;
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
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDao;
import de.symeda.sormas.app.backend.user.UserRoleToUser;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 * @see <a href="http://stackoverflow.com/questions/17529766/view-contents-of-database-file-in-android-studio">Viewing databases from Android Studio</a>
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "sormas.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 17;

	public static DatabaseHelper instance = null;

	public static void init(Context context) {
		if (instance != null) {
			logger.error("DatabaseHelper has already been initalized");
		}
		instance = new DatabaseHelper(context);
	}

	private PersonDao personDao = null;
	private CaseDao caseDao = null;
	private LocationDao locationDao = null;
	private FacilityDao facilityDao = null;
	private RegionDao regionDao = null;
	private DistrictDao districtDao = null;
	private CommunityDao communityDao = null;
	private UserDao userDao = null;

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);//, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Location.class);
			TableUtils.createTable(connectionSource, Region.class);
			TableUtils.createTable(connectionSource, District.class);
			TableUtils.createTable(connectionSource, Community.class);
			TableUtils.createTable(connectionSource, Facility.class);
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, UserRoleToUser.class);
			TableUtils.createTable(connectionSource, Person.class);
			TableUtils.createTable(connectionSource, Case.class);
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
			TableUtils.dropTable(connectionSource, UserRoleToUser.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
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
	}
}

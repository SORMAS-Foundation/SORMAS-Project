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
import de.symeda.sormas.app.backend.person.Person;
import de.symeda.sormas.app.backend.person.PersonDao;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 * @see <a href="http://stackoverflow.com/questions/17529766/view-contents-of-database-file-in-android-studio">Viewing databases from Android Studio</a>
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "sormas.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 6;

	public static DatabaseHelper instance = null;

	public static void init(Context context) {
		if (instance != null) {
			logger.error("DatabaseHelper has already been initalized");
		}
		instance = new DatabaseHelper(context);
	}

	private PersonDao personDao = null;
	private CaseDao caseDao = null;

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
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public static CaseDao getCaseDao() {
		if (instance.caseDao == null) {
			try {
				instance.caseDao = new CaseDao((Dao<Case, Long>) instance.getDao(Case.class));
			} catch (SQLException e) {
				Log.e(DatabaseHelper.class.getName(), "Can't create CaseDao", e);
				throw new RuntimeException(e);
			}
		}
		return instance.caseDao;
	}

	public static PersonDao getPersonDao() {
		if (instance.personDao == null) {
			try {
				instance.personDao = new PersonDao((Dao<Person, Long>) instance.getDao(Person.class));
			} catch (SQLException e) {
				Log.e(DatabaseHelper.class.getName(), "Can't create PersonDao", e);
				throw new RuntimeException(e);
			}
		}
		return instance.personDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		caseDao = null;
		personDao = null;
	}
}

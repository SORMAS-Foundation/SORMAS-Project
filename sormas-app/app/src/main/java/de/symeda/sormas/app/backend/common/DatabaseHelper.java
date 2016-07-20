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
import de.symeda.sormas.app.backend.person.Person;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 * @see <a href="http://stackoverflow.com/questions/17529766/view-contents-of-database-file-in-android-studio">Viewing databases from Android Studio</a>
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "sormas.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 2;

	// the DAO object we use to access the SimpleData table
	private Dao<Case, Integer> caseDao = null;
	private RuntimeExceptionDao<Case, Integer> simpleCaseDao = null;
	private RuntimeExceptionDao<Person, Integer> simplePersonDao = null;

	public DatabaseHelper(Context context) {
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
//
//		// here we try inserting data in the on-create as a test
//		RuntimeExceptionDao<Case, Integer> dao = getSimpleDataDao();
//		long millis = System.currentTimeMillis();
//		// create some entries in the onCreate
//		SimpleData simple = new SimpleData(millis);
//		dao.create(simple);
//		simple = new SimpleData(millis + 1);
//		dao.create(simple);
//		Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);
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

	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Case, Integer> getCaseDao() throws SQLException {
		if (caseDao == null) {
			caseDao = getDao(Case.class);
		}
		return caseDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Case, Integer> getSimpleCaseDao() {
		if (simpleCaseDao == null) {
			simpleCaseDao = getRuntimeExceptionDao(Case.class);
		}
		return simpleCaseDao;
	}

	public RuntimeExceptionDao<Person, Integer> getSimplePersonDao() {
		if (simplePersonDao == null) {
			simplePersonDao = getRuntimeExceptionDao(Person.class);
		}
		return simplePersonDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		caseDao = null;
		simpleCaseDao = null;
		simplePersonDao = null;
	}
}

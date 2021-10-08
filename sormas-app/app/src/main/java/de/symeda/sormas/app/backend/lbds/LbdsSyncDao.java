package de.symeda.sormas.app.backend.lbds;

import java.sql.Date;
import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import android.util.Log;

import de.symeda.sormas.app.backend.common.AbstractDomainObject;

public class LbdsSyncDao {

	private Dao<LbdsSync, String> dao;

	public LbdsSyncDao(Dao<LbdsSync, String> innerDao) throws SQLException {
		this.dao = innerDao;
	}

	public void logLbdsSend(String uuid) {
		LbdsSync lbdsSync = queryForId(uuid);
		if (lbdsSync == null) {
			lbdsSync = new LbdsSync(uuid);
		}
		lbdsSync.setLastSendDate(new Date(System.currentTimeMillis()));
		createOrUpdate(lbdsSync);
	}

	public void logLbdsReceive(String uuid) {
		LbdsSync lbdsSync = queryForId(uuid);
		if (lbdsSync == null) {
			lbdsSync = new LbdsSync(uuid);
		}
		lbdsSync.setLastReceivedDate(new Date(System.currentTimeMillis()));
		createOrUpdate(lbdsSync);
	}

	public boolean hasBeenSuccessfullySent(AbstractDomainObject ado) {
		LbdsSync lbdsSync = queryForId(ado.getUuid());
		return lbdsSync != null && lbdsSync.getLastReceivedDate() != null;
	}

	/**
	 * @see Dao#queryForId(Object)
	 */
	public LbdsSync queryForId(String id) {
		try {
			return dao.queryForId(id);
		} catch (SQLException e) {
			Log.e(getClass().getName(), "queryForId threw exception on: " + id, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#createOrUpdate(Object)
	 */
	public Dao.CreateOrUpdateStatus createOrUpdate(LbdsSync data) {
		try {
			return dao.createOrUpdate(data);
		} catch (SQLException e) {
			Log.e(getClass().getName(), "createOrUpdate threw exception on: " + data, e);
			throw new RuntimeException(e);
		}
	}
}

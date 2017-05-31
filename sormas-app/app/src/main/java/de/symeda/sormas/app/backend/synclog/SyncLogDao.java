package de.symeda.sormas.app.backend.synclog;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Mate Strysewske on 24.05.2017.
 */
public class SyncLogDao extends RuntimeExceptionDao<SyncLog, Long> {

    public SyncLogDao(Dao<SyncLog, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    public SyncLog create(String entityName, String conflictText) {
        SyncLog syncLog = new SyncLog(entityName, conflictText);
        syncLog.setCreationDate(new Date());
        super.create(syncLog);

        return syncLog;
    }

    public List<SyncLog> queryForAll(String orderBy, boolean ascending) {
        try {
            QueryBuilder builder = queryBuilder();
            return builder.orderBy(orderBy, ascending).query();
        } catch (SQLException | IllegalArgumentException e) {
            Log.e(getClass().getName(), "Could not perform queryForAll");
            throw new RuntimeException();
        }
    }

}

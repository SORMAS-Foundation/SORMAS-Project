package de.symeda.sormas.app.backend.synclog;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Some methods are copied from {@link com.j256.ormlite.dao.RuntimeExceptionDao}.
 *
 * Created by Mate Strysewske on 24.05.2017.
 */
public class SyncLogDao {

    private Dao<SyncLog, Long> dao;

    public SyncLogDao(Dao<SyncLog, Long> innerDao) throws SQLException {
        this.dao = innerDao;
    }

    public SyncLog create(String entityName, String conflictText) {
        SyncLog syncLog = new SyncLog(entityName, conflictText);
        syncLog.setCreationDate(new Date());
        create(syncLog);

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

    /**
     * @see Dao#create(Object)
     */
    public int create(SyncLog data) {
        try {
            return dao.create(data);
        } catch (SQLException e) {
            Log.e(getClass().getName(), "create threw exception on: " + data, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @see Dao#queryBuilder()
     */
    public QueryBuilder<SyncLog, Long> queryBuilder() {
        return dao.queryBuilder();
    }

}

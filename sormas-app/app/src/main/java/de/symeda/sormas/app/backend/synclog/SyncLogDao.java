package de.symeda.sormas.app.backend.synclog;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Mate Strysewske on 24.05.2017.
 */
public class SyncLogDao extends RuntimeExceptionDao<SyncLog, Long> {

    public SyncLogDao(Dao<SyncLog, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    public SyncLog create(String entityName, String entityUuid, String conflictText) {
        SyncLog syncLog = new SyncLog(entityName, entityUuid, conflictText);
        syncLog.setCreationDate(new Date());
        super.create(syncLog);

        return syncLog;
    }

}

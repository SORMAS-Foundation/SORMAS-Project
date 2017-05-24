package de.symeda.sormas.app.backend.user;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Mate Strysewske on 24.05.2017.
 */
public class SyncLogDao extends AbstractAdoDao<SyncLog> {

    public SyncLogDao(Dao<SyncLog, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<SyncLog> getAdoClass() {
        return SyncLog.class;
    }

    @Override
    public String getTableName() {
        return SyncLog.TABLE_NAME;
    }

    public SyncLog create(String entityName, String entityUuid, String conflictText) {
        SyncLog syncLog = super.create();
        syncLog.setEntityName(entityName);
        syncLog.setEntityUuid(entityUuid);
        syncLog.setConflictText(conflictText);

        return syncLog;
    }

}

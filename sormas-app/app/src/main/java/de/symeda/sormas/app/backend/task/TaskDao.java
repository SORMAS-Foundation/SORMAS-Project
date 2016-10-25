package de.symeda.sormas.app.backend.task;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public class TaskDao extends AbstractAdoDao<Task> {

    public TaskDao(Dao<Task,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Task.TABLE_NAME;
    }

    @Override
    public boolean saveUnmodified(Task task) {
        return super.saveUnmodified(task);
    }

    public void markAsModified(String uuid) {
        Task task = queryUuid(uuid);
        save(task);
    }


}

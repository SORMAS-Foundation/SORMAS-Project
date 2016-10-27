package de.symeda.sormas.app.backend.task;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Date;

import de.symeda.sormas.api.task.TaskStatus;
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

    public void changeTaskStatus(Task task, TaskStatus targetStatus) {
        task.setTaskStatus(targetStatus);
        task.setStatusChangeDate(new Date());
        save(task);
    }


}

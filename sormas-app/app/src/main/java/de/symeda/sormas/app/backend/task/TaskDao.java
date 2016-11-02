package de.symeda.sormas.app.backend.task;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Stefan Szczesny on 24.10.2016.
 */
public class TaskDao extends AbstractAdoDao<Task> {

    private static final Log.Level LOG_LEVEL = Log.Level.DEBUG;
    private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionDao.class);

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

    /**
     * Gets all pending tasks.
     * @return
     */
    public List<Task> queryPending() {
        try {
            return queryBuilder().orderBy(Task.PRIORITY, true).orderBy(Task.DUE_DATE, false).where().eq(Task.TASK_STATUS, TaskStatus.PENDING).query();
        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryFinished threw exception");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all done and discarded tasks.
     * @return
     */
    public List<Task> queryFinished() {
        try {
            return queryBuilder().orderBy(Task.DUE_DATE, false).where().eq(Task.TASK_STATUS, TaskStatus.DONE).or().eq(Task.TASK_STATUS, TaskStatus.DISCARDED).query();
        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryFinished threw exception");
            throw new RuntimeException(e);
        }
    }


}

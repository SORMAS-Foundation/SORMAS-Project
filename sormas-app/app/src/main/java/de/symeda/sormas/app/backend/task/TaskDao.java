package de.symeda.sormas.app.backend.task;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
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
     * Ordered by priority, then due date - oldest (most due) first
     * @return
     */
    public List<Task> queryPending() {
        try {
            return queryBuilder().orderBy(Task.PRIORITY, true).orderBy(Task.DUE_DATE, true).where().eq(Task.TASK_STATUS, TaskStatus.PENDING).query();
        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryPending threw exception");
            throw new RuntimeException(e);
        }
    }

    /**
     * query for the following PENDING tasks:
     * 1. suggested start within range
     * 2. due date within range
     * 3. localChangeDate within range, not modified (-> has been updated on server) and suggested start before end of range
     * Ordered by priority, then due date - oldes (most due) first
     * @return
     */
    public List<Task> queryPendingForNotification(Date rangeStart, Date rangeEnd) {
        try {

            QueryBuilder builder = queryBuilder();

            Where where = builder.where();
            where.and(
                where.eq(Task.TASK_STATUS, TaskStatus.PENDING),
                where.or(
                    where.between(Task.SUGGESTED_START, rangeStart, rangeEnd),
                    where.between(Task.DUE_DATE, rangeStart, rangeEnd),
                    where.and(
                        where.between(Task.LOCAL_CHANGE_DATE, rangeStart, rangeEnd),
                        where.eq(Task.MODIFIED, false),
                        where.le(Task.SUGGESTED_START, rangeEnd)
                    )
                )
            );

            builder.orderBy(Task.PRIORITY, true).orderBy(Task.DUE_DATE, true);

            String test = builder.prepareStatementString();
            return builder.query();

        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryPendingForNotification threw exception");
            throw new RuntimeException(e);
        }
    }


    /**
     * Gets all not executable tasks.
     * Ordered by due date - newest first
     * @return
     */
    public List<Task> queryNotExecutable() {
        try {
            return queryBuilder().orderBy(Task.DUE_DATE, false).where().eq(Task.TASK_STATUS, TaskStatus.NOT_EXECUTABLE).query();
        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryPending threw exception");
            throw new RuntimeException(e);
        }
    }
    /**
     * Gets all done and discarded tasks.
     * Ordered by due date - newest first
     * @return
     */
    public List<Task> queryDoneOrDiscarded() {
        try {
            return queryBuilder().orderBy(Task.DUE_DATE, false).where().eq(Task.TASK_STATUS, TaskStatus.DONE).or().eq(Task.TASK_STATUS, TaskStatus.DISCARDED).query();
        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryDoneOrDiscarded threw exception");
            throw new RuntimeException(e);
        }
    }


}

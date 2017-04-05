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
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.caze.CaseDao;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;

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

    public void changeTaskStatus(Task task, TaskStatus targetStatus) {
        task.setTaskStatus(targetStatus);
        task.setStatusChangeDate(new Date());
        save(task);
    }

    /**
     * query for the following PENDING tasks:
     * 1. suggested start within range
     * 2. due date within range
     * 3. localChangeDate within range, not modified (-> has been updated on server) and suggested start before end of range
     * Ordered by priority, then due date - oldes (most due) first
     * @return
     */
    public List<Task> queryMyPendingForNotification(Date rangeStart, Date rangeEnd) {
        try {

            QueryBuilder builder = queryBuilder();

            Where where = builder.where();
            where.and(
                where.eq(Task.ASSIGNEE_USER + "_id", ConfigProvider.getUser()),
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
            logger.log(LOG_LEVEL, e, "queryMyPendingForNotification threw exception");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all pending tasks.
     * Ordered by priority, then due date - oldest (most due) first
     * @return
     */
    public List<Task> queryMyPending() {
        try {

            QueryBuilder builder = queryBuilder();

            Where where = builder.where();
            where.and(
                    where.eq(Task.ASSIGNEE_USER + "_id", ConfigProvider.getUser()),
                    where.eq(Task.TASK_STATUS, TaskStatus.PENDING));

            return builder
                    .orderBy(Task.PRIORITY, true).orderBy(Task.DUE_DATE, true)
                    .query();
        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryMyPending threw exception");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all not executable tasks.
     * Ordered by due date - newest first
     * @return
     */
    public List<Task> queryMyNotExecutable() {
        try {
            QueryBuilder builder = queryBuilder();

            Where where = builder.where();
            where.and(
                    where.eq(Task.ASSIGNEE_USER + "_id", ConfigProvider.getUser()),
                    where.eq(Task.TASK_STATUS, TaskStatus.NOT_EXECUTABLE));

            return builder
                    .orderBy(Task.PRIORITY, true).orderBy(Task.DUE_DATE, true)
                    .query();
        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryMyPending threw exception");
            throw new RuntimeException(e);
        }
    }
    /**
     * Gets all done and discarded tasks.
     * Ordered by due date - newest first
     * @return
     */
    public List<Task> queryMyDoneOrRemoved() {
        try {
            QueryBuilder builder = queryBuilder();

            Where where = builder.where();
            where.and(
                    where.eq(Task.ASSIGNEE_USER + "_id", ConfigProvider.getUser()),
                    where.eq(Task.TASK_STATUS, TaskStatus.DONE).or().eq(Task.TASK_STATUS, TaskStatus.REMOVED));

            return builder
                    .orderBy(Task.PRIORITY, true).orderBy(Task.DUE_DATE, true)
                    .query();
        } catch (SQLException e) {
            logger.log(LOG_LEVEL, e, "queryMyDoneOrRemoved threw exception");
            throw new RuntimeException(e);
        }
    }

    public List<Task> queryForCase(Case caze) {
        try {
            return queryBuilder().orderBy(Task.PRIORITY, true).orderBy(Task.DUE_DATE, true).where().eq(Task.CAZE+"_id", caze).query();
        } catch(SQLException e) {
            logger.log(LOG_LEVEL, e, "queryForCase threw exception");
            throw new RuntimeException(e);
        }
    }

    public List<Task> queryForContact(Contact contact) {
        try {
            return queryBuilder().orderBy(Task.PRIORITY, true).orderBy(Task.DUE_DATE, true).where().eq(Task.CONTACT+"_id", contact).query();
        } catch(SQLException e) {
            logger.log(LOG_LEVEL, e, "queryForContact threw exception");
            throw new RuntimeException(e);
        }
    }

    public List<Task> queryForEvent(Event event) {
        try {
            return queryBuilder().orderBy(Task.PRIORITY, true).orderBy(Task.DUE_DATE, true).where().eq(Task.EVENT+"_id", event).query();
        } catch(SQLException e) {
            logger.log(LOG_LEVEL, e, "queryForEvent threw exception");
            throw new RuntimeException(e);
        }
    }

}

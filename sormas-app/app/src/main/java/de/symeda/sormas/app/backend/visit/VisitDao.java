package de.symeda.sormas.app.backend.visit;

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

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.task.Task;
import kotlin.NotImplementedError;

public class VisitDao extends AbstractAdoDao<Visit> {

    private static final Log.Level LOG_LEVEL = Log.Level.DEBUG;
    private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionDao.class);

    public VisitDao(Dao<Visit,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Visit.TABLE_NAME;
    }

    public List<Visit> getByContact(Contact contact) {
        try {

            QueryBuilder qb = queryBuilder();
            Where where = qb.where();

            where.and(
                    where.eq(Visit.PERSON+"_id", contact.getPerson()),
                    where.eq(Visit.DISEASE, contact.getCaze().getDisease())
            );

            Date lowerLimit = (contact.getLastContactDate() != null && contact.getLastContactDate().before(contact.getReportDateTime()))? contact.getLastContactDate() : contact.getReportDateTime();
            if(lowerLimit!=null) {
                where.and();
                where.gt(Visit.VISIT_DATE_TIME, lowerLimit);
            }

            Date upperLimit = contact.getFollowUpUntil();
            if(upperLimit!=null) {
                where.and();
                where.lt(Visit.VISIT_DATE_TIME, upperLimit);
            }

            qb.orderBy(Visit.VISIT_DATE_TIME, true);

            return qb.query();
        } catch(SQLException e) {
            logger.log(LOG_LEVEL, e, "getByContact threw exception");
            throw new RuntimeException(e);
        }
    }

}

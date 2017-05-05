package de.symeda.sormas.app.backend.event;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.util.DataUtils;

public class EventParticipantDao extends AbstractAdoDao<EventParticipant> {

    private static final Log.Level LOG_LEVEL = Log.Level.DEBUG;
    private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionDao.class);

    public EventParticipantDao(Dao<EventParticipant,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return EventParticipant.TABLE_NAME;
    }

    public List<EventParticipant> getByEvent(Event event) throws SQLException {
        QueryBuilder qb = queryBuilder();
        Where where = qb.where();
        where.eq(EventParticipant.EVENT+"_id", event);
//        qb.orderBy(EventParticipant.PERSON, true);
        return qb.query();
    }

    public EventParticipant getNewEventParticipant() throws IllegalAccessException, InstantiationException {
        EventParticipant eventParticipant = DataUtils.createNew(EventParticipant.class);

        //Person person = DataUtils.createNew(Person.class);
        //eventParticipant.setPerson(person);

        return eventParticipant;
    }

}

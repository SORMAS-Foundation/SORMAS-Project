package de.symeda.sormas.app.backend.event;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.util.DataUtils;

public class EventParticipantDao extends AbstractAdoDao<EventParticipant> {

    public EventParticipantDao(Dao<EventParticipant,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<EventParticipant> getAdoClass() {
        return EventParticipant.class;
    }

    @Override
    public String getTableName() {
        return EventParticipant.TABLE_NAME;
    }

    public List<EventParticipant> getByEvent(Event event) {
        try {
            QueryBuilder qb = queryBuilder();
            Where where = qb.where();
            where.eq(EventParticipant.EVENT + "_id", event);
//        qb.orderBy(EventParticipant.PERSON, true);
            return qb.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByEvent on EventParticipant");
            throw new RuntimeException(e);
        }
    }
}

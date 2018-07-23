package de.symeda.sormas.app.backend.event;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
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

        if (event.isSnapshot()) {
            throw new IllegalArgumentException("Does not support snapshot entities");
        }

        try {
            return queryBuilder()
                .where().eq(EventParticipant.EVENT + "_id", event)
                .and().eq(AbstractDomainObject.SNAPSHOT, false)
                .query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByEvent on EventParticipant");
            throw new RuntimeException(e);
        }
    }

    // TODO #704
//    @Override
//    public void markAsRead(EventParticipant eventParticipant) {
//        super.markAsRead(eventParticipant);
//        DatabaseHelper.getPersonDao().markAsRead(eventParticipant.getPerson());
//    }
}

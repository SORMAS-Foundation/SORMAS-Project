package de.symeda.sormas.app.backend.event;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.contact.Contact;

public class EventDao extends AbstractAdoDao<Event> {

    public EventDao(Dao<Event,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Event.TABLE_NAME;
    }

}

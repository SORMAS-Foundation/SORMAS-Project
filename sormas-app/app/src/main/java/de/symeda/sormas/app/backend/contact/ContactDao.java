package de.symeda.sormas.app.backend.contact;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;

/**
 * Created by Stefan Szczesny on 29.11.2016.
 */
public class ContactDao extends AbstractAdoDao<Contact> {

    public ContactDao(Dao<Contact,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    public String getTableName() {
        return Contact.TABLE_NAME;
    }


}

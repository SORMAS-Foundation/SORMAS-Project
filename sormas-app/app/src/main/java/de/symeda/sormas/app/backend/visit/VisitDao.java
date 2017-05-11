package de.symeda.sormas.app.backend.visit;

import android.support.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.util.DataUtils;
//import kotlin.NotImplementedError;

public class VisitDao extends AbstractAdoDao<Visit> {

    public VisitDao(Dao<Visit,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<Visit> getAdoClass() {
        return Visit.class;
    }

    @Override
    public String getTableName() {
        return Visit.TABLE_NAME;
    }

    @Override
    public boolean saveUnmodified(Visit visit) throws DaoException {

        // saving unmodified also includes cascading sub entities
        if (visit.getSymptoms() != null) {
            DatabaseHelper.getSymptomsDao().saveUnmodified(visit.getSymptoms());
        }

        return super.saveUnmodified(visit);
    }

    public List<Visit> getByContact(Contact contact) {
        try {
            QueryBuilder qb = queryBuilder();
            Where where = qb.where();
            where.and(
                    where.eq(Visit.PERSON + "_id", contact.getPerson()),
                    where.eq(Visit.DISEASE, contact.getCaze().getDisease())
            );
            // see sormas-backend/VisitService.getAllByContact()
            Date lowerLimit = contact.getLastContactDate() != null ? DateHelper.subtractDays(contact.getLastContactDate(), 10) : contact.getReportDateTime();
            if (lowerLimit != null) {
                where.and();
                where.gt(Visit.VISIT_DATE_TIME, lowerLimit);
            }

            Date upperLimit = DateHelper.addDays(contact.getFollowUpUntil(), 10);
            if (upperLimit != null) {
                where.and();
                where.lt(Visit.VISIT_DATE_TIME, upperLimit);
            }

            qb.orderBy(Visit.VISIT_DATE_TIME, true);

            return qb.query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getByContact on Visit");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Visit create() {
        throw new UnsupportedOperationException();
    }

    /**
     * new visit is assigned to the contact's person and has the same disease as the contact's case
     * @param contactUuid
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @NonNull
    public Visit create(String contactUuid) {
        Contact contact = DatabaseHelper.getContactDao().queryUuid(contactUuid);
        Visit visit = super.create();
        visit.setPerson(contact.getPerson());
        visit.setDisease(contact.getCaze().getDisease());
        visit.setVisitDateTime(new Date());
        return visit;
    }

}

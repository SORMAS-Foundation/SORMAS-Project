package de.symeda.sormas.app.backend.common;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.NonUniqueResultException;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public abstract class AbstractAdoDao<ADO extends AbstractDomainObject> extends RuntimeExceptionDao<ADO, Long> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAdoDao.class);

    public AbstractAdoDao(Dao<ADO, Long> innerDao)  {
        super(innerDao);
    }

    public ADO queryUuid(String uuid) {
        List<ADO> results = queryForEq(AbstractDomainObject.UUID, uuid);
        if (results.size() == 0) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            throw new NonUniqueResultException("Found multiple results for uuid: " + uuid);
        }
    }

    public abstract String getTableName();

    public Date getLatestChangeDate() {
        // TODO rollback comment, when
        String query = "SELECT MAX(" + AbstractDomainObject.CHANGE_DATE + ") FROM " + getTableName()
                ;//+ " WHERE " + AbstractDomainObject.MODIFIED + " = ?";
        GenericRawResults<Object[]> maxChangeDateResult = queryRaw(query, new DataType[]{DataType.DATE_LONG});//, "0");
        try {
            List<Object[]> dateResults = maxChangeDateResult.getResults();
            if (dateResults.size() > 0) {
                return (Date)dateResults.get(0)[0];
            }
        } catch (SQLException e) {
            logger.error(e, "getLatestChangeDateLong threw exception on: " + query);
            throw new RuntimeException(e);
        }
        return null;
    }

    public void save(ADO ado) {
        ado.setModified(true);
        createOrUpdate(ado);
    }
}

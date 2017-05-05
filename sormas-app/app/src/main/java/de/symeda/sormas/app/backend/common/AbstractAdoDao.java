package de.symeda.sormas.app.backend.common;

import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.persistence.NonUniqueResultException;

import de.symeda.sormas.api.ReferenceDto;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 *
 * Would be better to not extend RuntimeExceptionDao but ORMLite doesn't provide an alternative
 */
public abstract class AbstractAdoDao<ADO extends AbstractDomainObject> extends RuntimeExceptionDao<ADO, Long> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAdoDao.class);

    public AbstractAdoDao(Dao<ADO, Long> innerDao)  {
        super(innerDao);
    }

    public ADO queryUuid(String uuid) throws DaoException {
        try {
            List<ADO> results = queryForEq(AbstractDomainObject.UUID, uuid);
            if (results.size() == 0) {
                return null;
            } else if (results.size() == 1) {
                return results.get(0);
            } else {
                Log.e(getTableName(), "Found multiple results for UUID: " + uuid);
                throw new NonUniqueResultException("Found multiple results for UUID: " + uuid);
            }
        } catch (RuntimeException e) {
            throw new DaoException(e);
        }
    }

    public List<ADO> queryForEq(String fieldName, Object value, String orderBy, boolean ascending) throws SQLException, IllegalArgumentException {
        QueryBuilder builder = queryBuilder();
        Where where = builder.where();
        where.eq(fieldName, value);
        return builder.orderBy(orderBy, ascending).query();
    }

    public List<ADO> queryForAll(String orderBy, boolean ascending) throws SQLException, IllegalArgumentException {
        QueryBuilder builder = queryBuilder();
        return builder.orderBy(orderBy, ascending).query();
    }

    public ADO getByReferenceDto(ReferenceDto dto) throws DaoException {
        if (dto == null) {
            return null;
        }

        ADO ado = queryUuid(dto.getUuid());
        return ado;
    }

    public abstract String getTableName();

    public Date getLatestChangeDate() throws DaoException, SQLException {
        // TODO rollback comment, when
        String query = "SELECT MAX(" + AbstractDomainObject.CHANGE_DATE + ") FROM " + getTableName()
                ;//+ " WHERE " + AbstractDomainObject.MODIFIED + " = ?";
        try {
            GenericRawResults<Object[]> maxChangeDateResult = queryRaw(query, new DataType[]{DataType.DATE_LONG});//, "0");
            List<Object[]> dateResults = maxChangeDateResult.getResults();
            if (dateResults.size() > 0) {
                return (Date) dateResults.get(0)[0];
            }
        } catch (RuntimeException e) {
            throw new DaoException(e);
        }
        return null;
    }

    public boolean save(ADO ado) throws DaoException {
        ado.setModified(true);

        try {
            int result;
            if (ado.getId() == null) {
                result = create(ado);
            } else {
                result = update(ado);
            }
            if (result != 1) {
                // #139 check if we couldn't save because the server sent a new version in between
                // TODO replace with a proper merge mechanism
                ADO existing = queryForId(ado.getId());
                if (existing.getLocalChangeDate().after(ado.getLocalChangeDate())) {
                    return false;
                }
                throw new DaoException(getTableName() + ": Could not create or update entity - see log for additional details: " + ado);
            }
            return true;
        } catch (RuntimeException e) {
            throw new DaoException(e);
        }
    }

    public boolean saveUnmodified(ADO ado) throws DaoException {
        try {
            int result;
            if (ado.getId() == null) {
                result = create(ado);
            } else {
                result = update(ado);
            }
            if (result != 1) {
                Log.e(getTableName(), "Could not create or update entity: " + ado);
                return false;
            }
            return true;
        } catch (RuntimeException e) {
            throw new DaoException(e);
        }
    }
}

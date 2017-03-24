package de.symeda.sormas.app.backend.common;

import android.util.Log;

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

import javax.persistence.NonUniqueResultException;

import de.symeda.sormas.api.ReferenceDto;

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

    public List<ADO> queryForEq(String fieldName, Object value, String orderBy, boolean ascending) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.eq(fieldName, value);
            return builder.orderBy(orderBy, ascending).query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform queryForEq");
            throw new RuntimeException(e);
        }
    }

    public List<ADO> queryForAll(String orderBy, boolean ascending) {
        try {
            QueryBuilder builder = queryBuilder();
            return builder.orderBy(orderBy, ascending).query();
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform queryForAll");
            throw new RuntimeException(e);
        }
    }

    public ADO getByReferenceDto(ReferenceDto dto) {
        if (dto == null) {
            return null;
        }

        ADO ado = queryUuid(dto.getUuid());
        return ado;
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

    public boolean save(ADO ado) {
        ado.setModified(true);

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
            throw new RuntimeException(getTableName() +  ": Could not create or update entity - see log for additional details: " + ado);
        }
        return true;
    }

    public boolean saveUnmodified(ADO ado) {
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
    }
}

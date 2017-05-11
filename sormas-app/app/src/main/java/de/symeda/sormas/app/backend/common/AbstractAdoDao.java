package de.symeda.sormas.app.backend.common;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public abstract class AbstractAdoDao<ADO extends AbstractDomainObject> extends RuntimeExceptionDao<ADO, Long> {

    public AbstractAdoDao(Dao<ADO, Long> innerDao)  {
        super(innerDao);
    }

    protected abstract Class<ADO> getAdoClass();

    /**
     * Use queryUnmodifiedUuid if you want to retrieve the unmodified version in any case.
     * @param uuid
     * @return The modified version of the entity (if exists) or else the unmodified
     */
    public ADO queryUuid(String uuid) {
        List<ADO> results = queryForEq(AbstractDomainObject.UUID, uuid);
        if (results.size() == 0) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else if (results.size() == 2) {
            if (results.get(0).isModified()) {
                if (results.get(1).isModified()) {
                    throw new NonUniqueResultException("Found multiple modified results for uuid: " + uuid);
                }
                return results.get(0);
            } else {
                if (!results.get(1).isModified()) {
                    throw new NonUniqueResultException("Found multiple unmodified results for uuid: " + uuid);
                }
                return results.get(1);
            }
        } else {
            Log.e(getTableName(), "Found multiple results for UUID: " + uuid);
            throw new NonUniqueResultException("Found multiple results for UUID: " + uuid);
        }
    }

    public ADO queryUnmodifiedUuid(String uuid) {

        try {
            List<ADO> results = queryBuilder().where().eq(AbstractDomainObject.UUID, uuid).and().eq(AbstractDomainObject.MODIFIED, false).query();
            if (results.size() == 0) {
                return null;
            } else if (results.size() == 1) {
                return results.get(0);
            } else {
                throw new NonUniqueResultException("Found multiple results for uuid: " + uuid);
            }
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform queryForEq");
            throw new RuntimeException(e);
        }
    }

    public List<ADO> queryForEq(String fieldName, Object value, String orderBy, boolean ascending) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.eq(fieldName, value);
            return builder.orderBy(orderBy, ascending).query();
        } catch (SQLException | IllegalArgumentException e) {
            Log.e(getTableName(), "Could not perform queryForEq");
            throw new RuntimeException(e);
        }
    }

    public List<ADO> queryForAll(String orderBy, boolean ascending) {
        try {
            QueryBuilder builder = queryBuilder();
            return builder.orderBy(orderBy, ascending).query();
        } catch (SQLException | IllegalArgumentException e) {
            Log.e(getTableName(), "Could not perform queryForAll");
            throw new RuntimeException();
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
                return (Date) dateResults.get(0)[0];
            }
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform getLatestChangeDate");
            throw new RuntimeException();
        }
        return null;
    }

    public boolean save(ADO ado) throws DaoException {

        try {

            int result;
            if (ado.getId() == null) {
                // create the new entity and take not that it has been create in the app (modified)
                ado.setModified(true);
                result = create(ado);
            }
            else {
                if (ado.isModified()) {
                    // just update the existing modified version
                    result = update(ado);
                } else {
                    // we need to create a clone of the unmodified version, so we can use it for comparison when merging
                    ADO unmodifiedAdo = queryForId(ado.getId());
                    unmodifiedAdo.setId(null);

                    // take note that we are now working with a modified entity
                    ado.setModified(true);
                    result = update(ado);

                    // now save the clone
                    create(unmodifiedAdo);
                }
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

    public ADO create() {
        try {
            ADO ado = getAdoClass().newInstance();
            ado.setUuid(DataHelper.createUuid());
            Date now = new Date();
            ado.setCreationDate(now);
            ado.setChangeDate(now);
            return ado;
        } catch (InstantiationException e) {
            Log.e(DataUtils.class.getName(), "Could not perform createNew");
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            Log.e(DataUtils.class.getName(), "Could not perform createNew");
            throw new RuntimeException(e);
        }
    }
}

package de.symeda.sormas.app.backend.common;

import android.util.Log;

import com.googlecode.openbeans.BeanInfo;
import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
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
     * Use queryClonedOriginalUuid if you want to retrieve the unmodified version in any case.
     * @param uuid
     * @return The modified version of the entity (if exists) or else the unmodified
     */
    public ADO queryUuid(String uuid) {

        try {

            List<ADO> results = queryBuilder().where().eq(AbstractDomainObject.UUID, uuid)
                    .and().eq(AbstractDomainObject.CLONED_ORIGINAL, false).query();
        if (results.size() == 0) {
            return null;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            Log.e(getTableName(), "Found multiple results for UUID: " + uuid);
            throw new NonUniqueResultException("Found multiple results for UUID: " + uuid);
        }
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform queryUuid");
            throw new RuntimeException(e);
        }
    }

    public ADO queryClonedOriginalUuid(String uuid) {

        try {
            List<ADO> results = queryBuilder().where().eq(AbstractDomainObject.UUID, uuid)
                    .and().eq(AbstractDomainObject.CLONED_ORIGINAL, true).query();
            if (results.size() == 0) {
                return null;
            } else if (results.size() == 1) {
                return results.get(0);
            } else {
                throw new NonUniqueResultException("Found multiple results for uuid: " + uuid);
            }
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform queryClonedOriginalUuid");
            throw new RuntimeException(e);
        }
    }

    public List<ADO> queryForEq(String fieldName, Object value, String orderBy, boolean ascending) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.eq(fieldName, value);
            where.and().eq(AbstractDomainObject.CLONED_ORIGINAL, false).query();
            return builder.orderBy(orderBy, ascending).query();
        } catch (SQLException | IllegalArgumentException e) {
            Log.e(getTableName(), "Could not perform queryForEq");
            throw new RuntimeException(e);
        }
    }

    public List<ADO> queryForAll(String orderBy, boolean ascending) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.and().eq(AbstractDomainObject.CLONED_ORIGINAL, false).query();
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

        String query = "SELECT MAX(" + AbstractDomainObject.CHANGE_DATE + ") FROM " + getTableName();
        GenericRawResults<Object[]> maxChangeDateResult = queryRaw(query, new DataType[]{DataType.DATE_LONG});
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

    protected Date getLatestChangeDateJoin(String joinTableName, String joinColumnName) {

        String query = "SELECT MAX(jo." + AbstractDomainObject.CHANGE_DATE + ") FROM " + getTableName() + " AS ta" +
                " LEFT JOIN " + joinTableName + " AS jo ON jo." + AbstractDomainObject.ID + " = ta." + joinColumnName + "_ID";
        GenericRawResults<Object[]> maxChangeDateResult = queryRaw(query, new DataType[]{DataType.DATE_LONG});
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

    private AbstractDomainObject saveWithCast(AbstractDomainObject ado) throws DaoException {
        return save((ADO)ado);
    }

    /**
     * @param ado
     * @return the clonedOriginal if relevant
     * @throws DaoException
     */
    public ADO save(ADO ado) throws DaoException {

        if (ado.isClonedOriginal()) {
            throw new DaoException("Can't save a cloned original");
        }

        try {
            ADO clonedOriginal;
            boolean cloneNeeded = ado.getId() != null && !ado.isModified();
            if (cloneNeeded) {
                // we need to create a clone of the unmodified version, so we can use it for comparison when merging
                clonedOriginal = queryForId(ado.getId());
                clonedOriginal.setId(null);
                clonedOriginal.setClonedOriginal(true);
            } else {
                clonedOriginal = null;
            }

            // go through all embedded entities and save them
            Iterator<PropertyDescriptor> propertyIterator = AdoMergeHelper.getEmbeddedAdoProperties(ado.getClass());
            while (propertyIterator.hasNext()) {
                PropertyDescriptor property = propertyIterator.next();

                // get the embedded entity
                AbstractDomainObject embeddedAdo = (AbstractDomainObject)property.getReadMethod().invoke(ado);
                // save it - might return a created clone
                AbstractDomainObject clonedEmbeddedAdo = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).saveWithCast(embeddedAdo);

                if (cloneNeeded) {
                    if (clonedEmbeddedAdo == null) {
                        throw new IllegalArgumentException("No clone was created for " + embeddedAdo);
                    }
                    // write link for clone of embedded entity
                    property.getWriteMethod().invoke(clonedOriginal, clonedEmbeddedAdo);
                } else {
                    if (clonedEmbeddedAdo != null) {
                        throw new IllegalArgumentException("An unexpected clone was created for " + embeddedAdo);
                    }

                }
            }

            if (ado.getId() == null) {
                // create the new entity and take note that it has been create in the app (modified)
                ado.setModified(true);
                create(ado);
            }
            else {
                if (ado.isModified()) {

                    // just update the existing modified version
                    update(ado);
                } else {

                    // set to modified and update
                    // note: if doesn't matter whether the entity was really changed or not
                    ado.setModified(true);
                    update(ado);

                    // now really create the clone
                    create(clonedOriginal);
                }
            }

            return clonedOriginal;

        } catch (RuntimeException e) {
            throw new DaoException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private AbstractDomainObject mergeOrCreateWithCast(AbstractDomainObject ado) throws DaoException {
        return mergeOrCreate((ADO)ado);
    }

    public ADO mergeOrCreate(ADO source) throws DaoException {

        if (source.getId() != null) {
            throw new IllegalArgumentException("Merged ado is not allowed to have an id");
        }

        ADO result = queryUuid(source.getUuid());
        ADO original;
        if (result == null) {
            result = source;
            original = null;
        } else {
            if (result.isModified()) {
                original = queryClonedOriginalUuid(source.getUuid());
                // use change date from server
                original.setChangeDate(source.getChangeDate());
            } else {
                original = null;
            }
            // use change date from server
            result.setChangeDate(source.getChangeDate());
        }

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(source.getClass());
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }

        try {

            List<PropertyDescriptor> collectionProperties = null;

            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                // ignore some types and specific properties
                if (AbstractDomainObject.CREATION_DATE.equals(property.getName())
                        || AbstractDomainObject.CHANGE_DATE.equals(property.getName())
                        || AbstractDomainObject.LOCAL_CHANGE_DATE.equals(property.getName())
                        || AbstractDomainObject.UUID.equals(property.getName())
                        || AbstractDomainObject.ID.equals(property.getName())
                        || AbstractDomainObject.CLONED_ORIGINAL.equals(property.getName())
                        || AbstractDomainObject.MODIFIED.equals(property.getName())
                        || property.getWriteMethod() == null
                        || property.getReadMethod() == null)
                    continue;

                // we now have to write the value from source into target and base
                // there are four types of properties:

                // 1. embedded domain objects like a Location or Symptoms
                // -> call merge for the object
                if (property.getPropertyType().isAnnotationPresent(EmbeddedAdo.class)) {

                    // get the embedded entity
                    AbstractDomainObject embeddedSource = (AbstractDomainObject) property.getReadMethod().invoke(source);
                    // merge it - will return the merged result
                    AbstractDomainObject embeddedResult = DatabaseHelper.getAdoDao(embeddedSource.getClass()).mergeOrCreateWithCast(embeddedSource);

                    if (embeddedResult == null) {
                        throw new IllegalArgumentException("No merge result returned for was created for " + embeddedSource);
                    }
                    // write link for merged embedded
                    property.getWriteMethod().invoke(result, embeddedResult);
                }
                // 2. "value" types like String, Date, Enum, ...
                // -> just copy value from source into target and base
                // 3. reference domain objects like a reference to a Person or a District
                // -> just copy reference value from source into target and base
                else if (DataHelper.isValueType(property.getPropertyType())
                        || AbstractDomainObject.class.isAssignableFrom(property.getPropertyType())) {

                    Object sourceFieldValue = property.getReadMethod().invoke(source);

                    if (result.isModified()) {
                        // did the server send changes?
                        Object baseFieldValue = property.getReadMethod().invoke(original);
                        if (DataHelper.equal(baseFieldValue, sourceFieldValue)) {
                            continue;
                        }

                        // do we have local changes?
                        Object targetFieldValue = property.getReadMethod().invoke(result);
                        if (!DataHelper.equal(baseFieldValue, targetFieldValue)) {
                            // we have a conflict
                            Log.i(beanInfo.getBeanDescriptor().getName(), "Overriding " + property.getName() +
                                    " value changed from '" + DataHelper.toStringNullable(baseFieldValue) +
                                    "' to '" + DataHelper.toStringNullable(targetFieldValue) +
                                    "' with '" + DataHelper.toStringNullable(sourceFieldValue) + "'");
                        }

                        property.getWriteMethod().invoke(original, sourceFieldValue);
                    }

                    property.getWriteMethod().invoke(result, sourceFieldValue);
                }
                // 4. lists of embedded domain objects
                // -> TODO
                else if (Collection.class.isAssignableFrom(property.getPropertyType())) {

                    // merging lists is done after entity is saved
                    if (collectionProperties == null) {
                        collectionProperties = new ArrayList<>();
                    }
                    collectionProperties.add(property);
                }
                else {
                    // Other objects are not supported
                    throw new UnsupportedOperationException(property.getPropertyType().getName() + " is not supported as a property type.");
                }
            }

            if (result.getId() == null) {
                create(result);
            } else {
                update(result);
            }

            if (collectionProperties != null) {
                // TODO
//                for (PropertyDescriptor property : collectionProperties) {
//
//                    mergeAdoList(beanInfo, property,
//                            (Collection<AbstractDomainObject>) baseFieldValue,
//                            (Collection<AbstractDomainObject>) sourceFieldValue,
//                            (Collection<AbstractDomainObject>) targetFieldValue);
//                }
            }

            return result;

        } catch (RuntimeException e) {
            throw new DaoException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void acceptWithCast(AbstractDomainObject ado) throws DaoException {
        accept((ADO)ado);
    }

    /**
     * @param ado
     * @return the clonedOriginal if relevant
     * @throws DaoException
     */
    public void accept(ADO ado) throws DaoException {

        if (ado.isClonedOriginal()) {
            throw new DaoException("Can't save a cloned original");
        }
        if (!ado.isModified()) {
            throw new DaoException("Can't accept an unmodified entity");
        }

        try {

            // go through all embedded entities and accept them
            Iterator<PropertyDescriptor> propertyIterator = AdoMergeHelper.getEmbeddedAdoProperties(ado.getClass());
            while (propertyIterator.hasNext()) {
                PropertyDescriptor property = propertyIterator.next();

                // get the embedded entity
                AbstractDomainObject embeddedAdo = (AbstractDomainObject)property.getReadMethod().invoke(ado);
                // accept it
                DatabaseHelper.getAdoDao(embeddedAdo.getClass()).acceptWithCast(embeddedAdo);
            }

            ADO clonedOriginal = queryClonedOriginalUuid(ado.getUuid());
            if (clonedOriginal != null) {
                delete(clonedOriginal);
            }

            ado.setModified(false);
            update(ado);

        } catch (RuntimeException e) {
            throw new DaoException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public ADO create() {
        try {
            ADO ado = getAdoClass().newInstance();
            ado.setUuid(DataHelper.createUuid());
            Date now = new Date();
            ado.setCreationDate(now);
            ado.setChangeDate(now);

            // TODO create all embedded entities

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

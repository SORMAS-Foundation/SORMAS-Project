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
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.NonUniqueResultException;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public abstract class AbstractAdoDao<ADO extends AbstractDomainObject> extends RuntimeExceptionDao<ADO, Long> {

    public AbstractAdoDao(Dao<ADO, Long> innerDao)  {
        super(innerDao);
    }

    protected abstract Class<ADO> getAdoClass();

    /**
     * Use querySnapshotByUuid if you want to retrieve the unmodified version in any case.
     * @param uuid
     * @return The modified version of the entity (if exists) or else the unmodified
     */
    public ADO queryUuid(String uuid) {

        try {

            List<ADO> results = queryBuilder().where().eq(AbstractDomainObject.UUID, uuid)
                    .and().eq(AbstractDomainObject.SNAPSHOT, false).query();
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

    public ADO querySnapshotByUuid(String uuid) {

        try {
            List<ADO> results = queryBuilder().where().eq(AbstractDomainObject.UUID, uuid)
                    .and().eq(AbstractDomainObject.SNAPSHOT, true).query();
            if (results.size() == 0) {
                return null;
            } else if (results.size() == 1) {
                return results.get(0);
            } else {
                throw new NonUniqueResultException("Found multiple results for uuid: " + uuid);
            }
        } catch (SQLException e) {
            Log.e(getTableName(), "Could not perform querySnapshotByUuid");
            throw new RuntimeException(e);
        }
    }

    public List<ADO> queryForEq(String fieldName, Object value, String orderBy, boolean ascending) {
        try {
            QueryBuilder builder = queryBuilder();
            Where where = builder.where();
            where.eq(fieldName, value);
            where.and().eq(AbstractDomainObject.SNAPSHOT, false).query();
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
            where.and().eq(AbstractDomainObject.SNAPSHOT, false).query();
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

    private AbstractDomainObject saveAndSnapshotWithCast(AbstractDomainObject ado) throws DaoException {
        return saveAndSnapshot((ADO)ado);
    }

    /**
     * @param ado
     * @return the snapshot if relevant
     * @throws DaoException
     */
    public ADO saveAndSnapshot(ADO ado) throws DaoException {

        if (ado.isSnapshot()) {
            throw new IllegalArgumentException("Can't save a snapshot");
        }

        try {
            ADO snapshot;
            boolean snapshotNeeded = ado.getId() != null && !ado.isModified();
            if (snapshotNeeded) {
                // we need to create a snapshot of the unmodified version, so we can use it for comparison when merging
                snapshot = queryForId(ado.getId());
                snapshot.setId(null);
                snapshot.setSnapshot(true);
            } else {
                snapshot = null;
            }

            // ignore parent property
            EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
            String parentProperty = annotation != null ? annotation.parentAccessor() : "";

            // go through all embedded entities and saveAndSnapshot them
            Iterator<PropertyDescriptor> propertyIterator = AdoMergeHelper.getEmbeddedAdoProperties(ado.getClass());
            while (propertyIterator.hasNext()) {
                PropertyDescriptor property = propertyIterator.next();

                // get the embedded entity
                AbstractDomainObject embeddedAdo = (AbstractDomainObject)property.getReadMethod().invoke(ado);

                AbstractDomainObject embeddedAdoSnapshot;
                if (parentProperty.equals(property.getName())) {

                    // ignore parent property
                    if (!snapshotNeeded)
                        continue;

                    // set reference to parent in snapshot
                    embeddedAdoSnapshot = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).querySnapshotByUuid(embeddedAdo.getUuid());
                }
                else {
                    // save it - might return a created snapshot
                    embeddedAdoSnapshot = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).saveAndSnapshotWithCast(embeddedAdo);
                }

                if (snapshotNeeded) {
                    if (embeddedAdoSnapshot == null) {
                        throw new IllegalArgumentException("No snapshot was found or created for " + embeddedAdo);
                    }
                    // write link for cloneCascading of embedded entity
                    property.getWriteMethod().invoke(snapshot, embeddedAdoSnapshot);
                }
            }

            if (ado.getId() == null) {
                // create the new entity and take note that it has been create in the app (modified)
                ado.setModified(true);
                create(ado);
            }
            else {
                if (ado.isModified()) {

                    snapshot = querySnapshotByUuid(ado.getUuid());

                    if (snapshot != null && !ado.getChangeDate().equals(snapshot.getChangeDate())) {
                        throw new DaoException("Change date does not match. Looks like sync was done while between reading and saving the entity: " + ado);
                    }

                    // just update the existing modified version
                    update(ado);
                } else {

                    if (!ado.getChangeDate().equals(snapshot.getChangeDate())) {
                        throw new DaoException("Change date does not match. Looks like sync was done while between reading and saving the entity: " + ado);
                    }

                    // set to modified and update
                    // note: if doesn't matter whether the entity was really changed or not
                    ado.setModified(true);
                    update(ado);

                    // now really create the cloneCascading
                    create(snapshot);
                }
            }

            return snapshot;

        } catch (RuntimeException e) {
            throw new DaoException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCollectionWithSnapshot(Collection<ADO> existingCollection, Collection<ADO> modifiedCollection, AbstractDomainObject parent) throws DaoException {

        // delete no longer existing elements
        existingCollection.removeAll(modifiedCollection); // ignore kept
        for (ADO entry : existingCollection) {
            deleteWithSnapshot(entry);
        }

        try {
            // get the setter
            String parentPropertyName = getAdoClass().getAnnotation(EmbeddedAdo.class).parentAccessor();
            String methodName = "set" + parentPropertyName.substring(0, 1).toUpperCase() + parentPropertyName.substring(1);
            Method parentSetter = getAdoClass().getMethod(methodName, parent.getClass());

            // save remaining
            for (ADO entry : modifiedCollection) {
                parentSetter.invoke(entry, parent);
                saveAndSnapshot(entry);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private AbstractDomainObject deleteWithSnapshotCast(AbstractDomainObject ado) throws DaoException {
        return deleteWithSnapshot((ADO)ado);
    }

    /**
     * @param ado
     * @return the snapshot if relevant
     * @throws DaoException
     */
    public ADO deleteWithSnapshot(ADO ado) throws DaoException {

        if (ado.isSnapshot()) {
            throw new IllegalArgumentException("Can't delete a snapshot this way: " + ado);
        }

        if (ado.getId() == null) {
            throw new IllegalArgumentException("Can't delete an unpersisted entity: " + ado);
        }

        try {
            ADO snapshot;
            boolean snapshotNeeded = !ado.isModified();
            if (snapshotNeeded) {
                // we need to create a cloneCascading of the unmodified version, so we can use it for comparison when merging
                snapshot = queryForId(ado.getId());
                snapshot.setId(null);
                snapshot.setSnapshot(true);
            } else {
                snapshot = null;
            }

            // ignore parent property
            EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
            String parentProperty = annotation != null ? annotation.parentAccessor() : "";

            // go through all embedded entities and delete them
            Iterator<PropertyDescriptor> propertyIterator = AdoMergeHelper.getEmbeddedAdoProperties(ado.getClass());
            while (propertyIterator.hasNext()) {
                PropertyDescriptor property = propertyIterator.next();

                // get the embedded entity
                AbstractDomainObject embeddedAdo = (AbstractDomainObject)property.getReadMethod().invoke(ado);

                AbstractDomainObject embeddedAdoSnapshot;
                if (parentProperty.equals(property.getName())) {

                    // ignore parent property
                    if (!snapshotNeeded)
                        continue;

                    // set reference to parent in snapshot
                    embeddedAdoSnapshot = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).querySnapshotByUuid(embeddedAdo.getUuid());
                }
                else {
                    // save it - might return a created snapshot
                    embeddedAdoSnapshot = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).deleteWithSnapshotCast(embeddedAdo);
                }

                if (snapshotNeeded) {
                    if (embeddedAdoSnapshot == null) {
                        throw new IllegalArgumentException("No snapshot was found or created for " + embeddedAdo);
                    }
                    // write link for snapshot of embedded entity
                    property.getWriteMethod().invoke(snapshot, embeddedAdoSnapshot);
                }
            }

            delete(ado);
            if (snapshot != null) {
                create(snapshot);
            }

            return snapshot;

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

    /**
     * Result might be null, when it was previously deleted and didn't have changes
     * @param source
     * @return
     * @throws DaoException
     */
    public ADO mergeOrCreate(ADO source) throws DaoException {

        if (source.getId() != null) {
            throw new IllegalArgumentException("Merged source is not allowed to have an id");
        }

        ADO result = queryUuid(source.getUuid());
        ADO snapshot = querySnapshotByUuid(source.getUuid());

        if (result == null) {

            // use the source as new entity
            result = source;

            if (snapshot != null) {
                // no existing entity but a snapshot -> the entity was deleted
                if (!source.getChangeDate().equals(snapshot.getChangeDate())) {
                    // source does not match snapshot -> there are changed
                    // we have a conflict
                    Log.i(source.getClass().getSimpleName(), "Recreating deleted entity, because it was modified: " + source.getUuid());

                    // recreate the entity and set it to modified, because the list was changed before
                    result.setModified(true);
                }
                else {
                    // the entity was delete and the server didn't send changes -> keep the deletion
                    return null;
                }
            }
        }

        // use change date from server
        result.setChangeDate(source.getChangeDate());
        if (snapshot != null) {
            snapshot.setChangeDate(source.getChangeDate());
        }

        try {

            BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());

            // ignore parent property
            EmbeddedAdo annotation = source.getClass().getAnnotation(EmbeddedAdo.class);
            String parentProperty = annotation != null ? annotation.parentAccessor() : "";

            List<PropertyDescriptor> collectionProperties = null;

            for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
                // ignore some types and specific properties
                if (AbstractDomainObject.CREATION_DATE.equals(property.getName())
                        || AbstractDomainObject.CHANGE_DATE.equals(property.getName())
                        || AbstractDomainObject.LOCAL_CHANGE_DATE.equals(property.getName())
                        || AbstractDomainObject.UUID.equals(property.getName())
                        || AbstractDomainObject.ID.equals(property.getName())
                        || AbstractDomainObject.SNAPSHOT.equals(property.getName())
                        || AbstractDomainObject.MODIFIED.equals(property.getName())
                        || parentProperty.equals(property.getName())
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
                        Object baseFieldValue = property.getReadMethod().invoke(snapshot);
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

                        // update snapshot
                        property.getWriteMethod().invoke(snapshot, sourceFieldValue);
                    }

                    // update result
                    property.getWriteMethod().invoke(result, sourceFieldValue);
                }
                // 4. lists of embedded domain objects
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

            if (snapshot != null) {
                update(snapshot);
            }

            if (collectionProperties != null) {
                for (PropertyDescriptor property : collectionProperties) {

                    // merge all collection elements
                    Collection<AbstractDomainObject> existingCollection = (Collection<AbstractDomainObject>)property.getReadMethod().invoke(result);
                    Collection<AbstractDomainObject> sourceCollection = (Collection<AbstractDomainObject>)property.getReadMethod().invoke(source);
                    mergeCollection(existingCollection, sourceCollection, result);
                }
            }

            return result;

        } catch (RuntimeException e) {
            throw new DaoException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    private void mergeCollection(Collection<AbstractDomainObject> existingCollection, Collection<AbstractDomainObject> sourceCollection, ADO parent) throws DaoException {

        // delete no longer existing elements
        existingCollection.removeAll(sourceCollection); // ignore kept
        for (AbstractDomainObject entry : existingCollection) {
            DatabaseHelper.getAdoDao(entry.getClass()).deleteCascadeWithCast(entry);
        }

        try {
            Method parentSetter = null;

            for (AbstractDomainObject sourceElement : sourceCollection) {

                AbstractDomainObject resultElement = DatabaseHelper.getAdoDao(sourceElement.getClass()).mergeOrCreateWithCast(sourceElement);

                // result might be null, when it was previously deleted and didn't have changes
                if (resultElement != null) {

                    // we have to set the parent for the resulting element, because it might be new
                    if (parentSetter == null) {
                        // get the setter
                        String parentPropertyName = resultElement.getClass().getAnnotation(EmbeddedAdo.class).parentAccessor();
                        String methodName = "set" + parentPropertyName.substring(0, 1).toUpperCase() + parentPropertyName.substring(1);
                        parentSetter = resultElement.getClass().getMethod(methodName, parent.getClass());
                    }

                    // set and save
                    parentSetter.invoke(resultElement, parent);
                    DatabaseHelper.getAdoDao(resultElement.getClass()).updateWithCast(resultElement);
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void acceptWithCast(AbstractDomainObject ado) throws DaoException {
        accept((ADO)ado);
    }

    public void accept(ADO ado) throws DaoException {

        if (ado.isSnapshot()) {
            throw new DaoException("Can't accept a snapshot");
        }
        if (!ado.isModified()) {
            throw new DaoException("Can't accept an unmodified entity");
        }

        try {

            ADO snapshot = querySnapshotByUuid(ado.getUuid());

            Iterator<PropertyDescriptor> propertyIterator = AdoMergeHelper.getCollectionProperties(ado.getClass());
            while (propertyIterator.hasNext()) {
                PropertyDescriptor property = propertyIterator.next();

                // accept all collection elements
                Collection<AbstractDomainObject> sourceCollection = (Collection<AbstractDomainObject>)property.getReadMethod().invoke(ado);
                for (AbstractDomainObject sourceElement : sourceCollection) {
                    DatabaseHelper.getAdoDao(sourceElement.getClass()).acceptWithCast(sourceElement);
                }

                if (snapshot != null) {
                    // delete remaining snapshots
                    Collection<AbstractDomainObject> snapshotCollection = (Collection<AbstractDomainObject>) property.getReadMethod().invoke(snapshot);
                    snapshotCollection.removeAll(sourceCollection);
                    for (AbstractDomainObject snapshotElement : snapshotCollection) {
                        DatabaseHelper.getAdoDao(snapshotElement.getClass()).deleteCascadeWithCast(snapshotElement);
                    }
                }
            }

            // delete snapshot - don't cascade here
            if (snapshot != null) {
                delete(snapshot);
            }

            // ignore parent property
            EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
            String parentProperty = annotation != null ? annotation.parentAccessor() : "";

            // go through all embedded entities and accept them
            propertyIterator = AdoMergeHelper.getEmbeddedAdoProperties(ado.getClass());
            while (propertyIterator.hasNext()) {
                PropertyDescriptor property = propertyIterator.next();

                // ignore parent property
                if (parentProperty.equals(property.getName())) {
                    continue;
                }

                // get the embedded entity
                AbstractDomainObject embeddedAdo = (AbstractDomainObject)property.getReadMethod().invoke(ado);
                AbstractAdoDao<? extends AbstractDomainObject> adoDao = DatabaseHelper.getAdoDao(embeddedAdo.getClass());
                embeddedAdo = adoDao.queryForId(embeddedAdo.getId()); // refresh
                // accept it
                adoDao.acceptWithCast(embeddedAdo);
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

//    private AbstractDomainObject cloneCascadingCast(AbstractDomainObject ado) {
//        return cloneCascading((ADO)ado);
//    }
//
//    /**
//     *
//     * @param ado
//     * @return The cloneCascading - not persisted yet!
//     */
//    public ADO cloneCascading(ADO ado) {
//
//        if (ado.isSnapshot()) {
//            throw new IllegalArgumentException("Can't cloneCascading a cloned original");
//        }
//
//        try {
//
//            ADO clone = queryForId(ado.getId());
//            clone.setId(null);
//            clone.setSnapshot(true);
//
//            // ignore parent property
//            EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
//            String parentProperty = annotation.parentAccessor();
//
//            // go through all embedded entities and create clones for them
//            Iterator<PropertyDescriptor> propertyIterator = AdoMergeHelper.getEmbeddedAdoProperties(ado.getClass());
//            while (propertyIterator.hasNext()) {
//                PropertyDescriptor property = propertyIterator.next();
//
//                // ignore parent property
//                if (!parentProperty.isEmpty() == parentProperty.equals(property.getName()))
//                    continue;
//
//                // get the embedded entity
//                AbstractDomainObject embeddedAdo = (AbstractDomainObject)property.getReadMethod().invoke(ado);
//                // cloneCascading it
//                AbstractDomainObject clonedEmbeddedAdo = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).cloneCascadingCast(embeddedAdo);
//
//                if (clonedEmbeddedAdo == null) {
//                    throw new IllegalArgumentException("No cloneCascading was created for " + embeddedAdo);
//                }
//                // write link for cloneCascading of embedded entity
//                property.getWriteMethod().invoke(clone, clonedEmbeddedAdo);
//            }
//
//            return clone;
//
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public void deleteCascadeWithCast(AbstractDomainObject ado) {
        deleteCascade((ADO)ado);
    }

    public void deleteCascade(ADO ado) {

        super.delete(ado);

        try {
            // ignore parent property
            EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
            String parentProperty = annotation != null ? annotation.parentAccessor() : "";

            Iterator<PropertyDescriptor> propertyIterator = AdoMergeHelper.getEmbeddedAdoProperties(ado.getClass());
            while (propertyIterator.hasNext()) {
                PropertyDescriptor property = propertyIterator.next();

                // ignore parent property
                if (parentProperty.equals(property.getName()))
                    continue;

                // get embedded
                AbstractDomainObject embeddedAdo = (AbstractDomainObject)property.getReadMethod().invoke(ado);
                // delete it
                DatabaseHelper.getAdoDao(embeddedAdo.getClass()).deleteCascadeWithCast(embeddedAdo);
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public int updateWithCast(AbstractDomainObject ado) {
        return update((ADO)ado);
    }

    public ADO create() {
        try {
            ADO ado = getAdoClass().newInstance();
            ado.setUuid(DataHelper.createUuid());
            Date now = new Date();
            ado.setCreationDate(now);
            ado.setChangeDate(now);

            // create all embedded entities

            // ignore parent property
            EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
            String parentProperty = annotation != null ? annotation.parentAccessor() : "";

            Iterator<PropertyDescriptor> propertyIterator = AdoMergeHelper.getEmbeddedAdoProperties(ado.getClass());
            while (propertyIterator.hasNext()) {
                PropertyDescriptor property = propertyIterator.next();

                // ignore parent property
                if (parentProperty.equals(property.getName()))
                    continue;

                // create embedded
                AbstractDomainObject embeddedAdo = DatabaseHelper.getAdoDao((Class<AbstractDomainObject>)property.getPropertyType()).create();

                if (embeddedAdo == null) {
                    throw new IllegalArgumentException("No embedded entity was created for " + property.getName());
                }
                // write link of embedded entity
                property.getWriteMethod().invoke(ado, embeddedAdo);
            }

            return ado;

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.persistence.NonUniqueResultException;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.openbeans.PropertyDescriptor;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import android.util.Log;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;

/**
 * Some methods are copied from {@link com.j256.ormlite.dao.RuntimeExceptionDao}.
 * <p>
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public abstract class AbstractAdoDao<ADO extends AbstractDomainObject> {

	private Dao<ADO, Long> dao;

	public AbstractAdoDao(Dao<ADO, Long> innerDao) {
		this.dao = innerDao;
	}

	protected abstract Class<ADO> getAdoClass();

	/**
	 * Use querySnapshotByUuid if you want to retrieve the unmodified version in any case.
	 *
	 * @param uuid
	 * @return The modified version of the entity (if exists) or else the unmodified
	 */
	public ADO queryUuid(String uuid) {

		try {

			List<ADO> results = queryBuilder().where().eq(AbstractDomainObject.UUID, uuid).and().eq(AbstractDomainObject.SNAPSHOT, false).query();
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

	public List<ADO> queryUuids(List<String> uuids) {
		try {
			return queryBuilder().where().in(AbstractDomainObject.UUID, uuids).and().eq(AbstractDomainObject.SNAPSHOT, false).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryUuids");
			throw new RuntimeException(e);
		}
	}

	public ADO queryUuidReference(String uuid) {
		try {
			List<ADO> results = queryBuilder()
				.selectColumns(AbstractDomainObject.ID, AbstractDomainObject.UUID, AbstractDomainObject.MODIFIED, AbstractDomainObject.SNAPSHOT)
				.where()
				.eq(AbstractDomainObject.UUID, uuid)
				.and()
				.eq(AbstractDomainObject.SNAPSHOT, false)
				.query();
			if (results.size() == 0) {
				return null;
			} else if (results.size() == 1) {
				return results.get(0);
			} else {
				Log.e(getTableName(), "Found multiple results for UUID: " + uuid);
				throw new NonUniqueResultException("Found multiple results for UUID: " + uuid);
			}
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform queryUuidReference");
			throw new RuntimeException(e);
		}
	}

	public ADO queryUuidWithEmbedded(String uuid) {
		ADO result = queryUuid(uuid);
		if (result != null) {
			initEmbedded(result);
		}
		return result;
	}

	public ADO queryForIdWithEmbedded(Long id) {
		ADO result = queryForId(id);
		if (result != null) {
			initEmbedded(result);
		}
		return result;
	}

	protected void initEmbedded(ADO ado) {
		try {
			// ignore parent property
			EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
			String parentProperty = annotation != null ? annotation.parentAccessor() : "";

			// go through all embedded entities and saveAndSnapshot them
			Iterator<PropertyDescriptor> propertyIterator = AdoPropertyHelper.getEmbeddedAdoProperties(ado.getClass());
			while (propertyIterator.hasNext()) {
				PropertyDescriptor property = propertyIterator.next();

				// get the embedded entity
				AbstractDomainObject embeddedAdo = (AbstractDomainObject) property.getReadMethod().invoke(ado);

				if (embeddedAdo == null || parentProperty.equals(property.getName())) {
					continue;
				}

				embeddedAdo = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).queryForIdWithEmbedded(embeddedAdo.getId());
				property.getWriteMethod().invoke(ado, embeddedAdo);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForId(Object)
	 */
	public ADO queryForId(Long id) {
		try {
			return dao.queryForId(id);
		} catch (SQLException e) {
			Log.e(getTableName(), "queryForId threw exception on: " + id, e);
			throw new RuntimeException(e);
		}
	}

	public ADO querySnapshotByUuid(String uuid) {

		try {
			List<ADO> results = queryBuilder().where().eq(AbstractDomainObject.UUID, uuid).and().eq(AbstractDomainObject.SNAPSHOT, true).query();
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

	public List<ADO> queryForEq(String fieldName, Object value, String orderBy, boolean ascending, long offset, long limit) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(fieldName, value);
			where.and().eq(AbstractDomainObject.SNAPSHOT, false).query();
			return builder.orderBy(orderBy, ascending).offset(offset).limit(limit).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForEq");
			throw new RuntimeException(e);
		}
	}

	public long countOfEq(String fieldName, Object value) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(fieldName, value);
			where.and().eq(AbstractDomainObject.SNAPSHOT, false).query();
			return builder.countOf();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform countOfEq");
			throw new RuntimeException(e);
		}
	}

	public List<ADO> querySnapshotsForEq(String fieldName, Object value, String orderBy, boolean ascending) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(fieldName, value);
			where.and().eq(AbstractDomainObject.SNAPSHOT, true).query();
			return builder.orderBy(orderBy, ascending).query();
		} catch (SQLException | IllegalArgumentException e) {
			Log.e(getTableName(), "Could not perform queryForEq");
			throw new RuntimeException(e);
		}
	}

	public List<ADO> queryForAll(String orderBy, boolean ascending) {
		try {
			QueryBuilder builder = queryBuilder();
			builder.where().eq(AbstractDomainObject.SNAPSHOT, false).query();
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
		GenericRawResults<Object[]> maxChangeDateResult = queryRaw(
			query,
			new DataType[] {
				DataType.DATE_LONG });
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

		String query = "SELECT MAX(jo." + AbstractDomainObject.CHANGE_DATE + ") FROM " + getTableName() + " AS ta" + " LEFT JOIN " + joinTableName
			+ " AS jo ON jo." + AbstractDomainObject.ID + " = ta." + joinColumnName + "_ID";
		GenericRawResults<Object[]> maxChangeDateResult = queryRaw(
			query,
			new DataType[] {
				DataType.DATE_LONG });
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

	public AbstractDomainObject saveAndSnapshotWithCast(AbstractDomainObject ado) throws DaoException {
		return saveAndSnapshot((ADO) ado);
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
			ADO snapshot = null;
			boolean withSnapshot = ado.getId() != null;
			if (withSnapshot) {

				snapshot = querySnapshotByUuid(ado.getUuid());
				if (snapshot == null) {
					if (ado.isModified()) {
						Log.w(this.getClass().getSimpleName(), "Snapshot was missing for " + ado);
					}

					// we need to build a snapshot of the current version, so we can use it for comparison when merging
					snapshot = queryForId(ado.getId());
					snapshot.setId(null);
					snapshot.setSnapshot(true);
				} else if (!ado.getChangeDate().equals(snapshot.getChangeDate())) {
					throw new DaoException("Change date does not match. Looks like sync was done between opening and saving the entity.");
				}
			}

			ado.setLastOpenedDate(DateHelper.addSeconds(new Date(), 5));

			// ignore parent property
			EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
			String parentProperty = annotation != null ? annotation.parentAccessor() : "";

			// go through all embedded entities and saveAndSnapshot them
			Iterator<PropertyDescriptor> propertyIterator = AdoPropertyHelper.getEmbeddedAdoProperties(ado.getClass());
			while (propertyIterator.hasNext()) {
				PropertyDescriptor property = propertyIterator.next();

				// get the embedded entity
				AbstractDomainObject embeddedAdo = (AbstractDomainObject) property.getReadMethod().invoke(ado);

				AbstractDomainObject embeddedAdoSnapshot = null;
				if (parentProperty.equals(property.getName())) {

					// ignore parent property
					if (!withSnapshot)
						continue;

					// set reference to parent in snapshot
					if (embeddedAdo != null) {
						embeddedAdoSnapshot = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).querySnapshotByUuid(embeddedAdo.getUuid());
					}
				} else {
					// save it - might return a created snapshot
					if (embeddedAdo != null) {
						embeddedAdoSnapshot = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).saveAndSnapshotWithCast(embeddedAdo);
					}
				}

				if (withSnapshot) {
					if (embeddedAdoSnapshot != null) {
						// write link for cloneCascading of embedded entity
						property.getWriteMethod().invoke(snapshot, embeddedAdoSnapshot);
					}
				}
			}

			if (ado.getId() == null) {
				// build the new entity and take note that it has been build in the app (modified)
				ado.setModified(true);
				create(ado);
			} else {
				if (!ado.isModified()) {
					ado.setModified(true);
				}
				update(ado);

				if (snapshot.getId() == null) {
					// now really create a db entry for the snapshot
					create(snapshot);
				}
			}

			return snapshot;

		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void saveCollectionWithSnapshot(Collection<ADO> existingCollection, Collection<ADO> modifiedCollection, AbstractDomainObject parent)
		throws DaoException {

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
		return deleteWithSnapshot((ADO) ado);
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
				// we need to build a cloneCascading of the unmodified version, so we can use it for comparison when merging
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
			Iterator<PropertyDescriptor> propertyIterator = AdoPropertyHelper.getEmbeddedAdoProperties(ado.getClass());
			while (propertyIterator.hasNext()) {
				PropertyDescriptor property = propertyIterator.next();

				// get the embedded entity
				AbstractDomainObject embeddedAdo = (AbstractDomainObject) property.getReadMethod().invoke(ado);

				AbstractDomainObject embeddedAdoSnapshot;
				if (parentProperty.equals(property.getName())) {

					// ignore parent property
					if (!snapshotNeeded)
						continue;

					// set reference to parent in snapshot
					embeddedAdoSnapshot = DatabaseHelper.getAdoDao(embeddedAdo.getClass()).querySnapshotByUuid(embeddedAdo.getUuid());
				} else {
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

		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private AbstractDomainObject mergeOrCreateWithCast(AbstractDomainObject ado) throws DaoException {
		return mergeOrCreate((ADO) ado);
	}

	/**
	 * Result might be null, when it was previously deleted and didn't have changes
	 *
	 * @param source
	 * @return
	 * @throws DaoException
	 */
	public ADO mergeOrCreate(ADO source) throws DaoException {

		if (source.getId() != null) {
			throw new IllegalArgumentException("Merged source is not allowed to have an id");
		}

		ADO current = queryUuid(source.getUuid());
		ADO snapshot = querySnapshotByUuid(source.getUuid());
		String sourceEntityString = source.toString();
		if (StringUtils.isEmpty(sourceEntityString)) {
			sourceEntityString = source.getEntityName();
		}

		try {

			if (current == null) {

				// create a new entity
				current = getAdoClass().newInstance();
				current.setUuid(source.getUuid());
				current.setCreationDate(source.getCreationDate());

				if (snapshot != null) {
					// no existing entity but a snapshot -> the entity was deleted
					if (!source.getChangeDate().equals(snapshot.getChangeDate())) {
						// source does not match snapshot -> there are changed fields that we need to keep
						// we have a conflict
						Log.i(source.getClass().getSimpleName(), "Recreating deleted entity, because it was modified: " + source.getUuid());
						DatabaseHelper.getSyncLogDao().createWithParentStack(source.toString(), "Recreated because it was modified by someone else.");
						// recreate the entity and set it to modified, because the list was changed before
						current.setModified(true);
					} else {
						// the entity was delete and the server didn't send changes -> keep the deletion
						return null;
					}
				}
			}

			// use change date from server
			current.setChangeDate(source.getChangeDate());
			if (snapshot != null) {
				snapshot.setChangeDate(source.getChangeDate());
			}

			// ignore parent property
			EmbeddedAdo annotation = source.getClass().getAnnotation(EmbeddedAdo.class);
			String parentProperty = annotation != null ? annotation.parentAccessor() : "";

			List<PropertyDescriptor> collectionProperties = null;

			DatabaseHelper.getSyncLogDao().pushParentEntityName(sourceEntityString);

			StringBuilder conflictStringBuilder = new StringBuilder();
			for (PropertyDescriptor property : AdoPropertyHelper.getPropertyDescriptors(source.getClass())) {
				// ignore some types and specific properties
				if (!AdoPropertyHelper.isModifiableProperty(property)
					|| parentProperty.equals(property.getName())
					|| Case.COMPLETENESS.equals(property.getName()))
					continue;

				// we now have to write the value from source into target and base
				// there are four types of properties:

				// 1. embedded domain objects like a Location or Symptoms
				// -> call merge for the object
				if (AdoPropertyHelper.hasEmbeddedAnnotation(property)) {

					// get the embedded entity
					AbstractDomainObject embeddedSource = (AbstractDomainObject) property.getReadMethod().invoke(source);

					if (embeddedSource != null) {
						// merge it - will return the merged result
						AbstractDomainObject embeddedCurrent =
							DatabaseHelper.getAdoDao(embeddedSource.getClass()).mergeOrCreateWithCast(embeddedSource);

						if (embeddedCurrent == null) {
							throw new IllegalArgumentException("No merge result was created for " + embeddedSource);
						}
						// write link for merged embedded
						property.getWriteMethod().invoke(current, embeddedCurrent);
					}
				}
				// 2. "value" types like String, Date, Enum, ...
				// -> just copy value from source into target and base
				// 3. reference domain objects like a reference to a Person or a District
				// -> just copy reference value from source into target and base
				else if (DataHelper.isValueType(property.getPropertyType())
					|| AbstractDomainObject.class.isAssignableFrom(property.getPropertyType())) {

					Object sourceFieldValue = property.getReadMethod().invoke(source);

					if (current.isModified() && snapshot != null) {
						// did the server send changes?
						Object snapshotFieldValue = property.getReadMethod().invoke(snapshot);
						if (DataHelper.equal(snapshotFieldValue, sourceFieldValue)) {
							continue;
						}

						// did we change anything and is the server data different from ours?
						// - two persons may have set the exact same data
						Object currentFieldValue = property.getReadMethod().invoke(current);
						if (!DataHelper.equal(snapshotFieldValue, currentFieldValue) && !DataHelper.equal(currentFieldValue, sourceFieldValue)) {
							// we have a conflict
							Log.i(
								source.getClass().getName(),
								"Overriding " + property.getName() + "; Snapshot '" + DataHelper.toStringNullable(snapshotFieldValue) + "'; Yours: '"
									+ DataHelper.toStringNullable(currentFieldValue) + "'; Server: '" + DataHelper.toStringNullable(sourceFieldValue)
									+ "'");

							conflictStringBuilder.append(I18nProperties.getCaption(source.getI18nPrefix() + "." + property.getName()));
							conflictStringBuilder.append("<br/><i>");
							conflictStringBuilder.append(DatabaseHelper.getContext().getResources().getString(R.string.synclog_yours));
							conflictStringBuilder.append("</i>");
							conflictStringBuilder.append(DataHelper.toStringNullable(currentFieldValue));
							conflictStringBuilder.append("<br/><i>");
							conflictStringBuilder.append(DatabaseHelper.getContext().getResources().getString(R.string.synclog_server));
							conflictStringBuilder.append("</i>");
							conflictStringBuilder.append(DataHelper.toStringNullable(sourceFieldValue));
							conflictStringBuilder.append("<br/>");
						}

						// update snapshot
						property.getWriteMethod().invoke(snapshot, sourceFieldValue);
					}

					// update result
					property.getWriteMethod().invoke(current, sourceFieldValue);
				}
				// 4. lists of embedded domain objects
				else if (Collection.class.isAssignableFrom(property.getPropertyType())) {

					// merging lists is done after entity is saved
					if (collectionProperties == null) {
						collectionProperties = new ArrayList<>();
					}
					collectionProperties.add(property);

					// TODO: DA WEITER OBEN MUSS SCHON GECHECKT WERDEN, OB ES EINE COLLECTION VON ENUMS IST, WENN JA DANN MACH DAS ANDERE -> ELEMENT TYPE RAUSFINDEN

				} else {
					// Other objects are not supported
					throw new UnsupportedOperationException(property.getPropertyType().getName() + " is not supported as a property type.");
				}
			}

			DatabaseHelper.getSyncLogDao().popParentEntityName();

			String conflictString = conflictStringBuilder.toString();
			if (!conflictString.isEmpty()) {
				DatabaseHelper.getSyncLogDao().createWithParentStack(sourceEntityString, conflictString);
			}

			if (current.getId() == null) {
				create(current);
			} else {
				update(current);
			}

			if (snapshot != null) {
				update(snapshot);
			}

			if (collectionProperties != null) {

				DatabaseHelper.getSyncLogDao().pushParentEntityName(sourceEntityString);

				for (PropertyDescriptor property : collectionProperties) {

					// merge all collection elements - do this after saving because elements reference their parent
					Collection<AbstractDomainObject> currentCollection = (Collection<AbstractDomainObject>) property.getReadMethod().invoke(current);
					Collection<AbstractDomainObject> sourceCollection = (Collection<AbstractDomainObject>) property.getReadMethod().invoke(source);
					mergeCollection(currentCollection, sourceCollection, current);
				}

				DatabaseHelper.getSyncLogDao().popParentEntityName();
			}

			return current;

		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	private void mergeCollection(Collection<AbstractDomainObject> existingCollection, Collection<AbstractDomainObject> sourceCollection, ADO parent)
		throws DaoException {

		try {

			StringBuilder conflictStringBuilder = new StringBuilder();

			// delete no longer existing elements
			existingCollection.removeAll(sourceCollection); // ignore kept
			for (AbstractDomainObject existingEntry : existingCollection) {
				if (existingEntry.isModified()) {
					AbstractDomainObject existingSnapshot =
						DatabaseHelper.getAdoDao(existingEntry.getClass()).querySnapshotByUuid(existingEntry.getUuid());
					if (existingSnapshot == null) {
						// entry is new (not yet sent to the server) -> keep it
						continue;
					} else if (AdoPropertyHelper.hasModifiedProperty(existingEntry, existingSnapshot, true)) {
						// entry exists and is modified -> inform the user that the changes are deleted
						conflictStringBuilder
							.append(DatabaseHelper.getContext().getResources().getString(R.string.error_modified_list_entry_deleted));
						conflictStringBuilder.append("<br/><i>");
						conflictStringBuilder.append(DatabaseHelper.getContext().getResources().getString(R.string.synclog_yours));
						conflictStringBuilder.append("</i>");
						conflictStringBuilder.append(DataHelper.toStringNullable(existingEntry));
					}
				}
				DatabaseHelper.getAdoDao(existingEntry.getClass()).deleteCascadeWithCast(existingEntry);
			}

			String conflictString = conflictStringBuilder.toString();
			if (!conflictString.isEmpty()) {
				DatabaseHelper.getSyncLogDao().createWithParentStack(parent.toString(), conflictString);
			}

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
					AbstractAdoDao<? extends AbstractDomainObject> dao = DatabaseHelper.getAdoDao(resultElement.getClass());
					dao.updateWithCast(resultElement);

					// If the parent is modified we need to make sure this collection element is modified as well
					if (parent.isModified() && !resultElement.isModified()) {
						dao.saveAndSnapshotWithCast(resultElement);
					}
				}
			}
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void acceptWithCast(AbstractDomainObject ado) throws DaoException {
		accept((ADO) ado);
	}

	/**
	 * Set a modified entity and its children to unmodified and remove the snapshots
	 *
	 * @param ado
	 * @throws DaoException
	 */
	public void accept(ADO ado) throws DaoException {

		if (ado.isSnapshot()) {
			throw new DaoException("Can't accept a snapshot");
		}
		if (!ado.isModified()) {
			// this does not justify throwing an exception
			//throw new DaoException("Can't accept an unmodified entity");
			Log.w(this.getClass().getSimpleName(), "Accepting an unmodified entity - maybe data is corrupt due to a previous error");
		}

		try {

			ADO snapshot = querySnapshotByUuid(ado.getUuid());

			Iterator<PropertyDescriptor> propertyIterator = AdoPropertyHelper.getCollectionProperties(ado.getClass());
			while (propertyIterator.hasNext()) {
				PropertyDescriptor property = propertyIterator.next();

				if (!AdoPropertyHelper.isModifiableProperty(property)) {
					continue;
				}

				// accept all collection elements
				Collection<AbstractDomainObject> sourceCollection = (Collection<AbstractDomainObject>) property.getReadMethod().invoke(ado);
				for (AbstractDomainObject sourceElement : sourceCollection) {
					DatabaseHelper.getAdoDao(sourceElement.getClass()).acceptWithCast(sourceElement);
				}

				if (snapshot != null) {
					// delete remaining snapshots
					Collection<AbstractDomainObject> snapshotCollection =
						(Collection<AbstractDomainObject>) property.getReadMethod().invoke(snapshot);
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
			propertyIterator = AdoPropertyHelper.getEmbeddedAdoProperties(ado.getClass());
			while (propertyIterator.hasNext()) {
				PropertyDescriptor property = propertyIterator.next();

				// ignore parent property
				if (parentProperty.equals(property.getName())) {
					continue;
				}

				// get the embedded entity
				AbstractDomainObject embeddedAdo = (AbstractDomainObject) property.getReadMethod().invoke(ado);
				if (embeddedAdo != null) {
					AbstractAdoDao<? extends AbstractDomainObject> adoDao = DatabaseHelper.getAdoDao(embeddedAdo.getClass());
					embeddedAdo = adoDao.queryForId(embeddedAdo.getId()); // refresh
					// accept it
					adoDao.acceptWithCast(embeddedAdo);
				}
			}

			ado.setModified(false);
			update(ado);

		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteCascadeWithCast(AbstractDomainObject ado) throws SQLException {
		deleteCascade((ADO) ado);
	}

	public void deleteCascade(ADO ado) throws SQLException {

		try {
			if (ado.isModified() && ado.getClass().getAnnotation(EmbeddedAdo.class) == null) {
				// let user know if changes are lost (not for embedded entities)
				DatabaseHelper.getSyncLogDao()
					.createWithParentStack(ado.toString(), DatabaseHelper.getString(R.string.error_changes_dropped_no_access));
				// TODO include JSON backup
			}
			delete(ado);

			// ignore parent property
			EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
			String parentProperty = annotation != null ? annotation.parentAccessor() : "";

			Iterator<PropertyDescriptor> propertyIterator = AdoPropertyHelper.getEmbeddedAdoProperties(ado.getClass());
			while (propertyIterator.hasNext()) {
				PropertyDescriptor property = propertyIterator.next();

				// ignore parent property
				if (parentProperty.equals(property.getName()))
					continue;

				// get embedded
				AbstractDomainObject embeddedAdo = (AbstractDomainObject) property.getReadMethod().invoke(ado);
				// delete it
				if (embeddedAdo != null) { // data might be invalid and embedded missing
					DatabaseHelper.getAdoDao(embeddedAdo.getClass()).deleteCascadeWithCast(embeddedAdo);
				}
			}

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Delete all entities (cascading) that are not in the list
	 *
	 * @param validUuids
	 */
	public void deleteInvalid(final List<String> validUuids) throws DaoException {
		callBatchTasks(new Callable<Void>() {

			public Void call() throws Exception {
				QueryBuilder<ADO, Long> builder = queryBuilder();
				builder.where().notIn(AbstractDomainObject.UUID, validUuids);
				List<ADO> invalidEntities = builder.query();
				int deletionCounter = 0;
				for (ADO invalidEntity : invalidEntities) {

					if (invalidEntity.isNew()) {
						// don't delete new entities
						continue;
					} else {
						// delete with all embedded entities
						deleteCascade(invalidEntity);
						deletionCounter++;
					}
				}

				if (invalidEntities.size() > 0) {
					Log.d(getTableName(), "Deleted invalid entities: " + deletionCounter + " of " + invalidEntities.size());
				}
				return null;
			}
		});
	}

	/**
	 * Delete all entities (cascading) that are in the list
	 */
	public void delete(final List<String> uuids) throws DaoException {
		callBatchTasks(new Callable<Void>() {

			public Void call() throws Exception {
				QueryBuilder<ADO, Long> builder = queryBuilder();
				builder.where().in(AbstractDomainObject.UUID, uuids);
				List<ADO> entities = builder.query();
				int deletionCounter = 0;
				for (ADO entity : entities) {
					// delete with all embedded entities
					deleteCascade(entity);
					deletionCounter++;
				}

				if (entities.size() > 0) {
					Log.d(getTableName(), "Deleted entities: " + deletionCounter + " of " + entities.size());
				}
				return null;
			}
		});
	}

	public List<String> filterMissing(List<String> uuids) {
		try {
			GenericRawResults<Object[]> existingUuids = dao.queryRaw(
				"SELECT uuid FROM " + getTableName(),
				new DataType[] {
					DataType.STRING });
			List<String> results = new ArrayList<String>(uuids);
			for (Object[] existingUuid : existingUuids) {
				results.remove(existingUuid[0]);
			}
			return results;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public ADO build() {
		try {
			ADO ado = getAdoClass().newInstance();
			ado.setUuid(DataHelper.createUuid());
			ado.setCreationDate(new Date()); // now
			ado.setChangeDateForNew(); // this has to be set by the server

			// build all embedded entities

			// ignore parent property
			EmbeddedAdo annotation = ado.getClass().getAnnotation(EmbeddedAdo.class);
			String parentProperty = annotation != null ? annotation.parentAccessor() : "";

			Iterator<PropertyDescriptor> propertyIterator = AdoPropertyHelper.getEmbeddedAdoProperties(ado.getClass());
			while (propertyIterator.hasNext()) {
				PropertyDescriptor property = propertyIterator.next();

				// ignore parent property
				if (parentProperty.equals(property.getName()))
					continue;

				// build embedded
				AbstractDomainObject embeddedAdo = DatabaseHelper.getAdoDao((Class<AbstractDomainObject>) property.getPropertyType()).build();

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

	// TODO #704
//    /**
//     * Sets the last opened date OF THE ORIGINAL OBJECT in the database and its embedded objects to the current date.
//     * This does NOT manipulate the entity given as the parameter to avoid the unintended saving of other fields.
//     * @param ado It's ok if the ADO object only contains the id of the entity
//     */
//    public void markAsRead(ADO ado) {
//        ADO originalAdo = queryForId(ado.getId());
//        originalAdo.setLastOpenedDate(DateHelper.addSeconds(new Date(), 5));
//        update(originalAdo);
//
//        Iterator<PropertyDescriptor> propertyIterator = AdoPropertyHelper.getEmbeddedAdoProperties(originalAdo.getClass());
//        while (propertyIterator.hasNext()) {
//            try {
//                PropertyDescriptor property = propertyIterator.next();
//                AbstractDomainObject embeddedAdo = (AbstractDomainObject) property.getReadMethod().invoke(originalAdo);
//                if (embeddedAdo == null) {
//                    throw new IllegalArgumentException("No embedded entity was created for " + property.getName());
//                }
//
//                DatabaseHelper.getAdoDao(embeddedAdo.getClass()).markAsReadWithCast(embeddedAdo);
//            } catch (InvocationTargetException e) {
//                throw new RuntimeException("Error while trying to invoke read method to set last opened dates", e);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException("Error while trying to invoke read method to set last opened dates", e);
//            }
//        }
//    }
//
//    public void markAsReadWithCast(AbstractDomainObject ado) {
//        markAsRead((ADO) ado);
//    }

	public boolean isAnyModified() {
		try {
			ADO result = queryBuilder().where().eq(AbstractDomainObject.MODIFIED, true).queryForFirst();
			return result != null;
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform isAnyModified");
			throw new RuntimeException(e);
		}
	}

	public List<ADO> getModifiedEntities() {
		try {
			return queryBuilder().where().eq(AbstractDomainObject.MODIFIED, true).query();
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform getModifiedEntities");
			throw new RuntimeException(e);
		}
	}

	public boolean isEmpty() {

		try {
			ADO result = queryBuilder().queryForFirst();
			return result == null;
		} catch (SQLException e) {
			Log.e(getTableName(), "Could not perform isEmpty");
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForAll()
	 */
	public List<ADO> queryForAll() {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(AbstractDomainObject.SNAPSHOT, false).query();
			return builder.query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryForEq(String, Object)
	 */
	public List<ADO> queryForEq(String fieldName, Object value) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.eq(fieldName, value);
			where.and().eq(AbstractDomainObject.SNAPSHOT, false).query();
			return builder.query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<ADO> queryForNull(String fieldName) {
		try {
			QueryBuilder builder = queryBuilder();
			Where where = builder.where();
			where.and(where.isNull(fieldName), where.eq(AbstractDomainObject.SNAPSHOT, false)).query();
			return builder.query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#queryBuilder()
	 */
	public QueryBuilder<ADO, Long> queryBuilder() {
		return dao.queryBuilder();
	}

	/**
	 * @see Dao#create(Object)
	 */
	public void create(ADO data) throws SQLException {
		if (data == null)
			return;
//        try {
		int resultRowCount = dao.create(data);
		if (resultRowCount < 1)
			throw new SQLException(
				"Database entry was not created. Go back and try again.\n" + "Type: " + data.getClass().getSimpleName() + ", UUID: "
					+ data.getUuid());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
	}

	/**
	 * @see Dao#update(Object)
	 */
	protected void update(ADO data) throws SQLException {
		if (data == null)
			return;
		dao.update(data);
		// #1124 returns 0 when nothing has changed
//        if (resultRowCount < 1) {
//            throw new SQLException("Database entry was not updated - update all entered fields and save again.\n" +
//                    "Type: " + data.getClass().getSimpleName() + ", UUID: " + data.getUuid());
//        }
	}

	public void updateWithCast(AbstractDomainObject ado) throws SQLException {
		update((ADO) ado);
	}

	public void updateOrCreate(ADO data) throws SQLException {
		if (data.getId() == null) {
			create(data);
		} else {
			update(data);
		}
	}

	/**
	 * @see Dao#delete(Object)
	 */
	public void delete(ADO data) throws SQLException {
		if (data == null)
			return;
//        try {
		int resultRowCount = dao.delete(data);
		if (resultRowCount < 1)
			throw new SQLException(
				"Database entry was not deleted - go back and try again.\n" + "Type: " + data.getClass().getSimpleName() + ", UUID: "
					+ data.getUuid());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
	}

	/**
	 * @see Dao#queryRaw(String, DataType[], String...)
	 */
	protected GenericRawResults<Object[]> queryRaw(String query, DataType[] columnTypes, String... arguments) {
		try {
			return dao.queryRaw(query, columnTypes, arguments);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#callBatchTasks(Callable)
	 */
	public <CT> CT callBatchTasks(Callable<CT> callable) throws DaoException {
		try {
			return dao.callBatchTasks(callable);
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (DaoException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#countOf()
	 */
	public long countOf() {
		try {
			return dao.countOf();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Dao#getConnectionSource()
	 */
	public ConnectionSource getConnectionSource() {
		return dao.getConnectionSource();
	}
}

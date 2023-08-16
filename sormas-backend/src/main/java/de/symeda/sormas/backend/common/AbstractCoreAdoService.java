/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.security.DenyAll;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.util.IterableHelper;

public abstract class AbstractCoreAdoService<ADO extends CoreAdo, J extends QueryJoins<ADO>> extends AbstractDeletableAdoService<ADO> {

	private static final int ARCHIVE_BATCH_SIZE = 1000;

	@EJB
	protected FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	protected AbstractCoreAdoService(Class<ADO> elementClass) {
		super(elementClass);
	}

	/**
	 * @deprecated Invocation without a {@link QueryContext} is only allowed internally in the same class. For invocation from other EJBs,
	 *             use {@code createUserFilter(QueryContext)} instead (to be implemented by each subclass).
	 */
	@Deprecated
	@DenyAll
	@Override
	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, ADO> from) {
		return createUserFilterInternal(cb, cq, from);
	}

	/**
	 * Delegate this to {@code createUserFilter(QueryContext)}.
	 */
	@Deprecated
	@DenyAll
	@SuppressWarnings("rawtypes")
	protected abstract Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, ADO> from);

	public boolean isArchived(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(getElementClass());

		cq.where(cb.and(cb.equal(from.get(CoreAdo.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	protected abstract J toJoins(From<?, ADO> adoPath);

	private <T extends ChangeDateBuilder<T>> T addChangeDates(T builder, From<?, ADO> adoPath, boolean includeExtendedChangeDateFilters) {
		return addChangeDates(builder, toJoins(adoPath), includeExtendedChangeDateFilters);
	}

	protected <T extends ChangeDateBuilder<T>> T addChangeDates(T builder, J joins, boolean includeExtendedChangeDateFilters) {
		return builder.add(joins.getRoot());
	}

	public Map<String, Date> calculateEndOfProcessingDate(List<String> entityuuids) {

		if (entityuuids.isEmpty()) {
			return Collections.emptyMap();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<ADO> from = cq.from(getElementClass());

		Expression aggregatedChangeDateExpression = addChangeDates(new AggregatedChangeDateExpressionBuilder(cb), from, true).build();
		cq.multiselect(from.get(CoreAdo.UUID), cb.max(aggregatedChangeDateExpression));
		cq.where(from.get(CoreAdo.UUID).in(entityuuids));
		cq.groupBy(from.get(CoreAdo.UUID));

		Map<String, Date> collect = em.createQuery(cq).getResultList().stream().collect(Collectors.toMap(r -> (String) r[0], r -> (Date) r[1]));
		return collect;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void archive(String entityUuid, Date endOfProcessingDate) {

		if (endOfProcessingDate == null) {
			endOfProcessingDate = calculateEndOfProcessingDate(Collections.singletonList(entityUuid)).get(entityUuid);
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<ADO> cu = cb.createCriteriaUpdate(getElementClass());
		Root<ADO> root = cu.from(getElementClass());

		cu.set(AbstractDomainObject.CHANGE_DATE, Timestamp.from(Instant.now()));
		cu.set(root.get(CoreAdo.ARCHIVED), true);
		cu.set(root.get(CoreAdo.END_OF_PROCESSING_DATE), endOfProcessingDate);

		cu.where(cb.equal(root.get(AbstractDomainObject.UUID), entityUuid));

		em.createQuery(cu).executeUpdate();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void archive(List<String> entityUuids) {

		IterableHelper.executeBatched(
			entityUuids,
			ARCHIVE_BATCH_SIZE,
			batchedUuids -> calculateEndOfProcessingDate(batchedUuids).forEach((entityUuid, finalEndOfProcessingDate) -> {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<ADO> cu = cb.createCriteriaUpdate(getElementClass());
				Root<ADO> root = cu.from(getElementClass());

				cu.set(AbstractDomainObject.CHANGE_DATE, Timestamp.from(Instant.now()));
				cu.set(root.get(CoreAdo.ARCHIVED), true);
				cu.set(root.get(CoreAdo.END_OF_PROCESSING_DATE), finalEndOfProcessingDate);

				cu.where(cb.equal(root.get(AbstractDomainObject.UUID), entityUuid));

				em.createQuery(cu).executeUpdate();
			}));
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void dearchive(List<String> entityUuids, String dearchiveReason) {

		IterableHelper.executeBatched(entityUuids, ARCHIVE_BATCH_SIZE, batchedUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<ADO> cu = cb.createCriteriaUpdate(getElementClass());
			Root<ADO> root = cu.from(getElementClass());

			cu.set(AbstractDomainObject.CHANGE_DATE, Timestamp.from(Instant.now()));
			cu.set(root.get(CoreAdo.ARCHIVED), false);
			cu.set(root.get(CoreAdo.END_OF_PROCESSING_DATE), (Date) null);
			cu.set(root.get(CoreAdo.ARCHIVE_UNDONE_REASON), dearchiveReason);

			cu.where(root.get(AbstractDomainObject.UUID).in(batchedUuids));

			em.createQuery(cu).executeUpdate();
		});
	}

	public EditPermissionType getEditPermissionType(ADO entity) {
		if (entity.isArchived()) {
			return featureConfigurationFacade.isFeatureEnabled(FeatureType.EDIT_ARCHIVED_ENTITIES)
				? EditPermissionType.ALLOWED
				: EditPermissionType.ARCHIVING_STATUS_ONLY;
		}

		return EditPermissionType.ALLOWED;
	}

	public boolean isEditAllowed(ADO entity) {
		return getEditPermissionType(entity) == EditPermissionType.ALLOWED;
	}

	@Override
	public List<Long> getInJurisdictionIds(List<ADO> selectedEntities) {
		return getIdList(selectedEntities, this::inJurisdictionOrOwned);
	}

	@Override
	public boolean inJurisdictionOrOwned(ADO entity) {
		return fulfillsCondition(entity, this::inJurisdictionOrOwned);
	}

	/**
	 * Used to fetch {@link AdoServiceWithUserFilterAndJurisdiction#getInJurisdictionIds(List)}/{@link AdoServiceWithUserFilterAndJurisdiction#inJurisdictionOrOwned(AbstractDomainObject)}
	 * (without {@link QueryContext} because there are no other conditions etc.).
	 * 
	 * @return A filter on entities within the users jurisdiction or owned by him.
	 */
	public abstract Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, ADO> from);
}

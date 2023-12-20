/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

public class BaseAdoService<ADO extends AbstractDomainObject> implements AdoService<ADO> {

	// protected to be used by implementations
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Class<ADO> elementClass;

	@EJB
	private CurrentUserService currentUserService;

	// protected to be used by implementations
	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	protected BaseAdoService(Class<ADO> elementClass) {
		this.elementClass = elementClass;
	}

	public User getCurrentUser() {
		return currentUserService.getCurrentUser();
	}

	public boolean currentUserHasRestrictedAccessToAssignedEntities() {
		return currentUserService.hasRestrictedAccessToAssignedEntities();
	}

	public boolean hasRight(UserRight right) {
		return currentUserService.hasUserRight(right);
	}

	public boolean hasAnyRight(Set<UserRight> userRights) {
		return currentUserService.hasAnyUserRight(userRights);
	}

	protected Class<ADO> getElementClass() {
		return elementClass;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public long count() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(getElementClass());
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	public long count(BiFunction<CriteriaBuilder, Root<ADO>, Predicate> filterBuilder) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(getElementClass());
		cq.select(cb.count(from));
		Predicate filter = filterBuilder.apply(cb, from);
		if (filter != null) {
			cq.where(filter);
		}
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * @return null if no entry exists
	 */
	public Timestamp getLatestChangeDate() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Timestamp> cq = cb.createQuery(Timestamp.class);
		Root<ADO> from = cq.from(getElementClass());
		Path<Timestamp> changeDatePath = from.get(AbstractDomainObject.CHANGE_DATE);
		cq.select(cb.greatest(changeDatePath));
		return QueryHelper.getSingleResult(em, cq);
	}

	@Override
	public List<ADO> getAll() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.orderBy(cb.desc(from.get(AbstractDomainObject.CHANGE_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public List<ADO> getList(FilterProvider<ADO> filterProvider, Integer batchSize) {

		long startTime = DateHelper.startTime();
		logger.trace("getList started...");

		// 1. Fetch ids, avoid duplicates with DISTINCT. Only fetch some attributes to avoid costly DISTINCT
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AdoAttributes> cq = cb.createQuery(AdoAttributes.class);
		Root<ADO> from = cq.from(getElementClass());

		// SELECT
		cq.multiselect(from.get(ADO.ID), from.get(ADO.UUID), from.get(ADO.CHANGE_DATE));

		// FILTER
		Predicate filter = filterProvider.provide(cb, cq, from);
		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);

		List<AdoAttributes> attributes = getBatchedAttributesQueryResults(cb, cq, from, batchSize);
		List<Long> ids =
			attributes.stream().map(AdoAttributes::getId).limit(batchSize == null ? Long.MAX_VALUE : batchSize).collect(Collectors.toList());
		logger.trace(
			"getList: Unique ids identified. batchSize:{}, attributes:{}, ids:{}, {} ms",
			batchSize,
			attributes.size(),
			ids.size(),
			DateHelper.durationMillies(startTime));

		// 2. Fetch JPA entities by ids
		List<ADO> adoResult = getByIds(ids);
		logger.trace("getList finished. Fetched entities:{}, {} ms", adoResult.size(), DateHelper.durationMillies(startTime));
		return adoResult;
	}

	public List<ADO> getAll(String orderProperty, boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @return {@code true} if {@code entity} fulfills the provided condition, else {@code false}.
	 */
	protected boolean fulfillsCondition(ADO entity, FilterProvider<ADO> conditionProvider) {

		if (entity == null) {
			return false;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<ADO> root = cq.from(getElementClass());
		cq.multiselect(JurisdictionHelper.booleanSelector(cb, conditionProvider.provide(cb, cq, root)));
		cq.where(cb.equal(root.get(ADO.ID), entity.getId()));

		return em.createQuery(cq).getResultList().stream().anyMatch(bool -> bool);
	}

	/**
	 * @return Ids of given {@code selectedEntities} that match the filter given by the {@code filterProvider}.
	 */
	protected List<Long> getIdList(List<ADO> selectedEntities, FilterProvider<ADO> filterProvider) {

		List<Long> result = new ArrayList<>(selectedEntities.size());
		IterableHelper.executeBatched(selectedEntities, ModelConstants.PARAMETER_LIMIT, batchEntities -> {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<ADO> root = cq.from(getElementClass());
			cq.select(root.get(ADO.ID));

			List<Long> ids = batchEntities.stream().map(ADO::getId).collect(Collectors.toList());
			cq.where(cb.and(cb.in(root.get(ADO.ID)).value(ids), filterProvider.provide(cb, cq, root)));
			result.addAll(em.createQuery(cq).getResultList());
		});

		return result;
	}

	/**
	 * @param <S>
	 *            An object with the selected attributes (projection with id as first parameter in constructor).
	 * @param selectedEntities
	 *            From these entities attributes will be fetched.
	 * @param selectionProvider
	 *            To fetch or calculate the desired attributes.
	 * @param attributesConverter
	 *            Converts the query result to an S object.
	 *            The first entry of Object array (Object[0]) contains the entity id that is provided as map key in the method result.
	 * @return Objects with several selected or calculated attributes, with entity id as key.
	 */
	protected <S> Map<Long, S> getSelectionAttributes(
		List<ADO> selectedEntities,
		SelectionProvider<ADO> selectionProvider,
		Function<Object[], S> attributesConverter) {

		Map<Long, S> result = new LinkedHashMap<>(selectedEntities.size());
		IterableHelper.executeBatched(selectedEntities, ModelConstants.PARAMETER_LIMIT, batchEntities -> {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<ADO> root = cq.from(getElementClass());

			List<Selection<?>> selectionList = new ArrayList<>();
			selectionList.add(root.get(ADO.ID));
			selectionList.addAll(selectionProvider.provide(cb, cq, root));
			cq.multiselect(selectionList);

			List<Long> ids = batchEntities.stream().map(ADO::getId).collect(Collectors.toList());
			cq.where(cb.and(cb.in(root.get(ADO.ID)).value(ids)));
			List<Object[]> resultList = em.createQuery(cq).getResultList();
			Map<Long, S> batchResult = resultList.stream().collect(Collectors.toMap(e -> (Long) e[0], e -> attributesConverter.apply(e)));

			// Check that a result for every queried id was provided
			if (CollectionUtils.containsAll(batchResult.keySet(), ids)) {
				result.putAll(batchResult);
			} else {
				throw new IllegalArgumentException(
					String.format("No result found some of the queried ids: %s", CollectionUtils.subtract(ids, batchResult.keySet())));
			}
		});

		return result;
	}

	public List<String> getAllUuids(BiFunction<CriteriaBuilder, Root<ADO>, Predicate> filterBuilder) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ADO> from = cq.from(getElementClass());
		Predicate filter = filterBuilder.apply(cb, from);
		if (filter != null) {
			cq.where(filter);
		}
		cq.select(from.get(ADO.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<AdoAttributes> getBatchedAttributesQueryResults(
		CriteriaBuilder cb,
		CriteriaQuery<AdoAttributes> cq,
		From<?, ADO> from,
		Integer batchSize) {

		// Ordering by UUID is relevant if a batch includes some, but not all objects with the same timestamp.
		// the next batch can then resume with the same timestamp and the next UUID in lexicographical order.y
		cq.orderBy(cb.asc(from.get(AdoAttributes.CHANGE_DATE)), cb.asc(from.get(AdoAttributes.UUID)));

		return createQuery(cq, 0, batchSize).getResultList();
	}

	public long countAfter(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> root = cq.from(getElementClass());

		if (since != null) {
			cq.where(createChangeDateFilter(cb, root, since));
		}

		cq.select(cb.count(root));

		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * @return List of <strong>read-only</strong> entities. Sorts also by {@value AbstractDomainObject#CHANGE_DATE},
	 *         {@value AbstractDomainObject#UUID}, {@value AbstractDomainObject#ID} ASC
	 *         to match sorting for {@code getAllAfter} pattern (to be in sync with
	 *         {@link #getBatchedAttributesQueryResults(CriteriaBuilder, CriteriaQuery, From, Integer)}).
	 */
	public List<ADO> getByIds(List<Long> ids) {

		/*
		 * Use Set here to avoid possible duplicates over several batches or within one batch.
		 * This also avoids costly DISTINCT argument.
		 */
		Set<ADO> result = new LinkedHashSet<>();
		IterableHelper.executeBatched(ids, ModelConstants.PARAMETER_LIMIT, batchedIds -> {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
			Root<ADO> from = cq.from(getElementClass());
			fetchReferences(from);
			cq.where(from.get(AbstractDomainObject.ID).in(batchedIds));
			cq.orderBy(
				cb.asc(from.get(AbstractDomainObject.CHANGE_DATE)),
				cb.asc(from.get(AbstractDomainObject.UUID)),
				cb.asc(from.get(AbstractDomainObject.ID)));
			List<ADO> batchResult = em.createQuery(cq).setHint(ModelConstants.READ_ONLY, true).getResultList();
			result.addAll(batchResult);
		});

		return new ArrayList<>(result);
	}

	/**
	 * Override this method to eagerly fetch entity references in {@link #getByIds(List)}.
	 */
	protected void fetchReferences(From<?, ADO> from) {
		referencesToBeFetched().forEach(s -> from.fetch(s));
	}

	protected List<String> referencesToBeFetched() {
		return Collections.EMPTY_LIST;
	}

	protected EntityGraph<ADO> getEntityFetchGraph() {
		final EntityGraph<ADO> entityGraph = em.createEntityGraph(getElementClass());
		referencesToBeFetched().forEach(s -> entityGraph.addAttributeNodes(s));
		return entityGraph;
	}

	public List<ADO> getByUuids(Collection<String> uuids) {

		if (uuids == null || uuids.isEmpty()) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.where(from.get(AbstractDomainObject.UUID).in(uuids));

		return em.createQuery(cq).getResultList();
	}

	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, ADO> from, Timestamp date) {
		return cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
	}

	public Predicate createChangeDateFilter(CriteriaBuilder cb, QueryJoins<ADO> joins, Timestamp date) {
		return cb.greaterThan(joins.getRoot().get(AbstractDomainObject.CHANGE_DATE), date);
	}

	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, ADO> from, Date date) {
		return createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date));
	}

	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, ADO> from, Date date, String lastSynchronizedUuid) {
		if (lastSynchronizedUuid == null || EntityDto.NO_LAST_SYNCED_UUID.equals(lastSynchronizedUuid)) {
			return createChangeDateFilter(cb, from, date);
		} else {
			Timestamp timestampLower = new Timestamp(date.getTime());
			Timestamp timestampUpper = new Timestamp(date.getTime() + 1L);
			Predicate predicate = cb.or(
				cb.greaterThanOrEqualTo(from.get(AbstractDomainObject.CHANGE_DATE), timestampUpper),
				cb.and(
					cb.greaterThanOrEqualTo(from.get(AbstractDomainObject.CHANGE_DATE), timestampLower),
					cb.greaterThan(from.get(AbstractDomainObject.UUID), lastSynchronizedUuid)));
			return predicate;
		}
	}

	public Predicate recentDateFilter(CriteriaBuilder cb, Date date, Path<Date> datePath, int amountOfDays) {
		return date != null ? cb.between(datePath, DateHelper.subtractDays(date, amountOfDays), DateHelper.addDays(date, amountOfDays)) : null;
	}

	@Override
	public ADO getById(long id) {
		return em.find(getElementClass(), id);
	}

	public ADO getByReferenceDto(ReferenceDto dto) {

		if (dto != null && dto.getUuid() != null) {
			ADO result = getByUuid(dto.getUuid());
			if (result == null) {
				logger.warn("Could not find entity for " + dto.getClass().getSimpleName() + " with uuid " + dto.getUuid());
			}
			return result;
		} else {
			return null;
		}
	}

	public List<ADO> getByReferenceDtos(Collection<? extends ReferenceDto> dtos) {
		if (dtos == null || dtos.isEmpty()) {
			return Collections.emptyList();
		}

		List<String> uuids = dtos.stream().map(ReferenceDto::getUuid).collect(Collectors.toList());
		return getByUuids(uuids);
	}

	@Override
	public ADO getByUuid(String uuid) {

		return getByUuid(uuid, false);
	}

	public ADO getByUuid(String uuid, boolean fetchReferences) {

		if (uuid == null) {
			return null;
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final ParameterExpression<String> uuidParam = cb.parameter(String.class, AbstractDomainObject.UUID);
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<ADO> from = cq.from(getElementClass());

		cq.select(from.get(AbstractDomainObject.ID));

		cq.where(cb.equal(from.get(AbstractDomainObject.UUID), uuidParam));

		final TypedQuery<Long> q = em.createQuery(cq).setParameter(uuidParam, uuid);
		final Long id = QueryHelper.getSingleResult(q);

		if (id != null) {
			if (fetchReferences) {
				final Map<String, Object> hints = new HashMap();
				final EntityGraph<ADO> entityGraph = getEntityFetchGraph();
				hints.put("javax.persistence.fetchgraph", entityGraph);
				return em.find(getElementClass(), id, hints);
			} else {
				return em.find(getElementClass(), id);
			}
		} else {
			return null;
		}
	}

	@Override
	public Boolean exists(@NotNull String uuid) {

		return exists((cb, root, cq) -> cb.equal(root.get(AbstractDomainObject.UUID), uuid));
	}

	@Override
	public void ensurePersisted(ADO ado) throws EntityExistsException {

		if (ado.getId() == null) {
			em.persist(ado);
		} else if (!em.contains(ado)) {
			throw new EntityExistsException("Das Entity ist nicht attacht: " + getElementClass().getSimpleName() + "#" + ado.getUuid());
		}
		em.flush();
	}

	@Override
	public void persist(ADO persistme) {
		em.persist(persistme);
	}

	@Override
	public void deletePermanent(ADO ado) {
		em.remove(em.contains(ado) ? ado : em.merge(ado)); // todo: investigate why the entity might be detached (example: AdditionalTest)
		em.flush();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deletePermanentByUuids(List<String> uuids) {
		uuids.forEach(uuid -> deletePermanent(getByUuid(uuid)));
	}

	@Override
	public void doFlush() {
		em.flush();
	}

	public boolean exists(PredicateBuilder<ADO> filterBuilder) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();

		final CriteriaQuery<Object> query = cb.createQuery(Object.class);
		query.from(getElementClass());

		final Subquery<ADO> subquery = query.subquery(getElementClass());
		final Root<ADO> subRootEntity = subquery.from(getElementClass());
		subquery.select(subRootEntity);
		subquery.where(filterBuilder.buildPredicate(cb, subRootEntity, query));

		final Predicate exists = cb.exists(subquery);
		final Expression<Boolean> trueExpression = cb.literal(true);
		final Expression<Boolean> falseExpression = cb.literal(false);
		query.select(cb.selectCase().when(exists, trueExpression).otherwise(falseExpression));

		final TypedQuery<Object> typedQuery = em.createQuery(query);
		typedQuery.setMaxResults(1);

		try {
			return (Boolean) typedQuery.getSingleResult();
		} catch (NoResultException e) {
			// h2 database entity manager throws "NoResultException" if the entity not found
			return false;
		}
	}

	public List<ADO> getByPredicate(PredicateBuilder<ADO> filterBuilder) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		final Root<ADO> from = cq.from(getElementClass());

		cq.where(filterBuilder.buildPredicate(cb, from, cq));

		return em.createQuery(cq).getResultList();
	}

	public void incrementChangeDate(ADO ado) {
		Session session = em.unwrap(Session.class);
		session.lock(ado, LockMode.OPTIMISTIC_FORCE_INCREMENT);
	}

	public interface PredicateBuilder<ADO extends AbstractDomainObject> {

		Predicate buildPredicate(CriteriaBuilder cb, Root<ADO> root, CriteriaQuery<?> cq);
	}

	public List<Long> getIdsByReferenceDtos(List<? extends ReferenceDto> references) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(getElementClass());

		cq.where(from.get(AbstractDomainObject.UUID).in(references.stream().map(ReferenceDto::getUuid).collect(Collectors.toList())));
		cq.select(from.get(AbstractDomainObject.ID));
		return em.createQuery(cq).getResultList();
	}

	public List<ProcessedEntity> buildProcessedEntities(List<String> entityUuids, ProcessedEntityStatus processedEntityStatus) {
		return entityUuids.stream().map(uuid -> new ProcessedEntity(uuid, processedEntityStatus)).collect(Collectors.toList());
	}

	protected <T> TypedQuery<T> createQuery(CriteriaQuery<T> cq, Integer first, Integer max) {

		final TypedQuery<T> query = em.createQuery(cq);
		if (first != null && max != null) {
			query.setFirstResult(first).setMaxResults(max);
		}

		return query;
	}
}

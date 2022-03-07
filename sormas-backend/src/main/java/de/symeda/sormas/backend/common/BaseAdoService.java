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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
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
import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

public class BaseAdoService<ADO extends AbstractDomainObject> implements AdoService<ADO> {

	// protected to be used by implementations
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Class<ADO> elementClass;

	@Inject
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

	public List<ADO> getAll(BiFunction<CriteriaBuilder, Root<ADO>, Predicate> filterBuilder) {
		return getAll(filterBuilder, null);
	}

	public List<ADO> getAll(BiFunction<CriteriaBuilder, Root<ADO>, Predicate> filterBuilder, Integer batchSize) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		Predicate filter = filterBuilder.apply(cb, from);
		if (filter != null) {
			cq.where(filter);
		}
		return getBatchedQueryResults(cb, cq, from, batchSize);
	}

	public List<ADO> getAll(String orderProperty, boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));

		return em.createQuery(cq).getResultList();
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

	public List<ADO> getBatchedQueryResults(CriteriaBuilder cb, CriteriaQuery<ADO> cq, From<?, ADO> from, Integer batchSize) {

		// Ordering by UUID is relevant if a batch includes some, but not all objects with the same timestamp.
		// the next batch can then resume with the same timestamp and the next UUID in lexicographical order.y
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.CHANGE_DATE)), cb.asc(from.get(AbstractDomainObject.UUID)));

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

	public List<ADO> getByUuids(List<String> uuids) {

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

		if (uuid == null) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> uuidParam = cb.parameter(String.class, AbstractDomainObject.UUID);
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(AbstractDomainObject.UUID), uuidParam));

		TypedQuery<ADO> q = em.createQuery(cq).setParameter(uuidParam, uuid);

		return q.getResultList().stream().findFirst().orElse(null);
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

	@Override
	public void doFlush() {
		em.flush();
	}

	public boolean exists(ExistsPredicateBuilder<ADO> filterBuilder) {

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

	public interface ExistsPredicateBuilder<ADO extends AbstractDomainObject> {

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

	protected <T> TypedQuery<T> createQuery(CriteriaQuery<T> cq, Integer first, Integer max) {

		final TypedQuery<T> query = em.createQuery(cq);
		if (first != null && max != null) {
			query.setFirstResult(first).setMaxResults(max);
		}

		return query;
	}
}

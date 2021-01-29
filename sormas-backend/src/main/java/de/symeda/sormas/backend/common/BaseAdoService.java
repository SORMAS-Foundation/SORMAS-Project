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
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.enterprise.inject.Instance;
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

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.user.CurrentUser;
import de.symeda.sormas.backend.user.CurrentUserQualifier;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

public class BaseAdoService<ADO extends AbstractDomainObject> implements AdoService<ADO> {

	// protected to be used by implementations
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private SessionContext context;

	private final Class<ADO> elementClass;

	@Inject
	@CurrentUserQualifier
	private Instance<CurrentUser> currentUser;

	// protected to be used by implementations
	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	protected BaseAdoService(Class<ADO> elementClass) {
		this.elementClass = elementClass;
	}

	protected User getCurrentUser() {
		return currentUser.get().getUser();
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
		cq.where(filterBuilder.apply(cb, from));
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
		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public List<ADO> getAll() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.orderBy(cb.desc(from.get(AbstractDomainObject.CHANGE_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public List<ADO> getAll(String orderProperty, boolean asc) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));

		return em.createQuery(cq).getResultList();
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

	@Override
	public ADO getByUuid(@NotNull String uuid) {

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

		return exists((cb, root) -> cb.equal(root.get(AbstractDomainObject.UUID), uuid));
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
	public void delete(ADO deleteme) {
		em.remove(deleteme);
		em.flush();
	}

	@Override
	public void doFlush() {
		em.flush();
	}

	protected Boolean exists(BiFunction<CriteriaBuilder, Root<ADO>, Predicate> filterBuilder) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();

		final CriteriaQuery<Object> query = cb.createQuery(Object.class);
		query.from(getElementClass());

		final Subquery<ADO> subquery = query.subquery(getElementClass());
		final Root<ADO> subRootEntity = subquery.from(getElementClass());
		subquery.select(subRootEntity);
		subquery.where(filterBuilder.apply(cb, subRootEntity));

		final Predicate exists = cb.exists(subquery);
		final Expression<Boolean> trueExpression = cb.literal(true);
		final Expression<Boolean> falseExpression = cb.literal(false);
		query.select(cb.selectCase().when(exists, trueExpression).otherwise(falseExpression));

		final TypedQuery<Object> typedQuery = em.createQuery(query);

		try {
			return (Boolean) typedQuery.getSingleResult();
		} catch (NoResultException e) {
			// h2 database entity manager throws "NoResultException" if the entity not found
			return false;
		}
	}

	/**
	 * @return {@code true}, if the system itself is the executing user.
	 */
	protected boolean isSystem() {
		return context.isCallerInRole(UserRole._SYSTEM);
	}

	/**
	 * @return {@code true}, if the executing user is {@link UserRole#ADMIN}.
	 */
	protected boolean isAdmin() {
		return hasUserRole(UserRole.ADMIN);
	}

	/**
	 * @param permission
	 * @return {@code true}, if the executing user is {@code userRole}.
	 */
	protected boolean hasUserRole(UserRole userRole) {
		return context.isCallerInRole(userRole.name());
	}

	protected Timestamp requestTransactionDate() {
		return (Timestamp) this.em.createNativeQuery("SELECT NOW()").getSingleResult();
	}

	/**
	 * Prüft, ob ein eindeutig zu vergebener Wert bereits durch eine andere Entity verwendet wird.
	 * 
	 * @param uuid
	 *            uuid der aktuell in Bearbeitung befindlichen Entity.
	 * @param propertyName
	 *            Attribut-Name des zu prüfenden Werts.
	 * @param propertyValue
	 *            Zu prüfender eindeutiger Wert.
	 * @return
	 *         <ol>
	 *         <li>{@code true}, wenn {@code propertyValue == null}.</li>
	 *         <li>{@code true}, wenn {@code propertyValue} durch die Entity mit {@code uuid} verwendet wird.</li>
	 *         <li>{@code false}, wenn {@code propertyValue} bereits durch einen andere Entity verwendet wird.</li>
	 *         </ol>
	 */
	protected boolean isUnique(String uuid, String propertyName, Object propertyValue) {

		if (propertyValue == null) {
			return true;
		} else {
			ADO foundEntity = getByUniqueAttribute(propertyName, propertyValue);
			return foundEntity == null || foundEntity.getUuid().equals(uuid);
		}
	}

	/**
	 * Lädt eine Entity anhand einem als eindeutig erwartetem Attribut.
	 * 
	 * @param propertyName
	 *            Attribut-Name des zu prüfenden Werts.
	 * @param propertyValue
	 *            Zu prüfender eindeutiger Wert.
	 * @return {@code null}, wenn es keine Entity gibt, die {@code propertyValue} gesetzt hat.
	 */
	protected ADO getByUniqueAttribute(String propertyName, Object propertyValue) {

//		return JpaHelper.simpleSingleQuery(em, elementClass, propertyName, propertyValue);
		return null;
	}

	public static <T> StringBuilder appendInFilterValues(
		StringBuilder filterBuilder,
		List<Object> filterBuilderParameters,
		List<T> values,
		Function<T, ?> valueMapper) {

		filterBuilder.append("(");
		boolean first = true;
		for (T value : values) {
			if (first) {
				filterBuilder.append("?");
				first = false;
			} else {
				filterBuilder.append(",?");
			}
			filterBuilder.append(filterBuilderParameters.size() + 1);
			filterBuilderParameters.add(valueMapper.apply(value));
		}
		filterBuilder.append(")");
		return filterBuilder;
	}

	public List<Long> getIdsByReferenceDtos(List<? extends ReferenceDto> references) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(getElementClass());

		cq.where(from.get(AbstractDomainObject.UUID).in(references.stream().map(ReferenceDto::getUuid).collect(Collectors.toList())));
		cq.select(from.get(AbstractDomainObject.ID));
		return em.createQuery(cq).getResultList();
	}
}

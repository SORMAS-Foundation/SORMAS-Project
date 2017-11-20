package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * @author Martin Wahnschaffe
 * @param <ADO>
 */
public abstract class AbstractAdoService<ADO extends AbstractDomainObject> implements AdoService<ADO> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private SessionContext context;

	private final Class<ADO> elementClass;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	public AbstractAdoService(Class<ADO> elementClass) {
		this.elementClass = elementClass;
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
		long count = em.createQuery(cq).getSingleResult();
		return count;
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

	public List<ADO> getAllAfter(Date date, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from, user);		
		if (date != null) {
			Predicate dateFilter = createDateFilter(cb, cq, from, date);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}			
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(from.get(Case.CHANGE_DATE)));
		cq.distinct(true);

		List<ADO> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<String> getAllUuids(User user) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ADO> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from, user);
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.select(from.get(AbstractDomainObject.UUID));
		return em.createQuery(cq).getResultList();
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

	/**
	 * Used by most getAll* and getAllUuids methods to filter by user 
	 */
	public abstract Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<ADO,ADO> from, User user);

	public Predicate createDateFilter(CriteriaBuilder cb, CriteriaQuery cq, From<ADO,ADO> from, Date date) {		
		Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
		return dateFilter;
	}
	
	@Override
	public ADO getById(long id) {
		ADO result = em.find(getElementClass(), id);
		return result;
	}

	public ADO getByReferenceDto(ReferenceDto dto) {
		if (dto != null) {
			return getByUuid(dto.getUuid());
		} else {
			return null;
		}
	}
	
	@Override
	public ADO getByUuid(String uuid) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> uuidParam = cb.parameter(String.class, AbstractDomainObject.UUID);
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(AbstractDomainObject.UUID), uuidParam));
		
		TypedQuery<ADO> q = em.createQuery(cq)
			.setParameter(uuidParam, uuid);
		
		ADO entity = q.getResultList().stream()
				.findFirst()
				.orElse(null);
		
		return entity;
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
}

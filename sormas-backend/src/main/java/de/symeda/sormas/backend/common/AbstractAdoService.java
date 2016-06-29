package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.ejb.SessionContext;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.user.Permission;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * @author Martin Wahnschaffe
 * @param <ADO>
 */
public abstract class AbstractAdoService<ADO extends AbstractDomainObject> implements AdoService<ADO> {


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

	@Override
	public List<ADO> getAll() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	@Override
	public ADO getById(long id) {
		ADO result = em.find(getElementClass(), id);
		return result;
	}

	@Override
	public ADO getByUuid(String uuid) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> uuidParam = cb.parameter(String.class, "uuid");
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
	 * <u>Achtung:</u> Für korrekte Funktion muss die aufrufende Klasse {@link Permission#_SYSTEM_ROLE} in {@link DeclareRoles} definiert
	 * haben.
	 * 
	 * @return {@code true}, wenn das System selbst der ausführende Benutzer ist.
	 */
	protected boolean isSystem() {
		return context.isCallerInRole(Permission._SYSTEM_ROLE);
	}

	/**
	 * <u>Achtung:</u> Für korrekte Funktion muss die aufrufende Klasse {@link Permission#_ADMIN} in {@link DeclareRoles} definiert
	 * haben.
	 * 
	 * @return {@code true}, wenn der ausführende Benutzer {@link Permission#ADMIN} hat.
	 */
	protected boolean isAdmin() {
		return hasPermission(Permission.ADMIN);
	}

	/**
	 * <u>Achtung:</u> Für korrekte Funktion muss die aufrufende Klasse die entsprechende Permission in {@link DeclareRoles} definiert
	 * haben.
	 * 
	 * @param permission
	 * @return {@code true}, wenn der ausführende Benutzer die angegebene {@code permission} hat.
	 */
	protected boolean hasPermission(Permission permission) {
		return context.isCallerInRole(permission.name());
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

package de.symeda.sormas.backend.deletionconfiguration;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.deletionconfiguration.DeletionReference;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

public abstract class AbstractCoreEntityFacade<T extends AbstractDomainObject> {

	private Class<T> entityClass;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	protected AbstractCoreEntityFacade(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public void executeAutomaticDeletion(DeletionReference deletionReference, Date referenceDeletionDate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);
		Root<T> from = cq.from(entityClass);

		cq.where(cb.lessThanOrEqualTo(from.get(getDeleteReferenceField(deletionReference)), referenceDeletionDate));

		List<T> toDeleteEntities = QueryHelper.getResultList(em, cq, null, null);

		toDeleteEntities.forEach(entity -> {
			delete(entity);
		});
	}

	protected abstract void delete(T entity);

	protected String getDeleteReferenceField(DeletionReference deletionReference) {
		switch (deletionReference) {
		case CREATION:
			return AbstractDomainObject.CREATION_DATE;
		case END:
			return AbstractDomainObject.CHANGE_DATE;
		default:
			throw new IllegalArgumentException("deletion reference " + deletionReference + " not supported in " + getClass().getSimpleName());
		}
	}
}

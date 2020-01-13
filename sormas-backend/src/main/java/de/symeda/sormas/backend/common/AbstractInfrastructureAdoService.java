package de.symeda.sormas.backend.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class AbstractInfrastructureAdoService<ADO extends InfrastructureAdo> extends AbstractAdoService<ADO> {

	public AbstractInfrastructureAdoService(Class<ADO> elementClass) {
		super(elementClass);
	}

	public void archive(ADO archiveme) {
		archiveme.setArchived(true);
		em.persist(archiveme);
		em.flush();
	}
	
	public Predicate createBasicFilter() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		
		return cb.isFalse(from.get(InfrastructureAdo.ARCHIVED));
	}

	public List<ADO> getAllActive() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.where(createBasicFilter());
		cq.orderBy(cb.desc(from.get(AbstractDomainObject.CHANGE_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public List<ADO> getAllActive(String orderProperty, boolean asc) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ADO> cq = cb.createQuery(getElementClass());
		Root<ADO> from = cq.from(getElementClass());
		cq.where(createBasicFilter());
		cq.orderBy(asc ? cb.asc(from.get(orderProperty)) : cb.desc(from.get(orderProperty)));

		return em.createQuery(cq).getResultList();
	}

	public <T extends InfrastructureAdo> boolean isUsedInInfrastructureData(String uuid, String adoAttribute, Class<T> targetElementClass) {
		Set<String> uuidSet = new HashSet<>();
		uuidSet.add(uuid);
		return isUsedInInfrastructureData(uuidSet, adoAttribute, targetElementClass);
	}

	public <T extends InfrastructureAdo> boolean isUsedInInfrastructureData(Set<String> uuids, String adoAttribute, Class<T> targetElementClass) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(targetElementClass);
		Root<T> root = cq.from(targetElementClass);
		Join<T, ADO> join = root.join(adoAttribute);

		cq.where(
				cb.and(
						cb.or(
								cb.isNull(root.get(InfrastructureAdo.ARCHIVED)),
								cb.isFalse(root.get(InfrastructureAdo.ARCHIVED))
								),
						join.get(InfrastructureAdo.UUID).in(uuids)
						)
				);

		cq.select(join.get(InfrastructureAdo.ID));

		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
	}

}
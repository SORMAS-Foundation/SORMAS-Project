package de.symeda.sormas.backend.common;

import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
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

	public <T extends InfrastructureAdo> boolean isUsedInInfrastructureData(String uuid, String adoAttribute, Class<T> targetElementClass) {
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
						cb.equal(join.get(InfrastructureAdo.UUID), uuid)
						)
				);

		cq.select(join.get(InfrastructureAdo.ID));

		return !em.createQuery(cq).setMaxResults(1).getResultList().isEmpty();
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
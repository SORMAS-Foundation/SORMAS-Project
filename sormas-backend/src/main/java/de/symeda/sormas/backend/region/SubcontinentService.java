package de.symeda.sormas.backend.region;

import java.util.Collections;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.region.SubcontinentCriteria;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class SubcontinentService extends AbstractInfrastructureAdoService<Subcontinent> {

	public SubcontinentService() {
		super(Subcontinent.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Subcontinent> from) {
		return null;
	}

	public Predicate buildCriteriaFilter(SubcontinentCriteria criteria, CriteriaBuilder cb, Root<Subcontinent> from) {

		Predicate filter = null;
		if (criteria.getContinent() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(Subcontinent.CONTINENT, JoinType.LEFT).get(Continent.UUID), criteria.getContinent().getUuid()));
		}
		if (criteria.getNameLike() != null) {
			filter = CriteriaBuilderHelper.and(cb, cb.like(cb.lower(from.get(Subcontinent.DEFAULT_NAME)), criteria.getNameLike().toLowerCase()));
		}
		filter = addRelevancePredicate(cb, from, filter, criteria.getRelevanceStatus());
		return filter;
	}

	public List<Subcontinent> getByDefaultName(String name, boolean includeArchivedEntities) {
		if (name == null) {
			return Collections.emptyList();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Subcontinent> cq = cb.createQuery(getElementClass());
		Root<Subcontinent> from = cq.from(getElementClass());

		Predicate filter = CriteriaBuilderHelper.unaccentedIlikePrecise(cb, from.get(Subcontinent.DEFAULT_NAME), name.trim());
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	public List<Subcontinent> getByExternalId(String externalId, boolean includeArchived) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Subcontinent> cq = cb.createQuery(getElementClass());
		Root<Subcontinent> from = cq.from(getElementClass());

		Predicate filter = CriteriaBuilderHelper.ilikePrecise(cb, from.get(Subcontinent.EXTERNAL_ID), externalId.trim());
		if (!includeArchived) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}
}

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

import de.symeda.sormas.api.region.SubContinentCriteria;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class SubContinentService extends AbstractInfrastructureAdoService<SubContinent> {

	public SubContinentService() {
		super(SubContinent.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, SubContinent> from) {
		return null;
	}

	public Predicate buildCriteriaFilter(SubContinentCriteria criteria, CriteriaBuilder cb, Root<SubContinent> from) {

		Predicate filter = null;
		if (criteria.getContinent() != null) {
			filter = CriteriaBuilderHelper
					.and(cb, filter, cb.equal(from.join(SubContinent.CONTINENT, JoinType.LEFT).get(Continent.UUID), criteria.getContinent().getUuid()));
		}
		if (criteria.getNameLike() != null) {
			filter = CriteriaBuilderHelper.and(cb, cb.like(cb.lower(from.get(SubContinent.DEFAULT_NAME)), criteria.getNameLike().toLowerCase()));
		}
		filter = addRelevancePredicate(cb, from, filter, criteria.getRelevanceStatus());
		return filter;
	}

	public List<SubContinent> getByDefaultName(String name, boolean includeArchivedEntities) {
		if (name == null) {
			return Collections.emptyList();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SubContinent> cq = cb.createQuery(getElementClass());
		Root<SubContinent> from = cq.from(getElementClass());

		Predicate filter = cb.or(
			cb.equal(cb.trim(from.get(SubContinent.DEFAULT_NAME)), name.trim()),
			cb.equal(cb.lower(cb.trim(from.get(SubContinent.DEFAULT_NAME))), name.trim().toLowerCase()));
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}
}

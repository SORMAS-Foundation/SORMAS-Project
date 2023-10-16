package de.symeda.sormas.backend.infrastructure.continent;

import java.util.Collections;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.infrastructure.continent.ContinentCriteria;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureAdoService;

@Stateless
@LocalBean
public class ContinentService extends AbstractInfrastructureAdoService<Continent, ContinentCriteria> {

	public ContinentService() {
		super(Continent.class);
	}

	public List<Continent> getByDefaultName(String name, boolean includeArchivedEntities) {
		if (name == null) {
			return Collections.emptyList();
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Continent> cq = cb.createQuery(getElementClass());
		Root<Continent> from = cq.from(getElementClass());

		Predicate filter = CriteriaBuilderHelper.unaccentedIlikePrecise(cb, from.get(Continent.DEFAULT_NAME), name.trim());
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Continent> from) {
		return null;
	}

	@Override
	public Predicate buildCriteriaFilter(ContinentCriteria criteria, CriteriaBuilder cb, Root<Continent> from) {

		Predicate filter = null;
		if (criteria != null) {
			if (criteria.getNameLike() != null) {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Continent.DEFAULT_NAME), criteria.getNameLike().toLowerCase()));
			}
			filter = addRelevancePredicate(cb, from, filter, criteria.getRelevanceStatus());
		}
		return filter;
	}

	public List<Continent> getByExternalId(String externalId, boolean includeArchived) {
		return getByExternalId(externalId, Continent.EXTERNAL_ID, includeArchived);
	}

}

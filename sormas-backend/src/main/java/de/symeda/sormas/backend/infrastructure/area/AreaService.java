package de.symeda.sormas.backend.infrastructure.area;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.area.AreaCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.infrastructure.region.Region;

@Stateless
@LocalBean
public class AreaService extends AbstractInfrastructureAdoService<Area, AreaCriteria> {

	public AreaService() {
		super(Area.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Area> from) {
		return null;
	}

	public List<Area> getByName(String name, boolean includeArchivedEntities) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Area> cq = cb.createQuery(getElementClass());
		Root<Area> from = cq.from(getElementClass());
		Predicate filter = CriteriaBuilderHelper.unaccentedIlikePrecise(cb, from.get(Area.NAME), name.trim());
		if (!includeArchivedEntities) {
			filter = cb.and(filter, createBasicFilter(cb, from));
		}

		cq.where(filter);

		return em.createQuery(cq).getResultList();
	}

	@Override
	public Predicate buildCriteriaFilter(AreaCriteria criteria, CriteriaBuilder cb, Root<Area> areaRoot) {
		Predicate filter = null;
		if (StringUtils.isNotBlank(criteria.getTextFilter())) {
			String[] textFilters = criteria.getTextFilter().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = CriteriaBuilderHelper.unaccentedIlike(cb, areaRoot.get(Region.NAME), textFilter);
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(areaRoot.get(Area.ARCHIVED), false), cb.isNull(areaRoot.get(Area.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(areaRoot.get(Area.ARCHIVED), true));
			}
		}
		return filter;
	}

	@Override
	public List<Area> getByExternalId(String externalId, boolean includeArchived) {
		return getByExternalId(externalId, Area.EXTERNAL_ID, includeArchived);
	}
}

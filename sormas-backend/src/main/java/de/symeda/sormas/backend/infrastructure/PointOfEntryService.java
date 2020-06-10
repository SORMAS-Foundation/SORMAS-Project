package de.symeda.sormas.backend.infrastructure;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.infrastructure.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractInfrastructureAdoService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;

@Stateless
@LocalBean
public class PointOfEntryService extends AbstractInfrastructureAdoService<PointOfEntry> {

	@EJB
	private RegionService regionService;

	public PointOfEntryService() {
		super(PointOfEntry.class);
	}

	public List<PointOfEntry> getAllByDistrict(District district, boolean includeOthers) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntry> cq = cb.createQuery(getElementClass());
		Root<PointOfEntry> pointOfEntry = cq.from(getElementClass());

		Predicate filter = cb.and(cb.equal(pointOfEntry.get(PointOfEntry.DISTRICT), district), cb.equal(pointOfEntry.get(PointOfEntry.ACTIVE), true));

		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(pointOfEntry.get(PointOfEntry.NAME)));

		List<PointOfEntry> pointsOfEntry = em.createQuery(cq).getResultList();

		if (includeOthers) {
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_AIRPORT_UUID));
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_SEAPORT_UUID));
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID));
			pointsOfEntry.add(getByUuid(PointOfEntryDto.OTHER_POE_UUID));
		}

		return pointsOfEntry;
	}

	public List<PointOfEntry> getByName(String name, District district) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntry> cq = cb.createQuery(getElementClass());
		Root<PointOfEntry> from = cq.from(getElementClass());

		Predicate filter = cb.or(
			cb.equal(cb.trim(from.get(PointOfEntry.NAME)), name.trim()),
			cb.equal(cb.lower(cb.trim(from.get(PointOfEntry.NAME))), name.trim().toLowerCase()));
		if (!PointOfEntryDto.isNameOtherPointOfEntry(name.trim())) {
			filter = cb.and(filter, cb.equal(from.get(PointOfEntry.DISTRICT), district));
		}

		cq.where(filter);
		return em.createQuery(cq).getResultList();
	}

	public Predicate buildCriteriaFilter(PointOfEntryCriteria criteria, CriteriaBuilder cb, Root<PointOfEntry> pointOfEntry) {

		Predicate filter = null;
		if (criteria.getRegion() != null) {
			filter =
				and(cb, filter, cb.equal(pointOfEntry.join(PointOfEntry.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = and(
				cb,
				filter,
				cb.equal(pointOfEntry.join(PointOfEntry.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getType() != null) {
			filter = and(cb, filter, cb.equal(pointOfEntry.get(PointOfEntry.POINT_OF_ENTRY_TYPE), criteria.getType()));
		}
		if (criteria.getActive() != null) {
			filter = and(cb, filter, cb.equal(pointOfEntry.get(PointOfEntry.ACTIVE), criteria.getActive()));
		}
		if (criteria.getNameLike() != null) {
			String[] textFilters = criteria.getNameLike().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.like(cb.lower(pointOfEntry.get(PointOfEntry.NAME)), textFilter);
					filter = and(cb, filter, likeFilters);
				}
			}
		}
		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = and(
					cb,
					filter,
					cb.or(cb.equal(pointOfEntry.get(PointOfEntry.ARCHIVED), false), cb.isNull(pointOfEntry.get(PointOfEntry.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = and(cb, filter, cb.equal(pointOfEntry.get(PointOfEntry.ARCHIVED), true));
			}
		}

		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<PointOfEntry, PointOfEntry> from) {
		return null;
	}
}

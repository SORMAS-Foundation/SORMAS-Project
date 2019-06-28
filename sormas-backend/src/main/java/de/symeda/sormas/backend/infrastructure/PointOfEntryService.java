package de.symeda.sormas.backend.infrastructure;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class PointOfEntryService extends AbstractAdoService<PointOfEntry> {
	
	public PointOfEntryService() {
		super(PointOfEntry.class);
	}
	
	public List<PointOfEntry> getAllByDistrict(District district, boolean includeOthers) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PointOfEntry> cq = cb.createQuery(getElementClass());
		Root<PointOfEntry> pointOfEntry = cq.from(getElementClass());
		
		Predicate filter = cb.equal(pointOfEntry.get(PointOfEntry.DISTRICT), district);

		if (!includeOthers) {
			Predicate excludeFilter = cb.and(
					cb.notEqual(pointOfEntry.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_AIRPORT_UUID),
					cb.notEqual(pointOfEntry.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_SEAPORT_UUID),
					cb.notEqual(pointOfEntry.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_GROUND_CROSSING_UUID),
					cb.notEqual(pointOfEntry.get(PointOfEntry.UUID), PointOfEntryDto.OTHER_POE_UUID));
			filter = cb.and(filter, excludeFilter);
		}
		
		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(pointOfEntry.get(PointOfEntry.NAME)));
		
		return em.createQuery(cq).getResultList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<PointOfEntry, PointOfEntry> from, User user) {
		return null;
	}

}

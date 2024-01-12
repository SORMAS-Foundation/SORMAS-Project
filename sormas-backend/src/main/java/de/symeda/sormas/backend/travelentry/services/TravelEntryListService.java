package de.symeda.sormas.backend.travelentry.services;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.travelentry.TravelEntryListEntryDto;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryJoins;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.travelentry.transformers.TravelEntryListEntryDtoResultTransformer;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class TravelEntryListService extends BaseTravelEntryService {

	public List<TravelEntryListEntryDto> getEntriesList(Long personId, Long caseId, Integer first, Integer max) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		final Root<TravelEntry> travelEntry = cq.from(TravelEntry.class);

		final TravelEntryQueryContext travelEntryQueryContext = new TravelEntryQueryContext(cb, cq, travelEntry);

		final TravelEntryJoins joins = travelEntryQueryContext.getJoins();
		final Join<TravelEntry, PointOfEntry> pointOfEntry = joins.getPointOfEntry();

		cq.multiselect(
			travelEntry.get(TravelEntry.UUID),
			travelEntry.get(TravelEntry.REPORT_DATE),
			travelEntry.get(TravelEntry.DISEASE),
			pointOfEntry.get(PointOfEntry.NAME),
			travelEntry.get(TravelEntry.POINT_OF_ENTRY_DETAILS),
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(travelEntryQueryContext)),
			travelEntry.get(TravelEntry.CHANGE_DATE));

		final Predicate criteriaFilter = buildListEntryCriteriaFilter(personId, caseId, travelEntryQueryContext);

		if (criteriaFilter != null) {
			cq.where(criteriaFilter);
		}

		cq.orderBy(cb.desc(travelEntry.get(TravelEntry.CHANGE_DATE)));

		cq.distinct(true);

		return QueryHelper.getResultList(em, cq, new TravelEntryListEntryDtoResultTransformer(), first, max);
	}

	private Predicate buildListEntryCriteriaFilter(Long personId, Long caseId, TravelEntryQueryContext travelEntryQueryContext) {

		final CriteriaBuilder cb = travelEntryQueryContext.getCriteriaBuilder();
		final From<?, TravelEntry> from = travelEntryQueryContext.getRoot();

		Predicate filter = createUserFilter(travelEntryQueryContext, false);

		if (personId != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(TravelEntry.PERSON_ID), personId));
		}

		if (caseId != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(TravelEntry.RESULTING_CASE_ID), caseId));
		}

		filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));

		return filter;
	}

}

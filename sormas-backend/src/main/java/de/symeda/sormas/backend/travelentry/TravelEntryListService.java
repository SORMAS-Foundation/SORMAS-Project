package de.symeda.sormas.backend.travelentry;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.travelentry.TravelEntryListEntryDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.travelentry.transformers.TravelEntryListEntryDtoResultTransformer;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless
@LocalBean
public class TravelEntryListService extends TravelEntryService {

	public List<TravelEntryListEntryDto> getEntriesList(Long personId, String caseUuid, Integer first, Integer max) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<TravelEntry> travelEntry = cq.from(TravelEntry.class);

		final TravelEntryQueryContext travelEntryQueryContext = new TravelEntryQueryContext(cb, cq, travelEntry);

		final TravelEntryJoins<TravelEntry> joins = (TravelEntryJoins<TravelEntry>) travelEntryQueryContext.getJoins();
		final Join<TravelEntry, PointOfEntry> pointOfEntry = joins.getPointOfEntry();

		cq.multiselect(
			travelEntry.get(TravelEntry.UUID),
			travelEntry.get(TravelEntry.REPORT_DATE),
			travelEntry.get(TravelEntry.DISEASE),
			pointOfEntry.get(PointOfEntry.NAME),
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(travelEntryQueryContext)),
			travelEntry.get(TravelEntry.CHANGE_DATE));

		final Predicate criteriaFilter = buildListEntryCriteriaFilter(personId, caseUuid, travelEntryQueryContext);

		if (criteriaFilter != null) {
			cq.where(criteriaFilter);
		}

		cq.orderBy(cb.desc(travelEntry.get(TravelEntry.CHANGE_DATE)));

		cq.distinct(true);

		return createQuery(cq, first, max).unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new TravelEntryListEntryDtoResultTransformer())
			.getResultList();
	}

	private Predicate buildListEntryCriteriaFilter(Long personId, String caseUuid, TravelEntryQueryContext travelEntryQueryContext) {

		final TravelEntryJoins joins = (TravelEntryJoins) travelEntryQueryContext.getJoins();
		final CriteriaBuilder cb = travelEntryQueryContext.getCriteriaBuilder();
		final From<?, TravelEntry> from = travelEntryQueryContext.getRoot();
		Join<TravelEntry, Case> resultingCase = joins.getResultingCase();

		Predicate filter = createUserFilter(travelEntryQueryContext);

		if (personId != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(TravelEntry.PERSON_ID), personId));
		}

		if (caseUuid != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(resultingCase.get(Case.UUID), caseUuid));
		}

		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(TravelEntry.DELETED)));

		return filter;
	}
}

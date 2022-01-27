package de.symeda.sormas.backend.travelentry.services;

import javax.ejb.EJB;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDeletableAdoService;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryJurisdictionPredicateValidator;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

import java.util.Date;
import java.util.List;

public class BaseTravelEntryService extends AbstractCoreAdoService<TravelEntry> {

	@EJB
	protected UserService userService;

	public BaseTravelEntryService() {
		super(TravelEntry.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, TravelEntry> travelEntryPath) {
		return inJurisdictionOrOwned(new TravelEntryQueryContext(cb, cq, travelEntryPath));
	}

	public Predicate inJurisdictionOrOwned(TravelEntryQueryContext qc, User user) {
		return TravelEntryJurisdictionPredicateValidator.of(qc, user).inJurisdictionOrOwned();
	}

	public Predicate inJurisdictionOrOwned(TravelEntryQueryContext qc) {
		return inJurisdictionOrOwned(qc, userService.getCurrentUser());
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, TravelEntry> root) {
		return cb.isFalse(root.get(TravelEntry.DELETED));
	}

	protected Predicate createUserFilter(TravelEntryQueryContext travelEntryQueryContext) {
		return inJurisdictionOrOwned(travelEntryQueryContext);
	}

	public List<TravelEntry> getAllActiveAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TravelEntry> cq = cb.createQuery(getElementClass());
		Root<TravelEntry> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			if (userFilter != null) {
				filter = cb.and(filter, userFilter);
			}
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date));
			if (dateFilter != null) {
				filter = cb.and(filter, dateFilter);
			}
		}
		cq.where(filter);
		cq.orderBy(cb.desc(from.get(TravelEntry.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	@Override
	public List<TravelEntry> getAllActiveAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {
		return getAllActiveAfter(date);
	}
}

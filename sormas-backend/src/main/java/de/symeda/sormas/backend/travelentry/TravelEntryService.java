package de.symeda.sormas.backend.travelentry;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

@Stateless
@LocalBean
public class TravelEntryService extends AbstractCoreAdoService<TravelEntry> {

	@EJB
	private UserService userService;

	public TravelEntryService() {
		super(TravelEntry.class);
	}

	public boolean injurisdictionOrOwned(TravelEntry travelEntry) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<TravelEntry> root = cq.from(TravelEntry.class);
		cq.multiselect(JurisdictionHelper.booleanSelector(cb, injurisdictionOrOwned(new TravelEntryQueryContext(cb, cq, root))));
		cq.where(cb.equal(root.get(TravelEntry.UUID), travelEntry.getUuid()));
		return em.createQuery(cq).getSingleResult();
	}

	public Predicate injurisdictionOrOwned(TravelEntryQueryContext qc) {
		final User currentUser = userService.getCurrentUser();
		return TravelEntryJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, TravelEntry> travelEntryPath) {
		return injurisdictionOrOwned(new TravelEntryQueryContext(cb, cq, travelEntryPath));
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, TravelEntry> root) {
		return cb.isFalse(root.get(TravelEntry.DELETED));
	}

	public Predicate buildCriteriaFilter(TravelEntryCriteria criteria, CriteriaBuilder cb, Root<TravelEntry> from) {
		return cb.conjunction();
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
}

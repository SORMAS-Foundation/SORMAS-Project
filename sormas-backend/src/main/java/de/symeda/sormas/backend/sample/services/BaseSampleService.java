package de.symeda.sormas.backend.sample.services;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipantQueryContext;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleJurisdictionPredicateValidator;
import de.symeda.sormas.backend.sample.SampleQueryContext;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;

public class BaseSampleService extends AbstractCoreAdoService<Sample> {

	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;

	public BaseSampleService() {
		super(Sample.class);
	}

	@Override
	@Deprecated
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Sample> from) {
		return createUserFilter(cq, cb, new SampleJoins<>(from), new SampleCriteria());
	}

	public Predicate createUserFilter(CriteriaQuery cq, CriteriaBuilder cb, SampleJoins joins, SampleCriteria criteria) {

		Predicate filter = createUserFilterWithoutAssociations(cb, joins);

		User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getCalculatedJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.LABORATORY || jurisdictionLevel == JurisdictionLevel.EXTERNAL_LABORATORY) {
			return filter;
		}

		if (criteria != null) {
			filter = getUSerFilterFromSampleAssociations(cq, cb, joins, criteria.getSampleAssociationType(), filter, currentUser);
		}

		return filter;
	}

	protected Predicate getUSerFilterFromSampleAssociations(
		CriteriaQuery cq,
		CriteriaBuilder cb,
		SampleJoins joins,
		SampleAssociationType sampleAssociationType,
		Predicate filter,
		User currentUser) {
		if (sampleAssociationType != null && sampleAssociationType != SampleAssociationType.ALL) {
			if (sampleAssociationType == SampleAssociationType.CASE) {
				filter = CriteriaBuilderHelper.or(cb, filter, caseService.createUserFilter(cb, cq, joins.getCaze(), null));
			} else if (sampleAssociationType == SampleAssociationType.CONTACT) {
				filter = CriteriaBuilderHelper.or(cb, filter, contactService.createUserFilterForJoin(cb, cq, joins.getContact()));
			} else if (sampleAssociationType == SampleAssociationType.EVENT_PARTICIPANT) {
				filter = CriteriaBuilderHelper.or(cb, filter, eventParticipantService.createUserFilterForJoin(cb, cq, joins.getEventParticipant()));
			}
		} else if (currentUser.getLimitedDisease() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				CriteriaBuilderHelper.or(
					cb,
					caseService.createUserFilter(cb, cq, joins.getCaze(), null),
					contactService.createUserFilterForJoin(cb, cq, joins.getContact()),
					eventParticipantService.createUserFilterForJoin(cb, cq, joins.getEventParticipant())));
		} else {
			filter = CriteriaBuilderHelper.or(
				cb,
				filter,
				caseService.createUserFilter(cb, cq, joins.getCaze(), null),
				contactService.createUserFilterForJoin(cb, cq, joins.getContact()),
				eventParticipantService.createUserFilterForJoin(cb, cq, joins.getEventParticipant()));
		}
		return filter;
	}

	/**
	 * Creates a user filter that does not take sample associations into account, i.e. their associated cases, contacts, and event
	 * participants. Instead, it filters for samples of the user's laboratory (if present) and removes samples with diseases
	 * that the user can't access if they have a limited disease set. SHOULD GENERALLY NOT BE USED WITHOUT A PROPER USER FILTER!
	 */
	public Predicate createUserFilterWithoutAssociations(CriteriaBuilder cb, SampleJoins joins) {
		Predicate filter = null;

		User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getCalculatedJurisdictionLevel();
		// Lab users can see samples assigned to their laboratory
		if (jurisdictionLevel == JurisdictionLevel.LABORATORY || jurisdictionLevel == JurisdictionLevel.EXTERNAL_LABORATORY) {
			if (currentUser.getLaboratory() != null) {
				filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(joins.getLab(), currentUser.getLaboratory()));
			}
		}

		// Only show samples of a specific disease if a limited disease is set
		if (currentUser.getLimitedDisease() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.and(cb.isNotNull(joins.getEvent()), cb.isNull(joins.getEvent().get(Event.DISEASE))),
					cb.equal(
						cb.selectCase()
							.when(cb.isNotNull(joins.getCaze()), joins.getCaze().get(Case.DISEASE))
							.when(cb.isNotNull(joins.getContact()), joins.getContact().get(Contact.DISEASE))
							.otherwise(joins.getEvent().get(Event.DISEASE)),
						currentUser.getLimitedDisease())));
		}

		return filter;
	}

	public List<Selection<?>> getJurisdictionSelections(SampleQueryContext qc) {

		CriteriaBuilder cb = qc.getCriteriaBuilder();
		SampleJoins joins = (SampleJoins) qc.getJoins();
		CriteriaQuery cq = qc.getQuery();
		ContactJoins<Sample> contactJoins = new ContactJoins(joins.getContact());
		return Arrays.asList(
			JurisdictionHelper.booleanSelector(cb, inJurisdictionOrOwned(qc)),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(cb.isNotNull(joins.getCaze()), caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, cq, joins.getCaze())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(cb.isNotNull(joins.getContact()), contactService.inJurisdictionOrOwned(new ContactQueryContext(cb, cq, joins.getContact())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getContact()),
					cb.isNotNull(contactJoins.getCaze()),
					caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, cq, contactJoins.getCaze())))),
			JurisdictionHelper.booleanSelector(
				cb,
				cb.and(
					cb.isNotNull(joins.getEventParticipant()),
					eventParticipantService.inJurisdictionOrOwned(new EventParticipantQueryContext(cb, cq, joins.getEventParticipant())))));
	}

	public Predicate inJurisdictionOrOwned(SampleQueryContext qc) {
		final User currentUser = userService.getCurrentUser();
		return SampleJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
	}
}

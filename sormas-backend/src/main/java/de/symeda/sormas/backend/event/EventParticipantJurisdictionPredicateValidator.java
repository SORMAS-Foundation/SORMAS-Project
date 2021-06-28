package de.symeda.sormas.backend.event;

import java.util.Collections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;
import de.symeda.sormas.utils.EventJoins;
import de.symeda.sormas.utils.EventParticipantJoins;

public class EventParticipantJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private EventParticipantJoins<?> joins;
	private User currentUser;

	public static EventParticipantJurisdictionPredicateValidator of(CriteriaBuilder cb, EventParticipantJoins<?> joins, User currentUser) {
		return new EventParticipantJurisdictionPredicateValidator(cb, joins, currentUser);
	}

	private EventParticipantJurisdictionPredicateValidator(CriteriaBuilder cb, EventParticipantJoins<?> joins, User currentUser) {
		super(cb, Collections.singletonList(EventJurisdictionPredicateValidator.of(cb, new EventJoins<>(joins.getEvent()), currentUser)));
		this.joins = joins;
		this.currentUser = currentUser;
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.equal(joins.getEventParticipantReportingUser().get(User.UUID), currentUser.getUuid());

		return cb.or(reportedByCurrentUser, isInJurisdiction());
	}

	@Override
	protected Predicate isInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(currentUser.getJurisdictionLevel());
	}

	@Override
	protected Predicate whenNotAllowed() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenNationalLevel() {
		return cb.conjunction();
	}

	@Override
	protected Predicate whenRegionalLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getEventParticipantResponsibleRegion().get(Region.ID), currentUser.getRegion().getId()),
			cb.equal(joins.getEventAddressRegion().get(Region.ID), currentUser.getRegion().getId()));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return CriteriaBuilderHelper.or(
			cb,
			cb.equal(joins.getEventParticipantResponsibleDistrict().get(District.ID), currentUser.getDistrict().getId()),
			cb.equal(joins.getEventAddressDistrict().get(District.ID), currentUser.getDistrict().getId()));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return CriteriaBuilderHelper.or(cb, cb.equal(joins.getEventAddressCommunity().get(Community.ID), currentUser.getCommunity().getId()));
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.equal(joins.getAddressFacility().get(Facility.ID), currentUser.getHealthFacility().getId());
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		return cb.disjunction();
	}
}

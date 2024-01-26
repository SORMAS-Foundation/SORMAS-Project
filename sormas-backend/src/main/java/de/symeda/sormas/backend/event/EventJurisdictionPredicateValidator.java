/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.event;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class EventJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final EventJoins joins;
	private final CriteriaQuery<?> cq;

	private EventJurisdictionPredicateValidator(EventQueryContext qc, User user) {
		super(qc.getCriteriaBuilder(), user, null, null);
		this.joins = qc.getJoins();
		this.cq = qc.getQuery();
	}

	private EventJurisdictionPredicateValidator(EventQueryContext qc, Path userPath) {
		super(qc.getCriteriaBuilder(), null, userPath, null);
		this.joins = qc.getJoins();
		this.cq = qc.getQuery();
	}

	public static EventJurisdictionPredicateValidator of(EventQueryContext qc, User user) {
		return new EventJurisdictionPredicateValidator(qc, user);
	}

	public static EventJurisdictionPredicateValidator of(EventQueryContext qc, Path userPath) {
		return new EventJurisdictionPredicateValidator(qc, userPath);
	}

	@Override
	public Predicate isRootInJurisdiction() {
		return super.isRootInJurisdiction();
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = getReportedByCurrentUser();
		final Predicate currentUserResponsible = getCurrentUserResponsible();

		return cb.or(reportedByCurrentUser, currentUserResponsible, this.isRootInJurisdiction());
	}

	private Predicate getReportedByCurrentUser() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(Event.REPORTING_USER)),
			user != null
				? cb.equal(joins.getRoot().get(Event.REPORTING_USER).get(User.ID), user.getId())
				: cb.equal(joins.getRoot().get(Event.REPORTING_USER).get(User.ID), userPath.get(User.ID)));
		return reportedByCurrentUser;
	}

	private Predicate getCurrentUserResponsible() {
		return cb.and(
			cb.isNotNull(joins.getRoot().get(Event.RESPONSIBLE_USER)),
			user != null
				? cb.equal(joins.getRoot().get(Event.RESPONSIBLE_USER).get(User.ID), user.getId())
				: cb.equal(joins.getRoot().get(Event.RESPONSIBLE_USER).get(User.ID), userPath.get(User.ID)));
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		final Predicate reportedByCurrentUser = getReportedByCurrentUser();
		final Predicate currentUserResponsible = getCurrentUserResponsible();
		return cb.or(reportedByCurrentUser, currentUserResponsible);
	}

	@Override
	protected Predicate getLimitedDiseasePredicate() {
		return CriteriaBuilderHelper.limitedDiseasePredicate(cb, user, joins.getRoot().get(Event.DISEASE));
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
		return user != null
			? cb.equal(joins.getLocation().get(Location.REGION).get(Region.ID), user.getRegion().getId())
			: cb.equal(joins.getLocation().get(Location.REGION).get(Region.ID), userPath.get(User.REGION).get(Region.ID));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return user != null
			? cb.equal(joins.getLocation().get(Location.DISTRICT).get(District.ID), user.getDistrict().getId())
			: cb.equal(joins.getLocation().get(Location.DISTRICT).get(District.ID), userPath.get(User.DISTRICT).get(District.ID));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return user != null
			? cb.equal(joins.getLocation().get(Location.COMMUNITY).get(Community.ID), user.getCommunity().getId())
			: cb.equal(joins.getLocation().get(Location.COMMUNITY).get(Community.ID), userPath.get(User.COMMUNITY).get(Community.ID));
	}

	@Override
	protected Predicate whenFacilityLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenPointOfEntryLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenLaboratoryLevel() {
		if (user != null) {
			return EventParticipantJurisdictionPredicateValidator.of(new EventParticipantQueryContext(cb, cq, joins.getEventParticipantJoins()), user)
				.whenLaboratoryLevel();
		} else {
			return EventParticipantJurisdictionPredicateValidator
				.of(new EventParticipantQueryContext(cb, cq, joins.getEventParticipantJoins()), userPath)
				.whenLaboratoryLevel();
		}
	}
}

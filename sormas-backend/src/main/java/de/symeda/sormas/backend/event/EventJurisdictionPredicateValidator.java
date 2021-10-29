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

import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;
import de.symeda.sormas.utils.EventJoins;

public class EventJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final EventJoins<?> joins;
	private final CriteriaQuery<?> cq;

	private EventJurisdictionPredicateValidator(EventQueryContext qc, User user) {
		super(qc.getCriteriaBuilder(), user, null, null);
		this.joins = (EventJoins<?>) qc.getJoins();
		this.cq = qc.getQuery();
	}

	private EventJurisdictionPredicateValidator(EventQueryContext qc, Path userPath) {
		super(qc.getCriteriaBuilder(), null, userPath, null);
		this.joins = (EventJoins<?>) qc.getJoins();
		this.cq = qc.getQuery();
	}

	public static EventJurisdictionPredicateValidator of(EventQueryContext qc, User user) {
		return new EventJurisdictionPredicateValidator(qc, user);
	}

	public static EventJurisdictionPredicateValidator of(EventQueryContext qc, Path userPath) {
		return new EventJurisdictionPredicateValidator(qc, userPath);
	}

	@Override
	protected Predicate isInJurisdiction() {
		return super.isInJurisdiction();
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getReportingUser()),
			user != null
				? cb.equal(joins.getReportingUser().get(User.ID), user.getId())
				: cb.equal(joins.getReportingUser().get(User.ID), userPath.get(User.ID)));

		final Predicate currentUserResponsible = cb.and(
			cb.isNotNull(joins.getResponsibleUser()),
			user != null
				? cb.equal(joins.getResponsibleUser().get(User.ID), user.getId())
				: cb.equal(joins.getResponsibleUser().get(User.ID), userPath.get(User.ID)));

		return cb.or(reportedByCurrentUser, currentUserResponsible, isInJurisdiction());
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
		return cb.equal(joins.getRegion().get(Region.ID), user.getRegion().getId());
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return cb.equal(joins.getDistrict().get(District.ID), user.getDistrict().getId());
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb.equal(joins.getCommunity().get(Community.ID), user.getCommunity().getId());
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
		return EventParticipantJurisdictionPredicateValidator.of(new EventParticipantQueryContext(cb, cq, joins.getEventParticipants()), user)
			.whenLaboratoryLevel();
	}
}

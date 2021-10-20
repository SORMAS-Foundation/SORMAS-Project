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

package de.symeda.sormas.backend.contact;

import java.util.Collections;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleJurisdictionPredicateValidator;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class ContactJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final ContactJoins<?> joins;
	private final CriteriaQuery<?> cq;

	private ContactJurisdictionPredicateValidator(ContactQueryContext qc, User user) {
		super(
			qc.getCriteriaBuilder(),
                user, null, Collections.singletonList(
				CaseJurisdictionPredicateValidator
					.of(new CaseQueryContext<>(qc.getCriteriaBuilder(), qc.getQuery(), ((ContactJoins) qc.getJoins()).getCaze()), user)));

		this.joins = (ContactJoins<?>) qc.getJoins();
		this.cq = qc.getQuery();
	}

	public static ContactJurisdictionPredicateValidator of(ContactQueryContext qc, User user) {
		return new ContactJurisdictionPredicateValidator(qc, user);
	}

	@Override
	protected Predicate isInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(Contact.REPORTING_USER)),
			cb.equal(joins.getRoot().get(Contact.REPORTING_USER).get(User.ID), user.getId()));

		return cb.or(reportedByCurrentUser, inJurisdiction());
	}

	@Override
	protected Predicate isInJurisdiction() {
		return isInJurisdictionByJurisdictionLevel(user.getCalculatedJurisdictionLevel());
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
		return cb.equal(joins.getRoot().get(Contact.REGION).get(Region.ID), user.getRegion().getId());
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return cb.equal(joins.getRoot().get(Contact.DISTRICT).get(District.ID), user.getDistrict().getId());
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb.equal(joins.getRoot().get(Contact.COMMUNITY).get(Community.ID), user.getCommunity().getId());
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

		final Subquery<Long> sampleContactSubquery = cq.subquery(Long.class);
		final Root<Sample> sampleRoot = sampleContactSubquery.from(Sample.class);
		final SampleJoins sampleJoins = new SampleJoins(sampleRoot);
		final Join contactJoin = sampleJoins.getContact();
		SampleJurisdictionPredicateValidator sampleJurisdictionPredicateValidator =
			SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, user);
		sampleContactSubquery.where(cb.and(cb.equal(contactJoin, joins.getRoot()), sampleJurisdictionPredicateValidator.inJurisdictionOrOwned()));
		sampleContactSubquery.select(sampleRoot.get(Sample.ID));
		return cb.exists(sampleContactSubquery);
	}
}

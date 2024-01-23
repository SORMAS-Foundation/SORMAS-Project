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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleJoins;
import de.symeda.sormas.backend.sample.SampleJurisdictionPredicateValidator;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class ContactJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private final ContactJoins joins;
	private final CriteriaQuery<?> cq;

	private ContactJurisdictionPredicateValidator(ContactQueryContext qc, User user) {
		super(
			qc.getCriteriaBuilder(),
			user,
			null,
			Collections.singletonList(
				CaseJurisdictionPredicateValidator
					.of(new CaseQueryContext(qc.getCriteriaBuilder(), qc.getQuery(), (qc.getJoins()).getCaseJoins()), user)));

		this.joins = qc.getJoins();
		this.cq = qc.getQuery();
	}

	private ContactJurisdictionPredicateValidator(ContactQueryContext qc, Path userPath) {
		super(
			qc.getCriteriaBuilder(),
			null,
			userPath,
			Collections.singletonList(
				CaseJurisdictionPredicateValidator
					.of(new CaseQueryContext(qc.getCriteriaBuilder(), qc.getQuery(), (qc.getJoins()).getCaseJoins()), userPath)));

		this.joins = qc.getJoins();
		this.cq = qc.getQuery();
	}

	public static ContactJurisdictionPredicateValidator of(ContactQueryContext qc, User user) {
		return new ContactJurisdictionPredicateValidator(qc, user);
	}

	public static ContactJurisdictionPredicateValidator of(ContactQueryContext qc, Path userPath) {
		return new ContactJurisdictionPredicateValidator(qc, userPath);
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		final Predicate reportedByCurrentUser = getReportedByCurrentUser();

		return cb.or(reportedByCurrentUser, inJurisdiction());
	}

	private Predicate getReportedByCurrentUser() {
		final Predicate reportedByCurrentUser = cb.and(
			cb.isNotNull(joins.getRoot().get(Contact.REPORTING_USER)),
			user != null
				? cb.equal(joins.getRoot().get(Contact.REPORTING_USER).get(User.ID), user.getId())
				: cb.equal(joins.getRoot().get(Contact.REPORTING_USER).get(User.ID), userPath.get(User.ID)));
		return reportedByCurrentUser;
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		final Predicate reportedByCurrentUser = getReportedByCurrentUser();
		final Predicate restrictedAccess = user != null
			? cb.equal(joins.getRoot().get(Contact.CONTACT_OFFICER).get(User.ID), user.getId())
			: cb.equal(joins.getRoot().get(Contact.CONTACT_OFFICER).get(User.ID), userPath.get(User.ID));
		return cb.or(reportedByCurrentUser, restrictedAccess);
	}

	@Override
	protected Predicate getLimitedDiseasePredicate() {
		return CriteriaBuilderHelper.limitedDiseasePredicate(cb, user, joins.getRoot().get(Contact.DISEASE));
	}

	@Override
	public Predicate isRootInJurisdiction() {
		return super.isRootInJurisdiction();
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
			? cb.equal(joins.getRoot().get(Contact.REGION).get(Region.ID), user.getRegion().getId())
			: cb.equal(joins.getRoot().get(Contact.REGION).get(Region.ID), userPath.get(User.REGION).get(Region.ID));
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return user != null
			? cb.equal(joins.getRoot().get(Contact.DISTRICT).get(District.ID), user.getDistrict().getId())
			: cb.equal(joins.getRoot().get(Contact.DISTRICT).get(District.ID), userPath.get(User.DISTRICT).get(District.ID));
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return user != null
			? cb.equal(joins.getRoot().get(Contact.COMMUNITY).get(Community.ID), user.getCommunity().getId())
			: cb.equal(joins.getRoot().get(Contact.COMMUNITY).get(Community.ID), userPath.get(User.COMMUNITY).get(Community.ID));
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

		SampleJurisdictionPredicateValidator sampleJurisdictionPredicateValidator = user != null
			? SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, user)
			: SampleJurisdictionPredicateValidator.withoutAssociations(cb, sampleJoins, userPath);

		sampleContactSubquery.where(cb.and(cb.equal(contactJoin, joins.getRoot()), sampleJurisdictionPredicateValidator.inJurisdictionOrOwned()));
		sampleContactSubquery.select(sampleRoot.get(Sample.ID));
		return cb.exists(sampleContactSubquery);
	}
}

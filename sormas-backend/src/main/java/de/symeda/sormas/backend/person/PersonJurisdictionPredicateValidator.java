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

package de.symeda.sormas.backend.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.utils.jurisdiction.JurisdictionValidator;
import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.contact.ContactJurisdictionPredicateValidator;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.event.EventParticipantJurisdictionPredicateValidator;
import de.symeda.sormas.backend.event.EventParticipantQueryContext;
import de.symeda.sormas.backend.immunization.ImmunizationJurisdictionPredicateValidator;
import de.symeda.sormas.backend.immunization.ImmunizationQueryContext;
import de.symeda.sormas.backend.travelentry.TravelEntryJurisdictionPredicateValidator;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.PredicateJurisdictionValidator;

public class PersonJurisdictionPredicateValidator extends PredicateJurisdictionValidator {

	private PersonJurisdictionPredicateValidator(
		CriteriaBuilder cb,
		PersonJoins joins,
		User user,
		List<PredicateJurisdictionValidator> associatedJurisdictionValidators) {

		super(cb, user, null, associatedJurisdictionValidators);
	}

	public static PersonJurisdictionPredicateValidator of(
		CriteriaQuery<?> cq,
		CriteriaBuilder cb,
		PersonJoins joins,
		User user,
		Set<PersonAssociation> permittedAssociations) {

		final List<PredicateJurisdictionValidator> associatedJurisdictionValidators = new ArrayList<>();
		for (PersonAssociation personAssociation : permittedAssociations) {
			switch (personAssociation) {
			case CASE:
				associatedJurisdictionValidators.add(CaseJurisdictionPredicateValidator.of(new CaseQueryContext(cb, cq, joins.getCaseJoins()), user));
				break;
			case CONTACT:
				associatedJurisdictionValidators
					.add(ContactJurisdictionPredicateValidator.of(new ContactQueryContext(cb, cq, joins.getContactJoins()), user));
				break;
			case EVENT_PARTICIPANT:
				associatedJurisdictionValidators.add(
					EventParticipantJurisdictionPredicateValidator
						.of(new EventParticipantQueryContext(cb, cq, joins.getEventParticipantJoins()), user));
				break;
			case IMMUNIZATION:
				associatedJurisdictionValidators
					.add(ImmunizationJurisdictionPredicateValidator.of(new ImmunizationQueryContext(cb, cq, joins.getImmunizationJoins()), user));
				break;
			case TRAVEL_ENTRY:
				associatedJurisdictionValidators
					.add(TravelEntryJurisdictionPredicateValidator.of(new TravelEntryQueryContext(cb, cq, joins.getTravelEntryJoins()), user));
				break;
			case ALL:
				// NOOP: Persons need to be identified by permitted explicit associations
				break;
			default:
				throw new IllegalArgumentException(personAssociation.toString());
			}
		}

		return new PersonJurisdictionPredicateValidator(cb, joins, user, associatedJurisdictionValidators);
	}

	@Override
	public Predicate isRootInJurisdictionOrOwned() {
		return this.isRootInJurisdiction();
	}

	@Override
	public Predicate isRootInJurisdiction() {

		// Fallback if no associatedJurisdictionValidator was linked: No persons can to be identified by permitted explicit associations
		return cb.disjunction();
	}

	@Override
	public Predicate isRootInJurisdictionForRestrictedAccess() {
		List<Predicate> associationPredicates = new ArrayList<>(associatedJurisdictionValidators.size());
		for (JurisdictionValidator<Predicate> associatedJurisdictionValidator : associatedJurisdictionValidators) {
			if (!TravelEntryJurisdictionPredicateValidator.class.isAssignableFrom(associatedJurisdictionValidator.getClass())
				&& !ImmunizationJurisdictionPredicateValidator.class.isAssignableFrom(associatedJurisdictionValidator.getClass())) {
				associationPredicates.add(associatedJurisdictionValidator.isRootInJurisdictionForRestrictedAccess());
			}
		}
		return or(associationPredicates);

	}

	@Override
	protected Predicate whenNotAllowed() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenNationalLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenRegionalLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenDistrictLevel() {
		return cb.disjunction();
	}

	@Override
	protected Predicate whenCommunityLevel() {
		return cb.disjunction();
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
		return cb.disjunction();
	}
}

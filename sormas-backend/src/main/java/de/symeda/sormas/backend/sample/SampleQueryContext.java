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

package de.symeda.sormas.backend.sample;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.QueryContext;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.location.Location;

public class SampleQueryContext extends QueryContext<Sample, SampleJoins> {

	public SampleQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Sample> root) {
		super(cb, query, root, new SampleJoins(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}

	public Expression<Disease> getDiseaseExpression() {
		final Join<Sample, Case> caze = joins.getCaze();
		final Join<Sample, Contact> contact = joins.getContact();
		final Join<EventParticipant, Event> event = joins.getEvent();

		return criteriaBuilder.<Disease> selectCase()
			.when(criteriaBuilder.isNotNull(caze), caze.get(Case.DISEASE))
			.otherwise(
				criteriaBuilder.<Disease> selectCase()
					.when(criteriaBuilder.isNotNull(contact), contact.get(Contact.DISEASE))
					.otherwise(event.get(Event.DISEASE)));

	}

	public Expression<String> getDistrictNameExpression() {
		final Join<Case, District> caseDistrict = joins.getCaseResponsibleDistrict();
		final Join<Contact, District> contactDistrict = joins.getContactDistrict();
		final Join<Case, District> contactCaseDistrict = joins.getContactCaseResponsibleDistrict();
		final Join<EventParticipant, District> eventParticipantDistrict = joins.getEventParticipantDistrict();
		final Join<Location, District> eventDistrict = joins.getEventDistrict();

		return criteriaBuilder.<String> selectCase()
			.when(criteriaBuilder.isNotNull(caseDistrict), caseDistrict.get(District.NAME))
			.otherwise(
				criteriaBuilder.<String> selectCase()
					.when(criteriaBuilder.isNotNull(contactDistrict), contactDistrict.get(District.NAME))
					.otherwise(
						criteriaBuilder.<String> selectCase()
							.when(criteriaBuilder.isNotNull(contactCaseDistrict), contactCaseDistrict.get(District.NAME))
							.otherwise(
								criteriaBuilder.<String> selectCase()
									.when(criteriaBuilder.isNotNull(eventParticipantDistrict), eventParticipantDistrict.get(District.NAME))
									.otherwise(eventDistrict.get(District.NAME)))));

	}
}

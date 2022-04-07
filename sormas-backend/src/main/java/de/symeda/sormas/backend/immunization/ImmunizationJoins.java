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

package de.symeda.sormas.backend.immunization;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

public class ImmunizationJoins extends QueryJoins<Immunization> {

	private Join<Immunization, Person> person;
	private Join<Immunization, Region> responsibleRegion;
	private Join<Immunization, District> responsibleDistrict;
	private Join<Immunization, Community> responsibleCommunity;
	private Join<Immunization, User> reportingUser;

	public ImmunizationJoins(From<?, Immunization> root) {
		super(root);
	}

	public Join<Immunization, Person> getPerson() {
		return getOrCreate(person, Immunization.PERSON, JoinType.LEFT, this::setPerson);
	}

	private void setPerson(Join<Immunization, Person> person) {
		this.person = person;
	}

	public Join<Immunization, Region> getResponsibleRegion() {
		return getOrCreate(responsibleRegion, Immunization.RESPONSIBLE_REGION, JoinType.LEFT, this::setResponsibleRegion);
	}

	private void setResponsibleRegion(Join<Immunization, Region> responsibleRegion) {
		this.responsibleRegion = responsibleRegion;
	}

	public Join<Immunization, District> getResponsibleDistrict() {
		return getOrCreate(responsibleDistrict, Immunization.RESPONSIBLE_DISTRICT, JoinType.LEFT, this::setResponsibleDistrict);
	}

	private void setResponsibleDistrict(Join<Immunization, District> responsibleDistrict) {
		this.responsibleDistrict = responsibleDistrict;
	}

	public Join<Immunization, Community> getResponsibleCommunity() {
		return getOrCreate(responsibleCommunity, Immunization.RESPONSIBLE_COMMUNITY, JoinType.LEFT, this::setResponsibleCommunity);
	}

	private void setResponsibleCommunity(Join<Immunization, Community> responsibleCommunity) {
		this.responsibleCommunity = responsibleCommunity;
	}

	public Join<Immunization, User> getReportingUser() {
		return getOrCreate(reportingUser, Immunization.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<Immunization, User> reportingUser) {
		this.reportingUser = reportingUser;
	}
}

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

package de.symeda.sormas.backend.immunization.joins;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.user.User;

public class ImmunizationJoins extends BaseImmunizationJoins<Immunization> {

	private Join<Immunization, User> reportingUser;

	public ImmunizationJoins(From<?, Immunization> root) {
		super(root);
	}

	public Join<Immunization, User> getReportingUser() {
		return getOrCreate(reportingUser, Immunization.REPORTING_USER, JoinType.LEFT, this::setReportingUser);
	}

	private void setReportingUser(Join<Immunization, User> reportingUser) {
		this.reportingUser = reportingUser;
	}
}

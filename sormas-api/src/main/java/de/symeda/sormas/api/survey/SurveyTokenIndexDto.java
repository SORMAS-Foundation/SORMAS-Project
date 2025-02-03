/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.survey;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class SurveyTokenIndexDto extends AbstractUuidDto {

	private static final long serialVersionUID = 4358173798026207265L;

	private final String token;
	private final CaseReferenceDto caseAssignedTo;
	private final String assignmentDate;

	public SurveyTokenIndexDto(String uuid, String token, CaseReferenceDto caseAssignedTo, String assignmentDate) {
		super(uuid);
		this.token = token;
		this.caseAssignedTo = caseAssignedTo;
		this.assignmentDate = assignmentDate;
	}

	public String getToken() {
		return token;
	}

	public CaseReferenceDto getCaseAssignedTo() {
		return caseAssignedTo;
	}

	public String getAssignmentDate() {
		return assignmentDate;
	}
}

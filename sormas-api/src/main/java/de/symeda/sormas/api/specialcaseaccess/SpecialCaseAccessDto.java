/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.specialcaseaccess;

import java.util.Date;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class SpecialCaseAccessDto extends EntityDto {

	private static final long serialVersionUID = 6294697729114738908L;

	public static final String I18N_PREFIX = "SpecialCaseAccess";
	public static final String CAZE = "caze";
	public static final String ASSIGNED_TO = "assignedTo";
	public static final String ASSIGNED_BY = "assignedBy";
	public static final String END_DATE_TIME = "endDateTime";
	public static final String ASSIGNMENT_DATE = "assignmentDate";

	@NotNull
	private CaseReferenceDto caze;
	@NotNull
	private UserReferenceDto assignedTo;
	@NotNull
	private UserReferenceDto assignedBy;
	@NotNull
	private Date endDateTime;
	@NotNull
	private Date assignmentDate;

	public static SpecialCaseAccessDto build(CaseReferenceDto caze, UserReferenceDto assignedBy) {
		SpecialCaseAccessDto dto = new SpecialCaseAccessDto();

		dto.setCaze(caze);
		dto.setAssignmentDate(new Date());
		dto.setAssignedBy(assignedBy);

		return dto;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public UserReferenceDto getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(UserReferenceDto assignedTo) {
		this.assignedTo = assignedTo;
	}

	public UserReferenceDto getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(UserReferenceDto assignedBy) {
		this.assignedBy = assignedBy;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	public Date getAssignmentDate() {
		return assignmentDate;
	}

	public void setAssignmentDate(Date assignmentDate) {
		this.assignmentDate = assignmentDate;
	}
}

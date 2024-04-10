/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.selfreport;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class SelfReportIndexDto extends AbstractUuidDto {

	private static final long serialVersionUID = -4984417669514467918L;

	private SelfReportType type;
	private final Date reportDate;
	private Disease disease;
	private String firstName;
	private String lastName;
	private AgeAndBirthDateDto ageAndBirthDate;
	private Sex sex;
	private String district;
	private String street;
	private String houseNumber;
	private String postalCode;
	private String city;
	private String email;
	private String phoneNumber;
	private final UserReferenceDto responsibleUser;
	private final SelfReportInvestigationStatus investigationStatus;
	private final SelfReportProcessingStatus processingStatus;

	public SelfReportIndexDto(
		String uuid,
		Date reportDate,
		UserReferenceDto responsibleUser,
		SelfReportInvestigationStatus investigationStatus,
		SelfReportProcessingStatus processingStatus) {
		super(uuid);
		this.reportDate = reportDate;
		this.responsibleUser = responsibleUser;
		this.investigationStatus = investigationStatus;
		this.processingStatus = processingStatus;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public SelfReportInvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public SelfReportProcessingStatus getProcessingStatus() {
		return processingStatus;
	}
}

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

import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class SelfReportIndexDto extends AbstractUuidDto {

	private static final long serialVersionUID = -4984417669514467918L;

	private final Date reportDate;

	private final UserReferenceDto responsibleUser;

	private final InvestigationStatus investigationStatus;

	private final ProcessingStatus processingStatus;

	public SelfReportIndexDto(
		String uuid,
		Date reportDate,
		UserReferenceDto responsibleUser,
		InvestigationStatus investigationStatus,
		ProcessingStatus processingStatus) {
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

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public ProcessingStatus getProcessingStatus() {
		return processingStatus;
	}
}

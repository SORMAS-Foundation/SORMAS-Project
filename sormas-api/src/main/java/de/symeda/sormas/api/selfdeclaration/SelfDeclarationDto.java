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

package de.symeda.sormas.api.selfdeclaration;

import java.util.Date;

import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class SelfDeclarationDto extends EntityDto {

	private static final long serialVersionUID = 604507951783731873L;

	@NotNull(message = Validations.validReportDateTime)
	private Date reportDate;
	private UserReferenceDto responsibleUser;
	@NotNull(message = Validations.requiredField)
	private InvestigationStatus investigationStatus;
	@NotNull(message = Validations.requiredField)
	private ProcessingStatus processingStatus;

	public static SelfDeclarationDto build() {
		SelfDeclarationDto dto = new SelfDeclarationDto();

		dto.setUuid(DataHelper.createUuid());
		dto.setInvestigationStatus(InvestigationStatus.PENDING);
		dto.setProcessingStatus(ProcessingStatus.UNPROCESSED);

		return dto;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public ProcessingStatus getProcessingStatus() {
		return processingStatus;
	}

	public void setProcessingStatus(ProcessingStatus processingStatus) {
		this.processingStatus = processingStatus;
	}
}

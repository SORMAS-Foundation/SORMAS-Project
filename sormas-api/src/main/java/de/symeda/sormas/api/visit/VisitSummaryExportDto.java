/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.visit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.IsCase;
import de.symeda.sormas.api.contact.IsContact;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class VisitSummaryExportDto extends AbstractUuidDto implements IsContact {

	private static final long serialVersionUID = 7066530434713936967L;

	public static final String I18N_PREFIX = "ContactVisitExport";

	private Long contactId;
	private String caseUuid;
	private Integer maximumFollowUpVisits;

	private String firstName;
	private String lastName;
	private Date lastContactDate;
	private Date followUpUntil;
	private List<VisitSummaryExportDetailsDto> visitDetails = new ArrayList<>();

	public VisitSummaryExportDto(
		String uuid,
		Long contactId,
		String caseUuid,
		String firstName,
		String lastName,
		Date lastContactDate,
		Date followUpUntil) {
		super(uuid);
		this.contactId = contactId;
		this.caseUuid = caseUuid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.lastContactDate = lastContactDate;
		this.followUpUntil = followUpUntil;
	}

	public Long getContactId() {
		return contactId;
	}

	public Date getLastContactDate() {
		return lastContactDate;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public Integer getMaximumFollowUpVisits() {
		return maximumFollowUpVisits;
	}

	public void setMaximumFollowUpVisits(Integer maximumFollowUpVisits) {
		this.maximumFollowUpVisits = maximumFollowUpVisits;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<VisitSummaryExportDetailsDto> getVisitDetails() {
		return visitDetails;
	}

	public void setVisitDetails(List<VisitSummaryExportDetailsDto> visitDetails) {
		this.visitDetails = visitDetails;
	}

	@Override
	public IsCase getCaze() {
		return new CaseReferenceDto(caseUuid);
	}
}

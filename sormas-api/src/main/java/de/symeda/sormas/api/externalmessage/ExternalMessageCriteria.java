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

package de.symeda.sormas.api.externalmessage;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

@AuditedClass
public class ExternalMessageCriteria extends BaseCriteria implements Serializable {

	public static final String I18N_PREFIX = "ExternalMessageCriteria";

	public static final String SEARCH_FIELD_LIKE = "searchFieldLike";
	public static final String MESSAGE_DATE_FROM = "messageDateFrom";
	public static final String MESSAGE_DATE_TO = "messageDateTo";
	public static final String BIRTH_DATE_FROM = "birthDateFrom";
	public static final String BIRTH_DATE_TO = "birthDateTo";
	public static final String ASSIGNEE = "assignee";
	public static final String TYPE = "type";
	@AuditInclude
	private String uuid;
	private ExternalMessageType type;
	private ExternalMessageStatus externalMessageStatus;
	private SampleReferenceDto sample;
	private CaseReferenceDto caze;
	private String searchFieldLike;
	private Date messageDateFrom;
	private Date messageDateTo;
	private Date birthDateFrom;
	private Date birthDateTo;
	private UserReferenceDto assignee;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ExternalMessageType getType() {
		return type;
	}

	public void setType(ExternalMessageType type) {
		this.type = type;
	}

	public ExternalMessageStatus getExternalMessageStatus() {
		return externalMessageStatus;
	}

	public ExternalMessageCriteria externalMessageStatus(ExternalMessageStatus externalMessageStatus) {
		this.externalMessageStatus = externalMessageStatus;
		return this;
	}

	public SampleReferenceDto getSample() {
		return sample;
	}

	public void setSample(SampleReferenceDto sample) {
		this.sample = sample;
	}

	public CaseReferenceDto getCaze() {
		return caze;
	}

	public void setCaze(CaseReferenceDto caze) {
		this.caze = caze;
	}

	public String getSearchFieldLike() {
		return searchFieldLike;
	}

	public void setSearchFieldLike(String searchFieldLike) {
		this.searchFieldLike = searchFieldLike;
	}

	public Date getMessageDateFrom() {
		return messageDateFrom;
	}

	public void setMessageDateFrom(Date messageDateFrom) {
		this.messageDateFrom = messageDateFrom;
	}

	public Date getMessageDateTo() {
		return messageDateTo;
	}

	public void setMessageDateTo(Date messageDateTo) {
		this.messageDateTo = messageDateTo;
	}

	public Date getBirthDateFrom() {
		return birthDateFrom;
	}

	public void setBirthDateFrom(Date birthDateFrom) {
		this.birthDateFrom = birthDateFrom;
	}

	public Date getBirthDateTo() {
		return birthDateTo;
	}

	public void setBirthDateTo(Date birthDateTo) {
		this.birthDateTo = birthDateTo;
	}

	public UserReferenceDto getAssignee() {
		return assignee;
	}

	public void setAssignee(UserReferenceDto assignee) {
		this.assignee = assignee;
	}
}

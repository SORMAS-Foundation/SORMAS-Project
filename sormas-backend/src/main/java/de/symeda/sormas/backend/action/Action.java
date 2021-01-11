/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.action;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.action.ActionPriority;
import de.symeda.sormas.api.action.ActionStatus;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.user.User;

@Entity
@Audited
public class Action extends AbstractDomainObject {

	private static final long serialVersionUID = -4754578341242164661L;

	public static final String TABLE_NAME = "action";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String REPLY = "reply";
	public static final String LAST_MODIFIED_BY = "lastModifiedBy";
	public static final String CREATOR_USER = "creatorUser";
	public static final String PRIORITY = "priority";
	public static final String DATE = "date";
	public static final String EVENT = "event";
	public static final String STATUS_CHANGE_DATE = "statusChangeDate";
	public static final String ACTION_CONTEXT = "actionContext";
	public static final String ACTION_STATUS = "actionStatus";

	private ActionContext actionContext;
	private Event event;

	private ActionPriority priority;
	private Date date;
	private ActionStatus actionStatus;
	private Date statusChangeDate;

	private User creatorUser;
	private String title;
	private String description;
	private String reply;
	private User lastModifiedBy;

	@Enumerated(EnumType.STRING)
	public ActionContext getActionContext() {
		return actionContext;
	}

	public void setActionContext(ActionContext actionContext) {
		this.actionContext = actionContext;
	}

	@ManyToOne
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Enumerated(EnumType.STRING)
	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}

	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	@ManyToOne
	public User getCreatorUser() {
		return creatorUser;
	}

	public void setCreatorUser(User creatorUser) {
		this.creatorUser = creatorUser;
	}

	@Enumerated(EnumType.STRING)
	public ActionPriority getPriority() {
		return priority;
	}

	public void setPriority(ActionPriority priority) {
		this.priority = priority;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(columnDefinition = "text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(columnDefinition = "text")
	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	@ManyToOne
	public User getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(User lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
}

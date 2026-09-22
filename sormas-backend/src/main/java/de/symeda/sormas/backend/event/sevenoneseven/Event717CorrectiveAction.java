/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.backend.event.sevenoneseven;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.event.sevenoneseven.Event717CorrectiveActionPriority;
import de.symeda.sormas.api.event.sevenoneseven.Event717CorrectiveActionStatus;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = Event717CorrectiveAction.TABLE_NAME)
public class Event717CorrectiveAction extends AbstractDomainObject {

	private static final long serialVersionUID = -7351740917356612281L;

	public static final String TABLE_NAME = "event717correctiveaction";

	public static final String ASSESSMENT = "assessment";
	public static final String PROPOSED_ACTION = "proposedAction";
	public static final String BOTTLENECK = "bottleneck";
	public static final String PRIORITIZATION = "prioritization";
	public static final String RESPONSIBLE_AUTHORITY = "responsibleAuthority";
	public static final String TARGET_START_DATE = "targetStartDate";
	public static final String TARGET_END_DATE = "targetEndDate";
	public static final String PLANNING_FUNDING_OPPORTUNITIES = "planningFundingOpportunities";
	public static final String PROGRESS_STATUS = "progressStatus";
	public static final String NEXT_STEPS = "nextSteps";

	private Event717Assessment assessment;
	private String proposedAction;
	private Event717Bottleneck bottleneck;
	private Event717CorrectiveActionPriority prioritization;
	private String responsibleAuthority;
	private Date targetStartDate;
	private Date targetEndDate;
	private String planningFundingOpportunities;
	private Event717CorrectiveActionStatus progressStatus;
	private String nextSteps;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	public Event717Assessment getAssessment() {
		return assessment;
	}

	public void setAssessment(Event717Assessment assessment) {
		this.assessment = assessment;
	}

	@Column(columnDefinition = "text")
	public String getProposedAction() {
		return proposedAction;
	}

	public void setProposedAction(String proposedAction) {
		this.proposedAction = proposedAction;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Event717Bottleneck getBottleneck() {
		return bottleneck;
	}

	public void setBottleneck(Event717Bottleneck bottleneck) {
		this.bottleneck = bottleneck;
	}

	@Enumerated(EnumType.STRING)
	public Event717CorrectiveActionPriority getPrioritization() {
		return prioritization;
	}

	public void setPrioritization(Event717CorrectiveActionPriority prioritization) {
		this.prioritization = prioritization;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getResponsibleAuthority() {
		return responsibleAuthority;
	}

	public void setResponsibleAuthority(String responsibleAuthority) {
		this.responsibleAuthority = responsibleAuthority;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTargetStartDate() {
		return targetStartDate;
	}

	public void setTargetStartDate(Date targetStartDate) {
		this.targetStartDate = targetStartDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTargetEndDate() {
		return targetEndDate;
	}

	public void setTargetEndDate(Date targetEndDate) {
		this.targetEndDate = targetEndDate;
	}

	@Column(columnDefinition = "text")
	public String getPlanningFundingOpportunities() {
		return planningFundingOpportunities;
	}

	public void setPlanningFundingOpportunities(String planningFundingOpportunities) {
		this.planningFundingOpportunities = planningFundingOpportunities;
	}

	@Enumerated(EnumType.STRING)
	public Event717CorrectiveActionStatus getProgressStatus() {
		return progressStatus;
	}

	public void setProgressStatus(Event717CorrectiveActionStatus progressStatus) {
		this.progressStatus = progressStatus;
	}

	@Column(columnDefinition = "text")
	public String getNextSteps() {
		return nextSteps;
	}

	public void setNextSteps(String nextSteps) {
		this.nextSteps = nextSteps;
	}
}

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
package de.symeda.sormas.api.event.sevenoneseven;

import java.util.Date;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.SensitiveData;

/**
 * Immediate or longer-term action proposed to address a bottleneck identified in a 7-1-7 assessment.
 */
@DependingOnFeatureType(featureType = FeatureType.EVENT_717_ASSESSMENT)
public class Event717CorrectiveActionDto extends EntityDto {

	private static final long serialVersionUID = -1978251532617010376L;

	public static final String I18N_PREFIX = "Event717CorrectiveAction";

	public static final String PROPOSED_ACTION = "proposedAction";
	public static final String BOTTLENECK = "bottleneck";
	public static final String PRIORITIZATION = "prioritization";
	public static final String RESPONSIBLE_AUTHORITY = "responsibleAuthority";
	public static final String TARGET_START_DATE = "targetStartDate";
	public static final String TARGET_END_DATE = "targetEndDate";
	public static final String PLANNING_FUNDING_OPPORTUNITIES = "planningFundingOpportunities";
	public static final String PROGRESS_STATUS = "progressStatus";
	public static final String NEXT_STEPS = "nextSteps";

	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String proposedAction;
	private Event717BottleneckReferenceDto bottleneck;
	private Event717CorrectiveActionPriority prioritization;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String responsibleAuthority;
	private Date targetStartDate;
	private Date targetEndDate;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String planningFundingOpportunities;
	private Event717CorrectiveActionStatus progressStatus;
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String nextSteps;

	public static Event717CorrectiveActionDto build() {

		Event717CorrectiveActionDto action = new Event717CorrectiveActionDto();
		action.setUuid(DataHelper.createUuid());
		return action;
	}

	public String getProposedAction() {
		return proposedAction;
	}

	public void setProposedAction(String proposedAction) {
		this.proposedAction = proposedAction;
	}

	public Event717BottleneckReferenceDto getBottleneck() {
		return bottleneck;
	}

	public void setBottleneck(Event717BottleneckReferenceDto bottleneck) {
		this.bottleneck = bottleneck;
	}

	public Event717CorrectiveActionPriority getPrioritization() {
		return prioritization;
	}

	public void setPrioritization(Event717CorrectiveActionPriority prioritization) {
		this.prioritization = prioritization;
	}

	public String getResponsibleAuthority() {
		return responsibleAuthority;
	}

	public void setResponsibleAuthority(String responsibleAuthority) {
		this.responsibleAuthority = responsibleAuthority;
	}

	public Date getTargetStartDate() {
		return targetStartDate;
	}

	public void setTargetStartDate(Date targetStartDate) {
		this.targetStartDate = targetStartDate;
	}

	public Date getTargetEndDate() {
		return targetEndDate;
	}

	public void setTargetEndDate(Date targetEndDate) {
		this.targetEndDate = targetEndDate;
	}

	public String getPlanningFundingOpportunities() {
		return planningFundingOpportunities;
	}

	public void setPlanningFundingOpportunities(String planningFundingOpportunities) {
		this.planningFundingOpportunities = planningFundingOpportunities;
	}

	public Event717CorrectiveActionStatus getProgressStatus() {
		return progressStatus;
	}

	public void setProgressStatus(Event717CorrectiveActionStatus progressStatus) {
		this.progressStatus = progressStatus;
	}

	public String getNextSteps() {
		return nextSteps;
	}

	public void setNextSteps(String nextSteps) {
		this.nextSteps = nextSteps;
	}
}

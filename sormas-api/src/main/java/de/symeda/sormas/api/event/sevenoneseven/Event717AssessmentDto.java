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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;

/**
 * 7-1-7 assessment of an event: milestone dates, early response actions, bottlenecks/enablers and corrective actions,
 * following the 7-1-7 Alliance assessment tool.
 */
@DependingOnFeatureType(featureType = FeatureType.EVENT_717_ASSESSMENT)
public class Event717AssessmentDto extends EntityDto {

	private static final long serialVersionUID = -3620587437413062516L;

	public static final String I18N_PREFIX = "Event717Assessment";

	/**
	 * Maximum number of bottlenecks resp. enablers per interval as recommended by the 7-1-7 assessment tool.
	 */
	public static final int MAX_ENTRIES_PER_INTERVAL = 3;

	public static final String EVENT = "event";
	public static final String DATE_OF_EMERGENCE = "dateOfEmergence";
	public static final String EMERGENCE_NARRATIVE = "emergenceNarrative";
	public static final String DATE_OF_DETECTION = "dateOfDetection";
	public static final String DETECTION_NARRATIVE = "detectionNarrative";
	public static final String DATE_OF_NOTIFICATION = "dateOfNotification";
	public static final String NOTIFICATION_NARRATIVE = "notificationNarrative";
	public static final String INVESTIGATION_DATE = "investigationDate";
	public static final String INVESTIGATION_NOT_APPLICABLE = "investigationNotApplicable";
	public static final String INVESTIGATION_NARRATIVE = "investigationNarrative";
	public static final String EPI_ANALYSIS_DATE = "epiAnalysisDate";
	public static final String EPI_ANALYSIS_NOT_APPLICABLE = "epiAnalysisNotApplicable";
	public static final String EPI_ANALYSIS_NARRATIVE = "epiAnalysisNarrative";
	public static final String LAB_CONFIRMATION_DATE = "labConfirmationDate";
	public static final String LAB_CONFIRMATION_NOT_APPLICABLE = "labConfirmationNotApplicable";
	public static final String LAB_CONFIRMATION_NARRATIVE = "labConfirmationNarrative";
	public static final String CASE_MANAGEMENT_DATE = "caseManagementDate";
	public static final String CASE_MANAGEMENT_NOT_APPLICABLE = "caseManagementNotApplicable";
	public static final String CASE_MANAGEMENT_NARRATIVE = "caseManagementNarrative";
	public static final String COUNTERMEASURES_DATE = "countermeasuresDate";
	public static final String COUNTERMEASURES_NOT_APPLICABLE = "countermeasuresNotApplicable";
	public static final String COUNTERMEASURES_NARRATIVE = "countermeasuresNarrative";
	public static final String RISK_COMMUNICATION_DATE = "riskCommunicationDate";
	public static final String RISK_COMMUNICATION_NOT_APPLICABLE = "riskCommunicationNotApplicable";
	public static final String RISK_COMMUNICATION_NARRATIVE = "riskCommunicationNarrative";
	public static final String COORDINATION_DATE = "coordinationDate";
	public static final String COORDINATION_NOT_APPLICABLE = "coordinationNotApplicable";
	public static final String COORDINATION_NARRATIVE = "coordinationNarrative";
	public static final String EARLY_RESPONSE_COMPLETION_DATE = "earlyResponseCompletionDate";
	public static final String EARLY_RESPONSE_COMPLETION_NARRATIVE = "earlyResponseCompletionNarrative";
	public static final String OUTBREAK_END_DATE = "outbreakEndDate";
	public static final String REPORT_COMPLETED_DATE = "reportCompletedDate";
	public static final String REPORT_COMPLETED_BY_USER = "reportCompletedByUser";
	public static final String REPORT_COMPLETED_BY_NAME = "reportCompletedByName";
	public static final String GENERAL_NOTES = "generalNotes";
	public static final String BOTTLENECKS = "bottlenecks";
	public static final String ENABLERS = "enablers";
	public static final String CORRECTIVE_ACTIONS = "correctiveActions";

	@NotNull(message = Validations.requiredField)
	private EventReferenceDto event;
	private Date dateOfEmergence;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String emergenceNarrative;
	private Date dateOfDetection;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String detectionNarrative;
	private Date dateOfNotification;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String notificationNarrative;
	private Date investigationDate;
	private boolean investigationNotApplicable;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String investigationNarrative;
	private Date epiAnalysisDate;
	private boolean epiAnalysisNotApplicable;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String epiAnalysisNarrative;
	private Date labConfirmationDate;
	private boolean labConfirmationNotApplicable;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String labConfirmationNarrative;
	private Date caseManagementDate;
	private boolean caseManagementNotApplicable;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String caseManagementNarrative;
	private Date countermeasuresDate;
	private boolean countermeasuresNotApplicable;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String countermeasuresNarrative;
	private Date riskCommunicationDate;
	private boolean riskCommunicationNotApplicable;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String riskCommunicationNarrative;
	private Date coordinationDate;
	private boolean coordinationNotApplicable;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String coordinationNarrative;
	private Date earlyResponseCompletionDate;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String earlyResponseCompletionNarrative;
	private Date outbreakEndDate;
	private Date reportCompletedDate;
	private UserReferenceDto reportCompletedByUser;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String reportCompletedByName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String generalNotes;
	@Valid
	private List<Event717BottleneckDto> bottlenecks = new ArrayList<>();
	@Valid
	private List<Event717EnablerDto> enablers = new ArrayList<>();
	@Valid
	private List<Event717CorrectiveActionDto> correctiveActions = new ArrayList<>();

	public static Event717AssessmentDto build(EventReferenceDto event) {

		Event717AssessmentDto assessment = new Event717AssessmentDto();
		assessment.setUuid(DataHelper.createUuid());
		assessment.setEvent(event);
		return assessment;
	}

	public Date getEarlyResponseActionDate(Event717EarlyResponseAction action) {

		switch (action) {
		case INVESTIGATION:
			return investigationDate;
		case EPI_ANALYSIS:
			return epiAnalysisDate;
		case LAB_CONFIRMATION:
			return labConfirmationDate;
		case CASE_MANAGEMENT:
			return caseManagementDate;
		case COUNTERMEASURES:
			return countermeasuresDate;
		case RISK_COMMUNICATION:
			return riskCommunicationDate;
		case COORDINATION:
			return coordinationDate;
		default:
			throw new IllegalArgumentException(action.name());
		}
	}

	public void setEarlyResponseActionDate(Event717EarlyResponseAction action, Date date) {

		switch (action) {
		case INVESTIGATION:
			investigationDate = date;
			break;
		case EPI_ANALYSIS:
			epiAnalysisDate = date;
			break;
		case LAB_CONFIRMATION:
			labConfirmationDate = date;
			break;
		case CASE_MANAGEMENT:
			caseManagementDate = date;
			break;
		case COUNTERMEASURES:
			countermeasuresDate = date;
			break;
		case RISK_COMMUNICATION:
			riskCommunicationDate = date;
			break;
		case COORDINATION:
			coordinationDate = date;
			break;
		default:
			throw new IllegalArgumentException(action.name());
		}
	}

	public boolean isEarlyResponseActionNotApplicable(Event717EarlyResponseAction action) {

		switch (action) {
		case INVESTIGATION:
			return investigationNotApplicable;
		case EPI_ANALYSIS:
			return epiAnalysisNotApplicable;
		case LAB_CONFIRMATION:
			return labConfirmationNotApplicable;
		case CASE_MANAGEMENT:
			return caseManagementNotApplicable;
		case COUNTERMEASURES:
			return countermeasuresNotApplicable;
		case RISK_COMMUNICATION:
			return riskCommunicationNotApplicable;
		case COORDINATION:
			return coordinationNotApplicable;
		default:
			throw new IllegalArgumentException(action.name());
		}
	}

	public void setEarlyResponseActionNotApplicable(Event717EarlyResponseAction action, boolean notApplicable) {

		switch (action) {
		case INVESTIGATION:
			investigationNotApplicable = notApplicable;
			break;
		case EPI_ANALYSIS:
			epiAnalysisNotApplicable = notApplicable;
			break;
		case LAB_CONFIRMATION:
			labConfirmationNotApplicable = notApplicable;
			break;
		case CASE_MANAGEMENT:
			caseManagementNotApplicable = notApplicable;
			break;
		case COUNTERMEASURES:
			countermeasuresNotApplicable = notApplicable;
			break;
		case RISK_COMMUNICATION:
			riskCommunicationNotApplicable = notApplicable;
			break;
		case COORDINATION:
			coordinationNotApplicable = notApplicable;
			break;
		default:
			throw new IllegalArgumentException(action.name());
		}
	}

	/**
	 * @return The property name of the date field of the given early response action, e.g. for captions and validation messages.
	 */
	public static String getEarlyResponseActionDateProperty(Event717EarlyResponseAction action) {

		switch (action) {
		case INVESTIGATION:
			return INVESTIGATION_DATE;
		case EPI_ANALYSIS:
			return EPI_ANALYSIS_DATE;
		case LAB_CONFIRMATION:
			return LAB_CONFIRMATION_DATE;
		case CASE_MANAGEMENT:
			return CASE_MANAGEMENT_DATE;
		case COUNTERMEASURES:
			return COUNTERMEASURES_DATE;
		case RISK_COMMUNICATION:
			return RISK_COMMUNICATION_DATE;
		case COORDINATION:
			return COORDINATION_DATE;
		default:
			throw new IllegalArgumentException(action.name());
		}
	}

	public EventReferenceDto getEvent() {
		return event;
	}

	public void setEvent(EventReferenceDto event) {
		this.event = event;
	}

	public Date getDateOfEmergence() {
		return dateOfEmergence;
	}

	public void setDateOfEmergence(Date dateOfEmergence) {
		this.dateOfEmergence = dateOfEmergence;
	}

	public String getEmergenceNarrative() {
		return emergenceNarrative;
	}

	public void setEmergenceNarrative(String emergenceNarrative) {
		this.emergenceNarrative = emergenceNarrative;
	}

	public Date getDateOfDetection() {
		return dateOfDetection;
	}

	public void setDateOfDetection(Date dateOfDetection) {
		this.dateOfDetection = dateOfDetection;
	}

	public String getDetectionNarrative() {
		return detectionNarrative;
	}

	public void setDetectionNarrative(String detectionNarrative) {
		this.detectionNarrative = detectionNarrative;
	}

	public Date getDateOfNotification() {
		return dateOfNotification;
	}

	public void setDateOfNotification(Date dateOfNotification) {
		this.dateOfNotification = dateOfNotification;
	}

	public String getNotificationNarrative() {
		return notificationNarrative;
	}

	public void setNotificationNarrative(String notificationNarrative) {
		this.notificationNarrative = notificationNarrative;
	}

	public Date getInvestigationDate() {
		return investigationDate;
	}

	public void setInvestigationDate(Date investigationDate) {
		this.investigationDate = investigationDate;
	}

	public boolean isInvestigationNotApplicable() {
		return investigationNotApplicable;
	}

	public void setInvestigationNotApplicable(boolean investigationNotApplicable) {
		this.investigationNotApplicable = investigationNotApplicable;
	}

	public String getInvestigationNarrative() {
		return investigationNarrative;
	}

	public void setInvestigationNarrative(String investigationNarrative) {
		this.investigationNarrative = investigationNarrative;
	}

	public Date getEpiAnalysisDate() {
		return epiAnalysisDate;
	}

	public void setEpiAnalysisDate(Date epiAnalysisDate) {
		this.epiAnalysisDate = epiAnalysisDate;
	}

	public boolean isEpiAnalysisNotApplicable() {
		return epiAnalysisNotApplicable;
	}

	public void setEpiAnalysisNotApplicable(boolean epiAnalysisNotApplicable) {
		this.epiAnalysisNotApplicable = epiAnalysisNotApplicable;
	}

	public String getEpiAnalysisNarrative() {
		return epiAnalysisNarrative;
	}

	public void setEpiAnalysisNarrative(String epiAnalysisNarrative) {
		this.epiAnalysisNarrative = epiAnalysisNarrative;
	}

	public Date getLabConfirmationDate() {
		return labConfirmationDate;
	}

	public void setLabConfirmationDate(Date labConfirmationDate) {
		this.labConfirmationDate = labConfirmationDate;
	}

	public boolean isLabConfirmationNotApplicable() {
		return labConfirmationNotApplicable;
	}

	public void setLabConfirmationNotApplicable(boolean labConfirmationNotApplicable) {
		this.labConfirmationNotApplicable = labConfirmationNotApplicable;
	}

	public String getLabConfirmationNarrative() {
		return labConfirmationNarrative;
	}

	public void setLabConfirmationNarrative(String labConfirmationNarrative) {
		this.labConfirmationNarrative = labConfirmationNarrative;
	}

	public Date getCaseManagementDate() {
		return caseManagementDate;
	}

	public void setCaseManagementDate(Date caseManagementDate) {
		this.caseManagementDate = caseManagementDate;
	}

	public boolean isCaseManagementNotApplicable() {
		return caseManagementNotApplicable;
	}

	public void setCaseManagementNotApplicable(boolean caseManagementNotApplicable) {
		this.caseManagementNotApplicable = caseManagementNotApplicable;
	}

	public String getCaseManagementNarrative() {
		return caseManagementNarrative;
	}

	public void setCaseManagementNarrative(String caseManagementNarrative) {
		this.caseManagementNarrative = caseManagementNarrative;
	}

	public Date getCountermeasuresDate() {
		return countermeasuresDate;
	}

	public void setCountermeasuresDate(Date countermeasuresDate) {
		this.countermeasuresDate = countermeasuresDate;
	}

	public boolean isCountermeasuresNotApplicable() {
		return countermeasuresNotApplicable;
	}

	public void setCountermeasuresNotApplicable(boolean countermeasuresNotApplicable) {
		this.countermeasuresNotApplicable = countermeasuresNotApplicable;
	}

	public String getCountermeasuresNarrative() {
		return countermeasuresNarrative;
	}

	public void setCountermeasuresNarrative(String countermeasuresNarrative) {
		this.countermeasuresNarrative = countermeasuresNarrative;
	}

	public Date getRiskCommunicationDate() {
		return riskCommunicationDate;
	}

	public void setRiskCommunicationDate(Date riskCommunicationDate) {
		this.riskCommunicationDate = riskCommunicationDate;
	}

	public boolean isRiskCommunicationNotApplicable() {
		return riskCommunicationNotApplicable;
	}

	public void setRiskCommunicationNotApplicable(boolean riskCommunicationNotApplicable) {
		this.riskCommunicationNotApplicable = riskCommunicationNotApplicable;
	}

	public String getRiskCommunicationNarrative() {
		return riskCommunicationNarrative;
	}

	public void setRiskCommunicationNarrative(String riskCommunicationNarrative) {
		this.riskCommunicationNarrative = riskCommunicationNarrative;
	}

	public Date getCoordinationDate() {
		return coordinationDate;
	}

	public void setCoordinationDate(Date coordinationDate) {
		this.coordinationDate = coordinationDate;
	}

	public boolean isCoordinationNotApplicable() {
		return coordinationNotApplicable;
	}

	public void setCoordinationNotApplicable(boolean coordinationNotApplicable) {
		this.coordinationNotApplicable = coordinationNotApplicable;
	}

	public String getCoordinationNarrative() {
		return coordinationNarrative;
	}

	public void setCoordinationNarrative(String coordinationNarrative) {
		this.coordinationNarrative = coordinationNarrative;
	}

	public Date getEarlyResponseCompletionDate() {
		return earlyResponseCompletionDate;
	}

	public void setEarlyResponseCompletionDate(Date earlyResponseCompletionDate) {
		this.earlyResponseCompletionDate = earlyResponseCompletionDate;
	}

	public String getEarlyResponseCompletionNarrative() {
		return earlyResponseCompletionNarrative;
	}

	public void setEarlyResponseCompletionNarrative(String earlyResponseCompletionNarrative) {
		this.earlyResponseCompletionNarrative = earlyResponseCompletionNarrative;
	}

	public Date getOutbreakEndDate() {
		return outbreakEndDate;
	}

	public void setOutbreakEndDate(Date outbreakEndDate) {
		this.outbreakEndDate = outbreakEndDate;
	}

	public Date getReportCompletedDate() {
		return reportCompletedDate;
	}

	public void setReportCompletedDate(Date reportCompletedDate) {
		this.reportCompletedDate = reportCompletedDate;
	}

	public UserReferenceDto getReportCompletedByUser() {
		return reportCompletedByUser;
	}

	public void setReportCompletedByUser(UserReferenceDto reportCompletedByUser) {
		this.reportCompletedByUser = reportCompletedByUser;
	}

	public String getReportCompletedByName() {
		return reportCompletedByName;
	}

	public void setReportCompletedByName(String reportCompletedByName) {
		this.reportCompletedByName = reportCompletedByName;
	}

	public String getGeneralNotes() {
		return generalNotes;
	}

	public void setGeneralNotes(String generalNotes) {
		this.generalNotes = generalNotes;
	}

	public List<Event717BottleneckDto> getBottlenecks() {
		return bottlenecks;
	}

	public void setBottlenecks(List<Event717BottleneckDto> bottlenecks) {
		this.bottlenecks = bottlenecks;
	}

	public List<Event717EnablerDto> getEnablers() {
		return enablers;
	}

	public void setEnablers(List<Event717EnablerDto> enablers) {
		this.enablers = enablers;
	}

	public List<Event717CorrectiveActionDto> getCorrectiveActions() {
		return correctiveActions;
	}

	public void setCorrectiveActions(List<Event717CorrectiveActionDto> correctiveActions) {
		this.correctiveActions = correctiveActions;
	}
}

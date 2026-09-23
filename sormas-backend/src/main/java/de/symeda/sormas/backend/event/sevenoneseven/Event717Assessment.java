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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.event.Event;

/**
 * 7-1-7 assessment of an {@link Event}. Not mapped on the event itself, so it has to be removed explicitly when the event is
 * deleted permanently.
 */
@Entity(name = Event717Assessment.TABLE_NAME)
public class Event717Assessment extends AbstractDomainObject {

	private static final long serialVersionUID = 5404118541367520386L;

	public static final String TABLE_NAME = "event717assessment";

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
	public static final String REPORT_COMPLETED_BY_NAME = "reportCompletedByName";
	public static final String GENERAL_NOTES = "generalNotes";
	public static final String CHANGE_DATE_OF_EMBEDDED_LISTS = "changeDateOfEmbeddedLists";
	public static final String BOTTLENECKS = "bottlenecks";
	public static final String ENABLERS = "enablers";
	public static final String CORRECTIVE_ACTIONS = "correctiveActions";

	private Event event;
	private Date dateOfEmergence;
	private String emergenceNarrative;
	private Date dateOfDetection;
	private String detectionNarrative;
	private Date dateOfNotification;
	private String notificationNarrative;
	private Date investigationDate;
	private boolean investigationNotApplicable;
	private String investigationNarrative;
	private Date epiAnalysisDate;
	private boolean epiAnalysisNotApplicable;
	private String epiAnalysisNarrative;
	private Date labConfirmationDate;
	private boolean labConfirmationNotApplicable;
	private String labConfirmationNarrative;
	private Date caseManagementDate;
	private boolean caseManagementNotApplicable;
	private String caseManagementNarrative;
	private Date countermeasuresDate;
	private boolean countermeasuresNotApplicable;
	private String countermeasuresNarrative;
	private Date riskCommunicationDate;
	private boolean riskCommunicationNotApplicable;
	private String riskCommunicationNarrative;
	private Date coordinationDate;
	private boolean coordinationNotApplicable;
	private String coordinationNarrative;
	private Date earlyResponseCompletionDate;
	private String earlyResponseCompletionNarrative;
	private Date outbreakEndDate;
	private Date reportCompletedDate;
	private String reportCompletedByName;
	private String generalNotes;
	private Date changeDateOfEmbeddedLists;
	private List<Event717Bottleneck> bottlenecks = new ArrayList<>();
	private List<Event717Enabler> enablers = new ArrayList<>();
	private List<Event717CorrectiveAction> correctiveActions = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false, unique = true)
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateOfEmergence() {
		return dateOfEmergence;
	}

	public void setDateOfEmergence(Date dateOfEmergence) {
		this.dateOfEmergence = dateOfEmergence;
	}

	@Column(columnDefinition = "text")
	public String getEmergenceNarrative() {
		return emergenceNarrative;
	}

	public void setEmergenceNarrative(String emergenceNarrative) {
		this.emergenceNarrative = emergenceNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateOfDetection() {
		return dateOfDetection;
	}

	public void setDateOfDetection(Date dateOfDetection) {
		this.dateOfDetection = dateOfDetection;
	}

	@Column(columnDefinition = "text")
	public String getDetectionNarrative() {
		return detectionNarrative;
	}

	public void setDetectionNarrative(String detectionNarrative) {
		this.detectionNarrative = detectionNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateOfNotification() {
		return dateOfNotification;
	}

	public void setDateOfNotification(Date dateOfNotification) {
		this.dateOfNotification = dateOfNotification;
	}

	@Column(columnDefinition = "text")
	public String getNotificationNarrative() {
		return notificationNarrative;
	}

	public void setNotificationNarrative(String notificationNarrative) {
		this.notificationNarrative = notificationNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getInvestigationDate() {
		return investigationDate;
	}

	public void setInvestigationDate(Date investigationDate) {
		this.investigationDate = investigationDate;
	}

	@Column(nullable = false)
	public boolean isInvestigationNotApplicable() {
		return investigationNotApplicable;
	}

	public void setInvestigationNotApplicable(boolean investigationNotApplicable) {
		this.investigationNotApplicable = investigationNotApplicable;
	}

	@Column(columnDefinition = "text")
	public String getInvestigationNarrative() {
		return investigationNarrative;
	}

	public void setInvestigationNarrative(String investigationNarrative) {
		this.investigationNarrative = investigationNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEpiAnalysisDate() {
		return epiAnalysisDate;
	}

	public void setEpiAnalysisDate(Date epiAnalysisDate) {
		this.epiAnalysisDate = epiAnalysisDate;
	}

	@Column(nullable = false)
	public boolean isEpiAnalysisNotApplicable() {
		return epiAnalysisNotApplicable;
	}

	public void setEpiAnalysisNotApplicable(boolean epiAnalysisNotApplicable) {
		this.epiAnalysisNotApplicable = epiAnalysisNotApplicable;
	}

	@Column(columnDefinition = "text")
	public String getEpiAnalysisNarrative() {
		return epiAnalysisNarrative;
	}

	public void setEpiAnalysisNarrative(String epiAnalysisNarrative) {
		this.epiAnalysisNarrative = epiAnalysisNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLabConfirmationDate() {
		return labConfirmationDate;
	}

	public void setLabConfirmationDate(Date labConfirmationDate) {
		this.labConfirmationDate = labConfirmationDate;
	}

	@Column(nullable = false)
	public boolean isLabConfirmationNotApplicable() {
		return labConfirmationNotApplicable;
	}

	public void setLabConfirmationNotApplicable(boolean labConfirmationNotApplicable) {
		this.labConfirmationNotApplicable = labConfirmationNotApplicable;
	}

	@Column(columnDefinition = "text")
	public String getLabConfirmationNarrative() {
		return labConfirmationNarrative;
	}

	public void setLabConfirmationNarrative(String labConfirmationNarrative) {
		this.labConfirmationNarrative = labConfirmationNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCaseManagementDate() {
		return caseManagementDate;
	}

	public void setCaseManagementDate(Date caseManagementDate) {
		this.caseManagementDate = caseManagementDate;
	}

	@Column(nullable = false)
	public boolean isCaseManagementNotApplicable() {
		return caseManagementNotApplicable;
	}

	public void setCaseManagementNotApplicable(boolean caseManagementNotApplicable) {
		this.caseManagementNotApplicable = caseManagementNotApplicable;
	}

	@Column(columnDefinition = "text")
	public String getCaseManagementNarrative() {
		return caseManagementNarrative;
	}

	public void setCaseManagementNarrative(String caseManagementNarrative) {
		this.caseManagementNarrative = caseManagementNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCountermeasuresDate() {
		return countermeasuresDate;
	}

	public void setCountermeasuresDate(Date countermeasuresDate) {
		this.countermeasuresDate = countermeasuresDate;
	}

	@Column(nullable = false)
	public boolean isCountermeasuresNotApplicable() {
		return countermeasuresNotApplicable;
	}

	public void setCountermeasuresNotApplicable(boolean countermeasuresNotApplicable) {
		this.countermeasuresNotApplicable = countermeasuresNotApplicable;
	}

	@Column(columnDefinition = "text")
	public String getCountermeasuresNarrative() {
		return countermeasuresNarrative;
	}

	public void setCountermeasuresNarrative(String countermeasuresNarrative) {
		this.countermeasuresNarrative = countermeasuresNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getRiskCommunicationDate() {
		return riskCommunicationDate;
	}

	public void setRiskCommunicationDate(Date riskCommunicationDate) {
		this.riskCommunicationDate = riskCommunicationDate;
	}

	@Column(nullable = false)
	public boolean isRiskCommunicationNotApplicable() {
		return riskCommunicationNotApplicable;
	}

	public void setRiskCommunicationNotApplicable(boolean riskCommunicationNotApplicable) {
		this.riskCommunicationNotApplicable = riskCommunicationNotApplicable;
	}

	@Column(columnDefinition = "text")
	public String getRiskCommunicationNarrative() {
		return riskCommunicationNarrative;
	}

	public void setRiskCommunicationNarrative(String riskCommunicationNarrative) {
		this.riskCommunicationNarrative = riskCommunicationNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCoordinationDate() {
		return coordinationDate;
	}

	public void setCoordinationDate(Date coordinationDate) {
		this.coordinationDate = coordinationDate;
	}

	@Column(nullable = false)
	public boolean isCoordinationNotApplicable() {
		return coordinationNotApplicable;
	}

	public void setCoordinationNotApplicable(boolean coordinationNotApplicable) {
		this.coordinationNotApplicable = coordinationNotApplicable;
	}

	@Column(columnDefinition = "text")
	public String getCoordinationNarrative() {
		return coordinationNarrative;
	}

	public void setCoordinationNarrative(String coordinationNarrative) {
		this.coordinationNarrative = coordinationNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEarlyResponseCompletionDate() {
		return earlyResponseCompletionDate;
	}

	public void setEarlyResponseCompletionDate(Date earlyResponseCompletionDate) {
		this.earlyResponseCompletionDate = earlyResponseCompletionDate;
	}

	@Column(columnDefinition = "text")
	public String getEarlyResponseCompletionNarrative() {
		return earlyResponseCompletionNarrative;
	}

	public void setEarlyResponseCompletionNarrative(String earlyResponseCompletionNarrative) {
		this.earlyResponseCompletionNarrative = earlyResponseCompletionNarrative;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getOutbreakEndDate() {
		return outbreakEndDate;
	}

	public void setOutbreakEndDate(Date outbreakEndDate) {
		this.outbreakEndDate = outbreakEndDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getReportCompletedDate() {
		return reportCompletedDate;
	}

	public void setReportCompletedDate(Date reportCompletedDate) {
		this.reportCompletedDate = reportCompletedDate;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReportCompletedByName() {
		return reportCompletedByName;
	}

	public void setReportCompletedByName(String reportCompletedByName) {
		this.reportCompletedByName = reportCompletedByName;
	}

	@Column(columnDefinition = "text")
	public String getGeneralNotes() {
		return generalNotes;
	}

	public void setGeneralNotes(String generalNotes) {
		this.generalNotes = generalNotes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getChangeDateOfEmbeddedLists() {
		return changeDateOfEmbeddedLists;
	}

	public void setChangeDateOfEmbeddedLists(Date changeDateOfEmbeddedLists) {
		this.changeDateOfEmbeddedLists = changeDateOfEmbeddedLists;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = Event717Bottleneck.ASSESSMENT)
	@OrderBy("id")
	public List<Event717Bottleneck> getBottlenecks() {
		return bottlenecks;
	}

	public void setBottlenecks(List<Event717Bottleneck> bottlenecks) {
		this.bottlenecks = bottlenecks;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = Event717Enabler.ASSESSMENT)
	@OrderBy("id")
	public List<Event717Enabler> getEnablers() {
		return enablers;
	}

	public void setEnablers(List<Event717Enabler> enablers) {
		this.enablers = enablers;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = Event717CorrectiveAction.ASSESSMENT)
	@OrderBy("id")
	public List<Event717CorrectiveAction> getCorrectiveActions() {
		return correctiveActions;
	}

	public void setCorrectiveActions(List<Event717CorrectiveAction> correctiveActions) {
		this.correctiveActions = correctiveActions;
	}
}

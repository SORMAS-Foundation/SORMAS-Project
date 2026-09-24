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

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizable;

/**
 * Detailed export of the 7-1-7 view of the event directory, laid out like the "Assess 7-1-7 results" sheet of the 7-1-7 data
 * consolidation spreadsheet plus the milestone and early response action dates.
 */
public class Event717ExportDto implements Serializable, Pseudonymizable {

	private static final long serialVersionUID = -3316370806394738560L;

	public static final String I18N_PREFIX = "Event717Export";

	private final String eventUuid;
	private final String eventTitle;
	private final Disease eventDisease;
	private final String eventDiseaseDetails;
	private final EventStatus eventStatus;
	private final String region;
	private final String district;
	private final String community;
	private final Date dateOfEmergence;
	private final Date dateOfDetection;
	private final Date dateOfNotification;
	private final String detectionDays;
	private final Event717TimelinessStatus detectionStatus;
	private final String notificationDays;
	private final Event717TimelinessStatus notificationStatus;
	private final Date investigationDate;
	private final String investigationDays;
	private final Date epiAnalysisDate;
	private final String epiAnalysisDays;
	private final Date labConfirmationDate;
	private final String labConfirmationDays;
	private final Date caseManagementDate;
	private final String caseManagementDays;
	private final Date countermeasuresDate;
	private final String countermeasuresDays;
	private final Date riskCommunicationDate;
	private final String riskCommunicationDays;
	private final Date coordinationDate;
	private final String coordinationDays;
	private final Date earlyResponseCompletionDate;
	private final String responseDays;
	private final Event717TimelinessStatus responseStatus;
	private final Event717TimelinessStatus timelinessStatus;
	@SensitiveData
	private String generalNotes;
	private boolean pseudonymized;
	private boolean inJurisdiction;

	//@formatter:off
	public Event717ExportDto(String eventUuid, String eventTitle, Disease eventDisease, String eventDiseaseDetails, EventStatus eventStatus,
							 String region, String district, String community,
							 Date dateOfEmergence, Date dateOfDetection, Date dateOfNotification,
							 Integer detectionDays, Event717TimelinessStatus detectionStatus,
							 Integer notificationDays, Event717TimelinessStatus notificationStatus,
							 Date investigationDate, Boolean investigationNotApplicable, Integer investigationDays,
							 Date epiAnalysisDate, Boolean epiAnalysisNotApplicable, Integer epiAnalysisDays,
							 Date labConfirmationDate, Boolean labConfirmationNotApplicable, Integer labConfirmationDays,
							 Date caseManagementDate, Boolean caseManagementNotApplicable, Integer caseManagementDays,
							 Date countermeasuresDate, Boolean countermeasuresNotApplicable, Integer countermeasuresDays,
							 Date riskCommunicationDate, Boolean riskCommunicationNotApplicable, Integer riskCommunicationDays,
							 Date coordinationDate, Boolean coordinationNotApplicable, Integer coordinationDays,
							 Date earlyResponseCompletionDate, Integer responseDays, Event717TimelinessStatus responseStatus,
							 Event717TimelinessStatus timelinessStatus, String generalNotes, boolean inJurisdiction) {
	//@formatter:on

		this.eventUuid = eventUuid;
		this.eventTitle = eventTitle;
		this.eventDisease = eventDisease;
		this.eventDiseaseDetails = eventDiseaseDetails;
		this.eventStatus = eventStatus;
		this.region = region;
		this.district = district;
		this.community = community;
		this.dateOfEmergence = dateOfEmergence;
		this.dateOfDetection = dateOfDetection;
		this.dateOfNotification = dateOfNotification;
		this.detectionDays = Event717TimelinessCalculator.formatDays(detectionDays);
		this.detectionStatus = detectionStatus;
		this.notificationDays = Event717TimelinessCalculator.formatDays(notificationDays);
		this.notificationStatus = notificationStatus;
		this.investigationDate = investigationDate;
		this.investigationDays = formatActionDays(investigationDays, investigationNotApplicable);
		this.epiAnalysisDate = epiAnalysisDate;
		this.epiAnalysisDays = formatActionDays(epiAnalysisDays, epiAnalysisNotApplicable);
		this.labConfirmationDate = labConfirmationDate;
		this.labConfirmationDays = formatActionDays(labConfirmationDays, labConfirmationNotApplicable);
		this.caseManagementDate = caseManagementDate;
		this.caseManagementDays = formatActionDays(caseManagementDays, caseManagementNotApplicable);
		this.countermeasuresDate = countermeasuresDate;
		this.countermeasuresDays = formatActionDays(countermeasuresDays, countermeasuresNotApplicable);
		this.riskCommunicationDate = riskCommunicationDate;
		this.riskCommunicationDays = formatActionDays(riskCommunicationDays, riskCommunicationNotApplicable);
		this.coordinationDate = coordinationDate;
		this.coordinationDays = formatActionDays(coordinationDays, coordinationNotApplicable);
		this.earlyResponseCompletionDate = earlyResponseCompletionDate;
		this.responseDays = Event717TimelinessCalculator.formatDays(responseDays);
		this.responseStatus = responseStatus;
		this.timelinessStatus = timelinessStatus;
		this.generalNotes = generalNotes;
		this.inJurisdiction = inJurisdiction;
	}

	private static String formatActionDays(Integer days, Boolean notApplicable) {
		return Event717TimelinessCalculator.formatActionDays(days, Boolean.TRUE.equals(notApplicable));
	}

	@Order(0)
	public String getEventUuid() {
		return eventUuid;
	}

	@Order(1)
	public String getEventTitle() {
		return eventTitle;
	}

	@Order(2)
	public Disease getEventDisease() {
		return eventDisease;
	}

	@Order(3)
	public String getEventDiseaseDetails() {
		return eventDiseaseDetails;
	}

	@Order(4)
	public EventStatus getEventStatus() {
		return eventStatus;
	}

	@Order(5)
	public String getRegion() {
		return region;
	}

	@Order(6)
	public String getDistrict() {
		return district;
	}

	@Order(7)
	public String getCommunity() {
		return community;
	}

	@Order(8)
	public Date getDateOfEmergence() {
		return dateOfEmergence;
	}

	@Order(9)
	public Date getDateOfDetection() {
		return dateOfDetection;
	}

	@Order(10)
	public String getDetectionDays() {
		return detectionDays;
	}

	@Order(11)
	public Event717TimelinessStatus getDetectionStatus() {
		return detectionStatus;
	}

	@Order(12)
	public Date getDateOfNotification() {
		return dateOfNotification;
	}

	@Order(13)
	public String getNotificationDays() {
		return notificationDays;
	}

	@Order(14)
	public Event717TimelinessStatus getNotificationStatus() {
		return notificationStatus;
	}

	@Order(15)
	public Date getInvestigationDate() {
		return investigationDate;
	}

	@Order(16)
	public String getInvestigationDays() {
		return investigationDays;
	}

	@Order(17)
	public Date getEpiAnalysisDate() {
		return epiAnalysisDate;
	}

	@Order(18)
	public String getEpiAnalysisDays() {
		return epiAnalysisDays;
	}

	@Order(19)
	public Date getLabConfirmationDate() {
		return labConfirmationDate;
	}

	@Order(20)
	public String getLabConfirmationDays() {
		return labConfirmationDays;
	}

	@Order(21)
	public Date getCaseManagementDate() {
		return caseManagementDate;
	}

	@Order(22)
	public String getCaseManagementDays() {
		return caseManagementDays;
	}

	@Order(23)
	public Date getCountermeasuresDate() {
		return countermeasuresDate;
	}

	@Order(24)
	public String getCountermeasuresDays() {
		return countermeasuresDays;
	}

	@Order(25)
	public Date getRiskCommunicationDate() {
		return riskCommunicationDate;
	}

	@Order(26)
	public String getRiskCommunicationDays() {
		return riskCommunicationDays;
	}

	@Order(27)
	public Date getCoordinationDate() {
		return coordinationDate;
	}

	@Order(28)
	public String getCoordinationDays() {
		return coordinationDays;
	}

	@Order(29)
	public Date getEarlyResponseCompletionDate() {
		return earlyResponseCompletionDate;
	}

	@Order(30)
	public String getResponseDays() {
		return responseDays;
	}

	@Order(31)
	public Event717TimelinessStatus getResponseStatus() {
		return responseStatus;
	}

	@Order(32)
	public Event717TimelinessStatus getTimelinessStatus() {
		return timelinessStatus;
	}

	@Order(33)
	public String getGeneralNotes() {
		return generalNotes;
	}

	@Override
	public boolean isPseudonymized() {
		return pseudonymized;
	}

	@Override
	public void setPseudonymized(boolean pseudonymized) {
		this.pseudonymized = pseudonymized;
	}

	@Override
	public boolean isInJurisdiction() {
		return inJurisdiction;
	}

	@Override
	public void setInJurisdiction(boolean inJurisdiction) {
		this.inJurisdiction = inJurisdiction;
	}
}

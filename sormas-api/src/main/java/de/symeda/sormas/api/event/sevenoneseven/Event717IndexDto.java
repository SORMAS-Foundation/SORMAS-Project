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
import java.util.EnumMap;
import java.util.EnumSet;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.audit.AuditedClass;

/**
 * Row of the 7-1-7 view of the event directory: the stored timeliness of one 7-1-7 assessment, laid out like the "Assess 7-1-7
 * results" sheet of the 7-1-7 data consolidation spreadsheet.
 */
@AuditedClass
public class Event717IndexDto implements Serializable {

	private static final long serialVersionUID = 6004843985187003718L;

	public static final String I18N_PREFIX = "Event717Index";

	public static final String EVENT_UUID = "eventUuid";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_DISEASE = "eventDisease";
	public static final String DATE_OF_EMERGENCE = "dateOfEmergence";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String DETECTION_DAYS = "detectionDays";
	public static final String DETECTION_STATUS = "detectionStatus";
	public static final String NOTIFICATION_DAYS = "notificationDays";
	public static final String NOTIFICATION_STATUS = "notificationStatus";
	public static final String INVESTIGATION_DAYS = "investigationDays";
	public static final String EPI_ANALYSIS_DAYS = "epiAnalysisDays";
	public static final String LAB_CONFIRMATION_DAYS = "labConfirmationDays";
	public static final String CASE_MANAGEMENT_DAYS = "caseManagementDays";
	public static final String COUNTERMEASURES_DAYS = "countermeasuresDays";
	public static final String RISK_COMMUNICATION_DAYS = "riskCommunicationDays";
	public static final String COORDINATION_DAYS = "coordinationDays";
	public static final String RESPONSE_DAYS = "responseDays";
	public static final String RESPONSE_STATUS = "responseStatus";
	public static final String TIMELINESS_STATUS = "timelinessStatus";

	private final String eventUuid;
	private final String eventTitle;
	private final Disease eventDisease;
	private final String eventDiseaseDetails;
	private final Date dateOfEmergence;
	private final String region;
	private final String district;
	private final String community;
	private final Integer detectionDays;
	private final Event717TimelinessStatus detectionStatus;
	private final Integer notificationDays;
	private final Event717TimelinessStatus notificationStatus;
	private final EnumMap<Event717EarlyResponseAction, Integer> earlyResponseActionDays = new EnumMap<>(Event717EarlyResponseAction.class);
	private final EnumSet<Event717EarlyResponseAction> notApplicableActions = EnumSet.noneOf(Event717EarlyResponseAction.class);
	private final Integer responseDays;
	private final Event717TimelinessStatus responseStatus;
	private final Event717TimelinessStatus timelinessStatus;

	//@formatter:off
	public Event717IndexDto(String eventUuid, String eventTitle, Disease eventDisease, String eventDiseaseDetails, Date dateOfEmergence,
							String region, String district, String community,
							Integer detectionDays, Event717TimelinessStatus detectionStatus,
							Integer notificationDays, Event717TimelinessStatus notificationStatus,
							Integer investigationDays, Boolean investigationNotApplicable,
							Integer epiAnalysisDays, Boolean epiAnalysisNotApplicable,
							Integer labConfirmationDays, Boolean labConfirmationNotApplicable,
							Integer caseManagementDays, Boolean caseManagementNotApplicable,
							Integer countermeasuresDays, Boolean countermeasuresNotApplicable,
							Integer riskCommunicationDays, Boolean riskCommunicationNotApplicable,
							Integer coordinationDays, Boolean coordinationNotApplicable,
							Integer responseDays, Event717TimelinessStatus responseStatus, Event717TimelinessStatus timelinessStatus) {
	//@formatter:on

		this.eventUuid = eventUuid;
		this.eventTitle = eventTitle;
		this.eventDisease = eventDisease;
		this.eventDiseaseDetails = eventDiseaseDetails;
		this.dateOfEmergence = dateOfEmergence;
		this.region = region;
		this.district = district;
		this.community = community;
		this.detectionDays = detectionDays;
		this.detectionStatus = detectionStatus;
		this.notificationDays = notificationDays;
		this.notificationStatus = notificationStatus;
		putAction(Event717EarlyResponseAction.INVESTIGATION, investigationDays, investigationNotApplicable);
		putAction(Event717EarlyResponseAction.EPI_ANALYSIS, epiAnalysisDays, epiAnalysisNotApplicable);
		putAction(Event717EarlyResponseAction.LAB_CONFIRMATION, labConfirmationDays, labConfirmationNotApplicable);
		putAction(Event717EarlyResponseAction.CASE_MANAGEMENT, caseManagementDays, caseManagementNotApplicable);
		putAction(Event717EarlyResponseAction.COUNTERMEASURES, countermeasuresDays, countermeasuresNotApplicable);
		putAction(Event717EarlyResponseAction.RISK_COMMUNICATION, riskCommunicationDays, riskCommunicationNotApplicable);
		putAction(Event717EarlyResponseAction.COORDINATION, coordinationDays, coordinationNotApplicable);
		this.responseDays = responseDays;
		this.responseStatus = responseStatus;
		this.timelinessStatus = timelinessStatus;
	}

	private void putAction(Event717EarlyResponseAction action, Integer days, Boolean notApplicable) {

		if (days != null) {
			earlyResponseActionDays.put(action, days);
		}
		if (Boolean.TRUE.equals(notApplicable)) {
			notApplicableActions.add(action);
		}
	}

	/**
	 * @return The property holding the days from the notification to the given early response action, used as column id and sort
	 *         property.
	 */
	public static String getEarlyResponseActionDaysProperty(Event717EarlyResponseAction action) {

		switch (action) {
		case INVESTIGATION:
			return INVESTIGATION_DAYS;
		case EPI_ANALYSIS:
			return EPI_ANALYSIS_DAYS;
		case LAB_CONFIRMATION:
			return LAB_CONFIRMATION_DAYS;
		case CASE_MANAGEMENT:
			return CASE_MANAGEMENT_DAYS;
		case COUNTERMEASURES:
			return COUNTERMEASURES_DAYS;
		case RISK_COMMUNICATION:
			return RISK_COMMUNICATION_DAYS;
		case COORDINATION:
			return COORDINATION_DAYS;
		default:
			throw new IllegalArgumentException(action.name());
		}
	}

	public String getEventUuid() {
		return eventUuid;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public Disease getEventDisease() {
		return eventDisease;
	}

	public String getEventDiseaseDetails() {
		return eventDiseaseDetails;
	}

	public Date getDateOfEmergence() {
		return dateOfEmergence;
	}

	public String getRegion() {
		return region;
	}

	public String getDistrict() {
		return district;
	}

	public String getCommunity() {
		return community;
	}

	public Integer getDetectionDays() {
		return detectionDays;
	}

	public Event717TimelinessStatus getDetectionStatus() {
		return detectionStatus;
	}

	public Integer getNotificationDays() {
		return notificationDays;
	}

	public Event717TimelinessStatus getNotificationStatus() {
		return notificationStatus;
	}

	public Integer getEarlyResponseActionDays(Event717EarlyResponseAction action) {
		return earlyResponseActionDays.get(action);
	}

	public boolean isEarlyResponseActionNotApplicable(Event717EarlyResponseAction action) {
		return notApplicableActions.contains(action);
	}

	public Integer getResponseDays() {
		return responseDays;
	}

	public Event717TimelinessStatus getResponseStatus() {
		return responseStatus;
	}

	public Event717TimelinessStatus getTimelinessStatus() {
		return timelinessStatus;
	}
}

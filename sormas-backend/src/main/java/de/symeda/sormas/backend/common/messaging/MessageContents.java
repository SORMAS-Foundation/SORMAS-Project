/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.symeda.sormas.backend.common.messaging;

public final class MessageContents {

    // Message contents (via properties file)
    public static final String CONTENT_CONTACT_WITHOUT_CASE_SYMPTOMATIC = "notificationContactWithoutCaseSymptomatic";
    public static final String CONTENT_CASE_CLASSIFICATION_CHANGED = "notificationCaseClassificationChanged";
    public static final String CONTENT_CASE_INVESTIGATION_DONE = "notificationCaseInvestigationDone";
    public static final String CONTENT_EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED = "notificationEventParticipantCaseClassificationConfirmed";
    public static final String CONTENT_EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS = "notificationEventParticipantRelatedToOtherEvents";
    public static final String CONTENT_LAB_RESULT_ARRIVED = "notificationLabResultArrived";
    public static final String CONTENT_LAB_RESULT_ARRIVED_CONTACT = "notificationLabResultArrivedContact";
    public static final String CONTENT_LAB_RESULT_ARRIVED_EVENT_PARTICIPANT = "notificationLabResultArrivedEventParticipant";
    public static final String CONTENT_LAB_RESULT_ARRIVED_EVENT_PARTICIPANT_NO_DISEASE = "notificationLabResultArrivedEventParticipantNoDisease";
    public static final String CONTENT_LAB_SAMPLE_SHIPPED = "notificationLabSampleShipped";
    public static final String CONTENT_LAB_SAMPLE_SHIPPED_SHORT = "notificationLabSampleShippedShort";
    public static final String CONTENT_LAB_SAMPLE_SHIPPED_SHORT_FOR_CONTACT = "notificationLabSampleShippedShortForContact";
    public static final String CONTENT_LAB_SAMPLE_SHIPPED_SHORT_FOR_EVENT_PARTICIPANT = "notificationLabSampleShippedShortForEventParticipant";
    public static final String CONTENT_CONTACT_SYMPTOMATIC = "notificationContactSymptomatic";
    public static final String CONTENT_TASK_OBSERVER_INFORMATION = "notificationTaskObserverInformation";
    public static final String CONTENT_TASK_START_GENERAL = "notificationTaskStartGeneral";
    public static final String CONTENT_TASK_START_SPECIFIC = "notificationTaskStartSpecific";
    public static final String CONTENT_TASK_DUE_GENERAL = "notificationTaskDueGeneral";
    public static final String CONTENT_TASK_DUE_SPECIFIC = "notificationTaskDueSpecific";
    public static final String CONTENT_TASK_GENERAL_UPDATED_ASSIGNEE_SOURCE = "notificationTaskGeneralUpdatedAssigneeUserSource";
    public static final String CONTENT_TASK_GENERAL_UPDATED_ASSIGNEE_TARGET = "notificationTaskGeneralUpdatedAssigneeUserTarget";
    public static final String CONTENT_TASK_SPECIFIC_UPDATED_ASSIGNEE_SOURCE = "notificationTaskSpecificUpdatedAssigneeUserSource";
    public static final String CONTENT_TASK_SPECIFIC_UPDATED_ASSIGNEE_TARGET = "notificationTaskSpecificUpdatedAssigneeUserTarget";
    public static final String CONTENT_VISIT_COMPLETED = "notificationVisitCompleted";
    public static final String CONTENT_DISEASE_CHANGED = "notificationDiseaseChanged";
    public static final String CONTENT_EVENT_GROUP_CREATED = "notificationEventGroupCreated";
    public static final String CONTENT_EVENT_ADDED_TO_EVENT_GROUP = "notificationEventAddedToEventGroup";
    public static final String CONTENT_EVENT_REMOVED_FROM_EVENT_GROUP = "notificationEventRemovedFromEventGroup";
    private MessageContents() {
    }
}

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

package de.symeda.sormas.api.user;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum NotificationType {


	CASE_CLASSIFICATION_CHANGED(NotificationTypeGroup.CASES),
	CASE_INVESTIGATION_DONE(NotificationTypeGroup.CASES),
	CASE_LAB_RESULT_ARRIVED(NotificationTypeGroup.CASES),
	CASE_DISEASE_CHANGED(NotificationTypeGroup.CASES),
	CONTACT_LAB_RESULT_ARRIVED(NotificationTypeGroup.CONTACTS),
	CONTACT_SYMPTOMATIC(NotificationTypeGroup.CONTACTS),
	CONTACT_VISIT_COMPLETED(NotificationTypeGroup.CONTACTS),
	EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED(FeatureType.EVENT_PARTICIPANT_CASE_CONFIRMED_NOTIFICATIONS, NotificationTypeGroup.EVENTS),
	EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS(FeatureType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS_NOTIFICATIONS, NotificationTypeGroup.EVENTS),
	EVENT_PARTICIPANT_LAB_RESULT_ARRIVED(NotificationTypeGroup.EVENTS),
	EVENT_GROUP_CREATED(FeatureType.EVENT_GROUPS_MODIFICATION_NOTIFICATIONS, NotificationTypeGroup.EVENTS),
	EVENT_ADDED_TO_EVENT_GROUP(FeatureType.EVENT_GROUPS_MODIFICATION_NOTIFICATIONS, NotificationTypeGroup.EVENTS),
	EVENT_REMOVED_FROM_EVENT_GROUP(FeatureType.EVENT_GROUPS_MODIFICATION_NOTIFICATIONS, NotificationTypeGroup.EVENTS),
	LAB_SAMPLE_SHIPPED(NotificationTypeGroup.SAMPLES),
	TASK_START(FeatureType.TASK_NOTIFICATIONS, NotificationTypeGroup.TASKS),
	TASK_DUE(FeatureType.TASK_NOTIFICATIONS, NotificationTypeGroup.TASKS),
	TASK_UPDATED_ASSIGNEE(FeatureType.TASK_NOTIFICATIONS, NotificationTypeGroup.TASKS);

	private final FeatureType relatedFeatureType;

	private final NotificationTypeGroup notificationTypeGroup;

	NotificationType(NotificationTypeGroup notificationTypeGroup) {
		this(FeatureType.OTHER_NOTIFICATIONS, notificationTypeGroup);
	}

	NotificationType(FeatureType relatedFeatureType, NotificationTypeGroup notificationTypeGroup) {
		this.relatedFeatureType = relatedFeatureType;
		this.notificationTypeGroup = notificationTypeGroup;
	}

	public FeatureType getRelatedFeatureType() {
		return relatedFeatureType;
	}

	public NotificationTypeGroup getNotificationTypeGroup() {
		return notificationTypeGroup;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public String getDescription() {
		return I18nProperties.getEnumDescription(this);
	}
}

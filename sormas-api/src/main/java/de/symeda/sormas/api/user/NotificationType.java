/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.user;

import de.symeda.sormas.api.feature.FeatureType;

public enum NotificationType {

	CASE_CLASSIFICATION_CHANGED,
	CASE_INVESTIGATION_DONE,
	EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED(FeatureType.EVENT_PARTICIPANT_CASE_CONFIRMED_NOTIFICATIONS),
	EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS(FeatureType.EVENT_PARTICIPANT_RELATED_TO_OTHER_EVENTS_NOTIFICATIONS),
	CASE_LAB_RESULT_ARRIVED,
	CONTACT_LAB_RESULT_ARRIVED,
	EVENT_PARTICIPANT_LAB_RESULT_ARRIVED,
	LAB_SAMPLE_SHIPPED,
	CONTACT_SYMPTOMATIC,
	TASK_START(FeatureType.TASK_NOTIFICATIONS),
	TASK_DUE(FeatureType.TASK_NOTIFICATIONS),
	TASK_UPDATED_ASSIGNEE(FeatureType.TASK_NOTIFICATIONS),
	VISIT_COMPLETED,
	DISEASE_CHANGED,
	EVENT_GROUP_CREATED(FeatureType.EVENT_GROUPS_MODIFICATION_NOTIFICATIONS),
	EVENT_ADDED_TO_EVENT_GROUP(FeatureType.EVENT_GROUPS_MODIFICATION_NOTIFICATIONS),
	EVENT_REMOVED_FROM_EVENT_GROUP(FeatureType.EVENT_GROUPS_MODIFICATION_NOTIFICATIONS);

	private final FeatureType relatedFeatureType;

	NotificationType() {
		this.relatedFeatureType = FeatureType.OTHER_NOTIFICATIONS;
	}

	NotificationType(FeatureType relatedFeatureType) {
		this.relatedFeatureType = relatedFeatureType;
	}

	public FeatureType getRelatedFeatureType() {
		return relatedFeatureType;
	}
}

package de.symeda.sormas.backend.common.messaging;

import de.symeda.sormas.api.feature.FeatureType;

public enum MessageSubject {

	CASE_CLASSIFICATION_CHANGED,
	CASE_INVESTIGATION_DONE,
	EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED(FeatureType.EVENT_PARTICIPANT_CASE_CONFIRMED_NOTIFICATIONS),
	LAB_RESULT_ARRIVED,
	LAB_RESULT_SPECIFIED,
	LAB_SAMPLE_SHIPPED,
	CONTACT_SYMPTOMATIC,
	TASK_START(FeatureType.TASK_NOTIFICATIONS),
	TASK_DUE(FeatureType.TASK_NOTIFICATIONS),
	VISIT_COMPLETED,
	DISEASE_CHANGED;

	private final FeatureType relatedFeatureType;

	MessageSubject() {
		this.relatedFeatureType = FeatureType.OTHER_NOTIFICATIONS;
	}

	MessageSubject(FeatureType relatedFeatureType) {
		this.relatedFeatureType = relatedFeatureType;
	}

	public FeatureType getRelatedFeatureType() {
		return relatedFeatureType;
	}
}

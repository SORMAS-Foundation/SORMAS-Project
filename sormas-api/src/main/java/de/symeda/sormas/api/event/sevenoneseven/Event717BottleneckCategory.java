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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Common bottleneck categories as recommended by the 7-1-7 Alliance, grouped by {@link Event717BottleneckTheme}.
 */
public enum Event717BottleneckCategory {

	// Clinical or health care worker
	HCW_INADEQUATE_TRAINING(Event717BottleneckTheme.CLINICAL_HEALTH_CARE_WORKER),
	HCW_LIMITED_CASE_MANAGEMENT_CAPACITY(Event717BottleneckTheme.CLINICAL_HEALTH_CARE_WORKER),
	HCW_LOW_AWARENESS_CLINICAL_SUSPICION(Event717BottleneckTheme.CLINICAL_HEALTH_CARE_WORKER),
	HCW_INADEQUATE_SURVEILLANCE_FOCAL_POINT(Event717BottleneckTheme.CLINICAL_HEALTH_CARE_WORKER),

	// Coordination
	COORD_ACROSS_PUBLIC_HEALTH_UNITS(Event717BottleneckTheme.COORDINATION),
	COORD_MULTISECTORAL_TEAMS(Event717BottleneckTheme.COORDINATION),
	COORD_ONE_HEALTH_INFORMATION_SHARING(Event717BottleneckTheme.COORDINATION),
	COORD_WEAK_RESPONSE_COORDINATION(Event717BottleneckTheme.COORDINATION),
	COORD_NEIGHBORING_COUNTRIES(Event717BottleneckTheme.COORDINATION),
	COORD_PUBLIC_PRIVATE(Event717BottleneckTheme.COORDINATION),

	// Laboratory
	LAB_DIAGNOSTIC_CAPACITY(Event717BottleneckTheme.LABORATORY),
	LAB_DELAYED_SPECIMEN_COLLECTION(Event717BottleneckTheme.LABORATORY),
	LAB_DELAYED_SPECIMEN_TRANSPORTATION(Event717BottleneckTheme.LABORATORY),
	LAB_DIAGNOSTIC_COMMODITIES(Event717BottleneckTheme.LABORATORY),
	LAB_REPORTING_FAILURE_DELAY(Event717BottleneckTheme.LABORATORY),
	LAB_SLOW_INTERNAL_TURNAROUND(Event717BottleneckTheme.LABORATORY),

	// Data systems
	DATA_LACK_TIMELY_COMPLETE_DATA(Event717BottleneckTheme.DATA_SYSTEMS),
	DATA_TECHNOLOGICAL_CHALLENGE(Event717BottleneckTheme.DATA_SYSTEMS),

	// Event characteristics
	EVENT_ACCESS_ISSUES(Event717BottleneckTheme.EVENT_CHARACTERISTICS),
	EVENT_NEW_UNEXPECTED_DEPRIORITIZED_PATHOGEN(Event717BottleneckTheme.EVENT_CHARACTERISTICS),

	// Patient and community
	COMMUNITY_DELAY_CARE_SEEKING(Event717BottleneckTheme.PATIENT_COMMUNITY),
	COMMUNITY_INADEQUATE_DETECTION_SENSITIVITY(Event717BottleneckTheme.PATIENT_COMMUNITY),
	COMMUNITY_LOW_KNOWLEDGE_TRUST(Event717BottleneckTheme.PATIENT_COMMUNITY),
	COMMUNITY_INADEQUATE_RISK_COMMUNICATION(Event717BottleneckTheme.PATIENT_COMMUNITY),

	// Planning and procedures
	PLAN_NOT_FOLLOWING_NOTIFICATION_PROCEDURES(Event717BottleneckTheme.PLANNING_PROCEDURES),
	PLAN_NOT_FOLLOWING_RISK_ASSESSMENT_PROCEDURES(Event717BottleneckTheme.PLANNING_PROCEDURES),
	PLAN_INADEQUATE_NOTIFICATION_PROCEDURES(Event717BottleneckTheme.PLANNING_PROCEDURES),
	PLAN_INADEQUATE_PLANS(Event717BottleneckTheme.PLANNING_PROCEDURES),
	PLAN_INADEQUATE_POLICIES_GUIDELINES(Event717BottleneckTheme.PLANNING_PROCEDURES),

	// Resources and procurement
	RES_COMPETING_PRIORITIES(Event717BottleneckTheme.RESOURCES_PROCUREMENT),
	RES_INADEQUATE_RESPONSE_FINANCING(Event717BottleneckTheme.RESOURCES_PROCUREMENT),
	RES_LIMITED_COUNTERMEASURES_PPE(Event717BottleneckTheme.RESOURCES_PROCUREMENT),
	RES_LOGISTICS_SHIPMENT_DELAYS(Event717BottleneckTheme.RESOURCES_PROCUREMENT),
	RES_HUMAN_RESOURCE_GAPS(Event717BottleneckTheme.RESOURCES_PROCUREMENT),
	RES_DELAYED_APPROVALS(Event717BottleneckTheme.RESOURCES_PROCUREMENT),
	RES_INADEQUATE_PUBLIC_FINANCIAL_ASSISTANCE(Event717BottleneckTheme.RESOURCES_PROCUREMENT),

	OTHER(null);

	private final Event717BottleneckTheme theme;

	Event717BottleneckCategory(Event717BottleneckTheme theme) {
		this.theme = theme;
	}

	public Event717BottleneckTheme getTheme() {
		return theme;
	}

	public static List<Event717BottleneckCategory> getByTheme(Event717BottleneckTheme theme) {
		return Arrays.stream(values()).filter(c -> c.theme == theme).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}

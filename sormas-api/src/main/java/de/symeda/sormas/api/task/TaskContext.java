/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.task;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

public enum TaskContext {

	CASE(FeatureType.TASK_GENERATION_CASE_SURVEILLANCE, "cases", Strings.notificationTaskAssociatedCaseLink),
	CONTACT(FeatureType.TASK_GENERATION_CONTACT_TRACING, "contacts", Strings.notificationTaskAssociatedContactLink),
	EVENT(FeatureType.TASK_GENERATION_EVENT_SURVEILLANCE, "events", Strings.notificationTaskAssociatedEventLink),
	GENERAL(FeatureType.TASK_GENERATION_GENERAL, null, null);

	private final FeatureType featureType;
	private final String urlPattern;
	private final String associatedEntityLinkMessage;

	TaskContext(FeatureType featureType, String urlPattern, String associatedEntityLinkMessage) {
		this.featureType = featureType;
		this.urlPattern = urlPattern;
		this.associatedEntityLinkMessage = associatedEntityLinkMessage;
	}

	public FeatureType getFeatureType() {
		return featureType;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public String getAssociatedEntityLinkMessage() {
		return associatedEntityLinkMessage;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}

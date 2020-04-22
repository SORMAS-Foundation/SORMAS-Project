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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.task;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;

public enum TaskContext {

	CASE(FeatureType.TASK_GENERATION_CASE_SURVEILLANCE),
	CONTACT(FeatureType.TASK_GENERATION_CONTACT_TRACING),
	EVENT(FeatureType.TASK_GENERATION_EVENT_SURVEILLANCE),
	GENERAL(FeatureType.TASK_GENERATION_GENERAL);
	
	private FeatureType featureType;
	
	TaskContext(FeatureType featureType) {
		this.featureType = featureType;
	}
	
	public FeatureType getFeatureType() {
		return featureType;
	}

	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}

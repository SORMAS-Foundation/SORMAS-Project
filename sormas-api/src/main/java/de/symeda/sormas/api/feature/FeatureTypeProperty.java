/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.feature;

public enum FeatureTypeProperty {

	REDUCED(Boolean.class),
	AUTOMATIC_RESPONSIBILITY_ASSIGNMENT(Boolean.class),
	ALLOW_FREE_FOLLOW_UP_OVERWRITE(Boolean.class),
	ALLOW_FREE_EDITING(Boolean.class),
	THRESHOLD_IN_DAYS(Integer.class),
	EXCLUDE_NO_CASE_CLASSIFIED_CASES(Boolean.class),
	MAX_CHANGE_DATE_PERIOD(Integer.class),
	S2S_SHARING(Boolean.class),
	SHARE_ASSOCIATED_CONTACTS(Boolean.class),
	SHARE_SAMPLES(Boolean.class),
	SHARE_IMMUNIZATIONS(Boolean.class),
	SHARE_REPORTS(Boolean.class),
	FETCH_MODE(Boolean.class);

	private final Class<?> returnType;

	FeatureTypeProperty(Class<?> returnType) {
		this.returnType = returnType;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

}

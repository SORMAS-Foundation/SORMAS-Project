/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.sample;

import java.io.Serializable;

import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;

public class PathogenTestCriteria implements Serializable {

	private Sample sample;
	private EnvironmentSample environmentSample;

	public PathogenTestCriteria sample(Sample sample) {
		this.sample = sample;
		return this;
	}

	public Sample getSample() {
		return sample;
	}

	public PathogenTestCriteria environmentSample(EnvironmentSample environmentSample) {
		this.environmentSample = environmentSample;
		return this;
	}

	public EnvironmentSample getEnvironmentSample() {
		return environmentSample;
	}
}

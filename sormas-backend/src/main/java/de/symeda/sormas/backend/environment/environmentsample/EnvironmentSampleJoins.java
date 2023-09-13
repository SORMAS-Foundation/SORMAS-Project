/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.environment.environmentsample;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentJoins;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.location.Location;

public class EnvironmentSampleJoins extends QueryJoins<EnvironmentSample> {

	private Join<EnvironmentSample, Location> location;
	private Join<EnvironmentSample, Environment> environment;
	private EnvironmentJoins environmentJoins;
	private Join<EnvironmentSample, Facility> laboratory;

	public EnvironmentSampleJoins(From<?, EnvironmentSample> root) {
		super(root);
	}

	public Join<EnvironmentSample, Location> getLocation() {
		return getOrCreate(location, EnvironmentSample.LOCATION, JoinType.LEFT, this::setLocation);
	}

	private void setLocation(Join<EnvironmentSample, Location> location) {
		this.location = location;
	}

	public Join<EnvironmentSample, Environment> getEnvironment() {
		return getOrCreate(environment, EnvironmentSample.ENVIRONMENT, JoinType.LEFT, this::setEnvironment);
	}

	private void setEnvironment(Join<EnvironmentSample, Environment> environment) {
		this.environment = environment;
	}

	public EnvironmentJoins getEnvironmentJoins() {
		return getOrCreate(environmentJoins, () -> new EnvironmentJoins(getEnvironment()), this::setEnvironmentJoins);
	}

	private void setEnvironmentJoins(EnvironmentJoins environmentJoins) {
		this.environmentJoins = environmentJoins;
	}

	public Join<EnvironmentSample, Facility> getLaboratory() {
		return getOrCreate(laboratory, EnvironmentSample.LABORATORY, JoinType.LEFT, this::setLaboratory);
	}

	public void setLaboratory(Join<EnvironmentSample, Facility> laboratory) {
		this.laboratory = laboratory;
	}
}

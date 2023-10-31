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

package de.symeda.sormas.api.infrastructure;

import java.util.Date;

public abstract class InfrastructureDtoWithDefault extends InfrastructureDto {

	public static final String DEFAULT_INFRASTRUCTURE = "defaultInfrastructure";

	private boolean defaultInfrastructure;

	protected InfrastructureDtoWithDefault() {
		super();
	}

	protected InfrastructureDtoWithDefault(Date creationDate, Date changeDate, String uuid, boolean archived) {
		super(creationDate, changeDate, uuid, archived);
	}

	public boolean isDefaultInfrastructure() {
		return defaultInfrastructure;
	}

	public void setDefaultInfrastructure(boolean defaultInfrastructure) {
		this.defaultInfrastructure = defaultInfrastructure;
	}

}

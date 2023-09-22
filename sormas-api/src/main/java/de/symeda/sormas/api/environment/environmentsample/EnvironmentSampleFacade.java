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

package de.symeda.sormas.api.environment.environmentsample;

import javax.ejb.Remote;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.DeletableFacade;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;

@Remote
public interface EnvironmentSampleFacade
	extends BaseFacade<EnvironmentSampleDto, EnvironmentSampleIndexDto, EnvironmentSampleReferenceDto, EnvironmentSampleCriteria>, DeletableFacade {

	boolean exists(String uuid);

	boolean isEditAllowed(String uuid);

	EditPermissionType getEditPermissionType(String sampleUuid);

	DeletionInfoDto getAutomaticDeletionInfo(String sampleUuid);

	DeletionInfoDto getManuallyDeletionInfo(String sampleUuid);
}

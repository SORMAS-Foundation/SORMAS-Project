/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.data.received;

import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;

public abstract class ReceivedDataProcessor<DTO extends SormasToSormasShareableDto, SHARED extends SormasToSormasEntityDto<DTO>, PREVIEW extends PseudonymizableDto, ENTITY extends SormasToSormasShareable> {

	public ValidationErrors processReceivedData(SHARED sharedData, ENTITY existingData) {
		ValidationErrors uuidError = exists(sharedData.getEntity().getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}

		handleReceivedData(sharedData, existingData);
		return validation(sharedData, existingData);
	}

	public abstract void handleReceivedData(SHARED sharedData, ENTITY existingData);

	public ValidationErrors processReceivedPreview(PREVIEW sharedPreview) {
		ValidationErrors uuidError = exists(sharedPreview.getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}
		return validatePreview(sharedPreview);
	}

	public abstract ValidationErrors exists(String uuid);

	public abstract ValidationErrors validation(SHARED sharedData, ENTITY existingData);

	public abstract ValidationErrors validatePreview(PREVIEW preview);
}

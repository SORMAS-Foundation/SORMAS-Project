/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.sormastosormas.share;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;

public abstract class ShareDataBuilder<DTO extends SormasToSormasShareableDto, ADO extends SormasToSormasShareable, SHARED extends SormasToSormasEntityDto<DTO>, PREVIEW extends PseudonymizableDto, VALIDATOR extends SormasToSormasDtoValidator<DTO, SHARED, PREVIEW>> {

	VALIDATOR validator;

	protected ShareDataBuilder(VALIDATOR validator) {
		this.validator = validator;
	}

	protected ShareDataBuilder() {
	}

	protected abstract SHARED doBuildShareData(ADO data, ShareRequestInfo requestInfo);

	public SHARED buildShareData(ADO data, ShareRequestInfo requestInfo) throws SormasToSormasValidationException {
		SHARED shared = doBuildShareData(data, requestInfo);
		ValidationErrors errors = validator.validateOutgoing(shared);
		if (errors.hasError()) {
			List<ValidationErrors> validationErrors = new ArrayList<>();
			validationErrors.add(errors);
			throw new SormasToSormasValidationException(validationErrors);
		}
		return shared;
	}

	protected abstract PREVIEW doBuildShareDataPreview(ADO data, ShareRequestInfo requestInfo);

	public PREVIEW buildShareDataPreview(ADO data, ShareRequestInfo requestInfo) throws SormasToSormasValidationException {
		PREVIEW shared = doBuildShareDataPreview(data, requestInfo);
		ValidationErrors errors = validator.validateOutgoingPreview(shared);
		if (errors.hasError()) {
			List<ValidationErrors> validationErrors = new ArrayList<>();
			validationErrors.add(errors);
			throw new SormasToSormasValidationException(validationErrors);
		}
		return shared;
	}
}

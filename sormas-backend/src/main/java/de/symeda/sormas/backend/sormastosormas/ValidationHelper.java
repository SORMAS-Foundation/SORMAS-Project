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

package de.symeda.sormas.backend.sormastosormas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

public class ValidationHelper {

	public static ValidationErrorGroup buildValidationGroupName(String captionTag, HasUuid hasUuid) {
		return buildValidationGroupName(captionTag, hasUuid.getUuid());
	}

	public static ValidationErrorGroup buildValidationGroupName(String captionTag, String uuid) {
		return new ValidationErrorGroup(captionTag, DataHelper.getShortUuid(uuid));
	}

	public static ValidationErrorGroup buildCaseValidationGroupName(HasUuid caze) {
		return buildValidationGroupName(Captions.CaseData_uuid, caze);
	}

	public static ValidationErrorGroup buildContactValidationGroupName(HasUuid contact) {
		return buildValidationGroupName(Captions.Contact_uuid, contact);
	}

	public static ValidationErrorGroup buildSampleValidationGroupName(SampleDto sample) {
		return buildValidationGroupName(Captions.Sample_uuid, sample);
	}

	public static ValidationErrorGroup buildPathogenTestValidationGroupName(PathogenTestDto pathogenTest) {
		return buildValidationGroupName(Captions.PathogenTest, pathogenTest);
	}

	public static ValidationErrorGroup buildEventValidationGroupName(HasUuid event) {
		return buildValidationGroupName(Captions.Event_uuid, event);
	}

	public static ValidationErrorGroup buildEventParticipantValidationGroupName(HasUuid event) {
		return buildValidationGroupName(Captions.EventParticipant_uuid, event);
	}

	public static ValidationErrorGroup buildImmunizationValidationGroupName(ImmunizationDto immunization) {
		return buildValidationGroupName(Captions.Immunization_uuid, immunization);
	}

	public static ValidationErrorGroup buildLabMessageValidationGroupName(LabMessageDto labMessageDto) {
		return buildValidationGroupName(Captions.LabMessage, labMessageDto);
	}

	public static <T> T handleValidationError(Supplier<T> saveOperation, String validationGroupCaption, ValidationErrorGroup parentValidationGroup)
		throws SormasToSormasValidationException {
		try {
			return saveOperation.get();
		} catch (ValidationRuntimeException exception) {
			List<ValidationErrors> errors = new ArrayList<>();

			ValidationErrors validationErrors = new ValidationErrors(parentValidationGroup);
			validationErrors.add(new ValidationErrorGroup(validationGroupCaption), new ValidationErrorMessage(Validations.sormasToSormasSaveException, exception.getMessage()));
			errors.add(validationErrors);

			throw new SormasToSormasValidationException(errors, exception);
		}
	}
}

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

package de.symeda.sormas.backend.sormastosormas.entities.immunization;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasImmunizationPreview;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasCommonDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.vaccination.Vaccination;

@Stateless
@LocalBean
public class ReceivedImmunizationProcessor
	implements ReceivedDataProcessor<ImmunizationDto, SormasToSormasEntityDto<ImmunizationDto>, Void, Immunization> {

	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private Sormas2SormasCommonDataValidator commonDataValidator;
	@EJB
	private InfrastructureValidator infraValidator;
	@EJB
	private UserService userService;
	@EJB
	private ImmunizationValidatorS2S immunizationValidator;

	@Override
	public ValidationErrors processReceivedData(SormasToSormasEntityDto<ImmunizationDto> sharedData, Immunization existingData) {
		ImmunizationDto im = sharedData.getEntity();

		ValidationErrors uuidError = validateSharedUuid(im.getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}

		commonDataValidator.handleIgnoredProperties(im, ImmunizationFacadeEjb.toDto(existingData));
		commonDataValidator.updateReportingUser(im, existingData);

		im.getVaccinations().forEach(vaccination -> {
			Vaccination existingVaccination = existingData == null
				? null
				: existingData.getVaccinations().stream().filter(v -> v.getUuid().equals(vaccination.getUuid())).findFirst().orElse(null);
			UserReferenceDto reportingUser =
				existingVaccination == null ? userService.getCurrentUser().toReference() : existingVaccination.getReportingUser().toReference();

			vaccination.setReportingUser(reportingUser);
		});

		return immunizationValidator.validateInboundEntity(im);
	}

	@Override
	public ValidationErrors processReceivedPreview(Void sharedPreview) {
		return immunizationValidator.validateInboundPreviewEntity(new SormasToSormasImmunizationPreview());
	}

	private ValidationErrors validateSharedUuid(String uuid) {
		ValidationErrors errors = new ValidationErrors();

		if (immunizationService.exists(
			(cb, immunizationRoot, cq) -> cb.and(
				cb.equal(immunizationRoot.get(Immunization.UUID), uuid),
				cb.isNull(immunizationRoot.get(Immunization.SORMAS_TO_SORMAS_ORIGIN_INFO)),
				cb.isEmpty(immunizationRoot.get(Immunization.SORMAS_TO_SORMAS_SHARES))))) {
			errors.add(new ValidationErrorGroup(Captions.Immunization), new ValidationErrorMessage(Validations.sormasToSormasImmunizationExists));
		}

		return errors;
	}
}

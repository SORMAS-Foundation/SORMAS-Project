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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.vaccination.Vaccination;

@Stateless
@LocalBean
public class ReceivedImmunizationProcessor
	extends ReceivedDataProcessor<ImmunizationDto, SormasToSormasEntityDto<ImmunizationDto>, PreviewNotImplementedDto, Immunization> {

	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private Sormas2SormasDataValidator dataValidator;

	@EJB
	private InfrastructureValidator infraValidator;
	@EJB
	private UserService userService;

	@Override
	public void handleReceivedData(SormasToSormasEntityDto<ImmunizationDto> sharedData, Immunization existingData) {
		dataValidator.updateReportingUser(sharedData.getEntity(), existingData);
		dataValidator.handleIgnoredProperties(sharedData.getEntity(), ImmunizationFacadeEjb.toDto(existingData));
	}

	@Override
	public ValidationErrors processReceivedPreview(PreviewNotImplementedDto sharedPreview) {
		throw new RuntimeException("Immunizations preview not yet implemented");
	}

	@Override
	public ValidationErrors exists(String uuid) {
		ValidationErrors errors = new ValidationErrors();
		if (immunizationService.exists(
			(cb, immunizationRoot, cq) -> cb.and(
				cb.equal(immunizationRoot.get(AbstractDomainObject.UUID), uuid),
				cb.isNull(immunizationRoot.get(Immunization.SORMAS_TO_SORMAS_ORIGIN_INFO)),
				cb.isEmpty(immunizationRoot.get(Immunization.SORMAS_TO_SORMAS_SHARES))))) {
			errors.add(new ValidationErrorGroup(Captions.Immunization), new ValidationErrorMessage(Validations.sormasToSormasImmunizationExists));
		}

		return errors;
	}

	@Override
	public ValidationErrors validation(SormasToSormasEntityDto<ImmunizationDto> sharedData, Immunization existingData) {
		ImmunizationDto immunization = sharedData.getEntity();
		DataHelper.Pair<InfrastructureValidator.InfrastructureData, List<ValidationErrorMessage>> infrastructureAndErrors =
			infraValidator.validateInfrastructure(
				null,
				null,
				immunization.getCountry(),
				immunization.getResponsibleRegion(),
				immunization.getResponsibleDistrict(),
				immunization.getResponsibleCommunity(),
				immunization.getFacilityType(),
				immunization.getHealthFacility(),
				immunization.getHealthFacilityDetails(),
				null,
				null);

		ValidationErrors validationErrors = new ValidationErrors();
		infraValidator.handleInfraStructure(infrastructureAndErrors, Captions.Sample_lab, validationErrors, (infrastructureData -> {
			immunization.setCountry(infrastructureData.getCountry());
			immunization.setResponsibleRegion(infrastructureData.getRegion());
			immunization.setResponsibleDistrict(infrastructureData.getDistrict());
			immunization.setResponsibleCommunity(infrastructureData.getCommunity());
			immunization.setHealthFacility(infrastructureData.getFacility());
			immunization.setHealthFacilityDetails(infrastructureData.getFacilityDetails());
		}));

		immunization.getVaccinations().forEach(vaccination -> {
			Vaccination existingVaccination = existingData == null
				? null
				: existingData.getVaccinations().stream().filter(v -> v.getUuid().equals(vaccination.getUuid())).findFirst().orElse(null);
			UserReferenceDto reportingUser =
				existingVaccination == null ? userService.getCurrentUser().toReference() : existingVaccination.getReportingUser().toReference();

			vaccination.setReportingUser(reportingUser);
		});

		return validationErrors;
	}

	@Override
	public ValidationErrors validatePreview(PreviewNotImplementedDto previewNotImplementedDto) {
		throw new RuntimeException("Immunizations preview not yet implemented");
	}
}

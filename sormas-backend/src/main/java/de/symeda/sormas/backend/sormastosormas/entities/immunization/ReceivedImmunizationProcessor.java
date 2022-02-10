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
import javax.inject.Inject;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sormastosormas.immunization.SormasToSormasImmunizationDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.vaccination.Vaccination;

@Stateless
@LocalBean
public class ReceivedImmunizationProcessor
	extends
	ReceivedDataProcessor<Immunization, ImmunizationDto, SormasToSormasImmunizationDto, PreviewNotImplementedDto, Immunization, ImmunizationService, SormasToSormasImmunizationDtoValidator> {

	@EJB
	private ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal immunizationFacadeEjb;

	public ReceivedImmunizationProcessor() {
	}

	@Inject
	protected ReceivedImmunizationProcessor(
		ImmunizationService service,
		UserService userService,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade,
		SormasToSormasImmunizationDtoValidator validator) {
		super(service, userService, configFacade, validator);
	}

	@Override
	public void handleReceivedData(SormasToSormasImmunizationDto sharedData, Immunization existingData) {
		updateReportingUser(sharedData.getEntity(), existingData);
		handleIgnoredProperties(sharedData.getEntity(), immunizationFacadeEjb.toDto(existingData));

		ImmunizationDto im = sharedData.getEntity();
		im.getVaccinations().forEach(vaccination -> {
			Vaccination existingVaccination;
			if (existingData == null) {
				existingVaccination = null;
			} else {
				existingVaccination =
					existingData.getVaccinations().stream().filter(v -> v.getUuid().equals(vaccination.getUuid())).findFirst().orElse(null);
			}
			UserReferenceDto reportingUser;
			if (existingVaccination == null) {
				reportingUser = userService.getCurrentUser().toReference();
			} else {
				reportingUser = existingVaccination.getReportingUser().toReference();
			}
			vaccination.setReportingUser(reportingUser);
		});
	}

	@Override
	public ValidationErrors processReceivedPreview(PreviewNotImplementedDto sharedPreview) {
		throw new RuntimeException("Immunizations preview not yet implemented");
	}

	@Override
	public ValidationErrors existsNotShared(String uuid) {
		return existsNotShared(
			uuid,
			Immunization.SORMAS_TO_SORMAS_ORIGIN_INFO,
			Immunization.SORMAS_TO_SORMAS_SHARES,
			Captions.Immunization,
			Validations.sormasToSormasImmunizationExists);
	}
}

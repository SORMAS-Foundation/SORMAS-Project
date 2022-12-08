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

package de.symeda.sormas.backend.sormastosormas.entities.surveillancereport;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildSurveillanceReportValidationGroupName;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.entities.surveillancereport.SormasToSormasSurveillanceReportDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.data.infra.InfrastructureValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;
import de.symeda.sormas.backend.sormastosormas.data.validation.ValidationDirection;
import de.symeda.sormas.backend.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageDtoValidator;

@Stateless
@LocalBean
public class SormasSormasToSurveillanceReportDtoValidator
	extends SormasToSormasDtoValidator<SurveillanceReportDto, SormasToSormasSurveillanceReportDto, PreviewNotImplementedDto> {

	@EJB
	private SormasToSormasExternalMessageDtoValidator externalMessageDtoValidator;

	public SormasSormasToSurveillanceReportDtoValidator() {
	}

	@Inject
	public SormasSormasToSurveillanceReportDtoValidator(InfrastructureValidator infraValidator) {
		super(infraValidator);
	}

	@Override
	public ValidationErrors validate(SormasToSormasSurveillanceReportDto sharedData, ValidationDirection direction) {
		final SurveillanceReportDto report = sharedData.getEntity();
		ValidationErrors validationErrors = new ValidationErrors(buildSurveillanceReportValidationGroupName(report));

		final String groupNameTag = Captions.SurveillanceReport;
		infraValidator.validateResponsibleRegion(report.getFacilityRegion(), groupNameTag, validationErrors, report::setFacilityRegion, direction);
		infraValidator
			.validateResponsibleDistrict(report.getFacilityDistrict(), groupNameTag, validationErrors, report::setFacilityDistrict, direction);
		infraValidator
			.validateFacility(report.getFacility(), report.getFacilityType(), report.getFacilityDetails(), groupNameTag, validationErrors, f -> {
				report.setFacility(f.getEntity());
				report.setFacilityDetails(f.getDetails());
			});

		SormasToSormasExternalMessageDto externalMessage = sharedData.getExternalMessage();
		if (externalMessage != null) {
			validationErrors.addAll(externalMessageDtoValidator.validate(externalMessage, direction));
		}

		return validationErrors;
	}

	@Override
	public ValidationErrors validatePreview(PreviewNotImplementedDto previewNotImplementedDto, ValidationDirection direction) {
		// todo adjust test in InfraValidationSoundnessTest once preview is available for this entity
		throw new RuntimeException("SurveillanceReport preview not yet implemented");
	}
}

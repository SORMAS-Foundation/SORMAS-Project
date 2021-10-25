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

package de.symeda.sormas.backend.sormastosormas.entities.sample;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasCommonDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;

@Stateless
@LocalBean
public class ReceivedSampleProcessor implements ReceivedDataProcessor<SampleDto, SormasToSormasSampleDto, Void, Sample> {

	@EJB
	private Sormas2SormasCommonDataValidator commonDataValidator;
	@EJB
	private SampleService sampleService;
	@EJB
	private SampleValidatorS2S sampleValidator;

	@Override
	public ValidationErrors processReceivedData(SormasToSormasSampleDto sharedData, Sample existingSample) {
		SampleDto sample = sharedData.getEntity();

		ValidationErrors uuidError = validateSharedUuid(sample.getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}

		commonDataValidator.updateReportingUser(sample, existingSample);
		ValidationErrors validationErrors = sampleValidator.validateInboundEntity(sample);

		sharedData.getPathogenTests().forEach(pathogenTest -> commonDataValidator.validatePathogenTest(validationErrors, pathogenTest));

		return validationErrors;
	}

	@Override
	public ValidationErrors processReceivedPreview(Void sharedPreview) {
		throw new RuntimeException("Samples preview not yet implemented");
	}

	private ValidationErrors validateSharedUuid(String uuid) {
		ValidationErrors errors = new ValidationErrors();

		if (sampleService.exists(
			(cb, sampleRoot, cq) -> cb.and(
				cb.equal(sampleRoot.get(Sample.UUID), uuid),
				cb.isNull(sampleRoot.get(Sample.SORMAS_TO_SORMAS_ORIGIN_INFO)),
				cb.isEmpty(sampleRoot.get(Sample.SORMAS_TO_SORMAS_SHARES))))) {
			errors.add(new ValidationErrorGroup(Captions.Sample), new ValidationErrorMessage(Validations.sormasToSormasSampleExists));
		}

		return errors;
	}
}

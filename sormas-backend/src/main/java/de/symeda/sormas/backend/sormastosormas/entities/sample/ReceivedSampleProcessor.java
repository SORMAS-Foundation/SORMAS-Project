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
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.data.Sormas2SormasDataValidator;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;

@Stateless
@LocalBean
public class ReceivedSampleProcessor extends ReceivedDataProcessor<SampleDto, SormasToSormasSampleDto, PreviewNotImplementedDto, Sample> {

	@EJB
	private Sormas2SormasDataValidator dataValidator;
	@EJB
	private SampleService sampleService;

	@Override
	public void handleReceivedData(SormasToSormasSampleDto sharedData, Sample existingData) {
	}

	@Override
	public ValidationErrors processReceivedPreview(PreviewNotImplementedDto sharedPreview) {
		throw new RuntimeException("Samples preview not yet implemented");
	}

	@Override
	public ValidationErrors exists(String uuid) {
		ValidationErrors errors = new ValidationErrors();

		if (sampleService.exists(
			(cb, sampleRoot, cq) -> cb.and(
				cb.equal(sampleRoot.get(AbstractDomainObject.UUID), uuid),
				cb.isNull(sampleRoot.get(Sample.SORMAS_TO_SORMAS_ORIGIN_INFO)),
				cb.isEmpty(sampleRoot.get(Sample.SORMAS_TO_SORMAS_SHARES))))) {
			errors.add(new ValidationErrorGroup(Captions.Sample), new ValidationErrorMessage(Validations.sormasToSormasSampleExists));
		}

		return errors;
	}

	@Override
	public ValidationErrors validation(SormasToSormasSampleDto sharedData, Sample existingData) {
		ValidationErrors validationErrors = dataValidator.validateSample(existingData, sharedData.getEntity());
		sharedData.getPathogenTests().forEach(pathogenTest -> dataValidator.validatePathogenTest(validationErrors, pathogenTest));

		return validationErrors;
	}

	@Override
	public ValidationErrors validatePreview(PreviewNotImplementedDto previewNotImplementedDto) {
		throw new RuntimeException("Samples preview not yet implemented");
	}
}

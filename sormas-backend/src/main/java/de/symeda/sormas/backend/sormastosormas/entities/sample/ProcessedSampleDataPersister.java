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

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildPathogenTestValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildSampleValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import java.util.function.Consumer;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;

@Stateless
@LocalBean
public class ProcessedSampleDataPersister implements ProcessedDataPersister<SormasToSormasSampleDto> {

	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal additionalTestFacade;

	@Override
	public void persistSharedData(SormasToSormasSampleDto processedData) throws SormasToSormasValidationException {
		persistSample(processedData, null);
	}

	@Override
	public void persistReturnedData(SormasToSormasSampleDto processedData, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {
		persistSample(processedData, null);
	}

	@Override
	public void persistSyncData(SormasToSormasSampleDto processedData, SormasToSormasOriginInfoDto originInfo, ShareTreeCriteria shareTreeCriteria)
		throws SormasToSormasValidationException {
		persistSample(processedData, null);
	}

	public void persistSample(SormasToSormasSampleDto processedData, Consumer<SampleDto> beforeSaveSample) throws SormasToSormasValidationException {

		SampleDto sample = processedData.getEntity();

		if (beforeSaveSample != null) {
			beforeSaveSample.accept(sample);
		}

		handleValidationError(() -> sampleFacade.saveSample(sample, true, false, false), Captions.Sample, buildSampleValidationGroupName(sample));

		for (PathogenTestDto pathogenTest : processedData.getPathogenTests()) {
			handleValidationError(
				() -> pathogenTestFacade.savePathogenTest(pathogenTest, false, false),
				Captions.PathogenTest,
				buildPathogenTestValidationGroupName(pathogenTest));
		}

		for (AdditionalTestDto additionalTest : processedData.getAdditionalTests()) {
			handleValidationError(
				() -> additionalTestFacade.saveAdditionalTest(additionalTest, false),
				Captions.AdditionalTest,
				buildValidationGroupName(Captions.AdditionalTest, additionalTest));
		}
	}
}

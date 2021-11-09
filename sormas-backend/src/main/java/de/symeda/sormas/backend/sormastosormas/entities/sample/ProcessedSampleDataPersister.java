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

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedSampleDataPersister extends ProcessedDataPersister<SampleDto, SormasToSormasSampleDto, Sample> {

	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private AdditionalTestFacadeEjbLocal additionalTestFacade;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	public void persistSharedData(SormasToSormasSampleDto processedData, Sample existingSample) throws SormasToSormasValidationException {
		SampleDto sample = processedData.getEntity();

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

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(SampleDto entity, String organizationId) {
		return shareInfoService.getBySampleAndOrganization(entity.getUuid(), organizationId);
	}
}

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

package de.symeda.sormas.backend.sormastosormas.datapersister;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildPathogenTestValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildSampleValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedDataPersisterHelper {

	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal additionalTestFacade;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;

	public void persistSharedSamples(List<SormasToSormasSampleDto> samples, SormasToSormasOriginInfoDto sormasToSormasOriginInfo)
		throws SormasToSormasValidationException {
		for (SormasToSormasSampleDto sormasToSormasSample : samples) {
			SampleDto sample = sormasToSormasSample.getSample();
			sample.setSormasToSormasOriginInfo(sormasToSormasOriginInfo);

			handleValidationError(() -> sampleFacade.saveSample(sample), Captions.Sample, buildSampleValidationGroupName(sample));

			for (PathogenTestDto pathogenTest : sormasToSormasSample.getPathogenTests()) {
				handleValidationError(
					() -> pathogenTestFacade.savePathogenTest(pathogenTest),
					Captions.PathogenTest,
					buildPathogenTestValidationGroupName(pathogenTest));
			}

			for (AdditionalTestDto additionalTest : sormasToSormasSample.getAdditionalTests()) {
				handleValidationError(
					() -> additionalTestFacade.saveAdditionalTest(additionalTest),
					Captions.AdditionalTest,
					buildValidationGroupName(Captions.AdditionalTest, additionalTest));
			}
		}
	}

	public void persistReturnedSamples(List<SormasToSormasSampleDto> samples, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {

		SormasToSormasOriginInfoDto savedOriginInfo = null;

		for (SormasToSormasSampleDto sormasToSormasSample : samples) {
			SampleDto sample = sormasToSormasSample.getSample();

			SormasToSormasShareInfo sampleShareInfo = shareInfoService.getBySampleAndOrganization(sample.getUuid(), originInfo.getOrganizationId());
			if (sampleShareInfo == null) {
				if (savedOriginInfo == null) {
					savedOriginInfo = originInfoFacade.saveOriginInfo(originInfo);
				}

				sample.setSormasToSormasOriginInfo(savedOriginInfo);
			} else {
				sampleShareInfo.setOwnershipHandedOver(false);
				shareInfoService.persist(sampleShareInfo);
			}

			handleValidationError(() -> sampleFacade.saveSample(sample), Captions.Sample, buildSampleValidationGroupName(sample));

			for (PathogenTestDto pathogenTest : sormasToSormasSample.getPathogenTests()) {
				handleValidationError(
					() -> pathogenTestFacade.savePathogenTest(pathogenTest),
					Captions.PathogenTest,
					buildPathogenTestValidationGroupName(pathogenTest));
			}

			for (AdditionalTestDto additionalTest : sormasToSormasSample.getAdditionalTests()) {
				handleValidationError(
					() -> additionalTestFacade.saveAdditionalTest(additionalTest),
					Captions.AdditionalTest,
					buildValidationGroupName(Captions.AdditionalTest, additionalTest));
			}
		}
	}
}

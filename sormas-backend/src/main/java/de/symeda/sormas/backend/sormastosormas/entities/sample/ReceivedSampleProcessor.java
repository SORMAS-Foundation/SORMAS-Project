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

import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTest;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.user.UserService;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class ReceivedSampleProcessor
	extends
	ReceivedDataProcessor<Sample, SampleDto, SormasToSormasSampleDto, PreviewNotImplementedDto, Sample, SampleService, SormasToSormasSampleDtoValidator> {


	public ReceivedSampleProcessor() {
	}

	@Inject
	protected ReceivedSampleProcessor(
		SampleService service,
		UserService userService,
		ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade,
		SormasToSormasSampleDtoValidator validator) {
		super(service, userService, configFacade, validator);
	}

	@Override
	public void handleReceivedData(SormasToSormasSampleDto sharedData, Sample existingData, SormasToSormasOriginInfoDto originInfo) {
		Map<String, PathogenTestDto> existingPathogenTests;
		if (existingData != null) {
			existingPathogenTests = existingData.getPathogenTests()
				.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(PathogenTest::getUuid, PathogenTestFacadeEjb::toDto));
		} else {
			existingPathogenTests = Collections.emptyMap();
		}
		updateReportingUser(sharedData.getEntity(), existingData);
		sharedData.getPathogenTests()
			.forEach(pathogenTest -> handleIgnoredProperties(pathogenTest, existingPathogenTests.get(pathogenTest.getUuid())));
	}

	@Override
	public ValidationErrors processReceivedPreview(PreviewNotImplementedDto sharedPreview) {
		throw new RuntimeException("Samples preview not yet implemented");
	}

	@Override
	public ValidationErrors existsNotShared(String uuid) {
		return existsNotShared(
			uuid,
			Sample.SORMAS_TO_SORMAS_ORIGIN_INFO,
			Sample.SORMAS_TO_SORMAS_SHARES,
			Captions.Sample,
			Validations.sormasToSormasSampleExists);
	}

}

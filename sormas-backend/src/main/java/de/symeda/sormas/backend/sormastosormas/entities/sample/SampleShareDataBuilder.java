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

import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.PreviewNotImplementedDto;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class SampleShareDataBuilder
	extends ShareDataBuilder<SampleDto, Sample, SormasToSormasSampleDto, PreviewNotImplementedDto, SormasToSormasSampleDtoValidator> {

	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;
	@EJB
	private PathogenTestFacadeEjb.PathogenTestFacadeEjbLocal pathogenTestFacade;
	@EJB
	private AdditionalTestFacadeEjb.AdditionalTestFacadeEjbLocal additionalTestFacade;
	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;

	@Inject
	public SampleShareDataBuilder(SormasToSormasSampleDtoValidator validator) {
		super(validator);
	}

	public SampleShareDataBuilder() {
	}

	@Override
	protected SormasToSormasSampleDto doBuildShareData(Sample data, ShareRequestInfo requestInfo) {
		Pseudonymizer pseudonymizer =
			dataBuilderHelper.createPseudonymizer(requestInfo.isPseudonymizedPersonalData(), requestInfo.isPseudonymizedSensitiveData());

		SampleDto sampleDto = sampleFacade.convertToDto(data, pseudonymizer);
		sampleDto.setSormasToSormasOriginInfo(null);

		return new SormasToSormasSampleDto(sampleDto, data.getPathogenTests().stream().map(t -> {
			PathogenTestDto pathogenTestDto = pathogenTestFacade.convertToDto(t, pseudonymizer);
			dataBuilderHelper.clearIgnoredProperties(pathogenTestDto);
			return pathogenTestDto;
		}).collect(Collectors.toList()),
			data.getAdditionalTests().stream().map(t -> additionalTestFacade.convertToDto(t, pseudonymizer)).collect(Collectors.toList()));
	}

	@Override
	public PreviewNotImplementedDto doBuildShareDataPreview(Sample data, ShareRequestInfo requestInfo) {
		throw new RuntimeException("Samples preview not yet implemented");
	}
}

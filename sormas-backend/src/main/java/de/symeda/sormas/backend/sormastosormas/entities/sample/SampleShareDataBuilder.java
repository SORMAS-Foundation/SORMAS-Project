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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.entities.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.PreviewNotImplementedDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.sample.AdditionalTestFacadeEjb;
import de.symeda.sormas.backend.sample.PathogenTestFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sample.SamplePseudonymizer;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.SormasToSormasPseudonymizer;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;

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
	protected SormasToSormasSampleDto doBuildShareData(Sample sample, ShareRequestInfo requestInfo, boolean ownerShipHandedOver) {
		SormasToSormasPseudonymizer pseudonymizer = dataBuilderHelper.createPseudonymizer(requestInfo);

		SampleDto sampleDto = getDto(sample, pseudonymizer);

		List<PathogenTestDto> pathogenTests = sample.getPathogenTests().stream().map(t -> {
			PathogenTestDto pathogenTestDto = pathogenTestFacade.convertToDto(t, pseudonymizer.getPseudonymizer());
			dataBuilderHelper.clearIgnoredProperties(pathogenTestDto);
			return pathogenTestDto;
		}).collect(Collectors.toList());

		List<AdditionalTestDto> additionalTests = sample.getAdditionalTests()
			.stream()
			.map(t -> additionalTestFacade.convertToDto(t, pseudonymizer.getPseudonymizer()))
			.collect(Collectors.toList());

		List<SormasToSormasExternalMessageDto> externalMessages = Collections.emptyList();
		if (ownerShipHandedOver) {
			externalMessages = sample.getSampleReports()
				.stream()
				.map(s -> dataBuilderHelper.getExternalMessageDto(s.getLabMessage(), requestInfo))
				.collect(Collectors.toList());
		}

		return new SormasToSormasSampleDto(sampleDto, pathogenTests, additionalTests, externalMessages);
	}

	@Override
	protected SampleDto getDto(Sample sample, SormasToSormasPseudonymizer pseudonymizer) {

		SampleDto sampleDto = sampleFacade.convertToDto(
			sample,
			new SamplePseudonymizer<>(pseudonymizer.getPseudonymizer(), pseudonymizer.getPseudonymizer(), pseudonymizer.getPseudonymizer()));
		// reporting user is not set to null here as it would not pass the validation
		// the receiver appears to set it to SORMAS2SORMAS Client anyway
		sampleDto.setSormasToSormasOriginInfo(null);
		// todo no dataBuilderHelper.clearIgnoredProperties(sampleDto); ?
		return sampleDto;
	}

	@Override
	public void doBusinessValidation(SormasToSormasSampleDto sormasToSormasSampleDto) throws ValidationRuntimeException {
		sampleFacade.validate(sormasToSormasSampleDto.getEntity(), true);
		sormasToSormasSampleDto.getPathogenTests().forEach(pathogenTestFacade::validate);
		// todo additional test facade has no validation method. Add them here if they are available
		// todo external messages facade has no validation method. Add them if they are available
	}

	@Override
	public PreviewNotImplementedDto doBuildShareDataPreview(Sample data, ShareRequestInfo requestInfo) {
		throw new RuntimeException("Samples preview not yet implemented");
	}
}

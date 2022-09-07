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

package de.symeda.sormas.backend.externalmessage.labmessage;

import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportFacade;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportReferenceDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.externalmessage.ExternalMessage;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessageService;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Stateless(name = "SampleReportFacade")
@RightsAllowed(UserRight._EXTERNAL_MESSAGE_PROCESS)
public class SampleReportFacadeEjb implements SampleReportFacade {

	@EJB
	private TestReportFacadeEjb.TestReportFacadeEjbLocal testReportFacade;
	@EJB
	private SampleReportService sampleReportService;
	@EJB
	private SampleService sampleService;
	@EJB
	private ExternalMessageService externalMessageService;

	@Override
	public SampleReportDto getByUuid(String uuid) {
		return toDto(sampleReportService.getByUuid(uuid));
	}

	@Override
	public SampleReportDto saveSampleReport(@Valid SampleReportDto dto) {

		return saveSampleReport(dto, true);
	}

	public SampleReportDto saveSampleReport(@Valid SampleReportDto dto, boolean checkChangeDate) {

		SampleReport sampleReport = fromDto(dto, checkChangeDate);

		sampleReportService.ensurePersisted(sampleReport);

		return toDto(sampleReport);
	}

	public SampleReport fromDto(@NotNull SampleReportDto source, ExternalMessage labMessage, boolean checkChangeDate) {

		SampleReport target =
			DtoHelper.fillOrBuildEntity(source, sampleReportService.getByUuid(source.getUuid()), SampleReport::new, checkChangeDate);

		target.setLabMessage(labMessage);
		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleReceivedDate(source.getSampleReceivedDate());
		target.setLabSampleId(source.getLabSampleId());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setSampleOverallTestResult(source.getSampleOverallTestResult());
		target.setLabMessage(externalMessageService.getByReferenceDto(source.getLabMessage()));
		if (source.getSample() != null) {
			target.setSample(sampleService.getByReferenceDto(source.getSample()));
		}
		if (source.getTestReports() != null) {
			List<TestReport> testReports = new ArrayList<>();
			for (TestReportDto t : source.getTestReports()) {
				TestReport testReport = testReportFacade.fromDto(t, target, false);
				testReports.add(testReport);
			}
			target.setTestReports(testReports);
		}

		return target;
	}

	public SampleReport fromDto(@NotNull SampleReportDto source, boolean checkChangeDate) {
		ExternalMessage labMessage = externalMessageService.getByReferenceDto(source.getLabMessage());

		return fromDto(source, labMessage, checkChangeDate);
	}

	public SampleReportDto toDto(SampleReport source) {

		if (source == null) {
			return null;
		}

		SampleReportDto target = new SampleReportDto();
		DtoHelper.fillDto(target, source);

		target.setSampleDateTime(source.getSampleDateTime());
		target.setSampleReceivedDate(source.getSampleReceivedDate());
		target.setLabSampleId(source.getLabSampleId());
		target.setSampleMaterial(source.getSampleMaterial());
		target.setSampleMaterialText(source.getSampleMaterialText());
		target.setSpecimenCondition(source.getSpecimenCondition());
		target.setSampleOverallTestResult(source.getSampleOverallTestResult());
		target.setLabMessage(ExternalMessageFacadeEjb.toReferenceDto(source.getLabMessage()));

		if (source.getTestReports() != null) {
			target.setTestReports(source.getTestReports().stream().map(TestReportFacadeEjb::toDto).collect(toList()));
		}
		if (source.getSample() != null) {
			target.setSample(source.getSample().toReference());
		}

		return target;

	}

	public static SampleReportReferenceDto toReferenceDto(SampleReport entity) {

		if (entity == null) {
			return null;
		}

		return new SampleReportReferenceDto(entity.getUuid());
	}

	@LocalBean
	@Stateless
	public static class SampleReportFacadeEjbLocal extends SampleReportFacadeEjb {

	}

}

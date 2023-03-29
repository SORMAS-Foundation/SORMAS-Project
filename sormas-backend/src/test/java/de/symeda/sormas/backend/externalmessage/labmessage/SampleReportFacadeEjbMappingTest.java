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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.symeda.sormas.api.externalmessage.ExternalMessageReferenceDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.backend.externalmessage.ExternalMessage;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessageService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.sample.SampleService;

@ExtendWith(MockitoExtension.class)
public class SampleReportFacadeEjbMappingTest {

	@InjectMocks
	private SampleReportFacadeEjb sut;

	@Mock
	private SampleReportService sampleReportService;

	@Mock
	private ExternalMessageService externalMessageService;

	@Mock
	private SampleService sampleService;

	@Mock
	private TestReportFacadeEjb.TestReportFacadeEjbLocal testReportFacade;

	@Test
	public void testFromDto() {

		ExternalMessage labMessage = new ExternalMessage();
		labMessage.setUuid("labmessage-uuid");
		ExternalMessageReferenceDto labMessageReference = ExternalMessageFacadeEjb.toReferenceDto(labMessage);
		when(externalMessageService.getByReferenceDto(labMessageReference)).thenReturn(labMessage);

		when(sampleReportService.getByUuid("sampleReport-uuid")).thenReturn(null);

		TestReport testReport = new TestReport();
		testReport.setUuid("testreport-uuid");
		TestReportDto testReportDto = TestReportFacadeEjb.toDto(testReport);
		when(testReportFacade.fillOrBuildEntity(eq(testReportDto), any(SampleReport.class), eq(false))).thenReturn(testReport);

		Sample sample = new Sample();
		sample.setUuid("sample-uuid");
		SampleReferenceDto sampleReference = SampleFacadeEjb.toReferenceDto(sample);
		when(sampleService.getByReferenceDto(sampleReference)).thenReturn(sample);

		SampleReportDto source = new SampleReportDto();

		source.setUuid("sampleReport-uuid");
		source.setSampleDateTime(new Date(0L));
		source.setSampleReceivedDate(new Date(1L));
		source.setLabSampleId("lab-sample-id");
		source.setSampleMaterial(SampleMaterial.BLOOD);
		source.setSampleMaterialText("sample-material-text");
		source.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		source.setSampleOverallTestResult(PathogenTestResultType.INDETERMINATE);
		source.addTestReport(testReportDto);
		source.setSample(sampleReference);
		source.setLabMessage(labMessageReference);

		SampleReport result = sut.fromDto(source, false);

		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getSampleDateTime(), result.getSampleDateTime());
		assertEquals(source.getSampleReceivedDate(), result.getSampleReceivedDate());
		assertEquals(source.getLabSampleId(), result.getLabSampleId());
		assertEquals(source.getSampleMaterial(), result.getSampleMaterial());
		assertEquals(source.getSampleMaterialText(), result.getSampleMaterialText());
		assertEquals(source.getSpecimenCondition(), result.getSpecimenCondition());
		assertEquals(source.getSampleOverallTestResult(), result.getSampleOverallTestResult());
		assertEquals(source.getTestReports().size(), result.getTestReports().size());
		assertEquals(source.getTestReports().get(0).getUuid(), result.getTestReports().get(0).getUuid());
		assertEquals(labMessage, result.getLabMessage());
		assertEquals(sample, result.getSample());

	}

	@Test
	public void testToDto() {

		TestReport testReport = new TestReport();
		testReport.setUuid("testreport-uuid");
		TestReportDto testReportDto = TestReportFacadeEjb.toDto(testReport);

		ExternalMessage labMessage = new ExternalMessage();
		labMessage.setUuid("labmessage-uuid");
		ExternalMessageReferenceDto labMessageReference = ExternalMessageFacadeEjb.toReferenceDto(labMessage);

		Sample sample = new Sample();
		sample.setUuid("sample-uuid");
		SampleReferenceDto sampleReference = SampleFacadeEjb.toReferenceDto(sample);

		SampleReport source = new SampleReport();

		source.setUuid("sampleReport-uuid");
		source.setSampleDateTime(new Date(0L));
		source.setSampleReceivedDate(new Date(1L));
		source.setLabSampleId("lab-sample-id");
		source.setSampleMaterial(SampleMaterial.BLOOD);
		source.setSampleMaterialText("sample-material-text");
		source.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		source.setSampleOverallTestResult(PathogenTestResultType.INDETERMINATE);
		source.setTestReports(Collections.singletonList(testReport));
		source.setLabMessage(labMessage);
		source.setSample(sample);

		SampleReportDto result = sut.toDto(source);

		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getSampleDateTime(), result.getSampleDateTime());
		assertEquals(source.getSampleReceivedDate(), result.getSampleReceivedDate());
		assertEquals(source.getLabSampleId(), result.getLabSampleId());
		assertEquals(source.getSampleMaterial(), result.getSampleMaterial());
		assertEquals(source.getSampleMaterialText(), result.getSampleMaterialText());
		assertEquals(source.getSpecimenCondition(), result.getSpecimenCondition());
		assertEquals(source.getSampleOverallTestResult(), result.getSampleOverallTestResult());
		assertEquals(source.getTestReports().size(), result.getTestReports().size());
		assertEquals(source.getTestReports().get(0).getUuid(), result.getTestReports().get(0).getUuid());
		assertEquals(labMessageReference, result.getLabMessage());
		assertEquals(sampleReference, result.getSample());

	}
}

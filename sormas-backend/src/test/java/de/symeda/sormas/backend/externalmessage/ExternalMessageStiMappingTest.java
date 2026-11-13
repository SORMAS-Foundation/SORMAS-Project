package de.symeda.sormas.backend.externalmessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.symeda.sormas.api.epidata.ProbableRouteOfTransmission;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectionSite;
import de.symeda.sormas.api.utils.YesNoUnknownOther;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportService;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.therapy.TreatmentService;

class ExternalMessageStiMappingTest extends AbstractUnitTest {

	@InjectMocks
	private ExternalMessageFacadeEjb victim;

	@Mock
	private CountryService countryService;

	@Mock
	private FacilityService facilityService;

	@Mock
	private SurveillanceReportService surveillanceReportService;

	@Mock
	private TreatmentService treatmentService;

	@Mock
	private SymptomsFacadeEjb.SymptomsFacadeEjbLocal symptomsFacadeEjb;

	@ParameterizedTest
	@EnumSource(YesNoUnknownOther.class)
	void preservesStiFieldsAndNestedSymptomsInBothDirections(YesNoUnknownOther answer) {
		// PREPARE
		ExternalMessageDto source = ExternalMessageDto.build();
		source.setProbableRouteOfTransmission(ProbableRouteOfTransmission.OTHER);
		source.setProbableRouteOfTransmissionOther("Transmission details");
		source.setSexWorker(answer);
		source.setSexWorkerOther("Sex worker details");
		source.setContactWithSexWorker(answer);
		source.setContactWithSexWorkerOther("Contact details");
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setSyphilisInfectionSite(SyphilisInfectionSite.OTHER);
		symptoms.setSyphilisInfectionSiteOther("Infection site details");
		source.setCaseSymptoms(symptoms);
		when(symptomsFacadeEjb.fillOrBuildEntity(any(SymptomsDto.class), nullable(Symptoms.class), anyBoolean()))
			.thenAnswer(invocation -> new SymptomsFacadeEjb().fillOrBuildEntity(invocation.getArgument(0), invocation.getArgument(1), false));

		// EXECUTE
		ExternalMessage entity = victim.fillOrBuildEntity(source, null, false);
		ExternalMessageDto result = victim.toDto(entity);

		// CHECK
		assertEquals(answer, entity.getSexWorker());
		assertEquals(answer, entity.getContactWithSexWorker());
		assertEquals(source.getProbableRouteOfTransmissionOther(), entity.getProbableRouteOfTransmissionOther());
		assertEquals(source.getSexWorkerOther(), entity.getSexWorkerOther());
		assertEquals(source.getContactWithSexWorkerOther(), entity.getContactWithSexWorkerOther());
		assertEquals(source.getProbableRouteOfTransmission(), result.getProbableRouteOfTransmission());
		assertEquals(source.getProbableRouteOfTransmissionOther(), result.getProbableRouteOfTransmissionOther());
		assertEquals(answer, result.getSexWorker());
		assertEquals(source.getSexWorkerOther(), result.getSexWorkerOther());
		assertEquals(answer, result.getContactWithSexWorker());
		assertEquals(source.getContactWithSexWorkerOther(), result.getContactWithSexWorkerOther());
		assertEquals(symptoms.getSyphilisInfectionSiteOther(), result.getCaseSymptoms().getSyphilisInfectionSiteOther());
	}
}

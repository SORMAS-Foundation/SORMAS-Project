package de.symeda.sormas.backend.epidata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.ProbableRouteOfTransmission;
import de.symeda.sormas.api.utils.YesNoUnknownOther;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.infrastructure.country.CountryService;

class EpiDataStiMappingTest extends AbstractUnitTest {

	@InjectMocks
	private EpiDataFacadeEjb victim;

	@Mock
	private CountryService countryService;

	@ParameterizedTest
	@EnumSource(YesNoUnknownOther.class)
	void preservesStiAnswersAndOtherDetailsInBothDirections(YesNoUnknownOther answer) {
		// PREPARE
		EpiDataDto source = EpiDataDto.build();
		source.setProbableRouteOfTransmission(ProbableRouteOfTransmission.OTHER);
		source.setProbableRouteOfTransmissionOther("Transmission details");
		source.setSexWorker(answer);
		source.setSexWorkerOther("Sex worker details");
		source.setContactWithSexWorker(answer);
		source.setContactWithSexWorkerOther("Contact details");

		// EXECUTE
		EpiData entity = victim.fillOrBuildEntity(source, null, false);
		EpiDataDto result = EpiDataFacadeEjb.toDto(entity);

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
	}
}

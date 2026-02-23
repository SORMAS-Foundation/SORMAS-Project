package de.symeda.sormas.patch;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.DataReplacementStrategy;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.AbstractBeanTest;

class CaseDataPatcherImplTest extends AbstractBeanTest {

	@BeforeEach
	void setUp() {
		// TODO: create reference data: country.
		// TODO: try to change language to use the languages names / with without accent and stuff: Pérou
	}

	// TODO: test different replacement strategy: with/without failure
	// TODO: test forbidden fields

	@Test
	void patch_no_errors_alwas_replacement() {
		// PREPARE
		CaseDataDto originalCase = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		String newLastname = "toto";
		String newSequelaeDetails = "Some very interesting sequelaeDetails";
		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(originalCase.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(
				Map.of(
					"Person.lastName",
					newLastname,

					"Person.sex",
					Sex.FEMALE.getName(),

					"Person.personContactDetails.details",
					"name@email.de",

					"Person.personContactDetails.phoneNumberType",
					"123654687",

					"CaseData.sequelaeDetails",
					newSequelaeDetails));

		// EXECUTE
		DataPatchResponse result = getCaseDataPatcher().patch(request);

		// CHECK
		logger.info("result: [{}]", result);

		CaseDataDto actualCase = getCaseFacade().getByUuid(originalCase.getUuid());
		PersonDto actualPerson = getPersonFacade().getByUuid(originalCase.getPerson().getUuid());

		Assertions.assertAll(
			() -> Assertions.assertTrue(result.getFailures().isEmpty(), () -> "Failure found, but should be empty"),
			// PERSON
			() -> Assertions.assertEquals(newLastname, actualPerson.getLastName()),
			() -> Assertions.assertEquals(Sex.FEMALE, actualPerson.getSex()),
			// CASE
			() -> Assertions.assertEquals(newSequelaeDetails, actualCase.getSequelaeDetails()));
	}

}

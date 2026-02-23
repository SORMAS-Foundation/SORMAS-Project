package de.symeda.sormas.patch;

import java.util.Map;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.patch.CaseDataPatchRequest;
import de.symeda.sormas.api.patch.DataPatchResponse;
import de.symeda.sormas.api.patch.DataReplacementStrategy;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.AbstractBeanTest;

class CaseDataPatcherImplTest extends AbstractBeanTest {

	@Test
	void patch_no_errors() {
		CaseDataDto caze = creator.createUnclassifiedCase(Disease.PERTUSSIS);

		CaseDataPatchRequest request = new CaseDataPatchRequest().setCaseUuid(caze.getUuid())
			.setReplacementStrategy(DataReplacementStrategy.ALWAYS)
			.setPatchDictionary(
				Map.of(
					"Person.lastName",
					"toto",
					"Person.sex",
					Sex.FEMALE.getName(),
					"CaseData.sequelaeDetails",
					"Some very interesting sequelaeDetails"

				));
		DataPatchResponse result = getCaseDataPatcher().patch(request);

		logger.info("result: [{}]", result);
	}
}

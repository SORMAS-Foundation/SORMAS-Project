package de.symeda.sormas.backend.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class CaseServiceAttributeValuesTest extends AbstractBeanTest {

	@Test
	public void findsEveryUndeletedCaseForTheGivenAttributeValues() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = useNationalAdminLogin().toReference();
		creator.createCase(user, rdcf, c -> c.setExternalID("A"));
		creator.createCase(user, rdcf, c -> c.setExternalID("A"));
		creator.createCase(user, rdcf, c -> c.setExternalID("B"));
		creator.createCase(user, rdcf, c -> c.setExternalID("C"));
		CaseDataDto deleted = creator.createCase(user, rdcf, c -> c.setExternalID("B"));
		getCaseFacade().delete(deleted.getUuid(), new DeletionDetails(DeletionReason.OTHER_REASON, "test"));

		Map<String, Long> counts = getBean(CaseService.class).getUndeletedByAttributeValues(Case.EXTERNAL_ID, List.of("A", "B", "MISSING"))
			.stream()
			.collect(Collectors.groupingBy(Case::getExternalID, Collectors.counting()));

		assertEquals(Map.of("A", 2L, "B", 1L), counts);
	}

	@Test
	public void returnsAnEmptyListForNoIds() {
		assertTrue(getBean(CaseService.class).getUndeletedByAttributeValues(Case.EPID_NUMBER, List.of()).isEmpty());
	}
}

package de.symeda.sormas.backend.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class CaseServiceExternalIdsTest extends AbstractBeanTest {

	@Test
	public void findsEveryCaseForTheGivenExternalIds() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = useNationalAdminLogin().toReference();
		creator.createCase(user, rdcf, c -> c.setExternalID("A"));
		creator.createCase(user, rdcf, c -> c.setExternalID("A"));
		creator.createCase(user, rdcf, c -> c.setExternalID("B"));
		creator.createCase(user, rdcf, c -> c.setExternalID("C"));

		Map<String, Long> counts = getBean(CaseService.class).getByExternalIds(List.of("A", "B", "MISSING"))
			.stream()
			.collect(Collectors.groupingBy(Case::getExternalID, Collectors.counting()));

		assertEquals(Map.of("A", 2L, "B", 1L), counts);
	}

	@Test
	public void returnsAnEmptyListForNoIds() {
		assertTrue(getBean(CaseService.class).getByExternalIds(List.of()).isEmpty());
	}
}

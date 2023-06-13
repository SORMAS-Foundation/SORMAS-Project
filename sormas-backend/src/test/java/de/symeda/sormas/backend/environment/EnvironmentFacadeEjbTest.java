package de.symeda.sormas.backend.environment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

class EnvironmentFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetByUuid() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		EnvironmentDto environment = creator.createEnvironment("Test Environment", EnvironmentMedia.WATER, user.toReference(), rdcf);

		EnvironmentDto saved = getEnvironmentFacade().getByUuid(environment.getUuid());

		assertEquals(environment.getEnvironmentName(), saved.getEnvironmentName());
		assertEquals(environment.getEnvironmentMedia(), saved.getEnvironmentMedia());

		environment.setInvestigationStatus(InvestigationStatus.PENDING);

		getEnvironmentFacade().save(environment);

		saved = getEnvironmentFacade().getByUuid(environment.getUuid());
		assertEquals(environment.getInvestigationStatus(), saved.getInvestigationStatus());
	}

	@Test
	public void testGetIndexList() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		creator.createEnvironment("Test Environment", EnvironmentMedia.WATER, user.toReference(), rdcf);

		creator.createEnvironment("Test Environment 2", EnvironmentMedia.AIR, user.toReference(), rdcf);

		EnvironmentCriteria environmentCriteria = new EnvironmentCriteria();
		List<EnvironmentIndexDto> results = getEnvironmentFacade().getIndexList(environmentCriteria, 0, 100, null);
		assertEquals(2, results.size());

		environmentCriteria.setEnvironmentMedia(EnvironmentMedia.WATER);
		results = getEnvironmentFacade().getIndexList(environmentCriteria, 0, 100, null);
		assertEquals(1, results.size());
		assertEquals("Test Environment", results.get(0).getEnvironmentName());
	}
}

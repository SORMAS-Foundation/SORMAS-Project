package de.symeda.sormas.backend.environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentIndexDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.location.LocationDto;
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
	public void testCount() {
		UserDto user = creator.createNationalUser();
		TestDataCreator.RDCF rdcf1 = creator.createRDCF();
		creator.createEnvironment("Test Environment", EnvironmentMedia.WATER, user.toReference(), rdcf1);
		TestDataCreator.RDCF rdcf2 = creator.createRDCF();
		creator.createEnvironment("Test Environment 2", EnvironmentMedia.AIR, user.toReference(), rdcf2);

		long all = getEnvironmentFacade().count(new EnvironmentCriteria());
		assertEquals(2, all);

		long byMediaType = getEnvironmentFacade().count(new EnvironmentCriteria().environmentMedia(EnvironmentMedia.AIR));
		assertEquals(1, byMediaType);

		long byRdcf =
			getEnvironmentFacade().count(new EnvironmentCriteria().region(rdcf1.region).district(rdcf1.district).community(rdcf1.community));
		assertEquals(1, byRdcf);

		// surv off should see only the environment of his district
		UserDto survOff = creator.createSurveillanceOfficer(rdcf2);
		loginWith(survOff);

		long byUserRdcf = getEnvironmentFacade().count(new EnvironmentCriteria());
		assertEquals(1, byUserRdcf);
	}

	@Test
	public void testGetIndexList() {

		UserDto user = creator.createNationalUser();
		TestDataCreator.RDCF rdcf1 = creator.createRDCF();
		EnvironmentDto environment1 = creator.createEnvironment("Test Environment", EnvironmentMedia.WATER, user.toReference(), rdcf1);
		TestDataCreator.RDCF rdcf2 = creator.createRDCF();
		EnvironmentDto environment2 = creator.createEnvironment("Test Environment 2", EnvironmentMedia.AIR, user.toReference(), rdcf2);

		List<EnvironmentIndexDto> allResults = getEnvironmentFacade().getIndexList(new EnvironmentCriteria(), 0, 100, null);
		assertEquals(2, allResults.size());

		// check if all fields mapped correctly
		EnvironmentIndexDto environment1Idex = allResults.stream().filter(e -> e.getUuid().equals(environment1.getUuid())).findFirst().get();
		assertEquals(environment1.getUuid(), environment1Idex.getUuid());
		assertEquals(environment1.getExternalId(), environment1Idex.getExternalId());
		assertEquals(environment1.getEnvironmentName(), environment1Idex.getEnvironmentName());
		assertEquals(environment1.getEnvironmentMedia(), environment1Idex.getEnvironmentMedia());
		assertEquals(environment1.getReportDate(), environment1Idex.getReportDate());
		assertEquals(environment1.getInvestigationStatus(), environment1Idex.getInvestigationStatus());
		// check if location is mapped correctly
		LocationDto location = environment1.getLocation();
		assertEquals(location.getRegion().getCaption(), environment1Idex.getRegion());
		assertEquals(location.getDistrict().getCaption(), environment1Idex.getDistrict());
		assertEquals(location.getCommunity().getCaption(), environment1Idex.getCommunity());
		assertEquals(location.getLatitude(), environment1Idex.getLatitude());
		assertEquals(location.getLongitude(), environment1Idex.getLongitude());
		assertEquals(location.getPostalCode(), environment1Idex.getPostalCode());
		assertEquals(location.getCity(), environment1Idex.getCity());

		List<EnvironmentIndexDto> byMediaType =
			getEnvironmentFacade().getIndexList(new EnvironmentCriteria().environmentMedia(EnvironmentMedia.AIR), 0, 100, null);
		assertEquals(1, byMediaType.size());
		assertEquals(environment2.getUuid(), byMediaType.get(0).getUuid());

		List<EnvironmentIndexDto> byRdcf = getEnvironmentFacade()
			.getIndexList(new EnvironmentCriteria().region(rdcf1.region).district(rdcf1.district).community(rdcf1.community), 0, 100, null);
		assertEquals(1, byRdcf.size());
		assertEquals(environment1.getUuid(), byRdcf.get(0).getUuid());

		// surv off should see only the environment of his district
		UserDto survOff = creator.createSurveillanceOfficer(rdcf2);
		loginWith(survOff);

		List<EnvironmentIndexDto> byUserRdcf = getEnvironmentFacade().getIndexList(new EnvironmentCriteria(), 0, 100, null);
		assertEquals(1, byUserRdcf.size());
		assertEquals(environment2.getUuid(), byUserRdcf.get(0).getUuid());
	}

	@Test
	public void testGetIndexListByARestrictedAccessToAssignedEntities() {

		UserDto user = creator.createNationalUser();
		TestDataCreator.RDCF rdcf1 = creator.createRDCF();
		EnvironmentDto environment1 = creator.createEnvironment("Test Environment", EnvironmentMedia.WATER, user.toReference(), rdcf1);
		TestDataCreator.RDCF rdcf2 = creator.createRDCF();
		EnvironmentDto environment2 = creator.createEnvironment("Test Environment 2", EnvironmentMedia.AIR, user.toReference(), rdcf2);

		List<EnvironmentIndexDto> allResults = getEnvironmentFacade().getIndexList(new EnvironmentCriteria(), 0, 100, null);
		assertEquals(2, allResults.size());

		UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities =
			creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf1);
		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertTrue(getCurrentUserService().isRestrictedToAssignedEntities());
		assertEquals(0, getEnvironmentFacade().getIndexList(new EnvironmentCriteria(), 0, 100, null).size());

		loginWith(user);
		environment1.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEnvironmentFacade().save(environment1);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		assertEquals(1, getEnvironmentFacade().getIndexList(new EnvironmentCriteria(), 0, 100, null).size());
	}

}

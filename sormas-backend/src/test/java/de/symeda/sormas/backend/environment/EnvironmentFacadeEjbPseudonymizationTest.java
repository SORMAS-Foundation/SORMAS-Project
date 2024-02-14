package de.symeda.sormas.backend.environment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class EnvironmentFacadeEjbPseudonymizationTest extends AbstractBeanTest {

	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto surveillanceOfficer1;
	private UserDto nationalUser;
	private UserDto surveillanceOfficerWithRestrictedAccessToAssignedEntities;

	@Override
	public void init() {

		super.init();
		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		surveillanceOfficer1 = creator.createSurveillanceOfficer(rdcf1);
		nationalUser = creator.createNationalUser();
		surveillanceOfficerWithRestrictedAccessToAssignedEntities = creator.createSurveillanceOfficerWithRestrictedAccessToAssignedEntities(rdcf1);
	}

	@Test
	public void testPseudonymizedGetByUuidWithLimitedUser() {

		//environment with same jurisdiction as limitedUser's
		EnvironmentDto environment1 = creator.createEnvironment(
			"Test Environment",
			EnvironmentMedia.WATER,
			surveillanceOfficer1.toReference(),
			rdcf1,
			(e) -> e.getLocation().setStreet("Main street"));

		//environment with different jurisdiction from limited User's
		EnvironmentDto environment2 = creator.createEnvironment(
			"Test Environment",
			EnvironmentMedia.WATER,
			nationalUser.toReference(),
			rdcf2,
			(e) -> e.getLocation().setStreet("Main street second"));

		//environment created by limited user in the same jurisdiction
		EnvironmentDto environment3 = creator.createEnvironment(
			"Test Environment",
			EnvironmentMedia.WATER,
			surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference(),
			rdcf1,
			(e) -> e.getLocation().setStreet("Main street third"));

		//environment created by limited user outside limited user's jurisdiction
		EnvironmentDto environment4 = creator.createEnvironment(
			"Test Environment",
			EnvironmentMedia.WATER,
			surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference(),
			rdcf2,
			(e) -> e.getLocation().setStreet("Main street fourth"));

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		EnvironmentDto testEnvironment1 = getEnvironmentFacade().getByUuid(environment1.getUuid());
		assertThat(testEnvironment1.isPseudonymized(), is(true));
		assertThat(testEnvironment1.getLocation().getStreet(), is(emptyString()));

		EnvironmentDto testEnvironment2 = getEnvironmentFacade().getByUuid(environment2.getUuid());
		assertThat(testEnvironment2.isPseudonymized(), is(true));
		assertThat(testEnvironment2.getLocation().getStreet(), is(emptyString()));

		loginWith(nationalUser);
		environment1.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEnvironmentFacade().save(environment1);
		environment2.setResponsibleUser(surveillanceOfficerWithRestrictedAccessToAssignedEntities.toReference());
		getEnvironmentFacade().save(environment2);

		loginWith(surveillanceOfficerWithRestrictedAccessToAssignedEntities);
		EnvironmentDto testForEnvironment1 = getEnvironmentFacade().getByUuid(environment1.getUuid());
		assertThat(testForEnvironment1.isPseudonymized(), is(false));
		assertThat(testForEnvironment1.getLocation().getStreet(), is("Main street"));

		EnvironmentDto testForEnvironment2 = getEnvironmentFacade().getByUuid(environment2.getUuid());
		assertThat(testForEnvironment2.isPseudonymized(), is(false));
		assertThat(testForEnvironment2.getLocation().getStreet(), is("Main street second"));

		EnvironmentDto testForEnvironment3 = getEnvironmentFacade().getByUuid(environment3.getUuid());
		assertThat(testForEnvironment3.isPseudonymized(), is(false));
		assertThat(testForEnvironment3.getLocation().getStreet(), is("Main street third"));

		EnvironmentDto testForEnvironment4 = getEnvironmentFacade().getByUuid(environment4.getUuid());
		assertThat(testForEnvironment4.isPseudonymized(), is(false));
		assertThat(testForEnvironment4.getLocation().getStreet(), is("Main street fourth"));
	}

}

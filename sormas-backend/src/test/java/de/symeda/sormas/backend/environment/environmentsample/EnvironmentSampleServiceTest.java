/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.environment.environmentsample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class EnvironmentSampleServiceTest extends AbstractBeanTest {

	@Test
	public void testJurisdiction() {
		TestDataCreator.RDCF rdcf1 = creator.createRDCF();
		FacilityDto lab1 = creator.createFacility("Lab", rdcf1.region, rdcf1.district, rdcf1.community, FacilityType.LABORATORY);
		UserDto ownerUser = creator.createUser(rdcf1, "Env", "Surv", creator.getUserRoleReference(DefaultUserRole.ENVIRONMENTAL_SURVEILLANCE_USER));
		EnvironmentDto environmentWithEmptyLocation = creator.createEnvironment("Test env", EnvironmentMedia.WATER, ownerUser.toReference(), rdcf1);
		EnvironmentDto environmentWithFilledLocation = creator.createEnvironment("Test env", EnvironmentMedia.WATER, ownerUser.toReference(), rdcf1);

		UserDto environmentUser2 =
			creator.createUser(rdcf1, "Env", "Surv2", creator.getUserRoleReference(DefaultUserRole.ENVIRONMENTAL_SURVEILLANCE_USER));

		TestDataCreator.RDCF rdcf2 = creator.createRDCF();
		UserDto envUserOtherRdcf =
			creator.createUser(rdcf2, "Env", "Surv3", creator.getUserRoleReference(DefaultUserRole.ENVIRONMENTAL_SURVEILLANCE_USER));

		UserDto natUser = creator.createUser(rdcf1, "Nat", "User", creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		UserDto adminUser = creator.createUser(rdcf1, "Admin", "User", creator.getUserRoleReference(DefaultUserRole.ADMIN));

		UserDto survSupUser = creator.createUser(rdcf1, "Surv", "Sup", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		UserDto survSupUserOtherRdcf =
			creator.createUser(rdcf2, "Surv", "Sup2", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		UserDto commOffUser = creator.createUser(rdcf1, "Comm", "Off", creator.getUserRoleReference(DefaultUserRole.COMMUNITY_OFFICER));
		UserDto commOffUserOtherRdcf = creator.createUser(rdcf2, "Comm", "Off2", creator.getUserRoleReference(DefaultUserRole.COMMUNITY_OFFICER));
		UserDto hospInfUser = creator.createUser(rdcf2, "Hosp", "Inf", creator.getUserRoleReference(DefaultUserRole.HOSPITAL_INFORMANT));
		UserDto poeInfUser = creator.createUser(rdcf2, "Poe", "Inf", creator.getUserRoleReference(DefaultUserRole.POE_INFORMANT));

		UserDto labUser = creator.createUser(rdcf1, creator.getUserRoleReference(DefaultUserRole.LAB_USER), u -> {
			u.setFirstName("Lab");
			u.setLastName("User");
			u.setUserName("LabUser");

			u.setLaboratory(lab1.toReference());
		});
		FacilityDto lab2 = creator.createFacility("Lab2", rdcf1.region, rdcf1.district, rdcf1.community, FacilityType.LABORATORY);
		UserDto labUserOtherLab = creator.createUser(rdcf1, creator.getUserRoleReference(DefaultUserRole.LAB_USER), u -> {
			u.setFirstName("Lab");
			u.setLastName("User");
			u.setUserName("LabUser2");

			u.setLaboratory(lab2.toReference());
		});

		// owners should be able to see all samples
		loginWith(ownerUser);

		EnvironmentSampleDto sampleDtoEmptyLocationAll =
			creator.createEnvironmentSample(environmentWithEmptyLocation.toReference(), ownerUser.toReference(), lab1.toReference(), null);
		EnvironmentSample sampleEmptyLocationALL = getEnvironmentSampleService().getByUuid(sampleDtoEmptyLocationAll.getUuid());
		EnvironmentSampleDto sampleDtoRdcf1EnvEmptyLocation =
			creator.createEnvironmentSample(environmentWithEmptyLocation.toReference(), ownerUser.toReference(), lab1.toReference(), s -> {
				s.getLocation().setRegion(rdcf1.region);
				s.getLocation().setDistrict(rdcf1.district);
				s.getLocation().setCommunity(rdcf1.community);
			});
		EnvironmentSample samploRdcf1EnvEmptyLocation = getEnvironmentSampleService().getByUuid(sampleDtoRdcf1EnvEmptyLocation.getUuid());

		EnvironmentSampleDto sampleDtoEmptyLocationEnvFilledLocation =
			creator.createEnvironmentSample(environmentWithFilledLocation.toReference(), ownerUser.toReference(), lab1.toReference(), null);
		EnvironmentSample sampleEmptyLocationEnvFilledLocation =
			getEnvironmentSampleService().getByUuid(sampleDtoEmptyLocationEnvFilledLocation.getUuid());

		EnvironmentSampleDto sampleDtoFilledLocationEnvFilledLocation =
			creator.createEnvironmentSample(environmentWithFilledLocation.toReference(), ownerUser.toReference(), lab1.toReference(), null);
		EnvironmentSample sampleFilledLocationEnvFilledLocation =
			getEnvironmentSampleService().getByUuid(sampleDtoFilledLocationEnvFilledLocation.getUuid());

		// owner should have access to all samples
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(true));

		// user from same RDCF should have access to all samples
		loginWith(environmentUser2);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(true));

		// user from different RDCF should have no access to the samples
		loginWith(envUserOtherRdcf);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(false));

		// national user should have access to all samples
		loginWith(natUser);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(true));

		// admin user should have no access to all samples
		loginWith(adminUser);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(false));

		// surveillance supervisor from same district should have access to all samples
		loginWith(survSupUser);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(true));

		// surveillance supervisor from different district should have no access to all samples
		loginWith(survSupUserOtherRdcf);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(false));

		// community officer from same district should have access to all samples
		loginWith(commOffUser);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(true));

		// community officer from different district should have no access to all samples
		loginWith(commOffUserOtherRdcf);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(false));

		// hospital informant is not yet supported
		loginWith(hospInfUser);
		Assertions.assertThrowsExactly(
			NotImplementedException.class,
			() -> getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL),
			"Facility is not supported for environment samples");

		// point of entry informant is not yet supported
		loginWith(poeInfUser);
		Assertions.assertThrowsExactly(
			NotImplementedException.class,
			() -> getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL),
			"Point of entry is not supported for environment samples");

		// lab user from same lab should have access to all samples
		loginWith(labUser);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(true));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(true));

		// lab user from different lab should have no access to the samples
		loginWith(labUserOtherLab);
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationALL), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(samploRdcf1EnvEmptyLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleEmptyLocationEnvFilledLocation), is(false));
		assertThat(getEnvironmentSampleService().inJurisdictionOrOwned(sampleFilledLocationEnvFilledLocation), is(false));

	}
}

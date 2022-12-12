package de.symeda.sormas.backend.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

class PortHealthInfoFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetPortHealthInfoByCaseUuid() {
		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		PersonDto personDto = creator.createPerson("John", "Doe");

		PortHealthInfoDto portHealthInfoDto = PortHealthInfoDto.build();
		portHealthInfoDto.setAirlineName("WorldAir");

		CaseDataDto caseDataDto = creator.createCase(user.toReference(), rdcf, c -> {
			c.setPerson(personDto.toReference());
			c.setPointOfEntry(rdcf.pointOfEntry);
			c.setPortHealthInfo(portHealthInfoDto);
		});

		PortHealthInfoDto healthInfoDto = getPortHealthInfoFacade().getByCaseUuid(caseDataDto.getUuid());
		assertNotNull(healthInfoDto);
		assertEquals(portHealthInfoDto.getUuid(), healthInfoDto.getUuid());
		assertEquals(portHealthInfoDto.getAirlineName(), healthInfoDto.getAirlineName());
	}
}

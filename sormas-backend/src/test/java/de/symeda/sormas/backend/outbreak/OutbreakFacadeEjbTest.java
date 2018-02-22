package de.symeda.sormas.backend.outbreak;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class OutbreakFacadeEjbTest extends AbstractBeanTest {
	
	@Test
	public void testOutbreakCreationAndDeletion() {
		
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		OutbreakDto outbreak = creator.createOutbreak(rdcf, Disease.EVD, user.toReference());
		
		// Database should contain one outbreak
		assertEquals(1, getOutbreakFacade().getAllAfter(null).size());
		
		getOutbreakFacade().deleteOutbreak(outbreak);
		
		// Database should contain no outbreak
		assertEquals(0, getOutbreakFacade().getAllAfter(null).size());
	}
}

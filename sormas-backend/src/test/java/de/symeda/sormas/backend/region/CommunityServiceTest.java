package de.symeda.sormas.backend.region;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class CommunityServiceTest extends AbstractBeanTest {

	@Test
	public void testGetAllAfter() throws Exception {
		
		CommunityService communityService = getBean(CommunityService.class);

		Date dateBefore = new Date();

		RDCFEntities rdcf = creator.createRDCFEntities();
		
		Date dateAfter = new Date();
		
		long count = communityService.countAfter(null);
		long countBefore = communityService.countAfter(dateBefore);
		long countAfter = communityService.countAfter(dateAfter);
		
		List<Community> communities = communityService.getAllAfter(null, null);
		List<Community> communitiesBefore = communityService.getAllAfter(dateBefore, null);
		List<Community> communitiesAfter = communityService.getAllAfter(dateAfter, null);
		
		assertEquals(1, count);
		assertEquals(1, communities.size());
		assertEquals(1, countBefore);
		assertEquals(1, communitiesBefore.size());
		assertEquals(0, countAfter);
		assertEquals(0, communitiesAfter.size());
	}

}

package de.symeda.sormas.backend.campaign;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.backend.MockProducer;
import org.junit.Assert;
import org.junit.Test;

import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

import static org.mockito.Mockito.when;

public class CampaignFacadeEjbTest extends AbstractBeanTest {

	public static final int ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

	@Test
	public void testGetLastStartedCampaign() {

		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
		final CampaignDto campaign1 = creator.createCampaign(user);
		campaign1.setStartDate(new Date(System.currentTimeMillis() - 7 * ONE_DAY_IN_MILLIS)); // last week
		getCampaignFacade().saveCampaign(campaign1);
		final CampaignDto campaign2 = creator.createCampaign(user);
		campaign2.setStartDate(new Date(System.currentTimeMillis() - ONE_DAY_IN_MILLIS)); // yesterday
		getCampaignFacade().saveCampaign(campaign2);
		final CampaignDto campaign3 = creator.createCampaign(user);
		campaign3.setStartDate(new Date(System.currentTimeMillis() + ONE_DAY_IN_MILLIS)); // tomorrow
		getCampaignFacade().saveCampaign(campaign3);

		CampaignReferenceDto lastStartedCampaign = getCampaignFacade().getLastStartedCampaign();
		Assert.assertEquals(campaign2.getUuid(), lastStartedCampaign.getUuid());
	}
}

/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.backend.campaign.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class CampaignFormDataFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testSaveCampaignFormData() throws Exception {

		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		final CampaignDto campaign = creator.createCampaign(user);

		final CampaignFormDto campaignForm = creator.createCampaignForm(campaign);

		String formData = "[{\n" + "\"teamNumber\":\"12\",\n" + "\"namesOfTeamMembers\":\"Waldemar Stricker\",\n"
			+ "\"monitorName\":\"Josef Saks\",\n" + "\"agencyName\":\"HZI Institut\",\n" + "\"questionsSection\":\"questionsSection1\",\n"
			+ "\"questionsLabel\":\"LabelQuestionSection1\",\n" + "\"oneMemberResident\":\"yes\",\n" + "\"vaccinatorsTrained\":\"no\",\n"
			+ "\"questionsSection2\":\"teamObservation\",\n" + "\"q8To12Label\":\"teamObservationLabel\",\n" + "\"askingAboutMonthOlds\":\"yes\",\n"
			+ "\"questionsSection3\":\"finalQuestionsSection\",\n" + "\"atLeastOneMemberChw\":\"yes\",\n" + "\"numberOfChw\":\"7\",\n"
			+ "\"anyMemberFemale\":\"yes\",\n" + "\"accompaniedBySocialMobilizer\":\"no\",\n" + "\"comments\":\"other comments\"\n" + "}]";

		CampaignFormDataDto newCampaignFormDataDto = creator.buildCampaignFormDataDto(campaign, campaignForm, rdcf, formData);
		newCampaignFormDataDto = getCampaignFormDataFacade().saveCampaignFormData(newCampaignFormDataDto);

		assertNotNull(newCampaignFormDataDto);
		assertEquals(formData, newCampaignFormDataDto.getFormData());
		assertEquals(rdcf.region.getUuid(), newCampaignFormDataDto.getRegion().getUuid());
		assertEquals(rdcf.district.getUuid(), newCampaignFormDataDto.getDistrict().getUuid());
		assertEquals(rdcf.community.getUuid(), newCampaignFormDataDto.getCommunity().getUuid());

		String newFormData = "[{\"teamNumber\":\"12\",\"namesOfTeamMembers\": \"Ekkehard Rosin\","
			+ "    \"monitorName\": \"Ralf Windisch\", \"agencyName\": \"Sormas Institut\"}]";

		newCampaignFormDataDto.setFormData(newFormData);

		CampaignFormDataDto updatedCampaignFormData = getCampaignFormDataFacade().saveCampaignFormData(newCampaignFormDataDto);

		assertNotNull(updatedCampaignFormData);
		assertEquals(newFormData, updatedCampaignFormData.getFormData());
	}

	@Test
	public void testGetCampaignFormDataByUuid() throws Exception {
		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		final CampaignDto campaign = creator.createCampaign(user);

		final CampaignFormDto campaignForm = creator.createCampaignForm(campaign);

		String formData = "[{\n" + "\"teamNumber\":\"12\",\n" + "\"namesOfTeamMembers\":\"Waldemar Stricker\",\n"
			+ "\"monitorName\":\"Josef Saks\",\n" + "\"agencyName\":\"HZI Institut\",\n" + "\"questionsSection\":\"questionsSection1\",\n"
			+ "\"questionsLabel\":\"LabelQuestionSection1\",\n" + "\"oneMemberResident\":\"yes\",\n" + "\"vaccinatorsTrained\":\"no\",\n"
			+ "\"questionsSection2\":\"teamObservation\",\n" + "\"q8To12Label\":\"teamObservationLabel\",\n" + "\"askingAboutMonthOlds\":\"yes\",\n"
			+ "\"questionsSection3\":\"finalQuestionsSection\",\n" + "\"atLeastOneMemberChw\":\"yes\",\n" + "\"numberOfChw\":\"7\",\n"
			+ "\"anyMemberFemale\":\"yes\",\n" + "\"accompaniedBySocialMobilizer\":\"no\",\n" + "\"comments\":\"other comments\"\n" + "}]";

		CampaignFormDataDto newCampaignFormDataDto = creator.buildCampaignFormDataDto(campaign, campaignForm, rdcf, formData);
		newCampaignFormDataDto = getCampaignFormDataFacade().saveCampaignFormData(newCampaignFormDataDto);

		CampaignFormDataDto retrievedCampaignFormDataDto = getCampaignFormDataFacade().getCampaignFormDataByUuid(newCampaignFormDataDto.getUuid());

		assertNotNull(retrievedCampaignFormDataDto);
		assertEquals(formData, retrievedCampaignFormDataDto.getFormData());
	}

	@Test
	public void testDeleteCampaignFormData() throws Exception {
		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		final UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);

		final CampaignDto campaign = creator.createCampaign(user);

		final CampaignFormDto campaignForm = creator.createCampaignForm(campaign);

		String formData = "[{\n" + "\"teamNumber\":\"12\",\n" + "\"namesOfTeamMembers\":\"Waldemar Stricker\",\n"
			+ "\"monitorName\":\"Josef Saks\",\n" + "\"agencyName\":\"HZI Institut\",\n" + "\"questionsSection\":\"questionsSection1\",\n"
			+ "\"questionsLabel\":\"LabelQuestionSection1\",\n" + "\"oneMemberResident\":\"yes\",\n" + "\"vaccinatorsTrained\":\"no\",\n"
			+ "\"questionsSection2\":\"teamObservation\",\n" + "\"q8To12Label\":\"teamObservationLabel\",\n" + "\"askingAboutMonthOlds\":\"yes\",\n"
			+ "\"questionsSection3\":\"finalQuestionsSection\",\n" + "\"atLeastOneMemberChw\":\"yes\",\n" + "\"numberOfChw\":\"7\",\n"
			+ "\"anyMemberFemale\":\"yes\",\n" + "\"accompaniedBySocialMobilizer\":\"no\",\n" + "\"comments\":\"other comments\"\n" + "}]";

		CampaignFormDataDto newCampaignFormDataDto = creator.buildCampaignFormDataDto(campaign, campaignForm, rdcf, formData);
		newCampaignFormDataDto = getCampaignFormDataFacade().saveCampaignFormData(newCampaignFormDataDto);

		assertNotNull(newCampaignFormDataDto);
		assertEquals(formData, newCampaignFormDataDto.getFormData());

		getCampaignFormDataFacade().deleteCampaignFormData(newCampaignFormDataDto.getUuid());

		CampaignFormDataDto deletedCampaignFormDataDto = getCampaignFormDataFacade().getCampaignFormDataByUuid(newCampaignFormDataDto.getUuid());

		assertEquals(null, deletedCampaignFormDataDto);
	}
}

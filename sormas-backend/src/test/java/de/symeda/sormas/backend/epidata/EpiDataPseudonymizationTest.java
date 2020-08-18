/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.epidata;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;

@RunWith(MockitoJUnitRunner.class)
public class EpiDataPseudonymizationTest extends AbstractBeanTest {
	private TestDataCreator.RDCF rdcf1;
	private TestDataCreator.RDCF rdcf2;
	private UserDto user1;
	private UserDto user2;

	@Override
	public void init() {
		super.init();

		rdcf1 = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		user1 = creator
				.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "Surv", "Off1", UserRole.SURVEILLANCE_OFFICER);

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator
				.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "Surv", "Off2", UserRole.SURVEILLANCE_OFFICER);

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Test
	public void getEpiDataInJurisdiction(){
		CaseDataDto caseWEpiData = createCaseWEpiData(user2, rdcf2);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caseWEpiData.getUuid());
		EpiDataDto epiData = savedCase.getEpiData();

		assertThat(epiData.getSickDeadAnimalsDetails(), is("Test sick dead animal details"));
		assertThat(epiData.getSickDeadAnimalsLocation(), is("Test sick dead animal location"));
		assertThat(epiData.getEatingRawAnimalsDetails(), is("Test eating raw animals details"));
		assertThat(epiData.getOtherAnimalsDetails(), is("Test other animals details"));

		assertThat(epiData.getWaterSourceOther(), is("Test water source other"));
		assertThat(epiData.getWaterBodyDetails(), is("Test water body details"));
		assertThat(epiData.getKindOfExposureDetails(), is("Test kind of exposure details"));
		assertThat(epiData.getPlaceOfLastExposure(), is("Test place of last exposure"));

		EpiDataBurialDto burial = epiData.getBurials().get(0);

		assertThat(burial.getBurialPersonName(), is("John Smith"));
		assertThat(burial.getBurialRelation(), is("Test burial relation"));

		LocationDto burialAddress = burial.getBurialAddress();
		assertThat(burialAddress.getCommunity(), is(rdcf2.community));
		assertThat(burialAddress.getCity(), is("Test City"));

		EpiDataTravelDto travel = epiData.getTravels().get(0);
		assertThat(travel.getTravelDestination(), is("Test travel destination"));

		EpiDataGatheringDto gathering = epiData.getGatherings().get(0);
		assertThat(gathering.getDescription(), is("Test gathering description"));
		LocationDto gatheringAddress = gathering.getGatheringAddress();
		assertThat(gatheringAddress.getCommunity(), is(rdcf2.community));
		assertThat(gatheringAddress.getCity(), is("Test City"));
	}

	@Test
	public void getEpiDataOutsideJurisdiction(){
		CaseDataDto caseWEpiData = createCaseWEpiData(user1, rdcf1);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caseWEpiData.getUuid());
		EpiDataDto epiData = savedCase.getEpiData();

		assertThat(epiData.getSickDeadAnimalsDetails(), isEmptyString());
		assertThat(epiData.getSickDeadAnimalsLocation(), isEmptyString());
		assertThat(epiData.getEatingRawAnimalsDetails(), isEmptyString());
		assertThat(epiData.getOtherAnimalsDetails(), isEmptyString());

		assertThat(epiData.getWaterSourceOther(), isEmptyString());
		assertThat(epiData.getWaterBodyDetails(), isEmptyString());
		assertThat(epiData.getKindOfExposureDetails(), isEmptyString());
		assertThat(epiData.getPlaceOfLastExposure(), isEmptyString());

		EpiDataBurialDto burial = epiData.getBurials().get(0);

		assertThat(burial.getBurialPersonName(), isEmptyString());
		assertThat(burial.getBurialRelation(), isEmptyString());

		LocationDto burialAddress = burial.getBurialAddress();
		assertThat(burialAddress.getCommunity(), is(nullValue()));
		assertThat(burialAddress.getCity(), isEmptyString());

		EpiDataTravelDto travel = epiData.getTravels().get(0);
		assertThat(travel.getTravelDestination(), isEmptyString());

		EpiDataGatheringDto gathering = epiData.getGatherings().get(0);
		assertThat(gathering.getDescription(), isEmptyString());
		LocationDto gatheringAddress = gathering.getGatheringAddress();
		assertThat(gatheringAddress.getCommunity(), is(nullValue()));
		assertThat(gatheringAddress.getCity(), isEmptyString());
	}

	private CaseDataDto createCaseWEpiData(UserDto user, TestDataCreator.RDCF rdcf){
		CaseDataDto caze = creator.createCase(user.toReference(), rdcf, c -> {
			EpiDataDto epiData = c.getEpiData();

			epiData.setSickDeadAnimals(YesNoUnknown.YES);
			epiData.setSickDeadAnimalsDetails("Test sick dead animal details");
			epiData.setSickDeadAnimalsLocation("Test sick dead animal location");

			epiData.setEatingRawAnimals(YesNoUnknown.YES);
			epiData.setEatingRawAnimalsDetails("Test eating raw animals details");

			epiData.setOtherAnimals(YesNoUnknown.YES);
			epiData.setOtherAnimalsDetails("Test other animals details");

			epiData.setWaterSourceOther("Test water source other");

			epiData.setWaterBody(YesNoUnknown.YES);
			epiData.setWaterBodyDetails("Test water body details");

			epiData.setKindOfExposureDetails("Test kind of exposure details");
			epiData.setPlaceOfLastExposure("Test place of last exposure");

			EpiDataBurialDto burial = new EpiDataBurialDto();
			burial.setBurialPersonName("John Smith");
			burial.setBurialRelation("Test burial relation");

			LocationDto address = new LocationDto();
			address.setRegion(rdcf.region);
			address.setDistrict(rdcf.district);
			address.setCommunity(rdcf.community);
			address.setCity("Test City");

			burial.setBurialAddress(address);

			epiData.getBurials().add(burial);

			EpiDataTravelDto travel = new EpiDataTravelDto();
			travel.setTravelDestination("Test travel destination");
			epiData.getTravels().add(travel);

			EpiDataGatheringDto gathering = new EpiDataGatheringDto();
			gathering.setGatheringAddress(address);
			gathering.setDescription("Test gathering description");

			epiData.getGatherings().add(gathering);
		});

		return caze;
	}
}

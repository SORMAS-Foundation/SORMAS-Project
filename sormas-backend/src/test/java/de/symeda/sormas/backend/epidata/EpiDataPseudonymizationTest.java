/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.epidata;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.exposure.AnimalContactType;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.exposure.TypeOfAnimal;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.user.DefaultUserRole;

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
		user1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			rdcf1.facility.getUuid(),
			"Surv",
			"Off1",
			creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER));

		rdcf2 = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2", "Point of entry 2");
		user2 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			rdcf2.facility.getUuid(),
			"Surv",
			"Off2",
			creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER));

		when(MockProducer.getPrincipal().getName()).thenReturn("SurvOff2");
	}

	@Test
	public void getEpiDataInJurisdiction() {
		CaseDataDto caseWEpiData = createCaseWEpiData(user2, rdcf2);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caseWEpiData.getUuid());
		EpiDataDto epiData = savedCase.getEpiData();

		assertThat(epiData.getExposures().get(0).getDescription(), is("Test description"));
		assertThat(epiData.getExposures().get(0).getLocation().getDetails(), is("Test location details"));
		assertThat(epiData.getExposures().get(1).getTypeOfAnimalDetails(), is("Test other animal details"));
		assertThat(epiData.getExposures().get(2).getWaterSourceDetails(), is("Test water source details"));
		assertThat(epiData.getExposures().get(3).getAnimalContactTypeDetails(), is("Test animal contact type details"));

		ExposureDto burial = epiData.getExposures().get(4);
		assertThat(burial.getDeceasedPersonName(), is("John Smith"));
		assertThat(burial.getDeceasedPersonRelation(), is("Test burial relation"));
		LocationDto burialAddress = burial.getLocation();
		assertThat(burialAddress.getCommunity(), is(rdcf2.community));
		assertThat(burialAddress.getCity(), is("Test City"));

		ExposureDto travel = epiData.getExposures().get(5);
		assertThat(travel.getLocation().getDetails(), is("Test travel destination"));

		ExposureDto gathering = epiData.getExposures().get(6);
		assertThat(gathering.getDescription(), is("Test gathering description"));
		LocationDto gatheringAddress = gathering.getLocation();
		assertThat(gatheringAddress.getCommunity(), is(rdcf2.community));
		assertThat(gatheringAddress.getCity(), is("Test City"));
	}

	@Test
	public void getEpiDataOutsideJurisdiction() {
		CaseDataDto caseWEpiData = createCaseWEpiData(user1, rdcf1);

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caseWEpiData.getUuid());
		EpiDataDto epiData = savedCase.getEpiData();

		assertThat(epiData.getExposures().get(0).getDescription(), isEmptyString());
		assertThat(epiData.getExposures().get(0).getLocation().getDetails(), isEmptyString());
		assertThat(epiData.getExposures().get(1).getTypeOfAnimalDetails(), isEmptyString());
		assertThat(epiData.getExposures().get(2).getWaterSourceDetails(), isEmptyString());
		assertThat(epiData.getExposures().get(3).getAnimalContactTypeDetails(), isEmptyString());

		ExposureDto burial = epiData.getExposures().get(4);

		assertThat(burial.getDeceasedPersonName(), isEmptyString());
		assertThat(burial.getDeceasedPersonRelation(), isEmptyString());
		LocationDto burialAddress = burial.getLocation();
		assertThat(burialAddress.getCommunity(), is(nullValue()));
		assertThat(burialAddress.getCity(), isEmptyString());

		ExposureDto travel = epiData.getExposures().get(5);
		assertThat(travel.getLocation().getDetails(), isEmptyString());

		ExposureDto gathering = epiData.getExposures().get(6);
		assertThat(gathering.getDescription(), isEmptyString());
		LocationDto gatheringAddress = gathering.getLocation();
		assertThat(gatheringAddress.getCommunity(), is(nullValue()));
		assertThat(gatheringAddress.getCity(), isEmptyString());
	}

	private CaseDataDto createCaseWEpiData(UserDto user, TestDataCreator.RDCF rdcf) {
		CaseDataDto caze = creator.createCase(user.toReference(), rdcf, c -> {
			EpiDataDto epiData = c.getEpiData();

			ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
			exposure.setAnimalCondition(AnimalCondition.DEAD);
			exposure.setDescription("Test description");
			exposure.getLocation().setDetails("Test location details");
			epiData.getExposures().add(exposure);

			ExposureDto exposure2 = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
			exposure2.setTypeOfAnimal(TypeOfAnimal.OTHER);
			exposure2.setTypeOfAnimalDetails("Test other animal details");
			epiData.getExposures().add(exposure2);

			ExposureDto exposure3 = ExposureDto.build(ExposureType.TRAVEL);
			exposure3.setBodyOfWater(YesNoUnknown.YES);
			exposure3.setWaterSource(WaterSource.OTHER);
			exposure3.setWaterSourceDetails("Test water source details");
			epiData.getExposures().add(exposure3);

			ExposureDto exposure4 = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
			exposure4.setAnimalContactType(AnimalContactType.OTHER);
			exposure4.setAnimalContactTypeDetails("Test animal contact type details");
			epiData.getExposures().add(exposure4);

			ExposureDto burial = ExposureDto.build(ExposureType.BURIAL);
			burial.setDeceasedPersonName("John Smith");
			burial.setDeceasedPersonRelation("Test burial relation");
			LocationDto address = new LocationDto();
			address.setRegion(rdcf.region);
			address.setDistrict(rdcf.district);
			address.setCommunity(rdcf.community);
			address.setCity("Test City");
			burial.setLocation(address);
			epiData.getExposures().add(burial);

			ExposureDto travel = ExposureDto.build(ExposureType.TRAVEL);
			travel.getLocation().setDetails("Test travel destination");
			epiData.getExposures().add(travel);

			ExposureDto gathering = ExposureDto.build(ExposureType.GATHERING);
			gathering.setLocation(address);
			gathering.setDescription("Test gathering description");
			epiData.getExposures().add(gathering);
		});

		return caze;
	}
}

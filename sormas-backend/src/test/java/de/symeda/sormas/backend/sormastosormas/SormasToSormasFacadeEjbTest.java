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

package de.symeda.sormas.backend.sormastosormas;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.porthealthinfo.PortHealthInfoDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.epidata.EpiDataBurialDto;
import de.symeda.sormas.api.epidata.EpiDataGatheringDto;
import de.symeda.sormas.api.epidata.EpiDataTravelDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSourceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class SormasToSormasFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testSaveSharedCase() {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setHealthFacility(rdcf.remoteRdcf.facility);

		caze.setSormasToSormasSource(createSormasToSormasSource());

		getSormasToSormasFacade().saveSharedCase(new SormasToSormasCaseDto(person, caze));

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(person.getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.localRdcf.community));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase, is(notNullValue()));
		assertThat(savedCase.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedCase.getHealthFacility(), is(rdcf.localRdcf.facility));
		assertThat(savedCase.getHospitalization().getUuid(), is(caze.getHospitalization().getUuid()));
		assertThat(savedCase.getSymptoms().getUuid(), is(caze.getSymptoms().getUuid()));
		assertThat(savedCase.getEpiData().getUuid(), is(caze.getEpiData().getUuid()));
		assertThat(savedCase.getTherapy().getUuid(), is(caze.getTherapy().getUuid()));
		assertThat(savedCase.getClinicalCourse().getUuid(), is(caze.getClinicalCourse().getUuid()));
		assertThat(savedCase.getMaternalHistory().getUuid(), is(caze.getMaternalHistory().getUuid()));

		assertThat(savedCase.getSormasToSormasSource().getHealthDepartment(), is("Test Department"));
		assertThat(savedCase.getSormasToSormasSource().getSenderName(), is("John doe"));
	}

	@Test
	public void testRecreateEmbeddedUuidsOfCase() {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setHealthFacility(rdcf.remoteRdcf.facility);

		caze.setSormasToSormasSource(createSormasToSormasSource());

		caze.getHospitalization().getPreviousHospitalizations().add(PreviousHospitalizationDto.build(caze));
		caze.getEpiData().getBurials().add(EpiDataBurialDto.build());
		caze.getEpiData().getTravels().add(EpiDataTravelDto.build());
		caze.getEpiData().getGatherings().add(EpiDataGatheringDto.build());

		getSormasToSormasFacade().saveSharedCase(new SormasToSormasCaseDto(person, caze));

		caze.setUuid(DataHelper.createUuid());

		getSormasToSormasFacade().saveSharedCase(new SormasToSormasCaseDto(person, caze));

	}

	@Test
	public void testSaveSharedPointOfEntryCase() {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		CaseDataDto caze = CaseDataDto.build(person.toReference(), Disease.CORONAVIRUS);
		caze.setCaseOrigin(CaseOrigin.POINT_OF_ENTRY);
		caze.setRegion(rdcf.remoteRdcf.region);
		caze.setDistrict(rdcf.remoteRdcf.district);
		caze.setCommunity(rdcf.remoteRdcf.community);
		caze.setPointOfEntry(rdcf.remoteRdcf.pointOfEntry);
		PortHealthInfoDto portHealthInfo = PortHealthInfoDto.build();
		caze.setPortHealthInfo(portHealthInfo);
		caze.setSormasToSormasSource(createSormasToSormasSource());

		getSormasToSormasFacade().saveSharedCase(new SormasToSormasCaseDto(person, caze));

		CaseDataDto savedCase = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		assertThat(savedCase.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedCase.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedCase.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedCase.getPointOfEntry(), is(rdcf.localRdcf.pointOfEntry));
		assertThat(savedCase.getPortHealthInfo().getUuid(), is(portHealthInfo.getUuid()));
	}

	@Test
	public void testSaveSharedContact() {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		ContactDto contact = ContactDto.build(null, Disease.CORONAVIRUS, null);
		contact.setPerson(person.toReference());
		contact.setRegion(rdcf.remoteRdcf.region);
		contact.setDistrict(rdcf.remoteRdcf.district);
		contact.setCommunity(rdcf.remoteRdcf.community);

		contact.setSormasToSormasSource(createSormasToSormasSource());

		getSormasToSormasFacade().saveSharedContact(new SormasToSormasContactDto(person, contact));

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(person.getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.localRdcf.community));

		ContactDto savedContact = getContactFacade().getContactByUuid(contact.getUuid());

		assertThat(savedContact, is(notNullValue()));
		assertThat(savedContact.getRegion(), is(rdcf.localRdcf.region));
		assertThat(savedContact.getDistrict(), is(rdcf.localRdcf.district));
		assertThat(savedContact.getCommunity(), is(rdcf.localRdcf.community));
		assertThat(savedContact.getEpiData().getUuid(), is(contact.getEpiData().getUuid()));

		assertThat(savedContact.getSormasToSormasSource().getHealthDepartment(), is("Test Department"));
		assertThat(savedContact.getSormasToSormasSource().getSenderName(), is("John doe"));
	}

	@Test
	public void testRecreateEmbeddedUuidsOfContact() {
		MappableRdcf rdcf = createRDCF();

		PersonDto person = createPersonDto(rdcf);

		ContactDto contact = ContactDto.build(null, Disease.CORONAVIRUS, null);
		contact.setPerson(person.toReference());
		contact.setRegion(rdcf.remoteRdcf.region);
		contact.setDistrict(rdcf.remoteRdcf.district);
		contact.setCommunity(rdcf.remoteRdcf.community);

		contact.setSormasToSormasSource(createSormasToSormasSource());

		getSormasToSormasFacade().saveSharedContact(new SormasToSormasContactDto(person, contact));

		contact.setUuid(DataHelper.createUuid());

		getSormasToSormasFacade().saveSharedContact(new SormasToSormasContactDto(person, contact));
	}

	private PersonDto createPersonDto(MappableRdcf rdcf) {
		PersonDto person = PersonDto.build();
		person.setFirstName("John");
		person.setLastName("Smith");

		person.getAddress().setDistrict(rdcf.remoteRdcf.district);
		person.getAddress().setRegion(rdcf.remoteRdcf.region);
		person.getAddress().setCommunity(rdcf.remoteRdcf.community);

		return person;
	}

	private SormasToSormasSourceDto createSormasToSormasSource() {
		SormasToSormasSourceDto source = new SormasToSormasSourceDto();
		source.setHealthDepartment("Test Department");
		source.setSenderName("John doe");

		return source;
	}

	private MappableRdcf createRDCF() {
		String regionName = "Region";
		String districtName = "District";
		String communityName = "Community";
		String facilityName = "Facility";
		String pointOfEntryName = "Point of Entry";

		MappableRdcf rdcf = new MappableRdcf();
		rdcf.remoteRdcf = new TestDataCreator.RDCF(
			new RegionReferenceDto(DataHelper.createUuid(), regionName),
			new DistrictReferenceDto(DataHelper.createUuid(), districtName),
			new CommunityReferenceDto(DataHelper.createUuid(), communityName),
			new FacilityReferenceDto(DataHelper.createUuid(), facilityName),
			new PointOfEntryReferenceDto(DataHelper.createUuid(), pointOfEntryName));
		rdcf.localRdcf = creator.createRDCF(regionName, districtName, communityName, facilityName, pointOfEntryName);

		return rdcf;
	}

	private static class MappableRdcf {

		private TestDataCreator.RDCF remoteRdcf;
		private TestDataCreator.RDCF localRdcf;
	}
}

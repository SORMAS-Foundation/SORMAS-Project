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

package de.symeda.sormas.backend.sormastosormas.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;

import de.symeda.sormas.backend.sormastosormas.share.ShareRequestAcceptData;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;
import de.symeda.sormas.backend.user.DefaultUserRole;
import de.symeda.sormas.backend.user.User;

@RunWith(MockitoJUnitRunner.class)
public class SormasToSormasContactFacadeEjbTest extends SormasToSormasTest {

	@Test
	public void testShareContact() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		ContactDto contact = creator
			.createContact(officer, officer, person.toReference(), null, new Date(), null, null, rdcf, dto -> dto.setResultingCaseUser(officer));

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/contacts"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);
				assertThat(postBody.getContacts().size(), is(1));
				SormasToSormasContactDto sharedContact = postBody.getContacts().get(0);

				assertThat(sharedContact.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(sharedContact.getPerson().getLastName(), is(person.getLastName()));

				assertThat(sharedContact.getEntity().getUuid(), is(contact.getUuid()));
				// users should be cleaned up
				assertThat(sharedContact.getEntity().getReportingUser(), is(nullValue()));
				assertThat(sharedContact.getEntity().getContactOfficer(), is(nullValue()));
				assertThat(sharedContact.getEntity().getResultingCaseUser(), is(nullValue()));

				// share information
				assertThat(postBody.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(postBody.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(postBody.getOriginInfo().getComment(), is("Test comment"));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasContactFacade().share(Collections.singletonList(contact.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);
		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testShareContactWithSamples() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		ContactDto contact = creator.createContact(officer, officer, person.toReference(), null, new Date(), null, null, rdcf);

		Date sampleDateTime = new Date();
		SampleDto sample = creator.createSample(contact.toReference(), officer, rdcf.facility, s -> {
			s.setSampleDateTime(sampleDateTime);
			s.setComment("Test sample");
		});
		creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.RAPID_TEST,
			Disease.CORONAVIRUS,
			sampleDateTime,
			rdcf.facility,
			officer,
			PathogenTestResultType.INDETERMINATE,
			"result",
			true,
			null);
		creator.createAdditionalTest(sample.toReference());

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");
		options.setWithSamples(true);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);
				assertThat(postBody.getContacts().size(), is(1));

				assertThat(postBody.getSamples().size(), is(1));

				SormasToSormasSampleDto sharedSample = postBody.getSamples().get(0);
				assertThat(sharedSample.getEntity().getSampleDateTime().compareTo(sampleDateTime), is(0));
				assertThat(sharedSample.getEntity().getComment(), is("Test sample"));
				assertThat(sharedSample.getPathogenTests(), hasSize(1));
				assertThat(sharedSample.getAdditionalTests(), hasSize(1));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasContactFacade().share(Collections.singletonList(contact.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().sample(sample.toReference()), 0, 100);

		SormasToSormasShareInfoDto sampleShareInfoList = shareInfoList.get(0);
		assertThat(sampleShareInfoList.getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(sampleShareInfoList.getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(sampleShareInfoList.getComment(), is("Test comment"));
	}

	@Test
	public void testSaveSharedContact() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);
		person.setFirstName("James");
		person.setLastName("Smith");

		ContactDto contact = createRemoteContactDto(rdcf, person);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setContacts(Collections.singletonList(new SormasToSormasContactDto(person, contact)));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);
		getSormasToSormasContactFacade().saveSharedEntities(encryptedData);

		ContactDto savedContact = getContactFacade().getByUuid(contact.getUuid());

		assertThat(savedContact, is(notNullValue()));
		assertThat(savedContact.getRegion(), is(rdcf.region));
		assertThat(savedContact.getDistrict(), is(rdcf.district));
		assertThat(savedContact.getCommunity(), is(rdcf.community));
		assertThat(savedContact.getEpiData().getExposures().get(0).getAnimalCondition(), is(AnimalCondition.PROCESSED));

		assertThat(savedContact.getSormasToSormasOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
		assertThat(savedContact.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));

		PersonDto savedPerson = getPersonFacade().getPersonByUuid(savedContact.getPerson().getUuid());
		assertThat(savedPerson, is(notNullValue()));
		assertThat(savedPerson.getAddress().getRegion(), is(rdcf.region));
		assertThat(savedPerson.getAddress().getDistrict(), is(rdcf.district));
		assertThat(savedPerson.getAddress().getCommunity(), is(rdcf.community));
		assertThat(savedPerson.getFirstName(), is("James"));
		assertThat(savedPerson.getLastName(), is("Smith"));

	}

	@Test
	public void testSaveSharedContactWithSamples() throws SormasToSormasException, SormasToSormasValidationException {
		PersonDto person = createPersonDto(rdcf);

		ContactDto contact = createRemoteContactDto(rdcf, person);
		SormasToSormasSampleDto sample = createRemoteSampleDtoWithTests(rdcf, null, contact.toReference());

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setContacts(Collections.singletonList(new SormasToSormasContactDto(person, contact)));
		shareData.setSamples(Collections.singletonList(sample));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);
		getSormasToSormasContactFacade().saveSharedEntities(encryptedData);

		ContactDto savedContact = getContactFacade().getByUuid(contact.getUuid());
		SampleDto savedSample = getSampleFacade().getSampleByUuid(sample.getEntity().getUuid());

		assertThat(savedSample, is(notNullValue()));
		assertThat(savedSample.getLab(), is(rdcf.facility));

		assertThat(getPathogenTestFacade().getAllBySample(savedSample.toReference()), hasSize(1));
		assertThat(getAdditionalTestFacade().getAllBySample(savedSample.getUuid()), hasSize(1));

		assertThat(savedContact.getSormasToSormasOriginInfo().getUuid(), is(savedSample.getSormasToSormasOriginInfo().getUuid()));
	}

	@Test
	public void testReturnContact() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson();
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		ContactDto contact =
			creator.createContact(officer, officer, person.toReference(), null, new Date(), new Date(), Disease.CORONAVIRUS, rdcf, c -> {
				SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
				originInfo.setSenderName("Test Name");
				originInfo.setSenderEmail("test@email.com");
				originInfo.setOrganizationId(DEFAULT_SERVER_ID);
				originInfo.setOwnershipHandedOver(true);

				c.setSormasToSormasOriginInfo(originInfo);
			});

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(contact.getSormasToSormasOriginInfo());
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SampleDto sharedSample = creator
			.createSample(contact.toReference(), officer, rdcf.facility, s -> s.setSormasToSormasOriginInfo(contact.getSormasToSormasOriginInfo()));

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setHandOverOwnership(true);
		options.setWithSamples(true);
		options.setComment("Test comment");

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(new ShareRequestAcceptData(null, null)));

		getSormasToSormasContactFacade().share(Collections.singletonList(contact.getUuid()), options);

		// contact ownership should be lost
		ContactDto sharedContact = getContactFacade().getByUuid(contact.getUuid());
		assertThat(sharedContact.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		// sample ownership should be lost
		sharedSample = getSampleFacade().getSampleByUuid(sharedSample.getUuid());
		assertThat(sharedSample.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));
	}

	@Test
	public void testSaveReturnedContact() throws SormasToSormasException, SormasToSormasValidationException {
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		PersonDto contactPerson = creator.createPerson();
		ContactDto contact = creator.createContact(rdcf, officer, contactPerson.toReference());
		SampleDto sharedSample = creator.createSample(contact.toReference(), officer, rdcf.facility, null);
		SampleDto newSample = createRemoteSample(contact.toReference(), officer, rdcf.facility);

		User officerUser = getUserService().getByReferenceDto(officer);
		getShareRequestInfoService().persist(
			createShareRequestInfo(
				officerUser,
				DEFAULT_SERVER_ID,
				true,
				i -> i.setContact(getContactService().getByReferenceDto(contact.toReference()))));
		getShareRequestInfoService().persist(
			createShareRequestInfo(
				officerUser,
				DEFAULT_SERVER_ID,
				true,
				i -> i.setSample(getSampleService().getByReferenceDto(sharedSample.toReference()))));

		contact.setQuarantine(QuarantineType.HOTEL);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(contact.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		contact.setChangeDate(calendar.getTime());

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true));
		shareData.setContacts(Collections.singletonList(new SormasToSormasContactDto(contactPerson, contact)));
		shareData.setSamples(
			Arrays.asList(
				new SormasToSormasSampleDto(sharedSample, Collections.emptyList(), Collections.emptyList()),
				new SormasToSormasSampleDto(newSample, Collections.emptyList(), Collections.emptyList())));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

		getSormasToSormasContactFacade().saveSharedEntities(encryptedData);

		ContactDto returnedContact = getContactFacade().getByUuid(contact.getUuid());
		assertThat(returnedContact.getQuarantine(), is(QuarantineType.HOTEL));
		assertThat(returnedContact.getReportingUser(), is(officer));

		List<SormasToSormasShareInfoDto> contactShares =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().contact(contact.toReference()), 0, 100);
		assertThat(contactShares.get(0).isOwnershipHandedOver(), is(false));

		List<SormasToSormasShareInfoDto> sampleShares =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().sample(sharedSample.toReference()), 0, 100);
		assertThat(sampleShares.get(0).isOwnershipHandedOver(), is(false));

		SampleDto returnedNewSample = getSampleFacade().getSampleByUuid(newSample.getUuid());
		assertThat(returnedNewSample.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));
	}

	@Test
	public void testSyncContacts() throws SormasToSormasValidationException, SormasToSormasException {
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		PersonDto contactPerson = creator.createPerson();
		ContactDto contact =
			creator.createContact(officer, officer, contactPerson.toReference(), null, new Date(), new Date(), Disease.CORONAVIRUS, rdcf, c -> {
				SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
				originInfo.setSenderName("Test Name");
				originInfo.setSenderEmail("test@email.com");
				originInfo.setOrganizationId(DEFAULT_SERVER_ID);
				originInfo.setWithAssociatedContacts(true);
				originInfo.setOwnershipHandedOver(true);

				c.setSormasToSormasOriginInfo(originInfo);
			});

		getShareRequestInfoService().persist(
			createShareRequestInfo(getUserService().getByUuid(officer.getUuid()), SECOND_SERVER_ID, false, ShareRequestStatus.ACCEPTED, i -> {
				i.setContact(getContactService().getByUuid(contact.getUuid()));
			}));

		contact.setAdditionalDetails("Test updated details");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(contact.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		contact.setChangeDate(calendar.getTime());

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true));
		shareData.setContacts(Collections.singletonList(new SormasToSormasContactDto(contactPerson, contact)));

		SormasToSormasEncryptedDataDto encryptedData =
			encryptShareData(new SyncDataDto(shareData, new ShareTreeCriteria(contact.getUuid(), null, false)));

		MockProducer.getProperties().setProperty(SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS, Boolean.FALSE.toString());

		Mockito
			.when(
				MockProducer.getManagedScheduledExecutorService()
					.schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
			.then(invocation -> {
				((Runnable) invocation.getArgument(0)).run();
				return null;
			});

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.then(restInvocation -> {
				SyncDataDto syncData = restInvocation.getArgument(2);

				assertThat(syncData.getCriteria().getEntityUuid(), is(contact.getUuid()));
				assertThat(syncData.getCriteria().getExceptedOrganizationId(), is(SECOND_SERVER_ID));
				assertThat(syncData.getCriteria().isForwardOnly(), is(false));

				assertThat(syncData.getShareData().getContacts().get(0).getEntity().getUuid(), is(contact.getUuid()));
				assertThat(syncData.getShareData().getContacts().get(0).getEntity().getAdditionalDetails(), is("Test updated details"));

				return Response.noContent().build();
			});

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(SECOND_SERVER_ID), ArgumentMatchers.contains("/contacts/sync"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.then(invocation -> {
				SyncDataDto syncData = invocation.getArgument(2);

				assertThat(syncData.getCriteria().getEntityUuid(), is(contact.getUuid()));
				assertThat(syncData.getCriteria().getExceptedOrganizationId(), is(nullValue()));
				assertThat(syncData.getCriteria().isForwardOnly(), is(true));

				assertThat(syncData.getShareData().getContacts().get(0).getEntity().getUuid(), is(contact.getUuid()));
				assertThat(syncData.getShareData().getContacts().get(0).getEntity().getAdditionalDetails(), is("Test updated details"));

				return Response.noContent().build();
			});

		getSormasToSormasContactFacade().saveSyncedEntity(encryptedData);

		Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
			.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.contains("/contacts/sync"), ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
			.post(eq(SECOND_SERVER_ID), ArgumentMatchers.contains("/contacts/sync"), ArgumentMatchers.any(), ArgumentMatchers.any());

	}

	protected ContactDto createRemoteContactDto(TestDataCreator.RDCF remoteRdcf, PersonDto person) {
		ContactDto contact = ContactDto.build(null, Disease.CORONAVIRUS, null, null);
		contact.setPerson(person.toReference());
		contact.setRegion(remoteRdcf.region);
		contact.setDistrict(remoteRdcf.district);
		contact.setCommunity(remoteRdcf.community);
		ExposureDto exposure = ExposureDto.build(ExposureType.ANIMAL_CONTACT);
		exposure.setAnimalCondition(AnimalCondition.PROCESSED);
		contact.getEpiData().getExposures().add(exposure);
		return contact;
	}

	protected SampleDto createRemoteSample(ContactReferenceDto associatedContact, UserReferenceDto reportingUser, FacilityReferenceDto lab) {

		SampleDto sample = SampleDto.build(reportingUser, associatedContact);
		sample.setSampleDateTime(new Date());
		sample.setReportDateTime(new Date());
		sample.setSampleMaterial(SampleMaterial.BLOOD);
		sample.setSamplePurpose(SamplePurpose.EXTERNAL);
		sample.setLab(getFacilityFacade().getReferenceByUuid(lab.getUuid()));

		return sample;
	}
}

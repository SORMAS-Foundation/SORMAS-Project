/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import de.symeda.sormas.api.caze.VaccinationStatus;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventSourceType;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.user.DefaultUserRole;
import de.symeda.sormas.backend.user.User;

@RunWith(MockitoJUnitRunner.class)
public class SormasToSormasEventFacadeEjbTest extends SormasToSormasTest {

	@Test
	public void testShareEvent() throws SormasToSormasException {
		UserDto user = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.NATIONAL_USER));

		useSurveillanceOfficerLogin(rdcf);

		Date dateNow = new Date();

		EventDto event = creator
			.createEvent(EventStatus.SCREENING, EventInvestigationStatus.ONGOING, "Test event title", "Test description", user.toReference(), (e) -> {
				e.setRiskLevel(RiskLevel.MODERATE);
				e.setMultiDayEvent(true);
				e.setStartDate(dateNow);
				e.setEndDate(dateNow);
				e.setSrcType(EventSourceType.MEDIA_NEWS);
				e.setSrcMediaWebsite("Test media name");
				e.setSrcMediaName("Test media website");
				e.setSrcMediaDetails("Test media details");

				e.getEventLocation().setRegion(rdcf.region);
				e.getEventLocation().setDistrict(rdcf.district);
			});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setComment("Test comment");

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/events"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);
				assertThat(postBody.getEvents().size(), is(1));
				SormasToSormasEventDto sharedEventData = postBody.getEvents().get(0);
				EventDto sharedEvent = sharedEventData.getEntity();

				assertThat(sharedEvent.getEventTitle(), is("Test event title"));
				assertThat(sharedEvent.getEventDesc(), is("Test description"));
				assertThat(sharedEvent.getEventStatus(), is(EventStatus.SCREENING));
				assertThat(sharedEvent.getEventInvestigationStatus(), is(EventInvestigationStatus.ONGOING));
				assertThat(sharedEvent.getRiskLevel(), is(RiskLevel.MODERATE));
				assertThat(sharedEvent.isMultiDayEvent(), is(true));
				assertThat(sharedEvent.getStartDate().getTime(), is(dateNow.getTime()));
				assertThat(sharedEvent.getEndDate().getTime(), is(dateNow.getTime()));
				assertThat(sharedEvent.getSrcType(), is(EventSourceType.MEDIA_NEWS));
				assertThat(sharedEvent.getSrcMediaWebsite(), is("Test media name"));
				assertThat(sharedEvent.getSrcMediaName(), is("Test media website"));
				assertThat(sharedEvent.getSrcMediaDetails(), is("Test media details"));

				// share information
				assertThat(postBody.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(postBody.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(postBody.getOriginInfo().getComment(), is("Test comment"));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasEventFacade().share(Collections.singletonList(event.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().event(event.toReference()), 0, 100);
		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testShareEventWithSamples() throws SormasToSormasException {
		UserDto user = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.NATIONAL_USER));

		useSurveillanceOfficerLogin(rdcf);

		EventDto event = creator
			.createEvent(EventStatus.SCREENING, EventInvestigationStatus.ONGOING, "Test event title", "Test description", user.toReference(), (e) -> {
				e.getEventLocation().setRegion(rdcf.region);
				e.getEventLocation().setDistrict(rdcf.district);
			});

		EventParticipantDto eventParticipant = creator.createEventParticipant(
			event.toReference(),
			creator.createPerson("John", "Doe", p -> p.setBirthName("Test birth name")),
			"Involved",
			user.toReference(),
			(ep) -> {
				ep.setRegion(rdcf.region);
				ep.setDistrict(rdcf.district);
				ep.setVaccinationStatus(VaccinationStatus.VACCINATED);
			},
			null);

		SampleDto sample =
			creator.createSample(eventParticipant.toReference(), new Date(), new Date(), user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		creator.createPathogenTest(
			sample.toReference(),
			PathogenTestType.CULTURE,
			Disease.CORONAVIRUS,
			new Date(),
			rdcf.facility,
			user.toReference(),
			PathogenTestResultType.INDETERMINATE,
			"Test result",
			true,
			null);
		creator.createAdditionalTest(sample.toReference());

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setWithEventParticipants(true);
		options.setWithSamples(true);
		options.setComment("Test comment");

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/events"));

				SormasToSormasDto postBody = invocation.getArgument(2, SormasToSormasDto.class);
				assertThat(postBody.getEvents().size(), is(1));

				List<SormasToSormasEventParticipantDto> eventParticipants = postBody.getEventParticipants();
				assertThat(eventParticipants, hasSize(1));

				List<SormasToSormasSampleDto> samples = postBody.getSamples();
				assertThat(samples, hasSize(1));

				assertThat(samples.get(0).getPathogenTests(), hasSize(1));
				assertThat(samples.get(0).getAdditionalTests(), hasSize(1));

				return encryptShareData(new ShareRequestAcceptData(null, null));
			});

		getSormasToSormasEventFacade().share(Collections.singletonList(event.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().event(event.toReference()), 0, 100);
		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
	}

	@Test
	public void testSaveSharedEvents() throws SormasToSormasException, SormasToSormasValidationException {
		EventDto event = createEventDto(rdcf);
		event.setEventDesc("Test description");
		event.setEventStatus(EventStatus.SCREENING);
		event.setEventInvestigationStatus(EventInvestigationStatus.ONGOING);
		event.setRiskLevel(RiskLevel.MODERATE);
		event.setMultiDayEvent(true);
		Date dateNow = new Date();
		event.setStartDate(dateNow);
		event.setEndDate(dateNow);
		event.setSrcType(EventSourceType.MEDIA_NEWS);
		event.setSrcMediaWebsite("Test media name");
		event.setSrcMediaName("Test media website");
		event.setSrcMediaDetails("Test media details");

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setEvents(Collections.singletonList(new SormasToSormasEventDto(event)));
		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

		getSormasToSormasEventFacade().saveSharedEntities(encryptedData);

		EventDto savedEvent = getEventFacade().getEventByUuid(event.getUuid(), false);

		assertThat(savedEvent, is(notNullValue()));

		assertThat(savedEvent.getEventTitle(), is("Test event title"));
		assertThat(savedEvent.getEventDesc(), is("Test description"));
		assertThat(savedEvent.getEventStatus(), is(EventStatus.SCREENING));
		assertThat(savedEvent.getEventInvestigationStatus(), is(EventInvestigationStatus.ONGOING));
		assertThat(savedEvent.getRiskLevel(), is(RiskLevel.MODERATE));
		assertThat(savedEvent.isMultiDayEvent(), is(true));
		assertThat(savedEvent.getStartDate().getTime(), is(dateNow.getTime()));
		assertThat(savedEvent.getEndDate().getTime(), is(dateNow.getTime()));
		assertThat(savedEvent.getSrcType(), is(EventSourceType.MEDIA_NEWS));
		assertThat(savedEvent.getSrcMediaWebsite(), is("Test media name"));
		assertThat(savedEvent.getSrcMediaName(), is("Test media website"));
		assertThat(savedEvent.getSrcMediaDetails(), is("Test media details"));

		assertThat(savedEvent.getEventLocation().getRegion(), is(rdcf.region));
		assertThat(savedEvent.getEventLocation().getDistrict(), is(rdcf.district));

		assertThat(savedEvent.getSormasToSormasOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
		assertThat(savedEvent.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));
	}

	@Test
	public void testSaveSharedEventsWithParticipants() throws SormasToSormasException, SormasToSormasValidationException {
		EventDto event = createEventDto(rdcf);
		EventParticipantDto eventParticipant = createEventParticipantDto(event.toReference(), UserDto.build().toReference(), rdcf);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setEvents(Collections.singletonList(new SormasToSormasEventDto(event)));
		shareData.setEventParticipants(Collections.singletonList(new SormasToSormasEventParticipantDto(eventParticipant)));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

		getSormasToSormasEventFacade().saveSharedEntities(encryptedData);

		EventParticipantDto savedParticipant = getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid());

		assertThat(savedParticipant, is(notNullValue()));

		assertThat(savedParticipant.getPerson(), is(notNullValue()));
		assertThat(savedParticipant.getPerson().getFirstName(), is("John"));
		assertThat(savedParticipant.getPerson().getLastName(), is("Smith"));

		assertThat(savedParticipant.getPerson().getAddress().getRegion(), is(rdcf.region));
		assertThat(savedParticipant.getPerson().getAddress().getDistrict(), is(rdcf.district));

		assertThat(savedParticipant.getSormasToSormasOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
		assertThat(savedParticipant.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));
	}

	@Test
	public void testSaveSharedEventsWithSamples() throws SormasToSormasException, SormasToSormasValidationException {
		FacilityDto remoteLab = FacilityDto.build();
		remoteLab.setName("Test Lab");
		FacilityDto localLab = creator.createFacility("Test Lab", rdcf.region, rdcf.district, null, FacilityType.LABORATORY);

		EventDto event = createEventDto(rdcf);
		UserDto sampleUser = UserDto.build();
		EventParticipantDto eventParticipant = createEventParticipantDto(event.toReference(), sampleUser.toReference(), rdcf);

		SampleDto sample = createSample(eventParticipant.toReference(), sampleUser.toReference(), remoteLab.toReference());
		sample.setLabSampleID("Test lab sample id");

		PathogenTestDto pathogenTest = PathogenTestDto.build(sample, sampleUser);
		pathogenTest.setTestDateTime(new Date());
		pathogenTest.setLab(remoteLab.toReference());
		pathogenTest.setTestType(PathogenTestType.RAPID_TEST);
		pathogenTest.setTestResult(PathogenTestResultType.PENDING);

		AdditionalTestDto additionalTest = AdditionalTestDto.build(sample.toReference());
		additionalTest.setTestDateTime(new Date());
		additionalTest.setHaemoglobin(0.2F);
		additionalTest.setConjBilirubin(0.3F);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setEvents(Collections.singletonList(new SormasToSormasEventDto(event)));
		shareData.setEventParticipants(Collections.singletonList(new SormasToSormasEventParticipantDto(eventParticipant)));
		shareData.setSamples(
			Collections.singletonList(
				new SormasToSormasSampleDto(sample, Collections.singletonList(pathogenTest), Collections.singletonList(additionalTest))));
		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

		getSormasToSormasEventFacade().saveSharedEntities(encryptedData);

		SampleDto savedSample = getSampleFacade().getSampleByUuid(sample.getUuid());
		assertThat(savedSample, is(notNullValue()));
		assertThat(savedSample.getAssociatedEventParticipant(), is(eventParticipant.toReference()));
		assertThat(savedSample.getSampleMaterial(), is(SampleMaterial.BLOOD));
		assertThat(savedSample.getLab(), is(localLab.toReference()));
		assertThat(savedSample.getLabSampleID(), is("Test lab sample id"));

		assertThat(savedSample.getSormasToSormasOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
		assertThat(savedSample.getSormasToSormasOriginInfo().getSenderName(), is("John doe"));

		PathogenTestDto savedPathogenTest = getPathogenTestFacade().getByUuid(pathogenTest.getUuid());
		assertThat(savedPathogenTest, is(notNullValue()));
		assertThat(savedPathogenTest.getLab(), is(localLab.toReference()));
		assertThat(savedPathogenTest.getTestType(), is(PathogenTestType.RAPID_TEST));
		assertThat(savedPathogenTest.getTestResult(), is(PathogenTestResultType.PENDING));

		AdditionalTestDto savedAdditionalTest = getAdditionalTestFacade().getByUuid(additionalTest.getUuid());
		assertThat(savedAdditionalTest, is(notNullValue()));
		assertThat(savedAdditionalTest.getHaemoglobin(), is(0.2F));
		assertThat(savedAdditionalTest.getConjBilirubin(), is(0.3F));
	}

	@Test
	public void testReturnEvent() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		EventDto event =
			creator.createEvent(EventStatus.SCREENING, EventInvestigationStatus.ONGOING, "Test event title", "Test description", officer, (e) -> {
				e.getEventLocation().setRegion(rdcf.region);
				e.getEventLocation().setDistrict(rdcf.district);

				SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
				originInfo.setSenderName("Test Name");
				originInfo.setSenderEmail("test@email.com");
				originInfo.setOrganizationId(DEFAULT_SERVER_ID);
				originInfo.setOwnershipHandedOver(true);

				e.setSormasToSormasOriginInfo(originInfo);
			});

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(event.getSormasToSormasOriginInfo());
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		PersonDto person = creator.createPerson();
		EventParticipantDto eventParticipant = creator.createEventParticipant(
			event.toReference(),
			person,
			"Involved",
			officer,
			(p) -> p.setSormasToSormasOriginInfo(event.getSormasToSormasOriginInfo()),
			null);
		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setHandOverOwnership(true);
		options.setWithEventParticipants(true);
		options.setComment("Test comment");

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(new ShareRequestAcceptData(null, null)));

		getSormasToSormasEventFacade().share(Collections.singletonList(event.getUuid()), options);

		// event ownership should be lost
		EventDto sharedEvent = getEventFacade().getEventByUuid(event.getUuid(), false);
		assertThat(sharedEvent.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		// sample ownership should be lost
		EventParticipantDto sharedEventParticipant = getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid());
		assertThat(sharedEventParticipant.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));
	}

	@Test
	public void testSaveReturnedEvent() throws SormasToSormasException, SormasToSormasValidationException {
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		EventDto event = creator.createEvent(officer);
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), creator.createPerson(), officer);
		EventParticipantDto newEventParticipant = createEventParticipantDto(event.toReference(), UserDto.build().toReference(), rdcf);

		FacilityDto lab = creator.createFacility("Test Lab", rdcf.region, rdcf.district, null, FacilityType.LABORATORY);
		SampleDto newSample = createSample(newEventParticipant.toReference(), officer, lab.toReference());

		User officerUser = getUserService().getByReferenceDto(officer);
		ShareRequestInfo shareRequestInfo =
			createShareRequestInfo(officerUser, DEFAULT_SERVER_ID, true, i -> i.setEvent(getEventService().getByReferenceDto(event.toReference())));
		shareRequestInfo.setWithEventParticipants(true);
		shareRequestInfo.getShares()
			.add(
				createShareInfo(
					DEFAULT_SERVER_ID,
					true,
					i -> i.setEventParticipant(getEventParticipantService().getByReferenceDto(eventParticipant.toReference()))));
		getShareRequestInfoService().persist(shareRequestInfo);

		event.setEventDesc("Test updated description");
		eventParticipant.getPerson().setBirthName("Test birth name");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(event.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		event.setChangeDate(calendar.getTime());

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, true));
		shareData.setEvents(Collections.singletonList(new SormasToSormasEventDto(event)));
		shareData.setEventParticipants(
			Arrays.asList(new SormasToSormasEventParticipantDto(eventParticipant), new SormasToSormasEventParticipantDto(newEventParticipant)));
		shareData.setSamples(Collections.singletonList(new SormasToSormasSampleDto(newSample, Collections.emptyList(), Collections.emptyList())));

		SormasToSormasEncryptedDataDto encryptedData = encryptShareData(shareData);

		getSormasToSormasEventFacade().saveSharedEntities(encryptedData);

		EventDto returnedEvent = getEventFacade().getEventByUuid(event.getUuid(), false);
		assertThat(returnedEvent.getEventDesc(), is("Test updated description"));
		assertThat(returnedEvent.getReportingUser(), is(officer));

		List<SormasToSormasShareInfoDto> eventShares =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().event(event.toReference()), 0, 100);
		assertThat(eventShares.get(0).isOwnershipHandedOver(), is(false));

		assertThat(
			getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid()).getPerson().getBirthName(),
			is("Test birth name"));
		List<SormasToSormasShareInfoDto> eventParticipantShares = getSormasToSormasShareInfoFacade()
			.getIndexList(new SormasToSormasShareInfoCriteria().eventParticipant(eventParticipant.toReference()), 0, 100);
		assertThat(eventParticipantShares.get(0).isOwnershipHandedOver(), is(false));

		EventParticipantDto returnedNewEventParticipant = getEventParticipantFacade().getEventParticipantByUuid(newEventParticipant.getUuid());
		assertThat(returnedNewEventParticipant.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));

		SampleDto returnedNewSample = getSampleFacade().getSampleByUuid(newSample.getUuid());
		assertThat(returnedNewSample.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(true));
	}

	@Test
	public void testSyncEvent() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		EventDto event =
			creator.createEvent(EventStatus.SCREENING, EventInvestigationStatus.ONGOING, "Test event title", "Test description", officer, e -> {
				e.getEventLocation().setRegion(rdcf.region);
				e.getEventLocation().setDistrict(rdcf.district);

				SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
				originInfo.setSenderName("Test Name");
				originInfo.setSenderEmail("test@email.com");
				originInfo.setOrganizationId(DEFAULT_SERVER_ID);
				originInfo.setOwnershipHandedOver(true);
				originInfo.setWithEventParticipants(true);

				e.setSormasToSormasOriginInfo(originInfo);
			});

		PersonDto person = creator.createPerson();
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, "Involved", officer, ep -> {
			SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
			originInfo.setSenderName("Test Name");
			originInfo.setSenderEmail("test@email.com");
			originInfo.setOrganizationId(DEFAULT_SERVER_ID);
			originInfo.setOwnershipHandedOver(true);
			originInfo.setWithEventParticipants(true);

			ep.setSormasToSormasOriginInfo(originInfo);
		}, null);

		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
			getUserService().getByUuid(officer.getUuid()),
			SECOND_SERVER_ID,
			false,
			ShareRequestStatus.ACCEPTED,
			i -> i.setEvent(getEventService().getByUuid(event.getUuid())));
		shareRequestInfo.setWithEventParticipants(true);
		shareRequestInfo.getShares()
			.add(
				createShareInfo(
					SECOND_SERVER_ID,
					false,
					i -> i.setEventParticipant(getEventParticipantService().getByUuid(eventParticipant.getUuid()))));
		getShareRequestInfoService().persist(shareRequestInfo);

		EventParticipantDto newEventParticipant = creator.createEventParticipant(event.toReference(), person, "Involved", officer);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.contains("/events/sync"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.then(invocation -> {
				SyncDataDto syncData = invocation.getArgument(2);

				assertThat(syncData.getCriteria().getEntityUuid(), is(event.getUuid()));
				assertThat(syncData.getCriteria().getExceptedOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(syncData.getCriteria().isForwardOnly(), is(false));

				assertThat(syncData.getShareData().getEvents().get(0).getEntity().getUuid(), is(event.getUuid()));
				assertThat(syncData.getShareData().getEventParticipants(), hasSize(1)); // new event participant should not be shared

				return Response.noContent().build();
			});

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(SECOND_SERVER_ID), ArgumentMatchers.contains("/events/sync"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.then(invocation -> {
				SyncDataDto syncData = invocation.getArgument(2);

				assertThat(syncData.getCriteria().getEntityUuid(), is(event.getUuid()));
				assertThat(syncData.getCriteria().getExceptedOrganizationId(), is(nullValue()));
				assertThat(syncData.getCriteria().isForwardOnly(), is(true));

				assertThat(syncData.getShareData().getEvents().get(0).getEntity().getUuid(), is(event.getUuid()));
				// the new event participant should not be shared
				assertThat(syncData.getShareData().getEventParticipants(), hasSize(1));
				assertThat(syncData.getShareData().getEventParticipants().get(0).getEntity().getUuid(), is(eventParticipant.getUuid()));

				return Response.noContent().build();
			});

		getSormasToSormasEventFacade().syncShares(new ShareTreeCriteria(event.getUuid(), null, false));

		// new event participant should have share info with ownership handed over
		List<SormasToSormasShareInfoDto> newEventParticipantShareInfoList = getSormasToSormasShareInfoFacade()
			.getIndexList(new SormasToSormasShareInfoCriteria().eventParticipant(newEventParticipant.toReference()), 0, 100);
		assertThat(newEventParticipantShareInfoList, hasSize(0));
	}

	@Test
	public void testSaveSyncedEvent() throws SormasToSormasException, SormasToSormasValidationException {
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		EventDto event =
			creator.createEvent(EventStatus.SCREENING, EventInvestigationStatus.ONGOING, "Test event title", "Test description", officer, e -> {
				SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
				originInfo.setSenderName("Test Name");
				originInfo.setSenderEmail("test@email.com");
				originInfo.setOrganizationId(DEFAULT_SERVER_ID);
				originInfo.setOwnershipHandedOver(false);

				e.setSormasToSormasOriginInfo(originInfo);
			});

		EventParticipantDto eventParticipant = creator.createEventParticipant(
			event.toReference(),
			creator.createPerson(),
			"Involved",
			officer,
			(ep) -> ep.setSormasToSormasOriginInfo(event.getSormasToSormasOriginInfo()),
			null);

		EventParticipantDto newEventParticipant = createEventParticipantDto(event.toReference(), UserDto.build().toReference(), rdcf);

		event.setEventDesc("Test updated description");
		eventParticipant.getPerson().setBirthName("Test birth name");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(event.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		event.setChangeDate(calendar.getTime());

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false));
		shareData.setEvents(Collections.singletonList(new SormasToSormasEventDto(event)));
		shareData.setEventParticipants(
			Arrays.asList(new SormasToSormasEventParticipantDto(eventParticipant), new SormasToSormasEventParticipantDto(newEventParticipant)));

		SormasToSormasEncryptedDataDto encryptedData =
			encryptShareData(new SyncDataDto(shareData, new ShareTreeCriteria(event.getUuid(), null, false)));

		getSormasToSormasEventFacade().saveSyncedEntity(encryptedData);

		EventDto syncedEvent = getEventFacade().getEventByUuid(event.getUuid(), false);
		assertThat(syncedEvent.getEventDesc(), is("Test updated description"));
		assertThat(syncedEvent.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		EventParticipantDto syncedEventParticipant = getEventParticipantFacade().getEventParticipantByUuid(eventParticipant.getUuid());
		assertThat(syncedEventParticipant.getPerson().getBirthName(), is("Test birth name"));
		assertThat(syncedEventParticipant.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));

		EventParticipantDto returnedNewEventParticipant = getEventParticipantFacade().getEventParticipantByUuid(newEventParticipant.getUuid());
		assertThat(returnedNewEventParticipant.getSormasToSormasOriginInfo().isOwnershipHandedOver(), is(false));
	}

	@Test
	public void testSyncRecursively() throws SormasToSormasException, SormasToSormasValidationException {
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();

		EventDto event =
			creator.createEvent(EventStatus.SCREENING, EventInvestigationStatus.ONGOING, "Test event title", "Test description", officer, e -> {
				SormasToSormasOriginInfoDto originInfo = new SormasToSormasOriginInfoDto();
				originInfo.setSenderName("Test Name");
				originInfo.setSenderEmail("test@email.com");
				originInfo.setOrganizationId(DEFAULT_SERVER_ID);
				originInfo.setWithEventParticipants(true);
				originInfo.setOwnershipHandedOver(false);

				e.setSormasToSormasOriginInfo(originInfo);
			});

		EventParticipantDto eventParticipant = creator.createEventParticipant(
			event.toReference(),
			creator.createPerson(),
			"Involved",
			officer,
			(ep) -> ep.setSormasToSormasOriginInfo(event.getSormasToSormasOriginInfo()),
			null);

		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
			getUserService().getByUuid(officer.getUuid()),
			SECOND_SERVER_ID,
			false,
			ShareRequestStatus.ACCEPTED,
			i -> i.setEvent(getEventService().getByUuid(event.getUuid())));
		shareRequestInfo.setWithEventParticipants(true);
		shareRequestInfo.getShares()
			.add(
				createShareInfo(
					SECOND_SERVER_ID,
					false,
					i -> i.setEventParticipant(getEventParticipantService().getByUuid(eventParticipant.getUuid()))));
		getShareRequestInfoService().persist(shareRequestInfo);

		EventParticipantDto newEventParticipant = createEventParticipantDto(event.toReference(), UserDto.build().toReference(), rdcf);

		event.setEventDesc("Test updated description");
		eventParticipant.getPerson().setBirthName("Test birth name");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(event.getChangeDate());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		event.setChangeDate(calendar.getTime());

		SormasToSormasDto shareData = new SormasToSormasDto();
		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfo(DEFAULT_SERVER_ID, false);
		originInfo.setWithEventParticipants(true);
		shareData.setOriginInfo(originInfo);
		shareData.setEvents(Collections.singletonList(new SormasToSormasEventDto(event)));
		shareData.setEventParticipants(
			Arrays.asList(new SormasToSormasEventParticipantDto(eventParticipant), new SormasToSormasEventParticipantDto(newEventParticipant)));

		SormasToSormasEncryptedDataDto encryptedData =
			encryptShareData(new SyncDataDto(shareData, new ShareTreeCriteria(event.getUuid(), null, false)));

		Mockito
			.when(
				MockProducer.getManagedScheduledExecutorService()
					.schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
			.then(invocation -> {
				((Runnable) invocation.getArgument(0)).run();

				Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
					.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.contains("/events/sync"), ArgumentMatchers.any(), ArgumentMatchers.any());
				Mockito.verify(MockProducer.getSormasToSormasClient(), Mockito.times(1))
					.post(eq(SECOND_SERVER_ID), ArgumentMatchers.contains("/events/sync"), ArgumentMatchers.any(), ArgumentMatchers.any());

				return null;
			});

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(DEFAULT_SERVER_ID), ArgumentMatchers.contains("/events/sync"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.then(invocation -> {
				SyncDataDto syncData = invocation.getArgument(2);

				assertThat(syncData.getCriteria().getEntityUuid(), is(event.getUuid()));
				assertThat(syncData.getCriteria().getExceptedOrganizationId(), is(SECOND_SERVER_ID));
				assertThat(syncData.getCriteria().isForwardOnly(), is(false));

				assertThat(syncData.getShareData().getEvents().get(0).getEntity().getUuid(), is(event.getUuid()));
				assertThat(syncData.getShareData().getEventParticipants(), hasSize(2));

				return Response.noContent().build();
			});

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(eq(SECOND_SERVER_ID), ArgumentMatchers.contains("/events/sync"), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.then(invocation -> {
				SyncDataDto syncData = invocation.getArgument(2);

				assertThat(syncData.getCriteria().getEntityUuid(), is(event.getUuid()));
				assertThat(syncData.getCriteria().getExceptedOrganizationId(), is(nullValue()));
				assertThat(syncData.getCriteria().isForwardOnly(), is(true));

				assertThat(syncData.getShareData().getEvents().get(0).getEntity().getUuid(), is(event.getUuid()));
				assertThat(syncData.getShareData().getEventParticipants(), hasSize(1));

				return Response.noContent().build();
			});

		getSormasToSormasEventFacade().saveSyncedEntity(encryptedData);
	}

	private EventDto createEventDto(TestDataCreator.RDCF rdcf) {
		EventDto eventDto = EventDto.build();

		eventDto.setEventTitle("Test event title");
		eventDto.getEventLocation().setRegion(rdcf.region);
		eventDto.getEventLocation().setDistrict(rdcf.district);

		return eventDto;
	}

	private EventParticipantDto createEventParticipantDto(EventReferenceDto event, UserReferenceDto user, TestDataCreator.RDCF rdcf) {
		EventParticipantDto participantDto = EventParticipantDto.build(event, user);
		participantDto.setPerson(createPersonDto(rdcf));

		participantDto.setReportingUser(user);
		participantDto.setRegion(rdcf.region);
		participantDto.setDistrict(rdcf.district);

		return participantDto;
	}

	private SampleDto createSample(EventParticipantReferenceDto eventParticipant, UserReferenceDto reportingUser, FacilityReferenceDto lab) {
		SampleDto sample = SampleDto.build(reportingUser, eventParticipant);
		sample.setSampleMaterial(SampleMaterial.BLOOD);
		sample.setSamplePurpose(SamplePurpose.INTERNAL);
		sample.setLab(lab);
		sample.setSampleDateTime(new Date());

		return sample;
	}

}

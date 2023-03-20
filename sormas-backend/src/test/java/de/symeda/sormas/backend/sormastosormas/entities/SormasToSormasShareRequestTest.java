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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sormastosormas.DuplicateResult;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.entities.DuplicateResultType;
import de.symeda.sormas.api.sormastosormas.entities.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.entities.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.share.outgoing.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestData;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;

public class SormasToSormasShareRequestTest extends SormasToSormasTest {

	private UserReferenceDto officer;

	@Override
	public void init() {
		super.init();

		officer = useSurveillanceOfficerLogin(rdcf).toReference();
	}

	@AfterEach
	public void teardown() {
		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, false, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.SORMAS_TO_SORMAS_ACCEPT_REJECT);
	}

	protected boolean isAcceptRejectFeatureEnabled() {
		return true;
	}

	@Test
	public void testSendCaseShareRequest() throws SormasToSormasException {

		PersonDto person = creator.createPerson("John", "Doe", Sex.MALE, 1964, 4, 12);
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setDisease(Disease.CORONAVIRUS);
			dto.setCaseClassification(CaseClassification.SUSPECT);
			dto.setOutcome(CaseOutcome.NO_OUTCOME);
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
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases/request"));

				ShareRequestData postBody = invocation.getArgument(2, ShareRequestData.class);
				assertThat(postBody.getPreviews().getCases(), hasSize(1));

				SormasToSormasCasePreview casePreview = postBody.getPreviews().getCases().get(0);

				assertThat(casePreview.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(casePreview.getPerson().getLastName(), is(person.getLastName()));
				assertThat(casePreview.getPerson().getSex(), is(person.getSex()));
				assertThat(casePreview.getPerson().getBirthdateDD(), is(person.getBirthdateDD()));
				assertThat(casePreview.getPerson().getBirthdateMM(), is(person.getBirthdateMM()));
				assertThat(casePreview.getPerson().getBirthdateYYYY(), is(person.getBirthdateYYYY()));

				assertThat(casePreview.getUuid(), is(caze.getUuid()));
				assertThat(casePreview.getDisease(), is(caze.getDisease()));
				assertThat(casePreview.getCaseClassification(), is(caze.getCaseClassification()));
				assertThat(casePreview.getOutcome(), is(caze.getOutcome()));

				// share information
				assertThat(postBody.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(postBody.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(postBody.getOriginInfo().getComment(), is("Test comment"));

				return Response.noContent().build();
			});

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
		assertThat(shareInfoList.get(0).getRequestStatus(), is(ShareRequestStatus.PENDING));
	}

	@Test
	public void testResendCaseShareRequest() throws SormasToSormasException {

		PersonDto person = creator.createPerson("John", "Doe", Sex.MALE, 1964, 4, 12);
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setDisease(Disease.CORONAVIRUS);
			dto.setCaseClassification(CaseClassification.SUSPECT);
			dto.setOutcome(CaseOutcome.NO_OUTCOME);
		});

		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
			ShareRequestDataType.CASE,
			getUserService().getByUuid(officer.getUuid()),
			SECOND_SERVER_ID,
			false,
			ShareRequestStatus.ACCEPTED,
			i -> i.setCaze(getCaseService().getByUuid(caze.getUuid())));
		getShareRequestInfoService().persist(shareRequestInfo);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases/request"));

				ShareRequestData postBody = invocation.getArgument(2, ShareRequestData.class);
				assertThat(postBody.getPreviews().getCases(), hasSize(1));

				SormasToSormasCasePreview casePreview = postBody.getPreviews().getCases().get(0);

				assertThat(casePreview.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(casePreview.getPerson().getLastName(), is(person.getLastName()));
				assertThat(casePreview.getPerson().getSex(), is(person.getSex()));
				assertThat(casePreview.getPerson().getBirthdateDD(), is(person.getBirthdateDD()));
				assertThat(casePreview.getPerson().getBirthdateMM(), is(person.getBirthdateMM()));
				assertThat(casePreview.getPerson().getBirthdateYYYY(), is(person.getBirthdateYYYY()));

				assertThat(casePreview.getUuid(), is(caze.getUuid()));
				assertThat(casePreview.getDisease(), is(caze.getDisease()));
				assertThat(casePreview.getCaseClassification(), is(caze.getCaseClassification()));
				assertThat(casePreview.getOutcome(), is(caze.getOutcome()));

				// share information
				assertThat(postBody.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(postBody.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(postBody.getOriginInfo().isOwnershipHandedOver(), is(true));
				assertThat(postBody.getOriginInfo().getComment(), is("New comment"));

				assertThat(postBody.getRequestUuid(), not(is(shareRequestInfo.getUuid())));

				return Response.noContent().build();
			});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setHandOverOwnership(true);
		options.setComment("New comment");

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF"));
		assertThat(shareInfoList.get(0).isOwnershipHandedOver(), is(true));
		assertThat(shareInfoList.get(0).getComment(), is("New comment"));
		assertThat(shareInfoList.get(0).getRequestStatus(), is(ShareRequestStatus.PENDING));
	}

	@Test
	public void testResendCaseShareRequestWithoutResponodingToFirstOne() throws SormasToSormasException {

		PersonDto person = creator.createPerson("John", "Doe", Sex.MALE, 1964, 4, 12);
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setDisease(Disease.CORONAVIRUS);
			dto.setCaseClassification(CaseClassification.SUSPECT);
			dto.setOutcome(CaseOutcome.NO_OUTCOME);
		});

		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
			ShareRequestDataType.CASE,
			getUserService().getByUuid(officer.getUuid()),
			SECOND_SERVER_ID,
			false,
			ShareRequestStatus.PENDING,
			i -> i.setCaze(getCaseService().getByUuid(caze.getUuid())));
		getShareRequestInfoService().persist(shareRequestInfo);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> Response.noContent().build());

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setHandOverOwnership(true);
		options.setComment("New comment");

		assertThrows(SormasToSormasException.class, () -> getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options));

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList.size(), is(1));
	}

	@Test
	public void testShareWithModifiedOptions() throws SormasToSormasException {

		PersonDto person = creator.createPerson("John", "Doe", Sex.MALE, 1964, 4, 12);
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setDisease(Disease.CORONAVIRUS);
			dto.setCaseClassification(CaseClassification.SUSPECT);
			dto.setOutcome(CaseOutcome.NO_OUTCOME);
		});

		ShareRequestInfo shareRequestInfo = createShareRequestInfo(
			ShareRequestDataType.CASE,
			getUserService().getByUuid(officer.getUuid()),
			SECOND_SERVER_ID,
			false,
			ShareRequestStatus.ACCEPTED,
			i -> i.setCaze(getCaseService().getByUuid(caze.getUuid())));
		getShareRequestInfoService().persist(shareRequestInfo);

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases/request"));

				ShareRequestData postBody = invocation.getArgument(2, ShareRequestData.class);
				assertThat(postBody.getPreviews().getCases(), hasSize(1));

				SormasToSormasCasePreview casePreview = postBody.getPreviews().getCases().get(0);

				assertThat(casePreview.getPerson().getFirstName(), is(person.getFirstName()));
				assertThat(casePreview.getPerson().getLastName(), is(person.getLastName()));
				assertThat(casePreview.getPerson().getSex(), is(person.getSex()));
				assertThat(casePreview.getPerson().getBirthdateDD(), is(person.getBirthdateDD()));
				assertThat(casePreview.getPerson().getBirthdateMM(), is(person.getBirthdateMM()));
				assertThat(casePreview.getPerson().getBirthdateYYYY(), is(person.getBirthdateYYYY()));

				assertThat(casePreview.getUuid(), is(caze.getUuid()));
				assertThat(casePreview.getDisease(), is(caze.getDisease()));
				assertThat(casePreview.getCaseClassification(), is(caze.getCaseClassification()));
				assertThat(casePreview.getOutcome(), is(caze.getOutcome()));

				// share information
				assertThat(postBody.getOriginInfo().getOrganizationId(), is(DEFAULT_SERVER_ID));
				assertThat(postBody.getOriginInfo().getSenderName(), is("Surv Off"));
				assertThat(postBody.getOriginInfo().isOwnershipHandedOver(), is(true));
				assertThat(postBody.getOriginInfo().getComment(), is("New comment"));

				assertThat(postBody.getRequestUuid(), not(is(shareRequestInfo.getUuid())));

				return Response.noContent().build();
			});

		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();
		options.setOrganization(new SormasServerDescriptor(SECOND_SERVER_ID));
		options.setHandOverOwnership(true);
		options.setComment("New comment");

		getSormasToSormasCaseFacade().share(Collections.singletonList(caze.getUuid()), options);

		List<SormasToSormasShareInfoDto> shareInfoList =
			getSormasToSormasShareInfoFacade().getIndexList(new SormasToSormasShareInfoCriteria().caze(caze.toReference()), 0, 100);

		assertThat(shareInfoList.size(), is(1));
		assertThat(shareInfoList.get(0).getTargetDescriptor().getId(), is(SECOND_SERVER_ID));
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF"));
		assertThat(shareInfoList.get(0).isOwnershipHandedOver(), is(true));
		assertThat(shareInfoList.get(0).getComment(), is("New comment"));
		assertThat(shareInfoList.get(0).getRequestStatus(), is(ShareRequestStatus.PENDING));
	}

	@Test
	public void testAcceptShareRequest() throws SormasToSormasException, SormasToSormasValidationException {

		PersonDto person = createPersonDto(rdcf);
		CaseDataDto caze = createCaseDto(rdcf, person);
		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(person, caze)));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(shareData));

		DuplicateResult duplicateResult = getSormasToSormasCaseFacade().acceptShareRequest(shareRequest.getUuid(), true);

		assertThat(duplicateResult.getType(), is(DuplicateResultType.NONE));
		assertThat(duplicateResult.getUuids(), hasSize(0));
		assertThat(getCaseFacade().getByUuid(caze.getUuid()), is(notNullValue()));
		assertThat(getSormasToSormasShareRequestFacade().getShareRequestByUuid(shareRequest.getUuid()).getStatus(), is(ShareRequestStatus.ACCEPTED));
	}

	@Test
	public void testAcceptWithCaseDuplicate() throws SormasToSormasException, SormasToSormasValidationException {

		PersonDto person = createPersonDto(rdcf);
		getPersonFacade().save(person);
		CaseDataDto caze = createCaseDto(rdcf, person);
		caze.setReportingUser(officer);
		getCaseFacade().save(caze);

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		// create similar person and case
		PersonDto sharedPerson = createPersonDto(rdcf);
		CaseDataDto sharedCaze = createCaseDto(rdcf, person);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(sharedPerson, sharedCaze)));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(shareData));

		DuplicateResult duplicateResult = getSormasToSormasCaseFacade().acceptShareRequest(shareRequest.getUuid(), true);
		assertThat(duplicateResult.getType(), is(DuplicateResultType.CASE));
		assertThat(duplicateResult.getUuids(), hasSize(1));
		assertThat(duplicateResult.getUuids(), contains(caze.getUuid()));
		assertThat(getCaseFacade().getByUuid(sharedCaze.getUuid()), is(nullValue()));
		assertThat(getSormasToSormasShareRequestFacade().getShareRequestByUuid(shareRequest.getUuid()).getStatus(), is(ShareRequestStatus.PENDING));

		duplicateResult = getSormasToSormasCaseFacade().acceptShareRequest(shareRequest.getUuid(), false);

		assertThat(duplicateResult.getType(), is(DuplicateResultType.NONE));
		assertThat(getCaseFacade().getByUuid(sharedCaze.getUuid()), is(notNullValue()));
		assertThat(getSormasToSormasShareRequestFacade().getShareRequestByUuid(shareRequest.getUuid()).getStatus(), is(ShareRequestStatus.ACCEPTED));
	}

	@Test
	public void testAcceptWithConvertedCaseDuplicate() throws SormasToSormasException, SormasToSormasValidationException {

		PersonDto person = createPersonDto(rdcf);
		getPersonFacade().save(person);
		CaseDataDto caze = createCaseDto(rdcf, person);
		caze.setReportingUser(officer);
		getCaseFacade().save(caze);

		creator.createContact(officer, person.toReference(), caze.getDisease(), c -> {
			c.setResultingCase(caze.toReference());
		});

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		// create similar person and case
		PersonDto sharedPerson = createPersonDto(rdcf);
		CaseDataDto sharedCaze = createCaseDto(rdcf, person);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(sharedPerson, sharedCaze)));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(shareData));

		DuplicateResult duplicateResult = getSormasToSormasCaseFacade().acceptShareRequest(shareRequest.getUuid(), true);
		assertThat(duplicateResult.getType(), is(DuplicateResultType.CASE_CONVERTED));
		assertThat(duplicateResult.getUuids(), hasSize(1));
		assertThat(duplicateResult.getUuids(), contains(caze.getUuid()));
	}

	@Test
	public void testAcceptCaseHavingSimilarContact() throws SormasToSormasException, SormasToSormasValidationException {

		PersonDto person = createPersonDto(rdcf);
		getPersonFacade().save(person);
		ContactDto contact = createContactDto(rdcf, person);
		contact.setReportingUser(officer);
		getContactFacade().save(contact);

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		// create similar person and case
		PersonDto sharedPerson = createPersonDto(rdcf);
		CaseDataDto sharedCaze = createCaseDto(rdcf, person);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(sharedPerson, sharedCaze)));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(shareData));

		DuplicateResult duplicateResult = getSormasToSormasCaseFacade().acceptShareRequest(shareRequest.getUuid(), true);
		assertThat(duplicateResult.getType(), is(DuplicateResultType.CONTACT_TO_CASE));
		assertThat(duplicateResult.getUuids(), hasSize(1));
		assertThat(duplicateResult.getUuids(), contains(contact.getUuid()));
	}

	@Test
	public void testAcceptWithContactDuplicate() throws SormasToSormasException, SormasToSormasValidationException {

		PersonDto person = createPersonDto(rdcf);
		getPersonFacade().save(person);
		ContactDto contact = createContactDto(rdcf, person);
		contact.setReportingUser(officer);
		getContactFacade().save(contact);

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		// create similar person and contact
		PersonDto sharedPerson = createPersonDto(rdcf);
		ContactDto sharedContact = createContactDto(rdcf, person);
		shareData.setContacts(Collections.singletonList(new SormasToSormasContactDto(sharedPerson, sharedContact)));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(shareData));

		DuplicateResult duplicateResult = getSormasToSormasContactFacade().acceptShareRequest(shareRequest.getUuid(), true);
		assertThat(duplicateResult.getType(), is(DuplicateResultType.CONTACT));
		assertThat(duplicateResult.getUuids(), hasSize(1));
		assertThat(duplicateResult.getUuids(), contains(contact.getUuid()));
		assertThat(getContactFacade().getByUuid(sharedContact.getUuid()), is(nullValue()));
		assertThat(getSormasToSormasShareRequestFacade().getShareRequestByUuid(shareRequest.getUuid()).getStatus(), is(ShareRequestStatus.PENDING));

		duplicateResult = getSormasToSormasCaseFacade().acceptShareRequest(shareRequest.getUuid(), false);

		assertThat(duplicateResult.getType(), is(DuplicateResultType.NONE));
		assertThat(getContactFacade().getByUuid(sharedContact.getUuid()), is(notNullValue()));
		assertThat(getSormasToSormasShareRequestFacade().getShareRequestByUuid(shareRequest.getUuid()).getStatus(), is(ShareRequestStatus.ACCEPTED));
	}

	@Test
	public void testAcceptWithConvertedContactDuplicate() throws SormasToSormasException, SormasToSormasValidationException {

		PersonDto person = createPersonDto(rdcf);
		getPersonFacade().save(person);

		CaseDataDto convertedCaze = creator.createCase(officer, person.toReference(), rdcf);

		ContactDto contact = createContactDto(rdcf, person);
		contact.setReportingUser(officer);
		contact.setResultingCase(convertedCaze.toReference());
		getContactFacade().save(contact);

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		// create similar person and contact
		PersonDto sharedPerson = createPersonDto(rdcf);
		ContactDto sharedContact = createContactDto(rdcf, person);
		shareData.setContacts(Collections.singletonList(new SormasToSormasContactDto(sharedPerson, sharedContact)));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(shareData));

		DuplicateResult duplicateResult = getSormasToSormasContactFacade().acceptShareRequest(shareRequest.getUuid(), true);
		assertThat(duplicateResult.getType(), is(DuplicateResultType.CONTACT_CONVERTED));
		assertThat(duplicateResult.getUuids(), hasSize(1));
		assertThat(duplicateResult.getUuids(), contains(contact.getUuid()));
	}

	@Test
	public void testAcceptWithContactHavingSimilarCase() throws SormasToSormasException, SormasToSormasValidationException {

		PersonDto person = createPersonDto(rdcf);
		getPersonFacade().save(person);

		CaseDataDto caze = creator.createCase(officer, person.toReference(), rdcf, c -> {
			c.setDisease(Disease.CORONAVIRUS);
		});

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		// create similar person and contact
		PersonDto sharedPerson = createPersonDto(rdcf);
		ContactDto sharedContact = createContactDto(rdcf, person);
		shareData.setContacts(Collections.singletonList(new SormasToSormasContactDto(sharedPerson, sharedContact)));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(shareData));

		DuplicateResult duplicateResult = getSormasToSormasContactFacade().acceptShareRequest(shareRequest.getUuid(), true);
		assertThat(duplicateResult.getType(), is(DuplicateResultType.CASE_TO_CONTACT));
		assertThat(duplicateResult.getUuids(), hasSize(1));
		assertThat(duplicateResult.getUuids(), contains(caze.getUuid()));
	}

	@Test
	public void testAcceptCaseWithDuplicatePerson() throws SormasToSormasException, SormasToSormasValidationException {

		PersonDto person = createPersonDto(rdcf);
		getPersonFacade().save(person);

		creator.createCase(officer, person.toReference(), rdcf, c -> {
			c.setDisease(Disease.EVD);
		});

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		// create similar person and contact
		PersonDto sharedPerson = createPersonDto(rdcf);
		CaseDataDto sharedCase = createCaseDto(rdcf, person);
		shareData.setCases(Collections.singletonList(new SormasToSormasCaseDto(sharedPerson, sharedCase)));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(shareData));

		DuplicateResult duplicateResult = getSormasToSormasContactFacade().acceptShareRequest(shareRequest.getUuid(), true);
		assertThat(duplicateResult.getType(), is(DuplicateResultType.PERSON_ONLY));
		assertThat(duplicateResult.getUuids(), hasSize(1));
		assertThat(duplicateResult.getUuids(), contains(person.getUuid()));
	}

	@Test
	public void testAcceptContactWithDuplicatePerson() throws SormasToSormasException, SormasToSormasValidationException {

		PersonDto person = createPersonDto(rdcf);
		getPersonFacade().save(person);

		creator.createCase(officer, person.toReference(), rdcf, c -> {
			c.setDisease(Disease.EVD);
		});

		SormasToSormasOriginInfoDto originInfo = createSormasToSormasOriginInfoDto(DEFAULT_SERVER_ID, true);

		SormasToSormasShareRequestDto shareRequest = new SormasToSormasShareRequestDto();
		shareRequest.setUuid(DataHelper.createUuid());
		shareRequest.setOriginInfo(originInfo);
		shareRequest.setStatus(ShareRequestStatus.PENDING);
		getSormasToSormasShareRequestFacade().saveShareRequest(shareRequest);

		SormasToSormasDto shareData = new SormasToSormasDto();
		shareData.setOriginInfo(originInfo);
		// create similar person and contact
		PersonDto sharedPerson = createPersonDto(rdcf);
		ContactDto sharedContact = createContactDto(rdcf, person);
		shareData.setContacts(Collections.singletonList(new SormasToSormasContactDto(sharedPerson, sharedContact)));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.post(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> encryptShareData(shareData));

		DuplicateResult duplicateResult = getSormasToSormasContactFacade().acceptShareRequest(shareRequest.getUuid(), true);
		assertThat(duplicateResult.getType(), is(DuplicateResultType.PERSON_ONLY));
		assertThat(duplicateResult.getUuids(), hasSize(1));
		assertThat(duplicateResult.getUuids(), contains(person.getUuid()));
	}

	private ContactDto createContactDto(TestDataCreator.RDCF rdcf, PersonDto person) {
		ContactDto contactDto = ContactDto.build();

		contactDto.setDisease(Disease.CORONAVIRUS);
		contactDto.setRegion(rdcf.region);
		contactDto.setDistrict(rdcf.district);

		contactDto.setPerson(person.toReference());

		return contactDto;
	}
}

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

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoCriteria;
import de.symeda.sormas.api.sormastosormas.shareinfo.SormasToSormasShareInfoDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasTest;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestData;
import de.symeda.sormas.backend.user.User;

@RunWith(MockitoJUnitRunner.class)
public class SormasToSormasShareRequestTest extends SormasToSormasTest {

	@After
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
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson("John", "Doe", Sex.MALE, 1964, 4, 12);
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
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
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).getComment(), is("Test comment"));
		assertThat(shareInfoList.get(0).getRequestStatus(), is(ShareRequestStatus.PENDING));
	}

	@Test
	public void testResendCaseShareRequest() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson("John", "Doe", Sex.MALE, 1964, 4, 12);
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setDisease(Disease.CORONAVIRUS);
			dto.setCaseClassification(CaseClassification.SUSPECT);
			dto.setOutcome(CaseOutcome.NO_OUTCOME);
		});
		User officerUser = getUserService().getByReferenceDto(officer);
		getShareRequestInfoService().persist(createShareRequestInfo(officerUser, SECOND_SERVER_ID, false, i -> {
			i.setCaze(getCaseService().getByReferenceDto(caze.toReference()));
		}));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
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

				assertThat(postBody.getRequestUuid(), is("test-uuid"));

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
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).isOwnershipHandedOver(), is(true));
		assertThat(shareInfoList.get(0).getComment(), is("New comment"));
		assertThat(shareInfoList.get(0).getRequestStatus(), is(ShareRequestStatus.PENDING));
	}

	@Test
	public void testShareWithModifiedOptions() throws SormasToSormasException {
		useSurveillanceOfficerLogin(rdcf);

		PersonDto person = creator.createPerson("John", "Doe", Sex.MALE, 1964, 4, 12);
		UserReferenceDto officer = creator.createUser(rdcf, creator.getUserRoleDtoMap().get(DefaultUserRole.SURVEILLANCE_OFFICER)).toReference();
		CaseDataDto caze = creator.createCase(officer, rdcf, dto -> {
			dto.setPerson(person.toReference());
			dto.setDisease(Disease.CORONAVIRUS);
			dto.setCaseClassification(CaseClassification.SUSPECT);
			dto.setOutcome(CaseOutcome.NO_OUTCOME);
		});
		User officerUser = getUserService().getByReferenceDto(officer);
		getShareRequestInfoService().persist(createShareRequestInfo(officerUser, SECOND_SERVER_ID, false, i -> {
			i.setCaze(getCaseService().getByReferenceDto(caze.toReference()));
		}));

		Mockito
			.when(
				MockProducer.getSormasToSormasClient()
					.put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				assertThat(invocation.getArgument(0, String.class), is(SECOND_SERVER_ID));
				assertThat(invocation.getArgument(1, String.class), is("/sormasToSormas/cases/sync"));

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

				assertThat(postBody.getRequestUuid(), is("test-uuid"));

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
		assertThat(shareInfoList.get(0).getSender().getCaption(), is("Surv OFF - Surveillance Officer"));
		assertThat(shareInfoList.get(0).isOwnershipHandedOver(), is(true));
		assertThat(shareInfoList.get(0).getComment(), is("New comment"));
		assertThat(shareInfoList.get(0).getRequestStatus(), is(ShareRequestStatus.PENDING));
	}
}

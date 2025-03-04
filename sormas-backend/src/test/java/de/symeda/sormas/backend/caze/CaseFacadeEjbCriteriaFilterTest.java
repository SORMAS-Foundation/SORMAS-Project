/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.caze;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.SurveyResponseStatus;
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.api.survey.SurveyReferenceDto;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class CaseFacadeEjbCriteriaFilterTest extends AbstractBeanTest {

	@Test
	public void testSurveyFilters() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, DefaultUserRole.SURVEILLANCE_OFFICER);
		CaseDataDto caze1 = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf);
		CaseDataDto caze2 = creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf);
		// case with no survey
		creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf);

		SurveyDto survey1 = creator.createSurvey("Test Survey", caze1.getDisease());
		SurveyDto survey2 = creator.createSurvey("Test Survey", caze2.getDisease());

		Date token1Case1Date = DateHelper.parseDate("2025-02-10", new SimpleDateFormat("yyyy-MM-dd"));
		SurveyTokenDto token1Case1 = createSurveyToken(survey1.toReference(), "token1", caze1.toReference(), token1Case1Date, false);
		Date token1Case2Date = DateHelper.parseDate("2025-02-15", new SimpleDateFormat("yyyy-MM-dd"));
		SurveyTokenDto token1Case2 = createSurveyToken(survey1.toReference(), "token2", caze2.toReference(), token1Case2Date, true);
		Date token2Case1Date = DateHelper.parseDate("2025-02-12", new SimpleDateFormat("yyyy-MM-dd"));
		SurveyTokenDto token2Case1 = createSurveyToken(survey2.toReference(), "token1", caze1.toReference(), token2Case1Date, true);
		Date token2Case2Date = DateHelper.parseDate("2025-02-17", new SimpleDateFormat("yyyy-MM-dd"));
		SurveyTokenDto token2Case2 = createSurveyToken(survey2.toReference(), "token2", caze2.toReference(), token2Case2Date, false);

		assertThat(
			getCaseFacade().getIndexList(createSurveyRangeCriteria(null, DateHelper.subtractDays(token1Case1Date, 1)), null, null, null),
			hasSize(0));
		assertThat(
			getCaseFacade().getIndexList(createSurveyRangeCriteria(DateHelper.subtractDays(token1Case1Date, 1), null), null, null, null),
			hasSize(2));

		List<CaseIndexDto> case1Only = getCaseFacade().getIndexList(createSurveyRangeCriteria(token1Case1Date, token2Case1Date), null, null, null);
		assertThat(case1Only, hasSize(1));
		assertThat(case1Only.get(0).getUuid(), is(caze1.getUuid()));

		List<CaseIndexDto> case2Only =
			getCaseFacade().getIndexList(createSurveyRangeCriteria(DateHelper.addDays(token1Case2Date, 1), token2Case2Date), null, null, null);
		assertThat(case2Only, hasSize(1));
		assertThat(case2Only.get(0).getUuid(), is(caze2.getUuid()));

		CaseCriteria surveyCriteria = new CaseCriteria();
		surveyCriteria.setSurvey(survey1.toReference());
		List<CaseIndexDto> survey1Cases = getCaseFacade().getIndexList(surveyCriteria, null, null, null);
		assertThat(survey1Cases, hasSize(2));
		assertThat(
			survey1Cases.stream().map(CaseIndexDto::getUuid).collect(Collectors.toList()),
			containsInAnyOrder(caze1.getUuid(), caze2.getUuid()));

		CaseCriteria surveyWithRangeCriteria = createSurveyRangeCriteria(DateHelper.addDays(token1Case1Date, 1), null);
		surveyWithRangeCriteria.setSurvey(survey1.toReference());
		List<CaseIndexDto> survey1CasesWithRange = getCaseFacade().getIndexList(surveyWithRangeCriteria, null, null, null);
		assertThat(survey1CasesWithRange, hasSize(1));
		assertThat(survey1CasesWithRange.get(0).getUuid(), is(caze2.getUuid()));

		CaseCriteria surveyResponseReceivedCriteria = new CaseCriteria();
		surveyResponseReceivedCriteria.setSurveyResponseStatus(SurveyResponseStatus.RECEIVED);
		List<CaseIndexDto> responseReceivedCases = getCaseFacade().getIndexList(surveyResponseReceivedCriteria, null, null, null);
		assertThat(responseReceivedCases, hasSize(2));
		assertThat(responseReceivedCases.stream().map(CaseIndexDto::getUuid).collect(Collectors.toList()), containsInAnyOrder(caze1.getUuid(), caze2.getUuid()));

		CaseCriteria survey1ResponseNotReceivedCriteria = new CaseCriteria();
		survey1ResponseNotReceivedCriteria.setSurvey(survey1.toReference());
		survey1ResponseNotReceivedCriteria.setSurveyResponseStatus(SurveyResponseStatus.NOT_RECEIVED);
		List<CaseIndexDto> survey1ResponseNotReceivedCases = getCaseFacade().getIndexList(survey1ResponseNotReceivedCriteria, null, null, null);
		assertThat(survey1ResponseNotReceivedCases, hasSize(1));
		assertThat(survey1ResponseNotReceivedCases.get(0).getUuid(), is(caze1.getUuid()));
	}

	private CaseCriteria createSurveyRangeCriteria(Date from, Date to) {
		CaseCriteria criteria = new CaseCriteria();
		criteria.setSurveyAssignedFrom(from);
		criteria.setSurveyAssignedTo(to);

		return criteria;
	}

	private SurveyTokenDto createSurveyToken(SurveyReferenceDto referenceDto, String token, CaseReferenceDto caze, Date assignmentDate, boolean responseReceived) {
		SurveyTokenDto surveyToken = SurveyTokenDto.build(referenceDto);
		surveyToken.setToken(token);
		surveyToken.setCaseAssignedTo(caze);
		surveyToken.setAssignmentDate(assignmentDate);
		surveyToken.setResponseReceived(responseReceived);

		return getSurveyTokenFacade().save(surveyToken);
	}
}

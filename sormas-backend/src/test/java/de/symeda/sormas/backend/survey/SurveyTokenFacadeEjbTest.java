/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2025 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.survey;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.survey.SurveyTokenDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;

public class SurveyTokenFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetSurveyToken() {
		SurveyToken surveyToken = createSurveyToken("test-token");

		SurveyTokenDto tokenDto = getSurveyTokenFacade().getByUuid(surveyToken.getUuid());

		assertThat(tokenDto.getToken(), is("test-token"));
	}

	@Test
	public void testUpdateSurvey() {
		SurveyToken token = createSurveyToken("test-token");
		SurveyTokenDto tokenDto = getSurveyTokenFacade().getByUuid(token.getUuid());
		assertThat(tokenDto.isResponseReceived(), is(false));

		tokenDto.setResponseReceived(true);

		SurveyTokenDto updatedToken = getSurveyTokenFacade().save(tokenDto);
		assertThat(updatedToken.isResponseReceived(), is(true));

		tokenDto = getSurveyTokenFacade().getByUuid(token.getUuid());
		assertThat(tokenDto.isResponseReceived(), is(true));
	}

	private SurveyToken createSurveyToken(String token) {
		SurveyToken surveyToken = new SurveyToken();
		surveyToken.setUuid(DataHelper.createUuid());
		surveyToken.setToken("test-token");
		surveyToken.setSurvey(createSurvey("Test Survey", Disease.CORONAVIRUS));
		getSurveyTokenService().ensurePersisted(surveyToken);
		return surveyToken;
	}

	private Survey createSurvey(String name, Disease disease) {
		Survey survey = new Survey();
		survey.setUuid(DataHelper.createUuid());
		survey.setName(name);
		survey.setDisease(disease);

		getSurveyService().ensurePersisted(survey);

		return survey;
	}
}

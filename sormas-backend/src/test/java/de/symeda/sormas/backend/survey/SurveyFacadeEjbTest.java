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
import de.symeda.sormas.api.survey.SurveyDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class SurveyFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testCreateSurvey() {
		SurveyDto survey = SurveyDto.build();
		survey.setName("Test Survey");
		survey.setDisease(Disease.CORONAVIRUS);

		SurveyDto savedSurvey = getSurveyFacade().save(survey);

		assertThat(savedSurvey.getName(), is("Test Survey"));
		assertThat(savedSurvey.getDisease(), is(Disease.CORONAVIRUS));

		SurveyDto reloadedSurvey = getSurveyFacade().getByUuid(savedSurvey.getUuid());

		assertThat(reloadedSurvey.getName(), is("Test Survey"));
		assertThat(reloadedSurvey.getDisease(), is(Disease.CORONAVIRUS));
	}

	@Test
	public void testUpdateSurvey() {
		SurveyDto survey = creator.createSurvey("Test Survey", Disease.CORONAVIRUS);

		survey.setName("Updated Survey");
		survey.setDisease(Disease.EVD);
		SurveyDto savedSurvey = getSurveyFacade().save(survey);

		assertThat(savedSurvey.getName(), is("Updated Survey"));
		assertThat(savedSurvey.getDisease(), is(Disease.EVD));
	}
}

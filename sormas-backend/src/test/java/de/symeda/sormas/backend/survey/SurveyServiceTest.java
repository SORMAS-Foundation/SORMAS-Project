/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.survey.SurveyTokenCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

/**
 * Test class for testing the SurveyService.
 */
public class SurveyServiceTest extends AbstractBeanTest {

	private UserDto surveillanceOfficer;
	private TestDataCreator.RDCF rdcf;

	/**
	 * @see AbstractBeanTest#init()
	 *      Initializing the primary data for the test.
	 */
	@Override
	public void init() {
		super.init();
		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		surveillanceOfficer = creator.createSurveillanceOfficer(rdcf);
	}

	/**
	 * Test method for {@link de.symeda.sormas.backend.survey.SurveyService#findBy(de.symeda.sormas.api.survey.SurveyTokenCriteria)}.
	 * 
	 */
	@Test
	public void testSurveyTokens() {
		CaseDataDto caze = creator.createCase(surveillanceOfficer.toReference(), rdcf, (c) -> {
			c.setDisease(Disease.MALARIA);
		});
		creator.createSurveyToken(caze.toReference());
		List<SurveyToken> tokenList = getSurveyTokenService().findBy(new SurveyTokenCriteria());
		assertThat(tokenList.size(), is(greaterThan(0)));
		assertThat(tokenList.get(0).getToken(), is(equalTo("MALA01")));
	}
}

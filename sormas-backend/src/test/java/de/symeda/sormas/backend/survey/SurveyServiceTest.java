package de.symeda.sormas.backend.survey;

import static org.hamcrest.MatcherAssert.assertThat;
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

	@Override
	public void init() {
		super.init();
		rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		surveillanceOfficer = creator.createSurveillanceOfficer(rdcf);
	}

	@Test
	public void testSurveyTokens() {
		CaseDataDto caze = creator.createCase(surveillanceOfficer.toReference(), rdcf, (c) -> {
			c.setDisease(Disease.MALARIA);
		});
		creator.createSurveyToken(caze.toReference());
		List<SurveyToken> tokenList = getSurveyTokenService().findBy(new SurveyTokenCriteria());
		assertThat(tokenList.size(), is(greaterThan(0)));
	}
}

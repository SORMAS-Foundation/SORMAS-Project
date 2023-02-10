package de.symeda.sormas.app.campaign;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;

/**
 * This test is part of the android tests, to make sure
 * Spring expression languages and it's dependencies are correctly working.
 *
 * Execute this on a device with minimum android SDK version!
 */
public class CampaignFormDataFragmentUtilsTest {

	@Test
	public void handleExpression() {
		Object result = CampaignFormDataFragmentUtils.getExpressionValue(
			new SpelExpressionParser(),
			Arrays.asList(new CampaignFormDataEntry("missedChildren", 3), new CampaignFormDataEntry("teamDidNotVisit", 2)),
			"missedChildren > 2 and teamDidNotVisit >= 2");
		assertEquals(Boolean.TRUE, result);
	}
}

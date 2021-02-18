package de.symeda.sormas.backend.survnet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.symeda.sormas.api.survnet.SurvnetGatewayFacade;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

//@RunWith(MockitoJUnitRunner.class)
public class SurvnetGatewayFacadeEjbTest extends AbstractBeanTest {

  private SurvnetGatewayFacade subjectUnderTest;

  @Before
  public void setup() {
    subjectUnderTest = getSurvnetGatewayFacade();
  }

  @Test
  public void testFeatureIsEnabledWhenSurvNetUrlIsSet() {
    MockProducer.getProperties().setProperty("survnet.url", "https://www.google.com");
    assertTrue(subjectUnderTest.isFeatureEnabled());
  }

  @Test
  public void testFeatureIsDisabledWhenSurvNetUrlIsNotSet() {
    MockProducer.getProperties().setProperty("survnet.url", "");
    assertFalse(subjectUnderTest.isFeatureEnabled());
  }
}

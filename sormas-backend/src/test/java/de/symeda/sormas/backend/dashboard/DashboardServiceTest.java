package de.symeda.sormas.backend.dashboard;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Map;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolFacade;
import de.symeda.sormas.backend.MockProducer;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.event.EventFacadeEjb;

public class DashboardServiceTest extends AbstractBeanTest {

    private static final int WIREMOCK_TESTING_PORT = 8888;
    private ExternalSurveillanceToolFacade subjectUnderTest;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(WIREMOCK_TESTING_PORT), false);

    @Before
    public void setup() {
        configureExternalSurvToolUrlForWireMock();
        subjectUnderTest = getExternalSurveillanceToolGatewayFacade();
    }

    @After
    public void teardown() {
        clearExternalSurvToolUrlForWireMock();
    }

    private void configureExternalSurvToolUrlForWireMock() {
        MockProducer.getProperties().setProperty("survnet.url", String.format("http://localhost:%s", WIREMOCK_TESTING_PORT));
    }
    private void clearExternalSurvToolUrlForWireMock() {
        MockProducer.getProperties().setProperty("survnet.url", "");
    }

	@Test
	public void testGetEventCountByStatusWithArchivingAndDeletion() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		EventDto signal = creator.createEvent(user.toReference(), EventStatus.SIGNAL);
		EventDto event1 = creator.createEvent(user.toReference(), EventStatus.EVENT);
		creator.createEvent(user.toReference(), EventStatus.EVENT);
		EventDto screening = creator.createEvent(user.toReference(), EventStatus.SCREENING);
		EventDto cluster = creator.createEvent(user.toReference(), EventStatus.CLUSTER);

		DashboardService sut = getDashboardService();
		EventFacadeEjb.EventFacadeEjbLocal eventFacade = getEventFacade();

		Map<EventStatus, Long> result = sut.getEventCountByStatus(new DashboardCriteria());
		assertEquals(4, result.size());
		assertEquals(Long.valueOf(1), result.get(EventStatus.SIGNAL));
		assertEquals(Long.valueOf(2), result.get(EventStatus.EVENT));
		assertEquals(Long.valueOf(1), result.get(EventStatus.SCREENING));
		assertEquals(Long.valueOf(1), result.get(EventStatus.CLUSTER));

        stubFor(
                post(urlEqualTo("/export")).withRequestBody(containing(signal.getUuid()))
                        .withRequestBody(containing(event1.getUuid()))
                        .withRequestBody(containing(screening.getUuid()))
                        .withRequestBody(containing(cluster.getUuid()))
                        .withRequestBody(containing("eventUuids"))
                        .willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		// archive events (should not have any effect on result)
		eventFacade.archive(Arrays.asList(signal.getUuid(), event1.getUuid(), screening.getUuid(), cluster.getUuid()));

		result = sut.getEventCountByStatus(new DashboardCriteria());

		assertEquals(4, result.size());
		assertEquals(Long.valueOf(1), result.get(EventStatus.SIGNAL));
		assertEquals(Long.valueOf(2), result.get(EventStatus.EVENT));
		assertEquals(Long.valueOf(1), result.get(EventStatus.SCREENING));
		assertEquals(Long.valueOf(1), result.get(EventStatus.CLUSTER));

		DeletionDetails details = new DeletionDetails();

        stubFor(
                post(urlEqualTo("/delete")).withRequestBody(containing("eventTitle"))
                        .withRequestBody(containing("description"))
                        .withRequestBody(containing("null"))
                        .withRequestBody(containing("null"))
                        .withRequestBody(containing("null"))
                        .withRequestBody(containing("events"))
                        .willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		// delete events (should have an effect on result)
		eventFacade.delete(signal.getUuid(), details);
		eventFacade.delete(event1.getUuid(), details);
		eventFacade.delete(screening.getUuid(), details);
		eventFacade.delete(cluster.getUuid(), details);

		result = sut.getEventCountByStatus(new DashboardCriteria());
		assertEquals(1, result.size());
		assertEquals(Long.valueOf(1), result.get(EventStatus.EVENT));

	}
}

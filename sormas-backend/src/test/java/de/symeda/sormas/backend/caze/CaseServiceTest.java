package de.symeda.sormas.backend.caze;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolFacade;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class CaseServiceTest extends AbstractBeanTest {

	private static final int WIREMOCK_TESTING_PORT = 8888;
	private ExternalSurveillanceToolFacade subjectUnderTest;

	private TestDataCreator.RDCF rdcf;
	private UserDto nationalUser;

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

	@Override
	public void init() {

		super.init();
		rdcf = creator.createRDCF("Region", "District", "Community", "Facility", "Point of entry");
		nationalUser = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.community.getUuid(),
			rdcf.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
	}

	@Test
	public void testSetArchiveInExternalSurveillanceToolForEntity_WithProperEntity() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		CaseDataDto caze = creator.createCase(user, person, rdcf);
		Case case1 = getCaseService().getByUuid(caze.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(case1, ExternalShareStatus.SHARED);
		CaseService caseService = getBean(CaseService.class);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(caze.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		caseService.setArchiveInExternalSurveillanceToolForEntity(caze.getUuid(), true);
		wireMockRule.verify(exactly(1), postRequestedFor(urlEqualTo("/export")));
	}

	@Test
	public void testSetArchiveInExternalSurveillanceToolForEntity_WithoutProperEntity() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		CaseDataDto caze = creator.createCase(user, person, rdcf);
		CaseService caseService = getBean(CaseService.class);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(caze.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		//the case does not have an externalId set and after the filtering the sendCases will not be called
		caseService.setArchiveInExternalSurveillanceToolForEntity(caze.getUuid(), true);
		wireMockRule.verify(exactly(0), postRequestedFor(urlEqualTo("/export")));
	}

	@Test(expected = ExternalSurveillanceToolRuntimeException.class)
	public void testSetArchiveInExternalSurveillanceToolForEntity_Exception() throws ExternalSurveillanceToolException {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserReferenceDto user = creator.createUser(rdcf).toReference();
		PersonReferenceDto person = creator.createPerson("Walter", "Schuster").toReference();

		CaseDataDto caseDataDto = creator.createCase(user, person, rdcf);
		Case caze = getCaseService().getByUuid(caseDataDto.getUuid());
		getExternalShareInfoService().createAndPersistShareInfo(caze, ExternalShareStatus.SHARED);

		CaseService caseService = getBean(CaseService.class);

		stubFor(
			post(urlEqualTo("/export")).withRequestBody(containing(caseDataDto.getUuid()))
				.withRequestBody(containing("caseUuids"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)));

		caseService.setArchiveInExternalSurveillanceToolForEntity(caze.getUuid(), true);
	}

	private void configureExternalSurvToolUrlForWireMock() {
		MockProducer.getProperties().setProperty("survnet.url", String.format("http://localhost:%s", WIREMOCK_TESTING_PORT));
	}

	private void clearExternalSurvToolUrlForWireMock() {
		MockProducer.getProperties().setProperty("survnet.url", "");
	}

}

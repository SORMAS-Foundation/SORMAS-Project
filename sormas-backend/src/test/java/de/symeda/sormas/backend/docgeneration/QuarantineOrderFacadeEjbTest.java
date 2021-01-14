package de.symeda.sormas.backend.docgeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class QuarantineOrderFacadeEjbTest extends AbstractDocGenerationTest {

	private QuarantineOrderFacade quarantineOrderFacadeEjb;
	private CaseDataDto caseDataDto;
	private ContactDto contactDto;
	private PersonDto personDto;

	@Before
	public void setup() throws ParseException, URISyntaxException {
		quarantineOrderFacadeEjb = getQuarantineOrderFacade();
		resetCustomPath();

		LocationDto locationDto = new LocationDto();
		locationDto.setStreet("Nauwieserstraße");
		locationDto.setHouseNumber("7");
		locationDto.setCity("Saarbrücken");
		locationDto.setPostalCode("66111");

		personDto = PersonDto.build();
		personDto.setFirstName("Guy");
		personDto.setLastName("Debord");
		personDto.setBirthdateYYYY(1931);
		personDto.setBirthdateMM(12);
		personDto.setBirthdateDD(28);
		personDto.setAddress(locationDto);
		personDto.setPhone("+49 681 1234");

		getPersonFacade().savePerson(personDto);

		caseDataDto = creator.createUnclassifiedCase(Disease.CORONAVIRUS);
		caseDataDto.setPerson(personDto.toReference());
		caseDataDto.setQuarantineFrom(DATE_FORMAT.parse("10/09/2020"));
		caseDataDto.setQuarantineTo(DATE_FORMAT.parse("24/09/2020"));
		caseDataDto.setQuarantineOrderedOfficialDocumentDate(DATE_FORMAT.parse("09/09/2020"));
		getCaseFacade().saveCase(caseDataDto);

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		CommunityDto community2 = creator.createCommunity("Community2", rdcf.district);
		TestDataCreator.RDCF rdcf2 = new TestDataCreator.RDCF(
			rdcf.region,
			rdcf.district,
			community2.toReference(),
			creator.createFacility("Facility2", rdcf.region, rdcf.district, community2.toReference()).toReference());

		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		contactDto = creator.createContact(user.toReference(), personDto.toReference());
		contactDto.setCaze(caseDataDto.toReference());
		contactDto.setQuarantineFrom(DATE_FORMAT.parse("10/09/2020"));
		contactDto.setQuarantineTo(DATE_FORMAT.parse("24/09/2020"));
		contactDto.setQuarantineOrderedOfficialDocumentDate(DATE_FORMAT.parse("09/09/2020"));
		getContactFacade().saveContact(contactDto);
	}

	@Test
	public void generateQuarantineOrderCaseTest() throws IOException {
		ReferenceDto rootEntityReference = caseDataDto.toReference();
		generateQuarantineOrderTest(rootEntityReference, "QuarantineCase.cmp");
	}

	@Test
	public void generateQuarantineOrderContactTest() throws IOException {
		generateQuarantineOrderTest(contactDto.toReference(), "QuarantineContact.cmp");
	}

	@Test
	public void generateQuarantineOrderWrongReferenceTypeTest() throws IOException {
		try {
			generateQuarantineOrderTest(new ReferenceDto() {
			}, "Anything");
			fail("Wrong ReferenceDto not recognized");
		} catch (IllegalArgumentException e) {
			assertEquals("Quarantine can only be issued for cases or contacts.", e.getMessage());
		}
	}

	private void generateQuarantineOrderTest(ReferenceDto rootEntityReference, String comparisonFile) throws IOException {
		List<String> additionalVariables = quarantineOrderFacadeEjb.getAdditionalVariables("Quarantine.docx");
		List<String> expectedVariables = Arrays.asList("extraremark1", "extra.remark2", "extra.remark.no3");
		for (String additionaVariable : additionalVariables) {
			assertTrue(additionalVariables.contains(additionaVariable));
		}
		assertEquals(expectedVariables.size(), additionalVariables.size());

		Properties properties = new Properties();
		properties.setProperty("extraremark1", "the first remark");
		properties.setProperty("extra.remark.no3", "the third remark");

		ByteArrayInputStream generatedDocument =
			new ByteArrayInputStream(quarantineOrderFacadeEjb.getGeneratedDocument("Quarantine.docx", rootEntityReference, properties));

		XWPFDocument xwpfDocument = new XWPFDocument(generatedDocument);
		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(xwpfDocument);
		String docxText = xwpfWordExtractor.getText();
		xwpfWordExtractor.close();

		StringWriter writer = new StringWriter();
		IOUtils.copy(getClass().getResourceAsStream("/docgeneration/quarantine/" + comparisonFile), writer, "UTF-8");

		String expected = writer.toString().replaceAll("\\r\\n?", "\n");
		assertEquals(expected, docxText);
	}

	@Test
	public void getAvailableTemplatesTest() throws URISyntaxException {
		List<String> availableTemplates = quarantineOrderFacadeEjb.getAvailableTemplates();
		assertEquals(2, availableTemplates.size());
		assertTrue(availableTemplates.contains("Quarantine.docx"));
		assertTrue(availableTemplates.contains("FaultyTemplate.docx"));

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, "thisDirectoryDoesNotExist");

		assertTrue(quarantineOrderFacadeEjb.getAvailableTemplates().isEmpty());

		resetCustomPath();
	}
}

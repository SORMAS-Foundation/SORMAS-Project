package de.symeda.sormas.backend.docgeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
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
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class QuarantineOrderFacadeEjbTest extends AbstractBeanTest {

	private QuarantineOrderFacade quarantineOrderFacadeEjb;
	private CaseDataDto caseDataDto;
	private ContactDto contactDto;
	private PersonDto personDto;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Before
	public void setup() throws ParseException {
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
		caseDataDto.setQuarantineFrom(dateFormat.parse("10/09/2020"));
		caseDataDto.setQuarantineTo(dateFormat.parse("24/09/2020"));
		caseDataDto.setQuarantineOrderedOfficialDocumentDate(dateFormat.parse("09/09/2020"));
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
		contactDto.setQuarantineFrom(dateFormat.parse("10/09/2020"));
		contactDto.setQuarantineTo(dateFormat.parse("24/09/2020"));
		contactDto.setQuarantineOrderedOfficialDocumentDate(dateFormat.parse("09/09/2020"));
		getContactFacade().saveContact(contactDto);
	}

	@Test
	public void generateQuarantineOrderCaseTest() throws IOException {
		ReferenceDto rootEntityReference = caseDataDto.toReference();
		generateQuarantineOrderTest(rootEntityReference);
	}

	@Test
	public void generateQuarantineOrderContactTest() throws IOException {
		generateQuarantineOrderTest(contactDto.toReference());
	}

	@Test
	public void generateQuarantineOrderWrongRefernceTypeTest() throws IOException {
		try {
			generateQuarantineOrderTest(new ReferenceDto() {
			});
			fail("Wrong ReferenceDto not recognized");
		} catch (IllegalArgumentException e) {
			assertEquals("Quarantine can only be issued for cases or contacts.", e.getMessage());
		}
	}

	private void generateQuarantineOrderTest(ReferenceDto rootEntityReference) throws IOException {
		List<String> additionalVariables = quarantineOrderFacadeEjb.getAdditionalVariables("Quarantine.docx");
		assertEquals(Arrays.asList("other", "supervisor.name", "supervisor.phone", "supervisor.roomNumber"), additionalVariables);

		Properties properties = new Properties();
		properties.setProperty("supervisor.name", "Marcel Mariën");
		properties.setProperty("supervisor.phone", "+49 681 56789");
		properties.setProperty("supervisor.roomNumber", "17");

		ByteArrayInputStream generatedDocument =
			new ByteArrayInputStream(quarantineOrderFacadeEjb.getGeneratedDocument("Quarantine.docx", rootEntityReference, properties));

		XWPFDocument xwpfDocument = new XWPFDocument(generatedDocument);
		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(xwpfDocument);
		String docxText = xwpfWordExtractor.getText();

		StringWriter writer = new StringWriter();
		IOUtils.copy(getClass().getResourceAsStream("/docgeneration/quarantine/Quarantine.txt"), writer, "UTF-8");

		String expected = writer.toString().replaceAll("\\r\\n?", "\n");
		assertEquals(expected, docxText);
		System.out.println("  document generated.");
	}

	@Test
	public void getAvailableTemplatesTest() {
		List<String> availableTemplates = quarantineOrderFacadeEjb.getAvailableTemplates();
		assertEquals(2, availableTemplates.size());
		assertTrue(availableTemplates.contains("Quarantine.docx"));
		assertTrue(availableTemplates.contains("FaultyTemplate.docx"));

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, "thisDirectoryDoesNotExist");

		assertTrue(quarantineOrderFacadeEjb.getAvailableTemplates().isEmpty());

		resetCustomPath();
	}

	@Test
	public void isExistingTemplateTest() {
		assertTrue(quarantineOrderFacadeEjb.isExistingTemplate("Quarantine.docx"));
		assertFalse(quarantineOrderFacadeEjb.isExistingTemplate("ThisTemplateDoesNotExist.docx"));
	}

	@Test
	public void writeAndDeleteTemplateTest() throws IOException {
		String testDirectory = "target" + File.separator + "doctest";
		byte[] document = IOUtils.toByteArray(getClass().getResourceAsStream("/docgeneration/quarantine/Quarantine.docx"));
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, testDirectory);
		quarantineOrderFacadeEjb.writeQuarantineTemplate("TemplateFileToBeDeleted.docx", document);
		assertTrue(quarantineOrderFacadeEjb.getAvailableTemplates().contains("TemplateFileToBeDeleted.docx"));
		assertTrue(quarantineOrderFacadeEjb.deleteQuarantineTemplate("TemplateFileToBeDeleted.docx"));
		assertFalse(quarantineOrderFacadeEjb.getAvailableTemplates().contains("TemplateFileToBeDeleted.docx"));
		FileUtils.deleteDirectory(new File(testDirectory));
		resetCustomPath();
	}

	@Test
	public void validateTemplateTest() throws IOException {
		try {
			quarantineOrderFacadeEjb.writeQuarantineTemplate("TemplateFileToBeValidated.txt", new byte[0]);
			fail("Invalid file extension not recognized.");
		} catch (IllegalArgumentException e) {
			assertEquals("Wrong file type", e.getMessage());
		}
		try {
			quarantineOrderFacadeEjb.writeQuarantineTemplate("../TemplateFileToBeValidated.docx", new byte[0]);
			fail("Invalid file extension not recognized.");
		} catch (IllegalArgumentException e) {
			assertEquals("Illegal file name: ../TemplateFileToBeValidated.docx", e.getMessage());
		}
		try {
			quarantineOrderFacadeEjb.writeQuarantineTemplate("TemplateFileToBeValidated.docx", new byte[0]);
			fail("Invalid docx file not recognized.");
		} catch (IllegalArgumentException e) {
			assertEquals("Error reading from the stream (no bytes available)", e.getMessage());
		}
		try {
			byte[] document = IOUtils.toByteArray(getClass().getResourceAsStream("/docgeneration/quarantine/FaultyTemplate.docx"));
			quarantineOrderFacadeEjb.writeQuarantineTemplate("TemplateFileToBeValidated.docx", document);
			fail("Syntax error not recognized.");
		} catch (IllegalArgumentException e) {
			String message =
				"org.apache.velocity.runtime.parser.TemplateParseException: Encountered \"].</w:t>\\n            </w:r>\\n        </w:p>\\n        <w:p>\\n            <w:pPr>\\n                <w:pStyle w:val=\\\"Normal\\\"/>\\n                <w:bidi w:val=\\\"false\\\"/>\\n                <w:ind w:right=\\\"3117\\\" w:hanging=\\\"0\\\"/>\\n                <w:jc w:val=\\\"both\\\"/>\\n                <w:rPr>\\n                    <w:rFonts w:ascii=\\\"DejaVu Sans\\\" w:hAnsi=\\\"DejaVu Sans\\\"/>\\n                    <w:sz w:val=\\\"21\\\"/>\\n                    <w:szCs w:val=\\\"21\\\"/>\\n                </w:rPr>\\n            </w:pPr>\\n            <w:r>\\n                <w:rPr>\\n                    <w:b w:val=\\\"false\\\"/>\\n                    <w:bCs w:val=\\\"false\\\"/>\\n                </w:rPr>\\n            </w:r>\\n        </w:p>\\n        <w:p>\\n            <w:pPr>\\n                <w:pStyle w:val=\\\"Normal\\\"/>\\n                <w:bidi w:val=\\\"false\\\"/>\\n                <w:ind w:right=\\\"3117\\\" w:hanging=\\\"0\\\"/>\\n                <w:jc w:val=\\\"both\\\"/>\\n                <w:rPr>\\n                    <w:b w:val=\\\"false\\\"/>\\n                    <w:bCs w:val=\\\"false\\\"/>\\n                </w:rPr>\\n            </w:pPr>\\n            <w:r>\\n                <w:rPr>\\n                    <w:rFonts w:ascii=\\\"DejaVu Sans\\\" w:hAnsi=\\\"DejaVu Sans\\\"/>\\n                    <w:b w:val=\\\"false\\\"/>\\n                    <w:bCs w:val=\\\"false\\\"/>\\n                    <w:sz w:val=\\\"21\\\"/>\\n                    <w:szCs w:val=\\\"21\\\"/>\\n                </w:rPr>\\n                <w:t>Processing of this template should fail.</w:t>\\n            </w:r>\\n        </w:p>\\n        <w:sectPr>\\n            <w:type w:val=\\\"nextPage\\\"/>\\n            <w:pgSz w:w=\\\"11906\\\" w:h=\\\"16838\\\"/>\\n            <w:pgMar w:top=\\\"1134\\\" w:right=\\\"1134\\\" w:bottom=\\\"1134\\\" w:left=\\\"1134\\\" w:header=\\\"0\\\" w:footer=\\\"0\\\" w:gutter=\\\"0\\\"/>\\n            <w:pgNumType w:fmt=\\\"decimal\\\"/>\\n            <w:formProt w:val=\\\"false\\\"/>\\n            <w:textDirection w:val=\\\"lrTb\\\"/>\\n            <w:docGrid w:type=\\\"default\\\" w:linePitch=\\\"100\\\" w:charSpace=\\\"0\\\"/>\\n        </w:sectPr>\\n    </w:body>\\n</w:document>\" at word/document.xml[line 22, column 59]\n"
					+ "Was expecting one of:\n" //
					+ "    \"[\" ...\n" //
					+ "    \"}\" ...\n    ";
			assertEquals(message, e.getMessage().replaceAll("\\r\\n?", "\n"));
		}
	}

	@Test
	public void readTemplateTest() throws IOException {
		byte[] template = quarantineOrderFacadeEjb.getTemplate("Quarantine.docx");
		assertEquals(5416, template.length);
	}

	private void resetCustomPath() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, getClass().getResource("/").getPath());
	}
}

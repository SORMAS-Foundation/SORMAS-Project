/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.docgeneration;

import static de.symeda.sormas.backend.docgeneration.TemplateTestUtil.cleanLineSeparators;
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
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.docgeneneration.DocumentVariables;
import de.symeda.sormas.api.docgeneneration.DocumentWorkflow;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class QuarantineOrderFacadeEjbTest extends AbstractDocGenerationTest {

	private QuarantineOrderFacade quarantineOrderFacadeEjb;
	private CaseDataDto caseDataDto;
	private ContactDto contactDto;
	private EventParticipantDto eventParticipantDto;
	private UserDto userDto;

	private SampleDto sampleDto;
	private PathogenTestDto pathogenTestDto;

	@Before
	public void setup() throws ParseException, URISyntaxException {
		quarantineOrderFacadeEjb = getQuarantineOrderFacade();
		reset();

		LocationDto locationDto = new LocationDto();
		locationDto.setStreet("Nauwieserstraße");
		locationDto.setHouseNumber("7");
		locationDto.setCity("Saarbrücken");
		locationDto.setPostalCode("66111");

		PersonDto personDto = PersonDto.build();
		personDto.setFirstName("Guy");
		personDto.setLastName("Debord");
		personDto.setBirthdateYYYY(1931);
		personDto.setBirthdateMM(12);
		personDto.setBirthdateDD(28);
		personDto.setAddress(locationDto);
		personDto.setPrimaryPhone("+49 681 1234");

		getPersonFacade().savePerson(personDto);

		caseDataDto = creator.createUnclassifiedCase(Disease.CORONAVIRUS);
		caseDataDto.setPerson(personDto.toReference());
		caseDataDto.setQuarantineFrom(DATE_FORMAT.parse("10/09/2020"));
		caseDataDto.setQuarantineTo(DATE_FORMAT.parse("24/09/2020"));
		caseDataDto.setQuarantineOrderedOfficialDocumentDate(DATE_FORMAT.parse("09/09/2020"));
		getCaseFacade().saveCase(caseDataDto);

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");

		userDto = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);

		contactDto = creator.createContact(userDto.toReference(), personDto.toReference());
		contactDto.setCaze(caseDataDto.toReference());
		contactDto.setQuarantineFrom(DATE_FORMAT.parse("10/09/2020"));
		contactDto.setQuarantineTo(DATE_FORMAT.parse("24/09/2020"));
		contactDto.setQuarantineOrderedOfficialDocumentDate(DATE_FORMAT.parse("09/09/2020"));
		getContactFacade().saveContact(contactDto);

		EventDto eventDto = creator.createEvent(userDto.toReference());
		eventDto.setEventTitle("An event");
		getEventFacade().saveEvent(eventDto);
		eventParticipantDto = creator.createEventParticipant(eventDto.toReference(), personDto, "participated", userDto.toReference());

		sampleDto = SampleDto.build(userDto.toReference(), caseDataDto.toReference());
		sampleDto.setSampleDateTime(DATE_FORMAT.parse("11/09/2020"));
		sampleDto.setSampleMaterial(SampleMaterial.NASAL_SWAB);
		sampleDto.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
		sampleDto.setSamplePurpose(SamplePurpose.EXTERNAL);
		getSampleFacade().saveSample(sampleDto);

		pathogenTestDto = PathogenTestDto.build(sampleDto.toReference(), userDto.toReference());
		pathogenTestDto.setTestDateTime(DATE_FORMAT.parse("12/09/2020"));
		pathogenTestDto.setTestedDisease(Disease.CORONAVIRUS);
		pathogenTestDto.setTestResult(PathogenTestResultType.POSITIVE);
		pathogenTestDto.setTestResultVerified(false);
		pathogenTestDto.setTestType(PathogenTestType.ANTIBODY_DETECTION);
		pathogenTestDto = getPathogenTestFacade().savePathogenTest(pathogenTestDto);
	}

	@Test
	public void generateQuarantineOrderCaseTest() throws IOException, DocumentTemplateException {
		ReferenceDto rootEntityReference = caseDataDto.toReference();
		generateQuarantineOrderTest(
			rootEntityReference,
			userDto.toReference(),
			sampleDto.toReference(),
			pathogenTestDto.toReference(),
			"QuarantineCase.cmp");
	}

	@Test
	public void generateQuarantineOrderContactTest() throws IOException, DocumentTemplateException {
		generateQuarantineOrderTest(contactDto.toReference(), userDto.toReference(), null, null, "QuarantineContact.cmp");
	}

	@Test
	public void generateQuarantineOrderEventParticipantTest() throws IOException, DocumentTemplateException {
		generateQuarantineOrderTest(eventParticipantDto.toReference(), userDto.toReference(), null, null, "QuarantineEvent.cmp");
	}

	@Test
	public void generateQuarantineOrderCustomNullReplacementTest() throws IOException, DocumentTemplateException {
		ReferenceDto rootEntityReference = caseDataDto.toReference();

		setNullReplacement("");
		generateQuarantineOrderTest(rootEntityReference, userDto.toReference(), null, null, "QuarantineCaseEmptyNullReplacement.cmp");

		setNullReplacement("xxx");
		generateQuarantineOrderTest(rootEntityReference, userDto.toReference(), null, null, "QuarantineCaseCustomNullReplacement.cmp");
	}

	@Test
	public void generateQuarantineOrderWrongReferenceTypeTest() throws IOException {
		try {
			generateQuarantineOrderTest(new ReferenceDto() {
			}, null, null, null, "Anything");
			fail("Wrong ReferenceDto not recognized");
		} catch (IllegalArgumentException | DocumentTemplateException e) {
			assertEquals("Quarantine can only be issued for cases or contacts.", e.getMessage());
		}
	}

	private void generateQuarantineOrderTest(
		ReferenceDto rootEntityReference,
		UserReferenceDto userReference,
		SampleReferenceDto sampleReference,
		PathogenTestReferenceDto pathogenTest,
		String comparisonFile)
		throws IOException, DocumentTemplateException {

		DocumentVariables documentVariables = quarantineOrderFacadeEjb.getDocumentVariables(rootEntityReference, "Quarantine.docx");
		List<String> additionalVariables = documentVariables.getAdditionalVariables();
		List<String> expectedVariables = Arrays.asList("extraremark1", "extra.remark2", "extra.remark.no3");
		for (String additionaVariable : additionalVariables) {
			assertTrue(additionalVariables.contains(additionaVariable));
		}
		assertEquals(expectedVariables.size(), additionalVariables.size());

		String rootEntityName = rootEntityReference instanceof CaseReferenceDto
			? "case"
			: rootEntityReference instanceof ContactReferenceDto ? "contact" : "eventparticipant";
		List<String> expectedUsedEntities = Arrays.asList(rootEntityName, "person", "user", "sample", "pathogenTest");
		for (String usedEntity : expectedUsedEntities) {
			assertTrue("Used entity not detected: " + usedEntity, documentVariables.isUsedEntity(usedEntity));
		}
		assertEquals(expectedUsedEntities.size(), documentVariables.getUsedEntities().size());

		Properties properties = new Properties();
		properties.setProperty("extraremark1", "the first remark");
		properties.setProperty("extra.remark.no3", "the third remark");

		ByteArrayInputStream generatedDocument = new ByteArrayInputStream(
			quarantineOrderFacadeEjb
				.getGeneratedDocument("Quarantine.docx", rootEntityReference, userReference, sampleReference, pathogenTest, properties));

		XWPFDocument xwpfDocument = new XWPFDocument(generatedDocument);
		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(xwpfDocument);
		String docxText = cleanLineSeparators(xwpfWordExtractor.getText());
		xwpfWordExtractor.close();

		StringWriter writer = new StringWriter();
		IOUtils.copy(
			getClass()
				.getResourceAsStream("/docgeneration/" + getDocumentWorkflow(rootEntityReference).getTemplateDirectory() + "/" + comparisonFile),
			writer,
			"UTF-8");

		String expected = cleanLineSeparators(writer.toString());
		assertEquals(expected, docxText);
	}

	@Test
	public void getAvailableTemplatesTest() throws URISyntaxException, DocumentTemplateException {
		List<String> availableTemplates = quarantineOrderFacadeEjb.getAvailableTemplates(new CaseReferenceDto());
		assertEquals(2, availableTemplates.size());
		assertTrue(availableTemplates.contains("Quarantine.docx"));
		assertTrue(availableTemplates.contains("FaultyTemplate.docx"));

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, "thisDirectoryDoesNotExist");

		assertTrue(quarantineOrderFacadeEjb.getAvailableTemplates(new CaseReferenceDto()).isEmpty());

		resetCustomPath();
	}

	private DocumentWorkflow getDocumentWorkflow(ReferenceDto reference) {
		if (reference instanceof CaseReferenceDto) {
			return DocumentWorkflow.QUARANTINE_ORDER_CASE;
		} else if (reference instanceof ContactReferenceDto) {
			return DocumentWorkflow.QUARANTINE_ORDER_CONTACT;
		} else if (reference instanceof EventParticipantReferenceDto) {
			return DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT;
		} else {
			throw new IllegalArgumentException(I18nProperties.getString(Strings.errorQuarantineOnlyCaseAndContacts));
		}
	}
}

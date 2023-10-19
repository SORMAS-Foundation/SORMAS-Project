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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class QuarantineOrderFacadeEjbTest extends AbstractDocGenerationTest {

	private QuarantineOrderFacade quarantineOrderFacadeEjb;
	private CaseDataDto caseDataDto;
	private ContactDto contactDto;
	private EventDto eventDto;
	private EventParticipantDto eventParticipantDto;
	private TravelEntryDto travelEntryDto;

	private SampleDto sampleDto;
	private PathogenTestDto pathogenTestDto;

	@BeforeEach
	public void setup() throws URISyntaxException {
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility", "PointOfEntry");

		UserDto userDto = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		loginWith(userDto);

		quarantineOrderFacadeEjb = getQuarantineOrderFacade();
		reset();

		LocationDto locationDto = LocationDto.build();
		locationDto.setStreet("Nauwieserstraße");
		locationDto.setHouseNumber("7");
		locationDto.setCity("Saarbrücken");
		locationDto.setPostalCode("66111");

		PersonDto personDto = PersonDto.build();
		personDto.setFirstName("Guy");
		personDto.setLastName("Debord");
		personDto.setSex(Sex.UNKNOWN);
		personDto.setBirthdateYYYY(1931);
		personDto.setBirthdateMM(12);
		personDto.setBirthdateDD(28);
		personDto.setAddress(locationDto);
		personDto.setPhone("+49 681 1234");

		getPersonFacade().save(personDto);

		caseDataDto = creator.createCase(userDto.toReference(), rdcf, (c) -> {
			c.setDisease(Disease.CORONAVIRUS);
			c.setPerson(personDto.toReference());
			c.setQuarantineFrom(parseDate("10/09/2020"));
			c.setQuarantineTo(parseDate("24/09/2020"));
			c.setQuarantineOrderedOfficialDocumentDate(parseDate("09/09/2020"));
		});

		contactDto = creator.createContact(userDto.toReference(), personDto.toReference());
		contactDto.setCaze(caseDataDto.toReference());
		contactDto.setQuarantineFrom(parseDate("10/09/2020"));
		contactDto.setQuarantineTo(parseDate("24/09/2020"));
		contactDto.setQuarantineOrderedOfficialDocumentDate(parseDate("09/09/2020"));
		getContactFacade().save(contactDto);

		eventDto = creator.createEvent(userDto.toReference());
		eventDto.setEventTitle("An event");
		getEventFacade().save(eventDto);
		eventParticipantDto = creator.createEventParticipant(eventDto.toReference(), personDto, "participated", userDto.toReference());

		sampleDto = SampleDto.build(userDto.toReference(), caseDataDto.toReference());
		sampleDto.setLab(rdcf.facility);
		sampleDto.setSampleDateTime(parseDate("11/09/2020"));
		sampleDto.setSampleMaterial(SampleMaterial.NASAL_SWAB);
		sampleDto.setPathogenTestResult(PathogenTestResultType.NEGATIVE);
		sampleDto.setSamplePurpose(SamplePurpose.EXTERNAL);
		sampleDto = getSampleFacade().saveSample(sampleDto);

		pathogenTestDto = PathogenTestDto.build(sampleDto.toReference(), userDto.toReference());
		pathogenTestDto.setTestDateTime(parseDate("12/09/2020"));
		pathogenTestDto.setTestedDisease(Disease.CORONAVIRUS);
		pathogenTestDto.setTestResult(PathogenTestResultType.POSITIVE);
		pathogenTestDto.setTestResultVerified(false);
		pathogenTestDto.setTestType(PathogenTestType.ANTIBODY_DETECTION);
		pathogenTestDto.setLab(rdcf.facility);
		pathogenTestDto = getPathogenTestFacade().savePathogenTest(pathogenTestDto);

		travelEntryDto = TravelEntryDto.build(personDto.toReference());
		travelEntryDto.setUuid("VQCJU2-DZPOBO-7FWMLV-G3F3SACI");
		travelEntryDto.setReportDate(parseDate("09/01/2021"));
		travelEntryDto.setReportingUser(userDto.toReference());
		travelEntryDto.setDateOfArrival(new Date());
		travelEntryDto.setPointOfEntry(rdcf.pointOfEntry);
		// To survive validation:
		travelEntryDto.setDisease(Disease.CORONAVIRUS);
		travelEntryDto.setResponsibleRegion(rdcf.region);
		travelEntryDto.setResponsibleDistrict(rdcf.district);
		travelEntryDto = getTravelEntryFacade().save(travelEntryDto);
	}

	private Date parseDate(String dateString) {
		try {
			return DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void generateQuarantineOrderCaseTest() throws IOException, DocumentTemplateException {
		ReferenceDto rootEntityReference = caseDataDto.toReference();
		generateQuarantineOrderTest(
			rootEntityReference,
			DocumentWorkflow.QUARANTINE_ORDER_CASE,
			sampleDto.toReference(),
			pathogenTestDto.toReference(),
			"QuarantineCase.cmp");
	}

	@Test
	public void generateQuarantineOrderContactTest() throws IOException, DocumentTemplateException {
		generateQuarantineOrderTest(contactDto.toReference(), DocumentWorkflow.QUARANTINE_ORDER_CONTACT, null, null, "QuarantineContact.cmp");
	}

	@Test
	public void generateQuarantineOrderEventParticipantTest() throws IOException, DocumentTemplateException {
		generateQuarantineOrderTest(
			eventParticipantDto.toReference(),
			DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT,
			null,
			null,
			"QuarantineEvent.cmp");
	}

	@Test
	public void generateQuarantineOrderTravelEntryTest() throws IOException, DocumentTemplateException {
		generateQuarantineOrderTest(
			travelEntryDto.toReference(),
			DocumentWorkflow.QUARANTINE_ORDER_TRAVEL_ENTRY,
			null,
			null,
			"QuarantineTravelEntry.cmp");
	}

	@Test
	public void generateQuarantineOrderCustomNullReplacementTest() throws IOException, DocumentTemplateException {
		ReferenceDto rootEntityReference = caseDataDto.toReference();

		setNullReplacement("");
		generateQuarantineOrderTest(
			rootEntityReference,
			DocumentWorkflow.QUARANTINE_ORDER_CASE,
			null,
			null,
			"QuarantineCaseEmptyNullReplacement.cmp");

		setNullReplacement("xxx");
		generateQuarantineOrderTest(
			rootEntityReference,
			DocumentWorkflow.QUARANTINE_ORDER_CASE,
			null,
			null,
			"QuarantineCaseCustomNullReplacement.cmp");
	}

	@Test
	public void testBulkCaseDocumentCreation() throws DocumentTemplateException, IOException {
		ReferenceDto rootEntityReference = caseDataDto.toReference();

		Properties properties = new Properties();
		properties.setProperty("extraremark1", "the first remark");
		properties.setProperty("extra.remark.no3", "the third remark");

		DocumentWorkflow workflow = DocumentWorkflow.QUARANTINE_ORDER_CASE;
		Map<ReferenceDto, byte[]> documentContents = quarantineOrderFacadeEjb
			.getGeneratedDocuments("Quarantine.docx", workflow, Collections.singletonList(rootEntityReference), properties, false);

		verifyGeneratedDocument(rootEntityReference, workflow, "QuarantineCase.cmp", documentContents.get(rootEntityReference));
	}

	@Test
	public void testBulkContactDocumentCreation() throws DocumentTemplateException, IOException {
		ReferenceDto rootEntityReference = contactDto.toReference();
		Properties properties = new Properties();
		properties.setProperty("extraremark1", "the first remark");
		properties.setProperty("extra.remark.no3", "the third remark");

		DocumentWorkflow workflow = DocumentWorkflow.QUARANTINE_ORDER_CONTACT;
		Map<ReferenceDto, byte[]> documentContents = quarantineOrderFacadeEjb
			.getGeneratedDocuments("Quarantine.docx", workflow, Collections.singletonList(rootEntityReference), properties, false);

		verifyGeneratedDocument(rootEntityReference, workflow, "QuarantineContact.cmp", documentContents.get(rootEntityReference));
	}

	@Test
	public void testBulkEventParticipantDocumentCreation() throws DocumentTemplateException, IOException {
		EventParticipantReferenceDto rootEntityReference = eventParticipantDto.toReference();

		Properties properties = new Properties();
		properties.setProperty("extraremark1", "the first remark");
		properties.setProperty("extra.remark.no3", "the third remark");

		DocumentWorkflow workflow = DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT;
		Map<ReferenceDto, byte[]> documentContents = quarantineOrderFacadeEjb.getGeneratedDocumentsForEventParticipants(
			"Quarantine.docx",
			Collections.singletonList(rootEntityReference),
			eventDto.getDisease(),
			properties,
			false);

		verifyGeneratedDocument(rootEntityReference, workflow, "QuarantineEvent.cmp", documentContents.get(rootEntityReference));
	}

	private void generateQuarantineOrderTest(
		ReferenceDto rootEntityReference,
		DocumentWorkflow documentWorkflow,
		SampleReferenceDto sampleReference,
		PathogenTestReferenceDto pathogenTest,
		String comparisonFile)
		throws DocumentTemplateException, IOException {

		Properties properties = new Properties();
		properties.setProperty("extraremark1", "the first remark");
		properties.setProperty("extra.remark.no3", "the third remark");
		properties.setProperty("extraComment", "some Comment");

		verifyGeneratedDocument(
			rootEntityReference,
			documentWorkflow,
			comparisonFile,
			quarantineOrderFacadeEjb.getGeneratedDocument(
				"Quarantine.docx",
				documentWorkflow,
				rootEntityReference,
				sampleReference,
				pathogenTest,
				null,
				properties,
				Boolean.FALSE));
	}

	private void verifyGeneratedDocument(
		ReferenceDto rootEntityReference,
		DocumentWorkflow documentWorkflow,
		String comparisonFile,
		byte[] documentContent)
		throws IOException, DocumentTemplateException {

		DocumentVariables documentVariables = quarantineOrderFacadeEjb.getDocumentVariables(documentWorkflow, "Quarantine.docx");
		List<String> additionalVariables = documentVariables.getAdditionalVariables();

		String rootEntityName;
		List<String> expectedVariables = Arrays.asList("extraremark1", "extra.remark2", "extra.remark.no3");
		List<String> expectedUsedEntities = new ArrayList<>(Arrays.asList("person", "user", "sample", "pathogenTest"));

		switch (documentWorkflow) {
		case QUARANTINE_ORDER_CASE:
			rootEntityName = "case";
			break;

		case QUARANTINE_ORDER_CONTACT:
			rootEntityName = "contact";
			break;

		case QUARANTINE_ORDER_EVENT_PARTICIPANT:
			rootEntityName = "eventparticipant";
			break;

		case QUARANTINE_ORDER_TRAVEL_ENTRY:
			rootEntityName = "travelentry";
			expectedVariables = Collections.singletonList("extraComment");
			expectedUsedEntities.remove("sample");
			expectedUsedEntities.remove("pathogenTest");
			break;

		default:
			rootEntityName = "unkown";
		}

		expectedUsedEntities.add(rootEntityName);

		for (String additionaVariable : additionalVariables) {
			assertTrue(additionalVariables.contains(additionaVariable));
		}
		assertEquals(expectedVariables.size(), additionalVariables.size());

		for (String usedEntity : expectedUsedEntities) {
			assertTrue(documentVariables.isUsedEntity(usedEntity), "Used entity not detected: " + usedEntity);
		}
		assertEquals(expectedUsedEntities.size(), documentVariables.getUsedEntities().size());

		ByteArrayInputStream generatedDocument = new ByteArrayInputStream(documentContent);

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
	public void getAvailableTemplatesTest() throws URISyntaxException {
		List<String> availableTemplates = quarantineOrderFacadeEjb.getAvailableTemplates(DocumentWorkflow.QUARANTINE_ORDER_CASE);
		assertEquals(2, availableTemplates.size());
		assertTrue(availableTemplates.contains("Quarantine.docx"));
		assertTrue(availableTemplates.contains("FaultyTemplate.docx"));

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, "thisDirectoryDoesNotExist");

		assertTrue(quarantineOrderFacadeEjb.getAvailableTemplates(DocumentWorkflow.QUARANTINE_ORDER_CASE).isEmpty());

		resetCustomPath();
	}

	private DocumentWorkflow getDocumentWorkflow(ReferenceDto reference) {
		if (reference instanceof CaseReferenceDto) {
			return DocumentWorkflow.QUARANTINE_ORDER_CASE;
		} else if (reference instanceof ContactReferenceDto) {
			return DocumentWorkflow.QUARANTINE_ORDER_CONTACT;
		} else if (reference instanceof EventParticipantReferenceDto) {
			return DocumentWorkflow.QUARANTINE_ORDER_EVENT_PARTICIPANT;
		} else if (reference instanceof TravelEntryReferenceDto) {
			return DocumentWorkflow.QUARANTINE_ORDER_TRAVEL_ENTRY;
		} else {
			throw new IllegalArgumentException(I18nProperties.getString(Strings.errorQuarantineOnlySupportedEntities));
		}
	}
}

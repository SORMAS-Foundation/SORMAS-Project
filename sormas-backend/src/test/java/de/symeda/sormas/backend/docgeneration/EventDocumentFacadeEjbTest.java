package de.symeda.sormas.backend.docgeneration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.action.ActionContext;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.docgeneneration.EventDocumentFacade;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.TestDataCreator;

public class EventDocumentFacadeEjbTest extends AbstractDocGenerationTest {

	private EventDocumentFacade eventDocumentFacade;

	private PersonDto personDto1;
	private PersonDto personDto2;
	private PersonDto personDto3;

	private EventDto eventDto;

	@Before
	public void setup() throws ParseException {
		eventDocumentFacade = getEventDocumentFacade();
		resetCustomPath();

		TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator
			.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		eventDto = creator.createEvent(
			EventStatus.SIGNAL,
			EventInvestigationStatus.PENDING,
			"Some Event",
			"...where people meet",
			"Sourcy",
			"McSourceFace",
			"12345",
			TypeOfPlace.PUBLIC_PLACE,
			DATE_FORMAT.parse("12/11/2020"),
			DATE_FORMAT.parse("13/11/2020"),
			user.toReference(),
			user.toReference(),
			Disease.CORONAVIRUS,
			rdcf.district);

		personDto1 = PersonDto.build();
		personDto1.setFirstName("Isidore");
		personDto1.setLastName("Isou");
		personDto1.setPhone("+49 681 1234");

		getPersonFacade().savePerson(personDto1);

		EventParticipantDto eventParticipantDto1 = EventParticipantDto.build(eventDto.toReference(), user.toReference());
		eventParticipantDto1.setPerson(personDto1);
		eventParticipantDto1.setInvolvementDescription("involved");
		getEventParticipantFacade().saveEventParticipant(eventParticipantDto1);

		personDto2 = PersonDto.build();
		personDto2.setFirstName("Guy");
		personDto2.setLastName("Debord");
		personDto2.setPhone("+49 681 4567");

		getPersonFacade().savePerson(personDto2);

		EventParticipantDto eventParticipantDto2 = EventParticipantDto.build(eventDto.toReference(), user.toReference());
		eventParticipantDto2.setPerson(personDto2);
		eventParticipantDto2.setInvolvementDescription("involved");
		getEventParticipantFacade().saveEventParticipant(eventParticipantDto2);

		personDto3 = PersonDto.build();
		personDto3.setFirstName("Georges");
		personDto3.setLastName("Bataille");
		personDto3.setPhone("+49 681 8901");

		getPersonFacade().savePerson(personDto3);

		EventParticipantDto eventParticipantDto3 = EventParticipantDto.build(eventDto.toReference(), user.toReference());
		eventParticipantDto3.setPerson(personDto3);
		eventParticipantDto3.setInvolvementDescription("involved");
		getEventParticipantFacade().saveEventParticipant(eventParticipantDto3);

		ActionDto actionDto1 = new ActionDto();
		actionDto1.setTitle("An action");
		actionDto1.setActionContext(ActionContext.EVENT);
		actionDto1.setDate(DATE_FORMAT.parse("16/11/2020"));
		actionDto1.setDescription("Here is what to do.");
		actionDto1.setReply("This is your reply.");
		actionDto1.setEvent(eventDto.toReference());

		getActionFacade().saveAction(actionDto1);

		ActionDto actionDto2 = new ActionDto();
		actionDto2.setTitle("Another action");
		actionDto2.setActionContext(ActionContext.EVENT);
		actionDto2.setDate(DATE_FORMAT.parse("15/11/2020"));
		actionDto2.setDescription("This action hast no reply");
		actionDto2.setEvent(eventDto.toReference());

		getActionFacade().saveAction(actionDto2);
	}

	@Test
	public void generateEventHandoutTest() throws IOException {
		String testCasesDirPath = "/docgeneration/eventHandout";
		File testCasesDir = new File(getClass().getResource(testCasesDirPath).getPath());
		File[] testCasesHtml = testCasesDir.listFiles((d, name) -> name.endsWith(".html"));
		assertNotNull(testCasesHtml);

		for (File testCaseHtml : testCasesHtml) {
			String testcaseBasename = FilenameUtils.getBaseName(testCaseHtml.getName());

			String htmlText = eventDocumentFacade.getGeneratedDocument(testcaseBasename + ".html", eventDto.toReference(), new Properties());

			StringWriter writer = new StringWriter();
			IOUtils.copy(getClass().getResourceAsStream("/docgeneration/eventHandout/" + testcaseBasename + ".cmp"), writer, "UTF-8");

			String expected = writer.toString().replaceAll("\\r\\n?", "\n");
			assertEquals(
				expected,
				htmlText.replaceAll("<p>Event-ID: <b>[A-Z0-9-]*</b></p>", "<p>Event-ID: <b>STN3WX-5JTGYV-IU2LRM-4UHCSOEE</b></p>"));
			System.out.println("Testcase completed: " + testcaseBasename);
		}
	}
}

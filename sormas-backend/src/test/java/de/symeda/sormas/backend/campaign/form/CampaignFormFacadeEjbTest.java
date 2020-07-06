package de.symeda.sormas.backend.campaign.form;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import de.symeda.sormas.api.campaign.form.CampaignFormDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;

public class CampaignFormFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testValidateAndClean() throws IOException {
		// ID is required
		String schema = "[{\"type\": \"string\"}]";
		CampaignFormDto campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, null);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Type is required
		schema = "[{\"id\": \"element\"}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, null);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Type must be supported
		schema = "[{\"id\": \"element\", \"type\": \"unsupported-type\"}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, null);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Styles must be an array
		try {
			schema = "[{\"id\": \"element\", \"type\": \"string\", \"styles\": \"col-1\"}]";
			getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, null);
		} catch (MismatchedInputException ignored) {
		}

		// Style must be supported
		schema = "[{\"id\": \"element\", \"type\": \"string\", \"styles\": [\"unsupported-style\"]}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, null);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Elements with a dependingOn attribute also need the dependingOnValues attribute
		schema = "[{\"id\": \"element\", \"type\": \"string\"}, {\"id\": \"element2\", \"type\": \"string\", \"dependingOn\": \"element\"}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, null);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Element specified in dependingOn must exist
		schema = "[{\"id\": \"element\", \"type\": \"string\", \"dependingOn\": \"invalid-element\", \"dependingOnValues\": [\"value\"]}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, null);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Values specified in dependingOnValues must be supported
		schema =
			"[{\"id\": \"element\", \"type\": \"integer\"}, {\"id\": \"element2\", \"type\": \"string\", \"dependingOn\": \"element\", \"dependingOnValues\": [\"string\"]}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, null);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		schema =
			"[{\"id\": \"element\", \"type\": \"yes-no\"}, {\"id\": \"element2\", \"type\": \"string\", \"dependingOn\": \"element\", \"dependingOnValues\": [\"string\"]}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, null);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Translations must specify a language code
		schema = "[{\"id\": \"element\", \"type\": \"string\"}]";
		String translations = "[{\"translations\": [{\"elementId\": \"element\", \"caption\": \"translated-caption\"}]}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, translations);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Translation elements must contain an element ID
		schema = "[{\"id\": \"element\", \"type\": \"string\"}]";
		translations = "[{\"languageCode\": \"de-DE\", \"translations\": [{\"caption\": \"translated-caption\"}]}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, translations);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Translation elements must contain a caption
		schema = "[{\"id\": \"element\", \"type\": \"string\"}]";
		translations = "[{\"languageCode\": \"de-DE\", \"translations\": [{\"elementId\": \"element\"}]}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, translations);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Translation elements must contain an valid element ID
		schema = "[{\"id\": \"element\", \"type\": \"string\"}]";
		translations = "[{\"languageCode\": \"de-DE\", \"translations\": [{\"elementId\": \"invalid-id\", \"caption\": \"translated-caption\"}]}]";
		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, translations);

		try {
			getCampaignFormFacade().validateAndClean(campaignForm);
			fail("Malformed campaign form was saved!");
		} catch (ValidationRuntimeException ignored) {
		}

		// Valid schema and translations should be saved
		schema = "[{\"type\": \"string\",\"id\": \"teamNumber\",\"caption\": \"Team number\",\"styles\": [\"first\"]},{\"type\": \"string\",\"id\": "
			+ "\"namesOfTeamMembers\",\"caption\": \"Names of team members\",\"styles\": [\"col-8\"]},{\"type\": \"string\",\"id\": "
			+ "\"monitorName\",\"caption\": \"Name of monitor\",\"styles\": [\"first\"]},{\"type\": \"string\",\"id\": \"agencyName\",\"caption\": "
			+ "\"Agency\"},{\"type\": \"section\",\"id\": \"questionsSection\"},{\"type\": \"label\",\"id\": \"questionsLabel\",\"caption\": \"<h2>Questions</h2>\"}"
			+ ",{\"type\": \"yes-no\",\"id\": \"oneMemberResident\",\"caption\": \"1) At least one team member is resident of same area (villages)?\"},{\"type\": "
			+ "\"yes-no\",\"id\": \"vaccinatorsTrained\",\"caption\": \"2) Both vaccinators trained before this campaign?\"},{\"type\": \"section\","
			+ " \"id\": \"questionsSection2\"},{\"type\": \"label\",\"id\": \"q8To12Label\",\"caption\": \"Q 8-12: Based on observation of team only.\"},"
			+ "{\"type\": \"yes-no\",\"id\": \"askingAboutMonthOlds\",\"caption\": \"8) Is team specially asking about 0-11 months children?\"},"
			+ "{\"type\": \"section\", \"id\": \"questionsSection3\"},{\"type\": \"yes-no\",\"id\": \"atLeastOneMemberChw\","
			+ "\"caption\": \"13) Is at least one member of the team CHW?\"},{\"type\": \"integer\",\"id\": "
			+ "\"numberOfChw\",\"caption\": \"No. of CHW\",\"styles\": [\"row\"],\"dependingOn\": \"atLeastOneMemberChw\",\"dependingOnValues\": [\"YES\"]},"
			+ "{\"type\": \"yes-no\",\"id\": \"anyMemberFemale\",\"caption\": \"14) Is any member of the team female?\"},{\"type\": \"yes-no\","
			+ "\"id\": \"accompaniedBySocialMobilizer\",\"caption\": \"15) Does social mobilizer accompany the vaccination team in the field?\"},"
			+ "{\"type\": \"string\",\"id\": \"comments\",\"caption\": \"Comments\",\"styles\": [\"col-12\"]}]";
		translations =
			"[{\"languageCode\": \"de-DE\", \"translations\": [{\"elementId\": \"teamNumber\", \"caption\": \"Teamnummer\"}, {\"elementId\": \"namesOfTeamMembers\","
				+ " \"caption\": \"Namen der Teammitglieder\"}]}, {\"languageCode\": \"fr-FR\", \"translations\": [{\"elementId\": \"teamNumber\", "
				+ "\"caption\": \"Numéro de l'équipe\"}]}]";

		campaignForm = getCampaignFormFacade().buildCampaignFormFromJson("testForm", null, schema, translations);
		getCampaignFormFacade().validateAndClean(campaignForm);
	}

}

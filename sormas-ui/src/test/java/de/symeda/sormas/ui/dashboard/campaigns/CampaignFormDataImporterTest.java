package de.symeda.sormas.ui.dashboard.campaigns;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.campaign.importer.CampaignFormDataImporter;
import de.symeda.sormas.ui.importer.ImportResultStatus;

public class CampaignFormDataImporterTest extends AbstractUiBeanTest {

	@Test
	public void testImportCampaignFormData()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));

		final CampaignDto campaign = creator.createCampaign(user);

		final CampaignFormMetaDto campaignForm = createCampaignForm(campaign);

		File csvFile = new File(getClass().getClassLoader().getResource("campaign/sormas_campaign_data_import_test_success.csv").toURI());
		CampaignFormDataImporterExtension campaignFormDataImporter =
			new CampaignFormDataImporterExtension(csvFile, false, user, campaignForm.getUuid(), new CampaignReferenceDto(campaign.getUuid()));
		ImportResultStatus importResult = campaignFormDataImporter.runImport().getStatus();

		assertTrue(campaignFormDataImporter.getErrorMessages().isEmpty());
		assertEquals(ImportResultStatus.COMPLETED, importResult);
	}

	@Test
	public void testImportCampaignFormDataWithWrongDataType()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));

		final CampaignDto campaign = creator.createCampaign(user);

		final CampaignFormMetaDto campaignForm = createCampaignForm(campaign);

		File csvFile = new File(getClass().getClassLoader().getResource("campaign/sormas_campaign_data_import_test_wrong_type.csv").toURI());
		CampaignFormDataImporterExtension campaignFormDataImporter =
			new CampaignFormDataImporterExtension(csvFile, false, user, campaignForm.getUuid(), new CampaignReferenceDto(campaign.getUuid()));
		ImportResultStatus importResult = campaignFormDataImporter.runImport().getStatus();

		assertFalse(campaignFormDataImporter.getErrorMessages().isEmpty());
		assertEquals("Value nonNumeric in column infected does not match expected data type.", campaignFormDataImporter.getErrorMessages().get(0));
	}

	@Test
	public void testImportCampaignFormDataIgnoringNonExistingColumn()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Nat",
			"User",
			creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));

		final CampaignDto campaign = creator.createCampaign(user);

		final CampaignFormMetaDto campaignForm = createCampaignForm(campaign);

		File csvFile = new File(getClass().getClassLoader().getResource("campaign/sormas_campaign_data_import_test_nonexisting_column.csv").toURI());
		CampaignFormDataImporterExtension campaignFormDataImporter =
			new CampaignFormDataImporterExtension(csvFile, false, user, campaignForm.getUuid(), new CampaignReferenceDto(campaign.getUuid()));

		campaignFormDataImporter.runImport().getStatus();

		assertTrue(campaignFormDataImporter.getErrorMessages().isEmpty());
	}

	private CampaignFormMetaDto createCampaignForm(CampaignDto campaign) throws IOException {

		CampaignFormMetaDto campaignForm;

		String schema = "[{\n" + "  \"type\": \"section\",\n" + "  \"id\": \"totalNumbersSection\"\n" + "}, {\n" + "  \"type\": \"label\",\n"
				+ "  \"id\": \"totalNumbersLabel\",\n" + "  \"caption\": \"<h3>Total Numbers</h3>\"\n" + "}, {\n" + "  \"type\": \"number\",\n"
				+ "  \"id\": \"infected\",\n" + "  \"caption\": \"Number of infected\",\n" + "  \"styles\": [\"row\", \"col-3\"],\n"
				+ "  \"important\": true\n" + "}, {\n" + "  \"type\": \"number\",\n" + "  \"id\": \"withAntibodies\",\n"
				+ "  \"caption\": \"Number persons with antibodies\",\n" + "  \"styles\": [\"row\", \"col-3\"]\n" + "}, {\n" + "  \"type\": \"yes-no\",\n"
				+ "  \"id\": \"mostlyNonBelievers\",\n" + "  \"caption\": \"Mostly non believers?\",\n" + "  \"important\": true\n" + "}]";

		campaignForm = getCampaignFormFacade().buildCampaignFormMetaFromJson("testForm", null, schema, null);

		campaignForm = getCampaignFormFacade().saveCampaignFormMeta(campaignForm);

		return campaignForm;
	}

	private static class CampaignFormDataImporterExtension extends CampaignFormDataImporter {

		List<String> errorMessages = new ArrayList<>();

		public CampaignFormDataImporterExtension(
			File inputFile,
			boolean hasEntityClassRow,
			UserDto currentUser,
			String campaignFormMetaUUID,
			CampaignReferenceDto campaignReferenceDto)
			throws IOException {
			super(inputFile, hasEntityClassRow, currentUser, campaignFormMetaUUID, campaignReferenceDto, ValueSeparator.DEFAULT);
		}

		@Override
		protected void writeImportError(String[] errorLine, String message) {
			errorMessages.add(message);
		}

		protected Writer createErrorReportWriter() {
			return new OutputStreamWriter((new OutputStream() {

				@Override
				public void write(int b) {
					// Do nothing
				}
			}));
		}

		public List<String> getErrorMessages() {
			return errorMessages;
		}

		@Override
		protected Path getErrorReportFolderPath() {
			return Paths.get(System.getProperty("java.io.tmpdir"));
		}
	}
}

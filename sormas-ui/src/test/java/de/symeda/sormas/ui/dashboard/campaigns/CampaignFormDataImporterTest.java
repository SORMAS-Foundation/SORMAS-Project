package de.symeda.sormas.ui.dashboard.campaigns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.campaign.importer.CampaignFormDataImporter;
import de.symeda.sormas.ui.importer.ImportResultStatus;

public class CampaignFormDataImporterTest extends AbstractBeanTest {

	@Test
	@Ignore("Remove ignore once we have replaced H2 - #2526")
	public void testImportCampaignFormData()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user =
			creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Nat", "User", UserRole.NATIONAL_USER);

		final CampaignDto campaign = creator.createCampaign(user);

		final CampaignFormMetaDto campaignForm = creator.createCampaignForm(campaign);

		File csvFile = new File(getClass().getClassLoader().getResource("campaign/sormas_campaign_data_import_test_success.csv").toURI());
		CampaignFormDataImporterExtension campaignFormDataImporter = new CampaignFormDataImporterExtension(
			csvFile,
			false,
			user,
			campaignForm.getUuid(),
			new CampaignReferenceDto(campaign.getUuid()));
		ImportResultStatus importResult = campaignFormDataImporter.runImport();

		assertTrue(campaignFormDataImporter.getErrorMessages().isEmpty());
		assertEquals(ImportResultStatus.COMPLETED, importResult);
	}

	@Test
	public void testImportCampaignFormDataWithWrongDataType()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user =
			creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Nat", "User", UserRole.NATIONAL_USER);

		final CampaignDto campaign = creator.createCampaign(user);

		final CampaignFormMetaDto campaignForm = creator.createCampaignForm(campaign);

		File csvFile = new File(getClass().getClassLoader().getResource("campaign/sormas_campaign_data_import_test_wrong_type.csv").toURI());
		CampaignFormDataImporterExtension campaignFormDataImporter = new CampaignFormDataImporterExtension(
			csvFile,
			false,
			user,
			campaignForm.getUuid(),
			new CampaignReferenceDto(campaign.getUuid()));
		ImportResultStatus importResult = campaignFormDataImporter.runImport();

		assertFalse(campaignFormDataImporter.getErrorMessages().isEmpty());
		assertEquals("Value nonNumeric in column infected does not match expected data type.", campaignFormDataImporter.getErrorMessages().get(0));
	}

	@Test
	@Ignore("Remove ignore once we have replaced H2 - #2526")
	public void testImportCampaignFormDataIgnoringNonExistingColumn()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		final TestDataCreator.RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user =
			creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Nat", "User", UserRole.NATIONAL_USER);

		final CampaignDto campaign = creator.createCampaign(user);

		final CampaignFormMetaDto campaignForm = creator.createCampaignForm(campaign);

		File csvFile = new File(getClass().getClassLoader().getResource("campaign/sormas_campaign_data_import_test_nonexisting_column.csv").toURI());
		CampaignFormDataImporterExtension campaignFormDataImporter = new CampaignFormDataImporterExtension(
			csvFile,
			false,
			user,
			campaignForm.getUuid(),
			new CampaignReferenceDto(campaign.getUuid()));

		campaignFormDataImporter.runImport();

		assertTrue(campaignFormDataImporter.getErrorMessages().isEmpty());
	}

	private static class CampaignFormDataImporterExtension extends CampaignFormDataImporter {

		List<String> errorMessages = new ArrayList<>();

		public CampaignFormDataImporterExtension(
			File inputFile,
			boolean hasEntityClassRow,
			UserDto currentUser,
			String campaignFormMetaUUID,
			CampaignReferenceDto campaignReferenceDto) {
			super(inputFile, hasEntityClassRow, currentUser, campaignFormMetaUUID, campaignReferenceDto);
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
	}
}

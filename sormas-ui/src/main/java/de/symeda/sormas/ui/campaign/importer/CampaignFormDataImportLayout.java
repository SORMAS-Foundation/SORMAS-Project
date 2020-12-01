package de.symeda.sormas.ui.campaign.importer;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportReceiver;

public class CampaignFormDataImportLayout extends AbstractImportLayout {

	private static final long serialVersionUID = 4380218570798586587L;

	public CampaignFormDataImportLayout(CampaignFormMetaReferenceDto campaignForm, CampaignReferenceDto campaignReferenceDto) throws IOException {
		super();

		ImportFacade importFacade = FacadeProvider.getImportFacade();
		importFacade.generateCampaignFormImportTemplateFile(campaignForm.getUuid());

		String templateFileName = DataHelper.sanitizeFileName(campaignReferenceDto.getCaption().replaceAll(" ", "_")) + "_"
			+ DataHelper.sanitizeFileName(campaignForm.getCaption().replaceAll(" ", "_")) + ".csv";
		addDownloadImportTemplateComponent(1, importFacade.getCampaignFormImportTemplateFilePath(), templateFileName);
		addImportCsvComponent(2, new ImportReceiver("_campaign_data_import_", file -> {
			resetDownloadErrorReportButton();

			try {
				CampaignFormDataImporter importer =
					new CampaignFormDataImporter(file, false, currentUser, campaignForm.getUuid(), campaignReferenceDto);
				importer.startImport(this::extendDownloadErrorReportButton, currentUI, false);
			} catch (IOException | CsvValidationException e) {
				new Notification(
					I18nProperties.getString(Strings.headingImportFailed),
					I18nProperties.getString(Strings.messageImportFailed),
					Notification.Type.ERROR_MESSAGE,
					false).show(Page.getCurrent());
			}
		}));
		addDownloadErrorReportComponent(3);
	}

	protected void addDownloadImportTemplateComponent(int step, String templateFilePath, String templateFileName) {
		super.addDownloadImportTemplateComponent(step, templateFilePath, templateFileName);
	}

}

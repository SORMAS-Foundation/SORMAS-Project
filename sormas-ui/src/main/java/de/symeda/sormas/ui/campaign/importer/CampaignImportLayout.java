package de.symeda.sormas.ui.campaign.importer;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import javax.naming.NamingException;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportLayoutComponent;
import de.symeda.sormas.ui.importer.ImportReceiver;

public class CampaignImportLayout extends AbstractImportLayout {

	public CampaignImportLayout(String campaignFormUuid, CampaignReferenceDto campaignReferenceDto) throws IOException, NamingException {
		super();

		ImportFacade importFacade = FacadeProvider.getImportFacade();
		importFacade.generateCampaignFormImportTemplateFile(campaignFormUuid);

		ComboBox cbCampaign = addCampaignDropdown(1);
		addDownloadImportTemplateComponent(2, importFacade.getCampaignFormImportTemplateFilePath(), "sormas_import_campaign_form_data_template.csv");
		addImportCsvComponent(3, new ImportReceiver("_campaign_import_", new Consumer<File>() {

			@Override
			public void accept(File file) {
				resetDownloadErrorReportButton();

				try {
					CampaignFormDataImporter importer =
						new CampaignFormDataImporter(file, false, currentUser, campaignFormUuid, (CampaignReferenceDto) cbCampaign.getValue());
					importer.startImport(new Consumer<StreamResource>() {

						@Override
						public void accept(StreamResource resource) {
							extendDownloadErrorReportButton(resource);
						}
					}, currentUI, false);
				} catch (IOException | CsvValidationException e) {
					new Notification(
						I18nProperties.getString(Strings.headingImportFailed),
						I18nProperties.getString(Strings.messageImportFailed),
						Notification.Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}
		}));
		addDownloadErrorReportComponent(4);
		upload.setEnabled(false);
		this.getComponent(2).setEnabled(false);

		cbCampaign.setValue(campaignReferenceDto);
	}

	protected void addDownloadImportTemplateComponent(int step, String templateFilePath, String templateFileName) {
		super.addDownloadImportTemplateComponent(step, templateFilePath, templateFileName);

	}

	protected ComboBox addCampaignDropdown(int step) {
		String headline = I18nProperties.getString(Strings.headingSelectCampaign);
		ImportLayoutComponent importCsvComponent = new ImportLayoutComponent(step, headline, null, null, null);
		addComponent(importCsvComponent);
		ComboBox cbCampaign = new ComboBox();
		cbCampaign.setItems(FacadeProvider.getCampaignFacade().getAllCampaignsAsReference());
		cbCampaign.addValueChangeListener(event -> {
			if (Objects.nonNull(cbCampaign.getValue())) {
				this.getComponent(2).setEnabled(true);
				upload.setEnabled(true);
			} else {
				this.getComponent(2).setEnabled(false);
				upload.setEnabled(false);
			}
		});
		addComponent(cbCampaign);
		return cbCampaign;
	}
}

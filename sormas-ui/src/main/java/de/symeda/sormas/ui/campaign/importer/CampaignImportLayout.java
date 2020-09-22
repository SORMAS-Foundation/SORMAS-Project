package de.symeda.sormas.ui.campaign.importer;

import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.ImportReceiver;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;


public class CampaignImportLayout extends AbstractImportLayout {


    public CampaignImportLayout(String campaignFormUuid) throws IOException, NamingException {
        super();

        addDownloadResourcesComponent(1, new ClassResource("/SORMAS_Import_Guide.pdf"), new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));

        ImportFacade importFacade = FacadeProvider.getImportFacade();
        importFacade.generateCampaignFormImportTemplateFile(campaignFormUuid);

        addDownloadImportTemplateComponent(
                2,
                importFacade.getCampaignFormImportTemplateFilePath(),
                "sormas_import_campaign_form_data_template.csv");

        addImportCsvComponent(3, new ImportReceiver("_campaign_import_", new Consumer<File>() {

            @Override
            public void accept(File file) {
                resetDownloadErrorReportButton();

                try {
                    CampaignFormDataImporter importer = new CampaignFormDataImporter(file, false, currentUser,campaignFormUuid);
                    importer.startImport(new Consumer<StreamResource>() {

                        @Override
                        public void accept(StreamResource resource) {
                            extendDownloadErrorReportButton(resource);
                        }
                    }, currentUI, false);
                } catch (IOException e) {
                    new Notification(
                            I18nProperties.getString(Strings.headingImportFailed),
                            I18nProperties.getString(Strings.messageImportFailed),
                            Notification.Type.ERROR_MESSAGE,
                            false).show(Page.getCurrent());
                }
            }
        }));
        addDownloadErrorReportComponent(4);
    }

}

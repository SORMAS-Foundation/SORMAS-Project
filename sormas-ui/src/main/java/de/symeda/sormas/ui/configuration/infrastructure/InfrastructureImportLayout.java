package de.symeda.sormas.ui.configuration.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.ui.DateField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportReceiver;
import de.symeda.sormas.ui.importer.PointOfEntryImporter;
import de.symeda.sormas.ui.importer.PopulationDataImporter;

@SuppressWarnings("serial")
public class InfrastructureImportLayout extends AbstractImportLayout {

	public InfrastructureImportLayout(InfrastructureType infrastructureType) {
		super();
		
		DateField dfCollectionDate = new DateField();
		if (infrastructureType == InfrastructureType.POPULATION_DATA) {
			Label lblCollectionDateInfo = new Label(I18nProperties.getString(Strings.infoPopulationCollectionDate));
			addComponent(lblCollectionDateInfo);
			addComponent(dfCollectionDate);
			dfCollectionDate.setRequired(true);
			dfCollectionDate.addValueChangeListener(e -> {
				upload.setEnabled(e.getProperty().getValue() != null);
			});
		}
		
		String templateFilePath = null;
		String templateFileName = null;
		String fileNameAddition = null;
		switch (infrastructureType) {
		case POINT_OF_ENTRY:
			templateFilePath = FacadeProvider.getImportFacade().getPointOfEntryImportTemplateFilePath().toString();
			templateFileName = "sormas_import_point_of_entry_template.csv";
			fileNameAddition = "_point_of_entry_import_";
			break;
		case POPULATION_DATA:
			templateFilePath = FacadeProvider.getImportFacade().getPopulationDataImportTemplateFilePath().toString();
			templateFileName = "sormas_import_population_data_template.csv";
			fileNameAddition = "_population_data_import_";
			break;
		default:
			throw new UnsupportedOperationException("Import is currently not implemented for infrastructure type " + infrastructureType.name());
		}

		addDownloadImportTemplateComponent(1, templateFilePath, templateFileName);		
		addImportCsvComponent(2, new ImportReceiver(fileNameAddition, new Consumer<File>() {
			@Override
			public void accept(File file) {
				resetDownloadErrorReportButton();
				
				try {
					DataImporter importer;
					switch (infrastructureType) {
					case POINT_OF_ENTRY:
						importer = new PointOfEntryImporter(file, currentUser, currentUI);
						break;
					case POPULATION_DATA:
						importer = new PopulationDataImporter(file, currentUser, currentUI, dfCollectionDate.getValue());
						break;
					default:
						throw new UnsupportedOperationException("Import is currently not implemented for infrastructure type " + infrastructureType.name());
					}
					
					importer.startImport(new Consumer<StreamResource>() {
						@Override
						public void accept(StreamResource resource) {
							extendDownloadErrorReportButton(resource);
						}
					});
				} catch (IOException e) {
					new Notification(I18nProperties.getString(Strings.headingImportFailed), I18nProperties.getString(Strings.messageImportFailed), Type.ERROR_MESSAGE, false).show(Page.getCurrent());
				}
			}
		}));
		
		if (infrastructureType == InfrastructureType.POPULATION_DATA) {
			upload.setEnabled(false);
		}
		
		addDownloadErrorReportComponent(3);
	}

}

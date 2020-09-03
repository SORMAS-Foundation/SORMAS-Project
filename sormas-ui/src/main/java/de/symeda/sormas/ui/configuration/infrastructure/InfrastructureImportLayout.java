package de.symeda.sormas.ui.configuration.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.vaadin.server.ClassResource;
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
import de.symeda.sormas.ui.importer.InfrastructureImporter;
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
		case COMMUNITY:
			templateFilePath = FacadeProvider.getImportFacade().getCommunityImportTemplateFilePath();
			templateFileName = "sormas_import_community_template.csv";
			fileNameAddition = "_community_import_";
			break;
		case DISTRICT:
			templateFilePath = FacadeProvider.getImportFacade().getDistrictImportTemplateFilePath();
			templateFileName = "sormas_import_district_template.csv";
			fileNameAddition = "_district_import_";
			break;
		case FACILITY:
			templateFilePath = FacadeProvider.getImportFacade().getFacilityLaboratoryImportTemplateFilePath();
			templateFileName = "sormas_import_facility_laboratory_template.csv";
			fileNameAddition = "_facility_laboratory_import_";
			break;
		case POINT_OF_ENTRY:
			templateFilePath = FacadeProvider.getImportFacade().getPointOfEntryImportTemplateFilePath();
			templateFileName = "sormas_import_point_of_entry_template.csv";
			fileNameAddition = "_point_of_entry_import_";
			break;
		case POPULATION_DATA:
			templateFilePath = FacadeProvider.getImportFacade().getPopulationDataImportTemplateFilePath();
			templateFileName = "sormas_import_population_data_template.csv";
			fileNameAddition = "_population_data_import_";
			break;
		case REGION:
			templateFilePath = FacadeProvider.getImportFacade().getRegionImportTemplateFilePath();
			templateFileName = "sormas_import_region_template.csv";
			fileNameAddition = "_region_import_";
			break;
		case AREA:
			templateFilePath = FacadeProvider.getImportFacade().getAreaImportTemplateFilePath();
			templateFileName = "sormas_import_area_template.csv";
			fileNameAddition = "_area_import_";
			break;
		default:
			throw new UnsupportedOperationException("Import is currently not implemented for infrastructure type " + infrastructureType.name());
		}

		addDownloadResourcesComponent(
			1,
			new ClassResource("/SORMAS_Infrastructure_Import_Guide.pdf"),
			new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));
		addDownloadImportTemplateComponent(2, templateFilePath, templateFileName);
		addImportCsvComponent(3, new ImportReceiver(fileNameAddition, new Consumer<File>() {

			@Override
			public void accept(File file) {
				resetDownloadErrorReportButton();

				try {
					DataImporter importer;
					switch (infrastructureType) {
					case COMMUNITY:
						importer = new InfrastructureImporter(file, currentUser, InfrastructureType.COMMUNITY);
						break;
					case DISTRICT:
						importer = new InfrastructureImporter(file, currentUser, InfrastructureType.DISTRICT);
						break;
					case FACILITY:
						importer = new InfrastructureImporter(file, currentUser, InfrastructureType.FACILITY);
						break;
					case POINT_OF_ENTRY:
						importer = new InfrastructureImporter(file, currentUser, InfrastructureType.POINT_OF_ENTRY);
						break;
					case POPULATION_DATA:
						importer = new PopulationDataImporter(file, currentUser, dfCollectionDate.getValue());
						break;
					case REGION:
						importer = new InfrastructureImporter(file, currentUser, InfrastructureType.REGION);
						break;
					case AREA:
						importer = new InfrastructureImporter(file, currentUser, InfrastructureType.AREA);
						break;
					default:
						throw new UnsupportedOperationException(
							"Import is currently not implemented for infrastructure type " + infrastructureType.name());
					}

					importer.startImport(new Consumer<StreamResource>() {

						@Override
						public void accept(StreamResource resource) {
							extendDownloadErrorReportButton(resource);
						}
					}, currentUI, true);
				} catch (IOException e) {
					new Notification(
						I18nProperties.getString(Strings.headingImportFailed),
						I18nProperties.getString(Strings.messageImportFailed),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}
		}));

		if (infrastructureType == InfrastructureType.POPULATION_DATA) {
			upload.setEnabled(false);
		}

		addDownloadErrorReportComponent(4);
	}
}

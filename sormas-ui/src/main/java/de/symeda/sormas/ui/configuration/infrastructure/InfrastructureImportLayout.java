package de.symeda.sormas.ui.configuration.infrastructure;

import java.io.IOException;

import com.opencsv.exceptions.CsvValidationException;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.ui.DateField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.ui.caze.importer.CountryImporter;
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
			dfCollectionDate.addValueChangeListener(e -> upload.setEnabled(e.getProperty().getValue() != null));
		}

		String templateFilePath;
		String templateFileName;
		String fileNameAddition;
		ImportFacade importFacade = FacadeProvider.getImportFacade();
		switch (infrastructureType) {
		case COMMUNITY:
			templateFilePath = importFacade.getCommunityImportTemplateFilePath();
			templateFileName = importFacade.getCommunityImportTemplateFileName();
			fileNameAddition = "_community_import_";
			break;
		case DISTRICT:
			templateFilePath = importFacade.getDistrictImportTemplateFilePath();
			templateFileName = importFacade.getDistrictImportTemplateFileName();
			fileNameAddition = "_district_import_";
			break;
		case FACILITY:
			templateFilePath = importFacade.getFacilityImportTemplateFilePath();
			templateFileName = importFacade.getFacilityImportTemplateFileName();
			fileNameAddition = "_facility_import_";
			break;
		case POINT_OF_ENTRY:
			templateFilePath = importFacade.getPointOfEntryImportTemplateFilePath();
			templateFileName = importFacade.getPointOfEntryImportTemplateFileName();
			fileNameAddition = "_point_of_entry_import_";
			break;
		case POPULATION_DATA:
			templateFilePath = importFacade.getPopulationDataImportTemplateFilePath();
			templateFileName = importFacade.getPopulationDataImportTemplateFileName();
			fileNameAddition = "_population_data_import_";
			break;
		case COUNTRY:
			templateFilePath = importFacade.getCountryImportTemplateFilePath();
			templateFileName = importFacade.getCountryImportTemplateFileName();
			fileNameAddition = "_country_import_";
			break;
		case REGION:
			templateFilePath = importFacade.getRegionImportTemplateFilePath();
			templateFileName = importFacade.getRegionImportTemplateFileName();
			fileNameAddition = "_region_import_";
			break;
		case AREA:
			templateFilePath = importFacade.getAreaImportTemplateFilePath();
			templateFileName = importFacade.getAreaImportTemplateFileName();
			fileNameAddition = "_area_import_";
			break;
		case SUBCONTINENT:
			templateFilePath = importFacade.getSubcontinentImportTemplateFilePath();
			templateFileName = importFacade.getSubcontinentImportTemplateFileName();
			fileNameAddition = "_subcontinent_import_";
			break;
		case CONTINENT:
			templateFilePath = importFacade.getContinentImportTemplateFilePath();
			templateFileName = importFacade.getContinentImportTemplateFileName();
			fileNameAddition = "_continent_import_";
			break;
		default:
			throw new UnsupportedOperationException("Import is currently not implemented for infrastructure type " + infrastructureType.name());
		}

		addDownloadResourcesComponent(1, new ClassResource("/SORMAS_Infrastructure_Import_Guide.pdf"));
		addDownloadImportTemplateComponent(2, templateFilePath, templateFileName);

		if (infrastructureType == InfrastructureType.POPULATION_DATA) {
			addImportCsvComponent(3, new ImportReceiver(fileNameAddition, file -> {
				resetDownloadErrorReportButton();

				try {
					DataImporter importer =
						new PopulationDataImporter(file, currentUser, dfCollectionDate.getValue(), (ValueSeparator) separator.getValue());
					importer.startImport(this::extendDownloadErrorReportButton, currentUI, true);
				} catch (IOException | CsvValidationException e) {
					new Notification(
						I18nProperties.getString(Strings.headingImportFailed),
						I18nProperties.getString(Strings.messageImportFailed),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}));
			upload.setEnabled(false);
		} else {
			addImportCsvComponentWithOverwrite(3, allowOverwrite -> new ImportReceiver(fileNameAddition, file -> {
				resetDownloadErrorReportButton();

				try {
					DataImporter importer;
					switch (infrastructureType) {
					case COMMUNITY:
						importer = new InfrastructureImporter(
							file,
							currentUser,
							InfrastructureType.COMMUNITY,
							allowOverwrite,
							(ValueSeparator) separator.getValue());
						break;
					case DISTRICT:
						importer = new InfrastructureImporter(
							file,
							currentUser,
							InfrastructureType.DISTRICT,
							allowOverwrite,
							(ValueSeparator) separator.getValue());
						break;
					case FACILITY:
						importer = new InfrastructureImporter(
							file,
							currentUser,
							InfrastructureType.FACILITY,
							allowOverwrite,
							(ValueSeparator) separator.getValue());
						break;
					case POINT_OF_ENTRY:
						importer = new InfrastructureImporter(
							file,
							currentUser,
							InfrastructureType.POINT_OF_ENTRY,
							allowOverwrite,
							(ValueSeparator) separator.getValue());
						break;
					case COUNTRY:
						importer = new CountryImporter(file, currentUser, allowOverwrite, (ValueSeparator) separator.getValue());
						break;
					case REGION:
						importer = new InfrastructureImporter(
							file,
							currentUser,
							InfrastructureType.REGION,
							allowOverwrite,
							(ValueSeparator) separator.getValue());
						break;
					case AREA:
						importer = new InfrastructureImporter(
							file,
							currentUser,
							InfrastructureType.AREA,
							allowOverwrite,
							(ValueSeparator) separator.getValue());
						break;
					case SUBCONTINENT:
						importer = new InfrastructureImporter(
							file,
							currentUser,
							InfrastructureType.SUBCONTINENT,
							allowOverwrite,
							(ValueSeparator) separator.getValue());
						break;
					case CONTINENT:
						importer = new InfrastructureImporter(
							file,
							currentUser,
							InfrastructureType.CONTINENT,
							allowOverwrite,
							(ValueSeparator) separator.getValue());
						break;
					default:
						throw new UnsupportedOperationException(
							"Import is currently not implemented for infrastructure type " + infrastructureType.name());
					}

					importer.startImport(this::extendDownloadErrorReportButton, currentUI, true);
				} catch (IOException | CsvValidationException e) {
					new Notification(
						I18nProperties.getString(Strings.headingImportFailed),
						I18nProperties.getString(Strings.messageImportFailed),
						Type.ERROR_MESSAGE,
						false).show(Page.getCurrent());
				}
			}));
		}

		addDownloadErrorReportComponent(4);
	}
}

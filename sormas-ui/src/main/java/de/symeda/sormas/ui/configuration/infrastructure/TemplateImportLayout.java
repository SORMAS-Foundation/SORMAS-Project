package de.symeda.sormas.ui.configuration.infrastructure;

import com.vaadin.server.ClassResource;
import com.vaadin.v7.ui.DateField;

import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.importer.TemplateReceiver;

@SuppressWarnings("serial")
public class TemplateImportLayout extends AbstractImportLayout {

	public TemplateImportLayout() {

		super();

		DateField dfCollectionDate = new DateField();

		String fileNameAddition = "_district_import_";

		addDownloadResourcesComponent(
			1,
			new ClassResource("/SORMAS_Infrastructure_Import_Guide.pdf"),
			new ClassResource("/doc/SORMAS_Data_Dictionary.xlsx"));

		String headline = "Import Template i18n required";
		String infoText = "Info i18n required";

		addImportTemplateComponent(2, new TemplateReceiver());
	}
}

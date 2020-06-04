package de.symeda.sormas.ui.caze.exporter;

import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.ui.ControllerProvider;

@SuppressWarnings("serial")
public class CaseExportConfigurationsLayout extends VerticalLayout {

	private Label lblDescription;
	private Button btnNewExportConfiguration;
	private Button btnExport;
	private CaseExportConfigurationsGrid grid;

	public CaseExportConfigurationsLayout(Runnable closeCallback) {	
		lblDescription = new Label(I18nProperties.getString(Strings.infoCustomCaseExport));
		lblDescription.setWidth(100, Unit.PERCENTAGE);
		addComponent(lblDescription);

		btnNewExportConfiguration = new Button(I18nProperties.getCaption(Captions.exportNewExportConfiguration));
		btnNewExportConfiguration.setIcon(VaadinIcons.PLUS);
		btnNewExportConfiguration.setStyleName(ValoTheme.BUTTON_PRIMARY);
		addComponent(btnNewExportConfiguration);
		setComponentAlignment(btnNewExportConfiguration, Alignment.MIDDLE_RIGHT);

		btnNewExportConfiguration.addClickListener(e -> {
			ControllerProvider.getCaseController().openEditExportConfigurationWindow(grid, null);
		});

		grid = new CaseExportConfigurationsGrid();
		grid.setWidth(100, Unit.PERCENTAGE);
		addComponent(grid);

		Button btnClose = new Button(I18nProperties.getCaption(Captions.actionClose));
		btnClose.addClickListener(e -> closeCallback.run());
		addComponent(btnClose);
		setComponentAlignment(btnClose, Alignment.MIDDLE_RIGHT);
	}
	
	public Button getExportButton() {
		return btnExport;
	}
	
	public void setExportCallback(Consumer<ExportConfigurationDto> exportCallback) {
		grid.setExportCallback(exportCallback);
	}
	
}

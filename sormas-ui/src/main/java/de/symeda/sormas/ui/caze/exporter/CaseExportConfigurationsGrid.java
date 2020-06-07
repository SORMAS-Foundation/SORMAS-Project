package de.symeda.sormas.ui.caze.exporter;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;

@SuppressWarnings("serial")
public class CaseExportConfigurationsGrid extends Grid<ExportConfigurationDto> {

	public static final String COLUMN_ACTIONS = "actions";

//	private final String userUuid;
	private Consumer<ExportConfigurationDto> exportCallback;

	public CaseExportConfigurationsGrid() {

		buildGrid();
		reload();
	}

	private void buildGrid() {

		setSelectionMode(SelectionMode.NONE);
		setHeightMode(HeightMode.ROW);

		addColumn(ExportConfigurationDto::getName)
			.setCaption(I18nProperties.getPrefixCaption(ExportConfigurationDto.I18N_PREFIX, ExportConfigurationDto.NAME))
			.setExpandRatio(1);

		addComponentColumn(config -> {
			return buildButtonLayout(config);
		}).setId(COLUMN_ACTIONS).setCaption("");
	}

	public void reload() {
		List<ExportConfigurationDto> configs = FacadeProvider.getExportFacade().getExportConfigurations();
		setItems(configs);
		setHeightByRows(configs.size() > 0 ? (configs.size() <= 10 ? configs.size() : 10) : 1);
	}

	private HorizontalLayout buildButtonLayout(ExportConfigurationDto config) {

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);

		Button btnExport = ButtonHelper.createIconButtonWithCaption(
			config.getUuid() + "-download",
			null,
			VaadinIcons.DOWNLOAD,
			e -> exportCallback.accept(config),
			ValoTheme.BUTTON_PRIMARY);
		layout.addComponent(btnExport);

		Button btnEdit = ButtonHelper.createIconButtonWithCaption(config.getUuid() + "-edit", null, VaadinIcons.EDIT, e -> {
			ControllerProvider.getCaseController().openEditExportConfigurationWindow(this, config);
		});
		layout.addComponent(btnEdit);

		Button btnDelete = ButtonHelper.createIconButtonWithCaption(config.getUuid() + "-delete", null, VaadinIcons.TRASH, e -> {
			FacadeProvider.getExportFacade().deleteExportConfiguration(config.getUuid());
			new Notification(null, I18nProperties.getString(Strings.messageExportConfigurationDeleted), Type.WARNING_MESSAGE, false)
				.show(Page.getCurrent());
			reload();
		});
		layout.addComponent(btnDelete);

		return layout;
	}

	public void setExportCallback(Consumer<ExportConfigurationDto> exportCallback) {
		this.exportCallback = exportCallback;
	}
}

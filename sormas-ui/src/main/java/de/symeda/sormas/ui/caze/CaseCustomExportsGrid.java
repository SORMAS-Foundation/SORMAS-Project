package de.symeda.sormas.ui.caze;

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
import com.vaadin.ui.components.grid.SingleSelectionModel;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.ui.ControllerProvider;

@SuppressWarnings("serial")
public class CaseCustomExportsGrid extends Grid<ExportConfigurationDto> {

	public static final String COLUMN_ACTIONS = "actions";
	
	private final String userUuid;
	private Consumer<Boolean> selectionChangeCallback;
	
	public CaseCustomExportsGrid(String userUuid) {
		this.userUuid = userUuid;
		buildGrid();
		reload();
	}
	
	private void buildGrid() {
		SingleSelectionModel<ExportConfigurationDto> selectionModel = (SingleSelectionModel<ExportConfigurationDto>) setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);
		
		addColumn(ExportConfigurationDto::getName)
		.setCaption(I18nProperties.getPrefixCaption(ExportConfigurationDto.I18N_PREFIX, ExportConfigurationDto.NAME))
		.setExpandRatio(1);
		
		addComponentColumn(config -> {
			return buildButtonLayout(config);
		}).setId(COLUMN_ACTIONS).setCaption("");
		
		selectionModel.addSingleSelectionListener(e -> {
			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(e.getSelectedItem().isPresent());
			}
		});
	}
	
	public void reload() {
		List<ExportConfigurationDto> configs = FacadeProvider.getExportFacade().getExportConfigurations(userUuid);
		setItems(configs);
		setHeightByRows(configs.size() > 0 ? (configs.size() <= 10 ? configs.size() : 10) : 1);
	}
	
	private HorizontalLayout buildButtonLayout(ExportConfigurationDto config) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		
		Button btnEdit = new Button(VaadinIcons.EDIT);
		btnEdit.setStyleName(ValoTheme.BUTTON_PRIMARY);
		layout.addComponent(btnEdit);
		btnEdit.addClickListener(e -> {
			ControllerProvider.getCaseController().openEditExportConfigurationWindow(this, config);
		});
		
		Button btnDelete = new Button(VaadinIcons.TRASH);
		layout.addComponent(btnDelete);
		btnDelete.addClickListener(e -> {
			FacadeProvider.getExportFacade().deleteExportConfiguration(config.getUuid());
			new Notification(null, I18nProperties.getString(Strings.messageExportConfigurationDeleted), Type.WARNING_MESSAGE, false).show(Page.getCurrent());
			reload();
		});
		
		return layout;
	}
	
	public void setSelectionChangeCallback(Consumer<Boolean> selectionChangeCallback) {
		this.selectionChangeCallback = selectionChangeCallback;
	}
	
	public ExportConfigurationDto getSelectedExportConfiguration() {
		return ((SingleSelectionModel<ExportConfigurationDto>) getSelectionModel()).getSelectedItem().orElse(null);
	}
	
}

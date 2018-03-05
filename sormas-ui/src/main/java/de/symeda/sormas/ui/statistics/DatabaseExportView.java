package de.symeda.sormas.ui.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.export.DatabaseTable;
import de.symeda.sormas.api.export.DatabaseTableType;
import de.symeda.sormas.api.utils.ExportErrorException;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class DatabaseExportView extends AbstractStatisticsView {

	private static final long serialVersionUID = 1557269026685787333L;

	public static final String VIEW_NAME = "statistics/database-export";

	private VerticalLayout databaseExportLayout;
	private Map<CheckBox, DatabaseTable> databaseTableToggles;

	public DatabaseExportView() {
		super(VIEW_NAME);

		databaseTableToggles = new HashMap<>();
		databaseExportLayout = new VerticalLayout();
		Label infoLabel = new Label("Please select the database tables you want to export.");
		HorizontalLayout blubb = new HorizontalLayout();
		blubb.setSpacing(true);
		blubb.addComponent(infoLabel);
		blubb.setComponentAlignment(infoLabel, Alignment.MIDDLE_LEFT);
		blubb.addComponent(createSelectionButtonsLayout());
		databaseExportLayout.addComponent(blubb);
		databaseExportLayout.addComponent(createDatabaseTablesLayout());
		Button exportButton = new Button("Export");
		CssStyles.style(exportButton, ValoTheme.BUTTON_PRIMARY);

		exportButton.addClickListener(e -> {
			List<DatabaseTable> tablesToExport = new ArrayList<>();
			for (CheckBox checkBox : databaseTableToggles.keySet()) {
				if (checkBox.getValue() == true) {
					tablesToExport.add(databaseTableToggles.get(checkBox));
				}
			}
			
			try {
				String zipPath = FacadeProvider.getExportFacade().generateDatabaseExportArchive(tablesToExport);
				showDownloadDatabaseDialog(zipPath);
			} catch (ExportErrorException ex) {
				new Notification("Database export failed", "Please contact an admin and notify them about this problem.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
			}
		});

		databaseExportLayout.addComponent(exportButton);
		databaseExportLayout.setMargin(true);
		databaseExportLayout.setSpacing(true);

		addComponent(databaseExportLayout);
	}

	private HorizontalLayout createSelectionButtonsLayout() {
		HorizontalLayout selectionButtonsLayout = new HorizontalLayout();
		selectionButtonsLayout.setSpacing(true);
		Button selectAll = new Button("Select all");
		CssStyles.style(selectAll, ValoTheme.BUTTON_LINK);
		selectAll.addClickListener(e -> {
			for (CheckBox checkBox : databaseTableToggles.keySet()) {
				checkBox.setValue(true);
			}
		});
		selectionButtonsLayout.addComponent(selectAll);
		Button deselectAll = new Button ("Deselect all");
		CssStyles.style(deselectAll, ValoTheme.BUTTON_LINK);
		deselectAll.addClickListener(e -> {
			for (CheckBox checkBox : databaseTableToggles.keySet()) {
				checkBox.setValue(false);
			}
		});
		selectionButtonsLayout.addComponent(deselectAll);
		return selectionButtonsLayout;
	}

	private HorizontalLayout createDatabaseTablesLayout() {
		HorizontalLayout databaseTablesLayout = new HorizontalLayout();
		databaseTablesLayout.setSpacing(true);
		
		VerticalLayout sormasDataLayout = new VerticalLayout();
		Label sormasDataHeadline = new Label("SORMAS data");
		CssStyles.style(sormasDataHeadline, CssStyles.H4);
		sormasDataLayout.addComponent(sormasDataHeadline);
		
		VerticalLayout infrastructureDataLayout = new VerticalLayout();
		Label infrastructureDataHeadline = new Label("Infrastructure data");
		CssStyles.style(infrastructureDataHeadline, CssStyles.H4);
		infrastructureDataLayout.addComponent(infrastructureDataHeadline);
		
		for (DatabaseTable databaseTable : DatabaseTable.values()) {
			CheckBox checkBox = new CheckBox(databaseTable.toString());
			int indent = getIndent(databaseTable);
			if (indent == 1) {
				CssStyles.style(checkBox, CssStyles.INDENT_LEFT_1);
			} else if (indent == 2) {
				CssStyles.style(checkBox, CssStyles.INDENT_LEFT_2);
			}

			if (databaseTable.getDatabaseTableType() == DatabaseTableType.SORMAS) {
				sormasDataLayout.addComponent(checkBox);
			} else {
				infrastructureDataLayout.addComponent(checkBox);
			}
			databaseTableToggles.put(checkBox, databaseTable);
		}
		
		databaseTablesLayout.addComponent(sormasDataLayout);
		databaseTablesLayout.addComponent(infrastructureDataLayout);
		return databaseTablesLayout;
	}

	private int getIndent(DatabaseTable databaseTable) {
		int indent = 0;
		while (databaseTable.getParentTable() != null) {
			indent++;
			databaseTable = databaseTable.getParentTable();
		}
		return indent;
	}

	private void showDownloadDatabaseDialog(final String zipPath) {
		Window popupWindow = VaadinUiUtil.createPopupWindow();
		popupWindow.setWidth(640, Unit.PIXELS);
		popupWindow.setCaption("Download database export");
		
		VerticalLayout popupContent = new VerticalLayout();
		popupContent.setSpacing(true);
		popupContent.setMargin(true);
		
		Label infoLabel = new Label("The database export has been successfully generated. Please click the 'Download' button below to save the export to your computer.");
		popupContent.addComponent(infoLabel);
		
		ConfirmationComponent confirmationComponent = new ConfirmationComponent() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onConfirm() {
				popupWindow.close();
			}
			@Override
			protected void onCancel() {
				popupWindow.close();
			}
		};

		Button confirmButton = confirmationComponent.getConfirmButton();
		confirmButton.setCaption("Download");
		CssStyles.style(confirmButton, ValoTheme.BUTTON_PRIMARY);
		File zipFile = new File(zipPath);
		StreamResource streamResource = DownloadUtil.createStreamResource(zipFile, zipPath.substring(zipPath.lastIndexOf("\\") + 1), "application/zip");
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(confirmButton);
		
		confirmationComponent.getCancelButton().setCaption("Cancel");	
		
		popupContent.addComponent(confirmationComponent);
		popupContent.setComponentAlignment(confirmationComponent, Alignment.BOTTOM_RIGHT);		
		
		popupWindow.setContent(popupContent);
		getUI().addWindow(popupWindow);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
	}

}

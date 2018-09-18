package de.symeda.sormas.ui.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.importexport.DatabaseTableType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DownloadUtil;

public class DatabaseExportView extends AbstractStatisticsView {

	private static final long serialVersionUID = 1557269026685787333L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/database-export";

	private VerticalLayout databaseExportLayout;
	private Map<CheckBox, DatabaseTable> databaseTableToggles;

	public DatabaseExportView() {
		super(VIEW_NAME);

		databaseTableToggles = new HashMap<>();
		databaseExportLayout = new VerticalLayout();
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		Label infoLabel = new Label("Please select the database tables you want to export.");
		headerLayout.addComponent(infoLabel);
		headerLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_LEFT);
		headerLayout.addComponent(createSelectionButtonsLayout());
		databaseExportLayout.addComponent(headerLayout);
		databaseExportLayout.addComponent(createDatabaseTablesLayout());
		Button exportButton = new Button("Export", FontAwesome.DOWNLOAD);
		CssStyles.style(exportButton, ValoTheme.BUTTON_PRIMARY);
		StreamResource streamResource = DownloadUtil.createDatabaseExportStreamResource(this, "sormas_export_" + DateHelper.formatDateForExport(new Date()) + ".zip", "application/zip");
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(exportButton);
		databaseExportLayout.addComponent(exportButton);
		databaseExportLayout.setMargin(true);
		databaseExportLayout.setSpacing(true);

		addComponent(databaseExportLayout);
	}

	public void showExportErrorNotification() {
		new Notification("Database export failed", "Please contact an admin and notify them about this problem.", Type.ERROR_MESSAGE, false).show(Page.getCurrent());
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
		
		Button selectAllSormasData = new Button("Select all SORMAS data");
		CssStyles.style(selectAllSormasData, ValoTheme.BUTTON_LINK);
		selectAllSormasData.addClickListener(e -> {
			for (CheckBox checkBox : databaseTableToggles.keySet()) {
				if (databaseTableToggles.get(checkBox).getDatabaseTableType() == DatabaseTableType.SORMAS) {
					checkBox.setValue(true);
				}
			}
		});
		selectionButtonsLayout.addComponent(selectAllSormasData);
		
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

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
	}

	public Map<CheckBox, DatabaseTable> getDatabaseTableToggles() {
		return databaseTableToggles;
	}

}

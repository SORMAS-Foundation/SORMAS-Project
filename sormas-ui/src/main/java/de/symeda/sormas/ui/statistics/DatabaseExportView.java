/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.DatabaseTable;
import de.symeda.sormas.api.importexport.DatabaseTableType;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.ButtonHelper;
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
		databaseExportLayout.setSpacing(false);
		databaseExportLayout.setMargin(false);
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setSpacing(true);
		headerLayout.setMargin(false);
		Label infoLabel = new Label(I18nProperties.getString(Strings.infoDatabaseExportTables));
		headerLayout.addComponent(infoLabel);
		headerLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_LEFT);
		headerLayout.addComponent(createSelectionButtonsLayout());
		databaseExportLayout.addComponent(headerLayout);
		databaseExportLayout.addComponent(createDatabaseTablesLayout());

		Button exportButton = ButtonHelper.createIconButton(Captions.export, VaadinIcons.DOWNLOAD, null, ValoTheme.BUTTON_PRIMARY);

		StreamResource streamResource = DownloadUtil.createDatabaseExportStreamResource(this, "sormas_export_" + DateHelper.formatDateForExport(new Date()) + ".zip", "application/zip");
		FileDownloader fileDownloader = new FileDownloader(streamResource);
		fileDownloader.extend(exportButton);

		databaseExportLayout.addComponent(exportButton);
		databaseExportLayout.setMargin(true);
		databaseExportLayout.setSpacing(true);

		addComponent(databaseExportLayout);
	}

	public void showExportErrorNotification() {
		new Notification(I18nProperties.getString(Strings.headingDatabaseExportFailed), 
				I18nProperties.getString(Strings.messageDatabaseExportFailed), Type.ERROR_MESSAGE, false).show(Page.getCurrent());
	}
	
	private HorizontalLayout createSelectionButtonsLayout() {
		HorizontalLayout selectionButtonsLayout = new HorizontalLayout();
		selectionButtonsLayout.setMargin(false);
		selectionButtonsLayout.setSpacing(true);
		
		Button selectAll = ButtonHelper.createButton(Captions.actionSelectAll, e -> {
			for (CheckBox checkBox : databaseTableToggles.keySet()) {
				checkBox.setValue(true);
			}
		}, ValoTheme.BUTTON_LINK);

		selectionButtonsLayout.addComponent(selectAll);
		
		Button selectAllSormasData = ButtonHelper.createButton(Captions.exportSelectSormasData, e -> {
			for (CheckBox checkBox : databaseTableToggles.keySet()) {
				if (databaseTableToggles.get(checkBox).getDatabaseTableType() == DatabaseTableType.SORMAS) {
					checkBox.setValue(true);
				} else {
					checkBox.setValue(false);
				}
			}
		}, ValoTheme.BUTTON_LINK);

		selectionButtonsLayout.addComponent(selectAllSormasData);
		
		Button deselectAll = ButtonHelper.createButton(Captions.actionDeselectAll, e -> {
			for (CheckBox checkBox : databaseTableToggles.keySet()) {
				checkBox.setValue(false);
			}
		}, ValoTheme.BUTTON_LINK);

		selectionButtonsLayout.addComponent(deselectAll);
		
		return selectionButtonsLayout;
	}

	private HorizontalLayout createDatabaseTablesLayout() {
		HorizontalLayout databaseTablesLayout = new HorizontalLayout();
		databaseTablesLayout.setMargin(false);
		databaseTablesLayout.setSpacing(true);
		
		VerticalLayout sormasDataLayout = new VerticalLayout();
		sormasDataLayout.setMargin(false);
		sormasDataLayout.setSpacing(false);
		Label sormasDataHeadline = new Label(I18nProperties.getCaption(Captions.exportSormasData));
		CssStyles.style(sormasDataHeadline, CssStyles.H4);
		sormasDataLayout.addComponent(sormasDataHeadline);
		
		VerticalLayout infrastructureDataLayout = new VerticalLayout();
		infrastructureDataLayout.setMargin(false);
		infrastructureDataLayout.setSpacing(false);
		Label infrastructureDataHeadline = new Label(I18nProperties.getCaption(Captions.exportInfrastructureData));
		CssStyles.style(infrastructureDataHeadline, CssStyles.H4);
		infrastructureDataLayout.addComponent(infrastructureDataHeadline);
		
		for (DatabaseTable databaseTable : DatabaseTable.values()) {
			CheckBox checkBox = new CheckBox(databaseTable.toString());
			int indent = getIndent(databaseTable);
			if (indent == 1) {
				CssStyles.style(checkBox, CssStyles.INDENT_LEFT_1);
			} else if (indent == 2) {
				CssStyles.style(checkBox, CssStyles.INDENT_LEFT_2);
			} else if (indent == 3) {
				CssStyles.style(checkBox, CssStyles.INDENT_LEFT_3);
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

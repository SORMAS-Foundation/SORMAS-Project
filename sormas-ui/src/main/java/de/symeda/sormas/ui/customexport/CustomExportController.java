/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.customexport;

import java.util.List;
import java.util.function.Function;

import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportType;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.utils.ContactDownloadUtil;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CustomExportController {

	public void openContactExportWindow(ContactCriteria contactCriteria) {
		Window customExportWindow = VaadinUiUtil.createPopupWindow();
		ExportConfigurationsLayout customExportsLayout = new ExportConfigurationsLayout(
			ExportType.CONTACT,
			ImportExportUtils.getContactExportProperties(),
			ContactDownloadUtil::getPropertyCaption,
			customExportWindow::close);
		customExportsLayout.setExportCallback((exportConfig) -> {
			// TODO support XLSX #839
			Page.getCurrent().open(ContactDownloadUtil.createContactExportResourceCSV(contactCriteria, exportConfig), null, true);
		});
		customExportWindow.setWidth(1024, Sizeable.Unit.PIXELS);
		customExportWindow.setCaption(I18nProperties.getCaption(Captions.exportCustom));
		customExportWindow.setContent(customExportsLayout);
		UI.getCurrent().addWindow(customExportWindow);
	}

	public void openEditExportConfigurationWindow(
		ExportConfigurationsGrid grid,
		ExportConfigurationDto config,
		List<DataHelper.Pair<String, ExportGroupType>> availableProperties,
		Function<String, String> propertyCationProvider) {

		Window newExportWindow = VaadinUiUtil.createPopupWindow();
		ExportConfigurationEditLayout editLayout =
			new ExportConfigurationEditLayout(config, availableProperties, propertyCationProvider, (exportConfiguration) -> {
				FacadeProvider.getExportFacade().saveExportConfiguration(exportConfiguration);
				newExportWindow.close();
				new Notification(null, I18nProperties.getString(Strings.messageExportConfigurationSaved), Notification.Type.WARNING_MESSAGE, false)
					.show(Page.getCurrent());
				grid.reload();
			}, () -> {
				newExportWindow.close();
				grid.reload();
			});
		newExportWindow.setWidth(1024, Sizeable.Unit.PIXELS);
		newExportWindow.setCaption(I18nProperties.getCaption(Captions.exportNewExportConfiguration));
		newExportWindow.setContent(editLayout);
		UI.getCurrent().addWindow(newExportWindow);
	}
}

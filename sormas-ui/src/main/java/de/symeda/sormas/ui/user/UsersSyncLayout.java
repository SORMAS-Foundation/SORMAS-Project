/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.user;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.importer.AbstractImportLayout;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * @author Alex Vidrean
 * @since 11-Dec-20
 */
@SuppressWarnings("serial")
public class UsersSyncLayout extends AbstractImportLayout {

	public UsersSyncLayout() {
		super();

		addSyncUsersComponent(1, (event) -> {
			UserSyncHandler userSyncHandler = new UserSyncHandler(currentUser.toReference());
			userSyncHandler.startSync(this::extendDownloadErrorReportButton, currentUI);
		});

		addDownloadErrorReportComponent(2);
	}

	protected void addSyncUsersComponent(int step, Button.ClickListener clickListener) {

		Label headlineLabel =
			new Label(I18nProperties.getString(Strings.step) + " " + step + ": " + I18nProperties.getString(Strings.headingSyncUsers));
		CssStyles.style(headlineLabel, CssStyles.H3);
		addComponent(headlineLabel);

		Label infoTextLabel = new Label(I18nProperties.getString(Strings.infoSyncUsers), ContentMode.HTML);
		addComponent(infoTextLabel);

		Button button = ButtonHelper.createIconButtonWithCaption(
			"import-step-" + step,
			Captions.sync,
			VaadinIcons.REFRESH,
			clickListener,
			ValoTheme.BUTTON_PRIMARY,
			CssStyles.VSPACE_TOP_3);

		addComponent(button);
	}

}

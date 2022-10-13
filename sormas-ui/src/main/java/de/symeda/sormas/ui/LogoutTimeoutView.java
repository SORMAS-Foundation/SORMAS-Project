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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui;

import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import de.symeda.sormas.ui.login.LoginHelper;

@SuppressWarnings("serial")
public class LogoutTimeoutView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "logouttimer";

	public LogoutTimeoutView() {
		Notification.show("logging you out");
		Page.getCurrent().getJavaScript()
		.execute("var url = window.location.toString();\r\n"
				+ "if (url.includes(\"logouttimer\")); {\r\n"
				+ "    window.location = url.replace(/logouttimer/, '');}\r\n" + "");
		LoginHelper.logout();
	}
}

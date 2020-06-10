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
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

/**
 * View shown when trying to navigate to a view the user does not have access to using
 * {@link com.vaadin.navigator.Navigator}.
 */
@SuppressWarnings("serial")
public class AccessDeniedView extends VerticalLayout implements View {

	private Label explanation;

	public AccessDeniedView() {

		setMargin(true);
		setSpacing(true);

		Label header = new Label(I18nProperties.getString(Strings.headingAccessDenied));
		header.addStyleName(ValoTheme.LABEL_H1);
		addComponent(header);
		addComponent(explanation = new Label());
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		explanation.setValue(I18nProperties.getString(Strings.errorAccessDenied));
	}
}

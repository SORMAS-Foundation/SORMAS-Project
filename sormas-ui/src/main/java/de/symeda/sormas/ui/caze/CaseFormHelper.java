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

package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.ui.CheckBox;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class CaseFormHelper {

	private CaseFormHelper() {
	}

	public static void addDontShareWithReportingTool(AbstractEditForm<?> form, CustomLayout layout, String checkboxLoc, String warningLoc) {
		addDontShareWithReportingTool(form, layout, checkboxLoc, warningLoc, Strings.messageDontShareWithReportingToolWarning);
	}

	public static void addDontShareWithReportingTool(
		AbstractEditForm<?> form,
		CustomLayout layout,
		String checkboxLoc,
		String warningLoc,
		String warningMessageKey) {
		if (FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()) {
			CheckBox dontShareCheckbox = form.addField(checkboxLoc, CheckBox.class);
			dontShareCheckbox.addStyleName(VSPACE_3);
			dontShareCheckbox.addValueChangeListener(e -> {
				Boolean dontShare = (Boolean) e.getProperty().getValue();
				if (Boolean.TRUE.equals(dontShare)) {
					layout.addComponent(VaadinUiUtil.createWarningComponent(I18nProperties.getString(warningMessageKey), 20), warningLoc);
				} else {
					layout.removeComponent(warningLoc);
				}
			});
		}
	}
}

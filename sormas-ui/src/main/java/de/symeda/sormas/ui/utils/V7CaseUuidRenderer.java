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
package de.symeda.sormas.ui.utils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.v7.ui.renderers.HtmlRenderer;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import elemental.json.JsonValue;

@SuppressWarnings("serial")
public class V7CaseUuidRenderer extends HtmlRenderer {

	private final boolean withCreateCaseIfEmpty;

	public V7CaseUuidRenderer(boolean withCreateCaseIfEmpty) {
		this.withCreateCaseIfEmpty = withCreateCaseIfEmpty;
	}

	@Override
	public JsonValue encode(String value) {

		if (withCreateCaseIfEmpty && (value == null || value.isEmpty())) {
			value = "<a title='" + I18nProperties.getString(Strings.headingCreateNewCase) + "'>" + I18nProperties.getCaption(Captions.actionCreate)
				+ "</a> " + VaadinIcons.EDIT.getHtml();
			return super.encode(value);
		}

		if (value != null && !value.isEmpty()) {
			value = "<a title='" + value + "'>" + DataHelper.getShortUuid(value) + "</a>";
			return super.encode(value);
		} else {
			return null;
		}
	}
}

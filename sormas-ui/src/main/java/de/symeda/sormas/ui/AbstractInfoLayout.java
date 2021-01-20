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

package de.symeda.sormas.ui;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.ui.utils.CssStyles;

public abstract class AbstractInfoLayout<T> extends HorizontalLayout {

	private Class<T> dataType;

	private UiFieldAccessCheckers fieldAccessCheckers;

	public AbstractInfoLayout(Class<T> dataType, UiFieldAccessCheckers fieldAccessCheckers) {
		this.dataType = dataType;
		this.fieldAccessCheckers = fieldAccessCheckers;
	}

	protected Label addDescLabel(AbstractLayout layout, String fieldId, Object value, String caption) {
		return addCustomDescLabel(layout, dataType, fieldId, value, caption);
	}

	protected Label addCustomDescLabel(AbstractLayout layout, Class<?> dataType, String fieldId, Object value, String caption) {
		if (!fieldAccessCheckers.isAccessible(dataType, fieldId)
			|| (fieldAccessCheckers.isEmbedded(dataType, fieldId) && !fieldAccessCheckers.hasRight())) {

			Label label = addDescLabel(layout, I18nProperties.getCaption(Captions.inaccessibleValue), caption);
			label.addStyleName(CssStyles.INACCESSIBLE_LABEL);
			label.setId("infoLayout_" + dataType.getSimpleName() + "_" + fieldId);

			return label;
		}

		return addDescLabel(layout, value, caption);
	}

	private Label addDescLabel(AbstractLayout layout, Object content, String caption) {

		String contentString = content != null ? content.toString() : "";
		Label label = new Label(contentString);
		label.setCaption(caption);
		layout.addComponent(label);
		label.setWidthFull();
		return label;
	}
}

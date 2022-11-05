/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils;

import com.vaadin.ui.Component;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.ReferenceDto;

/**
 * A detail view shows specific details of an object identified by the URL parameter.
 *
 * In addition to that, it contains generic code to check if the object is editable.
 *
 * @param <R>
 *            {@link ReferenceDto} with the uuid as parsed from the URL.
 */
public abstract class AbstractEditAllowedDetailView<R extends ReferenceDto> extends AbstractDetailView<R> {

	protected AbstractEditAllowedDetailView(String viewName) {
		super(viewName);

	}

	protected abstract CoreFacade getCoreFacade();

	protected boolean isEditAllowed() {
		String uuid = getReference().getUuid();
		return getCoreFacade().isEditAllowed(uuid);
	}

	protected void setEditPermission(Component component) {
		if (!isEditAllowed()) {
			component.setEnabled(false);
		}
	}

	protected void setEditPermission(CommitDiscardWrapperComponent<? extends AbstractEditForm> root, String... excludeFields) {
		if (!isEditAllowed()) {
			root.getWrappedComponent().getFieldGroup().setEnabled(false);
			root.getButtonsPanel().setEnabled(false);
			for (String singleExcludedField : excludeFields) {
				root.getWrappedComponent().getFieldGroup().getField(singleExcludedField).setEnabled(true);
			}

//			for (Object propertyId : root.getWrappedComponent().getFieldGroup().getBoundPropertyIds()) {
//				Field<?> field = root.getWrappedComponent().getFieldGroup().getField(propertyId);
//				if (!ArrayUtils.contains(excludeFields, field.getId())){
//					field.setEnabled(false);
//				}
//			}
		}
	}
}

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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DoneListener;

/**
 * Besteht aus einem "confirm"/"Übernehmen"- und einem "cancel"/"Abbrechen"-Button
 * 
 * @author Martin Wahnschaffe
 */
@SuppressWarnings("serial")
public abstract class ConfirmationComponent extends HorizontalLayout {

	private transient List<DoneListener> doneListeners = new ArrayList<DoneListener>();

	private final boolean inverseOrder;
	private Button confirmButton;
	private Button cancelButton;
	private final String cancelButtonStyle;

	public ConfirmationComponent() {
		this(false);
	}

	public ConfirmationComponent(boolean inverseOrder) {
		this(inverseOrder, ValoTheme.BUTTON_PRIMARY);
	}

	public ConfirmationComponent(boolean inverseOrder, String cancelButtonStyle) {
		this.inverseOrder = inverseOrder;
		this.cancelButtonStyle = cancelButtonStyle;

		setSpacing(true);
		setSizeUndefined();

		Button discardButton = getCancelButton();
		if (!inverseOrder)
			addComponent(discardButton);

		Button commitButton = getConfirmButton();
		addComponent(commitButton);

		if (inverseOrder)
			addComponent(discardButton);
	}

	public void addExtraButton(Button button, Button.ClickListener handler) {
		button.addClickListener(e -> {
			handler.buttonClick(e);
			onDone();
		});

		addComponent(button, inverseOrder ? getComponentCount() - 1 : 0);
	}

	public Button getConfirmButton() {
		if (confirmButton == null) {
			confirmButton = ButtonHelper.createButton(Captions.actionConfirm, false, event -> {
				onConfirm();
				onDone();
			}, ValoTheme.BUTTON_PRIMARY);
		}

		return confirmButton;
	}

	public Button getCancelButton() {
		if (cancelButton == null) {
			cancelButton = ButtonHelper.createButton(Captions.actionCancel, false, event -> {
				onCancel();
				onDone();
			}, cancelButtonStyle);
		}

		return cancelButton;
	}

	public void addDoneListener(DoneListener listener) {
		if (!doneListeners.contains(listener))
			doneListeners.add(listener);
	}

	public void removeDoneListener(DoneListener listener) {
		doneListeners.remove(listener);
	}

	public void onDone() {
		for (DoneListener listener : doneListeners)
			listener.onDone();
	}

	protected abstract void onConfirm();

	protected void onCancel() {

	};
}

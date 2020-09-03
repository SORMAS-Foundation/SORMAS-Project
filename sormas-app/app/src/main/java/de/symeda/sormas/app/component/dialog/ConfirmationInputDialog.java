/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.component.dialog;

import android.content.Context;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;

public class ConfirmationInputDialog extends AbstractDialog {

	public static final String TAG = ConfirmationInputDialog.class.getSimpleName();

	private final String wordToType;

	// Constructors

	public ConfirmationInputDialog(final FragmentActivity activity, int headingResId, int subHeadingResId, String wordToType) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_confirmation_input_layout,
			R.layout.dialog_root_two_button_panel_layout,
			headingResId,
			subHeadingResId);

		this.wordToType = wordToType;
		getConfig().setSubHeading(String.format(getConfig().getSubHeading(), wordToType));
		getConfig().setHideHeadlineSeparator(true);
	}

	// Overrides

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		// Data variable is not needed in this dialog
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		getPositiveButton().setEnabled(false);

		((ControlTextEditField) getRoot().findViewById(R.id.confirmation_input)).addValueChangedListener(new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				if (field.getValue() != null) {
					getPositiveButton().setEnabled(wordToType.compareToIgnoreCase(field.getValue().toString()) == 0);
				} else {
					getPositiveButton().setEnabled(false);
				}
			}
		});
	}

	@Override
	public boolean isHeadingCentered() {
		return true;
	}

	@Override
	public boolean isRounded() {
		return true;
	}

	@Override
	public ControlButtonType getNegativeButtonType() {
		return ControlButtonType.LINE_DANGER;
	}
}

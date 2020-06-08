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
import android.content.res.Resources;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.component.controls.ControlButtonType;

public class ConfirmationDialog extends AbstractDialog {

	public static final String TAG = ConfirmationDialog.class.getSimpleName();

	// Constructors

	public ConfirmationDialog(final FragmentActivity activity, int headingResId, int subHeadingResId) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_confirmation_layout,
			R.layout.dialog_root_two_button_panel_layout,
			headingResId,
			subHeadingResId);

		getConfig().setHideHeadlineSeparator(true);
	}

	public ConfirmationDialog(
		final FragmentActivity activity,
		int headingResId,
		int subHeadingResId,
		int positiveButtonTextResId,
		int negativeButtonTextResId) {
		this(activity, headingResId, subHeadingResId);

		Resources resources = getContext().getResources();
		if (positiveButtonTextResId >= 0) {
			getConfig().setPositiveButtonText(resources.getString(positiveButtonTextResId));
		} else {
			getConfig().setPositiveButtonText(resources.getString(R.string.action_confirm));
		}
		if (negativeButtonTextResId >= 0) {
			getConfig().setNegativeButtonText(resources.getString(negativeButtonTextResId));
		} else {
			getConfig().setNegativeButtonText(resources.getString(R.string.action_dismiss));
		}
	}

	// Overrides

	@Override
	protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
		// Data variable is not needed in this dialog
	}

	@Override
	protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
		// Nothing to initialize
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

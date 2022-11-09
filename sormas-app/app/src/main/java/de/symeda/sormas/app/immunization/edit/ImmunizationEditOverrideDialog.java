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

package de.symeda.sormas.app.immunization.edit;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import de.symeda.sormas.app.BR;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.util.Callback;

public class ImmunizationEditOverrideDialog extends AlertDialog.Builder {

	public static final String TAG = InfoDialog.class.getSimpleName();

	protected int layoutId;
	private Object data;

	private AlertDialog dialog;

	private Callback saveCallback;
	private Callback dismissCallback;
	private ViewDataBinding binding;

	public ImmunizationEditOverrideDialog(Context context, int layoutId, Object data, Callback callback) {
		super(context);

		this.layoutId = layoutId;
		this.data = data;

		dismissCallback = () -> dialog.dismiss();

		saveCallback = () -> {
			callback.call();
			dialog.dismiss();
		};

		binding = bindLayout(context);

		if (binding != null) {
			setView(binding.getRoot());
		}
	}

	@Override
	public AlertDialog show() {
		dialog = super.show();
		return dialog;
	}

	protected ViewDataBinding bindLayout(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (inflater == null) {
			return null;
		}

		ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutId, null, false);
		String layoutName = context.getResources().getResourceEntryName(layoutId);

		if (!binding.setVariable(BR.data, data)) {
			Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
		}

		if (!binding.setVariable(BR.dismissCallback, dismissCallback)) {
			Log.e(TAG, "There is no variable 'dismissCallback' in layout " + layoutName);
		}

		if (!binding.setVariable(BR.saveCallback, saveCallback)) {
			Log.e(TAG, "There is no variable 'saveCallback' in layout " + layoutName);
		}

		return binding;
	}

	public ViewDataBinding getBinding() {
		return binding;
	}
}

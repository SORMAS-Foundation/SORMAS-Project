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

package de.symeda.sormas.app.util;

/**
 * Created by Orson on 03/11/2017.
 */

import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

public abstract class FormTab extends DialogFragment implements FormFragment {

	protected void activateField(View v) {
		v.setEnabled(true);
	}

	protected void deactivateField(View v) {
		v.setEnabled(false);
		v.clearFocus();
	}

	protected void setFieldVisibleOrGone(View v, boolean visible) {
		if (visible) {
			v.setVisibility(View.VISIBLE);
		} else {
			v.setVisibility(View.GONE);
			v.clearFocus();
		}
	}

	protected void setFieldVisible(View v, boolean visible) {
		if (visible) {
			v.setVisibility(View.VISIBLE);
		} else {
			v.setVisibility(View.INVISIBLE);
			v.clearFocus();
		}
	}

	protected void setFieldGone(View v) {
		v.setVisibility(View.GONE);
		v.clearFocus();
	}

	protected void reloadFragment() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.detach(this).attach(this).commit();
	}
}

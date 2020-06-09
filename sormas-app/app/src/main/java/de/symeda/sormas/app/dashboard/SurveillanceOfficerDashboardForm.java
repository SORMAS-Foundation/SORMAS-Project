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

package de.symeda.sormas.app.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.databinding.FragmentDashboardSurveillanceOfficerLayoutBinding;
import de.symeda.sormas.app.util.FormTab;

// import de.symeda.sormas.app.databinding.SettingsFragmentLayoutBinding;

/**
 * Created by Orson on 20/11/2017.
 */

public class SurveillanceOfficerDashboardForm extends FormTab {

	private FragmentDashboardSurveillanceOfficerLayoutBinding binding;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard_surveillance_officer_layout, container, false);

		return binding.getRoot();
	}

	@Override
	public void onResume() {
		super.onResume();

		/*
		 * boolean hasUser = ConfigProvider.getUser() != null;
		 * binding.btnSettingsChangePIN.setVisibility(hasUser ? View.VISIBLE : View.GONE);
		 * binding.btnSettingsSyncLog.setVisibility(hasUser ? View.VISIBLE : View.GONE);
		 * binding.btnSettingsLogout.setVisibility(hasUser ? View.VISIBLE : View.GONE);
		 */
	}

	@Override
	public AbstractDomainObject getData() {
		return null;
	}
}
